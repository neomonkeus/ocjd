package suncertify.db.reader;

import static org.junit.Assert.*;

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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		curdir = Paths.get(".").toAbsolutePath().normalize().toString();
		relResPath = "src" + File.separator + "test" + File.separator + "resources";
		absResDir = curdir + File.separator + relResPath;
		abspath = absResDir + File.separator + filename;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
	
	private void createMagicCookie(int cookieval) throws FileNotFoundException, IOException{
		
		file = new File(abspath);
		try(FileOutputStream output = new FileOutputStream(file)){
			
			ByteBuffer b = ByteBuffer.allocate(DBSchemaInfo.BYTES_MAGIC_COOKIE);
			b.putInt(cookieval);
			output.write(b.array());
		} 
	}
	
}
