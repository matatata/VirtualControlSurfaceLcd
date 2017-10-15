package de.ceruti.mackie.view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.awt.TextRenderer;

import de.ceruti.curcuma.api.appkit.view.cells.NSCell;
import de.ceruti.curcuma.appkit.widgets.swing.NSCellFactory;
import de.ceruti.curcuma.appkit.widgets.swing.PlugInFactory;
import de.ceruti.curcuma.foundation.NSObjectImpl;
import de.ceruti.curcuma.keyvaluebinding.DefaultBindingOptions;
import de.ceruti.curcuma.keyvalueobserving.PostKVONotifications;
import de.ceruti.mackie.ILCDView;
import de.ceruti.mackie.MackieLCD;

public class MackieLCDWindow extends NSObjectImpl implements GLEventListener, ILCDView {
	
	private GLCanvas canvas = new GLCanvas(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
	private GLU glu = new GLU();
	private JFrame frame;
	private TextRenderer mainRenderer;
	private TextRenderer renderer2;

	
	
	private int marginX = 3;
	private int marginV = 3;
	private String font = Font.MONOSPACED;
//			"Lucida Console"; 
			
//			Font.MONOSPACED;
	// "Arial Unicode MS";//"Lucida Console";
	private float scale = 1.0f;
	
	
	//ILCDView Properties
	private boolean antiAliased = true;
	
	private float interChanWeight = 0.0f;
	private boolean alwaysOnTop = true;
	private Color background = Color.blue, foreground = Color.white;
	private String title;
	private float alpha = 1.0f;
	private boolean undecorated = false;
	private Rectangle bounds;
	
	private int lines = 2;
	
	//computed values
	private float charWidth;
	private float spaceToDistribute;
	private float interCharWeight() {return 1.0f - interChanWeight;}
	private int fontSize = 20;
	private float fontSize() { return (float)fontSize * scale;}
	private int linespacing;
	private float bytesPerRowF;
	private float interCharacterSpace;
	private float interChannelSpace;

	
	private int lineSpacing(){
		return (int)fontSize() + linespacing;
	}
	
	
	public void show(boolean b) {
		if(frame!=null)
			frame.setVisible(b);
	}

	
	private void calc(TextRenderer mainRenderer,GLAutoDrawable gLDrawable){
		charWidth = mainRenderer.getCharWidth('x') * scale; //mono
		spaceToDistribute = (gLDrawable.getWidth() -  (int)(bytesPerRowF*charWidth) - marginX*2);
		
		//distribute
		interCharacterSpace = spaceToDistribute*interCharWeight()/(bytesPerRowF - 1.0f);
		interChannelSpace = spaceToDistribute*interChanWeight/7;
		
		
		linespacing = (gLDrawable.getHeight() - lines*(int)(fontSize()) - 2*marginV) / (lines-1);
	}
	
	interface Handler {
		void drawChar(TextRenderer r,char c);
	}
	
	void drawLine(String s,TextRenderer mainRenderer,GLAutoDrawable gLDrawable,int row,int baseY,Handler handler){
		calc(mainRenderer,gLDrawable);
		
		GL gl = gLDrawable.getGL();
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		gl.glEnable(GL.GL_CULL_FACE);
		
		//row1
		float curs = 0.0f;
		for(int i=0;i<s.length();i++){
			char c = s.charAt(i);
			float chSpace = (i%7)==0 && i>0 && i<bytesPerRow ? interChannelSpace : 0.0f;
			curs = curs+chSpace;
			
			
			
			if(handler!=null)
				handler.drawChar(mainRenderer,c);
			
//			mainRenderer.draw(String.valueOf(c), (int)(curs + marginX + (interCharacterSpace+charWidth)*(float)i), baseY - lineSpacing()*row);	
			
//			if(c>=0 && c <= 127)
				mainRenderer.draw3D(String.valueOf(c), (float)(curs + marginX + (interCharacterSpace+charWidth)*(float)i), (float) (baseY - lineSpacing()*row), (float)0.0f,scale);

		}
	}
	
	public void display(GLAutoDrawable gLDrawable) {
		
		
		final GL2 gl = gLDrawable.getGL().getGL2();
		glClearColor(gl,getBackground());
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		 
		
		String s = new String(displayData,0,bytesPerRow);
		String s2 = new String(displayData,bytesPerRow,bytesPerRow);
		
		
		
		
		int y = gLDrawable.getHeight() - (int)fontSize() - marginV;
		
//		mainRenderer.beginRendering(gLDrawable.getWidth(), gLDrawable.getHeight());
		mainRenderer.begin3DRendering();
		
		mainRenderer.setColor(getForeground());
		
		bytesPerRowF = this.bytesPerRow;

		
		drawLine(s, mainRenderer, gLDrawable, 0,y,null);
		drawLine(s2, mainRenderer, gLDrawable, 1,y,null);
		
		mainRenderer.end3DRendering();
//		mainRenderer.endRendering();

		//########
		
		if(lines == 3) {
		
			gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f(0.0f, 0.0f,0.0f);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(  gLDrawable.getWidth(),0, 0);
			gl.glVertex3f( gLDrawable.getWidth(),  fontSize(), 0);
			gl.glVertex3f(0,  fontSize(), 0);
			gl.glEnd();
			
			String s3   = "  S      S      S R    S R           S R                ";
			
			String tmpl = "M S R  M S R  M S R  M S R  M S R  M S R  M S R  M S R  ";
			
			renderer2.beginRendering(gLDrawable.getWidth(), gLDrawable.getHeight());
			renderer2.setColor(getBackground().darker());
			drawLine(tmpl, renderer2, gLDrawable, 2,y,null);
		
			
			drawLine(s3, renderer2, gLDrawable, 2,y,new Handler() {
				
				public void drawChar(TextRenderer r, char c) {
					switch(c){
					case 'M':
						r.setColor(Color.cyan);
						break;
					case 'R':
						r.setColor(Color.red);
						break;
					case 'S':
						r.setColor(Color.yellow);
						break;	
					}	
				}
			});
			renderer2.endRendering();
		}
		gl.glFlush();
	}

	public void displayChanged(GLAutoDrawable gLDrawable, 
			boolean modeChanged, boolean deviceChanged) {
	}
	
	/**
	 * Sets the given Color as the Clear-Color to GL
	 * @param gl
	 * @param bg
	 */
	private void glClearColor(GL gl,Color bg){
		gl.glClearColor((float)bg.getRed()/255.0f,(float)bg.getGreen()/255.0f, (float)bg.getBlue()/255.0f, (float)bg.getAlpha()/255.0f);
	}
	
	
	

	public void init(GLAutoDrawable gLDrawable) {
		GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glShadeModel(GL2.GL_SMOOTH);
		
		
		
		

	}

	public void reshape(GLAutoDrawable gLDrawable, int x, 
			int y, int w, int h) {
		GL2 gl = gLDrawable.getGL().getGL2();
		
	    gl.glViewport(0, 0, w, h);
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
//	    if (w <= h) glu.gluOrtho2D(0.0, 30.0, 0.0, 30.0 * (float) h / (float) w);
//	    else glu.gluOrtho2D(0.0, 30.0 * (float) w / (float) h, 0.0, 30.0);
	    
	    glu.gluOrtho2D(0.0, w , 0.0, h);
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	
	public boolean isUndecorated() {
		return undecorated;
	}

	@PostKVONotifications
	public void setUndecorated(boolean undecorated) {
		if(this.undecorated!=undecorated){
			int oldh=canvas.getHeight();
			
			if(!undecorated){
				setAlpha(1.0f);
			}
			
			dispose();
			this.undecorated = undecorated;
			
			init();
			
			
			int diff=canvas.getHeight()-oldh;
			
			if(undecorated){
				frame.setSize(frame.getWidth(), oldh);
			}else{
				frame.setSize(frame.getWidth(), oldh - diff);
			}
			frame.setLocation(frame.getLocation().x, frame.getLocation().y+diff);
			
		}
	}



	private Nofitications listener;
	
	public Nofitications getListener() {
		return listener;
	}

	public void setListener(Nofitications listener) {
		this.listener = listener;
	}
	
	public boolean isAntialiased() {
		return antiAliased;
	}
	
	@PostKVONotifications
	public void setAntialiased(boolean antiAliased) {
		if(this.antiAliased!=antiAliased){
			dispose();
			this.antiAliased = antiAliased;
			init();
		}
	}
	
	@PostKVONotifications
	public void setFontsize(int s) {
		if(this.fontSize != s){
			dispose();
			this.fontSize = s;
			init();
		}
	}
	
	public int getFontsize() {
		return this.fontSize;
	}
	
	public float getInterChanWeight() {
		return interChanWeight;
	}
	
	@PostKVONotifications
	public void setInterChanWeight(float interChanWeight) {
		this.interChanWeight = interChanWeight;
		redraw();
	}


	public boolean isAlwaysOnTop() {
		return alwaysOnTop;
	}

	@PostKVONotifications
	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
		frame.setAlwaysOnTop(alwaysOnTop);
	}

	@PostKVONotifications
	public void setAlpha(float a) {
			this.alpha = a;
		if(!this.undecorated){
			this.alpha=1.0f;
		}
			frame.getRootPane().putClientProperty("Window.alpha", a);
			redraw();
//		}
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public Color getBackground() {
		return background;
	}
	
	@PostKVONotifications
	public void setBackground(Color background) {
		this.background = background;
		redraw();
	}

	public Color getForeground() {
		return foreground;
	}

	@PostKVONotifications
	public void setForeground(Color foreground) {
		this.foreground = foreground;
		redraw();
	}

	
	public Rectangle getBounds() {
		if(frame==null)
			return bounds;
		return bounds = frame.getBounds();
	}

	public void setBounds(Rectangle bounds) {
		frame.setBounds(bounds);
		this.bounds=bounds;
	}

	public String getTitle() {
		return title;
	}

	@PostKVONotifications
	public void setTitle(String title) {
		this.title = title;
		frame.setTitle(title);
	}

	private final int rows = 2;
	private final int bytesPerRow = MackieLCD.CharsPerRow;
	private final byte[] displayData = new byte[rows * bytesPerRow];
	
	
	public MackieLCDWindow(Nofitications listener,String title,Color fg,Color bg, float alpha,Rectangle bounds, boolean onTop, boolean undecorated) {
		this.alpha=alpha;
		this.title=title;
		this.background=bg;
		this.foreground=fg;
		this.alwaysOnTop=onTop;
		this.listener = listener;
		this.undecorated = undecorated;
		
		System.setProperty("sun.awt.noerasebackground", "true");
		 
		
		init(false);
		createMenuBar();
		
		
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);
				if(arg0.getClickCount()>1 && getListener()!=null){
					getListener().configure(MackieLCDWindow.this);
				}
			}
		});

		MouseListener popupListener = new PopupListener();
		canvas.addMouseListener(popupListener);

		
		

	}
	

	private Stickie stickie = new Stickie();
	
	private void dispose() {
		
		stickie.disconnect();
		getBounds();//save bounds
		frame.remove(canvas);
		frame.setVisible(false);
		frame.dispose();
		frame=null;
		
		
	}
	
	
	private void init() {
		init(true);
	}
	private void init(boolean visible) {
		
		Rectangle oldBounds = getBounds();
		
		mainRenderer = new TextRenderer(new Font(font, Font.BOLD, (int)(fontSize()/scale)),antiAliased,false);
		renderer2 = new TextRenderer(new Font(font, Font.BOLD, (int)(fontSize()/scale)),antiAliased,false);

		
		frame = new JFrame(getTitle());
		frame.add(canvas);
		
		if(oldBounds==null){
			frame.setSize(900, (int)(fontSize()*3) + linespacing*2 + marginV*2);
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation((screenSize.width - frame.getWidth()) / 2,
					(screenSize.height - frame.getHeight()) / 2);
			setBounds(frame.getBounds());
		}
		else{
			frame.setBounds(oldBounds);
		}
		
		frame.setUndecorated(isUndecorated());
		frame.setAlwaysOnTop(isAlwaysOnTop());
		
		
		setAlpha(getAlpha());
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				displayText("Cannot close");
			}
		});
		

		

		
		
		frame.requestFocus();
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		canvas.addGLEventListener(this);
		
		stickie.connect(frame,canvas);
		
