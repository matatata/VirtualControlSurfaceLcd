

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This program demonstrates smooth shading. A smooth shaded polygon is drawn in
 * a 2-D projection.
 * 
 * @author Kiet Le (java port)
 */
public class smooth
  extends JFrame
    implements GLEventListener, KeyListener
{
  private GLU glu;
  private GLUT glut;
  private GLCapabilities caps;
  private GLCanvas canvas;
  private AnimatorBase animator;

  public smooth()
  {
    super("smooth");
    final GLProfile profile = GLProfile.get(GLProfile.GL2);
     caps = new GLCapabilities(profile);
    
    
    canvas = new GLCanvas(caps);
    canvas.addGLEventListener(this);
    canvas.addKeyListener(this);
   
    getContentPane().add(canvas);
  }

  public void run()
  {
    setSize(500, 500);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
    canvas.requestFocusInWindow();
  }

  public static void main(String[] args)
  {
	 System.setProperty("sun.awt.noerasebackground", "true");
	  
    new smooth().run();
  }

  public void init(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();
    glu = new GLU();
    glut = new GLUT();

    // gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glShadeModel(GL2.GL_SMOOTH);
    
    
    animator = new FPSAnimator(drawable, 60);
    
//    animator.start();
  }

  public void display(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();

    gl.glClear(GL.GL_COLOR_BUFFER_BIT );
    triangle(gl);
    gl.glFlush();
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
  {
    GL2 gl = drawable.getGL().getGL2();

    gl.glViewport(0, 0, w, h);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
//    if (w <= h) glu.gluOrtho2D(0.0, 30.0, 0.0, 30.0 * (float) h / (float) w);
//    else glu.gluOrtho2D(0.0, 30.0 * (float) w / (float) h, 0.0, 30.0);
    
    glu.gluOrtho2D(0.0, 30.0 , 0.0, 30.0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }

  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
      boolean deviceChanged)
  {
  }

  private void triangle(GL2 gl)
  {
    gl.glBegin(GL2.GL_TRIANGLES);
    gl.glColor3f(1.0f, 0.0f, 0.0f);
    gl.glVertex2f(5.0f, 5.0f);
    gl.glColor3f(0.0f, 1.0f, 0.0f);
    gl.glVertex2f(25.0f, 5.0f);
    gl.glColor3f(0.0f, 0.0f, 1.0f);
    gl.glVertex2f(5.0f, 25.0f);
    gl.glEnd();
  }

  public void keyTyped(KeyEvent key)
  {
  }

  public void keyPressed(KeyEvent key)
  {
    switch (key.getKeyCode()) {
      case KeyEvent.VK_ESCAPE:
        System.exit(0);
        break;

      default:
        break;
    }
  }

  public void keyReleased(KeyEvent key)
  {
  }

public void dispose(GLAutoDrawable arg0) {
	// TODO Auto-generated method stub
	
}

}