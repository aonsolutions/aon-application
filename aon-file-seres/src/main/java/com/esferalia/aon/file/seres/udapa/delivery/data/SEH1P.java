package com.esferalia.aon.file.seres.udapa.delivery.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1P entity.
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
 * 		<td>SEH1P</th>
 * 		<td>Embalajes</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SEH1P {

	private String embalajes;
	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeJerarquiaDeEmbalaje;
	private String numeroDeSub_jerarquiaDeEmbalaje;
	private String contadorDeEmbalaje;
	private String numeroDeBultosOEmbalajes;
	private String calificadorCodificacionDelEmbalaje;
	private String acuerdosYCondicionesDeEmbalajes;
	private String tipoDeEmbalajeCodificado;
	private String tipoDeEmbalajeDescripcion;
	private String resp_PagoTransporteDeEmbalajeRetornable;
	private String pesoTotalNeto1_AAC_;
	private String pesoTotalNeto2;
	private String cod_SignificacionDeLaMedidaPesoTotalNeto;
	private String codigoUnidadDeMedidaPesoTotalNeto;
	private String pesoTotalBruto1_AAD_;
	private String pesoTotalBruto2;
	private String cod_SignificacionDeLaMedidaPesoTotalBruto;
	private String codigoUnidadDeMedidaPesoTotalBruto;
	private String altura1_HT_;
	private String altura2;
	private String codigoSignificacionDeLaMedidaAltura_3_4_;
	private String codigoUnidadDeMedidaAltura;
	private String ancho1_WD_;
	private String ancho2;
	private String codigoSignificacionDeLaMedidaAncho_3_4_;
	private String codigoUnidadDeMedidaAncho;
	private String longitud1_AAC_;
	private String longitud2_AAC_;
	private String codigoSignificacionDeLaMedidaLongitud_3_4_;
	private String codigoUnidadDeMedidaLongitud;
	private String unidadesDeMaterialConsignado;
	private String calificadorManipulacion;
	private String instruccionesDeManipulacion;
	private String marcaOEtiquetaDeEmbarque1;
	private String marcaOEtiquetaDeEmbarque2;
	private String marcaOEtiquetaDeEmbarque3;
	private String marcaOEtiquetaDeEmbarque4;
	private String numero1DeSeriadoORangoInferiorDelEmbalaje;
	private String numero1DeSeriadoORangoSuperiorDelEmbalaje;
	private String numero2DeSeriadoORangoInferiorDelEmbalaje;
	private String numero2DeSeriadoORangoSuperiorDelEmbalaje;
	private String numero3DeSeriadoORangoInferiorDelEmbalaje;
	private String numero3DeSeriadoORangoSuperiorDelEmbalaje;


	private static java.util.regex.Pattern PATTERN_SEH1P_embalajes = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_tipoAvisoDeExpedicion_351_35E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numeroAvisoDeExpedicion = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numeroDeJerarquiaDeEmbalaje = java.util.regex.Pattern.compile("^.{63}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numeroDeSub_jerarquiaDeEmbalaje = java.util.regex.Pattern.compile("^.{75}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_contadorDeEmbalaje = java.util.regex.Pattern.compile("^.{87}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numeroDeBultosOEmbalajes = java.util.regex.Pattern.compile("^.{93}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_calificadorCodificacionDelEmbalaje = java.util.regex.Pattern.compile("^.{101}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_acuerdosYCondicionesDeEmbalajes = java.util.regex.Pattern.compile("^.{107}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_tipoDeEmbalajeCodificado = java.util.regex.Pattern.compile("^.{113}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_tipoDeEmbalajeDescripcion = java.util.regex.Pattern.compile("^.{119}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_resp_PagoTransporteDeEmbalajeRetornable = java.util.regex.Pattern.compile("^.{154}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_pesoTotalNeto1_AAC_ = java.util.regex.Pattern.compile("^.{160}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_pesoTotalNeto2 = java.util.regex.Pattern.compile("^.{178}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_cod_SignificacionDeLaMedidaPesoTotalNeto = java.util.regex.Pattern.compile("^.{196}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoUnidadDeMedidaPesoTotalNeto = java.util.regex.Pattern.compile("^.{202}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_pesoTotalBruto1_AAD_ = java.util.regex.Pattern.compile("^.{208}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_pesoTotalBruto2 = java.util.regex.Pattern.compile("^.{226}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_cod_SignificacionDeLaMedidaPesoTotalBruto = java.util.regex.Pattern.compile("^.{244}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoUnidadDeMedidaPesoTotalBruto = java.util.regex.Pattern.compile("^.{250}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_altura1_HT_ = java.util.regex.Pattern.compile("^.{256}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_altura2 = java.util.regex.Pattern.compile("^.{274}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaAltura_3_4_ = java.util.regex.Pattern.compile("^.{292}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoUnidadDeMedidaAltura = java.util.regex.Pattern.compile("^.{298}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_ancho1_WD_ = java.util.regex.Pattern.compile("^.{304}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_ancho2 = java.util.regex.Pattern.compile("^.{322}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaAncho_3_4_ = java.util.regex.Pattern.compile("^.{340}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoUnidadDeMedidaAncho = java.util.regex.Pattern.compile("^.{346}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_longitud1_AAC_ = java.util.regex.Pattern.compile("^.{352}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_longitud2_AAC_ = java.util.regex.Pattern.compile("^.{370}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaLongitud_3_4_ = java.util.regex.Pattern.compile("^.{388}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_codigoUnidadDeMedidaLongitud = java.util.regex.Pattern.compile("^.{394}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_unidadesDeMaterialConsignado = java.util.regex.Pattern.compile("^.{400}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_calificadorManipulacion = java.util.regex.Pattern.compile("^.{416}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_instruccionesDeManipulacion = java.util.regex.Pattern.compile("^.{422}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_marcaOEtiquetaDeEmbarque1 = java.util.regex.Pattern.compile("^.{492}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_marcaOEtiquetaDeEmbarque2 = java.util.regex.Pattern.compile("^.{527}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_marcaOEtiquetaDeEmbarque3 = java.util.regex.Pattern.compile("^.{562}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_marcaOEtiquetaDeEmbarque4 = java.util.regex.Pattern.compile("^.{597}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numero1DeSeriadoORangoInferiorDelEmbalaje = java.util.regex.Pattern.compile("^.{632}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numero1DeSeriadoORangoSuperiorDelEmbalaje = java.util.regex.Pattern.compile("^.{667}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numero2DeSeriadoORangoInferiorDelEmbalaje = java.util.regex.Pattern.compile("^.{702}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numero2DeSeriadoORangoSuperiorDelEmbalaje = java.util.regex.Pattern.compile("^.{737}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numero3DeSeriadoORangoInferiorDelEmbalaje = java.util.regex.Pattern.compile("^.{772}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1P_numero3DeSeriadoORangoSuperiorDelEmbalaje = java.util.regex.Pattern.compile("^.{807}(.{35}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SEH1P_embalajes.matcher(value)).find()) {
			setEmbalajes(m.group(1));
		}
		if((m = PATTERN_SEH1P_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find()) {
			setTipoAvisoDeExpedicion_351_35E_(m.group(1));
		}
		if((m = PATTERN_SEH1P_numeroAvisoDeExpedicion.matcher(value)).find()) {
			setNumeroAvisoDeExpedicion(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_SEH1P_numeroDeJerarquiaDeEmbalaje.matcher(value)).find()) {
			setNumeroDeJerarquiaDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_numeroDeSub_jerarquiaDeEmbalaje.matcher(value)).find()) {
			setNumeroDeSub_jerarquiaDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_contadorDeEmbalaje.matcher(value)).find()) {
			setContadorDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_numeroDeBultosOEmbalajes.matcher(value)).find()) {
			setNumeroDeBultosOEmbalajes(m.group(1));
		}
		if((m = PATTERN_SEH1P_calificadorCodificacionDelEmbalaje.matcher(value)).find()) {
			setCalificadorCodificacionDelEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_acuerdosYCondicionesDeEmbalajes.matcher(value)).find()) {
			setAcuerdosYCondicionesDeEmbalajes(m.group(1));
		}
		if((m = PATTERN_SEH1P_tipoDeEmbalajeCodificado.matcher(value)).find()) {
			setTipoDeEmbalajeCodificado(m.group(1));
		}
		if((m = PATTERN_SEH1P_tipoDeEmbalajeDescripcion.matcher(value)).find()) {
			setTipoDeEmbalajeDescripcion(m.group(1));
		}
		if((m = PATTERN_SEH1P_resp_PagoTransporteDeEmbalajeRetornable.matcher(value)).find()) {
			setResp_PagoTransporteDeEmbalajeRetornable(m.group(1));
		}
		if((m = PATTERN_SEH1P_pesoTotalNeto1_AAC_.matcher(value)).find()) {
			setPesoTotalNeto1_AAC_(m.group(1));
		}
		if((m = PATTERN_SEH1P_pesoTotalNeto2.matcher(value)).find()) {
			setPesoTotalNeto2(m.group(1));
		}
		if((m = PATTERN_SEH1P_cod_SignificacionDeLaMedidaPesoTotalNeto.matcher(value)).find()) {
			setCod_SignificacionDeLaMedidaPesoTotalNeto(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaPesoTotalNeto.matcher(value)).find()) {
			setCodigoUnidadDeMedidaPesoTotalNeto(m.group(1));
		}
		if((m = PATTERN_SEH1P_pesoTotalBruto1_AAD_.matcher(value)).find()) {
			setPesoTotalBruto1_AAD_(m.group(1));
		}
		if((m = PATTERN_SEH1P_pesoTotalBruto2.matcher(value)).find()) {
			setPesoTotalBruto2(m.group(1));
		}
		if((m = PATTERN_SEH1P_cod_SignificacionDeLaMedidaPesoTotalBruto.matcher(value)).find()) {
			setCod_SignificacionDeLaMedidaPesoTotalBruto(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaPesoTotalBruto.matcher(value)).find()) {
			setCodigoUnidadDeMedidaPesoTotalBruto(m.group(1));
		}
		if((m = PATTERN_SEH1P_altura1_HT_.matcher(value)).find()) {
			setAltura1_HT_(m.group(1));
		}
		if((m = PATTERN_SEH1P_altura2.matcher(value)).find()) {
			setAltura2(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaAltura_3_4_.matcher(value)).find()) {
			setCodigoSignificacionDeLaMedidaAltura_3_4_(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaAltura.matcher(value)).find()) {
			setCodigoUnidadDeMedidaAltura(m.group(1));
		}
		if((m = PATTERN_SEH1P_ancho1_WD_.matcher(value)).find()) {
			setAncho1_WD_(m.group(1));
		}
		if((m = PATTERN_SEH1P_ancho2.matcher(value)).find()) {
			setAncho2(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaAncho_3_4_.matcher(value)).find()) {
			setCodigoSignificacionDeLaMedidaAncho_3_4_(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaAncho.matcher(value)).find()) {
			setCodigoUnidadDeMedidaAncho(m.group(1));
		}
		if((m = PATTERN_SEH1P_longitud1_AAC_.matcher(value)).find()) {
			setLongitud1_AAC_(m.group(1));
		}
		if((m = PATTERN_SEH1P_longitud2_AAC_.matcher(value)).find()) {
			setLongitud2_AAC_(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaLongitud_3_4_.matcher(value)).find()) {
			setCodigoSignificacionDeLaMedidaLongitud_3_4_(m.group(1));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaLongitud.matcher(value)).find()) {
			setCodigoUnidadDeMedidaLongitud(m.group(1));
		}
		if((m = PATTERN_SEH1P_unidadesDeMaterialConsignado.matcher(value)).find()) {
			setUnidadesDeMaterialConsignado(m.group(1));
		}
		if((m = PATTERN_SEH1P_calificadorManipulacion.matcher(value)).find()) {
			setCalificadorManipulacion(m.group(1));
		}
		if((m = PATTERN_SEH1P_instruccionesDeManipulacion.matcher(value)).find()) {
			setInstruccionesDeManipulacion(m.group(1));
		}
		if((m = PATTERN_SEH1P_marcaOEtiquetaDeEmbarque1.matcher(value)).find()) {
			setMarcaOEtiquetaDeEmbarque1(m.group(1));
		}
		if((m = PATTERN_SEH1P_marcaOEtiquetaDeEmbarque2.matcher(value)).find()) {
			setMarcaOEtiquetaDeEmbarque2(m.group(1));
		}
		if((m = PATTERN_SEH1P_marcaOEtiquetaDeEmbarque3.matcher(value)).find()) {
			setMarcaOEtiquetaDeEmbarque3(m.group(1));
		}
		if((m = PATTERN_SEH1P_marcaOEtiquetaDeEmbarque4.matcher(value)).find()) {
			setMarcaOEtiquetaDeEmbarque4(m.group(1));
		}
		if((m = PATTERN_SEH1P_numero1DeSeriadoORangoInferiorDelEmbalaje.matcher(value)).find()) {
			setNumero1DeSeriadoORangoInferiorDelEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_numero1DeSeriadoORangoSuperiorDelEmbalaje.matcher(value)).find()) {
			setNumero1DeSeriadoORangoSuperiorDelEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_numero2DeSeriadoORangoInferiorDelEmbalaje.matcher(value)).find()) {
			setNumero2DeSeriadoORangoInferiorDelEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_numero2DeSeriadoORangoSuperiorDelEmbalaje.matcher(value)).find()) {
			setNumero2DeSeriadoORangoSuperiorDelEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_numero3DeSeriadoORangoInferiorDelEmbalaje.matcher(value)).find()) {
			setNumero3DeSeriadoORangoInferiorDelEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1P_numero3DeSeriadoORangoSuperiorDelEmbalaje.matcher(value)).find()) {
			setNumero3DeSeriadoORangoSuperiorDelEmbalaje(m.group(1));
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
	 * 		<td>SEH1P</th>
	 * 		<td>Embalajes</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getEmbalajes() {
		return embalajes;
	}
	public void setEmbalajes(String embalajes) {
		this.embalajes = embalajes;
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
	 * 		<td>V1001T</th>
	 * 		<td>Tipo Aviso de Expedición (351/35E)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoAvisoDeExpedicion_351_35E_() {
		return tipoAvisoDeExpedicion_351_35E_;
	}
	public void setTipoAvisoDeExpedicion_351_35E_(String tipoAvisoDeExpedicion_351_35E_) {
		this.tipoAvisoDeExpedicion_351_35E_ = tipoAvisoDeExpedicion_351_35E_;
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
	 * 		<td>V1004P</th>
	 * 		<td>Número Aviso de Expedición</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroAvisoDeExpedicion() {
		return numeroAvisoDeExpedicion;
	}
	public void setNumeroAvisoDeExpedicion(String numeroAvisoDeExpedicion) {
		this.numeroAvisoDeExpedicion = numeroAvisoDeExpedicion;
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
	 * 		<td>V3039E</th>
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
	 * 		<td>V3039R</th>
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
	 * V7164J-Número de Jerarquía de Embalaje: Se recomienda una numeración Secuencial.  Tanto los embalajes como los artículos que componen la expedición suelen emitirse bajo un mismo Nº de Jerarquía (valor 1). Se utiliza para identificar la secuencia en la que el embalaje físico está dispuesto.
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
	 * 		<td>V7164J</th>
	 * 		<td>Número de Jerarquía de Embalaje</th>
	 * 		<td>N</th>
	 * 		<td>12</th>
	 * 		<td>64</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeJerarquiaDeEmbalaje() {
		return numeroDeJerarquiaDeEmbalaje;
	}
	public void setNumeroDeJerarquiaDeEmbalaje(String numeroDeJerarquiaDeEmbalaje) {
		this.numeroDeJerarquiaDeEmbalaje = numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * V7166J-Número de Sub-jerarquía de Embalaje: Campo Opcional que indica el nivel jerárquico del que depende el nivel de empaquetamiento actual.
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
	 * 		<td>V7166J</th>
	 * 		<td>Número de Sub-jerarquia de Embalaje</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>76</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeSub_jerarquiaDeEmbalaje() {
		return numeroDeSub_jerarquiaDeEmbalaje;
	}
	public void setNumeroDeSub_jerarquiaDeEmbalaje(String numeroDeSub_jerarquiaDeEmbalaje) {
		this.numeroDeSub_jerarquiaDeEmbalaje = numeroDeSub_jerarquiaDeEmbalaje;
	}

	/** 
	 * V1082E-Contador de Embalaje: Contador que permite diferenciar los distintos tipos de embalajes en la expedición.
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
	 * 		<td>V1082E</th>
	 * 		<td>Contador de Embalaje</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>88</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getContadorDeEmbalaje() {
		return contadorDeEmbalaje;
	}
	public void setContadorDeEmbalaje(String contadorDeEmbalaje) {
		this.contadorDeEmbalaje = contadorDeEmbalaje;
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
	 * 		<td>V7224B</th>
	 * 		<td>Número de Bultos o Embalajes</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>94</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeBultosOEmbalajes() {
		return numeroDeBultosOEmbalajes;
	}
	public void setNumeroDeBultosOEmbalajes(String numeroDeBultosOEmbalajes) {
		this.numeroDeBultosOEmbalajes = numeroDeBultosOEmbalajes;
	}

	/** 
	 * V7233C-Calificador Codificación del Embalaje: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V7233C</th>
	 * 		<td>Calificador Codificación del Embalaje</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>102</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorCodificacionDelEmbalaje() {
		return calificadorCodificacionDelEmbalaje;
	}
	public void setCalificadorCodificacionDelEmbalaje(String calificadorCodificacionDelEmbalaje) {
		this.calificadorCodificacionDelEmbalaje = calificadorCodificacionDelEmbalaje;
	}

	/** 
	 * V7073T-Acuerdos y Condiciones de Embalajes: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V7073T</th>
	 * 		<td>Acuerdos y Condiciones de Embalajes</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>108</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getAcuerdosYCondicionesDeEmbalajes() {
		return acuerdosYCondicionesDeEmbalajes;
	}
	public void setAcuerdosYCondicionesDeEmbalajes(String acuerdosYCondicionesDeEmbalajes) {
		this.acuerdosYCondicionesDeEmbalajes = acuerdosYCondicionesDeEmbalajes;
	}

	/** 
	 * V7065T-Tipo de Embalaje codificado: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V7065T</th>
	 * 		<td>Tipo de Embalaje codificado</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>114</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDeEmbalajeCodificado() {
		return tipoDeEmbalajeCodificado;
	}
	public void setTipoDeEmbalajeCodificado(String tipoDeEmbalajeCodificado) {
		this.tipoDeEmbalajeCodificado = tipoDeEmbalajeCodificado;
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
	 * 		<td>V7064D</th>
	 * 		<td>Tipo de Embalaje Descripción</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>120</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDeEmbalajeDescripcion() {
		return tipoDeEmbalajeDescripcion;
	}
	public void setTipoDeEmbalajeDescripcion(String tipoDeEmbalajeDescripcion) {
		this.tipoDeEmbalajeDescripcion = tipoDeEmbalajeDescripcion;
	}

	/** 
	 * V8395P - Responsabilidad de pago del Transporte de embalajes Retornables: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V8395P</th>
	 * 		<td>Resp. Pago Transporte de Embalaje Retornable</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>155</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getResp_PagoTransporteDeEmbalajeRetornable() {
		return resp_PagoTransporteDeEmbalajeRetornable;
	}
	public void setResp_PagoTransporteDeEmbalajeRetornable(String resp_PagoTransporteDeEmbalajeRetornable) {
		this.resp_PagoTransporteDeEmbalajeRetornable = resp_PagoTransporteDeEmbalajeRetornable;
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
	 * 		<td>V3131N</th>
	 * 		<td>Peso Total Neto 1 (AAC)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>161</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPesoTotalNeto1_AAC_() {
		return pesoTotalNeto1_AAC_;
	}
	public void setPesoTotalNeto1_AAC_(String pesoTotalNeto1_AAC_) {
		this.pesoTotalNeto1_AAC_ = pesoTotalNeto1_AAC_;
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
	 * 		<td>V3132N</th>
	 * 		<td>Peso Total Neto 2</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>179</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPesoTotalNeto2() {
		return pesoTotalNeto2;
	}
	public void setPesoTotalNeto2(String pesoTotalNeto2) {
		this.pesoTotalNeto2 = pesoTotalNeto2;
	}

	/** 
	 * V6321N - Código de Significación del peso Total Neto
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
	 * 		<td>V6321N</th>
	 * 		<td>Cód. Significación de la medida Peso Total Neto</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>197</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCod_SignificacionDeLaMedidaPesoTotalNeto() {
		return cod_SignificacionDeLaMedidaPesoTotalNeto;
	}
	public void setCod_SignificacionDeLaMedidaPesoTotalNeto(String cod_SignificacionDeLaMedidaPesoTotalNeto) {
		this.cod_SignificacionDeLaMedidaPesoTotalNeto = cod_SignificacionDeLaMedidaPesoTotalNeto;
	}

	/** 
	 * V6411N - Unidad de medida del Peso Total Neto
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
	 * 		<td>V6411N</th>
	 * 		<td>Código Unidad de medida Peso Total Neto</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>203</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoUnidadDeMedidaPesoTotalNeto() {
		return codigoUnidadDeMedidaPesoTotalNeto;
	}
	public void setCodigoUnidadDeMedidaPesoTotalNeto(String codigoUnidadDeMedidaPesoTotalNeto) {
		this.codigoUnidadDeMedidaPesoTotalNeto = codigoUnidadDeMedidaPesoTotalNeto;
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
	 * 		<td>V3131B</th>
	 * 		<td>Peso Total Bruto 1 (AAD)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>209</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPesoTotalBruto1_AAD_() {
		return pesoTotalBruto1_AAD_;
	}
	public void setPesoTotalBruto1_AAD_(String pesoTotalBruto1_AAD_) {
		this.pesoTotalBruto1_AAD_ = pesoTotalBruto1_AAD_;
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
	 * 		<td>V3132B</th>
	 * 		<td>Peso Total Bruto 2</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>227</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPesoTotalBruto2() {
		return pesoTotalBruto2;
	}
	public void setPesoTotalBruto2(String pesoTotalBruto2) {
		this.pesoTotalBruto2 = pesoTotalBruto2;
	}

	/** 
	 * V6321B - Código de Significación del peso Total Bruto
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
	 * 		<td>V6321B</th>
	 * 		<td>Cód. Significación de la medida Peso Total Bruto</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>245</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCod_SignificacionDeLaMedidaPesoTotalBruto() {
		return cod_SignificacionDeLaMedidaPesoTotalBruto;
	}
	public void setCod_SignificacionDeLaMedidaPesoTotalBruto(String cod_SignificacionDeLaMedidaPesoTotalBruto) {
		this.cod_SignificacionDeLaMedidaPesoTotalBruto = cod_SignificacionDeLaMedidaPesoTotalBruto;
	}

	/** 
	 * V6411B - Unidad de medida del Peso Total Bruto
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
	 * 		<td>V6411B</th>
	 * 		<td>Código Unidad de medida Peso Total Bruto</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>251</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoUnidadDeMedidaPesoTotalBruto() {
		return codigoUnidadDeMedidaPesoTotalBruto;
	}
	public void setCodigoUnidadDeMedidaPesoTotalBruto(String codigoUnidadDeMedidaPesoTotalBruto) {
		this.codigoUnidadDeMedidaPesoTotalBruto = codigoUnidadDeMedidaPesoTotalBruto;
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
	 * 		<td>V3131A</th>
	 * 		<td>Altura 1 (HT)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>257</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getAltura1_HT_() {
		return altura1_HT_;
	}
	public void setAltura1_HT_(String altura1_HT_) {
		this.altura1_HT_ = altura1_HT_;
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
	 * 		<td>V3132A</th>
	 * 		<td>Altura 2</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>275</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getAltura2() {
		return altura2;
	}
	public void setAltura2(String altura2) {
		this.altura2 = altura2;
	}

	/** 
	 * V6321A - Código de Significación de la Altura
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
	 * 		<td>V6321A</th>
	 * 		<td>Código Significación de la medida Altura (3 , 4)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>293</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoSignificacionDeLaMedidaAltura_3_4_() {
		return codigoSignificacionDeLaMedidaAltura_3_4_;
	}
	public void setCodigoSignificacionDeLaMedidaAltura_3_4_(String codigoSignificacionDeLaMedidaAltura_3_4_) {
		this.codigoSignificacionDeLaMedidaAltura_3_4_ = codigoSignificacionDeLaMedidaAltura_3_4_;
	}

	/** 
	 * V6411A - Unidad de medida de la Altura
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
	 * 		<td>V6411A</th>
	 * 		<td>Código Unidad de medida Altura</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>299</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoUnidadDeMedidaAltura() {
		return codigoUnidadDeMedidaAltura;
	}
	public void setCodigoUnidadDeMedidaAltura(String codigoUnidadDeMedidaAltura) {
		this.codigoUnidadDeMedidaAltura = codigoUnidadDeMedidaAltura;
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
	 * 		<td>V3131W</th>
	 * 		<td>Ancho 1 (WD)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>305</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getAncho1_WD_() {
		return ancho1_WD_;
	}
	public void setAncho1_WD_(String ancho1_WD_) {
		this.ancho1_WD_ = ancho1_WD_;
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
	 * 		<td>V3132W</th>
	 * 		<td>Ancho 2</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>323</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getAncho2() {
		return ancho2;
	}
	public void setAncho2(String ancho2) {
		this.ancho2 = ancho2;
	}

	/** 
	 * V6321W- Código de Significación del  Ancho
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
	 * 		<td>V6321W</th>
	 * 		<td>Código Significación de la medida Ancho (3 , 4)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>341</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoSignificacionDeLaMedidaAncho_3_4_() {
		return codigoSignificacionDeLaMedidaAncho_3_4_;
	}
	public void setCodigoSignificacionDeLaMedidaAncho_3_4_(String codigoSignificacionDeLaMedidaAncho_3_4_) {
		this.codigoSignificacionDeLaMedidaAncho_3_4_ = codigoSignificacionDeLaMedidaAncho_3_4_;
	}

	/** 
	 * V6411W - Unidad de medida del  Ancho
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
	 * 		<td>V6411W</th>
	 * 		<td>Código Unidad de medida Ancho</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>347</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoUnidadDeMedidaAncho() {
		return codigoUnidadDeMedidaAncho;
	}
	public void setCodigoUnidadDeMedidaAncho(String codigoUnidadDeMedidaAncho) {
		this.codigoUnidadDeMedidaAncho = codigoUnidadDeMedidaAncho;
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
	 * 		<td>V3131L</th>
	 * 		<td>Longitud 1 (AAC)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>353</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getLongitud1_AAC_() {
		return longitud1_AAC_;
	}
	public void setLongitud1_AAC_(String longitud1_AAC_) {
		this.longitud1_AAC_ = longitud1_AAC_;
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
	 * 		<td>V3132L</th>
	 * 		<td>Longitud 2 (AAC)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>371</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getLongitud2_AAC_() {
		return longitud2_AAC_;
	}
	public void setLongitud2_AAC_(String longitud2_AAC_) {
		this.longitud2_AAC_ = longitud2_AAC_;
	}

	/** 
	 * V6321L - Código de Significación de la Longitud: Estos campos corresponden a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V6321L</th>
	 * 		<td>Código Significación de la medida Longitud(3 , 4)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>389</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoSignificacionDeLaMedidaLongitud_3_4_() {
		return codigoSignificacionDeLaMedidaLongitud_3_4_;
	}
	public void setCodigoSignificacionDeLaMedidaLongitud_3_4_(String codigoSignificacionDeLaMedidaLongitud_3_4_) {
		this.codigoSignificacionDeLaMedidaLongitud_3_4_ = codigoSignificacionDeLaMedidaLongitud_3_4_;
	}

	/** 
	 * V6411L - Unidad de medida de la Longitud: Estos campos corresponden a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V6411L</th>
	 * 		<td>Código Unidad de medida Longitud</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>395</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoUnidadDeMedidaLongitud() {
		return codigoUnidadDeMedidaLongitud;
	}
	public void setCodigoUnidadDeMedidaLongitud(String codigoUnidadDeMedidaLongitud) {
		this.codigoUnidadDeMedidaLongitud = codigoUnidadDeMedidaLongitud;
	}

	/** 
	 * V6060M-Unidades de Material Consignado: Cantidad de unidades de Material Consignado por embalaje especificado en V7224B. Por ejemplo:	10 pallets.
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
	 * 		<td>V6060M</th>
	 * 		<td>Unidades de Material Consignado</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>401</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getUnidadesDeMaterialConsignado() {
		return unidadesDeMaterialConsignado;
	}
	public void setUnidadesDeMaterialConsignado(String unidadesDeMaterialConsignado) {
		this.unidadesDeMaterialConsignado = unidadesDeMaterialConsignado;
	}

	/** 
	 * V4079C-Calificador de Instrucciones de Manipulación: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V4079C</th>
	 * 		<td>Calificador Manipulación</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>417</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorManipulacion() {
		return calificadorManipulacion;
	}
	public void setCalificadorManipulacion(String calificadorManipulacion) {
		this.calificadorManipulacion = calificadorManipulacion;
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
	 * 		<td>V4078D</th>
	 * 		<td>Instrucciones de Manipulación</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>423</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getInstruccionesDeManipulacion() {
		return instruccionesDeManipulacion;
	}
	public void setInstruccionesDeManipulacion(String instruccionesDeManipulacion) {
		this.instruccionesDeManipulacion = instruccionesDeManipulacion;
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
	 * 		<td>V71021</th>
	 * 		<td>Marca o Etiqueta de embarque 1</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>493</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getMarcaOEtiquetaDeEmbarque1() {
		return marcaOEtiquetaDeEmbarque1;
	}
	public void setMarcaOEtiquetaDeEmbarque1(String marcaOEtiquetaDeEmbarque1) {
		this.marcaOEtiquetaDeEmbarque1 = marcaOEtiquetaDeEmbarque1;
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
	 * 		<td>V71022</th>
	 * 		<td>Marca o Etiqueta de embarque 2</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>528</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getMarcaOEtiquetaDeEmbarque2() {
		return marcaOEtiquetaDeEmbarque2;
	}
	public void setMarcaOEtiquetaDeEmbarque2(String marcaOEtiquetaDeEmbarque2) {
		this.marcaOEtiquetaDeEmbarque2 = marcaOEtiquetaDeEmbarque2;
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
	 * 		<td>V71023</th>
	 * 		<td>Marca o Etiqueta de embarque 3</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>563</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getMarcaOEtiquetaDeEmbarque3() {
		return marcaOEtiquetaDeEmbarque3;
	}
	public void setMarcaOEtiquetaDeEmbarque3(String marcaOEtiquetaDeEmbarque3) {
		this.marcaOEtiquetaDeEmbarque3 = marcaOEtiquetaDeEmbarque3;
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
	 * 		<td>V71024</th>
	 * 		<td>Marca o Etiqueta de embarque 4</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>598</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getMarcaOEtiquetaDeEmbarque4() {
		return marcaOEtiquetaDeEmbarque4;
	}
	public void setMarcaOEtiquetaDeEmbarque4(String marcaOEtiquetaDeEmbarque4) {
		this.marcaOEtiquetaDeEmbarque4 = marcaOEtiquetaDeEmbarque4;
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
	 * 		<td>V74011</th>
	 * 		<td>Número 1 de Seriado o Rango inferior del embalaje</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>633</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumero1DeSeriadoORangoInferiorDelEmbalaje() {
		return numero1DeSeriadoORangoInferiorDelEmbalaje;
	}
	public void setNumero1DeSeriadoORangoInferiorDelEmbalaje(String numero1DeSeriadoORangoInferiorDelEmbalaje) {
		this.numero1DeSeriadoORangoInferiorDelEmbalaje = numero1DeSeriadoORangoInferiorDelEmbalaje;
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
	 * 		<td>V74012</th>
	 * 		<td>Número 1 de Seriado o Rango superior del embalaje</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>668</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumero1DeSeriadoORangoSuperiorDelEmbalaje() {
		return numero1DeSeriadoORangoSuperiorDelEmbalaje;
	}
	public void setNumero1DeSeriadoORangoSuperiorDelEmbalaje(String numero1DeSeriadoORangoSuperiorDelEmbalaje) {
		this.numero1DeSeriadoORangoSuperiorDelEmbalaje = numero1DeSeriadoORangoSuperiorDelEmbalaje;
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
	 * 		<td>V74021</th>
	 * 		<td>Número 2 de Seriado o Rango  inferior del embalaje</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>703</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumero2DeSeriadoORangoInferiorDelEmbalaje() {
		return numero2DeSeriadoORangoInferiorDelEmbalaje;
	}
	public void setNumero2DeSeriadoORangoInferiorDelEmbalaje(String numero2DeSeriadoORangoInferiorDelEmbalaje) {
		this.numero2DeSeriadoORangoInferiorDelEmbalaje = numero2DeSeriadoORangoInferiorDelEmbalaje;
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
	 * 		<td>V74022</th>
	 * 		<td>Número 2 de Seriado o Rango superior del embalaje</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>738</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumero2DeSeriadoORangoSuperiorDelEmbalaje() {
		return numero2DeSeriadoORangoSuperiorDelEmbalaje;
	}
	public void setNumero2DeSeriadoORangoSuperiorDelEmbalaje(String numero2DeSeriadoORangoSuperiorDelEmbalaje) {
		this.numero2DeSeriadoORangoSuperiorDelEmbalaje = numero2DeSeriadoORangoSuperiorDelEmbalaje;
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
	 * 		<td>V74031</th>
	 * 		<td>Número 3 de Seriado o Rango  inferior del embalaje</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>773</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumero3DeSeriadoORangoInferiorDelEmbalaje() {
		return numero3DeSeriadoORangoInferiorDelEmbalaje;
	}
	public void setNumero3DeSeriadoORangoInferiorDelEmbalaje(String numero3DeSeriadoORangoInferiorDelEmbalaje) {
		this.numero3DeSeriadoORangoInferiorDelEmbalaje = numero3DeSeriadoORangoInferiorDelEmbalaje;
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
	 * 		<td>V74032</th>
	 * 		<td>Número 3 de Seriado o Rango superior del embalaje</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>808</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumero3DeSeriadoORangoSuperiorDelEmbalaje() {
		return numero3DeSeriadoORangoSuperiorDelEmbalaje;
	}
	public void setNumero3DeSeriadoORangoSuperiorDelEmbalaje(String numero3DeSeriadoORangoSuperiorDelEmbalaje) {
		this.numero3DeSeriadoORangoSuperiorDelEmbalaje = numero3DeSeriadoORangoSuperiorDelEmbalaje;
	}

	public enum CalificadorCodificacionDelEmbalaje {
		;
		
		private String value;
		
		private CalificadorCodificacionDelEmbalaje(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum AcuerdosYCondicionesDeEmbalajes {
		;
		
		private String value;
		
		private AcuerdosYCondicionesDeEmbalajes(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum TipoDeEmbalajeCodificado {
		CAJA_DE_CARTO_CT("CT"),
		CAJA_RIGID_CS("CS"),
		PAQUET_PK("PK"),
		PLACA_DE_PLASTICO__HOJA_DE_EMBALAJE_SL("SL"),
		RETRACTILAD_SW("SW"),
		ROL_RO("RO"),
		PALET_RETORNABL_09("09"),
		PALET_NO_RETORNABL_08("08"),
		PALET_1_ISO__80_X_120_CM_201("201"),
		;
		
		private String value;
		
		private TipoDeEmbalajeCodificado(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum ResponsabilidadDePagoDelTransporteDeEmbalajesRetornables {
		PAGADO_POR_CLIENT_1("1"),
		GRATI_2("2"),
		PAGADO_POR_PROVEEDO_3("3"),
		;
		
		private String value;
		
		private ResponsabilidadDePagoDelTransporteDeEmbalajesRetornables(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CodigoDeSignificacionDeLaLongitud {
		APROXIMADAMENT_3("3"),
		IGUAL_4("4"),
		;
		
		private String value;
		
		private CodigoDeSignificacionDeLaLongitud(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum UnidadDeMedidaDeLaLongitud {
		CENTIMETRO_CMT("CMT"),
		KILOGRAMO_KGM("KGM"),
		;
		
		private String value;
		
		private UnidadDeMedidaDeLaLongitud(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadorDeInstruccionesDeManipulacion {
		COMESTIBLE_EAT("EAT"),
		CONTROL_DE_PESTE_PSC("PSC"),
		FRAGI_CRU("CRU"),
		TAMA_O_ESPECIA_BIG("BIG"),
		NO_APILA_UST("UST"),
		MANEJESE_CON_CUIDAD_HWC("HWC"),
		APLICACION_LIMITAD_STR("STR"),
		;
		
		private String value;
		
		private CalificadorDeInstruccionesDeManipulacion(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}