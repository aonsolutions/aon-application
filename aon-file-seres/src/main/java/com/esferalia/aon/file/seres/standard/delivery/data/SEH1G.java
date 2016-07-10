package com.esferalia.aon.file.seres.standard.delivery.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1G entity.
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
 * 		 <td>SEH1G</td> <td>Desglose cantidad/Localizaciones</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1G {

	private String codigoLugar_localizacion;
	private String agenciaResponsableListaDeCodigos;
	private String lugar_localizacion_TextoLibre;
	private String fecha_horaEstimadaDeEntrega_17_;
	private Double cantidadEnvioDividida_11_12_;
	private Double pesoEnvioDivididoEnKGM_12_;
	private String calificadorLugar_localizacion;
	private String fechaDeSacrificio_X20_;


	private static Pattern PATTERN_SEH1G_codigoLugar_localizacion = Pattern.compile("^.{6}(.{25}).*");
	private static Pattern PATTERN_SEH1G_agenciaResponsableListaDeCodigos = Pattern.compile("^.{31}(.{3}).*");
	private static Pattern PATTERN_SEH1G_lugar_localizacion_TextoLibre = Pattern.compile("^.{34}(.{70}).*");
	private static Pattern PATTERN_SEH1G_fecha_horaEstimadaDeEntrega_17_ = Pattern.compile("^.{104}(.{12}).*");
	private static Pattern PATTERN_SEH1G_cantidadEnvioDividida_11_12_ = Pattern.compile("^.{116}(.{16}).*");
	private static Pattern PATTERN_SEH1G_pesoEnvioDivididoEnKGM_12_ = Pattern.compile("^.{132}(.{16}).*");
	private static Pattern PATTERN_SEH1G_calificadorLugar_localizacion = Pattern.compile("^.{148}(.{3}).*");
	private static Pattern PATTERN_SEH1G_fechaDeSacrificio_X20_ = Pattern.compile("^.{151}(.{12}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1G_codigoLugar_localizacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoLugar_localizacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_agenciaResponsableListaDeCodigos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAgenciaResponsableListaDeCodigos(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_lugar_localizacion_TextoLibre.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setLugar_localizacion_TextoLibre(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_fecha_horaEstimadaDeEntrega_17_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaEstimadaDeEntrega_17_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_cantidadEnvioDividida_11_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadEnvioDividida_11_12_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_pesoEnvioDivididoEnKGM_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoEnvioDivididoEnKGM_12_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_calificadorLugar_localizacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorLugar_localizacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_fechaDeSacrificio_X20_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeSacrificio_X20_(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Código lugar de entrega: Lugar en el que se entrega el artículo. Es recomendable utilizar un número global de localización (GLN) - Formato n13.
	 */ 
	public String getCodigoLugar_localizacion() {
		return codigoLugar_localizacion;
	}

	/** 
	 * 2 - Código lugar de entrega: Lugar en el que se entrega el artículo. Es recomendable utilizar un número global de localización (GLN) - Formato n13.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Código lugar/localización</td> <td>C</td> <td>25</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoLugar_localizacion(String codigoLugar_localizacion) {
		this.codigoLugar_localizacion = codigoLugar_localizacion;
	}

	/** 
	 * 3 - Agencia responsable lista de códigos: Código que identifica la empresa responsable de la lista de códigos. Este campo se corresponde con el elemento 3055. Los posibles valores son:
	 */ 
	public String getAgenciaResponsableListaDeCodigos() {
		return agenciaResponsableListaDeCodigos;
	}

	/** 
	 * 3 - Agencia responsable lista de códigos: Código que identifica la empresa responsable de la lista de códigos. Este campo se corresponde con el elemento 3055. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Agencia responsable lista de códigos</td> <td>C</td> <td>3</td> <td>32</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setAgenciaResponsableListaDeCodigos(String agenciaResponsableListaDeCodigos) {
		this.agenciaResponsableListaDeCodigos = agenciaResponsableListaDeCodigos;
	}

	/** 
	 * 246 - Lugar de sacrificio. Obligatorio para la trazabilidad de la pesca
	 */ 
	public String getLugar_localizacion_TextoLibre() {
		return lugar_localizacion_TextoLibre;
	}

	/** 
	 * 246 - Lugar de sacrificio. Obligatorio para la trazabilidad de la pesca
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Lugar/localización, texto libre</td> <td>C</td> <td>70</td> <td>35</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setLugar_localizacion_TextoLibre(String lugar_localizacion_TextoLibre) {
		this.lugar_localizacion_TextoLibre = lugar_localizacion_TextoLibre;
	}

	/** 
	 * 5 - Fecha/hora estimada de entrega: Fecha y/o previsión de cuando el emisor del envío estima que se entregará la mercancía. El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaEstimadaDeEntrega_17_() {
		return fecha_horaEstimadaDeEntrega_17_;
	}

	/** 
	 * 5 - Fecha/hora estimada de entrega: Fecha y/o previsión de cuando el emisor del envío estima que se entregará la mercancía. El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Fecha/hora estimada de entrega (17)</td> <td>C</td> <td>12</td> <td>105</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaEstimadaDeEntrega_17_(String fecha_horaEstimadaDeEntrega_17_) {
		this.fecha_horaEstimadaDeEntrega_17_ = fecha_horaEstimadaDeEntrega_17_;
	}

	/** 
	 * 246 - Lugar de sacrificio. Obligatorio para la trazabilidad de la pesca
	 */ 
	public Double getCantidadEnvioDividida_11_12_() {
		return cantidadEnvioDividida_11_12_;
	}

	/** 
	 * 246 - Lugar de sacrificio. Obligatorio para la trazabilidad de la pesca
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Cantidad envío dividida (11/12)</td> <td>N(12,3)</td> <td>16</td> <td>117</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadEnvioDividida_11_12_(Double cantidadEnvioDividida_11_12_) {
		this.cantidadEnvioDividida_11_12_ = cantidadEnvioDividida_11_12_;
	}

	/** 
	 * 7 - Lugar de entrega. Valor por defecto si se deja en blanco
	 */ 
	public Double getPesoEnvioDivididoEnKGM_12_() {
		return pesoEnvioDivididoEnKGM_12_;
	}

	/** 
	 * 7 - Lugar de entrega. Valor por defecto si se deja en blanco
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Peso envío dividido en KGM (12)</td> <td>N(12,3)</td> <td>16</td> <td>133</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoEnvioDivididoEnKGM_12_(Double pesoEnvioDivididoEnKGM_12_) {
		this.pesoEnvioDivididoEnKGM_12_ = pesoEnvioDivididoEnKGM_12_;
	}

	/** 
	 * 8 - Calificador lugar/localización: Este campo se corresponde con el elemento 3227. Los posibles valores son:
	 */ 
	public String getCalificadorLugar_localizacion() {
		return calificadorLugar_localizacion;
	}

	/** 
	 * 8 - Calificador lugar/localización: Este campo se corresponde con el elemento 3227. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Calificador lugar/localización</td> <td>C</td> <td>3</td> <td>149</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorLugar_localizacion(String calificadorLugar_localizacion) {
		this.calificadorLugar_localizacion = calificadorLugar_localizacion;
	}

	/** 
	 * 9 - EAN
	 */ 
	public String getFechaDeSacrificio_X20_() {
		return fechaDeSacrificio_X20_;
	}

	/** 
	 * 9 - EAN
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Fecha de sacrificio (X20)</td> <td>C</td> <td>12</td> <td>152</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeSacrificio_X20_(String fechaDeSacrificio_X20_) {
		this.fechaDeSacrificio_X20_ = fechaDeSacrificio_X20_;
	}

}