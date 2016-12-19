package com.esferalia.aon.file.seres.connect.income.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI RECAC entity.
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
 * 		 <td>RECAC</td> <td>Cabecera confirmación de recepción</td> <td>Obligatorio</td> <td>1</td>
 * 	</tr>
 * </table>
 */ 

public class RECAC {

	private String tipoDocumento_352_;
	private String numeroDocumento;
	private String funcionDelMensaje_9_31_;
	private String fechaDelDocumento;
	private String fechaRecepcionDeLaMercancia;
	private String numeroDePedido_ON_;
	private String fecha_horaNumeroPedido_171__102_203_;
	private String numeroDeAlbaran_DQ_AAK_;
	private String fecha_horaNumeroAlbaran_171__102_203_;
	private String fecha_horaEnLaQueLaCargaEsRecogida_200_;
	private String matriculaVehiculo;


	private static Pattern PATTERN_RECAC_tipoDocumento_352_ = Pattern.compile("^.{6}(.{3}).*");
	private static Pattern PATTERN_RECAC_numeroDocumento = Pattern.compile("^.{9}(.{17}).*");
	private static Pattern PATTERN_RECAC_funcionDelMensaje_9_31_ = Pattern.compile("^.{26}(.{3}).*");
	private static Pattern PATTERN_RECAC_fechaDelDocumento = Pattern.compile("^.{29}(.{8}).*");
	private static Pattern PATTERN_RECAC_fechaRecepcionDeLaMercancia = Pattern.compile("^.{37}(.{8}).*");
	private static Pattern PATTERN_RECAC_numeroDePedido_ON_ = Pattern.compile("^.{45}(.{17}).*");
	private static Pattern PATTERN_RECAC_fecha_horaNumeroPedido_171__102_203_ = Pattern.compile("^.{62}(.{12}).*");
	private static Pattern PATTERN_RECAC_numeroDeAlbaran_DQ_AAK_ = Pattern.compile("^.{74}(.{17}).*");
	private static Pattern PATTERN_RECAC_fecha_horaNumeroAlbaran_171__102_203_ = Pattern.compile("^.{91}(.{12}).*");
	private static Pattern PATTERN_RECAC_fecha_horaEnLaQueLaCargaEsRecogida_200_ = Pattern.compile("^.{103}(.{12}).*");
	private static Pattern PATTERN_RECAC_matriculaVehiculo = Pattern.compile("^.{115}(.{35}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_RECAC_tipoDocumento_352_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDocumento_352_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_numeroDocumento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDocumento(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_funcionDelMensaje_9_31_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDelMensaje_9_31_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_fechaDelDocumento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDelDocumento(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_fechaRecepcionDeLaMercancia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaRecepcionDeLaMercancia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_numeroDePedido_ON_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido_ON_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_fecha_horaNumeroPedido_171__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaNumeroPedido_171__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_numeroDeAlbaran_DQ_AAK_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeAlbaran_DQ_AAK_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_fecha_horaNumeroAlbaran_171__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaNumeroAlbaran_171__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_fecha_horaEnLaQueLaCargaEsRecogida_200_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaEnLaQueLaCargaEsRecogida_200_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAC_matriculaVehiculo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMatriculaVehiculo(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Tipo documento: Este campo corresponde al elemento 1001. Los posibles valores son:
	 */ 
	public String getTipoDocumento_352_() {
		return tipoDocumento_352_;
	}

	/** 
	 * 2 - Tipo documento: Este campo corresponde al elemento 1001. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Tipo documento (352)</td> <td>C</td> <td>3</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDocumento_352_(String tipoDocumento_352_) {
		this.tipoDocumento_352_ = tipoDocumento_352_;
	}

	/** 
	 * 352 - Confirmación de Recepción
	 */ 
	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	/** 
	 * 352 - Confirmación de Recepción
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Número documento</td> <td>C</td> <td>17</td> <td>10</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	/** 
	 * 4 - Función del mensaje: Este campo corresponde al elemento 1225. Los posibles valores son:
	 */ 
	public String getFuncionDelMensaje_9_31_() {
		return funcionDelMensaje_9_31_;
	}

	/** 
	 * 4 - Función del mensaje: Este campo corresponde al elemento 1225. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Función del mensaje (9, 31)</td> <td>C</td> <td>3</td> <td>27</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFuncionDelMensaje_9_31_(String funcionDelMensaje_9_31_) {
		this.funcionDelMensaje_9_31_ = funcionDelMensaje_9_31_;
	}

	/** 
	 * 352 - Confirmación de Recepción
	 */ 
	public String getFechaDelDocumento() {
		return fechaDelDocumento;
	}

	/** 
	 * 352 - Confirmación de Recepción
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Fecha del documento</td> <td>C</td> <td>8</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDelDocumento(String fechaDelDocumento) {
		this.fechaDelDocumento = fechaDelDocumento;
	}

	/** 
	 * 6 - Fecha recepción de la mercancía: Fecha en la que se recibió la mercancía en formato AAAAMMDD. Es de cumplimentación Opcional.
	 */ 
	public String getFechaRecepcionDeLaMercancia() {
		return fechaRecepcionDeLaMercancia;
	}

	/** 
	 * 6 - Fecha recepción de la mercancía: Fecha en la que se recibió la mercancía en formato AAAAMMDD. Es de cumplimentación Opcional.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Fecha recepción de la mercancía</td> <td>C</td> <td>8</td> <td>38</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaRecepcionDeLaMercancia(String fechaRecepcionDeLaMercancia) {
		this.fechaRecepcionDeLaMercancia = fechaRecepcionDeLaMercancia;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDePedido_ON_() {
		return numeroDePedido_ON_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Número de pedido (ON)</td> <td>C</td> <td>17</td> <td>46</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePedido_ON_(String numeroDePedido_ON_) {
		this.numeroDePedido_ON_ = numeroDePedido_ON_;
	}

	/** 
	 * 8 - Fecha/hora número pedido (171) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaNumeroPedido_171__102_203_() {
		return fecha_horaNumeroPedido_171__102_203_;
	}

	/** 
	 * 8 - Fecha/hora número pedido (171) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Fecha/hora número pedido (171) (102/203)</td> <td>C</td> <td>12</td> <td>63</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaNumeroPedido_171__102_203_(String fecha_horaNumeroPedido_171__102_203_) {
		this.fecha_horaNumeroPedido_171__102_203_ = fecha_horaNumeroPedido_171__102_203_;
	}

	/** 
	 * 9 - Original - El envío de una relación de documentos original
	 */ 
	public String getNumeroDeAlbaran_DQ_AAK_() {
		return numeroDeAlbaran_DQ_AAK_;
	}

	/** 
	 * 9 - Original - El envío de una relación de documentos original
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Número de albarán (DQ/AAK)</td> <td>C</td> <td>17</td> <td>75</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeAlbaran_DQ_AAK_(String numeroDeAlbaran_DQ_AAK_) {
		this.numeroDeAlbaran_DQ_AAK_ = numeroDeAlbaran_DQ_AAK_;
	}

	/** 
	 * 10 - Fecha/hora número albarán (171) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaNumeroAlbaran_171__102_203_() {
		return fecha_horaNumeroAlbaran_171__102_203_;
	}

	/** 
	 * 10 - Fecha/hora número albarán (171) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Fecha/hora número albarán (171) (102/203)</td> <td>C</td> <td>12</td> <td>92</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaNumeroAlbaran_171__102_203_(String fecha_horaNumeroAlbaran_171__102_203_) {
		this.fecha_horaNumeroAlbaran_171__102_203_ = fecha_horaNumeroAlbaran_171__102_203_;
	}

	/** 
	 * 11 - Fecha/hora en la que la carga es recogida (200) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaEnLaQueLaCargaEsRecogida_200_() {
		return fecha_horaEnLaQueLaCargaEsRecogida_200_;
	}

	/** 
	 * 11 - Fecha/hora en la que la carga es recogida (200) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Fecha/hora en la que la carga es recogida (200)</td> <td>C</td> <td>12</td> <td>104</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaEnLaQueLaCargaEsRecogida_200_(String fecha_horaEnLaQueLaCargaEsRecogida_200_) {
		this.fecha_horaEnLaQueLaCargaEsRecogida_200_ = fecha_horaEnLaQueLaCargaEsRecogida_200_;
	}

	/** 
	 * 
	 */ 
	public String getMatriculaVehiculo() {
		return matriculaVehiculo;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Matrícula Vehículo</td> <td>C</td> <td>35</td> <td>116</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMatriculaVehiculo(String matriculaVehiculo) {
		this.matriculaVehiculo = matriculaVehiculo;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}