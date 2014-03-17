// export MATLAB=/Applications/MATLAB_R2013a.app
// export CLASSPATH="$MATLAB/java/jarext:jogl.jar:gluegen-rt.jar:."
// javac *.java
// java -Djava.library.path=$MATLAB/bin/maci64 View3
import javax.media.opengl.GLCanvas;
import javax.swing.JFrame;

public class View3 extends GLCanvas {
	private static final long serialVersionUID = 1L;
	private TrackballRenderer rend = new TrackballRenderer(this);

	public View3() {
		// OpenGL Control
		this.addGLEventListener(rend);

		// Mouse Control
		this.addMouseListener(rend);
		this.addMouseMotionListener(rend);

		// System.out.printf("View3::init()
		// "+gl.glGetString(GL.GL_VERSION)+"\n");
	}

	// Creates a mesh without surface normals
	public void add_mesh(float[] verts, int[] faces) {
		add_mesh(verts, faces, null);
	}

	// Creates a mesh with surface normals
	public void add_mesh(float[] verts, int[] faces, float[] normals) {
		TriMesh mesh = new TriMesh(verts, faces, normals);
		rend.add_render_object(mesh);
		this.display();
	}

	public void add(ObjectRenderer object) {
		rend.add_render_object(object);
		this.display();
	}

	// Test function
	public static void main(String[] args) {
		// System.out.printf( System.getProperty("java.class.path") );
		View3 view3 = new View3();
		JFrame frame = new JFrame("OpenGL Viewer");
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(view3);
		frame.setVisible(true);
		
		view3.add(new CubeRenderer());		
	}
}