


import javax.media.opengl.GL;

/// Buffered rendering
/// http://www.java-tips.org/other-api-tips/jogl/vertex-buffer-objects-nehe-tutorial-jogl-port-2.html
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import com.sun.opengl.util.BufferUtil;

public class TriMesh implements ObjectRenderer{
	private FloatBuffer vertices = null;
	private FloatBuffer normals = null;
	private IntBuffer faces = null;
    private int nfaces = 0;
    private boolean hasnormals = false;
    
    /// @todo Improve using IntBuffer.wrap()
	public TriMesh(float[] verts, int[] faces, float[] normals){       
        // System.out.printf("TriMesh::TriMesh()\n");

        
        /// Setup vertex buffer
        this.vertices = BufferUtil.newFloatBuffer(verts.length);
        this.vertices.put(verts);
        this.vertices.rewind();
        
        /// Setup face buffer
        this.nfaces = faces.length;
        this.faces = BufferUtil.newIntBuffer(faces.length);
        this.faces.put(faces);
        this.faces.rewind();
        
        if(normals != null){
            this.normals = BufferUtil.newFloatBuffer(normals.length);
            this.normals.put(normals);
            this.normals.rewind();
            this.hasnormals = true;
        }
        
        /// Test input data
        // for(int i=0; i<faces.length; i+=3)
        //    System.out.printf("i=" + i + " [" + faces[i] + " " + faces[i+1] + " " + faces[i+2] + "]\n"); 
    }
	
	public void init(GL gl){}
    
	/// @todo java only has INT, could the glDrawElements call generate problems?
    public void draw(GL gl){
        // System.out.printf("TriMesh::draw()\n");
 		if(vertices==null) return;
        
        gl.glColor3f(1.0f,0.0f,0.0f);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
        if(hasnormals) gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
        if(hasnormals) gl.glNormalPointer(GL.GL_FLOAT, 0, normals);
        if(nfaces>0) gl.glDrawElements(GL.GL_TRIANGLES, nfaces, GL.GL_UNSIGNED_INT, faces);
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        if(hasnormals) gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
	}
}