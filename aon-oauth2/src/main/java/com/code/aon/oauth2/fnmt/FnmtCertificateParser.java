package com.code.aon.oauth2.fnmt;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;

import com.esferalia.aon.watson.util.AonStringUtils;

public class FnmtCertificateParser {

	public static final String DNI_OID = "1.3.6.1.4.1.5734.1.4";
	public static final String NAME_OID = "1.3.6.1.4.1.5734.1.1";
	public static final String SURNAME_OID = "1.3.6.1.4.1.5734.1.2";
	public static final String SECOND_SURNAME_OID = "1.3.6.1.4.1.5734.1.3";
	public static final String CERTIFICATE_TYPE_OID = "1.3.6.1.4.1.5734.1.33";


	public static Map<String,String> readPropertiesOid(X509Certificate x509Certificate) throws IOException {

		Set<String> oids = x509Certificate.getNonCriticalExtensionOIDs();
		
		Map<String,String> properties = new HashMap<>();
		for (String oid : oids) {
			byte [] value = x509Certificate.getExtensionValue(oid);
			ASN1OctetString  asn1OctetString = (ASN1OctetString ) getObject(value);
			ASN1Primitive asn1Primitive = getObject(asn1OctetString.getOctets());
			readProperties(oid, asn1Primitive, properties);
		}

		return properties;
	}
	
	private static Map<String, String> readProperties (String oid, ASN1Primitive extension, Map<String, String> properties ) {
		if (extension instanceof DERSequence) {
			DERSequence secuence = (DERSequence) extension;
			Enumeration<?> enumObjetos = secuence.getObjects();
			while (enumObjetos.hasMoreElements()) {
				ASN1Primitive object = ((ASN1Encodable) enumObjetos.nextElement()).toASN1Primitive();
				if ( object instanceof ASN1ObjectIdentifier ) {
					oid = ((ASN1ObjectIdentifier) object).getId();
				} else {
					readProperties(oid, object, properties);
				}
			}
		} else if ( extension instanceof DERSet ) {
			DERSet set = (DERSet) extension ;
			Enumeration<?> enumObjetos = set.getObjects();
			while (enumObjetos.hasMoreElements()) {
				ASN1Primitive object = ((ASN1Encodable) enumObjetos.nextElement()).toASN1Primitive();
				readProperties(oid, object, properties);
			}
		} else if ( extension instanceof DERIA5String ) {
			DERIA5String ia5String = (DERIA5String) extension ;
			properties.put(oid, ia5String.getString());
		} else if ( extension instanceof DERUTF8String ) {
			DERUTF8String utf8String = (DERUTF8String) extension ;
			properties.put(oid, utf8String.getString());
		} else if ( extension instanceof DERPrintableString ) {
			DERPrintableString printableString = (DERPrintableString) extension ;
			properties.put(oid, printableString.getString());
		} else if ( extension instanceof DERTaggedObject ) {
			DERTaggedObject taggedObject = (DERTaggedObject) extension ;
			readProperties(oid, taggedObject.getBaseObject().toASN1Primitive(), properties);
		} else if ( extension instanceof DEROctetString ) {
			DEROctetString octetString = (DEROctetString) extension ;
			try {
				readProperties( oid, getObject(octetString.getOctets()) , properties);
			} catch ( Exception e ) {
				properties.put(oid, new String(octetString.getOctets()) );
			}
		} 

		return properties;
	}

	private static ASN1Primitive getObject(byte [] buf) throws IOException {
		try (ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(buf))){
			return in.readObject();
		} 
	}


	public static void main(String[] args) throws CertificateException, IOException {
		for (String arg : args) {
			try ( InputStream is = new FileInputStream(arg)){
				X509Certificate x509Certificate = 
				(X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(is);
				readPropertiesOid(x509Certificate);
				
			}
		}
		
	}

}
