package com.someluigi.slperiph.ccportable.gfx;


import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import com.someluigi.slperiph.ccportable.shared.Terminal;

public class TerminalRenderer {
	
	public static boolean getBlink(){
		return System.currentTimeMillis() % 1000 < 500;
	}

	protected RenderEngine render;
	protected Tessellator buff;

	public static final int FONT_WIDTH = 6;
	public static final int FONT_HEIGHT = 8;

	public static int TERM_PADDING_X = 3;
	public static int TERM_PADDING_Y = 4;
	
	protected static double U = 1 / 16d;
	protected static double W = 6 / 128d;

	protected int[] charWidth = new int[256];
	protected int[] colors = new int[]{
			0x1E1B1B,	0xB3312C,	0x3B511A,	0x51301A,
			0x253192,	0x7B2FBE,	0x287697,	0x999999,
			0x434343,	0xD88198,	0x41CD34,	0xDEDE6C,
			0x6689D3,	0xC354CD,	0xEB8844,	0xF0F0F0
	};
	
	protected int charTexture = -1;

	public TerminalRenderer(RenderEngine engine, Tessellator buff) {
		this.render = engine;
		this.buff = buff;

		BufferedImage image;

		try {
			image = ImageIO.read(getClass().getResourceAsStream(
					"/font/default.png"));
		} catch (IOException e) {
			throw new RuntimeException();
		}

		int w = image.getWidth();
		int h = image.getHeight();

		int[] rgbData = new int[w * h];
		image.getRGB(0, 0, w, h, rgbData, 0, w);

		for (int chrIndex = 0; chrIndex < 256; chrIndex++) {
			int x = chrIndex % 16;
			int y = chrIndex / 16;

			int wide = 7;

			while (wide >= 0) {
				int baseIndex = x * 8 + wide;

				boolean isEmpty = true;

				for (int lY = 0; lY < 8; lY++) {
					int index = (y * 8 + lY) * w;

					if ((rgbData[baseIndex + index] & 0xFF) > 0) {
						isEmpty = false;
						break;
					}
				}

				if (!isEmpty)
					break;

				wide--;
			}

			if (chrIndex == 32) // Space is 2 wide
				wide = 2;

			charWidth[chrIndex] = (wide + 2);
		}

		// Fix control chars
		for (int index = 0xB0; index < 0xDF; index++)
			charWidth[index] = FONT_WIDTH;

		// Setup the texture
		charTexture = engine.allocateAndSetupTexture(image);
	}

	protected void setColor( int code ){
		int color = colors[code % 16];
		
		buff.setColorOpaque(color >> 16 & 255, color >> 8 & 255, color & 255);
	}
	
	protected void renderCharAt(int xPos, int yPos, char chr) {
		if (chr == 32)
			return;

		double u0 = (chr % 16) * U;
		double v0 = (chr >> 4) * U;

		if (0xB0 < chr)
			u0 += 1 / 128d;

		buff.addVertexWithUV(xPos, 				yPos, 				0, u0, 		v0);
		buff.addVertexWithUV(xPos, 				yPos + FONT_HEIGHT, 0, u0, 		v0 + U);
		buff.addVertexWithUV(xPos + FONT_WIDTH, yPos + FONT_HEIGHT, 0, u0 + W, 	v0 + U);
		buff.addVertexWithUV(xPos + FONT_WIDTH, yPos, 				0, u0 + W, 	v0);
	}

	public void renderSingleChar( int x, int y, char chr ){
		if (chr == 32)
			return;
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, charTexture);

