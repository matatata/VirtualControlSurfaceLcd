package de.ceruti.midi.core;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MidiPortSelectorPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 9139396665294592313L;
	private JComboBox cbox;
	private Notifications listener;
	private Object context;
	
	public static interface Notifications {
		void portSelected(int index, String name, Object context, MidiPortSelectorPanel sender);
	}
	
	public MidiPortSelectorPanel(String title,String[] ports,Notifications listener, Object context) {
		super();
		
		this.context = context;
		
		String[] items = new String[ports.length+1];
		System.arraycopy(ports, 0, items, 0, ports.length);
		items[items.length-1] = "";
		
		this.listener = listener;
		cbox = new JComboBox(items);
		
		cbox.setSelectedIndex(items.length-1);
		
		if(title!=null)
			setBorder(BorderFactory.createTitledBorder(title));
		
		add(cbox);
		
		cbox.addActionListener(this);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(listener!=null)
			listener.portSelected(cbox.getSelectedIndex(),(String)cbox.getSelectedItem(),context,this);
	}
	
	
	public static void main(String[] args) {
		IMidiCentral central = new LCDMidiCentral("mmf src","mmf dest");
		
		JFrame frame = new JFrame();
		
		frame.setLayout(new FlowLayout());
		
		frame.add(new MidiPortSelectorPanel("Inputs", central.getIns(),null,null));
		frame.add(new MidiPortSelectorPanel("Outputs", central.getOuts(),null,null));
		
		frame.pack();
		
//		frame.getRootPane().putClientProperty("Window.alpha", new Float(0.25));
		
		frame.setVisible(true);
	}

	public String getSelectedPortName(){
		String item = (String) cbox.getSelectedItem();
		
		if(item==null || item.length()==0)
			return null;
		
		
		return item;
	}


	public void setSelectedPortName(String port) {
		cbox.setSelectedItem(port);
	}
	
	public int getSelectedPortIndex() {
		return cbox.getSelectedIndex();
	}
	
}
