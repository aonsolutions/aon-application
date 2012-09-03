package com.code.aon.jaas.auth.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various security related utilities like MessageDigest factories, SecureRandom
 * access, password hashing.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 * 
 */
public class Util {

	/** Proper Class logger. */
	private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);

	/** Encoding BASE64 type. */
	public static final String BASE64_ENCODING = "BASE64";

	/** Encoding BASE16 type. */
	public static final String BASE16_ENCODING = "HEX";

	public static final String RFC2617_ENCODING = "RFC2617";

	/**
	 * The ASCII printable characters the MD5 digest maps to for RFC2617
	 */
	private static char[] MD5_HEX = "0123456789abcdef".toCharArray();

	/**
	 * If hashing is enabled, this method is called from <code>login()</code>
	 * prior to password validation.
	 * <p>
	 * Subclasses may override it to provide customized password hashing, for
	 * example by adding user-specific information or salting.
	 * <p>
	 * The default version calculates the hash based on the following options:
	 * <ul>
	 * <li><em>hashAlgorithm</em>: The digest algorithm to use.
	 * <li><em>hashEncoding</em>: The format used to store the hashes (base64 or
	 * hex)
	 * <li><em>hashCharset</em>: The encoding used to convert the password to
	 * bytes for hashing.
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
			LOGGER.error("charset " + hashCharset //$NON-NLS-1$
					+ " not found. Using platform default", uee); //$NON-NLS-1$
			passBytes = password.getBytes();
		}

		// calculate the hash and apply the encoding.
		try {
			byte[] hash = MessageDigest.getInstance(hashAlgorithm).digest(
					passBytes);

			if (hashEncoding.equalsIgnoreCase(BASE64_ENCODING)) {
				passwordHash = encodeBase64(hash);
			} else if (hashEncoding.equalsIgnoreCase(BASE16_ENCODING)) {
				passwordHash = encodeBase16(hash);
			} else if (hashEncoding.equalsIgnoreCase(RFC2617_ENCODING)) {
				passwordHash = encodeRFC2617(hash);
			} else {
				LOGGER.error("Unsupported hash encoding format " + hashEncoding);
			}
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Password hash calculation failed", e);
		}
		return passwordHash;
	}

	/**
	 * BASE64 encoder implementation. Provides encoding methods, using the
	 * BASE64 encoding rules, as defined in the MIME specification, <a
	 * href="http://ietf.org/rfc/rfc1521.txt">rfc1521</a>.
	 */
	public static String encodeBase64(byte[] bytes) {
        String base64 = null;
        try {
            base64 = Base64Encoder.encode(bytes);
        } catch (IOException e) {
            LOGGER.error("Encode failed", e);
        }
        return base64;
	}

	/**
	 * 3.1.3 Representation of digest values
	 * 
	 * An optional header allows the server to specify the algorithm used to
	 * create the checksum or digest. By default the MD5 algorithm is used and
	 * that is the only algorithm described in this document.
	 * 
	 * For the purposes of this document, an MD5 digest of 128 bits is
	 * represented as 32 ASCII printable characters. The bits in the 128 bit
	 * digest are converted from most significant to least significant bit, four
	 * bits at a time to their ASCII presentation as follows. Each four bits is
	 * represented by its familiar hexadecimal notation from the characters
	 * 0123456789abcdef. That is, binary 0000 getInfos represented by the
	 * character '0', 0001, by '1', and so on up to the representation of 1111
	 * as 'f'.
	 * 
	 * @param data
	 *            - the raw MD5 hash data
	 * @return the encoded MD5 representation
	 */
	public static String encodeRFC2617(byte[] data) {
		char[] hash = new char[32];
		for (int i = 0; i < 16; i++) {
			int j = (data[i] >> 4) & 0xf;
			hash[i * 2] = MD5_HEX[j];
			j = data[i] & 0xf;
			hash[i * 2 + 1] = MD5_HEX[j];
		}
		return new String(hash);
	}

	/**
	 * Hex encoding of hashes, as used by Catalina. Each byte is converted to
	 * the corresponding two hex characters.
	 */
	public static String encodeBase16(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			// top 4 bits
			char c = (char) ((b >> 4) & 0xf);
			if (c > 9)
				c = (char) ((c - 10) + 'a');
			else
				c = (char) (c + '0');
			sb.append(c);
			// bottom 4 bits
			c = (char) (b & 0xf);
			if (c > 9)
				c = (char) ((c - 10) + 'a');
			else
				c = (char) (c + '0');
			sb.append(c);
		}
		return sb.toString();
	}

}