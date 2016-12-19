package com.esferalia.aon.file.seres.connect.income.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This is an AUTOMATICALLY GENERATED class for represent EDI RECAE entity.
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
 * 		 <td>RECAE</td> <td>Embalajes confirmación de recepción</td> <td>Obligatorio</td> <td>N</td>
 * 	</tr>
 * </table>
 */ 

public class RECAE {

	private String numeroDeJerarquiaDeEmbalaje;
	private String numeroDeJerarquiaPadreDeEmbalaje;
	private Integer numeroDePaquetes;
	private String informacionSobreElEmbalaje_Codificado;
	private String terminosYCondicionesDelEmbalaje_Codificado;
	private String tipoDeEmbalaje_Codificado;
	private String tipoDeEmbalaje_TextoLibre;
	private String marcaDeEnvio1;
	private String marcaDeEnvio2;
	private String marcaDeEnvio3;
	private String marcaDeEnvio4;
	private String numeroSerial1ONumeroDeIdentificacionInferior;
	private String numeroSerial1ONumeroDeIdentificacionSuperior;
	private String numeroSerial2ONumeroDeIdentificacionInferior;
	private String numeroSerial2ONumeroDeIdentificacionSuperior;
	private String numeroSerial3ONumeroDeIdentificacionInferior;
	private String numeroSerial3ONumeroDeIdentificacionSuperior;


