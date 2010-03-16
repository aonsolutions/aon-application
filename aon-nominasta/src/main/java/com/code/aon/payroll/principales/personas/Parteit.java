package com.code.aon.payroll.principales.personas;

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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.enumeration.Tipoit;
import com.code.aon.payroll.principales.persona.Trabajador;

/**
 * Mantenimiento de Parteit
 */
@Entity
// @Lines(joinProperty="emprper")
@Table(name = "parteit")
public class Parteit implements ITransferObject {

	private ParteitPK id;
	private String numcolbaj;
	private String ciasbaj;
	private Boolean bajproc;
	private Date fecfin;
	private String numcolalt;
	private String ciasalt;
	private Boolean altproc;
	private Tipoit tipoit;
	private Boolean recaida;
	private Date feciniori;
	private Prorateo proret;
	private BigDecimal baseant;
	private Integer diasant;
	private BigDecimal baseregdia;
	private BigDecimal basediacg;
	private BigDecimal basediaacc;
	private BigDecimal prest60;
	private BigDecimal prest75;
	private Boolean procesado;
	private Boolean riesgo;
	private Trabajador emprper;

	// private List<Parteconf> parteconfList = new ArrayList<Parteconf>(0);

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)) })
	public ParteitPK getId() {
		return this.id;
	}

	public void setId(ParteitPK id) {
		this.id = id;
	}

	// @DataDefinition(label="Número Colegiado Baja",descriptionColumn=true)
	@Column(name = "numcolbaj", length = 8)
	public String getNumcolbaj() {
		return this.numcolbaj;
	}

	public void setNumcolbaj(String numcolbaj) {
		this.numcolbaj = numcolbaj;
	}

	// @DataDefinition(label="C.I.A.S. Baja")
	@Column(name = "ciasbaj", length = 11)
	public String getCiasbaj() {
		return this.ciasbaj;
	}

	public void setCiasbaj(String ciasbaj) {
		this.ciasbaj = ciasbaj;
	}

	// @DataDefinition(label="Baja procesada")
	@Type(type="siNoType" )
	@Column(name = "bajproc", nullable = false, length = 1)
	public Boolean getBajproc() {
		return this.bajproc;
	}

	public void setBajproc(Boolean bajproc) {
		this.bajproc = bajproc;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Fin Incidencia")
	@Column(name = "fecfin")
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	// @DataDefinition(label="Número Colegiado Alta")
	@Column(name = "numcolalt", length = 8)
	public String getNumcolalt() {
		return this.numcolalt;
	}

	public void setNumcolalt(String numcolalt) {
		this.numcolalt = numcolalt;
	}

	// @DataDefinition(label="C.I.A.S. Alta")
	@Column(name = "ciasalt", length = 11)
	public String getCiasalt() {
		return this.ciasalt;
	}

	public void setCiasalt(String ciasalt) {
		this.ciasalt = ciasalt;
	}

	// @DataDefinition(label="Alta procesada")
	@Type(type="siNoType" )
	@Column(name = "altproc", nullable = false, length = 1)
	public Boolean getAltproc() {
		return this.altproc;
	}

	public void setAltproc(Boolean altproc) {
		this.altproc = altproc;
	}

	// @DataDefinition(label="Tipo de I.T.")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Tipoit")} )
	@Column(name = "tipoit", nullable = false, length = 1)
	public Tipoit getTipoit() {
		return this.tipoit;
	}

	public void setTipoit(Tipoit tipoit) {
		this.tipoit = tipoit;
	}

	// @DataDefinition(label="Recaida de Anterior I.T.")
	@Type(type="siNoType" )
	@Column(name = "recaida", length = 1)
	public Boolean getRecaida() {
		return this.recaida;
	}

	public void setRecaida(Boolean recaida) {
		this.recaida = recaida;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Si recaida, Fecha Inicial primera I.T.")
	@Column(name = "feciniori")
	public Date getFeciniori() {
		return this.feciniori;
	}

	public void setFeciniori(Date feciniori) {
		this.feciniori = feciniori;
	}

	// @DataDefinition(label="Prorrateo Cotizacion")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Prorateo")} )
	@Column(name = "proret", nullable = false, length = 1)
	public Prorateo getProret() {
		return this.proret;
	}

	public void setProret(Prorateo proret) {
		this.proret = proret;
	}

	// @DataDefinition(label="Base Retribucion Periodo Anterior")
	@Column(name = "baseant", scale=2, precision=11)
	public BigDecimal getBaseant() {
		return this.baseant;
	}

	public void setBaseant(BigDecimal baseant) {
		this.baseant = baseant;
	}

	// @DataDefinition(label="Dias Periodo Anterior")
	@Column(name = "diasant", length = 2)
	public Integer getDiasant() {
		return this.diasant;
	}

	public void setDiasant(Integer diasant) {
		this.diasant = diasant;
	}

	// @DataDefinition(label="Base Reguladora Diaria")
	@Column(name = "baseregdia", scale=2, precision=11)
	public BigDecimal getBaseregdia() {
		return this.baseregdia;
	}

	public void setBaseregdia(BigDecimal baseregdia) {
		this.baseregdia = baseregdia;
	}

	// @DataDefinition(label="Base Diaria contingencias Generales I.T.")
	@Column(name = "basediacg", scale=2, precision=11)
	public BigDecimal getBasediacg() {
		return this.basediacg;
	}

	public void setBasediacg(BigDecimal basediacg) {
		this.basediacg = basediacg;
	}

	// @DataDefinition(label="Base Diaria Accidentes Trabajo I.T.")
	@Column(name = "basediaacc", scale=2, precision=11)
	public BigDecimal getBasediaacc() {
		return this.basediaacc;
	}

	public void setBasediaacc(BigDecimal basediaacc) {
		this.basediaacc = basediaacc;
	}

	// @DataDefinition(label="Prestacion Diaria 60 %")
	@Column(name = "prest60", scale=2, precision=11)
	public BigDecimal getPrest60() {
		return this.prest60;
	}

	public void setPrest60(BigDecimal prest60) {
		this.prest60 = prest60;
	}

	// @DataDefinition(label="Prestacion Diaria 75 %")
	@Column(name = "prest75", scale=2, precision=11)
	public BigDecimal getPrest75() {
		return this.prest75;
	}

	public void setPrest75(BigDecimal prest75) {
		this.prest75 = prest75;
	}

	// @DataDefinition(label="Parte Procesado (S/N)")
	@Type(type="siNoType" )
	@Column(name = "procesado", length = 1)
	public Boolean getProcesado() {
		return this.procesado;
	}

	public void setProcesado(Boolean procesado) {
		this.procesado = procesado;
	}

	// @DataDefinition(label="Riesgo Embarazo")
	@Type(type="siNoType" )
	@Column(name = "riesgo", length = 1)
	public Boolean getRiesgo() {
		return this.riesgo;
	}

	public void setRiesgo(Boolean riesgo) {
		this.riesgo = riesgo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Trabajador getEmprper() {
		return this.emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}

	/*
	 * @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY,
	 * mappedBy="parteit") public List<Parteconf> getParteconfList() { return
	 * this.parteconfList; }
	 * 
	 * public void setParteconfList(List<Parteconf> parteconfList) {
	 * this.parteconfList = parteconfList; }
	 */

}
