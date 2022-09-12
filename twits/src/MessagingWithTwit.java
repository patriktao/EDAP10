import javax.swing.SwingUtilities;

import msg.client.Twit;
import msg.client.swing.MessagingClient;

public class MessagingWithTwit {
    public static void main(String[] args) throws Exception {
        // create an imaginary friend to talk to, generating 100 messages
        // with (up to) 30 seconds between them
        new Twit(100, 30000).start();

        // then open the chat window
        SwingUtilities.invokeAndWait(() -> new MessagingClient().login());
    }
}
