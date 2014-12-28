package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;


public class Field{
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
		name = new String(rawname, DBSchemaInfo.US_ASCII );
		
		dataLength = raFile.readShort();

		totalSize = namelength + name.length() + dataLength;
	}
	
	public void readFieldData(RandomAccessFile raFile) throws IOException{
		rawData = new byte[dataLength];
		raFile.readFully(rawData);
		data = new String(rawData, DBSchemaInfo.US_ASCII );
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


