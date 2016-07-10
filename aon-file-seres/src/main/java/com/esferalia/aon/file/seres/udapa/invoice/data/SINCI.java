package com.esferalia.aon.file.seres.udapa.invoice.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCI entity.
 * <br>
 * IMPORTANT: Any changes made to the code will be lost, so...
 * 
 * DO NOT CHANGE THIS!!!!!!!!!!!!!!!!
 * 
 * <table border="1" cellpadding="1" cellspacing="0">
 * 	<tr bgcolor="#CCCCFF"> 
 * 		 <th>Tipo de registro</th> <th>Descripcion</th> <th>Tipo</th> <th>Repeticiones</th>
 * 	</tr>
 * 	<tr>
 * 		 <td>SINCI</td> <td>Impuestos</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCI {

	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private Integer numeroDeLineaImpuesto;
	private String calificadorTipoDeImpuesto;
	private Double porcentajeTipoDeImpuesto;
	private Double importeTipoDeImpuesto;
	private Double baseImponible;


	private static Pattern PATTERN_SINCI_tipoFactura_325_380_381_383_385_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCI_numeroDeFactura = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SINCI_codigoVendedor_SU_ = Pattern.compile("^.{29}(.{13}).*");
	private static Pattern PATTERN_SINCI_codigoComprador_BY_ = Pattern.compile("^.{42}(.{13}).*");
	private static Pattern PATTERN_SINCI_numeroDeLineaImpuesto = Pattern.compile("^.{55}(.{2}).*");
	private static Pattern PATTERN_SINCI_calificadorTipoDeImpuesto = Pattern.compile("^.{57}(.{6}).*");
	private static Pattern PATTERN_SINCI_porcentajeTipoDeImpuesto = Pattern.compile("^.{63}(.{6}).*");
	private static Pattern PATTERN_SINCI_importeTipoDeImpuesto = Pattern.compile("^.{69}(.{18}).*");
	private static Pattern PATTERN_SINCI_baseImponible = Pattern.compile("^.{87}(.{18}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCI_tipoFactura_325_380_381_383_385_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoFactura_325_380_381_383_385_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCI_numeroDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCI_codigoVendedor_SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVendedor_SU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCI_codigoComprador_BY_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoComprador_BY_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCI_numeroDeLineaImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaImpuesto(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCI_calificadorTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorTipoDeImpuesto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCI_porcentajeTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCI_importeTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCI_baseImponible.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBaseImponible(Double.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public String getTipoFactura_325_380_381_383_385_() {
		return tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1001T</td> <td>Tipo Factura (325, 380, 381, 383, 385)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoFactura_325_380_381_383_385_(String tipoFactura_325_380_381_383_385_) {
		this.tipoFactura_325_380_381_383_385_ = tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeFactura() {
		return numeroDeFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1004N</td> <td>Número de Factura</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeFactura(String numeroDeFactura) {
		this.numeroDeFactura = numeroDeFactura;
	}

	/** 
	 * 
	 */ 
	public String getCodigoVendedor_SU_() {
		return codigoVendedor_SU_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039V</td> <td>Código Vendedor (SU)</td> <td>C</td> <td>13</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoVendedor_SU_(String codigoVendedor_SU_) {
		this.codigoVendedor_SU_ = codigoVendedor_SU_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoComprador_BY_() {
		return codigoComprador_BY_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039C</td> <td>Código Comprador (BY)</td> <td>C</td> <td>13</td> <td>43</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoComprador_BY_(String codigoComprador_BY_) {
		this.codigoComprador_BY_ = codigoComprador_BY_;
	}

	/** 
	 * F1082I - Número de  Línea de Impuesto:  Es un campo contador. Se sumará 1 por cada tipo de impuesto que se aplique. El valor inicial  es '1'
	 */ 
	public Integer getNumeroDeLineaImpuesto() {
		return numeroDeLineaImpuesto;
	}

	/** 
	 * F1082I - Número de  Línea de Impuesto:  Es un campo contador. Se sumará 1 por cada tipo de impuesto que se aplique. El valor inicial  es '1'
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1082I</td> <td>Número de Línea Impuesto</td> <td>N</td> <td>2</td> <td>56</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaImpuesto(Integer numeroDeLineaImpuesto) {
		this.numeroDeLineaImpuesto = numeroDeLineaImpuesto;
	}

	/** 
	 * F5153T - Calificador del tipo de Impuesto: El campo corresponde a un código interno que GAP convertirá a código EANCOM. Si no se utiliza nomenclatura interna puede moverse a este campo directamente el calificador EDI,  los valores posibles son:
	 */ 
	public String getCalificadorTipoDeImpuesto() {
		return calificadorTipoDeImpuesto;
	}

	/** 
	 * F5153T - Calificador del tipo de Impuesto: El campo corresponde a un código interno que GAP convertirá a código EANCOM. Si no se utiliza nomenclatura interna puede moverse a este campo directamente el calificador EDI,  los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5153T</td> <td>Calificador Tipo de Impuesto</td> <td>C</td> <td>6</td> <td>58</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorTipoDeImpuesto(String calificadorTipoDeImpuesto) {
		this.calificadorTipoDeImpuesto = calificadorTipoDeImpuesto;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeTipoDeImpuesto() {
		return porcentajeTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5278T</td> <td>% Tipo de Impuesto</td> <td>N(3,2)</td> <td>6</td> <td>64</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeTipoDeImpuesto(Double porcentajeTipoDeImpuesto) {
		this.porcentajeTipoDeImpuesto = porcentajeTipoDeImpuesto;
	}

	/** 
	 * 
	 */ 
	public Double getImporteTipoDeImpuesto() {
		return importeTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004T</td> <td>Importe Tipo de Impuesto</td> <td>N(14,3)</td> <td>18</td> <td>70</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTipoDeImpuesto(Double importeTipoDeImpuesto) {
		this.importeTipoDeImpuesto = importeTipoDeImpuesto;
	}

	/** 
	 * 
	 */ 
	public Double getBaseImponible() {
		return baseImponible;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004U</td> <td>Base Imponible</td> <td>N(14,3)</td> <td>18</td> <td>88</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setBaseImponible(Double baseImponible) {
		this.baseImponible = baseImponible;
	}

	/** 
	 * F5153T - Calificador del tipo de Impuesto: El campo corresponde a un código interno que GAP convertirá a código EANCOM. Si no se utiliza nomenclatura interna puede moverse a este campo directamente el calificador EDI,  los valores posibles son:
	 */
	public enum F5153T {
		IV_VAT("VAT"),
		IGI_IGI("IGI"),
		RECARGO_DE_EQUIVALENCI_RE("RE"),
		;
		
		private String value;
		
		private F5153T(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F5153T enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}