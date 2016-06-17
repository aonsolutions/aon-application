package com.esferalia.aon.file.seres.udapa.sales.data;

public class ERE1C {

	private String cabecera;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String funcionDelMensaje_31_;
	private String fechaDelDocumento_137__102_;
	private String calificadorFecha1;
	private String fechaDeServicio1;
	private String horaDeServicio1;
	private String calificadorFecha2;
	private String fechaDeServicio2;
	private String horaDeServicio2;
	private String condicionesEspeciales;
	private String numeroDePedidoAbierto_BO_;
	private String numeroDeListaDePrecios;
	private String numeroDePedidoProveedor_VN_;
	private String codigoComprador_BY_;
	private String codigoProveedor_SU_;
	private String codigoAQuienSeFactura_IV_;
	private String codigoPuntoDeEntrega_DP_;
	private String identificacionAdicionalP_Entrega_API_;
	private String numeroDeReposicion_ACD_;
	private String codigoPuntoDeExpedicion_PW_;
	private String codigoQuienPaga_PR_;
	private String codigoDeMoneda;
	private String fechaVencimientoUnico;
	private String metodoPagoDeCostesDeTransportes;
	private String condicionesDeEntrega;
	private String importeTotalNeto_79_;
	private String importeTotalDescuentos_Cargos_131_;
	private String importeBaseImponible_125_;
	private String importeTotalImpuestos_176_;
	private String importeAPagar_139_;
	private String importeTotalBruto_98_;
	private String estado;
	private String referenciaAdicional1_BYZZZ_;
	private String referenciaAdicional2_BYZZZ_;


	public java.util.List<ERE1T> ere1tList;
	public java.util.List<ERE1V> ere1vList;
	public java.util.List<ERE1D> ere1dList;
	public java.util.List<ERE1L> ere1lList;
	public java.util.List<ERE1G> ere1gList;
	public java.util.List<ERE1I> ere1iList;


