import javax.media.opengl.GL;

/** Renders an arcball a-la MeshLab */
public class Arcball extends Object {
	float alpha = 1.0f;
	float[] diffuse  = {.5f, .5f, .5f};
	float[] xy_color = {1.0f,0.0f,0.0f,alpha};
	float[] yz_color = {0.0f,1.0f,0.0f,alpha};
	float[] xz_color = {0.0f,0.0f,1.0f,alpha};
	
	public void xy_material(GL gl){
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, xy_color, 0);		
	}
	public void yz_material(GL gl){
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, yz_color, 0);		
	}
	public void xz_material(GL gl){
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, xz_color, 0);		
	}
	
	public void draw(GL gl){
		/// Don't show lines as segments
		gl.glEnable(GL.GL_LINE_SMOOTH);
		
		/// Use lighting (helps differentiate front/back circles)
		gl.glEnable(GL.GL_LIGHTING);
		
		/// TODO alpha blending
		// gl.glDisable(GL.GL_BLEND);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				
		gl.glPushMatrix();
			xy_material(gl);
			draw_trackball_circle(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
			yz_material(gl);
			gl.glRotated(90, 0, 1, 0);
			draw_trackball_circle(gl);
		gl.glPopMatrix();
		
		gl.glColor4fv(xz_color,0);
		gl.glPushMatrix();
			xz_material(gl);
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
			/// TODO why [0,0,1] doesn't work properly?
			gl.glNormal3f(0.0f,0.0f,0.0f);
			gl.glVertex3f(-1,+1,0);
			gl.glVertex3f(+1,+1,0);
			gl.glVertex3f(+1,-1,0);
			gl.glVertex3f(-1,-1,0);
		gl.glEnd();
	}
}