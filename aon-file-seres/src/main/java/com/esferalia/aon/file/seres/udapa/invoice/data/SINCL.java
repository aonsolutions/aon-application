package com.esferalia.aon.file.seres.udapa.invoice.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCL entity.
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
 * 		 <td>SINCL</td> <td>Líneas</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCL {

	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private Integer numeroDeLinea;
	private String codigoArticulo;
	private String descripcionDelArticulo;
	private String tipoArticulo;
	private String codigoInternoArticuloProveedor_SA_;
	private String codigoInternoArticuloCliente_IN_;
	private String codigoVariablePromocional_PV_;
	private String codigoUnidadDeExpedicion_EN_;
	private String numeroDeLote_BN_;
	private Double cantidadFacturada_47_;
	private Double cantidadBonificada_15E_;
	private String unidadDeMedida;
	private Double unidadesEntregadas;
	private Double numeroUnidadesDeConsumoEnU_Expedicion_59_;
	private Double importeTotalNetoDeLaLineaDeArticulo;
	private Double precioBrutoUnitario;
	private Double precioNetoUnitario;
	private String unidadDeMedidaDelPrecio;
	private String calificadorIVA_IGIG;
	private Double porcentajeImpuestoIVA_IGIG;
	private Double importeImpuestoIVA_IGIG;
	private Double porcentajeRecargoDeEquivalencia;
	private Double importeRecargoDeEquivalencia;
	private String calificadorOtroTipoDeImpuesto;
	private Double porcentajeOtroTipoDeImpuesto;
	private Double importeOtroTipoDeImpuesto;
	private String numeroPedido_ON_;
	private String numeroDeAlbaran_DQ_;
	private Integer numeroDeEmbalajes;
	private String tipoDeEmbalaje;
	private Double importeTotalBrutoDeLaLineaDeArticulo_98_;


	private static Pattern PATTERN_SINCL_tipoFactura_325_380_381_383_385_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCL_numeroDeFactura = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SINCL_codigoVendedor_SU_ = Pattern.compile("^.{29}(.{13}).*");
	private static Pattern PATTERN_SINCL_codigoComprador_BY_ = Pattern.compile("^.{42}(.{13}).*");
	private static Pattern PATTERN_SINCL_numeroDeLinea = Pattern.compile("^.{55}(.{6}).*");
	private static Pattern PATTERN_SINCL_codigoArticulo = Pattern.compile("^.{61}(.{15}).*");
	private static Pattern PATTERN_SINCL_descripcionDelArticulo = Pattern.compile("^.{76}(.{35}).*");
	private static Pattern PATTERN_SINCL_tipoArticulo = Pattern.compile("^.{111}(.{1}).*");
	private static Pattern PATTERN_SINCL_codigoInternoArticuloProveedor_SA_ = Pattern.compile("^.{112}(.{15}).*");
	private static Pattern PATTERN_SINCL_codigoInternoArticuloCliente_IN_ = Pattern.compile("^.{127}(.{15}).*");
	private static Pattern PATTERN_SINCL_codigoVariablePromocional_PV_ = Pattern.compile("^.{142}(.{15}).*");
	private static Pattern PATTERN_SINCL_codigoUnidadDeExpedicion_EN_ = Pattern.compile("^.{157}(.{15}).*");
	private static Pattern PATTERN_SINCL_numeroDeLote_BN_ = Pattern.compile("^.{172}(.{15}).*");
	private static Pattern PATTERN_SINCL_cantidadFacturada_47_ = Pattern.compile("^.{187}(.{16}).*");
	private static Pattern PATTERN_SINCL_cantidadBonificada_15E_ = Pattern.compile("^.{203}(.{16}).*");
	private static Pattern PATTERN_SINCL_unidadDeMedida = Pattern.compile("^.{219}(.{6}).*");
	private static Pattern PATTERN_SINCL_unidadesEntregadas = Pattern.compile("^.{225}(.{16}).*");
	private static Pattern PATTERN_SINCL_numeroUnidadesDeConsumoEnU_Expedicion_59_ = Pattern.compile("^.{241}(.{16}).*");
	private static Pattern PATTERN_SINCL_importeTotalNetoDeLaLineaDeArticulo = Pattern.compile("^.{257}(.{18}).*");
	private static Pattern PATTERN_SINCL_precioBrutoUnitario = Pattern.compile("^.{275}(.{16}).*");
	private static Pattern PATTERN_SINCL_precioNetoUnitario = Pattern.compile("^.{291}(.{16}).*");
	private static Pattern PATTERN_SINCL_unidadDeMedidaDelPrecio = Pattern.compile("^.{307}(.{6}).*");
	private static Pattern PATTERN_SINCL_calificadorIVA_IGIG = Pattern.compile("^.{313}(.{6}).*");
	private static Pattern PATTERN_SINCL_porcentajeImpuestoIVA_IGIG = Pattern.compile("^.{319}(.{6}).*");
	private static Pattern PATTERN_SINCL_importeImpuestoIVA_IGIG = Pattern.compile("^.{325}(.{18}).*");
	private static Pattern PATTERN_SINCL_porcentajeRecargoDeEquivalencia = Pattern.compile("^.{343}(.{6}).*");
	private static Pattern PATTERN_SINCL_importeRecargoDeEquivalencia = Pattern.compile("^.{349}(.{18}).*");
	private static Pattern PATTERN_SINCL_calificadorOtroTipoDeImpuesto = Pattern.compile("^.{367}(.{6}).*");
	private static Pattern PATTERN_SINCL_porcentajeOtroTipoDeImpuesto = Pattern.compile("^.{373}(.{6}).*");
	private static Pattern PATTERN_SINCL_importeOtroTipoDeImpuesto = Pattern.compile("^.{379}(.{18}).*");
	private static Pattern PATTERN_SINCL_numeroPedido_ON_ = Pattern.compile("^.{397}(.{17}).*");
	private static Pattern PATTERN_SINCL_numeroDeAlbaran_DQ_ = Pattern.compile("^.{414}(.{17}).*");
	private static Pattern PATTERN_SINCL_numeroDeEmbalajes = Pattern.compile("^.{431}(.{8}).*");
	private static Pattern PATTERN_SINCL_tipoDeEmbalaje = Pattern.compile("^.{439}(.{7}).*");
	private static Pattern PATTERN_SINCL_importeTotalBrutoDeLaLineaDeArticulo_98_ = Pattern.compile("^.{446}(.{18}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCL_tipoFactura_325_380_381_383_385_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoFactura_325_380_381_383_385_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoVendedor_SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVendedor_SU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoComprador_BY_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoComprador_BY_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeLinea.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLinea(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_descripcionDelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_tipoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoInternoArticuloProveedor_SA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInternoArticuloProveedor_SA_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoInternoArticuloCliente_IN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInternoArticuloCliente_IN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoVariablePromocional_PV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVariablePromocional_PV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoUnidadDeExpedicion_EN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoUnidadDeExpedicion_EN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeLote_BN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLote_BN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_cantidadFacturada_47_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadFacturada_47_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_cantidadBonificada_15E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadBonificada_15E_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_unidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_unidadesEntregadas.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadesEntregadas(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroUnidadesDeConsumoEnU_Expedicion_59_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroUnidadesDeConsumoEnU_Expedicion_59_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeTotalNetoDeLaLineaDeArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalNetoDeLaLineaDeArticulo(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_precioBrutoUnitario.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioBrutoUnitario(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_precioNetoUnitario.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioNetoUnitario(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_unidadDeMedidaDelPrecio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaDelPrecio(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_calificadorIVA_IGIG.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorIVA_IGIG(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_porcentajeImpuestoIVA_IGIG.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeImpuestoIVA_IGIG(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeImpuestoIVA_IGIG.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteImpuestoIVA_IGIG(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_porcentajeRecargoDeEquivalencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeRecargoDeEquivalencia(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeRecargoDeEquivalencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteRecargoDeEquivalencia(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_calificadorOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorOtroTipoDeImpuesto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_porcentajeOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeOtroTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteOtroTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroPedido_ON_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroPedido_ON_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeAlbaran_DQ_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeAlbaran_DQ_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeEmbalajes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeEmbalajes(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_tipoDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeTotalBrutoDeLaLineaDeArticulo_98_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalBrutoDeLaLineaDeArticulo_98_(Double.valueOf(m.group(1).trim()));
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
	 * 
	 */ 
	public Integer getNumeroDeLinea() {
		return numeroDeLinea;
	}

	/** 
	 * 
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
	 * F7140A - Código Artículo: Código de artículo que se está facturando.
	 */ 
	public String getCodigoArticulo() {
		return codigoArticulo;
	}

	/** 
	 * F7140A - Código Artículo: Código de artículo que se está facturando.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7140A</td> <td>Código Artículo</td> <td>C</td> <td>15</td> <td>62</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoArticulo(String codigoArticulo) {
		this.codigoArticulo = codigoArticulo;
	}

	/** 
	 * 
	 */ 
	public String getDescripcionDelArticulo() {
		return descripcionDelArticulo;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7008A</td> <td>Descripción del Articulo</td> <td>C</td> <td>35</td> <td>77</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescripcionDelArticulo(String descripcionDelArticulo) {
		this.descripcionDelArticulo = descripcionDelArticulo;
	}

	/** 
	 * F7081A - Tipo Artículo:
	 */ 
	public String getTipoArticulo() {
		return tipoArticulo;
	}

	/** 
	 * F7081A - Tipo Artículo:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7081A</td> <td>Tipo Articulo</td> <td>C</td> <td>1</td> <td>112</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	/** 
	 * 
	 */ 
	public String getCodigoInternoArticuloProveedor_SA_() {
		return codigoInternoArticuloProveedor_SA_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7140P</td> <td>Código Interno Articulo Proveedor (SA)</td> <td>C</td> <td>15</td> <td>113</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInternoArticuloProveedor_SA_(String codigoInternoArticuloProveedor_SA_) {
		this.codigoInternoArticuloProveedor_SA_ = codigoInternoArticuloProveedor_SA_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoInternoArticuloCliente_IN_() {
		return codigoInternoArticuloCliente_IN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7140C</td> <td>Código Interno Articulo Cliente (IN)</td> <td>C</td> <td>15</td> <td>128</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInternoArticuloCliente_IN_(String codigoInternoArticuloCliente_IN_) {
		this.codigoInternoArticuloCliente_IN_ = codigoInternoArticuloCliente_IN_;
	}

	/** 
	 * F7140V - Código Variable Promocional del Producto: Código que hace referencia a una promoción para la que no se ha generado un nuevo código de Barras. Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
	 */ 
	public String getCodigoVariablePromocional_PV_() {
		return codigoVariablePromocional_PV_;
	}

	/** 
	 * F7140V - Código Variable Promocional del Producto: Código que hace referencia a una promoción para la que no se ha generado un nuevo código de Barras. Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7140V</td> <td>Código Variable Promocional (PV)</td> <td>C</td> <td>15</td> <td>143</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoVariablePromocional_PV_(String codigoVariablePromocional_PV_) {
		this.codigoVariablePromocional_PV_ = codigoVariablePromocional_PV_;
	}

	/** 
	 * F7140E - Código Unidad de Expedición asociada al producto: Código que hace referencia a la forma de distribución del producto (por ejem: cajas de unidades). Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
	 */ 
	public String getCodigoUnidadDeExpedicion_EN_() {
		return codigoUnidadDeExpedicion_EN_;
	}

	/** 
	 * F7140E - Código Unidad de Expedición asociada al producto: Código que hace referencia a la forma de distribución del producto (por ejem: cajas de unidades). Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7140E</td> <td>Código Unidad de Expedición (EN)</td> <td>C</td> <td>15</td> <td>158</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoUnidadDeExpedicion_EN_(String codigoUnidadDeExpedicion_EN_) {
		this.codigoUnidadDeExpedicion_EN_ = codigoUnidadDeExpedicion_EN_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeLote_BN_() {
		return numeroDeLote_BN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7140B</td> <td>Número de Lote (BN)</td> <td>C</td> <td>15</td> <td>173</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLote_BN_(String numeroDeLote_BN_) {
		this.numeroDeLote_BN_ = numeroDeLote_BN_;
	}

	/** 
	 * F6060C - Cantidad: Cantidad Facturada.. Si se trata de un artículo de media variable, indica el nº de unidades en kilos o litros facturados.
	 */ 
	public Double getCantidadFacturada_47_() {
		return cantidadFacturada_47_;
	}

	/** 
	 * F6060C - Cantidad: Cantidad Facturada.. Si se trata de un artículo de media variable, indica el nº de unidades en kilos o litros facturados.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F6060C</td> <td>Cantidad Facturada (47)</td> <td>N(12,3)</td> <td>16</td> <td>188</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadFacturada_47_(Double cantidadFacturada_47_) {
		this.cantidadFacturada_47_ = cantidadFacturada_47_;
	}

	/** 
	 * F6060B - Cantidad Bonificada: Solo se utilizará si la compra de un artículo se bonifica con un artículo distinto. Si se bonifica con el mismo artículo se utilizará el registro de descuentos SINCD donde se detallará la cantidad bonificada.
	 */ 
	public Double getCantidadBonificada_15E_() {
		return cantidadBonificada_15E_;
	}

	/** 
	 * F6060B - Cantidad Bonificada: Solo se utilizará si la compra de un artículo se bonifica con un artículo distinto. Si se bonifica con el mismo artículo se utilizará el registro de descuentos SINCD donde se detallará la cantidad bonificada.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F6060B</td> <td>Cantidad Bonificada (15E)</td> <td>N(12,3)</td> <td>16</td> <td>204</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadBonificada_15E_(Double cantidadBonificada_15E_) {
		this.cantidadBonificada_15E_ = cantidadBonificada_15E_;
	}

	/** 
	 * F6411C - Unidad de Medida del producto que se está facturando:  Solo se utilizará si el producto es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getUnidadDeMedida() {
		return unidadDeMedida;
	}

	/** 
	 * F6411C - Unidad de Medida del producto que se está facturando:  Solo se utilizará si el producto es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F6411C</td> <td>Unidad de Medida</td> <td>C</td> <td>6</td> <td>220</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedida(String unidadDeMedida) {
		this.unidadDeMedida = unidadDeMedida;
	}

	/** 
	 * F6060E - Unidades Entregadas: Se usará solo para indicar en productos de peso variable, la cantidad de unidades entregadas.
	 */ 
	public Double getUnidadesEntregadas() {
		return unidadesEntregadas;
	}

	/** 
	 * F6060E - Unidades Entregadas: Se usará solo para indicar en productos de peso variable, la cantidad de unidades entregadas.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F6060E</td> <td>Unidades Entregadas</td> <td>N(12,3)</td> <td>16</td> <td>226</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadesEntregadas(Double unidadesEntregadas) {
		this.unidadesEntregadas = unidadesEntregadas;
	}

	/** 
	 * 
	 */ 
	public Double getNumeroUnidadesDeConsumoEnU_Expedicion_59_() {
		return numeroUnidadesDeConsumoEnU_Expedicion_59_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F6060U</td> <td>Número Unidades de Consumo en U. Expedición (59)</td> <td>N(12,3)</td> <td>16</td> <td>242</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroUnidadesDeConsumoEnU_Expedicion_59_(Double numeroUnidadesDeConsumoEnU_Expedicion_59_) {
		this.numeroUnidadesDeConsumoEnU_Expedicion_59_ = numeroUnidadesDeConsumoEnU_Expedicion_59_;
	}

	/** 
	 * F5004L - Importe total Neto de la  línea de Artículo:  (Cantidad x Precio) - Descuentos.
	 */ 
	public Double getImporteTotalNetoDeLaLineaDeArticulo() {
		return importeTotalNetoDeLaLineaDeArticulo;
	}

	/** 
	 * F5004L - Importe total Neto de la  línea de Artículo:  (Cantidad x Precio) - Descuentos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004L</td> <td>Importe Total Neto de la Línea de Articulo</td> <td>N(14,3)</td> <td>18</td> <td>258</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalNetoDeLaLineaDeArticulo(Double importeTotalNetoDeLaLineaDeArticulo) {
		this.importeTotalNetoDeLaLineaDeArticulo = importeTotalNetoDeLaLineaDeArticulo;
	}

	/** 
	 * F5118B - Precio Bruto Unitario:  Excluye descuentos, cargos e impuestos.
	 */ 
	public Double getPrecioBrutoUnitario() {
		return precioBrutoUnitario;
	}

	/** 
	 * F5118B - Precio Bruto Unitario:  Excluye descuentos, cargos e impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5118B</td> <td>Precio Bruto Unitario</td> <td>N(11,4)</td> <td>16</td> <td>276</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPrecioBrutoUnitario(Double precioBrutoUnitario) {
		this.precioBrutoUnitario = precioBrutoUnitario;
	}

	/** 
	 * F5118N - Precio Neto Unitario:  Incluye descuentos y cargos pero no impuestos.
	 */ 
	public Double getPrecioNetoUnitario() {
		return precioNetoUnitario;
	}

	/** 
	 * F5118N - Precio Neto Unitario:  Incluye descuentos y cargos pero no impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5118N</td> <td>Precio Neto Unitario</td> <td>N(11,4)</td> <td>16</td> <td>292</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPrecioNetoUnitario(Double precioNetoUnitario) {
		this.precioNetoUnitario = precioNetoUnitario;
	}

	/** 
	 * F6411P - Unidad de Medida del Precio: Solo se utilizará si el articulo es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getUnidadDeMedidaDelPrecio() {
		return unidadDeMedidaDelPrecio;
	}

	/** 
	 * F6411P - Unidad de Medida del Precio: Solo se utilizará si el articulo es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F6411P</td> <td>Unidad de Medida del Precio</td> <td>C</td> <td>6</td> <td>308</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaDelPrecio(String unidadDeMedidaDelPrecio) {
		this.unidadDeMedidaDelPrecio = unidadDeMedidaDelPrecio;
	}

	/** 
	 * F5153I - Calificador de IVA o IGIC: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCalificadorIVA_IGIG() {
		return calificadorIVA_IGIG;
	}

	/** 
	 * F5153I - Calificador de IVA o IGIC: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5153I</td> <td>Calificador IVA/IGIG</td> <td>C</td> <td>6</td> <td>314</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorIVA_IGIG(String calificadorIVA_IGIG) {
		this.calificadorIVA_IGIG = calificadorIVA_IGIG;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeImpuestoIVA_IGIG() {
		return porcentajeImpuestoIVA_IGIG;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5278V</td> <td>% Impuesto IVA/IGIG</td> <td>N(3,2)</td> <td>6</td> <td>320</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeImpuestoIVA_IGIG(Double porcentajeImpuestoIVA_IGIG) {
		this.porcentajeImpuestoIVA_IGIG = porcentajeImpuestoIVA_IGIG;
	}

	/** 
	 * F5004V - Importe del Impuesto: Se aconseja no cumplimentar el importe de IVA a nivel de línea con el fin de evitar descuadres con los totales de factura.
	 */ 
	public Double getImporteImpuestoIVA_IGIG() {
		return importeImpuestoIVA_IGIG;
	}

	/** 
	 * F5004V - Importe del Impuesto: Se aconseja no cumplimentar el importe de IVA a nivel de línea con el fin de evitar descuadres con los totales de factura.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004V</td> <td>Importe Impuesto IVA/IGIG</td> <td>N(14,3)</td> <td>18</td> <td>326</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteImpuestoIVA_IGIG(Double importeImpuestoIVA_IGIG) {
		this.importeImpuestoIVA_IGIG = importeImpuestoIVA_IGIG;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeRecargoDeEquivalencia() {
		return porcentajeRecargoDeEquivalencia;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5278R</td> <td>% Recargo de Equivalencia</td> <td>N(3,2)</td> <td>6</td> <td>344</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeRecargoDeEquivalencia(Double porcentajeRecargoDeEquivalencia) {
		this.porcentajeRecargoDeEquivalencia = porcentajeRecargoDeEquivalencia;
	}

	/** 
	 * 
	 */ 
	public Double getImporteRecargoDeEquivalencia() {
		return importeRecargoDeEquivalencia;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004R</td> <td>Importe Recargo de Equivalencia</td> <td>N(14,3)</td> <td>18</td> <td>350</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteRecargoDeEquivalencia(Double importeRecargoDeEquivalencia) {
		this.importeRecargoDeEquivalencia = importeRecargoDeEquivalencia;
	}

	/** 
	 * 
	 */ 
	public String getCalificadorOtroTipoDeImpuesto() {
		return calificadorOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5153O</td> <td>Calificador Otro Tipo de Impuesto</td> <td>C</td> <td>6</td> <td>368</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorOtroTipoDeImpuesto(String calificadorOtroTipoDeImpuesto) {
		this.calificadorOtroTipoDeImpuesto = calificadorOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeOtroTipoDeImpuesto() {
		return porcentajeOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5278O</td> <td>% Otro Tipo de Impuesto</td> <td>N(3,2)</td> <td>6</td> <td>374</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeOtroTipoDeImpuesto(Double porcentajeOtroTipoDeImpuesto) {
		this.porcentajeOtroTipoDeImpuesto = porcentajeOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 */ 
	public Double getImporteOtroTipoDeImpuesto() {
		return importeOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004O</td> <td>Importe Otro Tipo de Impuesto</td> <td>N(14,3)</td> <td>18</td> <td>380</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteOtroTipoDeImpuesto(Double importeOtroTipoDeImpuesto) {
		this.importeOtroTipoDeImpuesto = importeOtroTipoDeImpuesto;
	}

	/** 
	 * F1154L - Número Pedido: Solo se utilizará para facturas con más de un pedido que no son recapitulativas. Indica el  número de pedido del articulo. En caso de un artículo con más de un pedido se indicará una línea de	factura por cada uno de sus pedidos.
	 */ 
	public String getNumeroPedido_ON_() {
		return numeroPedido_ON_;
	}

	/** 
	 * F1154L - Número Pedido: Solo se utilizará para facturas con más de un pedido que no son recapitulativas. Indica el  número de pedido del articulo. En caso de un artículo con más de un pedido se indicará una línea de	factura por cada uno de sus pedidos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154L</td> <td>Número Pedido (ON)</td> <td>C</td> <td>17</td> <td>398</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroPedido_ON_(String numeroPedido_ON_) {
		this.numeroPedido_ON_ = numeroPedido_ON_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeAlbaran_DQ_() {
		return numeroDeAlbaran_DQ_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154Q</td> <td>Número de Albarán (DQ)</td> <td>C</td> <td>17</td> <td>415</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeAlbaran_DQ_(String numeroDeAlbaran_DQ_) {
		this.numeroDeAlbaran_DQ_ = numeroDeAlbaran_DQ_;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeEmbalajes() {
		return numeroDeEmbalajes;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7224P</td> <td>Número de Embalajes</td> <td>N</td> <td>8</td> <td>432</td> <td>N</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeEmbalajes(Integer numeroDeEmbalajes) {
		this.numeroDeEmbalajes = numeroDeEmbalajes;
	}

	/** 
	 * 
	 */ 
	public String getTipoDeEmbalaje() {
		return tipoDeEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F7065P</td> <td>Tipo de Embalaje</td> <td>C</td> <td>7</td> <td>440</td> <td>N</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeEmbalaje(String tipoDeEmbalaje) {
		this.tipoDeEmbalaje = tipoDeEmbalaje;
	}

	/** 
	 * 
	 */ 
	public Double getImporteTotalBrutoDeLaLineaDeArticulo_98_() {
		return importeTotalBrutoDeLaLineaDeArticulo_98_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004J</td> <td>Importe Total Bruto de la Línea de Articulo (98)</td> <td>N(14,3)</td> <td>18</td> <td>447</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalBrutoDeLaLineaDeArticulo_98_(Double importeTotalBrutoDeLaLineaDeArticulo_98_) {
		this.importeTotalBrutoDeLaLineaDeArticulo_98_ = importeTotalBrutoDeLaLineaDeArticulo_98_;
	}

	/** 
	 * F6411C - Unidad de Medida del producto que se está facturando:  Solo se utilizará si el producto es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum F6411C {
		KILOGRAM_KGM("KGM"),
		LITR_LTR("LTR"),
		;
		
		private String value;
		
		private F6411C(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F6411C enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * F6411P - Unidad de Medida del Precio: Solo se utilizará si el articulo es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum F6411P {
		KILOGRAM_KGM("KGM"),
		LITR_LTR("LTR"),
		;
		
		private String value;
		
		private F6411P(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F6411P enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * F5153I - Calificador de IVA o IGIC: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum F5153I {
		IV_VAT("VAT"),
		IGI_IGI("IGI"),
		;
		
		private String value;
		
		private F5153I(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F5153I enumByValue(String value) {
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