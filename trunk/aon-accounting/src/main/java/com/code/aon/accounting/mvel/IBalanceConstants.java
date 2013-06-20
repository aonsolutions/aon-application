package com.code.aon.accounting.mvel;

import java.text.SimpleDateFormat;


public interface IBalanceConstants {

	String CODE_METHOD = "codigo";
	String DESCRIPTION_METHOD = "descripcion";
	String BALANCE_METHOD = "saldo";
	String NOTES_METHOD = "notas";
	String PERIODO_METHOD = "periodo";
	String INICIO_PERIODO_METHOD = "inicioPeriodo";
	String FIN_PERIODO_METHOD = "finPeriodo";
	String XML_ITEM_METHOD = "xmlItem";
	
	String PERIOD_FOMATTER_PATTERN = "yyyy-MM-dd";
	SimpleDateFormat PERIOD_FOMATTER = new SimpleDateFormat( PERIOD_FOMATTER_PATTERN );
	
	String MODULE  = "module";
	String MODULE_ID = "moduleId";
	String TEMPLATE = "template";
	
	String NUM_PERIODS = "ejercicios";
	String PERIOD = "ejercicio";
	String LOCALE = "locale";
	String REPORT_DATE = "reportDate";
	
	String COMPANY_NAME = "nombreEmpresa";
	String COMPANY_DOCUMENT = "nifEmpresa";
	String COMPANY_ADDRESS = "direccionEmpresa";
	
	String PERIOD_START = "inicioPeriodo_";
	String PERIOD_END = "finPeriodo_";
	
	String XML_ITEM = "<item id=\"%d\" sign=\"%c\"><value>%d</value></item>";
	
	
}
