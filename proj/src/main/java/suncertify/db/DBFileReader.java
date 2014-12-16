package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DBFileReader {

	private int magiccookie;
	
	public DBFileReader(String dir) throws FileNotFoundException{
		
		File f = new File(dir);
		if(!f.exists()){
			System.out.println("Could not find file");
			System.exit(0);
		}
		System.out.println(f.getName());
		
		RandomAccessFile raFile = new RandomAccessFile(f,"r");
		readHeader(raFile);
	}
	
	private void readHeader(RandomAccessFile raFile){
		try {
			magiccookie = raFile.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(magiccookie);
	}

}
