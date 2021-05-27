package com.esferalia.aon.file.seres.connect.invoice.v4.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

	private String tipoDeFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String funcionDelMensaje_7_31_5_;
	private Integer fechaDeFactura;
	private String fechaDeAlbaran;
	private String modoDePago;
	private String razonDeCargoOAbono;
	private String criterioDeModificacion;
	private String numeroDePedido_ON_;
	private String numeroDeAlbaran_DQ_;
	private String calificadorDocumentoRectificado_Sustituido;
	private String documentoRectificado_Sustituido;
	private String numeroDeContrato_acuerdo_CT_;
	private String numeroDeRelacionDeEntrega_REN_;
	private String codigoDeMoneda;
	private Integer fechaDeVencimientoUnico;
	private Double importeNetoTotalDeFactura_79_;
	private Double baseImponible_125_;
	private Double importeBrutoTotalDeFactura_98_;
	private Double importeTotalDeImpuestos_Tasas_176_;
	private Double importeTotalAPagar_139_;
	private Double subvencionesVinculadasAlPrecio_80A_;
	private Double totalIncrementosDelImporteBruto_259_;
	private Double totalMinoracionesDelImporteBruto_260_;
	private String periodoImposicionesFactura_325_;
	private Integer fechaPedido;
	private String fecha_horaEfectivaDelServicio_2_;
	private String numeroConfirmacionDeEntrega;


	private static Pattern PATTERN_SINCC_tipoDeFactura_325_380_381_383_385_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCC_numeroDeFactura = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SINCC_funcionDelMensaje_7_31_5_ = Pattern.compile("^.{29}(.{6}).*");
	private static Pattern PATTERN_SINCC_fechaDeFactura = Pattern.compile("^.{35}(.{8}).*");
	private static Pattern PATTERN_SINCC_fechaDeAlbaran = Pattern.compile("^.{43}(.{16}).*");
	private static Pattern PATTERN_SINCC_modoDePago = Pattern.compile("^.{59}(.{6}).*");
	private static Pattern PATTERN_SINCC_razonDeCargoOAbono = Pattern.compile("^.{65}(.{3}).*");
	private static Pattern PATTERN_SINCC_criterioDeModificacion = Pattern.compile("^.{68}(.{3}).*");
	private static Pattern PATTERN_SINCC_numeroDePedido_ON_ = Pattern.compile("^.{71}(.{17}).*");
	private static Pattern PATTERN_SINCC_numeroDeAlbaran_DQ_ = Pattern.compile("^.{88}(.{17}).*");
	private static Pattern PATTERN_SINCC_calificadorDocumentoRectificado_Sustituido = Pattern.compile("^.{105}(.{3}).*");
	private static Pattern PATTERN_SINCC_documentoRectificado_Sustituido = Pattern.compile("^.{108}(.{17}).*");
	private static Pattern PATTERN_SINCC_numeroDeContrato_acuerdo_CT_ = Pattern.compile("^.{125}(.{17}).*");
	private static Pattern PATTERN_SINCC_numeroDeRelacionDeEntrega_REN_ = Pattern.compile("^.{142}(.{17}).*");
	private static Pattern PATTERN_SINCC_codigoDeMoneda = Pattern.compile("^.{159}(.{6}).*");
	private static Pattern PATTERN_SINCC_fechaDeVencimientoUnico = Pattern.compile("^.{165}(.{8}).*");
	private static Pattern PATTERN_SINCC_importeNetoTotalDeFactura_79_ = Pattern.compile("^.{173}(.{18}).*");
	private static Pattern PATTERN_SINCC_baseImponible_125_ = Pattern.compile("^.{191}(.{18}).*");
	private static Pattern PATTERN_SINCC_importeBrutoTotalDeFactura_98_ = Pattern.compile("^.{209}(.{18}).*");
	private static Pattern PATTERN_SINCC_importeTotalDeImpuestos_Tasas_176_ = Pattern.compile("^.{227}(.{18}).*");
	private static Pattern PATTERN_SINCC_importeTotalAPagar_139_ = Pattern.compile("^.{245}(.{18}).*");
	private static Pattern PATTERN_SINCC_subvencionesVinculadasAlPrecio_80A_ = Pattern.compile("^.{263}(.{18}).*");
	private static Pattern PATTERN_SINCC_totalIncrementosDelImporteBruto_259_ = Pattern.compile("^.{281}(.{18}).*");
	private static Pattern PATTERN_SINCC_totalMinoracionesDelImporteBruto_260_ = Pattern.compile("^.{299}(.{18}).*");
	private static Pattern PATTERN_SINCC_periodoImposicionesFactura_325_ = Pattern.compile("^.{317}(.{16}).*");
	private static Pattern PATTERN_SINCC_fechaPedido = Pattern.compile("^.{333}(.{8}).*");
	private static Pattern PATTERN_SINCC_fecha_horaEfectivaDelServicio_2_ = Pattern.compile("^.{341}(.{12}).*");
	private static Pattern PATTERN_SINCC_numeroConfirmacionDeEntrega = Pattern.compile("^.{353}(.{17}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCC_tipoDeFactura_325_380_381_383_385_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeFactura_325_380_381_383_385_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_funcionDelMensaje_7_31_5_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDelMensaje_7_31_5_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_fechaDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeFactura(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_fechaDeAlbaran.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeAlbaran(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_modoDePago.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setModoDePago(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_razonDeCargoOAbono.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setRazonDeCargoOAbono(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_criterioDeModificacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCriterioDeModificacion(String.valueOf(m.group(1).trim()));
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
		if((m = PATTERN_SINCC_documentoRectificado_Sustituido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDocumentoRectificado_Sustituido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDeContrato_acuerdo_CT_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeContrato_acuerdo_CT_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroDeRelacionDeEntrega_REN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeRelacionDeEntrega_REN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_codigoDeMoneda.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDeMoneda(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_fechaDeVencimientoUnico.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeVencimientoUnico(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_importeNetoTotalDeFactura_79_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteNetoTotalDeFactura_79_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_baseImponible_125_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBaseImponible_125_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_importeBrutoTotalDeFactura_98_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteBrutoTotalDeFactura_98_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_importeTotalDeImpuestos_Tasas_176_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalDeImpuestos_Tasas_176_(Double.valueOf(m.group(1).trim()));
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
		if((m = PATTERN_SINCC_periodoImposicionesFactura_325_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPeriodoImposicionesFactura_325_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_fechaPedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaPedido(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_fecha_horaEfectivaDelServicio_2_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaEfectivaDelServicio_2_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCC_numeroConfirmacionDeEntrega.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroConfirmacionDeEntrega(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Tipo de Factura: Existe un código para identificar cada tipo de factura a enviar. Los más comunes son las facturas comerciales (380) y las notas de abono (381). Este campo corresponde al elemento 1001. Los valores posibles son:
	 */ 
	public String getTipoDeFactura_325_380_381_383_385_() {
		return tipoDeFactura_325_380_381_383_385_;
	}

	/** 
	 * 2 - Tipo de Factura: Existe un código para identificar cada tipo de factura a enviar. Los más comunes son las facturas comerciales (380) y las notas de abono (381). Este campo corresponde al elemento 1001. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Tipo de factura (325, 380, 381, 383, 385)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeFactura_325_380_381_383_385_(String tipoDeFactura_325_380_381_383_385_) {
		this.tipoDeFactura_325_380_381_383_385_ = tipoDeFactura_325_380_381_383_385_;
	}

	/** 
	 * 325 - Factura Pro-Forma
	 */ 
	public String getNumeroDeFactura() {
		return numeroDeFactura;
	}

	/** 
	 * 325 - Factura Pro-Forma
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Número de factura</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeFactura(String numeroDeFactura) {
		this.numeroDeFactura = numeroDeFactura;
	}

	/** 
	 * 4 - Función del mensaje: El campo corresponde al elemento 1225. Los valores posibles son:
	 */ 
	public String getFuncionDelMensaje_7_31_5_() {
		return funcionDelMensaje_7_31_5_;
	}

	/** 
	 * 4 - Función del mensaje: El campo corresponde al elemento 1225. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Función del mensaje (7, 31, 5)</td> <td>C</td> <td>6</td> <td>30</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFuncionDelMensaje_7_31_5_(String funcionDelMensaje_7_31_5_) {
		this.funcionDelMensaje_7_31_5_ = funcionDelMensaje_7_31_5_;
	}

	/** 
	 * 325 - Factura Pro-Forma
	 */ 
	public Integer getFechaDeFactura() {
		return fechaDeFactura;
	}

	/** 
	 * 325 - Factura Pro-Forma
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Fecha de factura</td> <td>N</td> <td>8</td> <td>36</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeFactura(Integer fechaDeFactura) {
		this.fechaDeFactura = fechaDeFactura;
	}

	/** 
	 * 6 - Fecha de albarán: fecha de albarán o de servicio en formato CCYYMMDD. También se puede utilizar para enviar el periodo de la factura en formato CCYYMMDDCCYYMMDD
	 */ 
	public String getFechaDeAlbaran() {
		return fechaDeAlbaran;
	}

	/** 
	 * 6 - Fecha de albarán: fecha de albarán o de servicio en formato CCYYMMDD. También se puede utilizar para enviar el periodo de la factura en formato CCYYMMDDCCYYMMDD
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Fecha de albarán</td> <td>C</td> <td>16</td> <td>44</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeAlbaran(String fechaDeAlbaran) {
		this.fechaDeAlbaran = fechaDeAlbaran;
	}

	/** 
	 * 7 - Duplicado
	 */ 
	public String getModoDePago() {
		return modoDePago;
	}

	/** 
	 * 7 - Duplicado
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Modo de pago</td> <td>C</td> <td>6</td> <td>60</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setModoDePago(String modoDePago) {
		this.modoDePago = modoDePago;
	}

	/** 
	 * 380 - Factura comercial
	 */ 
	public String getRazonDeCargoOAbono() {
		return razonDeCargoOAbono;
	}

	/** 
	 * 380 - Factura comercial
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Razón de cargo o abono</td> <td>C</td> <td>3</td> <td>66</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setRazonDeCargoOAbono(String razonDeCargoOAbono) {
		this.razonDeCargoOAbono = razonDeCargoOAbono;
	}

	/** 
	 * 389 - Auto-Factura
	 */ 
	public String getCriterioDeModificacion() {
		return criterioDeModificacion;
	}

	/** 
	 * 389 - Auto-Factura
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Criterio de modificación</td> <td>C</td> <td>3</td> <td>69</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCriterioDeModificacion(String criterioDeModificacion) {
		this.criterioDeModificacion = criterioDeModificacion;
	}

	/** 
	 * 10 - En efectivo
	 */ 
	public String getNumeroDePedido_ON_() {
		return numeroDePedido_ON_;
	}

	/** 
	 * 10 - En efectivo
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Número de pedido (ON)</td> <td>C</td> <td>17</td> <td>72</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePedido_ON_(String numeroDePedido_ON_) {
		this.numeroDePedido_ON_ = numeroDePedido_ON_;
	}

	/** 
	 * 11 - Número de Albarán: Será obligatorio si el Tipo de Factura es 380 y la factura es de mercancías. O si la factura es 381 o 383 y la razón es por devolución de mercancías.
	 */ 
	public String getNumeroDeAlbaran_DQ_() {
		return numeroDeAlbaran_DQ_;
	}

	/** 
	 * 11 - Número de Albarán: Será obligatorio si el Tipo de Factura es 380 y la factura es de mercancías. O si la factura es 381 o 383 y la razón es por devolución de mercancías.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Número de albarán (DQ)</td> <td>C</td> <td>17</td> <td>89</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeAlbaran_DQ_(String numeroDeAlbaran_DQ_) {
		this.numeroDeAlbaran_DQ_ = numeroDeAlbaran_DQ_;
	}

	/** 
	 * 12 - Calificador documento rectificado: Será obligatorio si el Tipo de Factura es 381 o 383. Este campo corresponde al elemento 1153. Los valores posibles son:
	 */ 
	public String getCalificadorDocumentoRectificado_Sustituido() {
		return calificadorDocumentoRectificado_Sustituido;
	}

	/** 
	 * 12 - Calificador documento rectificado: Será obligatorio si el Tipo de Factura es 381 o 383. Este campo corresponde al elemento 1153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Calificador documento rectificado / sustituido</td> <td>C</td> <td>3</td> <td>106</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDocumentoRectificado_Sustituido(String calificadorDocumentoRectificado_Sustituido) {
		this.calificadorDocumentoRectificado_Sustituido = calificadorDocumentoRectificado_Sustituido;
	}

	/** 
	 * 
	 */ 
	public String getDocumentoRectificado_Sustituido() {
		return documentoRectificado_Sustituido;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Documento rectificado / sustituido</td> <td>C</td> <td>17</td> <td>109</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDocumentoRectificado_Sustituido(String documentoRectificado_Sustituido) {
		this.documentoRectificado_Sustituido = documentoRectificado_Sustituido;
	}

	/** 
	 * 14E - Pago mediante giro bancario
	 */ 
	public String getNumeroDeContrato_acuerdo_CT_() {
		return numeroDeContrato_acuerdo_CT_;
	}

	/** 
	 * 14E - Pago mediante giro bancario
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Número de contrato/acuerdo (CT)</td> <td>C</td> <td>17</td> <td>126</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeContrato_acuerdo_CT_(String numeroDeContrato_acuerdo_CT_) {
		this.numeroDeContrato_acuerdo_CT_ = numeroDeContrato_acuerdo_CT_;
	}

	/** 
	 * 15 - Número de relación de entregas: Será obligatorio si el Tipo de Factura es 385 (Fact. Recapitulativa)
	 */ 
	public String getNumeroDeRelacionDeEntrega_REN_() {
		return numeroDeRelacionDeEntrega_REN_;
	}

	/** 
	 * 15 - Número de relación de entregas: Será obligatorio si el Tipo de Factura es 385 (Fact. Recapitulativa)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Número de relación de entrega (REN)</td> <td>C</td> <td>17</td> <td>143</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeRelacionDeEntrega_REN_(String numeroDeRelacionDeEntrega_REN_) {
		this.numeroDeRelacionDeEntrega_REN_ = numeroDeRelacionDeEntrega_REN_;
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
	 * 		 <td>16</td> <td>Código de moneda</td> <td>C</td> <td>6</td> <td>160</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDeMoneda(String codigoDeMoneda) {
		this.codigoDeMoneda = codigoDeMoneda;
	}

	/** 
	 * 17 - Fecha de vencimiento: Se indicará si la factura es de Pago único, en caso contrario se dejará a cero y los vencimientos se indicarán en el registro SINCV. El formato aceptado es CCYYMMDD.
	 */ 
	public Integer getFechaDeVencimientoUnico() {
		return fechaDeVencimientoUnico;
	}

	/** 
	 * 17 - Fecha de vencimiento: Se indicará si la factura es de Pago único, en caso contrario se dejará a cero y los vencimientos se indicarán en el registro SINCV. El formato aceptado es CCYYMMDD.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Fecha de vencimiento único</td> <td>N</td> <td>8</td> <td>166</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeVencimientoUnico(Integer fechaDeVencimientoUnico) {
		this.fechaDeVencimientoUnico = fechaDeVencimientoUnico;
	}

	/** 
	 * 18 - Importe Neto total de factura: Corresponde al sumatorio de los importes netos por línea.
	 */ 
	public Double getImporteNetoTotalDeFactura_79_() {
		return importeNetoTotalDeFactura_79_;
	}

	/** 
	 * 18 - Importe Neto total de factura: Corresponde al sumatorio de los importes netos por línea.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Importe neto total de factura (79)</td> <td>N(14,3)</td> <td>18</td> <td>174</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteNetoTotalDeFactura_79_(Double importeNetoTotalDeFactura_79_) {
		this.importeNetoTotalDeFactura_79_ = importeNetoTotalDeFactura_79_;
	}

	/** 
	 * 19 - Base imponible: Importe Neto Total de Factura (Campo 18) + Total cargos (24) - Descuentos Globales (25)
	 */ 
	public Double getBaseImponible_125_() {
		return baseImponible_125_;
	}

	/** 
	 * 19 - Base imponible: Importe Neto Total de Factura (Campo 18) + Total cargos (24) - Descuentos Globales (25)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Base imponible (125)</td> <td>N(14,3)</td> <td>18</td> <td>192</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setBaseImponible_125_(Double baseImponible_125_) {
		this.baseImponible_125_ = baseImponible_125_;
	}

	/** 
	 * 20 - Cheque
	 */ 
	public Double getImporteBrutoTotalDeFactura_98_() {
		return importeBrutoTotalDeFactura_98_;
	}

	/** 
	 * 20 - Cheque
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Importe bruto total de factura (98)</td> <td>N(14,3)</td> <td>18</td> <td>210</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteBrutoTotalDeFactura_98_(Double importeBrutoTotalDeFactura_98_) {
		this.importeBrutoTotalDeFactura_98_ = importeBrutoTotalDeFactura_98_;
	}

	/** 
	 * 21 - Importe total de impuestos: Sumatorio de los importes de impuestos por línea.
	 */ 
	public Double getImporteTotalDeImpuestos_Tasas_176_() {
		return importeTotalDeImpuestos_Tasas_176_;
	}

	/** 
	 * 21 - Importe total de impuestos: Sumatorio de los importes de impuestos por línea.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Importe total de impuestos / tasas (176)</td> <td>N(14,3)</td> <td>18</td> <td>228</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalDeImpuestos_Tasas_176_(Double importeTotalDeImpuestos_Tasas_176_) {
		this.importeTotalDeImpuestos_Tasas_176_ = importeTotalDeImpuestos_Tasas_176_;
	}

	/** 
	 * 22 - Importe total a pagar: Base Imponible + Importe Total de Impuestos.
	 */ 
	public Double getImporteTotalAPagar_139_() {
		return importeTotalAPagar_139_;
	}

	/** 
	 * 22 - Importe total a pagar: Base Imponible + Importe Total de Impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Importe total a pagar (139)</td> <td>N(14,3)</td> <td>18</td> <td>246</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalAPagar_139_(Double importeTotalAPagar_139_) {
		this.importeTotalAPagar_139_ = importeTotalAPagar_139_;
	}

	/** 
	 * 23 - Subvenciones vinculadas al precio: Las subvenciones vinculadas al precio deben formar parte de la base imponible para calcular el IVA aunque no estén reflejadas en el importe total a pagar.
	 */ 
	public Double getSubvencionesVinculadasAlPrecio_80A_() {
		return subvencionesVinculadasAlPrecio_80A_;
	}

	/** 
	 * 23 - Subvenciones vinculadas al precio: Las subvenciones vinculadas al precio deben formar parte de la base imponible para calcular el IVA aunque no estén reflejadas en el importe total a pagar.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Subvenciones vinculadas al precio (80A)</td> <td>N(14,3)</td> <td>18</td> <td>264</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setSubvencionesVinculadasAlPrecio_80A_(Double subvencionesVinculadasAlPrecio_80A_) {
		this.subvencionesVinculadasAlPrecio_80A_ = subvencionesVinculadasAlPrecio_80A_;
	}

	/** 
	 * 24 - Total incrementos importe bruto: Sumatorio de los cargos globales de factura excluyendo los de líneas (no especificar signo).
	 */ 
	public Double getTotalIncrementosDelImporteBruto_259_() {
		return totalIncrementosDelImporteBruto_259_;
	}

	/** 
	 * 24 - Total incrementos importe bruto: Sumatorio de los cargos globales de factura excluyendo los de líneas (no especificar signo).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Total incrementos del importe bruto (259)</td> <td>N(14,3)</td> <td>18</td> <td>282</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTotalIncrementosDelImporteBruto_259_(Double totalIncrementosDelImporteBruto_259_) {
		this.totalIncrementosDelImporteBruto_259_ = totalIncrementosDelImporteBruto_259_;
	}

	/** 
	 * 325 - Factura Pro-Forma
	 */ 
	public Double getTotalMinoracionesDelImporteBruto_260_() {
		return totalMinoracionesDelImporteBruto_260_;
	}

	/** 
	 * 325 - Factura Pro-Forma
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Total minoraciones del importe bruto (260)</td> <td>N(14,3)</td> <td>18</td> <td>300</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTotalMinoracionesDelImporteBruto_260_(Double totalMinoracionesDelImporteBruto_260_) {
		this.totalMinoracionesDelImporteBruto_260_ = totalMinoracionesDelImporteBruto_260_;
	}

	/** 
	 * 26 - Periodo imposiciones factura (325): Formato aceptado CCYYMMDDCCYYMMDD
	 */ 
	public String getPeriodoImposicionesFactura_325_() {
		return periodoImposicionesFactura_325_;
	}

	/** 
	 * 26 - Periodo imposiciones factura (325): Formato aceptado CCYYMMDDCCYYMMDD
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Periodo imposiciones factura (325)</td> <td>C</td> <td>16</td> <td>318</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPeriodoImposicionesFactura_325_(String periodoImposicionesFactura_325_) {
		this.periodoImposicionesFactura_325_ = periodoImposicionesFactura_325_;
	}

	/** 
	 * 
	 */ 
	public Integer getFechaPedido() {
		return fechaPedido;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Fecha Pedido</td> <td>N</td> <td>8</td> <td>334</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaPedido(Integer fechaPedido) {
		this.fechaPedido = fechaPedido;
	}

	/** 
	 * 28 - Fecha/hora efectiva del servicio: Se indicará la fecha en la que se realizó la entrega de la mercancía o se realizó el servicio. El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaEfectivaDelServicio_2_() {
		return fecha_horaEfectivaDelServicio_2_;
	}

	/** 
	 * 28 - Fecha/hora efectiva del servicio: Se indicará la fecha en la que se realizó la entrega de la mercancía o se realizó el servicio. El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Fecha/hora efectiva del servicio (2)</td> <td>C</td> <td>12</td> <td>342</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaEfectivaDelServicio_2_(String fecha_horaEfectivaDelServicio_2_) {
		this.fecha_horaEfectivaDelServicio_2_ = fecha_horaEfectivaDelServicio_2_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroConfirmacionDeEntrega() {
		return numeroConfirmacionDeEntrega;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>29</td> <td>Número Confirmación de entrega</td> <td>C</td> <td>17</td> <td>354</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroConfirmacionDeEntrega(String numeroConfirmacionDeEntrega) {
		this.numeroConfirmacionDeEntrega = numeroConfirmacionDeEntrega;
	}

	/** 
	 * 2 - Tipo de Factura: Existe un código para identificar cada tipo de factura a enviar. Los más comunes son las facturas comerciales (380) y las notas de abono (381). Este campo corresponde al elemento 1001. Los valores posibles son:
	 */
	public enum SINCC_2 {
		FACTURA_PRO_FORMA_325("325"),
		FACTURA_COMERCIAL_380("380"),
		NOTA_E_ABONO_381("381"),
		NOTA_DE_CARGO_383("383"),
		FACTURA_CONSOLIDADA_385("385"),
		AUTO_FACTURA_389("389"),
		;
		
		private String value;
		
		private SINCC_2(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCC_2 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 4 - Función del mensaje: El campo corresponde al elemento 1225. Los valores posibles son:
	 */
	public enum SINCC_4 {
		DUPLICADO_7("7"),
		COPIA__PUEDE_UTILIZARSE_PARA_ENVIAR_LA_FACTURA_A_UN_TERCER_INTERLOCUTOR__31("31"),
		SUSTITUTIVA__SE_USARA_EN_EL_CASO_DE_ANULACION_DE_FACTURAS_POR_ERRORES_ADMINISTRATIVOS__LA_FACTURA_ANULA_LA_ANTERIOR_REFERENCIADA_EN_EL_CAMPO_13__NUMERO_DE_DOC__SUSTITUIDO___5("5"),
		TRANSMISION_ADICIONAL_43("43"),
		PAGO_A_CUENTA_BANCARIA_42("42"),
		PAGO_MEDIANTE_GIRO_BANCARIO_14E("14E"),
		EN_EFECTIVO_10("10"),
		CHEQUE_20("20"),
		PAGARE_60("60"),
		;
		
		private String value;
		
		private SINCC_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCC_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 8 - Razón del cargo o del abono: Solo se enviará si el tipo de Factura es 381(abonos) o 383 (notas de cargo). Este campo corresponde al elemento 4183. Los valores posibles son:
	 */
	public enum SINCC_8 {
		DEVOLUCION_DE_MERCANCIA_1A("1A"),
		BONIFICACION_POR_VOLUMEN__RAPPEL__2A("2A"),
		DIFERENCIAS__PRECIO__CANTIDAD______3A("3A"),
		;
		
		private String value;
		
		private SINCC_8(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCC_8 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 9 - Criterios de modificación: Los criterios de modificación son los siguientes. Los valores posibles son:
	 */
	public enum SINCC_9 {
		RECTIFICADO_ENTERO_01("01"),
		RECTIFICADO_POR_DIFERENCIAS_02("02"),
		RECTIFICACION_POR_GASTOS_EN_EL_VOLUMEN_DE_OPERACIONES_DE_UN_PERIODO_03("03"),
		RECTIFICACIONES_AUTORIZADAS_POR_LA_AGENCIA_TRIBUTARIA__04("04"),
		;
		
		private String value;
		
		private SINCC_9(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCC_9 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 12 - Calificador documento rectificado: Será obligatorio si el Tipo de Factura es 381 o 383. Este campo corresponde al elemento 1153. Los valores posibles son:
	 */
	public enum SINCC_12 {
		NUMERO_DE_FACTURA_IV("IV"),
		NUMERO_DE_RELACION_DE_FACTURAS_RFA("RFA"),
		NUMERO_DE_FACTURA_RECAPITULATIVA_FR("FR"),
		PERIODO_IMPOSICIONES_FACTURA__325___FORMATO_ACEPTADO_CCYYMMDDCCYYMMDD_26("26"),
		;
		
		private String value;
		
		private SINCC_12(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCC_12 enumByValue(String value) {
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