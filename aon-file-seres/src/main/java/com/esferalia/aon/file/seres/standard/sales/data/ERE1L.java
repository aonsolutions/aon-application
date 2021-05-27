package com.esferalia.aon.file.seres.standard.sales.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1L entity.
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
 * 		 <td>ERE1L</td> <td>Línea detalle</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class ERE1L {

	private Integer numeroDeLineaArticulo;
	private String codigoEAN_13_DUN_14DelArticulo;
	private String tipoCodigoArticulo;
	private String tipoIdentificacionDeArticulo_CU_DU_;
	private String descripcion1Articulo;
	private String descripcion2Articulo;
	private String tipoArticulo;
	private String numeroArticuloProveedor_SA_;
	private String numeroArticuloComprador_IN_BP_;
	private String variablePromocional_PV_;
	private String codigoEANDelArticuloAdicional_1__EN_;
	private Double cantidadPedida_21_;
	private Double cantidadBonificada_192_;
	private String calificadorUnidadDeMedida;
	private Double numeroUnidadesDeConsumoEnU_Expedicion;
	private String calificadorFecha_Hora1_2_11_64_;
	private String fecha_Hora1;
	private String calificadorFecha_Hora2_2_11_63_;
	private String fecha_Hora2;
	private Double importeNetoLinea_203_;
	private Double precioBrutoUnitario_AAB_;
	private Double precioNetoUnitario_AAA_;
	private Double precioATituloInformativo_INF_;
	private String calificadorUnidadDeMedidaPrecio;
	private String calificadorIVA_IGIC;
	private Double porcentajeIVA_IGIC;
	private Double importeIVA_IGIC;
	private Double porcentajeRecargoDeEquivalencia;
	private Double importeRecargoDeEquivalencia;
	private String calificadorOtroTipoDeImpuesto;
	private Double porcentajeOtroTipoDeImpuesto;
	private Double importeOtroTipoDeImpuesto;
	private Double pesoNeto_PD__AAA_;
	private String calificadorUnidadDeMedidaPeso;
	private String descripcionDelModelo_BRN_;
	private String color_35_;
	private String anchuraOTalla_UP5_;
	private String presentacion_Cantidad_Formato_U03_;
	private String codigoGrupoArticuloComprador_GB_;
	private String numeroDeSerieDelArticulo_SN_;
	private String numeroArticuloFabricante_MF_;
	private String numeroDeLote_NB_;
	private String calificadorFecha_Hora3;
	private String fecha_Hora3;
	private Double importeLineaConImpuestos_388_;
	private Integer basePrecioNetoUnitario_AAA_;
	private Double precioArticuloConImpuestos_NTP_;
	private Integer basePrecioArticuloConImpuestos_NTP_;
	private String numeroAlbaran_DQ_;
	private String fechaAlbaran;
	private String codigoClienteFinal;
	private String nombreClienteFinal;
	private String direccionClienteFinal;
	private String poblacionClienteFinal;
	private String codigoPostalClienteFinal;
	private String identificadorProducto_lineaPedido_MP_;


	private static Pattern PATTERN_ERE1L_numeroDeLineaArticulo = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ERE1L_codigoEAN_13_DUN_14DelArticulo = Pattern.compile("^.{12}(.{15}).*");
	private static Pattern PATTERN_ERE1L_tipoCodigoArticulo = Pattern.compile("^.{27}(.{3}).*");
	private static Pattern PATTERN_ERE1L_tipoIdentificacionDeArticulo_CU_DU_ = Pattern.compile("^.{30}(.{17}).*");
	private static Pattern PATTERN_ERE1L_descripcion1Articulo = Pattern.compile("^.{47}(.{70}).*");
	private static Pattern PATTERN_ERE1L_descripcion2Articulo = Pattern.compile("^.{117}(.{70}).*");
	private static Pattern PATTERN_ERE1L_tipoArticulo = Pattern.compile("^.{187}(.{1}).*");
	private static Pattern PATTERN_ERE1L_numeroArticuloProveedor_SA_ = Pattern.compile("^.{188}(.{35}).*");
	private static Pattern PATTERN_ERE1L_numeroArticuloComprador_IN_BP_ = Pattern.compile("^.{223}(.{35}).*");
	private static Pattern PATTERN_ERE1L_variablePromocional_PV_ = Pattern.compile("^.{258}(.{35}).*");
	private static Pattern PATTERN_ERE1L_codigoEANDelArticuloAdicional_1__EN_ = Pattern.compile("^.{293}(.{35}).*");
	private static Pattern PATTERN_ERE1L_cantidadPedida_21_ = Pattern.compile("^.{328}(.{16}).*");
	private static Pattern PATTERN_ERE1L_cantidadBonificada_192_ = Pattern.compile("^.{344}(.{16}).*");
	private static Pattern PATTERN_ERE1L_calificadorUnidadDeMedida = Pattern.compile("^.{360}(.{6}).*");
	private static Pattern PATTERN_ERE1L_numeroUnidadesDeConsumoEnU_Expedicion = Pattern.compile("^.{366}(.{16}).*");
	private static Pattern PATTERN_ERE1L_calificadorFecha_Hora1_2_11_64_ = Pattern.compile("^.{382}(.{3}).*");
	private static Pattern PATTERN_ERE1L_fecha_Hora1 = Pattern.compile("^.{385}(.{12}).*");
	private static Pattern PATTERN_ERE1L_calificadorFecha_Hora2_2_11_63_ = Pattern.compile("^.{397}(.{3}).*");
	private static Pattern PATTERN_ERE1L_fecha_Hora2 = Pattern.compile("^.{400}(.{12}).*");
	private static Pattern PATTERN_ERE1L_importeNetoLinea_203_ = Pattern.compile("^.{412}(.{18}).*");
	private static Pattern PATTERN_ERE1L_precioBrutoUnitario_AAB_ = Pattern.compile("^.{430}(.{16}).*");
	private static Pattern PATTERN_ERE1L_precioNetoUnitario_AAA_ = Pattern.compile("^.{446}(.{16}).*");
	private static Pattern PATTERN_ERE1L_precioATituloInformativo_INF_ = Pattern.compile("^.{462}(.{16}).*");
	private static Pattern PATTERN_ERE1L_calificadorUnidadDeMedidaPrecio = Pattern.compile("^.{478}(.{6}).*");
	private static Pattern PATTERN_ERE1L_calificadorIVA_IGIC = Pattern.compile("^.{484}(.{6}).*");
	private static Pattern PATTERN_ERE1L_porcentajeIVA_IGIC = Pattern.compile("^.{490}(.{6}).*");
	private static Pattern PATTERN_ERE1L_importeIVA_IGIC = Pattern.compile("^.{496}(.{18}).*");
	private static Pattern PATTERN_ERE1L_porcentajeRecargoDeEquivalencia = Pattern.compile("^.{514}(.{6}).*");
	private static Pattern PATTERN_ERE1L_importeRecargoDeEquivalencia = Pattern.compile("^.{520}(.{18}).*");
	private static Pattern PATTERN_ERE1L_calificadorOtroTipoDeImpuesto = Pattern.compile("^.{538}(.{6}).*");
	private static Pattern PATTERN_ERE1L_porcentajeOtroTipoDeImpuesto = Pattern.compile("^.{544}(.{6}).*");
	private static Pattern PATTERN_ERE1L_importeOtroTipoDeImpuesto = Pattern.compile("^.{550}(.{18}).*");
	private static Pattern PATTERN_ERE1L_pesoNeto_PD__AAA_ = Pattern.compile("^.{568}(.{18}).*");
	private static Pattern PATTERN_ERE1L_calificadorUnidadDeMedidaPeso = Pattern.compile("^.{586}(.{6}).*");
	private static Pattern PATTERN_ERE1L_descripcionDelModelo_BRN_ = Pattern.compile("^.{592}(.{25}).*");
	private static Pattern PATTERN_ERE1L_color_35_ = Pattern.compile("^.{617}(.{25}).*");
	private static Pattern PATTERN_ERE1L_anchuraOTalla_UP5_ = Pattern.compile("^.{642}(.{25}).*");
	private static Pattern PATTERN_ERE1L_presentacion_Cantidad_Formato_U03_ = Pattern.compile("^.{667}(.{25}).*");
	private static Pattern PATTERN_ERE1L_codigoGrupoArticuloComprador_GB_ = Pattern.compile("^.{692}(.{35}).*");
	private static Pattern PATTERN_ERE1L_numeroDeSerieDelArticulo_SN_ = Pattern.compile("^.{727}(.{35}).*");
	private static Pattern PATTERN_ERE1L_numeroArticuloFabricante_MF_ = Pattern.compile("^.{762}(.{35}).*");
	private static Pattern PATTERN_ERE1L_numeroDeLote_NB_ = Pattern.compile("^.{797}(.{35}).*");
	private static Pattern PATTERN_ERE1L_calificadorFecha_Hora3 = Pattern.compile("^.{832}(.{3}).*");
	private static Pattern PATTERN_ERE1L_fecha_Hora3 = Pattern.compile("^.{835}(.{12}).*");
	private static Pattern PATTERN_ERE1L_importeLineaConImpuestos_388_ = Pattern.compile("^.{847}(.{18}).*");
	private static Pattern PATTERN_ERE1L_basePrecioNetoUnitario_AAA_ = Pattern.compile("^.{865}(.{9}).*");
	private static Pattern PATTERN_ERE1L_precioArticuloConImpuestos_NTP_ = Pattern.compile("^.{874}(.{16}).*");
	private static Pattern PATTERN_ERE1L_basePrecioArticuloConImpuestos_NTP_ = Pattern.compile("^.{890}(.{9}).*");
	private static Pattern PATTERN_ERE1L_numeroAlbaran_DQ_ = Pattern.compile("^.{899}(.{17}).*");
	private static Pattern PATTERN_ERE1L_fechaAlbaran = Pattern.compile("^.{916}(.{12}).*");
	private static Pattern PATTERN_ERE1L_codigoClienteFinal = Pattern.compile("^.{928}(.{17}).*");
	private static Pattern PATTERN_ERE1L_nombreClienteFinal = Pattern.compile("^.{945}(.{70}).*");
	private static Pattern PATTERN_ERE1L_direccionClienteFinal = Pattern.compile("^.{1015}(.{70}).*");
	private static Pattern PATTERN_ERE1L_poblacionClienteFinal = Pattern.compile("^.{1085}(.{35}).*");
	private static Pattern PATTERN_ERE1L_codigoPostalClienteFinal = Pattern.compile("^.{1120}(.{9}).*");
	private static Pattern PATTERN_ERE1L_identificadorProducto_lineaPedido_MP_ = Pattern.compile("^.{1129}(.{15}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1L_numeroDeLineaArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoEAN_13_DUN_14DelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEAN_13_DUN_14DelArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_tipoCodigoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoCodigoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_tipoIdentificacionDeArticulo_CU_DU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoIdentificacionDeArticulo_CU_DU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_descripcion1Articulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcion1Articulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_descripcion2Articulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcion2Articulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_tipoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroArticuloProveedor_SA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroArticuloProveedor_SA_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroArticuloComprador_IN_BP_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroArticuloComprador_IN_BP_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_variablePromocional_PV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setVariablePromocional_PV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoEANDelArticuloAdicional_1__EN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEANDelArticuloAdicional_1__EN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_cantidadPedida_21_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadPedida_21_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_cantidadBonificada_192_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadBonificada_192_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorUnidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroUnidadesDeConsumoEnU_Expedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroUnidadesDeConsumoEnU_Expedicion(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorFecha_Hora1_2_11_64_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_Hora1_2_11_64_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_fecha_Hora1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_Hora1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorFecha_Hora2_2_11_63_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_Hora2_2_11_63_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_fecha_Hora2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_Hora2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_importeNetoLinea_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteNetoLinea_203_(Double.valueOf(m.group(1).trim()));
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
		if((m = PATTERN_ERE1L_calificadorIVA_IGIC.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorIVA_IGIC(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_porcentajeIVA_IGIC.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeIVA_IGIC(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_importeIVA_IGIC.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteIVA_IGIC(Double.valueOf(m.group(1).trim()));
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
		if((m = PATTERN_ERE1L_calificadorUnidadDeMedidaPeso.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedidaPeso(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_descripcionDelModelo_BRN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelModelo_BRN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_color_35_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setColor_35_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_anchuraOTalla_UP5_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAnchuraOTalla_UP5_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_presentacion_Cantidad_Formato_U03_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPresentacion_Cantidad_Formato_U03_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoGrupoArticuloComprador_GB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoGrupoArticuloComprador_GB_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroDeSerieDelArticulo_SN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeSerieDelArticulo_SN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroArticuloFabricante_MF_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroArticuloFabricante_MF_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroDeLote_NB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLote_NB_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_calificadorFecha_Hora3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_Hora3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_fecha_Hora3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_Hora3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_importeLineaConImpuestos_388_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteLineaConImpuestos_388_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_basePrecioNetoUnitario_AAA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBasePrecioNetoUnitario_AAA_(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_precioArticuloConImpuestos_NTP_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioArticuloConImpuestos_NTP_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_basePrecioArticuloConImpuestos_NTP_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBasePrecioArticuloConImpuestos_NTP_(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_numeroAlbaran_DQ_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroAlbaran_DQ_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_fechaAlbaran.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaAlbaran(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoClienteFinal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoClienteFinal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_nombreClienteFinal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombreClienteFinal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_direccionClienteFinal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccionClienteFinal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_poblacionClienteFinal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPoblacionClienteFinal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_codigoPostalClienteFinal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPostalClienteFinal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1L_identificadorProducto_lineaPedido_MP_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificadorProducto_lineaPedido_MP_(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Fecha/Hora de entrega requerida
	 */ 
	public Integer getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}

	/** 
	 * 2 - Fecha/Hora de entrega requerida
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
	 * 63 - Ultima Fecha/Hora de entrega
	 */ 
	public String getCodigoEAN_13_DUN_14DelArticulo() {
		return codigoEAN_13_DUN_14DelArticulo;
	}

	/** 
	 * 63 - Ultima Fecha/Hora de entrega
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Código EAN-13/DUN-14 del artículo</td> <td>C</td> <td>15</td> <td>13</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEAN_13_DUN_14DelArticulo(String codigoEAN_13_DUN_14DelArticulo) {
		this.codigoEAN_13_DUN_14DelArticulo = codigoEAN_13_DUN_14DelArticulo;
	}

	/** 
	 * 4 - Tipo código artículo: identificación del tipo de código del artículo especificado en el campo 3. Este campo corresponde al elemento 7143. Los valores posibles son:
	 */ 
	public String getTipoCodigoArticulo() {
		return tipoCodigoArticulo;
	}

	/** 
	 * 4 - Tipo código artículo: identificación del tipo de código del artículo especificado en el campo 3. Este campo corresponde al elemento 7143. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Tipo código artículo</td> <td>C</td> <td>3</td> <td>28</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoCodigoArticulo(String tipoCodigoArticulo) {
		this.tipoCodigoArticulo = tipoCodigoArticulo;
	}

	/** 
	 * 5 - Tipo identificación de artículo: Código que suministra información sobre el artículo especificado en el campo 3. Este campo corresponde al elemento 7009. Los valores posibles son:
	 */ 
	public String getTipoIdentificacionDeArticulo_CU_DU_() {
		return tipoIdentificacionDeArticulo_CU_DU_;
	}

	/** 
	 * 5 - Tipo identificación de artículo: Código que suministra información sobre el artículo especificado en el campo 3. Este campo corresponde al elemento 7009. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Tipo identificación de artículo (CU/DU)</td> <td>C</td> <td>17</td> <td>31</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoIdentificacionDeArticulo_CU_DU_(String tipoIdentificacionDeArticulo_CU_DU_) {
		this.tipoIdentificacionDeArticulo_CU_DU_ = tipoIdentificacionDeArticulo_CU_DU_;
	}

	/** 
	 * 64 - Primera Fecha/Hora de entrega
	 */ 
	public String getDescripcion1Articulo() {
		return descripcion1Articulo;
	}

	/** 
	 * 64 - Primera Fecha/Hora de entrega
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Descripción 1 artículo</td> <td>C</td> <td>70</td> <td>48</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescripcion1Articulo(String descripcion1Articulo) {
		this.descripcion1Articulo = descripcion1Articulo;
	}

	/** 
	 * 17, 18 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 17 corresponde al elemento 2005. Los valores posibles son:
	 */ 
	public String getDescripcion2Articulo() {
		return descripcion2Articulo;
	}

	/** 
	 * 17, 18 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 17 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Descripción 2 artículo</td> <td>C</td> <td>70</td> <td>118</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescripcion2Articulo(String descripcion2Articulo) {
		this.descripcion2Articulo = descripcion2Articulo;
	}

	/** 
	 * 8 - Tipo artículo: Este campo corresponde al elemento 7081. Los valores posibles son:
	 */ 
	public String getTipoArticulo() {
		return tipoArticulo;
	}

	/** 
	 * 8 - Tipo artículo: Este campo corresponde al elemento 7081. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Tipo artículo</td> <td>C</td> <td>1</td> <td>188</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	/** 
	 * 19, 20 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 19 corresponde al elemento 2005. Los valores posibles son:
	 */ 
	public String getNumeroArticuloProveedor_SA_() {
		return numeroArticuloProveedor_SA_;
	}

	/** 
	 * 19, 20 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 19 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Número artículo proveedor (SA)</td> <td>C</td> <td>35</td> <td>189</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroArticuloProveedor_SA_(String numeroArticuloProveedor_SA_) {
		this.numeroArticuloProveedor_SA_ = numeroArticuloProveedor_SA_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroArticuloComprador_IN_BP_() {
		return numeroArticuloComprador_IN_BP_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Número artículo comprador (IN/BP)</td> <td>C</td> <td>35</td> <td>224</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroArticuloComprador_IN_BP_(String numeroArticuloComprador_IN_BP_) {
		this.numeroArticuloComprador_IN_BP_ = numeroArticuloComprador_IN_BP_;
	}

	/** 
	 * 11 - Fecha/Hora de envío
	 */ 
	public String getVariablePromocional_PV_() {
		return variablePromocional_PV_;
	}

	/** 
	 * 11 - Fecha/Hora de envío
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Variable promocional (PV)</td> <td>C</td> <td>35</td> <td>259</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setVariablePromocional_PV_(String variablePromocional_PV_) {
		this.variablePromocional_PV_ = variablePromocional_PV_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoEANDelArticuloAdicional_1__EN_() {
		return codigoEANDelArticuloAdicional_1__EN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Código EAN del artículo adicional (1) (EN)</td> <td>C</td> <td>35</td> <td>294</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEANDelArticuloAdicional_1__EN_(String codigoEANDelArticuloAdicional_1__EN_) {
		this.codigoEANDelArticuloAdicional_1__EN_ = codigoEANDelArticuloAdicional_1__EN_;
	}

	/** 
	 * 
	 */ 
	public Double getCantidadPedida_21_() {
		return cantidadPedida_21_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Cantidad pedida (21)</td> <td>N(12,3)</td> <td>16</td> <td>329</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadPedida_21_(Double cantidadPedida_21_) {
		this.cantidadPedida_21_ = cantidadPedida_21_;
	}

	/** 
	 * 
	 */ 
	public Double getCantidadBonificada_192_() {
		return cantidadBonificada_192_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Cantidad bonificada (192)</td> <td>N(12,3)</td> <td>16</td> <td>345</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadBonificada_192_(Double cantidadBonificada_192_) {
		this.cantidadBonificada_192_ = cantidadBonificada_192_;
	}

	/** 
	 * 15 - Calificador unidad de medida: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 */ 
	public String getCalificadorUnidadDeMedida() {
		return calificadorUnidadDeMedida;
	}

	/** 
	 * 15 - Calificador unidad de medida: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Calificador unidad de medida</td> <td>C</td> <td>6</td> <td>361</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorUnidadDeMedida(String calificadorUnidadDeMedida) {
		this.calificadorUnidadDeMedida = calificadorUnidadDeMedida;
	}

	/** 
	 * 
	 */ 
	public Double getNumeroUnidadesDeConsumoEnU_Expedicion() {
		return numeroUnidadesDeConsumoEnU_Expedicion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Número Unidades de Consumo en U. Expedición</td> <td>N(12,3)</td> <td>16</td> <td>367</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroUnidadesDeConsumoEnU_Expedicion(Double numeroUnidadesDeConsumoEnU_Expedicion) {
		this.numeroUnidadesDeConsumoEnU_Expedicion = numeroUnidadesDeConsumoEnU_Expedicion;
	}

	/** 
	 * 17, 18 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 17 corresponde al elemento 2005. Los valores posibles son:
	 */ 
	public String getCalificadorFecha_Hora1_2_11_64_() {
		return calificadorFecha_Hora1_2_11_64_;
	}

	/** 
	 * 17, 18 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 17 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Calificador Fecha/Hora 1 (2-11-64)</td> <td>C</td> <td>3</td> <td>383</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha_Hora1_2_11_64_(String calificadorFecha_Hora1_2_11_64_) {
		this.calificadorFecha_Hora1_2_11_64_ = calificadorFecha_Hora1_2_11_64_;
	}

	/** 
	 * 17, 18 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 17 corresponde al elemento 2005. Los valores posibles son:
	 */ 
	public String getFecha_Hora1() {
		return fecha_Hora1;
	}

	/** 
	 * 17, 18 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 17 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Fecha/Hora 1</td> <td>C</td> <td>12</td> <td>386</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_Hora1(String fecha_Hora1) {
		this.fecha_Hora1 = fecha_Hora1;
	}

	/** 
	 * 19, 20 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 19 corresponde al elemento 2005. Los valores posibles son:
	 */ 
	public String getCalificadorFecha_Hora2_2_11_63_() {
		return calificadorFecha_Hora2_2_11_63_;
	}

	/** 
	 * 19, 20 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 19 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Calificador Fecha/Hora 2 (2-11-63)</td> <td>C</td> <td>3</td> <td>398</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha_Hora2_2_11_63_(String calificadorFecha_Hora2_2_11_63_) {
		this.calificadorFecha_Hora2_2_11_63_ = calificadorFecha_Hora2_2_11_63_;
	}

	/** 
	 * 19, 20 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 19 corresponde al elemento 2005. Los valores posibles son:
	 */ 
	public String getFecha_Hora2() {
		return fecha_Hora2;
	}

	/** 
	 * 19, 20 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 19 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Fecha/Hora 2</td> <td>C</td> <td>12</td> <td>401</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_Hora2(String fecha_Hora2) {
		this.fecha_Hora2 = fecha_Hora2;
	}

	/** 
	 * 21 - Importe neto línea: (Cantidad x Precio) - Descuentos.
	 */ 
	public Double getImporteNetoLinea_203_() {
		return importeNetoLinea_203_;
	}

	/** 
	 * 21 - Importe neto línea: (Cantidad x Precio) - Descuentos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Importe neto línea (203)</td> <td>N(14,3)</td> <td>18</td> <td>413</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteNetoLinea_203_(Double importeNetoLinea_203_) {
		this.importeNetoLinea_203_ = importeNetoLinea_203_;
	}

	/** 
	 * 22 - Precio bruto unitario: Excluye descuentos, cargos e impuestos.
	 */ 
	public Double getPrecioBrutoUnitario_AAB_() {
		return precioBrutoUnitario_AAB_;
	}

	/** 
	 * 22 - Precio bruto unitario: Excluye descuentos, cargos e impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Precio bruto unitario (AAB)</td> <td>N(12,3)</td> <td>16</td> <td>431</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPrecioBrutoUnitario_AAB_(Double precioBrutoUnitario_AAB_) {
		this.precioBrutoUnitario_AAB_ = precioBrutoUnitario_AAB_;
	}

	/** 
	 * 23 - Precio neto unitario: Incluye descuentos y cargos pero no impuestos.
	 */ 
	public Double getPrecioNetoUnitario_AAA_() {
		return precioNetoUnitario_AAA_;
	}

	/** 
	 * 23 - Precio neto unitario: Incluye descuentos y cargos pero no impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Precio neto unitario (AAA)</td> <td>N(12,3)</td> <td>16</td> <td>447</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPrecioNetoUnitario_AAA_(Double precioNetoUnitario_AAA_) {
		this.precioNetoUnitario_AAA_ = precioNetoUnitario_AAA_;
	}

	/** 
	 * 
	 */ 
	public Double getPrecioATituloInformativo_INF_() {
		return precioATituloInformativo_INF_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Precio a título Informativo (INF)</td> <td>N(12,3)</td> <td>16</td> <td>463</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPrecioATituloInformativo_INF_(Double precioATituloInformativo_INF_) {
		this.precioATituloInformativo_INF_ = precioATituloInformativo_INF_;
	}

	/** 
	 * 
	 */ 
	public String getCalificadorUnidadDeMedidaPrecio() {
		return calificadorUnidadDeMedidaPrecio;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Calificador unidad de medida precio</td> <td>C</td> <td>6</td> <td>479</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorUnidadDeMedidaPrecio(String calificadorUnidadDeMedidaPrecio) {
		this.calificadorUnidadDeMedidaPrecio = calificadorUnidadDeMedidaPrecio;
	}

	/** 
	 * 26 - Calificador IVA/IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */ 
	public String getCalificadorIVA_IGIC() {
		return calificadorIVA_IGIC;
	}

	/** 
	 * 26 - Calificador IVA/IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Calificador IVA/IGIC</td> <td>C</td> <td>6</td> <td>485</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorIVA_IGIC(String calificadorIVA_IGIC) {
		this.calificadorIVA_IGIC = calificadorIVA_IGIC;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeIVA_IGIC() {
		return porcentajeIVA_IGIC;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Porcentaje IVA/IGIC</td> <td>N(3,2)</td> <td>6</td> <td>491</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeIVA_IGIC(Double porcentajeIVA_IGIC) {
		this.porcentajeIVA_IGIC = porcentajeIVA_IGIC;
	}

	/** 
	 * 
	 */ 
	public Double getImporteIVA_IGIC() {
		return importeIVA_IGIC;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Importe IVA/IGIC</td> <td>N(14,3)</td> <td>18</td> <td>497</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteIVA_IGIC(Double importeIVA_IGIC) {
		this.importeIVA_IGIC = importeIVA_IGIC;
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
	 * 		 <td>29</td> <td>% Recargo de Equivalencia</td> <td>N(3,2)</td> <td>6</td> <td>515</td> <td>O</td>
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
	 * 		 <td>30</td> <td>Importe Recargo de Equivalencia</td> <td>N(14,3)</td> <td>18</td> <td>521</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteRecargoDeEquivalencia(Double importeRecargoDeEquivalencia) {
		this.importeRecargoDeEquivalencia = importeRecargoDeEquivalencia;
	}

	/** 
	 * 31 - Calificador Otro Tipo de Impuesto: Cuando el artículo tiene un impuesto adicional. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */ 
	public String getCalificadorOtroTipoDeImpuesto() {
		return calificadorOtroTipoDeImpuesto;
	}

	/** 
	 * 31 - Calificador Otro Tipo de Impuesto: Cuando el artículo tiene un impuesto adicional. Este campo corresponde al elemento 5153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>31</td> <td>Calificador Otro Tipo de Impuesto</td> <td>C</td> <td>6</td> <td>539</td> <td>O</td>
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
	 * 		 <td>32</td> <td>% Otro Tipo de Impuesto</td> <td>N(3,2)</td> <td>6</td> <td>545</td> <td>O</td>
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
	 * 		 <td>33</td> <td>Importe Otro Tipo de Impuesto</td> <td>N(14,3)</td> <td>18</td> <td>551</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteOtroTipoDeImpuesto(Double importeOtroTipoDeImpuesto) {
		this.importeOtroTipoDeImpuesto = importeOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 */ 
	public Double getPesoNeto_PD__AAA_() {
		return pesoNeto_PD__AAA_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>34</td> <td>Peso neto (PD) (AAA)</td> <td>N(14,3)</td> <td>18</td> <td>569</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoNeto_PD__AAA_(Double pesoNeto_PD__AAA_) {
		this.pesoNeto_PD__AAA_ = pesoNeto_PD__AAA_;
	}

	/** 
	 * 
	 */ 
	public String getCalificadorUnidadDeMedidaPeso() {
		return calificadorUnidadDeMedidaPeso;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>35</td> <td>Calificador unidad de medida peso</td> <td>C</td> <td>6</td> <td>587</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorUnidadDeMedidaPeso(String calificadorUnidadDeMedidaPeso) {
		this.calificadorUnidadDeMedidaPeso = calificadorUnidadDeMedidaPeso;
	}

	/** 
	 * 36 - Fecha caducidad
	 */ 
	public String getDescripcionDelModelo_BRN_() {
		return descripcionDelModelo_BRN_;
	}

	/** 
	 * 36 - Fecha caducidad
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>36</td> <td>Descripción del Modelo (BRN)</td> <td>C</td> <td>25</td> <td>593</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescripcionDelModelo_BRN_(String descripcionDelModelo_BRN_) {
		this.descripcionDelModelo_BRN_ = descripcionDelModelo_BRN_;
	}

	/** 
	 * 
	 */ 
	public String getColor_35_() {
		return color_35_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>37</td> <td>Color (35)</td> <td>C</td> <td>25</td> <td>618</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setColor_35_(String color_35_) {
		this.color_35_ = color_35_;
	}

	/** 
	 * 
	 */ 
	public String getAnchuraOTalla_UP5_() {
		return anchuraOTalla_UP5_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>38</td> <td>Anchura o talla (UP5)</td> <td>C</td> <td>25</td> <td>643</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setAnchuraOTalla_UP5_(String anchuraOTalla_UP5_) {
		this.anchuraOTalla_UP5_ = anchuraOTalla_UP5_;
	}

	/** 
	 * 
	 */ 
	public String getPresentacion_Cantidad_Formato_U03_() {
		return presentacion_Cantidad_Formato_U03_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>39</td> <td>Presentación, cantidad, formato (U03)</td> <td>C</td> <td>25</td> <td>668</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPresentacion_Cantidad_Formato_U03_(String presentacion_Cantidad_Formato_U03_) {
		this.presentacion_Cantidad_Formato_U03_ = presentacion_Cantidad_Formato_U03_;
	}

	/** 
	 * 40 - Código grupo producto comprador: Dentro del sector de Salud, se utiliza para indicar el código universal SAS.
	 */ 
	public String getCodigoGrupoArticuloComprador_GB_() {
		return codigoGrupoArticuloComprador_GB_;
	}

	/** 
	 * 40 - Código grupo producto comprador: Dentro del sector de Salud, se utiliza para indicar el código universal SAS.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>40</td> <td>Código grupo artículo comprador (GB)</td> <td>C</td> <td>35</td> <td>693</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoGrupoArticuloComprador_GB_(String codigoGrupoArticuloComprador_GB_) {
		this.codigoGrupoArticuloComprador_GB_ = codigoGrupoArticuloComprador_GB_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeSerieDelArticulo_SN_() {
		return numeroDeSerieDelArticulo_SN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>41</td> <td>Número de serie del artículo (SN)</td> <td>C</td> <td>35</td> <td>728</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeSerieDelArticulo_SN_(String numeroDeSerieDelArticulo_SN_) {
		this.numeroDeSerieDelArticulo_SN_ = numeroDeSerieDelArticulo_SN_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroArticuloFabricante_MF_() {
		return numeroArticuloFabricante_MF_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>42</td> <td>Número artículo fabricante (MF)</td> <td>C</td> <td>35</td> <td>763</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroArticuloFabricante_MF_(String numeroArticuloFabricante_MF_) {
		this.numeroArticuloFabricante_MF_ = numeroArticuloFabricante_MF_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeLote_NB_() {
		return numeroDeLote_NB_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>43</td> <td>Número de lote (NB)</td> <td>C</td> <td>35</td> <td>798</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLote_NB_(String numeroDeLote_NB_) {
		this.numeroDeLote_NB_ = numeroDeLote_NB_;
	}

	/** 
	 * 44, 45 - Fecha/Hora 3: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 44 corresponde al elemento 2005. Los valores posibles son:
	 */ 
	public String getCalificadorFecha_Hora3() {
		return calificadorFecha_Hora3;
	}

	/** 
	 * 44, 45 - Fecha/Hora 3: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 44 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>44</td> <td>Calificador Fecha/Hora 3</td> <td>C</td> <td>3</td> <td>833</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha_Hora3(String calificadorFecha_Hora3) {
		this.calificadorFecha_Hora3 = calificadorFecha_Hora3;
	}

	/** 
	 * 44, 45 - Fecha/Hora 3: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 44 corresponde al elemento 2005. Los valores posibles son:
	 */ 
	public String getFecha_Hora3() {
		return fecha_Hora3;
	}

	/** 
	 * 44, 45 - Fecha/Hora 3: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 44 corresponde al elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>45</td> <td>Fecha/Hora 3</td> <td>C</td> <td>12</td> <td>836</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_Hora3(String fecha_Hora3) {
		this.fecha_Hora3 = fecha_Hora3;
	}

	/** 
	 * 
	 */ 
	public Double getImporteLineaConImpuestos_388_() {
		return importeLineaConImpuestos_388_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>46</td> <td>Importe línea con impuestos (388)</td> <td>N(14,3)</td> <td>18</td> <td>848</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteLineaConImpuestos_388_(Double importeLineaConImpuestos_388_) {
		this.importeLineaConImpuestos_388_ = importeLineaConImpuestos_388_;
	}

	/** 
	 * 
	 */ 
	public Integer getBasePrecioNetoUnitario_AAA_() {
		return basePrecioNetoUnitario_AAA_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>47</td> <td>Base precio neto unitario (AAA)</td> <td>N(9)</td> <td>9</td> <td>866</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setBasePrecioNetoUnitario_AAA_(Integer basePrecioNetoUnitario_AAA_) {
		this.basePrecioNetoUnitario_AAA_ = basePrecioNetoUnitario_AAA_;
	}

	/** 
	 * 
	 */ 
	public Double getPrecioArticuloConImpuestos_NTP_() {
		return precioArticuloConImpuestos_NTP_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>48</td> <td>Precio artículo con impuestos (NTP)</td> <td>N(12,3)</td> <td>16</td> <td>875</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPrecioArticuloConImpuestos_NTP_(Double precioArticuloConImpuestos_NTP_) {
		this.precioArticuloConImpuestos_NTP_ = precioArticuloConImpuestos_NTP_;
	}

	/** 
	 * 
	 */ 
	public Integer getBasePrecioArticuloConImpuestos_NTP_() {
		return basePrecioArticuloConImpuestos_NTP_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>49</td> <td>Base precio artículo con impuestos (NTP)</td> <td>N(9)</td> <td>9</td> <td>891</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setBasePrecioArticuloConImpuestos_NTP_(Integer basePrecioArticuloConImpuestos_NTP_) {
		this.basePrecioArticuloConImpuestos_NTP_ = basePrecioArticuloConImpuestos_NTP_;
	}

	/** 
	 * 50 - Número Albarán: Se utiliza en el pedido de consigna para informar el albarán en la que se entregó la mercancía que hay que facturar.
	 */ 
	public String getNumeroAlbaran_DQ_() {
		return numeroAlbaran_DQ_;
	}

	/** 
	 * 50 - Número Albarán: Se utiliza en el pedido de consigna para informar el albarán en la que se entregó la mercancía que hay que facturar.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>50</td> <td>Número Albarán (DQ)</td> <td>C</td> <td>17</td> <td>900</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroAlbaran_DQ_(String numeroAlbaran_DQ_) {
		this.numeroAlbaran_DQ_ = numeroAlbaran_DQ_;
	}

	/** 
	 * 
	 */ 
	public String getFechaAlbaran() {
		return fechaAlbaran;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>51</td> <td>Fecha Albarán</td> <td>C</td> <td>12</td> <td>917</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaAlbaran(String fechaAlbaran) {
		this.fechaAlbaran = fechaAlbaran;
	}

	/** 
	 * 52 - Código cliente final: Dentro del sector de Salud, se utiliza para indicar el código o historia clínica del paciente.
	 */ 
	public String getCodigoClienteFinal() {
		return codigoClienteFinal;
	}

	/** 
	 * 52 - Código cliente final: Dentro del sector de Salud, se utiliza para indicar el código o historia clínica del paciente.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>52</td> <td>Código cliente final</td> <td>C</td> <td>17</td> <td>929</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoClienteFinal(String codigoClienteFinal) {
		this.codigoClienteFinal = codigoClienteFinal;
	}

	/** 
	 * 
	 */ 
	public String getNombreClienteFinal() {
		return nombreClienteFinal;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>53</td> <td>Nombre cliente final</td> <td>C</td> <td>70</td> <td>946</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombreClienteFinal(String nombreClienteFinal) {
		this.nombreClienteFinal = nombreClienteFinal;
	}

	/** 
	 * 
	 */ 
	public String getDireccionClienteFinal() {
		return direccionClienteFinal;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>54</td> <td>Dirección cliente final</td> <td>C</td> <td>70</td> <td>1016</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDireccionClienteFinal(String direccionClienteFinal) {
		this.direccionClienteFinal = direccionClienteFinal;
	}

	/** 
	 * 
	 */ 
	public String getPoblacionClienteFinal() {
		return poblacionClienteFinal;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>55</td> <td>Población cliente final</td> <td>C</td> <td>35</td> <td>1086</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPoblacionClienteFinal(String poblacionClienteFinal) {
		this.poblacionClienteFinal = poblacionClienteFinal;
	}

	/** 
	 * 
	 */ 
	public String getCodigoPostalClienteFinal() {
		return codigoPostalClienteFinal;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>56</td> <td>Código Postal cliente final</td> <td>C</td> <td>9</td> <td>1121</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoPostalClienteFinal(String codigoPostalClienteFinal) {
		this.codigoPostalClienteFinal = codigoPostalClienteFinal;
	}

	/** 
	 * 57 - Identificador producto/línea pedido: Este identificador se utiliza solo para Carrefour Servicios Generales. Se compone del número de pedido más la línea del pedido.
	 */ 
	public String getIdentificadorProducto_lineaPedido_MP_() {
		return identificadorProducto_lineaPedido_MP_;
	}

	/** 
	 * 57 - Identificador producto/línea pedido: Este identificador se utiliza solo para Carrefour Servicios Generales. Se compone del número de pedido más la línea del pedido.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>57</td> <td>Identificador producto/línea pedido (MP)</td> <td>C</td> <td>15</td> <td>1130</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIdentificadorProducto_lineaPedido_MP_(String identificadorProducto_lineaPedido_MP_) {
		this.identificadorProducto_lineaPedido_MP_ = identificadorProducto_lineaPedido_MP_;
	}

	/** 
	 * 4 - Tipo código artículo: identificación del tipo de código del artículo especificado en el campo 3. Este campo corresponde al elemento 7143. Los valores posibles son:
	 */
	public enum ERE1L_4 {
		EA_EN("EN"),
		UPC__CODIGO_PRODUCTO_UNIVERSAL_UP("UP"),
		;
		
		private String value;
		
		private ERE1L_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 5 - Tipo identificación de artículo: Código que suministra información sobre el artículo especificado en el campo 3. Este campo corresponde al elemento 7009. Los valores posibles son:
	 */
	public enum ERE1L_5 {
		UNIDAD_DE_CONSUM_CU("CU"),
		UNIDAD_DE_EXPEDICIO_DU("DU"),
		;
		
		private String value;
		
		private ERE1L_5(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_5 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 8 - Tipo artículo: Este campo corresponde al elemento 7081. Los valores posibles son:
	 */
	public enum ERE1L_8 {
		MERCANCIA_M("M"),
		MATERIAL_CONSIGNAD_C("C"),
		SERVICIO_S("S"),
		;
		
		private String value;
		
		private ERE1L_8(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_8 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 15 - Calificador unidad de medida: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 */
	public enum ERE1L_15 {
		KILOGRAM_KGM("KGM"),
		LITR_LTR("LTR"),
		;
		
		private String value;
		
		private ERE1L_15(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_15 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 17, 18 - Fecha/Hora 1: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 17 corresponde al elemento 2005. Los valores posibles son:
	 */
	public enum ERE1L_17_18 {
		FECHA_HORA_DE_ENTREGA_REQUERID_2("2"),
		FECHA_HORA_DE_ENVI_11("11"),
		PRIMERA_FECHA_HORA_DE_ENTREG_64("64"),
		ULTIMA_FECHA_HORA_DE_ENTREG_63("63"),
		FECHA_CADUCIDA_36("36"),
		FECHA_DE_INTERVENCIO_1("1"),
		;
		
		private String value;
		
		private ERE1L_17_18(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_17_18 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 19, 20 - Fecha/Hora 2: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 19 corresponde al elemento 2005. Los valores posibles son:
	 */
	public enum ERE1L_19_20 {
		FECHA_HORA_DE_ENTREGA_REQUERID_2("2"),
		FECHA_HORA_DE_ENVI_11("11"),
		PRIMERA_FECHA_HORA_DE_ENTREG_64("64"),
		ULTIMA_FECHA_HORA_DE_ENTREG_63("63"),
		FECHA_CADUCIDA_36("36"),
		FECHA_DE_INTERVENCIO_1("1"),
		;
		
		private String value;
		
		private ERE1L_19_20(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_19_20 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 26 - Calificador IVA/IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */
	public enum ERE1L_26 {
		IV_VAT("VAT"),
		IGIC__IMPUESTO_GENERAL_DE_LAS_ISLAS_CANARIAS_IGI("IGI"),
		;
		
		private String value;
		
		private ERE1L_26(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_26 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 31 - Calificador Otro Tipo de Impuesto: Cuando el artículo tiene un impuesto adicional. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */
	public enum ERE1L_31 {
		DERECHOS_DE_AUTO_DA("DA"),
		IPS_IPS("IPS"),
		EXENT_EXT("EXT"),
		IRPF__DENTRO_DE__FACTURA__ES_UN_IMPUESTO_RETENIDO_NO_REPERCUTIDO_IRP("IRP"),
		IMPUESTO_DE_ALCOHOLE_ACT("ACT"),
		;
		
		private String value;
		
		private ERE1L_31(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_31 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 44, 45 - Fecha/Hora 3: Cuando es necesario informar otra fecha relacionada con la línea del artículo. El campo 44 corresponde al elemento 2005. Los valores posibles son:
	 */
	public enum ERE1L_44_45 {
		FECHA_HORA_DE_ENTREGA_REQUERID_2("2"),
		FECHA_HORA_DE_ENVI_11("11"),
		PRIMERA_FECHA_HORA_DE_ENTREG_64("64"),
		ULTIMA_FECHA_HORA_DE_ENTREG_63("63"),
		FECHA_CADUCIDA_36("36"),
		FECHA_DE_INTERVENCIO_1("1"),
		;
		
		private String value;
		
		private ERE1L_44_45(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1L_44_45 enumByValue(String value) {
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