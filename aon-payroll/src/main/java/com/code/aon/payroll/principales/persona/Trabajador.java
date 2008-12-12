package com.code.aon.payroll.principales.persona;

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
import com.code.aon.payroll.principales.personas.Persona;

/**
 * Trabajador
 */
@Entity
@Table(name = "emprper")
public class Trabajador implements ITransferObject {

	private Integer cdg;
	private String codnsz;
	private Date fecalt;
	private Date fecbaj;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private String contrTemp;
	private String mayor65;
	private String afi;
	private String indagrario;
	private String indgrupo;
	private String pariente;
	//no necesario para mantenimiento de embargo
	/*
	private Domicilio domicilio;
	private Empract empract;
	private Emprccc emprccc;
	private Emprccos emprccos;
	private Emprnif emprnif;
	*/
	private Persona persona;
	
	/**
	 * Devuelve el Codigo de Trabajador
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
	 * Devuelve el Codigo BBX
	 * @return
	 */
	@Column(name = "codnsz", length = 8)
	public String getCodnsz() {
		return this.codnsz;
	}

	public void setCodnsz(String codnsz) {
		this.codnsz = codnsz;
	}

	/**
	 * Devuelve la Fecha de Alta
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecalt", nullable = false, length = 10)
	public Date getFecalt() {
		return this.fecalt;
	}

	public void setFecalt(Date fecalt) {
		this.fecalt = fecalt;
	}

	/**
	 * Devuelve la Fecha de Baja
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecbaj", length = 10)
	public Date getFecbaj() {
		return this.fecbaj;
	}

	public void setFecbaj(Date fecbaj) {
		this.fecbaj = fecbaj;
	}

	/**
	 * Devuelve la Fecha Creacion Fila
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
	 * Devuelve la Hora Creacion Fila
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
	 * Devuelve la Fecha Modificacion Fila
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
	 * Devuelve la Hora Modificacion Fila
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

	/**
	 * Devuelve el Contrato temporal
	 * @return
	 */
	@Column(name = "contr_temp", length = 1)
	public String getContrTemp() {
		return this.contrTemp;
	}

	public void setContrTemp(String contrTemp) {
		this.contrTemp = contrTemp;
	}

	/**
	 * Devuelve si Mayores de 65 años y mas de 35 años Cotizados 
	 * @return
	 */
	@Column(name = "mayor65", length = 1)
	public String getMayor65() {
		return this.mayor65;
	}

	public void setMayor65(String mayor65) {
		this.mayor65 = mayor65;
	}

	/**
	 * Devuelve si Insertado en AFI 
	 * @return
	 */
	@Column(name = "afi", length = 1)
	public String getAfi() {
		return this.afi;
	}

	public void setAfi(String afi) {
		this.afi = afi;
	}

	/**
	 * Devuelve el Tipo Contrato Agrario 
	 * @return
	 */
	@Column(name = "indagrario", length = 1)
	public String getIndagrario() {
		return this.indagrario;
	}

	public void setIndagrario(String indagrario) {
		this.indagrario = indagrario;
	}

	/**
	 * Devuelve el Artista 
	 * @return
	 */
	@Column(name = "indgrupo", length = 1)
	public String getIndgrupo() {
		return this.indgrupo;
	}

	public void setIndgrupo(String indgrupo) {
		this.indgrupo = indgrupo;
	}

	/**
	 * Devuelve el Pariente 1º o 2º Grado 
	 * @return
	 */
	@Column(name = "pariente", length = 1)
	public String getPariente() {
		return this.pariente;
	}

	public void setPariente(String pariente) {
		this.pariente = pariente;
	}

	/*
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "domicilio", nullable = false)
	public Domicilio getDomicilio() {
		return this.domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codact", nullable = false, insertable = false, updatable = false)
	public Empract getEmpract() {
		return this.empract;
	}

	public void setEmpract(Empract empract) {
		this.empract = empract;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "codact", referencedColumnName = "cdg", nullable = false),
			@JoinColumn(name = "codccc", referencedColumnName = "tipccc", nullable = false) })
	public Emprccc getEmprccc() {
		return this.emprccc;
	}

	public void setEmprccc(Emprccc emprccc) {
		this.emprccc = emprccc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codcco")
	public Emprccos getEmprccos() {
		return this.emprccos;
	}

	public void setEmprccos(Emprccos emprccos) {
		this.emprccos = emprccos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codemp", nullable = false)
	public Emprnif getEmprnif() {
		return this.emprnif;
	}

	public void setEmprnif(Emprnif emprnif) {
		this.emprnif = emprnif;
	}
*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codper", nullable = false)
	public Persona getPersona() {
		return this.persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	
}
