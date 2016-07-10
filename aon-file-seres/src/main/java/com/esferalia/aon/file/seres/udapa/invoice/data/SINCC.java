package com.esferalia.aon.file.seres.udapa.invoice.data;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCC entity.
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
 * 		 <td>SINCC</td> <td>Cabecera</td> <td>Obligatorio</td> <td>1</td>
 * 	</tr>
 * </table>
 */ 

public class SINCC {

	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_aQuienSePide__SU_;
	private String codigoComprador_QuienPide__BY_;
	private String funcionDelMensaje_7_31_5_;
	private Integer fechaFactura;
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
	private String nifDelReceptorDeLaFactura;
	private String nombre_NumeroDeLaCalleDelEmisorDeLaFactura;
	private String poblacionDelEmisorDeLaFactura;
	private String codigoPostalDelEmisorDeLaFactura;
	private String codigoDeMoneda;
	private Integer fechaVencimientoUnico;
	private Double importeNetoTotalFactura_79_;
	private Double baseImponible_125_;
	private Double importeBrutoTotalFactura_98_;
	private Double importeTotalDeImpuestos_176_;
	private Double importeTotalAPagar_139_;
	private Double subvencionesVinculadasAlPrecio_80A_;
	private Double totalIncrementosDelImporteBruto_259_;
	private Double totalMinoracionesDelImporteBruto_260_;
	private String identificacionAdicionalDeLaParte_API_;
	private String receptorDelDocumento;
	private String identificacionAdicionalProveedor_API__NAD_SU_;


	public List<SINCT> sinctList;
	public List<SINCV> sincvList;
	public List<SINCD> sincdList;
	public List<SINCL> sinclList;
	public List<SINCU> sincuList;
	public List<SINCE> sinceList;
	public List<SINCI> sinciList;


