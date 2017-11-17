package de.ceruti.mackie;

import java.io.ByteArrayOutputStream;

import de.ceruti.curcuma.foundation.NSObjectImpl;
import de.ceruti.midi.core.MidiDestination;
import de.ceruti.midi.core.MidiHandlerAdapter;
import de.ceruti.midi.core.MidiListener;
import de.ceruti.midi.core.MidiSource;

public abstract class MackieLCD extends NSObjectImpl {

	public static final int CharsPerRow = 56;
	
	private ByteArrayOutputStream currentData = new ByteArrayOutputStream(
			CharsPerRow * 2);
	
	private MidiSource fromController;

	private MidiSource fromHost;

	private MidiMapper fromController2HostMapper = new MidiMapper() ;//{

//		protected MidiDestination midiDestination() {
//			return getToHost();
//		}
//	};

	private MidiDestination toController;

	private MidiDestination toHost;


	public MackieLCD() {
	
	}

	

	public MidiSource getFromController() {
		return fromController;
	}

	public MidiSource getFromHost() {
		return fromHost;
	}

	public MidiDestination getToController() {
		return toController;
	}

	public MidiDestination getToHost() {
		return toHost;
	}


	private MidiListener fromHostToControllerLCDTextReader = new MidiHandlerAdapter() {
		
		@Override
		protected ActionCode sysexData(int vendor, byte[] data) {
			// we kind of know that the following will be 10 12
			if (data.length < 2)
				return ActionCode.Thru;

			int cursor = 0;
			int who = data[cursor++] & 0xFF;
			int knows = data[cursor++] & 0xFF;

			if (who != 0x10 && knows != 0x12) {
				// better ignore
				return ActionCode.Thru;
			}

			if (data.length <= cursor)
				return ActionCode.Thru;

			int lcdPos = data[cursor++] & 0xFF;

			updateLCD(lcdPos,cursor,data);
			
			return ActionCode.Thru;
		}
		
		private void updateLCD(int lcdPos, int dataOffset, byte[] bytes) {
			MackieLCD.this.updateLCD(lcdPos, dataOffset, bytes);
		}

		@Override
		protected MidiDestination midiDestination() {
			return getToController();
		}
	};
	
	
	public MidiMapper getMapper() {
		return fromController2HostMapper;
	}



	protected abstract void updateLCD(int lcdPos, int dataOffset, byte[] bytes);
	
	public void setFromController(MidiSource fromController) {
		if (this.fromController != fromController) {
			if (this.fromController != null)
				this.fromController.removeListener(fromController2HostMapper);

			this.fromController = fromController;
			if (this.fromController != null)
				this.fromController.addListener(fromController2HostMapper);

			currentData.reset();
		}
	}

	public void setFromHost(MidiSource fromHost) {
		if (this.fromHost != fromHost) {
			if (this.fromHost != null)
				this.fromHost.removeListener(fromHostToControllerLCDTextReader);

			this.fromHost = fromHost;
			if (this.fromHost != null)
				this.fromHost.addListener(fromHostToControllerLCDTextReader);

			currentData.reset();
		}
	}

	public void setToController(MidiDestination toController) {
		this.toController = toController;
	}

	public void setToHost(MidiDestination toHost) {
		this.toHost = toHost;
		fromController2HostMapper.setDestination(toHost);
	}


}
