package com.esferalia.aon.file.seres.udapa.delivery.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1P entity.
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
 * 		 <td>SEH1P</td> <td>Embalajes</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1P {

	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private Integer numeroDeJerarquiaDeEmbalaje;
	private String numeroDeSub_jerarquiaDeEmbalaje;
	private Integer contadorDeEmbalaje;
	private Integer numeroDeBultosOEmbalajes;
	private String calificadorCodificacionDelEmbalaje;
	private String acuerdosYCondicionesDeEmbalajes;
	private String tipoDeEmbalajeCodificado;
	private String tipoDeEmbalajeDescripcion;
	private String resp_PagoTransporteDeEmbalajeRetornable;
	private Double pesoTotalNeto1_AAC_;
	private Double pesoTotalNeto2;
	private String cod_SignificacionDeLaMedidaPesoTotalNeto;
	private String codigoUnidadDeMedidaPesoTotalNeto;
	private Double pesoTotalBruto1_AAD_;
	private Double pesoTotalBruto2;
	private String cod_SignificacionDeLaMedidaPesoTotalBruto;
	private String codigoUnidadDeMedidaPesoTotalBruto;
	private Double altura1_HT_;
	private Double altura2;
	private String codigoSignificacionDeLaMedidaAltura_3_4_;
	private String codigoUnidadDeMedidaAltura;
	private Double ancho1_WD_;
	private Double ancho2;
	private String codigoSignificacionDeLaMedidaAncho_3_4_;
	private String codigoUnidadDeMedidaAncho;
	private Double longitud1_AAC_;
	private Double longitud2_AAC_;
	private String codigoSignificacionDeLaMedidaLongitud_3_4_;
	private String codigoUnidadDeMedidaLongitud;
	private Double unidadesDeMaterialConsignado;
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


	private static Pattern PATTERN_SEH1P_tipoAvisoDeExpedicion_351_35E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SEH1P_numeroAvisoDeExpedicion = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SEH1P_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_SEH1P_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_SEH1P_numeroDeJerarquiaDeEmbalaje = Pattern.compile("^.{63}(.{12}).*");
	private static Pattern PATTERN_SEH1P_numeroDeSub_jerarquiaDeEmbalaje = Pattern.compile("^.{75}(.{12}).*");
	private static Pattern PATTERN_SEH1P_contadorDeEmbalaje = Pattern.compile("^.{87}(.{6}).*");
	private static Pattern PATTERN_SEH1P_numeroDeBultosOEmbalajes = Pattern.compile("^.{93}(.{8}).*");
	private static Pattern PATTERN_SEH1P_calificadorCodificacionDelEmbalaje = Pattern.compile("^.{101}(.{6}).*");
	private static Pattern PATTERN_SEH1P_acuerdosYCondicionesDeEmbalajes = Pattern.compile("^.{107}(.{6}).*");
	private static Pattern PATTERN_SEH1P_tipoDeEmbalajeCodificado = Pattern.compile("^.{113}(.{6}).*");
	private static Pattern PATTERN_SEH1P_tipoDeEmbalajeDescripcion = Pattern.compile("^.{119}(.{35}).*");
	private static Pattern PATTERN_SEH1P_resp_PagoTransporteDeEmbalajeRetornable = Pattern.compile("^.{154}(.{6}).*");
	private static Pattern PATTERN_SEH1P_pesoTotalNeto1_AAC_ = Pattern.compile("^.{160}(.{18}).*");
	private static Pattern PATTERN_SEH1P_pesoTotalNeto2 = Pattern.compile("^.{178}(.{18}).*");
	private static Pattern PATTERN_SEH1P_cod_SignificacionDeLaMedidaPesoTotalNeto = Pattern.compile("^.{196}(.{6}).*");
	private static Pattern PATTERN_SEH1P_codigoUnidadDeMedidaPesoTotalNeto = Pattern.compile("^.{202}(.{6}).*");
	private static Pattern PATTERN_SEH1P_pesoTotalBruto1_AAD_ = Pattern.compile("^.{208}(.{18}).*");
	private static Pattern PATTERN_SEH1P_pesoTotalBruto2 = Pattern.compile("^.{226}(.{18}).*");
	private static Pattern PATTERN_SEH1P_cod_SignificacionDeLaMedidaPesoTotalBruto = Pattern.compile("^.{244}(.{6}).*");
	private static Pattern PATTERN_SEH1P_codigoUnidadDeMedidaPesoTotalBruto = Pattern.compile("^.{250}(.{6}).*");
	private static Pattern PATTERN_SEH1P_altura1_HT_ = Pattern.compile("^.{256}(.{18}).*");
	private static Pattern PATTERN_SEH1P_altura2 = Pattern.compile("^.{274}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaAltura_3_4_ = Pattern.compile("^.{292}(.{6}).*");
	private static Pattern PATTERN_SEH1P_codigoUnidadDeMedidaAltura = Pattern.compile("^.{298}(.{6}).*");
	private static Pattern PATTERN_SEH1P_ancho1_WD_ = Pattern.compile("^.{304}(.{18}).*");
	private static Pattern PATTERN_SEH1P_ancho2 = Pattern.compile("^.{322}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaAncho_3_4_ = Pattern.compile("^.{340}(.{6}).*");
	private static Pattern PATTERN_SEH1P_codigoUnidadDeMedidaAncho = Pattern.compile("^.{346}(.{6}).*");
	private static Pattern PATTERN_SEH1P_longitud1_AAC_ = Pattern.compile("^.{352}(.{18}).*");
	private static Pattern PATTERN_SEH1P_longitud2_AAC_ = Pattern.compile("^.{370}(.{18}).*");
	private static Pattern PATTERN_SEH1P_codigoSignificacionDeLaMedidaLongitud_3_4_ = Pattern.compile("^.{388}(.{6}).*");
	private static Pattern PATTERN_SEH1P_codigoUnidadDeMedidaLongitud = Pattern.compile("^.{394}(.{6}).*");
	private static Pattern PATTERN_SEH1P_unidadesDeMaterialConsignado = Pattern.compile("^.{400}(.{16}).*");
	private static Pattern PATTERN_SEH1P_calificadorManipulacion = Pattern.compile("^.{416}(.{6}).*");
	private static Pattern PATTERN_SEH1P_instruccionesDeManipulacion = Pattern.compile("^.{422}(.{70}).*");
	private static Pattern PATTERN_SEH1P_marcaOEtiquetaDeEmbarque1 = Pattern.compile("^.{492}(.{35}).*");
	private static Pattern PATTERN_SEH1P_marcaOEtiquetaDeEmbarque2 = Pattern.compile("^.{527}(.{35}).*");
	private static Pattern PATTERN_SEH1P_marcaOEtiquetaDeEmbarque3 = Pattern.compile("^.{562}(.{35}).*");
	private static Pattern PATTERN_SEH1P_marcaOEtiquetaDeEmbarque4 = Pattern.compile("^.{597}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numero1DeSeriadoORangoInferiorDelEmbalaje = Pattern.compile("^.{632}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numero1DeSeriadoORangoSuperiorDelEmbalaje = Pattern.compile("^.{667}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numero2DeSeriadoORangoInferiorDelEmbalaje = Pattern.compile("^.{702}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numero2DeSeriadoORangoSuperiorDelEmbalaje = Pattern.compile("^.{737}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numero3DeSeriadoORangoInferiorDelEmbalaje = Pattern.compile("^.{772}(.{35}).*");
	private static Pattern PATTERN_SEH1P_numero3DeSeriadoORangoSuperiorDelEmbalaje = Pattern.compile("^.{807}(.{35}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1P_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoAvisoDeExpedicion_351_35E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroAvisoDeExpedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroAvisoDeExpedicion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroDeJerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeJerarquiaDeEmbalaje(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroDeSub_jerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeSub_jerarquiaDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_contadorDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setContadorDeEmbalaje(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numeroDeBultosOEmbalajes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeBultosOEmbalajes(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_calificadorCodificacionDelEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorCodificacionDelEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_acuerdosYCondicionesDeEmbalajes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAcuerdosYCondicionesDeEmbalajes(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_tipoDeEmbalajeCodificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeEmbalajeCodificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_tipoDeEmbalajeDescripcion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeEmbalajeDescripcion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_resp_PagoTransporteDeEmbalajeRetornable.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setResp_PagoTransporteDeEmbalajeRetornable(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_pesoTotalNeto1_AAC_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoTotalNeto1_AAC_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_pesoTotalNeto2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoTotalNeto2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_cod_SignificacionDeLaMedidaPesoTotalNeto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCod_SignificacionDeLaMedidaPesoTotalNeto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaPesoTotalNeto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoUnidadDeMedidaPesoTotalNeto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_pesoTotalBruto1_AAD_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoTotalBruto1_AAD_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_pesoTotalBruto2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoTotalBruto2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_cod_SignificacionDeLaMedidaPesoTotalBruto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCod_SignificacionDeLaMedidaPesoTotalBruto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaPesoTotalBruto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoUnidadDeMedidaPesoTotalBruto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_altura1_HT_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAltura1_HT_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_altura2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAltura2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaAltura_3_4_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaAltura_3_4_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaAltura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoUnidadDeMedidaAltura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_ancho1_WD_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAncho1_WD_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_ancho2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAncho2(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaAncho_3_4_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaAncho_3_4_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaAncho.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoUnidadDeMedidaAncho(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_longitud1_AAC_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setLongitud1_AAC_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_longitud2_AAC_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setLongitud2_AAC_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoSignificacionDeLaMedidaLongitud_3_4_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoSignificacionDeLaMedidaLongitud_3_4_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_codigoUnidadDeMedidaLongitud.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoUnidadDeMedidaLongitud(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_unidadesDeMaterialConsignado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadesDeMaterialConsignado(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_calificadorManipulacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorManipulacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_instruccionesDeManipulacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInstruccionesDeManipulacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_marcaOEtiquetaDeEmbarque1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaOEtiquetaDeEmbarque1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_marcaOEtiquetaDeEmbarque2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaOEtiquetaDeEmbarque2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_marcaOEtiquetaDeEmbarque3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaOEtiquetaDeEmbarque3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_marcaOEtiquetaDeEmbarque4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaOEtiquetaDeEmbarque4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numero1DeSeriadoORangoInferiorDelEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumero1DeSeriadoORangoInferiorDelEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numero1DeSeriadoORangoSuperiorDelEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumero1DeSeriadoORangoSuperiorDelEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numero2DeSeriadoORangoInferiorDelEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumero2DeSeriadoORangoInferiorDelEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numero2DeSeriadoORangoSuperiorDelEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumero2DeSeriadoORangoSuperiorDelEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numero3DeSeriadoORangoInferiorDelEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumero3DeSeriadoORangoInferiorDelEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1P_numero3DeSeriadoORangoSuperiorDelEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumero3DeSeriadoORangoSuperiorDelEmbalaje(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public String getTipoAvisoDeExpedicion_351_35E_() {
		return tipoAvisoDeExpedicion_351_35E_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1001T</td> <td>Tipo Aviso de Expedición (351/35E)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoAvisoDeExpedicion_351_35E_(String tipoAvisoDeExpedicion_351_35E_) {
		this.tipoAvisoDeExpedicion_351_35E_ = tipoAvisoDeExpedicion_351_35E_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroAvisoDeExpedicion() {
		return numeroAvisoDeExpedicion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1004P</td> <td>Número Aviso de Expedición</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroAvisoDeExpedicion(String numeroAvisoDeExpedicion) {
		this.numeroAvisoDeExpedicion = numeroAvisoDeExpedicion;
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
	 * 		 <td>V3039E</td> <td>Código Emisor  (MS)</td> <td>C</td> <td>17</td> <td>30</td> <td>M</td>
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
	 * 		 <td>V3039R</td> <td>Código Receptor (MR)</td> <td>C</td> <td>17</td> <td>47</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}

	/** 
	 * V7164J-Número de Jerarquía de Embalaje: Se recomienda una numeración Secuencial.  Tanto los embalajes como los artículos que componen la expedición suelen emitirse bajo un mismo Nº de Jerarquía (valor 1). Se utiliza para identificar la secuencia en la que el embalaje físico está dispuesto.
	 */ 
	public Integer getNumeroDeJerarquiaDeEmbalaje() {
		return numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * V7164J-Número de Jerarquía de Embalaje: Se recomienda una numeración Secuencial.  Tanto los embalajes como los artículos que componen la expedición suelen emitirse bajo un mismo Nº de Jerarquía (valor 1). Se utiliza para identificar la secuencia en la que el embalaje físico está dispuesto.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7164J</td> <td>Número de Jerarquía de Embalaje</td> <td>N</td> <td>12</td> <td>64</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeJerarquiaDeEmbalaje(Integer numeroDeJerarquiaDeEmbalaje) {
		this.numeroDeJerarquiaDeEmbalaje = numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * V7166J-Número de Sub-jerarquía de Embalaje: Campo Opcional que indica el nivel jerárquico del que depende el nivel de empaquetamiento actual.
	 */ 
	public String getNumeroDeSub_jerarquiaDeEmbalaje() {
		return numeroDeSub_jerarquiaDeEmbalaje;
	}

	/** 
	 * V7166J-Número de Sub-jerarquía de Embalaje: Campo Opcional que indica el nivel jerárquico del que depende el nivel de empaquetamiento actual.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7166J</td> <td>Número de Sub-jerarquia de Embalaje</td> <td>C</td> <td>12</td> <td>76</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeSub_jerarquiaDeEmbalaje(String numeroDeSub_jerarquiaDeEmbalaje) {
		this.numeroDeSub_jerarquiaDeEmbalaje = numeroDeSub_jerarquiaDeEmbalaje;
	}

	/** 
	 * V1082E-Contador de Embalaje: Contador que permite diferenciar los distintos tipos de embalajes en la expedición.
	 */ 
	public Integer getContadorDeEmbalaje() {
		return contadorDeEmbalaje;
	}

	/** 
	 * V1082E-Contador de Embalaje: Contador que permite diferenciar los distintos tipos de embalajes en la expedición.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1082E</td> <td>Contador de Embalaje</td> <td>N</td> <td>6</td> <td>88</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setContadorDeEmbalaje(Integer contadorDeEmbalaje) {
		this.contadorDeEmbalaje = contadorDeEmbalaje;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeBultosOEmbalajes() {
		return numeroDeBultosOEmbalajes;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7224B</td> <td>Número de Bultos o Embalajes</td> <td>N</td> <td>8</td> <td>94</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeBultosOEmbalajes(Integer numeroDeBultosOEmbalajes) {
		this.numeroDeBultosOEmbalajes = numeroDeBultosOEmbalajes;
	}

	/** 
	 * V7233C-Calificador Codificación del Embalaje: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCalificadorCodificacionDelEmbalaje() {
		return calificadorCodificacionDelEmbalaje;
	}

	/** 
	 * V7233C-Calificador Codificación del Embalaje: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7233C</td> <td>Calificador Codificación del Embalaje</td> <td>C</td> <td>6</td> <td>102</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorCodificacionDelEmbalaje(String calificadorCodificacionDelEmbalaje) {
		this.calificadorCodificacionDelEmbalaje = calificadorCodificacionDelEmbalaje;
	}

	/** 
	 * V7073T-Acuerdos y Condiciones de Embalajes: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getAcuerdosYCondicionesDeEmbalajes() {
		return acuerdosYCondicionesDeEmbalajes;
	}

	/** 
	 * V7073T-Acuerdos y Condiciones de Embalajes: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7073T</td> <td>Acuerdos y Condiciones de Embalajes</td> <td>C</td> <td>6</td> <td>108</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setAcuerdosYCondicionesDeEmbalajes(String acuerdosYCondicionesDeEmbalajes) {
		this.acuerdosYCondicionesDeEmbalajes = acuerdosYCondicionesDeEmbalajes;
	}

	/** 
	 * V7065T-Tipo de Embalaje codificado: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getTipoDeEmbalajeCodificado() {
		return tipoDeEmbalajeCodificado;
	}

	/** 
	 * V7065T-Tipo de Embalaje codificado: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7065T</td> <td>Tipo de Embalaje codificado</td> <td>C</td> <td>6</td> <td>114</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeEmbalajeCodificado(String tipoDeEmbalajeCodificado) {
		this.tipoDeEmbalajeCodificado = tipoDeEmbalajeCodificado;
	}

	/** 
	 * 
	 */ 
	public String getTipoDeEmbalajeDescripcion() {
		return tipoDeEmbalajeDescripcion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7064D</td> <td>Tipo de Embalaje Descripción</td> <td>C</td> <td>35</td> <td>120</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeEmbalajeDescripcion(String tipoDeEmbalajeDescripcion) {
		this.tipoDeEmbalajeDescripcion = tipoDeEmbalajeDescripcion;
	}

	/** 
	 * V8395P - Responsabilidad de pago del Transporte de embalajes Retornables: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getResp_PagoTransporteDeEmbalajeRetornable() {
		return resp_PagoTransporteDeEmbalajeRetornable;
	}

	/** 
	 * V8395P - Responsabilidad de pago del Transporte de embalajes Retornables: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V8395P</td> <td>Resp. Pago Transporte de Embalaje Retornable</td> <td>C</td> <td>6</td> <td>155</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setResp_PagoTransporteDeEmbalajeRetornable(String resp_PagoTransporteDeEmbalajeRetornable) {
		this.resp_PagoTransporteDeEmbalajeRetornable = resp_PagoTransporteDeEmbalajeRetornable;
	}

	/** 
	 * 
	 */ 
	public Double getPesoTotalNeto1_AAC_() {
		return pesoTotalNeto1_AAC_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3131N</td> <td>Peso Total Neto 1 (AAC)</td> <td>N(14,3)</td> <td>18</td> <td>161</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoTotalNeto1_AAC_(Double pesoTotalNeto1_AAC_) {
		this.pesoTotalNeto1_AAC_ = pesoTotalNeto1_AAC_;
	}

	/** 
	 * 
	 */ 
	public Double getPesoTotalNeto2() {
		return pesoTotalNeto2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3132N</td> <td>Peso Total Neto 2</td> <td>N(14,3)</td> <td>18</td> <td>179</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoTotalNeto2(Double pesoTotalNeto2) {
		this.pesoTotalNeto2 = pesoTotalNeto2;
	}

	/** 
	 * V6321N - Código de Significación del peso Total Neto
	 */ 
	public String getCod_SignificacionDeLaMedidaPesoTotalNeto() {
		return cod_SignificacionDeLaMedidaPesoTotalNeto;
	}

	/** 
	 * V6321N - Código de Significación del peso Total Neto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6321N</td> <td>Cód. Significación de la medida Peso Total Neto</td> <td>C</td> <td>6</td> <td>197</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCod_SignificacionDeLaMedidaPesoTotalNeto(String cod_SignificacionDeLaMedidaPesoTotalNeto) {
		this.cod_SignificacionDeLaMedidaPesoTotalNeto = cod_SignificacionDeLaMedidaPesoTotalNeto;
	}

	/** 
	 * V6411N - Unidad de medida del Peso Total Neto
	 */ 
	public String getCodigoUnidadDeMedidaPesoTotalNeto() {
		return codigoUnidadDeMedidaPesoTotalNeto;
	}

	/** 
	 * V6411N - Unidad de medida del Peso Total Neto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6411N</td> <td>Código Unidad de medida Peso Total Neto</td> <td>C</td> <td>6</td> <td>203</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoUnidadDeMedidaPesoTotalNeto(String codigoUnidadDeMedidaPesoTotalNeto) {
		this.codigoUnidadDeMedidaPesoTotalNeto = codigoUnidadDeMedidaPesoTotalNeto;
	}

	/** 
	 * 
	 */ 
	public Double getPesoTotalBruto1_AAD_() {
		return pesoTotalBruto1_AAD_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3131B</td> <td>Peso Total Bruto 1 (AAD)</td> <td>N(14,3)</td> <td>18</td> <td>209</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoTotalBruto1_AAD_(Double pesoTotalBruto1_AAD_) {
		this.pesoTotalBruto1_AAD_ = pesoTotalBruto1_AAD_;
	}

	/** 
	 * 
	 */ 
	public Double getPesoTotalBruto2() {
		return pesoTotalBruto2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3132B</td> <td>Peso Total Bruto 2</td> <td>N(14,3)</td> <td>18</td> <td>227</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoTotalBruto2(Double pesoTotalBruto2) {
		this.pesoTotalBruto2 = pesoTotalBruto2;
	}

	/** 
	 * V6321B - Código de Significación del peso Total Bruto
	 */ 
	public String getCod_SignificacionDeLaMedidaPesoTotalBruto() {
		return cod_SignificacionDeLaMedidaPesoTotalBruto;
	}

	/** 
	 * V6321B - Código de Significación del peso Total Bruto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6321B</td> <td>Cód. Significación de la medida Peso Total Bruto</td> <td>C</td> <td>6</td> <td>245</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCod_SignificacionDeLaMedidaPesoTotalBruto(String cod_SignificacionDeLaMedidaPesoTotalBruto) {
		this.cod_SignificacionDeLaMedidaPesoTotalBruto = cod_SignificacionDeLaMedidaPesoTotalBruto;
	}

	/** 
	 * V6411B - Unidad de medida del Peso Total Bruto
	 */ 
	public String getCodigoUnidadDeMedidaPesoTotalBruto() {
		return codigoUnidadDeMedidaPesoTotalBruto;
	}

	/** 
	 * V6411B - Unidad de medida del Peso Total Bruto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6411B</td> <td>Código Unidad de medida Peso Total Bruto</td> <td>C</td> <td>6</td> <td>251</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoUnidadDeMedidaPesoTotalBruto(String codigoUnidadDeMedidaPesoTotalBruto) {
		this.codigoUnidadDeMedidaPesoTotalBruto = codigoUnidadDeMedidaPesoTotalBruto;
	}

	/** 
	 * 
	 */ 
	public Double getAltura1_HT_() {
		return altura1_HT_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3131A</td> <td>Altura 1 (HT)</td> <td>N(14,3)</td> <td>18</td> <td>257</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setAltura1_HT_(Double altura1_HT_) {
		this.altura1_HT_ = altura1_HT_;
	}

	/** 
	 * 
	 */ 
	public Double getAltura2() {
		return altura2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3132A</td> <td>Altura 2</td> <td>N(14,3)</td> <td>18</td> <td>275</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setAltura2(Double altura2) {
		this.altura2 = altura2;
	}

	/** 
	 * V6321A - Código de Significación de la Altura
	 */ 
	public String getCodigoSignificacionDeLaMedidaAltura_3_4_() {
		return codigoSignificacionDeLaMedidaAltura_3_4_;
	}

	/** 
	 * V6321A - Código de Significación de la Altura
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6321A</td> <td>Código Significación de la medida Altura (3 , 4)</td> <td>C</td> <td>6</td> <td>293</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaAltura_3_4_(String codigoSignificacionDeLaMedidaAltura_3_4_) {
		this.codigoSignificacionDeLaMedidaAltura_3_4_ = codigoSignificacionDeLaMedidaAltura_3_4_;
	}

	/** 
	 * V6411A - Unidad de medida de la Altura
	 */ 
	public String getCodigoUnidadDeMedidaAltura() {
		return codigoUnidadDeMedidaAltura;
	}

	/** 
	 * V6411A - Unidad de medida de la Altura
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6411A</td> <td>Código Unidad de medida Altura</td> <td>C</td> <td>6</td> <td>299</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoUnidadDeMedidaAltura(String codigoUnidadDeMedidaAltura) {
		this.codigoUnidadDeMedidaAltura = codigoUnidadDeMedidaAltura;
	}

	/** 
	 * 
	 */ 
	public Double getAncho1_WD_() {
		return ancho1_WD_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3131W</td> <td>Ancho 1 (WD)</td> <td>N(14,3)</td> <td>18</td> <td>305</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setAncho1_WD_(Double ancho1_WD_) {
		this.ancho1_WD_ = ancho1_WD_;
	}

	/** 
	 * 
	 */ 
	public Double getAncho2() {
		return ancho2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3132W</td> <td>Ancho 2</td> <td>N(14,3)</td> <td>18</td> <td>323</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setAncho2(Double ancho2) {
		this.ancho2 = ancho2;
	}

	/** 
	 * V6321W- Código de Significación del  Ancho
	 */ 
	public String getCodigoSignificacionDeLaMedidaAncho_3_4_() {
		return codigoSignificacionDeLaMedidaAncho_3_4_;
	}

	/** 
	 * V6321W- Código de Significación del  Ancho
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6321W</td> <td>Código Significación de la medida Ancho (3 , 4)</td> <td>C</td> <td>6</td> <td>341</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaAncho_3_4_(String codigoSignificacionDeLaMedidaAncho_3_4_) {
		this.codigoSignificacionDeLaMedidaAncho_3_4_ = codigoSignificacionDeLaMedidaAncho_3_4_;
	}

	/** 
	 * V6411W - Unidad de medida del  Ancho
	 */ 
	public String getCodigoUnidadDeMedidaAncho() {
		return codigoUnidadDeMedidaAncho;
	}

	/** 
	 * V6411W - Unidad de medida del  Ancho
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6411W</td> <td>Código Unidad de medida Ancho</td> <td>C</td> <td>6</td> <td>347</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoUnidadDeMedidaAncho(String codigoUnidadDeMedidaAncho) {
		this.codigoUnidadDeMedidaAncho = codigoUnidadDeMedidaAncho;
	}

	/** 
	 * 
	 */ 
	public Double getLongitud1_AAC_() {
		return longitud1_AAC_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3131L</td> <td>Longitud 1 (AAC)</td> <td>N(14,3)</td> <td>18</td> <td>353</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setLongitud1_AAC_(Double longitud1_AAC_) {
		this.longitud1_AAC_ = longitud1_AAC_;
	}

	/** 
	 * 
	 */ 
	public Double getLongitud2_AAC_() {
		return longitud2_AAC_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3132L</td> <td>Longitud 2 (AAC)</td> <td>N(14,3)</td> <td>18</td> <td>371</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setLongitud2_AAC_(Double longitud2_AAC_) {
		this.longitud2_AAC_ = longitud2_AAC_;
	}

	/** 
	 * V6321L - Código de Significación de la Longitud: Estos campos corresponden a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCodigoSignificacionDeLaMedidaLongitud_3_4_() {
		return codigoSignificacionDeLaMedidaLongitud_3_4_;
	}

	/** 
	 * V6321L - Código de Significación de la Longitud: Estos campos corresponden a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6321L</td> <td>Código Significación de la medida Longitud(3 , 4)</td> <td>C</td> <td>6</td> <td>389</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoSignificacionDeLaMedidaLongitud_3_4_(String codigoSignificacionDeLaMedidaLongitud_3_4_) {
		this.codigoSignificacionDeLaMedidaLongitud_3_4_ = codigoSignificacionDeLaMedidaLongitud_3_4_;
	}

	/** 
	 * V6411L - Unidad de medida de la Longitud: Estos campos corresponden a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCodigoUnidadDeMedidaLongitud() {
		return codigoUnidadDeMedidaLongitud;
	}

	/** 
	 * V6411L - Unidad de medida de la Longitud: Estos campos corresponden a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6411L</td> <td>Código Unidad de medida Longitud</td> <td>C</td> <td>6</td> <td>395</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoUnidadDeMedidaLongitud(String codigoUnidadDeMedidaLongitud) {
		this.codigoUnidadDeMedidaLongitud = codigoUnidadDeMedidaLongitud;
	}

	/** 
	 * V6060M-Unidades de Material Consignado: Cantidad de unidades de Material Consignado por embalaje especificado en V7224B. Por ejemplo:	10 pallets.
	 */ 
	public Double getUnidadesDeMaterialConsignado() {
		return unidadesDeMaterialConsignado;
	}

	/** 
	 * V6060M-Unidades de Material Consignado: Cantidad de unidades de Material Consignado por embalaje especificado en V7224B. Por ejemplo:	10 pallets.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6060M</td> <td>Unidades de Material Consignado</td> <td>N(12,3)</td> <td>16</td> <td>401</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadesDeMaterialConsignado(Double unidadesDeMaterialConsignado) {
		this.unidadesDeMaterialConsignado = unidadesDeMaterialConsignado;
	}

	/** 
	 * V4079C-Calificador de Instrucciones de Manipulación: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCalificadorManipulacion() {
		return calificadorManipulacion;
	}

	/** 
	 * V4079C-Calificador de Instrucciones de Manipulación: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V4079C</td> <td>Calificador Manipulación</td> <td>C</td> <td>6</td> <td>417</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorManipulacion(String calificadorManipulacion) {
		this.calificadorManipulacion = calificadorManipulacion;
	}

	/** 
	 * 
	 */ 
	public String getInstruccionesDeManipulacion() {
		return instruccionesDeManipulacion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V4078D</td> <td>Instrucciones de Manipulación</td> <td>C</td> <td>70</td> <td>423</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setInstruccionesDeManipulacion(String instruccionesDeManipulacion) {
		this.instruccionesDeManipulacion = instruccionesDeManipulacion;
	}

	/** 
	 * 
	 */ 
	public String getMarcaOEtiquetaDeEmbarque1() {
		return marcaOEtiquetaDeEmbarque1;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V71021</td> <td>Marca o Etiqueta de embarque 1</td> <td>C</td> <td>35</td> <td>493</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaOEtiquetaDeEmbarque1(String marcaOEtiquetaDeEmbarque1) {
		this.marcaOEtiquetaDeEmbarque1 = marcaOEtiquetaDeEmbarque1;
	}

	/** 
	 * 
	 */ 
	public String getMarcaOEtiquetaDeEmbarque2() {
		return marcaOEtiquetaDeEmbarque2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V71022</td> <td>Marca o Etiqueta de embarque 2</td> <td>C</td> <td>35</td> <td>528</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaOEtiquetaDeEmbarque2(String marcaOEtiquetaDeEmbarque2) {
		this.marcaOEtiquetaDeEmbarque2 = marcaOEtiquetaDeEmbarque2;
	}

	/** 
	 * 
	 */ 
	public String getMarcaOEtiquetaDeEmbarque3() {
		return marcaOEtiquetaDeEmbarque3;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V71023</td> <td>Marca o Etiqueta de embarque 3</td> <td>C</td> <td>35</td> <td>563</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaOEtiquetaDeEmbarque3(String marcaOEtiquetaDeEmbarque3) {
		this.marcaOEtiquetaDeEmbarque3 = marcaOEtiquetaDeEmbarque3;
	}

	/** 
	 * 
	 */ 
	public String getMarcaOEtiquetaDeEmbarque4() {
		return marcaOEtiquetaDeEmbarque4;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V71024</td> <td>Marca o Etiqueta de embarque 4</td> <td>C</td> <td>35</td> <td>598</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaOEtiquetaDeEmbarque4(String marcaOEtiquetaDeEmbarque4) {
		this.marcaOEtiquetaDeEmbarque4 = marcaOEtiquetaDeEmbarque4;
	}

	/** 
	 * 
	 */ 
	public String getNumero1DeSeriadoORangoInferiorDelEmbalaje() {
		return numero1DeSeriadoORangoInferiorDelEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V74011</td> <td>Número 1 de Seriado o Rango inferior del embalaje</td> <td>C</td> <td>35</td> <td>633</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumero1DeSeriadoORangoInferiorDelEmbalaje(String numero1DeSeriadoORangoInferiorDelEmbalaje) {
		this.numero1DeSeriadoORangoInferiorDelEmbalaje = numero1DeSeriadoORangoInferiorDelEmbalaje;
	}

	/** 
	 * 
	 */ 
	public String getNumero1DeSeriadoORangoSuperiorDelEmbalaje() {
		return numero1DeSeriadoORangoSuperiorDelEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V74012</td> <td>Número 1 de Seriado o Rango superior del embalaje</td> <td>C</td> <td>35</td> <td>668</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumero1DeSeriadoORangoSuperiorDelEmbalaje(String numero1DeSeriadoORangoSuperiorDelEmbalaje) {
		this.numero1DeSeriadoORangoSuperiorDelEmbalaje = numero1DeSeriadoORangoSuperiorDelEmbalaje;
	}

	/** 
	 * 
	 */ 
	public String getNumero2DeSeriadoORangoInferiorDelEmbalaje() {
		return numero2DeSeriadoORangoInferiorDelEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V74021</td> <td>Número 2 de Seriado o Rango  inferior del embalaje</td> <td>C</td> <td>35</td> <td>703</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumero2DeSeriadoORangoInferiorDelEmbalaje(String numero2DeSeriadoORangoInferiorDelEmbalaje) {
		this.numero2DeSeriadoORangoInferiorDelEmbalaje = numero2DeSeriadoORangoInferiorDelEmbalaje;
	}

	/** 
	 * 
	 */ 
	public String getNumero2DeSeriadoORangoSuperiorDelEmbalaje() {
		return numero2DeSeriadoORangoSuperiorDelEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V74022</td> <td>Número 2 de Seriado o Rango superior del embalaje</td> <td>C</td> <td>35</td> <td>738</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumero2DeSeriadoORangoSuperiorDelEmbalaje(String numero2DeSeriadoORangoSuperiorDelEmbalaje) {
		this.numero2DeSeriadoORangoSuperiorDelEmbalaje = numero2DeSeriadoORangoSuperiorDelEmbalaje;
	}

	/** 
	 * 
	 */ 
	public String getNumero3DeSeriadoORangoInferiorDelEmbalaje() {
		return numero3DeSeriadoORangoInferiorDelEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V74031</td> <td>Número 3 de Seriado o Rango  inferior del embalaje</td> <td>C</td> <td>35</td> <td>773</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumero3DeSeriadoORangoInferiorDelEmbalaje(String numero3DeSeriadoORangoInferiorDelEmbalaje) {
		this.numero3DeSeriadoORangoInferiorDelEmbalaje = numero3DeSeriadoORangoInferiorDelEmbalaje;
	}

	/** 
	 * 
	 */ 
	public String getNumero3DeSeriadoORangoSuperiorDelEmbalaje() {
		return numero3DeSeriadoORangoSuperiorDelEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V74032</td> <td>Número 3 de Seriado o Rango superior del embalaje</td> <td>C</td> <td>35</td> <td>808</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumero3DeSeriadoORangoSuperiorDelEmbalaje(String numero3DeSeriadoORangoSuperiorDelEmbalaje) {
		this.numero3DeSeriadoORangoSuperiorDelEmbalaje = numero3DeSeriadoORangoSuperiorDelEmbalaje;
	}

	/** 
	 * V7233C-Calificador Codificación del Embalaje: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum V7233C {
		;
		
		private String value;
		
		private V7233C(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V7233C enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V7073T-Acuerdos y Condiciones de Embalajes: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum V7073T {
		;
		
		private String value;
		
		private V7073T(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V7073T enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V7065T-Tipo de Embalaje codificado: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum V7065T {
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
		
		private V7065T(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V7065T enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V8395P - Responsabilidad de pago del Transporte de embalajes Retornables: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum V8395P {
		PAGADO_POR_CLIENT_1("1"),
		GRATI_2("2"),
		PAGADO_POR_PROVEEDO_3("3"),
		;
		
		private String value;
		
		private V8395P(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V8395P enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V6321L - Código de Significación de la Longitud: Estos campos corresponden a un código EANCOM. Los valores posibles son:
	 */
	public enum V6321L {
		APROXIMADAMENT_3("3"),
		IGUAL_4("4"),
		;
		
		private String value;
		
		private V6321L(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V6321L enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V6411L - Unidad de medida de la Longitud: Estos campos corresponden a un código EANCOM. Los valores posibles son:
	 */
	public enum V6411L {
		CENTIMETRO_CMT("CMT"),
		KILOGRAMO_KGM("KGM"),
		;
		
		private String value;
		
		private V6411L(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V6411L enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V4079C-Calificador de Instrucciones de Manipulación: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum V4079C {
		COMESTIBLE_EAT("EAT"),
		CONTROL_DE_PESTE_PSC("PSC"),
		FRAGI_CRU("CRU"),
		TAMA_O_ESPECIA_BIG("BIG"),
		NO_APILA_UST("UST"),
		MANEJESE_CON_CUIDAD_HWC("HWC"),
		APLICACION_LIMITAD_STR("STR"),
		;
		
		private String value;
		
		private V4079C(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V4079C enumByValue(String value) {
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