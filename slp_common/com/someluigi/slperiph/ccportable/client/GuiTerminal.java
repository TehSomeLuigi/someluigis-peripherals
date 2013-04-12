package com.someluigi.slperiph.ccportable.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.someluigi.slperiph.SLPMod;
import com.someluigi.slperiph.ccportable.gfx.TerminalRenderer;
import com.someluigi.slperiph.ccportable.shared.ContainerTerminal;
import com.someluigi.slperiph.ccportable.shared.ContainerTerminal.TerminalTab;
import com.someluigi.slperiph.ccportable.shared.Terminal;

public class GuiTerminal extends GuiContainer{
	protected static final ItemStack CRYSTAL_TAB_ICON = new ItemStack( SLPMod.itemQuartz );
	protected static TerminalRenderer renderer;
	
	static{
		renderer = new TerminalRenderer( Minecraft.getMinecraft().renderEngine, Tessellator.instance );
	}
	
	protected ContainerTerminal terminal;
	protected TerminalTab activeTab;
	
	protected boolean allowInventory = false;
	
	public GuiTerminal(ContainerTerminal container) {
		super( container );
		
		terminal = container;
		
		xSize = 356;
		ySize = 214;
	}

	/*
	 * Events
	 */
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		super.initGui();
	}
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}
	
	/*
	 * Helpers
	 */
	protected int getTabAt( int x, int y ){
		int lX = x - guiLeft -8;
		int lY = y - guiTop -26;
		
		if ( 0 <= lX && lX <= 24 && 0 < lY ){ //Click at the tab bar
			int id = lY / 25;
			
			if ( 0 <= id && id < 6 )
				return id;
		}
		
		return -1;
	}
	
	protected void setActiveTab( int index ){
		if ( terminal.isValidTarget(index) ){
			TerminalTab target = terminal.tabs[index];
			
			if ( !target.isLinked )
				return;
			
			terminal.setActiveTab(index);
			activeTab	= target;
		}
	}
	
	protected TerminalTab getActiveTab(){
		if ( terminal.activeTabID == -1 )
			return null;
		
		return terminal.tabs[terminal.activeTabID];
	}
	
	/*
	 * Rendering
	 */
	public void drawScreen(int mX, int mY, float delta) {
		super.drawDefaultBackground();
		
		RenderEngine engine = mc.renderEngine;
		Tessellator buff = Tessellator.instance;

		//Bind the texture
		engine.bindTexture("/slp-gui/term.png");
		
		//Background
		buff.setTranslation(guiLeft, guiTop, 0);
		
		buff.startDrawingQuads();
			buff.addVertexWithUV(0,   0,   0, 0,    0);
			buff.addVertexWithUV(0,   256, 0, 0,    1);
			buff.addVertexWithUV(386, 256, 0, 0.75, 1);
			buff.addVertexWithUV(386, 0,   0, 0.75, 0);
		buff.draw();
		
		//Draw tab leafs
		buff.setTranslation(guiLeft +8, guiTop +26, 0);
		
		int anchorX = 0;
		int anchorY = 0;

		float uOff = 0; //0.125f;
		
		for ( int index = 0; index <= 5; index++ ){	
			TerminalTab tab = terminal.tabs[index];
		
			if ( index == terminal.activeTabID ){
				tab.isHilighted = false;
				
				uOff = 0.125f;
			} else {
				uOff = 0;
			}
			
			buff.startDrawingQuads();
				buff.setColorOpaque_I( tab.getColor() );
			
				buff.addVertexWithUV(0,  anchorY,     0, 0.75,   uOff);
				buff.addVertexWithUV(0,  anchorY +32, 0, 0.75,   uOff + 0.125);
				buff.addVertexWithUV(32, anchorY +32, 0, 0.8125, uOff + 0.125);
				buff.addVertexWithUV(32, anchorY,     0, 0.8125, uOff);
			buff.draw();
			
			anchorY += 25;
		}
		
		buff.setTranslation(0, 0, 0);
	
		//Render tab content, if any
		TerminalTab tab = getActiveTab();
		
		if ( tab != null && tab.terminal != null ){
			anchorX = guiLeft +35;
			anchorY = guiTop  +23;
			
			//Render terminal
			renderer.renderTerminal(tab.terminal, anchorX, anchorY);
		}
		
		//Render static info
		anchorX = guiLeft +8;
		anchorY = guiTop +6;
		
		fontRenderer.drawString("Wireless Terminal",	anchorX, anchorY, 4210752);
		
		//TODO: Battery support
		//anchorX += 270;
		//fontRenderer.drawString("Batt: [||||.]  89%",	anchorX, anchorY, 4210752);
		
		
		//Render tab icons
		RenderHelper.enableGUIStandardItemLighting();
		
		anchorX = guiLeft +12;
		anchorY = guiTop +30;
		
		for ( int index = 0; index <= 5; index++ ){
			itemRenderer.renderItemIntoGUI(fontRenderer, engine, terminal.tabs[index].getIcon(), anchorX, anchorY);
			
			anchorY += 25;
		}
		
		//Render standard inventory
		if ( allowInventory )
			drawInventory(delta, mX, mY);
		
		//Render inventory icon
		anchorY = guiTop +184;
		
		RenderHelper.disableStandardItemLighting();
		itemRenderer.renderItemIntoGUI(fontRenderer, engine, CRYSTAL_TAB_ICON, anchorX, anchorY);
	
		
		//Render tab overlay info
		int overTab = getTabAt(mX, mY);
		
		if ( overTab != -1 )
			drawCreativeTabHoveringText( terminal.tabs[overTab].getTooltip(), mX, mY);
	}
	
	protected void drawInventory( float delta, int mX, int mY ){
		RenderEngine engine = mc.renderEngine;
		Tessellator buff = Tessellator.instance;
		
		//Bind the texture
		engine.bindTexture("/slp-gui/term.png");
		
		//Standard item model
		RenderHelper.disableStandardItemLighting();
		
		//Draw the leaflet
		buff.setTranslation(guiLeft +8, guiTop +114, 0);
		
		buff.startDrawingQuads();
			buff.addVertexWithUV(0, 	0, 		0, 1, 			0);
			buff.addVertexWithUV(0, 	90, 	0, 422f/512f,	0);
			buff.addVertexWithUV(256, 	90, 	0, 422f/512f,	1);	
			buff.addVertexWithUV(256, 	0, 		0, 1,			1);
		buff.draw();
			
		buff.setTranslation(0, 0, 0);
	
		//Draw slots
		super.drawScreen(mX, mY, delta);
	}
	
	
	//Overrides - Empty, so the inventory renderer won't mess with it
	protected void drawGuiContainerBackgroundLayer(float delta, int mX, int mY) {
	}

	public void drawDefaultBackground() {
	}
	
	/*
	 * Input
	 */
	protected void mouseClicked(int x, int y, int button) {
		
		//Inventory toggle
		if ( isPointInRegion( 8, 180, 24, 24, x, y ) ){
			allowInventory = !allowInventory;
			return;
		}

		//Tab change
		int tabIndex = getTabAt(x, y);
		
		if ( tabIndex != -1 ){
			setActiveTab(tabIndex);
			return;
		}
		
		//Terminal input
		if ( handleMouseClick(x, y, button) )
			return;
		
		super.mouseClicked(x, y, button);
	}

		
	//Override slot clicks
	protected void handleMouseClick(Slot slot, int index, int p0, int p1) {
		
		if ( allowInventory )
			super.handleMouseClick(slot, index, p0, p1);

	}


	/*
	 * TODO: Clean up this mess!
	 * 
	 * Computer events
	 */
	
	protected int mButtonHeld  = -1;
	protected int mButtonPressX = -1;
	protected int mButtonPressY = -1;
	
	protected boolean handleMouseClick( int x, int y, int button ){
		
		//Ignore out of bounds events
		if ( !isPointInRegion(36, 24, 310, 180, x, y) )
			return false;
		
		int activeTabID = terminal.activeTabID;
		
		if ( !allowInventory && activeTabID != -1 ){
			Terminal term = terminal.tabs[activeTabID].terminal;
			
			if ( term != null ){
				int charX = ( x - guiLeft - 36 ) / TerminalRenderer.FONT_WIDTH;
				int charY = ( y - guiTop  - 26 ) / 9;
				
				charX = Math.min( Math.max(charX, 0), term.w -1);
				charY = Math.min( Math.max(charY, 0), term.h -1);
				
				mButtonHeld = button;
				mButtonPressX = charX;
				mButtonPressY = charY;

				terminal.sendMouseEvent(activeTabID, button, charX, charY);
			}
			
			return true;
		}

		return false;
	}
	
	public void handleMouseInput() {
		super.handleMouseInput();
		
		//Don't process events on inactive tab
		int activeTabID = terminal.activeTabID;
		
		if ( allowInventory || activeTabID == -1 )
			return;
		
		//Handle mouse events
		if ( mButtonHeld != -1 && !Mouse.isButtonDown(mButtonHeld) )
			mButtonHeld = -1;
		
		int deltaWheel = Mouse.getDWheel();
		
		//No events, skip the rest
		if ( mButtonHeld == -1 && deltaWheel == 0 )
			return;
	
		//Calculate terminal coordinates
		int scrX = Mouse.getEventX()			* width  / mc.displayWidth;
		int scrY = height - Mouse.getEventY() 	* height / mc.displayHeight;
		
		//Ignore out of bounds events
		if ( !isPointInRegion(36, 24, 310, 180, scrX, scrY) )
			return;
				
		//Char events
		Terminal term = terminal.tabs[activeTabID].terminal;
		
		int charX = ( scrX - guiLeft - 36 ) / TerminalRenderer.FONT_WIDTH;
		int charY = ( scrY - guiTop  - 26 ) / 9;
		
		charX = Math.min( Math.max(charX, 0), term.w -1);
		charY = Math.min( Math.max(charY, 0), term.h -1);
		
		//Send events to the computers
		if ( deltaWheel > 0 ){
			terminal.sendMouseEvent(activeTabID, 3, charX, charY);
		} else if ( deltaWheel < 0 ) {
			terminal.sendMouseEvent(activeTabID, 4, charX, charY);
		}
		
		if ( mButtonHeld != -1 && ( mButtonPressX != charX || mButtonPressY != charY ) ){
			terminal.sendMouseEvent(activeTabID, mButtonHeld +5, charX, charY);
			
			mButtonPressX = charX;
			mButtonPressY = charY;
		}
	}

	
	protected void keyTyped(char chr, int index) {
		
		if ( index == Keyboard.KEY_ESCAPE ){
			super.keyTyped(chr, index);
			return;
		}
		
		terminal.sendKeyEvent(terminal.activeTabID, chr, index);
	}

}
