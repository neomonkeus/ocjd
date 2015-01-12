package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;


public class Field{
	short nameLenght;
	String name;
	
	short valueLength;
	String value;

	int headerLength;

	public void readFieldHeader(RandomAccessFile raFile) throws IOException{
		nameLenght = raFile.readShort();
		name = readString(raFile, nameLenght);
		
		valueLength = raFile.readShort();
		headerLength = nameLenght + name.getBytes().length + valueLength;
	}
	
	public void readFieldString(RandomAccessFile raFile) throws IOException{
		 value = readString(raFile, valueLength);
	}
	
	public String fieldData(){
		return name + ":" + value.trim();
	}
	
	public String fieldDescription() {
		StringBuilder s = new StringBuilder();
		s.append("Field:[");
		s.append(name);
		s.append(":");
		s.append(valueLength);
		s.append("]");
		return s.toString();
	}
	
	@Override
	public String toString(){
		return name;
	}

	private String readString(RandomAccessFile raFile, int stringlength) throws IOException{
		byte[] rawString = new byte[nameLenght];
		raFile.read(rawString);
		return new String(rawString, DBSchemaInfo.US_ASCII);
	}
	
	
}


