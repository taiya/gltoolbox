import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;

/**
 * Objects that you would like to render should inherit this class
 */
public abstract class Object {
	/** Call this method when you need to update the GL context, for example after you have changed 
	 *  a renderer setting in your object*/
	public void display(){ if(_canvas!=null) _canvas.display(); }
	
	/** The context that renders this objects (initialized when object is added) */
	protected GLCanvas _canvas = null;

	/** By default objects are red (change as needed) */
	protected double[] color = { 1, 0, 0 };

	/** Override this function and draw the object */
	abstract public void draw(GL gl);
}