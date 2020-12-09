package com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll;

public interface EnterprisePayrollEntryType {
	String empleado = null;
	String tipo = null;
	Double devengado = null;
	Double ssTrab = null;
	Double irpf = null;
	Double deducciones = null;
	Double liquido = null;
	Double ssEmpr = null;
	Double costeTotal = null;
	Double ssTotal = null;

	String empleadoSS = null;
	String tipoSS = null;
	Double devengadoSS = null;
	Double ssTrabSS = null;
	Double irpfSS = null;
	Double deduccionesSS = null;
	Double liquidoSS = null;
	Double ssEmprSS = null;
	Double costeTotalSS = null;
	Double ssTotalSS = null;

	void setEmpleado(String empleado);
	void setEmpleadoSS(String empleado);

	void setTipo(String tipo);
	void setTipoSS(String tipo);

	void setDevengado(double devengado);
	void setDevengadoSS(double devengado);

	void setSsTrab(double ssTrab);
	void setSsTrabSS(double ssTrab);

	void setIrpf(double irpf);
	void setIrpfSS(double irpf);

	void setDeducciones(double deducciones);
	void setDeduccionesSS(double deducciones);

	void setLiquido(double liquido);
	void setLiquidoSS(double liquido);

	void setSsEmpr(double ssEmpr);
	void setSsEmprSS(double ssEmpr);

	void setCosteTotal(double costeTotal);
	void setCosteTotalSS(double costeTotal);

	void setSsTotal(double ssTotal);
	void setSsTotalSS(double ssTotal);
}
