package com.esferalia.aon.file.seres.connect.sales.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI ERE1C entity.
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
 * 		 <td>ERE1C</td> <td>Cabecera</td> <td>Obligatorio</td> <td>1</td>
 * 	</tr>
 * </table>
 */ 

public class ERE1C {

	private String tipoDocumento_220_221_224_226_22E_;
	private String numeroDePedido;
	private String funcionDelMensaje;
	private String fecha_horaDocumento_137__102_203_;
	private String calificadorFecha_hora1;
	private String fecha_hora1;
	private String calificadorFecha_hora2;
	private String fecha_hora2;
	private String informacionAdicional;
	private String numeroPedidoAbierto_BO_;
	private String numeroListaDePrecios_PL_;
	private String numeroPedidoProveedor_VN_;
	private String calificadorReferenciaAdicional;
	private String numeroReferenciaAdicional;
	private String codigoMoneda;
	private String fechaDeVencimientoUnico;
	private String metodoPagoDeCostesDeTransportes;
	private String condicionesDeEntrega;
	private Double importeTotalNeto_79_;
	private Double importeTotalDescuentos_Cargos_131_;
	private Double baseImponible_125_;
	private Double importeTotalImpuestos_176_;
	private Double importeAPagar_139_;
	private Double importeTotalBruto_98_;
	private String expedienteDeContratacionSAS_ACD_;


