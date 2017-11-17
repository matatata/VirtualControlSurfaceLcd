package de.ceruti.mackie.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import de.ceruti.curcuma.api.appkit.view.NSControl;
import de.ceruti.curcuma.api.appkit.view.cells.NSBoundedRangeCell;
import de.ceruti.curcuma.api.appkit.view.cells.NSCell;
import de.ceruti.curcuma.api.appkit.view.cells.NSComboCell;
import de.ceruti.curcuma.appkit.view.NSTextFieldImpl;
import de.ceruti.curcuma.appkit.widgets.swing.NSCellFactory;
import de.ceruti.curcuma.appkit.widgets.swing.PlugInFactory;
import de.ceruti.curcuma.appkit.widgets.swing.Utils;
import de.ceruti.curcuma.keyvaluebinding.DefaultBindingOptions;
import de.ceruti.mackie.VirtualControlSurfaceLCD;

public class SettingsView {
	private JFrame frame;
	private MappingWindow mWin;
	NSCellFactory nSCellFactory = NSCellFactory.create(PlugInFactory.get());
	
	public SettingsView(final VirtualControlSurfaceLCD controller) {
		frame = new JFrame(VirtualControlSurfaceLCD.class.getSimpleName());
		JTabbedPane tabPane = new JTabbedPane();
		
		
		//Display-Tab:
		JPanel displayTab = new JPanel(new BorderLayout());
		JPanel midiTab = new JPanel();
		midiTab.setLayout(new BoxLayout(midiTab,BoxLayout.Y_AXIS));
		
		JSlider opacitySlider = new JSlider();
		JSlider spacingSlider = new JSlider();
		JCheckBox alwaysOnTop = new JCheckBox("Always on top");
		
		JCheckBox smooth = new JCheckBox("Antialias");
		JComboBox fontSize = new JComboBox();
		
		JButton backgroundButton = new JButton("Background"),foregroundButton = new JButton("Foreground");
		JPanel sliderPanel = new JPanel(new GridLayout(2,2));
		sliderPanel.add(new Label("Opacity"));
		sliderPanel.add(opacitySlider);
		sliderPanel.add(new Label("Spacing"));
		sliderPanel.add(spacingSlider);
		

		
		displayTab.add(sliderPanel,BorderLayout.CENTER);
		JPanel checkPanel = new JPanel(new GridLayout(2,4));
		checkPanel.add(new Label("Font size"));
		checkPanel.add(fontSize);
		checkPanel.add(alwaysOnTop);
		checkPanel.add(new Label());//placeholder
		checkPanel.add(foregroundButton);
		checkPanel.add(backgroundButton);
		
		checkPanel.add(smooth);

		displayTab.add(checkPanel,BorderLayout.PAGE_END);
		
		
		initMidiTab(midiTab,controller);
		
		///REgister:
		JPanel registerTab = new JPanel();
		registerTab.setLayout(new GridLayout(3, 2,10,10));
//		registerTab.add(new JLabel("Customer"));
		JTextField customerField = new JTextField();
		registerTab.add(customerField);
//		registerTab.add(new JLabel("Key"));
		JTextField regKey = new JTextField();
		registerTab.add(regKey);
		JButton regButt = new JButton("Register");
		registerTab.add(regButt);
		regButt.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				register(controller);
			}
		});
	
		
		
		
		
		
		tabPane.addTab("Midi", midiTab);
		tabPane.addTab("Display", displayTab);
		/////// FREE SOFTWARE!!!
