/**
 * 
 */
package de.ceruti.midi.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public abstract class AbstractMidiHandler implements MidiListener {

	public AbstractMidiHandler() {
	}


	protected abstract MidiDestination midiDestination();
	
	// From Controller
	public void midiReceived(byte[] data) {

		// before
//		MidiDumper.dumpData(data);
		
		ActionCode code = parse(data);

		//after
//		MidiDumper.dumpData(data);

		// toHost
		if (midiDestination() != null)
		{
			switch(code){
			case Thru:
			case Mapped:
			case Invalid:
				midiDestination().sendMidiData(data);
				break;
			
			case Filtered:
				break;
			}
		}
	}


	protected abstract ActionCode note(int note, int sel, byte[] data);
	protected abstract ActionCode sysexData(int vendor,byte[] data);

	
	public enum ActionCode {
		Mapped,
		Filtered,
		Thru,
		Invalid
	}
	
	
	private ByteArrayOutputStream currentSysExData = new ByteArrayOutputStream(
			8 * 7 * 2);
	
	
	private boolean parsingSysEx = false;
	/**
	 * 
	 * @param data
	 * @return true, if data has been changed
	 */
	private ActionCode parse(byte[] data) {
		if (data.length == 0)
			return ActionCode.Invalid;

		int vendor = 0;
		int cursor = 0;
		
		int status = data[cursor++] & 0xFF;
		//TODO filter channel!!!
		
		
		switch (status) {

			//this is note on/off for channel 1 (me thinks)
			case 0x90:
			case 0x80: {
				if (data.length <= 1)
					return ActionCode.Invalid;
	
				int note = data[1] & 0xFF;
	
				if (data.length <= 2)
					return ActionCode.Invalid;
	
				int sel = data[2] & 0xFF;
	
				return note(note, sel, data);
			}
			
			case 0xF0:
				
				if(parsingSysEx)
					break;
				
				currentSysExData.reset();
				// get vendor:

				if (data.length <= 1)
					return ActionCode.Invalid;

				if (data[cursor++] == 0) {
					// two bytes are vendor:
					if (data.length <= 3)
						return ActionCode.Invalid;

					int a = data[cursor++] & 0xFF;
					int b = data[cursor++] & 0xFF;

					vendor = 0;
					vendor = a << 8;
					vendor |= b;
								
				} else {
					vendor = data[1];
				}
				parsingSysEx = true;
				break;

			}

			for (int i = cursor; i < data.length; i++) {

				int dataByte = data[i] & 0xFF;

				if (dataByte == 0xF7 && parsingSysEx) {
					
					// done reading sysex
					try { currentSysExData.flush(); } catch (IOException e) { e.printStackTrace();}
					byte[] bytes = currentSysExData.toByteArray();
					parsingSysEx = false;
					return sysexData(vendor,bytes);
				} else {
					currentSysExData.write(dataByte);
				}
			}

		

		return ActionCode.Thru;
	}



}