import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/// Extends the simple renderer with a Trackball controller
public class ArcballRenderer extends SimpleRenderer {
	private Matrix4f LastRot = new Matrix4f();
	private ArcBallHelper arcBall = null;
	private Arcball arcball_geo = new Arcball();
		
	public ArcballRenderer(GLCanvas canvas) {
		super(canvas);
		LastRot.setIdentity();
		arcBall = new ArcBallHelper(canvas.getWidth(), canvas.getHeight());
	}

	/**
	 * 
	 */
	@Override 
	public void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Make unit sphere fit loosely
		gl.glScaled(.85, .85, .85);
				
		// Arcball rotates, but doesn't translate/scale
		gl.glMultMatrixf(super.getRotation(), 0);
		arcball_geo.draw(gl);
		
		// Models are also scaled translated
		gl.glScaled(getScale(), getScale(), getScale());
		gl.glTranslated(getTx(),getTy(),getTz());
		for (int i = 0; i < objects.size(); i++)
			objects.elementAt(i).draw(gl);
		
		gl.glFlush();
	}
	
	
	@Override // Arcball needs to know about window geometry
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		arcBall.setBounds(width, height);
	}

	public void mousePressed(MouseEvent mouseEvent) {
		switch (mouseEvent.getButton()) {
		case MouseEvent.BUTTON1: // Left Mouse
			Point MousePt = mouseEvent.getPoint();
			LastRot.set(model_matrix);
			arcBall.click(MousePt);
		default:
			return;
		}
	}

	public void mouseDragged(MouseEvent mouseEvent) {
		switch (mouseEvent.getButton()) {
		case MouseEvent.BUTTON1: // Left Mouse
			// Update the model matrix
			Point MousePt = mouseEvent.getPoint();
			Quat4f ThisQuat = new Quat4f();
			arcBall.drag(MousePt, ThisQuat);
			model_matrix.setRotation(ThisQuat);
			model_matrix.mul(model_matrix, LastRot);			
			break;
		case MouseEvent.BUTTON2: // Middle Mouse
			System.out.printf("TODO: PANNING\n");
		default:
			return;
		}
			
		// Finally refresh the OpenGL window
		canvas.display();
	}

	public void mouseWheelMoved(MouseWheelEvent e){
		/// TODO make the zoom ratio exposed!
		float scale_move_ratio = .05f;
		setScale( getScale()*(1 + (scale_move_ratio*e.getWheelRotation()) ));
		canvas.display();
	}
	
	/** 
	 * The math to implementing ArcBall functionality
	 */
	class ArcBallHelper {
	    private static final float Epsilon = 1.0e-5f;

	    Vector3f StVec;          //Saved click vector
	    Vector3f EnVec;          //Saved drag vector
	    float adjustWidth;       //Mouse bounds width
	    float adjustHeight;      //Mouse bounds height

	    public ArcBallHelper(float NewWidth, float NewHeight) {
	        StVec = new Vector3f();
	        EnVec = new Vector3f();
	        setBounds(NewWidth, NewHeight);
	    }

	    public void mapToSphere(Point point, Vector3f vector) {
	        //Copy paramter into temp point
	        Vector2f tempPoint = new Vector2f(point.x, point.y);

	        //Adjust point coords and scale down to range of [-1 ... 1]
	        tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
	        tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);

	        //Compute the square of the length of the vector to the point from the center
	        float length = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y);

	        //If the point is mapped outside of the sphere... (length > radius squared)
	        if (length > 1.0f) {
	            //Compute a normalizing factor (radius / sqrt(length))
	            float norm = (float) (1.0 / Math.sqrt(length));

	            //Return the "normalized" vector, a point on the sphere
	            vector.x = tempPoint.x * norm;
	            vector.y = tempPoint.y * norm;
	            vector.z = 0.0f;
	        } else    //Else it's on the inside
	        {
	            //Return a vector to a point mapped inside the sphere sqrt(radius squared - length)
	            vector.x = tempPoint.x;
	            vector.y = tempPoint.y;
	            vector.z = (float) Math.sqrt(1.0f - length);
	        }

	    }

	    public void setBounds(float NewWidth, float NewHeight) {
	        assert((NewWidth > 1.0f) && (NewHeight > 1.0f));

	        //Set adjustment factor for width/height
	        adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
	        adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
	    }

	    //Mouse down
	    public void click(Point NewPt) {
	        mapToSphere(NewPt, this.StVec);

	    }

	    //Mouse drag, calculate rotation
	    public void drag(Point NewPt, Quat4f NewRot) {
	        //Map the point to the sphere
	        this.mapToSphere(NewPt, EnVec);

	        //Return the quaternion equivalent to the rotation
	        if (NewRot != null) {
	            Vector3f Perp = new Vector3f();

	            //Compute the vector perpendicular to the begin and end vectors
	            Perp.cross(StVec,EnVec);

	            //Compute the length of the perpendicular vector
	            if (Perp.length() > Epsilon)    //if its non-zero
	            {
	                //We're ok, so return the perpendicular vector as the transform after all
	                NewRot.x = Perp.x;
	                NewRot.y = Perp.y;
	                NewRot.z = Perp.z;
	                //In the quaternion values, w is cosine (theta / 2), where theta is rotation angle
	                NewRot.w = StVec.dot(EnVec);
	            } else                                    //if its zero
	            {
	                //The begin and end vectors coincide, so return an identity transform
	                NewRot.x = NewRot.y = NewRot.z = NewRot.w = 0.0f;
	            }
	        }
	    }
	}
}