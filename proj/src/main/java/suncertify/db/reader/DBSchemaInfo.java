package suncertify.db.reader;

public class DBSchemaInfo {

	/** The character encoding used for the database file. */
	public static final String US_ASCII = "US-ASCII";
	
	public static final byte BYTES_MAGIC_COOKIE = 4;
	
	public static final short EXPECTED_MAGIC_COOKIE = 0x201;
	
	public static final byte BYTES_REC_LENGTH = 4;
	
	public static final byte BYTES_NUM_FIELDS = 2;
	
	public static final byte BYTES_REC_DELETED = 2;
}
