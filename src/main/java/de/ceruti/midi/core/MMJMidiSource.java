package de.ceruti.midi.core;

import java.util.HashMap;
import java.util.Map;

import de.humatic.mmj.MidiInput;


class MMJMidiSource implements MidiSource {

	private MidiInput mmj;
	
	private Map<MidiListener,de.humatic.mmj.MidiListener>  lMap = new HashMap<MidiListener,de.humatic.mmj.MidiListener>();
	
	public MMJMidiSource(MidiInput mmjIn) {
		this.mmj = mmjIn;
	}

	
	public void addListener(final MidiListener l) {
		mmj.addMidiListener(new de.humatic.mmj.MidiListener() {
			public void midiInput(byte[] data) {
				l.midiReceived(data);
			}
		});
	}

	public void removeListener(MidiListener l) {
		mmj.removeMidiListener(lMap.get(l));
	}
	
	public String getName() {
		return mmj.getName();
	}

}
