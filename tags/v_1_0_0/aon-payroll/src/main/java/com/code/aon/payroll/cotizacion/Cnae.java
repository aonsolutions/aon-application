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
 * Elemento de Cotización.
 */
@Entity
@Table(name="lincnae")
public class Cnae  implements ITransferObject {

     private CnaePK id;
     private Date fecfin;
     private BigDecimal pctit;
     private BigDecimal pctims;
     private BigDecimal pcttotal;
     private CnaeMaestro cnaeMaestro;

    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=5) ), 
        @AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ) } )
    public CnaePK getId() {
        return this.id;
    }
    
    public void setId(CnaePK id) {
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
    public CnaeMaestro getCnaeMaestro() {
        return this.cnaeMaestro;
    }
    
    public void setCnaeMaestro(CnaeMaestro maestro) {
        this.cnaeMaestro = maestro;
    }


}