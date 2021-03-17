package com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans;

import java.util.Objects;
import java.util.Optional;

public class EnterprisePayrollEntry{
	Optional<String> empleado;
	Optional<String> tipo;
	Optional<Double> devengado;
	Optional<Double> ssTrab;
	Optional<Double> irpf;
	Optional<Double> deducciones;
	Optional<Double> liquido;
	Optional<Double> ssEmpr;
	Optional<Double> costeTotal;
	Optional<Double> ssTotal;
	Optional<Double> bonificaciones;

	Optional<String> empleadoSS;
	Optional<String> tipoSS;
	Optional<Double> devengadoSS;
	Optional<Double> ssTrabSS;
	Optional<Double> irpfSS;
	Optional<Double> deduccionesSS;
	Optional<Double> liquidoSS;
	Optional<Double> ssEmprSS;
	Optional<Double> costeTotalSS;
	Optional<Double> ssTotalSS;
	Optional<Double> bonificacionesSS;

	public static enum EnterpriseEntryType{
		AON_SYSTEM,
		SEG_SOCIAL
	}


	//CONSTRUCTOR
	public EnterprisePayrollEntry(EnterpriseEntryType type, String empleado, String tipo, Double devengado, Double ssTrab, Double irpf, Double deducciones, Double liquido, Double ssEmpr, Double costeTotal, Double ssTotal, Double bonificaciones) {

		this.empleado = 		Optional.empty();
		this.tipo = 			Optional.empty();
		this.devengado = 		Optional.empty();
		this.ssTrab = 			Optional.empty();
		this.irpf = 			Optional.empty();
		this.deducciones =  	Optional.empty();
		this.liquido = 			Optional.empty();
		this.ssEmpr = 			Optional.empty();
		this.costeTotal = 		Optional.empty();
		this.ssTotal = 			Optional.empty();
		this.bonificaciones = 	Optional.empty();
		
		this.empleadoSS = 		Optional.empty();
		this.tipoSS = 			Optional.empty();
		this.devengadoSS = 		Optional.empty();
		this.ssTrabSS = 		Optional.empty();
		this.irpfSS = 			Optional.empty();
		this.deduccionesSS = 	Optional.empty();
		this.liquidoSS = 		Optional.empty();
		this.ssEmprSS = 		Optional.empty();
		this.costeTotalSS = 	Optional.empty();
		this.ssTotalSS = 		Optional.empty();
		this.bonificacionesSS = Optional.empty();
		
		if(type == EnterpriseEntryType.AON_SYSTEM){
			this.empleado = 		Optional.ofNullable(empleado);
			this.tipo = 			Optional.ofNullable(tipo);
			this.devengado = 		Optional.ofNullable(devengado);
			this.ssTrab = 			Optional.ofNullable(ssTrab);
			this.irpf = 			Optional.ofNullable(irpf);
			this.deducciones =  	Optional.ofNullable(deducciones);
			this.liquido = 			Optional.ofNullable(liquido);
			this.ssEmpr = 			Optional.ofNullable(ssEmpr);
			this.costeTotal = 		Optional.ofNullable(costeTotal);
			this.ssTotal = 			Optional.ofNullable(ssTotal);
			this.bonificaciones = 	Optional.ofNullable(bonificaciones);
		}
		if(type == EnterpriseEntryType.SEG_SOCIAL){
			this.empleadoSS = 		Optional.ofNullable(empleado);
			this.tipoSS = 			Optional.ofNullable(tipo);
			this.devengadoSS = 		Optional.ofNullable(devengado);
			this.ssTrabSS = 		Optional.ofNullable(ssTrab);
			this.irpfSS = 			Optional.ofNullable(irpf);
			this.deduccionesSS = 	Optional.ofNullable(deducciones);
			this.liquidoSS = 		Optional.ofNullable(liquido);
			this.ssEmprSS = 		Optional.ofNullable(ssEmpr);
			this.costeTotalSS = 	Optional.ofNullable(costeTotal);
			this.ssTotalSS = 		Optional.ofNullable(ssTotal);
			this.bonificacionesSS = Optional.ofNullable(bonificaciones);
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
		this.bonificacionesSS = ss_en.getBonificacionesSS();
	}
	
	//GETTERS
	public Optional<String> getEmpleado() {return empleado;}
	public Optional<String> getEmpleadoSS() {return empleadoSS;}

	public Optional<String> getTipo() {return tipo;}
	public Optional<String> getTipoSS() {return tipoSS;}

	public Optional<Double> getDevengado() {return devengado;}
	public Optional<Double> getDevengadoSS() {return devengadoSS;}

	public Optional<Double> getSsTrab() {return ssTrab;}
	public Optional<Double> getSsTrabSS() {return ssTrabSS;}

	public Optional<Double> getIrpf() {return irpf;}
	public Optional<Double> getIrpfSS() {return irpfSS;}

	public Optional<Double> getDeducciones() {return deducciones;}
	public Optional<Double> getDeduccionesSS() {return deduccionesSS;}

	public Optional<Double> getLiquido() {return liquido;}
	public Optional<Double> getLiquidoSS() {return liquidoSS;}

	public Optional<Double> getSsEmpr() {return ssEmpr;}
	public Optional<Double> getSsEmprSS() {return ssEmprSS;}

	public Optional<Double> getCosteTotal() {return costeTotal;}
	public Optional<Double> getCosteTotalSS() {return costeTotalSS;}

	public Optional<Double> getSsTotal() {return ssTotal;}
	public Optional<Double> getSsTotalSS() {return ssTotalSS;}
	
	public Optional<Double> getBonificaciones() {return bonificaciones;}
	public Optional<Double> getBonificacionesSS() {return bonificacionesSS;}
	
	//SETTERS
	public void setEmpleado(Optional<String> empleado) {this.empleado = empleado;}
	public void setEmpleadoSS(Optional<String> empleadoSS) {this.empleadoSS = empleadoSS;}

	public void setTipo(Optional<String> tipo) {this.tipo = tipo;}
	public void setTipoSS(Optional<String> tipoSS) {this.tipoSS = tipoSS;}

	public void setDevengado(Optional<Double> devengado) {this.devengado = devengado;}
	public void setDevengadoSS(Optional<Double> devengadoSS) {this.devengadoSS = devengadoSS;}

	public void setSsTrab(Optional<Double> ssTrab) {this.ssTrab = ssTrab;}
	public void setSsTrabSS(Optional<Double> ssTrabSS) {this.ssTrabSS = ssTrabSS;}

	public void setIrpf(Optional<Double> irpf) {this.irpf = irpf;}
	public void setIrpfSS(Optional<Double> irpfSS) {this.irpfSS = irpfSS;}

	public void setDeducciones(Optional<Double> deducciones) {this.deducciones = deducciones;}
	public void setDeduccionesSS(Optional<Double> deduccionesSS) {this.deduccionesSS = deduccionesSS;}

	public void setLiquido(Optional<Double> liquido) {this.liquido = liquido;}
	public void setLiquidoSS(Optional<Double> liquidoSS) {this.liquidoSS = liquidoSS;}

	public void setSsEmpr(Optional<Double> ssEmpr) {this.ssEmpr = ssEmpr;}
	public void setSsEmprSS(Optional<Double> ssEmprSS) {this.ssEmprSS = ssEmprSS;}

	public void setCosteTotal(Optional<Double> costeTotal) {this.costeTotal = costeTotal;}
	public void setCosteTotalSS(Optional<Double> costeTotalSS) {this.costeTotalSS = costeTotalSS;}

	public void setSsTotal(Optional<Double> ssTotal) {this.ssTotal = ssTotal;}
	public void setSsTotalSS(Optional<Double> ssTotalSS) {this.ssTotalSS = ssTotalSS;}

	public void setBonificaciones(Optional<Double> bonificaciones) {this.bonificaciones = bonificaciones;}
	public void setBonificacionesSS(Optional<Double> bonificacionesSS) {this.bonificacionesSS = bonificacionesSS;}
	

	public boolean Has_ss(){
		return empleadoSS.isPresent() || tipoSS.isPresent() || devengadoSS.isPresent() 
				|| ssTrabSS.isPresent() || irpfSS.isPresent() || deduccionesSS.isPresent()
				|| liquidoSS.isPresent() || ssEmprSS.isPresent() || ssTotalSS.isPresent()
				|| bonificacionesSS.isPresent();
	}

	public boolean Has_aon(){
		return empleado.isPresent() || tipo.isPresent() || devengado.isPresent() 
				|| ssTrab.isPresent() || irpf.isPresent() || deducciones.isPresent()
				|| liquido.isPresent() || ssEmpr.isPresent() || ssTotal.isPresent()
				|| bonificaciones.isPresent();
	}



	@Override
	public String toString() {
		return "EnterprisePayrollEntry :\t\n{ \n\templeado: \t\t" + empleado + ", \n\ttipo: \t\t" + tipo
				+ ", \n\tdevengado: \t\t" + devengado + ", \n\tssTrab: \t\t" + ssTrab + ", \n\tirpf: \t\t" + irpf
				+ ", \n\tdeducciones: \t\t" + deducciones + ", \n\tliquido: \t\t" + liquido + ", \n\tssEmpr: \t\t"
				+ ssEmpr + ", \n\tcosteTotal: \t\t" + costeTotal + ", \n\tssTotal: \t\t" + ssTotal
				+ ", \n\tbonificaciones: \t\t" + bonificaciones + ", \n\templeadoSS: \t\t" + empleadoSS
				+ ", \n\ttipoSS: \t\t" + tipoSS + ", \n\tdevengadoSS: \t\t" + devengadoSS + ", \n\tssTrabSS: \t\t"
				+ ssTrabSS + ", \n\tirpfSS: \t\t" + irpfSS + ", \n\tdeduccionesSS: \t\t" + deduccionesSS
				+ ", \n\tliquidoSS: \t\t" + liquidoSS + ", \n\tssEmprSS: \t\t" + ssEmprSS + ", \n\tcosteTotalSS: \t\t"
				+ costeTotalSS + ", \n\tssTotalSS: \t\t" + ssTotalSS + ", \n\tbonificacionesSS: \t\t" + bonificacionesSS
				+ "\n}";
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

