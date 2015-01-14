package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;


public class DBParser{
	
	private int magicCookie;
	private List<Record> records;
	private RandomAccessFile raFile;
	public static long START_OF_RECORDS;
	
	public DBParser(RandomAccessFile raFile){
		this.raFile = raFile;
	}
	
	public void parseDBFile() throws IOException{
		readMagicCookie();
		readHeaders();
		START_OF_RECORDS = raFile.getFilePointer();
		System.out.println("Reading Records");
		readAllRecords();
	}
	
	public List<Record> getRecords(){
		return records;
	}
	
	void readMagicCookie() throws IOException{
		magicCookie = raFile.readInt();
		if(magicCookie != DBSchemaInfo.EXPECTED_MAGIC_COOKIE){
			throw new RuntimeException();
		}
		System.out.println("Magic Cookie" + magicCookie);
	}
	
	void readHeaders() throws IOException{
		int recLength = raFile.readInt();
		System.out.println("reclength: " + recLength);
		Record.setLength(recLength);
		Record.readHeaders(raFile);
	}

	void readAllRecords() throws IOException{
		records = new ArrayList<Record>();
		while (raFile.getFilePointer() != raFile.length()) {
			Record rec = new Record();
			rec.readField(raFile);
			if(!rec.isDeleted()){
				records.add(rec);
			}
		}
		
	}
	
	
	
}
