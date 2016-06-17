package com.esferalia.aon.file.seres.udapa.sales.data;

public class ERE1L {

	private String lineas;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeLineaArticulo;
	private String codigoDeArticuloEAN_13ODUN_14;
	private String tipoDeNumeroDeArticulo;
	private String descripcionDelArticulo1;
	private String descripcionDelArticulo2;
	private String tipoArticulo;
	private String codigoInternoArticuloProveedor_SA_;
	private String codigoInternoArticuloCliente_IN_;
	private String codigoVariablePromocional_1__PV_;
	private String codigoUnidadDeExpedicion_1__EN_;
	private String cantidadPedida_21_;
	private String cantidadBonificada_192_;
	private String calificadorUnidadDeMedidaCantidad;
	private String numeroDeU_C_EnUnidadDeExpedicion;
	private String calificadorFechaDeEntrega_3_63_64_PER_;
	private String fechaDeEntrega1;
	private String horaDeEntrega1;
	private String fechaDeEntrega2;
	private String horaDeEntrega2;
	private String importeTotalNetoLinea_203_;
	private String precioBrutoUnitario_AAB_;
	private String precioNetoUnitario_AAA_;
	private String precioATituloInformativo_INF_;
	private String calificadorUnidadDeMedidaPrecio;
	private String calificadorIVA_IGIG;
	private String porcentajeImpuestoIVA_IGIC;
	private String importeImpuestoIVA_IGIC;
	private String porcentajeRecargoDeEquivalencia;
	private String importeRecargoDeEquivalencia;
	private String calificadorOtroTipoDeImpuesto;
	private String porcentajeOtroTipoDeImpuesto;
	private String importeOtroTipoDeImpuesto;
	private String pesoNeto_PD__AAA_;
	private String unidadDeMedidaPeso;
	private String tipoArticuloEAN_CU_DU_;
	private String descripcionDelModelo_BRN_;
	private String variedad1_35_;
	private String variedad2_UP5_;
	private String presentacion_Cantidad_Formato_U03_;


