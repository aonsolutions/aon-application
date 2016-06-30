package com.esferalia.aon.file.seres.udapa.sales.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1D entity.
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
 * 		<td>ERE1D</th>
 * 		<td>Descuentos</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class ERE1D {

	private String descuentos;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private Integer numeroDeLineaArticulo;
	private Integer numeroDescuento_Cargo;
	private String indicadorDescuento_Cargo_A_C_;
	private String indicadorSecuenciaDeCalculo;
	private String serviciosEspeciales;
	private Double porcentajeDescuento_Cargo_3_;
	private Double importeDescuento_Cargo_23_204_;
	private Double cantidadDeUnidadesQueSeDescuentan_1_;
	private Double descuentosMonetariosPorUnidad;
	private String unidadDeMedida;


	private static Pattern PATTERN_ERE1D_descuentos = Pattern.compile("^(.{6}).*");
	private static Pattern PATTERN_ERE1D_tipoDePedido_220_221_224_226_22E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ERE1D_numeroDePedido = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_ERE1D_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_ERE1D_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_ERE1D_numeroDeLineaArticulo = Pattern.compile("^.{63}(.{6}).*");
	private static Pattern PATTERN_ERE1D_numeroDescuento_Cargo = Pattern.compile("^.{69}(.{2}).*");
	private static Pattern PATTERN_ERE1D_indicadorDescuento_Cargo_A_C_ = Pattern.compile("^.{71}(.{1}).*");
	private static Pattern PATTERN_ERE1D_indicadorSecuenciaDeCalculo = Pattern.compile("^.{72}(.{3}).*");
	private static Pattern PATTERN_ERE1D_serviciosEspeciales = Pattern.compile("^.{75}(.{6}).*");
	private static Pattern PATTERN_ERE1D_porcentajeDescuento_Cargo_3_ = Pattern.compile("^.{81}(.{9}).*");
	private static Pattern PATTERN_ERE1D_importeDescuento_Cargo_23_204_ = Pattern.compile("^.{90}(.{18}).*");
	private static Pattern PATTERN_ERE1D_cantidadDeUnidadesQueSeDescuentan_1_ = Pattern.compile("^.{108}(.{16}).*");
	private static Pattern PATTERN_ERE1D_descuentosMonetariosPorUnidad = Pattern.compile("^.{124}(.{16}).*");
	private static Pattern PATTERN_ERE1D_unidadDeMedida = Pattern.compile("^.{140}(.{6}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1D_descuentos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescuentos(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_tipoDePedido_220_221_224_226_22E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDePedido_220_221_224_226_22E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_numeroDePedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_numeroDeLineaArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_numeroDescuento_Cargo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDescuento_Cargo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_indicadorDescuento_Cargo_A_C_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorDescuento_Cargo_A_C_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_indicadorSecuenciaDeCalculo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIndicadorSecuenciaDeCalculo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_serviciosEspeciales.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setServiciosEspeciales(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_porcentajeDescuento_Cargo_3_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeDescuento_Cargo_3_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_importeDescuento_Cargo_23_204_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteDescuento_Cargo_23_204_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_cantidadDeUnidadesQueSeDescuentan_1_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadDeUnidadesQueSeDescuentan_1_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_descuentosMonetariosPorUnidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescuentosMonetariosPorUnidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1D_unidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
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
	 * 		<td>ERE1D</th>
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
	 * 		<td>C1001T</th>
	 * 		<td>Tipo de Pedido (220, 221, 224, 226, 22E)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDePedido_220_221_224_226_22E_() {
		return tipoDePedido_220_221_224_226_22E_;
	}
	public void setTipoDePedido_220_221_224_226_22E_(String tipoDePedido_220_221_224_226_22E_) {
		this.tipoDePedido_220_221_224_226_22E_ = tipoDePedido_220_221_224_226_22E_;
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
	 * 		<td>C1004P</th>
	 * 		<td>Número de Pedido</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDePedido() {
		return numeroDePedido;
	}
	public void setNumeroDePedido(String numeroDePedido) {
		this.numeroDePedido = numeroDePedido;
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
	 * 		<td>C3039E</th>
	 * 		<td>Código Emisor  (MS)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>30</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}
	public void setCodigoEmisor_MS_(String codigoEmisor_MS_) {
		this.codigoEmisor_MS_ = codigoEmisor_MS_;
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
	 * 		<td>C3039R</th>
	 * 		<td>Código Receptor (MR)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>47</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}

	/** 
	 * C1082L - Número de Línea Artículo: El valor que aquí se grabe debe coincidir con el Número de línea de pedido sobre la que se sugiere el descuento.
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
	 * 		<td>C1082L</th>
	 * 		<td>Número de Línea Artículo</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>64</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Integer getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}
	public void setNumeroDeLineaArticulo(Integer numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
	}

	/** 
	 * C1082D - Número Descuento/Cargo: Es un campo contador. Se sumará 1 por cada dto / cargo aplicado sobre una misma línea. El valor inicial por cada línea o a pie es 1
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
	 * 		<td>C1082D</th>
	 * 		<td>Número Descuento/Cargo</th>
	 * 		<td>N</th>
	 * 		<td>2</th>
	 * 		<td>70</th>
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
	 * 		<td>C5463E</th>
	 * 		<td>Indicador Descuento/Cargo (A/C)</th>
	 * 		<td>C</th>
	 * 		<td>1</th>
	 * 		<td>72</th>
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
	 * C1227S - Indicador Secuencia de cálculo: Se utiliza para aplicar descuentos en cascada. Tendrá valor '1' si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2' y así sucesivamente.
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
	 * 		<td>C1227S</th>
	 * 		<td>Indicador Secuencia de cálculo</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>73</th>
	 * 		<td>C</th>
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
	 * C7161S - Servicios Especiales: Los valores posibles son:
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
	 * 		<td>C7161S</th>
	 * 		<td>Servicios Especiales</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>76</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getServiciosEspeciales() {
		return serviciosEspeciales;
	}
	public void setServiciosEspeciales(String serviciosEspeciales) {
		this.serviciosEspeciales = serviciosEspeciales;
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
	 * 		<td>C5482D</th>
	 * 		<td>Porcentaje Descuento/Cargo (3)</th>
	 * 		<td>N(4,4)</th>
	 * 		<td>9</th>
	 * 		<td>82</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPorcentajeDescuento_Cargo_3_() {
		return porcentajeDescuento_Cargo_3_;
	}
	public void setPorcentajeDescuento_Cargo_3_(Double porcentajeDescuento_Cargo_3_) {
		this.porcentajeDescuento_Cargo_3_ = porcentajeDescuento_Cargo_3_;
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
	 * 		<td>C5004A</th>
	 * 		<td>Importe Descuento/Cargo (23/204)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>91</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getImporteDescuento_Cargo_23_204_() {
		return importeDescuento_Cargo_23_204_;
	}
	public void setImporteDescuento_Cargo_23_204_(Double importeDescuento_Cargo_23_204_) {
		this.importeDescuento_Cargo_23_204_ = importeDescuento_Cargo_23_204_;
	}

	/** 
	 * C6060D - Cantidad de Unidades que se descuentan: Solo se cumplimentará a nivel de Línea si se sugiere una bonificación sobre el mismo artículo que se pide. Se especificará en la misma unidad de medida que en la Línea.
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
	 * 		<td>C6060D</th>
	 * 		<td>Cantidad de Unidades que se descuentan (1)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>109</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getCantidadDeUnidadesQueSeDescuentan_1_() {
		return cantidadDeUnidadesQueSeDescuentan_1_;
	}
	public void setCantidadDeUnidadesQueSeDescuentan_1_(Double cantidadDeUnidadesQueSeDescuentan_1_) {
		this.cantidadDeUnidadesQueSeDescuentan_1_ = cantidadDeUnidadesQueSeDescuentan_1_;
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
	 * 		<td>C5420D</th>
	 * 		<td>Descuentos Monetarios por Unidad</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>125</th>
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
	 * 		<td>C6411D</th>
	 * 		<td>Unidad de Medida</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>141</th>
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
	 * C5463I - Indicador Descuento/Cargo (A/C): Los valores posibles son:
	 */
	public enum C5463I {
		DESCUENT_A("A"),
		CARG_C("C"),
		;
		
		private String value;
		
		private C5463I(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C5463I enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C7161S - Servicios Especiales: Los valores posibles son:
	 */
	public enum C7161S {
		DEALER_DISCOUNT_ALLOWANCE__EAN_CODE_DDA("DDA"),
		TRADE_DISCOUN_TD("TD"),
		;
		
		private String value;
		
		private C7161S(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C7161S enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}