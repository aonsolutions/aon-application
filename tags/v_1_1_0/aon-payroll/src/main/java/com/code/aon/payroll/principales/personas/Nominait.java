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
import com.code.aon.payroll.enumeration.Tipoit;
import com.code.aon.payroll.principales.persona.Trabajador;

/**
 * Mantenimiento de Nominait
 */
@Entity
// @Lines(joinProperty="emprper")
@Table(name = "nominait")
public class Nominait implements ITransferObject {

	private NominaitPK id;
	private Date fecfin;
	private Tipoit tipoit;
	private int diasit;
	private int diasSs;
	private int diasemp;
	private int diasin;
	private int dias60;
	private int dias75;
	private BigDecimal ptsSs;
	private BigDecimal ptsemp;
	private BigDecimal basecon;
	private BigDecimal baseacc;
	private Integer totaldias;
	private String simula;
	private BigDecimal baseconTotal;
	private BigDecimal baseaccTotal;
	private Boolean riesgo;
	private Trabajador emprper;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "feciniit", column = @Column(name = "feciniit", nullable = false, length = 10)),
			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)) })
	public NominaitPK getId() {
		return this.id;
	}

	public void setId(NominaitPK id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Fin Incidencia Periodo Nomina")
	@Column(name = "fecfin", nullable = false)
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
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

	// @DataDefinition(label="Dias I.T.")
	@Column(name = "diasit", nullable = false, length = 2)
	public int getDiasit() {
		return this.diasit;
	}

	public void setDiasit(int diasit) {
		this.diasit = diasit;
	}

	// @DataDefinition(label="Dias Prestacion I.T. Seg.Social")
	@Column(name = "dias_ss", nullable = false, length = 2)
	public int getDiasSs() {
		return this.diasSs;
	}

	public void setDiasSs(int diasSs) {
		this.diasSs = diasSs;
	}

	// @DataDefinition(label="Dias Prestacion I.T. Empresa")
	@Column(name = "diasemp", nullable = false, length = 2)
	public int getDiasemp() {
		return this.diasemp;
	}

	public void setDiasemp(int diasemp) {
		this.diasemp = diasemp;
	}

	// @DataDefinition(label="Dias sin Prestacion Dineraria")
	@Column(name = "diasin", nullable = false, length = 2)
	public int getDiasin() {
		return this.diasin;
	}

	public void setDiasin(int diasin) {
		this.diasin = diasin;
	}

	// @DataDefinition(label="Dias Prestacion 60 % S.S.")
	@Column(name = "dias60", nullable = false, length = 2)
	public int getDias60() {
		return this.dias60;
	}

	public void setDias60(int dias60) {
		this.dias60 = dias60;
	}

	// @DataDefinition(label="Dias Prestacion 75 % S.S.")
	@Column(name = "dias75", nullable = false, length = 2)
	public int getDias75() {
		return this.dias75;
	}

	public void setDias75(int dias75) {
		this.dias75 = dias75;
	}

	// @DataDefinition(label="Importe Prestacion I.T. Seg. Social")
	@Column(name = "pts_ss", nullable = false, scale=2, precision=11)
	public BigDecimal getPtsSs() {
		return this.ptsSs;
	}

	public void setPtsSs(BigDecimal ptsSs) {
		this.ptsSs = ptsSs;
	}

	// @DataDefinition(label="Importe Prestacion I.T. Empresa")
	@Column(name = "ptsemp", nullable = false, scale=2, precision=11)
	public BigDecimal getPtsemp() {
		return this.ptsemp;
	}

	public void setPtsemp(BigDecimal ptsemp) {
		this.ptsemp = ptsemp;
	}

	// @DataDefinition(label="Base Contingencias Generales I.T.")
	@Column(name = "basecon", nullable = false, scale=2, precision=11)
	public BigDecimal getBasecon() {
		return this.basecon;
	}

	public void setBasecon(BigDecimal basecon) {
		this.basecon = basecon;
	}

	// @DataDefinition(label="Base Accidentes Trabajo I.T.")
	@Column(name = "baseacc", nullable = false, scale=2, precision=11)
	public BigDecimal getBaseacc() {
		return this.baseacc;
	}

	public void setBaseacc(BigDecimal baseacc) {
		this.baseacc = baseacc;
	}

	// @DataDefinition(label="Total dias de I.T.")
	@Column(name = "totaldias", length = 2)
	public Integer getTotaldias() {
		return this.totaldias;
	}

	public void setTotaldias(Integer totaldias) {
		this.totaldias = totaldias;
	}

	// @DataDefinition(label="Indicador de registro simulado")
	@Column(name = "simula", length = 1)
	public String getSimula() {
		return this.simula;
	}

	public void setSimula(String simula) {
		this.simula = simula;
	}

	// @DataDefinition(label="Base C.G. Total")
	@Column(name = "basecon_total", nullable = false, scale=2, precision=11)
	public BigDecimal getBaseconTotal() {
		return this.baseconTotal;
	}

	public void setBaseconTotal(BigDecimal baseconTotal) {
		this.baseconTotal = baseconTotal;
	}

	// @DataDefinition(label="Base ACC. Total")
	@Column(name = "baseacc_total", nullable = false, scale=2, precision=11)
	public BigDecimal getBaseaccTotal() {
		return this.baseaccTotal;
	}

	public void setBaseaccTotal(BigDecimal baseaccTotal) {
		this.baseaccTotal = baseaccTotal;
	}

	// @DataDefinition(label="Riesgo Embarazo")
	@Type(type="siNoType")
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

}
