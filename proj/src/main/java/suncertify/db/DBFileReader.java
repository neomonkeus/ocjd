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
	private RandomAccessFile raFile;
	
	Header(RandomAccessFile raFile) throws IOException{
		this.raFile = raFile;
		readHeader();
		readFieldInfo();
	}
	
	
	private void readHeader() throws IOException{
		recordLength = raFile.readInt();
		magicCookie = raFile.readInt();
		numFields = raFile.readShort();
		System.out.println("Magic Cookie: " + magicCookie);
		System.out.println("Record Lenth: " + recordLength);
		System.out.println("Num fields: " + numFields);
	}
	
	private void readFieldInfo() throws IOException{
		fields = new Field[numFields];
		System.out.println("Reading Fields");
		for(int i = 0; i < numFields; i++){
			Field field = new Field();
			field.namelenght = raFile.readShort();
			byte[] name = new byte[field.namelenght];
			raFile.read(name);
			field.name = new String(name, "US-ASCII" );
			field.size = raFile.readShort();
			fields[i] = field; 
			System.out.println(field.toString());
		}
	}
	
}

class Field{
	short namelenght;
	String name;
	short size;
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Field:[");
		s.append(name);
		s.append(":");
		s.append(size);
		s.append("]");
		return s.toString();
	}
}
