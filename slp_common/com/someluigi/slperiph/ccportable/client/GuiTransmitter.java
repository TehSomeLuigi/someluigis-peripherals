package com.someluigi.slperiph.ccportable.client;

import java.util.LinkedList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;

import org.lwjgl.opengl.GL11;

import com.someluigi.slperiph.ccportable.shared.ContainerTransmitter;

public class GuiTransmitter extends GuiContainer {
    
    public LinkedList<GuiButton> controlList = new LinkedList<GuiButton>();
    
	public GuiTransmitter(ContainerTransmitter container) {
		super(container);
		
		xSize = 176;
		ySize = 150;
	}

	public void initGui() {
		super.initGui();

		addButton(0, 32, 16, 134, "Radius: 256").enabled = false;
		addButton(1, 32, 36, 134, "MultiUser: Enabled").enabled = false;
	}
	
	protected GuiButton addButton( int index, int x, int y, int w, String label ){
		GuiButton button = new GuiButton(index, guiLeft + x, guiTop + y, w, 20, label);
		
		controlList.add( button );
		return button;
	}
	
	protected void drawGuiContainerForegroundLayer(int mX, int mY) {
		fontRenderer.drawString("Wireless Transmitter", 8, 6, 4210752);	
	}
	
	protected void drawGuiContainerBackgroundLayer(float delta, int mX, int mY) {
		
		for ( Object button : controlList )
			((GuiButton) button).drawButton(mc, mX, mY);
		
		GL11.glColor3f(1, 1, 1);
		
					mc.renderEngine.bindTexture("/slp-gui/trans.png");

		int anchorX = ( this.width  - this.xSize ) /2;
		int anchorY = ( this.height - this.ySize ) /2;
		
		drawTexturedModalRect(anchorX, anchorY, 0, 0, xSize, ySize);
	}

}
