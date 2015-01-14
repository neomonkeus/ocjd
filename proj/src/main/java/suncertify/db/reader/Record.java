package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Record{
	boolean deleted = false;

	static List<Header> headers;
	static int lengthBytes;
	static short numFields;
	
	Map<Header, String> values;
	
	public Record(){
		values = new HashMap<Header, String>(Record.numFields);
	}
	
	public static List<Header> getHeaders(){
		return Record.headers;
	}

	public static void readHeaders(RandomAccessFile raFile) throws IOException {
		Record.numFields = raFile.readShort();
		System.out.println("Num Fields: " + Record.numFields);
		Record.headers = new ArrayList<Header>(numFields);
		for(short i = 0; i < numFields; i++){
			Header header = new Header();
			header.readFieldHeader(raFile);
			headers.add(header); 
		}
		System.out.println("Read Headers");
		System.out.println(headers);
	}

	public static void setLength(int lengthBytes){
		Record.lengthBytes = lengthBytes;
	}

	public boolean isDeleted() {
		return deleted;
	}
	
	public void readField(RandomAccessFile raFile) throws IOException{
		deleted = raFile.readByte() != 0;
		if(!deleted){
			for(Header h : headers){
				String value = Util.readString(raFile, h.valueLength);
				values.put(h, value.trim());
			}
		} else {
			raFile.seek((raFile.getFilePointer() + lengthBytes) - DBSchemaInfo.BYTES_REC_DELETED);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Record[{");
		s.append("deleted:");
		s.append(deleted);
		s.append("},");
		for(Header h : headers){
			s.append(h.toString() + values.get(h).trim());
		}
		s.append("]");
		return s.toString();
	}

}