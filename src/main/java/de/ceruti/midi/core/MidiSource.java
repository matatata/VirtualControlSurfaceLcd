package de.ceruti.midi.core;

public interface MidiSource {
	void addListener(MidiListener l);
	void removeListener(MidiListener l);
	String getName();
}
