package com.esferalia.aon.file.seres.connect.invoice.v4.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCL entity.
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
 * 		 <td>SINCL</td> <td>Línea detalle</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCL {

	private Integer numeroDeLinea;
	private String codigoArticulo;
	private String descripcionDelArticulo;
	private String tipoArticulo;
	private String codigoInternoArticuloProveedor_SA_;
	private String codigoInternoArticuloCliente_IN_;
	private String codigoVariablePromocional_PV_;
	private String codigoUnidadDeExpedicion_EN_;
	private String numeroDeLote_BN_;
	private Double cantidadFacturada_47_;
	private Double cantidadBonificada_15E_;
	private String unidadDeMedida;
	private Double unidadesEntregadas;
	private Double numeroUnidadesDeConsumoEnU_Expedicion;
	private Double importeTotalNetoDeLaLineaDeArticulo;
	private Double precioBrutoUnitario;
	private Double precioNetoUnitario;
	private String unidadDeMedidaDelPrecio;
	private String calificadorIVA_IGIG;
	private Double porcentajeImpuestoIVA_IGIG;
	private Double importeImpuestoIVA_IGIG;
	private Double porcentajeRecargoDeEquivalencia;
	private Double importeRecargoDeEquivalencia;
	private String calificadorOtroTipoDeImpuesto;
	private Double porcentajeOtroTipoDeImpuesto;
	private Double importeOtroTipoDeImpuesto;
	private String numeroPedido_ON_;
	private String numeroDeAlbaran_DQ_;
	private Integer numeroDeEmbalajes;
	private String tipoDeEmbalaje;
	private Double importeTotalBrutoDeLaLineaDeDetalle;
	private Integer numeroDeLineaSuperior;
	private Integer numeroDeLineaDelPedido_ON_;
	private Double unidadBasePrecio;
	private String identificadorProducto_lineaPedido_MP_;
	private String categoriaProducto_GB_;
	private String numeroArticuloFabricante_MF_;
	private String numeroConfirmacionDeEntrega;
	private Integer numeroDeLineaConfirmacionDeEntrega;
	private Integer fechaPedido_ON_171_;
	private Integer fechaAlbaran_DQ_171_;


	private static Pattern PATTERN_SINCL_numeroDeLinea = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SINCL_codigoArticulo = Pattern.compile("^.{12}(.{15}).*");
	private static Pattern PATTERN_SINCL_descripcionDelArticulo = Pattern.compile("^.{27}(.{35}).*");
	private static Pattern PATTERN_SINCL_tipoArticulo = Pattern.compile("^.{62}(.{1}).*");
	private static Pattern PATTERN_SINCL_codigoInternoArticuloProveedor_SA_ = Pattern.compile("^.{63}(.{15}).*");
	private static Pattern PATTERN_SINCL_codigoInternoArticuloCliente_IN_ = Pattern.compile("^.{78}(.{15}).*");
	private static Pattern PATTERN_SINCL_codigoVariablePromocional_PV_ = Pattern.compile("^.{93}(.{15}).*");
	private static Pattern PATTERN_SINCL_codigoUnidadDeExpedicion_EN_ = Pattern.compile("^.{108}(.{15}).*");
	private static Pattern PATTERN_SINCL_numeroDeLote_BN_ = Pattern.compile("^.{123}(.{15}).*");
	private static Pattern PATTERN_SINCL_cantidadFacturada_47_ = Pattern.compile("^.{138}(.{16}).*");
	private static Pattern PATTERN_SINCL_cantidadBonificada_15E_ = Pattern.compile("^.{154}(.{16}).*");
	private static Pattern PATTERN_SINCL_unidadDeMedida = Pattern.compile("^.{170}(.{6}).*");
	private static Pattern PATTERN_SINCL_unidadesEntregadas = Pattern.compile("^.{176}(.{16}).*");
	private static Pattern PATTERN_SINCL_numeroUnidadesDeConsumoEnU_Expedicion = Pattern.compile("^.{192}(.{16}).*");
	private static Pattern PATTERN_SINCL_importeTotalNetoDeLaLineaDeArticulo = Pattern.compile("^.{208}(.{18}).*");
	private static Pattern PATTERN_SINCL_precioBrutoUnitario = Pattern.compile("^.{226}(.{16}).*");
	private static Pattern PATTERN_SINCL_precioNetoUnitario = Pattern.compile("^.{242}(.{16}).*");
	private static Pattern PATTERN_SINCL_unidadDeMedidaDelPrecio = Pattern.compile("^.{258}(.{6}).*");
	private static Pattern PATTERN_SINCL_calificadorIVA_IGIG = Pattern.compile("^.{264}(.{6}).*");
	private static Pattern PATTERN_SINCL_porcentajeImpuestoIVA_IGIG = Pattern.compile("^.{270}(.{6}).*");
	private static Pattern PATTERN_SINCL_importeImpuestoIVA_IGIG = Pattern.compile("^.{276}(.{18}).*");
	private static Pattern PATTERN_SINCL_porcentajeRecargoDeEquivalencia = Pattern.compile("^.{294}(.{6}).*");
	private static Pattern PATTERN_SINCL_importeRecargoDeEquivalencia = Pattern.compile("^.{300}(.{18}).*");
	private static Pattern PATTERN_SINCL_calificadorOtroTipoDeImpuesto = Pattern.compile("^.{318}(.{6}).*");
	private static Pattern PATTERN_SINCL_porcentajeOtroTipoDeImpuesto = Pattern.compile("^.{324}(.{6}).*");
	private static Pattern PATTERN_SINCL_importeOtroTipoDeImpuesto = Pattern.compile("^.{330}(.{18}).*");
	private static Pattern PATTERN_SINCL_numeroPedido_ON_ = Pattern.compile("^.{348}(.{17}).*");
	private static Pattern PATTERN_SINCL_numeroDeAlbaran_DQ_ = Pattern.compile("^.{365}(.{17}).*");
	private static Pattern PATTERN_SINCL_numeroDeEmbalajes = Pattern.compile("^.{382}(.{8}).*");
	private static Pattern PATTERN_SINCL_tipoDeEmbalaje = Pattern.compile("^.{390}(.{7}).*");
	private static Pattern PATTERN_SINCL_importeTotalBrutoDeLaLineaDeDetalle = Pattern.compile("^.{397}(.{18}).*");
	private static Pattern PATTERN_SINCL_numeroDeLineaSuperior = Pattern.compile("^.{415}(.{6}).*");
	private static Pattern PATTERN_SINCL_numeroDeLineaDelPedido_ON_ = Pattern.compile("^.{421}(.{6}).*");
	private static Pattern PATTERN_SINCL_unidadBasePrecio = Pattern.compile("^.{427}(.{10}).*");
	private static Pattern PATTERN_SINCL_identificadorProducto_lineaPedido_MP_ = Pattern.compile("^.{437}(.{15}).*");
	private static Pattern PATTERN_SINCL_categoriaProducto_GB_ = Pattern.compile("^.{452}(.{15}).*");
	private static Pattern PATTERN_SINCL_numeroArticuloFabricante_MF_ = Pattern.compile("^.{467}(.{35}).*");
	private static Pattern PATTERN_SINCL_numeroConfirmacionDeEntrega = Pattern.compile("^.{502}(.{17}).*");
	private static Pattern PATTERN_SINCL_numeroDeLineaConfirmacionDeEntrega = Pattern.compile("^.{519}(.{6}).*");
	private static Pattern PATTERN_SINCL_fechaPedido_ON_171_ = Pattern.compile("^.{525}(.{8}).*");
	private static Pattern PATTERN_SINCL_fechaAlbaran_DQ_171_ = Pattern.compile("^.{533}(.{8}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCL_numeroDeLinea.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLinea(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_descripcionDelArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDescripcionDelArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_tipoArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoArticulo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoInternoArticuloProveedor_SA_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInternoArticuloProveedor_SA_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoInternoArticuloCliente_IN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInternoArticuloCliente_IN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoVariablePromocional_PV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoVariablePromocional_PV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_codigoUnidadDeExpedicion_EN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoUnidadDeExpedicion_EN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeLote_BN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLote_BN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_cantidadFacturada_47_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadFacturada_47_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_cantidadBonificada_15E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCantidadBonificada_15E_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_unidadDeMedida.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedida(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_unidadesEntregadas.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadesEntregadas(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroUnidadesDeConsumoEnU_Expedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroUnidadesDeConsumoEnU_Expedicion(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeTotalNetoDeLaLineaDeArticulo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalNetoDeLaLineaDeArticulo(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_precioBrutoUnitario.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioBrutoUnitario(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_precioNetoUnitario.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPrecioNetoUnitario(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_unidadDeMedidaDelPrecio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadDeMedidaDelPrecio(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_calificadorIVA_IGIG.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorIVA_IGIG(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_porcentajeImpuestoIVA_IGIG.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeImpuestoIVA_IGIG(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeImpuestoIVA_IGIG.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteImpuestoIVA_IGIG(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_porcentajeRecargoDeEquivalencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeRecargoDeEquivalencia(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeRecargoDeEquivalencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteRecargoDeEquivalencia(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_calificadorOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorOtroTipoDeImpuesto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_porcentajeOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeOtroTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeOtroTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteOtroTipoDeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroPedido_ON_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroPedido_ON_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeAlbaran_DQ_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeAlbaran_DQ_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeEmbalajes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeEmbalajes(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_tipoDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_importeTotalBrutoDeLaLineaDeDetalle.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalBrutoDeLaLineaDeDetalle(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeLineaSuperior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaSuperior(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeLineaDelPedido_ON_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaDelPedido_ON_(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_unidadBasePrecio.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setUnidadBasePrecio(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_identificadorProducto_lineaPedido_MP_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificadorProducto_lineaPedido_MP_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_categoriaProducto_GB_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCategoriaProducto_GB_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroArticuloFabricante_MF_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroArticuloFabricante_MF_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroConfirmacionDeEntrega.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroConfirmacionDeEntrega(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_numeroDeLineaConfirmacionDeEntrega.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaConfirmacionDeEntrega(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_fechaPedido_ON_171_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaPedido_ON_171_(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCL_fechaAlbaran_DQ_171_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaAlbaran_DQ_171_(Integer.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 12 - Cantidad Bonificada: Solo se utilizará si la compra de un artículo se bonifica con un artículo distinto. Si se bonifica con el mismo artículo se utilizará el registro de descuentos SINCE donde se detallará la cantidad bonificada.
	 */ 
	public Integer getNumeroDeLinea() {
		return numeroDeLinea;
	}

	/** 
	 * 12 - Cantidad Bonificada: Solo se utilizará si la compra de un artículo se bonifica con un artículo distinto. Si se bonifica con el mismo artículo se utilizará el registro de descuentos SINCE donde se detallará la cantidad bonificada.
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
	 * 3 - Código Artículo: Código de artículo que se está facturando.
	 */ 
	public String getCodigoArticulo() {
		return codigoArticulo;
	}

	/** 
	 * 3 - Código Artículo: Código de artículo que se está facturando.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Código Artículo</td> <td>C</td> <td>15</td> <td>13</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoArticulo(String codigoArticulo) {
		this.codigoArticulo = codigoArticulo;
	}

	/** 
	 * 14 - Unidades Entregadas: Se usará solo para indicar en productos de peso variable, la cantidad de unidades entregadas.
	 */ 
	public String getDescripcionDelArticulo() {
		return descripcionDelArticulo;
	}

	/** 
	 * 14 - Unidades Entregadas: Se usará solo para indicar en productos de peso variable, la cantidad de unidades entregadas.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Descripción del Articulo</td> <td>C</td> <td>35</td> <td>28</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDescripcionDelArticulo(String descripcionDelArticulo) {
		this.descripcionDelArticulo = descripcionDelArticulo;
	}

	/** 
	 * 5 - Tipo Artículo: Identifica el tipo de artículo facturado. Este campo corresponde al elemento 7081. Los valores posibles son:
	 */ 
	public String getTipoArticulo() {
		return tipoArticulo;
	}

	/** 
	 * 5 - Tipo Artículo: Identifica el tipo de artículo facturado. Este campo corresponde al elemento 7081. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Tipo Articulo</td> <td>C</td> <td>1</td> <td>63</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	/** 
	 * 16 - Importe total Neto de la línea de Artículo: (Cantidad x Precio) - Descuentos.
	 */ 
	public String getCodigoInternoArticuloProveedor_SA_() {
		return codigoInternoArticuloProveedor_SA_;
	}

	/** 
	 * 16 - Importe total Neto de la línea de Artículo: (Cantidad x Precio) - Descuentos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Código Interno Articulo Proveedor (SA)</td> <td>C</td> <td>15</td> <td>64</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInternoArticuloProveedor_SA_(String codigoInternoArticuloProveedor_SA_) {
		this.codigoInternoArticuloProveedor_SA_ = codigoInternoArticuloProveedor_SA_;
	}

	/** 
	 * 17 - Precio Bruto Unitario: Excluye descuentos, cargos e impuestos.
	 */ 
	public String getCodigoInternoArticuloCliente_IN_() {
		return codigoInternoArticuloCliente_IN_;
	}

	/** 
	 * 17 - Precio Bruto Unitario: Excluye descuentos, cargos e impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Código Interno Articulo Cliente (IN)</td> <td>C</td> <td>15</td> <td>79</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInternoArticuloCliente_IN_(String codigoInternoArticuloCliente_IN_) {
		this.codigoInternoArticuloCliente_IN_ = codigoInternoArticuloCliente_IN_;
	}

	/** 
	 * 18 - Precio Neto Unitario: Incluye descuentos y cargos pero no impuestos.
	 */ 
	public String getCodigoVariablePromocional_PV_() {
		return codigoVariablePromocional_PV_;
	}

	/** 
	 * 18 - Precio Neto Unitario: Incluye descuentos y cargos pero no impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Código Variable Promocional (PV)</td> <td>C</td> <td>15</td> <td>94</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoVariablePromocional_PV_(String codigoVariablePromocional_PV_) {
		this.codigoVariablePromocional_PV_ = codigoVariablePromocional_PV_;
	}

	/** 
	 * 9 - Código Unidad de Expedición asociada al producto: Código que hace referencia a la forma de distribución del producto (por ejemplo: cajas de unidades). Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
	 */ 
	public String getCodigoUnidadDeExpedicion_EN_() {
		return codigoUnidadDeExpedicion_EN_;
	}

	/** 
	 * 9 - Código Unidad de Expedición asociada al producto: Código que hace referencia a la forma de distribución del producto (por ejemplo: cajas de unidades). Este es un concepto que prácticamente no está en uso por las empresas que intercambian.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Código Unidad de Expedición (EN)</td> <td>C</td> <td>15</td> <td>109</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoUnidadDeExpedicion_EN_(String codigoUnidadDeExpedicion_EN_) {
		this.codigoUnidadDeExpedicion_EN_ = codigoUnidadDeExpedicion_EN_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeLote_BN_() {
		return numeroDeLote_BN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Número de Lote (BN)</td> <td>C</td> <td>15</td> <td>124</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLote_BN_(String numeroDeLote_BN_) {
		this.numeroDeLote_BN_ = numeroDeLote_BN_;
	}

	/** 
	 * 11 - Cantidad: Cantidad Facturada. Si se trata de un artículo de media variable, indica el nº de unidades en kilos, litros o en la unidad de medida que corresponda.
	 */ 
	public Double getCantidadFacturada_47_() {
		return cantidadFacturada_47_;
	}

	/** 
	 * 11 - Cantidad: Cantidad Facturada. Si se trata de un artículo de media variable, indica el nº de unidades en kilos, litros o en la unidad de medida que corresponda.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Cantidad Facturada (47)</td> <td>N(12,3)</td> <td>16</td> <td>139</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadFacturada_47_(Double cantidadFacturada_47_) {
		this.cantidadFacturada_47_ = cantidadFacturada_47_;
	}

	/** 
	 * 12 - Cantidad Bonificada: Solo se utilizará si la compra de un artículo se bonifica con un artículo distinto. Si se bonifica con el mismo artículo se utilizará el registro de descuentos SINCE donde se detallará la cantidad bonificada.
	 */ 
	public Double getCantidadBonificada_15E_() {
		return cantidadBonificada_15E_;
	}

	/** 
	 * 12 - Cantidad Bonificada: Solo se utilizará si la compra de un artículo se bonifica con un artículo distinto. Si se bonifica con el mismo artículo se utilizará el registro de descuentos SINCE donde se detallará la cantidad bonificada.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Cantidad Bonificada (15E)</td> <td>N(12,3)</td> <td>16</td> <td>155</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCantidadBonificada_15E_(Double cantidadBonificada_15E_) {
		this.cantidadBonificada_15E_ = cantidadBonificada_15E_;
	}

	/** 
	 * 13 - Unidad de Medida del producto que se está facturando: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 */ 
	public String getUnidadDeMedida() {
		return unidadDeMedida;
	}

	/** 
	 * 13 - Unidad de Medida del producto que se está facturando: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Unidad de Medida</td> <td>C</td> <td>6</td> <td>171</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedida(String unidadDeMedida) {
		this.unidadDeMedida = unidadDeMedida;
	}

	/** 
	 * 14 - Unidades Entregadas: Se usará solo para indicar en productos de peso variable, la cantidad de unidades entregadas.
	 */ 
	public Double getUnidadesEntregadas() {
		return unidadesEntregadas;
	}

	/** 
	 * 14 - Unidades Entregadas: Se usará solo para indicar en productos de peso variable, la cantidad de unidades entregadas.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Unidades Entregadas</td> <td>N(12,3)</td> <td>16</td> <td>177</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadesEntregadas(Double unidadesEntregadas) {
		this.unidadesEntregadas = unidadesEntregadas;
	}

	/** 
	 * 
	 */ 
	public Double getNumeroUnidadesDeConsumoEnU_Expedicion() {
		return numeroUnidadesDeConsumoEnU_Expedicion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Número Unidades de Consumo en U. Expedición</td> <td>N(12,3)</td> <td>16</td> <td>193</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroUnidadesDeConsumoEnU_Expedicion(Double numeroUnidadesDeConsumoEnU_Expedicion) {
		this.numeroUnidadesDeConsumoEnU_Expedicion = numeroUnidadesDeConsumoEnU_Expedicion;
	}

	/** 
	 * 16 - Importe total Neto de la línea de Artículo: (Cantidad x Precio) - Descuentos.
	 */ 
	public Double getImporteTotalNetoDeLaLineaDeArticulo() {
		return importeTotalNetoDeLaLineaDeArticulo;
	}

	/** 
	 * 16 - Importe total Neto de la línea de Artículo: (Cantidad x Precio) - Descuentos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Importe Total Neto de la Línea de Articulo</td> <td>N(14,3)</td> <td>18</td> <td>209</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalNetoDeLaLineaDeArticulo(Double importeTotalNetoDeLaLineaDeArticulo) {
		this.importeTotalNetoDeLaLineaDeArticulo = importeTotalNetoDeLaLineaDeArticulo;
	}

	/** 
	 * 17 - Precio Bruto Unitario: Excluye descuentos, cargos e impuestos.
	 */ 
	public Double getPrecioBrutoUnitario() {
		return precioBrutoUnitario;
	}

	/** 
	 * 17 - Precio Bruto Unitario: Excluye descuentos, cargos e impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Precio Bruto Unitario</td> <td>N(11,4)</td> <td>16</td> <td>227</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPrecioBrutoUnitario(Double precioBrutoUnitario) {
		this.precioBrutoUnitario = precioBrutoUnitario;
	}

	/** 
	 * 18 - Precio Neto Unitario: Incluye descuentos y cargos pero no impuestos.
	 */ 
	public Double getPrecioNetoUnitario() {
		return precioNetoUnitario;
	}

	/** 
	 * 18 - Precio Neto Unitario: Incluye descuentos y cargos pero no impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Precio Neto Unitario</td> <td>N(11,4)</td> <td>16</td> <td>243</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPrecioNetoUnitario(Double precioNetoUnitario) {
		this.precioNetoUnitario = precioNetoUnitario;
	}

	/** 
	 * 19 - Unidad de Medida del Precio: Sera utilizado únicamente si el artículo tiene medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 */ 
	public String getUnidadDeMedidaDelPrecio() {
		return unidadDeMedidaDelPrecio;
	}

	/** 
	 * 19 - Unidad de Medida del Precio: Sera utilizado únicamente si el artículo tiene medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Unidad de Medida del Precio</td> <td>C</td> <td>6</td> <td>259</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadDeMedidaDelPrecio(String unidadDeMedidaDelPrecio) {
		this.unidadDeMedidaDelPrecio = unidadDeMedidaDelPrecio;
	}

	/** 
	 * 20 - Calificador de IVA o IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */ 
	public String getCalificadorIVA_IGIG() {
		return calificadorIVA_IGIG;
	}

	/** 
	 * 20 - Calificador de IVA o IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Calificador IVA/IGIG</td> <td>C</td> <td>6</td> <td>265</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorIVA_IGIG(String calificadorIVA_IGIG) {
		this.calificadorIVA_IGIG = calificadorIVA_IGIG;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeImpuestoIVA_IGIG() {
		return porcentajeImpuestoIVA_IGIG;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>% Impuesto IVA/IGIG</td> <td>N(3,2)</td> <td>6</td> <td>271</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeImpuestoIVA_IGIG(Double porcentajeImpuestoIVA_IGIG) {
		this.porcentajeImpuestoIVA_IGIG = porcentajeImpuestoIVA_IGIG;
	}

	/** 
	 * 
	 */ 
	public Double getImporteImpuestoIVA_IGIG() {
		return importeImpuestoIVA_IGIG;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Importe Impuesto IVA/IGIG</td> <td>N(14,3)</td> <td>18</td> <td>277</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteImpuestoIVA_IGIG(Double importeImpuestoIVA_IGIG) {
		this.importeImpuestoIVA_IGIG = importeImpuestoIVA_IGIG;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeRecargoDeEquivalencia() {
		return porcentajeRecargoDeEquivalencia;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>% Recargo de Equivalencia</td> <td>N(3,2)</td> <td>6</td> <td>295</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeRecargoDeEquivalencia(Double porcentajeRecargoDeEquivalencia) {
		this.porcentajeRecargoDeEquivalencia = porcentajeRecargoDeEquivalencia;
	}

	/** 
	 * 
	 */ 
	public Double getImporteRecargoDeEquivalencia() {
		return importeRecargoDeEquivalencia;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Importe Recargo de Equivalencia</td> <td>N(14,3)</td> <td>18</td> <td>301</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteRecargoDeEquivalencia(Double importeRecargoDeEquivalencia) {
		this.importeRecargoDeEquivalencia = importeRecargoDeEquivalencia;
	}

	/** 
	 * 
	 */ 
	public String getCalificadorOtroTipoDeImpuesto() {
		return calificadorOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Calificador Otro Tipo de Impuesto</td> <td>C</td> <td>6</td> <td>319</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorOtroTipoDeImpuesto(String calificadorOtroTipoDeImpuesto) {
		this.calificadorOtroTipoDeImpuesto = calificadorOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 */ 
	public Double getPorcentajeOtroTipoDeImpuesto() {
		return porcentajeOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>% Otro Tipo de Impuesto</td> <td>N(3,2)</td> <td>6</td> <td>325</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPorcentajeOtroTipoDeImpuesto(Double porcentajeOtroTipoDeImpuesto) {
		this.porcentajeOtroTipoDeImpuesto = porcentajeOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 */ 
	public Double getImporteOtroTipoDeImpuesto() {
		return importeOtroTipoDeImpuesto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Importe Otro Tipo de Impuesto</td> <td>N(14,3)</td> <td>18</td> <td>331</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteOtroTipoDeImpuesto(Double importeOtroTipoDeImpuesto) {
		this.importeOtroTipoDeImpuesto = importeOtroTipoDeImpuesto;
	}

	/** 
	 * 28 - Número Pedido: Solo se utilizará para facturas con más de un pedido que no son recapitulativas. Indica el número de pedido del artículo. En caso de un artículo con más de un pedido se indicará una línea de factura por cada uno de sus pedidos.
	 */ 
	public String getNumeroPedido_ON_() {
		return numeroPedido_ON_;
	}

	/** 
	 * 28 - Número Pedido: Solo se utilizará para facturas con más de un pedido que no son recapitulativas. Indica el número de pedido del artículo. En caso de un artículo con más de un pedido se indicará una línea de factura por cada uno de sus pedidos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Número Pedido (ON)</td> <td>C</td> <td>17</td> <td>349</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroPedido_ON_(String numeroPedido_ON_) {
		this.numeroPedido_ON_ = numeroPedido_ON_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeAlbaran_DQ_() {
		return numeroDeAlbaran_DQ_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>29</td> <td>Número de Albarán (DQ)</td> <td>C</td> <td>17</td> <td>366</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeAlbaran_DQ_(String numeroDeAlbaran_DQ_) {
		this.numeroDeAlbaran_DQ_ = numeroDeAlbaran_DQ_;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeEmbalajes() {
		return numeroDeEmbalajes;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>30</td> <td>Número de Embalajes</td> <td>N</td> <td>8</td> <td>383</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeEmbalajes(Integer numeroDeEmbalajes) {
		this.numeroDeEmbalajes = numeroDeEmbalajes;
	}

	/** 
	 * 
	 */ 
	public String getTipoDeEmbalaje() {
		return tipoDeEmbalaje;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>31</td> <td>Tipo de Embalaje</td> <td>C</td> <td>7</td> <td>391</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeEmbalaje(String tipoDeEmbalaje) {
		this.tipoDeEmbalaje = tipoDeEmbalaje;
	}

	/** 
	 * 
	 */ 
	public Double getImporteTotalBrutoDeLaLineaDeDetalle() {
		return importeTotalBrutoDeLaLineaDeDetalle;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>32</td> <td>Importe total bruto de la línea de detalle</td> <td>N(14,3)</td> <td>18</td> <td>398</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalBrutoDeLaLineaDeDetalle(Double importeTotalBrutoDeLaLineaDeDetalle) {
		this.importeTotalBrutoDeLaLineaDeDetalle = importeTotalBrutoDeLaLineaDeDetalle;
	}

	/** 
	 * 33 - Número de línea superior: En este campo se informa el número de línea del que depende la línea actual, informada en el campo 2. Cuando se informa este campo se asume que la línea actual es una sublínea. Se utiliza principalmente para packs multireferencia.
	 */ 
	public Integer getNumeroDeLineaSuperior() {
		return numeroDeLineaSuperior;
	}

	/** 
	 * 33 - Número de línea superior: En este campo se informa el número de línea del que depende la línea actual, informada en el campo 2. Cuando se informa este campo se asume que la línea actual es una sublínea. Se utiliza principalmente para packs multireferencia.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>33</td> <td>Número de línea superior</td> <td>N</td> <td>6</td> <td>416</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaSuperior(Integer numeroDeLineaSuperior) {
		this.numeroDeLineaSuperior = numeroDeLineaSuperior;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeLineaDelPedido_ON_() {
		return numeroDeLineaDelPedido_ON_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>34</td> <td>Número de línea del pedido (ON)</td> <td>N</td> <td>6</td> <td>422</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaDelPedido_ON_(Integer numeroDeLineaDelPedido_ON_) {
		this.numeroDeLineaDelPedido_ON_ = numeroDeLineaDelPedido_ON_;
	}

	/** 
	 * 35 - Unidad base precio: Base del precio. Se utiliza principalmente cuando el producto es de unidad de medida variable. Ejemplo: Para establecer el precio por cada 200 Kilogramos.
	 */ 
	public Double getUnidadBasePrecio() {
		return unidadBasePrecio;
	}

	/** 
	 * 35 - Unidad base precio: Base del precio. Se utiliza principalmente cuando el producto es de unidad de medida variable. Ejemplo: Para establecer el precio por cada 200 Kilogramos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>35</td> <td>Unidad base precio</td> <td>N(6,3)</td> <td>10</td> <td>428</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setUnidadBasePrecio(Double unidadBasePrecio) {
		this.unidadBasePrecio = unidadBasePrecio;
	}

	/** 
	 * 36 - Identificador producto/línea pedido: Este identificador se utiliza solo para Carrefour Servicios Generales. Se compone del número de pedido más la línea del pedido. Este identificador lo envía Carrefour Servicios Generales en el pedido.
	 */ 
	public String getIdentificadorProducto_lineaPedido_MP_() {
		return identificadorProducto_lineaPedido_MP_;
	}

	/** 
	 * 36 - Identificador producto/línea pedido: Este identificador se utiliza solo para Carrefour Servicios Generales. Se compone del número de pedido más la línea del pedido. Este identificador lo envía Carrefour Servicios Generales en el pedido.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>36</td> <td>Identificador producto/línea pedido (MP)</td> <td>C</td> <td>15</td> <td>438</td> <td>O</td>
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
	 * 		 <td>37</td> <td>Categoría Producto (GB)</td> <td>C</td> <td>15</td> <td>453</td> <td>O</td>
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
	 * 		 <td>38</td> <td>Número artículo fabricante (MF)</td> <td>C</td> <td>35</td> <td>468</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroArticuloFabricante_MF_(String numeroArticuloFabricante_MF_) {
		this.numeroArticuloFabricante_MF_ = numeroArticuloFabricante_MF_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroConfirmacionDeEntrega() {
		return numeroConfirmacionDeEntrega;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>39</td> <td>Número Confirmación de entrega</td> <td>C</td> <td>17</td> <td>503</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroConfirmacionDeEntrega(String numeroConfirmacionDeEntrega) {
		this.numeroConfirmacionDeEntrega = numeroConfirmacionDeEntrega;
	}

	/** 
	 * 
	 */ 
	public Integer getNumeroDeLineaConfirmacionDeEntrega() {
		return numeroDeLineaConfirmacionDeEntrega;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>40</td> <td>Número de línea Confirmación de entrega</td> <td>N</td> <td>6</td> <td>520</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeLineaConfirmacionDeEntrega(Integer numeroDeLineaConfirmacionDeEntrega) {
		this.numeroDeLineaConfirmacionDeEntrega = numeroDeLineaConfirmacionDeEntrega;
	}

	/** 
	 * 
	 */ 
	public Integer getFechaPedido_ON_171_() {
		return fechaPedido_ON_171_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>41</td> <td>Fecha Pedido (ON/171)</td> <td>N</td> <td>8</td> <td>526</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaPedido_ON_171_(Integer fechaPedido_ON_171_) {
		this.fechaPedido_ON_171_ = fechaPedido_ON_171_;
	}

	/** 
	 * 
	 */ 
	public Integer getFechaAlbaran_DQ_171_() {
		return fechaAlbaran_DQ_171_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>42</td> <td>Fecha Albarán (DQ/171)</td> <td>N</td> <td>8</td> <td>534</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaAlbaran_DQ_171_(Integer fechaAlbaran_DQ_171_) {
		this.fechaAlbaran_DQ_171_ = fechaAlbaran_DQ_171_;
	}

	/** 
	 * 5 - Tipo Artículo: Identifica el tipo de artículo facturado. Este campo corresponde al elemento 7081. Los valores posibles son:
	 */
	public enum SINCL_5 {
		MERCANCIA_M("M"),
		SERVICIO_S("S"),
		MATERIAL_CONSIGNADO_C("C"),
		;
		
		private String value;
		
		private SINCL_5(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCL_5 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 13 - Unidad de Medida del producto que se está facturando: Este campo se utiliza solo para productos de medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 */
	public enum SINCL_13 {
		KILOGRAMO_KGM("KGM"),
		LITRO_LTR("LTR"),
		;
		
		private String value;
		
		private SINCL_13(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCL_13 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 19 - Unidad de Medida del Precio: Sera utilizado únicamente si el artículo tiene medida variable. Este campo corresponde al elemento 6411. Los valores posibles son:
	 */
	public enum SINCL_19 {
		KILOGRAMO_KGM("KGM"),
		LITRO_LTR("LTR"),
		;
		
		private String value;
		
		private SINCL_19(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCL_19 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 20 - Calificador de IVA o IGIC: Identificación del tipo de impuesto. Este campo corresponde al elemento 5153. Los valores posibles son:
	 */
	public enum SINCL_20 {
		IVA_VAT("VAT"),
		IGIC__IMPUESTO_GENERAL_DE_LAS_ISLAS_CANARIAS__IGI("IGI"),
		;
		
		private String value;
		
		private SINCL_20(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCL_20 enumByValue(String value) {
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