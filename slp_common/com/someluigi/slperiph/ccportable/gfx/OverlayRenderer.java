package com.someluigi.slperiph.ccportable.gfx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class OverlayRenderer {
	protected static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

	public static void renderOverlay(int x, int y, String info) {
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		int wide = fontRenderer.getStringWidth(info);
		int tall = 8;
				
		int xPos = x +12;
		int yPos = y -12;
		
		int color = -267386864;
		drawGradientRect(xPos - 3, yPos - 4, xPos + wide + 3, yPos - 3, color, color);
		drawGradientRect(xPos - 3, yPos + tall + 3, xPos + wide + 3, yPos + tall + 4, color, color);
		drawGradientRect(xPos - 3, yPos - 3, xPos + wide + 3, yPos + tall + 3, color, color);
		drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + tall + 3, color, color);
		drawGradientRect(xPos + wide + 3, yPos - 3, xPos + wide + 4, yPos	+ tall + 3, color, color);
		
		int var11 = 1347420415;
		int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
		
		drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + tall + 3 - 1, var11, var12);
		drawGradientRect(xPos + wide + 2, yPos - 3 + 1, xPos + wide + 3, yPos + tall + 3 - 1, var11, var12);
		drawGradientRect(xPos - 3, yPos - 3, xPos + wide + 3, yPos - 3 + 1, var11, var11);
		drawGradientRect(xPos - 3, yPos + tall + 2, xPos + wide + 3, yPos + tall + 3, var12, var12);
		
		fontRenderer.drawStringWithShadow(info, xPos, yPos, -1);
	}
	
	public static void renderInfoBox( int x, int y, int w, int h ){
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		int color = -267386864;
		
		drawGradientRect(x - 3, y - 4, x + w + 3, y - 3, color, color);
		drawGradientRect(x - 3, y + h + 3, x + w + 3, y + h + 4, color, color);
		drawGradientRect(x - 3, y - 3, x + w + 3, y + h + 3, color, color);
		drawGradientRect(x - 4, y - 3, x - 3, y + h + 3, color, color);
		drawGradientRect(x + w + 3, y - 3, x + w + 4, y	+ h + 3, color, color);
		
		int var11 = 1347420415;
		int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
		
		drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + h + 3 - 1, var11, var12);
		drawGradientRect(x + w + 2, y - 3 + 1, x + w + 3, y + h + 3 - 1, var11, var12);
		drawGradientRect(x - 3, y - 3, x + w + 3, y - 3 + 1, var11, var11);
		drawGradientRect(x - 3, y + h + 2, x + w + 3, y + h + 3, var12, var12);
	}
		
	protected static void drawGradientRect(int sX, int sY, int eX, int eY, int start, int end) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
        
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		Tessellator buff = Tessellator.instance;
		buff.startDrawingQuads();
		
		bindColor(start);
		
		buff.addVertex( eX, sY, 0 );
		buff.addVertex( sX, sY, 0 );
		
		bindColor(end);
		
		buff.addVertex( sX, eY, 0 );
		buff.addVertex( eX, eY, 0 );
		
		buff.draw();
		
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	protected static void bindColor( int color ){
		Tessellator.instance.setColorRGBA(
			color >> 16 & 255,
			color >>  8 & 255,
			color       & 255,
			color >> 24 & 255
		);	
	}
	
}
