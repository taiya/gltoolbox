import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;

//import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

/// Arcball
import java.awt.Point;
//import java.util.Iterator;
import java.util.Vector;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Quat4f;

//import com.sun.org.apache.xml.internal.utils.ObjectStack;

/// Shaders
//import java.io.BufferedReader; 
//import java.io.FileReader;
//import javax.media.opengl.GLContext;

/// Buffered rendering
//http://www.java-tips.org/other-api-tips/jogl/vertex-buffer-objects-nehe-tutorial-jogl-port-2.html
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import com.sun.opengl.util.BufferUtil;

public class View3 extends GLCanvas{
    private Renderer rend = new Renderer();
    private static final long serialVersionUID = 1L;
	
    public View3(){
        this.addGLEventListener(rend);
        /// Control
        Mouse mlist = new Mouse(this,rend);
        this.addMouseListener(mlist);
        this.addMouseMotionListener(mlist);
    }

    /// Creates a mesh without surface normals
    public void add_mesh(float[] verts, int[] faces){
        add_mesh(verts,faces,null);
    }
    
    /// Creates a mesh with surface normals
    public void add_mesh(float[] verts, int[] faces, float[] normals){
    	TriMesh mesh = new TriMesh(verts,faces,normals);
    	rend.add_render_object(mesh);
    }
    
	public static void main(String[] args) {
		System.out.printf("HELLO");
		View3 view = new View3();
		// view.show();
	}
	    
    public interface RenderObjectIFace{
    	public void init(GL gl);
    	public void draw(GL gl);
    }
       
    public class TriMesh implements RenderObjectIFace{
    	private FloatBuffer vertices = null;
    	private FloatBuffer normals = null;
    	private IntBuffer faces = null;
        private int nfaces = 0;
        private boolean hasnormals = false;
        
        /// @todo Improve using IntBuffer.wrap()
    	TriMesh(float[] verts, int[] faces, float[] normals){       
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

    static void draw_cube(GL gl){
        gl.glBegin(GL.GL_QUADS);           	// Draw A Quad
            gl.glColor3f(0.0f, 1.0f, 0.0f);			// Set The Color To Green
            gl.glVertex3f(1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Top)
            gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Left Of The Quad (Top)
            gl.glVertex3f(-1.0f, 1.0f, 1.0f);			// Bottom Left Of The Quad (Top)
            gl.glVertex3f(1.0f, 1.0f, 1.0f);			// Bottom Right Of The Quad (Top)

            gl.glColor3f(1.0f, 0.5f, 0.0f);			// Set The Color To Orange
            gl.glVertex3f(1.0f, -1.0f, 1.0f);			// Top Right Of The Quad (Bottom)
            gl.glVertex3f(-1.0f, -1.0f, 1.0f);			// Top Left Of The Quad (Bottom)
            gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Bottom)
            gl.glVertex3f(1.0f, -1.0f, -1.0f);			// Bottom Right Of The Quad (Bottom)

            gl.glColor3f(1.0f, 0.0f, 0.0f);			// Set The Color To Red
            gl.glVertex3f(1.0f, 1.0f, 1.0f);			// Top Right Of The Quad (Front)
            gl.glVertex3f(-1.0f, 1.0f, 1.0f);			// Top Left Of The Quad (Front)
            gl.glVertex3f(-1.0f, -1.0f, 1.0f);			// Bottom Left Of The Quad (Front)
            gl.glVertex3f(1.0f, -1.0f, 1.0f);			// Bottom Right Of The Quad (Front)

            gl.glColor3f(1.0f, 1.0f, 0.0f);			// Set The Color To Yellow
            gl.glVertex3f(1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Back)
            gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Right Of The Quad (Back)
            gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Back)
            gl.glVertex3f(1.0f, 1.0f, -1.0f);			// Top Left Of The Quad (Back)

            gl.glColor3f(0.0f, 0.0f, 1.0f);			// Set The Color To Blue
            gl.glVertex3f(-1.0f, 1.0f, 1.0f);			// Top Right Of The Quad (Left)
            gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Left Of The Quad (Left)
            gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Left)
            gl.glVertex3f(-1.0f, -1.0f, 1.0f);			// Bottom Right Of The Quad (Left)

