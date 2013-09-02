package com.esferalia.aon.payroll.contrata.enumeration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Enumeration for represent Contrat@ (S.E.P.E.) table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter
 */ 
public enum ContrataCodeTables {

	T_STDIDETC( "STDIDETC", "TIPO DE DOCUMENTO IDENTIFICATIVO", "" ),
	T_TABCCNAE( "TABCCNAE", "ACTIVIDAD ECONÓMICA", "15-03-2009" ),
	T_TAICLAOC1994( "TAICLAOC1994", "OCUPACIÓN ( Hasta 2011 )", "" ),
	T_TAICLAOC2011( "TAICLAOC2011", "OCUPACIÓN ( A partir de 2011 )", "29-02-2012" ),
	T_TAPCOPOS( "TAPCOPOS", "CODIGOS POSTALES", "17-02-2012" ),
	T_TAUCOMAU( "TAUCOMAU", "COMUNIDAD AUTÓNOMA", "" ),
	T_TBONVFOR( "TBONVFOR", "NIVEL FORMATIVO", "29-02-2012" ),
	T_TBXCPAIS( "TBXCPAIS", "PAÍS", "" ),
	T_TCHRGCOT( "TCHRGCOT", "RÉGIMEN DE COTIZACIÓN", "10-01-2012" ),
	T_TCMCSEXO( "TCMCSEXO", "SEXO", "" ),
	T_TCGPROVI( "TCGPROVI", "PROVINCIA", "" ),
	T_TDPMUNIC( "TDPMUNIC", "MUNICIPIOS", "" ),
	T_TDTVINFO( "*TDTVINFO", "VINCULACIÓN FORMATIVA", "17-04-2013" ),
	T_TEHTPCTO( "*TEHTPCTO", "CÓDIGOS DE CONTRATO", "07-02-2013" ),
	T_TEIINTER( "*TEIINTER", "CAUSA OBJETO DE LA INTERINIDAD", "" ),
	T_TEJINDIS( "TEJINDIS", "INDICADOR DISCAPACIDAD", "" ),
	T_TEKLEYBO( "*TEKLEYBO", "LEY BONIFICACIÓN", "17-04-2013" ),
	T_TELCOLBO( "*TELCOLBO", "COLECTIVO BONIFICACIÓN", "17-04-2013" ),
	T_TENLEYDE( "*TENLEYDE", "LEY FOMENTO DE LA CONTR. INDEFINIDA", "29-02-2012" ),
	T_TEOCOLDE( "*TEOCOLDE", "COLECTIVO FOMENTO DE LA CONTR. INDEFINIDA", "29-02-2012" ),
	T_TEQPTIEM( "TEQPTIEM", "PERÍODO DE TIEMPO", "" ),
	T_TERFIRCB( "TERFIRCB", "FIRMA COPIA BÁSICA", "" ),
	T_TESCETCO( "*TESCETCO", "RELACIÓN CONTRACTUAL ET / CO / TE", "" ),
	T_TETPGMEM( "TETPGMEM", "PROGRAMA DE EMPLEO", "" ),
	T_TEUECCLL( "TEUECCLL", "CORPORACIONES LOCALES", "" ),
	T_TEVACTCL( "TEVACTCL", "ACTUACIONES DE CORPORACIONES LOCALES", "" ),
	T_TEWEINVE( "TEWEINVE", "TIPO DE EMPLEADOR INVESTIGACIÓN", "10-01-2012" ),
	T_TEXTINVE( "*TEXTINVE", "TIPO DE TRABAJADOR INVESTIGACIÓN", "" ),
	T_TEYTRELE( "TEYTRELE", "TIPO DE TRABAJADOR DE RELEVO", "" ),
	T_TFGGRCOT( "*TFGGRCOT", "GRUPOS DE COTIZACION", "15-01-2013" ),
	T_THITIACA( "THITIACA", "TITULACIÓN ACADÉMICA", "15-01-2013" ),
	T_THPCOLFO( "*THPCOLFO", "COLECTIVOS CONTRATOS DE FORMACIÓN", "26-07-2012" ),
	T_THYDISLE( "*THYDISLE", "DISPOSICIONES LEGALES", "" ),
	T_TQOCOLRE( "*TQOCOLRE", "COLECTIVOS DE REDUCCIÓN", "17-04-2013" ),
	T_TQNLEYRE( "*TQNLEYRE", "LEYES DE REDUCCIÓN", "17-04-2013" ),
	T_TRCMODFO ( "*TRCMODFO", "MODALIDAD DE FORMACION", "22-03-2012" ),
	T_TRDACTFO( "*TRDACTFO", "ACTIVIDAD FORMATIVA", "15-01-2013" ),
	T_TRXLEYDF( "*TRXLEYDF", "LEY DE DEDUCCION FISCAL", "26-07-2012" ),
	T_TRWCOLDF( "*TRWCOLDF", "COLECTIVO DE DEDUCCION FISCAL", "29-02-2012" ),
	T_TSATPCEN( "*TSATPCEN", "TIPOS DE CENTRO", "22-03-2012" ),

	T_TRespuestaXML50( "TERRORES", "Errores de la comunicacion",null)
	;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private String code;
	private String description;
	private String lastUpdateDate;

	ContrataCodeTables( String code, String description, String lastUpdateDate) {
		this.code = code;
		this.description = description;
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Date getLastUpdateDate(){
		try {
			if(lastUpdateDate!=null){
				return sdf.parse(lastUpdateDate);
			}
		} catch (ParseException e) {
			// nothing to do
		}
	return null;
	}

	public static ContrataCodeTables getEnumByValue(String expression) {
		for( ContrataCodeTables o : ContrataCodeTables.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}