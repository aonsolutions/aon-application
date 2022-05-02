package com.esferalia.aon.file.seres.connect2.salesresponse.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ORSPE {
	private Integer numeroDescuento_Cargo;
	private String indicadorDescuento_Cargo_A_C_;
	private String indicadorSecuenciaDeCalculo;
	private String tipoDescuento_Cargo;
	private String descripcionDescuento_Cargo;
	private Double porcentajeDescuento_Cargo;
	private Double importeDescuento_Cargo;
	private Double importeTotalSujetoAAplicacion_13_;
	private Double descuentosMonetariosPorUnidad;
	private String unidadDeMedida;
	private Double cantidadDescuento_Cargo;
	
	private static Pattern PATTERN_ORSPG_numeroDescuento_Cargo = Pattern.compile("^.{6}(.{2}).*");
	private static Pattern PATTERN_ORSPG_indicadorDescuento_Cargo_A_C_ = Pattern.compile("^.{8}(.{1}).*");
	private static Pattern PATTERN_ORSPG_indicadorSecuenciaDeCalculo = Pattern.compile("^.{9}(.{3}).*");
	private static Pattern PATTERN_ORSPG_tipoDescuento_Cargo = Pattern.compile("^.{12}(.{3}).*");
	private static Pattern PATTERN_ORSPG_descripcionDescuento_Cargo = Pattern.compile("^.{15}(.{70}).*");
	private static Pattern PATTERN_ORSPG_porcentajeDescuento_Cargo = Pattern.compile("^.{85}(.{9}).*");
	private static Pattern PATTERN_ORSPG_importeDescuento_Cargo = Pattern.compile("^.{94}(.{18}).*");
	private static Pattern PATTERN_ORSPG_importeTotalSujetoAAplicacion_13_ = Pattern.compile("^.{112}(.{18}).*");
	private static Pattern PATTERN_ORSPG_descuentosMonetariosPorUnidad = Pattern.compile("^.{130}(.{18}).*");
	private static Pattern PATTERN_ORSPG_unidadDeMedida = Pattern.compile("^.{148}(.{6}).*");
	private static Pattern PATTERN_ORSPG_cantidadDescuento_Cargo = Pattern.compile("^.{154}(.{16}).*");
	
	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ORSPG_numeroDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDescuento_Cargo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_indicadorDescuento_Cargo_A_C_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorDescuento_Cargo_A_C_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_indicadorSecuenciaDeCalculo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorSecuenciaDeCalculo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_tipoDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDescuento_Cargo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_descripcionDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDescuento_Cargo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_porcentajeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeDescuento_Cargo(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_importeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteDescuento_Cargo(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_importeTotalSujetoAAplicacion_13_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalSujetoAAplicacion_13_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_descuentosMonetariosPorUnidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescuentosMonetariosPorUnidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_unidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPG_cantidadDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadDescuento_Cargo(Double.valueOf(m.group(1).trim()));
		}
	}

	
	/**
	 * 2 - Número de Descuento/Cargo: Es un campo contador. Se sumará 1 por cada descuento dentro de una misma línea. El valor inicial por cada línea de pedido es '1'.
	 */
	public Integer getNumeroDescuento_Cargo() {
		return numeroDescuento_Cargo;
	}

	/**
	 * 2 - Número de Descuento/Cargo: Es un campo contador. Se sumará 1 por cada descuento dentro de una misma línea. El valor inicial por cada línea de pedido es '1'.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número Descuento/Cargo</td> <td>N</td> <td>2</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroDescuento_Cargo(Integer numeroDescuento_Cargo) {
		this.numeroDescuento_Cargo = numeroDescuento_Cargo;
	}

	/**
	 * 3 - Indicador de Descuento/Cargo: Indica si se trata de un descuento, de un cargo o de ninguno de los dos anteriores. Este campo corresponde al elemento 5463. Los valores posibles son:
	 */
	public String getIndicadorDescuento_Cargo_A_C_() {
		return indicadorDescuento_Cargo_A_C_;
	}


	/**
	 * 3 - Indicador de Descuento/Cargo: Indica si se trata de un descuento, de un cargo o de ninguno de los dos anteriores. Este campo corresponde al elemento 5463. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Indicador Descuento/Cargo (A/C)</td> <td>C</td> <td>1</td> <td>9</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setIndicadorDescuento_Cargo_A_C_(String indicadorDescuento_Cargo_A_C_) {
		this.indicadorDescuento_Cargo_A_C_ = indicadorDescuento_Cargo_A_C_;
	}

	/**
	 * 4 - Indicador de Secuencia de Cálculo: Se utiliza para aplicar descuentos en cascada.
	 *	Tendrá valor "1" si aplicamos el descuento sobre la primera base imponible. Si sobre la base
	 *	resultante se aplica otro descuento se grabaran con valor '2' y así sucesivamente. Este campo
	 *	corresponde al elemento 1227. Los valores posibles son:
	 *	<ul>
	 *		<li> "1" - Primer paso para el cálculo</li>
	 *		<li>"2" - Segundo paso para el cálculo</li>
	 *		<li>"3" - Tercer paso para el cálculo</li>
	 *		<li>n - Etc, etc, etc.......</li>
	 */
	public String getIndicadorSecuenciaDeCalculo() {
		return indicadorSecuenciaDeCalculo;
	}


	/**
	 * 4 - Indicador de Secuencia de Cálculo: Se utiliza para aplicar descuentos en cascada.
	 *	Tendrá valor "1" si aplicamos el descuento sobre la primera base imponible. Si sobre la base
	 *	resultante se aplica otro descuento se grabaran con valor '2' y así sucesivamente. Este campo
	 *	corresponde al elemento 1227. Los valores posibles son:
	 *	<ul>
	 *		<li> "1" - Primer paso para el cálculo</li>
	 *		<li>"2" - Segundo paso para el cálculo</li>
	 *		<li>"3" - Tercer paso para el cálculo</li>
	 *		<li>n - Etc, etc, etc.......</li>
	 *
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Indicador Secuencia de cálculo</td> <td>C</td> <td>3</td> <td>10</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setIndicadorSecuenciaDeCalculo(String indicadorSecuenciaDeCalculo) {
		this.indicadorSecuenciaDeCalculo = indicadorSecuenciaDeCalculo;
	}

	/**
	 * 5 - Tipo Descuento/Cargo: Este campo corresponde al elemento 7161. Los valores posibles son:
	 */
	public String getTipoDescuento_Cargo() {
		return tipoDescuento_Cargo;
	}


	/**
	 * 5 - Tipo Descuento/Cargo: Este campo corresponde al elemento 7161. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Tipo Descuento/Cargo</td> <td>C</td> <td>3</td> <td>13</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTipoDescuento_Cargo(String tipoDescuento_Cargo) {
		this.tipoDescuento_Cargo = tipoDescuento_Cargo;
	}

	/**
	 * 6 - Descripción Descuento/Cargo
	 */
	public String getDescripcionDescuento_Cargo() {
		return descripcionDescuento_Cargo;
	}


	/**
	 * 6 - Descripción Descuento/Cargo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Descripción Descuento/Cargo</td> <td>C</td> <td>70</td> <td>16</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setDescripcionDescuento_Cargo(String descripcionDescuento_Cargo) {
		this.descripcionDescuento_Cargo = descripcionDescuento_Cargo;
	}

	/**
	 * 7 - Porcentaje Descuento/Cargo
	 */
	public Double getPorcentajeDescuento_Cargo() {
		return porcentajeDescuento_Cargo;
	}


	/**
	 * 7 - Porcentaje Descuento/Cargo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Porcentaje Descuento/Cargo</td> <td>N(4,4)</td> <td>9</td> <td>86</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPorcentajeDescuento_Cargo(Double porcentajeDescuento_Cargo) {
		this.porcentajeDescuento_Cargo = porcentajeDescuento_Cargo;
	}

	/**
	 * 8 -Importe Descuento/Cargo
	 */
	public Double getImporteDescuento_Cargo() {
		return importeDescuento_Cargo;
	}


	/**
	 * 8 -Importe Descuento/Cargo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Importe Descuento/Cargo</td> <td>N(14,3)</td> <td>18</td> <td>95</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteDescuento_Cargo(Double importeDescuento_Cargo) {
		this.importeDescuento_Cargo = importeDescuento_Cargo;
	}

	/**
	 * 9 - Importe Total Sujeto a Aplicación (13)
	 */
	public Double getImporteTotalSujetoAAplicacion_13_() {
		return importeTotalSujetoAAplicacion_13_;
	}


	/**
	 * 9 - Importe Total Sujeto a Aplicación (13)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Importe Total Sujeto a Aplicación (13)</td> <td>N(14,3)</td> <td>18</td> <td>113</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteTotalSujetoAAplicacion_13_(Double importeTotalSujetoAAplicacion_13_) {
		this.importeTotalSujetoAAplicacion_13_ = importeTotalSujetoAAplicacion_13_;
	}

	/**
	 * 10 - Descuentos Monetarios por Unidad
	 */
	public Double getDescuentosMonetariosPorUnidad() {
		return descuentosMonetariosPorUnidad;
	}


	/**
	 * 10 - Descuentos Monetarios por Unidad
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Descuentos Monetarios por Unidad</td> <td>N(14,3)</td> <td>18</td> <td>131</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setDescuentosMonetariosPorUnidad(Double descuentosMonetariosPorUnidad) {
		this.descuentosMonetariosPorUnidad = descuentosMonetariosPorUnidad;
	}

	/**
	 * 11 - Unidad de Medida
	 */
	public String getUnidadDeMedida() {
		return unidadDeMedida;
	}


	/**
	 * 11 - Unidad de Medida
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Unidad de Medida</td> <td>C</td> <td>6</td> <td>149</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setUnidadDeMedida(String unidadDeMedida) {
		this.unidadDeMedida = unidadDeMedida;
	}

	/**
	 * 12 - Cantidad Descuento/Cargo
	 */
	public Double getCantidadDescuento_Cargo() {
		return cantidadDescuento_Cargo;
	}


	/**
	 * 12 - Cantidad Descuento/Cargo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Cantidad Descuento/Cargo</td> <td>N(12,3)</td> <td>16</td> <td>155</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCantidadDescuento_Cargo(Double cantidadDescuento_Cargo) {
		this.cantidadDescuento_Cargo = cantidadDescuento_Cargo;
	}



	/** 
	 * 3 - Indicador de Descuento/Cargo: Indica si se trata de un descuento, de un cargo o de ninguno de los dos anteriores. Este campo corresponde al elemento 5463. Los valores posibles son:
	 */
	public enum ORSPU_3 {
		DESCUENTO("A"),
		CARGO("C"),
		NI_DESCUENTO_NI_CARGO("N"),
		;
		
		private String value;
		
		private ORSPU_3(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPU_3 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/**
	 * 5 - Tipo Descuento/Cargo: Este campo corresponde al elemento 7161. Los valores posibles son:
	 */
	public enum ORSPU_5 {
		DTO_PRONTO_PAGO("EAB"),
		DTO_COMERCIAL("TD"),
		CARGO_POR_FLETES("FC"),
		CARGO_POR_EMBALAJES("PC"),
		CARGO_POR_MONTAJES("SH"),
		;
		
		private String value;
		
		private ORSPU_5(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPU_5 enumByValue(String value) {
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