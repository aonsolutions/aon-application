package com.code.aon.file.tax.model.MOD130;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;

public enum MOD130Format {

	AEAT_2011(2011
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2011MOD130Factory.class);

	private Integer year;
	private MimeType mimeType;
	private Administration administration;
	private Class<? extends IMOD130Factory> factory;


	private MOD130Format(Integer year,Administration administration,MimeType mimeType,Class<? extends IMOD130Factory> factory)	{
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
	public Class<? extends IMOD130Factory> getFactory() {
		return factory;
	}
	
	
	public synchronized static MOD130Format getFormat(Administration administration, int year) {
		MOD130Format format = null;
		for (MOD130Format f : MOD130Format.values()) {
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
