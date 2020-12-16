package com.esferalia.aon.in.payroll.pdf.creators.enterprisePayroll;

import java.util.Objects;

public class EnterprisePayrollEntry{
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

	boolean has_ss;
	boolean has_aon;

	public static enum EnterpriseEntryType{
		AON_SYSTEM,
		SEG_SOCIAL
	}


	//CONSTRUCTOR
	public EnterprisePayrollEntry(EnterpriseEntryType type, String empleado, String tipo, Double devengado, Double ssTrab, Double irpf, Double deducciones, Double liquido, Double ssEmpr, Double costeTotal, Double ssTotal) {
		has_ss = false;
		has_aon = false;

		if(type == EnterpriseEntryType.AON_SYSTEM){
			this.empleado = 	empleado;
			this.tipo = 		tipo;
			this.devengado = 	devengado;
			this.ssTrab = 		ssTrab;
			this.irpf = 		irpf;
			this.deducciones = deducciones;
			this.liquido = 		liquido;
			this.ssEmpr = 		ssEmpr;
			this.costeTotal = 	costeTotal;
			this.ssTotal = 		ssTotal;
			has_aon = 			true;
		}
		if(type == EnterpriseEntryType.SEG_SOCIAL){
			this.empleadoSS = 		empleado;
			this.tipoSS = 			tipo;
			this.devengadoSS = 		devengado;
			this.ssTrabSS = 		ssTrab;
			this.irpfSS = 			irpf;
			this.deduccionesSS = 	deducciones;
			this.liquidoSS = 		liquido;
			this.ssEmprSS = 		ssEmpr;
			this.costeTotalSS = 	costeTotal;
			this.ssTotalSS = 		ssTotal;
			has_ss = 				true;
		}
	}

	public EnterprisePayrollEntry() {}

	//SETS SS PARAMETERS WITH OTHER OBJECT
	public void merge_ss_entry(EnterprisePayrollEntry ss_en){

		this.tipoSS = 			ss_en.getTipoSS();
		this.devengadoSS = 		ss_en.getDevengadoSS();
		this.ssTrabSS =			ss_en.getSsTrabSS();
		this.irpfSS = 			ss_en.getIrpfSS();
		this.deduccionesSS = 	ss_en.getDeduccionesSS();
		this.liquidoSS = 		ss_en.getLiquidoSS();
		this.ssEmprSS = 		ss_en.getSsEmprSS();
		this.costeTotalSS = 	ss_en.getCosteTotalSS();
		this.ssTotalSS = 		ss_en.getSsTotalSS();
		has_ss = true;
	}

	public String getEmpleado() {return empleado;}
	public String getEmpleadoSS() {return empleadoSS;}

	public String getTipo() {return tipo;}
	public String getTipoSS() {return tipoSS;}

	public Double getDevengado() {return devengado;}
	public Double getDevengadoSS() {return devengadoSS;}

	public Double getSsTrab() {return ssTrab;}
	public Double getSsTrabSS() {return ssTrabSS;}

	public Double getIrpf() {return irpf;}
	public Double getIrpfSS() {return irpfSS;}

	public Double getDeducciones() {return deducciones;}
	public Double getDeduccionesSS() {return deduccionesSS;}

	public Double getLiquido() {return liquido;}
	public Double getLiquidoSS() {return liquidoSS;}

	public Double getSsEmpr() {return ssEmpr;}
	public Double getSsEmprSS() {return ssEmprSS;}

	public Double getCosteTotal() {return costeTotal;}
	public Double getCosteTotalSS() {return costeTotalSS;}

	public Double getSsTotal() {return ssTotal;}
	public Double getSsTotalSS() {return ssTotalSS;}

	public boolean Has_ss(){return has_ss;}
	public boolean Has_aon(){return has_aon;}

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EnterprisePayrollEntry that = (EnterprisePayrollEntry) o;
		return Objects.equals(empleado, that.empleado) && Objects.equals(tipo, that.tipo) && Objects.equals(devengado, that.devengado) && Objects.equals(ssTrab, that.ssTrab) && Objects.equals(irpf, that.irpf) && Objects.equals(deducciones, that.deducciones) && Objects.equals(liquido, that.liquido) && Objects.equals(ssEmpr, that.ssEmpr) && Objects.equals(costeTotal, that.costeTotal) && Objects.equals(ssTotal, that.ssTotal) && Objects.equals(empleadoSS, that.empleadoSS) && Objects.equals(tipoSS, that.tipoSS) && Objects.equals(devengadoSS, that.devengadoSS) && Objects.equals(ssTrabSS, that.ssTrabSS) && Objects.equals(irpfSS, that.irpfSS) && Objects.equals(deduccionesSS, that.deduccionesSS) && Objects.equals(liquidoSS, that.liquidoSS) && Objects.equals(ssEmprSS, that.ssEmprSS) && Objects.equals(costeTotalSS, that.costeTotalSS) && Objects.equals(ssTotalSS, that.ssTotalSS);
	}

	@Override
	public int hashCode() {
		return Objects.hash(empleado, tipo, devengado, ssTrab, irpf, deducciones, liquido, ssEmpr, costeTotal, ssTotal, empleadoSS, tipoSS, devengadoSS, ssTrabSS, irpfSS, deduccionesSS, liquidoSS, ssEmprSS, costeTotalSS, ssTotalSS);
	}


}

