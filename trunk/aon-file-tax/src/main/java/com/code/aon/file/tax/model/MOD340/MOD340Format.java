package com.code.aon.file.tax.model.MOD340;

public enum MOD340Format {

	AEAT_2009(2009
			,"Territorio Común (desde 2009)"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml"),
	ALAVA_2009(2009
			,"Alava - (desde 2009)"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml"),
	BIZKAIA_2009(2009
			,"Bizkaia - (desde 2009)"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml"),
	GIPUZKOA_2009(2009
			,"Gipuzkoa - (desde 2009)"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml"),
	NAVARRA_2009(2009
			,"Navarra - (desde 2009)"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml");

	private Integer year;
	private String description;
	private String deponentMetadataResource;
	private String issuedMetadataResource;
	private String receivedMetadataResource;
	private String investmentMetadataResource;
	private String intracommunitaryMetadataResource;


	private MOD340Format(Integer year,String description,String deponentMetadataResource,
			String issuedMetadataResource,String receivedMetadataResource,
			String investmentMetadataResource,String intracommunitaryMetadataResource)	{
		this.year = year;
		this.description = description;
		this.deponentMetadataResource = deponentMetadataResource;
		this.issuedMetadataResource = issuedMetadataResource;
		this.receivedMetadataResource = receivedMetadataResource;
		this.investmentMetadataResource = investmentMetadataResource;
		this.intracommunitaryMetadataResource = intracommunitaryMetadataResource;
	}


	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getDeponentMetadataResource() {
		return deponentMetadataResource;
	}
	public void setDeponentMetadataResource(String deponentMetadataResource) {
		this.deponentMetadataResource = deponentMetadataResource;
	}

	public String getIssuedMetadataResource() {
		return issuedMetadataResource;
	}
	public void setIssuedMetadataResource(String issuedMetadataResource) {
		this.issuedMetadataResource = issuedMetadataResource;
	}

	public String getReceivedMetadataResource() {
		return receivedMetadataResource;
	}
	public void setReceivedMetadataResource(String receivedMetadataResource) {
		this.receivedMetadataResource = receivedMetadataResource;
	}

	public String getInvestmentMetadataResource() {
		return investmentMetadataResource;
	}
	public void setInvestmentMetadataResource(String investmentMetadataResource) {
		this.investmentMetadataResource = investmentMetadataResource;
	}

	public String getIntracommunitaryMetadataResource() {
		return intracommunitaryMetadataResource;
	}
	public void setIntracommunitaryMetadataResource(String intracommunitaryMetadataResource) {
		this.intracommunitaryMetadataResource = intracommunitaryMetadataResource;
	}
}
