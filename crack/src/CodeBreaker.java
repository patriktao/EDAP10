import java.awt.BorderLayout;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import client.view.WorklistItem;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

	private final JPanel workList;
	private final JPanel progressList;
	private final JProgressBar mainProgressBar;
	private final ExecutorService threadPool;

	// -----------------------------------------------------------------------

	private CodeBreaker() {
		StatusWindow w = new StatusWindow();
		workList = w.getWorkList();
		threadPool = Executors.newFixedThreadPool(2);
		progressList = w.getProgressList();
		mainProgressBar = w.getProgressBar();
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) {

		/*
		 * Most Swing operations (such as creating view elements) must be performed in
		 * the Swing EDT (Event Dispatch Thread).
		 * 
		 * That's what SwingUtilities.invokeLater is for.
		 */

		SwingUtilities.invokeLater(() -> {
			CodeBreaker codeBreaker = new CodeBreaker();
			new Sniffer(codeBreaker).start();
		});
	}

	// -----------------------------------------------------------------------

	/** Called by a Sniffer thread when an encrypted message is obtained. */
	@Override
	public void onMessageIntercepted(String message, BigInteger n) {
		System.out.println("message intercepted (N=" + n + ")...");
		SwingUtilities.invokeLater(() -> {
			WorklistItem workItem = new WorklistItem(n, message);
			workList.add(workItem);
			JButton button = new JButton("Break");
			button.addActionListener(e -> {
				System.out.println("Remove Worklist Item!");
				buttonOnBreak(message, n);
				workList.remove(workItem);
				workList.remove(button);
			});
			workItem.add(button);
		});
	}

	public void buttonOnBreak(String message, BigInteger n) {
		// ProgressItem
		ProgressItem progressItem = new ProgressItem(n, message);
		progressList.add(progressItem);
		// Cancel Button
		JButton cancelButton = new JButton("Cancel");
		progressItem.add(cancelButton);
		// Remove Button
		JButton removeButton = new JButton("Remove");
		// Progress Tracker
		ProgressTracker progressTracker = new Tracker(progressItem, mainProgressBar);

		// Increase mainProgressBar Max by 1,000,000
		mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);

		Future<?> crackedString = threadPool.submit(() -> {
			try {
				String result = Factorizer.crack(message, n, progressTracker);
				SwingUtilities.invokeLater(() -> {
					progressItem.getTextArea().setText(result);
					progressItem.remove(cancelButton);
					progressItem.add(removeButton);
				});
			} catch (Throwable t) {
				t.printStackTrace();
			}
			;
		});

		cancelButton.addActionListener(e -> {
			progressItem.getTextArea().setText("[cancelled]");
			crackedString.cancel(true);
			progressItem.remove(cancelButton);
			progressItem.add(removeButton);
			mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
			mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
		});

		removeButton.addActionListener(e -> {
			progressList.remove(progressItem);
			progressList.remove(removeButton);
			mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
			mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
		});
	}

	/** ProgressTracker: reports how far factorization has progressed */
	private static class Tracker implements ProgressTracker {
		private int totalProgress = 0;
		private ProgressItem progressItem;
		private JProgressBar mainProgressBar;

		public Tracker(ProgressItem progressItem, JProgressBar mainProgressBar) {
			this.progressItem = progressItem;
			this.mainProgressBar = mainProgressBar;
		}

		/**
		 * Called by Factorizer to indicate progress. The total sum of ppmDelta from all
		 * calls will add upp to 1000000 (one million).
		 * 
		 * @param ppmDelta portion of work done since last call, measured in ppm (parts
		 *                 per million)
		 */
		@Override
		public void onProgress(int ppmDelta) {
			totalProgress += ppmDelta;
			progressItem.getProgressBar().setValue(totalProgress);
			mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta);
		}
	}
}