		buff.startDrawingQuads();
			renderCharAt(x, y, chr);
		buff.draw();
	}
	
	public void renderText(int x, int y, char[] line) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, charTexture);

		buff.startDrawingQuads();

		for (int index = 0; index < line.length; index++) {
			char chr = line[index];

			if ( chr > 255 )
				chr = '?';
			
			int wide = charWidth[chr];
			int off = (FONT_WIDTH - wide) / 2;

			renderCharAt(x + off, y, chr);

			x += FONT_WIDTH;
		}

		buff.draw();
	}

	public void renderTextColor( int x, int y, char[] line, byte[] lineColor, int hOff ){
		int anchorX = x -3;
		
		//Render background
		GL11.glDisable( GL11.GL_TEXTURE_2D );
		
		buff.startDrawingQuads();
		
		int cellW = FONT_WIDTH;
		int cellH = FONT_HEIGHT +1 +hOff;
		
		for (int index = 0; index < line.length; index++){
			int color = (lineColor[index] >> 4) & 15;
				color = colors[color];
		
			if ( index == 0 || index == line.length -1 )
				cellW = FONT_WIDTH +3;
			else
				cellW = FONT_WIDTH;
			
			buff.setColorOpaque_I(color);
			
			buff.addVertex(anchorX, 		y, 			0);
			buff.addVertex(anchorX, 		y +cellH, 	0);
			buff.addVertex(anchorX +cellW, 	y +cellH, 	0);
			buff.addVertex(anchorX +cellW, 	y, 			0);
			
			anchorX += cellW;
		}
	
		buff.draw();
	
	
		//Render characters
		GL11.glEnable( GL11.GL_TEXTURE_2D );
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, charTexture);
		
		buff.startDrawingQuads();

		anchorX = x;
		for (int index = 0; index < line.length; index++) {
			char chr = line[index];

			if ( chr > 255 )
				chr = '?';
			
			int wide = charWidth[chr];
			int off = (FONT_WIDTH - wide) / 2;

			buff.setColorOpaque_I( colors[ lineColor[index] & 15 ] );
			
			renderCharAt(anchorX + off, y, chr);

			anchorX += FONT_WIDTH;
		}

		buff.draw();
	}
	
	public void renderTerminal( Terminal term, int x, int y ){
		GL11.glColor3f(1, 1, 1);
		
		int anchorX = x;
		int anchorY = y;
		
		byte[][] codes	= term.charColors;
		char[][] chars	= term.chars;
	
		//Draw background
		if ( term.isColorSupported ){
			GL11.glDisable( GL11.GL_TEXTURE_2D );
			
			buff.startDrawingQuads();
			
			int cellH = FONT_HEIGHT +1;
			int len = codes.length -1;
			
			for ( int lineID = 0; lineID <= len; lineID++ ){
				
				if ( lineID == 0 || lineID == len ){
					cellH = FONT_HEIGHT +1 + TERM_PADDING_Y;
				} else {
					cellH = FONT_HEIGHT +1;
				}
				
				drawBackground(x, anchorY, codes[lineID], cellH);
				anchorY += cellH;
			}
			
			buff.draw();
		}
		
		//Render characters
		GL11.glEnable( GL11.GL_TEXTURE_2D );
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, charTexture);
		
		anchorX = x +TERM_PADDING_X;
		anchorY = y +TERM_PADDING_Y;
		
		buff.startDrawingQuads();
		
		for ( int lineID = 0; lineID < chars.length; lineID++ ){
			char[] line			= chars[lineID]; 
			byte[] lineColor 	= term.isColorSupported ? codes[lineID] : null;
			
			anchorX = x +TERM_PADDING_X;
			
			for ( int index = 0; index < line.length; index++ ){
				char chr = line[index];

				if ( chr > 255 )
					chr = '?';
						
				int wide = charWidth[chr];
				int off = (FONT_WIDTH - wide) / 2;

				if ( lineColor != null )
					buff.setColorOpaque_I( colors[ lineColor[index] & 15 ] );
						
				renderCharAt(anchorX + off, anchorY, chr);

				anchorX += FONT_WIDTH;
			}
			
			anchorY += FONT_HEIGHT +1;
		}
		
		buff.draw();
		
		//Draw the cursor
		GL11.glColor3f(1, 1, 1);
		
		if ( term.cursorBlink && getBlink() ){
			int cX = term.getCursorX();
			int cY = term.getCursorY();
			
			//Don't draw an out of bounds cursor
			if ( cX < 0 || term.w < cX || cY < 0 || term.h < cY )
				return;
				
			anchorX = x +TERM_PADDING_X + cX *TerminalRenderer.FONT_WIDTH;
			anchorY = y +TERM_PADDING_Y + cY *9;
			
			renderSingleChar(anchorX, anchorY, '_');				
		}
	}
	
	protected void drawBackground( int anchorX, int anchorY, byte[] lineColor, int cellH ){
		int cellW 	= FONT_WIDTH +1;
		int len		= lineColor.length -1;
		
		for ( int index = 0; index <= len; index++ ){
			int color = (lineColor[index] >> 4) & 15;
				color = colors[color];
	
			if ( index == 0 || index == len ){
				cellW = FONT_WIDTH +TERM_PADDING_X;
			} else {
				cellW = FONT_WIDTH;
			}
			
			buff.setColorOpaque_I(color);
			
			buff.addVertex(anchorX, 		anchorY, 		0);
			buff.addVertex(anchorX, 		anchorY +cellH, 0);
			buff.addVertex(anchorX +cellW, 	anchorY +cellH, 0);
			buff.addVertex(anchorX +cellW, 	anchorY, 		0);
			
			anchorX += cellW;
		}
	}
	
}
