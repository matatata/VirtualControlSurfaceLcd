package de.ceruti.midi.core;

public interface IMidiCentral {

	public abstract String[] getIns();

	public abstract String[] getOuts();

	public abstract MidiSource openInput(int index);

	public abstract MidiDestination openOutput(int index);
	
	class Dummy implements IMidiCentral {

		public String[] getIns() {
			return new String[]{"Dummy"};
		}

		public String[] getOuts() {
			return new String[]{"Dummy"};
		}

		public MidiSource openInput(int index) {
			return null;
		}

		public MidiDestination openOutput(int index) {
			return null;
		}
		
	}

}