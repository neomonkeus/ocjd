package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.db.reader.DBParser;

public class DBFileReader {

	public DBFileReader(String filepath){
		File f = new File(filepath);
		if(!f.exists()){
			System.out.println("Could not find file");
			System.exit(0);
		}
		System.out.println(f.getName());
		
		try {
			DBParser parser = new DBParser(new RandomAccessFile(f,"r"));
			parser.parseDBFile();
		} catch (FileNotFoundException fnfEx){
			System.out.println("Could not find file");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}




