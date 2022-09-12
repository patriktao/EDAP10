import javax.swing.SwingUtilities;

import msg.client.swing.MessagingClient;

public class Messaging {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> new MessagingClient().login());
    }
}
