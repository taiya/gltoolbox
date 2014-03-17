import javax.media.opengl.GL;

/// Shaders
//import java.io.BufferedReader; 
//import java.io.FileReader;
//import javax.media.opengl.GLContext;

public interface ObjectRenderer{
	public void init(GL gl);
	public void draw(GL gl);
}