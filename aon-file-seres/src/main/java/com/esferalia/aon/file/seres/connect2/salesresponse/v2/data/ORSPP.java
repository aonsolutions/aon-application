package com.esferalia.aon.file.seres.connect2.salesresponse.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ORSPP {
	private String calificadorDelInterlocutor;
	private String codigoInterlocutor;
	private String agenciaResponsableDeLaListaDeCodigos;
	private String nombre1;
	private String nombre2;
	private String nombre3;
	private String nombre4;
	private String nombre5;
	private String calleYNumero1;
	private String calleYNumero2;
	private String calleYNumero3;
	private String calleYNumero4;
	private String poblacion;
	private String provincia;
	private String codigoPostal;
	private String codigoPais;
	private String calificadorReferencia1;
	private String referencia1;
	private String funcionDeContacto;
	private String departamentoOIdentificacionDelEmpleado;
	private String departamentoOEmpleado;
	private String calificadorReferencia2;
	private String referencia2;

	private static Pattern PATTERN_ORSPP_calificadorDelInterlocutor = Pattern.compile("^.{6}(.{3}).*");
	private static Pattern PATTERN_ORSPP_codigoInterlocutor = Pattern.compile("^.{16}(.{10}).*");
	private static Pattern PATTERN_ORSPP_agenciaResponsableDeLaListaDeCodigos = Pattern.compile("^.{26}(.{3}).*");
	private static Pattern PATTERN_ORSPP_nombre1 = Pattern.compile("^.{29}(.{35}).*");
	private static Pattern PATTERN_ORSPP_nombre2 = Pattern.compile("^.{64}(.{35}).*");
	private static Pattern PATTERN_ORSPP_nombre3 = Pattern.compile("^.{99}(.{35}).*");
	private static Pattern PATTERN_ORSPP_nombre4 = Pattern.compile("^.{134}(.{35}).*");
	private static Pattern PATTERN_ORSPP_nombre5 = Pattern.compile("^.{169}(.{35}).*");
	private static Pattern PATTERN_ORSPP_calleYNumero1 = Pattern.compile("^.{204}(.{35}).*");
	private static Pattern PATTERN_ORSPP_calleYNumero2 = Pattern.compile("^.{239}(.{35}).*");
	private static Pattern PATTERN_ORSPP_calleYNumero3 = Pattern.compile("^.{274}(.{35}).*");
	private static Pattern PATTERN_ORSPP_calleYNumero4 = Pattern.compile("^.{309}(.{35}).*");
	private static Pattern PATTERN_ORSPP_poblacion = Pattern.compile("^.{344}(.{35}).*");
	private static Pattern PATTERN_ORSPP_provincia = Pattern.compile("^.{379}(.{9}).*");
	private static Pattern PATTERN_ORSPP_codigoPostal = Pattern.compile("^.{388}(.{9}).*");
	private static Pattern PATTERN_ORSPP_codigoPais = Pattern.compile("^.{397}(.{3}).*");
	private static Pattern PATTERN_ORSPP_calificadorReferencia1 = Pattern.compile("^.{400}(.{3}).*");
	private static Pattern PATTERN_ORSPP_referencia1 = Pattern.compile("^.{403}(.{35}).*");
	private static Pattern PATTERN_ORSPP_funcionDeContacto = Pattern.compile("^.{438}(.{3}).*");
	private static Pattern PATTERN_ORSPP_departamentoOIdentificacionDelEmpleado = Pattern.compile("^.{441}(.{17}).*");
	private static Pattern PATTERN_ORSPP_departamentoOEmpleado = Pattern.compile("^.{458}(.{35}).*");
	private static Pattern PATTERN_ORSPP_calificadorReferencia2 = Pattern.compile("^.{493}(.{3}).*");
	private static Pattern PATTERN_ORSPP_referencia2 = Pattern.compile("^.{497}(.{17}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ORSPP_calificadorDelInterlocutor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorDelInterlocutor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_codigoInterlocutor.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoInterlocutor(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_agenciaResponsableDeLaListaDeCodigos.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setAgenciaResponsableDeLaListaDeCodigos(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_nombre1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_nombre2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_nombre3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_nombre4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_nombre5.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNombre5(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_calleYNumero1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalleYNumero1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_calleYNumero2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalleYNumero2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_calleYNumero3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalleYNumero3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_calleYNumero4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalleYNumero4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_poblacion.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPoblacion(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_provincia.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setProvincia(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_codigoPostal.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPostal(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_codigoPais.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCodigoPais(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_calificadorReferencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_referencia1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setReferencia1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_funcionDeContacto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setFuncionDeContacto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_departamentoOIdentificacionDelEmpleado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDepartamentoOIdentificacionDelEmpleado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_departamentoOEmpleado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setDepartamentoOEmpleado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_calificadorReferencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorReferencia2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPP_referencia2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setReferencia2(String.valueOf(m.group(1).trim()));
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
	 * 3 - Código Interlocutor
	 */
	public String getCodigoInterlocutor() {
		return codigoInterlocutor;
	}

	/**
	 * 3 - Código Interlocutor
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Código Interlocutor</td> <td>C</td> <td>17</td> <td>10</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCodigoInterlocutor(String codigoInterlocutor) {
		this.codigoInterlocutor = codigoInterlocutor;
	}

	/**
	 * 4 - Lista de código responsable de la agencia: Código identificando la agencia responsable de la lista de los códigos. El campo se corresponde con el elemento 3055. Los valores posibles son:
	 */
	public String getAgenciaResponsableDeLaListaDeCodigos() {
		return agenciaResponsableDeLaListaDeCodigos;
	}

	/**
	 * 4 - Lista de código responsable de la agencia: Código identificando la agencia responsable de la lista de los códigos. El campo se corresponde con el elemento 3055. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Agencia responsable de la lista de códigos</td> <td>C</td> <td>3</td> <td>27</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setAgenciaResponsableDeLaListaDeCodigos(String agenciaResponsableDeLaListaDeCodigos) {
		this.agenciaResponsableDeLaListaDeCodigos = agenciaResponsableDeLaListaDeCodigos;
	}
	
	/**
	 * 5 - Nombre 1
	 */
	public String getNombre1() {
		return nombre1;
	}

	/**
	 * 5 - Nombre 1
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
	 * 6 - Nombre 2
	 */
	public String getNombre2() {
		return nombre2;
	}

	/**
	 * 6 - Nombre 2
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Nombre 1</td> <td>C</td> <td>35</td> <td>65</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNombre2(String nombre2) {
		this.nombre2 = nombre2;
	}
	
	/**
	 * 7 - Nombre 3
	 */
	public String getNombre3() {
		return nombre3;
	}

	/**
	 * 7 - Nombre 3
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
	 * 8 - Nombre 4
	 */
	public String getNombre4() {
		return nombre4;
	}

	/**
	 * 8 - Nombre 4
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
	 * 9 - Nombre 5
	 */
	public String getNombre5() {
		return nombre5;
	}

	/**
	 * 9 - Nombre 5
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
	 * 10 - Calle y numero 1
	 */
	public String getCalleYNumero1() {
		return calleYNumero1;
	}

	/**
	 * 10 - Calle y numero 1
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Calle y numero 1</td> <td>C</td> <td>35</td> <td>205</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalleYNumero1(String calleYNumero1) {
		this.calleYNumero1 = calleYNumero1;
	}
	
	/**
	 * 11 - Calle y numero 2
	 */
	public String getCalleYNumero2() {
		return calleYNumero2;
	}

	/**
	 * 11 - Calle y numero 2
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Calle y numero 2</td> <td>C</td> <td>35</td> <td>240</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalleYNumero2(String calleYNumero2) {
		this.calleYNumero2 = calleYNumero2;
	}
	
	/**
	 * 12 - Calle y numero 3
	 */
	public String getCalleYNumero3() {
		return calleYNumero3;
	}

	/**
	 * 12 - Calle y numero 3
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Calle y numero 3</td> <td>C</td> <td>35</td> <td>275</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalleYNumero3(String calleYNumero3) {
		this.calleYNumero3 = calleYNumero3;
	}

	/**
	 * 13 - Calle y numero 4
	 */
	public String getCalleYNumero4() {
		return calleYNumero4;
	}

	/**
	 * 13 - Calle y numero 4
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Calle y numero 4</td> <td>C</td> <td>35</td> <td>310</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalleYNumero4(String calleYNumero4) {
		this.calleYNumero4 = calleYNumero4;
	}
	
	/**
	 * 14 - Población
	 */
	public String getPoblacion() {
		return poblacion;
	}

	/**
	 * 14 - Población
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Población</td> <td>C</td> <td>35</td> <td>345</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}
	
	/**
	 * 15 - Provincia
	 */
	public String getProvincia() {
		return provincia;
	}

	/**
	 * 15 - Provincia
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
	 * 16 - Código postal
	 */
	public String getCodigoPostal() {
		return codigoPostal;
	}

	/**
	 * 16 - Código postal
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Código postal</td> <td>C</td> <td>9</td> <td>389</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	
	/**
	 * 17 - Código país
	 */
	public String getCodigoPais() {
		return codigoPais;
	}

	/**
	 * 17 - Código país
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Código postal</td> <td>C</td> <td>3</td> <td>398</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCodigoPais(String codigoPais) {
		this.codigoPais = codigoPais;
	}

	/**
	 * 18, 19 - Referencia 1: Referencia relacionada al interlocutor. El campo 18 se corresponde con el elemento 3035. Los valores posibles son:
	 */
	public String getCalificadorReferencia1() {
		return calificadorReferencia1;
	}

	/**
	 * 18, 19 - Referencia 1: Referencia relacionada al interlocutor. El campo 18 se corresponde con el elemento 3035. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Calificador referencia 1</td> <td>C</td> <td>3</td> <td>401</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorReferencia1(String calificadorReferencia1) {
		this.calificadorReferencia1 = calificadorReferencia1;
	}

	/**
	 * 18, 19 - Referencia 1: Referencia relacionada al interlocutor. El campo 18 se corresponde con el elemento 3035. Los valores posibles son:
	 */
	public String getReferencia1() {
		return referencia1;
	}

	/**
	 * 18, 19 - Referencia 1: Referencia relacionada al interlocutor. El campo 18 se corresponde con el elemento 3035. Los valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Referencia 1</td> <td>C</td> <td>35</td> <td>404</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setReferencia1(String referencia1) {
		this.referencia1 = referencia1;
	}
	
	/**
	 * 20 - Función Contacto: Código que especifica la función del contacto. Este campo se corresponde con el elemento 3139. Los posibles valores son:
	 */
	public String getFuncionDeContacto() {
		return funcionDeContacto;
	}
	
	/**
	 * 20 - Función Contacto: Código que especifica la función del contacto. Este campo se corresponde con el elemento 3139. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>20</td> <td>Función de contacto</td> <td>C</td> <td>3</td> <td>439</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setFuncionDeContacto(String funcionDeContacto) {
		this.funcionDeContacto = funcionDeContacto;
	}
	
	/**
	 * 21 - Departamento o identificación de trabajador: Código de identificación interna. El código es estipulado por la organización en cuestión.
	 */
	public String getDepartamentoOIdentificacionDelEmpleado() {
		return departamentoOIdentificacionDelEmpleado;
	}

	/**
	 * 21 - Departamento o identificación de trabajador: Código de identificación interna. El código es estipulado por la organización en cuestión.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>21</td> <td>Departamento o identificación del empleado</td> <td>C</td> <td>17</td> <td>442</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setDepartamentoOIdentificacionDelEmpleado(String departamentoOIdentificacionDelEmpleado) {
		this.departamentoOIdentificacionDelEmpleado = departamentoOIdentificacionDelEmpleado;
	}

	/**
	 * 22 - Departamento o empleado
	 */
	public String getDepartamentoOEmpleado() {
		return departamentoOEmpleado;
	}

	/**
	 * 22 - Departamento o empleado
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>22</td> <td>Departamento o empleado</td> <td>C</td> <td>35</td> <td>459</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setDepartamentoOEmpleado(String departamentoOEmpleado) {
		this.departamentoOEmpleado = departamentoOEmpleado;
	}
	
	/**
	 * 23, 24 - Referencia 2: Lo mismo de los campos 18 y 19.
	 */
	public String getCalificadorReferencia2() {
		return calificadorReferencia2;
	}

	/**
	 * 23, 24 - Referencia 2: Lo mismo de los campos 18 y 19.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>23</td> <td>Calificador referencia 2</td> <td>C</td> <td>3</td> <td>494</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorReferencia2(String calificadorReferencia2) {
		this.calificadorReferencia2 = calificadorReferencia2;
	}

	/**
	 * 23, 24 - Referencia 2: Lo mismo de los campos 18 y 19.
	 */
	public String getReferencia2() {
		return referencia2;
	}

	/**
	 * 23, 24 - Referencia 2: Lo mismo de los campos 18 y 19.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>24</td> <td>Referencia 2</td> <td>C</td> <td>17</td> <td>497</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setReferencia2(String referencia2) {
		this.referencia2 = referencia2;
	}

	/** 
	 * 2 - Calificador del Interlocutor: Código que identifica cada tipo de interlocutor. Este campo se corresponde con el elemento 3035. Los valores posibles son:
	 */
	public enum ORSPP_2 {
		EMISOR_DEL_MENSAJE("MS"),
		RECEPTOR_DEL_MENSAJE("MR"),
		PROVEEDOR("SU"),
		PUNTO_DESTINO_DE_LA_MERCANCIA("DP"),
		DESTINATARIO_FINAL("UC"),
		COMPRADOR("BY"),
		A_QUIEN_SE_FACTURA("IV"),
		SUJETO_DEL_PAGO_A_QUIEN_SE_PAGA_("PE"),
		PAGADOR_QUIEN_PAGA_("PR"),
		PUNTO_DE_EXPEDICION("PW"),
		;
		
		private String value;
		
		private ORSPP_2(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ORSPP_2 enumByValue(String value) {
			return Arrays.asList(values()).stream()
				.filter(o -> (o.getValue().equalsIgnoreCase(value)))
				.findFirst().orElse(null);
		}

	}
	
	/** 
	 * 4 - Lista de código responsable de la agencia: Código identificando la agencia responsable de la lista de los códigos. El campo se corresponde con el elemento 3055. Los valores posibles son:
	 */
	public enum ORSPP_4 {
		EAN("9"),
		;
		
		private String value;
		
		private ORSPP_4(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPP_4 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 18, 19 - Referencia 1: Referencia relacionada al interlocutor. El campo 18 se corresponde con el elemento 3035. Los valores posibles son:
	 */
	public enum ORSPP_18 {
		IDENTIFICACION_ADICIONAL_DE_LA_PARTE("API"),
		NUMERO_DE_REFERENCIA_GUBERNAMENTAL("GN"),
		NUMERO_INTERNO_DE_VENDEDOR("IA"),
		NUMERO_INTERNO_DE_CLIENTE("IT"),
		NUMERO_DE_IDENTIFICACION_FISCAL("VA"),
		;
		
		private String value;
		
		private ORSPP_18(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPP_18 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 20 - Función Contacto: Código que especifica la función del contacto. Este campo se corresponde con el elemento 3139. Los posibles valores son:
	 */
	public enum ORSPP_20 {
		CONTACTO_INFORMACION("IC"),
		CONTACTO_PEDIDO("OC"),
		CONTACTO_DE_ENTREGA("DL"),
		CONTACTO_DE_TRANSPORTE("TR"),
		;
		
		private String value;
		
		private ORSPP_20(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPP_20 enumByValue(String value) {
			return Arrays.asList(values()).stream()
					.filter(o -> (o.getValue().equalsIgnoreCase(value)))
					.findFirst().orElse(null);
		}
		
	}
	
	/** 
	 * 23, 24 - Referencia 2: Lo mismo de los campos 18 y 19.
	 */
	public enum ORSPP_23 {
		IDENTIFICACION_ADICIONAL_DE_LA_PARTE("API"),
		NUMERO_DE_REFERENCIA_GUBERNAMENTAL("GN"),
		NUMERO_INTERNO_DE_VENDEDOR("IA"),
		NUMERO_INTERNO_DE_CLIENTE("IT"),
		NUMERO_DE_IDENTIFICACION_FISCAL("VA"),
		;
		
		private String value;
		
		private ORSPP_23(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static ORSPP_23 enumByValue(String value) {
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