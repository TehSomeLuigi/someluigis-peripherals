package com.someluigi.slperiph;

import dan200.computer.api.IComputerAccess;

public class ComputerManipulation {
    
    public static void mountDemoDir(IComputerAccess ica) {
        ica.mountFixedDir("slperiph", "slperiph-demo", true, 0);
    }
    
    
    
    
}
