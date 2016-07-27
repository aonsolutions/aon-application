package com.esferalia.aon.file.seres.udapa.sales.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1I entity.
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
 * 		 <td>ERE1I</td> <td>Impuestos</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class ERE1I {

	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private Integer numeroDeLineaImpuesto;
	private String calificadorTipoDeImpuesto;
	private Double porcentajeTipoDeImpuesto;
	private Double importeTipoDeImpuesto;
	private Double baseImponible;


	private static Pattern PATTERN_ERE1I_tipoDePedido_220_221_224_226_22E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ERE1I_numeroDePedido = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_ERE1I_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_ERE1I_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_ERE1I_numeroDeLineaImpuesto = Pattern.compile("^.{63}(.{2}).*");
	private static Pattern PATTERN_ERE1I_calificadorTipoDeImpuesto = Pattern.compile("^.{65}(.{6}).*");
	private static Pattern PATTERN_ERE1I_porcentajeTipoDeImpuesto = Pattern.compile("^.{71}(.{6}).*");
	private static Pattern PATTERN_ERE1I_importeTipoDeImpuesto = Pattern.compile("^.{77}(.{18}).*");
	private static Pattern PATTERN_ERE1I_baseImponible = Pattern.compile("^.{95}(.{18}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1I_tipoDePedido_220_221_224_226_22E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDePedido_220_221_224_226_22E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1I_numeroDePedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1I_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1I_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1I_numeroDeLineaImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaImpuesto(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1I_calificadorTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorTipoDeImpuesto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1I_porcentajeTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1I_importeTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1I_baseImponible.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBaseImponible(Double.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public String getTipoDePedido_220_221_224_226_22E_() {
		return tipoDePedido_220_221_224_226_22E_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1001T</td> <td>Tipo de Pedido (220, 221, 224, 226, 22E)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDePedido_220_221_224_226_22E_(String tipoDePedido_220_221_224_226_22E_) {
		this.tipoDePedido_220_221_224_226_22E_ = tipoDePedido_220_221_224_226_22E_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDePedido() {
		return numeroDePedido;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1004P</td> <td>Número de Pedido</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePedido(String numeroDePedido) {
		this.numeroDePedido = numeroDePedido;
	}

	/** 
	 * 
	 */ 
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039E</td> <td>Código Emisor  (MS)</td> <td>C</td> <td>17</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEmisor_MS_(String codigoEmisor_MS_) {
		this.codigoEmisor_MS_ = codigoEmisor_MS_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039R</td> <td>Código Receptor (MR)</td> <td>C</td> <td>17</td> <td>47</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}

	/** 
	 * C1082I - Número de Línea Impuesto: Es un campo contador. Se sumará 1 por cada tipo de impuesto que se aplique. El valor inicial  es '1'
	 */ 
	public Integer getNumeroDeLineaImpuesto() {
		return numeroDeLineaImpuesto;
	}

	/** 
	 * C1082I - Número de Línea Impuesto: Es un campo contador. Se sumará 1 por cada tipo de impuesto que se aplique. El valor inicial  es '1'
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1082I</td> <td>Número de Línea Impuesto</td> <td>N</td> <td>2</td> <td>64</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaImpuesto(Integer numeroDeLineaImpuesto) {
		this.numeroDeLineaImpuesto = numeroDeLineaImpuesto;
	}

	/** 
	 * C5153T - Calificador Tipo de Impuesto: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCalificadorTipoDeImpuesto() {
		return calificadorTipoDeImpuesto;
	}

	/** 
	 * C5153T - Calificador Tipo de Impuesto: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5153T</td> <td>Calificador Tipo de Impuesto</td> <td>C</td> <td>6</td> <td>66</td> <td>M</td>
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
	 * 		 <td>C5278T</td> <td>% Tipo de Impuesto</td> <td>N(3,2)</td> <td>6</td> <td>72</td> <td>M</td>
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
	 * 		 <td>C5004T</td> <td>Importe Tipo de Impuesto</td> <td>N(14,3)</td> <td>18</td> <td>78</td> <td>C</td>
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
	 * 		 <td>C5004F</td> <td>Base Imponible</td> <td>N(14,3)</td> <td>18</td> <td>96</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setBaseImponible(Double baseImponible) {
		this.baseImponible = baseImponible;
	}

	/** 
	 * C5153T - Calificador Tipo de Impuesto: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum C5153T {
		IV_VAT("VAT"),
		IGI_IGI("IGI"),
		RECARGO_DE_EQUIVALENCI_RE("RE"),
		EXENT_EXT("EXT"),
		;
		
		private String value;
		
		private C5153T(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C5153T enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}