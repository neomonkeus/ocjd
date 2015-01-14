package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Util {

	
	public static String readString(RandomAccessFile raFile, int stringlength) throws IOException{
		byte[] rawString = new byte[stringlength];
		raFile.read(rawString);
		return new String(rawString, DBSchemaInfo.US_ASCII);
	}
}
