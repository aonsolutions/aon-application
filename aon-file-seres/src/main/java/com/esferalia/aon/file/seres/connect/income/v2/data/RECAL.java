package com.esferalia.aon.file.seres.connect.income.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI RECAL entity.
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
 * 		 <td>RECAL</td> <td>Detalle confirmación de recepción</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class RECAL {

	private Integer numeroDeLinea;
	private String codigoEANDelArticulo;
	private String numeroDeArticuloDelProveedor_SA_;
	private String numeroVariablePromocional_PV_;
	private String codigoDUN_14_ADU_;
	private String codigoACU_ACU_;
	private String numeroDeLote_NB_;
	private String numeroDeArticuloDelComprador_IN_;
	private Double cantidadEnviada_12_;
	private Double cantidadRecibidaYAceptada;
	private String unidadDeMedidaCantidadEnviada;
	private Double unidadesDeConsumoEnUnidadDeExpedicion_59_;
	private String identificadorProducto_lineaPedido_MP_;
	private String categoriaProducto_GB_;
	private String numeroArticuloFabricante_MF_;
	private String numeroDeSerieDelArticulo_SN_;
	private String numeroDeAlbaran_DQ_AAK_;
	private Integer numeroDeLineaDelAlbaran_DQ_AAK_;


	private static Pattern PATTERN_RECAL_numeroDeLinea = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_RECAL_codigoEANDelArticulo = Pattern.compile("^.{12}(.{15}).*");
	private static Pattern PATTERN_RECAL_numeroDeArticuloDelProveedor_SA_ = Pattern.compile("^.{27}(.{35}).*");
	private static Pattern PATTERN_RECAL_numeroVariablePromocional_PV_ = Pattern.compile("^.{62}(.{35}).*");
	private static Pattern PATTERN_RECAL_codigoDUN_14_ADU_ = Pattern.compile("^.{97}(.{35}).*");
	private static Pattern PATTERN_RECAL_codigoACU_ACU_ = Pattern.compile("^.{132}(.{35}).*");
	private static Pattern PATTERN_RECAL_numeroDeLote_NB_ = Pattern.compile("^.{167}(.{35}).*");
	private static Pattern PATTERN_RECAL_numeroDeArticuloDelComprador_IN_ = Pattern.compile("^.{202}(.{35}).*");
	private static Pattern PATTERN_RECAL_cantidadEnviada_12_ = Pattern.compile("^.{237}(.{16}).*");
	private static Pattern PATTERN_RECAL_cantidadRecibidaYAceptada = Pattern.compile("^.{253}(.{16}).*");
	private static Pattern PATTERN_RECAL_unidadDeMedidaCantidadEnviada = Pattern.compile("^.{269}(.{3}).*");
	private static Pattern PATTERN_RECAL_unidadesDeConsumoEnUnidadDeExpedicion_59_ = Pattern.compile("^.{272}(.{16}).*");
	private static Pattern PATTERN_RECAL_identificadorProducto_lineaPedido_MP_ = Pattern.compile("^.{288}(.{15}).*");
	private static Pattern PATTERN_RECAL_categoriaProducto_GB_ = Pattern.compile("^.{303}(.{15}).*");
	private static Pattern PATTERN_RECAL_numeroArticuloFabricante_MF_ = Pattern.compile("^.{318}(.{35}).*");
	private static Pattern PATTERN_RECAL_numeroDeSerieDelArticulo_SN_ = Pattern.compile("^.{353}(.{35}).*");
	private static Pattern PATTERN_RECAL_numeroDeAlbaran_DQ_AAK_ = Pattern.compile("^.{388}(.{17}).*");
	private static Pattern PATTERN_RECAL_numeroDeLineaDelAlbaran_DQ_AAK_ = Pattern.compile("^.{405}(.{6}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_RECAL_numeroDeLinea.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLinea(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_codigoEANDelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEANDelArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_numeroDeArticuloDelProveedor_SA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeArticuloDelProveedor_SA_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_numeroVariablePromocional_PV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroVariablePromocional_PV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_codigoDUN_14_ADU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDUN_14_ADU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_codigoACU_ACU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoACU_ACU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_numeroDeLote_NB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLote_NB_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_numeroDeArticuloDelComprador_IN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeArticuloDelComprador_IN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_cantidadEnviada_12_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadEnviada_12_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_cantidadRecibidaYAceptada.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadRecibidaYAceptada(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_unidadDeMedidaCantidadEnviada.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaCantidadEnviada(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_unidadesDeConsumoEnUnidadDeExpedicion_59_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadesDeConsumoEnUnidadDeExpedicion_59_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_identificadorProducto_lineaPedido_MP_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificadorProducto_lineaPedido_MP_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_categoriaProducto_GB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCategoriaProducto_GB_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_numeroArticuloFabricante_MF_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroArticuloFabricante_MF_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_numeroDeSerieDelArticulo_SN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeSerieDelArticulo_SN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_numeroDeAlbaran_DQ_AAK_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeAlbaran_DQ_AAK_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAL_numeroDeLineaDelAlbaran_DQ_AAK_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaDelAlbaran_DQ_AAK_(Integer.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 
	 */ 
	public Integer getNumeroDeLinea() {
		return numeroDeLinea;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número de línea</td> <td>N</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLinea(Integer numeroDeLinea) {
		this.numeroDeLinea = numeroDeLinea;
	}

	/** 
	 * 
	 */ 
	public String getCodigoEANDelArticulo() {
		return codigoEANDelArticulo;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Código EAN del articulo</td> <td>C</td> <td>15</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoEANDelArticulo(String codigoEANDelArticulo) {
		this.codigoEANDelArticulo = codigoEANDelArticulo;
	}

	/** 
	 * 14 - Identificador producto/línea pedido: Este identificador se utiliza solo para Carrefour Servicios Generales. Se compone del número de pedido más la línea del pedido
	 */ 
	public String getNumeroDeArticuloDelProveedor_SA_() {
		return numeroDeArticuloDelProveedor_SA_;
	}

	/** 
	 * 14 - Identificador producto/línea pedido: Este identificador se utiliza solo para Carrefour Servicios Generales. Se compone del número de pedido más la línea del pedido
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Número de articulo del proveedor (SA)</td> <td>C</td> <td>35</td> <td>28</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeArticuloDelProveedor_SA_(String numeroDeArticuloDelProveedor_SA_) {
		this.numeroDeArticuloDelProveedor_SA_ = numeroDeArticuloDelProveedor_SA_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroVariablePromocional_PV_() {
		return numeroVariablePromocional_PV_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Número variable promocional (PV)</td> <td>C</td> <td>35</td> <td>63</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroVariablePromocional_PV_(String numeroVariablePromocional_PV_) {
		this.numeroVariablePromocional_PV_ = numeroVariablePromocional_PV_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoDUN_14_ADU_() {
		return codigoDUN_14_ADU_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Código DUN-14 (ADU)</td> <td>C</td> <td>35</td> <td>98</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDUN_14_ADU_(String codigoDUN_14_ADU_) {
		this.codigoDUN_14_ADU_ = codigoDUN_14_ADU_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoACU_ACU_() {
		return codigoACU_ACU_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Código ACU (ACU)</td> <td>C</td> <td>35</td> <td>133</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoACU_ACU_(String codigoACU_ACU_) {
		this.codigoACU_ACU_ = codigoACU_ACU_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeLote_NB_() {
		return numeroDeLote_NB_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Número de lote (NB)</td> <td>C</td> <td>35</td> <td>168</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLote_NB_(String numeroDeLote_NB_) {
		this.numeroDeLote_NB_ = numeroDeLote_NB_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeArticuloDelComprador_IN_() {
		return numeroDeArticuloDelComprador_IN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Número de articulo del comprador (IN)</td> <td>C</td> <td>35</td> <td>203</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeArticuloDelComprador_IN_(String numeroDeArticuloDelComprador_IN_) {
		this.numeroDeArticuloDelComprador_IN_ = numeroDeArticuloDelComprador_IN_;
	}

	/** 
	 * 
	 */ 
	public Double getCantidadEnviada_12_() {
		return cantidadEnviada_12_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Cantidad enviada (12)</td> <td>N(12,3)</td> <td>16</td> <td>238</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadEnviada_12_(Double cantidadEnviada_12_) {
		this.cantidadEnviada_12_ = cantidadEnviada_12_;
	}

	/** 
	 * 11 - Unidad de medida cantidad enviada: Solo se utiliza si el producto que se está identificando es de medida variable. Este campo se corresponde con el elemento 6411.
	 */ 
	public Double getCantidadRecibidaYAceptada() {
		return cantidadRecibidaYAceptada;
	}

	/** 
	 * 11 - Unidad de medida cantidad enviada: Solo se utiliza si el producto que se está identificando es de medida variable. Este campo se corresponde con el elemento 6411.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Cantidad Recibida y Aceptada</td> <td>N(12,3)</td> <td>16</td> <td>254</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadRecibidaYAceptada(Double cantidadRecibidaYAceptada) {
		this.cantidadRecibidaYAceptada = cantidadRecibidaYAceptada;
	}

	/** 
	 * 
	 */ 
	public String getUnidadDeMedidaCantidadEnviada() {
		return unidadDeMedidaCantidadEnviada;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Unidad de medida cantidad enviada</td> <td>C</td> <td>3</td> <td>270</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaCantidadEnviada(String unidadDeMedidaCantidadEnviada) {
		this.unidadDeMedidaCantidadEnviada = unidadDeMedidaCantidadEnviada;
	}

	/** 
	 * 
	 */ 
	public Double getUnidadesDeConsumoEnUnidadDeExpedicion_59_() {
		return unidadesDeConsumoEnUnidadDeExpedicion_59_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Unidades de consumo en unidad de expedición (59)</td> <td>N(12,3)</td> <td>16</td> <td>273</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadesDeConsumoEnUnidadDeExpedicion_59_(Double unidadesDeConsumoEnUnidadDeExpedicion_59_) {
		this.unidadesDeConsumoEnUnidadDeExpedicion_59_ = unidadesDeConsumoEnUnidadDeExpedicion_59_;
	}

	/** 
	 * 14 - Identificador producto/línea pedido: Este identificador se utiliza solo para Carrefour Servicios Generales. Se compone del número de pedido más la línea del pedido
	 */ 
	public String getIdentificadorProducto_lineaPedido_MP_() {
		return identificadorProducto_lineaPedido_MP_;
	}

	/** 
	 * 14 - Identificador producto/línea pedido: Este identificador se utiliza solo para Carrefour Servicios Generales. Se compone del número de pedido más la línea del pedido
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Identificador producto/línea pedido (MP)</td> <td>C</td> <td>15</td> <td>289</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIdentificadorProducto_lineaPedido_MP_(String identificadorProducto_lineaPedido_MP_) {
		this.identificadorProducto_lineaPedido_MP_ = identificadorProducto_lineaPedido_MP_;
	}

	/** 
	 * 
	 */ 
	public String getCategoriaProducto_GB_() {
		return categoriaProducto_GB_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Categoría Producto (GB)</td> <td>C</td> <td>15</td> <td>304</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCategoriaProducto_GB_(String categoriaProducto_GB_) {
		this.categoriaProducto_GB_ = categoriaProducto_GB_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroArticuloFabricante_MF_() {
		return numeroArticuloFabricante_MF_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Número artículo fabricante (MF)</td> <td>C</td> <td>35</td> <td>319</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroArticuloFabricante_MF_(String numeroArticuloFabricante_MF_) {
		this.numeroArticuloFabricante_MF_ = numeroArticuloFabricante_MF_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeSerieDelArticulo_SN_() {
		return numeroDeSerieDelArticulo_SN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Número de serie del artículo (SN)</td> <td>C</td> <td>35</td> <td>354</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeSerieDelArticulo_SN_(String numeroDeSerieDelArticulo_SN_) {
		this.numeroDeSerieDelArticulo_SN_ = numeroDeSerieDelArticulo_SN_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeAlbaran_DQ_AAK_() {
		return numeroDeAlbaran_DQ_AAK_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Número de albarán (DQ/AAK)</td> <td>C</td> <td>17</td> <td>389</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeAlbaran_DQ_AAK_(String numeroDeAlbaran_DQ_AAK_) {
		this.numeroDeAlbaran_DQ_AAK_ = numeroDeAlbaran_DQ_AAK_;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeLineaDelAlbaran_DQ_AAK_() {
		return numeroDeLineaDelAlbaran_DQ_AAK_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Número de línea del albarán (DQ/AAK)</td> <td>N</td> <td>6</td> <td>406</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaDelAlbaran_DQ_AAK_(Integer numeroDeLineaDelAlbaran_DQ_AAK_) {
		this.numeroDeLineaDelAlbaran_DQ_AAK_ = numeroDeLineaDelAlbaran_DQ_AAK_;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}