package rsa;

/**
 * Callback interface:
 * 
 * reports back to the application when progress has been made
 * in an ongoing decryption task.
 * 
 * NOTE: you are not expected to modify this code (but you are welcome to
 * if you wish). This file is only provided for your reference.
 */
public interface ProgressTracker {

    /**
     * Reports the progress made since the last report, in ppm (parts per
     * million). That is, if delta is 1 then one millionth of the total progress
     * has been made, or 0.0001%.
     * 
     * @param ppmDelta     Progress made since last report, in ppm.
     */
    public void onProgress(int ppmDelta);
}
