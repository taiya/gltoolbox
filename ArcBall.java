import javax.media.opengl.GL;

public class Arcball extends Object {
	double alpha = .5;
	double[] xy_color = {1.0,0.0,0.0,alpha};
	double[] yz_color = {0.0,1.0,0.0,alpha};
	double[] xz_color = {0.0,0.0,1.0,alpha};
	
	public void draw(GL gl){
		// gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glDisable(GL.GL_LIGHTING);

		// X-Y circle
		gl.glColor4dv(xy_color,0);
		gl.glPushMatrix();
			draw_trackball_circle(gl);
		gl.glPopMatrix();
		
		// Y-Z circle
		gl.glColor4dv(yz_color,0);
		gl.glPushMatrix();
			gl.glRotated(90, 0, 1, 0);
			draw_trackball_circle(gl);
		gl.glPopMatrix();
		
		gl.glColor4dv(xz_color,0);
		gl.glPushMatrix();
			gl.glRotated(90, 1, 0, 0);
			draw_trackball_circle(gl);
		gl.glPopMatrix();
	}
	
	public static void draw_trackball_circle(GL gl){
		draw_circle(gl);
		gl.glPushMatrix();
			gl.glPushMatrix();
				gl.glTranslated(1.0,1.0,0.0);
				gl.glRotated(45,0.0,0.0,1.0);
				gl.glScaled(.1,.1,.1);
				draw_square(gl);
			gl.glPopMatrix();
			gl.glPushMatrix();
				gl.glTranslated(-1.0,-1.0,0.0);
				gl.glRotated(45,0.0,0.0,1.0);
				gl.glScaled(.1,.1,.1);
				draw_square(gl);
			gl.glPopMatrix();
		gl.glPopMatrix();
	}
	
	public static void draw_circle(GL gl){
		final int nside=100;
		final double pi2=Math.PI*2;
		gl.glBegin(GL.GL_LINE_LOOP);
		for(double i=0;i<nside;i++){
			gl.glNormal3d(Math.cos(i*pi2/nside), Math.sin(i*pi2/nside), 0.0);
			gl.glVertex3d(Math.cos(i*pi2/nside), Math.sin(i*pi2/nside), 0.0);
		}
		gl.glEnd();
	}
	
	public static void draw_square(GL gl) {
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glNormal3f(0.0f,0.0f,1.0f);
			gl.glVertex3f(-1,+1,0);
			gl.glVertex3f(+1,+1,0);
			gl.glVertex3f(+1,-1,0);
			gl.glVertex3f(-1,-1,0);
		gl.glEnd();
	}
}