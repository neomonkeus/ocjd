package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DBFileReader {

	public DBFileReader(String dir){
		
		File f = new File(dir);
		if(!f.exists()){
			System.out.println("Could not find file");
			System.exit(0);
		}
		System.out.println(f.getName());
		
		RandomAccessFile raFile;
		try {
			raFile = new RandomAccessFile(f,"r");
			Header header = new Header(raFile);
		} catch (FileNotFoundException fnfEx){
			System.out.println("Could not find file");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}


class Header{
	
	private int magicCookie;
	private int recordLength;
	private short numFields;
	private Field[] fields;
	private Record[] records;
	private RandomAccessFile raFile;
	
	Header(RandomAccessFile raFile) throws IOException{
		this.raFile = raFile;
		readHeader();
		readFieldInfo();
		readRecords();
	}
	
	
	private void readHeader() throws IOException{
		magicCookie = raFile.readInt();
		recordLength = raFile.readInt();
		numFields = raFile.readShort();
		System.out.println("Magic Cookie: " + magicCookie);
		System.out.println("Records total size: " + recordLength);
		System.out.println("Num fields: " + numFields);
	}
	
	private void readFieldInfo() throws IOException{
		fields = new Field[numFields];
		System.out.println("Reading Fields");
		for(int i = 0; i < numFields; i++){
			Field field = new Field();
			field.readField(raFile);
			fields[i] = field; 
		}
		Record.setFields(fields);
	}
	
	private void readRecords() throws IOException{
		records = new Record[1];
		Record r = new Record();
		r.readRecord(raFile);
		System.out.println(r);
		r.readRecord(raFile);
		System.out.println(r);
		r.readRecord(raFile);
		System.out.println(r);
		r.readRecord(raFile);
		System.out.println(r);
		r.readRecord(raFile);
		System.out.println(r);
		r.readRecord(raFile);
		System.out.println(r);
		r.readRecord(raFile);
		System.out.println(r);
	}
	
}

class Field{
	short namelength;
	String name;
	short dataLength;
	int totalSize;
	
	byte[] rawData;
	String data;

	public void readField(RandomAccessFile raFile) throws IOException{
		namelength = raFile.readShort();
		byte[] rawname = new byte[namelength];
		raFile.read(rawname);
		name = new String(rawname, "US-ASCII" );
		
		dataLength = raFile.readShort();
		rawData = new byte[dataLength];

		totalSize = namelength + name.length() + dataLength;
	}
	
	public void readFieldData(RandomAccessFile raFile) throws IOException{
		raFile.readFully(rawData);
		data = new String(rawData, "US-ASCII" );
	}
	
	
	public String fieldData(){
		return name + ":" + data.trim();
	}
	
	public String fieldDescription() {
		StringBuilder s = new StringBuilder();
		s.append("Field:[");
		s.append(name);
		s.append(":");
		s.append(dataLength);
		s.append("]");
		return s.toString();
	}
}

class Record{
	boolean isdeleted = false;
	static Field[] fields;
	
	public static void setFields(Field[] fields){
		Record.fields = fields;
	}
	
	public void readRecord(RandomAccessFile raFile) throws IOException{
		isdeleted = raFile.readByte() != 0;
		for(Field f : fields){
			f.readFieldData(raFile);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Record[deleted:");
		s.append(isdeleted);
		s.append("[");
		for(Field f : fields){
			s.append(f.fieldData());
		}
		s.append("]");
		return s.toString();
	}
}