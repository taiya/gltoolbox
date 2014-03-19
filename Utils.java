import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.BufferUtil;

import java.awt.Color;

public class Utils {
	public static void write_depth(GLAutoDrawable drawable) {
		int width = drawable.getWidth();
		int height = drawable.getHeight();
		System.out.println(width + " " + height);
		int npixels = drawable.getWidth() * drawable.getHeight();
		File outputFile = new File("depthbuffer.png");
		GL gl = drawable.getGL();

		FloatBuffer zbuf = FloatBuffer.allocate(2*npixels);
		// gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
		gl.glReadBuffer(GL.GL_BACK);
		gl.glReadPixels(0, 0, width, height, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zbuf);
		zbuf.rewind();
		
		/// Fill the image
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++){
				float zdepth = zbuf.get(x+y*width);
//				System.out.println(zdepth);
				int color = Color.HSBtoRGB(0, 0, zdepth);
				bufferedImage.setRGB(x, height-y-1, color);
			}
		
		try{
			ImageIO.write(bufferedImage, "PNG", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		
	public static void writeBufferToFile(GLAutoDrawable drawable) {
		File outputFile = new File("colorbuffer.png");
		int width = drawable.getWidth();
		int height = drawable.getHeight();

		ByteBuffer pixelsRGB = BufferUtil.newByteBuffer(width * height * 3);

		GL gl = drawable.getGL();

		gl.glReadBuffer(GL.GL_BACK);
		gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);

		gl.glReadPixels(0, // GLint x
				0, // GLint y
				width, // GLsizei width
				height, // GLsizei height
				GL.GL_RGB, // GLenum format
				GL.GL_UNSIGNED_BYTE, // GLenum type
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
