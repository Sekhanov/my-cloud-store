package ru.skhanov.mycloudstoreserver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.skhanov.mycloudstorecommon.FileMessage;
import ru.skhanov.mycloudstorecommon.FileRequest;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof FileRequest) {
                FileRequest fileRequest = (FileRequest) msg;
                if (Files.exists(Paths.get("server_storage/" + fileRequest.getFilename()))) {
                    FileMessage fileMessage = new FileMessage(Paths.get("server_storage/" + fileRequest.getFilename()));
                    ctx.writeAndFlush(fileMessage);
                }
            }
            
            if(msg instanceof FileMessage) {
            	FileMessage fileMessage = (FileMessage) msg;
            	Files.write(Paths.get("server_storage/" + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE);
            	//TODO
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
