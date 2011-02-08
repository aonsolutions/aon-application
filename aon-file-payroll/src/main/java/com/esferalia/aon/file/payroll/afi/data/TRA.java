package com.esferalia.aon.file.payroll.afi.data;

import java.util.List;

/**
 * trabajador
 */
public class TRA {
	private String numeroAfiliacion;
	private String ipf;
	private AYN ayn;
	private DAP dap;
	private CUE cue;
	private DOM dom;
	private FAB fab;
	private DAM dam;
	private DSC dsc;
	private DRA dra;
	private FCT fct;
	private List<PIT> periodosIT;;
	
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
	public DAP getDap() {
		return dap;
	}
	public void setDap(DAP dap) {
		this.dap = dap;
	}
	public CUE getCue() {
		return cue;
	}
	public void setCue(CUE cue) {
		this.cue = cue;
	}
	public FAB getFab() {
		return fab;
	}
	public void setFab(FAB fab) {
		this.fab = fab;
	}
	public DAM getDam() {
		return dam;
	}
	public void setDam(DAM dam) {
		this.dam = dam;
	}
	public DSC getDsc() {
		return dsc;
	}
	public void setDsc(DSC dsc) {
		this.dsc = dsc;
	}
	public DRA getDra() {
		return dra;
	}
	public void setDra(DRA dra) {
		this.dra = dra;
	}
	public FCT getFct() {
		return fct;
	}
	public void setFct(FCT fct) {
		this.fct = fct;
	}
	public DOM getDom() {
		return dom;
	}
	public void setDom(DOM dom) {
		this.dom = dom;
	}
	
	public List<PIT> getPeriodosIT() {
		return periodosIT;
	}
	public void setPeriodosIT(List<PIT> periodosIT) {
		this.periodosIT = periodosIT;
	}
}
