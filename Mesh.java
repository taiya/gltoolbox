import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/// Buffered rendering
/// http://www.java-tips.org/other-api-tips/jogl/vertex-buffer-objects-nehe-tutorial-jogl-port-2.html
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh extends Object {
	private FloatBuffer vertices = null;
	private FloatBuffer normals = null;
	private IntBuffer faces = null;
	private int nfaces = 0;
	private boolean hasnormals = false;

	// / @todo Improve using IntBuffer.wrap()
	public Mesh(float[] verts, int[] faces, float[] normals) {
		// System.out.printf("TriMesh::TriMesh()\n");

		// / Setup vertex buffer
		this.vertices = Buffers.newDirectFloatBuffer(verts.length);
		this.vertices.put(verts);
		this.vertices.rewind();

		// / Setup face buffer
		this.nfaces = faces.length;
		this.faces = Buffers.newDirectIntBuffer(faces.length);
		this.faces.put(faces);
		this.faces.rewind();

		if (normals != null) {
			this.normals = Buffers.newDirectFloatBuffer(normals.length);
			this.normals.put(normals);
			this.normals.rewind();
			this.hasnormals = true;
		}

		// / Test input data
		// for(int i=0; i<faces.length; i+=3)
		// System.out.printf("i=" + i + " [" + faces[i] + " " + faces[i+1] + " "
		// + faces[i+2] + "]\n");
	}

	// / @todo java only has INT, could the glDrawElements call generate
	// problems?
	public void draw(GL2 gl) {
		// System.out.printf("TriMesh::draw()\n");
		if (vertices == null)
			return;

		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
		if (hasnormals)
			gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		if (hasnormals)
			gl.glNormalPointer(GL2.GL_FLOAT, 0, normals);
		if (nfaces > 0)
			gl.glDrawElements(GL2.GL_TRIANGLES, nfaces, GL2.GL_UNSIGNED_INT, faces);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		if (hasnormals)
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
	}
}