	private static java.util.regex.Pattern PATTERN_ERE1C_cabecera = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_tipoDePedido_220_221_224_226_22E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_numeroDePedido = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_funcionDelMensaje_31_ = java.util.regex.Pattern.compile("^.{63}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_fechaDelDocumento_137__102_ = java.util.regex.Pattern.compile("^.{69}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_calificadorFecha1 = java.util.regex.Pattern.compile("^.{77}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_fechaDeServicio1 = java.util.regex.Pattern.compile("^.{80}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_horaDeServicio1 = java.util.regex.Pattern.compile("^.{88}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_calificadorFecha2 = java.util.regex.Pattern.compile("^.{92}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_fechaDeServicio2 = java.util.regex.Pattern.compile("^.{95}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_horaDeServicio2 = java.util.regex.Pattern.compile("^.{103}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_condicionesEspeciales = java.util.regex.Pattern.compile("^.{107}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_numeroDePedidoAbierto_BO_ = java.util.regex.Pattern.compile("^.{110}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_numeroDeListaDePrecios = java.util.regex.Pattern.compile("^.{127}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_numeroDePedidoProveedor_VN_ = java.util.regex.Pattern.compile("^.{144}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoComprador_BY_ = java.util.regex.Pattern.compile("^.{161}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoProveedor_SU_ = java.util.regex.Pattern.compile("^.{178}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoAQuienSeFactura_IV_ = java.util.regex.Pattern.compile("^.{195}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoPuntoDeEntrega_DP_ = java.util.regex.Pattern.compile("^.{212}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_identificacionAdicionalP_Entrega_API_ = java.util.regex.Pattern.compile("^.{229}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_numeroDeReposicion_ACD_ = java.util.regex.Pattern.compile("^.{246}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoPuntoDeExpedicion_PW_ = java.util.regex.Pattern.compile("^.{263}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoQuienPaga_PR_ = java.util.regex.Pattern.compile("^.{280}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_codigoDeMoneda = java.util.regex.Pattern.compile("^.{297}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_fechaVencimientoUnico = java.util.regex.Pattern.compile("^.{303}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_metodoPagoDeCostesDeTransportes = java.util.regex.Pattern.compile("^.{311}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_condicionesDeEntrega = java.util.regex.Pattern.compile("^.{317}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_importeTotalNeto_79_ = java.util.regex.Pattern.compile("^.{323}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_importeTotalDescuentos_Cargos_131_ = java.util.regex.Pattern.compile("^.{341}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_importeBaseImponible_125_ = java.util.regex.Pattern.compile("^.{359}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_importeTotalImpuestos_176_ = java.util.regex.Pattern.compile("^.{377}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_importeAPagar_139_ = java.util.regex.Pattern.compile("^.{395}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_importeTotalBruto_98_ = java.util.regex.Pattern.compile("^.{413}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_estado = java.util.regex.Pattern.compile("^.{431}(.{1}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_referenciaAdicional1_BYZZZ_ = java.util.regex.Pattern.compile("^.{432}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1C_referenciaAdicional2_BYZZZ_ = java.util.regex.Pattern.compile("^.{449}(.{17}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_ERE1C_cabecera.matcher(value)).find()) {
			setCabecera(m.group(1));
		}
		if((m = PATTERN_ERE1C_tipoDePedido_220_221_224_226_22E_.matcher(value)).find()) {
			setTipoDePedido_220_221_224_226_22E_(m.group(1));
		}
		if((m = PATTERN_ERE1C_numeroDePedido.matcher(value)).find()) {
			setNumeroDePedido(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_ERE1C_funcionDelMensaje_31_.matcher(value)).find()) {
			setFuncionDelMensaje_31_(m.group(1));
		}
		if((m = PATTERN_ERE1C_fechaDelDocumento_137__102_.matcher(value)).find()) {
			setFechaDelDocumento_137__102_(m.group(1));
		}
		if((m = PATTERN_ERE1C_calificadorFecha1.matcher(value)).find()) {
			setCalificadorFecha1(m.group(1));
		}
		if((m = PATTERN_ERE1C_fechaDeServicio1.matcher(value)).find()) {
			setFechaDeServicio1(m.group(1));
		}
		if((m = PATTERN_ERE1C_horaDeServicio1.matcher(value)).find()) {
			setHoraDeServicio1(m.group(1));
		}
		if((m = PATTERN_ERE1C_calificadorFecha2.matcher(value)).find()) {
			setCalificadorFecha2(m.group(1));
		}
		if((m = PATTERN_ERE1C_fechaDeServicio2.matcher(value)).find()) {
			setFechaDeServicio2(m.group(1));
		}
		if((m = PATTERN_ERE1C_horaDeServicio2.matcher(value)).find()) {
			setHoraDeServicio2(m.group(1));
		}
		if((m = PATTERN_ERE1C_condicionesEspeciales.matcher(value)).find()) {
			setCondicionesEspeciales(m.group(1));
		}
		if((m = PATTERN_ERE1C_numeroDePedidoAbierto_BO_.matcher(value)).find()) {
			setNumeroDePedidoAbierto_BO_(m.group(1));
		}
		if((m = PATTERN_ERE1C_numeroDeListaDePrecios.matcher(value)).find()) {
			setNumeroDeListaDePrecios(m.group(1));
		}
		if((m = PATTERN_ERE1C_numeroDePedidoProveedor_VN_.matcher(value)).find()) {
			setNumeroDePedidoProveedor_VN_(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoComprador_BY_.matcher(value)).find()) {
			setCodigoComprador_BY_(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoProveedor_SU_.matcher(value)).find()) {
			setCodigoProveedor_SU_(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoAQuienSeFactura_IV_.matcher(value)).find()) {
			setCodigoAQuienSeFactura_IV_(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoPuntoDeEntrega_DP_.matcher(value)).find()) {
			setCodigoPuntoDeEntrega_DP_(m.group(1));
		}
		if((m = PATTERN_ERE1C_identificacionAdicionalP_Entrega_API_.matcher(value)).find()) {
			setIdentificacionAdicionalP_Entrega_API_(m.group(1));
		}
		if((m = PATTERN_ERE1C_numeroDeReposicion_ACD_.matcher(value)).find()) {
			setNumeroDeReposicion_ACD_(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoPuntoDeExpedicion_PW_.matcher(value)).find()) {
			setCodigoPuntoDeExpedicion_PW_(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoQuienPaga_PR_.matcher(value)).find()) {
			setCodigoQuienPaga_PR_(m.group(1));
		}
		if((m = PATTERN_ERE1C_codigoDeMoneda.matcher(value)).find()) {
			setCodigoDeMoneda(m.group(1));
		}
		if((m = PATTERN_ERE1C_fechaVencimientoUnico.matcher(value)).find()) {
			setFechaVencimientoUnico(m.group(1));
		}
		if((m = PATTERN_ERE1C_metodoPagoDeCostesDeTransportes.matcher(value)).find()) {
			setMetodoPagoDeCostesDeTransportes(m.group(1));
		}
		if((m = PATTERN_ERE1C_condicionesDeEntrega.matcher(value)).find()) {
			setCondicionesDeEntrega(m.group(1));
		}
		if((m = PATTERN_ERE1C_importeTotalNeto_79_.matcher(value)).find()) {
			setImporteTotalNeto_79_(m.group(1));
		}
		if((m = PATTERN_ERE1C_importeTotalDescuentos_Cargos_131_.matcher(value)).find()) {
			setImporteTotalDescuentos_Cargos_131_(m.group(1));
		}
		if((m = PATTERN_ERE1C_importeBaseImponible_125_.matcher(value)).find()) {
			setImporteBaseImponible_125_(m.group(1));
		}
		if((m = PATTERN_ERE1C_importeTotalImpuestos_176_.matcher(value)).find()) {
			setImporteTotalImpuestos_176_(m.group(1));
		}
		if((m = PATTERN_ERE1C_importeAPagar_139_.matcher(value)).find()) {
			setImporteAPagar_139_(m.group(1));
		}
		if((m = PATTERN_ERE1C_importeTotalBruto_98_.matcher(value)).find()) {
			setImporteTotalBruto_98_(m.group(1));
		}
		if((m = PATTERN_ERE1C_estado.matcher(value)).find()) {
			setEstado(m.group(1));
		}
		if((m = PATTERN_ERE1C_referenciaAdicional1_BYZZZ_.matcher(value)).find()) {
			setReferenciaAdicional1_BYZZZ_(m.group(1));
		}
		if((m = PATTERN_ERE1C_referenciaAdicional2_BYZZZ_.matcher(value)).find()) {
			setReferenciaAdicional2_BYZZZ_(m.group(1));
		}
	}


	public String getCabecera() {
		return cabecera;
	}
	public void setCabecera(String cabecera) {
		this.cabecera = cabecera;
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
	public String getFuncionDelMensaje_31_() {
		return funcionDelMensaje_31_;
	}
	public void setFuncionDelMensaje_31_(String funcionDelMensaje_31_) {
		this.funcionDelMensaje_31_ = funcionDelMensaje_31_;
	}
	public String getFechaDelDocumento_137__102_() {
		return fechaDelDocumento_137__102_;
	}
	public void setFechaDelDocumento_137__102_(String fechaDelDocumento_137__102_) {
		this.fechaDelDocumento_137__102_ = fechaDelDocumento_137__102_;
	}
	public String getCalificadorFecha1() {
		return calificadorFecha1;
	}
	public void setCalificadorFecha1(String calificadorFecha1) {
		this.calificadorFecha1 = calificadorFecha1;
	}
	public String getFechaDeServicio1() {
		return fechaDeServicio1;
	}
	public void setFechaDeServicio1(String fechaDeServicio1) {
		this.fechaDeServicio1 = fechaDeServicio1;
	}
	public String getHoraDeServicio1() {
		return horaDeServicio1;
	}
	public void setHoraDeServicio1(String horaDeServicio1) {
		this.horaDeServicio1 = horaDeServicio1;
	}
	public String getCalificadorFecha2() {
		return calificadorFecha2;
	}
	public void setCalificadorFecha2(String calificadorFecha2) {
		this.calificadorFecha2 = calificadorFecha2;
	}
	public String getFechaDeServicio2() {
		return fechaDeServicio2;
	}
	public void setFechaDeServicio2(String fechaDeServicio2) {
		this.fechaDeServicio2 = fechaDeServicio2;
	}
	public String getHoraDeServicio2() {
		return horaDeServicio2;
	}
	public void setHoraDeServicio2(String horaDeServicio2) {
		this.horaDeServicio2 = horaDeServicio2;
	}
	public String getCondicionesEspeciales() {
		return condicionesEspeciales;
	}
	public void setCondicionesEspeciales(String condicionesEspeciales) {
		this.condicionesEspeciales = condicionesEspeciales;
	}
	public String getNumeroDePedidoAbierto_BO_() {
		return numeroDePedidoAbierto_BO_;
	}
	public void setNumeroDePedidoAbierto_BO_(String numeroDePedidoAbierto_BO_) {
		this.numeroDePedidoAbierto_BO_ = numeroDePedidoAbierto_BO_;
	}
	public String getNumeroDeListaDePrecios() {
		return numeroDeListaDePrecios;
	}
	public void setNumeroDeListaDePrecios(String numeroDeListaDePrecios) {
		this.numeroDeListaDePrecios = numeroDeListaDePrecios;
	}
	public String getNumeroDePedidoProveedor_VN_() {
		return numeroDePedidoProveedor_VN_;
	}
	public void setNumeroDePedidoProveedor_VN_(String numeroDePedidoProveedor_VN_) {
		this.numeroDePedidoProveedor_VN_ = numeroDePedidoProveedor_VN_;
	}
	public String getCodigoComprador_BY_() {
		return codigoComprador_BY_;
	}
	public void setCodigoComprador_BY_(String codigoComprador_BY_) {
		this.codigoComprador_BY_ = codigoComprador_BY_;
	}
	public String getCodigoProveedor_SU_() {
		return codigoProveedor_SU_;
	}
	public void setCodigoProveedor_SU_(String codigoProveedor_SU_) {
		this.codigoProveedor_SU_ = codigoProveedor_SU_;
	}
	public String getCodigoAQuienSeFactura_IV_() {
		return codigoAQuienSeFactura_IV_;
	}
	public void setCodigoAQuienSeFactura_IV_(String codigoAQuienSeFactura_IV_) {
		this.codigoAQuienSeFactura_IV_ = codigoAQuienSeFactura_IV_;
	}
	public String getCodigoPuntoDeEntrega_DP_() {
		return codigoPuntoDeEntrega_DP_;
	}
	public void setCodigoPuntoDeEntrega_DP_(String codigoPuntoDeEntrega_DP_) {
		this.codigoPuntoDeEntrega_DP_ = codigoPuntoDeEntrega_DP_;
	}
	public String getIdentificacionAdicionalP_Entrega_API_() {
		return identificacionAdicionalP_Entrega_API_;
	}
	public void setIdentificacionAdicionalP_Entrega_API_(String identificacionAdicionalP_Entrega_API_) {
		this.identificacionAdicionalP_Entrega_API_ = identificacionAdicionalP_Entrega_API_;
	}
	public String getNumeroDeReposicion_ACD_() {
		return numeroDeReposicion_ACD_;
	}
	public void setNumeroDeReposicion_ACD_(String numeroDeReposicion_ACD_) {
		this.numeroDeReposicion_ACD_ = numeroDeReposicion_ACD_;
	}
	public String getCodigoPuntoDeExpedicion_PW_() {
		return codigoPuntoDeExpedicion_PW_;
	}
	public void setCodigoPuntoDeExpedicion_PW_(String codigoPuntoDeExpedicion_PW_) {
		this.codigoPuntoDeExpedicion_PW_ = codigoPuntoDeExpedicion_PW_;
	}
	public String getCodigoQuienPaga_PR_() {
		return codigoQuienPaga_PR_;
	}
	public void setCodigoQuienPaga_PR_(String codigoQuienPaga_PR_) {
		this.codigoQuienPaga_PR_ = codigoQuienPaga_PR_;
	}
	public String getCodigoDeMoneda() {
		return codigoDeMoneda;
	}
	public void setCodigoDeMoneda(String codigoDeMoneda) {
		this.codigoDeMoneda = codigoDeMoneda;
	}
	public String getFechaVencimientoUnico() {
		return fechaVencimientoUnico;
	}
	public void setFechaVencimientoUnico(String fechaVencimientoUnico) {
		this.fechaVencimientoUnico = fechaVencimientoUnico;
	}
	public String getMetodoPagoDeCostesDeTransportes() {
		return metodoPagoDeCostesDeTransportes;
	}
	public void setMetodoPagoDeCostesDeTransportes(String metodoPagoDeCostesDeTransportes) {
		this.metodoPagoDeCostesDeTransportes = metodoPagoDeCostesDeTransportes;
	}
	public String getCondicionesDeEntrega() {
		return condicionesDeEntrega;
	}
	public void setCondicionesDeEntrega(String condicionesDeEntrega) {
		this.condicionesDeEntrega = condicionesDeEntrega;
	}
	public String getImporteTotalNeto_79_() {
		return importeTotalNeto_79_;
	}
	public void setImporteTotalNeto_79_(String importeTotalNeto_79_) {
		this.importeTotalNeto_79_ = importeTotalNeto_79_;
	}
	public String getImporteTotalDescuentos_Cargos_131_() {
		return importeTotalDescuentos_Cargos_131_;
	}
	public void setImporteTotalDescuentos_Cargos_131_(String importeTotalDescuentos_Cargos_131_) {
		this.importeTotalDescuentos_Cargos_131_ = importeTotalDescuentos_Cargos_131_;
	}
	public String getImporteBaseImponible_125_() {
		return importeBaseImponible_125_;
	}
	public void setImporteBaseImponible_125_(String importeBaseImponible_125_) {
		this.importeBaseImponible_125_ = importeBaseImponible_125_;
	}
	public String getImporteTotalImpuestos_176_() {
		return importeTotalImpuestos_176_;
	}
	public void setImporteTotalImpuestos_176_(String importeTotalImpuestos_176_) {
		this.importeTotalImpuestos_176_ = importeTotalImpuestos_176_;
	}
	public String getImporteAPagar_139_() {
		return importeAPagar_139_;
	}
	public void setImporteAPagar_139_(String importeAPagar_139_) {
		this.importeAPagar_139_ = importeAPagar_139_;
	}
	public String getImporteTotalBruto_98_() {
		return importeTotalBruto_98_;
	}
	public void setImporteTotalBruto_98_(String importeTotalBruto_98_) {
		this.importeTotalBruto_98_ = importeTotalBruto_98_;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getReferenciaAdicional1_BYZZZ_() {
		return referenciaAdicional1_BYZZZ_;
	}
	public void setReferenciaAdicional1_BYZZZ_(String referenciaAdicional1_BYZZZ_) {
		this.referenciaAdicional1_BYZZZ_ = referenciaAdicional1_BYZZZ_;
	}
	public String getReferenciaAdicional2_BYZZZ_() {
		return referenciaAdicional2_BYZZZ_;
	}
	public void setReferenciaAdicional2_BYZZZ_(String referenciaAdicional2_BYZZZ_) {
		this.referenciaAdicional2_BYZZZ_ = referenciaAdicional2_BYZZZ_;
	}

}