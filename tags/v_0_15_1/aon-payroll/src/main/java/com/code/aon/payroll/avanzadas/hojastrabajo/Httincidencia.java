package com.code.aon.payroll.avanzadas.hojastrabajo;

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
@Table(name="httincidencia")
public class Httincidencia  implements ITransferObject {

     private HttincidenciaId id;
     private Date fecinicio;
     private Date fecfin;
     private Integer cantidad;
     private BigDecimal importe;
     private Httrabajador httrabajador;

  

    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=2) ), 
        @AttributeOverride(name="orden", column=@Column(name="orden", nullable=false, length=2) ), 
        @AttributeOverride(name="tipo", column=@Column(name="tipo", nullable=false, length=10) ) } )
    public HttincidenciaId getId() {
        return this.id;
    }
    
    public void setId(HttincidenciaId id) {
        this.id = id;
    }
    
    
    @Temporal(TemporalType.DATE)
    @Column(name="fecinicio", nullable=false)
    public Date getFecinicio() {
        return this.fecinicio;
    }
    
    public void setFecinicio(Date fecinicio) {
        this.fecinicio = fecinicio;
    }
    
    @Temporal(TemporalType.DATE)
    @Column(name="fecfin", nullable=false)
    public Date getFecfin() {
        return this.fecfin;
    }
    
    public void setFecfin(Date fecfin) {
        this.fecfin = fecfin;
    }
    

    @Column(name="cantidad", nullable=false, length=2)
    public Integer getCantidad() {
        return this.cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    

    @Column(name="importe", precision=8)
    public BigDecimal getImporte() {
        return this.importe;
    }
    
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public Httrabajador getHttrabajador() {
        return this.httrabajador;
    }
    
    public void setHttrabajador(Httrabajador httrabajador) {
        this.httrabajador = httrabajador;
    }


}


