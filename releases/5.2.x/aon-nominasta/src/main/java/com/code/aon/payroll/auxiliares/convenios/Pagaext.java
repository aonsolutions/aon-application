package com.code.aon.payroll.auxiliares.convenios;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.IndicadorAnio;
import com.code.aon.payroll.enumeration.TipoProrrateo;

/**
 * Pagaext
 */
@Entity
@Table(name = "pagaext")
public class Pagaext implements ITransferObject {

	private PagaextPK id;
    private String perini;
    private IndicadorAnio indini;
    private String perfin;
    private IndicadorAnio indfin;
    private String feccob;
    private TipoProrrateo prorat;
    private Complemento complemento;
    private Convenio convenio;

    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=2) ), 
        @AttributeOverride(name="codcom", column=@Column(name="codcom", nullable=false, length=2) ) } )
    public PagaextPK getId() {
        return this.id;
    }
    
    public void setId(PagaextPK id) {
        this.id = id;
    }
    
    /**
     * Devuelve el Periodo de Devengo Desde (DDMM)
     * @return
     */
    @Column(name="perini", nullable=false, length=4)
    public String getPerini() {
        return this.perini;
    }
    
    public void setPerini(String perini) {
        this.perini = perini;
    }
    
    /**
     * Devuelve el Indicador Anio Desde
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.IndicadorAnio")} )
    @Column(name="indini", nullable=false, length=1)
    public IndicadorAnio getIndini() {
        return this.indini;
    }
    
    public void setIndini(IndicadorAnio indini) {
        this.indini = indini;
        //this.indinienum = (this.indini != null) ? IndicadorAnio.valueOf("IndA" + this.indini) : null;
    }
    
    /**
     * Devuelve el Periodo Devengo Hasta (DDMM)
     * @return
     */
    @Column(name="perfin", nullable=false, length=4)
    public String getPerfin() {
        return this.perfin;
    }
    
    public void setPerfin(String perfin) {
        this.perfin = perfin;
    }
    
    /**
     * Devuelve el Indicador Anio Hasta
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.IndicadorAnio")} )
    @Column(name="indfin", nullable=false, length=1)
    public IndicadorAnio getIndfin() {
        return this.indfin;
    }
    
    public void setIndfin(IndicadorAnio indfin) {
        this.indfin = indfin;
        //this.indfinenum = (this.indfin != null) ? IndicadorAnio.valueOf("IndA" + this.indfin) : null;
    }
    
    /**
     * Devuelve el Fecha de Cobro (DDMM)
     * @return
     */
    @Column(name="feccob", nullable=false, length=4)
    public String getFeccob() {
        return this.feccob;
    }
    
    public void setFeccob(String feccob) {
        this.feccob = feccob;
    }
    
    /**
     * Devuelve el Tipo de Prorrateo
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipoProrrateo")} )
    @Column(name="prorat", nullable=false, length=1)
    public TipoProrrateo getProrat() {
        return this.prorat;
    }
    
    public void setProrat(TipoProrrateo prorat) {
        this.prorat = prorat;
        //this.proratenum = (this.prorat != null) ? TipoProrrateo.valueOf("Tpro" + this.prorat) : null;
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="codcom", insertable=false, updatable=false)
    public Complemento getComplemento() {
        return this.complemento;
    }
    
    public void setComplemento(Complemento complemento) {
        this.complemento = complemento;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public Convenio getConvenio() {
        return this.convenio;
    }
    
    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }
    
    
    
    
    
    
    
    
 // TODO Problemas en la creacion del enumerado a partir de un String.
	private IndicadorAnio indinienum;

	@Transient
	public IndicadorAnio getIndinienum() {
		return indinienum;
	}

	public void setIndinienum(IndicadorAnio indinienum) {
		this.indinienum = indinienum;
		//setIndini((this.indinienum != null) ? this.indinienum.name().substring(4) : null);
	}
	
	
	
	
	
	
	// TODO Problemas en la creacion del enumerado a partir de un String.
	private IndicadorAnio indfinenum;

	@Transient
	public IndicadorAnio getIndfinenum() {
		return indfinenum;
	}

	public void setIndfinenum(IndicadorAnio indfinenum) {
		this.indfinenum = indfinenum;
		//setIndfin((this.indfinenum != null) ? this.indfinenum.name().substring(4) : null);
	}
	
	
	
	
	// TODO Problemas en la creacion del enumerado a partir de un String.
	private TipoProrrateo proratenum;

	@Transient
	public TipoProrrateo getProratenum() {
		return proratenum;
	}

	public void setProratenum(TipoProrrateo proratenum) {
		this.proratenum = proratenum;
		//setProrat((this.proratenum != null) ? this.proratenum.name().substring(4) : null);
	}
	
	

}
