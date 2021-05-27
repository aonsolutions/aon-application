package com.esferalia.aon.file.seres.udapa.delivery.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
 * 		 <td>SEH1G</td> <td>Desglose Cantidad</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1G {

	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private Integer numeroDeJerarquiaDeEmbalaje;
	private String numeroDeSub_jerarquiaDeEmbalaje;
	private Integer numeroDeLineaArticulo;
	private Integer contadorDesgloseLineas;
	private String codigoLugarDeEntrega;
	private String tipoCodigo;
	private String fechaDeEntrega_17_;
	private Double cantidadDividida_11_;
	private Double pesoEnvioDivididoEnKGM;


	private static Pattern PATTERN_SEH1G_tipoAvisoDeExpedicion_351_35E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SEH1G_numeroAvisoDeExpedicion = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SEH1G_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_SEH1G_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_SEH1G_numeroDeJerarquiaDeEmbalaje = Pattern.compile("^.{63}(.{12}).*");
	private static Pattern PATTERN_SEH1G_numeroDeSub_jerarquiaDeEmbalaje = Pattern.compile("^.{75}(.{12}).*");
	private static Pattern PATTERN_SEH1G_numeroDeLineaArticulo = Pattern.compile("^.{87}(.{6}).*");
	private static Pattern PATTERN_SEH1G_contadorDesgloseLineas = Pattern.compile("^.{93}(.{4}).*");
	private static Pattern PATTERN_SEH1G_codigoLugarDeEntrega = Pattern.compile("^.{97}(.{25}).*");
	private static Pattern PATTERN_SEH1G_tipoCodigo = Pattern.compile("^.{122}(.{3}).*");
	private static Pattern PATTERN_SEH1G_fechaDeEntrega_17_ = Pattern.compile("^.{125}(.{12}).*");
	private static Pattern PATTERN_SEH1G_cantidadDividida_11_ = Pattern.compile("^.{137}(.{16}).*");
	private static Pattern PATTERN_SEH1G_pesoEnvioDivididoEnKGM = Pattern.compile("^.{153}(.{16}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1G_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoAvisoDeExpedicion_351_35E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_numeroAvisoDeExpedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroAvisoDeExpedicion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_numeroDeJerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeJerarquiaDeEmbalaje(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_numeroDeSub_jerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeSub_jerarquiaDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_numeroDeLineaArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_contadorDesgloseLineas.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setContadorDesgloseLineas(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_codigoLugarDeEntrega.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoLugarDeEntrega(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_tipoCodigo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoCodigo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_fechaDeEntrega_17_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeEntrega_17_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_cantidadDividida_11_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadDividida_11_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1G_pesoEnvioDivididoEnKGM.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPesoEnvioDivididoEnKGM(Double.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public String getTipoAvisoDeExpedicion_351_35E_() {
		return tipoAvisoDeExpedicion_351_35E_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1001T</td> <td>Tipo Aviso de Expedición (351/35E)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoAvisoDeExpedicion_351_35E_(String tipoAvisoDeExpedicion_351_35E_) {
		this.tipoAvisoDeExpedicion_351_35E_ = tipoAvisoDeExpedicion_351_35E_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroAvisoDeExpedicion() {
		return numeroAvisoDeExpedicion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1004P</td> <td>Número Aviso de Expedición</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroAvisoDeExpedicion(String numeroAvisoDeExpedicion) {
		this.numeroAvisoDeExpedicion = numeroAvisoDeExpedicion;
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
	 * 		 <td>V3039E</td> <td>Código Emisor  (MS)</td> <td>C</td> <td>17</td> <td>30</td> <td>M</td>
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
	 * 		 <td>V3039R</td> <td>Código Receptor (MR)</td> <td>C</td> <td>17</td> <td>47</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeJerarquiaDeEmbalaje() {
		return numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7164J</td> <td>Número de Jerarquía de Embalaje</td> <td>N</td> <td>12</td> <td>64</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeJerarquiaDeEmbalaje(Integer numeroDeJerarquiaDeEmbalaje) {
		this.numeroDeJerarquiaDeEmbalaje = numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeSub_jerarquiaDeEmbalaje() {
		return numeroDeSub_jerarquiaDeEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V7166J</td> <td>Número de Sub-jerarquía de Embalaje</td> <td>C</td> <td>12</td> <td>76</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeSub_jerarquiaDeEmbalaje(String numeroDeSub_jerarquiaDeEmbalaje) {
		this.numeroDeSub_jerarquiaDeEmbalaje = numeroDeSub_jerarquiaDeEmbalaje;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1082L</td> <td>Número de Línea Artículo</td> <td>N</td> <td>6</td> <td>88</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaArticulo(Integer numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
	}

	/** 
	 * 
	 */ 
	public Integer getContadorDesgloseLineas() {
		return contadorDesgloseLineas;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1082G</td> <td>Contador Desglose Líneas</td> <td>N</td> <td>4</td> <td>94</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setContadorDesgloseLineas(Integer contadorDesgloseLineas) {
		this.contadorDesgloseLineas = contadorDesgloseLineas;
	}

	/** 
	 * V3225L-Código Lugar de Entrega: Lugar en el que se entrega el artículo. El campo corresponde a un Punto Operacional.
	 */ 
	public String getCodigoLugarDeEntrega() {
		return codigoLugarDeEntrega;
	}

	/** 
	 * V3225L-Código Lugar de Entrega: Lugar en el que se entrega el artículo. El campo corresponde a un Punto Operacional.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3225L</td> <td>Código  Lugar de Entrega</td> <td>C</td> <td>25</td> <td>98</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoLugarDeEntrega(String codigoLugarDeEntrega) {
		this.codigoLugarDeEntrega = codigoLugarDeEntrega;
	}

	/** 
	 * V3055L-Tipo de Código: Los valores posibles son:
	 */ 
	public String getTipoCodigo() {
		return tipoCodigo;
	}

	/** 
	 * V3055L-Tipo de Código: Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3055L</td> <td>Tipo Código</td> <td>C</td> <td>3</td> <td>123</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoCodigo(String tipoCodigo) {
		this.tipoCodigo = tipoCodigo;
	}

	/** 
	 * V2380J-Fecha de Entrega: Fecha de entrega de la mercancía. Su formato, AAAAMMDD o AAAAMMDDHHMM.
	 */ 
	public String getFechaDeEntrega_17_() {
		return fechaDeEntrega_17_;
	}

	/** 
	 * V2380J-Fecha de Entrega: Fecha de entrega de la mercancía. Su formato, AAAAMMDD o AAAAMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V2380J</td> <td>Fecha de Entrega (17)</td> <td>C</td> <td>12</td> <td>126</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeEntrega_17_(String fechaDeEntrega_17_) {
		this.fechaDeEntrega_17_ = fechaDeEntrega_17_;
	}

	/** 
	 * V6060V-Cantidad Dividida o Separada: Cantidad que se deja en el punto de entrega V3225L.
	 */ 
	public Double getCantidadDividida_11_() {
		return cantidadDividida_11_;
	}

	/** 
	 * V6060V-Cantidad Dividida o Separada: Cantidad que se deja en el punto de entrega V3225L.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6060V</td> <td>Cantidad Dividida (11)</td> <td>N(12,3)</td> <td>16</td> <td>138</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadDividida_11_(Double cantidadDividida_11_) {
		this.cantidadDividida_11_ = cantidadDividida_11_;
	}

	/** 
	 * 
	 */ 
	public Double getPesoEnvioDivididoEnKGM() {
		return pesoEnvioDivididoEnKGM;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V6060P</td> <td>Peso Envío Dividido en KGM</td> <td>N(12,3)</td> <td>16</td> <td>154</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPesoEnvioDivididoEnKGM(Double pesoEnvioDivididoEnKGM) {
		this.pesoEnvioDivididoEnKGM = pesoEnvioDivididoEnKGM;
	}

	/** 
	 * V3055L-Tipo de Código: Los valores posibles son:
	 */
	public enum V3055L {
		EAN__ASOCIACION_INTERNACIONAL_DE_NUMERACION_DE_ARTICULOS_9("9"),
		;
		
		private String value;
		
		private V3055L(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V3055L enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V7405L-Calificador  del número de identidad: Los valores posibles son:
	 */
	public enum V7405L {
		NUMERO_EAN_UP_EU("EU"),
		N__SE_SERIE_DEL_CONTINENTE_DE_LA_EXPEDICIO_BJ("BJ"),
		N__DE_LA_EXPEDICIO_BX("BX"),
		;
		
		private String value;
		
		private V7405L(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V7405L enumByValue(String value) {
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