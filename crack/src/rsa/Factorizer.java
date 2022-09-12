package rsa;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Factorizer {

    private static final BigInteger ONE_MILLION = BigInteger.valueOf(1_000_000);
    
    /**
     * Breaks the RSA-encrypted message in 'ciphertext', by factorizing the integer n.
     * Progress is reported every millisecond using the ProgressTracker callback interface.
     * 
     * NOTE: you are not expected to modify this code (but you are welcome to if you wish).
     * This file is only provided for your reference.
     * 
     * @return  the decrypted (plaintext) message
     */
    public static String crack(String ciphertext, BigInteger n, ProgressTracker tracker) throws InterruptedException {
        List<BigInteger> primeFactors = new ArrayList<>();

        BigInteger sqrtN = n.sqrt();

        // Further down below, we will check all odd integers < sqrt(n).
        // Although we don't expect 2 to be a factor in n, check to be sure.

        while (!n.testBit(0)) {    // quick way of checking whether (n % 2) != 0
            primeFactors.add(BigInteger.TWO);
            n = n.shiftRight(1);   // quick way of dividing by 2
        }

        int lastReportPpm = 0;
        long lastReportTime = -1;

        // Check all odd values of k, where 3 <= k <= sqrt(n)
        
        BigInteger k = BigInteger.valueOf(3);
        while (k.compareTo(sqrtN) <= 0) {

            // Report progress
            long now = System.currentTimeMillis();
            if (now != lastReportTime) {
                int ppm = k.multiply(ONE_MILLION).divide(sqrtN).intValue();
                tracker.onProgress(ppm - lastReportPpm);
                lastReportPpm = ppm;
                lastReportTime = now;
            }

            // Check if k is a factor in n
            if (n.mod(k).signum() == 0) {
                primeFactors.add(k);
                n = n.divide(k);
            } else {
                k = k.add(BigInteger.TWO);
            }

            // Check interruption status
            if (Thread.interrupted()) {
                throw new InterruptedException("crack() was cancelled (interrupted)");
            }
        }

        primeFactors.add(n);

        tracker.onProgress(1_000_000 - lastReportPpm);

        if (primeFactors.size() < 2) {
            throw new Error("no integer factors found: " + n + " is prime!");
        }
        
        BigInteger p = primeFactors.get(0);
        BigInteger q = primeFactors.get(1);

        return new Crypto(p, q).decrypt(ciphertext);
    }
}
