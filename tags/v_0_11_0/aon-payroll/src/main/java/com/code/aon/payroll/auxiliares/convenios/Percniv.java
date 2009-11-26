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

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Epigrafe;

/**
 * Percepciones
 */
@Entity
@Table(name = "percniv")
public class Percniv implements ITransferObject {

	private PercnivPK id;
    private String descom;
    private String desabr;
    private String tipcot;
    private String calculo;
    private int mes;
    private BigDecimal unidades;
    private BigDecimal impuni;
    private BigDecimal importe;
    private BigDecimal garilt;
    private String redext;
    private String fijovar;
    private Date fecnew;
    private Date hornew;
    private Date fecmod;
    private Date hormod;
    private String indcom;
    private String tipcom;
    private String dinesp;
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
    @Column(name="tipcot", nullable=false, length=1)
    public String getTipcot() {
        return this.tipcot;
    }
    
    public void setTipcot(String tipcot) {
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
    @Column(name="redext", length=1)
    public String getRedext() {
        return this.redext;
    }
    
    public void setRedext(String redext) {
        this.redext = redext;
    }
    
    /**
     * Devuelve Fijo o Variable
     * @return
     */
    @Column(name="fijovar", nullable=false, length=1)
    public String getFijovar() {
        return this.fijovar;
    }
    
    public void setFijovar(String fijovar) {
        this.fijovar = fijovar;
    }
    
    /**
     * Devuelve la Fecha Creacion Fila
     * @return
     */
    @Temporal(TemporalType.DATE)
    @Column(name="fecnew", length=10)
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
    @Column(name="hornew", length=8)
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
    @Column(name="fecmod", length=10)
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
    @Column(name="hormod", length=8)
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
    @Column(name="indcom", length=1)
    public String getIndcom() {
        return this.indcom;
    }
    
    public void setIndcom(String indcom) {
        this.indcom = indcom;
    }
    
    /**
     * Devuelve el Tipo de Complemento
     * @return
     */
    @Column(name="tipcom", length=1)
    public String getTipcom() {
        return this.tipcom;
    }
    
    public void setTipcom(String tipcom) {
        this.tipcom = tipcom;
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
    }
    
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumns( { 
        @JoinColumn(name="cdg", referencedColumnName="codcon", insertable=false, updatable=false), 
        @JoinColumn(name="nivel", referencedColumnName="cdg", insertable=false, updatable=false) } )
    public Nivel getNivel() {
        return this.nivel;
    }
    
    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="codcomapl")
    public Complemento getComplemento() {
        return this.complemento;
    }
    
    public void setComplemento(Complemento complemento) {
        this.complemento = complemento;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="codcom", insertable=false, updatable=false)
    public Complemento getComplemento1() {
        return this.complemento1;
    }
    
    public void setComplemento1(Complemento complemento1) {
        this.complemento1 = complemento1;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cdg", insertable=false, updatable=false)
    public Convenio getConvenio() {
        return this.convenio;
    }
    
    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

}