	private static Pattern PATTERN_SINCC_tipoFactura_325_380_381_383_385_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCC_numeroDeFactura = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SINCC_codigoVendedor_aQuienSePide__SU_ = Pattern.compile("^.{29}(.{13}).*");
	private static Pattern PATTERN_SINCC_codigoComprador_QuienPide__BY_ = Pattern.compile("^.{42}(.{13}).*");
	private static Pattern PATTERN_SINCC_funcionDelMensaje_7_31_5_ = Pattern.compile("^.{55}(.{6}).*");
	private static Pattern PATTERN_SINCC_fechaFactura = Pattern.compile("^.{61}(.{8}).*");
	private static Pattern PATTERN_SINCC_periodoDeFacturacion = Pattern.compile("^.{69}(.{16}).*");
	private static Pattern PATTERN_SINCC_formaDePago = Pattern.compile("^.{85}(.{6}).*");
	private static Pattern PATTERN_SINCC_codigoEmisorDeLaFactura_QuienFactura__II_ = Pattern.compile("^.{91}(.{13}).*");
	private static Pattern PATTERN_SINCC_codigoReceptorDeLaFactura_aQuienSeFactura_ = Pattern.compile("^.{104}(.{13}).*");
	private static Pattern PATTERN_SINCC_codigoReceptorDeLasMercancias_QuienRecibe_ = Pattern.compile("^.{117}(.{13}).*");
	private static Pattern PATTERN_SINCC_codigoReceptorDelPago_aQuienSePaga_ = Pattern.compile("^.{130}(.{13}).*");
	private static Pattern PATTERN_SINCC_codigoEmisorDelPago_QuienPaga_ = Pattern.compile("^.{143}(.{13}).*");
	private static Pattern PATTERN_SINCC_razonDelCargoODelAbono = Pattern.compile("^.{156}(.{6}).*");
	private static Pattern PATTERN_SINCC_numeroDePedido_ON_ = Pattern.compile("^.{162}(.{17}).*");
	private static Pattern PATTERN_SINCC_numeroDeAlbaran_DQ_ = Pattern.compile("^.{179}(.{17}).*");
	private static Pattern PATTERN_SINCC_calificadorDocumentoRectificado_Sustituido = Pattern.compile("^.{196}(.{3}).*");
	private static Pattern PATTERN_SINCC_numeroDocumentoRectificado_Sustituido = Pattern.compile("^.{199}(.{17}).*");
	private static Pattern PATTERN_SINCC_numeroDeContrato_Acuerdo_CT_ = Pattern.compile("^.{216}(.{17}).*");
	private static Pattern PATTERN_SINCC_numeroDeRelacionDeEntregas_REN_ = Pattern.compile("^.{233}(.{17}).*");
	private static Pattern PATTERN_SINCC_razonSocialReceptorDeLaFactura = Pattern.compile("^.{250}(.{70}).*");
	private static Pattern PATTERN_SINCC_nombre_NumeroDeLaCalleDelReceptorDeLaFactura = Pattern.compile("^.{320}(.{70}).*");
	private static Pattern PATTERN_SINCC_poblacionDelReceptorDeLaFactura = Pattern.compile("^.{390}(.{35}).*");
	private static Pattern PATTERN_SINCC_codigoPostalDelReceptorDeLaFactura = Pattern.compile("^.{425}(.{9}).*");
	private static Pattern PATTERN_SINCC_nifDelReceptorDeLaFactura = Pattern.compile("^.{434}(.{17}).*");
	private static Pattern PATTERN_SINCC_nombre_NumeroDeLaCalleDelEmisorDeLaFactura = Pattern.compile("^.{451}(.{70}).*");
	private static Pattern PATTERN_SINCC_poblacionDelEmisorDeLaFactura = Pattern.compile("^.{521}(.{35}).*");
	private static Pattern PATTERN_SINCC_codigoPostalDelEmisorDeLaFactura = Pattern.compile("^.{556}(.{9}).*");
	private static Pattern PATTERN_SINCC_codigoDeMoneda = Pattern.compile("^.{565}(.{6}).*");
	private static Pattern PATTERN_SINCC_fechaVencimientoUnico = Pattern.compile("^.{571}(.{8}).*");
	private static Pattern PATTERN_SINCC_importeNetoTotalFactura_79_ = Pattern.compile("^.{579}(.{18}).*");
	private static Pattern PATTERN_SINCC_baseImponible_125_ = Pattern.compile("^.{597}(.{18}).*");
	private static Pattern PATTERN_SINCC_importeBrutoTotalFactura_98_ = Pattern.compile("^.{615}(.{18}).*");
	private static Pattern PATTERN_SINCC_importeTotalDeImpuestos_176_ = Pattern.compile("^.{633}(.{18}).*");
	private static Pattern PATTERN_SINCC_importeTotalAPagar_139_ = Pattern.compile("^.{651}(.{18}).*");
	private static Pattern PATTERN_SINCC_subvencionesVinculadasAlPrecio_80A_ = Pattern.compile("^.{669}(.{18}).*");
	private static Pattern PATTERN_SINCC_totalIncrementosDelImporteBruto_259_ = Pattern.compile("^.{687}(.{18}).*");
	private static Pattern PATTERN_SINCC_totalMinoracionesDelImporteBruto_260_ = Pattern.compile("^.{705}(.{18}).*");
	private static Pattern PATTERN_SINCC_identificacionAdicionalDeLaParte_API_ = Pattern.compile("^.{723}(.{17}).*");
	private static Pattern PATTERN_SINCC_receptorDelDocumento = Pattern.compile("^.{740}(.{13}).*");
	private static Pattern PATTERN_SINCC_identificacionAdicionalProveedor_API__NAD_SU_ = Pattern.compile("^.{753}(.{17}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCC_tipoFactura_325_380_381_383_385_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoFactura_325_380_381_383_385_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoVendedor_aQuienSePide__SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVendedor_aQuienSePide__SU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoComprador_QuienPide__BY_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoComprador_QuienPide__BY_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_funcionDelMensaje_7_31_5_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDelMensaje_7_31_5_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_fechaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaFactura(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_periodoDeFacturacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPeriodoDeFacturacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_formaDePago.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFormaDePago(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoEmisorDeLaFactura_QuienFactura__II_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisorDeLaFactura_QuienFactura__II_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoReceptorDeLaFactura_aQuienSeFactura_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptorDeLaFactura_aQuienSeFactura_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoReceptorDeLasMercancias_QuienRecibe_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptorDeLasMercancias_QuienRecibe_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoReceptorDelPago_aQuienSePaga_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptorDelPago_aQuienSePaga_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoEmisorDelPago_QuienPaga_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisorDelPago_QuienPaga_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_razonDelCargoODelAbono.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setRazonDelCargoODelAbono(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDePedido_ON_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido_ON_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDeAlbaran_DQ_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeAlbaran_DQ_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_calificadorDocumentoRectificado_Sustituido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDocumentoRectificado_Sustituido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDocumentoRectificado_Sustituido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDocumentoRectificado_Sustituido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDeContrato_Acuerdo_CT_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeContrato_Acuerdo_CT_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDeRelacionDeEntregas_REN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeRelacionDeEntregas_REN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_razonSocialReceptorDeLaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setRazonSocialReceptorDeLaFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_nombre_NumeroDeLaCalleDelReceptorDeLaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre_NumeroDeLaCalleDelReceptorDeLaFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_poblacionDelReceptorDeLaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPoblacionDelReceptorDeLaFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoPostalDelReceptorDeLaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPostalDelReceptorDeLaFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_nifDelReceptorDeLaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNifDelReceptorDeLaFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_nombre_NumeroDeLaCalleDelEmisorDeLaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre_NumeroDeLaCalleDelEmisorDeLaFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_poblacionDelEmisorDeLaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPoblacionDelEmisorDeLaFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoPostalDelEmisorDeLaFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPostalDelEmisorDeLaFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoDeMoneda.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDeMoneda(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_fechaVencimientoUnico.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaVencimientoUnico(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_importeNetoTotalFactura_79_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteNetoTotalFactura_79_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_baseImponible_125_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBaseImponible_125_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_importeBrutoTotalFactura_98_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteBrutoTotalFactura_98_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_importeTotalDeImpuestos_176_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalDeImpuestos_176_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_importeTotalAPagar_139_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalAPagar_139_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_subvencionesVinculadasAlPrecio_80A_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setSubvencionesVinculadasAlPrecio_80A_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_totalIncrementosDelImporteBruto_259_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTotalIncrementosDelImporteBruto_259_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_totalMinoracionesDelImporteBruto_260_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTotalMinoracionesDelImporteBruto_260_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_identificacionAdicionalDeLaParte_API_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificacionAdicionalDeLaParte_API_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_receptorDelDocumento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setReceptorDelDocumento(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_identificacionAdicionalProveedor_API__NAD_SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificacionAdicionalProveedor_API__NAD_SU_(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * F1001T - Tipo Factura: Existe un código para identificar cada tipo de factura que queramos enviar. Las más habituales son las Facturas Comerciales(tipo 380) y los Abonos (tipo 381). El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getTipoFactura_325_380_381_383_385_() {
		return tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * F1001T - Tipo Factura: Existe un código para identificar cada tipo de factura que queramos enviar. Las más habituales son las Facturas Comerciales(tipo 380) y los Abonos (tipo 381). El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1001T</td> <td>Tipo Factura (325, 380, 381, 383, 385)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoFactura_325_380_381_383_385_(String tipoFactura_325_380_381_383_385_) {
		this.tipoFactura_325_380_381_383_385_ = tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * F1004N - Número de Factura: Se cumplimentará con el número de factura o abono correspondiente
	 */ 
	public String getNumeroDeFactura() {
		return numeroDeFactura;
	}

	/** 
	 * F1004N - Número de Factura: Se cumplimentará con el número de factura o abono correspondiente
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1004N</td> <td>Número de Factura</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeFactura(String numeroDeFactura) {
		this.numeroDeFactura = numeroDeFactura;
	}

	/** 
	 * F3039V - Código Vendedor: Departamento al que se pide la mercancía.
	 */ 
	public String getCodigoVendedor_aQuienSePide__SU_() {
		return codigoVendedor_aQuienSePide__SU_;
	}

	/** 
	 * F3039V - Código Vendedor: Departamento al que se pide la mercancía.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039V</td> <td>Código Vendedor (a Quien se Pide) (SU)</td> <td>C</td> <td>13</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoVendedor_aQuienSePide__SU_(String codigoVendedor_aQuienSePide__SU_) {
		this.codigoVendedor_aQuienSePide__SU_ = codigoVendedor_aQuienSePide__SU_;
	}

	/** 
	 * F3039C - Código Comprador: Código EDI del Cliente que hace el Pedido
	 */ 
	public String getCodigoComprador_QuienPide__BY_() {
		return codigoComprador_QuienPide__BY_;
	}

	/** 
	 * F3039C - Código Comprador: Código EDI del Cliente que hace el Pedido
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039C</td> <td>Código Comprador (Quien Pide) (BY)</td> <td>C</td> <td>13</td> <td>43</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoComprador_QuienPide__BY_(String codigoComprador_QuienPide__BY_) {
		this.codigoComprador_QuienPide__BY_ = codigoComprador_QuienPide__BY_;
	}

	/** 
	 * 
	 */ 
	public String getFuncionDelMensaje_7_31_5_() {
		return funcionDelMensaje_7_31_5_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1225F</td> <td>Función del Mensaje (7, 31, 5)</td> <td>C</td> <td>6</td> <td>56</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFuncionDelMensaje_7_31_5_(String funcionDelMensaje_7_31_5_) {
		this.funcionDelMensaje_7_31_5_ = funcionDelMensaje_7_31_5_;
	}

	/** 
	 * F2380F - Fecha de Factura: Fecha de generación del Documento en formato AAAAMMDD
	 */ 
	public Integer getFechaFactura() {
		return fechaFactura;
	}

	/** 
	 * F2380F - Fecha de Factura: Fecha de generación del Documento en formato AAAAMMDD
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F2380F</td> <td>Fecha Factura</td> <td>N</td> <td>8</td> <td>62</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaFactura(Integer fechaFactura) {
		this.fechaFactura = fechaFactura;
	}

	/** 
	 * F2380P - Periodo de Facturación: Periodo de la factura en formato AAAAMMDDAAAAMMDD
	 */ 
	public String getPeriodoDeFacturacion() {
		return periodoDeFacturacion;
	}

	/** 
	 * F2380P - Periodo de Facturación: Periodo de la factura en formato AAAAMMDDAAAAMMDD
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F2380P</td> <td>Periodo de Facturación</td> <td>C</td> <td>16</td> <td>70</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPeriodoDeFacturacion(String periodoDeFacturacion) {
		this.periodoDeFacturacion = periodoDeFacturacion;
	}

	/** 
	 * F4461P - Forma de Pago: Permite al interlocutor que emite la factura, especificar cómo debe realizarse el pago de la misma. Valores posibles:
	 */ 
	public String getFormaDePago() {
		return formaDePago;
	}

	/** 
	 * F4461P - Forma de Pago: Permite al interlocutor que emite la factura, especificar cómo debe realizarse el pago de la misma. Valores posibles:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F4461P</td> <td>Forma de Pago</td> <td>C</td> <td>6</td> <td>86</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFormaDePago(String formaDePago) {
		this.formaDePago = formaDePago;
	}

	/** 
	 * F3039E - Código Emisor de la Factura: Departamento que factura (puede no coincidir con el código de vendedor).
	 */ 
	public String getCodigoEmisorDeLaFactura_QuienFactura__II_() {
		return codigoEmisorDeLaFactura_QuienFactura__II_;
	}

	/** 
	 * F3039E - Código Emisor de la Factura: Departamento que factura (puede no coincidir con el código de vendedor).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039E</td> <td>Código Emisor de la Factura (Quien Factura) (II)</td> <td>C</td> <td>13</td> <td>92</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEmisorDeLaFactura_QuienFactura__II_(String codigoEmisorDeLaFactura_QuienFactura__II_) {
		this.codigoEmisorDeLaFactura_QuienFactura__II_ = codigoEmisorDeLaFactura_QuienFactura__II_;
	}

	/** 
	 * F3039R - Código Receptor de la Factura: Código EDI  del Cliente al que se envía la  factura (puede no coincidir con el comprador)
	 */ 
	public String getCodigoReceptorDeLaFactura_aQuienSeFactura_() {
		return codigoReceptorDeLaFactura_aQuienSeFactura_;
	}

	/** 
	 * F3039R - Código Receptor de la Factura: Código EDI  del Cliente al que se envía la  factura (puede no coincidir con el comprador)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039R</td> <td>Código Receptor de la Factura (a Quien se Factura)</td> <td>C</td> <td>13</td> <td>105</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptorDeLaFactura_aQuienSeFactura_(String codigoReceptorDeLaFactura_aQuienSeFactura_) {
		this.codigoReceptorDeLaFactura_aQuienSeFactura_ = codigoReceptorDeLaFactura_aQuienSeFactura_;
	}

	/** 
	 * F3039A - Código Receptor de la Mercancía: El campo corresponde a un código a un Punto Operacional EDI.
	 */ 
	public String getCodigoReceptorDeLasMercancias_QuienRecibe_() {
		return codigoReceptorDeLasMercancias_QuienRecibe_;
	}

	/** 
	 * F3039A - Código Receptor de la Mercancía: El campo corresponde a un código a un Punto Operacional EDI.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039A</td> <td>Código Receptor de las Mercancías (Quien Recibe)</td> <td>C</td> <td>13</td> <td>118</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptorDeLasMercancias_QuienRecibe_(String codigoReceptorDeLasMercancias_QuienRecibe_) {
		this.codigoReceptorDeLasMercancias_QuienRecibe_ = codigoReceptorDeLasMercancias_QuienRecibe_;
	}

	/** 
	 * F3039P - Código Receptor del Pago: Departamento a quien se paga. El campo corresponde a un Punto Operacional EDI.
	 */ 
	public String getCodigoReceptorDelPago_aQuienSePaga_() {
		return codigoReceptorDelPago_aQuienSePaga_;
	}

	/** 
	 * F3039P - Código Receptor del Pago: Departamento a quien se paga. El campo corresponde a un Punto Operacional EDI.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039P</td> <td>Código Receptor del Pago (a Quien se Paga)</td> <td>C</td> <td>13</td> <td>131</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptorDelPago_aQuienSePaga_(String codigoReceptorDelPago_aQuienSePaga_) {
		this.codigoReceptorDelPago_aQuienSePaga_ = codigoReceptorDelPago_aQuienSePaga_;
	}

	/** 
	 * F3039Q - Código Emisor del Pago: Código interno del Cliente que paga la factura. El campo corresponde a un Punto Operacional EDI.
	 */ 
	public String getCodigoEmisorDelPago_QuienPaga_() {
		return codigoEmisorDelPago_QuienPaga_;
	}

	/** 
	 * F3039Q - Código Emisor del Pago: Código interno del Cliente que paga la factura. El campo corresponde a un Punto Operacional EDI.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039Q</td> <td>Código Emisor del Pago (Quien Paga)</td> <td>C</td> <td>13</td> <td>144</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEmisorDelPago_QuienPaga_(String codigoEmisorDelPago_QuienPaga_) {
		this.codigoEmisorDelPago_QuienPaga_ = codigoEmisorDelPago_QuienPaga_;
	}

	/** 
	 * F4183R - Razón del Cargo o del Abono: Solo se enviará si el tipo de Factura es 381(abonos) o 383 (notas de cargo). El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getRazonDelCargoODelAbono() {
		return razonDelCargoODelAbono;
	}

	/** 
	 * F4183R - Razón del Cargo o del Abono: Solo se enviará si el tipo de Factura es 381(abonos) o 383 (notas de cargo). El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F4183R</td> <td>Razón del Cargo o del Abono</td> <td>C</td> <td>6</td> <td>157</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setRazonDelCargoODelAbono(String razonDelCargoODelAbono) {
		this.razonDelCargoODelAbono = razonDelCargoODelAbono;
	}

	/** 
	 * F1154P - Número de Pedido: Será obligatorio si el Tipo de Factura es 380 y no es una entrega directa en tienda.
	 */ 
	public String getNumeroDePedido_ON_() {
		return numeroDePedido_ON_;
	}

	/** 
	 * F1154P - Número de Pedido: Será obligatorio si el Tipo de Factura es 380 y no es una entrega directa en tienda.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154P</td> <td>Número de Pedido (ON)</td> <td>C</td> <td>17</td> <td>163</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePedido_ON_(String numeroDePedido_ON_) {
		this.numeroDePedido_ON_ = numeroDePedido_ON_;
	}

	/** 
	 * F1154A - Número de Albarán: Será obligatorio si el Tipo de Factura es 380 y la factura es de mercancías. O si la factura es 381 o 383 y la razón es por devolución de mercancías.
	 */ 
	public String getNumeroDeAlbaran_DQ_() {
		return numeroDeAlbaran_DQ_;
	}

	/** 
	 * F1154A - Número de Albarán: Será obligatorio si el Tipo de Factura es 380 y la factura es de mercancías. O si la factura es 381 o 383 y la razón es por devolución de mercancías.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154A</td> <td>Número de Albarán (DQ)</td> <td>C</td> <td>17</td> <td>180</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeAlbaran_DQ_(String numeroDeAlbaran_DQ_) {
		this.numeroDeAlbaran_DQ_ = numeroDeAlbaran_DQ_;
	}

	/** 
	 * F1153F - Calificador Documento Rectificado: Será obligatorio si el Tipo de Factura es 381 o 383. El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCalificadorDocumentoRectificado_Sustituido() {
		return calificadorDocumentoRectificado_Sustituido;
	}

	/** 
	 * F1153F - Calificador Documento Rectificado: Será obligatorio si el Tipo de Factura es 381 o 383. El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1153F</td> <td>Calificador Documento Rectificado / Sustituido</td> <td>C</td> <td>3</td> <td>197</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDocumentoRectificado_Sustituido(String calificadorDocumentoRectificado_Sustituido) {
		this.calificadorDocumentoRectificado_Sustituido = calificadorDocumentoRectificado_Sustituido;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDocumentoRectificado_Sustituido() {
		return numeroDocumentoRectificado_Sustituido;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154F</td> <td>Número Documento Rectificado / Sustituido</td> <td>C</td> <td>17</td> <td>200</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDocumentoRectificado_Sustituido(String numeroDocumentoRectificado_Sustituido) {
		this.numeroDocumentoRectificado_Sustituido = numeroDocumentoRectificado_Sustituido;
	}

	/** 
	 * F1154C - Número de Contrato: Será obligatorio si el Tipo de Factura es 380 y la factura es de servicios.
	 */ 
	public String getNumeroDeContrato_Acuerdo_CT_() {
		return numeroDeContrato_Acuerdo_CT_;
	}

	/** 
	 * F1154C - Número de Contrato: Será obligatorio si el Tipo de Factura es 380 y la factura es de servicios.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154C</td> <td>Número de Contrato/Acuerdo (CT)</td> <td>C</td> <td>17</td> <td>217</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeContrato_Acuerdo_CT_(String numeroDeContrato_Acuerdo_CT_) {
		this.numeroDeContrato_Acuerdo_CT_ = numeroDeContrato_Acuerdo_CT_;
	}

	/** 
	 * F1154R - Número Relación de Entregas: Será obligatorio si el Tipo de Factura es 385 (Fact.Recapitulativa)
	 */ 
	public String getNumeroDeRelacionDeEntregas_REN_() {
		return numeroDeRelacionDeEntregas_REN_;
	}

	/** 
	 * F1154R - Número Relación de Entregas: Será obligatorio si el Tipo de Factura es 385 (Fact.Recapitulativa)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154R</td> <td>Número de Relación de Entregas (REN)</td> <td>C</td> <td>17</td> <td>234</td> <td>D</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeRelacionDeEntregas_REN_(String numeroDeRelacionDeEntregas_REN_) {
		this.numeroDeRelacionDeEntregas_REN_ = numeroDeRelacionDeEntregas_REN_;
	}

	/** 
	 * 
	 */ 
	public String getRazonSocialReceptorDeLaFactura() {
		return razonSocialReceptorDeLaFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3036R</td> <td>Razón social Receptor de la Factura</td> <td>C</td> <td>70</td> <td>251</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setRazonSocialReceptorDeLaFactura(String razonSocialReceptorDeLaFactura) {
		this.razonSocialReceptorDeLaFactura = razonSocialReceptorDeLaFactura;
	}

	/** 
	 * 
	 */ 
	public String getNombre_NumeroDeLaCalleDelReceptorDeLaFactura() {
		return nombre_NumeroDeLaCalleDelReceptorDeLaFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3042D</td> <td>Nombre/Número de la calle del Receptor de la Factura</td> <td>C</td> <td>70</td> <td>321</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombre_NumeroDeLaCalleDelReceptorDeLaFactura(String nombre_NumeroDeLaCalleDelReceptorDeLaFactura) {
		this.nombre_NumeroDeLaCalleDelReceptorDeLaFactura = nombre_NumeroDeLaCalleDelReceptorDeLaFactura;
	}

	/** 
	 * 
	 */ 
	public String getPoblacionDelReceptorDeLaFactura() {
		return poblacionDelReceptorDeLaFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3164P</td> <td>Población del Receptor de la Factura</td> <td>C</td> <td>35</td> <td>391</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPoblacionDelReceptorDeLaFactura(String poblacionDelReceptorDeLaFactura) {
		this.poblacionDelReceptorDeLaFactura = poblacionDelReceptorDeLaFactura;
	}

	/** 
	 * 
	 */ 
	public String getCodigoPostalDelReceptorDeLaFactura() {
		return codigoPostalDelReceptorDeLaFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3251P</td> <td>Código Postal del Receptor de la Factura</td> <td>C</td> <td>9</td> <td>426</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoPostalDelReceptorDeLaFactura(String codigoPostalDelReceptorDeLaFactura) {
		this.codigoPostalDelReceptorDeLaFactura = codigoPostalDelReceptorDeLaFactura;
	}

	/** 
	 * 
	 */ 
	public String getNifDelReceptorDeLaFactura() {
		return nifDelReceptorDeLaFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154N</td> <td>NIF del Receptor de la Factura</td> <td>C</td> <td>17</td> <td>435</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNifDelReceptorDeLaFactura(String nifDelReceptorDeLaFactura) {
		this.nifDelReceptorDeLaFactura = nifDelReceptorDeLaFactura;
	}

	/** 
	 * 
	 */ 
	public String getNombre_NumeroDeLaCalleDelEmisorDeLaFactura() {
		return nombre_NumeroDeLaCalleDelEmisorDeLaFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3042E</td> <td>Nombre/Número de la calle del Emisor de la Factura</td> <td>C</td> <td>70</td> <td>452</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombre_NumeroDeLaCalleDelEmisorDeLaFactura(String nombre_NumeroDeLaCalleDelEmisorDeLaFactura) {
		this.nombre_NumeroDeLaCalleDelEmisorDeLaFactura = nombre_NumeroDeLaCalleDelEmisorDeLaFactura;
	}

	/** 
	 * 
	 */ 
	public String getPoblacionDelEmisorDeLaFactura() {
		return poblacionDelEmisorDeLaFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3164E</td> <td>Población del Emisor de la Factura</td> <td>C</td> <td>35</td> <td>522</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPoblacionDelEmisorDeLaFactura(String poblacionDelEmisorDeLaFactura) {
		this.poblacionDelEmisorDeLaFactura = poblacionDelEmisorDeLaFactura;
	}

	/** 
	 * 
	 */ 
	public String getCodigoPostalDelEmisorDeLaFactura() {
		return codigoPostalDelEmisorDeLaFactura;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3251E</td> <td>Código Postal del Emisor de la Factura</td> <td>C</td> <td>9</td> <td>557</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoPostalDelEmisorDeLaFactura(String codigoPostalDelEmisorDeLaFactura) {
		this.codigoPostalDelEmisorDeLaFactura = codigoPostalDelEmisorDeLaFactura;
	}

	/** 
	 * 
	 */ 
	public String getCodigoDeMoneda() {
		return codigoDeMoneda;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F6345M</td> <td>Código de Moneda</td> <td>C</td> <td>6</td> <td>566</td> <td>N</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDeMoneda(String codigoDeMoneda) {
		this.codigoDeMoneda = codigoDeMoneda;
	}

	/** 
	 * F2380V - Fecha vencimiento: Se indicará si la factura es de Pago único, en caso contrario se dejará a cero y los vencimientos se indicarán en el registro SINCV. Debe montarse en formato AAAAMMDD
	 */ 
	public Integer getFechaVencimientoUnico() {
		return fechaVencimientoUnico;
	}

	/** 
	 * F2380V - Fecha vencimiento: Se indicará si la factura es de Pago único, en caso contrario se dejará a cero y los vencimientos se indicarán en el registro SINCV. Debe montarse en formato AAAAMMDD
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F2380V</td> <td>Fecha vencimiento único</td> <td>N</td> <td>8</td> <td>572</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaVencimientoUnico(Integer fechaVencimientoUnico) {
		this.fechaVencimientoUnico = fechaVencimientoUnico;
	}

	/** 
	 * F5004N - Importe Neto Total Factura: Corresponde al sumatorio de los importes netos por línea
	 */ 
	public Double getImporteNetoTotalFactura_79_() {
		return importeNetoTotalFactura_79_;
	}

	/** 
	 * F5004N - Importe Neto Total Factura: Corresponde al sumatorio de los importes netos por línea
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004N</td> <td>Importe Neto Total Factura (79)</td> <td>N(14,3)</td> <td>18</td> <td>580</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteNetoTotalFactura_79_(Double importeNetoTotalFactura_79_) {
		this.importeNetoTotalFactura_79_ = importeNetoTotalFactura_79_;
	}

	/** 
	 * F5004B - Base Imponible: Importe Neto Total  de Factura (F500N) + Total cargos y descuentos Gobales (F5004D)
	 */ 
	public Double getBaseImponible_125_() {
		return baseImponible_125_;
	}

	/** 
	 * F5004B - Base Imponible: Importe Neto Total  de Factura (F500N) + Total cargos y descuentos Gobales (F5004D)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004B</td> <td>Base Imponible (125)</td> <td>N(14,3)</td> <td>18</td> <td>598</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setBaseImponible_125_(Double baseImponible_125_) {
		this.baseImponible_125_ = baseImponible_125_;
	}

	/** 
	 * F5004D - Importe Bruto: Sumatorio de los importes Brutos de las lineas (cantidad facturada x precio unitario Bruto). No se tienen en cuenta Cargos ni Descuentos tanto a nivel de líneas como globales.
	 */ 
	public Double getImporteBrutoTotalFactura_98_() {
		return importeBrutoTotalFactura_98_;
	}

	/** 
	 * F5004D - Importe Bruto: Sumatorio de los importes Brutos de las lineas (cantidad facturada x precio unitario Bruto). No se tienen en cuenta Cargos ni Descuentos tanto a nivel de líneas como globales.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004D</td> <td>Importe Bruto Total Factura (98)</td> <td>N(14,3)</td> <td>18</td> <td>616</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteBrutoTotalFactura_98_(Double importeBrutoTotalFactura_98_) {
		this.importeBrutoTotalFactura_98_ = importeBrutoTotalFactura_98_;
	}

	/** 
	 * F5004I - Importe Total de Impuestos: Sumatorio de los importes  de impuestos por línea.
	 */ 
	public Double getImporteTotalDeImpuestos_176_() {
		return importeTotalDeImpuestos_176_;
	}

	/** 
	 * F5004I - Importe Total de Impuestos: Sumatorio de los importes  de impuestos por línea.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004I</td> <td>Importe Total de Impuestos (176)</td> <td>N(14,3)</td> <td>18</td> <td>634</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalDeImpuestos_176_(Double importeTotalDeImpuestos_176_) {
		this.importeTotalDeImpuestos_176_ = importeTotalDeImpuestos_176_;
	}

	/** 
	 * F5004P - Importe Total a Pagar: Base Imponible + Importe Total de Impuestos
	 */ 
	public Double getImporteTotalAPagar_139_() {
		return importeTotalAPagar_139_;
	}

	/** 
	 * F5004P - Importe Total a Pagar: Base Imponible + Importe Total de Impuestos
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004P</td> <td>Importe Total a Pagar (139)</td> <td>N(14,3)</td> <td>18</td> <td>652</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalAPagar_139_(Double importeTotalAPagar_139_) {
		this.importeTotalAPagar_139_ = importeTotalAPagar_139_;
	}

	/** 
	 * F5004S - Subvenciones vinculadas al precio: Las subvenciones vinculadas al precio deben formar parte de la base imponible para calcular el IVA aunque no estén reflejadas en el importe total a pagar.
	 */ 
	public Double getSubvencionesVinculadasAlPrecio_80A_() {
		return subvencionesVinculadasAlPrecio_80A_;
	}

	/** 
	 * F5004S - Subvenciones vinculadas al precio: Las subvenciones vinculadas al precio deben formar parte de la base imponible para calcular el IVA aunque no estén reflejadas en el importe total a pagar.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004S</td> <td>Subvenciones vinculadas al Precio  (80A)</td> <td>N(14,3)</td> <td>18</td> <td>670</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setSubvencionesVinculadasAlPrecio_80A_(Double subvencionesVinculadasAlPrecio_80A_) {
		this.subvencionesVinculadasAlPrecio_80A_ = subvencionesVinculadasAlPrecio_80A_;
	}

	/** 
	 * F5004E - Total Incrementos Importe Bruto: Sumatorio de los cargos globales de factura excluyendo los de líneas (no especificar signo).
	 */ 
	public Double getTotalIncrementosDelImporteBruto_259_() {
		return totalIncrementosDelImporteBruto_259_;
	}

	/** 
	 * F5004E - Total Incrementos Importe Bruto: Sumatorio de los cargos globales de factura excluyendo los de líneas (no especificar signo).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004E</td> <td>Total Incrementos del Importe Bruto (259)</td> <td>N(14,3)</td> <td>18</td> <td>688</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTotalIncrementosDelImporteBruto_259_(Double totalIncrementosDelImporteBruto_259_) {
		this.totalIncrementosDelImporteBruto_259_ = totalIncrementosDelImporteBruto_259_;
	}

	/** 
	 * 
	 */ 
	public Double getTotalMinoracionesDelImporteBruto_260_() {
		return totalMinoracionesDelImporteBruto_260_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F5004M</td> <td>Total Minoraciones del Importe Bruto (260)</td> <td>N(14,3)</td> <td>18</td> <td>706</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTotalMinoracionesDelImporteBruto_260_(Double totalMinoracionesDelImporteBruto_260_) {
		this.totalMinoracionesDelImporteBruto_260_ = totalMinoracionesDelImporteBruto_260_;
	}

	/** 
	 * F1154I - Identificación adicional de la parte: Se utiliza para especificar el departamento que realizó el pedido. Se informa como una referencia asociada al Punto Operacional del Comprador (lo encontraremos como segmento RFF con calificador API asociado al NAD con calificador BY).
	 */ 
	public String getIdentificacionAdicionalDeLaParte_API_() {
		return identificacionAdicionalDeLaParte_API_;
	}

	/** 
	 * F1154I - Identificación adicional de la parte: Se utiliza para especificar el departamento que realizó el pedido. Se informa como una referencia asociada al Punto Operacional del Comprador (lo encontraremos como segmento RFF con calificador API asociado al NAD con calificador BY).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154I</td> <td>Identificación Adicional de la Parte (API)</td> <td>C</td> <td>17</td> <td>724</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIdentificacionAdicionalDeLaParte_API_(String identificacionAdicionalDeLaParte_API_) {
		this.identificacionAdicionalDeLaParte_API_ = identificacionAdicionalDeLaParte_API_;
	}

	/** 
	 * F3039M - Receptor del documento: Código EDI  del Cliente al que se recibe la  factura
	 */ 
	public String getReceptorDelDocumento() {
		return receptorDelDocumento;
	}

	/** 
	 * F3039M - Receptor del documento: Código EDI  del Cliente al que se recibe la  factura
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039M</td> <td>Receptor del documento</td> <td>C</td> <td>13</td> <td>741</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setReceptorDelDocumento(String receptorDelDocumento) {
		this.receptorDelDocumento = receptorDelDocumento;
	}

	/** 
	 * F1154S - Identificación Adicional Proveedor(API)(NAD+SU):
	 */ 
	public String getIdentificacionAdicionalProveedor_API__NAD_SU_() {
		return identificacionAdicionalProveedor_API__NAD_SU_;
	}

	/** 
	 * F1154S - Identificación Adicional Proveedor(API)(NAD+SU):
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1154S</td> <td>Identificación Adicional Proveedor(API)(NAD+SU)</td> <td>C</td> <td>17</td> <td>754</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIdentificacionAdicionalProveedor_API__NAD_SU_(String identificacionAdicionalProveedor_API__NAD_SU_) {
		this.identificacionAdicionalProveedor_API__NAD_SU_ = identificacionAdicionalProveedor_API__NAD_SU_;
	}

	/** 
	 * F1001T - Tipo Factura: Existe un código para identificar cada tipo de factura que queramos enviar. Las más habituales son las Facturas Comerciales(tipo 380) y los Abonos (tipo 381). El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum F1001T {
		FACTURA_PRO_FORM_325("325"),
		FACTURA_COMERCIA_380("380"),
		NOTA_DE_ABON_381("381"),
		NOTA_DE_CARG_383("383"),
		FACTURA_CONSOLIDAD_385("385"),
		AUTOFACTUR_389("389"),
		;
		
		private String value;
		
		private F1001T(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F1001T enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * F1225 - Función del Mensaje: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum F1225 {
		DUPLICAD_7("7"),
		COPIA__INDICA_QUE_EL_MENSAJE_ES_UNA_COPIA__PUEDE_UTILIZARSE_PARA_ENVIAR_LA_FACTURA_A_UN_TERCER_INTERLOCUTOR_CON_PROPOSITOS_INFORMATIVOS_31("31"),
		SUSTITUTIVA__SE_USARA_EN_EL_CASO_DE_ANULACION_DE_FACTURAS_POR_ERRORES_ADMINISTRATIVOS__LA_FACTURA_ANULA_LA_ANTERIOR_REFERENCIADA_EN_EL_CAMPO_F1154F__NUMERO_DE_DOC__SUSTITUIDO_5("5"),
		TRANSMISION_ADICIONA_43("43"),
		PAGO_A_CUENTA_BANCARI_42("42"),
		;
		
		private String value;
		
		private F1225(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F1225 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * F4183R - Razón del Cargo o del Abono: Solo se enviará si el tipo de Factura es 381(abonos) o 383 (notas de cargo). El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum F4183R {
		DEVOLUCION_DE_MERCANCI_1A("1A"),
		BONIFICACION_POR_VOLUMEN__RAPPEL_2A("2A"),
		DIFERENCIA__PRECIO__CANTIDAD_____3A("3A"),
		;
		
		private String value;
		
		private F4183R(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F4183R enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * F1153F - Calificador Documento Rectificado: Será obligatorio si el Tipo de Factura es 381 o 383. El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum F1153F {
		NUMERO_DE_FACTUR_IV("IV"),
		NUMERO_DE_RELACION_DE_FACTURA_RFA("RFA"),
		NUMERO_DE_FACTURA_RECAPITULATIV_FR("FR"),
		;
		
		private String value;
		
		private F1153F(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F1153F enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}