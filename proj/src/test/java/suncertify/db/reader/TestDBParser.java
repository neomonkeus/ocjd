package suncertify.db.reader;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.List;

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
	private static File file;
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
	
	}

	@Before
	public void setUp() throws Exception{
		file = new File(abspath);
		file.createNewFile();
		raFile = new RandomAccessFile(abspath, "rw");
		raFile.setLength(0);
	}

	@After
	public void tearDown() throws Exception {
		file.delete();
	}

	@Test
	public void testMagicCookie() throws FileNotFoundException, IOException {
		createMagicCookie(DBSchemaInfo.EXPECTED_MAGIC_COOKIE);

		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readMagicCookie();
	}

	@Test(expected=RuntimeException.class)
	public void testInvalidMagicCookie() throws FileNotFoundException, IOException {
		short incorrectval = 0x123;
		createMagicCookie(incorrectval);
		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readMagicCookie();
	}
	
	@Test()
	public void testHeader() throws FileNotFoundException, IOException {
		//setup
		Header template = createHeader();
		short numFields = 4;
		createHeaders(numFields, template);
		
		//execute
		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readHeaders();
		
		//verify
		int recSize = template.headerLength * numFields;
		assertThat(Record.lengthBytes, equalTo(recSize));
		assertThat(Record.numFields, equalTo(numFields));
	}
	
	@Test()
	public void testRecord() throws FileNotFoundException, IOException {
		//setup
		Header template = createHeader();
		short numFields = 4;
		createHeaders(numFields, template);
		createRecords(numFields, template);
		
		//execute
		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readHeaders();
		parser.readAllRecords();
		
		//verify
		int recSize = template.headerLength * numFields;
		assertThat(Record.lengthBytes, equalTo(recSize));
		assertThat(Record.numFields, equalTo(numFields));
		List<Record> recs = parser.getRecords();
		System.out.println("Records:");
		for(Record r : recs){
			
			System.out.println(r);
		}
	}
	
	private void createRecords(short numFields, Header template) throws FileNotFoundException, IOException {
		writeRecord(template, numFields, (byte) 1);
		writeRecord(template, numFields, (byte) 0);
	}

	private void createMagicCookie(short cookieval) throws FileNotFoundException, IOException{
		try(FileOutputStream output = new FileOutputStream(file, true)){
			buffer = ByteBuffer.allocate(DBSchemaInfo.BYTES_MAGIC_COOKIE);
			buffer.putInt(cookieval);
			output.write(buffer.array());
		} 
	}
	
	private void createHeaders(short numFields, Header template) throws FileNotFoundException, IOException {
		try(FileOutputStream output = new FileOutputStream(file, true)){
			int headersize = numFields * template.headerLength;
			buffer = ByteBuffer.allocate(DBSchemaInfo.BYTES_REC_LENGTH + DBSchemaInfo.BYTES_NUM_FIELDS + (headersize * numFields));
			buffer.putInt(headersize);
			
			//field info
			buffer.putShort(numFields);
			for(int i = 0; i < numFields; i++){
				writeFieldHeader(template);
			}
			output.write(buffer.array());
		} 
	}
	
	private void writeFieldHeader(Header template) throws IOException{
		buffer.putShort(template.nameLenght);
		buffer.put(template.name.getBytes(DBSchemaInfo.US_ASCII));
		buffer.putShort(template.valueLength);
	}

	private Header createHeader(){
		String name = "name";
		String value = "value_padded";
		
		Header h = new Header();
		h.nameLenght = (short) name.getBytes().length;
		h.name = name;
		h.valueLength = (short) value.getBytes().length;
		h.headerLength = h.nameLenght + h.name.getBytes().length + h.valueLength;
		return h;
	}

	private void writeRecord(Header template, short numFields, byte deleted) throws FileNotFoundException, IOException{
		file = new File(abspath);
		try(FileOutputStream output = new FileOutputStream(file, true)){
			buffer = ByteBuffer.allocate(Record.lengthBytes);
			buffer.put(deleted);
			int len = template.valueLength;
			for(int i = 0; i < numFields; i++){
				StringBuilder s = new StringBuilder(len);
				s.insert(0, i);
				buffer.put(s.toString().getBytes());
			}
			output.write(buffer.array());
		}
	}
}
