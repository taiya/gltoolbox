import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.event.MouseInputAdapter;
import javax.vecmath.Matrix4f;

import javax.media.opengl.glu.GLU;


public abstract class SimpleRenderer extends MouseInputAdapter implements GLEventListener {
	public SimpleRenderer(GLCanvas canvas) {
		this.canvas = canvas;
		model_matrix.setIdentity();
	}

	// Reference to GLCanvas we are rendering
	protected GLCanvas canvas = null;
	// GLU 
	protected GLU glu = new GLU();
	// Container of objects to be drawn
	protected Vector<Object> objects = new Vector<Object>();
	// Light position
	protected float[] light0_pos = { 0, 0, 100, 1 }; // /< light position
	// Ambient light
	protected float[] light0_amb = { 0.2f, 0.2f, 0.2f, 1f };
	// Diffuse color
	protected float[] light0_dif = { 1f, 1f, 1f, 1f };

	/** Model matrix (of model view projection) */
	protected Matrix4f model_matrix = new Matrix4f();
	// Array version of model_matrix
	// @internal this is because glMultMatrixf() needs a float[16]
	// the conversion is done by get_model_matrix()
	private float[] model_matrix_array = new float[16];
	
	// 
	private float scale = 1;
	private float[] translation = {.0f,.0f,.0f};
	
	public float getScale(){ return scale; }
	public float[] getTranslation() { return translation; }
	public void setScale(float scale){ this.scale = scale; }
	public void setTranslation(float[] translation){ this.translation = translation; }
	
	
	public void setOriginAt(double[] tr){ 
		this.translation[0] = -(float) tr[0];
		this.translation[1] = -(float) tr[1];
		this.translation[2] = -(float) tr[2];
	}

	/**
	 * Centers a scene so that the clicked point point p becomes the arcball center
	 */
	protected void setSceneCenter(Point p){
		GL gl = canvas.getGL();	
		double[] point_glwin_coords = Utils.getGLWindowCoordinates(p,gl);
		double[] point_world_coords = Utils.windowToWorld(point_glwin_coords,gl);
		if(point_world_coords!=null) this.setOriginAt(point_world_coords);
	}
	
	// @todo can this be simplified?
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
	
	public float[] getRotation(){
		cache_model_matrix();
		return model_matrix_array;
	}

	// Initializes the OpenGL context (colors, lighting, etc)
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		// Common Initialization
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // /< Background
		gl.glColor3f(1.0f, 0.0f, 0.0f); // /< foreground
	
        // Depth Buffer Setup
		gl.glEnable(GL.GL_DEPTH_TEST); ///< enable depth buffering
		gl.glDepthMask(true);          ///< depth buffer writable
		gl.glDepthRange(0.0f, 1.0f);   ///< depth value is [0,1]
		gl.glDepthFunc(GL.GL_LEQUAL);  ///< closest geometry kept
		gl.glClearDepth(1.0f);		   ///< clear with this value
        
		// Simple lighting
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light0_pos, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light0_amb, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light0_dif, 0);
		
		// Back faces receive light
		gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
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
	
		if(true){
			gl.glOrtho(-1.0 * aspect_ratio, 1.0 * aspect_ratio, -1.0, 1.0, -10, 10);
		} else {
			float range = 3;
			glu.gluPerspective(45, (float) width / height, .01, 2*range);	
			gl.glTranslatef(0, 0, -range); //< default perspective would be at origin
			// glu.gluLookAt(/*pos*/ 0,0,2, /*at*/ 0, 0, 0, /*up*/ 0, 1, 0); //< Another way?
		}		
	}
	
	// Display
	public void display(GLAutoDrawable drawable) {
		System.out.printf("SimpleRenderer::display()\n");
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glPushMatrix();
			gl.glMultMatrixf(getRotation(), 0);
			for (int i = 0; i < objects.size(); i++){
				gl.glPushMatrix();
					objects.elementAt(i).draw(gl);
				gl.glPopMatrix();
			}
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