//		tabPane.addTab("Registration", registerTab);
		
		
		//Bindings:
		nSCellFactory.createCellForComponent(alwaysOnTop).bind(NSCell.CellValueBinding,
				controller, "lcdView.alwaysOnTop", new DefaultBindingOptions());
		nSCellFactory.createCellForComponent(smooth).bind(NSCell.CellValueBinding,
				controller, "lcdView.antialiased", new DefaultBindingOptions());
		
		NSBoundedRangeCell opacitiySliderCell = nSCellFactory.createCellForComponent(opacitySlider);
		opacitiySliderCell.setCellValueRange(new Object[]{0.25f,1.0f});
		opacitiySliderCell.setResolution(100);
		opacitiySliderCell.bind(NSCell.CellValueBinding, controller, "lcdView.alpha", new DefaultBindingOptions());
		
		
		
		
		NSBoundedRangeCell spacingSliderCell = nSCellFactory.createCellForComponent(spacingSlider);
		spacingSliderCell.setCellValueRange(new Object[]{0.0f,1.0f});
		spacingSliderCell.setResolution(100);
		spacingSliderCell.bind(NSCell.CellValueBinding, controller, "lcdView.interChanWeight", new DefaultBindingOptions());
		
		foregroundButton.addActionListener(Utils.createColorPickerForKeyPath(controller, "lcdView.foreground", "Select Foreground-Color", frame));
		backgroundButton.addActionListener(Utils.createColorPickerForKeyPath(controller, "lcdView.background", "Select Background-Color", frame));
		
		NSComboCell fontSizeCell =  nSCellFactory.createCellForComponent(fontSize);
		List<Integer> arr=new ArrayList<Integer>();
		for(int i=10;i<=30;i++)
			arr.add(i);
		fontSizeCell.setContentDataArray(arr);
		fontSizeCell.bind(NSCell.CellValueBinding, controller, "lcdView.fontsize", new DefaultBindingOptions());
		
		
		//Register
		NSControl customerCtrl = new NSTextFieldImpl();
		customerCtrl.setCell(nSCellFactory.createCellForComponent(customerField));
		customerCtrl.bind(NSControl.ControlValueBinding, controller, "registrationModel.customer", new DefaultBindingOptions());
		NSControl regKeyCtrl = new NSTextFieldImpl();
		regKeyCtrl.setCell(nSCellFactory.createCellForComponent(regKey));
		regKeyCtrl.bind(NSControl.ControlValueBinding, controller, "registrationModel.registrationKey", new DefaultBindingOptions());
		
		
		
		frame.setResizable(false);
		
		frame.add(tabPane);
		frame.pack();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2,
				(screenSize.height - frame.getHeight()) / 2);
		
		frame.setVisible(false);
	}

	private void initMidiTab(JPanel midiTab,final VirtualControlSurfaceLCD controller) {
		JPanel tt=new JPanel(new GridLayout(1,3));
		tt.setBorder(BorderFactory.createTitledBorder("from Controller via Note-Mapping to DAW (optional)"));
		JComboBox box1 = new JComboBox();
		JComboBox box2 = new JComboBox();
		tt.add(box1);
		JButton mapbutt = new JButton("Edit Note-Mappings");
		
		mWin=new MappingWindow(controller.getMapper());
		mWin.show(false);
		
		mapbutt.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				mWin.show(true);
			}
		});
		tt.add(mapbutt);
		tt.add(box2);
		midiTab.add(tt);
		
		NSComboCell cell1 = nSCellFactory.createCellForComponent(box1);
		cell1.bind(NSComboCell.CellValueBinding, controller, "fromControllerPortName", new DefaultBindingOptions());
		cell1.bind(NSComboCell.ContentBinding, controller, "ins", new DefaultBindingOptions());
		
		cell1 = nSCellFactory.createCellForComponent(box2);
		cell1.bind(NSComboCell.CellValueBinding, controller, "toHostPortName", new DefaultBindingOptions());
		cell1.bind(NSComboCell.ContentBinding, controller, "outs", new DefaultBindingOptions());
		
		
		tt=new JPanel(new GridLayout(1,2));
		tt.setBorder(BorderFactory.createTitledBorder("from DAW via " + VirtualControlSurfaceLCD.class.getSimpleName() + " to Controller (required)"));
		JComboBox box3 = new JComboBox();
		JComboBox box4 = new JComboBox();
		tt.add(box3);
		tt.add(box4);
		midiTab.add(tt);
		
		cell1 = nSCellFactory.createCellForComponent(box3);
		cell1.bind(NSComboCell.CellValueBinding, controller, "fromHostPortName", new DefaultBindingOptions());
		cell1.bind(NSComboCell.ContentBinding, controller, "ins", new DefaultBindingOptions());
		
		cell1 = nSCellFactory.createCellForComponent(box4);
		cell1.bind(NSComboCell.CellValueBinding, controller, "toControllerPortName", new DefaultBindingOptions());
		cell1.bind(NSComboCell.ContentBinding, controller, "outs", new DefaultBindingOptions());
		
	}

	public void show(boolean b) {
		frame.setVisible(b);
	}

	public void register(final VirtualControlSurfaceLCD controller) {
		boolean valid = controller.getRegistrationModel().isRegistered();
		
		if(valid)
			JOptionPane.showMessageDialog(frame, "Thank you for purchasing this Software!",  "Registration", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(frame, "The Key is not valid. Check the name and key. Contact support@infomac.de.",  "Registration", JOptionPane.ERROR_MESSAGE);			
	}
	
	

	
}
