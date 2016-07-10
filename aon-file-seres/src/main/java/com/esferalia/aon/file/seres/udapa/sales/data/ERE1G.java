package com.esferalia.aon.file.seres.udapa.sales.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1G entity.
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
 * 		 <td>ERE1G</td> <td>Desglose de Cantidad</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class ERE1G {

	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private Integer numeroDeLineaArticulo;
	private Integer contadorDesglose;
	private String codigoLugarDeEntrega_7_;
	private Double cantidadDividida_11_;


	private static Pattern PATTERN_ERE1G_tipoDePedido_220_221_224_226_22E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ERE1G_numeroDePedido = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_ERE1G_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_ERE1G_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_ERE1G_numeroDeLineaArticulo = Pattern.compile("^.{63}(.{6}).*");
	private static Pattern PATTERN_ERE1G_contadorDesglose = Pattern.compile("^.{69}(.{4}).*");
	private static Pattern PATTERN_ERE1G_codigoLugarDeEntrega_7_ = Pattern.compile("^.{73}(.{25}).*");
	private static Pattern PATTERN_ERE1G_cantidadDividida_11_ = Pattern.compile("^.{98}(.{16}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1G_tipoDePedido_220_221_224_226_22E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDePedido_220_221_224_226_22E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_numeroDePedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_numeroDeLineaArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaArticulo(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_contadorDesglose.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setContadorDesglose(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_codigoLugarDeEntrega_7_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoLugarDeEntrega_7_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1G_cantidadDividida_11_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadDividida_11_(Double.valueOf(m.group(1).trim()));
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
	 * 		 <td>C1082L</td> <td>Número de Línea Artículo</td> <td>N</td> <td>6</td> <td>64</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaArticulo(Integer numeroDeLineaArticulo) {
		this.numeroDeLineaArticulo = numeroDeLineaArticulo;
	}

	/** 
	 * 
	 */ 
	public Integer getContadorDesglose() {
		return contadorDesglose;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1082G</td> <td>Contador Desglose</td> <td>N</td> <td>4</td> <td>70</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setContadorDesglose(Integer contadorDesglose) {
		this.contadorDesglose = contadorDesglose;
	}

	/** 
	 * C3225L - Código Lugar de Entrega (7): Código EAN  del  lugar donde se entrega la mercancía.
	 */ 
	public String getCodigoLugarDeEntrega_7_() {
		return codigoLugarDeEntrega_7_;
	}

	/** 
	 * C3225L - Código Lugar de Entrega (7): Código EAN  del  lugar donde se entrega la mercancía.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3225L</td> <td>Código Lugar de Entrega (7)</td> <td>C</td> <td>25</td> <td>74</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoLugarDeEntrega_7_(String codigoLugarDeEntrega_7_) {
		this.codigoLugarDeEntrega_7_ = codigoLugarDeEntrega_7_;
	}

	/** 
	 * C6060V - Cantidad Dividida (11):Cantidad que se entrega.
	 */ 
	public Double getCantidadDividida_11_() {
		return cantidadDividida_11_;
	}

	/** 
	 * C6060V - Cantidad Dividida (11):Cantidad que se entrega.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C6060V</td> <td>Cantidad Dividida (11)</td> <td>N(12,3)</td> <td>16</td> <td>99</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadDividida_11_(Double cantidadDividida_11_) {
		this.cantidadDividida_11_ = cantidadDividida_11_;
	}

}