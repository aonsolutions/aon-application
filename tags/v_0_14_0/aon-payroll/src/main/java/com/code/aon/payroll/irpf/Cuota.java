package com.code.aon.payroll.irpf;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
//import com.transtools.expand.annotation.DataDefinition;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * I.R.P.F.: Cuotas de Retención
 * 
 * @author eagirrezabal
 *
 */

@Entity
@Table(name="cuota")
public class Cuota implements ITransferObject {

  private CuotaPK id;
  private Date fecfin;
  private BigDecimal hasta;
  private BigDecimal pesetas;
  private BigDecimal resto;
  private BigDecimal porcentaje;



 @EmbeddedId    
 @AttributeOverrides( {
     @AttributeOverride(name="numTramo", column=@Column(name="num_tramo", nullable=false, length=2) ), 
     @AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ) } )
 public CuotaPK getId() {
     return this.id;
 }
 
 public void setId(CuotaPK id) {
     this.id = id;
 }
 
 @Temporal(TemporalType.DATE)
 @Column(name="fecfin", length=10)
 public Date getFecfin() {
     return this.fecfin;
 }
 
 public void setFecfin(Date fecfin) {
     this.fecfin = fecfin;
 }
 
 @Column(name="hasta", nullable=false, precision=11)
 public BigDecimal getHasta() {
     return this.hasta;
 }
 
 public void setHasta(BigDecimal hasta) {
     this.hasta = hasta;
 }
 
 @Column(name="pesetas", precision=11)
 public BigDecimal getPesetas() {
     return this.pesetas;
 }
 
 public void setPesetas(BigDecimal pesetas) {
     this.pesetas = pesetas;
 }
 
 @Column(name="resto", precision=11)
 public BigDecimal getResto() {
     return this.resto;
 }
 
 public void setResto(BigDecimal resto) {
     this.resto = resto;
 }
 
 @Column(name="porcentaje", precision=5)
 public BigDecimal getPorcentaje() {
     return this.porcentaje;
 }
 
 public void setPorcentaje(BigDecimal porcentaje) {
     this.porcentaje = porcentaje;
 }


}




