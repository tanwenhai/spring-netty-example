import com.example.proto.Frame;
import com.example.proto.TextMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tanwenhai@bilibili.com
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        CounterHandler counterHandler = new CounterHandler();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(TextMessage.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new NioEventLoopGroup(200), counterHandler);
                    }
                });
        ChannelFuture connect = b.connect("127.0.0.1", 9999);

        connect.channel().closeFuture().sync();
    }
}

@ChannelHandler.Sharable
class CounterHandler extends SimpleChannelInboundHandler<TextMessage> {
    private static AtomicLong count = new AtomicLong(0);
    long end;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextMessage msg) throws Exception {
        if (System.currentTimeMillis() < end) {
            count.incrementAndGet();
            TextMessage textMessage = TextMessage.newBuilder().setText("1111").build();
            Frame frame = Frame.newBuilder().setPath("/hello/say").setPayload(textMessage.toByteString()).build();
            ctx.writeAndFlush(frame);
        } else {
            System.out.println(count.get() / 60);
            ctx.close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        end = System.currentTimeMillis() + 60 * 1000;
        TextMessage textMessage = TextMessage.newBuilder().setText("1111").build();
        Frame frame = Frame.newBuilder().setPath("/hello/say").setPayload(textMessage.toByteString()).build();
        ctx.writeAndFlush(frame);
    }
}
