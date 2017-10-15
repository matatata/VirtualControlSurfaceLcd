/**
 * 
 */
package de.ceruti.mackie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ceruti.curcuma.api.keyvaluecoding.KeyValueCoding;
import de.ceruti.curcuma.api.keyvalueobserving.KeyValueObserving;
import de.ceruti.curcuma.foundation.KVOMutableArrayProxy;
import de.ceruti.curcuma.foundation.NSObjectImpl;
import de.ceruti.curcuma.keyvaluebinding.KeyValueBindingCreator;
import de.ceruti.curcuma.keyvaluecoding.DefaultErrorHandling;
import de.ceruti.curcuma.keyvaluecoding.KeyValueCodeable;
import de.ceruti.curcuma.keyvalueobserving.KeyValueObservable;
import de.ceruti.midi.core.MidiHandlerAdapter;

@KeyValueCodeable
@DefaultErrorHandling
@KeyValueObservable
@KeyValueBindingCreator
//@NSObject
public abstract class MackieMapper extends MidiHandlerAdapter {

	private Map<Integer, Integer> keyMapper = new HashMap<Integer, Integer>();

	public MackieMapper() {
		super();
		readMap();
	}


	public static Set<String> keyPathsForValuesAffectingValueForKey(String key) {
		return new HashSet<String>();
	}	
	
	public List mutableArrayValueForKey(String key) {
		return new KVOMutableArrayProxy((KeyValueCoding)this,(KeyValueObserving)this,key);
	}
	

	protected synchronized ActionCode note(int note, int sel, byte[] data) {
		if (keyMapper.containsKey(note)) {
			data[1] = keyMapper.get(note).byteValue();
			return ActionCode.Mapped;
		}

		return ActionCode.Thru;
	}

	

	private void readMap() {
//		keyMapper.put(91, 98);
//		keyMapper.put(92, 99);
//		keyMapper.put(93, 96);
//		keyMapper.put(94, 97);
//
//		keyMapper.put(96, 94);
//		keyMapper.put(97, 93);
//		keyMapper.put(98, 91);
//		keyMapper.put(99, 92);
		
		insertObjectInMappingsAtIndex(new MappingEntry(91, 98), countOfMappings());
		insertObjectInMappingsAtIndex(new MappingEntry(92, 99), countOfMappings());
		insertObjectInMappingsAtIndex(new MappingEntry(93, 96), countOfMappings());
		insertObjectInMappingsAtIndex(new MappingEntry(94, 97), countOfMappings());
	
		insertObjectInMappingsAtIndex(new MappingEntry(96, 94), countOfMappings());
		insertObjectInMappingsAtIndex(new MappingEntry(97, 93), countOfMappings());
		insertObjectInMappingsAtIndex(new MappingEntry(98, 91), countOfMappings());
		insertObjectInMappingsAtIndex(new MappingEntry(99, 92), countOfMappings());
		
	}
	
	public static class MappingEntry extends NSObjectImpl {
//		private int 
		private int from,to;
		
		public MappingEntry(){
			this(0,0);	
		}
		
		public MappingEntry(int from,int to){
			this.from=from;
			this.to=to;
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

	public synchronized void insertObjectInMappingsAtIndex(Object obj,int i){
		mappings.add(i, (MappingEntry) obj);
		keyMapper.put(((MappingEntry) obj).getFrom(),((MappingEntry) obj).getTo());
	}
	
	////Object removeObjectFrom<Key>AtIndex(int)
	
	public synchronized Object removeObjectFromMappingsAtIndex(int i) {
		MappingEntry e = mappings.remove(i);
		if(e!=null)
			keyMapper.remove(e.getFrom());
		
		return e;
	}
}