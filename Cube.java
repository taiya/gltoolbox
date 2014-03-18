import javax.media.opengl.GL;

public class Cube extends Object {
    public void draw(GL gl){
    	gl.glDisable(GL.GL_LIGHTING);
    	gl.glPushMatrix();
    		gl.glScaled(.5, .5, .5);
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
	    gl.glPopMatrix();
    }
}
