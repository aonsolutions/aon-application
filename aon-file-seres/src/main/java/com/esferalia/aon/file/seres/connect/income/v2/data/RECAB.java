package com.esferalia.aon.file.seres.connect.income.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI RECAB entity.
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
 * 		 <td>RECAB</td> <td>Información de lotes</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class RECAB {

	private String codigoInstrucciones;
	private String marcasDeEnvio;
	private String calificadorDeCantidad_11_12_;
	private Double cantidad;
	private String calificadorNumeroIdentidad;
	private String numeroIdentidad;


	private static Pattern PATTERN_RECAB_codigoInstrucciones = Pattern.compile("^.{6}(.{3}).*");
	private static Pattern PATTERN_RECAB_marcasDeEnvio = Pattern.compile("^.{9}(.{35}).*");
	private static Pattern PATTERN_RECAB_calificadorDeCantidad_11_12_ = Pattern.compile("^.{44}(.{3}).*");
	private static Pattern PATTERN_RECAB_cantidad = Pattern.compile("^.{47}(.{16}).*");
	private static Pattern PATTERN_RECAB_calificadorNumeroIdentidad = Pattern.compile("^.{63}(.{3}).*");
	private static Pattern PATTERN_RECAB_numeroIdentidad = Pattern.compile("^.{66}(.{35}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_RECAB_codigoInstrucciones.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInstrucciones(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAB_marcasDeEnvio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcasDeEnvio(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAB_calificadorDeCantidad_11_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDeCantidad_11_12_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAB_cantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAB_calificadorNumeroIdentidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorNumeroIdentidad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAB_numeroIdentidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroIdentidad(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Código Instrucciones: Código indicando las instrucciones de como los embalajes o unidades físicas deben ser marcados. Este campo se corresponde con el elemento 4233. Los posibles valores son:
	 */ 
	public String getCodigoInstrucciones() {
		return codigoInstrucciones;
	}

	/** 
	 * 2 - Código Instrucciones: Código indicando las instrucciones de como los embalajes o unidades físicas deben ser marcados. Este campo se corresponde con el elemento 4233. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Código Instrucciones</td> <td>C</td> <td>3</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInstrucciones(String codigoInstrucciones) {
		this.codigoInstrucciones = codigoInstrucciones;
	}

	/** 
	 * 33E - Marcado con un código de seriado (Código EAN)
	 */ 
	public String getMarcasDeEnvio() {
		return marcasDeEnvio;
	}

	/** 
	 * 33E - Marcado con un código de seriado (Código EAN)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Marcas de envío</td> <td>C</td> <td>35</td> <td>10</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcasDeEnvio(String marcasDeEnvio) {
		this.marcasDeEnvio = marcasDeEnvio;
	}

	/** 
	 * 4 - Calificador de cantidad (11/12): Código que da significado a la cantidad. Este campo se corresponde con el elemento 6063. Los posibles valores son:
	 */ 
	public String getCalificadorDeCantidad_11_12_() {
		return calificadorDeCantidad_11_12_;
	}

	/** 
	 * 4 - Calificador de cantidad (11/12): Código que da significado a la cantidad. Este campo se corresponde con el elemento 6063. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Calificador de cantidad (11/12)</td> <td>C</td> <td>3</td> <td>45</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDeCantidad_11_12_(String calificadorDeCantidad_11_12_) {
		this.calificadorDeCantidad_11_12_ = calificadorDeCantidad_11_12_;
	}

	/** 
	 * 
	 */ 
	public Double getCantidad() {
		return cantidad;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Cantidad</td> <td>N(12,3)</td> <td>16</td> <td>48</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	/** 
	 * 36E - Marcado con número de lote
	 */ 
	public String getCalificadorNumeroIdentidad() {
		return calificadorNumeroIdentidad;
	}

	/** 
	 * 36E - Marcado con número de lote
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Calificador número identidad</td> <td>C</td> <td>3</td> <td>64</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorNumeroIdentidad(String calificadorNumeroIdentidad) {
		this.calificadorNumeroIdentidad = calificadorNumeroIdentidad;
	}

	/** 
	 * 17 - Instrucciones del proveedor
	 */ 
	public String getNumeroIdentidad() {
		return numeroIdentidad;
	}

	/** 
	 * 17 - Instrucciones del proveedor
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Número identidad</td> <td>C</td> <td>35</td> <td>67</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroIdentidad(String numeroIdentidad) {
		this.numeroIdentidad = numeroIdentidad;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}