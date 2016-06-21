package com.esferalia.aon.file.seres.udapa.invoice.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCL entity.
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
 * 		<td>SINCL</th>
 * 		<td>Líneas</th>
 * 		<td>Obligatorio</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SINCL {

	private String lineas;
	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private String numeroDeLinea;
	private String codigoArticulo;
	private String descripcionDelArticulo;
	private String tipoArticulo;
	private String codigoInternoArticuloProveedor_SA_;
	private String codigoInternoArticuloCliente_IN_;
	private String codigoVariablePromocional_PV_;
	private String codigoUnidadDeExpedicion_EN_;
	private String numeroDeLote_BN_;
	private String cantidadFacturada_47_;
	private String cantidadBonificada_15E_;
	private String unidadDeMedida;
	private String unidadesEntregadas;
	private String numeroUnidadesDeConsumoEnU_Expedicion_59_;
	private String importeTotalNetoDeLaLineaDeArticulo;
	private String precioBrutoUnitario;
	private String precioNetoUnitario;
	private String unidadDeMedidaDelPrecio;
	private String calificadorIVA_IGIG;
	private String porcentajeImpuestoIVA_IGIG;
	private String importeImpuestoIVA_IGIG;
	private String porcentajeRecargoDeEquivalencia;
	private String importeRecargoDeEquivalencia;
	private String calificadorOtroTipoDeImpuesto;
	private String porcentajeOtroTipoDeImpuesto;
	private String importeOtroTipoDeImpuesto;
	private String numeroPedido_ON_;
	private String numeroDeAlbaran_DQ_;
	private String numeroDeEmbalajes;
	private String tipoDeEmbalaje;
	private String importeTotalBrutoDeLaLineaDeArticulo_98_;


	private static java.util.regex.Pattern PATTERN_SINCL_lineas = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_tipoFactura_325_380_381_383_385_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_numeroDeFactura = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_codigoVendedor_SU_ = java.util.regex.Pattern.compile("^.{29}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_codigoComprador_BY_ = java.util.regex.Pattern.compile("^.{42}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_numeroDeLinea = java.util.regex.Pattern.compile("^.{55}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_codigoArticulo = java.util.regex.Pattern.compile("^.{61}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_descripcionDelArticulo = java.util.regex.Pattern.compile("^.{76}(.{35}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_tipoArticulo = java.util.regex.Pattern.compile("^.{111}(.{1}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_codigoInternoArticuloProveedor_SA_ = java.util.regex.Pattern.compile("^.{112}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_codigoInternoArticuloCliente_IN_ = java.util.regex.Pattern.compile("^.{127}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_codigoVariablePromocional_PV_ = java.util.regex.Pattern.compile("^.{142}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_codigoUnidadDeExpedicion_EN_ = java.util.regex.Pattern.compile("^.{157}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_numeroDeLote_BN_ = java.util.regex.Pattern.compile("^.{172}(.{15}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_cantidadFacturada_47_ = java.util.regex.Pattern.compile("^.{187}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_cantidadBonificada_15E_ = java.util.regex.Pattern.compile("^.{203}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_unidadDeMedida = java.util.regex.Pattern.compile("^.{219}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_unidadesEntregadas = java.util.regex.Pattern.compile("^.{225}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_numeroUnidadesDeConsumoEnU_Expedicion_59_ = java.util.regex.Pattern.compile("^.{241}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_importeTotalNetoDeLaLineaDeArticulo = java.util.regex.Pattern.compile("^.{257}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_precioBrutoUnitario = java.util.regex.Pattern.compile("^.{275}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_precioNetoUnitario = java.util.regex.Pattern.compile("^.{291}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_unidadDeMedidaDelPrecio = java.util.regex.Pattern.compile("^.{307}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_calificadorIVA_IGIG = java.util.regex.Pattern.compile("^.{313}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_porcentajeImpuestoIVA_IGIG = java.util.regex.Pattern.compile("^.{319}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_importeImpuestoIVA_IGIG = java.util.regex.Pattern.compile("^.{325}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_porcentajeRecargoDeEquivalencia = java.util.regex.Pattern.compile("^.{343}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_importeRecargoDeEquivalencia = java.util.regex.Pattern.compile("^.{349}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_calificadorOtroTipoDeImpuesto = java.util.regex.Pattern.compile("^.{367}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_porcentajeOtroTipoDeImpuesto = java.util.regex.Pattern.compile("^.{373}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_importeOtroTipoDeImpuesto = java.util.regex.Pattern.compile("^.{379}(.{18}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_numeroPedido_ON_ = java.util.regex.Pattern.compile("^.{397}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_numeroDeAlbaran_DQ_ = java.util.regex.Pattern.compile("^.{414}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_numeroDeEmbalajes = java.util.regex.Pattern.compile("^.{431}(.{8}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_tipoDeEmbalaje = java.util.regex.Pattern.compile("^.{439}(.{7}).*");
	private static java.util.regex.Pattern PATTERN_SINCL_importeTotalBrutoDeLaLineaDeArticulo_98_ = java.util.regex.Pattern.compile("^.{446}(.{18}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SINCL_lineas.matcher(value)).find()) {
			setLineas(m.group(1));
		}
		if((m = PATTERN_SINCL_tipoFactura_325_380_381_383_385_.matcher(value)).find()) {
			setTipoFactura_325_380_381_383_385_(m.group(1));
		}
		if((m = PATTERN_SINCL_numeroDeFactura.matcher(value)).find()) {
			setNumeroDeFactura(m.group(1));
		}
		if((m = PATTERN_SINCL_codigoVendedor_SU_.matcher(value)).find()) {
			setCodigoVendedor_SU_(m.group(1));
		}
		if((m = PATTERN_SINCL_codigoComprador_BY_.matcher(value)).find()) {
			setCodigoComprador_BY_(m.group(1));
		}
		if((m = PATTERN_SINCL_numeroDeLinea.matcher(value)).find()) {
			setNumeroDeLinea(m.group(1));
		}
		if((m = PATTERN_SINCL_codigoArticulo.matcher(value)).find()) {
			setCodigoArticulo(m.group(1));
		}
		if((m = PATTERN_SINCL_descripcionDelArticulo.matcher(value)).find()) {
			setDescripcionDelArticulo(m.group(1));
		}
		if((m = PATTERN_SINCL_tipoArticulo.matcher(value)).find()) {
			setTipoArticulo(m.group(1));
		}
		if((m = PATTERN_SINCL_codigoInternoArticuloProveedor_SA_.matcher(value)).find()) {
			setCodigoInternoArticuloProveedor_SA_(m.group(1));
		}
		if((m = PATTERN_SINCL_codigoInternoArticuloCliente_IN_.matcher(value)).find()) {
			setCodigoInternoArticuloCliente_IN_(m.group(1));
		}
		if((m = PATTERN_SINCL_codigoVariablePromocional_PV_.matcher(value)).find()) {
			setCodigoVariablePromocional_PV_(m.group(1));
		}
		if((m = PATTERN_SINCL_codigoUnidadDeExpedicion_EN_.matcher(value)).find()) {
			setCodigoUnidadDeExpedicion_EN_(m.group(1));
		}
		if((m = PATTERN_SINCL_numeroDeLote_BN_.matcher(value)).find()) {
			setNumeroDeLote_BN_(m.group(1));
		}
		if((m = PATTERN_SINCL_cantidadFacturada_47_.matcher(value)).find()) {
			setCantidadFacturada_47_(m.group(1));
		}
		if((m = PATTERN_SINCL_cantidadBonificada_15E_.matcher(value)).find()) {
			setCantidadBonificada_15E_(m.group(1));
		}
		if((m = PATTERN_SINCL_unidadDeMedida.matcher(value)).find()) {
			setUnidadDeMedida(m.group(1));
		}
		if((m = PATTERN_SINCL_unidadesEntregadas.matcher(value)).find()) {
			setUnidadesEntregadas(m.group(1));
		}
		if((m = PATTERN_SINCL_numeroUnidadesDeConsumoEnU_Expedicion_59_.matcher(value)).find()) {
			setNumeroUnidadesDeConsumoEnU_Expedicion_59_(m.group(1));
		}
		if((m = PATTERN_SINCL_importeTotalNetoDeLaLineaDeArticulo.matcher(value)).find()) {
			setImporteTotalNetoDeLaLineaDeArticulo(m.group(1));
		}
		if((m = PATTERN_SINCL_precioBrutoUnitario.matcher(value)).find()) {
			setPrecioBrutoUnitario(m.group(1));
		}
		if((m = PATTERN_SINCL_precioNetoUnitario.matcher(value)).find()) {
			setPrecioNetoUnitario(m.group(1));
		}
		if((m = PATTERN_SINCL_unidadDeMedidaDelPrecio.matcher(value)).find()) {
			setUnidadDeMedidaDelPrecio(m.group(1));
		}
		if((m = PATTERN_SINCL_calificadorIVA_IGIG.matcher(value)).find()) {
			setCalificadorIVA_IGIG(m.group(1));
		}
		if((m = PATTERN_SINCL_porcentajeImpuestoIVA_IGIG.matcher(value)).find()) {
			setPorcentajeImpuestoIVA_IGIG(m.group(1));
		}
		if((m = PATTERN_SINCL_importeImpuestoIVA_IGIG.matcher(value)).find()) {
			setImporteImpuestoIVA_IGIG(m.group(1));
		}
		if((m = PATTERN_SINCL_porcentajeRecargoDeEquivalencia.matcher(value)).find()) {
			setPorcentajeRecargoDeEquivalencia(m.group(1));
		}
		if((m = PATTERN_SINCL_importeRecargoDeEquivalencia.matcher(value)).find()) {
			setImporteRecargoDeEquivalencia(m.group(1));
		}
		if((m = PATTERN_SINCL_calificadorOtroTipoDeImpuesto.matcher(value)).find()) {
			setCalificadorOtroTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_SINCL_porcentajeOtroTipoDeImpuesto.matcher(value)).find()) {
			setPorcentajeOtroTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_SINCL_importeOtroTipoDeImpuesto.matcher(value)).find()) {
			setImporteOtroTipoDeImpuesto(m.group(1));
		}
		if((m = PATTERN_SINCL_numeroPedido_ON_.matcher(value)).find()) {
			setNumeroPedido_ON_(m.group(1));
		}
		if((m = PATTERN_SINCL_numeroDeAlbaran_DQ_.matcher(value)).find()) {
			setNumeroDeAlbaran_DQ_(m.group(1));
		}
		if((m = PATTERN_SINCL_numeroDeEmbalajes.matcher(value)).find()) {
			setNumeroDeEmbalajes(m.group(1));
		}
		if((m = PATTERN_SINCL_tipoDeEmbalaje.matcher(value)).find()) {
			setTipoDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SINCL_importeTotalBrutoDeLaLineaDeArticulo_98_.matcher(value)).find()) {
			setImporteTotalBrutoDeLaLineaDeArticulo_98_(m.group(1));
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
	 * 		<td>SINCL</th>
	 * 		<td>Lineas</th>
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
	 * 		<td>F3039V</th>
	 * 		<td>Código Vendedor (SU)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>30</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoVendedor_SU_() {
		return codigoVendedor_SU_;
	}
	public void setCodigoVendedor_SU_(String codigoVendedor_SU_) {
		this.codigoVendedor_SU_ = codigoVendedor_SU_;
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
	 * 		<td>F3039C</th>
	 * 		<td>Código Comprador (BY)</th>
	 * 		<td>C</th>
	 * 		<td>13</th>
	 * 		<td>43</th>
	 * 		<td>M</th>
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
	 * 		<td>F1082L</th>
	 * 		<td>Número de Línea</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>56</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeLinea() {
		return numeroDeLinea;
	}
	public void setNumeroDeLinea(String numeroDeLinea) {
		this.numeroDeLinea = numeroDeLinea;
	}

	/** 
	 * F7140A - Código Artículo: Código de artículo que se está facturando.
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
	 * 		<td>F7140A</th>
	 * 		<td>Código Artículo</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>62</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoArticulo() {
		return codigoArticulo;
	}
	public void setCodigoArticulo(String codigoArticulo) {
		this.codigoArticulo = codigoArticulo;
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
	 * 		<td>F7008A</th>
	 * 		<td>Descripción del Articulo</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>77</th>
	 * 		<td>M</th>
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
	 * F7081A - Tipo Artículo:
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
	 * 		<td>F7081A</th>
	 * 		<td>Tipo Articulo</th>
	 * 		<td>C</th>
	 * 		<td>1</th>
	 * 		<td>112</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoArticulo() {
		return tipoArticulo;
	}
	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
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
	 * 		<td>F7140P</th>
	 * 		<td>Código Interno Articulo Proveedor (SA)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>113</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoInternoArticuloProveedor_SA_() {
		return codigoInternoArticuloProveedor_SA_;
	}
	public void setCodigoInternoArticuloProveedor_SA_(String codigoInternoArticuloProveedor_SA_) {
		this.codigoInternoArticuloProveedor_SA_ = codigoInternoArticuloProveedor_SA_;
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
	 * 		<td>F7140C</th>
	 * 		<td>Código Interno Articulo Cliente (IN)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>128</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoInternoArticuloCliente_IN_() {
		return codigoInternoArticuloCliente_IN_;
	}
	public void setCodigoInternoArticuloCliente_IN_(String codigoInternoArticuloCliente_IN_) {
		this.codigoInternoArticuloCliente_IN_ = codigoInternoArticuloCliente_IN_;
	}

	/** 
	 * F7140V - Código Variable Promocional del Producto: Código que hace referencia a una promoción para la que no se ha generado un nuevo código de Barras. Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
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
	 * 		<td>F7140V</th>
	 * 		<td>Código Variable Promocional (PV)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>143</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoVariablePromocional_PV_() {
		return codigoVariablePromocional_PV_;
	}
	public void setCodigoVariablePromocional_PV_(String codigoVariablePromocional_PV_) {
		this.codigoVariablePromocional_PV_ = codigoVariablePromocional_PV_;
	}

	/** 
	 * F7140E - Código Unidad de Expedición asociada al producto: Código que hace referencia a la forma de distribución del producto (por ejem: cajas de unidades). Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
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
	 * 		<td>F7140E</th>
	 * 		<td>Código Unidad de Expedición (EN)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>158</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoUnidadDeExpedicion_EN_() {
		return codigoUnidadDeExpedicion_EN_;
	}
	public void setCodigoUnidadDeExpedicion_EN_(String codigoUnidadDeExpedicion_EN_) {
		this.codigoUnidadDeExpedicion_EN_ = codigoUnidadDeExpedicion_EN_;
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
	 * 		<td>F7140B</th>
	 * 		<td>Número de Lote (BN)</th>
	 * 		<td>C</th>
	 * 		<td>15</th>
	 * 		<td>173</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeLote_BN_() {
		return numeroDeLote_BN_;
	}
	public void setNumeroDeLote_BN_(String numeroDeLote_BN_) {
		this.numeroDeLote_BN_ = numeroDeLote_BN_;
	}

	/** 
	 * F6060C - Cantidad: Cantidad Facturada.. Si se trata de un artículo de media variable, indica el nº de unidades en kilos o litros facturados.
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
	 * 		<td>F6060C</th>
	 * 		<td>Cantidad Facturada (47)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>188</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCantidadFacturada_47_() {
		return cantidadFacturada_47_;
	}
	public void setCantidadFacturada_47_(String cantidadFacturada_47_) {
		this.cantidadFacturada_47_ = cantidadFacturada_47_;
	}

	/** 
	 * F6060B - Cantidad Bonificada: Solo se utilizará si la compra de un artículo se bonifica con un artículo distinto. Si se bonifica con el mismo artículo se utilizará el registro de descuentos SINCD donde se detallará la cantidad bonificada.
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
	 * 		<td>F6060B</th>
	 * 		<td>Cantidad Bonificada (15E)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>204</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCantidadBonificada_15E_() {
		return cantidadBonificada_15E_;
	}
	public void setCantidadBonificada_15E_(String cantidadBonificada_15E_) {
		this.cantidadBonificada_15E_ = cantidadBonificada_15E_;
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
	 * 		<td>F6411C</th>
	 * 		<td>Unidad de Medida</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>220</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getUnidadDeMedida() {
		return unidadDeMedida;
	}
	public void setUnidadDeMedida(String unidadDeMedida) {
		this.unidadDeMedida = unidadDeMedida;
	}

	/** 
	 * F6060E - Unidades Entregadas: Se usará solo para indicar en productos de peso variable, la cantidad de unidades entregadas.
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
	 * 		<td>F6060E</th>
	 * 		<td>Unidades Entregadas</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>226</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getUnidadesEntregadas() {
		return unidadesEntregadas;
	}
	public void setUnidadesEntregadas(String unidadesEntregadas) {
		this.unidadesEntregadas = unidadesEntregadas;
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
	 * 		<td>F6060U</th>
	 * 		<td>Número Unidades de Consumo en U. Expedición (59)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>242</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroUnidadesDeConsumoEnU_Expedicion_59_() {
		return numeroUnidadesDeConsumoEnU_Expedicion_59_;
	}
	public void setNumeroUnidadesDeConsumoEnU_Expedicion_59_(String numeroUnidadesDeConsumoEnU_Expedicion_59_) {
		this.numeroUnidadesDeConsumoEnU_Expedicion_59_ = numeroUnidadesDeConsumoEnU_Expedicion_59_;
	}

	/** 
	 * F5004L - Importe total Neto de la  línea de Artículo:  (Cantidad x Precio) - Descuentos.
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
	 * 		<td>F5004L</th>
	 * 		<td>Importe Total Neto de la Línea de Articulo</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>258</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTotalNetoDeLaLineaDeArticulo() {
		return importeTotalNetoDeLaLineaDeArticulo;
	}
	public void setImporteTotalNetoDeLaLineaDeArticulo(String importeTotalNetoDeLaLineaDeArticulo) {
		this.importeTotalNetoDeLaLineaDeArticulo = importeTotalNetoDeLaLineaDeArticulo;
	}

	/** 
	 * F5118B - Precio Bruto Unitario:  Excluye descuentos, cargos e impuestos.
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
	 * 		<td>F5118B</th>
	 * 		<td>Precio Bruto Unitario</th>
	 * 		<td>N(11,4)</th>
	 * 		<td>16</th>
	 * 		<td>276</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPrecioBrutoUnitario() {
		return precioBrutoUnitario;
	}
	public void setPrecioBrutoUnitario(String precioBrutoUnitario) {
		this.precioBrutoUnitario = precioBrutoUnitario;
	}

	/** 
	 * F5118N - Precio Neto Unitario:  Incluye descuentos y cargos pero no impuestos.
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
	 * 		<td>F5118N</th>
	 * 		<td>Precio Neto Unitario</th>
	 * 		<td>N(11,4)</th>
	 * 		<td>16</th>
	 * 		<td>292</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPrecioNetoUnitario() {
		return precioNetoUnitario;
	}
	public void setPrecioNetoUnitario(String precioNetoUnitario) {
		this.precioNetoUnitario = precioNetoUnitario;
	}

	/** 
	 * F6411P - Unidad de Medida del producto que se está facturando:  Solo se utilizará si el producto es de medida variable. El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>F6411P</th>
	 * 		<td>Unidad de Medida del Precio</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>308</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getUnidadDeMedidaDelPrecio() {
		return unidadDeMedidaDelPrecio;
	}
	public void setUnidadDeMedidaDelPrecio(String unidadDeMedidaDelPrecio) {
		this.unidadDeMedidaDelPrecio = unidadDeMedidaDelPrecio;
	}

	/** 
	 * F5153I - Calificador de IVA o IGIC: El campo corresponde a un código EANCOM. Los valores posibles son:
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
	 * 		<td>F5153I</th>
	 * 		<td>Calificador IVA/IGIG</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>314</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorIVA_IGIG() {
		return calificadorIVA_IGIG;
	}
	public void setCalificadorIVA_IGIG(String calificadorIVA_IGIG) {
		this.calificadorIVA_IGIG = calificadorIVA_IGIG;
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
	 * 		<td>F5278V</th>
	 * 		<td>% Impuesto IVA/IGIG</th>
	 * 		<td>N(3,2)</th>
	 * 		<td>6</th>
	 * 		<td>320</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPorcentajeImpuestoIVA_IGIG() {
		return porcentajeImpuestoIVA_IGIG;
	}
	public void setPorcentajeImpuestoIVA_IGIG(String porcentajeImpuestoIVA_IGIG) {
		this.porcentajeImpuestoIVA_IGIG = porcentajeImpuestoIVA_IGIG;
	}

	/** 
	 * F5004V - Importe del Impuesto: Se aconseja no cumplimentar el importe de IVA a nivel de línea con el fin de evitar descuadres con los totales de factura.
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
	 * 		<td>F5004V</th>
	 * 		<td>Importe Impuesto IVA/IGIG</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>326</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteImpuestoIVA_IGIG() {
		return importeImpuestoIVA_IGIG;
	}
	public void setImporteImpuestoIVA_IGIG(String importeImpuestoIVA_IGIG) {
		this.importeImpuestoIVA_IGIG = importeImpuestoIVA_IGIG;
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
	 * 		<td>F5278R</th>
	 * 		<td>% Recargo de Equivalencia</th>
	 * 		<td>N(3,2)</th>
	 * 		<td>6</th>
	 * 		<td>344</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPorcentajeRecargoDeEquivalencia() {
		return porcentajeRecargoDeEquivalencia;
	}
	public void setPorcentajeRecargoDeEquivalencia(String porcentajeRecargoDeEquivalencia) {
		this.porcentajeRecargoDeEquivalencia = porcentajeRecargoDeEquivalencia;
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
	 * 		<td>F5004R</th>
	 * 		<td>Importe Recargo de Equivalencia</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>350</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteRecargoDeEquivalencia() {
		return importeRecargoDeEquivalencia;
	}
	public void setImporteRecargoDeEquivalencia(String importeRecargoDeEquivalencia) {
		this.importeRecargoDeEquivalencia = importeRecargoDeEquivalencia;
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
	 * 		<td>F5153O</th>
	 * 		<td>Calificador Otro Tipo de Impuesto</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>368</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorOtroTipoDeImpuesto() {
		return calificadorOtroTipoDeImpuesto;
	}
	public void setCalificadorOtroTipoDeImpuesto(String calificadorOtroTipoDeImpuesto) {
		this.calificadorOtroTipoDeImpuesto = calificadorOtroTipoDeImpuesto;
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
	 * 		<td>F5278O</th>
	 * 		<td>% Otro Tipo de Impuesto</th>
	 * 		<td>N(3,2)</th>
	 * 		<td>6</th>
	 * 		<td>374</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPorcentajeOtroTipoDeImpuesto() {
		return porcentajeOtroTipoDeImpuesto;
	}
	public void setPorcentajeOtroTipoDeImpuesto(String porcentajeOtroTipoDeImpuesto) {
		this.porcentajeOtroTipoDeImpuesto = porcentajeOtroTipoDeImpuesto;
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
	 * 		<td>F5004O</th>
	 * 		<td>Importe Otro Tipo de Impuesto</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>380</th>
	 * 		<td>D</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteOtroTipoDeImpuesto() {
		return importeOtroTipoDeImpuesto;
	}
	public void setImporteOtroTipoDeImpuesto(String importeOtroTipoDeImpuesto) {
		this.importeOtroTipoDeImpuesto = importeOtroTipoDeImpuesto;
	}

	/** 
	 * F1154L - Número Pedido: Solo se utilizará para facturas con más de un pedido que no son recapitulativas. Indica el  número de pedido del articulo. En caso de un artículo con más de un pedido se indicará una línea de	factura por cada uno de sus pedidos.
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
	 * 		<td>F1154L</th>
	 * 		<td>Número Pedido (ON)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>398</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroPedido_ON_() {
		return numeroPedido_ON_;
	}
	public void setNumeroPedido_ON_(String numeroPedido_ON_) {
		this.numeroPedido_ON_ = numeroPedido_ON_;
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
	 * 		<td>F1154Q</th>
	 * 		<td>Número de Albarán (DQ)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>415</th>
	 * 		<td>C</th>
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
	 * 		<td>F7224P</th>
	 * 		<td>Número de Embalajes</th>
	 * 		<td>N</th>
	 * 		<td>8</th>
	 * 		<td>432</th>
	 * 		<td>N</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeEmbalajes() {
		return numeroDeEmbalajes;
	}
	public void setNumeroDeEmbalajes(String numeroDeEmbalajes) {
		this.numeroDeEmbalajes = numeroDeEmbalajes;
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
	 * 		<td>F7065P</th>
	 * 		<td>Tipo de Embalaje</th>
	 * 		<td>C</th>
	 * 		<td>7</th>
	 * 		<td>440</th>
	 * 		<td>N</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDeEmbalaje() {
		return tipoDeEmbalaje;
	}
	public void setTipoDeEmbalaje(String tipoDeEmbalaje) {
		this.tipoDeEmbalaje = tipoDeEmbalaje;
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
	 * 		<td>F5004J</th>
	 * 		<td>Importe Total Bruto de la Línea de Articulo (98)</th>
	 * 		<td>N(14,3)</th>
	 * 		<td>18</th>
	 * 		<td>447</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getImporteTotalBrutoDeLaLineaDeArticulo_98_() {
		return importeTotalBrutoDeLaLineaDeArticulo_98_;
	}
	public void setImporteTotalBrutoDeLaLineaDeArticulo_98_(String importeTotalBrutoDeLaLineaDeArticulo_98_) {
		this.importeTotalBrutoDeLaLineaDeArticulo_98_ = importeTotalBrutoDeLaLineaDeArticulo_98_;
	}

	public enum UnidadDeMedidaDelProductoQueSeEstaFacturando {
		KILOGRAM_KGM("KGM"),
		LITR_LTR("LTR"),
		;
		
		private String value;
		
		private UnidadDeMedidaDelProductoQueSeEstaFacturando(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum UnidadDeMedidaDelPrecio {
		KILOGRAM_KGM("KGM"),
		LITR_LTR("LTR"),
		;
		
		private String value;
		
		private UnidadDeMedidaDelPrecio(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadorDeIVAOIGIC {
		IV_VAT("VAT"),
		IGI_IGI("IGI"),
		;
		
		private String value;
		
		private CalificadorDeIVAOIGIC(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}