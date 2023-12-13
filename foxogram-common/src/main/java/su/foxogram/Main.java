package su.foxogram;

import com.google.protobuf.ByteString;
import su.foxogram.enums.AMQP;
import su.foxogram.protos.AmqpMessage;

public class Main {
    public static void main(String[] args) {
        AmqpMessage message = AmqpMessage.newBuilder()
                .setType(AMQP.Events.ATTACHMENT_UPLOAD.getValue())
                .setBytes(ByteString.copyFrom(new byte[]{0x01, 0x02}))
                .build();

        System.out.println(message);
    }
}