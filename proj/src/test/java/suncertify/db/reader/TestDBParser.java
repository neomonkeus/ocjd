package suncertify.db.reader;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

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
		Field template = createField();
		short numFields = 10;
		createFieldHeaders(numFields, template);
		
		//execute
		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readFieldHeaders();
		
		//verify
		int recSize = template.headerLength * numFields;
		assertThat(Record.lengthBytes, equalTo(recSize));
		assertThat(Record.numFields, equalTo(numFields));
	}
	
	@Test()
	public void testRecord() throws FileNotFoundException, IOException {
		//setup
		Field template = createField();
		short numFields = 10;
		createFieldHeaders(numFields, template);
		createRecords(numFields, template);
		
		//execute
		raFile = new RandomAccessFile(abspath, "r");
		parser = new DBParser(raFile);
		parser.readFieldHeaders();
		parser.readAllRecords();
		
		//verify
		int recSize = template.headerLength * numFields;
		assertThat(Record.lengthBytes, equalTo(recSize));
		assertThat(Record.numFields, equalTo(numFields));
		List<Record> recs = parser.getRecords();
		System.out.println(recs);
	}
	
	private void createRecords(short numFields, Field template) throws FileNotFoundException, IOException {
		writeRecord(template, numFields, (byte) 0x8000);
		writeRecord(template, numFields, (byte) 0x0000);
	}

	private void createMagicCookie(short cookieval) throws FileNotFoundException, IOException{
		try(FileOutputStream output = new FileOutputStream(file, true)){
			buffer = ByteBuffer.allocate(DBSchemaInfo.BYTES_MAGIC_COOKIE);
			buffer.putInt(cookieval);
			output.write(buffer.array());
		} 
	}
	
	private void createFieldHeaders(short numFields, Field template) throws FileNotFoundException, IOException {
		try(FileOutputStream output = new FileOutputStream(file, true)){
			int headersize = numFields * template.headerLength;
			buffer = ByteBuffer.allocate(DBSchemaInfo.BYTES_REC_LENGTH + DBSchemaInfo.BYTES_NUM_FIELDS + (headersize * 10));
			buffer.putInt(headersize);
			
			//field info
			buffer.putShort(numFields);
			for(int i = 0; i < numFields; i++){
				writeFieldHeader(template);
			}
			output.write(buffer.array());
		} 
	}
	
	private void writeFieldHeader(Field template) throws UnsupportedEncodingException{
		buffer.putShort(template.nameLenght);
		buffer.put(template.name.getBytes(DBSchemaInfo.US_ASCII));
		buffer.putShort(template.valueLength);
	}

	private Field createField(){
		String name = "name";
		String value = "value_padded";
		
		Field f = new Field();
		f.nameLenght = (short) name.getBytes().length;
		f.name = name;
		f.valueLength = (short) value.getBytes().length;
		f.value = value;
		f.headerLength = f.nameLenght + f.name.getBytes().length + f.valueLength;
		return f;
	}

	private void writeRecord(Field template, short numFields, byte deleted) throws FileNotFoundException, IOException{
		file = new File(abspath);
		try(FileOutputStream output = new FileOutputStream(file, true)){
			buffer = ByteBuffer.allocate((DBSchemaInfo.BYTES_REC_DELETED + template.valueLength) * numFields);
			buffer.put(deleted);
			for(int i = 0; i < numFields; i++){
				buffer.put(template.value.getBytes());
			}
			output.write(buffer.array());
		}
	}
}
