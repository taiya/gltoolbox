/**
 * @author Andrea Tagliasacchi <andrea.tagliasacchi@epfl.ch>
 * @version 1.0
 */

// export MATLAB=/Applications/MATLAB_R2013a.app
// export CLASSPATH="$MATLAB/java/jarext:jogl.jar:gluegen-rt.jar:."
// javac *.java
// java -Djava.library.path=$MATLAB/bin/maci64 View3
// http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
import javax.swing.JFrame;
import javax.media.opengl.*;

public class View3 extends GLCanvas {
	private static final long serialVersionUID = 1L;
	
	/** mouse controller we hook up to */
	private TrackballRenderer rend = new TrackballRenderer(this);

	/** constructor */
	public View3() {
		// OpenGL Controls
		this.addGLEventListener(rend);
		// Mouse Controls
		this.addMouseListener(rend);
		this.addMouseMotionListener(rend);
	}

	/** Fetches the used OpenGL version */
	public String gl_version() {
		return getGL().glGetString(GL.GL_VERSION);
	}

	/** Renders a mesh without surface normals */
	public MeshRenderer add_mesh(float[] verts, int[] faces) {
		return add_mesh(verts, faces, null);
	}

	/** Renders a mesh with surface normals */
	public MeshRenderer add_mesh(float[] verts, int[] faces, float[] normals) {
		MeshRenderer mesh = new MeshRenderer(verts, faces, normals);
		rend.add_render_object(mesh);
		return mesh;
	}

	/** displays a point cloud */
	public PointCloud scatter(float[] vpoints, float[] vcolors) {
        return scatter(vpoints, vcolors, null);
    }
    
	public PointCloud scatter(float[] vpoints, float[] vcolors, float[] vnormals) {
		PointCloud cloud = new PointCloud(vpoints, vnormals, vcolors);
		rend.add_render_object(cloud);
        return cloud;
	}

	/** Adds a generic object to the renderer */
	public void add(ObjectRenderer object) {
		rend.add_render_object(object);
	}

	/** Standalone test entry point */
	public static void main(String[] args) {
		// System.out.printf( System.getProperty("java.class.path") );
		View3 view3 = new View3();
		JFrame frame = new JFrame("OpenGL Viewer");
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(view3);
		frame.setVisible(true);

		// Test adding some rendering objects
		// view3.add(new CubeRenderer());

		// Test adding a point cloud
		int howmany = 1000000;
		float[] points = PointCloud.random_points(howmany);
		float[] colors = PointCloud.random_colors(howmany);		
		view3.scatter(points, null, colors);
		
	}
}