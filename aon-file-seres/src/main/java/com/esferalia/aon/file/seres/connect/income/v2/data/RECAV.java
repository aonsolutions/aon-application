package com.esferalia.aon.file.seres.connect.income.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI RECAV entity.
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
 * 		 <td>RECAV</td> <td>Variaciones confirmación de recepción</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class RECAV {

	private String calificadorDeCantidad;
	private Double cantidad;
	private String codigoDiscrepancia;
	private String codigoRazonDelCambio;


	private static Pattern PATTERN_RECAV_calificadorDeCantidad = Pattern.compile("^.{6}(.{3}).*");
	private static Pattern PATTERN_RECAV_cantidad = Pattern.compile("^.{9}(.{16}).*");
	private static Pattern PATTERN_RECAV_codigoDiscrepancia = Pattern.compile("^.{25}(.{3}).*");
	private static Pattern PATTERN_RECAV_codigoRazonDelCambio = Pattern.compile("^.{28}(.{3}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_RECAV_calificadorDeCantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDeCantidad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAV_cantidad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidad(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAV_codigoDiscrepancia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDiscrepancia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAV_codigoRazonDelCambio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoRazonDelCambio(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Calificador de Cantidad: El campo se corresponde con el elemento 6063. Los valores posibles son:
	 */ 
	public String getCalificadorDeCantidad() {
		return calificadorDeCantidad;
	}

	/** 
	 * 2 - Calificador de Cantidad: El campo se corresponde con el elemento 6063. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Calificador de Cantidad</td> <td>C</td> <td>3</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDeCantidad(String calificadorDeCantidad) {
		this.calificadorDeCantidad = calificadorDeCantidad;
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
	 * 		 <td>3</td> <td>Cantidad</td> <td>N(12,3)</td> <td>16</td> <td>10</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	/** 
	 * 46 - Cantidad entregada
	 */ 
	public String getCodigoDiscrepancia() {
		return codigoDiscrepancia;
	}

	/** 
	 * 46 - Cantidad entregada
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Código Discrepancia</td> <td>C</td> <td>3</td> <td>26</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDiscrepancia(String codigoDiscrepancia) {
		this.codigoDiscrepancia = codigoDiscrepancia;
	}

	/** 
	 * 195 - Recibida, no aceptada, para ser devuelta
	 */ 
	public String getCodigoRazonDelCambio() {
		return codigoRazonDelCambio;
	}

	/** 
	 * 195 - Recibida, no aceptada, para ser devuelta
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Código Razón del Cambio</td> <td>C</td> <td>3</td> <td>29</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoRazonDelCambio(String codigoRazonDelCambio) {
		this.codigoRazonDelCambio = codigoRazonDelCambio;
	}

	/** 
	 * 2 - Calificador de Cantidad: El campo se corresponde con el elemento 6063. Los valores posibles son:
	 */
	public enum RECAV_2 {
		CANTIDAD_ENVIADA_12("12"),
		CANTIDAD_PEDIDA_21("21"),
		CANTIDAD_ENTREGADA_46("46"),
		RECIBIDA__NO_ACEPTADA__PARA_SER_DEVUELTA_195("195"),
		RECIBIDA__NO_ACEPTADA__PARA_SER_DESTRUIDA_196("196"),
		;
		
		private String value;
		
		private RECAV_2(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static RECAV_2 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 4 - Código de Discrepancia: El campo se corresponde con el elemento 4221. Los valores posibles son:
	 */
	public enum RECAV_4 {
		EXPEDIDO_EN_DEMASIA_AC("AC"),
		DESPACHADO_SIN_AVISO_AE("AE"),
		BIENES_DA_ADOS_AF("AF"),
		ENTREGADO_TARDE_AG("AG"),
		ENTREGA_PARCIAL__QUE_TIENE_QUE_COMPLETARSE_BP("BP"),
		ENTREGA_PARCIAL_CONSIDERADA_COMPLETA_CP("CP"),
		;
		
		private String value;
		
		private RECAV_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static RECAV_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 5 - Código Razón del Cambio: El campo se corresponde con el elemento 4295. Los valores posibles son:
	 */
	public enum RECAV_5 {
		ARTICULO_NO_PEDIDO_AT("AT"),
		CODIGO_ARTICULO_DESCONOCIDO__CODIGO_EAN__AUE("AUE"),
		CODIGO_DE_BARRAS_NO_LEGIBLE__CODIGO_EAN__BN("BN"),
		DA_ADO__CODIGO_EAN__DME("DME"),
		EMPAQUETADO_DIFERENTE_PC("PC"),
		;
		
		private String value;
		
		private RECAV_5(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static RECAV_5 enumByValue(String value) {
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