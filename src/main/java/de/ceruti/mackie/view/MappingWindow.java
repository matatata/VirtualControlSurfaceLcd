package de.ceruti.mackie.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.ceruti.curcuma.api.appkit.view.cells.NSCell;
import de.ceruti.curcuma.api.appkit.view.cells.NSTextCell;
import de.ceruti.curcuma.api.appkit.view.table.NSTableColumn;
import de.ceruti.curcuma.api.appkit.view.table.NSTableView;
import de.ceruti.curcuma.api.keyvalueobserving.KVOEvent;
import de.ceruti.curcuma.api.keyvalueobserving.KVOOption;
import de.ceruti.curcuma.api.keyvalueobserving.KVObserver;
import de.ceruti.curcuma.api.keyvalueobserving.KeyValueObserving;
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
import de.ceruti.mackie.MackieMapper.MappingEntry;
import de.ceruti.midi.core.MidiNotes;


public class MappingWindow {
	
	private JFrame frame;
	private NSTable nsTable;
	private JTable table;
	
	
	public MappingWindow(final MackieMapper mapper) {
		frame=new JFrame("Key Mappings");
		table=new JTable();
		nsTable=new NSTable();
		
		SwingTableViewPlugIn plug = new SwingTableViewPlugIn();
		plug.setViewWidget(table);
		nsTable.setViewPlugIn(plug);

		//test
//		if(mapper==null) {
//			mapper=new MackieMapper(){
//				@Override
//				protected MidiDestination midiDestination() {
//					return null;
//				}};
//			}
		
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
		fromCombo.setEditable(true);
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
		toCombo.setEditable(true);
		toCombo.putClientProperty("JComboBox.isSquare", Boolean.TRUE);
		pl2.setWidget(toCombo);
		toCell.setWidgetPlugIn(pl2);
		toCell.setContentDataArray(MidiNotes.sharedInstance().codeList());
		toCell.setDisplayDataArray(MidiNotes.sharedInstance().notesList());
		
		toCol.setDataCell(toDataCell);
		toCol.setEditorCell(toCell);
		toCol.bind(NSCell.CellValueBinding,arrayController,"arrangedObjects.to",new DefaultBindingOptions());
		
		nsTable.addTableColumn(toCol);
		
		
//		JCheckBox persist= new JCheckBox();
//		NSToggleCell persistCell = nsCellFactory.createCellForComponent(persist);
//		persistCell.setEditable(false);
//		NSTableColumn persistCol=new NSTableCol();
//		persistCol.setIdentifier("Persist");
//		persistCol.setEditorCell(persistCell);
//		nsTable.addTableColumn(persistCol);
//		persistCol.bind(NSCell.CellValueBinding,arrayController,"arrangedObjects.persist",new DefaultBindingOptions());
//		
		
		JTextField name= new JTextField();
		NSTextCell nameCell = nsCellFactory.createCellForComponent(name);
		NSTableColumn nameCol=new NSTableCol();
		nameCol.setIdentifier("Name");
		nameCol.setEditorCell(nameCell);
		nsTable.addTableColumn(nameCol);
		nameCol.bind(NSCell.CellValueBinding,arrayController,"arrangedObjects.name",new DefaultBindingOptions());
		
		
		nsTable.bind(NSTableView.ContentArrayBinding, arrayController,
				"arrangedObjects", new DefaultBindingOptions());
		nsTable.bind("selectionIndexes", arrayController, "selectionIndexes",
				new DefaultBindingOptions());
		
		
		KVObserver obs=new KVObserver() {
			
			public void observeValue(String keypath, KeyValueObserving object, KVOEvent change, Object context) {
				if (frame.isActive() && frame.isVisible()) {
					frame.setTitle(mapper.getMonitor());
				}
			}
		};
		mapper.addObserver(obs, "monitor", null, KVOOption.KeyValueObservingOptionNew);
		
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

		JMenuBar menubar=new JMenuBar();
		JMenuItem bcf2000lcSwapCursorsTransport=new JMenuItem("Invert Shift for Cursor/Transport (BCF2000, LogicControl)");
		bcf2000lcSwapCursorsTransport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				arrayController.addObjects(swapCursorTransportLogicControl());
			}
		});
		JMenu presets=new JMenu("Presets");
		presets.add(bcf2000lcSwapCursorsTransport);
		menubar.add(presets);
		frame.setJMenuBar(menubar);
		
		frame.setLayout(new BorderLayout());
		table.setPreferredScrollableViewportSize(new Dimension(300,400));
		frame.add(new JScrollPane(table),BorderLayout.CENTER);
		JPanel butts = new JPanel();
		butts.add(addButt);
		butts.add(delButt);
		frame.add(butts,BorderLayout.SOUTH);
		frame.pack();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2,
				(screenSize.height - frame.getHeight()) / 2);
		
	}
	
	public static List<MappingEntry> swapCursorTransportLogicControl() {
		/*
		 * This swaps the Shift cursor up/down with transport BCF2000 Logic Control
		 */
		
		List<MappingEntry> result = new ArrayList<MappingEntry>();
		result.add(new MappingEntry(91, 98,false,"Rewind->Left"));
		result.add(new MappingEntry(92, 99,false,"Forward->Right"));
		result.add(new MappingEntry(93, 96,false,"Stop->Down"));
		result.add(new MappingEntry(94, 97,false,"Play->Up"));
	
		result.add(new MappingEntry(96, 94,false,"Down->Stop"));
		result.add(new MappingEntry(97, 93,false,"Up->Play"));
		result.add(new MappingEntry(98, 91,false,"Left->Rewind"));
		result.add(new MappingEntry(99, 92,false,"Right->Forward"));
	
		return result;
		
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
