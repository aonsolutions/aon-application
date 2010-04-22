package com.code.aon.ui.payroll.wizard;

import java.util.Date;

import com.code.aon.payroll.enumeration.ItStatus;
import com.code.aon.payroll.principales.personas.Parteit;

public class ParteRen {

     private String numcol;
     private String cias;
     private Date fecconf;
     private String parproc;
     private Parteit parteit;
     private ItStatus status;

    public String getNumcol() {
        return this.numcol;
    }
    
    public void setNumcol(String numcol) {
        this.numcol = numcol;
    }
    
	public String getCias() {
        return this.cias;
    }
    
    public void setCias(String cias) {
        this.cias = cias;
    }
    public Date getFecconf() {
        return this.fecconf;
    }
    
    public void setFecconf(Date fecconf) {
        this.fecconf = fecconf;
    }
    
    public String getParproc() {
        return this.parproc;
    }
    
    public void setParproc(String parproc) {
        this.parproc = parproc;
    }
    
    public Parteit getParteit() {
        return this.parteit;
    }
    
    public void setParteit(Parteit parteit) {
        this.parteit = parteit;
    }

    public ItStatus getStatus() {
    	return this.status;
    }

    public void setStatus(ItStatus status) {
		this.status = status;
	}
    

}


