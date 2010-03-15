package com.code.aon.payroll.resultados.salarios;

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
import com.code.aon.payroll.auxiliares.convenios.Complemento;

/**
 * Finipext 
 */
@Entity
@Table(name="finipext")
public class Finipext implements ITransferObject {

     private FinipextPK id;
     private Date fecfin;
     private BigDecimal importe;
     private String descom;
     private Complemento complemento;
     private Finiquito finiquito;

    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=4) ), 
        @AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ), 
        @AttributeOverride(name="codcom", column=@Column(name="codcom", nullable=false, length=2) ) } )
    public FinipextPK getId() {
        return this.id;
    }
    
    public void setId(FinipextPK id) {
        this.id = id;
    }
    
    /**
     * Fin Devengo Paga Extra
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
	 * Importe Parte Proporcional
	 * @return
	 */
    @Column(name="importe", nullable=false, scale=2, precision=11)
    public BigDecimal getImporte() {
        return this.importe;
    }
    
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
    
	/**
	 * Descripcion Complemento
	 * @return
	 */
    @Column(name="descom", length=50)
    public String getDescom() {
        return this.descom;
    }
    
    public void setDescom(String descom) {
        this.descom = descom;
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="codcom", insertable=false, updatable=false)
    public Complemento getComplemento() {
        return this.complemento;
    }
    
    public void setComplemento(Complemento complemento) {
        this.complemento = complemento;
        if(complemento.getCdg()!=null)this.getId().setCodcom(complemento.getCdg());
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public Finiquito getFiniquito() {
        return this.finiquito;
    }
    
    public void setFiniquito(Finiquito finiquito) {
        this.finiquito = finiquito;
    }


}



