package com.code.aon.payroll.divisa;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.Divisas;
import com.code.aon.payroll.enumeration.Prorateo;


@Entity
@Table(name="divisa")

public class Divisa  implements  ITransferObject {

     private String cdg;
     private String description;
     private Integer redondeo;
     private Integer mask1;
     private Integer mask2;
    



    @Id     
    @Column(name="cdg", unique=true, nullable=false, length=3)
    public String getCdg() {
        return this.cdg;
    }
    
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }
    

    @Column(name="descripcion", length=25)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String descripcion) {
        this.description = descripcion;
    }
    

    @Column(name="redondeo", length=2)
    public Integer getRedondeo() {
        return this.redondeo;
    }
    
    public void setRedondeo(Integer redondeo) {
        this.redondeo = redondeo;
    }
    
    @Column(name="mask1", length=2)
    public Integer getMask1() {
        return this.mask1;
    }
    
    public void setMask1(Integer mask1) {
        this.mask1 = mask1;
    }
    
	
    @Column(name="mask2", length=2)
    public Integer getMask2() {
        return this.mask2;
    }
    
    public void setMask2(Integer mask2) {
        this.mask2 = mask2;
    }



}


