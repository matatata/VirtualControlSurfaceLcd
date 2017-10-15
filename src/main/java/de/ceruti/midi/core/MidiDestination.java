package de.ceruti.midi.core;

public interface MidiDestination {

	void sendMidiData(byte[] data);

	String getName();

}
