package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;


public class DBParser{
	
	private int magicCookie;
	private int recordLength;
	private short numFields;
	private List<Record> records;
	private RandomAccessFile raFile;
	public static long START_OF_RECORDS;
	
	public DBParser(RandomAccessFile raFile) throws IOException{
		this.raFile = raFile;
	}
	
	public void parseDBFile() throws IOException{
		readHeader();
		readFields();
		START_OF_RECORDS = raFile.getFilePointer();
		readAllRecords();
	}
	
	private void readHeader() throws IOException{
		magicCookie = raFile.readInt();
		Record.setRecordSize(raFile.readInt());
		numFields = raFile.readShort();
		System.out.println("Magic Cookie: " + magicCookie);
		System.out.println("Records total size: " + recordLength);
		System.out.println("Num fields: " + numFields);
	}
	
	private void readFields() throws IOException{
		List<Field> fields = new ArrayList<Field>(numFields);
		System.out.println("Reading Fields");
		for(int i = 0; i < numFields; i++){
			Field field = new Field();
			field.readField(raFile);
			fields.add(field); 
		}
		Record.setFields(fields);
	}
	
	private void readAllRecords() throws IOException{
		records = new ArrayList<Record>();
		while (raFile.getFilePointer() != raFile.length()) {
			Record r = new Record();
			r.readRecord(raFile);
			records.add(r);
		}
	}
	
	
	
}
