package com.esferalia.aon.file.seres.udapa.sales.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1C entity.
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
 * 		<td>ERE1C</th>
 * 		<td>Cabecera</th>
 * 		<td>Obligatorio</th>
 * 		<td>1</th>
 * 	</tr>
 * </table>
 */ 

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
	 * 		<td>ERE1C</th>
	 * 		<td>Cabecera</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCabecera() {
		return cabecera;
	}
	public void setCabecera(String cabecera) {
		this.cabecera = cabecera;
	}

	/** 
	 * C1001T - Tipo de Pedido (220, 221, 224, 226, 22E): Existe un código para identificar cada tipo de pedido que queramos enviar.El campo corresponde a un código EDI. Los valores posibles:
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
	 * C1004P - Número de Pedido: Se cumplimentará con el número de pedido correspondiente
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
	 * C3039E - Código Emisor  (MS): Departamento que emite el pedido (función general asociada FGEN).
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
	 * C3039R - Código Receptor (MR): Código interno del Cliente que recibe el pedido (función asociada DSPI).
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
	 * C1225F - Función del Mensaje (31): El campo corresponde a un código EDI. Los valores posibles:
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
	 * 		<td>C1225F</th>
	 * 		<td>Función del Mensaje (31)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>64</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFuncionDelMensaje_31_() {
		return funcionDelMensaje_31_;
	}
	public void setFuncionDelMensaje_31_(String funcionDelMensaje_31_) {
		this.funcionDelMensaje_31_ = funcionDelMensaje_31_;
	}

	/** 
	 * C2380D - Fecha del Documento (137) (102): Fecha de generación del Documento en formato AAAAMMDD
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
	 * 		<td>C2380D</th>
	 * 		<td>Fecha del Documento (137) (102)</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>70</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDelDocumento_137__102_() {
		return fechaDelDocumento_137__102_;
	}
	public void setFechaDelDocumento_137__102_(String fechaDelDocumento_137__102_) {
		this.fechaDelDocumento_137__102_ = fechaDelDocumento_137__102_;
	}

	/** 
	 * C20051 / C20052 - Calificador de las Fechas de Entrega: El campo corresponde a un código EDI. Los valores posibles son:
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
	 * 		<td>C20051</th>
	 * 		<td>Calificador Fecha 1</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>78</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorFecha1() {
		return calificadorFecha1;
	}
	public void setCalificadorFecha1(String calificadorFecha1) {
		this.calificadorFecha1 = calificadorFecha1;
	}

	/** 
	 * C2380F/C2380G - Fechas de Servicio en formato AAAAMMDD
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
	 * 		<td>C2380F</th>
	 * 		<td>Fecha de Servicio 1</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>81</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeServicio1() {
		return fechaDeServicio1;
	}
	public void setFechaDeServicio1(String fechaDeServicio1) {
		this.fechaDeServicio1 = fechaDeServicio1;
	}

	/** 
	 * C2380H/C2380I - Hora de Servicio en formato HHMM
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
	 * 		<td>C2380H</th>
	 * 		<td>Hora de Servicio 1</th>
	 * 		<td>N</th>
	 * 		<td>4</th>
	 * 		<td>89</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getHoraDeServicio1() {
		return horaDeServicio1;
	}
	public void setHoraDeServicio1(String horaDeServicio1) {
		this.horaDeServicio1 = horaDeServicio1;
	}

	/** 
	 * C20051 / C20052 - Calificador de las Fechas de Entrega: El campo corresponde a un código EDI. Los valores posibles son:
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
	 * 		<td>C20052</th>
	 * 		<td>Calificador Fecha 2</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>93</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorFecha2() {
		return calificadorFecha2;
	}
	public void setCalificadorFecha2(String calificadorFecha2) {
		this.calificadorFecha2 = calificadorFecha2;
	}

	/** 
	 * C2380F/C2380G - Fechas de Servicio en formato AAAAMMDD
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
	 * 		<td>C2380G</th>
	 * 		<td>Fecha de Servicio 2</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>96</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeServicio2() {
		return fechaDeServicio2;
	}
	public void setFechaDeServicio2(String fechaDeServicio2) {
		this.fechaDeServicio2 = fechaDeServicio2;
	}

	/** 
	 * C2380H/C2380I - Hora de Servicio en formato HHMM
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
	 * 		<td>C2380I</th>
	 * 		<td>Hora de Servicio 2</th>
	 * 		<td>N</th>
	 * 		<td>4</th>
	 * 		<td>104</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getHoraDeServicio2() {
		return horaDeServicio2;
	}
	public void setHoraDeServicio2(String horaDeServicio2) {
		this.horaDeServicio2 = horaDeServicio2;
	}

	/** 
	 * C4183E - Condiciones Especiales: Campo opcional. Se utiliza para especificar información adicional relacionada con el pedido. Los valores posibles:
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
	 * 		<td>C4183E</th>
	 * 		<td>Condiciones Especiales</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>108</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCondicionesEspeciales() {
		return condicionesEspeciales;
	}
	public void setCondicionesEspeciales(String condicionesEspeciales) {
		this.condicionesEspeciales = condicionesEspeciales;
	}

	/** 
	 * C1154A - Número de Pedido Abierto (BO):En caso de estar montando una cancelación, hace referencia al número de Pedido Abierto enviado antes.
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
	 * 		<td>C1154A</th>
	 * 		<td>Número de Pedido Abierto (BO)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>111</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDePedidoAbierto_BO_() {
		return numeroDePedidoAbierto_BO_;
	}
	public void setNumeroDePedidoAbierto_BO_(String numeroDePedidoAbierto_BO_) {
		this.numeroDePedidoAbierto_BO_ = numeroDePedidoAbierto_BO_;
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
	 * 		<td>C1154L</th>
	 * 		<td>Número de Lista de Precios</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>128</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeListaDePrecios() {
		return numeroDeListaDePrecios;
	}
	public void setNumeroDeListaDePrecios(String numeroDeListaDePrecios) {
		this.numeroDeListaDePrecios = numeroDeListaDePrecios;
	}

	/** 
	 * C1154P - Código Proveedor (SU): Departamento al que se pide la mercancia (función asociada DSPI).
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
	 * 		<td>C1154P</th>
	 * 		<td>Número de Pedido Proveedor (VN)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>145</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDePedidoProveedor_VN_() {
		return numeroDePedidoProveedor_VN_;
	}
	public void setNumeroDePedidoProveedor_VN_(String numeroDePedidoProveedor_VN_) {
		this.numeroDePedidoProveedor_VN_ = numeroDePedidoProveedor_VN_;
	}

	/** 
	 * C3039C - Código Comprador  (BY): Punto Operacional EDI del Cliente que hace el Pedido (función asociada QPID).
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
	 * 		<td>C3039C</th>
	 * 		<td>Código Comprador  (BY)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>162</th>
	 * 		<td>C</th>
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
	 * 		<td>C3039P</th>
	 * 		<td>Código Proveedor (SU)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>179</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoProveedor_SU_() {
		return codigoProveedor_SU_;
	}
	public void setCodigoProveedor_SU_(String codigoProveedor_SU_) {
		this.codigoProveedor_SU_ = codigoProveedor_SU_;
	}

	/** 
	 * C3039F - Código A quien se Factura (IV): Departamento al que se factura (función asociada AQSF).
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
	 * 		<td>C3039F</th>
	 * 		<td>Código A quien se Factura (IV)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>196</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoAQuienSeFactura_IV_() {
		return codigoAQuienSeFactura_IV_;
	}
	public void setCodigoAQuienSeFactura_IV_(String codigoAQuienSeFactura_IV_) {
		this.codigoAQuienSeFactura_IV_ = codigoAQuienSeFactura_IV_;
	}

	/** 
	 * C3039Q - Código Punto de Entrega (DP): Código interno del Cliente al que se entrega la mercancía (función asociada QREC).
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
	 * 		<td>C3039Q</th>
	 * 		<td>Código Punto de Entrega (DP)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>213</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoPuntoDeEntrega_DP_() {
		return codigoPuntoDeEntrega_DP_;
	}
	public void setCodigoPuntoDeEntrega_DP_(String codigoPuntoDeEntrega_DP_) {
		this.codigoPuntoDeEntrega_DP_ = codigoPuntoDeEntrega_DP_;
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
	 * 		<td>C1154I</th>
	 * 		<td>Identificación Adicional  P. Entrega (API)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>230</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getIdentificacionAdicionalP_Entrega_API_() {
		return identificacionAdicionalP_Entrega_API_;
	}
	public void setIdentificacionAdicionalP_Entrega_API_(String identificacionAdicionalP_Entrega_API_) {
		this.identificacionAdicionalP_Entrega_API_ = identificacionAdicionalP_Entrega_API_;
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
	 * 		<td>C1154R</th>
	 * 		<td>Número de Reposición (ACD)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>247</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeReposicion_ACD_() {
		return numeroDeReposicion_ACD_;
	}
	public void setNumeroDeReposicion_ACD_(String numeroDeReposicion_ACD_) {
		this.numeroDeReposicion_ACD_ = numeroDeReposicion_ACD_;
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
	 * 		<td>C3039M</th>
	 * 		<td>Código Punto de Expedición (PW)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>264</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoPuntoDeExpedicion_PW_() {
		return codigoPuntoDeExpedicion_PW_;
	}
	public void setCodigoPuntoDeExpedicion_PW_(String codigoPuntoDeExpedicion_PW_) {
		this.codigoPuntoDeExpedicion_PW_ = codigoPuntoDeExpedicion_PW_;
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
	 * 		<td>C3039A</th>
	 * 		<td>Código quien Paga (PR)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>281</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoQuienPaga_PR_() {
		return codigoQuienPaga_PR_;
	}
	public void setCodigoQuienPaga_PR_(String codigoQuienPaga_PR_) {
		this.codigoQuienPaga_PR_ = codigoQuienPaga_PR_;
	}

	/** 
	 * C6345M - Código de Moneda: Los valores posibles:
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
	 * 		<td>C6345M</th>
	 * 		<td>Código de Moneda</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>298</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoDeMoneda() {
		return codigoDeMoneda;
	}
	public void setCodigoDeMoneda(String codigoDeMoneda) {
		this.codigoDeMoneda = codigoDeMoneda;
	}

	/** 
	 * C2380V - Fecha vencimiento único: Está pensado para que el emisor del pedido pueda solicitar una fecha de pago. Se indicará si la factura es de Pago único, en caso contrario se dejará a cero y los vencimientos se especificaran en el fichero ERE1V. Debe montarse en formato AAAAMMDD.
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
	 * 		<td>C2380V</th>
	 * 		<td>Fecha vencimiento único</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>304</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaVencimientoUnico() {
		return fechaVencimientoUnico;
	}
	public void setFechaVencimientoUnico(String fechaVencimientoUnico) {
		this.fechaVencimientoUnico = fechaVencimientoUnico;
	}

	/** 
	 * C4215P - Método Pago de costes de transportes: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>C4215P</th>
	 * 		<td>Método Pago de costes de transportes</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>312</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getMetodoPagoDeCostesDeTransportes() {
		return metodoPagoDeCostesDeTransportes;
	}
	public void setMetodoPagoDeCostesDeTransportes(String metodoPagoDeCostesDeTransportes) {
		this.metodoPagoDeCostesDeTransportes = metodoPagoDeCostesDeTransportes;
	}

	/** 
	 * C4053E - Condiciones de Entrega: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>C4053E</th>
	 * 		<td>Condiciones de Entrega</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>318</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCondicionesDeEntrega() {
		return condicionesDeEntrega;
	}
	public void setCondicionesDeEntrega(String condicionesDeEntrega) {
		this.condicionesDeEntrega = condicionesDeEntrega;
	}

	/** 
	 * C5004N - Importe Total Neto (79): Corresponde al sumatorio de los importes netos por línea si se valora el pedido.
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
	 * 		<td>C5004N</th>
	 * 		<td>Importe Total Neto (79)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>324</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTotalNeto_79_() {
		return importeTotalNeto_79_;
	}
	public void setImporteTotalNeto_79_(String importeTotalNeto_79_) {
		this.importeTotalNeto_79_ = importeTotalNeto_79_;
	}

	/** 
	 * C5004D - Importe Total Descuentos/Cargos (131): Suma de todos los descuentos y cargos globales del pedido, excluyendo los de líneas (en caso de pedidos valorados).
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
	 * 		<td>C5004D</th>
	 * 		<td>Importe Total Descuentos/Cargos (131)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>342</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTotalDescuentos_Cargos_131_() {
		return importeTotalDescuentos_Cargos_131_;
	}
	public void setImporteTotalDescuentos_Cargos_131_(String importeTotalDescuentos_Cargos_131_) {
		this.importeTotalDescuentos_Cargos_131_ = importeTotalDescuentos_Cargos_131_;
	}

	/** 
	 * C5004B - Importe Base Imponible (125):Importe Neto Total  de Factura (C500N) + Total cargos y descuentos Gobales (C5004D)
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
	 * 		<td>C5004B</th>
	 * 		<td>Importe Base Imponible (125)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>360</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteBaseImponible_125_() {
		return importeBaseImponible_125_;
	}
	public void setImporteBaseImponible_125_(String importeBaseImponible_125_) {
		this.importeBaseImponible_125_ = importeBaseImponible_125_;
	}

	/** 
	 * C5004I - Importe Total Impuestos (176): Sumatorio de los importes  de impuestos por línea.
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
	 * 		<td>C5004I</th>
	 * 		<td>Importe Total Impuestos (176)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>378</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTotalImpuestos_176_() {
		return importeTotalImpuestos_176_;
	}
	public void setImporteTotalImpuestos_176_(String importeTotalImpuestos_176_) {
		this.importeTotalImpuestos_176_ = importeTotalImpuestos_176_;
	}

	/** 
	 * C5004P - Importe a Pagar (139): Base Imponible + Importe Total de Impuestos
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
	 * 		<td>C5004P</th>
	 * 		<td>Importe a Pagar (139)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>396</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteAPagar_139_() {
		return importeAPagar_139_;
	}
	public void setImporteAPagar_139_(String importeAPagar_139_) {
		this.importeAPagar_139_ = importeAPagar_139_;
	}

	/** 
	 * C5004U - Importe Total Bruto (98): Sumatorio de los importes Brutos de las líneas (cantidad facturada x precio unitario Bruto).No se tienen en cuenta Cargos ni Descuentos(ni a nivel de líneas, ni globales).
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
	 * 		<td>C5004U</th>
	 * 		<td>Importe Total Bruto (98)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>414</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTotalBruto_98_() {
		return importeTotalBruto_98_;
	}
	public void setImporteTotalBruto_98_(String importeTotalBruto_98_) {
		this.importeTotalBruto_98_ = importeTotalBruto_98_;
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
	 * 		<td>CESTAD</th>
	 * 		<td>Estado</th>
	 * 		<td>C</th>
	 * 		<td>1</th>
	 * 		<td>432</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
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
	 * 		<td>C1154B</th>
	 * 		<td>Referencia Adicional  1 (BY ZZZ)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>433</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getReferenciaAdicional1_BYZZZ_() {
		return referenciaAdicional1_BYZZZ_;
	}
	public void setReferenciaAdicional1_BYZZZ_(String referenciaAdicional1_BYZZZ_) {
		this.referenciaAdicional1_BYZZZ_ = referenciaAdicional1_BYZZZ_;
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
	 * 		<td>C1154C</th>
	 * 		<td>Referencia Adicional  2 (BY ZZZ)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>450</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getReferenciaAdicional2_BYZZZ_() {
		return referenciaAdicional2_BYZZZ_;
	}
	public void setReferenciaAdicional2_BYZZZ_(String referenciaAdicional2_BYZZZ_) {
		this.referenciaAdicional2_BYZZZ_ = referenciaAdicional2_BYZZZ_;
	}

	public enum TipoDePedido_220_221_224_226_22E_ {
		PEDID_220("220"),
		PEDIDO_ABIERT_221("221"),
		PEDIDO_URGENT_224("224"),
		CANCELACION_DE_PEDID_226("226"),
		AUTOPEDIDO__ES_EMITIDO_POR_EL_FABRICANTE_PARA_AVISAR_DE_LA_MERCANCIA_QUE_VA_A_SERVIR__22E("22E"),
		;
		
		private String value;
		
		private TipoDePedido_220_221_224_226_22E_(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum FuncionDelMensaje_31_ {
		CONFIRMATIO_6("6"),
		DUPLICAT_7("7"),
		ORIGINA_9("9"),
		PROPOSA_16("16"),
		COP_31("31"),
		CONFIRMATION_VIA_SPECIFIC_MEAN_42("42"),
		PROVISIONA_46("46"),
		;
		
		private String value;
		
		private FuncionDelMensaje_31_(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadorDeLasFechasDeEntrega {
		DELIVERY_DATE_TIME__REQUESTE_2("2"),
		SHIPMENT_DATE_TIME__REQUESTE_10("10"),
		DESPATCH_DATE_AND_OR_TIM_11("11"),
		PROMOTION_START_DATE_TIME___NOTE_15("15"),
		SHIP_NOT_BEFORE_DATE_TIM_37("37"),
		SHIP_NOT_LATER_THAN_DATE_TIM_38("38"),
		CANCEL_IF_NOT_DELIVERED_BY_THIS_DAT_61("61"),
		DELIVERY_DATE_TIME__LATEST____FECHA_HORA_LIMITE_DE_ENTREGA_63("63"),
		DELIVERY_DATE_TIME__EARLIEST____ENTREGAR_A_PARTIR_DE_FECHA_HORA_64("64"),
		DELIVERY_DATE_TIME__PROMISED_FOR___FECHA_HORA_DE_ENTREGA_FIJA_69("69"),
		REQUESTED_FOR_DELIVERY_WEEK_COMMENCING__EAN_CODE_77("77"),
		DOCUMENT_MESSAGE_DATE_TIM_137("137"),
		PICK_UP_COLLECTION_DATE_TIME_OF_CARG_200("200"),
		INVOICING_PERIO_263("263"),
		CONFIRMATION_DATE_LEAD_TIM_282("282"),
		CANCEL_IF_NOT_SHIPPED_DESPATCHED_BY_THIS_DATE__EAN_CODE_43E("43E"),
		;
		
		private String value;
		
		private CalificadorDeLasFechasDeEntrega(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CondicionesEspeciales {
		BUYING_GROUP_CONDITIONS__EAN_CODE_71E("71E"),
		CANCEL_ORDER_UNLESS_COMPLETE_DELIVERY_POSSIBLE_ON_REQUESTED_DATE_TIME__EAN_CODE_72E("72E"),
		DELIVERY_SUBJECT_TO_FINAL_AUTHORIZATION__EAN_CODE_73E("73E"),
		INVOICED_BUT_DO_NOT_REPLENISH__EAN_CODE_81E("81E"),
		REPLENISHED_BUT_DO_NOT_INVOICE__EAN_CODE_82E("82E"),
		DELIVER_FULL_ORDER__EAN_CODE_83E("83E"),
		;
		
		private String value;
		
		private CondicionesEspeciales(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CodigoDeMoneda {
		PESETA_ESPA_OL_ESP("ESP"),
		EURO_EUROPE_EUR("EUR"),
		ESCUDO_PORTUGUE_PTE("PTE"),
		;
		
		private String value;
		
		private CodigoDeMoneda(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum MetodoPagoDeCostesDeTransportes {
		COLLEC_CC("CC"),
		MIXE_MX("MX"),
		PREPAID_BUT_CHARGED_TO_CUSTOME_PC("PC"),
		PREPAID_ONL_PO("PO"),
		PORTES_PAGADO_PP("PP"),
		PORTES_DEBIDO_PD("PD"),
		;
		
		private String value;
		
		private MetodoPagoDeCostesDeTransportes(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CondicionesDeEntrega {
		RECOGIDA_POR_EL_EMISOR_DEL_PEDID_PD("PD"),
		ENVIADA_POR_EL_RECEPTOR_DEL_PEDID_EP("EP"),
		;
		
		private String value;
		
		private CondicionesDeEntrega(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}