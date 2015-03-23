package com.code.aon.file.tax.model.MOD190;

import com.code.aon.config.enumeration.Administration;

public enum MOD190Format {

	ALAVA_2013(2013,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD190/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD190/Receiver.xml"),
	BIZKAIA_2013(2013,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD190/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD190/Receiver.xml"),
	GIPUZKOA_2013(2013,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD190/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD190/Receiver.xml"),
	NAVARRA_2013(2013,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD190/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD190/Receiver.xml"),
	AEAT_2013(2013,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD190/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD190/Receiver.xml");
			  
	private Integer year;
	private Administration administration;
	private String deponentMetadataResource;
	private String receiverMetadataResource;


	private MOD190Format(Integer year,Administration administration,String deponentMetadataResource,
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
	
	public static MOD190Format obtainFormat(int year, int administration) {
		Administration adm = Administration.values()[administration];
		MOD190Format f = null;
		for (MOD190Format format : MOD190Format.values()) {
			if (format.getAdministration() == adm && year >= format.getYear()) {
				if (f == null || f.getYear() < format.getYear()) {
					f = format;
				}
			}
		}
		return f;
	}
}
