package de.ceruti.mackie.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.ceruti.curcuma.api.appkit.view.cells.NSCell;
import de.ceruti.curcuma.api.appkit.view.cells.NSTextCell;
import de.ceruti.curcuma.api.appkit.view.table.NSTableView;
import de.ceruti.curcuma.appkit.controllers.NSArrayControllerImpl;
import de.ceruti.curcuma.appkit.view.cells.NSActionCellImpl;
import de.ceruti.curcuma.appkit.view.cells.NSComboBoxCell;
import de.ceruti.curcuma.appkit.view.table.NSTable;
import de.ceruti.curcuma.appkit.view.table.NSTableCol;
import de.ceruti.curcuma.appkit.widgets.awt.AWTWidgetPlugIn;
import de.ceruti.curcuma.appkit.widgets.swing.NSCellFactory;
import de.ceruti.curcuma.appkit.widgets.swing.PlugInFactory;
import de.ceruti.curcuma.appkit.widgets.swing.SwingComboBoxWidgetPlugIn;
import de.ceruti.curcuma.appkit.widgets.swing.SwingTableViewPlugIn;
import de.ceruti.curcuma.keyvaluebinding.DefaultBindingOptions;
import de.ceruti.mackie.MackieMapper;
import de.ceruti.midi.core.MidiDestination;
import de.ceruti.midi.core.MidiNotes;


public class MappingWindow {
	
	private JFrame frame;
	private NSTable nsTable;
	private JTable table;
	
	
	public MappingWindow(MackieMapper mapper) {
		frame=new JFrame("Key Mappings");
		table=new JTable();
		nsTable=new NSTable();
		
		SwingTableViewPlugIn plug = new SwingTableViewPlugIn();
		plug.setViewWidget(table);
		nsTable.setViewPlugIn(plug);

		//test
		if(mapper==null) {
		mapper=new MackieMapper(){
			@Override
			protected MidiDestination midiDestination() {
				return null;
			}};
		}
		
		NSCellFactory nsCellFactory=NSCellFactory.create(PlugInFactory.get());
		
		final NSArrayControllerImpl arrayController=new NSArrayControllerImpl();
		arrayController.setObjectClass(MackieMapper.MappingEntry.class);
		
		arrayController.setContent(mapper.mutableArrayValueForKey("mappings"));
		
		NSTableCol fromCol = new NSTableCol();
		fromCol.setIdentifier("From");
		NSTextCell fromDataCell = nsCellFactory.createCellForComponent(new JTextField());
		fromDataCell.setFormat(MidiNotes.format);
		NSComboBoxCell fromCell = new NSComboBoxCell();
		SwingComboBoxWidgetPlugIn pl = new SwingComboBoxWidgetPlugIn();
		JComboBox fromCombo = new JComboBox();
		fromCombo.putClientProperty("JComboBox.isSquare", Boolean.TRUE);
		pl.setWidget(fromCombo);
		fromCell.setWidgetPlugIn(pl);
		fromCell.setContentDataArray(MidiNotes.sharedInstance().codeList());
		fromCell.setDisplayDataArray(MidiNotes.sharedInstance().notesList());
		
		fromCol.setDataCell(fromDataCell);
		fromCol.setEditorCell(fromCell);
		fromCol.bind(NSCell.CellValueBinding,arrayController,"arrangedObjects.from",new DefaultBindingOptions());
		nsTable.addTableColumn(fromCol);
		
		NSTableCol toCol = new NSTableCol();
		toCol.setIdentifier("To");
		NSTextCell toDataCell = nsCellFactory.createCellForComponent(new JTextField());
		toDataCell.setFormat(MidiNotes.format);
		NSComboBoxCell toCell = new NSComboBoxCell();
		SwingComboBoxWidgetPlugIn pl2 = new SwingComboBoxWidgetPlugIn();
		JComboBox toCombo = new JComboBox();
		toCombo.putClientProperty("JComboBox.isSquare", Boolean.TRUE);
		pl2.setWidget(toCombo);
		toCell.setWidgetPlugIn(pl2);
		toCell.setContentDataArray(MidiNotes.sharedInstance().codeList());
		toCell.setDisplayDataArray(MidiNotes.sharedInstance().notesList());
		
		toCol.setDataCell(toDataCell);
		toCol.setEditorCell(toCell);
		toCol.bind(NSCell.CellValueBinding,arrayController,"arrangedObjects.to",new DefaultBindingOptions());
		
		nsTable.addTableColumn(toCol);
		
		
		nsTable.bind(NSTableView.ContentArrayBinding, arrayController,
				"arrangedObjects", new DefaultBindingOptions());
		nsTable.bind("selectionIndexes", arrayController, "selectionIndexes",
				new DefaultBindingOptions());
		
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				show(false);
			}
		});
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JButton addButt;
		final JButton delButt;
		addButt = new JButton("add");
		delButt = new JButton("remove");
		
		addButt.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				arrayController.add();
			}
		});
		
		delButt.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				arrayController.remove();
			}
		});
		
		
		// Make Buttons more easy!
		NSActionCellImpl delButtCell = new NSActionCellImpl();
		AWTWidgetPlugIn delButtPlug = new AWTWidgetPlugIn() {
			public Object getPlugInValue() {
				return null;
			}
			protected void _setPlugInValue(Object v) {
			}
		};
		delButtPlug.setWidget(delButt);
		delButtCell.setWidgetPlugIn(delButtPlug);
		delButtCell.bind(NSCell.CellEnabledBinding,arrayController,"canRemove",new DefaultBindingOptions());

		
		frame.setLayout(new BorderLayout());
		table.setPreferredScrollableViewportSize(new Dimension(100,200));
		frame.add(new JScrollPane(table),BorderLayout.CENTER);
		JPanel butts = new JPanel();
		butts.add(addButt);
		butts.add(delButt);
		frame.add(butts,BorderLayout.SOUTH);
		frame.pack();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2,
				(screenSize.height - frame.getHeight()) / 2);
		
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {

				new MappingWindow(null);
			}
		});
	}

	public void show(boolean b) {
		frame.setVisible(b);
	}
}
