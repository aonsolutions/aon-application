package com.esferalia.aon.file.payroll.fan.data;

import java.util.List;

public class TRA {
	private String numeroAfiliacion;
	private String ipf;
	private AYN ayn;

	private List<DAT> dat;
	
	public String getNumeroAfiliacion() {
		return numeroAfiliacion;
	}
	public void setNumeroAfiliacion(String numeroAfiliacion) {
		this.numeroAfiliacion = numeroAfiliacion;
	}
	
	public String getIpf() {
		return ipf;
	}
	public void setIpf(String ipf) {
		this.ipf = ipf;
	}
	public AYN getAyn() {
		return ayn;
	}
	public void setAyn(AYN ayn) {
		this.ayn = ayn;
	}
	public List<DAT> getDat() {
		return dat;
	}
	public void setDat(List<DAT> dat) {
		this.dat = dat;
	}
	
}
