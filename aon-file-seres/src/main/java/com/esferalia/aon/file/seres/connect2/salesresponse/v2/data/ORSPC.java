package com.esferalia.aon.file.seres.connect2.salesresponse.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ORSPC {

	private String tipoDocumento_231_;
	private String numeroDeRespuestaAlPedido;
	private String funcionDelMensaje;
	private String tipoDeRespuesta;
	private String fecha_horaDocumento_137_102_203_;
	private String calificadorFecha_hora1;
	private String fecha_hora1;
	private String calificadorFecha_hora2;
	private String fecha_hora2;
	private String informacionAdicional;
	private String numeroPedidoComprador_ON_;
	private String fechaPedido;
	private String numeroPedidoAbierto_BO_;
	private String numeroListaDePrecios_PL_;
	private String numeroPedidoProveedor_VN_;
	private String calificadorReferenciaAdicional;
	private String numeroReferenciaAdicional;
	private String fechaReferenciaAdicional;
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


	private static Pattern PATTERN_ORSPC_tipoDocumento_231_ = Pattern.compile("^.{6}(.{3}).*");
	private static Pattern PATTERN_ORSPC_numeroDeRespuestaAlPedido = Pattern.compile("^.{9}(.{17}).*");
	private static Pattern PATTERN_ORSPC_funcionDelMensaje = Pattern.compile("^.{26}(.{3}).*");
	private static Pattern PATTERN_ORSPC_tipoDeRespuesta = Pattern.compile("^.{29}(.{3}).*");
	private static Pattern PATTERN_ORSPC_fecha_horaDocumento_137_102_203_ = Pattern.compile("^.{32}(.{12}).*");
	private static Pattern PATTERN_ORSPC_calificadorFecha_hora1 = Pattern.compile("^.{44}(.{3}).*");
	private static Pattern PATTERN_ORSPC_fecha_hora1 = Pattern.compile("^.{47}(.{12}).*");
	private static Pattern PATTERN_ORSPC_calificadorFecha_hora2 = Pattern.compile("^.{59}(.{3}).*");
	private static Pattern PATTERN_ORSPC_fecha_hora2 = Pattern.compile("^.{62}(.{12}).*");
	private static Pattern PATTERN_ORSPC_informacionAdicional = Pattern.compile("^.{74}(.{3}).*");
	private static Pattern PATTERN_ORSPC_numeroPedidoComprador_ON_ = Pattern.compile("^.{77}(.{17}).*");
	private static Pattern PATTERN_ORSPC_fechaPedido = Pattern.compile("^.{94}(.{8}).*");
	private static Pattern PATTERN_ORSPC_numeroPedidoAbierto_BO_ = Pattern.compile("^.{102}(.{17}).*");
	private static Pattern PATTERN_ORSPC_numeroListaDePrecios_PL_ = Pattern.compile("^.{119}(.{17}).*");
	private static Pattern PATTERN_ORSPC_numeroPedidoProveedor_VN_ = Pattern.compile("^.{136}(.{17}).*");
	private static Pattern PATTERN_ORSPC_calificadorReferenciaAdicional = Pattern.compile("^.{153}(.{3}).*");
	private static Pattern PATTERN_ORSPC_numeroReferenciaAdicional = Pattern.compile("^.{156}(.{17}).*");
	private static Pattern PATTERN_ORSPC_fechaReferenciaAdicional = Pattern.compile("^.{173}(.{8}).*");
	private static Pattern PATTERN_ORSPC_codigoMoneda = Pattern.compile("^.{181}(.{3}).*");
	private static Pattern PATTERN_ORSPC_fechaDeVencimientoUnico = Pattern.compile("^.{184}(.{8}).*");
	private static Pattern PATTERN_ORSPC_metodoPagoDeCostesDeTransportes = Pattern.compile("^.{192}(.{3}).*");
	private static Pattern PATTERN_ORSPC_condicionesDeEntrega = Pattern.compile("^.{195}(.{3}).*");
	private static Pattern PATTERN_ORSPC_importeTotalNeto_79_ = Pattern.compile("^.{198}(.{18}).*");
	private static Pattern PATTERN_ORSPC_importeTotalDescuentos_Cargos_131_ = Pattern.compile("^.{216}(.{18}).*");
	private static Pattern PATTERN_ORSPC_baseImponible_125_ = Pattern.compile("^.{234}(.{18}).*");
	private static Pattern PATTERN_ORSPC_importeTotalImpuestos_176_ = Pattern.compile("^.{252}(.{18}).*");
	private static Pattern PATTERN_ORSPC_importeAPagar_139_ = Pattern.compile("^.{270}(.{18}).*");
	private static Pattern PATTERN_ORSPC_importeTotalBruto_98_ = Pattern.compile("^.{288}(.{18}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ORSPC_tipoDocumento_231_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDocumento_231_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_numeroDeRespuestaAlPedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeRespuestaAlPedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_funcionDelMensaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDelMensaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_tipoDeRespuesta.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeRespuesta(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_fecha_horaDocumento_137_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaDocumento_137_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_calificadorFecha_hora1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_hora1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_fecha_hora1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_hora1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_calificadorFecha_hora2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_hora2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_fecha_hora2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_hora2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_informacionAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInformacionAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_numeroPedidoComprador_ON_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroPedidoComprador_ON_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_fechaPedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaPedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_numeroPedidoAbierto_BO_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroPedidoAbierto_BO_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_numeroListaDePrecios_PL_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroListaDePrecios_PL_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_numeroPedidoProveedor_VN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroPedidoProveedor_VN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_calificadorReferenciaAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferenciaAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_numeroReferenciaAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroReferenciaAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_fechaReferenciaAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaReferenciaAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_codigoMoneda.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoMoneda(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_fechaDeVencimientoUnico.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeVencimientoUnico(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_metodoPagoDeCostesDeTransportes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMetodoPagoDeCostesDeTransportes(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_condicionesDeEntrega.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCondicionesDeEntrega(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_importeTotalNeto_79_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalNeto_79_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_importeTotalDescuentos_Cargos_131_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalDescuentos_Cargos_131_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_baseImponible_125_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBaseImponible_125_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_importeTotalImpuestos_176_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalImpuestos_176_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_importeAPagar_139_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteAPagar_139_(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPC_importeTotalBruto_98_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteTotalBruto_98_(Double.valueOf(m.group(1).trim()));
		}
	}

	
	/**
	 * 2 - Tipo documento: Este campo corresponde al elemento 1001. Los valores posibles son:
	 */
	public String getTipoDocumento_231_() {
		return tipoDocumento_231_;
	}

	/**
	 * 2 - Tipo documento: Este campo corresponde al elemento 1001. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Tipo documento (231)</td> <td>C</td> <td>3</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTipoDocumento_231_(String tipoDocumento_231_) {
		this.tipoDocumento_231_ = tipoDocumento_231_;
	}

	/**
	 * 3 - Número de respuesta al pedido: Referencia de la respuesta a una orden de compra. Esta referencia es asignada por el emisor del mensaje.
	 */
	public String getNumeroDeRespuestaAlPedido() {
		return numeroDeRespuestaAlPedido;
	}

	/**
	 * 3 - Número de respuesta al pedido: Referencia de la respuesta a una orden de compra. Esta referencia es asignada por el emisor del mensaje.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Número de respuesta al pedido</td> <td>C</td> <td>17</td> <td>10</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroDeRespuestaAlPedido(String numeroDeRespuestaAlPedido) {
		this.numeroDeRespuestaAlPedido = numeroDeRespuestaAlPedido;
	}

	/**
	 * 4 - Función del mensaje: Este campo corresponde al elemento 1225. Los valores posibles son:
	 */
	public String getFuncionDelMensaje() {
		return funcionDelMensaje;
	}


	/**
	 * 4 - Función del mensaje: Este campo corresponde al elemento 1225. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Función del mensaje</td> <td>C</td> <td>3</td> <td>27</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFuncionDelMensaje(String funcionDelMensaje) {
		this.funcionDelMensaje = funcionDelMensaje;
	}

	
	/**
	 * 5 - Tipo de respuesta
	 */
	public String getTipoDeRespuesta() {
		return tipoDeRespuesta;
	}


	/**
	 * 5 - Tipo de respuesta
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Tipo de respuesta</td> <td>C</td> <td>3</td> <td>30</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setTipoDeRespuesta(String tipoDeRespuesta) {
		this.tipoDeRespuesta = tipoDeRespuesta;
	}

	/**
	 * 6 - Fecha/hora Documento (137) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */
	public String getFecha_horaDocumento_137_102_203_() {
		return fecha_horaDocumento_137_102_203_;
	}


	/**
	 * 6 - Fecha/hora Documento (137) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Tipo de respuesta</td> <td>C</td> <td>12</td> <td>33</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFecha_horaDocumento_137_102_203_(String fecha_horaDocumento_137_102_203_) {
		this.fecha_horaDocumento_137_102_203_ = fecha_horaDocumento_137_102_203_;
	}

	/**
	 * 7, 8 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 6 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public String getCalificadorFecha_hora1() {
		return calificadorFecha_hora1;
	}


	/**
	 * 7, 8 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 6 se corresponde con el elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Calificador fecha/hora 1</td> <td>C</td> <td>3</td> <td>45</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorFecha_hora1(String calificadorFecha_hora1) {
		this.calificadorFecha_hora1 = calificadorFecha_hora1;
	}


	/**
	 * 7, 8 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 6 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public String getFecha_hora1() {
		return fecha_hora1;
	}


	/**
	 * 7, 8 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 6 se corresponde con el elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Fecha/hora 1</td> <td>C</td> <td>12</td> <td>48</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFecha_hora1(String fecha_hora1) {
		this.fecha_hora1 = fecha_hora1;
	}

	/**
	 * 9, 10 - Fecha/Hora 2: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 8 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public String getCalificadorFecha_hora2() {
		return calificadorFecha_hora2;
	}


	/**
	 * 9, 10 - Fecha/Hora 2: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 8 se corresponde con el elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Calificador fecha/hora 2</td> <td>C</td> <td>3</td> <td>60</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorFecha_hora2(String calificadorFecha_hora2) {
		this.calificadorFecha_hora2 = calificadorFecha_hora2;
	}


	/**
	 * 9, 10 - Fecha/Hora 2: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 8 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public String getFecha_hora2() {
		return fecha_hora2;
	}


	/**
	 * 9, 10 - Fecha/Hora 2: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 8 se corresponde con el elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Fecha/hora 2</td> <td>C</td> <td>12</td> <td>63</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFecha_hora2(String fecha_hora2) {
		this.fecha_hora2 = fecha_hora2;
	}

	/**
	 * 11 - Información adicional: Este campo se utiliza para especificar condiciones especiales. Los valores posibles son:
	 */
	public String getInformacionAdicional() {
		return informacionAdicional;
	}


	/**
	 * 11 - Información adicional: Este campo se utiliza para especificar condiciones especiales. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Información adicional</td> <td>C</td> <td>3</td> <td>75</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setInformacionAdicional(String informacionAdicional) {
		this.informacionAdicional = informacionAdicional;
	}

	/**
	 * 12 - Número pedido comprador (ON)
	 */
	public String getNumeroPedidoComprador_ON_() {
		return numeroPedidoComprador_ON_;
	}


	/**
	 * 12 - Número pedido comprador (ON)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Número pedido comprador (ON)</td> <td>C</td> <td>17</td> <td>78</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroPedidoComprador_ON_(String numeroPedidoComprador_ON_) {
		this.numeroPedidoComprador_ON_ = numeroPedidoComprador_ON_;
	}

	/**
	 * 13 -Número pedido comprador (ON)
	 */
	public String getFechaPedido() {
		return fechaPedido;
	}


	/**
	 * 13 -Número pedido comprador (ON)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Número pedido comprador (ON)</td> <td>C</td> <td>8</td> <td>95</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFechaPedido(String fechaPedido) {
		this.fechaPedido = fechaPedido;
	}

	/**
	 * 14 - Número pedido abierto (BO)
	 */
	public String getNumeroPedidoAbierto_BO_() {
		return numeroPedidoAbierto_BO_;
	}


	/**
	 * 14 - Número pedido abierto (BO)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Número pedido abierto (BO)</td> <td>C</td> <td>17</td> <td>103</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroPedidoAbierto_BO_(String numeroPedidoAbierto_BO_) {
		this.numeroPedidoAbierto_BO_ = numeroPedidoAbierto_BO_;
	}

	/**
	 * 15 - Número lista de precios (PL)
	 */
	public String getNumeroListaDePrecios_PL_() {
		return numeroListaDePrecios_PL_;
	}


	/**
	 * 15 - Número lista de precios (PL)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Número lista de precios (PL)</td> <td>C</td> <td>17</td> <td>120</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroListaDePrecios_PL_(String numeroListaDePrecios_PL_) {
		this.numeroListaDePrecios_PL_ = numeroListaDePrecios_PL_;
	}

	/**
	 * 16 - Número pedido proveedor (VN)
	 */
	public String getNumeroPedidoProveedor_VN_() {
		return numeroPedidoProveedor_VN_;
	}


	/**
	 * 16 - Número pedido proveedor (VN)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Número pedido proveedor (VN)</td> <td>C</td> <td>17</td> <td>137</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroPedidoProveedor_VN_(String numeroPedidoProveedor_VN_) {
		this.numeroPedidoProveedor_VN_ = numeroPedidoProveedor_VN_;
	}

	/**
	 * 17, 18 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 */
	public String getCalificadorReferenciaAdicional() {
		return calificadorReferenciaAdicional;
	}


	/**
	 * 17, 18 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Calificador referencia adicional</td> <td>C</td> <td>3</td> <td>154</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorReferenciaAdicional(String calificadorReferenciaAdicional) {
		this.calificadorReferenciaAdicional = calificadorReferenciaAdicional;
	}

	/**
	 * 17, 18 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 */
	public String getNumeroReferenciaAdicional() {
		return numeroReferenciaAdicional;
	}

	
	/**
	 * 17, 18 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Número referencia adicional</td> <td>C</td> <td>17</td> <td>157</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroReferenciaAdicional(String numeroReferenciaAdicional) {
		this.numeroReferenciaAdicional = numeroReferenciaAdicional;
	}

	/**
	 * 19 - Fecha referencia adicional: El formato aceptado es CCYYMMDD.
	 */
	public String getFechaReferenciaAdicional() {
		return fechaReferenciaAdicional;
	}


	/**
	 * 19 - Fecha referencia adicional: El formato aceptado es CCYYMMDD.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Fecha referencia adicional</td> <td>C</td> <td>8</td> <td>174</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFechaReferenciaAdicional(String fechaReferenciaAdicional) {
		this.fechaReferenciaAdicional = fechaReferenciaAdicional;
	}

	/**
	 * 20 - Código de moneda: Para especificar el código de moneda para todo la respuesta al pedido. Este campo corresponde al elemento 6345. Los valores posibles son:
	 */
	public String getCodigoMoneda() {
		return codigoMoneda;
	}


	/**
	 * 20 - Código de moneda: Para especificar el código de moneda para todo la respuesta al pedido. Este campo corresponde al elemento 6345. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Código moneda</td> <td>C</td> <td>3</td> <td>182</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCodigoMoneda(String codigoMoneda) {
		this.codigoMoneda = codigoMoneda;
	}

	/**
	 * 21 - Fecha de vencimiento único: Solo cuando la factura tiene vencimiento único, en otro caso se deberá utilizar el registro ORSPV.
	 */
	public String getFechaDeVencimientoUnico() {
		return fechaDeVencimientoUnico;
	}


	/**
	 * 21 - Fecha de vencimiento único: Solo cuando la factura tiene vencimiento único, en otro caso se deberá utilizar el registro ORSPV.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Fecha de vencimiento único</td> <td>C</td> <td>8</td> <td>185</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFechaDeVencimientoUnico(String fechaDeVencimientoUnico) {
		this.fechaDeVencimientoUnico = fechaDeVencimientoUnico;
	}

	/**
	 * 22 - Método Pago de costes de transportes: Este campo corresponde al elemento 4215. Los valores posibles son:
	 */
	public String getMetodoPagoDeCostesDeTransportes() {
		return metodoPagoDeCostesDeTransportes;
	}


	/**
	 * 22 - Método Pago de costes de transportes: Este campo corresponde al elemento 4215. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Método Pago de costes de transportes</td> <td>C</td> <td>3</td> <td>193</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setMetodoPagoDeCostesDeTransportes(String metodoPagoDeCostesDeTransportes) {
		this.metodoPagoDeCostesDeTransportes = metodoPagoDeCostesDeTransportes;
	}

	/**
	 * 23 - Condiciones de Entrega: Este campo corresponde al elemento 4053. Los valores posibles son:
	 */
	public String getCondicionesDeEntrega() {
		return condicionesDeEntrega;
	}


	/**
	 * 23 - Condiciones de Entrega: Este campo corresponde al elemento 4053. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Condiciones de Entrega</td> <td>C</td> <td>3</td> <td>196</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCondicionesDeEntrega(String condicionesDeEntrega) {
		this.condicionesDeEntrega = condicionesDeEntrega;
	}

	/**
	 * 24 - Importe total neto: Corresponde al sumatorio de los importes netos por línea.
	 */
	public Double getImporteTotalNeto_79_() {
		return importeTotalNeto_79_;
	}


	/**
	 * 24 - Importe total neto: Corresponde al sumatorio de los importes netos por línea.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Importe Total Neto (79)</td> <td>N(14,3)</td> <td>18</td> <td>199</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteTotalNeto_79_(Double importeTotalNeto_79_) {
		this.importeTotalNeto_79_ = importeTotalNeto_79_;
	}

	/**
	 * 25 - Importe total descuentos/cargos: Importe total de cargos y descuentos globales
	 */
	public Double getImporteTotalDescuentos_Cargos_131_() {
		return importeTotalDescuentos_Cargos_131_;
	}


	/**
	 * 25 - Importe total descuentos/cargos: Importe total de cargos y descuentos globales
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Importe Total Descuentos/Cargos (131)</td> <td>N(14,3)</td> <td>18</td> <td>217</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteTotalDescuentos_Cargos_131_(Double importeTotalDescuentos_Cargos_131_) {
		this.importeTotalDescuentos_Cargos_131_ = importeTotalDescuentos_Cargos_131_;
	}

	/**
	 * 26 - Base imponible: Importe Neto Total (Campo 24) + Total descuentos/cargos (25)
	 */
	public Double getBaseImponible_125_() {
		return baseImponible_125_;
	}


	/**
	 * 26 - Base imponible: Importe Neto Total (Campo 24) + Total descuentos/cargos (25)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Base Imponible (125)</td> <td>N(14,3)</td> <td>18</td> <td>235</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setBaseImponible_125_(Double baseImponible_125_) {
		this.baseImponible_125_ = baseImponible_125_;
	}

	/**
	 * 27 - Importe Total Impuestos (176)
	 */
	public Double getImporteTotalImpuestos_176_() {
		return importeTotalImpuestos_176_;
	}


	/**
	 * 27 - Importe Total Impuestos (176)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Importe Total Impuestos (176)</td> <td>N(14,3)</td> <td>18</td> <td>253</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteTotalImpuestos_176_(Double importeTotalImpuestos_176_) {
		this.importeTotalImpuestos_176_ = importeTotalImpuestos_176_;
	}

	/**
	 * 28 - Importe a Pagar: Base Imponible (26) + Importe Total de Impuestos (27).
	 */
	public Double getImporteAPagar_139_() {
		return importeAPagar_139_;
	}


	/**
	 * 28 - Importe a Pagar: Base Imponible (26) + Importe Total de Impuestos (27).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Importe a Pagar (139)</td> <td>N(14,3)</td> <td>18</td> <td>271</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteAPagar_139_(Double importeAPagar_139_) {
		this.importeAPagar_139_ = importeAPagar_139_;
	}

	/**
	 * 29 - Importe Total Bruto: Sumatorio de los importes Brutos de las líneas (cantidad facturada x precio unitario Bruto). No se tienen en cuenta Cargos ni Descuentos tanto a nivel de líneas como globales.
	 */
	public Double getImporteTotalBruto_98_() {
		return importeTotalBruto_98_;
	}


	/**
	 * 29 - Importe Total Bruto: Sumatorio de los importes Brutos de las líneas (cantidad facturada x precio unitario Bruto). No se tienen en cuenta Cargos ni Descuentos tanto a nivel de líneas como globales.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>29</td> <td>Importe Total Bruto (98)</td> <td>N(14,3)</td> <td>18</td> <td>289</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteTotalBruto_98_(Double importeTotalBruto_98_) {
		this.importeTotalBruto_98_ = importeTotalBruto_98_;
	}


	/** 
	 * 2 - Tipo documento: Este campo corresponde al elemento 1001. Los valores posibles son:
	 */
	public enum ORSPC_2 {
		RESPUESTA_A_UNA_ORDEN_DE_COMPRA("231"),
		;
		
		private String value;
		
		private ORSPC_2(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ORSPC_2 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	
	/** 
	 * 4 - Función del mensaje: Este campo corresponde al elemento 1225. Los valores posibles son:
	 */
	public enum ORSPC_4 {
		CAMBIO("4"),
		NO_PROCESADO("12"),
		NO_ACEPTADO("27"),
		ACEPTADO_SIN_CORRECCION("29")
		;
		
		private String value;
		
		private ORSPC_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPC_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 7, 8 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 6 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public enum ORSPC_7 {
		ENTREGA_FECHA_HORA_REQUERIDA("2"),
		ENVIADO_FECHA_Y_O_HORA("11"),
		ENTREGA_FECHA_HORA_ADELANTADA("64"),
		ENTREGA_FECHA_HORA_ATRASADA("63"),
		;
		
		private String value;
		
		private ORSPC_7(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPC_7 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 9, 10 - Fecha/Hora 2: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El Campo 8 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public enum ORSPC_9 {
		ENTREGA_FECHA_HORA_REQUERIDA("2"),
		ENVIADO_FECHA_Y_O_HORA("11"),
		ENTREGA_FECHA_HORA_ADELANTADA("64"),
		ENTREGA_FECHA_HORA_ATRASADA("63"),
		;
		
		private String value;
		
		private ORSPC_9(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPC_9 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 11 - Información adicional: Este campo se utiliza para especificar condiciones especiales. Los valores posibles son:
	 */
	public enum ORSPC_11 {
		CONDICIONES_DE_COMPRA_DEL_GRUPO("71E"),
		CANCELAR_PEDIDO_SI_NO_ES_POSIBLE_LA_ENTREGA_TOTAL_EN_LAS_FECHAS_SOLICITADAS("72E"),
		ENTREGA_SUJETA_A_AUTORIZACION_FINAL("73E"),
		FACTURAR_PERO_NO_REABASTECER("81E"),
		ENVIAR_PERO_NO_FACTURAR("82E"),
		ENTREGAR_EL_PEDIDO_ENTERO("83E"),
		;
		
		private String value;
		
		private ORSPC_11(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPC_11 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 17, 18 - Referencia adicional: Cuando es necesario enviar otra referencia relacionada con todo el mensaje. El Campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 */
	public enum ORSPC_17 {
		NUMERO_DE_AUTORIZACION_CODIGO_EAN_("ATZ"),
		NUMERO_DE_REFERENCIA_DEL_CLIENTE("CR"),
		NUMERO_DE_CONTRATO("CT"),
		NUMERO_DE_LICENCIA_DE_IMPORTACION("IP"),
		NUMERO_DE_ACUERDO_DE_PROMOCION("PD"),
		ULTIMO_NUMERO_DE_REFERENCIA_DEL_CLIENTE("UC"),
		;
		
		private String value;
		
		private ORSPC_17(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPC_17 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 20 - Código de moneda: Para especificar el código de moneda para todo la respuesta al pedido. Este campo corresponde al elemento 6345. Los valores posibles son:
	 */
	public enum ORSPC_20 {
		EURO("EUR"),
		LIBRA("GBP"),
		;
		
		private String value;
		
		private ORSPC_20(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPC_20 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 22 - Método Pago de costes de transportes: Este campo corresponde al elemento 4215. Los valores posibles son:
	 */
	public enum ORSPC_22 {
		DEFINIDO_POR_EL_COMPRADOR_Y_EL_PROVEEDOR("DF"),
		PORTES_PAGADOS_PERO_CARGADOS_AL_CLIENTE("PC"),
		PORTES_PAGADOS("PP"),
		;
		
		private String value;
		
		private ORSPC_22(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPC_22 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 23 - Condiciones de Entrega: Este campo corresponde al elemento 4053. Los valores posibles son:
	 */
	public enum ORSPC_23 {
		RECOGIDA_POR_EL_EMISOR_DEL_PEDIDO("PD"),
		ENVIADA_POR_EL_RECEPTOR_DEL_PEDIDO("EP"),
		;
		
		private String value;
		
		private ORSPC_23(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPC_23 enumByValue(String value) {
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