package suncertify;
import java.io.FileNotFoundException;

import suncertify.db.DBFileReader;

public class TestRunner {

	public static void main(String[] args) {
		System.out.println("Running via test runner");
		if(args.length == 0)
			System.out.println("No args provided");
		else{
			try {
				DBFileReader reader = new DBFileReader(args[0]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
