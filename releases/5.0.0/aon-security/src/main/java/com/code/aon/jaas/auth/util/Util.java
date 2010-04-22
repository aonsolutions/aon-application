package com.code.aon.jaas.auth.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.IConstants;

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
    private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);

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
            LOGGER.error("charset " + hashCharset //$NON-NLS-1$
                    + " not found. Using platform default", uee); //$NON-NLS-1$
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
                LOGGER.error("Unsupported hash encoding format {}", hashEncoding); //$NON-NLS-1$
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO [iayerbe] Mirar si se debe propagar esta excepción.
            LOGGER.error("Password hash calculation failed", e); //$NON-NLS-1$
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
            LOGGER.error("Encode failed", e); //$NON-NLS-1$
        }
        return base64;
    }

    /**
     * Find server IP adddress where the web application is deployed.
     * 
     * @param thisIp
     * @param contextPath
     * @return
     * @throws UnknownHostException
     */
    public static final String findStoredApplicationIp(String thisIp, String contextPath) 
    			throws UnknownHostException {
		File[] files = new File( IConstants.RESOURCES_DEFAULT_DIR ).listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if ( file.isFile() && file.canRead() ) {
				String ip = file.getName(); 
				if ( !ip.equals( thisIp ) ) {
					Iterator<String> it = deserialize( ip ).iterator();
					while (it.hasNext()) {
						String elem = it.next();
						if ( elem.indexOf( contextPath ) > -1 )
							return ip;
					}
				}
			}
		}
		return thisIp;
	}

	/**
	 * Serialize deployed applications.
	 * 
	 * @param l
	 * @throws IOException
	 */
	public static void serialize(List<String> l) throws IOException {
		FileOutputStream ostream = null;
		try {
			String thisIp = InetAddress.getLocalHost().getHostAddress();
			String path = IConstants.RESOURCES_DEFAULT_DIR + thisIp;
			LOGGER.debug( "Serializing deployed applications: {} on {}", l, thisIp );
			ostream = new FileOutputStream( path );
			/* Create the output stream */
			ObjectOutputStream oopstream = new ObjectOutputStream( ostream );
			oopstream.writeObject( l );
			oopstream.flush();
		} catch (UnknownHostException e) {
			LOGGER.error( e.getMessage(), e );
		} catch(IOException e) {
			LOGGER.error( e.getMessage(), e );
			throw e;
		} finally {
			if ( ostream != null ) 
				ostream.close();
		}
	}

	/**
	 * Deserialize deployed applications.
	 * 
	 * @param thisIp
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> deserialize(String thisIp) {
		FileInputStream istream = null;
		try {
			String path = IConstants.RESOURCES_DEFAULT_DIR + thisIp;
			LOGGER.debug( "Deserializing deployed applications from {}", thisIp );
			istream = new FileInputStream( path );
			/* Create the output stream */
			ObjectInputStream p = new ObjectInputStream( istream );
			return (List) p.readObject();
		} catch(IOException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (ClassNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			if ( istream != null )
				try {
					istream.close();
				} catch(IOException e) {
				}
		}
		return null;
	}

	/**
	 * Return <code>AonGenericPrincipal</code> in the application server AonSessionManager MBean.
	 * 
	 * @param mserver
	 * @param on
	 * @param sessionId
	 * 
	 * @return
	 * @throws NullPointerException 
	 * @throws MalformedObjectNameException 
	 * @throws ReflectionException 
	 * @throws MBeanException 
	 * @throws InstanceNotFoundException 
	 */
	public static final Object getSSOPrincipal(MBeanServer mserver, ObjectName on, String sessionId) 
				throws MalformedObjectNameException, NullPointerException, InstanceNotFoundException
				, MBeanException, ReflectionException {
		Object[] params = { sessionId };
		String[] sig = { String.class.getName() };
		return mserver.invoke( on, "getSSOPrincipal", params, sig );
	}

	/**
	 * Remove SSO principal from the application server AonSessionManager MBean.
	 * 
	 * @param mserver
	 * @param on
	 * @param sessionId
	 * 
	 * @return
	 */
	public static final void removeSSOPrincipal(MBeanServer mserver, ObjectName on, String sessionId)
				throws MalformedObjectNameException, NullPointerException, InstanceNotFoundException
				, MBeanException, ReflectionException {
		Object[] params = { sessionId };
		String[] sig = { String.class.getName() };
		mserver.invoke( on, "removeSSOPrincipal", params, sig );
	}
}