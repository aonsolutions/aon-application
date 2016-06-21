package com.esferalia.aon.file.seres.udapa.delivery.data;

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
	private String numeroDeLineaArticulo;
	private String codigoDeArticuloEAN_13ODUN_14;
	private String descripcionDelArticulo;
	private String tipoArticuloEAN_CU_DU_;
	private String codigoInternoArticuloParaElProveedor_SA_;
	private String variablePromocional_PV_;
	private String codigoDUN_14_ADU_;
	private String codigoACU;
	private String numeroDeLote_NB_;
	private String numeroDeArticuloDelComprador_1__IN_;
	private String cantidadDeEnvio_12_;
	private String calificadorUnidadDeMedida;
	private String unidadesDeConsumoEnUnidadDeExpedicion;
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
	private String otrasCantidades;
	private String unidadDeMedidaCantidad;


	private static java.util.regex.Pattern PATTERN_SEH1L_lineas = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_tipoAvisoDeExpedicion_351_35E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroAvisoDeExpedicion = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroDeJerarquiaDeEmbalaje = java.util.regex.Pattern.compile("^.{63}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroDeSub_jerarquiaDeEmbalaje = java.util.regex.Pattern.compile("^.{75}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroDeLineaArticulo = java.util.regex.Pattern.compile("^.{87}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_codigoDeArticuloEAN_13ODUN_14 = java.util.regex.Pattern.compile("^.{93}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_descripcionDelArticulo = java.util.regex.Pattern.compile("^.{108}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_tipoArticuloEAN_CU_DU_ = java.util.regex.Pattern.compile("^.{178}(.{7}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_codigoInternoArticuloParaElProveedor_SA_ = java.util.regex.Pattern.compile("^.{185}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_variablePromocional_PV_ = java.util.regex.Pattern.compile("^.{200}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_codigoDUN_14_ADU_ = java.util.regex.Pattern.compile("^.{215}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_codigoACU = java.util.regex.Pattern.compile("^.{230}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroDeLote_NB_ = java.util.regex.Pattern.compile("^.{245}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroDeArticuloDelComprador_1__IN_ = java.util.regex.Pattern.compile("^.{280}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_cantidadDeEnvio_12_ = java.util.regex.Pattern.compile("^.{295}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_calificadorUnidadDeMedida = java.util.regex.Pattern.compile("^.{311}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_unidadesDeConsumoEnUnidadDeExpedicion = java.util.regex.Pattern.compile("^.{317}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_fechaDeExpiracion_36__102_203_ = java.util.regex.Pattern.compile("^.{333}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_calificadorReferencia1 = java.util.regex.Pattern.compile("^.{345}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroReferencia1 = java.util.regex.Pattern.compile("^.{351}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_fechaReferencia1_102_203_ = java.util.regex.Pattern.compile("^.{368}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_calificadorReferencia2 = java.util.regex.Pattern.compile("^.{380}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroReferencia2 = java.util.regex.Pattern.compile("^.{386}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_fechaReferencia2_102_203_ = java.util.regex.Pattern.compile("^.{403}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_calificadorReferencia3 = java.util.regex.Pattern.compile("^.{415}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_numeroReferencia3 = java.util.regex.Pattern.compile("^.{421}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_fechaReferencia3_102_203_ = java.util.regex.Pattern.compile("^.{438}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_unidadesEnAgrupacionSuperior_45E_ = java.util.regex.Pattern.compile("^.{450}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_codigoEANAdicional = java.util.regex.Pattern.compile("^.{466}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_cantidadSinCargo_192_ = java.util.regex.Pattern.compile("^.{481}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_calificadorDeCantidad = java.util.regex.Pattern.compile("^.{497}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_otrasCantidades = java.util.regex.Pattern.compile("^.{500}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SEH1L_unidadDeMedidaCantidad = java.util.regex.Pattern.compile("^.{516}(.{6}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SEH1L_lineas.matcher(value)).find()) {
			setLineas(m.group(1));
		}
		if((m = PATTERN_SEH1L_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find()) {
			setTipoAvisoDeExpedicion_351_35E_(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroAvisoDeExpedicion.matcher(value)).find()) {
			setNumeroAvisoDeExpedicion(m.group(1));
		}
		if((m = PATTERN_SEH1L_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_SEH1L_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroDeJerarquiaDeEmbalaje.matcher(value)).find()) {
			setNumeroDeJerarquiaDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroDeSub_jerarquiaDeEmbalaje.matcher(value)).find()) {
			setNumeroDeSub_jerarquiaDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroDeLineaArticulo.matcher(value)).find()) {
			setNumeroDeLineaArticulo(m.group(1));
		}
		if((m = PATTERN_SEH1L_codigoDeArticuloEAN_13ODUN_14.matcher(value)).find()) {
			setCodigoDeArticuloEAN_13ODUN_14(m.group(1));
		}
		if((m = PATTERN_SEH1L_descripcionDelArticulo.matcher(value)).find()) {
			setDescripcionDelArticulo(m.group(1));
		}
		if((m = PATTERN_SEH1L_tipoArticuloEAN_CU_DU_.matcher(value)).find()) {
			setTipoArticuloEAN_CU_DU_(m.group(1));
		}
		if((m = PATTERN_SEH1L_codigoInternoArticuloParaElProveedor_SA_.matcher(value)).find()) {
			setCodigoInternoArticuloParaElProveedor_SA_(m.group(1));
		}
		if((m = PATTERN_SEH1L_variablePromocional_PV_.matcher(value)).find()) {
			setVariablePromocional_PV_(m.group(1));
		}
		if((m = PATTERN_SEH1L_codigoDUN_14_ADU_.matcher(value)).find()) {
			setCodigoDUN_14_ADU_(m.group(1));
		}
		if((m = PATTERN_SEH1L_codigoACU.matcher(value)).find()) {
			setCodigoACU(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroDeLote_NB_.matcher(value)).find()) {
			setNumeroDeLote_NB_(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroDeArticuloDelComprador_1__IN_.matcher(value)).find()) {
			setNumeroDeArticuloDelComprador_1__IN_(m.group(1));
		}
		if((m = PATTERN_SEH1L_cantidadDeEnvio_12_.matcher(value)).find()) {
			setCantidadDeEnvio_12_(m.group(1));
		}
		if((m = PATTERN_SEH1L_calificadorUnidadDeMedida.matcher(value)).find()) {
			setCalificadorUnidadDeMedida(m.group(1));
		}
		if((m = PATTERN_SEH1L_unidadesDeConsumoEnUnidadDeExpedicion.matcher(value)).find()) {
			setUnidadesDeConsumoEnUnidadDeExpedicion(m.group(1));
		}
		if((m = PATTERN_SEH1L_fechaDeExpiracion_36__102_203_.matcher(value)).find()) {
			setFechaDeExpiracion_36__102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia1.matcher(value)).find()) {
			setCalificadorReferencia1(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroReferencia1.matcher(value)).find()) {
			setNumeroReferencia1(m.group(1));
		}
		if((m = PATTERN_SEH1L_fechaReferencia1_102_203_.matcher(value)).find()) {
			setFechaReferencia1_102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia2.matcher(value)).find()) {
			setCalificadorReferencia2(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroReferencia2.matcher(value)).find()) {
			setNumeroReferencia2(m.group(1));
		}
		if((m = PATTERN_SEH1L_fechaReferencia2_102_203_.matcher(value)).find()) {
			setFechaReferencia2_102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1L_calificadorReferencia3.matcher(value)).find()) {
			setCalificadorReferencia3(m.group(1));
		}
		if((m = PATTERN_SEH1L_numeroReferencia3.matcher(value)).find()) {
			setNumeroReferencia3(m.group(1));
		}
		if((m = PATTERN_SEH1L_fechaReferencia3_102_203_.matcher(value)).find()) {
			setFechaReferencia3_102_203_(m.group(1));
		}
		if((m = PATTERN_SEH1L_unidadesEnAgrupacionSuperior_45E_.matcher(value)).find()) {
			setUnidadesEnAgrupacionSuperior_45E_(m.group(1));
		}
		if((m = PATTERN_SEH1L_codigoEANAdicional.matcher(value)).find()) {
			setCodigoEANAdicional(m.group(1));
		}
		if((m = PATTERN_SEH1L_cantidadSinCargo_192_.matcher(value)).find()) {
			setCantidadSinCargo_192_(m.group(1));
		}
		if((m = PATTERN_SEH1L_calificadorDeCantidad.matcher(value)).find()) {
			setCalificadorDeCantidad(m.group(1));
		}
		if((m = PATTERN_SEH1L_otrasCantidades.matcher(value)).find()) {
			setOtrasCantidades(m.group(1));
		}
		if((m = PATTERN_SEH1L_unidadDeMedidaCantidad.matcher(value)).find()) {
			setUnidadDeMedidaCantidad(m.group(1));
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
	public String getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}
	public void setNumeroDeLineaArticulo(String numeroDeLineaArticulo) {
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
	public String getCantidadDeEnvio_12_() {
		return cantidadDeEnvio_12_;
	}
	public void setCantidadDeEnvio_12_(String cantidadDeEnvio_12_) {
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
	public String getUnidadesDeConsumoEnUnidadDeExpedicion() {
		return unidadesDeConsumoEnUnidadDeExpedicion;
	}
	public void setUnidadesDeConsumoEnUnidadDeExpedicion(String unidadesDeConsumoEnUnidadDeExpedicion) {
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
	public String getOtrasCantidades() {
		return otrasCantidades;
	}
	public void setOtrasCantidades(String otrasCantidades) {
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

	public enum TipoArticuloEAN_CU_DU_ {
		SI_EL_EAN_HACE_REFERENCIA_A_UNA_UNIDAD_DE_CONSUM_CU("CU"),
		SI_EL_EAN_HACE_REFERENCIA_A_UNA_UNIDAD_D_DU("DU"),
		;
		
		private String value;
		
		private TipoArticuloEAN_CU_DU_(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadorUnidadDeMedida {
		KILOGRAM_KGM("KGM"),
		;
		
		private String value;
		
		private CalificadorUnidadDeMedida(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum V11532_V11533CalificadoresDeReferenciasAplicablesALaLineaDeArticulo {
		N__DE_PEDIDO_ASIGNADO_POR_EL_COMPRADO_ON("ON"),
		N__DE_ALBARA_DQ("DQ"),
		;
		
		private String value;
		
		private V11532_V11533CalificadoresDeReferenciasAplicablesALaLineaDeArticulo(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}