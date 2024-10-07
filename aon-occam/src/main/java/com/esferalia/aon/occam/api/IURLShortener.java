package com.esferalia.aon.occam.api;

import java.util.Date;

public interface IURLShortener {
	
	public String getURL(AONContext ctx, String shortUrl );

	public String getShortURL(AONContext ctx, String path, String url, Date expirationDate );

}
