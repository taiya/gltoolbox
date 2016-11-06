// http://jogamp.org/deployment/v2.3.2/javadoc/jogl/javadoc/
// see jogl-demos/src/redbook/src/glredbook11/texbind.java
import javax.swing.JFrame; //< for standalone test only
import com.jogamp.opengl.GL; //< basic enumerations
import com.jogamp.opengl.GL2; //< MATLAB didn't update beyond OpenGL2 :(
import com.jogamp.opengl.awt.GLCanvas; //< main class
import com.jogamp.opengl.GLEventListener; //< gl init/display
import com.jogamp.opengl.GLAutoDrawable; //< ?? meaning ??
import com.jogamp.common.nio.Buffers; //< vertex arrays (initialization)
import java.nio.FloatBuffer; //< vertex arrays
import java.nio.IntBuffer; //< index arrays
import java.util.Vector; //< store render objects
import java.awt.event.MouseWheelEvent;
import javax.swing.event.MouseInputAdapter;

// import com.jogamp.opengl.math.Matrix4; //< TODO

public class JGLCanvas extends GLCanvas implements GLEventListener{   
    /// Set of objects to be rendered
    Vector<RenderObject> objects = new Vector<RenderObject>();
    /// Simple view transformation
    Float angle = new Float(0);
    
    /// Constructor
    public JGLCanvas() {
        // init/display events sent to this object
        this.addGLEventListener(this);
    }
       
    public void init(GLAutoDrawable drawable) {
        // Initialize the global GL references
        GL2 gl = drawable.getGL().getGL2();
        
        // Background
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        // Depth Buffer Setup
		gl.glEnable(GL.GL_DEPTH_TEST); ///< enable depth buffering
		gl.glDepthMask(true);          ///< depth buffer writable
		gl.glDepthRange(0.0f, 1.0f);   ///< depth value is [0,1]
		gl.glDepthFunc(GL.GL_LEQUAL);  ///< closest geometry kept
		gl.glClearDepth(1.0f);		   ///< clear with this value
        
        //--- Simple lighting
        // Light position (w=0 implies *parallel* light source)
        float[] light0_pos = { 0f, 0f, 1f, 0f }; 
		float[] light0_amb = { 0.2f, 0.2f, 0.2f, 1f };
        float[] light0_dif = { 1f, 1f, 1f, 1f };
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_pos, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light0_amb, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_dif, 0);
        
        // Back faces receive light
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
        
