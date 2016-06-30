package com.esferalia.aon.file.seres.udapa.sales.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1L entity.
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
 * 		<td>ERE1L</th>
 * 		<td>Líneas</th>
 * 		<td>Obligatorio</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class ERE1L {

	private String lineas;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private Integer numeroDeLineaArticulo;
	private String codigoDeArticuloEAN_13ODUN_14;
	private String tipoDeNumeroDeArticulo;
	private String descripcionDelArticulo1;
	private String descripcionDelArticulo2;
	private String tipoArticulo;
	private String codigoInternoArticuloProveedor_SA_;
	private String codigoInternoArticuloCliente_IN_;
	private String codigoVariablePromocional_1__PV_;
	private String codigoUnidadDeExpedicion_1__EN_;
	private Double cantidadPedida_21_;
	private Double cantidadBonificada_192_;
	private String calificadorUnidadDeMedidaCantidad;
	private Double numeroDeU_C_EnUnidadDeExpedicion;
	private String calificadorFechaDeEntrega_3_63_64_PER_;
	private Integer fechaDeEntrega1;
	private Integer horaDeEntrega1;
	private Integer fechaDeEntrega2;
	private Integer horaDeEntrega2;
	private Double importeTotalNetoLinea_203_;
	private Double precioBrutoUnitario_AAB_;
	private Double precioNetoUnitario_AAA_;
	private Double precioATituloInformativo_INF_;
	private String calificadorUnidadDeMedidaPrecio;
	private String calificadorIVA_IGIG;
	private Double porcentajeImpuestoIVA_IGIC;
	private Double importeImpuestoIVA_IGIC;
	private Double porcentajeRecargoDeEquivalencia;
	private Double importeRecargoDeEquivalencia;
	private String calificadorOtroTipoDeImpuesto;
	private Double porcentajeOtroTipoDeImpuesto;
	private Double importeOtroTipoDeImpuesto;
	private Double pesoNeto_PD__AAA_;
	private String unidadDeMedidaPeso;
	private String tipoArticuloEAN_CU_DU_;
	private String descripcionDelModelo_BRN_;
	private String variedad1_35_;
	private String variedad2_UP5_;
	private String presentacion_Cantidad_Formato_U03_;


	private static Pattern PATTERN_ERE1L_lineas = Pattern.compile("^(.{6}).*");
	private static Pattern PATTERN_ERE1L_tipoDePedido_220_221_224_226_22E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ERE1L_numeroDePedido = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_ERE1L_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_ERE1L_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_ERE1L_numeroDeLineaArticulo = Pattern.compile("^.{63}(.{6}).*");
	private static Pattern PATTERN_ERE1L_codigoDeArticuloEAN_13ODUN_14 = Pattern.compile("^.{69}(.{15}).*");
	private static Pattern PATTERN_ERE1L_tipoDeNumeroDeArticulo = Pattern.compile("^.{84}(.{3}).*");
	private static Pattern PATTERN_ERE1L_descripcionDelArticulo1 = Pattern.compile("^.{87}(.{70}).*");
	private static Pattern PATTERN_ERE1L_descripcionDelArticulo2 = Pattern.compile("^.{157}(.{70}).*");
	private static Pattern PATTERN_ERE1L_tipoArticulo = Pattern.compile("^.{227}(.{1}).*");
	private static Pattern PATTERN_ERE1L_codigoInternoArticuloProveedor_SA_ = Pattern.compile("^.{228}(.{35}).*");
	private static Pattern PATTERN_ERE1L_codigoInternoArticuloCliente_IN_ = Pattern.compile("^.{263}(.{35}).*");
	private static Pattern PATTERN_ERE1L_codigoVariablePromocional_1__PV_ = Pattern.compile("^.{298}(.{35}).*");
	private static Pattern PATTERN_ERE1L_codigoUnidadDeExpedicion_1__EN_ = Pattern.compile("^.{333}(.{35}).*");
	private static Pattern PATTERN_ERE1L_cantidadPedida_21_ = Pattern.compile("^.{368}(.{16}).*");
	private static Pattern PATTERN_ERE1L_cantidadBonificada_192_ = Pattern.compile("^.{384}(.{16}).*");
	private static Pattern PATTERN_ERE1L_calificadorUnidadDeMedidaCantidad = Pattern.compile("^.{400}(.{6}).*");
	private static Pattern PATTERN_ERE1L_numeroDeU_C_EnUnidadDeExpedicion = Pattern.compile("^.{406}(.{16}).*");
	private static Pattern PATTERN_ERE1L_calificadorFechaDeEntrega_3_63_64_PER_ = Pattern.compile("^.{422}(.{3}).*");
	private static Pattern PATTERN_ERE1L_fechaDeEntrega1 = Pattern.compile("^.{425}(.{8}).*");
	private static Pattern PATTERN_ERE1L_horaDeEntrega1 = Pattern.compile("^.{433}(.{4}).*");
	private static Pattern PATTERN_ERE1L_fechaDeEntrega2 = Pattern.compile("^.{437}(.{8}).*");
	private static Pattern PATTERN_ERE1L_horaDeEntrega2 = Pattern.compile("^.{445}(.{4}).*");
	private static Pattern PATTERN_ERE1L_importeTotalNetoLinea_203_ = Pattern.compile("^.{449}(.{18}).*");
	private static Pattern PATTERN_ERE1L_precioBrutoUnitario_AAB_ = Pattern.compile("^.{467}(.{16}).*");
	private static Pattern PATTERN_ERE1L_precioNetoUnitario_AAA_ = Pattern.compile("^.{483}(.{16}).*");
	private static Pattern PATTERN_ERE1L_precioATituloInformativo_INF_ = Pattern.compile("^.{499}(.{16}).*");
	private static Pattern PATTERN_ERE1L_calificadorUnidadDeMedidaPrecio = Pattern.compile("^.{515}(.{6}).*");
	private static Pattern PATTERN_ERE1L_calificadorIVA_IGIG = Pattern.compile("^.{521}(.{6}).*");
	private static Pattern PATTERN_ERE1L_porcentajeImpuestoIVA_IGIC = Pattern.compile("^.{527}(.{6}).*");
	private static Pattern PATTERN_ERE1L_importeImpuestoIVA_IGIC = Pattern.compile("^.{533}(.{18}).*");
	private static Pattern PATTERN_ERE1L_porcentajeRecargoDeEquivalencia = Pattern.compile("^.{551}(.{6}).*");
	private static Pattern PATTERN_ERE1L_importeRecargoDeEquivalencia = Pattern.compile("^.{557}(.{18}).*");
	private static Pattern PATTERN_ERE1L_calificadorOtroTipoDeImpuesto = Pattern.compile("^.{575}(.{6}).*");
	private static Pattern PATTERN_ERE1L_porcentajeOtroTipoDeImpuesto = Pattern.compile("^.{581}(.{6}).*");
	private static Pattern PATTERN_ERE1L_importeOtroTipoDeImpuesto = Pattern.compile("^.{587}(.{18}).*");
	private static Pattern PATTERN_ERE1L_pesoNeto_PD__AAA_ = Pattern.compile("^.{605}(.{18}).*");
	private static Pattern PATTERN_ERE1L_unidadDeMedidaPeso = Pattern.compile("^.{623}(.{6}).*");
	private static Pattern PATTERN_ERE1L_tipoArticuloEAN_CU_DU_ = Pattern.compile("^.{629}(.{17}).*");
	private static Pattern PATTERN_ERE1L_descripcionDelModelo_BRN_ = Pattern.compile("^.{646}(.{25}).*");
	private static Pattern PATTERN_ERE1L_variedad1_35_ = Pattern.compile("^.{681}(.{25}).*");
	private static Pattern PATTERN_ERE1L_variedad2_UP5_ = Pattern.compile("^.{716}(.{25}).*");
	private static Pattern PATTERN_ERE1L_presentacion_Cantidad_Formato_U03_ = Pattern.compile("^.{751}(.{25}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1L_lineas.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setLineas(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_tipoDePedido_220_221_224_226_22E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDePedido_220_221_224_226_22E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroDePedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroDeLineaArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoDeArticuloEAN_13ODUN_14.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDeArticuloEAN_13ODUN_14(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_tipoDeNumeroDeArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeNumeroDeArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_descripcionDelArticulo1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelArticulo1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_descripcionDelArticulo2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelArticulo2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_tipoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoInternoArticuloProveedor_SA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInternoArticuloProveedor_SA_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoInternoArticuloCliente_IN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInternoArticuloCliente_IN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoVariablePromocional_1__PV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVariablePromocional_1__PV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoUnidadDeExpedicion_1__EN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoUnidadDeExpedicion_1__EN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_cantidadPedida_21_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadPedida_21_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_cantidadBonificada_192_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadBonificada_192_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorUnidadDeMedidaCantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedidaCantidad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroDeU_C_EnUnidadDeExpedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeU_C_EnUnidadDeExpedicion(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorFechaDeEntrega_3_63_64_PER_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFechaDeEntrega_3_63_64_PER_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_fechaDeEntrega1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeEntrega1(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_horaDeEntrega1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setHoraDeEntrega1(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_fechaDeEntrega2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeEntrega2(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_horaDeEntrega2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setHoraDeEntrega2(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_importeTotalNetoLinea_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalNetoLinea_203_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_precioBrutoUnitario_AAB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioBrutoUnitario_AAB_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_precioNetoUnitario_AAA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioNetoUnitario_AAA_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_precioATituloInformativo_INF_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioATituloInformativo_INF_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorUnidadDeMedidaPrecio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedidaPrecio(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorIVA_IGIG.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorIVA_IGIG(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_porcentajeImpuestoIVA_IGIC.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeImpuestoIVA_IGIC(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_importeImpuestoIVA_IGIC.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteImpuestoIVA_IGIC(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_porcentajeRecargoDeEquivalencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeRecargoDeEquivalencia(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_importeRecargoDeEquivalencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteRecargoDeEquivalencia(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorOtroTipoDeImpuesto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_porcentajeOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeOtroTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_importeOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteOtroTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_pesoNeto_PD__AAA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoNeto_PD__AAA_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_unidadDeMedidaPeso.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaPeso(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_tipoArticuloEAN_CU_DU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoArticuloEAN_CU_DU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_descripcionDelModelo_BRN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelModelo_BRN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_variedad1_35_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setVariedad1_35_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_variedad2_UP5_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setVariedad2_UP5_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_presentacion_Cantidad_Formato_U03_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPresentacion_Cantidad_Formato_U03_(String.valueOf(m.group(1).trim()));
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
	 * 		<td>ERE1L</th>
	 * 		<td>Líneas</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getLineas() {
		return lineas;
	}
	public void setLineas(String lineas) {
		this.lineas = lineas;
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
	 * C1082L - Número de Línea Artículo: Contador Secuencial.
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
	 * 		<td>C7140E</th>
	 * 		<td>Código de Artículo EAN-13 o DUN-14</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>70</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoDeArticuloEAN_13ODUN_14() {
		return codigoDeArticuloEAN_13ODUN_14;
	}
	public void setCodigoDeArticuloEAN_13ODUN_14(String codigoDeArticuloEAN_13ODUN_14) {
		this.codigoDeArticuloEAN_13ODUN_14 = codigoDeArticuloEAN_13ODUN_14;
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
	 * 		<td>C7140E</th>
	 * 		<td>Tipo de Número de Artículo</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>85</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDeNumeroDeArticulo() {
		return tipoDeNumeroDeArticulo;
	}
	public void setTipoDeNumeroDeArticulo(String tipoDeNumeroDeArticulo) {
		this.tipoDeNumeroDeArticulo = tipoDeNumeroDeArticulo;
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
	 * 		<td>C7008D</th>
	 * 		<td>Descripción del Artículo 1</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>88</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDescripcionDelArticulo1() {
		return descripcionDelArticulo1;
	}
	public void setDescripcionDelArticulo1(String descripcionDelArticulo1) {
		this.descripcionDelArticulo1 = descripcionDelArticulo1;
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
	 * 		<td>C70082</th>
	 * 		<td>Descripción del Artículo 2</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>158</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDescripcionDelArticulo2() {
		return descripcionDelArticulo2;
	}
	public void setDescripcionDelArticulo2(String descripcionDelArticulo2) {
		this.descripcionDelArticulo2 = descripcionDelArticulo2;
	}

	/** 
	 * C7081A - Tipo Artículo: Los valores posibles son:
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
	 * 		<td>C7081A</th>
	 * 		<td>Tipo Artículo</th>
	 * 		<td>C</th>
	 * 		<td>1</th>
	 * 		<td>228</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoArticulo() {
		return tipoArticulo;
	}
	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
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
	 * 		<td>C7140P</th>
	 * 		<td>Código Interno Artículo Proveedor (SA)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>229</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoInternoArticuloProveedor_SA_() {
		return codigoInternoArticuloProveedor_SA_;
	}
	public void setCodigoInternoArticuloProveedor_SA_(String codigoInternoArticuloProveedor_SA_) {
		this.codigoInternoArticuloProveedor_SA_ = codigoInternoArticuloProveedor_SA_;
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
	 * 		<td>C7140C</th>
	 * 		<td>Código Interno Artículo Cliente (IN)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>264</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoInternoArticuloCliente_IN_() {
		return codigoInternoArticuloCliente_IN_;
	}
	public void setCodigoInternoArticuloCliente_IN_(String codigoInternoArticuloCliente_IN_) {
		this.codigoInternoArticuloCliente_IN_ = codigoInternoArticuloCliente_IN_;
	}

	/** 
	 * C7140V - Código Variable Promocional (1) (PV):  El Código Variable Promocional del Producto hace referencia a una promoción para la que no se ha generado un nuevo código de Barras. Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
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
	 * 		<td>C7140V</th>
	 * 		<td>Código Variable Promocional (1) (PV)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>299</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoVariablePromocional_1__PV_() {
		return codigoVariablePromocional_1__PV_;
	}
	public void setCodigoVariablePromocional_1__PV_(String codigoVariablePromocional_1__PV_) {
		this.codigoVariablePromocional_1__PV_ = codigoVariablePromocional_1__PV_;
	}

	/** 
	 * C7140D - Código Unidad de Expedición (1) (EN): Código que hace referencia a la forma de distribución del producto (por ej: cajas de unidades).
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
	 * 		<td>C7140D</th>
	 * 		<td>Código Unidad de Expedición (1) (EN)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>334</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoUnidadDeExpedicion_1__EN_() {
		return codigoUnidadDeExpedicion_1__EN_;
	}
	public void setCodigoUnidadDeExpedicion_1__EN_(String codigoUnidadDeExpedicion_1__EN_) {
		this.codigoUnidadDeExpedicion_1__EN_ = codigoUnidadDeExpedicion_1__EN_;
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
	 * 		<td>C6060C</th>
	 * 		<td>Cantidad Pedida (21)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>369</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getCantidadPedida_21_() {
		return cantidadPedida_21_;
	}
	public void setCantidadPedida_21_(Double cantidadPedida_21_) {
		this.cantidadPedida_21_ = cantidadPedida_21_;
	}

	/** 
	 * C6060B - Cantidad Bonificada (192): Cantidad de Mercancías sin Cargo. Si se bonifica con el mismo artículo, la cantidad bonificada puede encontrarse en este campo o en el fichero de descuentos ERE1D.
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
	 * 		<td>C6060B</th>
	 * 		<td>Cantidad Bonificada (192)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>385</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getCantidadBonificada_192_() {
		return cantidadBonificada_192_;
	}
	public void setCantidadBonificada_192_(Double cantidadBonificada_192_) {
		this.cantidadBonificada_192_ = cantidadBonificada_192_;
	}

	/** 
	 * C6411C - Calificador Unidad de Medida Cantidad: Solo se utilizará si el producto es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>C6411C</th>
	 * 		<td>Calificador Unidad de Medida Cantidad</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>401</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorUnidadDeMedidaCantidad() {
		return calificadorUnidadDeMedidaCantidad;
	}
	public void setCalificadorUnidadDeMedidaCantidad(String calificadorUnidadDeMedidaCantidad) {
		this.calificadorUnidadDeMedidaCantidad = calificadorUnidadDeMedidaCantidad;
	}

	/** 
	 * C6060U - Número de U.C. en Unidad de Expedición: Se usará solo para indicar en productos de peso variable, la cantidad de unidades que se piden.
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
	 * 		<td>C6060U</th>
	 * 		<td>Número de U.C. en Unidad de Expedición</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>407</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getNumeroDeU_C_EnUnidadDeExpedicion() {
		return numeroDeU_C_EnUnidadDeExpedicion;
	}
	public void setNumeroDeU_C_EnUnidadDeExpedicion(Double numeroDeU_C_EnUnidadDeExpedicion) {
		this.numeroDeU_C_EnUnidadDeExpedicion = numeroDeU_C_EnUnidadDeExpedicion;
	}

	/** 
	 * C2005F - Calificador Fecha de Entrega (69-63-64-PE): Los valores posibles son:
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
	 * 		<td>C2005F</th>
	 * 		<td>Calificador Fecha de Entrega (3-63-64-PER)</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>423</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorFechaDeEntrega_3_63_64_PER_() {
		return calificadorFechaDeEntrega_3_63_64_PER_;
	}
	public void setCalificadorFechaDeEntrega_3_63_64_PER_(String calificadorFechaDeEntrega_3_63_64_PER_) {
		this.calificadorFechaDeEntrega_3_63_64_PER_ = calificadorFechaDeEntrega_3_63_64_PER_;
	}

	/** 
	 * C2380J - Fecha de Entrega 1: Fecha de entrega si el calificador es 69 o 64 o 63  en formato AAAAMMDD
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
	 * 		<td>C2380J</th>
	 * 		<td>Fecha de Entrega 1</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>426</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Integer getFechaDeEntrega1() {
		return fechaDeEntrega1;
	}
	public void setFechaDeEntrega1(Integer fechaDeEntrega1) {
		this.fechaDeEntrega1 = fechaDeEntrega1;
	}

	/** 
	 * C2380L - Hora de Entrega 1: En formato HHMM
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
	 * 		<td>C2380L</th>
	 * 		<td>Hora de Entrega 1</th>
	 * 		<td>N</th>
	 * 		<td>4</th>
	 * 		<td>434</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Integer getHoraDeEntrega1() {
		return horaDeEntrega1;
	}
	public void setHoraDeEntrega1(Integer horaDeEntrega1) {
		this.horaDeEntrega1 = horaDeEntrega1;
	}

	/** 
	 * C2380K - Fecha de Entrega 2: Fecha de entrega límite si el calificador es PER en formato AAAAMMDD
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
	 * 		<td>C2380K</th>
	 * 		<td>Fecha de Entrega 2</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>438</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Integer getFechaDeEntrega2() {
		return fechaDeEntrega2;
	}
	public void setFechaDeEntrega2(Integer fechaDeEntrega2) {
		this.fechaDeEntrega2 = fechaDeEntrega2;
	}

	/** 
	 * C2380M - Hora de Entrega 2: En formato HHMM
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
	 * 		<td>C2380M</th>
	 * 		<td>Hora de Entrega 2</th>
	 * 		<td>N</th>
	 * 		<td>4</th>
	 * 		<td>446</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Integer getHoraDeEntrega2() {
		return horaDeEntrega2;
	}
	public void setHoraDeEntrega2(Integer horaDeEntrega2) {
		this.horaDeEntrega2 = horaDeEntrega2;
	}

	/** 
	 * C5004L - Importe Total Neto Línea (203): Incluye descuentos y cargos, no incluye impuestos.
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
	 * 		<td>C5004L</th>
	 * 		<td>Importe Total Neto Línea (203)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>450</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getImporteTotalNetoLinea_203_() {
		return importeTotalNetoLinea_203_;
	}
	public void setImporteTotalNetoLinea_203_(Double importeTotalNetoLinea_203_) {
		this.importeTotalNetoLinea_203_ = importeTotalNetoLinea_203_;
	}

	/** 
	 * C5118B - Precio Bruto Unitario (AAB): Excluye descuentos, cargos e impuestos.
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
	 * 		<td>C5118B</th>
	 * 		<td>Precio Bruto Unitario (AAB)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>468</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPrecioBrutoUnitario_AAB_() {
		return precioBrutoUnitario_AAB_;
	}
	public void setPrecioBrutoUnitario_AAB_(Double precioBrutoUnitario_AAB_) {
		this.precioBrutoUnitario_AAB_ = precioBrutoUnitario_AAB_;
	}

	/** 
	 * C5118N - Precio Neto Unitario  (AAA):Incluye descuentos y cargos pero no impuestos..
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
	 * 		<td>C5118N</th>
	 * 		<td>Precio Neto Unitario  (AAA)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>484</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPrecioNetoUnitario_AAA_() {
		return precioNetoUnitario_AAA_;
	}
	public void setPrecioNetoUnitario_AAA_(Double precioNetoUnitario_AAA_) {
		this.precioNetoUnitario_AAA_ = precioNetoUnitario_AAA_;
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
	 * 		<td>C5118I</th>
	 * 		<td>Precio a titulo Informativo  (INF)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>500</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPrecioATituloInformativo_INF_() {
		return precioATituloInformativo_INF_;
	}
	public void setPrecioATituloInformativo_INF_(Double precioATituloInformativo_INF_) {
		this.precioATituloInformativo_INF_ = precioATituloInformativo_INF_;
	}

	/** 
	 * C6411P - Calificador Unidad de Medida Precio: Solo se utilizará si el articulo es de medida variable. Los  Valores posibles son:
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
	 * 		<td>C6411P</th>
	 * 		<td>Calificador Unidad de Medida Precio</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>516</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorUnidadDeMedidaPrecio() {
		return calificadorUnidadDeMedidaPrecio;
	}
	public void setCalificadorUnidadDeMedidaPrecio(String calificadorUnidadDeMedidaPrecio) {
		this.calificadorUnidadDeMedidaPrecio = calificadorUnidadDeMedidaPrecio;
	}

	/** 
	 * C5153I - Calificador IVA/IGIC: El campo corresponde a un código EANCOM. Los Valores posibles son:
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
	 * 		<td>C5153I</th>
	 * 		<td>Calificador IVA/IGIG</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>522</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorIVA_IGIG() {
		return calificadorIVA_IGIG;
	}
	public void setCalificadorIVA_IGIG(String calificadorIVA_IGIG) {
		this.calificadorIVA_IGIG = calificadorIVA_IGIG;
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
	 * 		<td>C5278V</th>
	 * 		<td>% Impuesto  IVA/IGIC</th>
	 * 		<td>N(3,2)</th>
	 * 		<td>6</th>
	 * 		<td>528</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPorcentajeImpuestoIVA_IGIC() {
		return porcentajeImpuestoIVA_IGIC;
	}
	public void setPorcentajeImpuestoIVA_IGIC(Double porcentajeImpuestoIVA_IGIC) {
		this.porcentajeImpuestoIVA_IGIC = porcentajeImpuestoIVA_IGIC;
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
	 * 		<td>C5004V</th>
	 * 		<td>Importe Impuesto IVA/IGIC</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>534</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getImporteImpuestoIVA_IGIC() {
		return importeImpuestoIVA_IGIC;
	}
	public void setImporteImpuestoIVA_IGIC(Double importeImpuestoIVA_IGIC) {
		this.importeImpuestoIVA_IGIC = importeImpuestoIVA_IGIC;
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
	 * 		<td>C5278R</th>
	 * 		<td>% Recargo de Equivalencia</th>
	 * 		<td>N(3,2)</th>
	 * 		<td>6</th>
	 * 		<td>552</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPorcentajeRecargoDeEquivalencia() {
		return porcentajeRecargoDeEquivalencia;
	}
	public void setPorcentajeRecargoDeEquivalencia(Double porcentajeRecargoDeEquivalencia) {
		this.porcentajeRecargoDeEquivalencia = porcentajeRecargoDeEquivalencia;
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
	 * 		<td>C5004R</th>
	 * 		<td>Importe Recargo de Equivalencia</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>558</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getImporteRecargoDeEquivalencia() {
		return importeRecargoDeEquivalencia;
	}
	public void setImporteRecargoDeEquivalencia(Double importeRecargoDeEquivalencia) {
		this.importeRecargoDeEquivalencia = importeRecargoDeEquivalencia;
	}

	/** 
	 * C5153O - Calificador Otro Tipo de Impuesto: Campo Opcional, corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>C5153O</th>
	 * 		<td>Calificador Otro Tipo de Impuesto</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>576</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorOtroTipoDeImpuesto() {
		return calificadorOtroTipoDeImpuesto;
	}
	public void setCalificadorOtroTipoDeImpuesto(String calificadorOtroTipoDeImpuesto) {
		this.calificadorOtroTipoDeImpuesto = calificadorOtroTipoDeImpuesto;
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
	 * 		<td>C5278O</th>
	 * 		<td>% Otro Tipo de Impuesto</th>
	 * 		<td>N(3,2)</th>
	 * 		<td>6</th>
	 * 		<td>582</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPorcentajeOtroTipoDeImpuesto() {
		return porcentajeOtroTipoDeImpuesto;
	}
	public void setPorcentajeOtroTipoDeImpuesto(Double porcentajeOtroTipoDeImpuesto) {
		this.porcentajeOtroTipoDeImpuesto = porcentajeOtroTipoDeImpuesto;
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
	 * 		<td>C5004O</th>
	 * 		<td>Importe Otro Tipo de Impuesto</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>588</th>
	 * 		<td>-</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getImporteOtroTipoDeImpuesto() {
		return importeOtroTipoDeImpuesto;
	}
	public void setImporteOtroTipoDeImpuesto(Double importeOtroTipoDeImpuesto) {
		this.importeOtroTipoDeImpuesto = importeOtroTipoDeImpuesto;
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
	 * 		<td>C6314N</th>
	 * 		<td>Peso Neto (PD) (AAA)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>606</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getPesoNeto_PD__AAA_() {
		return pesoNeto_PD__AAA_;
	}
	public void setPesoNeto_PD__AAA_(Double pesoNeto_PD__AAA_) {
		this.pesoNeto_PD__AAA_ = pesoNeto_PD__AAA_;
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
	 * 		<td>C6411N</th>
	 * 		<td>Unidad de Medida Peso</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>624</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getUnidadDeMedidaPeso() {
		return unidadDeMedidaPeso;
	}
	public void setUnidadDeMedidaPeso(String unidadDeMedidaPeso) {
		this.unidadDeMedidaPeso = unidadDeMedidaPeso;
	}

	/** 
	 * C7009A - Tipo Articulo EAN (CU/DU): Los valores posibles son:
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
	 * 		<td>C7009A</th>
	 * 		<td>Tipo Articulo EAN (CU/DU)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>630</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoArticuloEAN_CU_DU_() {
		return tipoArticuloEAN_CU_DU_;
	}
	public void setTipoArticuloEAN_CU_DU_(String tipoArticuloEAN_CU_DU_) {
		this.tipoArticuloEAN_CU_DU_ = tipoArticuloEAN_CU_DU_;
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
	 * 		<td>C7008M</th>
	 * 		<td>Descripción del Modelo (BRN)</th>
	 * 		<td>C</th>
	 * 		<td>25</th>
	 * 		<td>647</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDescripcionDelModelo_BRN_() {
		return descripcionDelModelo_BRN_;
	}
	public void setDescripcionDelModelo_BRN_(String descripcionDelModelo_BRN_) {
		this.descripcionDelModelo_BRN_ = descripcionDelModelo_BRN_;
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
	 * 		<td>C7008V</th>
	 * 		<td>Variedad 1 (35)</th>
	 * 		<td>C</th>
	 * 		<td>25</th>
	 * 		<td>682</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getVariedad1_35_() {
		return variedad1_35_;
	}
	public void setVariedad1_35_(String variedad1_35_) {
		this.variedad1_35_ = variedad1_35_;
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
	 * 		<td>C7008W</th>
	 * 		<td>Variedad 2 (UP5)</th>
	 * 		<td>C</th>
	 * 		<td>25</th>
	 * 		<td>717</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getVariedad2_UP5_() {
		return variedad2_UP5_;
	}
	public void setVariedad2_UP5_(String variedad2_UP5_) {
		this.variedad2_UP5_ = variedad2_UP5_;
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
	 * 		<td>C7008P</th>
	 * 		<td>Presentación, cantidad, formato (U03)</th>
	 * 		<td>C</th>
	 * 		<td>25</th>
	 * 		<td>752</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPresentacion_Cantidad_Formato_U03_() {
		return presentacion_Cantidad_Formato_U03_;
	}
	public void setPresentacion_Cantidad_Formato_U03_(String presentacion_Cantidad_Formato_U03_) {
		this.presentacion_Cantidad_Formato_U03_ = presentacion_Cantidad_Formato_U03_;
	}

	/** 
	 * C7143E - Tipo de Número de Artículo: Los valores posibles son:
	 */
	public enum C7143E {
		INTERNATIONAL_ARTICLE_NUMBERING_ASSOCIATION__EAN_EN("EN"),
		UPC__UNIVERSAL_PRODUCT_CODE_UP("UP"),
		;
		
		private String value;
		
		private C7143E(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C7143E enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C7081A - Tipo Artículo: Los valores posibles son:
	 */
	public enum C7081A {
		MERCANCI_M("M"),
		SERVICI_S("S"),
		MATERIAL_CONSIGNAD_C("C"),
		;
		
		private String value;
		
		private C7081A(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C7081A enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C6411C - Calificador Unidad de Medida Cantidad: Solo se utilizará si el producto es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum C6411C {
		KILOGRAM_KGM("KGM"),
		LITR_LTR("LTR"),
		;
		
		private String value;
		
		private C6411C(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C6411C enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C2005F - Calificador Fecha de Entrega (69-63-64-PE): Los valores posibles son:
	 */
	public enum C2005F {
		FECHA___HORA_DE_ENTREGA_FIJ_69("69"),
		ENTREGAR_A_PARTIR_DE_FECHA___HOR_64("64"),
		FECHA___HORA_LIMITE_DE_ENTREG_63("63"),
		INDICA_UN_PERIODO_DE_ENTREGA__64__63_PER("PER"),
		KILOGRAM_KGM("KGM"),
		LITR_LTR("LTR"),
		IV_VAT("VAT"),
		IGI_IGI("IGI"),
		;
		
		private String value;
		
		private C2005F(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C2005F enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C5153O - Calificador Otro Tipo de Impuesto: Campo Opcional, corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum C5153O {
		EXENTO_DE_IV_EXT("EXT"),
		IMPUESTO_DE_ALCOHOLE_ACT("ACT"),
		CANON_DE_SOCIEDAD_DE_AUTORE_CSA("CSA"),
		RETENCIONES_DE_SERVICIOS_PROFESIONALE_RET("RET"),
		OTRO_OTH("OTH"),
		;
		
		private String value;
		
		private C5153O(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C5153O enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C7009A - Tipo Articulo EAN (CU/DU): Los valores posibles son:
	 */
	public enum C7009A {
		CONSUMER_UNIT__EAN_CODE_CU("CU"),
		DESPATCH_UNIT__EAN_CODE_DU("DU"),
		TRADED_UNIT__EAN_CODE_TU("TU"),
		VARIABLE_QUANTITY_PRODUCT__EAN_CODE_VQ("VQ"),
		;
		
		private String value;
		
		private C7009A(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C7009A enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}