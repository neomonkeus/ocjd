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
	
	public DBParser(RandomAccessFile raFile){
		this.raFile = raFile;
	}
	
	public void parseDBFile() throws IOException{
		readMagicCookie();
		readHeader();
		readFields();
		START_OF_RECORDS = raFile.getFilePointer();
		readAllRecords();
	}
	
	void readHeader() throws IOException{
		Record.setSize(raFile.readInt());
		numFields = raFile.readShort();
	}
	
	void readMagicCookie() throws IOException{
		magicCookie = raFile.readInt();
		if(magicCookie != DBSchemaInfo.EXPECTED_MAGIC_COOKIE){
			throw new RuntimeException();
		}
	}
	
	void readFields() throws IOException{
		List<Field> fields = new ArrayList<Field>(numFields);
		System.out.println("Reading Fields");
		for(int i = 0; i < numFields; i++){
			Field field = new Field();
			field.readField(raFile);
			fields.add(field); 
		}
		Record.setFields(fields);
	}
	
	void readAllRecords() throws IOException{
		records = new ArrayList<Record>();
		while (raFile.getFilePointer() != raFile.length()) {
			Record rec = new Record();
			rec.readRecord(raFile);
			if(!rec.isDeleted()){
				records.add(rec);
			}
			System.out.println(rec);
		}
	}
	
	
	
}
