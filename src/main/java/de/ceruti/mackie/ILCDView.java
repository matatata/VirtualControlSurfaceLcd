package de.ceruti.mackie;
import java.awt.Color;
import java.awt.Rectangle;


public interface ILCDView {

	public interface Nofitications {
		void configure(ILCDView jFrameLCDView);
		void exit(ILCDView jFrameLCDView);
	}
	
	
	
	void updateDisplayData(int lcdPos, byte[] data, int offset, int n);

	void setTitle(String title);
	String getTitle();
		
	void setBackground(Color c);
	void setForeground(Color c);
	
	Color getBackground();
	Color getForeground();

	boolean isAlwaysOnTop();
	void setAlwaysOnTop(boolean b);
	
	Rectangle getBounds();
	void setBounds(Rectangle r);
	
	float getAlpha();
	void setAlpha(float a);
	
	boolean isAntialiased();
	void setAntialiased(boolean antiAliased);

	float getInterChanWeight();
	void setInterChanWeight(float interChanWeight);
	
	
	void redraw();
	
	void setFontsize(int s);
	int getFontsize();
	
	void displayText(String s);
	
	
	public class Dummy implements ILCDView{

		public final static ILCDView Instance = new Dummy();
		
		
		public void redraw() {
		}

		
		public Color getBackground() {
			return null;
		}

		
		public Rectangle getBounds() {
			return null;
		}

		
		public Color getForeground() {
			return null;
		}

		
		public String getTitle() {
			return null;
		}

		
		public boolean isAlwaysOnTop() {
			return false;
		}

		
		public void setAlwaysOnTop(boolean b) {
		}

		
		public void setBackground(Color c) {
		}

		
		public void setBounds(Rectangle r) {
		}

		
		public void setForeground(Color c) {
		}

		public void setTitle(String title) {
		}

		public void updateDisplayData(int lcdPos, byte[] data, int offset, int n) {
		}

		public float getAlpha() {
			return 1.0f;
		}

		
		public void setAlpha(float a) {
		}

		
		public boolean isUndecorated() {
			return false;
		}

		
		public void setUndecorated(boolean undecorated) {
		}

		
		public float getInterChanWeight() {
			return 0;
		}

		
		public boolean isAntialiased() {
			return false;
		}

		
		public void setAntialiased(boolean antiAliased) {
		}

		
		public void setInterChanWeight(float interChanWeight) {
		}

		
		public int getFontsize() {
			return 0;
		}

		
		public void setFontsize(int s) {
		}


		public void displayText(String s) {
			// TODO Auto-generated method stub
			
		}


		public void show(boolean b) {
			// TODO Auto-generated method stub
			
		}

	}


	void show(boolean b);
	
}
