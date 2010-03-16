package com.code.aon.payroll.irpf;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;

/**
 * I.R.P.F.: Exclusión de la Obligación de Retener
 * 
 * @author eagirrezabal
 *
 */

@Entity
@Table(name="exclusion")
public class Exclusion  implements ITransferObject {

     private ExclusionPK id;
     private Date fecfin;
     private BigDecimal importe;

    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ), 
        @AttributeOverride(name="situacion", column=@Column(name="situacion", nullable=false, length=1) ), 
        @AttributeOverride(name="hijos", column=@Column(name="hijos", nullable=false, length=25) ) } )
    public ExclusionPK getId() {
        return this.id;
    }
    
    public void setId(ExclusionPK id) {
        this.id = id;
    }
    
    @Temporal(TemporalType.DATE)
    @Column(name="fecfin")
    public Date getFecfin() {
        return this.fecfin;
    }
    
    public void setFecfin(Date fecfin) {
        this.fecfin = fecfin;
    }
    
    @Column(name="importe", scale=2, precision=11)
    public BigDecimal getImporte() {
        return this.importe;
    }
    
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }


}
