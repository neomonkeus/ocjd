package suncertify.db.reader;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Header {

	short nameLenght;
	String name;
	
	short valueLength;

	int headerLength;
	
	
	public void readFieldHeader(RandomAccessFile raFile) throws IOException{
		nameLenght = raFile.readShort();
		name = Util.readString(raFile, nameLenght);
		
		valueLength = raFile.readShort();
		headerLength = nameLenght + name.getBytes().length + valueLength;
	}


	@Override
	public String toString() {
		return "Header[" + name +"]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + headerLength;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + nameLenght;
		result = prime * result + valueLength;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Header other = (Header) obj;
		if (headerLength != other.headerLength)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameLenght != other.nameLenght)
			return false;
		if (valueLength != other.valueLength)
			return false;
		return true;
	}
	
	
}
