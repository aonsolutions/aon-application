package com.esferalia.aon.file.seres.standard.invoice.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCD entity.
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
 * 		 <td>SINCD</td> <td>Descuentos y cargos cabecera</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCD {

	private Integer numeroDescuento_Cargo;
	private String indicadorDescuento_Cargo_A_C_;
	private String indicadorSecuenciaDeCalculo;
	private Double porcentajeDescuento_Cargo;
	private Double importeDescuento_Cargo;
	private Double importeTotalSujetoAAplicacion_13_;
	private Double cantidadDeUnidadesQueSeDescuentanPorLinea;
	private String tipoDescuento;
	private Double descuentosMonetariosPorUnidad;
	private String unidadDeMedida;
	private String descripcionDeDescuento_Cargo;


	private static Pattern PATTERN_SINCD_numeroDescuento_Cargo = Pattern.compile("^.{6}(.{2}).*");
	private static Pattern PATTERN_SINCD_indicadorDescuento_Cargo_A_C_ = Pattern.compile("^.{8}(.{1}).*");
	private static Pattern PATTERN_SINCD_indicadorSecuenciaDeCalculo = Pattern.compile("^.{9}(.{3}).*");
	private static Pattern PATTERN_SINCD_porcentajeDescuento_Cargo = Pattern.compile("^.{12}(.{9}).*");
	private static Pattern PATTERN_SINCD_importeDescuento_Cargo = Pattern.compile("^.{21}(.{18}).*");
	private static Pattern PATTERN_SINCD_importeTotalSujetoAAplicacion_13_ = Pattern.compile("^.{39}(.{18}).*");
	private static Pattern PATTERN_SINCD_cantidadDeUnidadesQueSeDescuentanPorLinea = Pattern.compile("^.{57}(.{16}).*");
	private static Pattern PATTERN_SINCD_tipoDescuento = Pattern.compile("^.{73}(.{6}).*");
	private static Pattern PATTERN_SINCD_descuentosMonetariosPorUnidad = Pattern.compile("^.{79}(.{19}).*");
	private static Pattern PATTERN_SINCD_unidadDeMedida = Pattern.compile("^.{98}(.{6}).*");
	private static Pattern PATTERN_SINCD_descripcionDeDescuento_Cargo = Pattern.compile("^.{104}(.{35}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCD_numeroDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDescuento_Cargo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_indicadorDescuento_Cargo_A_C_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorDescuento_Cargo_A_C_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_indicadorSecuenciaDeCalculo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorSecuenciaDeCalculo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_porcentajeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeDescuento_Cargo(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_importeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteDescuento_Cargo(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_importeTotalSujetoAAplicacion_13_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalSujetoAAplicacion_13_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_cantidadDeUnidadesQueSeDescuentanPorLinea.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadDeUnidadesQueSeDescuentanPorLinea(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_tipoDescuento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDescuento(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_descuentosMonetariosPorUnidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescuentosMonetariosPorUnidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_unidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_descripcionDeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDeDescuento_Cargo(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Número de Descuento/Cargo: Es un campo contador. Se sumará 1 por cada descuento dentro de una misma factura. El valor inicial por cada factura es '1'.
	 */ 
	public Integer getNumeroDescuento_Cargo() {
		return numeroDescuento_Cargo;
	}

	/** 
	 * 2 - Número de Descuento/Cargo: Es un campo contador. Se sumará 1 por cada descuento dentro de una misma factura. El valor inicial por cada factura es '1'.
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
	 * 3 - Indicador de Descuento/Cargo: puede ser 'A' (Descuento) o 'C' (Cargo).
	 */ 
	public String getIndicadorDescuento_Cargo_A_C_() {
		return indicadorDescuento_Cargo_A_C_;
	}

	/** 
	 * 3 - Indicador de Descuento/Cargo: puede ser 'A' (Descuento) o 'C' (Cargo).
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
	 * 4 - Indicador de Secuencia de Cálculo: Se utiliza para aplicar descuentos en cascada. Tendrá valor '1' si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2' y así sucesivamente.
	 */ 
	public String getIndicadorSecuenciaDeCalculo() {
		return indicadorSecuenciaDeCalculo;
	}

	/** 
	 * 4 - Indicador de Secuencia de Cálculo: Se utiliza para aplicar descuentos en cascada. Tendrá valor '1' si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2' y así sucesivamente.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Indicador Secuencia de cálculo</td> <td>C</td> <td>3</td> <td>10</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIndicadorSecuenciaDeCalculo(String indicadorSecuenciaDeCalculo) {
		this.indicadorSecuenciaDeCalculo = indicadorSecuenciaDeCalculo;
	}

	/** 
	 * 5 y 6 - Porcentaje de Descuento/Cargo - Importe de Descuento/Cargo: Uno de los dos campos debe ser cumplimentado. Es aconsejable cumplimentar los dos.
	 */ 
	public Double getPorcentajeDescuento_Cargo() {
		return porcentajeDescuento_Cargo;
	}

	/** 
	 * 5 y 6 - Porcentaje de Descuento/Cargo - Importe de Descuento/Cargo: Uno de los dos campos debe ser cumplimentado. Es aconsejable cumplimentar los dos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Porcentaje Descuento/Cargo</td> <td>N(4,4)</td> <td>9</td> <td>13</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeDescuento_Cargo(Double porcentajeDescuento_Cargo) {
		this.porcentajeDescuento_Cargo = porcentajeDescuento_Cargo;
	}

	/** 
	 * 5 y 6 - Porcentaje de Descuento/Cargo - Importe de Descuento/Cargo: Uno de los dos campos debe ser cumplimentado. Es aconsejable cumplimentar los dos.
	 */ 
	public Double getImporteDescuento_Cargo() {
		return importeDescuento_Cargo;
	}

	/** 
	 * 5 y 6 - Porcentaje de Descuento/Cargo - Importe de Descuento/Cargo: Uno de los dos campos debe ser cumplimentado. Es aconsejable cumplimentar los dos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Importe Descuento/Cargo</td> <td>N(14,3)</td> <td>18</td> <td>22</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteDescuento_Cargo(Double importeDescuento_Cargo) {
		this.importeDescuento_Cargo = importeDescuento_Cargo;
	}

	/** 
	 * 
	 */ 
	public Double getImporteTotalSujetoAAplicacion_13_() {
		return importeTotalSujetoAAplicacion_13_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Importe Total Sujeto a Aplicación (13)</td> <td>N(14,3)</td> <td>18</td> <td>40</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalSujetoAAplicacion_13_(Double importeTotalSujetoAAplicacion_13_) {
		this.importeTotalSujetoAAplicacion_13_ = importeTotalSujetoAAplicacion_13_;
	}

	/** 
	 * 8 - Cantidad de Unidades que se descuentan por línea: Campo no aplicable a nivel de factura
	 */ 
	public Double getCantidadDeUnidadesQueSeDescuentanPorLinea() {
		return cantidadDeUnidadesQueSeDescuentanPorLinea;
	}

	/** 
	 * 8 - Cantidad de Unidades que se descuentan por línea: Campo no aplicable a nivel de factura
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Cantidad de Unidades que se descuentan por Línea</td> <td>N(12,3)</td> <td>16</td> <td>58</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadDeUnidadesQueSeDescuentanPorLinea(Double cantidadDeUnidadesQueSeDescuentanPorLinea) {
		this.cantidadDeUnidadesQueSeDescuentanPorLinea = cantidadDeUnidadesQueSeDescuentanPorLinea;
	}

	/** 
	 * 9 - Tipo de Descuento: Solo se cumplimentará a nivel de Cabecera. Los valores posibles son:
	 */ 
	public String getTipoDescuento() {
		return tipoDescuento;
	}

	/** 
	 * 9 - Tipo de Descuento: Solo se cumplimentará a nivel de Cabecera. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Tipo Descuento</td> <td>C</td> <td>6</td> <td>74</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDescuento(String tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}

	/** 
	 * 
	 */ 
	public Double getDescuentosMonetariosPorUnidad() {
		return descuentosMonetariosPorUnidad;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Descuentos Monetarios por Unidad</td> <td>N(15,3)</td> <td>19</td> <td>80</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescuentosMonetariosPorUnidad(Double descuentosMonetariosPorUnidad) {
		this.descuentosMonetariosPorUnidad = descuentosMonetariosPorUnidad;
	}

	/** 
	 * 
	 */ 
	public String getUnidadDeMedida() {
		return unidadDeMedida;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Unidad de Medida</td> <td>C</td> <td>6</td> <td>99</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedida(String unidadDeMedida) {
		this.unidadDeMedida = unidadDeMedida;
	}

	/** 
	 * 
	 */ 
	public String getDescripcionDeDescuento_Cargo() {
		return descripcionDeDescuento_Cargo;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Descripción de Descuento/Cargo</td> <td>C</td> <td>35</td> <td>105</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescripcionDeDescuento_Cargo(String descripcionDeDescuento_Cargo) {
		this.descripcionDeDescuento_Cargo = descripcionDeDescuento_Cargo;
	}

	/** 
	 * 9 - Tipo de Descuento: Solo se cumplimentará a nivel de Cabecera. Los valores posibles son:
	 */
	public enum SINCD_9 {
		DTO__PRONTO_PAG_EAB("EAB"),
		DTO__COMERCIA_TD("TD"),
		CARGO_POR_FLETE_FC("FC"),
		CARGO_POR_EMBALAJE_PC("PC"),
		CARGO_POR_MONTAJE_SH("SH"),
		;
		
		private String value;
		
		private SINCD_9(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCD_9 enumByValue(String value) {
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