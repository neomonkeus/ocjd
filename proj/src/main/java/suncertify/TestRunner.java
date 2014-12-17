package suncertify;

import suncertify.db.DBFileReader;

public class TestRunner {

	public static void main(String[] args) {
		System.out.println("Running via test runner");
		if(args.length == 0)
			System.out.println("No args provided");
		else{
			DBFileReader reader = new DBFileReader(args[0]);
		}
	}

}
