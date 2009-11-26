package com.code.aon.payroll.auxiliares.convenios;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.enumeration.IndiceComplemento;
import com.code.aon.payroll.enumeration.Retribuciones;
import com.code.aon.payroll.enumeration.TipoComplemento;
import com.code.aon.payroll.enumeration.TipoCotizaciones;

/**
 * Complementos
 */
@Entity
@Table(name="complemento")
public class Complemento  implements ITransferObject {
	
     private String cdg;
     private String tipcot;
     private String description;
     private String desabr;
     private String tipcom;
     private String fijovar;
     private String indcom;
     private String dinesp;
    

    @Id     
    @Column(name="cdg", unique=true, nullable=false, length=2)
    public String getCdg() {
        return this.cdg;
    }
    
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }
    
    /**
     * Devuelve el Tipo de Cotizacion
     * 
     * @return
     */
    @Column(name="tipcot", length=1)
    public String getTipcot() {
        return this.tipcot;
    }
    
    public void setTipcot(String tipcot) {
        this.tipcot = tipcot;
        this.tipcotenum = ( this.tipcot != null)? TipoCotizaciones.valueOf( "Cot" + this.tipcot ): null;
    }
    
    /**
     * Devuelve la Descripcion de Complemento
     * 
     * @return
     */
    @Column(name="descripcion", length=50)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Devuelve la Descripcion Abreviada de Complemento
     * @return
     */
    @Column(name="desabr", length=15)
    public String getDesabr() {
        return this.desabr;
    }
    
    public void setDesabr(String desabr) {
        this.desabr = desabr;
    }
    
    /**
     * Devuelve el Tipo de Complemento
     * 
     * @return
     */
    @Column(name="tipcom", nullable=false, length=1)
    public String getTipcom() {
        return this.tipcom;
    }
    
    public void setTipcom(String tipcom) {
        this.tipcom = tipcom;
        this.tipcomenum = ( this.tipcom != null)? TipoComplemento.valueOf( "Com" + this.tipcom ): null;
    }
    
    /**
     * Devuelve si es Fijo o Variable
     * 
     * @return
     */
    @Column(name="fijovar", length=1)
    public String getFijovar() {
        return this.fijovar;
    }
    
    public void setFijovar(String fijovar) {
        this.fijovar = fijovar;
    }
    
    /**
     * Devuelve el Indicador de Complemento
     * 
     * @return
     */
    @Column(name="indcom", length=1)
    public String getIndcom() {
        return this.indcom;
    }
    
    public void setIndcom(String indcom) {
        this.indcom = indcom;
    }
    
    /**
     * Devuelve si es Dinerario o en Especie
     * @return
     */
    @Column(name="dinesp", nullable=false, length=1)
    public String getDinesp() {
        return this.dinesp;
    }
    
    public void setDinesp(String dinesp) {
        this.dinesp = dinesp;
        this.retribuciones = ( this.dinesp != null)? Retribuciones.valueOf( "retrib" + this.dinesp ): null;
    }
    
    
  //TODO Problemas en la creacion del enumerado a partir de un String.
	private TipoCotizaciones tipcotenum;
	@Transient 
	public TipoCotizaciones getTipcotenum() {
		return tipcotenum;
	}
	public void setTipcotenum(TipoCotizaciones tipcotenum) {
		this.tipcotenum = tipcotenum;
		setTipcot( (this.tipcotenum != null)? this.tipcotenum.name().substring( 3 ) : null );
	}
	
	//TODO Problemas en la creacion del enumerado a partir de un String.
	private TipoComplemento tipcomenum;
	@Transient 
	public TipoComplemento getTipcomenum() {
		return tipcomenum;
	}
	public void setTipcomenum(TipoComplemento tipcomenum) {
		this.tipcomenum = tipcomenum;
		setTipcom( (this.tipcomenum != null)? this.tipcomenum.name().substring( 3 ) : null );
	}
	
	//TODO Problemas en la creacion del enumerado a partir de un String.
	private Retribuciones retribuciones;
	@Transient 
	public Retribuciones getRetribuciones() {
		return retribuciones;
	}
	public void setRetribuciones(Retribuciones retribuciones) {
		this.retribuciones = retribuciones;
		setDinesp( (this.retribuciones != null)? this.retribuciones.name().substring( 6 ) : null );
	}
	
	//TODO Problemas en la creacion del enumerado a partir de un String.
	private FijoVariable fijoVariable;
	@Transient 
	public FijoVariable getFijoVariable() {
		return fijoVariable;
	}
	public void setFijoVariable(FijoVariable fijoVariable) {
		this.fijoVariable = fijoVariable;
		setFijovar( (this.fijoVariable != null)? this.fijoVariable.name().substring( 7 ) : null );
	}
	
	//TODO Problemas en la creacion del enumerado a partir de un String.
	private IndiceComplemento indComplemento;
	@Transient 
	public IndiceComplemento getIndiceComplemento() {
		return indComplemento;
	}
	public void setIndiceComplemento(IndiceComplemento indComplemento) {
		this.indComplemento = indComplemento;
		setIndcom( (this.indComplemento != null)? this.indComplemento.name().substring( 7 ) : null );
	}

}


