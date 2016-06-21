package com.esferalia.aon.file.seres.udapa.invoice.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCC entity.
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
 * 		<td>SINCC</th>
 * 		<td>Cabecera</th>
 * 		<td>Obligatorio</th>
 * 		<td>1</th>
 * 	</tr>
 * </table>
 */ 

public class SINCC {

	private String cabecera;
	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_aQuienSePide__SU_;
	private String codigoComprador_QuienPide__BY_;
	private String funcionDelMensaje_7_31_5_;
	private String fechaFactura;
	private String periodoDeFacturacion;
	private String formaDePago;
	private String codigoEmisorDeLaFactura_QuienFactura__II_;
	private String codigoReceptorDeLaFactura_aQuienSeFactura_;
	private String codigoReceptorDeLasMercancias_QuienRecibe_;
	private String codigoReceptorDelPago_aQuienSePaga_;
	private String codigoEmisorDelPago_QuienPaga_;
	private String razonDelCargoODelAbono;
	private String numeroDePedido_ON_;
	private String numeroDeAlbaran_DQ_;
	private String calificadorDocumentoRectificado_Sustituido;
	private String numeroDocumentoRectificado_Sustituido;
	private String numeroDeContrato_Acuerdo_CT_;
	private String numeroDeRelacionDeEntregas_REN_;
	private String razonSocialReceptorDeLaFactura;
	private String nombre_NumeroDeLaCalleDelReceptorDeLaFactura;
	private String poblacionDelReceptorDeLaFactura;
	private String codigoPostalDelReceptorDeLaFactura;
	private String nIFDelReceptorDeLaFactura;
	private String nombre_NumeroDeLaCalleDelEmisorDeLaFactura;
	private String poblacionDelEmisorDeLaFactura;
	private String codigoPostalDelEmisorDeLaFactura;
	private String codigoDeMoneda;
	private String fechaVencimientoUnico;
	private String importeNetoTotalFactura_79_;
	private String baseImponible_125_;
	private String importeBrutoTotalFactura_98_;
	private String importeTotalDeImpuestos_176_;
	private String importeTotalAPagar_139_;
	private String subvencionesVinculadasAlPrecio_80A_;
	private String totalIncrementosDelImporteBruto_259_;
	private String totalMinoracionesDelImporteBruto_260_;
	private String identificacionAdicionalDeLaParte_API_;
	private String receptorDelDocumento;
	private String identificacionAdicionalProveedor_API__NAD_SU_;


	public java.util.List<SINCT> sinctList;
	public java.util.List<SINCV> sincvList;
	public java.util.List<SINCD> sincdList;
	public java.util.List<SINCL> sinclList;
	public java.util.List<SINCU> sincuList;
	public java.util.List<SINCE> sinceList;
	public java.util.List<SINCI> sinciList;