            gl.glColor3f(1.0f, 0.0f, 1.0f);			// Set The Color To Violet
            gl.glVertex3f(1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Right)
            gl.glVertex3f(1.0f, 1.0f, 1.0f);			// Top Left Of The Quad (Right)
            gl.glVertex3f(1.0f, -1.0f, 1.0f);			// Bottom Left Of The Quad (Right)
            gl.glVertex3f(1.0f, -1.0f, -1.0f);			// Bottom Right Of The Quad (Right)
        gl.glEnd();				// Done Drawing The Quad
    }
    
    class Renderer implements GLEventListener{
        Renderer(){
            LastRot.setIdentity();                                // Reset Rotation
            ThisRot.setIdentity();                                // Reset Rotation
        }
        
        private Vector<RenderObjectIFace> objects = new Vector<RenderObjectIFace>();
        
        public void add_render_object(RenderObjectIFace obj){
        	objects.add(obj);
        }
        
        private GLU glu = new GLU();
        
        private Matrix4f LastRot = new Matrix4f();
        private Matrix4f ThisRot = new Matrix4f();
        private ArcBall arcBall = new ArcBall(640.0f, 480.0f);
        
        float[] light0_pos = { 0, 0, 100, 1 };          // light position
        float[] light0_amb = { 0.2f, 0.2f, 0.2f, 1f };  // low ambient light
        float[] light0_dif = { 1f, 1f, 1f, 1f };        // diffuse colour
        

        // PROBLEMS :(
        // private GLAutoDrawable drawable = null; ///< WARNING: why do I have to store it?
        
        public void displayChanged(GLAutoDrawable drawable, boolean a, boolean b){
            System.out.printf("View3::displayChanged()\n");
        }
        public void reshape(GLAutoDrawable drawable, int x,int y, int width, int height){
            final double aspect_ratio = 3.0/2.0;
                        
            // int new_height = (int) Math.ceil( width / aspect_ratio );
            // if (new_height <= 0) // avoid a divide by zero error!
            //  new_height = 1;
            
            GL gl = drawable.getGL();
            
            /// Frustum [-1:1] mapped to full window  
            gl.glViewport(0, 0, width, height);
            
            /// Setup the projection matrix
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrtho(-1.0*aspect_ratio, 1.0*aspect_ratio, -1.0, 1.0, -1.0, 1.0);
            // glu.gluPerspective(45.0f, h, 1.0, 20.0);
            
            /// Setup the arcball
            arcBall.setBounds(width, height);
        }

        void get(Matrix4f mat, float[] dest) {
            dest[0] = mat.m00;
            dest[1] = mat.m10;
            dest[2] = mat.m20;
            dest[3] = mat.m30;
            dest[4] = mat.m01;
            dest[5] = mat.m11;
            dest[6] = mat.m21;
            dest[7] = mat.m31;
            dest[8] = mat.m02;
            dest[9] = mat.m12;
            dest[10] = mat.m22;
            dest[11] = mat.m32;
            dest[12] = mat.m03;
            dest[13] = mat.m13;
            dest[14] = mat.m23;
            dest[15] = mat.m33;
        }
        
        public void display(GLAutoDrawable drawable){
            // System.out.printf("View3::display()\n");
            GL gl = drawable.getGL();
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();
            
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            
            // Initial rotation/scale
            // gl.glScalef(.1f,.1f,.1f);
            // gl.glRotatef(30,1,1,1);
            
            /// @todo Can this be avoided?
            float mbuffer[] = new float[16];
            get(ThisRot,mbuffer);
           
            
            gl.glPushMatrix();
                gl.glMultMatrixf( mbuffer,0 );
                // draw_cube(gl);
                for (int i = 0; i < objects.size(); i++)
				   objects.elementAt(i).draw(gl);
            gl.glPopMatrix();
            gl.glFlush();
        }

        public void init(GLAutoDrawable drawable) {
            GL gl = drawable.getGL();                
            System.out.printf("View3::init() "+gl.glGetString(GL.GL_VERSION)+"\n");
            gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); ///< Background
            gl.glColor3f(1.0f, 0.0f, 0.0f); ///< foreground
            gl.glEnable(GL.GL_DEPTH_TEST);
            
            /// Lighting
            gl.glEnable(GL.GL_LIGHTING);
            gl.glEnable(GL.GL_LIGHT0);
            gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,light0_pos, 0);
            gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light0_amb, 0);
            gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light0_dif, 0);
        }
        
        void startDrag( Point MousePt ){
            LastRot.set( ThisRot );
            arcBall.click( MousePt );
        }

        void drag( Point MousePt ){
            Quat4f ThisQuat = new Quat4f();
            arcBall.drag( MousePt, ThisQuat);
            ThisRot.setRotation(ThisQuat);
            ThisRot.mul( ThisRot, LastRot);
        }
    }

    class Mouse extends MouseInputAdapter{
        private Renderer renderer = null;
        private GLCanvas canvas = null;
        public Mouse(GLCanvas canvas, Renderer rend){ 
            this.canvas = canvas;
            this.renderer = rend; 
        }
        
        public void mousePressed(MouseEvent mouseEvent) { 
            renderer.startDrag(mouseEvent.getPoint());
        }
        public void mouseDragged(MouseEvent mouseEvent) { 
            renderer.drag(mouseEvent.getPoint()); 
            canvas.display();
        }
        public void mouseClicked(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}   
    }
    class ArcBall {
        private static final float Epsilon = 1.0e-5f;

        Vector3f StVec;          //Saved click vector
        Vector3f EnVec;          //Saved drag vector
        float adjustWidth;       //Mouse bounds width
        float adjustHeight;      //Mouse bounds height

        public ArcBall(float NewWidth, float NewHeight) {
            StVec = new Vector3f();
            EnVec = new Vector3f();
            setBounds(NewWidth, NewHeight);
        }

        public void mapToSphere(Point point, Vector3f vector) {
            //Copy paramter into temp point
            Vector2f tempPoint = new Vector2f(point.x, point.y);

            //Adjust point coords and scale down to range of [-1 ... 1]
            tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
            tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);

            //Compute the square of the length of the vector to the point from the center
            float length = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y);

            //If the point is mapped outside of the sphere... (length > radius squared)
            if (length > 1.0f) {
                //Compute a normalizing factor (radius / sqrt(length))
                float norm = (float) (1.0 / Math.sqrt(length));

                //Return the "normalized" vector, a point on the sphere
                vector.x = tempPoint.x * norm;
                vector.y = tempPoint.y * norm;
                vector.z = 0.0f;
            } else    //Else it's on the inside
            {
                //Return a vector to a point mapped inside the sphere sqrt(radius squared - length)
                vector.x = tempPoint.x;
                vector.y = tempPoint.y;
                vector.z = (float) Math.sqrt(1.0f - length);
            }

        }

        public void setBounds(float NewWidth, float NewHeight) {
            assert((NewWidth > 1.0f) && (NewHeight > 1.0f));

            //Set adjustment factor for width/height
            adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
            adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
        }

        //Mouse down
        public void click(Point NewPt) {
            mapToSphere(NewPt, this.StVec);

        }

        //Mouse drag, calculate rotation
        public void drag(Point NewPt, Quat4f NewRot) {
            //Map the point to the sphere
            this.mapToSphere(NewPt, EnVec);

            //Return the quaternion equivalent to the rotation
            if (NewRot != null) {
                Vector3f Perp = new Vector3f();

                //Compute the vector perpendicular to the begin and end vectors
                Perp.cross(StVec,EnVec);

                //Compute the length of the perpendicular vector
                if (Perp.length() > Epsilon)    //if its non-zero
                {
                    //We're ok, so return the perpendicular vector as the transform after all
                    NewRot.x = Perp.x;
                    NewRot.y = Perp.y;
                    NewRot.z = Perp.z;
                    //In the quaternion values, w is cosine (theta / 2), where theta is rotation angle
                    NewRot.w = StVec.dot(EnVec);
                } else                                    //if its zero
                {
                    //The begin and end vectors coincide, so return an identity transform
                    NewRot.x = NewRot.y = NewRot.z = NewRot.w = 0.0f;
                }
            }
        }

    }
}	