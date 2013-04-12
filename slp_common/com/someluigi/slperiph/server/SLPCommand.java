package com.someluigi.slperiph.server;

import java.util.List;

import com.someluigi.slperiph.SLPMod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class SLPCommand extends CommandBase {

    @Override
    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCommandName() {
        return "slp";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender cs, String[] astring) {
        cs.sendChatToPlayer(EnumChatFormatting.GREEN + "SomeLuigi's Peripherals: Info");
        
        cs.sendChatToPlayer(EnumChatFormatting.YELLOW + "   HTTP Server running: " + SLPMod.httpdStat);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(ICommandSender icommandsender,
            String[] astring) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] astring, int i) {
        return false;
    }

}
