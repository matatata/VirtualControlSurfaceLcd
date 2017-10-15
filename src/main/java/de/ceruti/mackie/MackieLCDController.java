package de.ceruti.mackie;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import de.ceruti.curcuma.foundation.NSObjectImpl;
import de.ceruti.curcuma.keyvalueobserving.PostKVONotifications;
import de.ceruti.mackie.ILCDView.Nofitications;
import de.ceruti.mackie.view.MackieLCDWindow;
import de.ceruti.mackie.view.SettingsView;
import de.ceruti.midi.core.IMidiCentral;
import de.ceruti.midi.core.LCDMidiCentral;
import de.ceruti.midi.core.MidiDestination;
import de.ceruti.midi.core.MidiSource;

public class MackieLCDController extends NSObjectImpl implements Nofitications {
	
	private static final String BACKGROUND_KEY = "backgroundKey";
	private static final String FOREGROUND_KEY = "foregroundKey";
	private static final String FONT_SIZE_KEY = "fontSizeKey";
	private static final String SPACING_KEY = "spacingKey";
	private static final String OPACITY_KEY = "opacityKey";
	private static final String ANTIALIASED_PREF_KEY = "antialiasedPrefKey";
	private static final String ALWAYS_ON_TOP_PREF_KEY = "alwaysOnTopPrefKey";
	private static final String NOPORT = "-";
	private static final String VirtualPortIN = "MackieLCD in";
	private static final String VirtualPortOUT = "MackieLCD out";
	
	private static IMidiCentral central = 
		new LCDMidiCentral(VirtualPortIN, VirtualPortOUT);
//		new IMidiCentral.Dummy();

	
	private SettingsView settings;
	private ILCDView lcdView;
	
	public ILCDView getLcdView() {
		return lcdView;
	}


	private MackieLCD mackieView;
	
	private String fromHostPortName=NOPORT,
	toHostPortName=NOPORT,fromControllerPortName=NOPORT,
	toControllerPortName=NOPORT;
	
	public String getFromHostPortName() {
		return fromHostPortName;
	}

	@PostKVONotifications
	public void setFromHostPortName(String fromHostPortName) {
		this.fromHostPortName = fromHostPortName;
		int index=getIns().indexOf(fromHostPortName);
		MidiSource in=null;
		if(index>=1)
			in = central.openInput(index-1);
		mackieView.setFromHost(in);
	}

	public String getToHostPortName() {
		return toHostPortName;
	}

	@PostKVONotifications
	public void setToHostPortName(String toHostPortName) {
		this.toHostPortName = toHostPortName;
		int index=getOuts().indexOf(toHostPortName);
		MidiDestination out=null;
		if(index>=1)
			out = central.openOutput(index-1);
		mackieView.setToHost(out);
	}
	
	public String getFromControllerPortName() {
		return fromControllerPortName;
	}

	@PostKVONotifications
	public void setFromControllerPortName(String fromControllerPortName) {
		this.fromControllerPortName = fromControllerPortName;
		int index=getIns().indexOf(fromControllerPortName);
		MidiSource in=null;
		if(index>=1)
			in = central.openInput(index-1);
		mackieView.setFromController(in);
	}

	public String getToControllerPortName() {
		return toControllerPortName;
	}

	@PostKVONotifications
	public void setToControllerPortName(String toControllerPortName) {
		this.toControllerPortName = toControllerPortName;
		int index=getOuts().indexOf(toControllerPortName);
		MidiDestination out=null;
		if(index>=1)
			out = central.openOutput(index-1);
		mackieView.setToController(out);
	}
	
	private List<String> ins=new ArrayList<String>(),outs=new ArrayList<String>();
	
	private void initInsAndOuts(){
		willChangeValueForKey("ins");
		ins = new ArrayList<String>();
		ins.add(NOPORT);
		ins.addAll(Arrays.asList(central.getIns()));
		didChangeValueForKey("ins");
		
		willChangeValueForKey("outs");
		outs = new ArrayList<String>();
		outs.add(NOPORT);
		outs.addAll(Arrays.asList(central.getOuts()));
		didChangeValueForKey("outs");
	}
	
	public List<String> getIns(){
		return ins;
	}
	
	public List<String> getOuts(){
		return outs;
	}
	

	private boolean complainMode = false;

	private Timer registerTimer = new Timer();
	
	private RegistrationModel registrationModel = null;

	public RegistrationModel getRegistrationModel() {
		return registrationModel;
	}

	@PostKVONotifications
	public void setRegistrationModel(RegistrationModel registrationModel) {
		this.registrationModel = registrationModel;
	}

	MackieLCDController(){
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		lcdView = new MackieLCDWindow(this,"Mackie View",Color.white,Color.blue,0.75f,null,false);
		

		mackieView = new MackieLCD(){

			@Override
			protected void updateLCD(int lcdPos, int dataOffset, byte[] bytes) {
				if(!complainMode || registrationModel.isRegistered())
					getLcdView().updateDisplayData(lcdPos, bytes, dataOffset, bytes.length-dataOffset);
			}
			
		};
		registrationModel = new RegistrationModel();
		
		settings = new SettingsView(this);
		
		initInsAndOuts();

		loadSettings();
		
		if(mackieView.getFromHost() == null /* || mackieView.getToController() == null*/){
			lcdView.displayText("Welcome! At least, you need to select a \"from host\" to make the display receive data.");
			settings.show(true);
		}

		lcdView.show(true);
		
		if(!registrationModel.isRegistered()){
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					if(registrationModel.isRegistered()){
						registerTimer.cancel();
						complainMode = false;
					}
					
					if(!complainMode){
						getLcdView().displayText("Please buy this product and register at infomac.de");
						complainMode = true;
					}
					else
						complainMode = false;
				}
			};
			
