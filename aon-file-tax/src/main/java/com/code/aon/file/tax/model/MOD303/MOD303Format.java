package com.code.aon.file.tax.model.MOD303;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;

public enum MOD303Format {

	ALAVA_2010(2011
			,Administration.ALAVA
			,MimeType.MIME_XML
			,null)
//	,BIZKAIA_2010(2010
//			,Administration.BIZKAIA
//			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml")
//	,GIPUZKOA_2010(2010
//			,Administration.GIPUZKOA
//			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml")
//	,NAVARRA_2010(2010
//			,Administration.NAVARRA
//			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml")
//	,AEAT_2010(2010
//			,Administration.COMMON_TERRITORY
//			,"/com/code/aon/file/tax/model/MOD303/xml/2010_ALAVA_Declaration.xml")
	;

	private Integer year;
	private Administration administration;
	private String declarationMetadataResource;
	private MimeType mimeType;


	private MOD303Format(Integer year,Administration administration,MimeType mimeType,String declarationMetadataResource)	{
		this.year = year;
		this.administration = administration;
		this.declarationMetadataResource = declarationMetadataResource;
		this.mimeType = mimeType;
	}

	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
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

	public MimeType getMimeType() {
		return mimeType;
	}
}
