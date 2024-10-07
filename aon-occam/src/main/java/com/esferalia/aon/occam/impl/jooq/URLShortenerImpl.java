package com.esferalia.aon.occam.impl.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.UrlShorten.URL_SHORTEN;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.UrlShortenRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IURLShortener;
import com.esferalia.aon.watson.server.http.AonURIBuilder;

public class URLShortenerImpl implements IURLShortener {

	@Override
	public String getURL(AONContext ctx, String shortUrl) {
		
		AonURIBuilder aonURIBuilder = new AonURIBuilder(URI.create(shortUrl));
		
		String domain = aonURIBuilder.getHost();
		String uuid = aonURIBuilder.getPathSegments().getLast();
		
		UrlShortenRecord urlShorten =
		ctx.getDslContext()
		.select()
		.from(URL_SHORTEN)
		.innerJoin(DOMAIN).onKey()
		.where(DOMAIN.NAME.eq(domain))
		.and(URL_SHORTEN.UUID.eq(uuid))
		.and(URL_SHORTEN.EXPIRATION_DATE.isNull()
		.or(URL_SHORTEN.EXPIRATION_DATE.ge(DSL.currentTimestamp())))
		.fetchOneInto(URL_SHORTEN);
		
		String url = urlShorten.getUrl();
		
		urlShorten.setCount(urlShorten.getCount() + 1);
		urlShorten.update();
		
		return url;
	}

	@Override
	public String getShortURL(AONContext ctx, String path, String url, Date expirationDate) {
		
		AonURIBuilder aonURIBuilder = 
		new AonURIBuilder(URI.create(url));
		
		String domainName = aonURIBuilder.getQueryParamsMap().getOrDefault("domain",
				new String[] { aonURIBuilder.getHost() })[0];		
		
		int domain = 
		ctx.getDslContext()
		.select()
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq(domainName))
		.fetchOne(DOMAIN.ID);

		String uuid = generateUUID();
		
		Field<Timestamp> expirationTime = expirationDate == null ? DSL.castNull(Timestamp.class) : 
				DSL.cast(new Timestamp(expirationDate.getTime()), Timestamp.class);
		
		ctx.getDslContext()
		.insertInto(URL_SHORTEN)
		.set(URL_SHORTEN.COUNT, 0)
		.set(URL_SHORTEN.URL, url)
		.set(URL_SHORTEN.UUID, uuid)
		.set(URL_SHORTEN.DOMAIN, domain)
		.set(URL_SHORTEN.EXPIRATION_DATE, expirationTime)
		.execute();
		
		return aonURIBuilder.setHost(domainName).setPathSegments(path, uuid).clearParameters().clearFragment().toString();
		
	}
	
	
	private static String generateUUID() {
		return UUID.randomUUID().toString();
	}
	

}
