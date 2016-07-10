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
 * 		 <th>Tipo de registro</th> <th>Descripcion</th> <th>Tipo</th> <th>Repeticiones</th>
 * 	</tr>
 * 	<tr>
 * 		 <td>SEH1D</td> <td>Direcciones</td> <td>Opcional</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SEH1D {

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
	 */ 
	public String getTipoAvisoDeExpedicion_351_35E_() {
		return tipoAvisoDeExpedicion_351_35E_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1001T</td> <td>Tipo Aviso de Expedición (351/35E)</td> <td>C</td> <td>6</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoAvisoDeExpedicion_351_35E_(String tipoAvisoDeExpedicion_351_35E_) {
		this.tipoAvisoDeExpedicion_351_35E_ = tipoAvisoDeExpedicion_351_35E_;
	}

	/** 
	 * 
	 */ 
	public String getNumeroAvisoDeExpedicion() {
		return numeroAvisoDeExpedicion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1004P</td> <td>Número Aviso de Expedición</td> <td>C</td> <td>17</td> <td>13</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroAvisoDeExpedicion(String numeroAvisoDeExpedicion) {
		this.numeroAvisoDeExpedicion = numeroAvisoDeExpedicion;
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
	 * 		 <td>V3039E</td> <td>Código Emisor  (MS)</td> <td>C</td> <td>17</td> <td>30</td> <td>M</td>
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
	 * 		 <td>V3039R</td> <td>Código Receptor (MR)</td> <td>C</td> <td>17</td> <td>47</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoReceptor_MR_(String codigoReceptor_MR_) {
		this.codigoReceptor_MR_ = codigoReceptor_MR_;
	}

	/** 
	 * V3035D-Calificador Interlocutor: Los valores posibles son:
	 */ 
	public String getCalificadorInterlocutor() {
		return calificadorInterlocutor;
	}

	/** 
	 * V3035D-Calificador Interlocutor: Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3035D</td> <td>Calificador Interlocutor</td> <td>C</td> <td>3</td> <td>64</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorInterlocutor(String calificadorInterlocutor) {
		this.calificadorInterlocutor = calificadorInterlocutor;
	}

	/** 
	 * 
	 */ 
	public String getCodigoInterlocutor() {
		return codigoInterlocutor;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3039D</td> <td>Código Interlocutor</td> <td>C</td> <td>17</td> <td>67</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInterlocutor(String codigoInterlocutor) {
		this.codigoInterlocutor = codigoInterlocutor;
	}

	/** 
	 * V3055C-Agencia Responsable lista de Códigos: Los valores posibles son:
	 */ 
	public String getAgenciaResponsableListaDeCodigos() {
		return agenciaResponsableListaDeCodigos;
	}

	/** 
	 * V3055C-Agencia Responsable lista de Códigos: Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3055C</td> <td>Agencia Responsable lista de Códigos</td> <td>C</td> <td>3</td> <td>84</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setAgenciaResponsableListaDeCodigos(String agenciaResponsableListaDeCodigos) {
		this.agenciaResponsableListaDeCodigos = agenciaResponsableListaDeCodigos;
	}

	/** 
	 * 
	 */ 
	public String getNombre1() {
		return nombre1;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30361</td> <td>Nombre 1</td> <td>C</td> <td>35</td> <td>87</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombre1(String nombre1) {
		this.nombre1 = nombre1;
	}

	/** 
	 * 
	 */ 
	public String getNombre2() {
		return nombre2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30362</td> <td>Nombre 2</td> <td>C</td> <td>35</td> <td>122</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombre2(String nombre2) {
		this.nombre2 = nombre2;
	}

	/** 
	 * 
	 */ 
	public String getNombre3() {
		return nombre3;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30363</td> <td>Nombre 3</td> <td>C</td> <td>35</td> <td>157</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombre3(String nombre3) {
		this.nombre3 = nombre3;
	}

	/** 
	 * 
	 */ 
	public String getNombre4() {
		return nombre4;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30364</td> <td>Nombre 4</td> <td>C</td> <td>35</td> <td>192</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombre4(String nombre4) {
		this.nombre4 = nombre4;
	}

	/** 
	 * 
	 */ 
	public String getNombre5() {
		return nombre5;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30365</td> <td>Nombre 5</td> <td>C</td> <td>35</td> <td>227</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombre5(String nombre5) {
		this.nombre5 = nombre5;
	}

	/** 
	 * 
	 */ 
	public String getDireccion1_Calle_Numero_() {
		return direccion1_Calle_Numero_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30421</td> <td>Dirección 1  (Calle+Número)</td> <td>C</td> <td>35</td> <td>262</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDireccion1_Calle_Numero_(String direccion1_Calle_Numero_) {
		this.direccion1_Calle_Numero_ = direccion1_Calle_Numero_;
	}

	/** 
	 * 
	 */ 
	public String getDireccion2_Calle_Numero_() {
		return direccion2_Calle_Numero_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30422</td> <td>Dirección 2  (Calle+Número)</td> <td>C</td> <td>35</td> <td>297</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDireccion2_Calle_Numero_(String direccion2_Calle_Numero_) {
		this.direccion2_Calle_Numero_ = direccion2_Calle_Numero_;
	}

	/** 
	 * 
	 */ 
	public String getDireccion3_Calle_Numero_() {
		return direccion3_Calle_Numero_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30423</td> <td>Dirección 3  (Calle+Número)</td> <td>C</td> <td>35</td> <td>332</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDireccion3_Calle_Numero_(String direccion3_Calle_Numero_) {
		this.direccion3_Calle_Numero_ = direccion3_Calle_Numero_;
	}

	/** 
	 * 
	 */ 
	public String getDireccion4_Calle_Numero_() {
		return direccion4_Calle_Numero_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V30424</td> <td>Dirección 4  (Calle+Número)</td> <td>C</td> <td>35</td> <td>367</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDireccion4_Calle_Numero_(String direccion4_Calle_Numero_) {
		this.direccion4_Calle_Numero_ = direccion4_Calle_Numero_;
	}

	/** 
	 * 
	 */ 
	public String getPoblacion() {
		return poblacion;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3164P</td> <td>Población</td> <td>C</td> <td>35</td> <td>402</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}

	/** 
	 * 
	 */ 
	public String getProvincia() {
		return provincia;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3229P</td> <td>Provincia</td> <td>C</td> <td>9</td> <td>437</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	/** 
	 * 
	 */ 
	public String getCodigoPostal() {
		return codigoPostal;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3251C</td> <td>Código Postal</td> <td>C</td> <td>9</td> <td>446</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	/** 
	 * 
	 */ 
	public String getCodigoPais() {
		return codigoPais;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3207P</td> <td>Código País</td> <td>C</td> <td>3</td> <td>455</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoPais(String codigoPais) {
		this.codigoPais = codigoPais;
	}

	/** 
	 * V1153D-Calificador Referencia: Los valores posibles son:
	 */ 
	public String getCalificadorReferencia() {
		return calificadorReferencia;
	}

	/** 
	 * V1153D-Calificador Referencia: Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1153D</td> <td>Calificador Referencia</td> <td>C</td> <td>3</td> <td>458</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorReferencia(String calificadorReferencia) {
		this.calificadorReferencia = calificadorReferencia;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeReferencia() {
		return numeroDeReferencia;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1154D</td> <td>Número de Referencia</td> <td>C</td> <td>35</td> <td>461</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeReferencia(String numeroDeReferencia) {
		this.numeroDeReferencia = numeroDeReferencia;
	}

	/** 
	 * V3139D-Función de Contacto: Utilizado para identificar los nombres de contacto especificados por la compañía. Los valores posibles son:
	 */ 
	public String getFuncionDeContacto() {
		return funcionDeContacto;
	}

	/** 
	 * V3139D-Función de Contacto: Utilizado para identificar los nombres de contacto especificados por la compañía. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3139D</td> <td>Función de Contacto</td> <td>C</td> <td>3</td> <td>496</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFuncionDeContacto(String funcionDeContacto) {
		this.funcionDeContacto = funcionDeContacto;
	}

	/** 
	 * V3413D-Código Departamento o Empleado: Punto Operacional o Código interno que identifica a la persona de contacto.
	 */ 
	public String getCodigoDepartamentoOEmpleado() {
		return codigoDepartamentoOEmpleado;
	}

	/** 
	 * V3413D-Código Departamento o Empleado: Punto Operacional o Código interno que identifica a la persona de contacto.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3413D</td> <td>Código Departamento o Empleado</td> <td>C</td> <td>17</td> <td>499</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoDepartamentoOEmpleado(String codigoDepartamentoOEmpleado) {
		this.codigoDepartamentoOEmpleado = codigoDepartamentoOEmpleado;
	}

	/** 
	 * 
	 */ 
	public String getNombreDepartamentoOEmpleado() {
		return nombreDepartamentoOEmpleado;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V3412D</td> <td>Nombre Departamento o Empleado</td> <td>C</td> <td>35</td> <td>516</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombreDepartamentoOEmpleado(String nombreDepartamentoOEmpleado) {
		this.nombreDepartamentoOEmpleado = nombreDepartamentoOEmpleado;
	}

	/** 
	 * 
	 */ 
	public String getCalificadorReferencia2() {
		return calificadorReferencia2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1153E</td> <td>Calificador Referencia 2</td> <td>C</td> <td>3</td> <td>551</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorReferencia2(String calificadorReferencia2) {
		this.calificadorReferencia2 = calificadorReferencia2;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeReferencia2() {
		return numeroDeReferencia2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>V1154E</td> <td>Número de Referencia 2</td> <td>C</td> <td>17</td> <td>554</td> <td>C</td>
	 * 	</tr>
	 * </table>
	 */ 
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