package com.esferalia.aon.file.seres.udapa.invoice.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCU entity.
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
 * 		<td>SINCU</th>
 * 		<td>Observaciones Línea</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SINCU {

	private String textosLinea;
	private String tipoFactura_325_380_381_383_385_;
	private String numeroDeFactura;
	private String codigoVendedor_SU_;
	private String codigoComprador_BY_;
	private String numeroDeLinea;
	private String calificadorDelTemaDelTexto;
	private String texto1;
	private String texto2;
	private String texto3;
	private String texto4;
	private String texto5;


	private static java.util.regex.Pattern PATTERN_SINCU_textosLinea = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_tipoFactura_325_380_381_383_385_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_numeroDeFactura = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_codigoVendedor_SU_ = java.util.regex.Pattern.compile("^.{29}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_codigoComprador_BY_ = java.util.regex.Pattern.compile("^.{42}(.{13}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_numeroDeLinea = java.util.regex.Pattern.compile("^.{55}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_calificadorDelTemaDelTexto = java.util.regex.Pattern.compile("^.{61}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_texto1 = java.util.regex.Pattern.compile("^.{67}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_texto2 = java.util.regex.Pattern.compile("^.{137}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_texto3 = java.util.regex.Pattern.compile("^.{207}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_texto4 = java.util.regex.Pattern.compile("^.{277}(.{70}).*");
	private static java.util.regex.Pattern PATTERN_SINCU_texto5 = java.util.regex.Pattern.compile("^.{347}(.{70}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SINCU_textosLinea.matcher(value)).find()) {
			setTextosLinea(m.group(1));
		}
		if((m = PATTERN_SINCU_tipoFactura_325_380_381_383_385_.matcher(value)).find()) {
			setTipoFactura_325_380_381_383_385_(m.group(1));
		}
		if((m = PATTERN_SINCU_numeroDeFactura.matcher(value)).find()) {
			setNumeroDeFactura(m.group(1));
		}
		if((m = PATTERN_SINCU_codigoVendedor_SU_.matcher(value)).find()) {
			setCodigoVendedor_SU_(m.group(1));
		}
		if((m = PATTERN_SINCU_codigoComprador_BY_.matcher(value)).find()) {
			setCodigoComprador_BY_(m.group(1));
		}
		if((m = PATTERN_SINCU_numeroDeLinea.matcher(value)).find()) {
			setNumeroDeLinea(m.group(1));
		}
		if((m = PATTERN_SINCU_calificadorDelTemaDelTexto.matcher(value)).find()) {
			setCalificadorDelTemaDelTexto(m.group(1));
		}
		if((m = PATTERN_SINCU_texto1.matcher(value)).find()) {
			setTexto1(m.group(1));
		}
		if((m = PATTERN_SINCU_texto2.matcher(value)).find()) {
			setTexto2(m.group(1));
		}
		if((m = PATTERN_SINCU_texto3.matcher(value)).find()) {
			setTexto3(m.group(1));
		}
		if((m = PATTERN_SINCU_texto4.matcher(value)).find()) {
			setTexto4(m.group(1));
		}
		if((m = PATTERN_SINCU_texto5.matcher(value)).find()) {
			setTexto5(m.group(1));
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
	 * 		<td>SINCT</th>
	 * 		<td>Textos Línea</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTextosLinea() {
		return textosLinea;
	}
	public void setTextosLinea(String textosLinea) {
		this.textosLinea = textosLinea;
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
	 * F1082L  - Número de Línea a la que se refiere el texto: Aquí se graba el Número de línea de factura (campo F1082 del registro SINCL) al que haga referencia.
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
	 * F4451C  - Calificador del tema de Texto: Los valores posibles son:
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
	 * 		<td>F4451C</th>
	 * 		<td>Calificador del Tema del Texto</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>62</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorDelTemaDelTexto() {
		return calificadorDelTemaDelTexto;
	}
	public void setCalificadorDelTemaDelTexto(String calificadorDelTemaDelTexto) {
		this.calificadorDelTemaDelTexto = calificadorDelTemaDelTexto;
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
	 * 		<td>F44401</th>
	 * 		<td>Texto 1</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>68</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTexto1() {
		return texto1;
	}
	public void setTexto1(String texto1) {
		this.texto1 = texto1;
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
	 * 		<td>F44402</th>
	 * 		<td>Texto 2</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>138</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTexto2() {
		return texto2;
	}
	public void setTexto2(String texto2) {
		this.texto2 = texto2;
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
	 * 		<td>F44403</th>
	 * 		<td>Texto 3</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>208</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTexto3() {
		return texto3;
	}
	public void setTexto3(String texto3) {
		this.texto3 = texto3;
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
	 * 		<td>F44404</th>
	 * 		<td>Texto 4</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>278</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTexto4() {
		return texto4;
	}
	public void setTexto4(String texto4) {
		this.texto4 = texto4;
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
	 * 		<td>F44405</th>
	 * 		<td>Texto 5</th>
	 * 		<td>C</th>
	 * 		<td>70</th>
	 * 		<td>348</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTexto5() {
		return texto5;
	}
	public void setTexto5(String texto5) {
		this.texto5 = texto5;
	}

	public enum CalificadorDelTemaDeTexto {
		INFORMACION_GENERA_AAI("AAI"),
		;
		
		private String value;
		
		private CalificadorDelTemaDeTexto(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}