package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Field{
	
	String value;
	
	public void readFieldString(RandomAccessFile raFile, short length) throws IOException{
		 value = Util.readString(raFile, length);
	}
	
	@Override
	public String toString(){
		return value;
	}

	
}