	private static java.util.regex.Pattern PATTERN_SINCC_cabecera = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_tipoFactura_325_380_381_383_385_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_numeroDeFactura = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoVendedor_aQuienSePide__SU_ = java.util.regex.Pattern.compile("^.{29}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoComprador_QuienPide__BY_ = java.util.regex.Pattern.compile("^.{42}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_funcionDelMensaje_7_31_5_ = java.util.regex.Pattern.compile("^.{55}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_fechaFactura = java.util.regex.Pattern.compile("^.{61}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_periodoDeFacturacion = java.util.regex.Pattern.compile("^.{69}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_formaDePago = java.util.regex.Pattern.compile("^.{85}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoEmisorDeLaFactura_QuienFactura__II_ = java.util.regex.Pattern.compile("^.{91}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoReceptorDeLaFactura_aQuienSeFactura_ = java.util.regex.Pattern.compile("^.{104}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoReceptorDeLasMercancias_QuienRecibe_ = java.util.regex.Pattern.compile("^.{117}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoReceptorDelPago_aQuienSePaga_ = java.util.regex.Pattern.compile("^.{130}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoEmisorDelPago_QuienPaga_ = java.util.regex.Pattern.compile("^.{143}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_razonDelCargoODelAbono = java.util.regex.Pattern.compile("^.{156}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_numeroDePedido_ON_ = java.util.regex.Pattern.compile("^.{162}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_numeroDeAlbaran_DQ_ = java.util.regex.Pattern.compile("^.{179}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_calificadorDocumentoRectificado_Sustituido = java.util.regex.Pattern.compile("^.{196}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_numeroDocumentoRectificado_Sustituido = java.util.regex.Pattern.compile("^.{199}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_numeroDeContrato_Acuerdo_CT_ = java.util.regex.Pattern.compile("^.{216}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_numeroDeRelacionDeEntregas_REN_ = java.util.regex.Pattern.compile("^.{233}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_razonSocialReceptorDeLaFactura = java.util.regex.Pattern.compile("^.{250}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_nombre_NumeroDeLaCalleDelReceptorDeLaFactura = java.util.regex.Pattern.compile("^.{320}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_poblacionDelReceptorDeLaFactura = java.util.regex.Pattern.compile("^.{390}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoPostalDelReceptorDeLaFactura = java.util.regex.Pattern.compile("^.{425}(.{9}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_nIFDelReceptorDeLaFactura = java.util.regex.Pattern.compile("^.{434}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_nombre_NumeroDeLaCalleDelEmisorDeLaFactura = java.util.regex.Pattern.compile("^.{451}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_poblacionDelEmisorDeLaFactura = java.util.regex.Pattern.compile("^.{521}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoPostalDelEmisorDeLaFactura = java.util.regex.Pattern.compile("^.{556}(.{9}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_codigoDeMoneda = java.util.regex.Pattern.compile("^.{565}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_fechaVencimientoUnico = java.util.regex.Pattern.compile("^.{571}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_importeNetoTotalFactura_79_ = java.util.regex.Pattern.compile("^.{579}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_baseImponible_125_ = java.util.regex.Pattern.compile("^.{597}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_importeBrutoTotalFactura_98_ = java.util.regex.Pattern.compile("^.{615}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_importeTotalDeImpuestos_176_ = java.util.regex.Pattern.compile("^.{633}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_importeTotalAPagar_139_ = java.util.regex.Pattern.compile("^.{651}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_subvencionesVinculadasAlPrecio_80A_ = java.util.regex.Pattern.compile("^.{669}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_totalIncrementosDelImporteBruto_259_ = java.util.regex.Pattern.compile("^.{687}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_totalMinoracionesDelImporteBruto_260_ = java.util.regex.Pattern.compile("^.{705}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_identificacionAdicionalDeLaParte_API_ = java.util.regex.Pattern.compile("^.{723}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_receptorDelDocumento = java.util.regex.Pattern.compile("^.{740}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCC_identificacionAdicionalProveedor_API__NAD_SU_ = java.util.regex.Pattern.compile("^.{753}(.{17}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SINCC_cabecera.matcher(value)).find()) {
			setCabecera(m.group(1));
		}
		if((m = PATTERN_SINCC_tipoFactura_325_380_381_383_385_.matcher(value)).find()) {
			setTipoFactura_325_380_381_383_385_(m.group(1));
		}
		if((m = PATTERN_SINCC_numeroDeFactura.matcher(value)).find()) {
			setNumeroDeFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoVendedor_aQuienSePide__SU_.matcher(value)).find()) {
			setCodigoVendedor_aQuienSePide__SU_(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoComprador_QuienPide__BY_.matcher(value)).find()) {
			setCodigoComprador_QuienPide__BY_(m.group(1));
		}
		if((m = PATTERN_SINCC_funcionDelMensaje_7_31_5_.matcher(value)).find()) {
			setFuncionDelMensaje_7_31_5_(m.group(1));
		}
		if((m = PATTERN_SINCC_fechaFactura.matcher(value)).find()) {
			setFechaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_periodoDeFacturacion.matcher(value)).find()) {
			setPeriodoDeFacturacion(m.group(1));
		}
		if((m = PATTERN_SINCC_formaDePago.matcher(value)).find()) {
			setFormaDePago(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoEmisorDeLaFactura_QuienFactura__II_.matcher(value)).find()) {
			setCodigoEmisorDeLaFactura_QuienFactura__II_(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoReceptorDeLaFactura_aQuienSeFactura_.matcher(value)).find()) {
			setCodigoReceptorDeLaFactura_aQuienSeFactura_(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoReceptorDeLasMercancias_QuienRecibe_.matcher(value)).find()) {
			setCodigoReceptorDeLasMercancias_QuienRecibe_(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoReceptorDelPago_aQuienSePaga_.matcher(value)).find()) {
			setCodigoReceptorDelPago_aQuienSePaga_(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoEmisorDelPago_QuienPaga_.matcher(value)).find()) {
			setCodigoEmisorDelPago_QuienPaga_(m.group(1));
		}
		if((m = PATTERN_SINCC_razonDelCargoODelAbono.matcher(value)).find()) {
			setRazonDelCargoODelAbono(m.group(1));
		}
		if((m = PATTERN_SINCC_numeroDePedido_ON_.matcher(value)).find()) {
			setNumeroDePedido_ON_(m.group(1));
		}
		if((m = PATTERN_SINCC_numeroDeAlbaran_DQ_.matcher(value)).find()) {
			setNumeroDeAlbaran_DQ_(m.group(1));
		}
		if((m = PATTERN_SINCC_calificadorDocumentoRectificado_Sustituido.matcher(value)).find()) {
			setCalificadorDocumentoRectificado_Sustituido(m.group(1));
		}
		if((m = PATTERN_SINCC_numeroDocumentoRectificado_Sustituido.matcher(value)).find()) {
			setNumeroDocumentoRectificado_Sustituido(m.group(1));
		}
		if((m = PATTERN_SINCC_numeroDeContrato_Acuerdo_CT_.matcher(value)).find()) {
			setNumeroDeContrato_Acuerdo_CT_(m.group(1));
		}
		if((m = PATTERN_SINCC_numeroDeRelacionDeEntregas_REN_.matcher(value)).find()) {
			setNumeroDeRelacionDeEntregas_REN_(m.group(1));
		}
		if((m = PATTERN_SINCC_razonSocialReceptorDeLaFactura.matcher(value)).find()) {
			setRazonSocialReceptorDeLaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_nombre_NumeroDeLaCalleDelReceptorDeLaFactura.matcher(value)).find()) {
			setNombre_NumeroDeLaCalleDelReceptorDeLaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_poblacionDelReceptorDeLaFactura.matcher(value)).find()) {
			setPoblacionDelReceptorDeLaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoPostalDelReceptorDeLaFactura.matcher(value)).find()) {
			setCodigoPostalDelReceptorDeLaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_nIFDelReceptorDeLaFactura.matcher(value)).find()) {
			setNIFDelReceptorDeLaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_nombre_NumeroDeLaCalleDelEmisorDeLaFactura.matcher(value)).find()) {
			setNombre_NumeroDeLaCalleDelEmisorDeLaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_poblacionDelEmisorDeLaFactura.matcher(value)).find()) {
			setPoblacionDelEmisorDeLaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoPostalDelEmisorDeLaFactura.matcher(value)).find()) {
			setCodigoPostalDelEmisorDeLaFactura(m.group(1));
		}
		if((m = PATTERN_SINCC_codigoDeMoneda.matcher(value)).find()) {
			setCodigoDeMoneda(m.group(1));
		}
		if((m = PATTERN_SINCC_fechaVencimientoUnico.matcher(value)).find()) {
			setFechaVencimientoUnico(m.group(1));
		}
		if((m = PATTERN_SINCC_importeNetoTotalFactura_79_.matcher(value)).find()) {
			setImporteNetoTotalFactura_79_(m.group(1));
		}
		if((m = PATTERN_SINCC_baseImponible_125_.matcher(value)).find()) {
			setBaseImponible_125_(m.group(1));
		}
		if((m = PATTERN_SINCC_importeBrutoTotalFactura_98_.matcher(value)).find()) {
			setImporteBrutoTotalFactura_98_(m.group(1));
		}
		if((m = PATTERN_SINCC_importeTotalDeImpuestos_176_.matcher(value)).find()) {
			setImporteTotalDeImpuestos_176_(m.group(1));
		}
		if((m = PATTERN_SINCC_importeTotalAPagar_139_.matcher(value)).find()) {
			setImporteTotalAPagar_139_(m.group(1));
		}
		if((m = PATTERN_SINCC_subvencionesVinculadasAlPrecio_80A_.matcher(value)).find()) {
			setSubvencionesVinculadasAlPrecio_80A_(m.group(1));
		}
		if((m = PATTERN_SINCC_totalIncrementosDelImporteBruto_259_.matcher(value)).find()) {
			setTotalIncrementosDelImporteBruto_259_(m.group(1));
		}
		if((m = PATTERN_SINCC_totalMinoracionesDelImporteBruto_260_.matcher(value)).find()) {
			setTotalMinoracionesDelImporteBruto_260_(m.group(1));
		}
		if((m = PATTERN_SINCC_identificacionAdicionalDeLaParte_API_.matcher(value)).find()) {
			setIdentificacionAdicionalDeLaParte_API_(m.group(1));
		}
		if((m = PATTERN_SINCC_receptorDelDocumento.matcher(value)).find()) {
			setReceptorDelDocumento(m.group(1));
		}
		if((m = PATTERN_SINCC_identificacionAdicionalProveedor_API__NAD_SU_.matcher(value)).find()) {
			setIdentificacionAdicionalProveedor_API__NAD_SU_(m.group(1));
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
	 * 		<td>SINCC</th>
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
	 * F1001T - Tipo Factura: Existe un código para identificar cada tipo de factura que queramos enviar. Las más habituales son las Facturas Comerciales(tipo 380) y los Abonos (tipo 381). El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>F1001T</th>
	 * 		<td>Tipo Factura (325, 380, 381, 383, 385)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoFactura_325_380_381_383_385_() {
		return tipoFactura_325_380_381_383_385_;
	}
	public void setTipoFactura_325_380_381_383_385_(String tipoFactura_325_380_381_383_385_) {
		this.tipoFactura_325_380_381_383_385_ = tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * F1004N - Número de Factura: Se cumplimentará con el número de factura o abono correspondiente
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
	 * 		<td>F1004N</th>
	 * 		<td>Número de Factura</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeFactura() {
		return numeroDeFactura;
	}
	public void setNumeroDeFactura(String numeroDeFactura) {
		this.numeroDeFactura = numeroDeFactura;
	}

	/** 
	 * F3039V - Código Vendedor: Departamento al que se pide la mercancía.
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
	 * 		<td>F3039V</th>
	 * 		<td>Código Vendedor (a Quien se Pide) (SU)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>30</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoVendedor_aQuienSePide__SU_() {
		return codigoVendedor_aQuienSePide__SU_;
	}
	public void setCodigoVendedor_aQuienSePide__SU_(String codigoVendedor_aQuienSePide__SU_) {
		this.codigoVendedor_aQuienSePide__SU_ = codigoVendedor_aQuienSePide__SU_;
	}

	/** 
	 * F3039C - Código Comprador: Código EDI del Cliente que hace el Pedido
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
	 * 		<td>F3039C</th>
	 * 		<td>Código Comprador (Quien Pide) (BY)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>43</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoComprador_QuienPide__BY_() {
		return codigoComprador_QuienPide__BY_;
	}
	public void setCodigoComprador_QuienPide__BY_(String codigoComprador_QuienPide__BY_) {
		this.codigoComprador_QuienPide__BY_ = codigoComprador_QuienPide__BY_;
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
	 * 		<td>F1225F</th>
	 * 		<td>Función del Mensaje (7, 31, 5)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>56</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFuncionDelMensaje_7_31_5_() {
		return funcionDelMensaje_7_31_5_;
	}
	public void setFuncionDelMensaje_7_31_5_(String funcionDelMensaje_7_31_5_) {
		this.funcionDelMensaje_7_31_5_ = funcionDelMensaje_7_31_5_;
	}

	/** 
	 * F2380F - Fecha de Factura: Fecha de generación del Documento en formato AAAAMMDD
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
	 * 		<td>F2380F</th>
	 * 		<td>Fecha Factura</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>62</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaFactura() {
		return fechaFactura;
	}
	public void setFechaFactura(String fechaFactura) {
		this.fechaFactura = fechaFactura;
	}

	/** 
	 * F2380P - Periodo de Facturación: Periodo de la factura en formato AAAAMMDDAAAAMMDD
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
	 * 		<td>F2380P</th>
	 * 		<td>Periodo de Facturación</th>
	 * 		<td>C</th>
	 * 		<td>16</th>
	 * 		<td>70</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPeriodoDeFacturacion() {
		return periodoDeFacturacion;
	}
	public void setPeriodoDeFacturacion(String periodoDeFacturacion) {
		this.periodoDeFacturacion = periodoDeFacturacion;
	}

	/** 
	 * F4461P - Forma de Pago: Permite al interlocutor que emite la factura, especificar cómo debe realizarse el pago de la misma. Valores posibles:
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
	 * 		<td>F4461P</th>
	 * 		<td>Forma de Pago</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>86</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFormaDePago() {
		return formaDePago;
	}
	public void setFormaDePago(String formaDePago) {
		this.formaDePago = formaDePago;
	}

	/** 
	 * F3039E - Código Emisor de la Factura: Departamento que factura (puede no coincidir con el código de vendedor).
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
	 * 		<td>F3039E</th>
	 * 		<td>Código Emisor de la Factura (Quien Factura) (II)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>92</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoEmisorDeLaFactura_QuienFactura__II_() {
		return codigoEmisorDeLaFactura_QuienFactura__II_;
	}
	public void setCodigoEmisorDeLaFactura_QuienFactura__II_(String codigoEmisorDeLaFactura_QuienFactura__II_) {
		this.codigoEmisorDeLaFactura_QuienFactura__II_ = codigoEmisorDeLaFactura_QuienFactura__II_;
	}

	/** 
	 * F3039R - Código Receptor de la Factura: Código EDI  del Cliente al que se envía la  factura (puede no coincidir con el comprador)
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
	 * 		<td>F3039R</th>
	 * 		<td>Código Receptor de la Factura (a Quien se Factura)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>105</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoReceptorDeLaFactura_aQuienSeFactura_() {
		return codigoReceptorDeLaFactura_aQuienSeFactura_;
	}
	public void setCodigoReceptorDeLaFactura_aQuienSeFactura_(String codigoReceptorDeLaFactura_aQuienSeFactura_) {
		this.codigoReceptorDeLaFactura_aQuienSeFactura_ = codigoReceptorDeLaFactura_aQuienSeFactura_;
	}

	/** 
	 * F3039A - Código Receptor de la Mercancía: El campo corresponde a un código a un Punto Operacional EDI.
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
	 * 		<td>F3039A</th>
	 * 		<td>Código Receptor de las Mercancías (Quien Recibe)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>118</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoReceptorDeLasMercancias_QuienRecibe_() {
		return codigoReceptorDeLasMercancias_QuienRecibe_;
	}
	public void setCodigoReceptorDeLasMercancias_QuienRecibe_(String codigoReceptorDeLasMercancias_QuienRecibe_) {
		this.codigoReceptorDeLasMercancias_QuienRecibe_ = codigoReceptorDeLasMercancias_QuienRecibe_;
	}

	/** 
	 * F3039P - Código Receptor del Pago: Departamento a quien se paga. El campo corresponde a un Punto Operacional EDI.
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
	 * 		<td>F3039P</th>
	 * 		<td>Código Receptor del Pago (a Quien se Paga)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>131</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoReceptorDelPago_aQuienSePaga_() {
		return codigoReceptorDelPago_aQuienSePaga_;
	}
	public void setCodigoReceptorDelPago_aQuienSePaga_(String codigoReceptorDelPago_aQuienSePaga_) {
		this.codigoReceptorDelPago_aQuienSePaga_ = codigoReceptorDelPago_aQuienSePaga_;
	}

	/** 
	 * F3039Q - Código Emisor del Pago: Código interno del Cliente que paga la factura. El campo corresponde a un Punto Operacional EDI.
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
	 * 		<td>F3039Q</th>
	 * 		<td>Código Emisor del Pago (Quien Paga)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>144</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoEmisorDelPago_QuienPaga_() {
		return codigoEmisorDelPago_QuienPaga_;
	}
	public void setCodigoEmisorDelPago_QuienPaga_(String codigoEmisorDelPago_QuienPaga_) {
		this.codigoEmisorDelPago_QuienPaga_ = codigoEmisorDelPago_QuienPaga_;
	}

	/** 
	 * F4183R - Razón del Cargo o del Abono: Solo se enviará si el tipo de Factura es 381(abonos) o 383 (notas de cargo). El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>F4183R</th>
	 * 		<td>Razón del Cargo o del Abono</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>157</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getRazonDelCargoODelAbono() {
		return razonDelCargoODelAbono;
	}
	public void setRazonDelCargoODelAbono(String razonDelCargoODelAbono) {
		this.razonDelCargoODelAbono = razonDelCargoODelAbono;
	}

	/** 
	 * F1154P - Número de Pedido: Será obligatorio si el Tipo de Factura es 380 y no es una entrega directa en tienda.
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
	 * 		<td>F1154P</th>
	 * 		<td>Número de Pedido (ON)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>163</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDePedido_ON_() {
		return numeroDePedido_ON_;
	}
	public void setNumeroDePedido_ON_(String numeroDePedido_ON_) {
		this.numeroDePedido_ON_ = numeroDePedido_ON_;
	}

	/** 
	 * F1154A - Número de Albarán: Será obligatorio si el Tipo de Factura es 380 y la factura es de mercancías. O si la factura es 381 o 383 y la razón es por devolución de mercancías.
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
	 * 		<td>F1154A</th>
	 * 		<td>Número de Albarán (DQ)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>180</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeAlbaran_DQ_() {
		return numeroDeAlbaran_DQ_;
	}
	public void setNumeroDeAlbaran_DQ_(String numeroDeAlbaran_DQ_) {
		this.numeroDeAlbaran_DQ_ = numeroDeAlbaran_DQ_;
	}

	/** 
	 * F1153F - Calificador Documento Rectificado: Será obligatorio si el Tipo de Factura es 381 o 383. El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>F1153F</th>
	 * 		<td>Calificador Documento Rectificado / Sustituido</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>197</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorDocumentoRectificado_Sustituido() {
		return calificadorDocumentoRectificado_Sustituido;
	}
	public void setCalificadorDocumentoRectificado_Sustituido(String calificadorDocumentoRectificado_Sustituido) {
		this.calificadorDocumentoRectificado_Sustituido = calificadorDocumentoRectificado_Sustituido;
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
	 * 		<td>F1154F</th>
	 * 		<td>Número Documento Rectificado / Sustituido</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>200</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDocumentoRectificado_Sustituido() {
		return numeroDocumentoRectificado_Sustituido;
	}
	public void setNumeroDocumentoRectificado_Sustituido(String numeroDocumentoRectificado_Sustituido) {
		this.numeroDocumentoRectificado_Sustituido = numeroDocumentoRectificado_Sustituido;
	}

	/** 
	 * F1154C - Número de Contrato: Será obligatorio si el Tipo de Factura es 380 y la factura es de servicios.
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
	 * 		<td>F1154C</th>
	 * 		<td>Número de Contrato/Acuerdo (CT)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>217</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeContrato_Acuerdo_CT_() {
		return numeroDeContrato_Acuerdo_CT_;
	}
	public void setNumeroDeContrato_Acuerdo_CT_(String numeroDeContrato_Acuerdo_CT_) {
		this.numeroDeContrato_Acuerdo_CT_ = numeroDeContrato_Acuerdo_CT_;
	}

	/** 
	 * F1154R - Número Relación de Entregas: Será obligatorio si el Tipo de Factura es 385 (Fact.Recapitulativa)
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
	 * 		<td>F1154R</th>
	 * 		<td>Número de Relación de Entregas (REN)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>234</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeRelacionDeEntregas_REN_() {
		return numeroDeRelacionDeEntregas_REN_;
	}
	public void setNumeroDeRelacionDeEntregas_REN_(String numeroDeRelacionDeEntregas_REN_) {
		this.numeroDeRelacionDeEntregas_REN_ = numeroDeRelacionDeEntregas_REN_;
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
	 * 		<td>F3036R</th>
	 * 		<td>Razón social Receptor de la Factura</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>251</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getRazonSocialReceptorDeLaFactura() {
		return razonSocialReceptorDeLaFactura;
	}
	public void setRazonSocialReceptorDeLaFactura(String razonSocialReceptorDeLaFactura) {
		this.razonSocialReceptorDeLaFactura = razonSocialReceptorDeLaFactura;
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
	 * 		<td>F3042D</th>
	 * 		<td>Nombre/Número de la calle del Receptor de la Factura</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>321</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombre_NumeroDeLaCalleDelReceptorDeLaFactura() {
		return nombre_NumeroDeLaCalleDelReceptorDeLaFactura;
	}
	public void setNombre_NumeroDeLaCalleDelReceptorDeLaFactura(String nombre_NumeroDeLaCalleDelReceptorDeLaFactura) {
		this.nombre_NumeroDeLaCalleDelReceptorDeLaFactura = nombre_NumeroDeLaCalleDelReceptorDeLaFactura;
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
	 * 		<td>F3164P</th>
	 * 		<td>Población del Receptor de la Factura</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>391</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPoblacionDelReceptorDeLaFactura() {
		return poblacionDelReceptorDeLaFactura;
	}
	public void setPoblacionDelReceptorDeLaFactura(String poblacionDelReceptorDeLaFactura) {
		this.poblacionDelReceptorDeLaFactura = poblacionDelReceptorDeLaFactura;
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
	 * 		<td>F3251P</th>
	 * 		<td>Código Postal del Receptor de la Factura</th>
	 * 		<td>C</th>
	 * 		<td>9</th>
	 * 		<td>426</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoPostalDelReceptorDeLaFactura() {
		return codigoPostalDelReceptorDeLaFactura;
	}
	public void setCodigoPostalDelReceptorDeLaFactura(String codigoPostalDelReceptorDeLaFactura) {
		this.codigoPostalDelReceptorDeLaFactura = codigoPostalDelReceptorDeLaFactura;
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
	 * 		<td>F1154N</th>
	 * 		<td>NIF del Receptor de la Factura</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>435</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNIFDelReceptorDeLaFactura() {
		return nIFDelReceptorDeLaFactura;
	}
	public void setNIFDelReceptorDeLaFactura(String nIFDelReceptorDeLaFactura) {
		this.nIFDelReceptorDeLaFactura = nIFDelReceptorDeLaFactura;
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
	 * 		<td>F3042E</th>
	 * 		<td>Nombre/Número de la calle del Emisor de la Factura</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>452</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombre_NumeroDeLaCalleDelEmisorDeLaFactura() {
		return nombre_NumeroDeLaCalleDelEmisorDeLaFactura;
	}
	public void setNombre_NumeroDeLaCalleDelEmisorDeLaFactura(String nombre_NumeroDeLaCalleDelEmisorDeLaFactura) {
		this.nombre_NumeroDeLaCalleDelEmisorDeLaFactura = nombre_NumeroDeLaCalleDelEmisorDeLaFactura;
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
	 * 		<td>F3164E</th>
	 * 		<td>Población del Emisor de la Factura</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>522</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPoblacionDelEmisorDeLaFactura() {
		return poblacionDelEmisorDeLaFactura;
	}
	public void setPoblacionDelEmisorDeLaFactura(String poblacionDelEmisorDeLaFactura) {
		this.poblacionDelEmisorDeLaFactura = poblacionDelEmisorDeLaFactura;
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
	 * 		<td>F3251E</th>
	 * 		<td>Código Postal del Emisor de la Factura</th>
	 * 		<td>C</th>
	 * 		<td>9</th>
	 * 		<td>557</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoPostalDelEmisorDeLaFactura() {
		return codigoPostalDelEmisorDeLaFactura;
	}
	public void setCodigoPostalDelEmisorDeLaFactura(String codigoPostalDelEmisorDeLaFactura) {
		this.codigoPostalDelEmisorDeLaFactura = codigoPostalDelEmisorDeLaFactura;
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
	 * 		<td>F6345M</th>
	 * 		<td>Código de Moneda</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>566</th>
	 * 		<td>N</th>
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
	 * F2380V - Fecha vencimiento: Se indicará si la factura es de Pago único, en caso contrario se dejará a cero y los vencimientos se indicarán en el registro SINCV. Debe montarse en formato AAAAMMDD
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
	 * 		<td>F2380V</th>
	 * 		<td>Fecha vencimiento único</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>572</th>
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
	 * F5004N - Importe Neto Total Factura: Corresponde al sumatorio de los importes netos por línea
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
	 * 		<td>F5004N</th>
	 * 		<td>Importe Neto Total Factura (79)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>580</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteNetoTotalFactura_79_() {
		return importeNetoTotalFactura_79_;
	}
	public void setImporteNetoTotalFactura_79_(String importeNetoTotalFactura_79_) {
		this.importeNetoTotalFactura_79_ = importeNetoTotalFactura_79_;
	}

	/** 
	 * F5004B - Base Imponible: Importe Neto Total  de Factura (F500N) + Total cargos y descuentos Gobales (F5004D)
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
	 * 		<td>F5004B</th>
	 * 		<td>Base Imponible (125)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>598</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getBaseImponible_125_() {
		return baseImponible_125_;
	}
	public void setBaseImponible_125_(String baseImponible_125_) {
		this.baseImponible_125_ = baseImponible_125_;
	}

	/** 
	 * F5004D - Importe Bruto: Sumatorio de los importes Brutos de las lineas (cantidad facturada x precio unitario Bruto). No se tienen en cuenta Cargos ni Descuentos tanto a nivel de líneas como globales.
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
	 * 		<td>F5004D</th>
	 * 		<td>Importe Bruto Total Factura (98)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>616</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteBrutoTotalFactura_98_() {
		return importeBrutoTotalFactura_98_;
	}
	public void setImporteBrutoTotalFactura_98_(String importeBrutoTotalFactura_98_) {
		this.importeBrutoTotalFactura_98_ = importeBrutoTotalFactura_98_;
	}

	/** 
	 * F5004I - Importe Total de Impuestos: Sumatorio de los importes  de impuestos por línea.
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
	 * 		<td>F5004I</th>
	 * 		<td>Importe Total de Impuestos (176)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>634</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTotalDeImpuestos_176_() {
		return importeTotalDeImpuestos_176_;
	}
	public void setImporteTotalDeImpuestos_176_(String importeTotalDeImpuestos_176_) {
		this.importeTotalDeImpuestos_176_ = importeTotalDeImpuestos_176_;
	}

	/** 
	 * F5004P - Importe Total a Pagar: Base Imponible + Importe Total de Impuestos
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
	 * 		<td>F5004P</th>
	 * 		<td>Importe Total a Pagar (139)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>652</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTotalAPagar_139_() {
		return importeTotalAPagar_139_;
	}
	public void setImporteTotalAPagar_139_(String importeTotalAPagar_139_) {
		this.importeTotalAPagar_139_ = importeTotalAPagar_139_;
	}

	/** 
	 * F5004S - Subvenciones vinculadas al precio: Las subvenciones vinculadas al precio deben formar parte de la base imponible para calcular el IVA aunque no estén reflejadas en el importe total a pagar.
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
	 * 		<td>F5004S</th>
	 * 		<td>Subvenciones vinculadas al Precio  (80A)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>670</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getSubvencionesVinculadasAlPrecio_80A_() {
		return subvencionesVinculadasAlPrecio_80A_;
	}
	public void setSubvencionesVinculadasAlPrecio_80A_(String subvencionesVinculadasAlPrecio_80A_) {
		this.subvencionesVinculadasAlPrecio_80A_ = subvencionesVinculadasAlPrecio_80A_;
	}

	/** 
	 * F5004E - Total Incrementos Importe Bruto: Sumatorio de los cargos globales de factura excluyendo los de líneas (no especificar signo).
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
	 * 		<td>F5004E</th>
	 * 		<td>Total Incrementos del Importe Bruto (259)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>688</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTotalIncrementosDelImporteBruto_259_() {
		return totalIncrementosDelImporteBruto_259_;
	}
	public void setTotalIncrementosDelImporteBruto_259_(String totalIncrementosDelImporteBruto_259_) {
		this.totalIncrementosDelImporteBruto_259_ = totalIncrementosDelImporteBruto_259_;
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
	 * 		<td>F5004M</th>
	 * 		<td>Total Minoraciones del Importe Bruto (260)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>706</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTotalMinoracionesDelImporteBruto_260_() {
		return totalMinoracionesDelImporteBruto_260_;
	}
	public void setTotalMinoracionesDelImporteBruto_260_(String totalMinoracionesDelImporteBruto_260_) {
		this.totalMinoracionesDelImporteBruto_260_ = totalMinoracionesDelImporteBruto_260_;
	}

	/** 
	 * F1154I - Identificación adicional de la parte: Se utiliza para especificar el departamento que realizó el pedido. Se informa como una referencia asociada al Punto Operacional del Comprador (lo encontraremos como segmento RFF con calificador API asociado al NAD con calificador BY).
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
	 * 		<td>F1154I</th>
	 * 		<td>Identificación Adicional de la Parte (API)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>724</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getIdentificacionAdicionalDeLaParte_API_() {
		return identificacionAdicionalDeLaParte_API_;
	}
	public void setIdentificacionAdicionalDeLaParte_API_(String identificacionAdicionalDeLaParte_API_) {
		this.identificacionAdicionalDeLaParte_API_ = identificacionAdicionalDeLaParte_API_;
	}

	/** 
	 * F3039M - Receptor del documento: Código EDI  del Cliente al que se recibe la  factura
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
	 * 		<td>F3039M</th>
	 * 		<td>Receptor del documento</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>741</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getReceptorDelDocumento() {
		return receptorDelDocumento;
	}
	public void setReceptorDelDocumento(String receptorDelDocumento) {
		this.receptorDelDocumento = receptorDelDocumento;
	}

	/** 
	 * F1154S - Identificación Adicional Proveedor(API)(NAD+SU):
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
	 * 		<td>F1154S</th>
	 * 		<td>Identificación Adicional Proveedor(API)(NAD+SU)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>754</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getIdentificacionAdicionalProveedor_API__NAD_SU_() {
		return identificacionAdicionalProveedor_API__NAD_SU_;
	}
	public void setIdentificacionAdicionalProveedor_API__NAD_SU_(String identificacionAdicionalProveedor_API__NAD_SU_) {
		this.identificacionAdicionalProveedor_API__NAD_SU_ = identificacionAdicionalProveedor_API__NAD_SU_;
	}

	public enum TipoFactura {
		FACTURA_PRO_FORM_325("325"),
		FACTURA_COMERCIA_380("380"),
		NOTA_DE_ABON_381("381"),
		NOTA_DE_CARG_383("383"),
		FACTURA_CONSOLIDAD_385("385"),
		AUTOFACTUR_389("389"),
		;
		
		private String value;
		
		private TipoFactura(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum FuncionDelMensaje {
		DUPLICAD_7("7"),
		COPIA__INDICA_QUE_EL_MENSAJE_ES_UNA_COPIA__PUEDE_UTILIZARSE_PARA_ENVIAR_LA_FACTURA_A_UN_TERCER_INTERLOCUTOR_CON_PROPOSITOS_INFORMATIVOS_31("31"),
		SUSTITUTIVA__SE_USARA_EN_EL_CASO_DE_ANULACION_DE_FACTURAS_POR_ERRORES_ADMINISTRATIVOS__LA_FACTURA_ANULA_LA_ANTERIOR_REFERENCIADA_EN_EL_CAMPO_F1154F__NUMERO_DE_DOC__SUSTITUIDO_5("5"),
		TRANSMISION_ADICIONA_43("43"),
		PAGO_A_CUENTA_BANCARI_42("42"),
		;
		
		private String value;
		
		private FuncionDelMensaje(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum RazonDelCargoODelAbono {
		DEVOLUCION_DE_MERCANCI_1A("1A"),
		BONIFICACION_POR_VOLUMEN__RAPPEL_2A("2A"),
		DIFERENCIA__PRECIO__CANTIDAD_____3A("3A"),
		;
		
		private String value;
		
		private RazonDelCargoODelAbono(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadorDocumentoRectificado {
		NUMERO_DE_FACTUR_IV("IV"),
		NUMERO_DE_RELACION_DE_FACTURA_RFA("RFA"),
		NUMERO_DE_FACTURA_RECAPITULATIV_FR("FR"),
		;
		
		private String value;
		
		private CalificadorDocumentoRectificado(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}