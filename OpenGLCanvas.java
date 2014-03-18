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

public class OpenGLCanvas extends GLCanvas {
	/** For serialization */
	private static final long serialVersionUID = 1L;
	
	/** mouse controller we hook up to */
	private ArcballRenderer rend = new ArcballRenderer(this);

	/** AntiAlias capabilities across all viewers*/
	static class AntialiasCapabilities extends GLCapabilities{
		public AntialiasCapabilities(){
			this.setSampleBuffers(true);
			this.setNumSamples(4);
		}
	}
	
	/** constructor given capabilities */
	public OpenGLCanvas() {
		// Specify AntiAliasing
		super(new AntialiasCapabilities());
		// OpenGL Controls
		this.addGLEventListener(rend);
		// Mouse Controls
		this.addMouseListener(rend);
		this.addMouseMotionListener(rend);
		this.addMouseWheelListener(rend);
	}

	/** Fetches the used OpenGL version */
	public String gl_version() {
		return getGL().glGetString(GL.GL_VERSION);
	}

	/** Adds a generic object to the renderer */
	public void add(Object object) {
		rend.add_render_object(object);
	}

	/** Standalone test entry point */
	public static void main(String[] args) {
		// System.out.printf( System.getProperty("java.class.path") );
		OpenGLCanvas view3 = new OpenGLCanvas();
		JFrame frame = new JFrame("OpenGL Viewer");
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(view3);
		frame.setVisible(true);

		// Test adding a point cloud
		if(false){
			int howmany = 1000000;
			float[] vpoints = PointCloud.random_points(howmany);
			float[] vcolors = PointCloud.random_colors(howmany);		
			PointCloud cloud = new PointCloud(vpoints, vcolors, null);
			view3.add(cloud);	
		}
		
		if(true){
			view3.add(new Cube());
		}
	}
}