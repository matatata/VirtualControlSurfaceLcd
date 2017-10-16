/**
 * 
 */
package de.ceruti.mackie;

import static de.ceruti.midi.core.MidiNotes.sharedInstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.ceruti.curcuma.api.keyvaluecoding.KeyValueCoding;
import de.ceruti.curcuma.api.keyvaluecoding.exceptions.ValidationException;
import de.ceruti.curcuma.api.keyvalueobserving.KeyValueObserving;
import de.ceruti.curcuma.foundation.KVOMutableArrayProxy;
import de.ceruti.curcuma.foundation.NSObjectImpl;
import de.ceruti.curcuma.keyvaluebinding.KeyValueBindingCreator;
import de.ceruti.curcuma.keyvaluecoding.DefaultErrorHandling;
import de.ceruti.curcuma.keyvaluecoding.KeyValueCodeable;
import de.ceruti.curcuma.keyvalueobserving.KeyValueObservable;
import de.ceruti.midi.core.MidiDestination;
import de.ceruti.midi.core.MidiHandlerAdapter;
import de.ceruti.midi.core.MidiListener;

@KeyValueCodeable
@DefaultErrorHandling
@KeyValueObservable
@KeyValueBindingCreator
//@NSObject
public class MackieMapper 
extends NSObjectImpl
implements MidiListener{
	private HH del;

	
	@Override
	public void midiReceived(byte[] data) {
		del.midiReceived(data);
	}


	public void setDestination(MidiDestination destination) {
		del.setDestination(destination);
	}

	private String monitor;

	public MackieMapper() {
		super();
		
		del=new HH();
	}


	public static Set<String> keyPathsForValuesAffectingValueForKey(String key) {
		return new HashSet<String>();
	}	
	
	public List mutableArrayValueForKey(String key) {
		return new KVOMutableArrayProxy((KeyValueCoding)this,(KeyValueObserving)this,key);
	}
	

	private MappingEntry findMappingEntry(int from){
		for(MappingEntry e:mappings){
			if(e.getFrom()==from){
				return e;
			}
		}
		return null;
	}
	
	protected class HH extends MidiHandlerAdapter {

		private MidiDestination destination;

		public HH() {
		}
		
		@Override
		protected MidiDestination midiDestination() {
			return destination;
		}

		public void setDestination(MidiDestination destination) {
			this.destination = destination;
		}

		protected synchronized ActionCode note(int note, int sel, byte[] data) {

			MappingEntry entry = findMappingEntry(note);
			if (entry != null) {
				data[1] = (byte) entry.getTo();
				if (sel == 127)
					setMonitor(entry.toString() + "(" + sharedInstance().code2Note(entry.getFrom()) + "->"
							+ sharedInstance().code2Note(entry.getTo()) + ")");
				return ActionCode.Mapped;
			}

			if (sel == 127)
				setMonitor("Received " + sharedInstance().code2Note(note));

			return ActionCode.Thru;
		}
	}
	
	
	

	public void setMonitor(String title) {
		this.monitor = title;
	}
	
	public String getMonitor() {
		return monitor;
	}


	
	public static class MappingEntry extends NSObjectImpl {
//		private int 
		private int from,to;
		private boolean persist=true;
		private String name;
		
		public MappingEntry(){
			this(0,0,true,"Untitled Mapping");	
		}
		
		
		public MappingEntry(int from,int to,boolean persist,String name){
			this.from=from;
			this.to=to;
			this.persist = persist;
			this.name = name;
		}
		
		public boolean getPersist(){
			return persist;
		}
		
		public void setPersist(boolean persist) {
			this.persist = persist;
		}
		
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String validateName(String name) throws ValidationException{
			if(name==null || name.trim().isEmpty())
				throw new ValidationException("must not be empty");
			return name.replaceAll("(;|:)","_");
		}
		
		public String getName() {
			return name;
		}

		public int getFrom() {
			return from;
		}

		public void setFrom(int from) {
			this.from = from;
		}

		public int getTo() {
			return to;
		}

		public void setTo(int to) {
			this.to = to;
		}
		
		@Override
		public String toString() {
			return from + ":" + to + ":" + name;
		}
		
		public static MappingEntry parse(String s){
			String[] strings = s.split(":");
			if(strings.length!=3)
				throw new IllegalArgumentException("cannot parse Midi-Note-Mapping:" + s);
			return new MappingEntry(Integer.valueOf(strings[0]),Integer.valueOf(strings[1]),true,strings[2]);
		}
	}
	
	private List<MappingEntry> mappings=new ArrayList<MappingEntry>();


	public int countOfMappings(){
		return mappings.size();
	}
	
	public MappingEntry objectInMappingsAtIndex(int index){
		return mappings.get(index);
	}
	
	/*//insertObjectIn<Key>AtIndex(Object,int)
	protected Method insertObjectMethod;
	
	//Object removeObjectFrom<Key>AtIndex(int)
	*/
	
	
	public String serialize(){
		return StringUtils.join(mappings, ";");
	}
	
	public void deserialize(String mappingString){
		List<MappingEntry> result=new ArrayList<MappingEntry>();
		if(mappingString==null||mappingString.isEmpty())
			return;
		for (String s : mappingString.split(";")) {
			try{
			result.add(MappingEntry.parse(s));
			
			}catch(IllegalArgumentException e){
				System.err.println(e.getMessage());
			}
		}
		
		mutableArrayValueForKey("mappings").addAll(result);
	
	}

	public synchronized void insertObjectInMappingsAtIndex(Object obj,int i){
		mappings.add(i, (MappingEntry) obj);
	}
	
	////Object removeObjectFrom<Key>AtIndex(int)
	
	public synchronized Object removeObjectFromMappingsAtIndex(int i) {
		MappingEntry e = mappings.remove(i);
		
		return e;
	}
}