package com.esferalia.aon.gwt.api.client;

import com.esferalia.aon.gwt.api.client.commercial.Commission;
import com.esferalia.aon.gwt.api.client.common.Common;
import com.esferalia.aon.gwt.api.client.communication.Communication;
import com.esferalia.aon.gwt.api.client.documental.Attachment;
import com.esferalia.aon.gwt.api.client.expedient.Expedient;
import com.esferalia.aon.gwt.api.client.finance.Finance;
import com.esferalia.aon.gwt.api.client.fiscal.Fiscal;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.payroll.Payroll;
import com.esferalia.aon.gwt.api.client.product.Product;
import com.esferalia.aon.gwt.api.client.registry.Registry;
import com.esferalia.aon.gwt.api.client.seres.Seres;
import com.esferalia.aon.gwt.api.client.sii.Sii;
import com.esferalia.aon.gwt.api.client.warehouse.Warehouse;

public class API {
	private String url;
	private String domainName;
	private Integer domainId;
	private String userName;
	private String accessToken;
	
	public API(String url, String accessToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accessToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
	}
	
	public Finance getFinance() {
		return new Finance(url, accessToken, domainName, domainId, userName);
	}
	
	public Incidence getIncidence() {
		return new Incidence(url, accessToken, domainName, domainId, userName);
	}
	
	public Product getProduct() {
		return new Product(url, accessToken, domainName, domainId, userName);
	}
	
	public Attachment getAttachment() {
		return new Attachment(url, accessToken, domainName, domainId, userName);
	}
	
	public Registry getRegistry() {
		return new Registry(url, accessToken, domainName, domainId, userName);
	}
	
	public Common getCommon() {
		return new Common(url, accessToken, domainName, domainId, userName);
	}
	
	public Warehouse getWarehouse() {
		return new Warehouse(url, accessToken, domainName, domainId, userName);
	}
	
	public Payroll getPayroll() {
		return new Payroll(url, accessToken, domainName, domainId, userName);
	}
	
	public Sii getSii() {
		return new Sii(url, accessToken, domainName, domainId, userName);
	}
	
	public Seres getSeres() {
		return new Seres(url, accessToken, domainName, domainId, userName);
	}
	
	public Communication getCommunication() {
		return new Communication(url, accessToken, domainName, domainId, userName);
	}
	
	public Commission getCommission() {
		return new Commission(url, accessToken, domainName, domainId, userName);
	}
	
	public Fiscal getFiscal() {
		return new Fiscal(url, accessToken, domainName, domainId, userName);
	}

	public Expedient getExpedient() {
		return new Expedient(url, accessToken, domainName, domainId, userName);
	}
}
