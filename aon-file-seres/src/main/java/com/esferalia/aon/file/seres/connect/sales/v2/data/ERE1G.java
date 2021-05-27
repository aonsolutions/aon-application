package com.esferalia.aon.file.seres.connect.sales.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1G entity.
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
 * 		 <td>ERE1G</td> <td>Desglose en cantidad</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class ERE1G {

	private String codigoLugarDeEntrega;
	private String agenciaResponsableListaDeCodigos;
	private String lugarDeEntrega_TextoLibre;
	private String fecha_horaDeEntregaRequerida_2_;
	private Double cantidadEnvioDividida_11_;
	private String calificadorUnidadDeMedida;


	private static Pattern PATTERN_ERE1G_codigoLugarDeEntrega = Pattern.compile("^.{6}(.{25}).*");
	private static Pattern PATTERN_ERE1G_agenciaResponsableListaDeCodigos = Pattern.compile("^.{31}(.{3}).*");
	private static Pattern PATTERN_ERE1G_lugarDeEntrega_TextoLibre = Pattern.compile("^.{34}(.{70}).*");
	private static Pattern PATTERN_ERE1G_fecha_horaDeEntregaRequerida_2_ = Pattern.compile("^.{104}(.{12}).*");
	private static Pattern PATTERN_ERE1G_cantidadEnvioDividida_11_ = Pattern.compile("^.{116}(.{16}).*");
	private static Pattern PATTERN_ERE1G_calificadorUnidadDeMedida = Pattern.compile("^.{132}(.{6}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1G_codigoLugarDeEntrega.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoLugarDeEntrega(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_agenciaResponsableListaDeCodigos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAgenciaResponsableListaDeCodigos(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_lugarDeEntrega_TextoLibre.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setLugarDeEntrega_TextoLibre(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_fecha_horaDeEntregaRequerida_2_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaDeEntregaRequerida_2_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_cantidadEnvioDividida_11_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadEnvioDividida_11_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_calificadorUnidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Código lugar de entrega: Lugar en el que se entrega el artículo. Es recomendable utilizar un número global de localización (GLN) - Formato n13.
	 */ 
	public String getCodigoLugarDeEntrega() {
		return codigoLugarDeEntrega;
	}

	/** 
	 * 2 - Código lugar de entrega: Lugar en el que se entrega el artículo. Es recomendable utilizar un número global de localización (GLN) - Formato n13.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Código lugar de entrega</td> <td>C</td> <td>25</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoLugarDeEntrega(String codigoLugarDeEntrega) {
		this.codigoLugarDeEntrega = codigoLugarDeEntrega;
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
	 * 
	 */ 
	public String getLugarDeEntrega_TextoLibre() {
		return lugarDeEntrega_TextoLibre;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Lugar de entrega, texto libre</td> <td>C</td> <td>70</td> <td>35</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setLugarDeEntrega_TextoLibre(String lugarDeEntrega_TextoLibre) {
		this.lugarDeEntrega_TextoLibre = lugarDeEntrega_TextoLibre;
	}

	/** 
	 * 5 - Fecha/hora de entrega requerida: Fecha/Hora en la que el comprador solicita que se entreguen las mercancías. El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaDeEntregaRequerida_2_() {
		return fecha_horaDeEntregaRequerida_2_;
	}

	/** 
	 * 5 - Fecha/hora de entrega requerida: Fecha/Hora en la que el comprador solicita que se entreguen las mercancías. El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Fecha/hora de entrega requerida (2)</td> <td>C</td> <td>12</td> <td>105</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaDeEntregaRequerida_2_(String fecha_horaDeEntregaRequerida_2_) {
		this.fecha_horaDeEntregaRequerida_2_ = fecha_horaDeEntregaRequerida_2_;
	}

	/** 
	 * 
	 */ 
	public Double getCantidadEnvioDividida_11_() {
		return cantidadEnvioDividida_11_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Cantidad envío dividida (11)</td> <td>N(12,3)</td> <td>16</td> <td>117</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadEnvioDividida_11_(Double cantidadEnvioDividida_11_) {
		this.cantidadEnvioDividida_11_ = cantidadEnvioDividida_11_;
	}

	/** 
	 * 
	 */ 
	public String getCalificadorUnidadDeMedida() {
		return calificadorUnidadDeMedida;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Calificador unidad de medida</td> <td>C</td> <td>6</td> <td>133</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorUnidadDeMedida(String calificadorUnidadDeMedida) {
		this.calificadorUnidadDeMedida = calificadorUnidadDeMedida;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}