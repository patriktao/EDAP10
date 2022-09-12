package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import msg.client.Twit;
import msg.client.test.MessagingLog;
import msg.client.test.ServerControl;

public class ServerTest {

    @BeforeEach
    void setUp(TestInfo info) throws Exception {
        ServerControl.restartServer(info.getDisplayName());
    }

    @Test
    void testOneTwit() throws InterruptedException {
        final int NBR_MESSAGES  = 5;     // number of messages from each client
        final int MESSAGE_DELAY = 100;   // maximal delay between messages 

        new Twit(NBR_MESSAGES, MESSAGE_DELAY).start();

        MessagingLog.expect(1, NBR_MESSAGES);
    }

    @Test
    void testTwoTwits() throws InterruptedException {
        final int NBR_TWITS     = 2;     // number of clients
        final int NBR_MESSAGES  = 5;     // number of messages from each client
        final int MESSAGE_DELAY = 100;   // maximal delay between messages 

        for (int i = 0; i < NBR_TWITS; i++) {
            new Twit(NBR_MESSAGES, MESSAGE_DELAY).start();
        }

        MessagingLog.expect(NBR_TWITS, NBR_TWITS * NBR_MESSAGES);
    }
}
