package de.ceruti.midi.core;

public abstract class MidiHandlerAdapter extends AbstractMidiHandler {

	@Override
	protected ActionCode note(int note, int sel, byte[] data) {
		return ActionCode.Thru;
	}

	@Override
	protected ActionCode sysexData(int vendor, byte[] data) {
		return ActionCode.Thru;
	}

}
