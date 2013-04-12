package com.someluigi.slperiph.ccportable.shared;

public class PayloadStream {
	
	protected byte[] data;
	protected int count;
	
	public PayloadStream(){
		this(32);
	}
	
	public PayloadStream( int size ){
		data = new byte[size];
	}

	protected void realloc(){
		byte[] newData = new byte[count << 1];
			System.arraycopy(data, 0, newData, 0, count);
		
		data = newData;
	}
	
	protected void checkSize( int delta ){
		if ( data.length < count + delta )
			realloc();
	}
	
	protected void writeRaw( int val ){
		data[count++] = (byte) val;
	}
	
	
	/*
	 * Data encoded
	 */
	public void write( int val ){
		writeByte(val);
	}
	
	public void writeByte( int val ){
		checkSize(1);
		writeRaw(val);
	}
	
	public void writeBoolean( boolean val ){
		writeByte( val ? 1 : 0 );
	}

	public void writeChar( char chr ){
		writeShort(chr);
	}
	
	public void writeShort( int val ){
		checkSize(2);
		
		writeRaw( (val >>> 8) & 0xFF );
		writeRaw( (val >>> 0) & 0xFF );
	}
	
	public void writeInt( int val ){
		checkSize(4);
		
		writeRaw( (val >>> 24) & 0xFF );
		writeRaw( (val >>> 16) & 0xFF );
		writeRaw( (val >>>  8) & 0xFF );
		writeRaw( (val >>>  0) & 0xFF );
	}

	public void writeLong( long val ){
		checkSize(8);
		
		writeRaw( (int) (val >>> 56) & 0xFF );
		writeRaw( (int) (val >>> 48) & 0xFF );
		writeRaw( (int) (val >>> 40) & 0xFF );
		writeRaw( (int) (val >>> 32) & 0xFF );
		
		writeRaw( (int) (val >>> 24) & 0xFF );
		writeRaw( (int) (val >>> 16) & 0xFF );
		writeRaw( (int) (val >>>  8) & 0xFF );
		writeRaw( (int) (val >>>  0) & 0xFF );
	}
	
	public void writeFloat( float val ){
		writeInt( Float.floatToIntBits(val) );
	}
	
	public void writeDouble( double val ){
		writeLong( Double.doubleToLongBits(val) );
	}

	public byte[] toByteArray(){
		byte[] array = new byte[count];
			System.arraycopy(data, 0, array, 0, count);
		
		return array;
	}
	
}
