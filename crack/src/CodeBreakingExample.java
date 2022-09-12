import java.math.BigInteger;

import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreakingExample {

    public static void main(String[] args) throws InterruptedException {

        // 'N' from a public key, and an encrypted message.
        // NOTE: updated 220729 (Patrik)
        BigInteger n = new BigInteger("113546829441971119");
        String ciphertext = "2o1k5jnv4x102gqilsqmp194dwj6k4nu31ycsrg6oo9407oi83447b3kpsuckqeey2nrenev0dv8qoxbpyonlbyojk8opwrgb1h0lt7zr39ygsb35aky0hlqz624yninojlkhij2vapt85qafyxmyeo8hxx0g2el2bd57qufnmwkkv0t";

        ProgressTracker tracker = new Tracker();
        String plaintext = Factorizer.crack(ciphertext, n, tracker);

        System.out.println("\nDecryption complete. The message is:\n\n  " + plaintext);
    }

    // -----------------------------------------------------------------------

    /** ProgressTracker: reports how far factorization has progressed */ 
    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
            totalProgress += ppmDelta;
            System.out.println("progress = " + totalProgress + "/1000000");
        }
    }
}
