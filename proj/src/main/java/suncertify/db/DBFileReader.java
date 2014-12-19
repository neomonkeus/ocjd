package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DBFileReader {

	public DBFileReader(String dir){
		
		File f = new File(dir);
		if(!f.exists()){
			System.out.println("Could not find file");
			System.exit(0);
		}
		System.out.println(f.getName());
		
		try {
			Header header = new Header(new RandomAccessFile(f,"r"));
			header.readFile();
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
	private List<Record> records;
	private RandomAccessFile raFile;
	
	Header(RandomAccessFile raFile) throws IOException{
		this.raFile = raFile;
	}
	public void readFile() throws IOException{
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
		List<Field> fields = new ArrayList<Field>(numFields);
		System.out.println("Reading Fields");
		for(int i = 0; i < numFields; i++){
			Field field = new Field();
			field.readField(raFile);
			fields.add(field); 
		}
		Record.setFields(fields);
	}
	
	private void readRecords() throws IOException{
		records = new ArrayList<Record>();
		for(int i = 0;i < 5;i++){
			Record r = new Record();
			r.readRecord(raFile);
			records.add(r);
		}

		System.out.println(records);
		
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

		totalSize = namelength + name.length() + dataLength;
	}
	
	public void readFieldData(RandomAccessFile raFile) throws IOException{
		rawData = new byte[dataLength];
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
	
	@Override
	public String toString(){
		return name;
	}
}

class Record{
	boolean isdeleted = false;
	static List<Field> fields;
	
	
	public static void setFields(List<Field> fields){
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
		s.append("Record[{");
		s.append("deleted:");
		s.append(isdeleted);
		s.append("},{");
		
		Iterator<Field> iter = fields.iterator();
		boolean hasMore = iter.hasNext();
		while(hasMore){
			s.append(iter.next().fieldData());
			if(hasMore = iter.hasNext())
				s.append(",");
		}
		s.append("}]");
		return s.toString();
	}
}