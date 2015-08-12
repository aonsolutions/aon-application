package com.esferalia.aon.watson.server.codec;

public class AonMessageDigestAlgorithms {

	  private AonMessageDigestAlgorithms() {
	        // cannot be instantiated.
	    }

	    /**
	     * The MD2 message digest algorithm defined in RFC 1319.
	     */
	    public static final String MD2 = "MD2";

	    /**
	     * The MD5 message digest algorithm defined in RFC 1321.
	     */
	    public static final String MD5 = "MD5";

	    /**
	     * The SHA-1 hash algorithm defined in the FIPS PUB 180-2.
	     */
	    public static final String SHA_1 = "SHA-1";

	    /**
	     * The SHA-256 hash algorithm defined in the FIPS PUB 180-2.
	     */
	    public static final String SHA_256 = "SHA-256";

	    /**
	     * The SHA-384 hash algorithm defined in the FIPS PUB 180-2.
	     */
	    public static final String SHA_384 = "SHA-384";

	    /**
	     * The SHA-512 hash algorithm defined in the FIPS PUB 180-2.
	     */
	    public static final String SHA_512 = "SHA-512";
}
