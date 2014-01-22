package com.code.aon.file.tax.model.MOD311;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;

public enum MOD311Format {

	AEAT_2013(2013
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2013MOD311Factory.class);

	private Integer year;
	private MimeType mimeType;
	private Administration administration;
	private Class<? extends IMOD311Factory> factory;


	private MOD311Format(Integer year,Administration administration,MimeType mimeType,Class<? extends IMOD311Factory> factory)	{
		this.year = year;
		this.mimeType = mimeType;		
		this.administration = administration;
		this.factory = factory;
	}

	public Administration getAdministration() {
		return administration;
	}
	public Integer getYear() {
		return year;
	}
	public MimeType getMimeType() {
		return mimeType;
	}
	public Class<? extends IMOD311Factory> getFactory() {
		return factory;
	}
	
	
	public synchronized static MOD311Format getFormat(Administration administration, int year) {
		MOD311Format format = null;
		for (MOD311Format f : MOD311Format.values()) {
			if (f.getAdministration() == administration && year >= f.getYear()) {
				format = f;
				break;
			}
		}
		if (format == null) {
			String msg = "La generación de archivos para la administracion "
					+ administration + " no está aún implementada.";
			throw new IllegalArgumentException(msg);
		}
		return format;
	}
}
