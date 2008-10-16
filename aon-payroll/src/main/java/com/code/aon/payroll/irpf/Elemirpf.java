package com.code.aon.payroll.irpf;



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



@Entity
@Table(name="elemirpf")
public class Elemirpf  implements  ITransferObject  {

     private String cdg;
     private String description;
     private String porcentaje;
    

 

    @Id     
    @Column(name="cdg", unique=true, nullable=false, length=10)
    public String getCdg() {
        return this.cdg;
    }
    
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }
    
    @Column(name="descripcion", length=60)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String descripcion) {
        this.description = descripcion;
    }
    

    @Column(name="porcentaje", length=1)
    public String getPorcentaje() {
        return this.porcentaje;
    }
    
    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Transient 
	public Boolean getPorcentajebol() {
		return (getPorcentaje() != null && getPorcentaje().equals("S")?true:false );
	}
	public void setPorcentajebol(Boolean bol) {
		setPorcentaje( (bol!=null && bol)? "S":"N" );
	}
	
}


