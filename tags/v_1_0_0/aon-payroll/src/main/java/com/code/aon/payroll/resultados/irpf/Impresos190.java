package com.code.aon.payroll.resultados.irpf;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.auxiliares.Admon;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.empresa.Empresa;

/**
 * Impresos190
 */
@Entity
@Table(name = "impr190")
public class Impresos190 implements ITransferObject {

	private Integer cdg;
	private Integer anio;
	private Integer numPercep;
	private BigDecimal impPercep;
	private BigDecimal impRetenc;
	private Date fecha;
	private String disco;
	private Boolean descuadrado;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private Admon admon;
	private Empresa emprnif;
	private Empresa emprnif1;
	private Divisa divisa;
	private Provincia provincia;
	private Empresa emprnif2;
	
	
	private Set<LinImpresos190> lineasImpresos190 = new HashSet<LinImpresos190>();  

	/**
	 * Codigo de 190
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
	 * Anio de Devengo
	 * 
	 * @return
	 */
	@Column(name = "anio", length = 2)
	public Integer getAnio() {
		return this.anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	/**
	 * Numero Perceptores
	 * 
	 * @return
	 */
	@Column(name = "num_percep", length = 2)
	public Integer getNumPercep() {
		return this.numPercep;
	}

	public void setNumPercep(Integer numPercep) {
		this.numPercep = numPercep;
	}

	/**
	 * Importe Percepciones
	 * 
	 * @return
	 */
	@Column(name = "imp_percep", nullable = false, precision = 11)
	public BigDecimal getImpPercep() {
		return this.impPercep;
	}

	public void setImpPercep(BigDecimal impPercep) {
		this.impPercep = impPercep;
	}

	/**
	 * Importe Retenciones
	 * 
	 * @return
	 */
	@Column(name = "imp_retenc", nullable = false, precision = 11)
	public BigDecimal getImpRetenc() {
		return this.impRetenc;
	}

	public void setImpRetenc(BigDecimal impRetenc) {
		this.impRetenc = impRetenc;
	}

	/**
	 * Fecha de Proceso
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", length = 10)
	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Column(name = "disco", length = 1)
	public String getDisco() {
		return this.disco;
	}

	public void setDisco(String disco) {
		this.disco = disco;
	}

	/**
	 * Descuadrado
	 * 
	 * @return
	 */
	@Type(type="siNoType" )
	@Column(name = "descuadrado", length = 1)
	public Boolean getDescuadrado() {
		return this.descuadrado;
	}

	public void setDescuadrado(Boolean descuadrado) {
		this.descuadrado = descuadrado;
	}

	/**
	 * Fecha Creacion Fila
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecnew", length = 10)
	public Date getFecnew() {
		return this.fecnew;
	}

	public void setFecnew(Date fecnew) {
		this.fecnew = fecnew;
	}

	/**
	 * Hora Creacion Fila
	 * 
	 * @return
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "hornew", length = 8)
	public Date getHornew() {
		return this.hornew;
	}

	public void setHornew(Date hornew) {
		this.hornew = hornew;
	}

	/**
	 * Fecha Modificacion Fila
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecmod", length = 10)
	public Date getFecmod() {
		return this.fecmod;
	}

	public void setFecmod(Date fecmod) {
		this.fecmod = fecmod;
	}

	/**
	 * Hora Modificacion Fila
	 * 
	 * @return
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "hormod", length = 8)
	public Date getHormod() {
		return this.hormod;
	}

	public void setHormod(Date hormod) {
		this.hormod = hormod;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codadm")
	public Admon getAdmon() {
		return this.admon;
	}

	public void setAdmon(Admon admon) {
		this.admon = admon;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codemp", nullable = false, insertable = false, updatable = false)
	public Empresa getEmprnif() {
		return this.emprnif;
	}

	public void setEmprnif(Empresa emprnif) {
		this.emprnif = emprnif;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codemp", insertable = false, updatable = false)
	public Empresa getEmprnif1() {
		return this.emprnif1;
	}

	public void setEmprnif1(Empresa emprnif1) {
		this.emprnif1 = emprnif1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "divisa")
	public Divisa getDivisa() {
		return this.divisa;
	}

	public void setDivisa(Divisa divisa) {
		this.divisa = divisa;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "provincia")
	public Provincia getProvincia() {
		return this.provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codemp")
	public Empresa getEmprnif2() {
		return this.emprnif2;
	}

	public void setEmprnif2(Empresa emprnif2) {
		this.emprnif2 = emprnif2;
	}
	
	@OneToMany(mappedBy = "impresos190", cascade={CascadeType.REMOVE})
	public Set<LinImpresos190> getLineasImpresos190() {
		return lineasImpresos190;
	}

	public void setLineasImpresos190(Set<LinImpresos190> lineasImpresos190) {
		this.lineasImpresos190 = lineasImpresos190;
	}

}
