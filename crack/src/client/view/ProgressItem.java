package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigInteger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 * A panel that constructs a single item in the progress list. It displays
 * information about a single message to break, including its N (key), message
 * text (encrypted initially, readable when done) and its current progress.
 */
@SuppressWarnings("serial")
public class ProgressItem extends ConvenientPanel {

    private final JProgressBar progressBar;
    private final JTextArea textArea;
    
    private static final Color TEXT_COLOR = new Color(128, 255, 128);

    private static final Font MESSAGE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    private static final Dimension MESSAGE_SIZE = new Dimension(100, 72);
    private static final Dimension PROGRESS_BAR_SIZE = new Dimension(100, 20);

    /** Create the panel, displaying the integer _n_ and the encrypted message _code_. */
    public ProgressItem(BigInteger n, String code) {
        
        // set a border with some space around components
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("N=" + n + " (" + n.bitLength() + " bits)"),
            BorderFactory.createEmptyBorder(0, 4, 0, 4)));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel main = new JPanel(new BorderLayout());
        add(main);
        add(Box.createRigidArea(new Dimension(8, HEIGHT)));  // add space after the text area

        textArea = new JTextArea(code);
        textArea.setFont(MESSAGE_FONT);
        textArea.setPreferredSize(MESSAGE_SIZE);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(TEXT_COLOR);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        main.add(textArea, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(PROGRESS_BAR_SIZE);
        progressBar.setStringPainted(true);
        progressBar.setMaximum(1_000_000);
        progressBar.setValue(0);
        main.add(progressBar, BorderLayout.SOUTH);
    }

    /** Access this item's progress bar. */
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /** Access this item's text area. */
    public JTextArea getTextArea() {
        return textArea;
    }

    @Override
    /** Ensure this item doesn't expand vertically, only horizontally. */
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
