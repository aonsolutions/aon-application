package com.esferalia.aon.file.seres.udapa.invoice.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCT entity.
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
 * 		 <td>SINCT</td> <td>Observaciones Cabecera</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCT {

	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private Integer numeroDeLinea;
	private String calificadorDelTemaDelTexto;
	private String texto1;
	private String texto2;
	private String texto3;
	private String texto4;
	private String texto5;


	private static Pattern PATTERN_SINCT_tipoFactura_325_380_381_383_385_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCT_numeroDeFactura = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SINCT_codigoVendedor_SU_ = Pattern.compile("^.{29}(.{13}).*");
	private static Pattern PATTERN_SINCT_codigoComprador_BY_ = Pattern.compile("^.{42}(.{13}).*");
	private static Pattern PATTERN_SINCT_numeroDeLinea = Pattern.compile("^.{55}(.{6}).*");
	private static Pattern PATTERN_SINCT_calificadorDelTemaDelTexto = Pattern.compile("^.{61}(.{6}).*");
	private static Pattern PATTERN_SINCT_texto1 = Pattern.compile("^.{67}(.{70}).*");
	private static Pattern PATTERN_SINCT_texto2 = Pattern.compile("^.{137}(.{70}).*");
	private static Pattern PATTERN_SINCT_texto3 = Pattern.compile("^.{207}(.{70}).*");
	private static Pattern PATTERN_SINCT_texto4 = Pattern.compile("^.{277}(.{70}).*");
	private static Pattern PATTERN_SINCT_texto5 = Pattern.compile("^.{347}(.{70}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCT_tipoFactura_325_380_381_383_385_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoFactura_325_380_381_383_385_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_numeroDeFactura.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeFactura(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_codigoVendedor_SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVendedor_SU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_codigoComprador_BY_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoComprador_BY_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_numeroDeLinea.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLinea(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_calificadorDelTemaDelTexto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDelTemaDelTexto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_texto1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_texto2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_texto3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_texto4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCT_texto5.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto5(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public String getTipoFactura_325_380_381_383_385_() {
		return tipoFactura_325_380_381_383_385_;
	}

	/** 
	 * 
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
	 * 
	 */ 
	public String getNumeroDeFactura() {
		return numeroDeFactura;
	}

	/** 
	 * 
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
	 * 
	 */ 
	public String getCodigoVendedor_SU_() {
		return codigoVendedor_SU_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039V</td> <td>Código Vendedor (SU)</td> <td>C</td> <td>13</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoVendedor_SU_(String codigoVendedor_SU_) {
		this.codigoVendedor_SU_ = codigoVendedor_SU_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoComprador_BY_() {
		return codigoComprador_BY_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F3039C</td> <td>Código Comprador (BY)</td> <td>C</td> <td>13</td> <td>43</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoComprador_BY_(String codigoComprador_BY_) {
		this.codigoComprador_BY_ = codigoComprador_BY_;
	}

	/** 
	 * F1082L  - Número de Línea a la que se refiere el texto:  Si se está especificando una aclaración a pie de factura, el Número de Línea será cero. Si se trata de un apunte sobre una línea , el valor que aquí se grabe debe coincidir con el Número de línea de factura (campo F1082 del registro SINCL) al que haga referencia.
	 */ 
	public Integer getNumeroDeLinea() {
		return numeroDeLinea;
	}

	/** 
	 * F1082L  - Número de Línea a la que se refiere el texto:  Si se está especificando una aclaración a pie de factura, el Número de Línea será cero. Si se trata de un apunte sobre una línea , el valor que aquí se grabe debe coincidir con el Número de línea de factura (campo F1082 del registro SINCL) al que haga referencia.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F1082L</td> <td>Número de Línea</td> <td>N</td> <td>6</td> <td>56</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLinea(Integer numeroDeLinea) {
		this.numeroDeLinea = numeroDeLinea;
	}

	/** 
	 * F4451C  - Calificador del tema de Texto: Los valores posibles son:
	 */ 
	public String getCalificadorDelTemaDelTexto() {
		return calificadorDelTemaDelTexto;
	}

	/** 
	 * F4451C  - Calificador del tema de Texto: Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F4451C</td> <td>Calificador del Tema del Texto</td> <td>C</td> <td>6</td> <td>62</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDelTemaDelTexto(String calificadorDelTemaDelTexto) {
		this.calificadorDelTemaDelTexto = calificadorDelTemaDelTexto;
	}

	/** 
	 * 
	 */ 
	public String getTexto1() {
		return texto1;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F44401</td> <td>Texto 1</td> <td>C</td> <td>70</td> <td>68</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTexto1(String texto1) {
		this.texto1 = texto1;
	}

	/** 
	 * 
	 */ 
	public String getTexto2() {
		return texto2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F44402</td> <td>Texto 2</td> <td>C</td> <td>70</td> <td>138</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTexto2(String texto2) {
		this.texto2 = texto2;
	}

	/** 
	 * 
	 */ 
	public String getTexto3() {
		return texto3;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F44403</td> <td>Texto 3</td> <td>C</td> <td>70</td> <td>208</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTexto3(String texto3) {
		this.texto3 = texto3;
	}

	/** 
	 * 
	 */ 
	public String getTexto4() {
		return texto4;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F44404</td> <td>Texto 4</td> <td>C</td> <td>70</td> <td>278</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTexto4(String texto4) {
		this.texto4 = texto4;
	}

	/** 
	 * 
	 */ 
	public String getTexto5() {
		return texto5;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>F44405</td> <td>Texto 5</td> <td>C</td> <td>70</td> <td>348</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTexto5(String texto5) {
		this.texto5 = texto5;
	}

	/** 
	 * F4451C  - Calificador del tema de Texto: Los valores posibles son:
	 */
	public enum F4451C {
		INFORMACION_GENERA_AAI("AAI"),
		;
		
		private String value;
		
		private F4451C(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static F4451C enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}