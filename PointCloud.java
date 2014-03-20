import java.nio.FloatBuffer;
import javax.media.opengl.GL2;
import com.jogamp.common.nio.Buffers;

public class PointCloud extends Object {
	protected FloatBuffer vertices = null;
	protected int 		  npoints = 0;
	protected float 	  pointsize = 1;
	
	protected FloatBuffer vcolors = null;
	protected boolean 	  has_vcolors = false;
	
	protected FloatBuffer normals = null;
	protected boolean 	  has_normals = false;

    public PointCloud(float[] vpoints, float[] vcolors) {
    	this(vpoints, vcolors, null);
    }

	public PointCloud(float[] vpoints, float[] vcolors, float[] vnormals) {
		// TODO why FloatBuffer.wrap(verts) does not work?
		this.vertices = Buffers.newDirectFloatBuffer(vpoints.length);
		this.vertices.put(vpoints);
		this.vertices.rewind();
		this.npoints = vpoints.length/3; /// 3D point

		if(vcolors != null && vcolors.length>0){
			// System.out.printf("Using color array");
			this.vcolors = Buffers.newDirectFloatBuffer(vcolors.length);
			this.vcolors.put(vcolors);
			this.vcolors.rewind();
			has_vcolors = true;
		}
		
		if(vnormals != null && vnormals.length>0){
			this.normals = Buffers.newDirectFloatBuffer(vnormals.length);
			this.normals.put(vnormals);
			this.normals.rewind();			
			has_normals = true;
		}
	}
	
	public void draw(GL2 gl) {
		// if(true) return;
		if (vertices == null) return;
		// gl.glColor3dv(color,0);
		gl.glPointSize(pointsize);
		
		if (has_normals){
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL2.GL_FLOAT, 0, normals);
		} else {
			/// Cannot shade, so what's the point
			gl.glDisable(GL2.GL_LIGHTING);
		}
		
		if(has_vcolors){
			gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl.glColorPointer(3, GL2.GL_FLOAT, 0, vcolors);
		}

		/// Buffered draw arrays
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
		gl.glDrawArrays(GL2.GL_POINTS, 0, npoints);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		
		if(has_vcolors){
			gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		}
		
		/// Turn off employed client states
		if (has_normals){
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		}
	}
	
	/** changes point side and refreses GL context */
	public void setPointSize(float size){
		this.pointsize = size;
		display();
	}
	
	/** to generate test data */
	public static float[] random_points(int howmany){
		java.util.Random random = new java.util.Random();
		float[] points = new float[3 * howmany];
		for (int i = 0; i < points.length; i++)
			points[i] = (random.nextFloat() - .5f); // < [-.5, .5]
		return points;
	}
	
	/** to generate test data */
	public static float[] random_colors(int howmany){
		java.util.Random random = new java.util.Random();
		float[] colors = new float[3 * howmany];
		for (int i = 0; i < colors.length; i++)
			colors[i] = (random.nextFloat());
		return colors;
	}	
}
