package us.drullk.shizzel.networking.appEng;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class HandlerChiselingTerminalServer implements IMessageHandler<PacketChiselingTerminalServer, IMessage>
{
    @Override
    public IMessage onMessage(final PacketChiselingTerminalServer message, final MessageContext ctx)
    {
        message.execute();
        return null;
    }
}
