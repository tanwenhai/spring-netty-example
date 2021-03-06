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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;

/**
 * @author tanwenhai@bilibili.com
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CounterHandler counterHandler = new CounterHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.TCP_NODELAY, false)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(TextMessage.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(counterHandler);
                        }
                    });

            ChannelFuture connect = null;
            for (int i = 0; i < 20000; i++) {
                connect = b.connect("10.23.157.199", 9999);
                connect.addListener(CLOSE_ON_FAILURE);
            }
            connect.channel().closeFuture().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }
        System.out.println(counterHandler.getqps());
    }
}

@ChannelHandler.Sharable
class CounterHandler extends SimpleChannelInboundHandler<TextMessage> {
    private volatile long count;
    private AtomicLongFieldUpdater countUpdater = AtomicLongFieldUpdater.newUpdater(CounterHandler.class, "count");
    TextMessage textMessage = TextMessage.newBuilder().setText("11111111111").build();
    private Frame frame = Frame.newBuilder().setPath("/hello/say").setPayload(textMessage.toByteString()).build();

    long end;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextMessage msg) throws Exception {
        if (System.currentTimeMillis() < end) {
            countUpdater.incrementAndGet(this);
            ctx.writeAndFlush(frame);
        } else {
            ctx.close();
            System.out.println(getqps());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(frame);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        end = System.currentTimeMillis() + (60 * 1000);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public long getqps() {
        return count / 60;
    }
}
