package com.code.aon.webmail;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;

public class EmailSecurity {

	public static MimeMultipart sign( MimeMultipart mp, SecurityInfo si ) throws WebmailException {
	
		try {
			Certificate[] chain = si.getCertificateChain();
			PrivateKey privateKey = si.getPrivateKey();
			if (privateKey == null) {
				throw new WebmailException("cannot find private key for alias: " + si.getAlias());
			}
			X509Certificate origCert = (X509Certificate) chain[0];
			String signDN = origCert.getIssuerDN().getName();
			
			List<KeyStore> certList = new ArrayList<KeyStore>();
	
			certList.add(si.getKeystore());
	
	        //
	        // create a CertStore containing the certificates we want carried
	        // in the signature
	        //
	        CertStore certsAndcrls = CertStore.getInstance(
	                                "Collection",
	                                new CollectionCertStoreParameters(certList), "BC");
	
	        //
	        // create some smime capabilities in case someone wants to respond
	        //
	        ASN1EncodableVector         signedAttrs = new ASN1EncodableVector();
	        SMIMECapabilityVector       caps = new SMIMECapabilityVector();
	
	        caps.addCapability(SMIMECapability.dES_EDE3_CBC);
	        caps.addCapability(SMIMECapability.rC2_CBC, 128);
	        caps.addCapability(SMIMECapability.dES_CBC);
	
	        signedAttrs.add(new SMIMECapabilitiesAttribute(caps));
	
	        //
	        // add an encryption key preference for encrypted responses -
	        // normally this would be different from the signing certificate...
	        //
	        IssuerAndSerialNumber   issAndSer = new IssuerAndSerialNumber(
	                new X509Name(signDN), origCert.getSerialNumber());
	
	        signedAttrs.add(new SMIMEEncryptionKeyPreferenceAttribute(issAndSer));		
	        //
	        // create the generator for creating an smime/signed message
	        //
	        SMIMESignedGenerator gen = new SMIMESignedGenerator();
	        
	        //
	        // add a signer to the generator - this specifies we are using SHA1 and
	        // adding the smime attributes above to the signed attributes that
	        // will be generated as part of the signature. The encryption algorithm
	        // used is taken from the key - in this RSA with PKCS1Padding
	        //
	        gen.addSigner(privateKey, origCert, SMIMESignedGenerator.DIGEST_SHA1, new AttributeTable(signedAttrs), null);
	
	        //
	        // add our pool of certs and cerls (if any) to go with the signature
	        //
	        gen.addCertificatesAndCRLs(certsAndcrls);
	        
	        MimeBodyPart m = new MimeBodyPart();
	
	        //
	        // be careful about setting extra headers here. Some mail clients
	        // ignore the To and From fields (for example) in the body part
	        // that contains the multipart. The result of this will be that the
	        // signature fails to verify... Outlook Express is an example of
	        // a client that exhibits this behaviour.
	        //
	        m.setContent(mp);
	
	        //
	        // extract the multipart object from the SMIMESigned object.
	        //
	        return gen.generate(m, "BC");
		} catch ( Throwable th ) {
			throw new WebmailException( th.getMessage(), th );
		}
	}
	
}
