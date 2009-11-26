package com.code.aon.payroll.cotizacion;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;


/**
 * Tipos de Bonificacion.
 * 
 * @author alatorre
 *
 */
@Entity
@Table(name="tipboni")
public class Bonificacion  implements ITransferObject {

     private int cdg;
     private String description;
     private String calculo;
     private BigDecimal prcCg;
     private BigDecimal prcAcc;
     private BigDecimal prcAccfgs;
     private String boniss;
     private String mayor60;
     private String rdl052006;
     private String restait;
     

   
     /**
      * Devuelve el codigo del tipo de bonificacion.
      * 
      * @return
      */
    @Id     
	@Column(name="cdg", unique=true, nullable=false, length=2)
    public int getCdg() {
        return this.cdg;
    }
    
    public void setCdg(int cdg) {
        this.cdg = cdg;
    }
    
    /**
     * Devuelve la descripcion del tipo de bonificacion.
     * 
     * @return
     */
	@Column(name="descripcion", nullable=false, length=50)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String descripcion) {
        this.description = descripcion;
    }
    
    /**
     * Devuelve la forma de calculo del tipo de bonificacion.
     * 
     * @return
     */
	@Column(name="calculo", length=1)
    public String getCalculo() {
        return this.calculo;
    }
    
    public void setCalculo(String calculo) {
        this.calculo = calculo;
    }
    
    /**
     * Devuelve el % de bonificacion contingencias generales del tipo bonificacion.
     * 
     * @return
     */
	@Column(name="prc_cg", nullable=false, precision=5)
    public BigDecimal getPrcCg() {
        return this.prcCg;
    }
    
    public void setPrcCg(BigDecimal prcCg) {
        this.prcCg = prcCg;
    }
    
    /**
     * Devuelve el % de bonificacion accidentes del tipo bonificacion.
     * 
     * @return
     */
	@Column(name="prc_acc", nullable=false, precision=5)
    public BigDecimal getPrcAcc() {
        return this.prcAcc;
    }
    
    public void setPrcAcc(BigDecimal prcAcc) {
        this.prcAcc = prcAcc;
    }
    
    /**
     * Devuelve el % de bonificacion base conjunto del tipo bonificacion.
     * 
     * @return
     */
	@Column(name="prc_accfgs", nullable=false, precision=5)
    public BigDecimal getPrcAccfgs() {
        return this.prcAccfgs;
    }
    
    public void setPrcAccfgs(BigDecimal prcAccfgs) {
        this.prcAccfgs = prcAccfgs;
    }
    
    /**
     * Devuelve el % de bonificacion S.S del tipo bonificacion.
     * 
     * @return
     */
	@Column(name="boniss", length=1)
    public String getBoniss() {
        return this.boniss;
    }
    
    public void setBoniss(String boniss) {
        this.boniss = boniss;
    }
    
    /**
     * Indica si es mayor de 60 años y tiene mas de 5 años de antiguedad
     * 
     * @return
     */
	@Column(name="mayor60", length=1)
    public String getMayor60() {
        return this.mayor60;
    }
    
    public void setMayor60(String mayor60) {
        this.mayor60 = mayor60;
    }
    
    /**
     * Devuelve Real Decreto Ley 5/2006 del tipo bonificacion
     * 
     * @return
     */
	@Column(name="rdl052006", length=1)
    public String getRdl052006() {
        return this.rdl052006;
    }
    
    public void setRdl052006(String rdl052006) {
        this.rdl052006 = rdl052006;
    }
    
    /**
     * Indica si hay que restar I.T.
     * 
     * @return
     */
	@Column(name="restait", length=1)
    public String getRestait() {
        return this.restait;
    }
    
    public void setRestait(String restait) {
        this.restait = restait;
    }

//TODO A la espera de implementar un SelectBooleanCheckboxRenderer.
	@Transient 
	public Boolean getBonissbol() {
		return (getBoniss() != null && getBoniss().equals("S")?true:false );
	}
	public void setBonissbol(Boolean bol) {
		setBoniss( (bol!=null && bol)? "S":"N" );
	}

	@Transient 
	public Boolean getMayor60bol() {
		return (getMayor60() != null && getMayor60().equals("S")?true:false );
	}
	public void setMayor60bol(Boolean bol) {
		setMayor60( (bol!=null && bol)? "S":"N" );
	}

	@Transient 
	public Boolean getRdl052006bol() {
		return (getRdl052006() != null && getRdl052006().equals("S")?true:false );
	}
	public void setRdl052006bol(Boolean bol) {
		setRdl052006( (bol!=null && bol)? "S":"N" );
	}

	@Transient 
	public Boolean getRestaitbol() {
		return (getRestait() != null && getRestait().equals("S")?true:false );
	}
	public void setRestaitbol(Boolean bol) {
		setRestait( (bol!=null && bol)? "S":"N" );
	}
//	******************************************************************
}