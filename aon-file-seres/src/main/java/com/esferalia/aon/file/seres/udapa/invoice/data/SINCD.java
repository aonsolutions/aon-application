package com.esferalia.aon.file.seres.udapa.invoice.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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
 * 		 <td>SINCD</td> <td>Descuentos Cabecera</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCD {

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


	private static Pattern PATTERN_SINCD_tipoFactura_325_380_381_383_385_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCD_numeroDeFactura = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SINCD_codigoVendedor_SU_ = Pattern.compile("^.{29}(.{13}).*");
	private static Pattern PATTERN_SINCD_codigoComprador_BY_ = Pattern.compile("^.{42}(.{13}).*");
	private static Pattern PATTERN_SINCD_numeroDeLinea = Pattern.compile("^.{55}(.{6}).*");
	private static Pattern PATTERN_SINCD_numeroDescuento_Cargo = Pattern.compile("^.{61}(.{2}).*");
	private static Pattern PATTERN_SINCD_indicadorDescuento_Cargo_A_C_ = Pattern.compile("^.{63}(.{1}).*");
	private static Pattern PATTERN_SINCD_indicadorSecuenciaDeCalculo = Pattern.compile("^.{64}(.{3}).*");
	private static Pattern PATTERN_SINCD_porcentajeDescuento_Cargo = Pattern.compile("^.{67}(.{9}).*");
	private static Pattern PATTERN_SINCD_importeDescuento_Cargo = Pattern.compile("^.{76}(.{18}).*");
	private static Pattern PATTERN_SINCD_importeTotalSujetoAAplicacion_13_ = Pattern.compile("^.{94}(.{18}).*");
	private static Pattern PATTERN_SINCD_cantidadDeUnidadesQueSeDescuentanPorLinea = Pattern.compile("^.{112}(.{16}).*");
	private static Pattern PATTERN_SINCD_tipoDescuento = Pattern.compile("^.{128}(.{6}).*");
	private static Pattern PATTERN_SINCD_descuentosMonetariosPorUnidad = Pattern.compile("^.{134}(.{16}).*");
	private static Pattern PATTERN_SINCD_unidadDeMedida = Pattern.compile("^.{150}(.{6}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCD_tipoFactura_325_380_381_383_385_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoFactura_325_380_381_383_385_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_numeroDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_codigoVendedor_SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVendedor_SU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_codigoComprador_BY_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoComprador_BY_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCD_numeroDeLinea.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLinea(Integer.valueOf(m.group(1).trim()));
		}
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
	 * F1082L  - Número de Línea:   Si los Descuentos o Cargos se están aplicando a pie de factura, el Número de Línea será cero. Si se trata de un descuento/cargo aplicado sobre una línea, el valor que aquí se grabe debe coincidir con el Número de línea de factura (campo F1082 del registro SINCL) al que haga referencia.
	 */ 
	public Integer getNumeroDeLinea() {
		return numeroDeLinea;
	}

	/** 
	 * F1082L  - Número de Línea:   Si los Descuentos o Cargos se están aplicando a pie de factura, el Número de Línea será cero. Si se trata de un descuento/cargo aplicado sobre una línea, el valor que aquí se grabe debe coincidir con el Número de línea de factura (campo F1082 del registro SINCL) al que haga referencia.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1082L</td> <td>Número de Línea</td> <td>N</td> <td>6</td> <td>56</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLinea(Integer numeroDeLinea) {
		this.numeroDeLinea = numeroDeLinea;
	}

	/** 
	 * F1082D - Número de Descuento o Cargo:  Es un campo contador. Se sumará 1 por cada dto / cargo aplicado sobre una misma línea a sobre pie de factura. El valor inicial por cada línea o a pie es '1'
	 */ 
	public Integer getNumeroDescuento_Cargo() {
		return numeroDescuento_Cargo;
	}

	/** 
	 * F1082D - Número de Descuento o Cargo:  Es un campo contador. Se sumará 1 por cada dto / cargo aplicado sobre una misma línea a sobre pie de factura. El valor inicial por cada línea o a pie es '1'
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1082D</td> <td>Número Descuento/Cargo</td> <td>N</td> <td>2</td> <td>62</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDescuento_Cargo(Integer numeroDescuento_Cargo) {
		this.numeroDescuento_Cargo = numeroDescuento_Cargo;
	}

	/** 
	 * F5463I  -  Indicador de Descuento/Cargo: puede ser 'A' (Descuento) o 'C' (Cargo).
	 */ 
	public String getIndicadorDescuento_Cargo_A_C_() {
		return indicadorDescuento_Cargo_A_C_;
	}

	/** 
	 * F5463I  -  Indicador de Descuento/Cargo: puede ser 'A' (Descuento) o 'C' (Cargo).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5463I</td> <td>Indicador Descuento/Cargo (A/C)</td> <td>C</td> <td>1</td> <td>64</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIndicadorDescuento_Cargo_A_C_(String indicadorDescuento_Cargo_A_C_) {
		this.indicadorDescuento_Cargo_A_C_ = indicadorDescuento_Cargo_A_C_;
	}

	/** 
	 * F1227S -  Indicador de Secuencia de Cálculo:  Se utiliza para aplicar descuentos en cascada. Tendrá valor '1  ' si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2  ' y así sucesivamente.
	 */ 
	public String getIndicadorSecuenciaDeCalculo() {
		return indicadorSecuenciaDeCalculo;
	}

	/** 
	 * F1227S -  Indicador de Secuencia de Cálculo:  Se utiliza para aplicar descuentos en cascada. Tendrá valor '1  ' si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2  ' y así sucesivamente.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1227S</td> <td>Indicador Secuencia de cálculo</td> <td>C</td> <td>3</td> <td>65</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIndicadorSecuenciaDeCalculo(String indicadorSecuenciaDeCalculo) {
		this.indicadorSecuenciaDeCalculo = indicadorSecuenciaDeCalculo;
	}

	/** 
	 * F5482D  - Porcentaje de Descuento/cargo * F5004A - Importe de Descuento/Cargo:  Uno de los dos campos debe ser cumplimentado (F5482D o F5004A). Es aconsejable cumplimentar los dos.
	 */ 
	public Double getPorcentajeDescuento_Cargo() {
		return porcentajeDescuento_Cargo;
	}

	/** 
	 * F5482D  - Porcentaje de Descuento/cargo * F5004A - Importe de Descuento/Cargo:  Uno de los dos campos debe ser cumplimentado (F5482D o F5004A). Es aconsejable cumplimentar los dos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5482D</td> <td>Porcentaje Descuento/Cargo</td> <td>N(4,4)</td> <td>9</td> <td>68</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeDescuento_Cargo(Double porcentajeDescuento_Cargo) {
		this.porcentajeDescuento_Cargo = porcentajeDescuento_Cargo;
	}

	/** 
	 * F5482D  - Porcentaje de Descuento/cargo * F5004A - Importe de Descuento/Cargo:  Uno de los dos campos debe ser cumplimentado (F5482D o F5004A). Es aconsejable cumplimentar los dos.
	 */ 
	public Double getImporteDescuento_Cargo() {
		return importeDescuento_Cargo;
	}

	/** 
	 * F5482D  - Porcentaje de Descuento/cargo * F5004A - Importe de Descuento/Cargo:  Uno de los dos campos debe ser cumplimentado (F5482D o F5004A). Es aconsejable cumplimentar los dos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004A</td> <td>Importe Descuento/Cargo</td> <td>N(14,3)</td> <td>18</td> <td>77</td> <td>D</td>
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
	 * 		 <td>F5004F</td> <td>Importe Total Sujeto a Aplicación (13)</td> <td>N(14,3)</td> <td>18</td> <td>95</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalSujetoAAplicacion_13_(Double importeTotalSujetoAAplicacion_13_) {
		this.importeTotalSujetoAAplicacion_13_ = importeTotalSujetoAAplicacion_13_;
	}

	/** 
	 * F6060D  - Cantidad de Unidades que se descuentan por línea:  Solo se cumplimentará a nivel de Línea si se hace una bonificación sobre el mismo artículo que se factura. Se especificará en la misma unidad de medida que en la Línea.
	 */ 
	public Double getCantidadDeUnidadesQueSeDescuentanPorLinea() {
		return cantidadDeUnidadesQueSeDescuentanPorLinea;
	}

	/** 
	 * F6060D  - Cantidad de Unidades que se descuentan por línea:  Solo se cumplimentará a nivel de Línea si se hace una bonificación sobre el mismo artículo que se factura. Se especificará en la misma unidad de medida que en la Línea.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F6060D</td> <td>Cantidad de Unidades que se descuentan por Línea</td> <td>N(12,3)</td> <td>16</td> <td>113</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadDeUnidadesQueSeDescuentanPorLinea(Double cantidadDeUnidadesQueSeDescuentanPorLinea) {
		this.cantidadDeUnidadesQueSeDescuentanPorLinea = cantidadDeUnidadesQueSeDescuentanPorLinea;
	}

	/** 
	 * F7161T  -  Tipo de Descuento: Solo se cumplimentará a nivel de Cabecera. Los valores posibles son:
	 */ 
	public String getTipoDescuento() {
		return tipoDescuento;
	}

	/** 
	 * F7161T  -  Tipo de Descuento: Solo se cumplimentará a nivel de Cabecera. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7161T</td> <td>Tipo Descuento</td> <td>C</td> <td>6</td> <td>129</td> <td>C</td>
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
	 * 		 <td>F5420D</td> <td>Descuentos Monetarios por Unidad</td> <td>N(12,3)</td> <td>16</td> <td>135</td> <td>C</td>
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
	 * 		 <td>F6411D</td> <td>Unidad de Medida</td> <td>C</td> <td>6</td> <td>151</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
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