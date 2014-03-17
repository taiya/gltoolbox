import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

/// Extends the simple renderer with a Trackball controller
public class TrackballRenderer extends Renderer {
	private Matrix4f LastRot = new Matrix4f();
	private ArcBall arcBall = null;
	
	public TrackballRenderer(GLCanvas canvas) {
		super(canvas);
		LastRot.setIdentity();
		arcBall = new ArcBall(canvas.getWidth(), canvas.getHeight());
	}

	// / Arcball needs to know about window geometry
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		arcBall.setBounds(width, height);
	}

	public void mousePressed(MouseEvent mouseEvent) {
		Point MousePt = mouseEvent.getPoint();
		LastRot.set(model_matrix);
		arcBall.click(MousePt);
	}

	public void mouseDragged(MouseEvent mouseEvent) {
		// / Update the model matrix
		Point MousePt = mouseEvent.getPoint();
		Quat4f ThisQuat = new Quat4f();
		arcBall.drag(MousePt, ThisQuat);
		model_matrix.setRotation(ThisQuat);
		model_matrix.mul(model_matrix, LastRot);

		// / Finally refresh the OpenGL window
		canvas.display();
	}
}