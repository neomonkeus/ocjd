package suncertify.db.reader;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import suncertify.db.reader.DBSchemaInfo;

public class TestDBParser {

	private static String absResDir;
	private static String curdir;
	private static String relResPath;
	private static String abspath;
	private static String filename = "test.txt";
	private File file;
	private DBParser parser;
	private RandomAccessFile raFile;
	private ByteBuffer buffer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		curdir = Paths.get(".").toAbsolutePath().normalize().toString();
		relResPath = "src" + File.separator + "test" + File.separator + "resources";
		absResDir = curdir + File.separator + relResPath;
		abspath = absResDir + File.separator + filename;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File file = new File(abspath);
		file.delete();
	}

	@Before
	public void setUp() throws Exception{
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testMagicCookie() throws FileNotFoundException, IOException {
		String filename = "test.txt";
		String abspath = absResDir + File.separator + filename;
		file = new File(abspath);
		try(FileOutputStream output = new FileOutputStream(file)){
			
			ByteBuffer b = ByteBuffer.allocate(DBSchemaInfo.BYTES_MAGIC_COOKIE);
			b.putInt(DBSchemaInfo.EXPECTED_MAGIC_COOKIE);
			output.write(b.array());
		} 
		
		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readMagicCookie();
	}

	@Test(expected=RuntimeException.class)
	public void testInvalidMagicCookie() throws FileNotFoundException, IOException {
		createMagicCookie(123);
		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readMagicCookie();
	}
	
	@Test()
	public void testHeader() throws FileNotFoundException, IOException {
		//setup
		int recSize = 123;
		short numFields = 10;
		createHeader(recSize, numFields);
		
		//run
		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readHeader();
		
		//verify
		assertThat(Record.totalSize, equalTo(recSize));
	}
	
	
	private void createHeader(int recSize, short numFields) throws FileNotFoundException, IOException {
		file = new File(abspath);
		try(FileOutputStream output = new FileOutputStream(file)){
			
			int headerLength = DBSchemaInfo.BYTES_REC_LENGTH + DBSchemaInfo.BYTES_NUM_FIELDS;
			buffer = ByteBuffer.allocate(headerLength);
			buffer.putInt(recSize);
			buffer.putShort(numFields);
			output.write(buffer.array());
		} 
	}

	private void createMagicCookie(int cookieval) throws FileNotFoundException, IOException{
		file = new File(abspath);
		try(FileOutputStream output = new FileOutputStream(file)){
			
			buffer = ByteBuffer.allocate(DBSchemaInfo.BYTES_MAGIC_COOKIE);
			buffer.putInt(cookieval);
			output.write(buffer.array());
		} 
	}
	
//	private void createField(Field[] fields) throws FileNotFoundException, IOException{
//		file = new File(abspath);
//		try(FileOutputStream output = new FileOutputStream(file)){
//			buffer = ByteBuffer.allocate();
//			buffer.
//		}
//	}
}
