package de.ceruti.mackie.view;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Stickie extends MouseAdapter implements MouseMotionListener{

	public Stickie() {
	}
	
	public void connect(Container frame,Component clickReceiver){
		if(this.theFrame!=null || this.clickReceiver!=null)
			disconnect();
		
		if(frame==null || clickReceiver==null)
			throw new NullPointerException();
		this.theFrame = frame;
		clickReceiver.addMouseMotionListener(this);
		clickReceiver.addMouseListener(this);
		this.clickReceiver=clickReceiver;
	}
	
	public void disconnect(){
		clickReceiver.removeMouseMotionListener(this);
		clickReceiver.removeMouseListener(this);
		clickReceiver=null;
		theFrame = null;
	}



	
	private Component clickReceiver;
	private Container theFrame = null;
	private boolean mMoveStart = false;
	private boolean mResizeStart = false;
	private Point mMouseClickPoint = null;
	private Dimension oldSize;
	
	public void mouseDragged(MouseEvent mouseEvent) {
		if (mMoveStart)
			_moveWindowTo(mouseEvent.getPoint());
		if (mResizeStart)
			_resizeWindowTo(mouseEvent.getPoint());
	}

	public void mouseMoved(MouseEvent mouseEvent) {
	}
	
	
	public void mousePressed(MouseEvent mouseEvent) {
		mMouseClickPoint = mouseEvent.getPoint();

		if (mMouseClickPoint.getX() > theFrame.getWidth() - 15
				&& mMouseClickPoint.getY() > theFrame.getHeight() - 15) {
			// resize
			mResizeStart = true;
			oldSize = theFrame.getSize();
		} else {
			mMoveStart = true;
		}
	}

	public void mouseReleased(MouseEvent mouseEvent) {
		mMoveStart = false;
		mResizeStart = false;
	}

	private void _moveWindowTo(Point point) {
		int musDiffX = point.x - mMouseClickPoint.x;
		int musDiffY = point.y - mMouseClickPoint.y;

		theFrame.setLocation(theFrame.getLocation().x + musDiffX, theFrame.getLocation().y + musDiffY);
	}
	
	private void _resizeWindowTo(Point point) {
		int musDiffX = point.x - mMouseClickPoint.x;
		int musDiffY = point.y - mMouseClickPoint.y;

		theFrame.setSize(oldSize.width + musDiffX, oldSize.height + musDiffY);
	}
	
	
	
	
	


	public static void main(String[] args) {
		JPanel p = new JPanel();
		JFrame f = new JFrame();
		f.add(p);
		f.pack();
		new Stickie().connect(f,p);
		
		f.setVisible(true);
	}
}