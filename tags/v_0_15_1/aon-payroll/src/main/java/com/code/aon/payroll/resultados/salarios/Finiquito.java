package com.code.aon.payroll.resultados.salarios;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.principales.persona.Trabajador;

/**
 * Finiquito
 */
@Entity
@Table(name = "finiquito")
public class Finiquito implements ITransferObject {

	private Integer cdg;
	private Date fecbaj;
	private String causa;
	private Date vacfecini;
	private BigDecimal vacimporte;
	private BigDecimal totalConceptos;
	private BigDecimal base;
	private BigDecimal irpf;
	private BigDecimal importeIrpf;
	private BigDecimal liquido;
	private BigDecimal importesin;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private String simula;
	private Date feccobreal;
	private Integer diasvac;
	private BigDecimal costessemp;
	private String codbas;
	private BigDecimal basecg;
	private BigDecimal prccg;
	private BigDecimal importecg;
	private BigDecimal baseacc;
	private BigDecimal prcacc;
	private BigDecimal importeacc;
	private Divisa divisa;
	private Trabajador emprper;
	/*
	private List<Finidto> finidtoList = new ArrayList<Finidto>(0);
	private List<Finindem> finindemList = new ArrayList<Finindem>(0);
	private List<Finipext> finipextList = new ArrayList<Finipext>(0);
	*/
	
	/**
	 * Codigo de Finiquito
	 * @return
	 */
	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getCdg() {
		return this.cdg;
	}

	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

	/**
	 * Fecha de Baja
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecbaj", nullable = false)
	public Date getFecbaj() {
		return this.fecbaj;
	}

	public void setFecbaj(Date fecbaj) {
		this.fecbaj = fecbaj;
	}

	/**
	 * Causa de Baja
	 * @return
	 */
	@Column(name = "causa", length = 30)
	public String getCausa() {
		return this.causa;
	}

	public void setCausa(String causa) {
		this.causa = causa;
	}

	/**
	 * Fecha Desde Vacaciones
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "vacfecini")
	public Date getVacfecini() {
		return this.vacfecini;
	}

	public void setVacfecini(Date vacfecini) {
		this.vacfecini = vacfecini;
	}

	/**
	 * Importe Vacaciones
	 * @return
	 */
	@Column(name = "vacimporte", nullable = false, precision = 11)
	public BigDecimal getVacimporte() {
		return this.vacimporte;
	}

	public void setVacimporte(BigDecimal vacimporte) {
		this.vacimporte = vacimporte;
	}

	/**
	 * Total Conceptos Finiquito
	 * @return
	 */
	@Column(name = "total_conceptos", nullable = false, precision = 11)
	public BigDecimal getTotalConceptos() {
		return this.totalConceptos;
	}

	public void setTotalConceptos(BigDecimal totalConceptos) {
		this.totalConceptos = totalConceptos;
	}

	/**
	 * Base I.R.P.F.
	 * @return
	 */
	@Column(name = "base", nullable = false, precision = 11)
	public BigDecimal getBase() {
		return this.base;
	}

	public void setBase(BigDecimal base) {
		this.base = base;
	}

	/**
	 * % I.R.P.F.
	 * @return
	 */
	@Column(name = "irpf", nullable = false, precision = 5)
	public BigDecimal getIrpf() {
		return this.irpf;
	}

	public void setIrpf(BigDecimal irpf) {
		this.irpf = irpf;
	}

	/**
	 * Importe Retenido I.R.P.F.
	 * @return
	 */
	@Column(name = "importe_irpf", nullable = false, precision = 11)
	public BigDecimal getImporteIrpf() {
		return this.importeIrpf;
	}

	public void setImporteIrpf(BigDecimal importeIrpf) {
		this.importeIrpf = importeIrpf;
	}

	/**
	 * Importe Liquido
	 * @return
	 */
	@Column(name = "liquido", nullable = false, precision = 11)
	public BigDecimal getLiquido() {
		return this.liquido;
	}

	public void setLiquido(BigDecimal liquido) {
		this.liquido = liquido;
	}

	/**
	 * Importe Indemnizaciones no sujetas a I.R.P.F.
	 * @return
	 */
	@Column(name = "importesin", nullable = false, precision = 11)
	public BigDecimal getImportesin() {
		return this.importesin;
	}

	public void setImportesin(BigDecimal importesin) {
		this.importesin = importesin;
	}

