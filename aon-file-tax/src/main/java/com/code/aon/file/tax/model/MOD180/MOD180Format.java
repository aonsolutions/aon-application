package com.code.aon.file.tax.model.MOD180;

import com.code.aon.config.enumeration.Administration;

public enum MOD180Format {

	 ALAVA_2014(2014,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180Detail.xml")
	,BIZKAIA_2014(2014,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180Detail.xml")
	,GIPUZKOA_2014(2014,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180Detail.xml")
	,NAVARRA_2014(2014,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180Detail.xml")
	,AEAT_2014(2014,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2014_Mod180Detail.xml")
	,ALAVA_2013(2013,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180Detail.xml")
	,BIZKAIA_2013(2013,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180Detail.xml")
	,GIPUZKOA_2013(2013,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180Detail.xml")
	,NAVARRA_2013(2013,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180Detail.xml")
	,AEAT_2013(2013,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180.xml"
			,"/com/code/aon/file/tax/model/MOD180/2013_Mod180Detail.xml");
			  
	private Integer year;
	private Administration administration;
	private String deponentMetadataResource;
	private String receiverMetadataResource;


	private MOD180Format(Integer year,Administration administration,String deponentMetadataResource,
			String receiverMetadataResource)	{
		this.year =  year;
		this.administration = administration;
		this.deponentMetadataResource = deponentMetadataResource;
		this.receiverMetadataResource = receiverMetadataResource;
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

	public String getReceiverMetadataResource() {
		return receiverMetadataResource;
	}
	
	public static MOD180Format obtainFormat(int year, int administration) {
		Administration adm = Administration.values()[administration];
		MOD180Format f = null;
		for (MOD180Format format : MOD180Format.values()) {
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
