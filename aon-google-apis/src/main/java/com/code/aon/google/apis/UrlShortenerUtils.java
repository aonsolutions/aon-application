package com.code.aon.google.apis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.Arrays;

import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.UrlshortenerScopes;
import com.google.api.services.urlshortener.model.Url;


public class UrlShortenerUtils{
	
public static Urlshortener serviceInitialize(DomainGserviceaccount g) throws KeyStoreException, IOException, GeneralSecurityException{
				
		
		final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		final JsonFactory JSON_FACTORY = new JacksonFactory();
		final String SERVICE_ACCOUNT_ID = g.getEmailAddress();

		InputStream keyStream = new ByteArrayInputStream(g.getPrivateKey());
		PrivateKey serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), keyStream, "notasecret",
		          "privatekey", "notasecret");
		
		String google_account = g.getGoogleAccount(); 
		
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(
						Arrays.asList(UrlshortenerScopes.URLSHORTENER))
				.setServiceAccountPrivateKey(serviceAccountPrivateKey)
				.setServiceAccountUser(google_account)
				.build();

		
		Urlshortener client= new Urlshortener.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential )
				.setApplicationName("AON SOLUTIONS").build();
		
		return client;
		
	}
	
	public static  String getShortUrl(Urlshortener urlshortener, String longUrl) throws IOException{
		Url url = new Url().setLongUrl(longUrl);
		url = urlshortener.url().insert(url).execute();
		return url.getId();
	}
	
	
	
}