			registerTimer.schedule(task, 5000,1000);
		}

		
	}
	
	

	
	public void configure(ILCDView jFrameLCDView) {
		settings.show(true);
	}
	

	
	
	public static void main(String[] args) {
		try{
		UIManager.setLookAndFeel(UIManager
				.getSystemLookAndFeelClassName());
		}catch(Exception e){}
		
		final MackieLCDController mackie = new MackieLCDController();
		
		
		
//		com.apple.eawt.Application a = com.apple.eawt.Application.getApplication();
//		
//		a.addApplicationListener(new com.apple.eawt.ApplicationAdapter(){
//			@Override
//			public void handleQuit(com.apple.eawt.ApplicationEvent arg0) {
//				mackie.writeSettings();
//				System.exit(0);
//			}
//			
//			@Override
//			public void handleAbout(com.apple.eawt.ApplicationEvent arg0) {
//				super.handleAbout(arg0);
//			}
//		});
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			@Override
			public void run() {
				mackie.writeSettings();
			}}));
		
	}


	public MackieMapper getMapper() {
		return mackieView.getMapper();
	}
	
	private static final String fromHostPortNamePrefKey = "fromHostPortName";
	private static final String toHostPortNamePrefKey = "toHostPortName";
	private static final String toControllerPortNamePrefKey = "toControllerPortName";
	private static final String fromControllerPortNamePrefKey = "fromControllerPortName";
	private static final String registrationKeyPrefKey = "registrationKey";
	private static final String customerPrefKey = "customerKey";
	
	
	public void loadSettings(){
		Preferences prefs = getPrefs();
		
		setFromHostPortName(prefs.get(fromHostPortNamePrefKey, NOPORT));
		setToHostPortName(prefs.get(toHostPortNamePrefKey, NOPORT));
		setToControllerPortName(prefs.get(toControllerPortNamePrefKey, NOPORT));
		setFromControllerPortName(prefs.get(fromControllerPortNamePrefKey, NOPORT));
		registrationModel.setRegistrationKey(prefs.get(registrationKeyPrefKey, ""));
		registrationModel.setCustomer(prefs.get(customerPrefKey, ""));
		
		Rectangle bounds = new Rectangle(prefs.getInt("x", 0), prefs.getInt("y", 0), prefs.getInt("w", 0), prefs.getInt("h", 0));
		
		if(!bounds.isEmpty())
			lcdView.setBounds(bounds);
		
		
		lcdView.setAlwaysOnTop(prefs.getBoolean(ALWAYS_ON_TOP_PREF_KEY, true));
		lcdView.setAntialiased(prefs.getBoolean(ANTIALIASED_PREF_KEY, true));
		lcdView.setAlpha(prefs.getFloat(OPACITY_KEY, 0.75f));
		lcdView.setInterChanWeight(prefs.getFloat(SPACING_KEY, 0.0f));
		lcdView.setFontsize(prefs.getInt(FONT_SIZE_KEY, 20));
		
		lcdView.setForeground(new Color(prefs.getInt(FOREGROUND_KEY, Color.white.getRGB())));
		lcdView.setBackground(new Color(prefs.getInt(BACKGROUND_KEY, Color.blue.getRGB())));
		
	}
	
	@Override
	public void exit(ILCDView jFrameLCDView) {
		System.exit(0);
	}
	
	public void writeSettings(){
		Preferences prefs = getPrefs();
		
		prefs.put(fromHostPortNamePrefKey, getFromHostPortName());
		prefs.put(toHostPortNamePrefKey, getToHostPortName());
		prefs.put(toControllerPortNamePrefKey, getToControllerPortName());
		prefs.put(fromControllerPortNamePrefKey, getFromControllerPortName());
		prefs.put(registrationKeyPrefKey,registrationModel.getRegistrationKey());
		prefs.put(customerPrefKey,registrationModel.getCustomer());
		
		
		prefs.putInt("x",lcdView.getBounds().x);
		prefs.putInt("y",lcdView.getBounds().y);
		prefs.putInt("h",lcdView.getBounds().height);
		prefs.putInt("w",lcdView.getBounds().width);
		
		prefs.putBoolean(ALWAYS_ON_TOP_PREF_KEY, lcdView.isAlwaysOnTop());
		prefs.putBoolean(ANTIALIASED_PREF_KEY, lcdView.isAntialiased());
		prefs.putFloat(OPACITY_KEY, lcdView.getAlpha());
		prefs.putFloat(SPACING_KEY, lcdView.getInterChanWeight());
		prefs.putInt(FONT_SIZE_KEY, lcdView.getFontsize());
		
		prefs.putInt(FOREGROUND_KEY, lcdView.getForeground().getRGB());
		prefs.putInt(BACKGROUND_KEY, lcdView.getBackground().getRGB());
		
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private Preferences getPrefs() {
		Preferences prefs = Preferences.userRoot().node("de").node("infomac").node("MacBCFView");
		return prefs;
	}


	

}
