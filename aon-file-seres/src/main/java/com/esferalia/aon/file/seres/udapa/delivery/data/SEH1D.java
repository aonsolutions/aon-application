package com.esferalia.aon.file.seres.udapa.delivery.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SEH1D entity.
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
 * 		<td>SEH1D</th>
 * 		<td>Direcciones</th>
 * 		<td>Opcional</th>
 * 		<td>N</th>
 * 	</tr>
 * </table>
 */ 

public class SEH1D {

	private String direcciones;
	private String tipoAvisoDeExpedicion_351_35E_;
	private String numeroAvisoDeExpedicion;
	private String codigoEmisor_MS_;
	private String codigoReceptor_MR_;
	private String calificadorInterlocutor;
	private String codigoInterlocutor;
	private String agenciaResponsableListaDeCodigos;
	private String nombre1;
	private String nombre2;
	private String nombre3;
	private String nombre4;
	private String nombre5;
	private String direccion1_Calle_Numero_;
	private String direccion2_Calle_Numero_;
	private String direccion3_Calle_Numero_;
	private String direccion4_Calle_Numero_;
	private String poblacion;
	private String provincia;
	private String codigoPostal;
	private String codigoPais;
	private String calificadorReferencia;
	private String numeroDeReferencia;
	private String funcionDeContacto;
	private String codigoDepartamentoOEmpleado;
	private String nombreDepartamentoOEmpleado;
	private String calificadorReferencia2;
	private String numeroDeReferencia2;


