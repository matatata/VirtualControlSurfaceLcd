import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;
 
public class BitMapFont
{
  public static void main(String[] args)
  {
    Frame frame = 
    	new Frame("BitMap Fonts");
    final GLProfile profile = GLProfile.get(GLProfile.GL2);
    GLCapabilities capabilities = new GLCapabilities(profile);
             
    // The canvas
    final GLCanvas canvas = new GLCanvas(capabilities);
//    GLCanvas canvas = new GLCanvas();
    Renderer renderer = new Renderer();
	canvas.addGLEventListener(renderer);
	frame.addKeyListener(renderer);
    frame.add(canvas);
    frame.setSize(400, 300);
   
    frame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
       
        System.exit(0);
      }
    });
    frame.show();
    canvas.requestFocus();
   
  }
 
  static class Renderer implements GLEventListener, KeyListener
  {
   
       
    public void display(GLAutoDrawable gLDrawable, boolean b, boolean b2)
    {
      String [] fonts = { "BitMap 9 by 15", "BitMap 8 by 13",
            "Times Roman 10 Point ", "Times Roman 24 Point ",
            "Helvetica 10 Point ","Helvetica 12 Point ","Helvetica 18 Point "};
           
     
      final GL2 gl = gLDrawable.getGL().getGL2();
      final GLUT glut = new GLUT();
 
      gl.glClear (GL.GL_COLOR_BUFFER_BIT);  // Set display window to color.
      gl.glColor3f (0.0f, 0.0f, 0.0f);  // Set text e.color to black
      gl.glMatrixMode (GL2.GL_MODELVIEW);
      gl.glLoadIdentity();
     
      int x = 20, y=15;
      for (int i=0; i<7;i++){
           
            gl.glRasterPos2i(x,y); // set position
     
            glut.glutBitmapString( i+2, fonts[i]);
      
             y+= 20;
      }
    
    }
   
   
       public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged)
    {
    }
   
    public void init(GLAutoDrawable gLDrawable)
    {
      final GL2 gl = gLDrawable.getGL().getGL2();
      final GLU glu = new GLU();
           
      gl.glMatrixMode (GL2.GL_PROJECTION); 
      gl.glClearColor (1.0f, 1.0f, 1.0f, 0.0f);   //set background to white
      glu.gluOrtho2D (0.0, 200.0, 0.0, 150.0);  // define drawing area
//      gLDrawable.addKeyListener(this);
    }
   
       public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height)
    {
     
           
    }
 
   
    public void keyPressed(KeyEvent e)
    {
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        System.exit(0);
    }
      
    public void keyReleased(KeyEvent e) {}
   
    public void keyTyped(KeyEvent e) {}


	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}


	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}


	


      }
 
 
}