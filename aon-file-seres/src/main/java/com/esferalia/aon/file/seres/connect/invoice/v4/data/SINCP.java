package com.esferalia.aon.file.seres.connect.invoice.v4.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI SINCP entity.
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
 * 		 <td>SINCP</td> <td>Información partes involucradas</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class SINCP {

	private String calificadorDelInterlocutor;
	private String codigoInterlocutor;
	private String tipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_;
	private String nombre1;
	private String nombre2;
	private String nombre3;
	private String nombre4;
	private String nombre5;
	private String direccion1_Calle_Numero_;
	private String direccion2_Calle_Numero_;
	private String direccion3_Calle_Numero_;
	private String direccion4_Calle_Numero_;
	private String ciudad;
	private String provincia;
	private String codigoPostal;
	private String codigoPais;
	private String numeroDeIdentificacionFiscal;
	private String codigoAdicional;
	private String funcionDeContacto;
	private String codigoDepartamentoOEmpleado;
	private String nombreDepartamentoOEmpleado;
	private String telefono;
	private String fax;
	private String numeroDeCuentaBancaria_IBAN_;
	private String registroMercantilDelEmisor;
	private String capitalSocial;
	private String calificadorReferenciaAdicional;
	private String referenciaAdicional;


	private static Pattern PATTERN_SINCP_calificadorDelInterlocutor = Pattern.compile("^.{6}(.{3}).*");
	private static Pattern PATTERN_SINCP_codigoInterlocutor = Pattern.compile("^.{9}(.{17}).*");
	private static Pattern PATTERN_SINCP_tipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_ = Pattern.compile("^.{26}(.{3}).*");
	private static Pattern PATTERN_SINCP_nombre1 = Pattern.compile("^.{29}(.{35}).*");
	private static Pattern PATTERN_SINCP_nombre2 = Pattern.compile("^.{64}(.{35}).*");
	private static Pattern PATTERN_SINCP_nombre3 = Pattern.compile("^.{99}(.{35}).*");
	private static Pattern PATTERN_SINCP_nombre4 = Pattern.compile("^.{134}(.{35}).*");
	private static Pattern PATTERN_SINCP_nombre5 = Pattern.compile("^.{169}(.{35}).*");
	private static Pattern PATTERN_SINCP_direccion1_Calle_Numero_ = Pattern.compile("^.{204}(.{35}).*");
	private static Pattern PATTERN_SINCP_direccion2_Calle_Numero_ = Pattern.compile("^.{239}(.{35}).*");
	private static Pattern PATTERN_SINCP_direccion3_Calle_Numero_ = Pattern.compile("^.{274}(.{35}).*");
	private static Pattern PATTERN_SINCP_direccion4_Calle_Numero_ = Pattern.compile("^.{309}(.{35}).*");
	private static Pattern PATTERN_SINCP_ciudad = Pattern.compile("^.{344}(.{35}).*");
	private static Pattern PATTERN_SINCP_provincia = Pattern.compile("^.{379}(.{9}).*");
	private static Pattern PATTERN_SINCP_codigoPostal = Pattern.compile("^.{388}(.{9}).*");
	private static Pattern PATTERN_SINCP_codigoPais = Pattern.compile("^.{397}(.{3}).*");
	private static Pattern PATTERN_SINCP_numeroDeIdentificacionFiscal = Pattern.compile("^.{400}(.{35}).*");
	private static Pattern PATTERN_SINCP_codigoAdicional = Pattern.compile("^.{435}(.{35}).*");
	private static Pattern PATTERN_SINCP_funcionDeContacto = Pattern.compile("^.{470}(.{3}).*");
	private static Pattern PATTERN_SINCP_codigoDepartamentoOEmpleado = Pattern.compile("^.{473}(.{17}).*");
	private static Pattern PATTERN_SINCP_nombreDepartamentoOEmpleado = Pattern.compile("^.{490}(.{35}).*");
	private static Pattern PATTERN_SINCP_telefono = Pattern.compile("^.{525}(.{35}).*");
	private static Pattern PATTERN_SINCP_fax = Pattern.compile("^.{560}(.{35}).*");
	private static Pattern PATTERN_SINCP_numeroDeCuentaBancaria_IBAN_ = Pattern.compile("^.{595}(.{35}).*");
	private static Pattern PATTERN_SINCP_registroMercantilDelEmisor = Pattern.compile("^.{630}(.{70}).*");
	private static Pattern PATTERN_SINCP_capitalSocial = Pattern.compile("^.{700}(.{35}).*");
	private static Pattern PATTERN_SINCP_calificadorReferenciaAdicional = Pattern.compile("^.{735}(.{3}).*");
	private static Pattern PATTERN_SINCP_referenciaAdicional = Pattern.compile("^.{738}(.{35}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_SINCP_calificadorDelInterlocutor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDelInterlocutor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_codigoInterlocutor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInterlocutor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_tipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_nombre1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_nombre2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_nombre3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_nombre4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_nombre5.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre5(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_direccion1_Calle_Numero_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccion1_Calle_Numero_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_direccion2_Calle_Numero_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccion2_Calle_Numero_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_direccion3_Calle_Numero_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccion3_Calle_Numero_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_direccion4_Calle_Numero_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDireccion4_Calle_Numero_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_ciudad.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCiudad(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_provincia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setProvincia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_codigoPostal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPostal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_codigoPais.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPais(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_numeroDeIdentificacionFiscal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeIdentificacionFiscal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_codigoAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_funcionDeContacto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDeContacto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_codigoDepartamentoOEmpleado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoDepartamentoOEmpleado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_nombreDepartamentoOEmpleado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombreDepartamentoOEmpleado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_telefono.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTelefono(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_fax.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFax(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_numeroDeCuentaBancaria_IBAN_.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeCuentaBancaria_IBAN_(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_registroMercantilDelEmisor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setRegistroMercantilDelEmisor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_capitalSocial.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCapitalSocial(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_calificadorReferenciaAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferenciaAdicional(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_SINCP_referenciaAdicional.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setReferenciaAdicional(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Calificador del Interlocutor: Código que identifica cada tipo de interlocutor. Este campo se corresponde con el elemento 3035. Los valores posibles son:
	 */ 
	public String getCalificadorDelInterlocutor() {
		return calificadorDelInterlocutor;
	}

	/** 
	 * 2 - Calificador del Interlocutor: Código que identifica cada tipo de interlocutor. Este campo se corresponde con el elemento 3035. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Calificador del Interlocutor</td> <td>C</td> <td>3</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorDelInterlocutor(String calificadorDelInterlocutor) {
		this.calificadorDelInterlocutor = calificadorDelInterlocutor;
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
	 * 		 <td>3</td> <td>Código Interlocutor</td> <td>C</td> <td>17</td> <td>10</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoInterlocutor(String codigoInterlocutor) {
		this.codigoInterlocutor = codigoInterlocutor;
	}

	/** 
	 * 41 - Tipo Interlocutor: Código que identifica el tipo de código del interlocutor. Este campo se corresponde con el elemento 3055. Los valores posibles son:
	 */ 
	public String getTipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_() {
		return tipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_;
	}

	/** 
	 * 41 - Tipo Interlocutor: Código que identifica el tipo de código del interlocutor. Este campo se corresponde con el elemento 3055. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Tipo Interlocutor (J- Persa. Jurídica/ F - Persa. Física/9 -EDI)</td> <td>C</td> <td>3</td> <td>27</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_(String tipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_) {
		this.tipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_ = tipoInterlocutor_J_Persa_Juridica_F_Persa_Fisica_9_EDI_;
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
	 * 		 <td>5</td> <td>Nombre 1</td> <td>C</td> <td>35</td> <td>30</td> <td>O</td>
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
	 * 		 <td>6</td> <td>Nombre 2</td> <td>C</td> <td>35</td> <td>65</td> <td>O</td>
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
	 * 		 <td>7</td> <td>Nombre 3</td> <td>C</td> <td>35</td> <td>100</td> <td>O</td>
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
	 * 		 <td>8</td> <td>Nombre 4</td> <td>C</td> <td>35</td> <td>135</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombre4(String nombre4) {
		this.nombre4 = nombre4;
	}

	/** 
	 * 9 - Código EAN
	 */ 
	public String getNombre5() {
		return nombre5;
	}

	/** 
	 * 9 - Código EAN
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Nombre 5</td> <td>C</td> <td>35</td> <td>170</td> <td>O</td>
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
	 * 		 <td>10</td> <td>Dirección 1 (Calle + Número)</td> <td>C</td> <td>35</td> <td>205</td> <td>O</td>
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
	 * 		 <td>11</td> <td>Dirección 2 (Calle + Número)</td> <td>C</td> <td>35</td> <td>240</td> <td>O</td>
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
	 * 		 <td>12</td> <td>Dirección 3 (Calle + Número)</td> <td>C</td> <td>35</td> <td>275</td> <td>O</td>
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
	 * 		 <td>13</td> <td>Dirección 4 (Calle + Número)</td> <td>C</td> <td>35</td> <td>310</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setDireccion4_Calle_Numero_(String direccion4_Calle_Numero_) {
		this.direccion4_Calle_Numero_ = direccion4_Calle_Numero_;
	}

	/** 
	 * 
	 */ 
	public String getCiudad() {
		return ciudad;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Ciudad</td> <td>C</td> <td>35</td> <td>345</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
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
	 * 		 <td>15</td> <td>Provincia</td> <td>C</td> <td>9</td> <td>380</td> <td>O</td>
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
	 * 		 <td>16</td> <td>Código Postal</td> <td>C</td> <td>9</td> <td>389</td> <td>O</td>
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
	 * 		 <td>17</td> <td>Código País</td> <td>C</td> <td>3</td> <td>398</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoPais(String codigoPais) {
		this.codigoPais = codigoPais;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeIdentificacionFiscal() {
		return numeroDeIdentificacionFiscal;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Número de Identificación Fiscal</td> <td>C</td> <td>35</td> <td>401</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeIdentificacionFiscal(String numeroDeIdentificacionFiscal) {
		this.numeroDeIdentificacionFiscal = numeroDeIdentificacionFiscal;
	}

	/** 
	 * 
	 */ 
	public String getCodigoAdicional() {
		return codigoAdicional;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>19</td> <td>Código Adicional</td> <td>C</td> <td>35</td> <td>436</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCodigoAdicional(String codigoAdicional) {
		this.codigoAdicional = codigoAdicional;
	}

	/** 
	 * 
	 */ 
	public String getFuncionDeContacto() {
		return funcionDeContacto;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Función de contacto</td> <td>C</td> <td>3</td> <td>471</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFuncionDeContacto(String funcionDeContacto) {
		this.funcionDeContacto = funcionDeContacto;
	}

	/** 
	 * 
	 */ 
	public String getCodigoDepartamentoOEmpleado() {
		return codigoDepartamentoOEmpleado;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Código departamento o empleado</td> <td>C</td> <td>17</td> <td>474</td> <td>O</td>
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
	 * 		 <td>22</td> <td>Nombre departamento o empleado</td> <td>C</td> <td>35</td> <td>491</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNombreDepartamentoOEmpleado(String nombreDepartamentoOEmpleado) {
		this.nombreDepartamentoOEmpleado = nombreDepartamentoOEmpleado;
	}

	/** 
	 * 
	 */ 
	public String getTelefono() {
		return telefono;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Teléfono</td> <td>C</td> <td>35</td> <td>526</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/** 
	 * 
	 */ 
	public String getFax() {
		return fax;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Fax</td> <td>C</td> <td>35</td> <td>561</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setFax(String fax) {
		this.fax = fax;
	}

	/** 
	 * 
	 */ 
	public String getNumeroDeCuentaBancaria_IBAN_() {
		return numeroDeCuentaBancaria_IBAN_;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>25</td> <td>Número de cuenta bancaria (IBAN)</td> <td>C</td> <td>35</td> <td>596</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeCuentaBancaria_IBAN_(String numeroDeCuentaBancaria_IBAN_) {
		this.numeroDeCuentaBancaria_IBAN_ = numeroDeCuentaBancaria_IBAN_;
	}

	/** 
	 * 
	 */ 
	public String getRegistroMercantilDelEmisor() {
		return registroMercantilDelEmisor;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>26</td> <td>Registro Mercantil del emisor</td> <td>C</td> <td>70</td> <td>631</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setRegistroMercantilDelEmisor(String registroMercantilDelEmisor) {
		this.registroMercantilDelEmisor = registroMercantilDelEmisor;
	}

	/** 
	 * 
	 */ 
	public String getCapitalSocial() {
		return capitalSocial;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>27</td> <td>Capital Social</td> <td>C</td> <td>35</td> <td>701</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCapitalSocial(String capitalSocial) {
		this.capitalSocial = capitalSocial;
	}

	/** 
	 * 
	 */ 
	public String getCalificadorReferenciaAdicional() {
		return calificadorReferenciaAdicional;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>28</td> <td>Calificador referencia adicional</td> <td>C</td> <td>3</td> <td>736</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setCalificadorReferenciaAdicional(String calificadorReferenciaAdicional) {
		this.calificadorReferenciaAdicional = calificadorReferenciaAdicional;
	}

	/** 
	 * 
	 */ 
	public String getReferenciaAdicional() {
		return referenciaAdicional;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>29</td> <td>Referencia adicional</td> <td>C</td> <td>35</td> <td>739</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setReferenciaAdicional(String referenciaAdicional) {
		this.referenciaAdicional = referenciaAdicional;
	}

	/** 
	 * 2 - Calificador del Interlocutor: Código que identifica cada tipo de interlocutor. Este campo se corresponde con el elemento 3035. Los valores posibles son:
	 */
	public enum SINCP_2 {
		PROVEEDOR__SU("SU"),
		EMISOR_DE_UNA_FACTURA__QUIEN_FACTURA__II("II"),
		PUNTO_DESTINO_DE_LA_MERCANCIA_DP("DP"),
		DESTINATARIO_FINAL_UC("UC"),
		COMPRADOR_BY("BY"),
		A_QUIEN_SE_FACTURA_IV("IV"),
		SUJETO_DEL_PAGO__A_QUIEN_SE_PAGA__PE("PE"),
		PAGADOR__QUIEN_PAGA__PR("PR"),
		EMISOR_DEL_MENSAJE_MS("MS"),
		RECEPTOR_DEL_MENSAJE_MR("MR"),
		;
		
		private String value;
		
		private SINCP_2(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCP_2 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	/** 
	 * 41 - Tipo Interlocutor: Código que identifica el tipo de código del interlocutor. Este campo se corresponde con el elemento 3055. Los valores posibles son:
	 */
	public enum SINCP_41 {
		CODIGO_EAN_9("9"),
		CODIGO_ASIGNADO_POR_EL_EMISOR_DEL_MENSAJE_91("91"),
		PERSONA_JURIDICA_J("J"),
		PERSONA_FISICA_F("F"),
		;
		
		private String value;
		
		private SINCP_41(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static SINCP_41 enumByValue(String value) {
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