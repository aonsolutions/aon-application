package com.code.aon.payroll.cotizacion;

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

/**
 * Cnae 2009
 */
@Entity
@Table(name="lincnae2009")
public class Cnae2009  implements ITransferObject {

     private Cnae2009PK id;
     private Date fecfin;
     private BigDecimal pctit;
     private BigDecimal pctims;
     private BigDecimal pcttotal;
     private Cnae2009Maestro cnae2009Maestro;

    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=5) ), 
        @AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ) } )
    public Cnae2009PK getId() {
        return this.id;
    }
    
    public void setId(Cnae2009PK id) {
        this.id = id;
    }
    
    @Temporal(TemporalType.DATE)
    @Column(name="fecfin", nullable=false)
    public Date getFecfin() {
        return this.fecfin;
    }
    
    public void setFecfin(Date fecfin) {
        this.fecfin = fecfin;
    }
    
    @Column(name="pctit", precision=5)
    public BigDecimal getPctit() {
        return this.pctit;
    }
    
    public void setPctit(BigDecimal pctit) {
        this.pctit = pctit;
    }
    
    @Column(name="pctims", precision=5)
    public BigDecimal getPctims() {
        return this.pctims;
    }
    
    public void setPctims(BigDecimal pctims) {
        this.pctims = pctims;
    }
    
    @Column(name="pcttotal", precision=5)
    public BigDecimal getPcttotal() {
        return this.pcttotal;
    }
    
    public void setPcttotal(BigDecimal pcttotal) {
        this.pcttotal = pcttotal;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public Cnae2009Maestro getCnae2009Maestro() {
        return this.cnae2009Maestro;
    }
    
    public void setCnae2009Maestro(Cnae2009Maestro maestro) {
        this.cnae2009Maestro = maestro;
    }


}
