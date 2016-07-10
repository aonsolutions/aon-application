package com.esferalia.aon.file.seres.standard.delivery.data;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI RECTL entity.
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
 * 		 <td>RECTL</td> <td>Registro de control</td> <td>Obligatorio</td> <td>1</td>
 * 	</tr>
 * </table>
 */ 

public class RECTL {

	private String tipoDeMensaje;
	private String codigoEmisor;
	private String codigoReceptor;
	private String identificacionDelMensaje;
	private String fecha_horaDelMensaje;


	public SEH1C seh1c;
	public List<SEH1D> seh1dList;
	public List<SEH1P> seh1pList;
	public List<SEH1L> seh1lList;
	public List<SEH1G> seh1gList;
	public List<SEH1B> seh1bList;


	private static Pattern PATTERN_RECTL_tipoDeMensaje = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_RECTL_codigoEmisor = Pattern.compile("^.{12}(.{35}).*");
	private static Pattern PATTERN_RECTL_codigoReceptor = Pattern.compile("^.{47}(.{35}).*");
	private static Pattern PATTERN_RECTL_identificacionDelMensaje = Pattern.compile("^.{82}(.{40}).*");
	private static Pattern PATTERN_RECTL_fecha_horaDelMensaje = Pattern.compile("^.{122}(.{12}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_RECTL_tipoDeMensaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeMensaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECTL_codigoEmisor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECTL_codigoReceptor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECTL_identificacionDelMensaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificacionDelMensaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECTL_fecha_horaDelMensaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaDelMensaje(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Tipo de mensaje: Existe un código para identificar cada tipo de mensaje a enviar. El campo corresponde al código EANCOM. Los valores posibles son:
	 */ 
	public String getTipoDeMensaje() {
		return tipoDeMensaje;
	}

	/** 
	 * 2 - Tipo de mensaje: Existe un código para identificar cada tipo de mensaje a enviar. El campo corresponde al código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Tipo de mensaje</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeMensaje(String tipoDeMensaje) {
		this.tipoDeMensaje = tipoDeMensaje;
	}

	/** 
	 * 3 - Código emisor: Código que identifica el emisor del mensaje.
	 */ 
	public String getCodigoEmisor() {
		return codigoEmisor;
	}

	/** 
	 * 3 - Código emisor: Código que identifica el emisor del mensaje.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Código emisor</td> <td>C</td> <td>35</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEmisor(String codigoEmisor) {
		this.codigoEmisor = codigoEmisor;
	}

	/** 
	 * 4 - Código receptor: Código que identifica el receptor del mensaje.
	 */ 
	public String getCodigoReceptor() {
		return codigoReceptor;
	}

	/** 
	 * 4 - Código receptor: Código que identifica el receptor del mensaje.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Código receptor</td> <td>C</td> <td>35</td> <td>48</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptor(String codigoReceptor) {
		this.codigoReceptor = codigoReceptor;
	}

	/** 
	 * 5 - Identificación del mensaje: Referencia única del mensaje asignada por el emisor.
	 */ 
	public String getIdentificacionDelMensaje() {
		return identificacionDelMensaje;
	}

	/** 
	 * 5 - Identificación del mensaje: Referencia única del mensaje asignada por el emisor.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Identificación del mensaje</td> <td>C</td> <td>40</td> <td>83</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIdentificacionDelMensaje(String identificacionDelMensaje) {
		this.identificacionDelMensaje = identificacionDelMensaje;
	}

	/** 
	 * 6 - Fecha/hora del mensaje: Fecha y hora de la generación del mensaje. El formato aceptado es CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaDelMensaje() {
		return fecha_horaDelMensaje;
	}

	/** 
	 * 6 - Fecha/hora del mensaje: Fecha y hora de la generación del mensaje. El formato aceptado es CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Fecha/hora del mensaje</td> <td>C</td> <td>12</td> <td>123</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaDelMensaje(String fecha_horaDelMensaje) {
		this.fecha_horaDelMensaje = fecha_horaDelMensaje;
	}

	/** 
	 * 2 - Tipo de mensaje: Existe un código para identificar cada tipo de mensaje a enviar. El campo corresponde al código EANCOM. Los valores posibles son:
	 */
	public enum RECTL_2 {
		;
		
		private String value;
		
		private RECTL_2(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static RECTL_2 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}