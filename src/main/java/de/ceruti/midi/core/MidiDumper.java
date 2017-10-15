package de.ceruti.midi.core;

public class MidiDumper {
	private static String[] hexChars = new String[] { "0", "1", "2", "3", "4",
		"5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
	public static void dumpData(byte[] data) {
		for (int i = 0; i < data.length; i++) {
			System.out.print(hexChars[(data[i] & 0xFF) / 16]);
			System.out.print(hexChars[(data[i] & 0xFF) % 16] + "  ");
			if (data.length > 5 && (i + 1) % 16 == 0)
				System.out.println("");
		}
		System.out.println("");
	}
	
	private MidiDumper(){}
}
