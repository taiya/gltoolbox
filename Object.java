import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

/**
 * Objects that you would like to render should inherit this class
 */
public abstract class Object {
	/** Call this method when you need to update the GL context, for example after you have changed 
	 *  a renderer setting in your object*/
	public void display(){ if(drawable!=null) drawable.display(); }
	
	/** The context that renders this objects (initialized when object is added) */
	protected GLAutoDrawable drawable = null;

	/** By default objects are red (change as needed) */
	protected double[] color = { 1, 0, 0 };

	/** Override this function and draw the object */
	abstract public void draw(GL2 gl);
}