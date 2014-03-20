import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import java.awt.Color;
import java.awt.Point;

public class Utils {

	/** 
	 * @brief converts an AWT space coordinate into the OpenGL coordinate frame 
	 * @note type is double because gluUnProject uses that
	 * */
	public static double[] getGLWindowCoordinates(Point p, GL2 gl){
		if(gl==null) System.out.println("GL NULL");
		
		//--- Fetch vieport
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		// System.out.println("V" + Arrays.toString(viewport));
		
		//--- Fetch vieport
		int x = p.x;
		int y = viewport[3] -p.y -1; // OpenGL has flipped axis

		//--- Fetch vieport
		FloatBuffer zbuf = FloatBuffer.allocate(1);
		gl.glReadBuffer(GL2.GL_BACK);
		gl.glReadPixels(x, y, 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, zbuf);
		zbuf.rewind();
		double z = zbuf.get();
		// System.out.println("Z" + z);
		
		//--- Assemble result
		double[] ret = {x,y,z};
		return ret;
	}
	
	/** 
	 * @brief converts a point in OpenGL screen coordinate into world coordinats by accessing the depth buffer
	 * @note type is double because gluUnProject uses double
	 * @see http://www.java-tips.org/index.php?option=com_content&task=view&id=1628&Itemid=29 
	 * @see http://nehe.gamedev.net/article/using_gluunproject/16013
	 */
	public static double[] windowToWorld(double[] p, GL2 gl){
		//--- Nothing was found?
		final double DEPTH_THRESHOLD = 1;
		if(p[2]>=DEPTH_THRESHOLD)
			return null;
		
		//--- Fetch matrices
		int viewport[] = new int[4];
		double modelview[] = new double[16];
		double projection[] = new double[16];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
		// System.out.println("M" + Arrays.toString(modelview));
		// System.out.println("P" + Arrays.toString(projection));
		// System.out.println("V" + Arrays.toString(viewport));
		
		//--- Invert MVP
		GLU glu = new GLU();
		double[] p_ = new double[4];
		boolean unproject_ok = glu.gluUnProject(p[0], p[1], p[2], modelview, 0, projection, 0, viewport, 0, p_, 0);
		if(!unproject_ok) return null;
		double[] ret = {p_[0], p_[1], p_[2]};
		return ret;
	}
	
	/** call it in display() after drawing but before glFlush() */
	public static void write_depth(GLAutoDrawable drawable) {
		int width = drawable.getWidth();
		int height = drawable.getHeight();
		// System.out.println(width + " " + height);
		int npixels = drawable.getWidth() * drawable.getHeight();
		File outputFile = new File("depthbuffer.png");
		GL2 gl = drawable.getGL().getGL2();

		FloatBuffer zbuf = FloatBuffer.allocate(npixels);
		gl.glReadBuffer(GL2.GL_BACK);
		gl.glReadPixels(0, 0, width, height, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, zbuf);
		zbuf.rewind();

		// Fill the image
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				float zdepth = zbuf.get(x + y * width);
				int color = Color.HSBtoRGB(0, 0, zdepth);
				bufferedImage.setRGB(x, height - y - 1, color);
			}
		try {
			ImageIO.write(bufferedImage, "PNG", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** call it in display() after drawing but before glFlush() */
	public static void write_color(GLAutoDrawable drawable) {
		File outputFile = new File("colorbuffer.png");
		int width = drawable.getWidth();
		int height = drawable.getHeight();

		ByteBuffer pixelsRGB = ByteBuffer.wrap( new byte[width * height * 3] );

		GL2 gl = drawable.getGL().getGL2();

		gl.glReadBuffer(GL2.GL_BACK);
		gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);

		gl.glReadPixels(0, // GLint x
				0, // GLint y
				width, // GLsizei width
				height, // GLsizei height
				GL2.GL_RGB, // GLenum format
				GL2.GL_UNSIGNED_BYTE, // GLenum type
				pixelsRGB); // GLvoid *pixels

		int[] pixelInts = new int[width * height];

		// Convert RGB bytes to ARGB ints with no transparency. Flip image vertically by reading the
		// rows of pixels in the byte buffer in reverse - (0,0) is at bottom left in OpenGL.

		int p = width * height * 3; // Points to first byte (red) in each row.
		int q; // Index into ByteBuffer
		int i = 0; // Index into target int[]
		int w3 = width * 3; // Number of bytes in each row

		for (int row = 0; row < height; row++) {
			p -= w3;
			q = p;
			for (int col = 0; col < width; col++) {
				int iR = pixelsRGB.get(q++);
				int iG = pixelsRGB.get(q++);
				int iB = pixelsRGB.get(q++);

				pixelInts[i++] = 0xFF000000 | ((iR & 0x000000FF) << 16) | ((iG & 0x000000FF) << 8) | (iB & 0x000000FF);
			}

		}

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bufferedImage.setRGB(0, 0, width, height, pixelInts, 0, width);

		try {
			ImageIO.write(bufferedImage, "PNG", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
