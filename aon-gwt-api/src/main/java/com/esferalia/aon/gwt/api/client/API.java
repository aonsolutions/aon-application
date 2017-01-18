package com.esferalia.aon.gwt.api.client;

import com.esferalia.aon.gwt.api.client.common.Common;
import com.esferalia.aon.gwt.api.client.documental.Attachment;
import com.esferalia.aon.gwt.api.client.finance.Finance;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.product.Product;
import com.esferalia.aon.gwt.api.client.registry.Registry;

public class API {
	private String url;
	private String domainName;
	private String userName;
	private String accessToken;
	
	public API(String url, String accessToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accessToken;
		this.domainName = domainName;
		this.userName = userName;
	}
	
	public Finance getFinance() {
		return new Finance(url, accessToken, domainName, userName);
	}
	
	public Incidence getIncidence() {
		return new Incidence(url, accessToken, domainName, userName);
	}
	
	public Product getProduct() {
		return new Product(url, accessToken, domainName, userName);
	}
	
	public Attachment getAttachment() {
		return new Attachment(url, accessToken, domainName, userName);
	}
	
	public Registry getRegistry() {
		return new Registry(url, accessToken, domainName, userName);
	}
	
	public Common getCommon() {
		return new Common(url, accessToken, domainName, userName);
	}
}
