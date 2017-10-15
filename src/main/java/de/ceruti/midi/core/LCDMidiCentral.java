package de.ceruti.midi.core;

import de.humatic.mmj.MidiSystemListener;

public class LCDMidiCentral implements MidiSystemListener, IMidiCentral {
	

	public LCDMidiCentral(String virtualSource,String virtualDestination) {
		StringBuffer sb = new StringBuffer("mmj\n");
		sb.append("jar: " + de.humatic.mmj.MidiSystem.getJarVersion()
				+ ", dll: " + de.humatic.mmj.MidiSystem.getLibraryVersion()
				+ "\n");
		sb.append("JDK: " + System.getProperty("java.version") + "\n");
		sb.append("OS: " + System.getProperty("os.name") + " "
				+ System.getProperty("os.version") + " "
				+ System.getProperty("os.arch"));
		System.out.println(sb.toString());

//		 boolean sendActiveSensing = true;
				
//		 Preferences prefs =
//		 Preferences.userRoot().node("de").node("humatic").node("mmj");
//				
//		 prefs.put("as", String.valueOf(sendActiveSensing));
//				
		 //
//		 try {
//			 de.humatic.mmj.MidiSystem.enableActiveSensing(sendActiveSensing);
//		 } catch (Exception e) {
//			 e.printStackTrace(System.err);
//		 }

		de.humatic.mmj.MidiSystem.initMidiSystem(virtualSource, virtualDestination);

		de.humatic.mmj.MidiSystem.addSystemListener(this);

		readSystem();

	}

	private void readSystem() {
		
	}

	/* (non-Javadoc)
	 * @see de.ceruti.midi.core.IMidiCentral#getIns()
	 */
	public String[] getIns() {
		return de.humatic.mmj.MidiSystem.getInputs();
	}

	/* (non-Javadoc)
	 * @see de.ceruti.midi.core.IMidiCentral#getOuts()
	 */
	public String[] getOuts() {
		return de.humatic.mmj.MidiSystem.getOutputs();
	}

	/* (non-Javadoc)
	 * @see de.ceruti.midi.core.IMidiCentral#openInput(int)
	 */
	public MidiSource openInput(int index) {
		return new MMJMidiSource(de.humatic.mmj.MidiSystem.openMidiInput(index));
	}

	/* (non-Javadoc)
	 * @see de.ceruti.midi.core.IMidiCentral#openOutput(int)
	 */
	public MidiDestination openOutput(int index) {
		return new MMJMidiDestination(de.humatic.mmj.MidiSystem.openMidiOutput(index));
	}

	public void systemChanged() {
		readSystem();
		System.out.println("MIDI SYSTEM CHANGED");
	}


}