	private static Pattern PATTERN_RECAE_numeroDeJerarquiaDeEmbalaje = Pattern.compile("^.{6}(.{12}).*");
	private static Pattern PATTERN_RECAE_numeroDeJerarquiaPadreDeEmbalaje = Pattern.compile("^.{18}(.{12}).*");
	private static Pattern PATTERN_RECAE_numeroDePaquetes = Pattern.compile("^.{30}(.{8}).*");
	private static Pattern PATTERN_RECAE_informacionSobreElEmbalaje_Codificado = Pattern.compile("^.{38}(.{3}).*");
	private static Pattern PATTERN_RECAE_terminosYCondicionesDelEmbalaje_Codificado = Pattern.compile("^.{41}(.{3}).*");
	private static Pattern PATTERN_RECAE_tipoDeEmbalaje_Codificado = Pattern.compile("^.{44}(.{17}).*");
	private static Pattern PATTERN_RECAE_tipoDeEmbalaje_TextoLibre = Pattern.compile("^.{61}(.{35}).*");
	private static Pattern PATTERN_RECAE_marcaDeEnvio1 = Pattern.compile("^.{96}(.{35}).*");
	private static Pattern PATTERN_RECAE_marcaDeEnvio2 = Pattern.compile("^.{131}(.{35}).*");
	private static Pattern PATTERN_RECAE_marcaDeEnvio3 = Pattern.compile("^.{166}(.{35}).*");
	private static Pattern PATTERN_RECAE_marcaDeEnvio4 = Pattern.compile("^.{201}(.{35}).*");
	private static Pattern PATTERN_RECAE_numeroSerial1ONumeroDeIdentificacionInferior = Pattern.compile("^.{236}(.{35}).*");
	private static Pattern PATTERN_RECAE_numeroSerial1ONumeroDeIdentificacionSuperior = Pattern.compile("^.{271}(.{35}).*");
	private static Pattern PATTERN_RECAE_numeroSerial2ONumeroDeIdentificacionInferior = Pattern.compile("^.{306}(.{35}).*");
	private static Pattern PATTERN_RECAE_numeroSerial2ONumeroDeIdentificacionSuperior = Pattern.compile("^.{341}(.{35}).*");
	private static Pattern PATTERN_RECAE_numeroSerial3ONumeroDeIdentificacionInferior = Pattern.compile("^.{376}(.{35}).*");
	private static Pattern PATTERN_RECAE_numeroSerial3ONumeroDeIdentificacionSuperior = Pattern.compile("^.{411}(.{35}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_RECAE_numeroDeJerarquiaDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeJerarquiaDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_numeroDeJerarquiaPadreDeEmbalaje.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeJerarquiaPadreDeEmbalaje(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_numeroDePaquetes.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDePaquetes(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_informacionSobreElEmbalaje_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setInformacionSobreElEmbalaje_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_terminosYCondicionesDelEmbalaje_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTerminosYCondicionesDelEmbalaje_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_tipoDeEmbalaje_Codificado.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeEmbalaje_Codificado(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_tipoDeEmbalaje_TextoLibre.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setTipoDeEmbalaje_TextoLibre(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_marcaDeEnvio1.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaDeEnvio1(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_marcaDeEnvio2.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaDeEnvio2(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_marcaDeEnvio3.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaDeEnvio3(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_marcaDeEnvio4.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setMarcaDeEnvio4(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_numeroSerial1ONumeroDeIdentificacionInferior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial1ONumeroDeIdentificacionInferior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_numeroSerial1ONumeroDeIdentificacionSuperior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial1ONumeroDeIdentificacionSuperior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_numeroSerial2ONumeroDeIdentificacionInferior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial2ONumeroDeIdentificacionInferior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_numeroSerial2ONumeroDeIdentificacionSuperior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial2ONumeroDeIdentificacionSuperior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_numeroSerial3ONumeroDeIdentificacionInferior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial3ONumeroDeIdentificacionInferior(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_RECAE_numeroSerial3ONumeroDeIdentificacionSuperior.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroSerial3ONumeroDeIdentificacionSuperior(String.valueOf(m.group(1).trim()));
		}
	}


	/** 
	 * 2 - Número de jerarquía de embalaje: Un número único asignado por el emisor para identificar el nivel de embalaje. Se recomienda utilizar un contador secuencial.
	 */ 
	public String getNumeroDeJerarquiaDeEmbalaje() {
		return numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * 2 - Número de jerarquía de embalaje: Un número único asignado por el emisor para identificar el nivel de embalaje. Se recomienda utilizar un contador secuencial.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número de jerarquía de embalaje</td> <td>C</td> <td>12</td> <td>7</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeJerarquiaDeEmbalaje(String numeroDeJerarquiaDeEmbalaje) {
		this.numeroDeJerarquiaDeEmbalaje = numeroDeJerarquiaDeEmbalaje;
	}

	/** 
	 * 3 - Número de jerarquía padre de embalaje: Identifica el nivel de embalaje superior al que pertenece.
	 */ 
	public String getNumeroDeJerarquiaPadreDeEmbalaje() {
		return numeroDeJerarquiaPadreDeEmbalaje;
	}

	/** 
	 * 3 - Número de jerarquía padre de embalaje: Identifica el nivel de embalaje superior al que pertenece.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Número de jerarquía padre de embalaje</td> <td>C</td> <td>12</td> <td>19</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDeJerarquiaPadreDeEmbalaje(String numeroDeJerarquiaPadreDeEmbalaje) {
		this.numeroDeJerarquiaPadreDeEmbalaje = numeroDeJerarquiaPadreDeEmbalaje;
	}

	/** 
	 * 51 - Embalaje con código de barras ITF-14 o ITF-6
	 */ 
	public Integer getNumeroDePaquetes() {
		return numeroDePaquetes;
	}

	/** 
	 * 51 - Embalaje con código de barras ITF-14 o ITF-6
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>Número de paquetes</td> <td>N</td> <td>8</td> <td>31</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroDePaquetes(Integer numeroDePaquetes) {
		this.numeroDePaquetes = numeroDePaquetes;
	}

	/** 
	 * 5 - Información sobre el embalaje, codificada: Este campo se corresponde con el elemento 7233. Lo posibles valores son:
	 */ 
	public String getInformacionSobreElEmbalaje_Codificado() {
		return informacionSobreElEmbalaje_Codificado;
	}

	/** 
	 * 5 - Información sobre el embalaje, codificada: Este campo se corresponde con el elemento 7233. Lo posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Información sobre el embalaje, codificado</td> <td>C</td> <td>3</td> <td>39</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setInformacionSobreElEmbalaje_Codificado(String informacionSobreElEmbalaje_Codificado) {
		this.informacionSobreElEmbalaje_Codificado = informacionSobreElEmbalaje_Codificado;
	}

	/** 
	 * 6 - Términos y condiciones del embalaje, codificado: Este campo se corresponde con el elemento 7073. Los posibles valores son:
	 */ 
	public String getTerminosYCondicionesDelEmbalaje_Codificado() {
		return terminosYCondicionesDelEmbalaje_Codificado;
	}

	/** 
	 * 6 - Términos y condiciones del embalaje, codificado: Este campo se corresponde con el elemento 7073. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Términos y Condiciones del embalaje, codificado</td> <td>C</td> <td>3</td> <td>42</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTerminosYCondicionesDelEmbalaje_Codificado(String terminosYCondicionesDelEmbalaje_Codificado) {
		this.terminosYCondicionesDelEmbalaje_Codificado = terminosYCondicionesDelEmbalaje_Codificado;
	}

	/** 
	 * 7 - Tipo de embalaje, codificado: Este campo se corresponde con el elemento 7065. Los posibles valores son:
	 */ 
	public String getTipoDeEmbalaje_Codificado() {
		return tipoDeEmbalaje_Codificado;
	}

	/** 
	 * 7 - Tipo de embalaje, codificado: Este campo se corresponde con el elemento 7065. Los posibles valores son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>7</td> <td>Tipo de embalaje, codificado</td> <td>C</td> <td>17</td> <td>45</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeEmbalaje_Codificado(String tipoDeEmbalaje_Codificado) {
		this.tipoDeEmbalaje_Codificado = tipoDeEmbalaje_Codificado;
	}

	/** 
	 * 08 - Pallet no retornable
	 */ 
	public String getTipoDeEmbalaje_TextoLibre() {
		return tipoDeEmbalaje_TextoLibre;
	}

	/** 
	 * 08 - Pallet no retornable
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>8</td> <td>Tipo de embalaje, texto libre</td> <td>C</td> <td>35</td> <td>62</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setTipoDeEmbalaje_TextoLibre(String tipoDeEmbalaje_TextoLibre) {
		this.tipoDeEmbalaje_TextoLibre = tipoDeEmbalaje_TextoLibre;
	}

	/** 
	 * 09 - Pallet retornable
	 */ 
	public String getMarcaDeEnvio1() {
		return marcaDeEnvio1;
	}

	/** 
	 * 09 - Pallet retornable
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>9</td> <td>Marca de envió 1</td> <td>C</td> <td>35</td> <td>97</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaDeEnvio1(String marcaDeEnvio1) {
		this.marcaDeEnvio1 = marcaDeEnvio1;
	}

	/** 
	 * 
	 */ 
	public String getMarcaDeEnvio2() {
		return marcaDeEnvio2;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>10</td> <td>Marca de envió 2</td> <td>C</td> <td>35</td> <td>132</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaDeEnvio2(String marcaDeEnvio2) {
		this.marcaDeEnvio2 = marcaDeEnvio2;
	}

	/** 
	 * 
	 */ 
	public String getMarcaDeEnvio3() {
		return marcaDeEnvio3;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>11</td> <td>Marca de envió 3</td> <td>C</td> <td>35</td> <td>167</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaDeEnvio3(String marcaDeEnvio3) {
		this.marcaDeEnvio3 = marcaDeEnvio3;
	}

	/** 
	 * 
	 */ 
	public String getMarcaDeEnvio4() {
		return marcaDeEnvio4;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>12</td> <td>Marca de envió 4</td> <td>C</td> <td>35</td> <td>202</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setMarcaDeEnvio4(String marcaDeEnvio4) {
		this.marcaDeEnvio4 = marcaDeEnvio4;
	}

	/** 
	 * 50 - Embalaje con código de barras EAN-13 o EAN-8
	 */ 
	public String getNumeroSerial1ONumeroDeIdentificacionInferior() {
		return numeroSerial1ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 50 - Embalaje con código de barras EAN-13 o EAN-8
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>13</td> <td>Número Serial 1 o número de identificación inferior</td> <td>C</td> <td>35</td> <td>237</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial1ONumeroDeIdentificacionInferior(String numeroSerial1ONumeroDeIdentificacionInferior) {
		this.numeroSerial1ONumeroDeIdentificacionInferior = numeroSerial1ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 51 - Embalaje con código de barras ITF-14 o ITF-6
	 */ 
	public String getNumeroSerial1ONumeroDeIdentificacionSuperior() {
		return numeroSerial1ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 51 - Embalaje con código de barras ITF-14 o ITF-6
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>14</td> <td>Número Serial 1 o número de identificación superior</td> <td>C</td> <td>35</td> <td>272</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial1ONumeroDeIdentificacionSuperior(String numeroSerial1ONumeroDeIdentificacionSuperior) {
		this.numeroSerial1ONumeroDeIdentificacionSuperior = numeroSerial1ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial2ONumeroDeIdentificacionInferior() {
		return numeroSerial2ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>15</td> <td>Número Serial 2 o número de identificación inferior</td> <td>C</td> <td>35</td> <td>307</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial2ONumeroDeIdentificacionInferior(String numeroSerial2ONumeroDeIdentificacionInferior) {
		this.numeroSerial2ONumeroDeIdentificacionInferior = numeroSerial2ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial2ONumeroDeIdentificacionSuperior() {
		return numeroSerial2ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>16</td> <td>Número Serial 2 o número de identificación superior</td> <td>C</td> <td>35</td> <td>342</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial2ONumeroDeIdentificacionSuperior(String numeroSerial2ONumeroDeIdentificacionSuperior) {
		this.numeroSerial2ONumeroDeIdentificacionSuperior = numeroSerial2ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial3ONumeroDeIdentificacionInferior() {
		return numeroSerial3ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>17</td> <td>Número Serial 3 o número de identificación inferior</td> <td>C</td> <td>35</td> <td>377</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial3ONumeroDeIdentificacionInferior(String numeroSerial3ONumeroDeIdentificacionInferior) {
		this.numeroSerial3ONumeroDeIdentificacionInferior = numeroSerial3ONumeroDeIdentificacionInferior;
	}

	/** 
	 * 
	 */ 
	public String getNumeroSerial3ONumeroDeIdentificacionSuperior() {
		return numeroSerial3ONumeroDeIdentificacionSuperior;
	}

	/** 
	 * 
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>18</td> <td>Número Serial 3 o número de identificación superior</td> <td>C</td> <td>35</td> <td>412</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */ 
	public void setNumeroSerial3ONumeroDeIdentificacionSuperior(String numeroSerial3ONumeroDeIdentificacionSuperior) {
		this.numeroSerial3ONumeroDeIdentificacionSuperior = numeroSerial3ONumeroDeIdentificacionSuperior;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}