	/**
	 * Fecha Creacion Fila
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecnew")
	public Date getFecnew() {
		return this.fecnew;
	}

	public void setFecnew(Date fecnew) {
		this.fecnew = fecnew;
	}

	/**
	 * Hora Creacion Fila
	 * @return
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "hornew")
	public Date getHornew() {
		return this.hornew;
	}

	public void setHornew(Date hornew) {
		this.hornew = hornew;
	}

	/**
	 * Fecha Modificacion Fila
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecmod")
	public Date getFecmod() {
		return this.fecmod;
	}

	public void setFecmod(Date fecmod) {
		this.fecmod = fecmod;
	}

	/**
	 * Hora Modificacion Fila
	 * @return
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "hormod")
	public Date getHormod() {
		return this.hormod;
	}

	public void setHormod(Date hormod) {
		this.hormod = hormod;
	}

	/**
	 * Calculo o Simulacion
	 * @return
	 */
	@Column(name = "simula", nullable = false, length = 1)
	public String getSimula() {
		return this.simula;
	}

	public void setSimula(String simula) {
		this.simula = simula;
	}

	/**
	 * Fecha de Cobro Real
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "feccobreal")
	public Date getFeccobreal() {
		return this.feccobreal;
	}

	public void setFeccobreal(Date feccobreal) {
		this.feccobreal = feccobreal;
	}

	/**
	 * Días vacaciones
	 * @return
	 */
	@Column(name = "diasvac", nullable = false, length = 2)
	public Integer getDiasvac() {
		return this.diasvac;
	}

	public void setDiasvac(Integer diasvac) {
		this.diasvac = diasvac;
	}

	/**
	 * Coste S.S. empresa
	 * @return
	 */
	@Column(name = "costessemp", nullable = false, precision = 11)
	public BigDecimal getCostessemp() {
		return this.costessemp;
	}

	public void setCostessemp(BigDecimal costessemp) {
		this.costessemp = costessemp;
	}

	/**
	 * Grupo de tarifa
	 * @return
	 */
	@Column(name = "codbas", length = 2)
	public String getCodbas() {
		return this.codbas;
	}

	public void setCodbas(String codbas) {
		this.codbas = codbas;
	}

	/**
	 * Base Contingencias Generales
	 * @return
	 */
	@Column(name = "basecg", nullable = false, precision = 11)
	public BigDecimal getBasecg() {
		return this.basecg;
	}

	public void setBasecg(BigDecimal basecg) {
		this.basecg = basecg;
	}

	/**
	 * % Contingencias Generales
	 * @return
	 */
	@Column(name = "prccg", nullable = false, precision = 5)
	public BigDecimal getPrccg() {
		return this.prccg;
	}

	public void setPrccg(BigDecimal prccg) {
		this.prccg = prccg;
	}

	/**
	 * Importe Contingencias Generales
	 * @return
	 */
	@Column(name = "importecg", nullable = false, precision = 11)
	public BigDecimal getImportecg() {
		return this.importecg;
	}

	public void setImportecg(BigDecimal importecg) {
		this.importecg = importecg;
	}

	/**
	 * Base Accidente de Trabajo
	 * @return
	 */
	@Column(name = "baseacc", nullable = false, precision = 11)
	public BigDecimal getBaseacc() {
		return this.baseacc;
	}

	public void setBaseacc(BigDecimal baseacc) {
		this.baseacc = baseacc;
	}

	/**
	 * % Accidente de Trabajo
	 * @return
	 */
	@Column(name = "prcacc", nullable = false, precision = 5)
	public BigDecimal getPrcacc() {
		return this.prcacc;
	}

	public void setPrcacc(BigDecimal prcacc) {
		this.prcacc = prcacc;
	}

	/**
	 * Importe Accidente de Trabajo
	 * @return
	 */
	@Column(name = "importeacc", nullable = false, precision = 11)
	public BigDecimal getImporteacc() {
		return this.importeacc;
	}

	public void setImporteacc(BigDecimal importeacc) {
		this.importeacc = importeacc;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "divisa")
	public Divisa getDivisa() {
		return this.divisa;
	}

	public void setDivisa(Divisa divisa) {
		this.divisa = divisa;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codper")
	public Trabajador getEmprper() {
		return this.emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}

	/*
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "finiquito")
	public List<Finidto> getFinidtoList() {
		return this.finidtoList;
	}

	public void setFinidtoList(List<Finidto> finidtoList) {
		this.finidtoList = finidtoList;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "finiquito")
	public List<Finindem> getFinindemList() {
		return this.finindemList;
	}

	public void setFinindemList(List<Finindem> finindemList) {
		this.finindemList = finindemList;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "finiquito")
	public List<Finipext> getFinipextList() {
		return this.finipextList;
	}

	public void setFinipextList(List<Finipext> finipextList) {
		this.finipextList = finipextList;
	}
	*/

}
