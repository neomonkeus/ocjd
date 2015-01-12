package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Record{
	boolean deleted = false;

	static List<Field> fields;
	static int lengthBytes;
	static short numFields;
	
	public static List<Field> getFields(){
		return Record.fields;
	}

	public static void readFieldHeader(RandomAccessFile raFile) throws IOException {
		Record.numFields = raFile.readShort();
		Record.fields = new ArrayList<Field>(numFields);
		for(short i = 0; i < numFields; i++){
			Field field = new Field();
			field.readFieldHeader(raFile);
			fields.add(field); 
		}
	}

	public static void setLength(int lengthBytes){
		Record.lengthBytes = lengthBytes;
	}

	public boolean isDeleted() {
		return deleted;
	}
	
	public void readRecord(RandomAccessFile raFile) throws IOException{
		deleted = raFile.readByte() != 0;
		if(!deleted){
			for(Field f : fields){
				f.readFieldString(raFile);
			}
		} 
		raFile.seek((raFile.getFilePointer() + lengthBytes) - DBSchemaInfo.BYTES_REC_DELETED);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Record[{");
		s.append("deleted:");
		s.append(deleted);
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