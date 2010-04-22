package com.code.aon.payroll.auxiliares.organismosyentidades;


import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class SucursalId  implements java.io.Serializable {

     private String codent;
     private String cdg;



	
    @Column(name="codent", nullable=false, length=4)
    public String getCodent() {
        return this.codent;
    }
    
    public void setCodent(String codent) {
        this.codent = codent;
    }

	
    @Column(name="cdg", nullable=false, length=4)
    public String getCdg() {
        return this.cdg;
    }
    
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }



}


