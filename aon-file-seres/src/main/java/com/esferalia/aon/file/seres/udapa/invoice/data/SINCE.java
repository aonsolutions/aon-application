package com.esferalia.aon.file.seres.udapa.invoice.data;

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
 * 		<th>Tipo de registro</th>
 * 		<th>Descripcion</th>
 * 		<th>Tipo</th>
 * 		<th>Repeticiones</th>
 * 	</tr>
 * 	<tr>
 * 		<td>SINCE</th>
 * 		<td>Descuentos Línea</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SINCE {

	private String descuentos;
	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private Integer numeroDeLinea;
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


	private static Pattern PATTERN_SINCE_descuentos = Pattern.compile("^(.{6}).*");
	private static Pattern PATTERN_SINCE_tipoFactura_325_380_381_383_385_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCE_numeroDeFactura = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SINCE_codigoVendedor_SU_ = Pattern.compile("^.{29}(.{13}).*");
	private static Pattern PATTERN_SINCE_codigoComprador_BY_ = Pattern.compile("^.{42}(.{13}).*");
	private static Pattern PATTERN_SINCE_numeroDeLinea = Pattern.compile("^.{55}(.{6}).*");
	private static Pattern PATTERN_SINCE_numeroDescuento_Cargo = Pattern.compile("^.{61}(.{2}).*");
	private static Pattern PATTERN_SINCE_indicadorDescuento_Cargo_A_C_ = Pattern.compile("^.{63}(.{1}).*");
	private static Pattern PATTERN_SINCE_indicadorSecuenciaDeCalculo = Pattern.compile("^.{64}(.{3}).*");
	private static Pattern PATTERN_SINCE_porcentajeDescuento_Cargo = Pattern.compile("^.{67}(.{9}).*");
	private static Pattern PATTERN_SINCE_importeDescuento_Cargo = Pattern.compile("^.{76}(.{18}).*");
	private static Pattern PATTERN_SINCE_importeTotalSujetoAAplicacion_13_ = Pattern.compile("^.{94}(.{18}).*");
	private static Pattern PATTERN_SINCE_cantidadDeUnidadesQueSeDescuentanPorLinea = Pattern.compile("^.{112}(.{16}).*");
	private static Pattern PATTERN_SINCE_tipoDescuento = Pattern.compile("^.{128}(.{6}).*");
	private static Pattern PATTERN_SINCE_descuentosMonetariosPorUnidad = Pattern.compile("^.{134}(.{16}).*");
	private static Pattern PATTERN_SINCE_unidadDeMedida = Pattern.compile("^.{150}(.{6}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCE_descuentos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescuentos(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_tipoFactura_325_380_381_383_385_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoFactura_325_380_381_383_385_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_numeroDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_codigoVendedor_SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVendedor_SU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_codigoComprador_BY_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoComprador_BY_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_numeroDeLinea.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLinea(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_numeroDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDescuento_Cargo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCE_indicadorDescuento_Cargo_A_C_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorDescuento_Cargo_A_C_(String.valueOf(m.group(1).trim()));
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
		if((m = PATTERN_SINCE_importeTotalSujetoAAplicacion_13_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalSujetoAAplicacion_13_(Double.valueOf(m.group(1).trim()));
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
	 * 		<td>SINCD</th>
	 * 		<td>Descuentos</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDescuentos() {
		return descuentos;
	}
	public void setDescuentos(String descuentos) {
		this.descuentos = descuentos;
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
	 * F1082L  - Número de Línea: Se trata de un descuento/cargo aplicado sobre una línea, el valor que aquí se grabe debe coincidir con el Número de línea de factura (campo F1082 del registro SINCL) al que haga referencia.
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
	 * 		<td>F1082L</th>
	 * 		<td>Número de Línea</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>56</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Integer getNumeroDeLinea() {
		return numeroDeLinea;
	}
	public void setNumeroDeLinea(Integer numeroDeLinea) {
		this.numeroDeLinea = numeroDeLinea;
	}

	/** 
	 * F1082D - Número de Descuento o Cargo:  Es un campo contador. Se sumará 1 por cada dto / cargo aplicado sobre una misma línea a sobre pie de factura. El valor inicial por cada línea o a pie es '1'
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
	 * 		<td>F1082D</th>
	 * 		<td>Número Descuento/Cargo</th>
	 * 		<td>N</th>
	 * 		<td>2</th>
	 * 		<td>62</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Integer getNumeroDescuento_Cargo() {
		return numeroDescuento_Cargo;
	}
	public void setNumeroDescuento_Cargo(Integer numeroDescuento_Cargo) {
		this.numeroDescuento_Cargo = numeroDescuento_Cargo;
	}

	/** 
	 * F5463I  -  Indicador de Descuento/Cargo: puede ser 'A' (Descuento) o 'C' (Cargo).
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
	 * 		<td>F5463I</th>
	 * 		<td>Indicador Descuento/Cargo (A/C)</th>
	 * 		<td>C</th>
	 * 		<td>1</th>
	 * 		<td>64</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getIndicadorDescuento_Cargo_A_C_() {
		return indicadorDescuento_Cargo_A_C_;
	}
	public void setIndicadorDescuento_Cargo_A_C_(String indicadorDescuento_Cargo_A_C_) {
		this.indicadorDescuento_Cargo_A_C_ = indicadorDescuento_Cargo_A_C_;
	}

	/** 
	 * F1227S -  Indicador de Secuencia de Cálculo:  Se utiliza para aplicar descuentos en cascada. Tendrá valor '1  ' si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2  ' y así sucesivamente.
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
	 * 		<td>F1227S</th>
	 * 		<td>Indicador Secuencia de cálculo</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>65</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getIndicadorSecuenciaDeCalculo() {
		return indicadorSecuenciaDeCalculo;
	}
	public void setIndicadorSecuenciaDeCalculo(String indicadorSecuenciaDeCalculo) {
		this.indicadorSecuenciaDeCalculo = indicadorSecuenciaDeCalculo;
	}

	/** 
	 * F5482D  - Porcentaje de Descuento/cargo * F5004A - Importe de Descuento/Cargo:  Uno de los dos campos debe ser cumplimentado (F5482D o F5004A). Es aconsejable cumplimentar los dos.
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
	 * 		<td>F5482D</th>
	 * 		<td>Porcentaje Descuento/Cargo</th>
	 * 		<td>N(4,4)</th>
	 * 		<td>9</th>
	 * 		<td>68</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPorcentajeDescuento_Cargo() {
		return porcentajeDescuento_Cargo;
	}
	public void setPorcentajeDescuento_Cargo(Double porcentajeDescuento_Cargo) {
		this.porcentajeDescuento_Cargo = porcentajeDescuento_Cargo;
	}

	/** 
	 * F5482D  - Porcentaje de Descuento/cargo * F5004A - Importe de Descuento/Cargo:  Uno de los dos campos debe ser cumplimentado (F5482D o F5004A). Es aconsejable cumplimentar los dos.
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
	 * 		<td>F5004A</th>
	 * 		<td>Importe Descuento/Cargo</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>77</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getImporteDescuento_Cargo() {
		return importeDescuento_Cargo;
	}
	public void setImporteDescuento_Cargo(Double importeDescuento_Cargo) {
		this.importeDescuento_Cargo = importeDescuento_Cargo;
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
	 * 		<td>F5004F</th>
	 * 		<td>Importe Total Sujeto a Aplicación (13)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>95</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getImporteTotalSujetoAAplicacion_13_() {
		return importeTotalSujetoAAplicacion_13_;
	}
	public void setImporteTotalSujetoAAplicacion_13_(Double importeTotalSujetoAAplicacion_13_) {
		this.importeTotalSujetoAAplicacion_13_ = importeTotalSujetoAAplicacion_13_;
	}

	/** 
	 * F6060D  - Cantidad de Unidades que se descuentan por línea:  Solo se cumplimentará a nivel de Línea si se hace una bonificación sobre el mismo artículo que se factura. Se especificará en la misma unidad de medida que en la Línea.
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
	 * 		<td>F6060D</th>
	 * 		<td>Cantidad de Unidades que se descuentan por Línea</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>113</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getCantidadDeUnidadesQueSeDescuentanPorLinea() {
		return cantidadDeUnidadesQueSeDescuentanPorLinea;
	}
	public void setCantidadDeUnidadesQueSeDescuentanPorLinea(Double cantidadDeUnidadesQueSeDescuentanPorLinea) {
		this.cantidadDeUnidadesQueSeDescuentanPorLinea = cantidadDeUnidadesQueSeDescuentanPorLinea;
	}

	/** 
	 * F7161T  -  Tipo de Descuento: Solo se cumplimentará a nivel de Cabecera. Los valores posibles son:
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
	 * 		<td>F7161T</th>
	 * 		<td>Tipo Descuento</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>129</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDescuento() {
		return tipoDescuento;
	}
	public void setTipoDescuento(String tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
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
	 * 		<td>F5420D</th>
	 * 		<td>Descuentos Monetarios por Unidad</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>135</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getDescuentosMonetariosPorUnidad() {
		return descuentosMonetariosPorUnidad;
	}
	public void setDescuentosMonetariosPorUnidad(Double descuentosMonetariosPorUnidad) {
		this.descuentosMonetariosPorUnidad = descuentosMonetariosPorUnidad;
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
	 * 		<td>F6411D</th>
	 * 		<td>Unidad de Medida</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>151</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getUnidadDeMedida() {
		return unidadDeMedida;
	}
	public void setUnidadDeMedida(String unidadDeMedida) {
		this.unidadDeMedida = unidadDeMedida;
	}

	/** 
	 * F7161T  -  Tipo de Descuento: Solo se cumplimentará a nivel de Cabecera. Los valores posibles son:
	 */
	public enum F7161T {
		DTO_PRONTO_PAG_EAB("EAB"),
		DTO_COMERCIA_TD("TD"),
		CARGO_POR_FLETE_FC("FC"),
		CARGO_POR_EMBALAJE_PC("PC"),
		CARGO_POR_MONTAJE_SH("SH"),
		;
		
		private String value;
		
		private F7161T(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F7161T enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}