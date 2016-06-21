package com.esferalia.aon.file.seres.udapa.invoice.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCI entity.
 * <br>
 * IMPORTANT: Any changes made to the code will be lost, so...
 * 
 * DO NOT CHANGE THIS!!!!!!!!!!!!!!!!
 * 
 * <table border="1" cellpadding="1" cellspacing="0">
 * 	<tr bgcolor="#CCCCFF"> 
 * 		<th>Tipo de registro</th>
 * 		<th>Descripcion</th>
 * 		<th>Tipo</th>
 * 		<th>Repeticiones</th>
 * 	</tr>
 * 	<tr>
 * 		<td>SINCI</th>
 * 		<td>Impuestos</th>
 * 		<td>Obligatorio</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SINCI {

	private String impuestos;
	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private String numeroDeLineaImpuesto;
	private String calificadorTipoDeImpuesto;
	private String porcentajeTipoDeImpuesto;
	private String importeTipoDeImpuesto;
	private String baseImponible;


	private static java.util.regex.Pattern PATTERN_SINCI_impuestos = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_tipoFactura_325_380_381_383_385_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_numeroDeFactura = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_codigoVendedor_SU_ = java.util.regex.Pattern.compile("^.{29}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_codigoComprador_BY_ = java.util.regex.Pattern.compile("^.{42}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_numeroDeLineaImpuesto = java.util.regex.Pattern.compile("^.{55}(.{2}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_calificadorTipoDeImpuesto = java.util.regex.Pattern.compile("^.{57}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_porcentajeTipoDeImpuesto = java.util.regex.Pattern.compile("^.{63}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_importeTipoDeImpuesto = java.util.regex.Pattern.compile("^.{69}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCI_baseImponible = java.util.regex.Pattern.compile("^.{87}(.{18}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SINCI_impuestos.matcher(value)).find()) {
			setImpuestos(m.group(1));
		}
		if((m = PATTERN_SINCI_tipoFactura_325_380_381_383_385_.matcher(value)).find()) {
			setTipoFactura_325_380_381_383_385_(m.group(1));
		}
		if((m = PATTERN_SINCI_numeroDeFactura.matcher(value)).find()) {
			setNumeroDeFactura(m.group(1));
		}
		if((m = PATTERN_SINCI_codigoVendedor_SU_.matcher(value)).find()) {
			setCodigoVendedor_SU_(m.group(1));
		}
		if((m = PATTERN_SINCI_codigoComprador_BY_.matcher(value)).find()) {
			setCodigoComprador_BY_(m.group(1));
		}
		if((m = PATTERN_SINCI_numeroDeLineaImpuesto.matcher(value)).find()) {
			setNumeroDeLineaImpuesto(m.group(1));
		}
		if((m = PATTERN_SINCI_calificadorTipoDeImpuesto.matcher(value)).find()) {
			setCalificadorTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_SINCI_porcentajeTipoDeImpuesto.matcher(value)).find()) {
			setPorcentajeTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_SINCI_importeTipoDeImpuesto.matcher(value)).find()) {
			setImporteTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_SINCI_baseImponible.matcher(value)).find()) {
			setBaseImponible(m.group(1));
		}
	}


	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>SINCI</th>
	 * 		<td>Impuestos</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImpuestos() {
		return impuestos;
	}
	public void setImpuestos(String impuestos) {
		this.impuestos = impuestos;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F1001T</th>
	 * 		<td>Tipo Factura (325, 380, 381, 383, 385)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoFactura_325_380_381_383_385_() {
		return tipoFactura_325_380_381_383_385_;
	}
	public void setTipoFactura_325_380_381_383_385_(String tipoFactura_325_380_381_383_385_) {
		this.tipoFactura_325_380_381_383_385_ = tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F1004N</th>
	 * 		<td>Número de Factura</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeFactura() {
		return numeroDeFactura;
	}
	public void setNumeroDeFactura(String numeroDeFactura) {
		this.numeroDeFactura = numeroDeFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F3039V</th>
	 * 		<td>Código Vendedor (SU)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>30</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoVendedor_SU_() {
		return codigoVendedor_SU_;
	}
	public void setCodigoVendedor_SU_(String codigoVendedor_SU_) {
		this.codigoVendedor_SU_ = codigoVendedor_SU_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F3039C</th>
	 * 		<td>Código Comprador (BY)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>43</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoComprador_BY_() {
		return codigoComprador_BY_;
	}
	public void setCodigoComprador_BY_(String codigoComprador_BY_) {
		this.codigoComprador_BY_ = codigoComprador_BY_;
	}

	/** 
	 * F1082I - Número de  Línea de Impuesto:  Es un campo contador. Se sumará 1 por cada tipo de impuesto que se aplique. El valor inicial  es '1'
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F1082I</th>
	 * 		<td>Número de Línea Impuesto</th>
	 * 		<td>N</th>
	 * 		<td>2</th>
	 * 		<td>56</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeLineaImpuesto() {
		return numeroDeLineaImpuesto;
	}
	public void setNumeroDeLineaImpuesto(String numeroDeLineaImpuesto) {
		this.numeroDeLineaImpuesto = numeroDeLineaImpuesto;
	}

	/** 
	 * F5153T - Calificador del tipo de Impuesto: El campo corresponde a un código interno que GAP convertirá a código EANCOM. Si no se utiliza nomenclatura interna puede moverse a este campo directamente el calificador EDI,  los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F5153T</th>
	 * 		<td>Calificador Tipo de Impuesto</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>58</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorTipoDeImpuesto() {
		return calificadorTipoDeImpuesto;
	}
	public void setCalificadorTipoDeImpuesto(String calificadorTipoDeImpuesto) {
		this.calificadorTipoDeImpuesto = calificadorTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F5278T</th>
	 * 		<td>% Tipo de Impuesto</th>
	 * 		<td>N(3,2)</th>
	 * 		<td>6</th>
	 * 		<td>64</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPorcentajeTipoDeImpuesto() {
		return porcentajeTipoDeImpuesto;
	}
	public void setPorcentajeTipoDeImpuesto(String porcentajeTipoDeImpuesto) {
		this.porcentajeTipoDeImpuesto = porcentajeTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F5004T</th>
	 * 		<td>Importe Tipo de Impuesto</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>70</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTipoDeImpuesto() {
		return importeTipoDeImpuesto;
	}
	public void setImporteTipoDeImpuesto(String importeTipoDeImpuesto) {
		this.importeTipoDeImpuesto = importeTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		<th>Campo</th>
	 * 		<th>Descripcion</th>
	 * 		<th>Tipo</th>
	 * 		<th>Longitud</th>
	 * 		<th>Pos. Inicial</th>
	 * 		<th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>F5004U</th>
	 * 		<td>Base Imponible</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>88</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getBaseImponible() {
		return baseImponible;
	}
	public void setBaseImponible(String baseImponible) {
		this.baseImponible = baseImponible;
	}

	public enum CalificadorDelTipoDeImpuesto {
		IV_VAT("VAT"),
		IGI_IGI("IGI"),
		RECARGO_DE_EQUIVALENCI_RE("RE"),
		;
		
		private String value;
		
		private CalificadorDelTipoDeImpuesto(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}