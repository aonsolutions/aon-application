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
 * 		 <th>Tipo de registro</th> <th>Descripcion</th> <th>Tipo</th> <th>Repeticiones</th>
 * 	</tr>
 * 	<tr>
 * 		 <td>ERE1D</td> <td>Descuentos</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class ERE1D {

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
	 * C1082L - Número de Línea Artículo: El valor que aquí se grabe debe coincidir con el Número de línea de pedido sobre la que se sugiere el descuento.
	 */ 
	public Integer getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}

	/** 
	 * C1082L - Número de Línea Artículo: El valor que aquí se grabe debe coincidir con el Número de línea de pedido sobre la que se sugiere el descuento.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1082L</td> <td>Número de Línea Artículo</td> <td>N</td> <td>6</td> <td>64</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaArticulo(Integer numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
	}

	/** 
	 * C1082D - Número Descuento/Cargo: Es un campo contador. Se sumará 1 por cada dto / cargo aplicado sobre una misma línea. El valor inicial por cada línea o a pie es 1
	 */ 
	public Integer getNumeroDescuento_Cargo() {
		return numeroDescuento_Cargo;
	}

	/** 
	 * C1082D - Número Descuento/Cargo: Es un campo contador. Se sumará 1 por cada dto / cargo aplicado sobre una misma línea. El valor inicial por cada línea o a pie es 1
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1082D</td> <td>Número Descuento/Cargo</td> <td>N</td> <td>2</td> <td>70</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDescuento_Cargo(Integer numeroDescuento_Cargo) {
		this.numeroDescuento_Cargo = numeroDescuento_Cargo;
	}

	/** 
	 * 
	 */ 
	public String getIndicadorDescuento_Cargo_A_C_() {
		return indicadorDescuento_Cargo_A_C_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5463E</td> <td>Indicador Descuento/Cargo (A/C)</td> <td>C</td> <td>1</td> <td>72</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIndicadorDescuento_Cargo_A_C_(String indicadorDescuento_Cargo_A_C_) {
		this.indicadorDescuento_Cargo_A_C_ = indicadorDescuento_Cargo_A_C_;
	}

	/** 
	 * C1227S - Indicador Secuencia de cálculo: Se utiliza para aplicar descuentos en cascada. Tendrá valor '1' si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2' y así sucesivamente.
	 */ 
	public String getIndicadorSecuenciaDeCalculo() {
		return indicadorSecuenciaDeCalculo;
	}

	/** 
	 * C1227S - Indicador Secuencia de cálculo: Se utiliza para aplicar descuentos en cascada. Tendrá valor '1' si aplicamos el descuento sobre la primera base imponible. Si sobre la base resultante se aplica otro descuento se grabaran con valor '2' y así sucesivamente.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1227S</td> <td>Indicador Secuencia de cálculo</td> <td>C</td> <td>3</td> <td>73</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIndicadorSecuenciaDeCalculo(String indicadorSecuenciaDeCalculo) {
		this.indicadorSecuenciaDeCalculo = indicadorSecuenciaDeCalculo;
	}

	/** 
	 * C7161S - Servicios Especiales: Los valores posibles son:
	 */ 
	public String getServiciosEspeciales() {
		return serviciosEspeciales;
	}

	/** 
	 * C7161S - Servicios Especiales: Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C7161S</td> <td>Servicios Especiales</td> <td>C</td> <td>6</td> <td>76</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setServiciosEspeciales(String serviciosEspeciales) {
		this.serviciosEspeciales = serviciosEspeciales;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeDescuento_Cargo_3_() {
		return porcentajeDescuento_Cargo_3_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5482D</td> <td>Porcentaje Descuento/Cargo (3)</td> <td>N(4,4)</td> <td>9</td> <td>82</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeDescuento_Cargo_3_(Double porcentajeDescuento_Cargo_3_) {
		this.porcentajeDescuento_Cargo_3_ = porcentajeDescuento_Cargo_3_;
	}

	/** 
	 * 
	 */ 
	public Double getImporteDescuento_Cargo_23_204_() {
		return importeDescuento_Cargo_23_204_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5004A</td> <td>Importe Descuento/Cargo (23/204)</td> <td>N(14,3)</td> <td>18</td> <td>91</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteDescuento_Cargo_23_204_(Double importeDescuento_Cargo_23_204_) {
		this.importeDescuento_Cargo_23_204_ = importeDescuento_Cargo_23_204_;
	}

	/** 
	 * C6060D - Cantidad de Unidades que se descuentan: Solo se cumplimentará a nivel de Línea si se sugiere una bonificación sobre el mismo artículo que se pide. Se especificará en la misma unidad de medida que en la Línea.
	 */ 
	public Double getCantidadDeUnidadesQueSeDescuentan_1_() {
		return cantidadDeUnidadesQueSeDescuentan_1_;
	}

	/** 
	 * C6060D - Cantidad de Unidades que se descuentan: Solo se cumplimentará a nivel de Línea si se sugiere una bonificación sobre el mismo artículo que se pide. Se especificará en la misma unidad de medida que en la Línea.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C6060D</td> <td>Cantidad de Unidades que se descuentan (1)</td> <td>N(12,3)</td> <td>16</td> <td>109</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadDeUnidadesQueSeDescuentan_1_(Double cantidadDeUnidadesQueSeDescuentan_1_) {
		this.cantidadDeUnidadesQueSeDescuentan_1_ = cantidadDeUnidadesQueSeDescuentan_1_;
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
	 * 		 <td>C5420D</td> <td>Descuentos Monetarios por Unidad</td> <td>N(12,3)</td> <td>16</td> <td>125</td> <td>C</td>
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
	 * 		 <td>C6411D</td> <td>Unidad de Medida</td> <td>C</td> <td>6</td> <td>141</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
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