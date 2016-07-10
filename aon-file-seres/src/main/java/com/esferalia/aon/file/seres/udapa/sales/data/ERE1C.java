package com.esferalia.aon.file.seres.udapa.sales.data;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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

	private String tipoDePedido_220_221_224_226_22E_;
	private String numeroDePedido;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String funcionDelMensaje_31_;
	private Integer fechaDelDocumento_137__102_;
	private String calificadorFecha1;
	private Integer fechaDeServicio1;
	private Integer horaDeServicio1;
	private String calificadorFecha2;
	private Integer fechaDeServicio2;
	private Integer horaDeServicio2;
	private String condicionesEspeciales;
	private String numeroDePedidoAbierto_BO_;
	private String numeroDeListaDePrecios;
	private String numeroDePedidoProveedor_VN_;
	private String codigoComprador_BY_;
	private String codigoProveedor_SU_;
	private String codigoAQuienSeFactura_IV_;
	private String codigoPuntoDeEntrega_DP_;
	private String identificacionAdicionalP_Entrega_API_;
	private String numeroDeReposicion_ACD_;
	private String codigoPuntoDeExpedicion_PW_;
	private String codigoQuienPaga_PR_;
	private String codigoDeMoneda;
	private Integer fechaVencimientoUnico;
	private String metodoPagoDeCostesDeTransportes;
	private String condicionesDeEntrega;
	private Double importeTotalNeto_79_;
	private Double importeTotalDescuentos_Cargos_131_;
	private Double importeBaseImponible_125_;
	private Double importeTotalImpuestos_176_;
	private Double importeAPagar_139_;
	private Double importeTotalBruto_98_;
	private String estado;
	private String referenciaAdicional1_BYZZZ_;
	private String referenciaAdicional2_BYZZZ_;


	public List<ERE1T> ere1tList;
	public List<ERE1V> ere1vList;
	public List<ERE1D> ere1dList;
	public List<ERE1L> ere1lList;
	public List<ERE1G> ere1gList;
	public List<ERE1I> ere1iList;


	private static Pattern PATTERN_ERE1C_tipoDePedido_220_221_224_226_22E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_ERE1C_numeroDePedido = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_ERE1C_funcionDelMensaje_31_ = Pattern.compile("^.{63}(.{6}).*");
	private static Pattern PATTERN_ERE1C_fechaDelDocumento_137__102_ = Pattern.compile("^.{69}(.{8}).*");
	private static Pattern PATTERN_ERE1C_calificadorFecha1 = Pattern.compile("^.{77}(.{3}).*");
	private static Pattern PATTERN_ERE1C_fechaDeServicio1 = Pattern.compile("^.{80}(.{8}).*");
	private static Pattern PATTERN_ERE1C_horaDeServicio1 = Pattern.compile("^.{88}(.{4}).*");
	private static Pattern PATTERN_ERE1C_calificadorFecha2 = Pattern.compile("^.{92}(.{3}).*");
	private static Pattern PATTERN_ERE1C_fechaDeServicio2 = Pattern.compile("^.{95}(.{8}).*");
	private static Pattern PATTERN_ERE1C_horaDeServicio2 = Pattern.compile("^.{103}(.{4}).*");
	private static Pattern PATTERN_ERE1C_condicionesEspeciales = Pattern.compile("^.{107}(.{3}).*");
	private static Pattern PATTERN_ERE1C_numeroDePedidoAbierto_BO_ = Pattern.compile("^.{110}(.{17}).*");
	private static Pattern PATTERN_ERE1C_numeroDeListaDePrecios = Pattern.compile("^.{127}(.{17}).*");
	private static Pattern PATTERN_ERE1C_numeroDePedidoProveedor_VN_ = Pattern.compile("^.{144}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoComprador_BY_ = Pattern.compile("^.{161}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoProveedor_SU_ = Pattern.compile("^.{178}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoAQuienSeFactura_IV_ = Pattern.compile("^.{195}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoPuntoDeEntrega_DP_ = Pattern.compile("^.{212}(.{17}).*");
	private static Pattern PATTERN_ERE1C_identificacionAdicionalP_Entrega_API_ = Pattern.compile("^.{229}(.{17}).*");
	private static Pattern PATTERN_ERE1C_numeroDeReposicion_ACD_ = Pattern.compile("^.{246}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoPuntoDeExpedicion_PW_ = Pattern.compile("^.{263}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoQuienPaga_PR_ = Pattern.compile("^.{280}(.{17}).*");
	private static Pattern PATTERN_ERE1C_codigoDeMoneda = Pattern.compile("^.{297}(.{6}).*");
	private static Pattern PATTERN_ERE1C_fechaVencimientoUnico = Pattern.compile("^.{303}(.{8}).*");
	private static Pattern PATTERN_ERE1C_metodoPagoDeCostesDeTransportes = Pattern.compile("^.{311}(.{6}).*");
	private static Pattern PATTERN_ERE1C_condicionesDeEntrega = Pattern.compile("^.{317}(.{6}).*");
	private static Pattern PATTERN_ERE1C_importeTotalNeto_79_ = Pattern.compile("^.{323}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeTotalDescuentos_Cargos_131_ = Pattern.compile("^.{341}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeBaseImponible_125_ = Pattern.compile("^.{359}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeTotalImpuestos_176_ = Pattern.compile("^.{377}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeAPagar_139_ = Pattern.compile("^.{395}(.{18}).*");
	private static Pattern PATTERN_ERE1C_importeTotalBruto_98_ = Pattern.compile("^.{413}(.{18}).*");
	private static Pattern PATTERN_ERE1C_estado = Pattern.compile("^.{431}(.{1}).*");
	private static Pattern PATTERN_ERE1C_referenciaAdicional1_BYZZZ_ = Pattern.compile("^.{432}(.{17}).*");
	private static Pattern PATTERN_ERE1C_referenciaAdicional2_BYZZZ_ = Pattern.compile("^.{449}(.{17}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ERE1C_tipoDePedido_220_221_224_226_22E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDePedido_220_221_224_226_22E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroDePedido.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedido(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_funcionDelMensaje_31_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDelMensaje_31_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_fechaDelDocumento_137__102_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDelDocumento_137__102_(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_calificadorFecha1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_fechaDeServicio1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeServicio1(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_horaDeServicio1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setHoraDeServicio1(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_calificadorFecha2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_fechaDeServicio2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaDeServicio2(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_horaDeServicio2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setHoraDeServicio2(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_condicionesEspeciales.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCondicionesEspeciales(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroDePedidoAbierto_BO_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedidoAbierto_BO_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroDeListaDePrecios.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeListaDePrecios(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroDePedidoProveedor_VN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePedidoProveedor_VN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoComprador_BY_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoComprador_BY_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoProveedor_SU_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoProveedor_SU_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoAQuienSeFactura_IV_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoAQuienSeFactura_IV_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoPuntoDeEntrega_DP_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPuntoDeEntrega_DP_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_identificacionAdicionalP_Entrega_API_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificacionAdicionalP_Entrega_API_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_numeroDeReposicion_ACD_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeReposicion_ACD_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoPuntoDeExpedicion_PW_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPuntoDeExpedicion_PW_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoQuienPaga_PR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoQuienPaga_PR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_codigoDeMoneda.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDeMoneda(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_fechaVencimientoUnico.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFechaVencimientoUnico(Integer.valueOf(m.group(1).trim()));
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
		if((m = PATTERN_ERE1C_importeBaseImponible_125_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteBaseImponible_125_(Double.valueOf(m.group(1).trim()));
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
		if((m = PATTERN_ERE1C_estado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setEstado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_referenciaAdicional1_BYZZZ_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setReferenciaAdicional1_BYZZZ_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ERE1C_referenciaAdicional2_BYZZZ_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setReferenciaAdicional2_BYZZZ_(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * C1001T - Tipo de Pedido (220, 221, 224, 226, 22E): Existe un código para identificar cada tipo de pedido que queramos enviar.El campo corresponde a un código EDI. Los valores posibles:
	 */ 
	public String getTipoDePedido_220_221_224_226_22E_() {
		return tipoDePedido_220_221_224_226_22E_;
	}

	/** 
	 * C1001T - Tipo de Pedido (220, 221, 224, 226, 22E): Existe un código para identificar cada tipo de pedido que queramos enviar.El campo corresponde a un código EDI. Los valores posibles:
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
	 * C1004P - Número de Pedido: Se cumplimentará con el número de pedido correspondiente
	 */ 
	public String getNumeroDePedido() {
		return numeroDePedido;
	}

	/** 
	 * C1004P - Número de Pedido: Se cumplimentará con el número de pedido correspondiente
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
	 * C3039E - Código Emisor  (MS): Departamento que emite el pedido (función general asociada FGEN).
	 */ 
	public String getCodigoEmisor_MS_() {
		return codigoEmisor_MS_;
	}

	/** 
	 * C3039E - Código Emisor  (MS): Departamento que emite el pedido (función general asociada FGEN).
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
	 * C3039R - Código Receptor (MR): Código interno del Cliente que recibe el pedido (función asociada DSPI).
	 */ 
	public String getCodigoReceptor_MR_() {
		return codigoReceptor_MR_;
	}

	/** 
	 * C3039R - Código Receptor (MR): Código interno del Cliente que recibe el pedido (función asociada DSPI).
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
	 * C1225F - Función del Mensaje (31): El campo corresponde a un código EDI. Los valores posibles:
	 */ 
	public String getFuncionDelMensaje_31_() {
		return funcionDelMensaje_31_;
	}

	/** 
	 * C1225F - Función del Mensaje (31): El campo corresponde a un código EDI. Los valores posibles:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1225F</td> <td>Función del Mensaje (31)</td> <td>C</td> <td>6</td> <td>64</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFuncionDelMensaje_31_(String funcionDelMensaje_31_) {
		this.funcionDelMensaje_31_ = funcionDelMensaje_31_;
	}

	/** 
	 * C2380D - Fecha del Documento (137) (102): Fecha de generación del Documento en formato AAAAMMDD
	 */ 
	public Integer getFechaDelDocumento_137__102_() {
		return fechaDelDocumento_137__102_;
	}

	/** 
	 * C2380D - Fecha del Documento (137) (102): Fecha de generación del Documento en formato AAAAMMDD
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C2380D</td> <td>Fecha del Documento (137) (102)</td> <td>N</td> <td>8</td> <td>70</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDelDocumento_137__102_(Integer fechaDelDocumento_137__102_) {
		this.fechaDelDocumento_137__102_ = fechaDelDocumento_137__102_;
	}

	/** 
	 * C20051 / C20052 - Calificador de las Fechas de Entrega: El campo corresponde a un código EDI. Los valores posibles son:
	 */ 
	public String getCalificadorFecha1() {
		return calificadorFecha1;
	}

	/** 
	 * C20051 / C20052 - Calificador de las Fechas de Entrega: El campo corresponde a un código EDI. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C20051</td> <td>Calificador Fecha 1</td> <td>C</td> <td>3</td> <td>78</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha1(String calificadorFecha1) {
		this.calificadorFecha1 = calificadorFecha1;
	}

	/** 
	 * C2380F/C2380G - Fechas de Servicio en formato AAAAMMDD
	 */ 
	public Integer getFechaDeServicio1() {
		return fechaDeServicio1;
	}

	/** 
	 * C2380F/C2380G - Fechas de Servicio en formato AAAAMMDD
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C2380F</td> <td>Fecha de Servicio 1</td> <td>N</td> <td>8</td> <td>81</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeServicio1(Integer fechaDeServicio1) {
		this.fechaDeServicio1 = fechaDeServicio1;
	}

	/** 
	 * C2380H/C2380I - Hora de Servicio en formato HHMM
	 */ 
	public Integer getHoraDeServicio1() {
		return horaDeServicio1;
	}

	/** 
	 * C2380H/C2380I - Hora de Servicio en formato HHMM
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C2380H</td> <td>Hora de Servicio 1</td> <td>N</td> <td>4</td> <td>89</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setHoraDeServicio1(Integer horaDeServicio1) {
		this.horaDeServicio1 = horaDeServicio1;
	}

	/** 
	 * C20051 / C20052 - Calificador de las Fechas de Entrega: El campo corresponde a un código EDI. Los valores posibles son:
	 */ 
	public String getCalificadorFecha2() {
		return calificadorFecha2;
	}

	/** 
	 * C20051 / C20052 - Calificador de las Fechas de Entrega: El campo corresponde a un código EDI. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C20052</td> <td>Calificador Fecha 2</td> <td>C</td> <td>3</td> <td>93</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha2(String calificadorFecha2) {
		this.calificadorFecha2 = calificadorFecha2;
	}

	/** 
	 * C2380F/C2380G - Fechas de Servicio en formato AAAAMMDD
	 */ 
	public Integer getFechaDeServicio2() {
		return fechaDeServicio2;
	}

	/** 
	 * C2380F/C2380G - Fechas de Servicio en formato AAAAMMDD
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C2380G</td> <td>Fecha de Servicio 2</td> <td>N</td> <td>8</td> <td>96</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaDeServicio2(Integer fechaDeServicio2) {
		this.fechaDeServicio2 = fechaDeServicio2;
	}

	/** 
	 * C2380H/C2380I - Hora de Servicio en formato HHMM
	 */ 
	public Integer getHoraDeServicio2() {
		return horaDeServicio2;
	}

	/** 
	 * C2380H/C2380I - Hora de Servicio en formato HHMM
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C2380I</td> <td>Hora de Servicio 2</td> <td>N</td> <td>4</td> <td>104</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setHoraDeServicio2(Integer horaDeServicio2) {
		this.horaDeServicio2 = horaDeServicio2;
	}

	/** 
	 * C4183E - Condiciones Especiales: Campo opcional. Se utiliza para especificar información adicional relacionada con el pedido. Los valores posibles:
	 */ 
	public String getCondicionesEspeciales() {
		return condicionesEspeciales;
	}

	/** 
	 * C4183E - Condiciones Especiales: Campo opcional. Se utiliza para especificar información adicional relacionada con el pedido. Los valores posibles:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C4183E</td> <td>Condiciones Especiales</td> <td>C</td> <td>3</td> <td>108</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCondicionesEspeciales(String condicionesEspeciales) {
		this.condicionesEspeciales = condicionesEspeciales;
	}

	/** 
	 * C1154A - Número de Pedido Abierto (BO):En caso de estar montando una cancelación, hace referencia al número de Pedido Abierto enviado antes.
	 */ 
	public String getNumeroDePedidoAbierto_BO_() {
		return numeroDePedidoAbierto_BO_;
	}

	/** 
	 * C1154A - Número de Pedido Abierto (BO):En caso de estar montando una cancelación, hace referencia al número de Pedido Abierto enviado antes.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1154A</td> <td>Número de Pedido Abierto (BO)</td> <td>C</td> <td>17</td> <td>111</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePedidoAbierto_BO_(String numeroDePedidoAbierto_BO_) {
		this.numeroDePedidoAbierto_BO_ = numeroDePedidoAbierto_BO_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeListaDePrecios() {
		return numeroDeListaDePrecios;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1154L</td> <td>Número de Lista de Precios</td> <td>C</td> <td>17</td> <td>128</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeListaDePrecios(String numeroDeListaDePrecios) {
		this.numeroDeListaDePrecios = numeroDeListaDePrecios;
	}

	/** 
	 * C1154P - Código Proveedor (SU): Departamento al que se pide la mercancia (función asociada DSPI).
	 */ 
	public String getNumeroDePedidoProveedor_VN_() {
		return numeroDePedidoProveedor_VN_;
	}

	/** 
	 * C1154P - Código Proveedor (SU): Departamento al que se pide la mercancia (función asociada DSPI).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1154P</td> <td>Número de Pedido Proveedor (VN)</td> <td>C</td> <td>17</td> <td>145</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePedidoProveedor_VN_(String numeroDePedidoProveedor_VN_) {
		this.numeroDePedidoProveedor_VN_ = numeroDePedidoProveedor_VN_;
	}

	/** 
	 * C3039C - Código Comprador  (BY): Punto Operacional EDI del Cliente que hace el Pedido (función asociada QPID).
	 */ 
	public String getCodigoComprador_BY_() {
		return codigoComprador_BY_;
	}

	/** 
	 * C3039C - Código Comprador  (BY): Punto Operacional EDI del Cliente que hace el Pedido (función asociada QPID).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039C</td> <td>Código Comprador  (BY)</td> <td>C</td> <td>17</td> <td>162</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoComprador_BY_(String codigoComprador_BY_) {
		this.codigoComprador_BY_ = codigoComprador_BY_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoProveedor_SU_() {
		return codigoProveedor_SU_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039P</td> <td>Código Proveedor (SU)</td> <td>C</td> <td>17</td> <td>179</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoProveedor_SU_(String codigoProveedor_SU_) {
		this.codigoProveedor_SU_ = codigoProveedor_SU_;
	}

	/** 
	 * C3039F - Código A quien se Factura (IV): Departamento al que se factura (función asociada AQSF).
	 */ 
	public String getCodigoAQuienSeFactura_IV_() {
		return codigoAQuienSeFactura_IV_;
	}

	/** 
	 * C3039F - Código A quien se Factura (IV): Departamento al que se factura (función asociada AQSF).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039F</td> <td>Código A quien se Factura (IV)</td> <td>C</td> <td>17</td> <td>196</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoAQuienSeFactura_IV_(String codigoAQuienSeFactura_IV_) {
		this.codigoAQuienSeFactura_IV_ = codigoAQuienSeFactura_IV_;
	}

	/** 
	 * C3039Q - Código Punto de Entrega (DP): Código interno del Cliente al que se entrega la mercancía (función asociada QREC).
	 */ 
	public String getCodigoPuntoDeEntrega_DP_() {
		return codigoPuntoDeEntrega_DP_;
	}

	/** 
	 * C3039Q - Código Punto de Entrega (DP): Código interno del Cliente al que se entrega la mercancía (función asociada QREC).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039Q</td> <td>Código Punto de Entrega (DP)</td> <td>C</td> <td>17</td> <td>213</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoPuntoDeEntrega_DP_(String codigoPuntoDeEntrega_DP_) {
		this.codigoPuntoDeEntrega_DP_ = codigoPuntoDeEntrega_DP_;
	}

	/** 
	 * 
	 */ 
	public String getIdentificacionAdicionalP_Entrega_API_() {
		return identificacionAdicionalP_Entrega_API_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1154I</td> <td>Identificación Adicional  P. Entrega (API)</td> <td>C</td> <td>17</td> <td>230</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIdentificacionAdicionalP_Entrega_API_(String identificacionAdicionalP_Entrega_API_) {
		this.identificacionAdicionalP_Entrega_API_ = identificacionAdicionalP_Entrega_API_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeReposicion_ACD_() {
		return numeroDeReposicion_ACD_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1154R</td> <td>Número de Reposición (ACD)</td> <td>C</td> <td>17</td> <td>247</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeReposicion_ACD_(String numeroDeReposicion_ACD_) {
		this.numeroDeReposicion_ACD_ = numeroDeReposicion_ACD_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoPuntoDeExpedicion_PW_() {
		return codigoPuntoDeExpedicion_PW_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039M</td> <td>Código Punto de Expedición (PW)</td> <td>C</td> <td>17</td> <td>264</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoPuntoDeExpedicion_PW_(String codigoPuntoDeExpedicion_PW_) {
		this.codigoPuntoDeExpedicion_PW_ = codigoPuntoDeExpedicion_PW_;
	}

	/** 
	 * 
	 */ 
	public String getCodigoQuienPaga_PR_() {
		return codigoQuienPaga_PR_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C3039A</td> <td>Código quien Paga (PR)</td> <td>C</td> <td>17</td> <td>281</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoQuienPaga_PR_(String codigoQuienPaga_PR_) {
		this.codigoQuienPaga_PR_ = codigoQuienPaga_PR_;
	}

	/** 
	 * C6345M - Código de Moneda: Los valores posibles:
	 */ 
	public String getCodigoDeMoneda() {
		return codigoDeMoneda;
	}

	/** 
	 * C6345M - Código de Moneda: Los valores posibles:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C6345M</td> <td>Código de Moneda</td> <td>C</td> <td>6</td> <td>298</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDeMoneda(String codigoDeMoneda) {
		this.codigoDeMoneda = codigoDeMoneda;
	}

	/** 
	 * C2380V - Fecha vencimiento único: Está pensado para que el emisor del pedido pueda solicitar una fecha de pago. Se indicará si la factura es de Pago único, en caso contrario se dejará a cero y los vencimientos se especificaran en el fichero ERE1V. Debe montarse en formato AAAAMMDD.
	 */ 
	public Integer getFechaVencimientoUnico() {
		return fechaVencimientoUnico;
	}

	/** 
	 * C2380V - Fecha vencimiento único: Está pensado para que el emisor del pedido pueda solicitar una fecha de pago. Se indicará si la factura es de Pago único, en caso contrario se dejará a cero y los vencimientos se especificaran en el fichero ERE1V. Debe montarse en formato AAAAMMDD.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C2380V</td> <td>Fecha vencimiento único</td> <td>N</td> <td>8</td> <td>304</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFechaVencimientoUnico(Integer fechaVencimientoUnico) {
		this.fechaVencimientoUnico = fechaVencimientoUnico;
	}

	/** 
	 * C4215P - Método Pago de costes de transportes: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getMetodoPagoDeCostesDeTransportes() {
		return metodoPagoDeCostesDeTransportes;
	}

	/** 
	 * C4215P - Método Pago de costes de transportes: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C4215P</td> <td>Método Pago de costes de transportes</td> <td>C</td> <td>6</td> <td>312</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMetodoPagoDeCostesDeTransportes(String metodoPagoDeCostesDeTransportes) {
		this.metodoPagoDeCostesDeTransportes = metodoPagoDeCostesDeTransportes;
	}

	/** 
	 * C4053E - Condiciones de Entrega: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */ 
	public String getCondicionesDeEntrega() {
		return condicionesDeEntrega;
	}

	/** 
	 * C4053E - Condiciones de Entrega: El campo corresponde a un código EANCOM. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C4053E</td> <td>Condiciones de Entrega</td> <td>C</td> <td>6</td> <td>318</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCondicionesDeEntrega(String condicionesDeEntrega) {
		this.condicionesDeEntrega = condicionesDeEntrega;
	}

	/** 
	 * C5004N - Importe Total Neto (79): Corresponde al sumatorio de los importes netos por línea si se valora el pedido.
	 */ 
	public Double getImporteTotalNeto_79_() {
		return importeTotalNeto_79_;
	}

	/** 
	 * C5004N - Importe Total Neto (79): Corresponde al sumatorio de los importes netos por línea si se valora el pedido.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5004N</td> <td>Importe Total Neto (79)</td> <td>N(14,3)</td> <td>18</td> <td>324</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalNeto_79_(Double importeTotalNeto_79_) {
		this.importeTotalNeto_79_ = importeTotalNeto_79_;
	}

	/** 
	 * C5004D - Importe Total Descuentos/Cargos (131): Suma de todos los descuentos y cargos globales del pedido, excluyendo los de líneas (en caso de pedidos valorados).
	 */ 
	public Double getImporteTotalDescuentos_Cargos_131_() {
		return importeTotalDescuentos_Cargos_131_;
	}

	/** 
	 * C5004D - Importe Total Descuentos/Cargos (131): Suma de todos los descuentos y cargos globales del pedido, excluyendo los de líneas (en caso de pedidos valorados).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5004D</td> <td>Importe Total Descuentos/Cargos (131)</td> <td>N(14,3)</td> <td>18</td> <td>342</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalDescuentos_Cargos_131_(Double importeTotalDescuentos_Cargos_131_) {
		this.importeTotalDescuentos_Cargos_131_ = importeTotalDescuentos_Cargos_131_;
	}

	/** 
	 * C5004B - Importe Base Imponible (125):Importe Neto Total  de Factura (C500N) + Total cargos y descuentos Gobales (C5004D)
	 */ 
	public Double getImporteBaseImponible_125_() {
		return importeBaseImponible_125_;
	}

	/** 
	 * C5004B - Importe Base Imponible (125):Importe Neto Total  de Factura (C500N) + Total cargos y descuentos Gobales (C5004D)
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5004B</td> <td>Importe Base Imponible (125)</td> <td>N(14,3)</td> <td>18</td> <td>360</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteBaseImponible_125_(Double importeBaseImponible_125_) {
		this.importeBaseImponible_125_ = importeBaseImponible_125_;
	}

	/** 
	 * C5004I - Importe Total Impuestos (176): Sumatorio de los importes  de impuestos por línea.
	 */ 
	public Double getImporteTotalImpuestos_176_() {
		return importeTotalImpuestos_176_;
	}

	/** 
	 * C5004I - Importe Total Impuestos (176): Sumatorio de los importes  de impuestos por línea.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5004I</td> <td>Importe Total Impuestos (176)</td> <td>N(14,3)</td> <td>18</td> <td>378</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalImpuestos_176_(Double importeTotalImpuestos_176_) {
		this.importeTotalImpuestos_176_ = importeTotalImpuestos_176_;
	}

	/** 
	 * C5004P - Importe a Pagar (139): Base Imponible + Importe Total de Impuestos
	 */ 
	public Double getImporteAPagar_139_() {
		return importeAPagar_139_;
	}

	/** 
	 * C5004P - Importe a Pagar (139): Base Imponible + Importe Total de Impuestos
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5004P</td> <td>Importe a Pagar (139)</td> <td>N(14,3)</td> <td>18</td> <td>396</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteAPagar_139_(Double importeAPagar_139_) {
		this.importeAPagar_139_ = importeAPagar_139_;
	}

	/** 
	 * C5004U - Importe Total Bruto (98): Sumatorio de los importes Brutos de las líneas (cantidad facturada x precio unitario Bruto).No se tienen en cuenta Cargos ni Descuentos(ni a nivel de líneas, ni globales).
	 */ 
	public Double getImporteTotalBruto_98_() {
		return importeTotalBruto_98_;
	}

	/** 
	 * C5004U - Importe Total Bruto (98): Sumatorio de los importes Brutos de las líneas (cantidad facturada x precio unitario Bruto).No se tienen en cuenta Cargos ni Descuentos(ni a nivel de líneas, ni globales).
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C5004U</td> <td>Importe Total Bruto (98)</td> <td>N(14,3)</td> <td>18</td> <td>414</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setImporteTotalBruto_98_(Double importeTotalBruto_98_) {
		this.importeTotalBruto_98_ = importeTotalBruto_98_;
	}

	/** 
	 * 
	 */ 
	public String getEstado() {
		return estado;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>CESTAD</td> <td>Estado</td> <td>C</td> <td>1</td> <td>432</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/** 
	 * 
	 */ 
	public String getReferenciaAdicional1_BYZZZ_() {
		return referenciaAdicional1_BYZZZ_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1154B</td> <td>Referencia Adicional  1 (BY ZZZ)</td> <td>C</td> <td>17</td> <td>433</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setReferenciaAdicional1_BYZZZ_(String referenciaAdicional1_BYZZZ_) {
		this.referenciaAdicional1_BYZZZ_ = referenciaAdicional1_BYZZZ_;
	}

	/** 
	 * 
	 */ 
	public String getReferenciaAdicional2_BYZZZ_() {
		return referenciaAdicional2_BYZZZ_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>C1154C</td> <td>Referencia Adicional  2 (BY ZZZ)</td> <td>C</td> <td>17</td> <td>450</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setReferenciaAdicional2_BYZZZ_(String referenciaAdicional2_BYZZZ_) {
		this.referenciaAdicional2_BYZZZ_ = referenciaAdicional2_BYZZZ_;
	}

	/** 
	 * C1001T - Tipo de Pedido (220, 221, 224, 226, 22E): Existe un código para identificar cada tipo de pedido que queramos enviar.El campo corresponde a un código EDI. Los valores posibles:
	 */
	public enum C1001T {
		PEDID_220("220"),
		PEDIDO_ABIERT_221("221"),
		PEDIDO_URGENT_224("224"),
		CANCELACION_DE_PEDID_226("226"),
		AUTOPEDIDO__ES_EMITIDO_POR_EL_FABRICANTE_PARA_AVISAR_DE_LA_MERCANCIA_QUE_VA_A_SERVIR__22E("22E"),
		;
		
		private String value;
		
		private C1001T(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C1001T enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C1225F - Función del Mensaje (31): El campo corresponde a un código EDI. Los valores posibles:
	 */
	public enum C1225F {
		CONFIRMATIO_6("6"),
		DUPLICAT_7("7"),
		ORIGINA_9("9"),
		PROPOSA_16("16"),
		COP_31("31"),
		CONFIRMATION_VIA_SPECIFIC_MEAN_42("42"),
		PROVISIONA_46("46"),
		;
		
		private String value;
		
		private C1225F(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C1225F enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C20051 / C20052 - Calificador de las Fechas de Entrega: El campo corresponde a un código EDI. Los valores posibles son:
	 */
	public enum C20051_C20052 {
		DELIVERY_DATE_TIME__REQUESTE_2("2"),
		SHIPMENT_DATE_TIME__REQUESTE_10("10"),
		DESPATCH_DATE_AND_OR_TIM_11("11"),
		PROMOTION_START_DATE_TIME___NOTE_15("15"),
		SHIP_NOT_BEFORE_DATE_TIM_37("37"),
		SHIP_NOT_LATER_THAN_DATE_TIM_38("38"),
		CANCEL_IF_NOT_DELIVERED_BY_THIS_DAT_61("61"),
		DELIVERY_DATE_TIME__LATEST____FECHA_HORA_LIMITE_DE_ENTREGA_63("63"),
		DELIVERY_DATE_TIME__EARLIEST____ENTREGAR_A_PARTIR_DE_FECHA_HORA_64("64"),
		DELIVERY_DATE_TIME__PROMISED_FOR___FECHA_HORA_DE_ENTREGA_FIJA_69("69"),
		REQUESTED_FOR_DELIVERY_WEEK_COMMENCING__EAN_CODE_77("77"),
		DOCUMENT_MESSAGE_DATE_TIM_137("137"),
		PICK_UP_COLLECTION_DATE_TIME_OF_CARG_200("200"),
		INVOICING_PERIO_263("263"),
		CONFIRMATION_DATE_LEAD_TIM_282("282"),
		CANCEL_IF_NOT_SHIPPED_DESPATCHED_BY_THIS_DATE__EAN_CODE_43E("43E"),
		;
		
		private String value;
		
		private C20051_C20052(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C20051_C20052 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C4183E - Condiciones Especiales: Campo opcional. Se utiliza para especificar información adicional relacionada con el pedido. Los valores posibles:
	 */
	public enum C4183E {
		BUYING_GROUP_CONDITIONS__EAN_CODE_71E("71E"),
		CANCEL_ORDER_UNLESS_COMPLETE_DELIVERY_POSSIBLE_ON_REQUESTED_DATE_TIME__EAN_CODE_72E("72E"),
		DELIVERY_SUBJECT_TO_FINAL_AUTHORIZATION__EAN_CODE_73E("73E"),
		INVOICED_BUT_DO_NOT_REPLENISH__EAN_CODE_81E("81E"),
		REPLENISHED_BUT_DO_NOT_INVOICE__EAN_CODE_82E("82E"),
		DELIVER_FULL_ORDER__EAN_CODE_83E("83E"),
		;
		
		private String value;
		
		private C4183E(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C4183E enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C6345M - Código de Moneda: Los valores posibles:
	 */
	public enum C6345M {
		PESETA_ESPA_OL_ESP("ESP"),
		EURO_EUROPE_EUR("EUR"),
		ESCUDO_PORTUGUE_PTE("PTE"),
		;
		
		private String value;
		
		private C6345M(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C6345M enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C4215P - Método Pago de costes de transportes: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum C4215P {
		COLLEC_CC("CC"),
		MIXE_MX("MX"),
		PREPAID_BUT_CHARGED_TO_CUSTOME_PC("PC"),
		PREPAID_ONL_PO("PO"),
		PORTES_PAGADO_PP("PP"),
		PORTES_DEBIDO_PD("PD"),
		;
		
		private String value;
		
		private C4215P(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C4215P enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * C4053E - Condiciones de Entrega: El campo corresponde a un código EANCOM. Los valores posibles son:
	 */
	public enum C4053E {
		RECOGIDA_POR_EL_EMISOR_DEL_PEDID_PD("PD"),
		ENVIADA_POR_EL_RECEPTOR_DEL_PEDID_EP("EP"),
		;
		
		private String value;
		
		private C4053E(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static C4053E enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}