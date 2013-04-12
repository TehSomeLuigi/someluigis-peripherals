package com.someluigi.slperiph.ccportable.shared;


import java.io.DataInputStream;
import java.io.IOException;

public class Terminal {
	
	public static byte parseColor( int color ){
		byte index = 0;

		while ( color > 0 ){
			color >>= 1;
			index++;
		}
		
		if ( index > 15 ) 
			return 0;
		
		return (byte) (16 - index);
	}
	
	public static Terminal readNewPayload( DataInputStream in ) throws IOException{
		//Read constructor data, and full update
		Terminal term = new Terminal( in.readShort(), in.readShort(), in.readBoolean() );
			term.readUpdatePayload(in);
		
		return term;
	}
	
	public final boolean isColorSupported;
	
	public final int w;
	public final int h;

	protected boolean[] isLineDirty;

	public char[][] chars;
	public byte[][] charColors;
	
	protected int cursorX = 0;
	protected int cursorY = 0;
	
	public boolean cursorBlink = false;
	
	public byte colorText;
	public byte colorBackground;
	
	public Terminal( int wide, int tall, boolean isColor ){
		this.w = wide;
		this.h = tall;
		
		this.chars = new char[tall][wide];
		this.isLineDirty = new boolean[tall];
		
		this.isColorSupported = isColor;
		
		if ( isColor )
			charColors = new byte[tall][wide];
	}
	
	public void write( String text ){
		if ( cursorY < 0 || cursorY > h )
			return;
		
		cursorX += write( cursorX, cursorY, text );
	}
	
	protected int write( int x, int y, String text ){	
		char[] line = text.toCharArray();	
		
		int len = Math.min( w - x, line.length );
		int off = x < 0 ? -x : 0;
		
		for ( int index = off; index < len; index++ )
			setChar(x + index, y, line[index] );
		
		isLineDirty[y] = true;
		
		return len;
	}
	
	protected void setChar( int x, int y, char chr ){
		chars[y][x] = chr;
		
		if ( isColorSupported )
			charColors[y][x] = (byte) (colorText | colorBackground << 4);
	}

	public void setCursorPos( int x, int y ){
		cursorX = x;
		cursorY = y;
	}
	
	public int getCursorX(){
		return cursorX;
	}
	public int getCursorY(){
		return cursorY;
	}
	
	
	//TODO: Scrolling has some extreme network overhead!
	public void scroll( int delta ){
		char[][] newChars		= new char[h][];
		byte[][] newCharColors	= new byte[h][];
		
		for ( int lineID = 0; lineID < h; lineID++ ){
			int newID = lineID + delta;
			
			if ( 0 <= newID && newID < h ){
				newChars[lineID] = chars[newID];
				
				if ( isColorSupported )
					newCharColors[lineID] = charColors[newID];
			} else {
				newChars[lineID] = new char[w];
				
				if ( isColorSupported )
					newCharColors[lineID] = new byte[w];
			}
		
			isLineDirty[lineID] = true;
		}
		
		chars 		= newChars;
		charColors	= newCharColors;
	}
	
	protected void clearLine( int lineID ){
		chars[lineID] = new char[w];
		
		if ( isColorSupported )
			charColors[lineID] = new byte[w];
	}
	
	public void clearLine(){
		clearLine( cursorY );
	}
	
	public void clear(){
		for ( int lineID = 0; lineID < h; lineID++ )
			clearLine(lineID);
	}
	
	/*
	 * Network
	 */
	public void writeNewPayload( PayloadStream out ){
		//Constructor data
		out.writeShort( w );
		out.writeShort( h );
		
		out.writeBoolean( isColorSupported );
		
		//Regular data, full update
		writeUpdatePayload(out, true);
	}
	
	public void readUpdatePayload( DataInputStream in ) throws IOException{
		
		//Generic data
		cursorBlink = in.readBoolean();
		cursorX = in.readShort();
		cursorY = in.readShort();
		
		int lineID = 0;
	
		//Read line offset
		while( lineID < h ){
			int off = in.readByte(); //Skip lines that are not changed
			
			if ( off == -1 ) //End semaphore
				break;
			
			lineID += off;
			
			//Read char data
			for( int x = 0; x < w; x++ )
				chars[lineID][x] = in.readChar();
			
			//Read colors, if supported
			if ( isColorSupported ){
				for( int x = 0; x < w; x++ )
					charColors[lineID][x] = in.readByte();	
			}
		}

	}
	
	public void writeUpdatePayload( PayloadStream out ){
		writeUpdatePayload(out, false);
	}
	
	protected void writeUpdatePayload( PayloadStream out, boolean isFull ){
		
		//Generic data
		out.writeBoolean( cursorBlink );
		out.writeShort( cursorX );
		out.writeShort( cursorY );
		
		int delta = 0;
		
		//Write lines, one by one
		for ( int y = 0; y < h; y++ ){
			
			//Write only dirty lines, indexing is done with some offset magic
			if ( isLineDirty[y] || isFull ){
				out.writeByte(delta);
				
				delta = 1;
			} else {
				delta++;
				continue;
			}
			
			for( int x = 0; x < w; x++ ) //Write char payload	
				out.writeChar( chars[y][x] );

		
			//Write colors, if supported
			if ( isColorSupported ){
				for( int x = 0; x < w; x++ )
					out.writeByte( charColors[y][x] );	
			}
			
		}
		
		out.write( -1 ); //End semaphore
	}
	
}
