package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;


public class Record{
	boolean isdeleted = false;

	static List<Field> fields;
	static int totalSize;
	static int totalFields;
	
	public static void setFields(List<Field> fields){
		Record.fields = fields;
	}

	public static void setSize(int totalSize){
		Record.totalSize = totalSize;
	}

	public boolean isDeleted() {
		return isdeleted;
	}
	
	public void readRecord(RandomAccessFile raFile) throws IOException{
		isdeleted = raFile.readByte() != 0;
		if(isdeleted){
			raFile.seek((raFile.getFilePointer() + totalSize) - 2);
			System.out.println("Skipping deleted record");
			return;
		} 
		for(Field f : fields){
			f.readFieldString(raFile);
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