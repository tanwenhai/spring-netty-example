package com.example.services;

import com.example.codec.DispatchMessage;
import com.example.codec.MessageCodec;
import com.example.proto.Frame;
import com.example.proto.TextMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
//@TestPropertySource(locations = {"classpath:application-druid.yaml"})
//@ActiveProfiles(value = ["druid"])
@ActiveProfiles("test")
public class TestServiceTest {

    @Autowired
    MessageCodec messageCodec;

    @Autowired
    DispatchMessage dispatchMessage;

    @Test
    public void consume() throws InvalidProtocolBufferException {
        EmbeddedChannel channel = new EmbeddedChannel(
                new ProtobufVarint32FrameDecoder(),
                new ProtobufDecoder(Frame.getDefaultInstance()),
                new ProtobufVarint32LengthFieldPrepender(),
                messageCodec,
                dispatchMessage
        );
        long end = System.currentTimeMillis() + 60 * 1000;

        long count = 0;
        while (System.currentTimeMillis() < end) {
            TextMessage build = TextMessage.newBuilder().setText("1111").build();
            channel.writeInbound(build);
            Frame o = channel.readOutbound();
            TextMessage rtv = TextMessage.parseFrom(o.getPayload());
            Assert.assertEquals(build.getText() + " xixi", rtv.getText());
            count ++;
        }
        System.out.println(count / 60);
    }
}