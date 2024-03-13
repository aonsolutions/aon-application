package org.aonsolutions.jsoup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JSoup {
    
	private static  KeyStore sslClientCertificateStore_;
    private static  char[] sslClientCertificatePassword_;
    
    /**
     * Sets the SSL client certificate to use. The needed parameters are used to
     * construct a {@link java.security.KeyStore}.
     * <p>
     * If the web server requires Renegotiation, you have to set system property
     * "sun.security.ssl.allowUnsafeRenegotiation" to true, as hinted in
     * <a href="http://www.oracle.com/technetwork/java/javase/documentation/tlsreadme2-176330.html">
     * TLS Renegotiation Issue</a>.
     *
     * @param certificateInputStream the input stream which represents the certificate
     * @param certificatePassword the certificate password
     * @param certificateType the type of certificate, usually {@code jks} or {@code pkcs12}
     */
    public static void setSSLClientCertificate(final InputStream certificateInputStream, final String certificatePassword,
            final String certificateType) {
        try {
            sslClientCertificateStore_ = getKeyStore(certificateInputStream, certificatePassword, certificateType);
            sslClientCertificatePassword_ = certificatePassword == null ? null : certificatePassword.toCharArray();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyStore getKeyStore(final InputStream inputStream, final String keystorePassword,
            final String keystoreType)
                    throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        if (inputStream == null) {
            return null;
        }

        final KeyStore keyStore = KeyStore.getInstance(keystoreType);
        final char[] passwordChars = keystorePassword == null ? null : keystorePassword.toCharArray();
        keyStore.load(inputStream, passwordChars);
        return keyStore;
    }
    

	public static void main(String[] args) throws Exception {
		String url = "https://w2.seg-social.es/GetAccess/ResourceList";
		
		FileInputStream fin = new FileInputStream("/home/ndiaz/Descargas/aon.p12");
		KeyStore ks = KeyStore.getInstance("pkcs12");
		ks.load(fin,"Alma1981".toCharArray());
		
		ks.store(new FileOutputStream("certificate.jks"), "Alma1981".toCharArray());
		
		System.setProperty("javax.net.ssl.keyStore", "certificate.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "Alma1981"); 

		Connection.Response res = Jsoup.connect(url).
	        timeout(5000).ignoreHttpErrors(true).followRedirects(true).execute();
		Document post = res.parse();
		
		post.getElementsByTag("a").forEach( a -> {
			a.getElementsContainingText("Online Real").forEach(e -> {
				
				try {
					Response res2 = Jsoup.connect(e.attribute("href").getValue()).timeout(5000).execute();
					System.out.println(res2.body());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			
		});
	}
}
