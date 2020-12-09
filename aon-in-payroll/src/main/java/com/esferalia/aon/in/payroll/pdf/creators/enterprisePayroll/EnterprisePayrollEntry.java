package com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll;

public class EnterprisePayrollEntry implements EnterprisePayrollEntryType {
	String empleado;
	String tipo;
	Double devengado;
	Double ssTrab;
	Double irpf;
	Double deducciones;
	Double liquido;
	Double ssEmpr;
	Double costeTotal;
	Double ssTotal;

	String empleadoSS;
	String tipoSS;
	Double devengadoSS;
	Double ssTrabSS;
	Double irpfSS;
	Double deduccionesSS;
	Double liquidoSS;
	Double ssEmprSS;
	Double costeTotalSS;
	Double ssTotalSS;

	public void setEmpleado(String empleado) {this.empleado = empleado;}
	public void setEmpleadoSS(String empleado) {this.empleadoSS = empleado;}

	public void setTipo(String tipo) {this.tipo = tipo;}
	public void setTipoSS(String tipo) {this.tipoSS = tipo;}

	public void setDevengado(double devengado) {this.devengado = devengado;}
	public void setDevengadoSS(double devengado) {this.devengadoSS = devengado;}

	public void setSsTrab(double ssTrab) {this.ssTrab = ssTrab;}
	public void setSsTrabSS(double ssTrab) {this.ssTrabSS = ssTrab;}

	public void setIrpf(double irpf) {this.irpf = irpf;}
	public void setIrpfSS(double irpf) {this.irpfSS = irpf;}

	public void setDeducciones(double deducciones) {this.deducciones = deducciones;}
	public void setDeduccionesSS(double deducciones) {this.deduccionesSS = deducciones;}

	public void setLiquido(double liquido) {this.liquido = liquido;}
	public void setLiquidoSS(double liquido) {this.liquidoSS = liquido;}

	public void setSsEmpr(double ssEmpr) {this.ssEmpr = ssEmpr;}
	public void setSsEmprSS(double ssEmpr) {this.ssEmprSS = ssEmpr;}

	public void setCosteTotal(double costeTotal) {this.costeTotal = costeTotal;}
	public void setCosteTotalSS(double costeTotal) {this.costeTotalSS = costeTotal;}

	public void setSsTotal(double ssTotal) {this.ssTotal = ssTotal;}
	public void setSsTotalSS(double ssTotal) {this.ssTotalSS = ssTotal;}

	public String toString() {
		return "EnterprisePayrollEntry{" +
				"empleado='" + empleado + '\'' +
				", tipo='" + tipo + '\'' +
				", devengado=" + devengado +
				", ssTrab=" + ssTrab +
				", irpf=" + irpf +
				", deducciones=" + deducciones +
				", liquido=" + liquido +
				", ssEmpr=" + ssEmpr +
				", costeTotal=" + costeTotal +
				", ssTotal=" + ssTotal +
				", empleadoSS='" + empleadoSS + '\'' +
				", tipoSS='" + tipoSS + '\'' +
				", devengadoSS=" + devengadoSS +
				", ssTrabSS=" + ssTrabSS +
				", irpfSS=" + irpfSS +
				", deduccionesSS=" + deduccionesSS +
				", liquidoSS=" + liquidoSS +
				", ssEmprSS=" + ssEmprSS +
				", costeTotalSS=" + costeTotalSS +
				", ssTotalSS=" + ssTotalSS +
				'}';
	}
}

