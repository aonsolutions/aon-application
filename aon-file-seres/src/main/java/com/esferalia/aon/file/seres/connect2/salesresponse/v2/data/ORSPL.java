package com.esferalia.aon.file.seres.connect2.salesresponse.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ORSPL {
	private Integer numeroDeLineaArticulo;
	private String solicitudDeAccionONotificacion;
	private String codigoEAN_13_DUN_14DelArticulo;
	private String tipoCodigoArticulo;
	private String tipoIdentificacionDeArticulo_CU_DU_;
	private String descripcion1Articulo;
	private String descripcion2Articulo;
	private String tipoArticulo;
	private String numeroArticuloProveedor_SA_;
	private String numeroArticuloComprador_IN_BP_;
	private String variablePromocional_PV_;
	private String codigoEANDelArticuloAdicional_1_EN_;
	private Double cantidadPedida_21_;
	private Double cantidadBonificada_192_;
	private String calificadorUnidadDeMedida;
	private Double numeroUnidadesDeConsumoEnU_Expedicion;
	private Double variacionEnCantidad;
	private String codigoDiscrepancia;
	private String razonDelCambio;
	private String calificadorFecha_Hora1_2_11_64_;
	private String fecha_Hora1;
	private String calificadorFecha_Hora2_2_11_63_;
	private String fecha_Hora2;
	private Double importeNetoLinea_203_;
	private Double importeLineaConImpuestos_388_;
	private Double precioBrutoUnitario_AAB_;
	private Double precioNetoUnitario_AAA_;
	private Double precioATituloInformativo_INF_;
	private String calificadorUnidadDeMedidaPrecio;
	private Double unidadBasePrecio;
	private String calificadorIVA_IGIC;
	private Double porcentajeIVA_IGIC;
	private Double importeIVA_IGIC;
	private Double porcentajeRecargoDeEquivalencia;
	private Double importeRecargoDeEquivalencia;
	private String calificadorOtroTipoDeImpuesto;
	private Double porcentajeOtroTipoDeImpuesto;
	private Double importeOtroTipoDeImpuesto;
	private Double pesoNeto_PD_AAA_;
	private String calificadorUnidadDeMedidaPeso;
	private String descripcionDelModelo_BRN_;
	private String color_35_;
	private String anchuraOTalla_UP5_;
	private String presentacion_cantidad_formato_U03_;
	private Double cantidadAceptada_enviada_12_;
	private Double cantidadRechazada_83_;
	private Double cancelledQuantity_QTY_182_;
	private Double rejectedQuantity_QTY_185_;
	private Integer numeroDeEmbalajes_PAC_;
	private String identificacionDelTipoDeEmbalaje_PAC_;
	
	private static Pattern PATTERN_ORSPL_numeroDeLineaArticulo = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ORSPL_solicitudDeAccionONotificacion = Pattern.compile("^.{12}(.{3}).*");
	private static Pattern PATTERN_ORSPL_codigoEAN_13_DUN_14DelArticulo = Pattern.compile("^.{15}(.{15}).*");
	private static Pattern PATTERN_ORSPL_tipoCodigoArticulo = Pattern.compile("^.{30}(.{3}).*");
	private static Pattern PATTERN_ORSPL_tipoIdentificacionDeArticulo_CU_DU_ = Pattern.compile("^.{33}(.{17}).*");
	private static Pattern PATTERN_ORSPL_descripcion1Articulo = Pattern.compile("^.{50}(.{70}).*");
	private static Pattern PATTERN_ORSPL_descripcion2Articulo = Pattern.compile("^.{120}(.{70}).*");
	private static Pattern PATTERN_ORSPL_tipoArticulo = Pattern.compile("^.{190}(.{1}).*");
	private static Pattern PATTERN_ORSPL_numeroArticuloProveedor_SA_ = Pattern.compile("^.{191}(.{35}).*");
	private static Pattern PATTERN_ORSPL_numeroArticuloComprador_IN_BP_ = Pattern.compile("^.{226}(.{35}).*");
	private static Pattern PATTERN_ORSPL_variablePromocional_PV_ = Pattern.compile("^.{261}(.{35}).*");
	private static Pattern PATTERN_ORSPL_codigoEANDelArticuloAdicional_1_EN_ = Pattern.compile("^.{296}(.{35}).*");
	private static Pattern PATTERN_ORSPL_cantidadPedida_21_ = Pattern.compile("^.{331}(.{16}).*");
	private static Pattern PATTERN_ORSPL_cantidadBonificada_192_ = Pattern.compile("^.{347}(.{16}).*");
	private static Pattern PATTERN_ORSPL_calificadorUnidadDeMedida = Pattern.compile("^.{363}(.{6}).*");
	private static Pattern PATTERN_ORSPL_numeroUnidadesDeConsumoEnU_Expedicion = Pattern.compile("^.{369}(.{16}).*");
	private static Pattern PATTERN_ORSPL_variacionEnCantidad = Pattern.compile("^.{385}(.{16}).*");
	private static Pattern PATTERN_ORSPL_codigoDiscrepancia = Pattern.compile("^.{401}(.{3}).*");
	private static Pattern PATTERN_ORSPL_razonDelCambio = Pattern.compile("^.{404}(.{3}).*");
	private static Pattern PATTERN_ORSPL_calificadorFecha_Hora1_2_11_64_ = Pattern.compile("^.{407}(.{3}).*");
	private static Pattern PATTERN_ORSPL_fecha_Hora1 = Pattern.compile("^.{410}(.{12}).*");
	private static Pattern PATTERN_ORSPL_calificadorFecha_Hora2_2_11_63_ = Pattern.compile("^.{422}(.{3}).*");
	private static Pattern PATTERN_ORSPL_fecha_Hora2 = Pattern.compile("^.{425}(.{12}).*");
	private static Pattern PATTERN_ORSPL_importeNetoLinea_203_ = Pattern.compile("^.{437}(.{18}).*");
	private static Pattern PATTERN_ORSPL_importeLineaConImpuestos_388_ = Pattern.compile("^.{455}(.{18}).*");
	private static Pattern PATTERN_ORSPL_precioBrutoUnitario_AAB_ = Pattern.compile("^.{473}(.{16}).*");
	private static Pattern PATTERN_ORSPL_precioNetoUnitario_AAA_ = Pattern.compile("^.{489}(.{16}).*");
	private static Pattern PATTERN_ORSPL_precioATituloInformativo_INF_ = Pattern.compile("^.{505}(.{16}).*");
	private static Pattern PATTERN_ORSPL_calificadorUnidadDeMedidaPrecio = Pattern.compile("^.{521}(.{6}).*");
	private static Pattern PATTERN_ORSPL_unidadBasePrecio = Pattern.compile("^.{527}(.{10}).*");
	private static Pattern PATTERN_ORSPL_calificadorIVA_IGIC = Pattern.compile("^.{537}(.{6}).*");
	private static Pattern PATTERN_ORSPL_porcentajeIVA_IGIC = Pattern.compile("^.{543}(.{6}).*");
	private static Pattern PATTERN_ORSPL_importeIVA_IGIC = Pattern.compile("^.{549}(.{18}).*");
	private static Pattern PATTERN_ORSPL_porcentajeRecargoDeEquivalencia = Pattern.compile("^.{567}(.{6}).*");
	private static Pattern PATTERN_ORSPL_importeRecargoDeEquivalencia = Pattern.compile("^.{573}(.{18}).*");
	private static Pattern PATTERN_ORSPL_calificadorOtroTipoDeImpuesto = Pattern.compile("^.{591}(.{6}).*");
	private static Pattern PATTERN_ORSPL_porcentajeOtroTipoDeImpuesto = Pattern.compile("^.{597}(.{6}).*");
	private static Pattern PATTERN_ORSPL_importeOtroTipoDeImpuesto = Pattern.compile("^.{603}(.{18}).*");
	private static Pattern PATTERN_ORSPL_pesoNeto_PD_AAA_ = Pattern.compile("^.{621}(.{18}).*");
	private static Pattern PATTERN_ORSPL_calificadorUnidadDeMedidaPeso = Pattern.compile("^.{639}(.{6}).*");
	private static Pattern PATTERN_ORSPL_descripcionDelModelo_BRN_ = Pattern.compile("^.{645}(.{25}).*");
	private static Pattern PATTERN_ORSPL_color_35_ = Pattern.compile("^.{670}(.{25}).*");
	private static Pattern PATTERN_ORSPL_anchuraOTalla_UP5_ = Pattern.compile("^.{695}(.{25}).*");
	private static Pattern PATTERN_ORSPL_presentacion_cantidad_formato_U03_ = Pattern.compile("^.{720}(.{25}).*");
	private static Pattern PATTERN_ORSPL_cantidadAceptada_enviada_12_ = Pattern.compile("^.{745}(.{16}).*");
	private static Pattern PATTERN_ORSPL_cantidadRechazada_83_ = Pattern.compile("^.{761}(.{16}).*");
	private static Pattern PATTERN_ORSPL_cancelledQuantity_QTY_182_ = Pattern.compile("^.{777}(.{16}).*");
	private static Pattern PATTERN_ORSPL_rejectedQuantity_QTY_185_ = Pattern.compile("^.{793}(.{16}).*");
	private static Pattern PATTERN_ORSPL_numeroDeEmbalajes_PAC_ = Pattern.compile("^.{809}(.{8}).*");
	private static Pattern PATTERN_ORSPL_identificacionDelTipoDeEmbalaje_PAC_ = Pattern.compile("^.{817}(.{17}).*");
	
	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ORSPL_numeroDeLineaArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_solicitudDeAccionONotificacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setSolicitudDeAccionONotificacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_codigoEAN_13_DUN_14DelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEAN_13_DUN_14DelArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_tipoCodigoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoCodigoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_tipoIdentificacionDeArticulo_CU_DU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoIdentificacionDeArticulo_CU_DU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_descripcion1Articulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcion1Articulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_descripcion2Articulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcion2Articulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_tipoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_numeroArticuloProveedor_SA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroArticuloProveedor_SA_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_numeroArticuloComprador_IN_BP_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroArticuloComprador_IN_BP_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_variablePromocional_PV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setVariablePromocional_PV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_codigoEANDelArticuloAdicional_1_EN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEANDelArticuloAdicional_1_EN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_cantidadPedida_21_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadPedida_21_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_cantidadBonificada_192_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadBonificada_192_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_calificadorUnidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_numeroUnidadesDeConsumoEnU_Expedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroUnidadesDeConsumoEnU_Expedicion(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_variacionEnCantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setVariacionEnCantidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_codigoDiscrepancia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDiscrepancia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_razonDelCambio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setRazonDelCambio(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_calificadorFecha_Hora1_2_11_64_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_Hora1_2_11_64_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_fecha_Hora1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_Hora1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_calificadorFecha_Hora2_2_11_63_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_Hora2_2_11_63_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_fecha_Hora2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_Hora2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_importeNetoLinea_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteNetoLinea_203_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_importeLineaConImpuestos_388_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteLineaConImpuestos_388_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_precioBrutoUnitario_AAB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioBrutoUnitario_AAB_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_precioNetoUnitario_AAA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioNetoUnitario_AAA_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_precioATituloInformativo_INF_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioATituloInformativo_INF_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_calificadorUnidadDeMedidaPrecio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedidaPrecio(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_unidadBasePrecio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadBasePrecio(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_calificadorIVA_IGIC.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorIVA_IGIC(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_porcentajeIVA_IGIC.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeIVA_IGIC(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_importeIVA_IGIC.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteIVA_IGIC(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_porcentajeRecargoDeEquivalencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeRecargoDeEquivalencia(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_importeRecargoDeEquivalencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteRecargoDeEquivalencia(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_calificadorOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorOtroTipoDeImpuesto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_porcentajeOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeOtroTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_importeOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteOtroTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_pesoNeto_PD_AAA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoNeto_PD_AAA_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_calificadorUnidadDeMedidaPeso.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedidaPeso(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_descripcionDelModelo_BRN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelModelo_BRN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_descripcionDelModelo_BRN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelModelo_BRN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_color_35_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setColor_35_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_anchuraOTalla_UP5_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAnchuraOTalla_UP5_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_presentacion_cantidad_formato_U03_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPresentacion_cantidad_formato_U03_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_cantidadAceptada_enviada_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadAceptada_enviada_12_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_cantidadRechazada_83_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadRechazada_83_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_cancelledQuantity_QTY_182_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCancelledQuantity_QTY_182_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_rejectedQuantity_QTY_185_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setRejectedQuantity_QTY_185_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_numeroDeEmbalajes_PAC_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeEmbalajes_PAC_(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPL_identificacionDelTipoDeEmbalaje_PAC_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificacionDelTipoDeEmbalaje_PAC_(String.valueOf(m.group(1).trim()));
		}
	}

	
	/**
	 * 2 - Número de línea artículo
	 */
	public Integer getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}

	/**
	 * 2 - Número de línea artículo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número de línea artículo</td> <td>N</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroDeLineaArticulo(Integer numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
	}

	/**
	 * 3 - Solicitud de acción o notificación: identificación del tipo de acción a realizar con la línea actual del pedido. Este campo corresponde al elemento 1229. Los valores posibles son:
	 */
	public String getSolicitudDeAccionONotificacion() {
		return solicitudDeAccionONotificacion;
	}


	/**
	 * 3 - Solicitud de acción o notificación: identificación del tipo de acción a realizar con la línea actual del pedido. Este campo corresponde al elemento 1229. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Solicitud de acción o notificación</td> <td>C</td> <td>3</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setSolicitudDeAccionONotificacion(String solicitudDeAccionONotificacion) {
		this.solicitudDeAccionONotificacion = solicitudDeAccionONotificacion;
	}

	/**
	 * 4 - Código EAN-13/DUN-14 del artículo
	 */
	public String getCodigoEAN_13_DUN_14DelArticulo() {
		return codigoEAN_13_DUN_14DelArticulo;
	}


	/**
	 * 4 - Código EAN-13/DUN-14 del artículo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Código EAN-13/DUN-14 del artículo</td> <td>C</td> <td>15</td> <td>16</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCodigoEAN_13_DUN_14DelArticulo(String codigoEAN_13_DUN_14DelArticulo) {
		this.codigoEAN_13_DUN_14DelArticulo = codigoEAN_13_DUN_14DelArticulo;
	}

	/**
	 * 5 - Tipo código artículo: identificación del tipo de código del artículo especificado en el campo 4. Este campo corresponde al elemento 7143. Los valores posibles son:
	 */
	public String getTipoCodigoArticulo() {
		return tipoCodigoArticulo;
	}

	/**
	 * 5 - Tipo código artículo: identificación del tipo de código del artículo especificado en el campo 4. Este campo corresponde al elemento 7143. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Tipo código artículo</td> <td>C</td> <td>3</td> <td>31</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTipoCodigoArticulo(String tipoCodigoArticulo) {
		this.tipoCodigoArticulo = tipoCodigoArticulo;
	}


	/**
	 * 6 - Tipo identificación de artículo: Código que suministra información sobre el artículo especificado en el campo 4. Este campo corresponde al elemento 7009. Los valores posibles son:
	 */
	public String getTipoIdentificacionDeArticulo_CU_DU_() {
		return tipoIdentificacionDeArticulo_CU_DU_;
	}


	/**
	 * 6 - Tipo identificación de artículo: Código que suministra información sobre el artículo especificado en el campo 4. Este campo corresponde al elemento 7009. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Tipo identificación de artículo (CU/DU)</td> <td>C</td> <td>17</td> <td>34</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTipoIdentificacionDeArticulo_CU_DU_(String tipoIdentificacionDeArticulo_CU_DU_) {
		this.tipoIdentificacionDeArticulo_CU_DU_ = tipoIdentificacionDeArticulo_CU_DU_;
	}

	/**
	 * 7 - Descripción 1 artículo
	 */
	public String getDescripcion1Articulo() {
		return descripcion1Articulo;
	}


	/**
	 * 7 - Descripción 1 artículo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Descripción 1 artículo</td> <td>C</td> <td>70</td> <td>51</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setDescripcion1Articulo(String descripcion1Articulo) {
		this.descripcion1Articulo = descripcion1Articulo;
	}


	/**
	 * 8 - Descripción 2 artículo
	 */
	public String getDescripcion2Articulo() {
		return descripcion2Articulo;
	}


	/**
	 * 8 - Descripción 2 artículo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Descripción 2 artículo</td> <td>C</td> <td>70</td> <td>121</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setDescripcion2Articulo(String descripcion2Articulo) {
		this.descripcion2Articulo = descripcion2Articulo;
	}

	/**
	 * 9 - Tipo artículo: Este campo corresponde al elemento 7081. Los valores posibles son:
	 */
	public String getTipoArticulo() {
		return tipoArticulo;
	}


	/**
	 * 9 - Tipo artículo: Este campo corresponde al elemento 7081. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Tipo artículo</td> <td>C</td> <td>1</td> <td>191</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	/**
	 * 10 - Número artículo proveedor (SA)
	 */
	public String getNumeroArticuloProveedor_SA_() {
		return numeroArticuloProveedor_SA_;
	}


	/**
	 * 10 - Número artículo proveedor (SA)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Número artículo proveedor (SA)</td> <td>C</td> <td>35</td> <td>192</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroArticuloProveedor_SA_(String numeroArticuloProveedor_SA_) {
		this.numeroArticuloProveedor_SA_ = numeroArticuloProveedor_SA_;
	}

	/**
	 * 11 - Número artículo comprador (IN/BP)
	 */
	public String getNumeroArticuloComprador_IN_BP_() {
		return numeroArticuloComprador_IN_BP_;
	}


	/**
	 * 11 - Número artículo comprador (IN/BP)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Número artículo comprador (IN/BP)</td> <td>C</td> <td>35</td> <td>227</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroArticuloComprador_IN_BP_(String numeroArticuloComprador_IN_BP_) {
		this.numeroArticuloComprador_IN_BP_ = numeroArticuloComprador_IN_BP_;
	}

	/**
	 * 12 - Variable promocional (PV)
	 */
	public String getVariablePromocional_PV_() {
		return variablePromocional_PV_;
	}


	/**
	 * 12 - Variable promocional (PV)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Variable promocional (PV)</td> <td>C</td> <td>35</td> <td>262</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setVariablePromocional_PV_(String variablePromocional_PV_) {
		this.variablePromocional_PV_ = variablePromocional_PV_;
	}

	/**
	 * 13 - Código EAN del artículo adicional (1) (EN)
	 */
	public String getCodigoEANDelArticuloAdicional_1_EN_() {
		return codigoEANDelArticuloAdicional_1_EN_;
	}


	/**
	 * 13 - Código EAN del artículo adicional (1) (EN)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Código EAN del artículo adicional (1) (EN)</td> <td>C</td> <td>35</td> <td>297</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCodigoEANDelArticuloAdicional_1_EN_(String codigoEANDelArticuloAdicional_1_EN_) {
		this.codigoEANDelArticuloAdicional_1_EN_ = codigoEANDelArticuloAdicional_1_EN_;
	}

	/**
	 * 14 - Cantidad pedida (21)
	 */
	public Double getCantidadPedida_21_() {
		return cantidadPedida_21_;
	}

	/**
	 * 14 - Cantidad pedida (21)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Cantidad pedida (21)</td> <td>N(12,3)</td> <td>16</td> <td>332</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCantidadPedida_21_(Double cantidadPedida_21_) {
		this.cantidadPedida_21_ = cantidadPedida_21_;
	}

	/**
	 * 15 - Cantidad bonificada (192)
	 */
	public Double getCantidadBonificada_192_() {
		return cantidadBonificada_192_;
	}


	/**
	 * 15 - Cantidad bonificada (192)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Cantidad bonificada (192)</td> <td>N(12,3)</td> <td>16</td> <td>348</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCantidadBonificada_192_(Double cantidadBonificada_192_) {
		this.cantidadBonificada_192_ = cantidadBonificada_192_;
	}

	/**
	 * 16 - Calificador unidad de medida: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 */
	public String getCalificadorUnidadDeMedida() {
		return calificadorUnidadDeMedida;
	}


	/**
	 * 16 - Calificador unidad de medida: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Calificador unidad de medida</td> <td>C</td> <td>6</td> <td>364</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorUnidadDeMedida(String calificadorUnidadDeMedida) {
		this.calificadorUnidadDeMedida = calificadorUnidadDeMedida;
	}

	/**
	 * 17 - Número Unidades de Consumo en U. Expedición
	 */
	public Double getNumeroUnidadesDeConsumoEnU_Expedicion() {
		return numeroUnidadesDeConsumoEnU_Expedicion;
	}


	/**
	 * 17 - Número Unidades de Consumo en U. Expedición
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Número Unidades de Consumo en U. Expedición</td> <td>N(12,3)</td> <td>16</td> <td>370</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroUnidadesDeConsumoEnU_Expedicion(Double numeroUnidadesDeConsumoEnU_Expedicion) {
		this.numeroUnidadesDeConsumoEnU_Expedicion = numeroUnidadesDeConsumoEnU_Expedicion;
	}

	/**
	 * 18 - Variación en cantidad: Este campo se utiliza para indicar la variación entre la cantidad pedida originalmente y la indicada en la respuesta al pedido. Si la cantidad en la respuesta es menor que la del pedido original, se indicará en negativo.
	 */
	public Double getVariacionEnCantidad() {
		return variacionEnCantidad;
	}


	/**
	 * 18 - Variación en cantidad: Este campo se utiliza para indicar la variación entre la cantidad pedida originalmente y la indicada en la respuesta al pedido. Si la cantidad en la respuesta es menor que la del pedido original, se indicará en negativo.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Variación en cantidad</td> <td>N(12,3)</td> <td>16</td> <td>386</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setVariacionEnCantidad(Double variacionEnCantidad) {
		this.variacionEnCantidad = variacionEnCantidad;
	}

	/**
	 * 19 - Código discrepancia: En este campo se detalla el motivo de la discrepancia que genera la diferencia de cantidades. Este campo corresponde al elemento 4221. Los valores posibles son:
	 */
	public String getCodigoDiscrepancia() {
		return codigoDiscrepancia;
	}


	/**
	 * 19 - Código discrepancia: En este campo se detalla el motivo de la discrepancia que genera la diferencia de cantidades. Este campo corresponde al elemento 4221. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Código discrepancia</td> <td>C</td> <td>3</td> <td>402</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCodigoDiscrepancia(String codigoDiscrepancia) {
		this.codigoDiscrepancia = codigoDiscrepancia;
	}

	/**
	 * 20 - Razón del cambio: Este campo se utiliza detallar la razón del cambio. Este campo corresponde al elemento 4295. Los valores posibles son:
	 */
	public String getRazonDelCambio() {
		return razonDelCambio;
	}


	/**
	 * 20 - Razón del cambio: Este campo se utiliza detallar la razón del cambio. Este campo corresponde al elemento 4295. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Razón del cambio</td> <td>C</td> <td>3</td> <td>405</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setRazonDelCambio(String razonDelCambio) {
		this.razonDelCambio = razonDelCambio;
	}

	/**
	 * 21, 22 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 21 corresponde al elemento 2005. Los valores posibles son:
	 */
	public String getCalificadorFecha_Hora1_2_11_64_() {
		return calificadorFecha_Hora1_2_11_64_;
	}


	/**
	 * 21, 22 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 21 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Calificador Fecha/Hora 1 (2-11-64)</td> <td>C</td> <td>3</td> <td>408</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorFecha_Hora1_2_11_64_(String calificadorFecha_Hora1_2_11_64_) {
		this.calificadorFecha_Hora1_2_11_64_ = calificadorFecha_Hora1_2_11_64_;
	}


	/**
	 * 21, 22 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 21 corresponde al elemento 2005. Los valores posibles son:
	 */
	public String getFecha_Hora1() {
		return fecha_Hora1;
	}


	/**
	 * 21, 22 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 21 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Fecha/Hora 1</td> <td>C</td> <td>12</td> <td>411</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFecha_Hora1(String fecha_Hora1) {
		this.fecha_Hora1 = fecha_Hora1;
	}

	/**
	 * 23, 24 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 23 corresponde al elemento 2005. Los valores posibles son:
	 */
	public String getCalificadorFecha_Hora2_2_11_63_() {
		return calificadorFecha_Hora2_2_11_63_;
	}


	/**
	 * 23, 24 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 23 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Calificador Fecha/Hora 2 (2-11-63)</td> <td>C</td> <td>3</td> <td>423</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorFecha_Hora2_2_11_63_(String calificadorFecha_Hora2_2_11_63_) {
		this.calificadorFecha_Hora2_2_11_63_ = calificadorFecha_Hora2_2_11_63_;
	}

	/**
	 * 23, 24 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 23 corresponde al elemento 2005. Los valores posibles son:
	 */
	public String getFecha_Hora2() {
		return fecha_Hora2;
	}


	/**
	 * 23, 24 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 23 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Fecha/Hora 2</td> <td>C</td> <td>12</td> <td>426</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFecha_Hora2(String fecha_Hora2) {
		this.fecha_Hora2 = fecha_Hora2;
	}

	/**
	 * 25 - Importe neto línea: Cantidad x Precio Neto.
	 */
	public Double getImporteNetoLinea_203_() {
		return importeNetoLinea_203_;
	}


	/**
	 * 25 - Importe neto línea: Cantidad x Precio Neto.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Importe neto línea (203)</td> <td>N(14,3)</td> <td>18</td> <td>438</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteNetoLinea_203_(Double importeNetoLinea_203_) {
		this.importeNetoLinea_203_ = importeNetoLinea_203_;
	}

	/**
	 * 26 - Importe línea con impuestos: Cantidad x (Precio Neto + Impuestos).
	 */
	public Double getImporteLineaConImpuestos_388_() {
		return importeLineaConImpuestos_388_;
	}


	/**
	 * 26 - Importe línea con impuestos: Cantidad x (Precio Neto + Impuestos).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Importe línea con impuestos (388)</td> <td>N(14,3)</td> <td>18</td> <td>456</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteLineaConImpuestos_388_(Double importeLineaConImpuestos_388_) {
		this.importeLineaConImpuestos_388_ = importeLineaConImpuestos_388_;
	}

	/**
	 * 27 - Precio bruto unitario: Excluye descuentos, cargos e impuestos.
	 */
	public Double getPrecioBrutoUnitario_AAB_() {
		return precioBrutoUnitario_AAB_;
	}


	/**
	 * 27 - Precio bruto unitario: Excluye descuentos, cargos e impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Precio bruto unitario (AAB)</td> <td>N(12,3)</td> <td>18</td> <td>456</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPrecioBrutoUnitario_AAB_(Double precioBrutoUnitario_AAB_) {
		this.precioBrutoUnitario_AAB_ = precioBrutoUnitario_AAB_;
	}

	/**
	 * 28 - Precio neto unitario: Incluye descuentos y cargos pero no impuestos.
	 */
	public Double getPrecioNetoUnitario_AAA_() {
		return precioNetoUnitario_AAA_;
	}


	/**
	 * 28 - Precio neto unitario: Incluye descuentos y cargos pero no impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Precio neto unitario (AAA)</td> <td>N(12,3)</td> <td>16</td> <td>490</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPrecioNetoUnitario_AAA_(Double precioNetoUnitario_AAA_) {
		this.precioNetoUnitario_AAA_ = precioNetoUnitario_AAA_;
	}

	/**
	 * 29 - Precio a título Informativo (INF)
	 */
	public Double getPrecioATituloInformativo_INF_() {
		return precioATituloInformativo_INF_;
	}


	/**
	 * 29 - Precio a título Informativo (INF)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>29</td> <td>Precio a título Informativo (INF)</td> <td>N(12,3)</td> <td>16</td> <td>506</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPrecioATituloInformativo_INF_(Double precioATituloInformativo_INF_) {
		this.precioATituloInformativo_INF_ = precioATituloInformativo_INF_;
	}

	/**
	 * 30 - Calificador unidad de medida precio
	 */
	public String getCalificadorUnidadDeMedidaPrecio() {
		return calificadorUnidadDeMedidaPrecio;
	}


	/**
	 * 30 - Calificador unidad de medida precio
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>30</td> <td>Calificador unidad de medida precio</td> <td>N(12,3)</td> <td>6</td> <td>522</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorUnidadDeMedidaPrecio(String calificadorUnidadDeMedidaPrecio) {
		this.calificadorUnidadDeMedidaPrecio = calificadorUnidadDeMedidaPrecio;
	}

	/**
	 * 31 - Unidad base precio: Base del precio. Se utiliza principalmente cuando el producto es de unidad de medida variable. Ejemplo: Para establecer el precio por cada 200 Kilogramos.
	 */
	public Double getUnidadBasePrecio() {
		return unidadBasePrecio;
	}


	/**
	 * 31 - Unidad base precio: Base del precio. Se utiliza principalmente cuando el producto es de unidad de medida variable. Ejemplo: Para establecer el precio por cada 200 Kilogramos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>31</td> <td>Unidad base precio</td> <td>N(6,3)</td> <td>10</td> <td>528</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setUnidadBasePrecio(Double unidadBasePrecio) {
		this.unidadBasePrecio = unidadBasePrecio;
	}

	/**
	 * 32 - Calificador IVA/IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */
	public String getCalificadorIVA_IGIC() {
		return calificadorIVA_IGIC;
	}


	/**
	 * 32 - Calificador IVA/IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>32</td> <td>Calificador IVA/IGIC</td> <td>C</td> <td>6</td> <td>538</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorIVA_IGIC(String calificadorIVA_IGIC) {
		this.calificadorIVA_IGIC = calificadorIVA_IGIC;
	}

	/**
	 * 33 - Porcentaje IVA/IGIC
	 */
	public Double getPorcentajeIVA_IGIC() {
		return porcentajeIVA_IGIC;
	}


	/**
	 * 33 - Porcentaje IVA/IGIC
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>33</td> <td>Porcentaje IVA/IGIC</td> <td>N(3,2)</td> <td>6</td> <td>544</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPorcentajeIVA_IGIC(Double porcentajeIVA_IGIC) {
		this.porcentajeIVA_IGIC = porcentajeIVA_IGIC;
	}

	/**
	 * 34 - Importe IVA/IGIC
	 */
	public Double getImporteIVA_IGIC() {
		return importeIVA_IGIC;
	}


	/**
	 * 34 - Importe IVA/IGIC
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>34</td> <td>Importe IVA/IGIC</td> <td>N(14,3)</td> <td>6</td> <td>544</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteIVA_IGIC(Double importeIVA_IGIC) {
		this.importeIVA_IGIC = importeIVA_IGIC;
	}

	/**
	 * 35 - % Recargo de Equivalencia
	 */
	public Double getPorcentajeRecargoDeEquivalencia() {
		return porcentajeRecargoDeEquivalencia;
	}


	/**
	 * 35 - % Recargo de Equivalencia
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>35</td> <td>% Recargo de Equivalencia</td> <td>N(3,2)</td> <td>6</td> <td>568</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPorcentajeRecargoDeEquivalencia(Double porcentajeRecargoDeEquivalencia) {
		this.porcentajeRecargoDeEquivalencia = porcentajeRecargoDeEquivalencia;
	}

	/**
	 * 36 - Importe Recargo de Equivalencia
	 */
	public Double getImporteRecargoDeEquivalencia() {
		return importeRecargoDeEquivalencia;
	}


	/**
	 * 36 - Importe Recargo de Equivalencia
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>36</td> <td>Importe IVA/IGIC</td> <td>N(14,3)</td> <td>18</td> <td>574</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteRecargoDeEquivalencia(Double importeRecargoDeEquivalencia) {
		this.importeRecargoDeEquivalencia = importeRecargoDeEquivalencia;
	}

	/**
	 * 37 - Calificador Otro Tipo de Impuesto: Cuando el artículo tiene un impuesto adicional. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */
	public String getCalificadorOtroTipoDeImpuesto() {
		return calificadorOtroTipoDeImpuesto;
	}


	/**
	 * 37 - Calificador Otro Tipo de Impuesto: Cuando el artículo tiene un impuesto adicional. Este campo corresponde al elemento 5153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>37</td> <td>Calificador Otro Tipo de Impuesto</td> <td>C</td> <td>6</td> <td>592</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorOtroTipoDeImpuesto(String calificadorOtroTipoDeImpuesto) {
		this.calificadorOtroTipoDeImpuesto = calificadorOtroTipoDeImpuesto;
	}

	/**
	 * 38 - % Otro Tipo de Impuesto
	 */
	public Double getPorcentajeOtroTipoDeImpuesto() {
		return porcentajeOtroTipoDeImpuesto;
	}


	/**
	 * 38 - % Otro Tipo de Impuesto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>38</td> <td>% Otro Tipo de Impuesto</td> <td>N(3,2)</td> <td>6</td> <td>598</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPorcentajeOtroTipoDeImpuesto(Double porcentajeOtroTipoDeImpuesto) {
		this.porcentajeOtroTipoDeImpuesto = porcentajeOtroTipoDeImpuesto;
	}

	/**
	 * 39 - Importe Otro Tipo de Impuesto
	 */
	public Double getImporteOtroTipoDeImpuesto() {
		return importeOtroTipoDeImpuesto;
	}


	/**
	 * 39 - Importe Otro Tipo de Impuesto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>39</td> <td>Importe Otro Tipo de Impuesto</td> <td>N(14,3)</td> <td>18</td> <td>604</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteOtroTipoDeImpuesto(Double importeOtroTipoDeImpuesto) {
		this.importeOtroTipoDeImpuesto = importeOtroTipoDeImpuesto;
	}

	/**
	 * 40 - Peso neto (PD) (AAA)
	 */
	public Double getPesoNeto_PD_AAA_() {
		return pesoNeto_PD_AAA_;
	}


	/**
	 * 40 - Peso neto (PD) (AAA)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>40</td> <td>Peso neto (PD) (AAA)</td> <td>N(14,3)</td> <td>18</td> <td>622</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPesoNeto_PD_AAA_(Double pesoNeto_PD_AAA_) {
		this.pesoNeto_PD_AAA_ = pesoNeto_PD_AAA_;
	}

	/**
	 * 41 - Calificador unidad de medida peso
	 */
	public String getCalificadorUnidadDeMedidaPeso() {
		return calificadorUnidadDeMedidaPeso;
	}


	/**
	 * 41 - Calificador unidad de medida peso
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>41</td> <td>Calificador unidad de medida peso</td> <td>C</td> <td>6</td> <td>640</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorUnidadDeMedidaPeso(String calificadorUnidadDeMedidaPeso) {
		this.calificadorUnidadDeMedidaPeso = calificadorUnidadDeMedidaPeso;
	}

	/**
	 * 42 - Descripción del Modelo (BRN)
	 */
	public String getDescripcionDelModelo_BRN_() {
		return descripcionDelModelo_BRN_;
	}


	/**
	 * 42 - Descripción del Modelo (BRN)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>42</td> <td>Descripción del Modelo (BRN)</td> <td>C</td> <td>25</td> <td>646</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setDescripcionDelModelo_BRN_(String descripcionDelModelo_BRN_) {
		this.descripcionDelModelo_BRN_ = descripcionDelModelo_BRN_;
	}

	/**
	 * 43 - Color (35)
	 */
	public String getColor_35_() {
		return color_35_;
	}


	/**
	 * 43 - Color (35)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>43</td> <td>Color (35)</td> <td>C</td> <td>25</td> <td>671</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setColor_35_(String color_35_) {
		this.color_35_ = color_35_;
	}

	/**
	 * 44 - Anchura o talla (UP5)
	 */
	public String getAnchuraOTalla_UP5_() {
		return anchuraOTalla_UP5_;
	}


	/**
	 * 44 - Anchura o talla (UP5)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>44</td> <td>Color (35)</td> <td>C</td> <td>25</td> <td>696</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setAnchuraOTalla_UP5_(String anchuraOTalla_UP5_) {
		this.anchuraOTalla_UP5_ = anchuraOTalla_UP5_;
	}

	/**
	 * 45 - Presentación, cantidad, formato (U03)
	 */
	public String getPresentacion_cantidad_formato_U03_() {
		return presentacion_cantidad_formato_U03_;
	}


	/**
	 * 45 - Presentación, cantidad, formato (U03)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>45</td> <td>Presentación, cantidad, formato (U03)</td> <td>C</td> <td>25</td> <td>721</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPresentacion_cantidad_formato_U03_(String presentacion_cantidad_formato_U03_) {
		this.presentacion_cantidad_formato_U03_ = presentacion_cantidad_formato_U03_;
	}

	/**
	 * 46 - Cantidad aceptada/enviada (12)
	 */
	public Double getCantidadAceptada_enviada_12_() {
		return cantidadAceptada_enviada_12_;
	}

	
	/**
	 * 46 - Cantidad aceptada/enviada (12)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>46</td> <td>Cantidad aceptada/enviada (12)</td> <td>N(12,3)</td> <td>16</td> <td>746</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCantidadAceptada_enviada_12_(Double cantidadAceptada_enviada_12_) {
		this.cantidadAceptada_enviada_12_ = cantidadAceptada_enviada_12_;
	}

	/**
	 * 47 - Cantidad rechazada (83)
	 */
	public Double getCantidadRechazada_83_() {
		return cantidadRechazada_83_;
	}


	/**
	 * 47 - Cantidad rechazada (83)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>47</td> <td>Cantidad rechazada (83)</td> <td>N(12,3)</td> <td>16</td> <td>762</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCantidadRechazada_83_(Double cantidadRechazada_83_) {
		this.cantidadRechazada_83_ = cantidadRechazada_83_;
	}

	/**
	 * 48 - Cancelled quantity(QTY+182)
	 */
	public Double getCancelledQuantity_QTY_182_() {
		return cancelledQuantity_QTY_182_;
	}


	/**
	 * 48 - Cancelled quantity(QTY+182)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>48</td> <td>Cancelled quantity(QTY+182)</td> <td>N(12,3)</td> <td>16</td> <td>778</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCancelledQuantity_QTY_182_(Double cancelledQuantity_QTY_182_) {
		this.cancelledQuantity_QTY_182_ = cancelledQuantity_QTY_182_;
	}

	/**
	 * 49 - Rejected quantity (QTY+185)
	 */
	public Double getRejectedQuantity_QTY_185_() {
		return rejectedQuantity_QTY_185_;
	}


	/**
	 * 49 - Rejected quantity (QTY+185)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>49</td> <td>Rejected quantity (QTY+185)</td> <td>N(12,3)</td> <td>16</td> <td>794</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setRejectedQuantity_QTY_185_(Double rejectedQuantity_QTY_185_) {
		this.rejectedQuantity_QTY_185_ = rejectedQuantity_QTY_185_;
	}

	/**
	 * 50 - Número de embalajes (PAC)
	 */
	public Integer getNumeroDeEmbalajes_PAC_() {
		return numeroDeEmbalajes_PAC_;
	}


	/**
	 * 50 - Número de embalajes (PAC)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>50</td> <td>Número de embalajes (PAC)</td> <td>N(8)</td> <td>8</td> <td>810</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroDeEmbalajes_PAC_(Integer numeroDeEmbalajes_PAC_) {
		this.numeroDeEmbalajes_PAC_ = numeroDeEmbalajes_PAC_;
	}

	/**
	 * 51 - Identificación del tipo de embalaje (PAC)
	 */
	public String getIdentificacionDelTipoDeEmbalaje_PAC_() {
		return identificacionDelTipoDeEmbalaje_PAC_;
	}


	/**
	 * 51 - Identificación del tipo de embalaje (PAC)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>51</td> <td>Identificación del tipo de embalaje (PAC)</td> <td>C</td> <td>17</td> <td>818</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setIdentificacionDelTipoDeEmbalaje_PAC_(String identificacionDelTipoDeEmbalaje_PAC_) {
		this.identificacionDelTipoDeEmbalaje_PAC_ = identificacionDelTipoDeEmbalaje_PAC_;
	}



	/** 
	 * 3 - Solicitud de acción o notificación: identificación del tipo de acción a realizar con la línea actual del pedido. Este campo corresponde al elemento 1229. Los valores posibles son:
	 */
	public enum ORSPL_3 {
		ANADIDO("1"),
		CAMBIADO("3"),
		ACEPTADO_SIN_CORRECCION("5"),
		ACEPTADO_CON_CORRECCION("6"),
		NO_ACEPTADA("7"),
		;
		
		private String value;
		
		private ORSPL_3(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_3 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 5 - Tipo código artículo: identificación del tipo de código del artículo especificado en el campo 4. Este campo corresponde al elemento 7143. Los valores posibles son:
	 */
	public enum ORSPL_5 {
		EAN("EN"),
		UPC_CODIGO_PRODUCTO_UNIVERSAL_("UP"),
		;
		
		private String value;
		
		private ORSPL_5(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_5 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 6 - Tipo identificación de artículo: Código que suministra información sobre el artículo especificado en el campo 4. Este campo corresponde al elemento 7009. Los valores posibles son:
	 */
	public enum ORSPL_6 {
		UNIDAD_DE_CONSUMO("CU"),
		UNIDAD_DE_EXPEDICION("DU"),
		;
		
		private String value;
		
		private ORSPL_6(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_6 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 9 - Tipo artículo: Este campo corresponde al elemento 7081. Los valores posibles son:
	 */
	public enum ORSPL_9 {
		MERCANCIAS("M"),
		MATERIAL_CONSIGNADO("C"),
		SERVICIOS("S"),
		;
		
		private String value;
		
		private ORSPL_9(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_9 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 16 - Calificador unidad de medida: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 */
	public enum ORSPL_16 {
		KILOGRAMO("KGM"),
		LITRO("LTR"),
		;
		
		private String value;
		
		private ORSPL_16(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_16 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 19 - Código discrepancia: En este campo se detalla el motivo de la discrepancia que genera la diferencia de cantidades. Este campo corresponde al elemento 4221. Los valores posibles son:
	 */
	public enum ORSPL_19 {
		ARTICULO_DADO_DE_BAJA_POR_EL_MAYORISTA("AA"),
		ARTICULO_QUE_SE_HA_DEJADO_DE_FABRICAR("AB"),
		ARTICULO_SIN_EXISTENCIAS_EN_FABRICA("AD"),
		DISPONIBLE_AHORA_ENVIO_PLANIFICADO("AS"),
		ENVIO_SUPLEMENTARIO_DE_UN_PEDIDO_ANTERIOR("BK"),
		ENVIO_PARCIAL_QUE_IRA_SEGUIDO_DE_UN_ENVIO_COMPLEMENTARIO("BP"),
		ENVIO_PARCIAL_CONSIDERADO_COMPLETO_SIN_ENVIO_SUPLEMENTARIO("CP"),
		PRÓXIMO_TRANSPORTISTA("CN"),
		EN_PROCESO_ENVIO_PLANIFICADO("PS"),
		ARTICULO_AGOTADO_DEBIDO_A_FUERZA_MAYOR("OS"),
		ARTICULO_AGOTADO_EN_EL_MAYORISTA("OW"),
		ARTICULO_NO_DISPONIBLE_TEMPORALMENTE_POR_EL_MAYORISTA("TW");
		
		private String value;
		
		private ORSPL_19(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_19 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 20 - Razón del cambio: Este campo se utiliza detallar la razón del cambio. Este campo corresponde al elemento 4295. Los valores posibles son:
	 */
	public enum ORSPL_20 {
		AJUSTE("AJT"),
		CODIGO_DE_ARTICULO_DESCONOCIDO("AUE"),
		FUERA_DE_INVENTARIO("AV"),
		CANTIDAD_Y_UNIDAD_DE_MEDIDA_ALTERNATIVAS("AQ"),
		EL_PEDIDO_REPRESENTA_UNA_SUSTITUCION_DEL_PEDIDO_ORIGINAL("IS"),
		DIFERENCIA_DE_BULTO("PC"),
		DIFERENCIA_DE_UNIDAD_DE_MEDIDA("UM"),
		LA_CANTIDAD_SOLICITADA_NO_CONCUERDA_CON_LO_PACTADO("WV"),
		;
		
		private String value;
		
		private ORSPL_20(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_20 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 21, 22 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 21 corresponde al elemento 2005. Los valores posibles son:
	 */
	public enum ORSPL_21 {
		FECHA_HORA_DE_ENTREGA_REQUERIDA("2"),
		FECHA_HORA_DE_ENVIO("11"),
		PRIMERA_FECHA_HORA_DE_ENTREGA("64"),
		ULTIMA_FECHA_HORA_DE_ENTREGA("63"),
		;
		
		private String value;
		
		private ORSPL_21(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_21 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 23, 24 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 23 corresponde al elemento 2005. Los valores posibles son:
	 */
	public enum ORSPL_23 {
		FECHA_HORA_DE_ENTREGA_REQUERIDA("2"),
		FECHA_HORA_DE_ENVIO("11"),
		PRIMERA_FECHA_HORA_DE_ENTREGA("64"),
		ULTIMA_FECHA_HORA_DE_ENTREGA("63"),
		;
		
		private String value;
		
		private ORSPL_23(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_23 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 32 - Calificador IVA/IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */
	public enum ORSPL_32 {
		IVA("VAT"),
		IGIC_IMPUESTO_GENERAL_DE_LAS_ISLAS_CANARIAS_("IGI"),
		;
		
		private String value;
		
		private ORSPL_32(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_32 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 37 - Calificador Otro Tipo de Impuesto: Cuando el artículo tiene un impuesto adicional. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */
	public enum ORSPL_37 {
		DERECHOS_DE_AUTOR("DA"),
		IPSI("IPS"),
		EXENTO("EXT"),
		IRPF_DENTRO_DE_FACTURA_ES_UN_IMPUESTO_RETENIDO_NO_REPERCUTIDO("IRP"),
		IMPUESTO_DE_ALCOHOLES("ACT"),
		;
		
		private String value;
		
		private ORSPL_37(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPL_37 enumByValue(String value) {
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