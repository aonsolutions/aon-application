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

import com.code.aon.common.ITransferObject;

/**
 * Elemento de Cotización.
 */
@Entity
@Table(name="linelem")
public class Elemento  implements ITransferObject {

     private ElementoPK id;
     private Date fecfin;
     private BigDecimal dato1;
     private BigDecimal dato2;
     private ElementoMaestro maestro;


    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=8) ), 
        @AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ) } )
    public ElementoPK getId() {
        return this.id;
    }
    
    public void setId(ElementoPK id) {
        this.id = id;
    }
    
    @Column(name="fecfin", nullable=false, length=10)
    public Date getFecfin() {
        return this.fecfin;
    }
    
    public void setFecfin(Date fecfin) {
        this.fecfin = fecfin;
    }
    
	/**
	 * Dato Principal
	 * 
	 * @return
	 */
    @Column(name="dato1", nullable=false, precision=8)
    public BigDecimal getDato1() {
        return this.dato1;
    }
    
    public void setDato1(BigDecimal dato1) {
        this.dato1 = dato1;
    }
    
    /**
	 * Dato Secundario
	 * 
	 * @return
	 */
    @Column(name="dato2", nullable=false, precision=8)
    public BigDecimal getDato2() {
        return this.dato2;
    }
    
    public void setDato2(BigDecimal dato2) {
        this.dato2 = dato2;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public ElementoMaestro getElementoMaestro() {
        return this.maestro;
    }
    
    public void setElementoMaestro(ElementoMaestro maestro) {
        this.maestro = maestro;
    }


}


