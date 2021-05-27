package com.esferalia.aon.file.seres.udapa.sales.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1T entity.
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
 * 		 <td>ERE1T</td> <td>Observaciones</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class ERE1T {

	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private Integer numeroTexto;
	private String calificadorDelTemaDelTexto_AAI_DEL_;
	private String texto1;
	private String texto2;
	private String texto3;
	private String texto4;
	private String texto5;


	private static Pattern PATTERN_ERE1T_tipoDePedido_220_221_224_226_22E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ERE1T_numeroDePedido = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_ERE1T_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_ERE1T_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_ERE1T_numeroTexto = Pattern.compile("^.{63}(.{2}).*");
	private static Pattern PATTERN_ERE1T_calificadorDelTemaDelTexto_AAI_DEL_ = Pattern.compile("^.{65}(.{3}).*");
	private static Pattern PATTERN_ERE1T_texto1 = Pattern.compile("^.{68}(.{70}).*");
	private static Pattern PATTERN_ERE1T_texto2 = Pattern.compile("^.{138}(.{70}).*");
	private static Pattern PATTERN_ERE1T_texto3 = Pattern.compile("^.{208}(.{70}).*");
	private static Pattern PATTERN_ERE1T_texto4 = Pattern.compile("^.{278}(.{70}).*");
	private static Pattern PATTERN_ERE1T_texto5 = Pattern.compile("^.{348}(.{70}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1T_tipoDePedido_220_221_224_226_22E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDePedido_220_221_224_226_22E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_numeroDePedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_numeroTexto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroTexto(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_calificadorDelTemaDelTexto_AAI_DEL_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDelTemaDelTexto_AAI_DEL_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_texto1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_texto2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_texto3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_texto4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1T_texto5.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTexto5(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public String getTipoDePedido_220_221_224_226_22E_() {
		return tipoDePedido_220_221_224_226_22E_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1001T</td> <td>Tipo de Pedido (220, 221, 224, 226, 22E)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDePedido_220_221_224_226_22E_(String tipoDePedido_220_221_224_226_22E_) {
		this.tipoDePedido_220_221_224_226_22E_ = tipoDePedido_220_221_224_226_22E_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDePedido() {
		return numeroDePedido;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1004P</td> <td>Número de Pedido</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePedido(String numeroDePedido) {
		this.numeroDePedido = numeroDePedido;
	}

	/** 
	 * 
	 */ 
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039E</td> <td>Código Emisor  (MS)</td> <td>C</td> <td>17</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEmisor_MS_(String codigoEmisor_MS_) {
		this.codigoEmisor_MS_ = codigoEmisor_MS_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039R</td> <td>Código Receptor (MR)</td> <td>C</td> <td>17</td> <td>47</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroTexto() {
		return numeroTexto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1082T</td> <td>Número Texto</td> <td>N</td> <td>2</td> <td>64</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroTexto(Integer numeroTexto) {
		this.numeroTexto = numeroTexto;
	}

	/** 
	 * C4451C - Calificador del Tema del Texto (AAI , DEL): El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCalificadorDelTemaDelTexto_AAI_DEL_() {
		return calificadorDelTemaDelTexto_AAI_DEL_;
	}

	/** 
	 * C4451C - Calificador del Tema del Texto (AAI , DEL): El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C4451C</td> <td>Calificador del Tema del Texto (AAI , DEL)</td> <td>C</td> <td>3</td> <td>66</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDelTemaDelTexto_AAI_DEL_(String calificadorDelTemaDelTexto_AAI_DEL_) {
		this.calificadorDelTemaDelTexto_AAI_DEL_ = calificadorDelTemaDelTexto_AAI_DEL_;
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
	 * 		 <td>C44401</td> <td>Texto 1</td> <td>C</td> <td>70</td> <td>69</td> <td>M</td>
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
	 * 		 <td>C44402</td> <td>Texto 2</td> <td>C</td> <td>70</td> <td>139</td> <td>C</td>
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
	 * 		 <td>C44403</td> <td>Texto 3</td> <td>C</td> <td>70</td> <td>209</td> <td>C</td>
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
	 * 		 <td>C44404</td> <td>Texto 4</td> <td>C</td> <td>70</td> <td>279</td> <td>C</td>
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
	 * 		 <td>C44405</td> <td>Texto 5</td> <td>C</td> <td>70</td> <td>349</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTexto5(String texto5) {
		this.texto5 = texto5;
	}

	/** 
	 * C4451C - Calificador del Tema del Texto (AAI , DEL): El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum C4451C {
		INFORMACION_GENERA_AAI("AAI"),
		INFORMACION_DE_FACTURACIO_INV("INV"),
		INFORMACION_DE_ENTREG_DEL("DEL"),
		;
		
		private String value;
		
		private C4451C(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C4451C enumByValue(String value) {
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