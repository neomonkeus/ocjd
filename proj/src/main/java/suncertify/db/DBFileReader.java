package suncertify.db;

import java.io.File;

public class DBFileReader implements DBAccess {

	public DBFileReader(String dir){
		
		File f = new File(dir);
		if(!f.exists()){
			System.out.println("Could not find file");
			System.exit(0);
		}
		System.out.println(f.getName());
	}
	
	
	public String[] readRecord(long recNo) throws RecordNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateRecord(long recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		// TODO Auto-generated method stub

	}

	public void deleteRecord(long recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		// TODO Auto-generated method stub

	}

	public long[] findByCriteria(String[] criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	public long createRecord(String[] data) throws DuplicateKeyException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long lockRecord(long recNo) throws RecordNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void unlock(long recNo, long cookie) throws SecurityException {
		// TODO Auto-generated method stub

	}

}
