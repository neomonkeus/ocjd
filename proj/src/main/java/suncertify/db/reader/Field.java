package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;


public class Field{
	short namelength;
	String name;
	
	short dataLength;
	String fieldData;

	int fieldsize;

	public void readField(RandomAccessFile raFile) throws IOException{
		namelength = raFile.readShort();
		name = readString(raFile, namelength);
		
		dataLength = raFile.readShort();

		fieldsize = namelength + name.length() + dataLength;
	}
	
	public void readFieldString(RandomAccessFile raFile) throws IOException{
		 fieldData = readString(raFile, dataLength);
	}
	
	public int getFieldSize(){
		return fieldsize;
	}
	
	public String fieldData(){
		return name + ":" + fieldData.trim();
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

	private String readString(RandomAccessFile raFile, int stringlength) throws IOException{
		byte[] rawString = new byte[namelength];
		raFile.read(rawString);
		return new String(rawString, DBSchemaInfo.US_ASCII);
	}
	
	
}


