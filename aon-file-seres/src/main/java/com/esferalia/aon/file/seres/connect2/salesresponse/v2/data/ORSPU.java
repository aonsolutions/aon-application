package com.esferalia.aon.file.seres.connect2.salesresponse.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ORSPU {
	private String calificadorDelTemaDeTexto;
	private String texto1;
	private String texto2;
	private String texto3;
	private String texto4;
	private String texto5;
	
	private static Pattern PATTERN_ORSPU_calificadorDelTemaDeTexto = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ORSPU_texto1 = Pattern.compile("^.{12}(.{70}).*");
	private static Pattern PATTERN_ORSPU_texto2 = Pattern.compile("^.{82}(.{70}).*");
	private static Pattern PATTERN_ORSPU_texto3 = Pattern.compile("^.{152}(.{70}).*");
	private static Pattern PATTERN_ORSPU_texto4 = Pattern.compile("^.{222}(.{70}).*");
	private static Pattern PATTERN_ORSPU_texto5 = Pattern.compile("^.{292}(.{70}).*");
	
	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ORSPU_calificadorDelTemaDeTexto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDelTemaDeTexto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPU_texto1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPU_texto2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPU_texto3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPU_texto4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPU_texto5.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {			
			setTexto5(String.valueOf(m.group(1).trim()));
		}
		
	}

	
	/**
	 * 2 - Calificador del tema de Texto: Este campo se utiliza para especificar el asunto de las observaciones. Este campo corresponde al elemento 4451. Los valores posibles son:
	 */
	public String getCalificadorDelTemaDeTexto() {
		return calificadorDelTemaDeTexto;
	}

	/**
	 * 2 - Calificador del tema de Texto: Este campo se utiliza para especificar el asunto de las observaciones. Este campo corresponde al elemento 4451. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Calificador del tema de Texto</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorDelTemaDeTexto(String calificadorDelTemaDeTexto) {
		this.calificadorDelTemaDeTexto = calificadorDelTemaDeTexto;
	}

	/**
	 * Texto 1
	 */
	public String getTexto1() {
		return texto1;
	}


	/**
	 * Texto 1
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Texto 1</td> <td>C</td> <td>70</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTexto1(String texto1) {
		this.texto1 = texto1;
	}

	/**
	 * Texto 2
	 */
	public String getTexto2() {
		return texto2;
	}

	/**
	 * Texto 2
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Texto 2</td> <td>C</td> <td>70</td> <td>83</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTexto2(String texto2) {
		this.texto2 = texto2;
	}

	/**
	 * Texto 3
	 */
	public String getTexto3() {
		return texto3;
	}

	/**
	 * Texto 3
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Texto 3</td> <td>C</td> <td>70</td> <td>153</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTexto3(String texto3) {
		this.texto3 = texto3;
	}

	/**
	 * Texto 4
	 */
	public String getTexto4() {
		return texto4;
	}

	/**
	 * Texto 4
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Texto 4</td> <td>C</td> <td>70</td> <td>223</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTexto4(String texto4) {
		this.texto4 = texto4;
	}

	/**
	 * Texto 5
	 */
	public String getTexto5() {
		return texto5;
	}

	/**
	 * Texto 5
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Texto 5</td> <td>C</td> <td>70</td> <td>293</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTexto5(String texto5) {
		this.texto5 = texto5;
	}



	/** 
	 * 2 - Calificador del tema de Texto: Este campo se utiliza para especificar el asunto de las
observaciones. Este campo corresponde al elemento 4451. Los valores posibles son:
	 */
	public enum ORSPU_3 {
		INFORMACION_GENERAL("AAI"),
		INFORMACION_DE_ENTREGA("DEL"),
		INSTRUCCIONES_DE_FACTURACION("INV"),
		INFORMACION_DE_COMPRA("PUR"),
		;
		
		private String value;
		
		private ORSPU_3(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPU_3 enumByValue(String value) {
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