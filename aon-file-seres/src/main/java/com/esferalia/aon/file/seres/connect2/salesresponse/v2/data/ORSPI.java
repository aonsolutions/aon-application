package com.esferalia.aon.file.seres.connect2.salesresponse.v2.data;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ORSPI {
	private Integer numeroDeLineaDeImpuesto;
	private String calificadorTipoDeImpuesto;
	private Double porcentajeImpuesto;
	private Double importeImpuesto;
	private Double baseImponible;

	
	private static Pattern PATTERN_ORSPI_numeroDeLineaDeImpuesto = Pattern.compile("^.{6}(.{2}).*");
	private static Pattern PATTERN_ORSPI_calificadorTipoDeImpuesto = Pattern.compile("^.{8}(.{6}).*");
	private static Pattern PATTERN_ORSPI_porcentajeImpuesto = Pattern.compile("^.{14}(.{6}).*");
	private static Pattern PATTERN_ORSPI_importeImpuesto = Pattern.compile("^.{20}(.{18}).*");
	private static Pattern PATTERN_ORSPI_baseImponible = Pattern.compile("^.{38}(.{18}).*");

	public void parse(String value) {
		Matcher m;
		if((m = PATTERN_ORSPI_numeroDeLineaDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setNumeroDeLineaDeImpuesto(Integer.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPI_calificadorTipoDeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setCalificadorTipoDeImpuesto(String.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPI_porcentajeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setPorcentajeImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPI_importeImpuesto.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setImporteImpuesto(Double.valueOf(m.group(1).trim()));
		}
		if((m = PATTERN_ORSPI_baseImponible.matcher(value)).find() && StringUtils.isNotBlank(m.group(1))) {
			setBaseImponible(Double.valueOf(m.group(1).trim()));
		}
	}

	
	/**
	 * 2 - Número de Línea de Impuesto: Es un campo contador. Se sumará 1 por cada tipo de impuesto que se aplique. El valor inicial es '1'.
	 */
	public Integer getNumeroDeLineaDeImpuesto() {
		return numeroDeLineaDeImpuesto;
	}


	/**
	 * 2 - Número de Línea de Impuesto: Es un campo contador. Se sumará 1 por cada tipo de impuesto que se aplique. El valor inicial es '1'.
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>2</td> <td>Número de línea de impuesto</td> <td>N</td> <td>2</td> <td>7</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setNumeroDeLineaDeImpuesto(Integer numeroDeLineaDeImpuesto) {
		this.numeroDeLineaDeImpuesto = numeroDeLineaDeImpuesto;
	}


	/**
	 * 3 - Calificador tipo de Impuesto: valores posibles son:
	 */
	public String getCalificadorTipoDeImpuesto() {
		return calificadorTipoDeImpuesto;
	}


	/**
	 * 3 - Calificador tipo de Impuesto: valores posibles son:
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>3</td> <td>Calificador tipo de impuesto</td> <td>C</td> <td>6</td> <td>9</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setCalificadorTipoDeImpuesto(String calificadorTipoDeImpuesto) {
		this.calificadorTipoDeImpuesto = calificadorTipoDeImpuesto;
	}


	/**
	 * 4 - % Impuesto
	 */
	public Double getPorcentajeImpuesto() {
		return porcentajeImpuesto;
	}


	/**
	 * 4 - % Impuesto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>4</td> <td>% Impuesto</td> <td>N(3,2)</td> <td>6</td> <td>15</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setPorcentajeImpuesto(Double porcentajeImpuesto) {
		this.porcentajeImpuesto = porcentajeImpuesto;
	}


	/**
	 * 5 - Importe impuesto
	 */
	public Double getImporteImpuesto() {
		return importeImpuesto;
	}


	/**
	 * 5 - Importe impuesto
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>5</td> <td>Importe impuesto</td> <td>N(14,3)</td> <td>18</td> <td>21</td> <td>M</td>
	 * 	</tr>
	 * </table>
	 */
	public void setImporteImpuesto(Double importeImpuesto) {
		this.importeImpuesto = importeImpuesto;
	}


	/**
	 * 6 - Base imponible
	 */
	public Double getBaseImponible() {
		return baseImponible;
	}


	/**
	 * 6 - Base imponible
	 * 
	 * <table border="1" cellpadding="1" cellspacing="0">
	 * 	<tr bgcolor="#CCCCFF"> 
	 * 		 <th>Campo</th> <th>Descripcion</th> <th>Tipo</th> <th>Longitud</th> <th>Pos. Inicial</th> <th>Obligatoriedad</th>
	 * 	</tr>
	 * 	<tr>
	 * 		 <td>6</td> <td>Base imponible</td> <td>N(14,3)</td> <td>18</td> <td>39</td> <td>O</td>
	 * 	</tr>
	 * </table>
	 */
	public void setBaseImponible(Double baseImponible) {
		this.baseImponible = baseImponible;
	}

	/** 
	 * 3 - Calificador tipo de Impuesto: valores posibles son:
	 */
	public enum ORSPI_3 {
		IVA("VAT"),
		IGIC("IGI"),
		RECARGO_DE_EQUIVALENCIA("RE"),
		IPSI("IPS"),
		EXENTO("EXT"),
		IRPF_DENTRO_DE_FACTURA_ES_UN_IMPUESTO_RETENIDO_NO_REPERCUTIDO_("IRP"),
		IMPUESTO_DE_ALCOHOLES("ACT"),
		DERECHOS_DE_AUTOR("DA"),
		;
		
		private String value;
		
		private ORSPI_3(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static ORSPI_3 enumByValue(String value) {
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