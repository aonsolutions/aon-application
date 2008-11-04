package com.code.aon.payroll.irpf;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *  Clave primaria de Exclusión de la Obligación de Retener.
 * 
 * @author eagirrezabal
 *
 */

@Embeddable
public class ExclusionPK implements Serializable{
	
	 private Date fecini;
     private String situacion;
     private String hijos;
		
	@Temporal(TemporalType.DATE)
	@Column(name="fecini", nullable=false, length=10)
	public Date getFecini() {
		return fecini;
	}
	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}
	
    @Column(name="situacion", nullable=false, length=1)
    public String getSituacion() {
        return this.situacion;
    }
    
    public void setSituacion(String situacion) {
        this.situacion = situacion;
    }

    @Column(name="hijos", nullable=false, length=25)
    public String getHijos() {
        return this.hijos;
    }
    
    public void setHijos(String hijos) {
        this.hijos = hijos;
    }
	
	
}	
