package de.ceruti.midi.core;

import de.humatic.mmj.MidiOutput;

class MMJMidiDestination implements MidiDestination {

	private MidiOutput mmj;
	
	public MMJMidiDestination(MidiOutput mmjOut) {
		this.mmj = mmjOut;
	}

	public void sendMidiData(byte[] data) {
		mmj.sendMidi(data);
	}

	public String getName() {
		return mmj.getName();
	}

}
