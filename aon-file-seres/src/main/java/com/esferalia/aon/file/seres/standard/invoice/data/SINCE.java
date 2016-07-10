package com.esferalia.aon.file.seres.standard.invoice.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCE entity.
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
 * 		 <td>SINCE</td> <td>Descuentos y cargos línea de detalle</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCE {

	private Integer numeroDeDescuento_Cargo;
	private String indicadorDeDescuento_Cargo;
	private String indicadorSecuenciaDeCalculo;
	private Double porcentajeDescuento_Cargo;
	private Double importeDescuento_Cargo;
	private Double importeTotalSujetoAAplicacion;
	private Double cantidadDeUnidadesQueSeDescuentanPorLinea;
	private String tipoDescuento;
	private Double descuentosMonetariosPorUnidad;
	private String unidadDeMedida;
	private String descripcionDescuento_Cargo;


	private static Pattern PATTERN_SINCE_numeroDeDescuento_Cargo = Pattern.compile("^.{6}(.{2}).*");
	private static Pattern PATTERN_SINCE_indicadorDeDescuento_Cargo = Pattern.compile("^.{8}(.{1}).*");
	private static Pattern PATTERN_SINCE_indicadorSecuenciaDeCalculo = Pattern.compile("^.{9}(.{3}).*");
	private static Pattern PATTERN_SINCE_porcentajeDescuento_Cargo = Pattern.compile("^.{12}(.{9}).*");
	private static Pattern PATTERN_SINCE_importeDescuento_Cargo = Pattern.compile("^.{21}(.{18}).*");
	private static Pattern PATTERN_SINCE_importeTotalSujetoAAplicacion = Pattern.compile("^.{39}(.{18}).*");
	private static Pattern PATTERN_SINCE_cantidadDeUnidadesQueSeDescuentanPorLinea = Pattern.compile("^.{57}(.{16}).*");
	private static Pattern PATTERN_SINCE_tipoDescuento = Pattern.compile("^.{73}(.{6}).*");
	private static Pattern PATTERN_SINCE_descuentosMonetariosPorUnidad = Pattern.compile("^.{79}(.{19}).*");
	private static Pattern PATTERN_SINCE_unidadDeMedida = Pattern.compile("^.{98}(.{6}).*");
	private static Pattern PATTERN_SINCE_descripcionDescuento_Cargo = Pattern.compile("^.{104}(.{35}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCE_numeroDeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeDescuento_Cargo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_indicadorDeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorDeDescuento_Cargo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_indicadorSecuenciaDeCalculo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorSecuenciaDeCalculo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_porcentajeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeDescuento_Cargo(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_importeDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteDescuento_Cargo(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_importeTotalSujetoAAplicacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalSujetoAAplicacion(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_cantidadDeUnidadesQueSeDescuentanPorLinea.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadDeUnidadesQueSeDescuentanPorLinea(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_tipoDescuento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDescuento(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_descuentosMonetariosPorUnidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescuentosMonetariosPorUnidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_unidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_descripcionDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDescuento_Cargo(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Número de Descuento o Cargo: Es un campo contador. Se sumará 1 por cada descuento/ cargo aplicado sobre una misma línea. El valor inicial por cada línea es '1'.
	 */ 
	public Integer getNumeroDeDescuento_Cargo() {
		return numeroDeDescuento_Cargo;
	}

	/** 
	 * 2 - Número de Descuento o Cargo: Es un campo contador. Se sumará 1 por cada descuento/ cargo aplicado sobre una misma línea. El valor inicial por cada línea es '1'.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número de Descuento/Cargo</td> <td>N</td> <td>2</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeDescuento_Cargo(Integer numeroDeDescuento_Cargo) {
		this.numeroDeDescuento_Cargo = numeroDeDescuento_Cargo;
	}

	/** 
	 * 3 - Indicador de Descuento/Cargo: puede ser 'A' (Descuento) o 'C' (Cargo).
	 */ 
	public String getIndicadorDeDescuento_Cargo() {
		return indicadorDeDescuento_Cargo;
	}

	/** 
	 * 3 - Indicador de Descuento/Cargo: puede ser 'A' (Descuento) o 'C' (Cargo).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Indicador de Descuento/Cargo</td> <td>C</td> <td>1</td> <td>9</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIndicadorDeDescuento_Cargo(String indicadorDeDescuento_Cargo) {
		this.indicadorDeDescuento_Cargo = indicadorDeDescuento_Cargo;
	}

	/** 
	 * 4 - Indicador de Secuencia de Cálculo: Se utiliza para aplicar descuentos en cascada. Tendrá valor '1 'si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2 'y así sucesivamente.
	 */ 
	public String getIndicadorSecuenciaDeCalculo() {
		return indicadorSecuenciaDeCalculo;
	}

	/** 
	 * 4 - Indicador de Secuencia de Cálculo: Se utiliza para aplicar descuentos en cascada. Tendrá valor '1 'si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2 'y así sucesivamente.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Indicador secuencia de calculo</td> <td>C</td> <td>3</td> <td>10</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIndicadorSecuenciaDeCalculo(String indicadorSecuenciaDeCalculo) {
		this.indicadorSecuenciaDeCalculo = indicadorSecuenciaDeCalculo;
	}

	/** 
	 * 5 y 6 - Porcentaje o Importe de Descuento/Cargo: Uno de los dos campos debe ser cumplimentado (5 o 6). Es aconsejable cumplimentar los dos.
	 */ 
	public Double getPorcentajeDescuento_Cargo() {
		return porcentajeDescuento_Cargo;
	}

	/** 
	 * 5 y 6 - Porcentaje o Importe de Descuento/Cargo: Uno de los dos campos debe ser cumplimentado (5 o 6). Es aconsejable cumplimentar los dos.
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
	 * 5 y 6 - Porcentaje o Importe de Descuento/Cargo: Uno de los dos campos debe ser cumplimentado (5 o 6). Es aconsejable cumplimentar los dos.
	 */ 
	public Double getImporteDescuento_Cargo() {
		return importeDescuento_Cargo;
	}

	/** 
	 * 5 y 6 - Porcentaje o Importe de Descuento/Cargo: Uno de los dos campos debe ser cumplimentado (5 o 6). Es aconsejable cumplimentar los dos.
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
	public Double getImporteTotalSujetoAAplicacion() {
		return importeTotalSujetoAAplicacion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Importe Total Sujeto a Aplicación</td> <td>N(14,3)</td> <td>18</td> <td>40</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalSujetoAAplicacion(Double importeTotalSujetoAAplicacion) {
		this.importeTotalSujetoAAplicacion = importeTotalSujetoAAplicacion;
	}

	/** 
	 * 8 - Cantidad de Unidades que se descuentan por línea: Solo se cumplimentará a nivel de Línea si se hace una bonificación sobre el mismo artículo que se factura. Se especificará en la misma unidad de medida que en la Línea.
	 */ 
	public Double getCantidadDeUnidadesQueSeDescuentanPorLinea() {
		return cantidadDeUnidadesQueSeDescuentanPorLinea;
	}

	/** 
	 * 8 - Cantidad de Unidades que se descuentan por línea: Solo se cumplimentará a nivel de Línea si se hace una bonificación sobre el mismo artículo que se factura. Se especificará en la misma unidad de medida que en la Línea.
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
	 * 
	 */ 
	public String getTipoDescuento() {
		return tipoDescuento;
	}

	/** 
	 * 
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
	public String getDescripcionDescuento_Cargo() {
		return descripcionDescuento_Cargo;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Descripción Descuento/Cargo</td> <td>C</td> <td>35</td> <td>105</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescripcionDescuento_Cargo(String descripcionDescuento_Cargo) {
		this.descripcionDescuento_Cargo = descripcionDescuento_Cargo;
	}

}