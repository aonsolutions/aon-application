package com.esferalia.aon.file.seres.udapa.sales.data;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1G entity.
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
 * 		<td>ERE1G</th>
 * 		<td>Desglose de Cantidad</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class ERE1G {

	private String desgloseDeCantidad;
	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String numeroDeLineaArticulo;
	private String contadorDesglose;
	private String codigoLugarDeEntrega_7_;
	private String cantidadDividida_11_;


	private static java.util.regex.Pattern PATTERN_ERE1G_desgloseDeCantidad = java.util.regex.Pattern.compile("^(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_tipoDePedido_220_221_224_226_22E_ = java.util.regex.Pattern.compile("^.{6}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_numeroDePedido = java.util.regex.Pattern.compile("^.{12}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_codigoEmisor_MS_ = java.util.regex.Pattern.compile("^.{29}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_codigoReceptor_MR_ = java.util.regex.Pattern.compile("^.{46}(.{17}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_numeroDeLineaArticulo = java.util.regex.Pattern.compile("^.{63}(.{6}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_contadorDesglose = java.util.regex.Pattern.compile("^.{69}(.{4}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_codigoLugarDeEntrega_7_ = java.util.regex.Pattern.compile("^.{73}(.{25}).*");
	private static java.util.regex.Pattern PATTERN_ERE1G_cantidadDividida_11_ = java.util.regex.Pattern.compile("^.{98}(.{16}).*");

	public void parse(String value) {
		java.util.regex.Matcher m;
		if((m = PATTERN_ERE1G_desgloseDeCantidad.matcher(value)).find()) {
			setDesgloseDeCantidad(m.group(1));
		}
		if((m = PATTERN_ERE1G_tipoDePedido_220_221_224_226_22E_.matcher(value)).find()) {
			setTipoDePedido_220_221_224_226_22E_(m.group(1));
		}
		if((m = PATTERN_ERE1G_numeroDePedido.matcher(value)).find()) {
			setNumeroDePedido(m.group(1));
		}
		if((m = PATTERN_ERE1G_codigoEmisor_MS_.matcher(value)).find()) {
			setCodigoEmisor_MS_(m.group(1));
		}
		if((m = PATTERN_ERE1G_codigoReceptor_MR_.matcher(value)).find()) {
			setCodigoReceptor_MR_(m.group(1));
		}
		if((m = PATTERN_ERE1G_numeroDeLineaArticulo.matcher(value)).find()) {
			setNumeroDeLineaArticulo(m.group(1));
		}
		if((m = PATTERN_ERE1G_contadorDesglose.matcher(value)).find()) {
			setContadorDesglose(m.group(1));
		}
		if((m = PATTERN_ERE1G_codigoLugarDeEntrega_7_.matcher(value)).find()) {
			setCodigoLugarDeEntrega_7_(m.group(1));
		}
		if((m = PATTERN_ERE1G_cantidadDividida_11_.matcher(value)).find()) {
			setCantidadDividida_11_(m.group(1));
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
	 * 		<td>ERE1G</th>
	 * 		<td>Desglose de Cantidad</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDesgloseDeCantidad() {
		return desgloseDeCantidad;
	}
	public void setDesgloseDeCantidad(String desgloseDeCantidad) {
		this.desgloseDeCantidad = desgloseDeCantidad;
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
	 * 		<td>C1001T</th>
	 * 		<td>Tipo de Pedido (220, 221, 224, 226, 22E)</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>7</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getTipoDePedido_220_221_224_226_22E_() {
		return tipoDePedido_220_221_224_226_22E_;
	}
	public void setTipoDePedido_220_221_224_226_22E_(String tipoDePedido_220_221_224_226_22E_) {
		this.tipoDePedido_220_221_224_226_22E_ = tipoDePedido_220_221_224_226_22E_;
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
	 * 		<td>C1004P</th>
	 * 		<td>Número de Pedido</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>13</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDePedido() {
		return numeroDePedido;
	}
	public void setNumeroDePedido(String numeroDePedido) {
		this.numeroDePedido = numeroDePedido;
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
	 * 		<td>C3039E</th>
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
	 * 		<td>C3039R</th>
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
	 * 		<td>C1082L</th>
	 * 		<td>Número de Línea Artículo</th>
	 * 		<td>N</th>
	 * 		<td>6</th>
	 * 		<td>64</th>
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
	 * 		<td>C1082G</th>
	 * 		<td>Contador Desglose</th>
	 * 		<td>N</th>
	 * 		<td>4</th>
	 * 		<td>70</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getContadorDesglose() {
		return contadorDesglose;
	}
	public void setContadorDesglose(String contadorDesglose) {
		this.contadorDesglose = contadorDesglose;
	}

	/** 
	 * C3225L - Código Lugar de Entrega (7): Código EAN  del  lugar donde se entrega la mercancía.
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
	 * 		<td>C3225L</th>
	 * 		<td>Código Lugar de Entrega (7)</th>
	 * 		<td>C</th>
	 * 		<td>25</th>
	 * 		<td>74</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoLugarDeEntrega_7_() {
		return codigoLugarDeEntrega_7_;
	}
	public void setCodigoLugarDeEntrega_7_(String codigoLugarDeEntrega_7_) {
		this.codigoLugarDeEntrega_7_ = codigoLugarDeEntrega_7_;
	}

	/** 
	 * C6060V - Cantidad Dividida (11):Cantidad que se entrega.
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
	 * 		<td>C6060V</th>
	 * 		<td>Cantidad Dividida (11)</th>
	 * 		<td>N(12,3)</th>
	 * 		<td>16</th>
	 * 		<td>99</th>
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

}