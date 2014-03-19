import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.nio.FloatBuffer;

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
	/** Controls zoom speed */
	float scale_move_ratio = .05f; // / TODO make the zoom ratio exposed!
	/** Controls pan speed */
	float pan_move_ratio = 1;
	/** Used by the click-to-center logic */
	private MouseEvent mouse_pressed = null;

	public ArcballRenderer(GLCanvas canvas) {
		super(canvas);
		LastRot.setIdentity();
		arcBall = new ArcBallHelper(canvas.getWidth(), canvas.getHeight());
		adjust_pan_speed(canvas.getWidth(), canvas.getHeight());
	}

	/**
	 * Make sure panning speed is ~constant TODO use it
	 */
	private void adjust_pan_speed(int width, int height) {
		// / Pan speed adjusted normalized w.r.t. window size
		pan_move_ratio = 1.0f / ((float) canvas.getWidth());
		// System.out.printf("pan_move_ratio: %f\n", pan_move_ratio);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Make unit sphere fit loosely
		// gl.glScaled(.85, .85, .85);

		// Arcball rotates, but doesn't translate/scale
		gl.glMultMatrixf(super.getRotation(), 0);
		gl.glPushMatrix();
			arcball_geo.draw(gl);
		gl.glPopMatrix();

		// Models are also scaled translated
		gl.glScaled(getScale(), getScale(), getScale());
		float[] tr = getTranslation();
		gl.glTranslated(tr[0], tr[1], tr[2]);
		for (int i = 0; i < objects.size(); i++) {
			gl.glPushMatrix(); // < avoid contamination
				objects.elementAt(i).draw(gl);
			gl.glPopMatrix();
		}

		System.out.println("WRITING DEPTH BUFFER");
		Utils.write_depth(drawable);
		Utils.writeBufferToFile(drawable);
		System.exit(0);
		
		if (mouse_pressed != null && mouse_pressed.getClickCount() == 1) {
			// recenter_scene(drawable);
			Utils.write_depth(drawable);
		}

		gl.glFlush();
	}
	
	private void recenter_scene(GLAutoDrawable drawable){
		GL gl = drawable.getGL();
		
		// / Fetch screen point & avoid next event
		Point p = mouse_pressed.getPoint();
		mouse_pressed = null;
		
		int viewport[] = new int[4];
		double modelview[] = new double[16];
		double projection[] = new double[16];
		double[] wcoord = new double[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview, 0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
		// System.out.println("V" + Arrays.toString(viewport));
		// System.out.println("M" + Arrays.toString(modelview));
		// System.out.println("P" + Arrays.toString(projection));

		// / Fetch (x,y,z=depth)
		double x = p.x;
		double y = ((double) viewport[3]) - ((double) p.y) - 1.0;
		FloatBuffer zbuf = FloatBuffer.allocate(1);
		// gl.glReadBuffer(GL.GL_FRONT);
		// gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1); /// TODO CHECK
		gl.glReadPixels(p.x, p.y, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zbuf);
		zbuf.rewind();
		double z = zbuf.get();
		System.out.printf("Window coords are [%f %f %f]\n", x, y, z);

		if(true) return;

		// / Invert MVP
		boolean unproject_ok = glu.gluUnProject(x, y, z, modelview, 0, projection, 0, viewport,
				0, wcoord, 0);
		boolean not_farplane = Math.abs(z - 1) > 1e-10;
		if (unproject_ok && not_farplane) {
			System.out.println("World coords are (" + wcoord[0] + ", " + wcoord[1] + ", "
					+ wcoord[2] + ")");
			setOriginAt(wcoord);
			canvas.display();
		}
	}
	
	@Override
	
	// Arcball needs to know about window geometry
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		arcBall.setBounds(width, height);
		adjust_pan_speed(width, height);
	}

	@Override
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

	@Override
	public void mouseDragged(MouseEvent event) {
		switch (event.getButton()) {
		case MouseEvent.BUTTON1: // Left Mouse
			// Update the model matrix
			Point MousePt = event.getPoint();
			Quat4f ThisQuat = new Quat4f();
			arcBall.drag(MousePt, ThisQuat);
			model_matrix.setRotation(ThisQuat);
			model_matrix.mul(model_matrix, LastRot);
			break;
		case MouseEvent.BUTTON2: // Middle Mouse
			System.out.printf("TODO: PANNING \n");
			break;
		default:
			return;
		}

		// Finally refresh the OpenGL window
		canvas.display();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		/** @see http://www.java-tips.org/index.php?option=com_content&task=view&id=1628&Itemid=29 
		 * @see http://nehe.gamedev.net/article/using_gluunproject/16013 */
		mouse_pressed = event;
		canvas.display();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		setScale(getScale() * (1 + (scale_move_ratio * e.getWheelRotation())));
		canvas.display();
	}

	/**
	 * The math to implementing ArcBall functionality
	 */
	class ArcBallHelper {
		private static final float Epsilon = 1.0e-5f;

		Vector3f StVec; // Saved click vector
		Vector3f EnVec; // Saved drag vector
		float adjustWidth; // Mouse bounds width
		float adjustHeight; // Mouse bounds height

		public ArcBallHelper(float NewWidth, float NewHeight) {
			StVec = new Vector3f();
			EnVec = new Vector3f();
			setBounds(NewWidth, NewHeight);
		}

		public void mapToSphere(Point point, Vector3f vector) {
			// Copy paramter into temp point
			Vector2f tempPoint = new Vector2f(point.x, point.y);

			// Adjust point coords and scale down to range of [-1 ... 1]
			tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
			tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);

			// Compute the square of the length of the vector to the point from the center
			float length = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y);

			// If the point is mapped outside of the sphere... (length > radius squared)
			if (length > 1.0f) {
				// Compute a normalizing factor (radius / sqrt(length))
				float norm = (float) (1.0 / Math.sqrt(length));

				// Return the "normalized" vector, a point on the sphere
				vector.x = tempPoint.x * norm;
				vector.y = tempPoint.y * norm;
				vector.z = 0.0f;
			} else // Else it's on the inside
			{
				// Return a vector to a point mapped inside the sphere sqrt(radius squared - length)
				vector.x = tempPoint.x;
				vector.y = tempPoint.y;
				vector.z = (float) Math.sqrt(1.0f - length);
			}

		}

		public void setBounds(float NewWidth, float NewHeight) {
			assert ((NewWidth > 1.0f) && (NewHeight > 1.0f));

			// Set adjustment factor for width/height
			adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
			adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
		}

		// Mouse down
		public void click(Point NewPt) {
			mapToSphere(NewPt, this.StVec);
		}

		// Mouse drag, calculate rotation
		public void drag(Point NewPt, Quat4f NewRot) {
			// Map the point to the sphere
			this.mapToSphere(NewPt, EnVec);

			// Return the quaternion equivalent to the rotation
			if (NewRot != null) {
				Vector3f Perp = new Vector3f();

				// Compute the vector perpendicular to the begin and end vectors
				Perp.cross(StVec, EnVec);

				// Compute the length of the perpendicular vector
				if (Perp.length() > Epsilon) // if its non-zero
				{
					// We're ok, so return the perpendicular vector as the transform after all
					NewRot.x = Perp.x;
					NewRot.y = Perp.y;
					NewRot.z = Perp.z;
					// In the quaternion values, w is cosine (theta / 2), where theta is rotation
					// angle
					NewRot.w = StVec.dot(EnVec);
				} else // if its zero
				{
					// The begin and end vectors coincide, so return an identity transform
					NewRot.x = NewRot.y = NewRot.z = NewRot.w = 0.0f;
				}
			}
		}
	}
}