package com.esferalia.aon.file.seres.udapa.delivery.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1G entity.
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
 * 		<td>SEH1G</th>
 * 		<td>Desglose Cantidad</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SEH1G {

	private String desgloseCantidadLineas;
	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeJerarquiaDeEmbalaje;
	private String numeroDeSub_jerarquiaDeEmbalaje;
	private String numeroDeLineaArticulo;
	private String contadorDesgloseLineas;
	private String codigoLugarDeEntrega;
	private String tipoCodigo;
	private String fechaDeEntrega_17_;
	private String cantidadDividida_11_;
	private String pesoEnvioDivididoEnKGM;


	private static java.util.regex.Pattern PATTERN_SEH1G_desgloseCantidadLineas = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_tipoAvisoDeExpedicion_351_35E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_numeroAvisoDeExpedicion = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_numeroDeJerarquiaDeEmbalaje = java.util.regex.Pattern.compile("^.{63}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_numeroDeSub_jerarquiaDeEmbalaje = java.util.regex.Pattern.compile("^.{75}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_numeroDeLineaArticulo = java.util.regex.Pattern.compile("^.{87}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_contadorDesgloseLineas = java.util.regex.Pattern.compile("^.{93}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_codigoLugarDeEntrega = java.util.regex.Pattern.compile("^.{97}(.{25}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_tipoCodigo = java.util.regex.Pattern.compile("^.{122}(.{3}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_fechaDeEntrega_17_ = java.util.regex.Pattern.compile("^.{125}(.{12}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_cantidadDividida_11_ = java.util.regex.Pattern.compile("^.{137}(.{16}).*");
	private static java.util.regex.Pattern PATTERN_SEH1G_pesoEnvioDivididoEnKGM = java.util.regex.Pattern.compile("^.{153}(.{16}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_SEH1G_desgloseCantidadLineas.matcher(value)).find()) {
			setDesgloseCantidadLineas(m.group(1));
		}
		if((m = PATTERN_SEH1G_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find()) {
			setTipoAvisoDeExpedicion_351_35E_(m.group(1));
		}
		if((m = PATTERN_SEH1G_numeroAvisoDeExpedicion.matcher(value)).find()) {
			setNumeroAvisoDeExpedicion(m.group(1));
		}
		if((m = PATTERN_SEH1G_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_SEH1G_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_SEH1G_numeroDeJerarquiaDeEmbalaje.matcher(value)).find()) {
			setNumeroDeJerarquiaDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1G_numeroDeSub_jerarquiaDeEmbalaje.matcher(value)).find()) {
			setNumeroDeSub_jerarquiaDeEmbalaje(m.group(1));
		}
		if((m = PATTERN_SEH1G_numeroDeLineaArticulo.matcher(value)).find()) {
			setNumeroDeLineaArticulo(m.group(1));
		}
		if((m = PATTERN_SEH1G_contadorDesgloseLineas.matcher(value)).find()) {
			setContadorDesgloseLineas(m.group(1));
		}
		if((m = PATTERN_SEH1G_codigoLugarDeEntrega.matcher(value)).find()) {
			setCodigoLugarDeEntrega(m.group(1));
		}
		if((m = PATTERN_SEH1G_tipoCodigo.matcher(value)).find()) {
			setTipoCodigo(m.group(1));
		}
		if((m = PATTERN_SEH1G_fechaDeEntrega_17_.matcher(value)).find()) {
			setFechaDeEntrega_17_(m.group(1));
		}
		if((m = PATTERN_SEH1G_cantidadDividida_11_.matcher(value)).find()) {
			setCantidadDividida_11_(m.group(1));
		}
		if((m = PATTERN_SEH1G_pesoEnvioDivididoEnKGM.matcher(value)).find()) {
			setPesoEnvioDivididoEnKGM(m.group(1));
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
	 * 		<td>SEH1G</th>
	 * 		<td>Desglose Cantidad Líneas</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDesgloseCantidadLineas() {
		return desgloseCantidadLineas;
	}
	public void setDesgloseCantidadLineas(String desgloseCantidadLineas) {
		this.desgloseCantidadLineas = desgloseCantidadLineas;
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
	 * 		<td>V1001T</th>
	 * 		<td>Tipo Aviso de Expedición (351/35E)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoAvisoDeExpedicion_351_35E_() {
		return tipoAvisoDeExpedicion_351_35E_;
	}
	public void setTipoAvisoDeExpedicion_351_35E_(String tipoAvisoDeExpedicion_351_35E_) {
		this.tipoAvisoDeExpedicion_351_35E_ = tipoAvisoDeExpedicion_351_35E_;
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
	 * 		<td>V1004P</th>
	 * 		<td>Número Aviso de Expedición</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroAvisoDeExpedicion() {
		return numeroAvisoDeExpedicion;
	}
	public void setNumeroAvisoDeExpedicion(String numeroAvisoDeExpedicion) {
		this.numeroAvisoDeExpedicion = numeroAvisoDeExpedicion;
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
	 * 		<td>V3039E</th>
	 * 		<td>Código Emisor  (MS)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>30</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}
	public void setCodigoEmisor_MS_(String codigoEmisor_MS_) {
		this.codigoEmisor_MS_ = codigoEmisor_MS_;
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
	 * 		<td>V3039R</th>
	 * 		<td>Código Receptor (MR)</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>47</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
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
	 * 		<td>V7164J</th>
	 * 		<td>Número de Jerarquía de Embalaje</th>
	 * 		<td>N</th>
	 * 		<td>12</th>
	 * 		<td>64</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeJerarquiaDeEmbalaje() {
		return numeroDeJerarquiaDeEmbalaje;
	}
	public void setNumeroDeJerarquiaDeEmbalaje(String numeroDeJerarquiaDeEmbalaje) {
		this.numeroDeJerarquiaDeEmbalaje = numeroDeJerarquiaDeEmbalaje;
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
	 * 		<td>V7166J</th>
	 * 		<td>Número de Sub-jerarquía de Embalaje</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>76</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeSub_jerarquiaDeEmbalaje() {
		return numeroDeSub_jerarquiaDeEmbalaje;
	}
	public void setNumeroDeSub_jerarquiaDeEmbalaje(String numeroDeSub_jerarquiaDeEmbalaje) {
		this.numeroDeSub_jerarquiaDeEmbalaje = numeroDeSub_jerarquiaDeEmbalaje;
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
	 * 		<td>V1082L</th>
	 * 		<td>Número de Línea Artículo</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>88</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeLineaArticulo() {
		return numeroDeLineaArticulo;
	}
	public void setNumeroDeLineaArticulo(String numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
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
	 * 		<td>V1082G</th>
	 * 		<td>Contador Desglose Líneas</th>
	 * 		<td>N</th>
	 * 		<td>4</th>
	 * 		<td>94</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getContadorDesgloseLineas() {
		return contadorDesgloseLineas;
	}
	public void setContadorDesgloseLineas(String contadorDesgloseLineas) {
		this.contadorDesgloseLineas = contadorDesgloseLineas;
	}

	/** 
	 * V3225L-Código Lugar de Entrega: Lugar en el que se entrega el artículo. El campo corresponde a un Punto Operacional.
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
	 * 		<td>V3225L</th>
	 * 		<td>Código  Lugar de Entrega</th>
	 * 		<td>C</th>
	 * 		<td>25</th>
	 * 		<td>98</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoLugarDeEntrega() {
		return codigoLugarDeEntrega;
	}
	public void setCodigoLugarDeEntrega(String codigoLugarDeEntrega) {
		this.codigoLugarDeEntrega = codigoLugarDeEntrega;
	}

	/** 
	 * V3055L-Tipo de Código: Los valores posibles son:
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
	 * 		<td>V3055L</th>
	 * 		<td>Tipo Código</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>123</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoCodigo() {
		return tipoCodigo;
	}
	public void setTipoCodigo(String tipoCodigo) {
		this.tipoCodigo = tipoCodigo;
	}

	/** 
	 * V2380J-Fecha de Entrega: Fecha de entrega de la mercancía. Su formato, AAAAMMDD o AAAAMMDDHHMM.
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
	 * 		<td>V2380J</th>
	 * 		<td>Fecha de Entrega (17)</th>
	 * 		<td>C</th>
	 * 		<td>12</th>
	 * 		<td>126</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFechaDeEntrega_17_() {
		return fechaDeEntrega_17_;
	}
	public void setFechaDeEntrega_17_(String fechaDeEntrega_17_) {
		this.fechaDeEntrega_17_ = fechaDeEntrega_17_;
	}

	/** 
	 * V6060V-Cantidad Dividida o Separada: Cantidad que se deja en el punto de entrega V3225L.
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
	 * 		<td>V6060V</th>
	 * 		<td>Cantidad Dividida (11)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>138</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCantidadDividida_11_() {
		return cantidadDividida_11_;
	}
	public void setCantidadDividida_11_(String cantidadDividida_11_) {
		this.cantidadDividida_11_ = cantidadDividida_11_;
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
	 * 		<td>V6060P</th>
	 * 		<td>Peso Envío Dividido en KGM</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>154</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPesoEnvioDivididoEnKGM() {
		return pesoEnvioDivididoEnKGM;
	}
	public void setPesoEnvioDivididoEnKGM(String pesoEnvioDivididoEnKGM) {
		this.pesoEnvioDivididoEnKGM = pesoEnvioDivididoEnKGM;
	}

	public enum TipoDeCodigo {
		EAN__ASOCIACION_INTERNACIONAL_DE_NUMERACION_DE_ARTICULOS_9("9"),
		;
		
		private String value;
		
		private TipoDeCodigo(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	public enum CalificadorDelNumeroDeIdentidad {
		NUMERO_EAN_UP_EU("EU"),
		N__SE_SERIE_DEL_CONTINENTE_DE_LA_EXPEDICIO_BJ("BJ"),
		N__DE_LA_EXPEDICIO_BX("BX"),
		;
		
		private String value;
		
		private CalificadorDelNumeroDeIdentidad(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

}