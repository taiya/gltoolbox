/**
 * @author Andrea Tagliasacchi <andrea.tagliasacchi@epfl.ch>
 * @see http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 * @version 1.0
 */
import javax.swing.JFrame;
import javax.media.opengl.*;

public class OpenGLCanvas extends GLCanvas {
	/** For serialization */
	private static final long serialVersionUID = 1L;
	
	/** mouse controller we hook up to */
	private ArcballRenderer rend = new ArcballRenderer(this);

	/** AntiAlias capabilities across all viewers*/
	static class MyCapabilities extends GLCapabilities{
		public MyCapabilities(){
			System.err.print("WARNING: Depth Buffer Operations Corrupted");
			this.setSampleBuffers(true);
			this.setNumSamples(4);
			this.setDepthBits(32);
		}
	}
	
	/** constructor given capabilities */
	public OpenGLCanvas() {
		// TODO AntiAliasing (see SHA#81ed3a23d2)
		// super(new MyCapabilities());
		
		// OpenGL init/display
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
		if(true){
			int howmany = 1000000;
			float[] vpoints = PointCloud.random_points(howmany);
			float[] vcolors = PointCloud.random_colors(howmany);		
			PointCloud cloud = new PointCloud(vpoints, vcolors, null);
			view3.add(cloud);	
		}
		
		/// Just draw a unit cube
		if(false) view3.add(new Cube());
	}
}