	private static java.util.regex.Pattern PATTERN_ERE1L_lineas = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_tipoDePedido_220_221_224_226_22E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_numeroDePedido = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_numeroDeLineaArticulo = java.util.regex.Pattern.compile("^.{63}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_codigoDeArticuloEAN_13ODUN_14 = java.util.regex.Pattern.compile("^.{69}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_tipoDeNumeroDeArticulo = java.util.regex.Pattern.compile("^.{84}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_descripcionDelArticulo1 = java.util.regex.Pattern.compile("^.{87}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_descripcionDelArticulo2 = java.util.regex.Pattern.compile("^.{157}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_tipoArticulo = java.util.regex.Pattern.compile("^.{227}(.{1}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_codigoInternoArticuloProveedor_SA_ = java.util.regex.Pattern.compile("^.{228}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_codigoInternoArticuloCliente_IN_ = java.util.regex.Pattern.compile("^.{263}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_codigoVariablePromocional_1__PV_ = java.util.regex.Pattern.compile("^.{298}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_codigoUnidadDeExpedicion_1__EN_ = java.util.regex.Pattern.compile("^.{333}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_cantidadPedida_21_ = java.util.regex.Pattern.compile("^.{368}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_cantidadBonificada_192_ = java.util.regex.Pattern.compile("^.{384}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_calificadorUnidadDeMedidaCantidad = java.util.regex.Pattern.compile("^.{400}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_numeroDeU_C_EnUnidadDeExpedicion = java.util.regex.Pattern.compile("^.{406}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_calificadorFechaDeEntrega_3_63_64_PER_ = java.util.regex.Pattern.compile("^.{422}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_fechaDeEntrega1 = java.util.regex.Pattern.compile("^.{425}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_horaDeEntrega1 = java.util.regex.Pattern.compile("^.{433}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_fechaDeEntrega2 = java.util.regex.Pattern.compile("^.{437}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_horaDeEntrega2 = java.util.regex.Pattern.compile("^.{445}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_importeTotalNetoLinea_203_ = java.util.regex.Pattern.compile("^.{449}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_precioBrutoUnitario_AAB_ = java.util.regex.Pattern.compile("^.{467}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_precioNetoUnitario_AAA_ = java.util.regex.Pattern.compile("^.{483}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_precioATituloInformativo_INF_ = java.util.regex.Pattern.compile("^.{499}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_calificadorUnidadDeMedidaPrecio = java.util.regex.Pattern.compile("^.{515}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_calificadorIVA_IGIG = java.util.regex.Pattern.compile("^.{521}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_porcentajeImpuestoIVA_IGIC = java.util.regex.Pattern.compile("^.{527}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_importeImpuestoIVA_IGIC = java.util.regex.Pattern.compile("^.{533}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_porcentajeRecargoDeEquivalencia = java.util.regex.Pattern.compile("^.{551}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_importeRecargoDeEquivalencia = java.util.regex.Pattern.compile("^.{557}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_calificadorOtroTipoDeImpuesto = java.util.regex.Pattern.compile("^.{575}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_porcentajeOtroTipoDeImpuesto = java.util.regex.Pattern.compile("^.{581}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_importeOtroTipoDeImpuesto = java.util.regex.Pattern.compile("^.{587}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_pesoNeto_PD__AAA_ = java.util.regex.Pattern.compile("^.{605}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_unidadDeMedidaPeso = java.util.regex.Pattern.compile("^.{623}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_tipoArticuloEAN_CU_DU_ = java.util.regex.Pattern.compile("^.{629}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_descripcionDelModelo_BRN_ = java.util.regex.Pattern.compile("^.{646}(.{25}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_variedad1_35_ = java.util.regex.Pattern.compile("^.{681}(.{25}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_variedad2_UP5_ = java.util.regex.Pattern.compile("^.{716}(.{25}).*");
	private static java.util.regex.Pattern PATTERN_ERE1L_presentacion_Cantidad_Formato_U03_ = java.util.regex.Pattern.compile("^.{751}(.{25}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_ERE1L_lineas.matcher(value)).find()) {
			setLineas(m.group(1));
		}
		if((m = PATTERN_ERE1L_tipoDePedido_220_221_224_226_22E_.matcher(value)).find()) {
			setTipoDePedido_220_221_224_226_22E_(m.group(1));
		}
		if((m = PATTERN_ERE1L_numeroDePedido.matcher(value)).find()) {
			setNumeroDePedido(m.group(1));
		}
		if((m = PATTERN_ERE1L_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_ERE1L_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_ERE1L_numeroDeLineaArticulo.matcher(value)).find()) {
			setNumeroDeLineaArticulo(m.group(1));
		}
		if((m = PATTERN_ERE1L_codigoDeArticuloEAN_13ODUN_14.matcher(value)).find()) {
			setCodigoDeArticuloEAN_13ODUN_14(m.group(1));
		}
		if((m = PATTERN_ERE1L_tipoDeNumeroDeArticulo.matcher(value)).find()) {
			setTipoDeNumeroDeArticulo(m.group(1));
		}
		if((m = PATTERN_ERE1L_descripcionDelArticulo1.matcher(value)).find()) {
			setDescripcionDelArticulo1(m.group(1));
		}
		if((m = PATTERN_ERE1L_descripcionDelArticulo2.matcher(value)).find()) {
			setDescripcionDelArticulo2(m.group(1));
		}
		if((m = PATTERN_ERE1L_tipoArticulo.matcher(value)).find()) {
			setTipoArticulo(m.group(1));
		}
		if((m = PATTERN_ERE1L_codigoInternoArticuloProveedor_SA_.matcher(value)).find()) {
			setCodigoInternoArticuloProveedor_SA_(m.group(1));
		}
		if((m = PATTERN_ERE1L_codigoInternoArticuloCliente_IN_.matcher(value)).find()) {
			setCodigoInternoArticuloCliente_IN_(m.group(1));
		}
		if((m = PATTERN_ERE1L_codigoVariablePromocional_1__PV_.matcher(value)).find()) {
			setCodigoVariablePromocional_1__PV_(m.group(1));
		}
		if((m = PATTERN_ERE1L_codigoUnidadDeExpedicion_1__EN_.matcher(value)).find()) {
			setCodigoUnidadDeExpedicion_1__EN_(m.group(1));
		}
		if((m = PATTERN_ERE1L_cantidadPedida_21_.matcher(value)).find()) {
			setCantidadPedida_21_(m.group(1));
		}
		if((m = PATTERN_ERE1L_cantidadBonificada_192_.matcher(value)).find()) {
			setCantidadBonificada_192_(m.group(1));
		}
		if((m = PATTERN_ERE1L_calificadorUnidadDeMedidaCantidad.matcher(value)).find()) {
			setCalificadorUnidadDeMedidaCantidad(m.group(1));
		}
		if((m = PATTERN_ERE1L_numeroDeU_C_EnUnidadDeExpedicion.matcher(value)).find()) {
			setNumeroDeU_C_EnUnidadDeExpedicion(m.group(1));
		}
		if((m = PATTERN_ERE1L_calificadorFechaDeEntrega_3_63_64_PER_.matcher(value)).find()) {
			setCalificadorFechaDeEntrega_3_63_64_PER_(m.group(1));
		}
		if((m = PATTERN_ERE1L_fechaDeEntrega1.matcher(value)).find()) {
			setFechaDeEntrega1(m.group(1));
		}
		if((m = PATTERN_ERE1L_horaDeEntrega1.matcher(value)).find()) {
			setHoraDeEntrega1(m.group(1));
		}
		if((m = PATTERN_ERE1L_fechaDeEntrega2.matcher(value)).find()) {
			setFechaDeEntrega2(m.group(1));
		}
		if((m = PATTERN_ERE1L_horaDeEntrega2.matcher(value)).find()) {
			setHoraDeEntrega2(m.group(1));
		}
		if((m = PATTERN_ERE1L_importeTotalNetoLinea_203_.matcher(value)).find()) {
			setImporteTotalNetoLinea_203_(m.group(1));
		}
		if((m = PATTERN_ERE1L_precioBrutoUnitario_AAB_.matcher(value)).find()) {
			setPrecioBrutoUnitario_AAB_(m.group(1));
		}
		if((m = PATTERN_ERE1L_precioNetoUnitario_AAA_.matcher(value)).find()) {
			setPrecioNetoUnitario_AAA_(m.group(1));
		}
		if((m = PATTERN_ERE1L_precioATituloInformativo_INF_.matcher(value)).find()) {
			setPrecioATituloInformativo_INF_(m.group(1));
		}
		if((m = PATTERN_ERE1L_calificadorUnidadDeMedidaPrecio.matcher(value)).find()) {
			setCalificadorUnidadDeMedidaPrecio(m.group(1));
		}
		if((m = PATTERN_ERE1L_calificadorIVA_IGIG.matcher(value)).find()) {
			setCalificadorIVA_IGIG(m.group(1));
		}
		if((m = PATTERN_ERE1L_porcentajeImpuestoIVA_IGIC.matcher(value)).find()) {
			setPorcentajeImpuestoIVA_IGIC(m.group(1));
		}
		if((m = PATTERN_ERE1L_importeImpuestoIVA_IGIC.matcher(value)).find()) {
			setImporteImpuestoIVA_IGIC(m.group(1));
		}
		if((m = PATTERN_ERE1L_porcentajeRecargoDeEquivalencia.matcher(value)).find()) {
			setPorcentajeRecargoDeEquivalencia(m.group(1));
		}
		if((m = PATTERN_ERE1L_importeRecargoDeEquivalencia.matcher(value)).find()) {
			setImporteRecargoDeEquivalencia(m.group(1));
		}
		if((m = PATTERN_ERE1L_calificadorOtroTipoDeImpuesto.matcher(value)).find()) {
			setCalificadorOtroTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_ERE1L_porcentajeOtroTipoDeImpuesto.matcher(value)).find()) {
			setPorcentajeOtroTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_ERE1L_importeOtroTipoDeImpuesto.matcher(value)).find()) {
			setImporteOtroTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_ERE1L_pesoNeto_PD__AAA_.matcher(value)).find()) {
			setPesoNeto_PD__AAA_(m.group(1));
		}
		if((m = PATTERN_ERE1L_unidadDeMedidaPeso.matcher(value)).find()) {
			setUnidadDeMedidaPeso(m.group(1));
		}
		if((m = PATTERN_ERE1L_tipoArticuloEAN_CU_DU_.matcher(value)).find()) {
			setTipoArticuloEAN_CU_DU_(m.group(1));
		}
		if((m = PATTERN_ERE1L_descripcionDelModelo_BRN_.matcher(value)).find()) {
			setDescripcionDelModelo_BRN_(m.group(1));
		}
		if((m = PATTERN_ERE1L_variedad1_35_.matcher(value)).find()) {
			setVariedad1_35_(m.group(1));
		}
		if((m = PATTERN_ERE1L_variedad2_UP5_.matcher(value)).find()) {
			setVariedad2_UP5_(m.group(1));
		}
		if((m = PATTERN_ERE1L_presentacion_Cantidad_Formato_U03_.matcher(value)).find()) {
			setPresentacion_Cantidad_Formato_U03_(m.group(1));
		}
	}


	public String getLineas() {
		return lineas;
	}
	public void setLineas(String lineas) {
		this.lineas = lineas;
	}
	public String getTipoDePedido_220_221_224_226_22E_() {
		return tipoDePedido_220_221_224_226_22E_;
	}
	public void setTipoDePedido_220_221_224_226_22E_(String tipoDePedido_220_221_224_226_22E_) {
		this.tipoDePedido_220_221_224_226_22E_ = tipoDePedido_220_221_224_226_22E_;
	}
	public String getNumeroDePedido() {
		return numeroDePedido;
	}
	public void setNumeroDePedido(String numeroDePedido) {
		this.numeroDePedido = numeroDePedido;
	}
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}
	public void setCodigoEmisor_MS_(String codigoEmisor_MS_) {
		this.codigoEmisor_MS_ = codigoEmisor_MS_;
	}
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}
	public String getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}
	public void setNumeroDeLineaArticulo(String numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
	}
	public String getCodigoDeArticuloEAN_13ODUN_14() {
		return codigoDeArticuloEAN_13ODUN_14;
	}
	public void setCodigoDeArticuloEAN_13ODUN_14(String codigoDeArticuloEAN_13ODUN_14) {
		this.codigoDeArticuloEAN_13ODUN_14 = codigoDeArticuloEAN_13ODUN_14;
	}
	public String getTipoDeNumeroDeArticulo() {
		return tipoDeNumeroDeArticulo;
	}
	public void setTipoDeNumeroDeArticulo(String tipoDeNumeroDeArticulo) {
		this.tipoDeNumeroDeArticulo = tipoDeNumeroDeArticulo;
	}
	public String getDescripcionDelArticulo1() {
		return descripcionDelArticulo1;
	}
	public void setDescripcionDelArticulo1(String descripcionDelArticulo1) {
		this.descripcionDelArticulo1 = descripcionDelArticulo1;
	}
	public String getDescripcionDelArticulo2() {
		return descripcionDelArticulo2;
	}
	public void setDescripcionDelArticulo2(String descripcionDelArticulo2) {
		this.descripcionDelArticulo2 = descripcionDelArticulo2;
	}
	public String getTipoArticulo() {
		return tipoArticulo;
	}
	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}
	public String getCodigoInternoArticuloProveedor_SA_() {
		return codigoInternoArticuloProveedor_SA_;
	}
	public void setCodigoInternoArticuloProveedor_SA_(String codigoInternoArticuloProveedor_SA_) {
		this.codigoInternoArticuloProveedor_SA_ = codigoInternoArticuloProveedor_SA_;
	}
	public String getCodigoInternoArticuloCliente_IN_() {
		return codigoInternoArticuloCliente_IN_;
	}
	public void setCodigoInternoArticuloCliente_IN_(String codigoInternoArticuloCliente_IN_) {
		this.codigoInternoArticuloCliente_IN_ = codigoInternoArticuloCliente_IN_;
	}
	public String getCodigoVariablePromocional_1__PV_() {
		return codigoVariablePromocional_1__PV_;
	}
	public void setCodigoVariablePromocional_1__PV_(String codigoVariablePromocional_1__PV_) {
		this.codigoVariablePromocional_1__PV_ = codigoVariablePromocional_1__PV_;
	}
	public String getCodigoUnidadDeExpedicion_1__EN_() {
		return codigoUnidadDeExpedicion_1__EN_;
	}
	public void setCodigoUnidadDeExpedicion_1__EN_(String codigoUnidadDeExpedicion_1__EN_) {
		this.codigoUnidadDeExpedicion_1__EN_ = codigoUnidadDeExpedicion_1__EN_;
	}
	public String getCantidadPedida_21_() {
		return cantidadPedida_21_;
	}
	public void setCantidadPedida_21_(String cantidadPedida_21_) {
		this.cantidadPedida_21_ = cantidadPedida_21_;
	}
	public String getCantidadBonificada_192_() {
		return cantidadBonificada_192_;
	}
	public void setCantidadBonificada_192_(String cantidadBonificada_192_) {
		this.cantidadBonificada_192_ = cantidadBonificada_192_;
	}
	public String getCalificadorUnidadDeMedidaCantidad() {
		return calificadorUnidadDeMedidaCantidad;
	}
	public void setCalificadorUnidadDeMedidaCantidad(String calificadorUnidadDeMedidaCantidad) {
		this.calificadorUnidadDeMedidaCantidad = calificadorUnidadDeMedidaCantidad;
	}
	public String getNumeroDeU_C_EnUnidadDeExpedicion() {
		return numeroDeU_C_EnUnidadDeExpedicion;
	}
	public void setNumeroDeU_C_EnUnidadDeExpedicion(String numeroDeU_C_EnUnidadDeExpedicion) {
		this.numeroDeU_C_EnUnidadDeExpedicion = numeroDeU_C_EnUnidadDeExpedicion;
	}
	public String getCalificadorFechaDeEntrega_3_63_64_PER_() {
		return calificadorFechaDeEntrega_3_63_64_PER_;
	}
	public void setCalificadorFechaDeEntrega_3_63_64_PER_(String calificadorFechaDeEntrega_3_63_64_PER_) {
		this.calificadorFechaDeEntrega_3_63_64_PER_ = calificadorFechaDeEntrega_3_63_64_PER_;
	}
	public String getFechaDeEntrega1() {
		return fechaDeEntrega1;
	}
	public void setFechaDeEntrega1(String fechaDeEntrega1) {
		this.fechaDeEntrega1 = fechaDeEntrega1;
	}
	public String getHoraDeEntrega1() {
		return horaDeEntrega1;
	}
	public void setHoraDeEntrega1(String horaDeEntrega1) {
		this.horaDeEntrega1 = horaDeEntrega1;
	}
	public String getFechaDeEntrega2() {
		return fechaDeEntrega2;
	}
	public void setFechaDeEntrega2(String fechaDeEntrega2) {
		this.fechaDeEntrega2 = fechaDeEntrega2;
	}
	public String getHoraDeEntrega2() {
		return horaDeEntrega2;
	}
	public void setHoraDeEntrega2(String horaDeEntrega2) {
		this.horaDeEntrega2 = horaDeEntrega2;
	}
	public String getImporteTotalNetoLinea_203_() {
		return importeTotalNetoLinea_203_;
	}
	public void setImporteTotalNetoLinea_203_(String importeTotalNetoLinea_203_) {
		this.importeTotalNetoLinea_203_ = importeTotalNetoLinea_203_;
	}
	public String getPrecioBrutoUnitario_AAB_() {
		return precioBrutoUnitario_AAB_;
	}
	public void setPrecioBrutoUnitario_AAB_(String precioBrutoUnitario_AAB_) {
		this.precioBrutoUnitario_AAB_ = precioBrutoUnitario_AAB_;
	}
	public String getPrecioNetoUnitario_AAA_() {
		return precioNetoUnitario_AAA_;
	}
	public void setPrecioNetoUnitario_AAA_(String precioNetoUnitario_AAA_) {
		this.precioNetoUnitario_AAA_ = precioNetoUnitario_AAA_;
	}
	public String getPrecioATituloInformativo_INF_() {
		return precioATituloInformativo_INF_;
	}
	public void setPrecioATituloInformativo_INF_(String precioATituloInformativo_INF_) {
		this.precioATituloInformativo_INF_ = precioATituloInformativo_INF_;
	}
	public String getCalificadorUnidadDeMedidaPrecio() {
		return calificadorUnidadDeMedidaPrecio;
	}
	public void setCalificadorUnidadDeMedidaPrecio(String calificadorUnidadDeMedidaPrecio) {
		this.calificadorUnidadDeMedidaPrecio = calificadorUnidadDeMedidaPrecio;
	}
	public String getCalificadorIVA_IGIG() {
		return calificadorIVA_IGIG;
	}
	public void setCalificadorIVA_IGIG(String calificadorIVA_IGIG) {
		this.calificadorIVA_IGIG = calificadorIVA_IGIG;
	}
	public String getPorcentajeImpuestoIVA_IGIC() {
		return porcentajeImpuestoIVA_IGIC;
	}
	public void setPorcentajeImpuestoIVA_IGIC(String porcentajeImpuestoIVA_IGIC) {
		this.porcentajeImpuestoIVA_IGIC = porcentajeImpuestoIVA_IGIC;
	}
	public String getImporteImpuestoIVA_IGIC() {
		return importeImpuestoIVA_IGIC;
	}
	public void setImporteImpuestoIVA_IGIC(String importeImpuestoIVA_IGIC) {
		this.importeImpuestoIVA_IGIC = importeImpuestoIVA_IGIC;
	}
	public String getPorcentajeRecargoDeEquivalencia() {
		return porcentajeRecargoDeEquivalencia;
	}
	public void setPorcentajeRecargoDeEquivalencia(String porcentajeRecargoDeEquivalencia) {
		this.porcentajeRecargoDeEquivalencia = porcentajeRecargoDeEquivalencia;
	}
	public String getImporteRecargoDeEquivalencia() {
		return importeRecargoDeEquivalencia;
	}
	public void setImporteRecargoDeEquivalencia(String importeRecargoDeEquivalencia) {
		this.importeRecargoDeEquivalencia = importeRecargoDeEquivalencia;
	}
	public String getCalificadorOtroTipoDeImpuesto() {
		return calificadorOtroTipoDeImpuesto;
	}
	public void setCalificadorOtroTipoDeImpuesto(String calificadorOtroTipoDeImpuesto) {
		this.calificadorOtroTipoDeImpuesto = calificadorOtroTipoDeImpuesto;
	}
	public String getPorcentajeOtroTipoDeImpuesto() {
		return porcentajeOtroTipoDeImpuesto;
	}
	public void setPorcentajeOtroTipoDeImpuesto(String porcentajeOtroTipoDeImpuesto) {
		this.porcentajeOtroTipoDeImpuesto = porcentajeOtroTipoDeImpuesto;
	}
	public String getImporteOtroTipoDeImpuesto() {
		return importeOtroTipoDeImpuesto;
	}
	public void setImporteOtroTipoDeImpuesto(String importeOtroTipoDeImpuesto) {
		this.importeOtroTipoDeImpuesto = importeOtroTipoDeImpuesto;
	}
	public String getPesoNeto_PD__AAA_() {
		return pesoNeto_PD__AAA_;
	}
	public void setPesoNeto_PD__AAA_(String pesoNeto_PD__AAA_) {
		this.pesoNeto_PD__AAA_ = pesoNeto_PD__AAA_;
	}
	public String getUnidadDeMedidaPeso() {
		return unidadDeMedidaPeso;
	}
	public void setUnidadDeMedidaPeso(String unidadDeMedidaPeso) {
		this.unidadDeMedidaPeso = unidadDeMedidaPeso;
	}
	public String getTipoArticuloEAN_CU_DU_() {
		return tipoArticuloEAN_CU_DU_;
	}
	public void setTipoArticuloEAN_CU_DU_(String tipoArticuloEAN_CU_DU_) {
		this.tipoArticuloEAN_CU_DU_ = tipoArticuloEAN_CU_DU_;
	}
	public String getDescripcionDelModelo_BRN_() {
		return descripcionDelModelo_BRN_;
	}
	public void setDescripcionDelModelo_BRN_(String descripcionDelModelo_BRN_) {
		this.descripcionDelModelo_BRN_ = descripcionDelModelo_BRN_;
	}
	public String getVariedad1_35_() {
		return variedad1_35_;
	}
	public void setVariedad1_35_(String variedad1_35_) {
		this.variedad1_35_ = variedad1_35_;
	}
	public String getVariedad2_UP5_() {
		return variedad2_UP5_;
	}
	public void setVariedad2_UP5_(String variedad2_UP5_) {
		this.variedad2_UP5_ = variedad2_UP5_;
	}
	public String getPresentacion_Cantidad_Formato_U03_() {
		return presentacion_Cantidad_Formato_U03_;
	}
	public void setPresentacion_Cantidad_Formato_U03_(String presentacion_Cantidad_Formato_U03_) {
		this.presentacion_Cantidad_Formato_U03_ = presentacion_Cantidad_Formato_U03_;
	}

}