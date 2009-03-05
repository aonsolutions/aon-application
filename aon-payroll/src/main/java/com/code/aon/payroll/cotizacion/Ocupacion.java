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
@Table(name="linocupacion")
public class Ocupacion  implements ITransferObject {

     private OcupacionPK id;
     private Date fecfin;
     private BigDecimal pctit;
     private BigDecimal pctims;
     private BigDecimal pcttotal;
     private OcupacionMaestro ocupacionMaestro;

    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=1) ), 
        @AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ) } )
    public OcupacionPK getId() {
        return this.id;
    }
    
    public void setId(OcupacionPK id) {
        this.id = id;
    }
    
    /**
     * Devuelve la fecha fin de vigencia
     * @return
     */
    @Temporal(TemporalType.DATE)
    @Column(name="fecfin", nullable=false)
    public Date getFecfin() {
        return this.fecfin;
    }
    
    public void setFecfin(Date fecfin) {
        this.fecfin = fecfin;
    }
    
    /**
     * Devuelve el % I.T.
     * @return
     */
    @Column(name="pctit", precision=5)
    public BigDecimal getPctit() {
        return this.pctit;
    }
    
    public void setPctit(BigDecimal pctit) {
        this.pctit = pctit;
    }
    
    /**
     * Devuelve el % I.M.S.
     * @return
     */
    @Column(name="pctims", precision=5)
    public BigDecimal getPctims() {
        return this.pctims;
    }
    
    public void setPctims(BigDecimal pctims) {
        this.pctims = pctims;
    }
    
    /**
     * Devuelve el % Total
     * @return
     */
    @Column(name="pcttotal", precision=5)
    public BigDecimal getPcttotal() {
        return this.pcttotal;
    }
    
    public void setPcttotal(BigDecimal pcttotal) {
        this.pcttotal = pcttotal;
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public OcupacionMaestro getOcupacionMaestro() {
        return this.ocupacionMaestro;
    }
    
    public void setOcupacionMaestro(OcupacionMaestro maestro) {
        this.ocupacionMaestro = maestro;
    }
    
}