	private static Pattern PATTERN_ERE1C_tipoDocumento_220_221_224_226_22E_ = Pattern.compile("^.{6}(.{3}).*");
	private static Pattern PATTERN_ERE1C_numeroDePedido = Pattern.compile("^.{9}(.{17}).*");
	private static Pattern PATTERN_ERE1C_funcionDelMensaje = Pattern.compile("^.{26}(.{3}).*");
	private static Pattern PATTERN_ERE1C_fecha_horaDocumento_137__102_203_ = Pattern.compile("^.{29}(.{12}).*");
	private static Pattern PATTERN_ERE1C_calificadorFecha_hora1 = Pattern.compile("^.{41}(.{3}).*");
	private static Pattern PATTERN_ERE1C_fecha_hora1 = Pattern.compile("^.{44}(.{12}).*");
	private static Pattern PATTERN_ERE1C_calificadorFecha_hora2 = Pattern.compile("^.{56}(.{3}).*");
	private static Pattern PATTERN_ERE1C_fecha_hora2 = Pattern.compile("^.{59}(.{12}).*");
	private static Pattern PATTERN_ERE1C_informacionAdicional = Pattern.compile("^.{71}(.{3}).*");
	private static Pattern PATTERN_ERE1C_numeroPedidoAbierto_BO_ = Pattern.compile("^.{74}(.{17}).*");
	private static Pattern PATTERN_ERE1C_numeroListaDePrecios_PL_ = Pattern.compile("^.{91}(.{17}).*");
	private static Pattern PATTERN_ERE1C_numeroPedidoProveedor_VN_ = Pattern.compile("^.{108}(.{17}).*");
	private static Pattern PATTERN_ERE1C_calificadorReferenciaAdicional = Pattern.compile("^.{125}(.{3}).*");
	private static Pattern PATTERN_ERE1C_numeroReferenciaAdicional = Pattern.compile("^.{128}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoMoneda = Pattern.compile("^.{145}(.{3}).*");
	private static Pattern PATTERN_ERE1C_fechaDeVencimientoUnico = Pattern.compile("^.{148}(.{8}).*");
	private static Pattern PATTERN_ERE1C_metodoPagoDeCostesDeTransportes = Pattern.compile("^.{156}(.{3}).*");
	private static Pattern PATTERN_ERE1C_condicionesDeEntrega = Pattern.compile("^.{159}(.{3}).*");
	private static Pattern PATTERN_ERE1C_importeTotalNeto_79_ = Pattern.compile("^.{162}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeTotalDescuentos_Cargos_131_ = Pattern.compile("^.{180}(.{18}).*");
	private static Pattern PATTERN_ERE1C_baseImponible_125_ = Pattern.compile("^.{198}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeTotalImpuestos_176_ = Pattern.compile("^.{216}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeAPagar_139_ = Pattern.compile("^.{234}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeTotalBruto_98_ = Pattern.compile("^.{252}(.{18}).*");
	private static Pattern PATTERN_ERE1C_expedienteDeContratacionSAS_ACD_ = Pattern.compile("^.{270}(.{17}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1C_tipoDocumento_220_221_224_226_22E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDocumento_220_221_224_226_22E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroDePedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_funcionDelMensaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDelMensaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_fecha_horaDocumento_137__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaDocumento_137__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_calificadorFecha_hora1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_hora1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_fecha_hora1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_hora1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_calificadorFecha_hora2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_hora2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_fecha_hora2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_hora2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_informacionAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInformacionAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroPedidoAbierto_BO_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroPedidoAbierto_BO_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroListaDePrecios_PL_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroListaDePrecios_PL_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroPedidoProveedor_VN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroPedidoProveedor_VN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_calificadorReferenciaAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferenciaAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroReferenciaAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroReferenciaAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoMoneda.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoMoneda(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_fechaDeVencimientoUnico.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeVencimientoUnico(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_metodoPagoDeCostesDeTransportes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMetodoPagoDeCostesDeTransportes(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_condicionesDeEntrega.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCondicionesDeEntrega(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_importeTotalNeto_79_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalNeto_79_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_importeTotalDescuentos_Cargos_131_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalDescuentos_Cargos_131_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_baseImponible_125_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBaseImponible_125_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_importeTotalImpuestos_176_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalImpuestos_176_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_importeAPagar_139_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteAPagar_139_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_importeTotalBruto_98_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalBruto_98_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_expedienteDeContratacionSAS_ACD_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setExpedienteDeContratacionSAS_ACD_(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Tipo documento: Este campo corresponde al elemento 1001. Los valores posibles son:
	 */ 
	public String getTipoDocumento_220_221_224_226_22E_() {
		return tipoDocumento_220_221_224_226_22E_;
	}

	/** 
	 * 2 - Tipo documento: Este campo corresponde al elemento 1001. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Tipo documento (220, 221, 224, 226, 22E)</td> <td>C</td> <td>3</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDocumento_220_221_224_226_22E_(String tipoDocumento_220_221_224_226_22E_) {
		this.tipoDocumento_220_221_224_226_22E_ = tipoDocumento_220_221_224_226_22E_;
	}

	/** 
	 * 3 - Número de pedido: Referencia de la orden de compra asignada por el emisor.
	 */ 
	public String getNumeroDePedido() {
		return numeroDePedido;
	}

	/** 
	 * 3 - Número de pedido: Referencia de la orden de compra asignada por el emisor.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Número de pedido</td> <td>C</td> <td>17</td> <td>10</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePedido(String numeroDePedido) {
		this.numeroDePedido = numeroDePedido;
	}

	/** 
	 * 224 - Pedido urgente
	 */ 
	public String getFuncionDelMensaje() {
		return funcionDelMensaje;
	}

	/** 
	 * 224 - Pedido urgente
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Función del mensaje</td> <td>C</td> <td>3</td> <td>27</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFuncionDelMensaje(String funcionDelMensaje) {
		this.funcionDelMensaje = funcionDelMensaje;
	}

	/** 
	 * 5 - Fecha/hora Documento (137) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaDocumento_137__102_203_() {
		return fecha_horaDocumento_137__102_203_;
	}

	/** 
	 * 5 - Fecha/hora Documento (137) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Fecha/hora documento (137) (102/203)</td> <td>C</td> <td>12</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaDocumento_137__102_203_(String fecha_horaDocumento_137__102_203_) {
		this.fecha_horaDocumento_137__102_203_ = fecha_horaDocumento_137__102_203_;
	}

	/** 
	 * 226 - Pedido parcial
	 */ 
	public String getCalificadorFecha_hora1() {
		return calificadorFecha_hora1;
	}

	/** 
	 * 226 - Pedido parcial
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Calificador fecha/hora 1</td> <td>C</td> <td>3</td> <td>42</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha_hora1(String calificadorFecha_hora1) {
		this.calificadorFecha_hora1 = calificadorFecha_hora1;
	}

	/** 
	 * 227 - Pedido en consigna
	 */ 
	public String getFecha_hora1() {
		return fecha_hora1;
	}

	/** 
	 * 227 - Pedido en consigna
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Fecha/hora 1</td> <td>C</td> <td>12</td> <td>45</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_hora1(String fecha_hora1) {
		this.fecha_hora1 = fecha_hora1;
	}

	/** 
	 * 8, 9 - Fecha/Hora 2: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 8 se corresponde con el elemento 2005. Los valores posibles son:
	 */ 
	public String getCalificadorFecha_hora2() {
		return calificadorFecha_hora2;
	}

	/** 
	 * 8, 9 - Fecha/Hora 2: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 8 se corresponde con el elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Calificador fecha/hora 2</td> <td>C</td> <td>3</td> <td>57</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha_hora2(String calificadorFecha_hora2) {
		this.calificadorFecha_hora2 = calificadorFecha_hora2;
	}

	/** 
	 * 9 - Original
	 */ 
	public String getFecha_hora2() {
		return fecha_hora2;
	}

	/** 
	 * 9 - Original
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Fecha/hora 2</td> <td>C</td> <td>12</td> <td>60</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_hora2(String fecha_hora2) {
		this.fecha_hora2 = fecha_hora2;
	}

	/** 
	 * 10 - Información adicional: Este campo se utiliza para especificar condiciones especiales. Los valores posibles son:
	 */ 
	public String getInformacionAdicional() {
		return informacionAdicional;
	}

	/** 
	 * 10 - Información adicional: Este campo se utiliza para especificar condiciones especiales. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Información adicional</td> <td>C</td> <td>3</td> <td>72</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setInformacionAdicional(String informacionAdicional) {
		this.informacionAdicional = informacionAdicional;
	}

	/** 
	 * 11 - Enviado fecha y/o hora
	 */ 
	public String getNumeroPedidoAbierto_BO_() {
		return numeroPedidoAbierto_BO_;
	}

	/** 
	 * 11 - Enviado fecha y/o hora
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Número pedido abierto (BO)</td> <td>C</td> <td>17</td> <td>75</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroPedidoAbierto_BO_(String numeroPedidoAbierto_BO_) {
		this.numeroPedidoAbierto_BO_ = numeroPedidoAbierto_BO_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroListaDePrecios_PL_() {
		return numeroListaDePrecios_PL_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Número lista de precios (PL)</td> <td>C</td> <td>17</td> <td>92</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroListaDePrecios_PL_(String numeroListaDePrecios_PL_) {
		this.numeroListaDePrecios_PL_ = numeroListaDePrecios_PL_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroPedidoProveedor_VN_() {
		return numeroPedidoProveedor_VN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Número pedido proveedor (VN)</td> <td>C</td> <td>17</td> <td>109</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroPedidoProveedor_VN_(String numeroPedidoProveedor_VN_) {
		this.numeroPedidoProveedor_VN_ = numeroPedidoProveedor_VN_;
	}

	/** 
	 * 14, 15 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 14 se corresponde con el elemento 1153. Los valores posibles son:
	 */ 
	public String getCalificadorReferenciaAdicional() {
		return calificadorReferenciaAdicional;
	}

	/** 
	 * 14, 15 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 14 se corresponde con el elemento 1153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Calificador referencia adicional</td> <td>C</td> <td>3</td> <td>126</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorReferenciaAdicional(String calificadorReferenciaAdicional) {
		this.calificadorReferenciaAdicional = calificadorReferenciaAdicional;
	}

	/** 
	 * 14, 15 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 14 se corresponde con el elemento 1153. Los valores posibles son:
	 */ 
	public String getNumeroReferenciaAdicional() {
		return numeroReferenciaAdicional;
	}

	/** 
	 * 14, 15 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 14 se corresponde con el elemento 1153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Número referencia adicional</td> <td>C</td> <td>17</td> <td>129</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroReferenciaAdicional(String numeroReferenciaAdicional) {
		this.numeroReferenciaAdicional = numeroReferenciaAdicional;
	}

	/** 
	 * 16 - Propuesta
	 */ 
	public String getCodigoMoneda() {
		return codigoMoneda;
	}

	/** 
	 * 16 - Propuesta
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Código moneda</td> <td>C</td> <td>3</td> <td>146</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoMoneda(String codigoMoneda) {
		this.codigoMoneda = codigoMoneda;
	}

	/** 
	 * 17 - Fecha de vencimiento único: Solo cuando la factura tiene vencimiento único, en otro caso se deberá utilizar el registro ERE1V.
	 */ 
	public String getFechaDeVencimientoUnico() {
		return fechaDeVencimientoUnico;
	}

	/** 
	 * 17 - Fecha de vencimiento único: Solo cuando la factura tiene vencimiento único, en otro caso se deberá utilizar el registro ERE1V.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Fecha de vencimiento único</td> <td>C</td> <td>8</td> <td>149</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeVencimientoUnico(String fechaDeVencimientoUnico) {
		this.fechaDeVencimientoUnico = fechaDeVencimientoUnico;
	}

	/** 
	 * 18 - Método Pago de costes de transportes: Este campo corresponde al elemento 4215. Los valores posibles son:
	 */ 
	public String getMetodoPagoDeCostesDeTransportes() {
		return metodoPagoDeCostesDeTransportes;
	}

	/** 
	 * 18 - Método Pago de costes de transportes: Este campo corresponde al elemento 4215. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Método Pago de costes de transportes</td> <td>C</td> <td>3</td> <td>157</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMetodoPagoDeCostesDeTransportes(String metodoPagoDeCostesDeTransportes) {
		this.metodoPagoDeCostesDeTransportes = metodoPagoDeCostesDeTransportes;
	}

	/** 
	 * 19 - Condiciones de Entrega: Este campo corresponde al elemento 4053. Los valores posibles son:
	 */ 
	public String getCondicionesDeEntrega() {
		return condicionesDeEntrega;
	}

	/** 
	 * 19 - Condiciones de Entrega: Este campo corresponde al elemento 4053. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Condiciones de Entrega</td> <td>C</td> <td>3</td> <td>160</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCondicionesDeEntrega(String condicionesDeEntrega) {
		this.condicionesDeEntrega = condicionesDeEntrega;
	}

	/** 
	 * 220 - Pedido normal
	 */ 
	public Double getImporteTotalNeto_79_() {
		return importeTotalNeto_79_;
	}

	/** 
	 * 220 - Pedido normal
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Importe Total Neto (79)</td> <td>N(14,3)</td> <td>18</td> <td>163</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalNeto_79_(Double importeTotalNeto_79_) {
		this.importeTotalNeto_79_ = importeTotalNeto_79_;
	}

	/** 
	 * 221 - Pedido abierto
	 */ 
	public Double getImporteTotalDescuentos_Cargos_131_() {
		return importeTotalDescuentos_Cargos_131_;
	}

	/** 
	 * 221 - Pedido abierto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Importe Total Descuentos/Cargos (131)</td> <td>N(14,3)</td> <td>18</td> <td>181</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalDescuentos_Cargos_131_(Double importeTotalDescuentos_Cargos_131_) {
		this.importeTotalDescuentos_Cargos_131_ = importeTotalDescuentos_Cargos_131_;
	}

	/** 
	 * 220 - Pedido normal
	 */ 
	public Double getBaseImponible_125_() {
		return baseImponible_125_;
	}

	/** 
	 * 220 - Pedido normal
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Base Imponible (125)</td> <td>N(14,3)</td> <td>18</td> <td>199</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setBaseImponible_125_(Double baseImponible_125_) {
		this.baseImponible_125_ = baseImponible_125_;
	}

	/** 
	 * 23 - Importe total de impuestos: Sumatorio de los importes de impuestos.
	 */ 
	public Double getImporteTotalImpuestos_176_() {
		return importeTotalImpuestos_176_;
	}

	/** 
	 * 23 - Importe total de impuestos: Sumatorio de los importes de impuestos.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Importe Total Impuestos (176)</td> <td>N(14,3)</td> <td>18</td> <td>217</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalImpuestos_176_(Double importeTotalImpuestos_176_) {
		this.importeTotalImpuestos_176_ = importeTotalImpuestos_176_;
	}

	/** 
	 * 224 - Pedido urgente
	 */ 
	public Double getImporteAPagar_139_() {
		return importeAPagar_139_;
	}

	/** 
	 * 224 - Pedido urgente
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Importe a Pagar (139)</td> <td>N(14,3)</td> <td>18</td> <td>235</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteAPagar_139_(Double importeAPagar_139_) {
		this.importeAPagar_139_ = importeAPagar_139_;
	}

	/** 
	 * 25 - Importe Total Bruto: Sumatorio de los importes Brutos de las líneas (cantidad facturada x precio unitario Bruto). No se tienen en cuenta Cargos ni Descuentos tanto a nivel de líneas como globales.
	 */ 
	public Double getImporteTotalBruto_98_() {
		return importeTotalBruto_98_;
	}

	/** 
	 * 25 - Importe Total Bruto: Sumatorio de los importes Brutos de las líneas (cantidad facturada x precio unitario Bruto). No se tienen en cuenta Cargos ni Descuentos tanto a nivel de líneas como globales.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Importe Total Bruto (98)</td> <td>N(14,3)</td> <td>18</td> <td>253</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalBruto_98_(Double importeTotalBruto_98_) {
		this.importeTotalBruto_98_ = importeTotalBruto_98_;
	}

	/** 
	 * 226 - Pedido parcial
	 */ 
	public String getExpedienteDeContratacionSAS_ACD_() {
		return expedienteDeContratacionSAS_ACD_;
	}

	/** 
	 * 226 - Pedido parcial
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Expediente de contratación SAS (ACD)</td> <td>C</td> <td>17</td> <td>271</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setExpedienteDeContratacionSAS_ACD_(String expedienteDeContratacionSAS_ACD_) {
		this.expedienteDeContratacionSAS_ACD_ = expedienteDeContratacionSAS_ACD_;
	}

	/** 
	 * 2 - Tipo documento: Este campo corresponde al elemento 1001. Los valores posibles son:
	 */
	public enum ERE1C_2 {
		PEDIDO_NORMAL_220("220"),
		PEDIDO_ABIERTO_221("221"),
		PEDIDO_URGENTE_224("224"),
		PEDIDO_PARCIAL_226("226"),
		PEDIDO_EN_CONSIGNA_227("227"),
		PROPUESTA_DE_PEDIDO_DEL_PROVEEDOR_22E("22E"),
		;
		
		private String value;
		
		private ERE1C_2(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_2 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 4 - Función del mensaje: Este campo corresponde al elemento 1225. Los valores posibles son:
	 */
	public enum ERE1C_4 {
		CONFIRMACION_6("6"),
		DUPLICADO_7("7"),
		ORIGINAL_9("9"),
		PROPUESTA_16("16"),
		COPIA_31("31"),
		CONFIRMACION_42("42"),
		PROVISIONAL_46("46"),
		FECHA_HORA_DOCUMENTO__137___102_203___EL_FORMATO_ACEPTADO_ES_CCYYMMDD_O_CCYYMMDDHHMM__5("5"),
		;
		
		private String value;
		
		private ERE1C_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 6, 7 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 6 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public enum ERE1C_6_7 {
		ENTREGA_FECHA_HORA__REQUERIDA_2("2"),
		ENVIADO_FECHA_Y_O_HORA_11("11"),
		ENTREGA_FECHA_HORA__ADELANTADA_64("64"),
		ENTREGA_FECHA_HORA__ATRASADA_63("63"),
		;
		
		private String value;
		
		private ERE1C_6_7(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_6_7 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 8, 9 - Fecha/Hora 2: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 8 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public enum ERE1C_8_9 {
		ENTREGA_FECHA_HORA__REQUERIDA_2("2"),
		ENVIADO_FECHA_Y_O_HORA_11("11"),
		ENTREGA_FECHA_HORA__ADELANTADA_64("64"),
		ENTREGA_FECHA_HORA__ATRASADA_63("63"),
		;
		
		private String value;
		
		private ERE1C_8_9(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_8_9 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 10 - Información adicional: Este campo se utiliza para especificar condiciones especiales. Los valores posibles son:
	 */
	public enum ERE1C_10 {
		CONDICIONES_DE_COMPRA_DEL_GRUPO_71E("71E"),
		CANCELAR_PEDIDO_SI_NO_ES_POSIBLE_LA_ENTREGA_TOTAL_EN_LAS_FECHAS_SOLICITADAS_72E("72E"),
		ENTREGA_SUJETA_A_AUTORIZACION_FINAL_73E("73E"),
		FACTURAR_PERO_NO_REABASTECER_81E("81E"),
		ENVIAR_PERO_NO_FACTURAR_82E("82E"),
		ENTREGAR_EL_PEDIDO_ENTERO_83E("83E"),
		;
		
		private String value;
		
		private ERE1C_10(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_10 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 14, 15 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 14 se corresponde con el elemento 1153. Los valores posibles son:
	 */
	public enum ERE1C_14_15 {
		NUMERO_DE_AUTORIZACION__EAN_CODE__ATZ("ATZ"),
		NUMERO_DE_REFERENCIA_DEL_CLIENTE_CR("CR"),
		NUMERO_DE_CONTRATO_CT("CT"),
		NUMERO_DE_LICENCIA_DE_IMPORTACION_IP("IP"),
		NUMERO_DE_ACUERDO_DE_PROMOCION_PD("PD"),
		ULTIMO_NUMERO_DE_REFERENCIA_DEL_CLIENTE_UC("UC"),
		;
		
		private String value;
		
		private ERE1C_14_15(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_14_15 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 16 - Código de moneda: Para especificar el código de moneda para todo el pedido. Este campo corresponde al elemento 6345. Los valores posibles son:
	 */
	public enum ERE1C_16 {
		EURO_EUR("EUR"),
		LIBRA_GBP("GBP"),
		;
		
		private String value;
		
		private ERE1C_16(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_16 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 18 - Método Pago de costes de transportes: Este campo corresponde al elemento 4215. Los valores posibles son:
	 */
	public enum ERE1C_18 {
		DEFINIDO_POR_EL_COMPRADOR_Y_EL_PROVEEDOR_DF("DF"),
		PORTES_PAGADOS_PERO_CARGADOS_AL_CLIENTE_PC("PC"),
		PORTES_PAGADOS_PP("PP"),
		;
		
		private String value;
		
		private ERE1C_18(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_18 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 19 - Condiciones de Entrega: Este campo corresponde al elemento 4053. Los valores posibles son:
	 */
	public enum ERE1C_19 {
		RECOGIDA_POR_EL_EMISOR_DEL_PEDIDO_PD("PD"),
		ENVIADA_POR_EL_RECEPTOR_DEL_PEDIDO_EP("EP"),
		;
		
		private String value;
		
		private ERE1C_19(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ERE1C_19 enumByValue(String value) {
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