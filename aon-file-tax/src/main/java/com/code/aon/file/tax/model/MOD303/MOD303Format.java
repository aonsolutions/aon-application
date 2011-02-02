package com.code.aon.file.tax.model.MOD303;

public enum MOD303Format {

	ALAVA_2010(2010
			,"Alava"
			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml"),
	BIZKAIA_2010(2010
			,"Bizkaia"
			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml"),
	GIPUZKOA_2010(2010
			,"Gipuzkoa"
			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml"),
	NAVARRA_2010(2010
			,"Navarra"
			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml"),
	AEAT_2010(2010
			,"AEAT"
			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml");

	private Integer year;
	private String description;
	private String declarationMetadataResource;


	private MOD303Format(Integer year,String description,String declarationMetadataResource)	{
		this.year = year;
		this.description = description;
		this.declarationMetadataResource = declarationMetadataResource;
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
	
	public String getDeclarationMetadataResource() {
		return declarationMetadataResource;
	}
	public void setDeclarationMetadataResource(String declarationMetadataResource) {
		this.declarationMetadataResource = declarationMetadataResource;
	}
}
