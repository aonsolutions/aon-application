package com.esferalia.aon.file.seres.udapa.delivery.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1L entity.
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
 * 		<td>SEH1L</th>
 * 		<td>Líneas</th>
 * 		<td>Obligatorio</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SEH1L {

	private String lineas;
	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeJerarquiaDeEmbalaje;
	private String numeroDeSub_jerarquiaDeEmbalaje;
	private Integer numeroDeLineaArticulo;
	private String codigoDeArticuloEAN_13ODUN_14;
	private String descripcionDelArticulo;
	private String tipoArticuloEAN_CU_DU_;
	private String codigoInternoArticuloParaElProveedor_SA_;
	private String variablePromocional_PV_;
	private String codigoDUN_14_ADU_;
	private String codigoACU;
	private String numeroDeLote_NB_;
	private String numeroDeArticuloDelComprador_1__IN_;
	private Double cantidadDeEnvio_12_;
	private String calificadorUnidadDeMedida;
	private Double unidadesDeConsumoEnUnidadDeExpedicion;
	private String fechaDeExpiracion_36__102_203_;
	private String calificadorReferencia1;
	private String numeroReferencia1;
	private String fechaReferencia1_102_203_;
	private String calificadorReferencia2;
	private String numeroReferencia2;
	private String fechaReferencia2_102_203_;
	private String calificadorReferencia3;
	private String numeroReferencia3;
	private String fechaReferencia3_102_203_;
	private String unidadesEnAgrupacionSuperior_45E_;
	private String codigoEANAdicional;
	private String cantidadSinCargo_192_;
	private String calificadorDeCantidad;
	private Double otrasCantidades;
	private String unidadDeMedidaCantidad;


	private static Pattern PATTERN_SEH1L_lineas = Pattern.compile("^(.{6}).*");
	private static Pattern PATTERN_SEH1L_tipoAvisoDeExpedicion_351_35E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroAvisoDeExpedicion = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SEH1L_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_SEH1L_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_SEH1L_numeroDeJerarquiaDeEmbalaje = Pattern.compile("^.{63}(.{12}).*");
	private static Pattern PATTERN_SEH1L_numeroDeSub_jerarquiaDeEmbalaje = Pattern.compile("^.{75}(.{12}).*");
	private static Pattern PATTERN_SEH1L_numeroDeLineaArticulo = Pattern.compile("^.{87}(.{6}).*");
	private static Pattern PATTERN_SEH1L_codigoDeArticuloEAN_13ODUN_14 = Pattern.compile("^.{93}(.{15}).*");
	private static Pattern PATTERN_SEH1L_descripcionDelArticulo = Pattern.compile("^.{108}(.{70}).*");
	private static Pattern PATTERN_SEH1L_tipoArticuloEAN_CU_DU_ = Pattern.compile("^.{178}(.{7}).*");
	private static Pattern PATTERN_SEH1L_codigoInternoArticuloParaElProveedor_SA_ = Pattern.compile("^.{185}(.{15}).*");
	private static Pattern PATTERN_SEH1L_variablePromocional_PV_ = Pattern.compile("^.{200}(.{15}).*");
	private static Pattern PATTERN_SEH1L_codigoDUN_14_ADU_ = Pattern.compile("^.{215}(.{15}).*");
	private static Pattern PATTERN_SEH1L_codigoACU = Pattern.compile("^.{230}(.{15}).*");
	private static Pattern PATTERN_SEH1L_numeroDeLote_NB_ = Pattern.compile("^.{245}(.{35}).*");
	private static Pattern PATTERN_SEH1L_numeroDeArticuloDelComprador_1__IN_ = Pattern.compile("^.{280}(.{15}).*");
	private static Pattern PATTERN_SEH1L_cantidadDeEnvio_12_ = Pattern.compile("^.{295}(.{16}).*");
	private static Pattern PATTERN_SEH1L_calificadorUnidadDeMedida = Pattern.compile("^.{311}(.{6}).*");
	private static Pattern PATTERN_SEH1L_unidadesDeConsumoEnUnidadDeExpedicion = Pattern.compile("^.{317}(.{16}).*");
	private static Pattern PATTERN_SEH1L_fechaDeExpiracion_36__102_203_ = Pattern.compile("^.{333}(.{12}).*");
	private static Pattern PATTERN_SEH1L_calificadorReferencia1 = Pattern.compile("^.{345}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroReferencia1 = Pattern.compile("^.{351}(.{17}).*");
	private static Pattern PATTERN_SEH1L_fechaReferencia1_102_203_ = Pattern.compile("^.{368}(.{12}).*");
	private static Pattern PATTERN_SEH1L_calificadorReferencia2 = Pattern.compile("^.{380}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroReferencia2 = Pattern.compile("^.{386}(.{17}).*");
	private static Pattern PATTERN_SEH1L_fechaReferencia2_102_203_ = Pattern.compile("^.{403}(.{12}).*");
	private static Pattern PATTERN_SEH1L_calificadorReferencia3 = Pattern.compile("^.{415}(.{6}).*");
	private static Pattern PATTERN_SEH1L_numeroReferencia3 = Pattern.compile("^.{421}(.{17}).*");
	private static Pattern PATTERN_SEH1L_fechaReferencia3_102_203_ = Pattern.compile("^.{438}(.{12}).*");
	private static Pattern PATTERN_SEH1L_unidadesEnAgrupacionSuperior_45E_ = Pattern.compile("^.{450}(.{16}).*");
	private static Pattern PATTERN_SEH1L_codigoEANAdicional = Pattern.compile("^.{466}(.{15}).*");
	private static Pattern PATTERN_SEH1L_cantidadSinCargo_192_ = Pattern.compile("^.{481}(.{16}).*");
	private static Pattern PATTERN_SEH1L_calificadorDeCantidad = Pattern.compile("^.{497}(.{3}).*");
	private static Pattern PATTERN_SEH1L_otrasCantidades = Pattern.compile("^.{500}(.{16}).*");
	private static Pattern PATTERN_SEH1L_unidadDeMedidaCantidad = Pattern.compile("^.{516}(.{6}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1L_lineas.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setLineas(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoAvisoDeExpedicion_351_35E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroAvisoDeExpedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroAvisoDeExpedicion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeJerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeJerarquiaDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeSub_jerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeSub_jerarquiaDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeLineaArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoDeArticuloEAN_13ODUN_14.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDeArticuloEAN_13ODUN_14(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_descripcionDelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_tipoArticuloEAN_CU_DU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoArticuloEAN_CU_DU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoInternoArticuloParaElProveedor_SA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInternoArticuloParaElProveedor_SA_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_variablePromocional_PV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setVariablePromocional_PV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoDUN_14_ADU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDUN_14_ADU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoACU.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoACU(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeLote_NB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLote_NB_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroDeArticuloDelComprador_1__IN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeArticuloDelComprador_1__IN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_cantidadDeEnvio_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadDeEnvio_12_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorUnidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadesDeConsumoEnUnidadDeExpedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadesDeConsumoEnUnidadDeExpedicion(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fechaDeExpiracion_36__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeExpiracion_36__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroReferencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroReferencia1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fechaReferencia1_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaReferencia1_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroReferencia2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fechaReferencia2_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaReferencia2_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_numeroReferencia3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroReferencia3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_fechaReferencia3_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaReferencia3_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadesEnAgrupacionSuperior_45E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadesEnAgrupacionSuperior_45E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_codigoEANAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEANAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_cantidadSinCargo_192_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadSinCargo_192_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_calificadorDeCantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDeCantidad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_otrasCantidades.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setOtrasCantidades(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1L_unidadDeMedidaCantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaCantidad(String.valueOf(m.group(1).trim()));
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
	 * 		<td>SEH1L</th>
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
	 * 		<td>V7164J</th>
	 * 		<td>Número de Jerarquía de Embalaje</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>64</th>
	 * 		<td>C</th>
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
	 * 		<td>V7166J</th>
	 * 		<td>Número de Sub-jerarquía de Embalaje</th>
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
	 * 		<td>V1082L</th>
	 * 		<td>Número de Línea Artículo</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>88</th>
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
	 * 		<td>V7140E</th>
	 * 		<td>Código de Artículo EAN-13 o DUN-14</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>94</th>
	 * 		<td>D</th>
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
	 * 		<td>V7008D</th>
	 * 		<td>Descripción del Artículo</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>109</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDescripcionDelArticulo() {
		return descripcionDelArticulo;
	}
	public void setDescripcionDelArticulo(String descripcionDelArticulo) {
		this.descripcionDelArticulo = descripcionDelArticulo;
	}

	/** 
	 * V7009E-Tipo Artículo EAN (CU/DU): Los valores posibles son:
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
	 * 		<td>V7009E</th>
	 * 		<td>Tipo Artículo EAN (CU/DU)</th>
	 * 		<td>C</th>
	 * 		<td>7</th>
	 * 		<td>179</th>
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
	 * 		<td>V7140P</th>
	 * 		<td>Código Interno Artículo para el Proveedor  (SA)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>186</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoInternoArticuloParaElProveedor_SA_() {
		return codigoInternoArticuloParaElProveedor_SA_;
	}
	public void setCodigoInternoArticuloParaElProveedor_SA_(String codigoInternoArticuloParaElProveedor_SA_) {
		this.codigoInternoArticuloParaElProveedor_SA_ = codigoInternoArticuloParaElProveedor_SA_;
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
	 * 		<td>V7140V</th>
	 * 		<td>Variable Promocional  (PV)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>201</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getVariablePromocional_PV_() {
		return variablePromocional_PV_;
	}
	public void setVariablePromocional_PV_(String variablePromocional_PV_) {
		this.variablePromocional_PV_ = variablePromocional_PV_;
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
	 * 		<td>V7140D</th>
	 * 		<td>Código DUN-14  (ADU)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>216</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoDUN_14_ADU_() {
		return codigoDUN_14_ADU_;
	}
	public void setCodigoDUN_14_ADU_(String codigoDUN_14_ADU_) {
		this.codigoDUN_14_ADU_ = codigoDUN_14_ADU_;
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
	 * 		<td>V7140A</th>
	 * 		<td>Código  ACU</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>231</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoACU() {
		return codigoACU;
	}
	public void setCodigoACU(String codigoACU) {
		this.codigoACU = codigoACU;
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
	 * 		<td>V7140L</th>
	 * 		<td>Número de Lote  (NB)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>246</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeLote_NB_() {
		return numeroDeLote_NB_;
	}
	public void setNumeroDeLote_NB_(String numeroDeLote_NB_) {
		this.numeroDeLote_NB_ = numeroDeLote_NB_;
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
	 * 		<td>V7140C</th>
	 * 		<td>Número de Artículo del Comprador (1) (IN)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>281</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeArticuloDelComprador_1__IN_() {
		return numeroDeArticuloDelComprador_1__IN_;
	}
	public void setNumeroDeArticuloDelComprador_1__IN_(String numeroDeArticuloDelComprador_1__IN_) {
		this.numeroDeArticuloDelComprador_1__IN_ = numeroDeArticuloDelComprador_1__IN_;
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
	 * 		<td>V6060C</th>
	 * 		<td>Cantidad de Envío (12)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>296</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getCantidadDeEnvio_12_() {
		return cantidadDeEnvio_12_;
	}
	public void setCantidadDeEnvio_12_(Double cantidadDeEnvio_12_) {
		this.cantidadDeEnvio_12_ = cantidadDeEnvio_12_;
	}

	/** 
	 * V6411U-Calificador Unidad de Medida: Solo se utiliza si el producto que se está identificando es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V6411U</th>
	 * 		<td>Calificador Unidad de Medida</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>312</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorUnidadDeMedida() {
		return calificadorUnidadDeMedida;
	}
	public void setCalificadorUnidadDeMedida(String calificadorUnidadDeMedida) {
		this.calificadorUnidadDeMedida = calificadorUnidadDeMedida;
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
	 * 		<td>V6060U</th>
	 * 		<td>Unidades de Consumo en Unidad de Expedición</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>318</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getUnidadesDeConsumoEnUnidadDeExpedicion() {
		return unidadesDeConsumoEnUnidadDeExpedicion;
	}
	public void setUnidadesDeConsumoEnUnidadDeExpedicion(Double unidadesDeConsumoEnUnidadDeExpedicion) {
		this.unidadesDeConsumoEnUnidadDeExpedicion = unidadesDeConsumoEnUnidadDeExpedicion;
	}

	/** 
	 * V2380E-Fecha de Expiración (36) (102/203): Se utiliza para especificar las fechas/horas de caducidad de los productos individuales expedidos. Su formato, AAAAMMDD o AAAAMMDDHHMM
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
	 * 		<td>V2380E</th>
	 * 		<td>Fecha de Expiración (36) (102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>334</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeExpiracion_36__102_203_() {
		return fechaDeExpiracion_36__102_203_;
	}
	public void setFechaDeExpiracion_36__102_203_(String fechaDeExpiracion_36__102_203_) {
		this.fechaDeExpiracion_36__102_203_ = fechaDeExpiracion_36__102_203_;
	}

	/** 
	 * V11531-V11532-V11533 Calificadores de referencias aplicables a la línea de artículo: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V11531</th>
	 * 		<td>Calificador Referencia 1</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>346</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorReferencia1() {
		return calificadorReferencia1;
	}
	public void setCalificadorReferencia1(String calificadorReferencia1) {
		this.calificadorReferencia1 = calificadorReferencia1;
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
	 * 		<td>V11541</th>
	 * 		<td>Número Referencia 1</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>352</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroReferencia1() {
		return numeroReferencia1;
	}
	public void setNumeroReferencia1(String numeroReferencia1) {
		this.numeroReferencia1 = numeroReferencia1;
	}

	/** 
	 * V23801-V23802-V23803 Fechas Referencia: Se utiliza para especificar las fechas/horas relacionadas con las referencias especificadas. Su formato, AAAAMMDD o AAAAMMDDHHMM.
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
	 * 		<td>V23801</th>
	 * 		<td>Fecha Referencia 1(102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>369</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaReferencia1_102_203_() {
		return fechaReferencia1_102_203_;
	}
	public void setFechaReferencia1_102_203_(String fechaReferencia1_102_203_) {
		this.fechaReferencia1_102_203_ = fechaReferencia1_102_203_;
	}

	/** 
	 * V11531-V11532-V11533 Calificadores de referencias aplicables a la línea de artículo: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>V11532</th>
	 * 		<td>Calificador Referencia 2</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>381</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorReferencia2() {
		return calificadorReferencia2;
	}
	public void setCalificadorReferencia2(String calificadorReferencia2) {
		this.calificadorReferencia2 = calificadorReferencia2;
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
	 * 		<td>V11542</th>
	 * 		<td>Número Referencia 2</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>387</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroReferencia2() {
		return numeroReferencia2;
	}
	public void setNumeroReferencia2(String numeroReferencia2) {
		this.numeroReferencia2 = numeroReferencia2;
	}

	/** 
	 * V23801-V23802-V23803 Fechas Referencia: Se utiliza para especificar las fechas/horas relacionadas con las referencias especificadas. Su formato, AAAAMMDD o AAAAMMDDHHMM.
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
	 * 		<td>V23802</th>
	 * 		<td>Fecha Referencia 2(102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>404</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaReferencia2_102_203_() {
		return fechaReferencia2_102_203_;
	}
	public void setFechaReferencia2_102_203_(String fechaReferencia2_102_203_) {
		this.fechaReferencia2_102_203_ = fechaReferencia2_102_203_;
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
	 * 		<td>V11533</th>
	 * 		<td>Calificador Referencia 3</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>416</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorReferencia3() {
		return calificadorReferencia3;
	}
	public void setCalificadorReferencia3(String calificadorReferencia3) {
		this.calificadorReferencia3 = calificadorReferencia3;
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
	 * 		<td>V11543</th>
	 * 		<td>Número Referencia 3</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>422</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroReferencia3() {
		return numeroReferencia3;
	}
	public void setNumeroReferencia3(String numeroReferencia3) {
		this.numeroReferencia3 = numeroReferencia3;
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
	 * 		<td>V23803</th>
	 * 		<td>Fecha Referencia 3(102/203)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>439</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaReferencia3_102_203_() {
		return fechaReferencia3_102_203_;
	}
	public void setFechaReferencia3_102_203_(String fechaReferencia3_102_203_) {
		this.fechaReferencia3_102_203_ = fechaReferencia3_102_203_;
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
	 * 		<td>V6060G</th>
	 * 		<td>Unidades en Agrupación Superior (45E)</th>
	 * 		<td>C</th>
	 * 		<td>16</th>
	 * 		<td>451</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getUnidadesEnAgrupacionSuperior_45E_() {
		return unidadesEnAgrupacionSuperior_45E_;
	}
	public void setUnidadesEnAgrupacionSuperior_45E_(String unidadesEnAgrupacionSuperior_45E_) {
		this.unidadesEnAgrupacionSuperior_45E_ = unidadesEnAgrupacionSuperior_45E_;
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
	 * 		<td>V7140N</th>
	 * 		<td>Código EAN adicional</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>467</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoEANAdicional() {
		return codigoEANAdicional;
	}
	public void setCodigoEANAdicional(String codigoEANAdicional) {
		this.codigoEANAdicional = codigoEANAdicional;
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
	 * 		<td>V6060B</th>
	 * 		<td>Cantidad sin Cargo (192)</th>
	 * 		<td>C</th>
	 * 		<td>16</th>
	 * 		<td>482</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCantidadSinCargo_192_() {
		return cantidadSinCargo_192_;
	}
	public void setCantidadSinCargo_192_(String cantidadSinCargo_192_) {
		this.cantidadSinCargo_192_ = cantidadSinCargo_192_;
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
	 * 		<td>V6063A</th>
	 * 		<td>Calificador  de Cantidad</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>498</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorDeCantidad() {
		return calificadorDeCantidad;
	}
	public void setCalificadorDeCantidad(String calificadorDeCantidad) {
		this.calificadorDeCantidad = calificadorDeCantidad;
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
	 * 		<td>V6060A</th>
	 * 		<td>Otras Cantidades</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>501</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public Double getOtrasCantidades() {
		return otrasCantidades;
	}
	public void setOtrasCantidades(Double otrasCantidades) {
		this.otrasCantidades = otrasCantidades;
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
	 * 		<td>V6411O</th>
	 * 		<td>Unidad de Medida Cantidad</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>517</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getUnidadDeMedidaCantidad() {
		return unidadDeMedidaCantidad;
	}
	public void setUnidadDeMedidaCantidad(String unidadDeMedidaCantidad) {
		this.unidadDeMedidaCantidad = unidadDeMedidaCantidad;
	}

	/** 
	 * V7009E-Tipo Artículo EAN (CU/DU): Los valores posibles son:
	 */
	public enum V7009E {
		SI_EL_EAN_HACE_REFERENCIA_A_UNA_UNIDAD_DE_CONSUM_CU("CU"),
		SI_EL_EAN_HACE_REFERENCIA_A_UNA_UNIDAD_D_DU("DU"),
		;
		
		private String value;
		
		private V7009E(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V7009E enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V6411U-Calificador Unidad de Medida: Solo se utiliza si el producto que se está identificando es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum V6411U {
		KILOGRAM_KGM("KGM"),
		;
		
		private String value;
		
		private V6411U(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V6411U enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V11531-V11532-V11533 Calificadores de referencias aplicables a la línea de artículo: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum V11531 {
		N__DE_PEDIDO_ASIGNADO_POR_EL_COMPRADO_ON("ON"),
		N__DE_ALBARA_DQ("DQ"),
		;
		
		private String value;
		
		private V11531(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V11531 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}