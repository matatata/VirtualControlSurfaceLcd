package de.ceruti.midi.core;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.map.ListOrderedMap;



public class MidiNotes {
	private ListOrderedMap note2Code = new ListOrderedMap();
	private ListOrderedMap code2Note = new ListOrderedMap();
	
	
	private void init(){
		String[] noteStrings = new String[]{
			"c","c#","d","d#","e","f","f#","g","g#","a","a#","b"	
		};
		int code = 0;
		for(int oct = -2; oct<=8; oct++){
			for(int note=0;note<12;note++){
				if(code > 127)
					return;
			
				String noteString = noteStrings[note].toUpperCase() + String.valueOf(oct);
				note2Code.put(noteString,code);
				code2Note.put(code,noteString);
				
				code++;
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<String> notesList(){
		return Collections.unmodifiableList(note2Code.keyList());
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> codeList(){
		return Collections.unmodifiableList(code2Note.keyList());
	}
	
	public String code2Note(int code){
		if(code < 0 || code > 127)
			throw new IllegalArgumentException();
		Object ret = code2Note.get((Object)code);
		
		return (String)ret;
	}
	
	public int note2Code(String note){
		if(!note2Code.containsKey(note))
			throw new IllegalArgumentException();
		return (Integer)note2Code.get((Object)note);
	}
	
	@Override
	public String toString() {
		return note2Code.toString() + "\n" + code2Note.toString();
	}
	
	public static void main(String[] args) {
		MidiNotes nn = MidiNotes.sharedInstance();
		

		System.out.println(nn);
		
		System.out.println(nn.codeList());
		System.out.println(nn.notesList());
		
	}
	
	private MidiNotes() {
		init();
	}
	
	private static MidiNotes instance = new MidiNotes();
	
	public static MidiNotes sharedInstance(){
		return instance;
	}
	
	public static final Format format =  new Format() {
		
		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return MidiNotes.sharedInstance().note2Code(source);
		}
		
		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo,
				FieldPosition pos) {
			toAppendTo.append(MidiNotes.sharedInstance().code2Note((Integer)obj));
			return toAppendTo;
		}
	};
	
}
