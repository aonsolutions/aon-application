package com.code.aon.file.tax.model.MOD184;

import com.code.aon.config.enumeration.Administration;

public enum MOD184Format {

	 ALAVA_2014(2014,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Income.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Partner.xml")
	,BIZKAIA_2014(2014,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Income.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Partner.xml")
	,GIPUZKOA_2014(2014,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Income.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Partner.xml")
	,NAVARRA_2014(2014,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Income.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Partner.xml")
	,AEAT_2014(2014,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Income.xml"
			,"/com/code/aon/file/tax/model/MOD184/2014_Mod184Partner.xml")
	;
			  
	private Integer year;
	private Administration administration;
	private String deponentMetadataResource;
	private String incomeMetadataResource;
	private String partnerMetadataResource;


	private MOD184Format(Integer year,Administration administration,String deponentMetadataResource,
			String incomeMetadataResource,String partnerMetadataResource)	{
		this.year =  year;
		this.administration = administration;
		this.deponentMetadataResource = deponentMetadataResource;
		this.incomeMetadataResource = incomeMetadataResource;
		this.partnerMetadataResource = partnerMetadataResource;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public Administration getAdministration() {
		return administration;
	}

	public String getDeponentMetadataResource() {
		return deponentMetadataResource;
	}

	public String getIncomeMetadataResource() {
		return incomeMetadataResource;
	}
	
	public String getPartnerMetadataResource() {
		return partnerMetadataResource;
	}
	
	public static MOD184Format obtainFormat(int year, int administration) {
		Administration adm = Administration.values()[administration];
		MOD184Format f = null;
		for (MOD184Format format : MOD184Format.values()) {
			if (format.getAdministration() == adm && year >= format.getYear()) {
				if (f == null || f.getYear() < format.getYear()) {
					f = format;
				}
			}
		}
		System.out.println( f);
		return f;
	}
	
}
