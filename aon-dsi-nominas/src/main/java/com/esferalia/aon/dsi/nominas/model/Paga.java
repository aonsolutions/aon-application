package com.esferalia.aon.dsi.nominas.model;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Paga {
	
	private String mes;
	private double importe;
	private String descripcion;
	private String diaInicio;
	private String mesInicio;
	private String anoInicio; // A-Año anterior, C-Año en curso 
	private String diaFin;
	private String mesFin;
	private String anoFin; // A-Año anterior, C-Año en curso
	private String tipo;
	
	public String getMes() {
		return mes;
	}
	public Paga setMes(String mes) {
		this.mes = mes;
		return this;
	}
	public double getImporte() {
		return importe;
	}
	public Paga setImporte(double importe) {
		this.importe = importe;
		return this;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public Paga setDescripcion(String descripcion) {
		this.descripcion = descripcion;
		return this;
	}
	public String getDiaInicio() {
		return diaInicio;
	}
	public Paga setDiaInicio(String diaInicio) {
		this.diaInicio = diaInicio;
		return this;
	}
	public String getMesInicio() {
		return mesInicio;
	}
	public Paga setMesInicio(String mesInicio) {
		this.mesInicio = mesInicio;
		return this;
	}
	public String getAnoInicio() {
		return anoInicio;
	}
	public Paga setAnoInicio(String anoInicio) {
		this.anoInicio = anoInicio;
		return this;
	}
	public String getDiaFin() {
		return diaFin;
	}
	public Paga setDiaFin(String diaFin) {
		this.diaFin = diaFin;
		return this;
	}
	public String getMesFin() {
		return mesFin;
	}
	public Paga setMesFin(String mesFin) {
		this.mesFin = mesFin;
		return this;
	}
	public String getAnoFin() {
		return anoFin;
	}
	public Paga setAnoFin(String anoFin) {
		this.anoFin = anoFin;
		return this;
	}
	public String getTipo() {
		return tipo;
	}
	public Paga setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}	
	
	// Devuelve si la paga es por días
	// true si importe > 0 y importe <= 61, false en caso contrario	
	public boolean esPorDias() {
		return (this.importe > 0 && this.importe <= 61);
	}

	// Codigo del concepto en AON para payment_concept 	 
	public String getAonCode() {
		return "PAGA_EXTRA_" + AonStringUtils.trimToEmpty(this.mes);
	}
	
	// Valor del campo salary_type del concepto en AON
	// 0 si la paga está prorrateada, 1 en caso contrario	 
	public byte getAonSalaryType() {
		return (byte) ("P".equals(this.tipo) ? 0 : 1);
	}
	
	// Nombre de la variable que se utilizara en la expresion del concepto de AON	
	public String getAonVariable(boolean pagaDistinta) {
		String variable = "";
		if (esPorDias()) {
			if (pagaDistinta) {
				if (this.importe != 30)
					variable =  ("APLICAR_PAGA_" + AonStringUtils.trimToEmpty(this.mes) + "_" + this.importe).replace(".", "");
				else variable =  "APLICAR_PAGA_" + AonStringUtils.trimToEmpty(this.mes);
			}				
		}
		else {
			variable =  "IMPORTE_PAGA_" + AonStringUtils.trimToEmpty(this.mes);
		}	
		// Además si la paga está prorrateada, se añade _P a la variable
		if (AonStringUtils.isNotBlank(variable) && "P".equals(this.tipo))
			variable = variable + "_P";
		return variable;				
	}
	
	// Expresión que se utilizará en el concepto de AON
	public String getAonExpression(String conceptosPagas, boolean pagaDistinta) {
		String expression = "";		
		if (esPorDias()) {
			// Paga por Días 
			if (pagaDistinta) {
				if (this.importe != 30)
					expression = getAonVariable(pagaDistinta) + " * (" + String.format("%s/30 * ("+conceptosPagas+")", this.importe) + ")";
				else expression = getAonVariable(pagaDistinta) + " * (" + conceptosPagas + ")";
			}
			else {
				if (this.importe != 30)
					expression = String.format("%s/30 * ("+conceptosPagas+")", this.importe);
				else expression = conceptosPagas;
			}
		}
		else {
			// Paga por importe
			expression = getAonVariable(pagaDistinta) + " * DIAS_TRABAJADOS / DIAS_MES";
		}
		return expression;
	}

	// Valor del campo start_date que se guarda en AON en la definición de las pagas extras no prorrateadas
	// dd/mm [-1] (dia inicio/mes inicio y -1 si año="A")	 
	public String getAonStartDate() {
		return AonStringUtils.trimToEmpty(this.diaInicio) + "/" + AonStringUtils.trimToEmpty(this.mesInicio) + ("A".equals(this.anoInicio) ? " -1" : ""); 
	}
	
	// Valor del campo end_date que se guarda en AON en la definición de las pagas extras no prorrateadas
	// dd/mm [-1] (dia inicio/mes inicio y -1 si año="A")
	public String getAonEndDate() {
		return AonStringUtils.trimToEmpty(this.diaFin) + "/" + AonStringUtils.trimToEmpty(this.mesFin) + ("A".equals(this.anoFin) ? " -1" : ""); 
	}
	
	// Valor del campo issue_date que se guarda en AON en la definición de las pagas extras no prorrateadas
	// dd/mm (ultimo dia del mes/mes)
	public String getAonIssueDate() {
		String dia = "";
		switch (AonStringUtils.trimToEmpty(this.mes)) {
			case "01":
			case "03":
			case "05":
			case "07":
			case "08":
			case "10":
			case "12":				
				dia = "31";
				break;
			case "02":
				dia = "28";
				break;
			case "04":
			case "06":
			case "09":
			case "11":	
				dia = "30";
				break;
			default:
				break;
		}		
		return dia + "/" + AonStringUtils.trimToEmpty(this.mes);
	}
	
	// Valor del campo month del concepto en AON, para las pagas extras no prorrateadas
	// mes de la paga extra (en base 0, es decir enero=0 y diciembre=11)
	public Byte getAonMonth() {		
		if ("P".equals(this.tipo))
			return null;
		else return (byte) (Byte.parseByte(this.mes) - 1);				
	}
	
	// Valor del campo description en el concepto de AON
	// descripcion en Omega, si no contiene la descripción de Omega la palabra "PAGA" ni "EXTRA", se le antepone el literal "PAGA EXTRA"
	public String getAonDescription() {
		
		String description = AonStringUtils.trimToEmpty(this.descripcion);
		
		if (!description.contains("PAGA") && !description.contains("EXTRA"))
			description = "PAGA EXTRA " + description;
		
		return description;
		
	}
	
	// Expresion que se guarda en QUOTE_EXPRESSION del concepto de AON, para las pagas extras
	// "_P" si la paga está prorrateada, en caso contrario "PRORRATEAR()"
	// Solo se usa al traspasar el convenio y categorias
	public String getAonQuoteExpression() {
		return "P".equals(this.tipo) ? "_P" : "PRORRATEAR()";		
	}
	
	@Override
	public String toString() {
		return "Paga [mes=" + mes + ", importe=" + importe + ", descripcion=" + descripcion + ", diaInicio=" + diaInicio
				+ ", mesInicio=" + mesInicio + ", anoInicio=" + anoInicio + ", diaFin=" + diaFin + ", mesFin=" + mesFin
				+ ", anoFin=" + anoFin + ", tipo=" + tipo + "]";
	}
	
}
