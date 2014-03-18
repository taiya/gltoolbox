import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.event.MouseInputAdapter;
import javax.vecmath.Matrix4f;

public abstract class SimpleRenderer extends MouseInputAdapter implements GLEventListener {
	public SimpleRenderer(GLCanvas canvas) {
		this.canvas = canvas;
		model_matrix.setIdentity();
	}

	/// Reference to GLCanvas we are rendering
	protected GLCanvas canvas = null;
	// Container of objects to be drawn
	protected Vector<Object> objects = new Vector<Object>();
	// Light position
	protected float[] light0_pos = { 0, 0, 100, 1 }; // /< light position
	// Ambient light
	protected float[] light0_amb = { 0.2f, 0.2f, 0.2f, 1f };
	// Diffuse color
	protected float[] light0_dif = { 1f, 1f, 1f, 1f };

	// Model matrix (of model view projection)
	protected Matrix4f model_matrix = new Matrix4f();
	// Array version of model_matrix
	// @internal this is because glMultMatrixf() needs a float[16]
	// the conversion is done by get_model_matrix()
	private float[] model_matrix_array = new float[16];

	// / @todo can this
	void cache_model_matrix() {
		model_matrix_array[0] = model_matrix.m00;
		model_matrix_array[1] = model_matrix.m10;
		model_matrix_array[2] = model_matrix.m20;
		model_matrix_array[3] = model_matrix.m30;
		model_matrix_array[4] = model_matrix.m01;
		model_matrix_array[5] = model_matrix.m11;
		model_matrix_array[6] = model_matrix.m21;
		model_matrix_array[7] = model_matrix.m31;
		model_matrix_array[8] = model_matrix.m02;
		model_matrix_array[9] = model_matrix.m12;
		model_matrix_array[10] = model_matrix.m22;
		model_matrix_array[11] = model_matrix.m32;
		model_matrix_array[12] = model_matrix.m03;
		model_matrix_array[13] = model_matrix.m13;
		model_matrix_array[14] = model_matrix.m23;
		model_matrix_array[15] = model_matrix.m33;
	}

	// Initializes the OpenGL context (colors, lighting, etc)
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		// Common Initialization
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // /< Background
		gl.glColor3f(1.0f, 0.0f, 0.0f); // /< foreground
		gl.glEnable(GL.GL_DEPTH_TEST);

		// / Lighting
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light0_pos, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light0_amb, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light0_dif, 0);
	}

	// Manages changes in viewport
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// System.out.printf("reshape" + width + " " + height + "\n");
		assert (height > 0);
		double aspect_ratio = ((double) width) / ((double) height);

		GL gl = drawable.getGL();

		// / Frustum [-1:1] mapped to full window
		gl.glViewport(0, 0, width, height);

		// / Setup the projection matrix
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-1.0 * aspect_ratio, 1.0 * aspect_ratio, -1.0, 1.0, -1.0, 1.0);

		// @todo:
		// glu.gluPerspective(45.0f, h, 1.0, 20.0);
	}

	// Display
	public void display(GLAutoDrawable drawable) {
		// System.out.printf("View3::display()\n");
		GL gl = drawable.getGL();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		// / @todo Can this be avoided?
		cache_model_matrix();

		gl.glPushMatrix();
		gl.glMultMatrixf(model_matrix_array, 0);
		for (int i = 0; i < objects.size(); i++)
			objects.elementAt(i).draw(gl);
		gl.glPopMatrix();
		gl.glFlush();
	}

	public void add_render_object(Object obj) {
		/// Add the object to the render queue
		objects.add(obj);
		/// Save a pointer to context in the canvas
		obj._canvas = canvas;
		/// Demand a refresh of the GL display
		canvas.display();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean a, boolean b) {
		throw new RuntimeException("displayChanged() not implemented");
	}

	/// Dragging the mouse refreshes the OpenGL canvas
	public void mouseDragged(MouseEvent mouseEvent) {
		canvas.display();
	}
}
