import java.nio.FloatBuffer;
import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

public class PointCloud extends ObjectRenderer {
	protected FloatBuffer vertices = null;
	protected int 		  npoints = 0;
	protected float 	  pointsize = 1;
	
	protected FloatBuffer vcolors = null;
	protected boolean 	  has_vcolors = false;
	
	protected FloatBuffer normals = null;
	protected boolean 	  has_normals = false;
	
	public PointCloud(float[] vpoints, float[] vnormals, float[] vcolors) {
		// TODO why FloatBuffer.wrap(verts) does not work?
		this.vertices = BufferUtil.newFloatBuffer(vpoints.length);
		this.vertices.put(vpoints);
		this.vertices.rewind();
		this.npoints = vpoints.length/3; /// 3D point

		if(vcolors != null && vcolors.length>0){
			// System.out.printf("Using color array");
			this.vcolors = BufferUtil.newFloatBuffer(vcolors.length);
			this.vcolors.put(vcolors);
			this.vcolors.rewind();
			has_vcolors = true;
		}
		
		if(vnormals != null && vnormals.length>0){
			this.normals = BufferUtil.newFloatBuffer(vnormals.length);
			this.normals.put(vnormals);
			this.normals.rewind();			
			has_normals = true;
		}
	}
	
	public void draw(GL gl) {
		// if(true) return;
		if (vertices == null) return;
		// gl.glColor3dv(color,0);
		gl.glPointSize(pointsize);
		
		if (has_normals){
			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL.GL_FLOAT, 0, normals);
		} else {
			/// Cannot shade, so what's the point
			gl.glDisable(GL.GL_LIGHTING);
		}
		
		if(has_vcolors){
			gl.glEnableClientState(GL.GL_COLOR_ARRAY);
			gl.glColorPointer(3, GL.GL_FLOAT, 0, vcolors);
		}

		/// Buffered draw arrays
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
		gl.glDrawArrays(GL.GL_POINTS, 0, npoints);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		
		if(has_vcolors){
			gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		}
		
		/// Turn off employed client states
		if (has_normals){
			gl.glDisable(GL.GL_LIGHTING);
			gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
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
