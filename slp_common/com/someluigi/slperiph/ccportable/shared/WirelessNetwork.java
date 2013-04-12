package com.someluigi.slperiph.ccportable.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.util.Vec3;

public class WirelessNetwork {	
	protected Map<Integer, Set<PeripheralTerminal>> frequencies = new HashMap<Integer, Set<PeripheralTerminal>>();
	
	protected static Set<PeripheralTerminal> newDeviceSet(){
		return Collections.newSetFromMap( new WeakHashMap<PeripheralTerminal, Boolean>() );
	}
	
	public synchronized void addDevice( PeripheralTerminal trans ){
		Integer key = Integer.valueOf( trans.getFreq() );
		
		Set<PeripheralTerminal> devices = frequencies.get(key);
		
		if ( devices == null ){
			devices = newDeviceSet();
			frequencies.put(key, devices);
		}
		
		devices.add(trans);
	}
	
	public synchronized void removeDevice( PeripheralTerminal trans ){
		Integer key = Integer.valueOf( trans.getFreq() );
		
		Set<PeripheralTerminal> devices = frequencies.get(key);
		
		if ( devices != null ){
			devices.remove(trans);
			
			if ( devices.isEmpty() )
				frequencies.remove(devices);
		}
	}
	
	public synchronized Set<PeripheralTerminal> queryDevices( Vec3 pos, int freq ){
		Integer key = Integer.valueOf( freq );
		
		Set<PeripheralTerminal> devices = frequencies.get(key);
		
		if ( devices == null )
			return null;
		
		Set<PeripheralTerminal> result = new HashSet<PeripheralTerminal>();
		
		for ( PeripheralTerminal tile : devices ){	
			if ( tile.canConnectTo(pos) )
				result.add(tile);
		}
		
		return result;
	}
	
	public synchronized PeripheralTerminal querySingleDevice( Vec3 pos, int freq ){
		Set<PeripheralTerminal> result = queryDevices(pos, freq);
		
		if ( result != null && result.size() == 1 )
			return result.iterator().next();
	
		return null;
	}
	
}
