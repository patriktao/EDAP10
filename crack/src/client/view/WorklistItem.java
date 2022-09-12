package client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigInteger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;

/**
 * A panel that constructs a single item in the work list. It displays
 * information about a single sniffed message, including its N (key) and message
 * text (currently encrypted).
 */
@SuppressWarnings("serial")
public class WorklistItem extends ConvenientPanel {

    public static final int HEIGHT = 104;
    
    private static final Color TEXT_COLOR = new Color(128, 255, 128);
    
    private static final Font MESSAGE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    /** Create the panel, displaying the integer _n_ and the encrypted message _code_. */
    public WorklistItem(BigInteger n, String code) {
        
        // set a border with some space around components
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("N=" + n + " (" + n.bitLength() + " bits)"),
            BorderFactory.createEmptyBorder(0, 4, 8, 4)));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JTextArea textArea = new JTextArea(code);
        textArea.setPreferredSize(new Dimension());  // make sure it shrinks as needed
        textArea.setFont(MESSAGE_FONT);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(TEXT_COLOR);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        add(textArea);
        add(Box.createRigidArea(new Dimension(8, HEIGHT)));  // add space after the text area

        // make sure this component shrinks and grows as needed
        setPreferredSize(new Dimension(0, HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
    }
}
