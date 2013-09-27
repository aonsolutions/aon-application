package com.esferalia.aon.payroll.enumeration.certificados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.esferalia.aon.payroll.sepe.SEPECodeTablesWriter.ISepeEnum;
/** 
 * Enumeration for represent Certific@2 (S.E.P.E.) table codes.
 * Generation main class: com.esferalia.aon.payroll.sepe.CertificadosCodeTablesWriter
 */ 
public enum CertificadosCodeTables implements ISepeEnum {

	T_DCODEDTC( "DCODEDTC", "",null),
	T_DCSPCPTC( "DCSPCPTC", "",null),
	T_DGRCOTTC( "DGRCOTTC", "Grupo de cotización",null),
	T_DSTEMCTC( "DSTEMCTC", "",null),
	T_SACECOTC( "SACECOTC", "",null),
	T_TAICLAOC( "TAICLAOC", "Códigos de ocupación, profesiones",null),
	T_TCGPROVI( "TCGPROVI", "",null),
	T_TCHRGCOT( "TCHRGCOT", "",null),
	T_Terrores( "Terrores", "Códigos de errores",null),
	T_TKCSITEM( "TKCSITEM", "",null),
	T_TKDIASAC( "TKDIASAC", "",null),
	T_TKEINDUC( "TKEINDUC", "Indicador duración del contrato",null),
	T_TKFCOEFI( "TKFCOEFI", "",null),
	T_TKZCARPS( "TKZCARPS", "Cargos públicos o sindicales",null),
	T_TLDCAUSS( "TLDCAUSS", "Códigos de causas de suspensión o extinción",null),
	T_TMJMINSS( "TMJMINSS", "Códigos de la mineria del carbón",null),
	T_TMPORCRD( "TMPORCRD", "Causas de porcentaje de reducción de jornada",null),
	T_TMQTDIST( "TMQTDIST", "Distribución de jornadas (regular o irregular) para los contratos a tiempo parcial",null),
	T_TNWTPCOM( "TNWTPCOM", "",null),
	;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private String code;
	private String description;
	private String lastUpdateDate;

	CertificadosCodeTables( String code, String description, String lastUpdateDate) {
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

	public boolean isActive(){
		return true;
	}

	public static CertificadosCodeTables getEnumByValue(String expression) {
		for( CertificadosCodeTables o : CertificadosCodeTables.values() ) {
			if ( o.getCode().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}