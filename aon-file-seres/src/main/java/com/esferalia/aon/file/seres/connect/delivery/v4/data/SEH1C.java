package com.esferalia.aon.file.seres.connect.delivery.v4.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1C entity.
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
 * 		 <td>SEH1C</td> <td>Cabecera</td> <td>Obligatorio</td> <td>1</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1C {

	private String tipoDeDocumento_351_35E_;
	private String numeroDelDocumento;
	private String funcionDelMensaje;
	private String fecha_horaDelDocumento_137__102_203_;
	private String fecha_horaEstimadaDeEntrega_17__102_203_;
	private String calificadorFecha_Hora1_2_11_64_;
	private String fecha_hora1;
	private String calificadorFecha_Hora2_2_11_63_;
	private String fecha_hora2;
	private String informacionAdicional;
	private String numeroPedido_comprador__ON_;
	private String fecha_horaNumeroPedido_171__102_203_;
	private String numeroAlbaran_DQ_;
	private String fecha_horaNumeroAlbaran_171__102_203_;
	private String calificadorDeReferencia1;
	private String numeroDeReferencia1;
	private String fecha_horaReferencia1_102_203_;
	private String calificadorDeReferencia2;
	private String numeroDeReferencia2;
	private String fecha_horaReferencia2_102_203_;
	private String metodoPagoDeCostesDeTransporte;
	private String condicionesDeEntregaOTransporte_Codificada;
	private String condicionesDeEntregaOTransporte_TextoLibre;
	private String modoDeTransporte_Codificado;
	private String identificacionDeTransportista;
	private String nombreDelTransportista;
	private String matriculaDelVehiculo;
	private String lugarDeEntrega_Codificado_8_;
	private String lugarDeEntrega_TextoLibre_8_;


	private static Pattern PATTERN_SEH1C_tipoDeDocumento_351_35E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SEH1C_numeroDelDocumento = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SEH1C_funcionDelMensaje = Pattern.compile("^.{29}(.{6}).*");
	private static Pattern PATTERN_SEH1C_fecha_horaDelDocumento_137__102_203_ = Pattern.compile("^.{35}(.{12}).*");
	private static Pattern PATTERN_SEH1C_fecha_horaEstimadaDeEntrega_17__102_203_ = Pattern.compile("^.{47}(.{12}).*");
	private static Pattern PATTERN_SEH1C_calificadorFecha_Hora1_2_11_64_ = Pattern.compile("^.{59}(.{3}).*");
	private static Pattern PATTERN_SEH1C_fecha_hora1 = Pattern.compile("^.{62}(.{12}).*");
	private static Pattern PATTERN_SEH1C_calificadorFecha_Hora2_2_11_63_ = Pattern.compile("^.{74}(.{3}).*");
	private static Pattern PATTERN_SEH1C_fecha_hora2 = Pattern.compile("^.{77}(.{12}).*");
	private static Pattern PATTERN_SEH1C_informacionAdicional = Pattern.compile("^.{89}(.{6}).*");
	private static Pattern PATTERN_SEH1C_numeroPedido_comprador__ON_ = Pattern.compile("^.{95}(.{17}).*");
	private static Pattern PATTERN_SEH1C_fecha_horaNumeroPedido_171__102_203_ = Pattern.compile("^.{112}(.{12}).*");
	private static Pattern PATTERN_SEH1C_numeroAlbaran_DQ_ = Pattern.compile("^.{124}(.{17}).*");
	private static Pattern PATTERN_SEH1C_fecha_horaNumeroAlbaran_171__102_203_ = Pattern.compile("^.{141}(.{12}).*");
	private static Pattern PATTERN_SEH1C_calificadorDeReferencia1 = Pattern.compile("^.{153}(.{3}).*");
	private static Pattern PATTERN_SEH1C_numeroDeReferencia1 = Pattern.compile("^.{156}(.{17}).*");
	private static Pattern PATTERN_SEH1C_fecha_horaReferencia1_102_203_ = Pattern.compile("^.{173}(.{12}).*");
	private static Pattern PATTERN_SEH1C_calificadorDeReferencia2 = Pattern.compile("^.{185}(.{3}).*");
	private static Pattern PATTERN_SEH1C_numeroDeReferencia2 = Pattern.compile("^.{188}(.{17}).*");
	private static Pattern PATTERN_SEH1C_fecha_horaReferencia2_102_203_ = Pattern.compile("^.{205}(.{12}).*");
	private static Pattern PATTERN_SEH1C_metodoPagoDeCostesDeTransporte = Pattern.compile("^.{217}(.{3}).*");
	private static Pattern PATTERN_SEH1C_condicionesDeEntregaOTransporte_Codificada = Pattern.compile("^.{220}(.{3}).*");
	private static Pattern PATTERN_SEH1C_condicionesDeEntregaOTransporte_TextoLibre = Pattern.compile("^.{223}(.{70}).*");
	private static Pattern PATTERN_SEH1C_modoDeTransporte_Codificado = Pattern.compile("^.{293}(.{3}).*");
	private static Pattern PATTERN_SEH1C_identificacionDeTransportista = Pattern.compile("^.{296}(.{13}).*");
	private static Pattern PATTERN_SEH1C_nombreDelTransportista = Pattern.compile("^.{309}(.{35}).*");
	private static Pattern PATTERN_SEH1C_matriculaDelVehiculo = Pattern.compile("^.{344}(.{17}).*");
	private static Pattern PATTERN_SEH1C_lugarDeEntrega_Codificado_8_ = Pattern.compile("^.{361}(.{17}).*");
	private static Pattern PATTERN_SEH1C_lugarDeEntrega_TextoLibre_8_ = Pattern.compile("^.{378}(.{70}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1C_tipoDeDocumento_351_35E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeDocumento_351_35E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_numeroDelDocumento.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDelDocumento(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_funcionDelMensaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDelMensaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_fecha_horaDelDocumento_137__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaDelDocumento_137__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_fecha_horaEstimadaDeEntrega_17__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaEstimadaDeEntrega_17__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_calificadorFecha_Hora1_2_11_64_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_Hora1_2_11_64_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_fecha_hora1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_hora1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_calificadorFecha_Hora2_2_11_63_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorFecha_Hora2_2_11_63_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_fecha_hora2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_hora2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_informacionAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInformacionAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_numeroPedido_comprador__ON_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroPedido_comprador__ON_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_fecha_horaNumeroPedido_171__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaNumeroPedido_171__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_numeroAlbaran_DQ_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroAlbaran_DQ_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_fecha_horaNumeroAlbaran_171__102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaNumeroAlbaran_171__102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_calificadorDeReferencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDeReferencia1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_numeroDeReferencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeReferencia1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_fecha_horaReferencia1_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaReferencia1_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_calificadorDeReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDeReferencia2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_numeroDeReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeReferencia2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_fecha_horaReferencia2_102_203_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFecha_horaReferencia2_102_203_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_metodoPagoDeCostesDeTransporte.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMetodoPagoDeCostesDeTransporte(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_condicionesDeEntregaOTransporte_Codificada.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCondicionesDeEntregaOTransporte_Codificada(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_condicionesDeEntregaOTransporte_TextoLibre.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCondicionesDeEntregaOTransporte_TextoLibre(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_modoDeTransporte_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setModoDeTransporte_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_identificacionDeTransportista.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setIdentificacionDeTransportista(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_nombreDelTransportista.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombreDelTransportista(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_matriculaDelVehiculo.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMatriculaDelVehiculo(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_lugarDeEntrega_Codificado_8_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setLugarDeEntrega_Codificado_8_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1C_lugarDeEntrega_TextoLibre_8_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setLugarDeEntrega_TextoLibre_8_(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Tipo de documento (351/35E): Este campo corresponde al elemento 1001. Los valores posibles son:
	 */ 
	public String getTipoDeDocumento_351_35E_() {
		return tipoDeDocumento_351_35E_;
	}

	/** 
	 * 2 - Tipo de documento (351/35E): Este campo corresponde al elemento 1001. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Tipo de documento (351/35E)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeDocumento_351_35E_(String tipoDeDocumento_351_35E_) {
		this.tipoDeDocumento_351_35E_ = tipoDeDocumento_351_35E_;
	}

	/** 
	 * 351 - Notas de envió
	 */ 
	public String getNumeroDelDocumento() {
		return numeroDelDocumento;
	}

	/** 
	 * 351 - Notas de envió
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Número del documento</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDelDocumento(String numeroDelDocumento) {
		this.numeroDelDocumento = numeroDelDocumento;
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
	 * 		 <td>4</td> <td>Función del mensaje</td> <td>C</td> <td>6</td> <td>30</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFuncionDelMensaje(String funcionDelMensaje) {
		this.funcionDelMensaje = funcionDelMensaje;
	}

	/** 
	 * 351 - Notas de envió
	 */ 
	public String getFecha_horaDelDocumento_137__102_203_() {
		return fecha_horaDelDocumento_137__102_203_;
	}

	/** 
	 * 351 - Notas de envió
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Fecha/hora del documento (137) (102/203)</td> <td>C</td> <td>12</td> <td>36</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaDelDocumento_137__102_203_(String fecha_horaDelDocumento_137__102_203_) {
		this.fecha_horaDelDocumento_137__102_203_ = fecha_horaDelDocumento_137__102_203_;
	}

	/** 
	 * 6 - Fecha/hora Entrega estimada (17) (102/203): Fecha y/o hora en que el expedidor de la mercancía estima la entrega. Los formatos aceptados son CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaEstimadaDeEntrega_17__102_203_() {
		return fecha_horaEstimadaDeEntrega_17__102_203_;
	}

	/** 
	 * 6 - Fecha/hora Entrega estimada (17) (102/203): Fecha y/o hora en que el expedidor de la mercancía estima la entrega. Los formatos aceptados son CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Fecha/hora estimada de entrega(17) (102/203)</td> <td>C</td> <td>12</td> <td>48</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaEstimadaDeEntrega_17__102_203_(String fecha_horaEstimadaDeEntrega_17__102_203_) {
		this.fecha_horaEstimadaDeEntrega_17__102_203_ = fecha_horaEstimadaDeEntrega_17__102_203_;
	}

	/** 
	 * 7 - Duplicado - Un reenvió del mensaje, a solicitud del receptor
	 */ 
	public String getCalificadorFecha_Hora1_2_11_64_() {
		return calificadorFecha_Hora1_2_11_64_;
	}

	/** 
	 * 7 - Duplicado - Un reenvió del mensaje, a solicitud del receptor
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Calificador Fecha/Hora 1 (2-11-64)</td> <td>C</td> <td>3</td> <td>60</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha_Hora1_2_11_64_(String calificadorFecha_Hora1_2_11_64_) {
		this.calificadorFecha_Hora1_2_11_64_ = calificadorFecha_Hora1_2_11_64_;
	}

	/** 
	 * 7, 8 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El campo 7 se corresponde con el elemento 2005. Los valores posibles son:
	 */ 
	public String getFecha_hora1() {
		return fecha_hora1;
	}

	/** 
	 * 7, 8 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El campo 7 se corresponde con el elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Fecha/hora 1</td> <td>C</td> <td>12</td> <td>63</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_hora1(String fecha_hora1) {
		this.fecha_hora1 = fecha_hora1;
	}

	/** 
	 * 9 - Original - El envío de un aviso de expedición original
	 */ 
	public String getCalificadorFecha_Hora2_2_11_63_() {
		return calificadorFecha_Hora2_2_11_63_;
	}

	/** 
	 * 9 - Original - El envío de un aviso de expedición original
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Calificador Fecha/Hora 2 (2-11-63)</td> <td>C</td> <td>3</td> <td>75</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorFecha_Hora2_2_11_63_(String calificadorFecha_Hora2_2_11_63_) {
		this.calificadorFecha_Hora2_2_11_63_ = calificadorFecha_Hora2_2_11_63_;
	}

	/** 
	 * 9, 10 - Fecha/hora 2: cuando es necesario enviar otras fechas relacionadas con todos los mensajes. El campo 9 se corresponde con el elemento 2005. Los valores posibles son:
	 */ 
	public String getFecha_hora2() {
		return fecha_hora2;
	}

	/** 
	 * 9, 10 - Fecha/hora 2: cuando es necesario enviar otras fechas relacionadas con todos los mensajes. El campo 9 se corresponde con el elemento 2005. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Fecha/hora 2</td> <td>C</td> <td>12</td> <td>78</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_hora2(String fecha_hora2) {
		this.fecha_hora2 = fecha_hora2;
	}

	/** 
	 * 11 - Enviado fecha y/o hora
	 */ 
	public String getInformacionAdicional() {
		return informacionAdicional;
	}

	/** 
	 * 11 - Enviado fecha y/o hora
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Información adicional</td> <td>C</td> <td>6</td> <td>90</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setInformacionAdicional(String informacionAdicional) {
		this.informacionAdicional = informacionAdicional;
	}

	/** 
	 * 
	 */ 
	public String getNumeroPedido_comprador__ON_() {
		return numeroPedido_comprador__ON_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Número pedido (comprador) (ON)</td> <td>C</td> <td>17</td> <td>96</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroPedido_comprador__ON_(String numeroPedido_comprador__ON_) {
		this.numeroPedido_comprador__ON_ = numeroPedido_comprador__ON_;
	}

	/** 
	 * 13 - Fecha/hora número pedido (171) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaNumeroPedido_171__102_203_() {
		return fecha_horaNumeroPedido_171__102_203_;
	}

	/** 
	 * 13 - Fecha/hora número pedido (171) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Fecha/hora número pedido (171) (102/203)</td> <td>C</td> <td>12</td> <td>113</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaNumeroPedido_171__102_203_(String fecha_horaNumeroPedido_171__102_203_) {
		this.fecha_horaNumeroPedido_171__102_203_ = fecha_horaNumeroPedido_171__102_203_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroAlbaran_DQ_() {
		return numeroAlbaran_DQ_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Número albarán (DQ)</td> <td>C</td> <td>17</td> <td>125</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroAlbaran_DQ_(String numeroAlbaran_DQ_) {
		this.numeroAlbaran_DQ_ = numeroAlbaran_DQ_;
	}

	/** 
	 * 15 - Fecha/hora número albarán (171) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 */ 
	public String getFecha_horaNumeroAlbaran_171__102_203_() {
		return fecha_horaNumeroAlbaran_171__102_203_;
	}

	/** 
	 * 15 - Fecha/hora número albarán (171) (102/203): El formato aceptado es CCYYMMDD o CCYYMMDDHHMM.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Fecha/hora número albarán (171) (102/203)</td> <td>C</td> <td>12</td> <td>142</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaNumeroAlbaran_171__102_203_(String fecha_horaNumeroAlbaran_171__102_203_) {
		this.fecha_horaNumeroAlbaran_171__102_203_ = fecha_horaNumeroAlbaran_171__102_203_;
	}

	/** 
	 * 16, 17, 18 - Otras referencias 1: Cuando es necesario el envío a otras referencias. El formato aceptado para el campo 19 es CCYYMMDD o CCYYMMDDHHMM. El campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 */ 
	public String getCalificadorDeReferencia1() {
		return calificadorDeReferencia1;
	}

	/** 
	 * 16, 17, 18 - Otras referencias 1: Cuando es necesario el envío a otras referencias. El formato aceptado para el campo 19 es CCYYMMDD o CCYYMMDDHHMM. El campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Calificador de referencia 1</td> <td>C</td> <td>3</td> <td>154</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDeReferencia1(String calificadorDeReferencia1) {
		this.calificadorDeReferencia1 = calificadorDeReferencia1;
	}

	/** 
	 * 16, 17, 18 - Otras referencias 1: Cuando es necesario el envío a otras referencias. El formato aceptado para el campo 19 es CCYYMMDD o CCYYMMDDHHMM. El campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 */ 
	public String getNumeroDeReferencia1() {
		return numeroDeReferencia1;
	}

	/** 
	 * 16, 17, 18 - Otras referencias 1: Cuando es necesario el envío a otras referencias. El formato aceptado para el campo 19 es CCYYMMDD o CCYYMMDDHHMM. El campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Numero de referencia 1</td> <td>C</td> <td>17</td> <td>157</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeReferencia1(String numeroDeReferencia1) {
		this.numeroDeReferencia1 = numeroDeReferencia1;
	}

	/** 
	 * 16, 17, 18 - Otras referencias 1: Cuando es necesario el envío a otras referencias. El formato aceptado para el campo 19 es CCYYMMDD o CCYYMMDDHHMM. El campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 */ 
	public String getFecha_horaReferencia1_102_203_() {
		return fecha_horaReferencia1_102_203_;
	}

	/** 
	 * 16, 17, 18 - Otras referencias 1: Cuando es necesario el envío a otras referencias. El formato aceptado para el campo 19 es CCYYMMDD o CCYYMMDDHHMM. El campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Fecha/hora referencia 1(102/203)</td> <td>C</td> <td>12</td> <td>174</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaReferencia1_102_203_(String fecha_horaReferencia1_102_203_) {
		this.fecha_horaReferencia1_102_203_ = fecha_horaReferencia1_102_203_;
	}

	/** 
	 * 191 - Fecha/hora entrega prevista
	 */ 
	public String getCalificadorDeReferencia2() {
		return calificadorDeReferencia2;
	}

	/** 
	 * 191 - Fecha/hora entrega prevista
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Calificador de Referencia 2</td> <td>C</td> <td>3</td> <td>186</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDeReferencia2(String calificadorDeReferencia2) {
		this.calificadorDeReferencia2 = calificadorDeReferencia2;
	}

	/** 
	 * 19, 20, 21 - Otra referencia 2: Como en los campos 16, 17 y 18.
	 */ 
	public String getNumeroDeReferencia2() {
		return numeroDeReferencia2;
	}

	/** 
	 * 19, 20, 21 - Otra referencia 2: Como en los campos 16, 17 y 18.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Número de referencia 2</td> <td>C</td> <td>17</td> <td>189</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeReferencia2(String numeroDeReferencia2) {
		this.numeroDeReferencia2 = numeroDeReferencia2;
	}

	/** 
	 * 19, 20, 21 - Otra referencia 2: Como en los campos 16, 17 y 18.
	 */ 
	public String getFecha_horaReferencia2_102_203_() {
		return fecha_horaReferencia2_102_203_;
	}

	/** 
	 * 19, 20, 21 - Otra referencia 2: Como en los campos 16, 17 y 18.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Fecha/hora referencia 2(102/203)</td> <td>C</td> <td>12</td> <td>206</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFecha_horaReferencia2_102_203_(String fecha_horaReferencia2_102_203_) {
		this.fecha_horaReferencia2_102_203_ = fecha_horaReferencia2_102_203_;
	}

	/** 
	 * 22 - Método de pago de costes de transporte: Identificador del método de pago del coste del transporte. Este campo se corresponde con el elemento 4215. Los valores posibles son:
	 */ 
	public String getMetodoPagoDeCostesDeTransporte() {
		return metodoPagoDeCostesDeTransporte;
	}

	/** 
	 * 22 - Método de pago de costes de transporte: Identificador del método de pago del coste del transporte. Este campo se corresponde con el elemento 4215. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Método pago de costes de transporte</td> <td>C</td> <td>3</td> <td>218</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMetodoPagoDeCostesDeTransporte(String metodoPagoDeCostesDeTransporte) {
		this.metodoPagoDeCostesDeTransporte = metodoPagoDeCostesDeTransporte;
	}

	/** 
	 * 23 - Condiciones de entrega o del transporte: Identificación de los términos acordados entre ambas partes para la entrega de un producto o servicio. Este campo se corresponde con el elemento 4053. Los valores posibles son:
	 */ 
	public String getCondicionesDeEntregaOTransporte_Codificada() {
		return condicionesDeEntregaOTransporte_Codificada;
	}

	/** 
	 * 23 - Condiciones de entrega o del transporte: Identificación de los términos acordados entre ambas partes para la entrega de un producto o servicio. Este campo se corresponde con el elemento 4053. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Condiciones de entrega o transporte, codificada</td> <td>C</td> <td>3</td> <td>221</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCondicionesDeEntregaOTransporte_Codificada(String condicionesDeEntregaOTransporte_Codificada) {
		this.condicionesDeEntregaOTransporte_Codificada = condicionesDeEntregaOTransporte_Codificada;
	}

	/** 
	 * 
	 */ 
	public String getCondicionesDeEntregaOTransporte_TextoLibre() {
		return condicionesDeEntregaOTransporte_TextoLibre;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Condiciones de entrega o transporte, texto libre</td> <td>C</td> <td>70</td> <td>224</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCondicionesDeEntregaOTransporte_TextoLibre(String condicionesDeEntregaOTransporte_TextoLibre) {
		this.condicionesDeEntregaOTransporte_TextoLibre = condicionesDeEntregaOTransporte_TextoLibre;
	}

	/** 
	 * 25 - Modo de transporte: Método de transporte codificado utilizado para el transporte de la mercancía. Este campo se corresponde con el elemento 8067. Los valores posibles:
	 */ 
	public String getModoDeTransporte_Codificado() {
		return modoDeTransporte_Codificado;
	}

	/** 
	 * 25 - Modo de transporte: Método de transporte codificado utilizado para el transporte de la mercancía. Este campo se corresponde con el elemento 8067. Los valores posibles:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Modo de transporte , codificado</td> <td>C</td> <td>3</td> <td>294</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setModoDeTransporte_Codificado(String modoDeTransporte_Codificado) {
		this.modoDeTransporte_Codificado = modoDeTransporte_Codificado;
	}

	/** 
	 * 26 - Identificación de transportista: Identificación de la empresa u organización que realiza el trasporte de la mercancía entre los puntos determinados.
	 */ 
	public String getIdentificacionDeTransportista() {
		return identificacionDeTransportista;
	}

	/** 
	 * 26 - Identificación de transportista: Identificación de la empresa u organización que realiza el trasporte de la mercancía entre los puntos determinados.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Identificación de transportista</td> <td>C</td> <td>13</td> <td>297</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setIdentificacionDeTransportista(String identificacionDeTransportista) {
		this.identificacionDeTransportista = identificacionDeTransportista;
	}

	/** 
	 * 
	 */ 
	public String getNombreDelTransportista() {
		return nombreDelTransportista;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Nombre del transportista</td> <td>C</td> <td>35</td> <td>310</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombreDelTransportista(String nombreDelTransportista) {
		this.nombreDelTransportista = nombreDelTransportista;
	}

	/** 
	 * 
	 */ 
	public String getMatriculaDelVehiculo() {
		return matriculaDelVehiculo;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Matricula del vehículo</td> <td>C</td> <td>17</td> <td>345</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMatriculaDelVehiculo(String matriculaDelVehiculo) {
		this.matriculaDelVehiculo = matriculaDelVehiculo;
	}

	/** 
	 * 
	 */ 
	public String getLugarDeEntrega_Codificado_8_() {
		return lugarDeEntrega_Codificado_8_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>29</td> <td>Lugar de entrega, codificado (8)</td> <td>C</td> <td>17</td> <td>362</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setLugarDeEntrega_Codificado_8_(String lugarDeEntrega_Codificado_8_) {
		this.lugarDeEntrega_Codificado_8_ = lugarDeEntrega_Codificado_8_;
	}

	/** 
	 * 30 - Trasporte por carretera
	 */ 
	public String getLugarDeEntrega_TextoLibre_8_() {
		return lugarDeEntrega_TextoLibre_8_;
	}

	/** 
	 * 30 - Trasporte por carretera
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>30</td> <td>Lugar de entrega, texto libre (8)</td> <td>C</td> <td>70</td> <td>379</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setLugarDeEntrega_TextoLibre_8_(String lugarDeEntrega_TextoLibre_8_) {
		this.lugarDeEntrega_TextoLibre_8_ = lugarDeEntrega_TextoLibre_8_;
	}

	/** 
	 * 2 - Tipo de documento (351/35E): Este campo corresponde al elemento 1001. Los valores posibles son:
	 */
	public enum SEH1C_2 {
		NOTAS_DE_ENVIO_351("351"),
		NOTAS_DE_DEVOLUCION_35E("35E"),
		;
		
		private String value;
		
		private SEH1C_2(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_2 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 4 - Función del mensaje: Este campo corresponde al elemento 1225. Los valores posibles son:
	 */
	public enum SEH1C_4 {
		CANCELACION___CANCELA_UN_AVISO_DE_EXPEDICION_PREVIO_1("1"),
		CAMBIO___MODIFICA_UN_AVISO_DE_EXPEDICION_PREVIO_4("4"),
		REEMPLAZAR___EL_MENSAJE_ACTUAL_CANCELA_Y_REEMPLAZA_UN_AVISO_DE_EXPEDICION_PREVIO_5("5"),
		DUPLICADO___UN_REENVIO_DEL_MENSAJE__A_SOLICITUD_DEL_RECEPTOR_7("7"),
		ORIGINAL___EL_ENVIO_DE_UN_AVISO_DE_EXPEDICION_ORIGINAL_9("9"),
		COPIA___UNA_COPIA_DEL_AVISO_DE_EXPEDICION_A_UNA_TERCERA_PARTE__31("31"),
		FECHA_HORA_DOCUMENTO__137___102_203___EL_FORMATO_ACEPTADO_ES_CCYYMMDD_O_CCYYMMDDHHMM__5("5"),
		FECHA_HORA_ENTREGA_ESTIMADA__17___102_203___FECHA_Y_O_HORA_EN_QUE_EL_EXPEDIDOR_DE_LA_MERCANCIA_ESTIMA_LA_ENTREGA__LOS_FORMATOS_ACEPTADOS_SON_CCYYMMDD_O_CCYYMMDDHHMM__6("6"),
		;
		
		private String value;
		
		private SEH1C_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 7, 8 - Fecha/Hora 1: Cuando es necesario enviar otras fechas relacionadas con todo el mensaje. El campo 7 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public enum SEH1C_7_8 {
		ENTREGA_FECHA_HORA__REQUERIDA_2("2"),
		ENVIADO_FECHA_Y_O_HORA_11("11"),
		ENTREGA_FECHA_HORA__PRIMERA_64("64"),
		ENTREGA_FECHA_HORA__ULTIMA_63("63"),
		FECHA_HORA_ENTREGA_PREVISTA_191("191"),
		;
		
		private String value;
		
		private SEH1C_7_8(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_7_8 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 9, 10 - Fecha/hora 2: cuando es necesario enviar otras fechas relacionadas con todos los mensajes. El campo 9 se corresponde con el elemento 2005. Los valores posibles son:
	 */
	public enum SEH1C_9_10 {
		ENTREGA_FECHA_HORA__REQUERIDA_2("2"),
		ENVIADO_FECHA_Y_O_HORA_11("11"),
		ENTREGA_FECHA_HORA__PRIMERA_64("64"),
		ENTREGA_FECHA_HORA__ULTIMA_63("63"),
		FECHA_HORA_ENTREGA_PREVISTA_191("191"),
		;
		
		private String value;
		
		private SEH1C_9_10(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_9_10 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 11 - Información adicional: Este campo es utilizado para indicar las condiciones especiales. Los valores posibles son:
	 */
	public enum SEH1C_11 {
		ENVIAR_PERO_NO_FACTURAR_82E("82E"),
		FECHA_HORA_NUMERO_PEDIDO__171___102_203___EL_FORMATO_ACEPTADO_ES_CCYYMMDD_O_CCYYMMDDHHMM__13("13"),
		FECHA_HORA_NUMERO_ALBARAN__171___102_203___EL_FORMATO_ACEPTADO_ES_CCYYMMDD_O_CCYYMMDDHHMM__15("15"),
		;
		
		private String value;
		
		private SEH1C_11(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_11 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 16, 17, 18 - Otras referencias 1: Cuando es necesario el envío a otras referencias. El formato aceptado para el campo 19 es CCYYMMDD o CCYYMMDDHHMM. El campo 17 se corresponde con el elemento 1153. Los valores posibles son:
	 */
	public enum SEH1C_16_17_18 {
		NUMERO_DEL_PEDIDO__PROVEEDOR__VN("VN"),
		NUMERO_DE_ENTREGA_DEL_PEDIDO__AAJ("AAJ"),
		;
		
		private String value;
		
		private SEH1C_16_17_18(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_16_17_18 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 22 - Método de pago de costes de transporte: Identificador del método de pago del coste del transporte. Este campo se corresponde con el elemento 4215. Los valores posibles son:
	 */
	public enum SEH1C_22 {
		DEFINIDO_POR_EL_COMPRADOR_Y_POR_EL_PROVEEDOR__DF("DF"),
		PREPAGO_PERO_A_CARGO_DEL_CLIENTE_PC("PC"),
		PORTES_PAGADOS_PP("PP"),
		;
		
		private String value;
		
		private SEH1C_22(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_22 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 23 - Condiciones de entrega o del transporte: Identificación de los términos acordados entre ambas partes para la entrega de un producto o servicio. Este campo se corresponde con el elemento 4053. Los valores posibles son:
	 */
	public enum SEH1C_23 {
		RECOGIDO_POR_EL_EMISOR_DEL_PEDIDO__RD("RD"),
		ENVIADO_POR_EL_RECEPTOR_DEL_PEDIDO__EP("EP"),
		;
		
		private String value;
		
		private SEH1C_23(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_23 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 25 - Modo de transporte: Método de transporte codificado utilizado para el transporte de la mercancía. Este campo se corresponde con el elemento 8067. Los valores posibles:
	 */
	public enum SEH1C_25 {
		TRANSPORTE_MARITIMO_10("10"),
		TRASPORTE_FERROVIARIO_20("20"),
		TRASPORTE_POR_CARRETERA_30("30"),
		TRANSPORTE_AEREO__40("40"),
		TRANSPORTE_MULTIPLE_60("60"),
		;
		
		private String value;
		
		private SEH1C_25(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SEH1C_25 enumByValue(String value) {
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