        //--- Handlers
        MouseHandler mh = new MouseHandler(drawable, this);
        this.addMouseListener(mh);
        this.addMouseMotionListener(mh);
		this.addMouseWheelListener(mh);
    }
    
    public void dispose(GLAutoDrawable drawable) {}
       
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        /// Frustum [-1:1] mapped to full window
		gl.glViewport(0, 0, width, height);

		/// Setup the projection matrix
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		double r = ((double) width) / ((double) height);
        gl.glOrtho(-r, r, -1, 1, -10, 10);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
        gl.glRotatef(angle.floatValue(), 0.0f,1.0f,0.0f);
        for (int i = 0; i < objects.size(); i++){
            gl.glPushMatrix();
                objects.elementAt(i).display(drawable);
            gl.glPopMatrix();
        }
        gl.glFlush();
        
        for(; gl.glGetError()!=GL.GL_NO_ERROR;)
            System.out.println("OpenGL error detected");
        
    }
    
    public PointCloud draw_cloud(float[] V, float[] C){
        objects.add(new PointCloud(V,C));
        return (PointCloud) objects.lastElement();
    }
       
    public Mesh draw_mesh(float[] V, int[] F){
        objects.add(new Mesh(V,F));
        return (Mesh) objects.lastElement();
    }
    
    /// Superclass of any object to be rendered
    abstract class RenderObject{ 
        boolean has_init = false;
        public void display(GLAutoDrawable drawable){
            if(has_init==false){ init(drawable); has_init=true; }
            draw(drawable);
        }
        /// Abstract interfaces
        abstract protected void init(GLAutoDrawable drawable);
        abstract protected void draw(GLAutoDrawable drawable); 
    }
    
    public class PointCloud extends RenderObject{
        protected int n_points = 0;
        protected FloatBuffer v_buffer = null;
        protected FloatBuffer c_buffer = null;
        
        public PointCloud(float[] vpoints, float[] vcolors){
            this.n_points = vpoints.length/3;
            this.v_buffer = Buffers.newDirectFloatBuffer(vpoints.length);
            this.v_buffer.put(vpoints);
            this.v_buffer.rewind();
            this.c_buffer = Buffers.newDirectFloatBuffer(vcolors.length);
            this.c_buffer.put(vcolors);
            this.c_buffer.rewind();
        }
        protected void init(GLAutoDrawable drawable){}            
        public void draw(GLAutoDrawable drawable){
            GL2 gl = drawable.getGL().getGL2();
            gl.glPointSize(3);
            gl.glDisable(GL2.GL_LIGHTING);
            gl.glPushClientAttrib( (int) GL2.GL_CLIENT_ALL_ATTRIB_BITS);
                gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
                gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
                gl.glVertexPointer(3, GL2.GL_FLOAT, 0, v_buffer);
                gl.glColorPointer(3, GL2.GL_FLOAT, 0, c_buffer);
                gl.glDrawArrays(GL2.GL_POINTS, 0, n_points);
            gl.glPopClientAttrib();
        }
    }
    
    public class Mesh extends RenderObject {
        // @todo Improve using IntBuffer.wrap()
        private FloatBuffer vertices = null; //< vertex coordinates
        private FloatBuffer normals = null; //< per-vertex normals
        private FloatBuffer bVT = null; //< per-vertex tex coordinates
        private IntBuffer faces = null; //< faces (index buffer)
            
        ///--- Texture
        int tex_w = 0;
        int tex_h = 0;
        private int[] texName = new int[1];        
        private FloatBuffer bTEX = null;
        
        public Mesh(float[] V, int[] F) {
            //--- 
            this.vertices = Buffers.newDirectFloatBuffer(V.length);
            this.vertices.put(V);
            this.vertices.rewind();
            //--- 
            this.faces = Buffers.newDirectIntBuffer(F.length);
            this.faces.put(F);
            this.faces.rewind();
        }
        
        public void set_vnormals(float[] VN){
            this.normals = Buffers.newDirectFloatBuffer(VN.length);
            this.normals.put(VN);
            this.normals.rewind();
        }
        
        public void set_vtexcoords(float[] VT){
            this.bVT = Buffers.newDirectFloatBuffer(VT.length);
            this.bVT.put(VT);
            this.bVT.rewind();
        }

        public void set_texture(int w, int h, float[] data){
            this.bTEX = Buffers.newDirectFloatBuffer(data.length);
            this.bTEX.put(data);
            this.bTEX.rewind();
            this.tex_w = w;
            this.tex_h = h;
        }
        
        protected void init(GLAutoDrawable drawable){
            // System.out.println("init");          
            GL2 gl = drawable.getGL().getGL2();
            gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
            gl.glGenTextures(1, texName, 0);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
            gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB32F, tex_w, tex_h, 0, 
                            GL2.GL_RGB, GL.GL_FLOAT, bTEX);   
            gl.glEnable(GL2.GL_TEXTURE_2D);
        }
        
        public void draw(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();
            // gl.glColor3f(1.0f, 0.0f, 0.0f);
                    
            ///--- Bind textures
            gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);         
            if (normals==null) gl.glDisable(GL2.GL_LIGHTING); else gl.glEnable(GL2.GL_LIGHTING);
            
            ///--- setup state
            gl.glPushClientAttrib( (int) GL2.GL_CLIENT_ALL_ATTRIB_BITS); //< https://java.net/jira/browse/JOGL-374
                gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
                gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
                if(normals!=null){
                    gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
                    gl.glNormalPointer(GL2.GL_FLOAT, 0, normals);
                }
                if(bVT!=null){
                    gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
                    gl.glTexCoordPointer(2,GL2.GL_FLOAT, 0, bVT);
                }
                gl.glDrawElements(GL2.GL_TRIANGLES, faces.limit(), GL2.GL_UNSIGNED_INT, faces);                
            gl.glPopClientAttrib();
        }
        
        public void draw_SIMPLE(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();
                       
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
            gl.glScaled(.5,.5,.5);
            gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2d(0.0, 0.0);
                gl.glVertex3d(-2.0, -1.0, 0.0);
                gl.glTexCoord2d(0.0, 1.0);
                gl.glVertex3d(-2.0, 1.0, 0.0);
                gl.glTexCoord2d(1.0, 1.0);
                gl.glVertex3d(0.0, 1.0, 0.0);
                gl.glTexCoord2d(1.0, 0.0);
                gl.glVertex3d(0.0, -1.0, 0.0);
            gl.glEnd();
            gl.glFlush();
        }
    }
    
    public class MouseHandler extends MouseInputAdapter {
        JGLCanvas canvas = null;
        GLAutoDrawable drawable = null;
        MouseHandler(GLAutoDrawable drawable, JGLCanvas canvas){
            this.drawable = drawable;
            this.canvas = canvas; 
        }
        public void mouseWheelMoved(MouseWheelEvent e) {
            int notches = e.getWheelRotation();
            canvas.angle += notches;
            this.drawable.display();
        }
    }
    
    // Standalone test entry point
	public static void main(String[] args) {
		JGLCanvas view3 = new JGLCanvas();
		JFrame frame = new JFrame("JGLCanvas");
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(view3);
		frame.setVisible(true);
	}
}
