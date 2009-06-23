package com.code.aon.payroll.auxiliares.convenios;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.enumeration.IndiceComplemento;
import com.code.aon.payroll.enumeration.PagaExtra;
import com.code.aon.payroll.enumeration.Retribuciones;
import com.code.aon.payroll.enumeration.TipoComplemento;
import com.code.aon.payroll.enumeration.TipoCotizaciones;

/**
 * Percepciones
 */
@Entity
@Table(name = "percniv")
public class Percniv implements ITransferObject {

	private PercnivPK id;
    private String descom;
    private String desabr;
    private TipoCotizaciones tipcot;
    private String calculo;
    private int mes;
    private BigDecimal unidades;
    private BigDecimal impuni;
    private BigDecimal importe;
    private BigDecimal garilt;
    private PagaExtra redext;
    private FijoVariable fijovar;
    private Date fecnew;
    private Date hornew;
    private Date fecmod;
    private Date hormod;
    private IndiceComplemento indcom;
    private TipoComplemento tipcom;
    private Retribuciones dinesp;
    private Nivel nivel;
    private Complemento complemento;
    private Complemento complemento1;
    private Convenio convenio;

    @EmbeddedId    
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=2) ), 
        @AttributeOverride(name="nivel", column=@Column(name="nivel", nullable=false, length=2) ), 
        @AttributeOverride(name="codcom", column=@Column(name="codcom", nullable=false, length=2) ) } )
    public PercnivPK getId() {
        return this.id;
    }
    
    public void setId(PercnivPK id) {
        this.id = id;
    }
    
    /**
     * Devuelve la Descripcion de Complemento
     * @return
     */
    @Column(name="descom", nullable=false, length=50)
    public String getDescom() {
        return this.descom;
    }
    
    public void setDescom(String descom) {
        this.descom = descom;
    }
    
    /**
     * Devuelve la Descripcion Abreviada de Complemento
     * @return
     */
    @Column(name="desabr", nullable=false, length=15)
    public String getDesabr() {
        return this.desabr;
    }
    
    public void setDesabr(String desabr) {
        this.desabr = desabr;
    }
    
    /**
     * Devuelve el Tipo Cotizacion
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipoCotizaciones")} )
    @Column(name="tipcot", nullable=false, length=1)
    public TipoCotizaciones getTipcot() {
        return this.tipcot;
    }
    
    public void setTipcot(TipoCotizaciones tipcot) {
        this.tipcot = tipcot;
    }
    
    /**
     * Devuelve la Forma de Calculo
     * @return
     */
    @Column(name="calculo", nullable=false, length=1)
    public String getCalculo() {
        return this.calculo;
    }
    
    public void setCalculo(String calculo) {
        this.calculo = calculo;
    }
    
    /**
     * Devuelve el Mes a Aplicar el Complemento ( 0 todos )
     * @return
     */
    @Column(name="mes", nullable=false, length=2)
    public int getMes() {
        return this.mes;
    }
    
    public void setMes(int mes) {
        this.mes = mes;
    }
    
    /**
     * Devuelve las Unidades Complemento
     * @return
     */
    @Column(name="unidades", precision=8)
    public BigDecimal getUnidades() {
        return this.unidades;
    }
    
    public void setUnidades(BigDecimal unidades) {
        this.unidades = unidades;
        if(getImpuni()!=null && getImpuni().intValue()!=0)
        	setImporte(getImpuni().multiply(getUnidades()));
        if(unidades.intValue()==0)
        	setImpuni(unidades);
        	
    }
    
    /**
     * Devuelve el Importe Unitario
     * @return
     */
    @Column(name="impuni", precision=11)
    public BigDecimal getImpuni() {
        return this.impuni;
    }
    
    public void setImpuni(BigDecimal impuni) {
        this.impuni = impuni;
    }
    
    /**
     * Devuelve el Importe Complemento
     * @return
     */
    @Column(name="importe", precision=11)
    public BigDecimal getImporte() {
        return this.importe;
    }
    
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
    
    /**
     * Devuelve el % Garantizado I.L.T.
     * @return
     */
    @Column(name="garilt", precision=5)
    public BigDecimal getGarilt() {
        return this.garilt;
    }
    
    public void setGarilt(BigDecimal garilt) {
        this.garilt = garilt;
    }
    
    /**
     * Devuelve el Redondeo Paga Extra
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.PagaExtra")} )
    @Column(name="redext", length=1)
    public PagaExtra getRedext() {
        return this.redext;
    }
    
    public void setRedext(PagaExtra redext) {
        this.redext = redext;
    }
    
    /**
     * Devuelve Fijo o Variable
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.FijoVariable")} )
    @Column(name="fijovar", length=1)
    public FijoVariable getFijovar() {
        return this.fijovar;
    }
    
    public void setFijovar(FijoVariable fijovar) {
        this.fijovar = fijovar;
    }
    
    /**
     * Devuelve la Fecha Creacion Fila
     * @return
     */
    @Temporal(TemporalType.DATE)
    @Column(name="fecnew")
    public Date getFecnew() {
        return this.fecnew;
    }
    
    public void setFecnew(Date fecnew) {
        this.fecnew = fecnew;
    }
    
    /**
     * Devuelve la Hora Creacion Fila
     * @return
     */
    @Temporal(TemporalType.TIME)
    @Column(name="hornew")
    public Date getHornew() {
        return this.hornew;
    }
    
    public void setHornew(Date hornew) {
        this.hornew = hornew;
    }
    
    /**
     * Devuelve la Fecha Modificacion Fila
     * @return
     */
    @Temporal(TemporalType.DATE)
    @Column(name="fecmod")
    public Date getFecmod() {
        return this.fecmod;
    }
    
    public void setFecmod(Date fecmod) {
        this.fecmod = fecmod;
    }
    
    /**
     * Devuelve la Hora Modificacion Fila
     * @return
     */
    @Temporal(TemporalType.TIME)
    @Column(name="hormod")
    public Date getHormod() {
        return this.hormod;
    }
    
    public void setHormod(Date hormod) {
        this.hormod = hormod;
    }
    
    /**
     * Devuelve el Indicador de Complemento
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.IndiceComplemento")} )
    @Column(name="indcom", length=1)
    public IndiceComplemento getIndcom() {
        return this.indcom;
    }
    
    public void setIndcom(IndiceComplemento indcom) {
        this.indcom = indcom;
        if(indcom == IndiceComplemento.HORAS)
        	disableRedondeo = true;
        else
        	disableRedondeo = false;
    }
    
    /**
     * Devuelve el Tipo de Complemento
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipoComplemento")} )
    @Column(name="tipcom", length=1)
    public TipoComplemento getTipcom() {
        return this.tipcom;
    }
    
    public void setTipcom(TipoComplemento tipcom) {
        this.tipcom = tipcom;
    }
    
    /**
     * Devuelve si es Dinerario o en Especie
     * @return
     */
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Retribuciones")} )
    @Column(name="dinesp", nullable=false, length=1)
    public Retribuciones getDinesp() {
        return this.dinesp;
    }
    
    public void setDinesp(Retribuciones dinesp) {
        this.dinesp = dinesp;
    }
    
    //A la espera de implementar clave primaria compuesta con objeto no primitivos
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumns( { 
        @JoinColumn(name="cdg", referencedColumnName="codcon", insertable=false, updatable=false), 
        @JoinColumn(name="nivel", referencedColumnName="cdg", insertable=false, updatable=false) } )
    public Nivel getNivel() {
        return this.nivel;
    }
    
    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    /**
     * Devuelve el complemento sobre el que se aplica
     * @return
     */
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="codcomapl", insertable=false, updatable=false)
    public Complemento getComplemento() {
        return this.complemento;
    }
    
    public void setComplemento(Complemento complemento) {
        this.complemento = complemento;
       
    }
    
    //A la espera de implementar clave primaria compuesta con objeto no primitivos
    /**
     * Devuelve el complemento
     * @return
     */
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="codcom", insertable=false, updatable=false)
    public Complemento getComplemento1() {
        return this.complemento1;
    }
    
    public void setComplemento1(Complemento complemento1) {
        this.complemento1 = complemento1;
        setDescom(complemento1.getDescription());
        setDesabr(complemento1.getDesabr());
        setTipcom(complemento1.getTipcom());
        setTipcot(complemento1.getTipcot());
        if(complemento1.getDinesp() != null)	
        	setDinesp(complemento1.getDinesp());
        if(complemento1.getFijovar() != null)
        	setFijovar(complemento1.getFijovar());
        setIndcom(complemento1.getIndcom());
    }
    
    //A la espera de implementar clave primaria compuesta con objeto no primitivos
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public Convenio getConvenio() {
        return this.convenio;
    }
    
    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }
    

    // Comprueba si el radio buttom 'horas complementarias' esta seleccionado
    // para habilitar/desabilitar otros radio buttom
    private boolean disableRedondeo;
    
    @Transient
	public boolean isDisableRedondeo() {
		return disableRedondeo;
	}

	public void setDisableRedondeo(boolean disableRedondeo) {
		this.disableRedondeo = disableRedondeo;
	}

}
