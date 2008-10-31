package com.code.aon.payroll.auxiliares.organismosyentidades;



import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;



@Entity
@Table(name="linmutua")
public class Linmutua  implements ITransferObject {

     private LinmutuaId id;
     private Date fecfin;
     private BigDecimal prcacctrab;
     private BigDecimal prcit;
     private Mutua mutua;


    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=3) ), 
        @AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ) } )
    public LinmutuaId getId() {
        return this.id;
    }
    
    public void setId(LinmutuaId id) {
        this.id = id;
    }
  
    
	
    @Column(name="fecfin", length=10)
    public Date getFecfin() {
        return this.fecfin;
    }
    
    public void setFecfin(Date fecfin) {
        this.fecfin = fecfin;
    }
   
    @Column(name="prcacctrab", precision=5)
    public BigDecimal getPrcacctrab() {
        return this.prcacctrab;
    }
    
    public void setPrcacctrab(BigDecimal prcacctrab) {
        this.prcacctrab = prcacctrab;
    }
    
	
    @Column(name="prcit", precision=5)
    public BigDecimal getPrcit() {
        return this.prcit;
    }
    
    public void setPrcit(BigDecimal prcit) {
        this.prcit = prcit;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public Mutua getMutua() {
        return this.mutua;
    }
    
    public void setMutua(Mutua mutua) {
        this.mutua = mutua;
    }


}


