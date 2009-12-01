package com.code.aon.jaas.auth.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Various security related utilities like MessageDigest factories, SecureRandom access, 
 * password hashing.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 *  
 */
public class Util {

    /** Proper Class logger. */
    private static final Log LOGGER = LogFactory.getLog( Util.class.getName() );

    /** Encoding BASE64 type. */
    public static final String BASE64_ENCODING = "BASE64";

    /** Encoding BASE16 type. */
    public static final String BASE16_ENCODING = "HEX";

    /**
     * If hashing is enabled, this method is called
     * from <code>login()</code> prior to password validation.
     * <p>
     * Subclasses may override it to provide customized password hashing, for
     * example by adding user-specific information or salting.
     * <p>
     * The default version calculates the hash based on the following options:
     * <ul>
     * <li><em>hashAlgorithm</em>: The digest algorithm to use.
     * <li><em>hashEncoding</em>: The format used to store the hashes
     * (base64 or hex)
     * <li><em>hashCharset</em>: The encoding used to convert the password
     * to bytes for hashing.
     * </ul>
     * It will return null if the hash fails for any reason, which will in turn
     * cause <code>validatePassword()</code> to fail.
     * 
     * @param hashAlgorithm
     *            the MessageDigest algorithm name
     * @param hashEncoding
     *            either base64 or hex to specify the type of encoding the
     *            MessageDigest as a string.
     * @param hashCharset
     *            the charset used to create the digest encoded string. If null
     *            the platform default is used.
     * @param username
     *            ignored in default version
     * @param password
     *            the password string to be hashed
     * @return String
     */
    public static String createPasswordHash(String hashAlgorithm,
            String hashEncoding, String hashCharset, String username,
            String password) {
        byte[] passBytes;
        String passwordHash = null;

        // convert password to byte data
        try {
            if (hashCharset == null) {
                passBytes = password.getBytes();
            } else {
                passBytes = password.getBytes(hashCharset);
            }

        } catch (UnsupportedEncodingException uee) {
            LOGGER.fatal("charset " + hashCharset //$NON-NLS-1$
                    + " not found. Using platform default." + uee.getMessage()); //$NON-NLS-1$
            passBytes = password.getBytes();
        }

        // calculate the hash and apply the encoding.
        try {
            byte[] hash = MessageDigest.getInstance(hashAlgorithm).digest(passBytes);
            if (hashEncoding.equalsIgnoreCase(BASE64_ENCODING)) {
                passwordHash = Util.encodeBase64(hash);
            } else if (hashEncoding.equalsIgnoreCase(BASE16_ENCODING)) {
                passwordHash = Util.encodeBase16(hash);
            } else {
                LOGGER.fatal("Unsupported hash encoding format " + hashEncoding); //$NON-NLS-1$
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO [iayerbe] Mirar si se debe propagar esta excepción.
            LOGGER.fatal("Password hash calculation failed " + e.getMessage()); //$NON-NLS-1$
        }
        return passwordHash;
    }

    /**
     * Hex encoding of hashes, as used by Catalina.
     * Each byte is converted to the corresponding two hex characters.
     * 
     * @param bytes
     *            byte[]
     * @return String
     */
    public static String encodeBase16(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2); // $codepro.audit.disable
        // numericLiterals
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            // top 4 bits
            char c = (char) ((b >> 4) & 0xf); // $codepro.audit.disable
            // numericLiterals
            if (c > 9) {// $codepro.audit.disable numericLiterals
                c = (char) ((c - 10) + 'a'); // $codepro.audit.disable
                // numericLiterals
            } else {
                c = (char) (c + '0');
            }
            sb.append(c);
            // bottom 4 bits
            c = (char) (b & 0xf); // $codepro.audit.disable numericLiterals
            if (c > 9) { // $codepro.audit.disable numericLiterals
                c = (char) ((c - 10) + 'a'); // $codepro.audit.disable
                // numericLiterals
            } else {
                c = (char) (c + '0');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * BASE64 encoder implementation. Provides
     * encoding methods, using the BASE64 encoding rules, as defined in the MIME
     * specification, <a href="http://ietf.org/rfc/rfc1521.txt">rfc1521 </a>.
     * 
     * @param bytes
     *            byte[]
     * @return String
     */
    public static String encodeBase64(byte[] bytes) {
        String base64 = null;
        try {
            base64 = Base64Encoder.encode(bytes);
        } catch (IOException e) {
            // TODO [iayerbe] Mirar si se debe propagar esta excepción.
            LOGGER.fatal("Encode failed " + e.getMessage()); //$NON-NLS-1$
        }
        return base64;
    }
}