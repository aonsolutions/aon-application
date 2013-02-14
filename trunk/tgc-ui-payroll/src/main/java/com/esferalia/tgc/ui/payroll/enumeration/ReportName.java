package com.esferalia.tgc.ui.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum ReportName implements IResourceable, IStringEnum {
	// INFORMES DE EMPRESA
	INFORME_TGC_EMPRESA("informeTgcEmpresa", ReportType.ENTERPRISE),
	
	// INFORMES DE CONTRATO
	INFORME_TGC_CONTRATO("informeTgcContrato", ReportType.CONTRACT),
	INFORME_TGC2_CONTRATO("informeTgc2Contrato", ReportType.CONTRACT),
		
	// INFORMES DE PERSONA
	INFORME_TGC_PERSONA("informeTgcPersona", ReportType.PERSON),
	INFORME_TGC_ALFABETICO("informeTgcAlphabetical", ReportType.PERSON),
	;
	
	private static final String BASE_NAME = "com.esferalia.tgc.ui.payroll.i18n.report";
	private static final String MSG_KEY_PREFIX = "aon_enum_report_name_";
	
	@Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	private String value;
	private ReportType type;
	
	ReportName(String value, ReportType type) {
		this.value = value;
		this.type = type;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	public ReportType getType() {
		return type;
	}
}