//		String s  = "Audio1 Audio2 Audio3 Audio4 Audio5 Audio6 Audio7 Audio8 ";
//		String s2 = "--     0db    --     Volume --     Pan    --     ------ ";
//
//		updateDisplayData(0, s.getBytes(), 0, s.length());
//		updateDisplayData(bytesPerRow, s2.getBytes(), 0, s2.length());
//		
		
		frame.setVisible(visible);
	}
	
	public void displayText(String s){
		StringBuffer buf = new StringBuffer(s);
		
//		if(buf.length()>bytesPerRow*2)
//			s = s.substring(0,bytesPerRow*2);

		buf.setLength(Math.max(bytesPerRow*2, buf.length()));

		while(buf.length() < bytesPerRow*2)
			buf.append(" ");
		
		s = buf.toString();
		updateDisplayData(0, s.getBytes(), 0, s.length());
	}
	
	
	class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            popup.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}

	public void updateDisplayData(int lcdPos, byte[] data, int offset, int n){
		
		
		updateDisplayData(lcdPos, data, offset, n,true);
	}
	
	private void updateDisplayData(int lcdPos, byte[] data, int offset, int n, boolean update) {
		for(int i=0 ; i<n ;i++){
    		displayData[lcdPos + i] = data[offset + i];
    	}
		
		if(update)
			redraw();
	}
	
	public void redraw() {
		canvas.display();
	}
	
	private JPopupMenu popup;
 
	private void createMenuBar() {

		popup = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Configure");
		item.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(getListener()!=null)
					getListener().configure(MackieLCDWindow.this);
			}
		});
		popup.add(item);
		
		item = new JCheckBoxMenuItem("Always on top");
		
		NSCellFactory.create(PlugInFactory.get()).createCellForComponent(item).bind(
				NSCell.CellValueBinding, this, "alwaysOnTop",
				new DefaultBindingOptions());

		popup.add(item);
	}


	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	
	


}
