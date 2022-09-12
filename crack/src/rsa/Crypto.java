package rsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * An implementation of the RSA cryptosystem.
 * [ https://en.wikipedia.org/wiki/RSA_(cryptosystem) ]
 *
 * DISCLAIMER: This implementation is for educational purposes only, and NOT
 * to be used for sensitive data. (No padding is made, for example.)
 *
 * @author Mattias Nordahl
 * @author Patrik Persson
 */
public class Crypto {

    public static final BigInteger PUBLIC_KEY_E = BigInteger.valueOf(65537);

    public final BigInteger n;      // public key (together with PUBLIC_KEY_E above)
    private final BigInteger d;     // private key

    /** Use a known pair of prime integers (decryption). */
    public Crypto(BigInteger p, BigInteger q) {

        // Compute the public key n as p*q,
        // and the private key d as e^−1 (mod lambda(n)), where
        //
        // e          ==  65537          ==  PUBLIC_KEY_E above, and
        // lambda(n)  ==  lcm(p-1, q-1)  ==  (p-1)*(q-1)/gcd(p-1,q-1)
        //
        // see: https://en.wikipedia.org/wiki/RSA_(cryptosystem)#Key_generation

        n = p.multiply(q);

        BigInteger p1 = p.subtract(BigInteger.ONE);
        BigInteger q1 = q.subtract(BigInteger.ONE);
        BigInteger lambda = p1.multiply(q1).divide(p1.gcd(q1));

        d = PUBLIC_KEY_E.modInverse(lambda);
    }
    
    /** Use new pair of random, prime integers (encryption). */
    public Crypto(int bits, Random rnd) {
        this(BigInteger.probablePrime(bits / 2, rnd),
             BigInteger.probablePrime(bits - bits / 2, rnd));
    }

    /** Encrypt a message. */
    public String encrypt(String plaintext) {
        BigInteger b = new BigInteger(1, plaintext.getBytes(StandardCharsets.UTF_8));
        return transform(b, PUBLIC_KEY_E).toString(Character.MAX_RADIX);
    }

    /** Decrypt a message. */
    public String decrypt(String ciphertext) {
        BigInteger b = new BigInteger(ciphertext, Character.MAX_RADIX);
        byte[] utf = transform(b, d).toByteArray();

        // An initial zero byte may have been added by BigInteger to indicate
        // a positive sign, but a zero byte is never part of a valid UTF-8 string.

        int skip = utf[0] == 0 ? 1 : 0;
        return new String(utf, skip, utf.length - skip, StandardCharsets.UTF_8);
    }

    private BigInteger transform(BigInteger b, BigInteger exponent) {
        BigInteger result = BigInteger.ZERO;
        BigInteger[] q = { b };
        while (q[0].signum() > 0) {
            q = q[0].divideAndRemainder(n);
            result = result.multiply(n).add(q[1].modPow(exponent, n));
        }
        return result;
    }
}