	private static Pattern PATTERN_SEH1D_direcciones = Pattern.compile("^(.{6}).*");
	private static Pattern PATTERN_SEH1D_tipoAvisoDeExpedicion_351_35E_ = Pattern.compile("^.{6}(.{6}).*");
	private static Pattern PATTERN_SEH1D_numeroAvisoDeExpedicion = Pattern.compile("^.{12}(.{17}).*");
	private static Pattern PATTERN_SEH1D_codigoEmisor_MS_ = Pattern.compile("^.{29}(.{17}).*");
	private static Pattern PATTERN_SEH1D_codigoReceptor_MR_ = Pattern.compile("^.{46}(.{17}).*");
	private static Pattern PATTERN_SEH1D_calificadorInterlocutor = Pattern.compile("^.{63}(.{3}).*");
	private static Pattern PATTERN_SEH1D_codigoInterlocutor = Pattern.compile("^.{66}(.{17}).*");
	private static Pattern PATTERN_SEH1D_agenciaResponsableListaDeCodigos = Pattern.compile("^.{83}(.{3}).*");
	private static Pattern PATTERN_SEH1D_nombre1 = Pattern.compile("^.{86}(.{35}).*");
	private static Pattern PATTERN_SEH1D_nombre2 = Pattern.compile("^.{121}(.{35}).*");
	private static Pattern PATTERN_SEH1D_nombre3 = Pattern.compile("^.{156}(.{35}).*");
	private static Pattern PATTERN_SEH1D_nombre4 = Pattern.compile("^.{191}(.{35}).*");
	private static Pattern PATTERN_SEH1D_nombre5 = Pattern.compile("^.{226}(.{35}).*");
	private static Pattern PATTERN_SEH1D_direccion1_Calle_Numero_ = Pattern.compile("^.{261}(.{35}).*");
	private static Pattern PATTERN_SEH1D_direccion2_Calle_Numero_ = Pattern.compile("^.{296}(.{35}).*");
	private static Pattern PATTERN_SEH1D_direccion3_Calle_Numero_ = Pattern.compile("^.{331}(.{35}).*");
	private static Pattern PATTERN_SEH1D_direccion4_Calle_Numero_ = Pattern.compile("^.{366}(.{35}).*");
	private static Pattern PATTERN_SEH1D_poblacion = Pattern.compile("^.{401}(.{35}).*");
	private static Pattern PATTERN_SEH1D_provincia = Pattern.compile("^.{436}(.{9}).*");
	private static Pattern PATTERN_SEH1D_codigoPostal = Pattern.compile("^.{445}(.{9}).*");
	private static Pattern PATTERN_SEH1D_codigoPais = Pattern.compile("^.{454}(.{3}).*");
	private static Pattern PATTERN_SEH1D_calificadorReferencia = Pattern.compile("^.{457}(.{3}).*");
	private static Pattern PATTERN_SEH1D_numeroDeReferencia = Pattern.compile("^.{460}(.{35}).*");
	private static Pattern PATTERN_SEH1D_funcionDeContacto = Pattern.compile("^.{495}(.{3}).*");
	private static Pattern PATTERN_SEH1D_codigoDepartamentoOEmpleado = Pattern.compile("^.{498}(.{17}).*");
	private static Pattern PATTERN_SEH1D_nombreDepartamentoOEmpleado = Pattern.compile("^.{515}(.{35}).*");
	private static Pattern PATTERN_SEH1D_calificadorReferencia2 = Pattern.compile("^.{550}(.{3}).*");
	private static Pattern PATTERN_SEH1D_numeroDeReferencia2 = Pattern.compile("^.{553}(.{17}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SEH1D_direcciones.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDirecciones(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_tipoAvisoDeExpedicion_351_35E_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoAvisoDeExpedicion_351_35E_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_numeroAvisoDeExpedicion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroAvisoDeExpedicion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_codigoEmisor_MS_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoEmisor_MS_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_codigoReceptor_MR_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoReceptor_MR_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_calificadorInterlocutor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorInterlocutor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_codigoInterlocutor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInterlocutor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_agenciaResponsableListaDeCodigos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAgenciaResponsableListaDeCodigos(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_nombre1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_nombre2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_nombre3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_nombre4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_nombre5.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre5(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_direccion1_Calle_Numero_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccion1_Calle_Numero_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_direccion2_Calle_Numero_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccion2_Calle_Numero_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_direccion3_Calle_Numero_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccion3_Calle_Numero_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_direccion4_Calle_Numero_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccion4_Calle_Numero_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_poblacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPoblacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_provincia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setProvincia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_codigoPostal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPostal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_codigoPais.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPais(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_calificadorReferencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_numeroDeReferencia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeReferencia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_funcionDeContacto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDeContacto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_codigoDepartamentoOEmpleado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDepartamentoOEmpleado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_nombreDepartamentoOEmpleado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombreDepartamentoOEmpleado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_calificadorReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SEH1D_numeroDeReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeReferencia2(String.valueOf(m.group(1).trim()));
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
	 * 		<td>SEH1D</th>
	 * 		<td>Direcciones</th>
	 * 		<td>C</th>
	 * 		<td>6</th>
	 * 		<td>1</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDirecciones() {
		return direcciones;
	}
	public void setDirecciones(String direcciones) {
		this.direcciones = direcciones;
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
	 * V3035D-Calificador Interlocutor: Los valores posibles son:
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
	 * 		<td>V3035D</th>
	 * 		<td>Calificador Interlocutor</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>64</th>
	 * 		<td>M</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorInterlocutor() {
		return calificadorInterlocutor;
	}
	public void setCalificadorInterlocutor(String calificadorInterlocutor) {
		this.calificadorInterlocutor = calificadorInterlocutor;
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
	 * 		<td>V3039D</th>
	 * 		<td>Código Interlocutor</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>67</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoInterlocutor() {
		return codigoInterlocutor;
	}
	public void setCodigoInterlocutor(String codigoInterlocutor) {
		this.codigoInterlocutor = codigoInterlocutor;
	}

	/** 
	 * V3055C-Agencia Responsable lista de Códigos: Los valores posibles son:
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
	 * 		<td>V3055C</th>
	 * 		<td>Agencia Responsable lista de Códigos</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>84</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getAgenciaResponsableListaDeCodigos() {
		return agenciaResponsableListaDeCodigos;
	}
	public void setAgenciaResponsableListaDeCodigos(String agenciaResponsableListaDeCodigos) {
		this.agenciaResponsableListaDeCodigos = agenciaResponsableListaDeCodigos;
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
	 * 		<td>V30361</th>
	 * 		<td>Nombre 1</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>87</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombre1() {
		return nombre1;
	}
	public void setNombre1(String nombre1) {
		this.nombre1 = nombre1;
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
	 * 		<td>V30362</th>
	 * 		<td>Nombre 2</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>122</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombre2() {
		return nombre2;
	}
	public void setNombre2(String nombre2) {
		this.nombre2 = nombre2;
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
	 * 		<td>V30363</th>
	 * 		<td>Nombre 3</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>157</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombre3() {
		return nombre3;
	}
	public void setNombre3(String nombre3) {
		this.nombre3 = nombre3;
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
	 * 		<td>V30364</th>
	 * 		<td>Nombre 4</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>192</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombre4() {
		return nombre4;
	}
	public void setNombre4(String nombre4) {
		this.nombre4 = nombre4;
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
	 * 		<td>V30365</th>
	 * 		<td>Nombre 5</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>227</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombre5() {
		return nombre5;
	}
	public void setNombre5(String nombre5) {
		this.nombre5 = nombre5;
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
	 * 		<td>V30421</th>
	 * 		<td>Dirección 1  (Calle+Número)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>262</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDireccion1_Calle_Numero_() {
		return direccion1_Calle_Numero_;
	}
	public void setDireccion1_Calle_Numero_(String direccion1_Calle_Numero_) {
		this.direccion1_Calle_Numero_ = direccion1_Calle_Numero_;
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
	 * 		<td>V30422</th>
	 * 		<td>Dirección 2  (Calle+Número)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>297</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDireccion2_Calle_Numero_() {
		return direccion2_Calle_Numero_;
	}
	public void setDireccion2_Calle_Numero_(String direccion2_Calle_Numero_) {
		this.direccion2_Calle_Numero_ = direccion2_Calle_Numero_;
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
	 * 		<td>V30423</th>
	 * 		<td>Dirección 3  (Calle+Número)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>332</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDireccion3_Calle_Numero_() {
		return direccion3_Calle_Numero_;
	}
	public void setDireccion3_Calle_Numero_(String direccion3_Calle_Numero_) {
		this.direccion3_Calle_Numero_ = direccion3_Calle_Numero_;
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
	 * 		<td>V30424</th>
	 * 		<td>Dirección 4  (Calle+Número)</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>367</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getDireccion4_Calle_Numero_() {
		return direccion4_Calle_Numero_;
	}
	public void setDireccion4_Calle_Numero_(String direccion4_Calle_Numero_) {
		this.direccion4_Calle_Numero_ = direccion4_Calle_Numero_;
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
	 * 		<td>V3164P</th>
	 * 		<td>Población</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>402</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getPoblacion() {
		return poblacion;
	}
	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
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
	 * 		<td>V3229P</th>
	 * 		<td>Provincia</th>
	 * 		<td>C</th>
	 * 		<td>9</th>
	 * 		<td>437</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
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
	 * 		<td>V3251C</th>
	 * 		<td>Código Postal</th>
	 * 		<td>C</th>
	 * 		<td>9</th>
	 * 		<td>446</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
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
	 * 		<td>V3207P</th>
	 * 		<td>Código País</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>455</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoPais() {
		return codigoPais;
	}
	public void setCodigoPais(String codigoPais) {
		this.codigoPais = codigoPais;
	}

	/** 
	 * V1153D-Calificador Referencia: Los valores posibles son:
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
	 * 		<td>V1153D</th>
	 * 		<td>Calificador Referencia</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>458</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorReferencia() {
		return calificadorReferencia;
	}
	public void setCalificadorReferencia(String calificadorReferencia) {
		this.calificadorReferencia = calificadorReferencia;
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
	 * 		<td>V1154D</th>
	 * 		<td>Número de Referencia</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>461</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeReferencia() {
		return numeroDeReferencia;
	}
	public void setNumeroDeReferencia(String numeroDeReferencia) {
		this.numeroDeReferencia = numeroDeReferencia;
	}

	/** 
	 * V3139D-Función de Contacto: Utilizado para identificar los nombres de contacto especificados por la compañía. Los valores posibles son:
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
	 * 		<td>V3139D</th>
	 * 		<td>Función de Contacto</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>496</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getFuncionDeContacto() {
		return funcionDeContacto;
	}
	public void setFuncionDeContacto(String funcionDeContacto) {
		this.funcionDeContacto = funcionDeContacto;
	}

	/** 
	 * V3413D-Código Departamento o Empleado: Punto Operacional o Código interno que identifica a la persona de contacto.
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
	 * 		<td>V3413D</th>
	 * 		<td>Código Departamento o Empleado</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>499</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCodigoDepartamentoOEmpleado() {
		return codigoDepartamentoOEmpleado;
	}
	public void setCodigoDepartamentoOEmpleado(String codigoDepartamentoOEmpleado) {
		this.codigoDepartamentoOEmpleado = codigoDepartamentoOEmpleado;
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
	 * 		<td>V3412D</th>
	 * 		<td>Nombre Departamento o Empleado</th>
	 * 		<td>C</th>
	 * 		<td>35</th>
	 * 		<td>516</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNombreDepartamentoOEmpleado() {
		return nombreDepartamentoOEmpleado;
	}
	public void setNombreDepartamentoOEmpleado(String nombreDepartamentoOEmpleado) {
		this.nombreDepartamentoOEmpleado = nombreDepartamentoOEmpleado;
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
	 * 		<td>V1153E</th>
	 * 		<td>Calificador Referencia 2</th>
	 * 		<td>C</th>
	 * 		<td>3</th>
	 * 		<td>551</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getCalificadorReferencia2() {
		return calificadorReferencia2;
	}
	public void setCalificadorReferencia2(String calificadorReferencia2) {
		this.calificadorReferencia2 = calificadorReferencia2;
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
	 * 		<td>V1154E</th>
	 * 		<td>Número de Referencia 2</th>
	 * 		<td>C</th>
	 * 		<td>17</th>
	 * 		<td>554</th>
	 * 		<td>C</th>
	 * 	</tr>
	 * </table>
	 */ 
	public String getNumeroDeReferencia2() {
		return numeroDeReferencia2;
	}
	public void setNumeroDeReferencia2(String numeroDeReferencia2) {
		this.numeroDeReferencia2 = numeroDeReferencia2;
	}

	/** 
	 * V3035D-Calificador Interlocutor: Los valores posibles son:
	 */
	public enum V3035D {
		ORIGEN_DEL_MENSAJE___FUNCION_PROV_MS("MS"),
		RECEPTOR_DEL_MENSAJE__FUNCION_PRES_MR("MR"),
		PROVEEDOR_DE_LAS_MERCANCIAS__FUNCION_PROV_SU("SU"),
		PUNTO_DESDE_DONDE_SE_ENVIAN_LAS_MERCANCIAS__FUNCION_QENT_PW("PW"),
		RECEPTOR_DE_LA_ENTREGA__DONDE_SE_ENTREGAN_LAS_MERCANCIAS___FUNCION_QREC_DP("DP"),
		ULTIMO_CONSIGNATARIO__DESTINO_FINAL_DE_LAS_MERCANCIAS___FUNCION_QREC_UC("UC"),
		COMPRADOR__QUIEN_PIDE___FUNCION_QPID_BY("BY"),
		;
		
		private String value;
		
		private V3035D(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V3035D enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V3055C-Agencia Responsable lista de Códigos: Los valores posibles son:
	 */
	public enum V3055C {
		EA_9("9"),
		;
		
		private String value;
		
		private V3055C(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V3055C enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V1153D-Calificador Referencia: Los valores posibles son:
	 */
	public enum V1153D {
		N__DE_IDENTIFICACION_FISCAL__NIF__VA("VA"),
		;
		
		private String value;
		
		private V1153D(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V1153D enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * V3139D-Función de Contacto: Utilizado para identificar los nombres de contacto especificados por la compañía. Los valores posibles son:
	 */
	public enum V3139D {
		CONTACTO_PARA_LA_ENTREGA_DL("DL"),
		CONTACTO_PARA_TRANSPORTE_TR("TR"),
		;
		
		private String value;
		
		private V3139D(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static V3139D enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
}