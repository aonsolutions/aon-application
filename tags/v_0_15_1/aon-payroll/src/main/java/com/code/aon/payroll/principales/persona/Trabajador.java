package com.code.aon.payroll.principales.persona;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.IndiceAgrario;
import com.code.aon.payroll.enumeration.IndiceGrupo;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprccc;
import com.code.aon.payroll.principales.empresa.Emprccos;
import com.code.aon.payroll.principales.empresa.Empresa;
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
	private Boolean contrTemp;
	private Boolean mayor65;
	private Boolean afi;
	private IndiceAgrario indagrario;
	private IndiceGrupo indgrupo;
	private Boolean pariente;
	private Actividad actividad;
	private Domicilio domicilio;	
	private Emprccc emprccc;
	private Emprccos emprccos;	
	private Empresa empresa;
	private Persona persona;
	
	// unknown message error -1016, al poner fetchType a EAGER
	//private Domicilio domicilio;
	
	public Trabajador() {
		domicilio = new Domicilio();
	}
	
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
	@Column(name = "fecalt", nullable = false)
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
	@Column(name = "fecbaj")
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
	@Column(name = "fecnew")
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
	@Column(name = "hornew")
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
	@Column(name = "fecmod")
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
	@Column(name = "hormod")
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
	@Type(type="siNoType" )
	@Column(name = "contr_temp", length = 1)
	public Boolean getContrTemp() {
		return this.contrTemp==null?false:this.contrTemp;
	}

	public void setContrTemp(Boolean contrTemp) {
		this.contrTemp = contrTemp;
	}

	/**
	 * Devuelve si Mayores de 65 años y mas de 35 años Cotizados 
	 * @return
	 */
	@Type(type="siNoType" )
	@Column(name = "mayor65", length = 1)
	public Boolean getMayor65() {
		return this.mayor65==null?false:this.mayor65;
	}

	public void setMayor65(Boolean mayor65) {
		this.mayor65 = mayor65;
	}

	/**
	 * Devuelve si Insertado en AFI 
	 * @return
	 */
	@Type(type="siNoType" )
	@Column(name = "afi", length = 1)
	public Boolean getAfi() {
		return this.afi==null?false:this.afi;
	}

	public void setAfi(Boolean afi) {
		this.afi = afi;
	}

	/**
	 * Devuelve el Tipo Contrato Agrario 
	 * @return
	 */
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.IndiceAgrario")} )
	@Column(name = "indagrario", length = 1)
	public IndiceAgrario getIndagrario() {
		return this.indagrario;
	}

	public void setIndagrario(IndiceAgrario indagrario) {
		this.indagrario = indagrario;
	}

	/**
	 * Devuelve el Artista 
	 * @return
	 */
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.IndiceGrupo")} )
	@Column(name = "indgrupo", length = 1)
	public IndiceGrupo getIndgrupo() {
		return this.indgrupo;
	}

	public void setIndgrupo(IndiceGrupo indgrupo) {
		this.indgrupo = indgrupo;
	}

	/**
	 * Devuelve el Pariente 1º o 2º Grado 
	 * @return
	 */
	@Type(type="siNoType" )
	@Column(name = "pariente", length = 1)
	public Boolean getPariente() {
		return this.pariente==null?false:this.pariente;
		
	}

	public void setPariente(Boolean pariente) {
		this.pariente = pariente;
	}

	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codact", nullable = false, insertable = false, updatable = false)
	public Actividad getActividad() {
		return this.actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "domicilio", referencedColumnName = "cdg", nullable = false)
	public Domicilio getDomicilio() {
		return this.domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns( {
			@JoinColumn(name = "codact", referencedColumnName = "cdg", nullable = false),
			@JoinColumn(name = "codccc", referencedColumnName = "tipccc", nullable = false) })
	public Emprccc getEmprccc() {
		return this.emprccc;
	}

	public void setEmprccc(Emprccc emprccc) {
		this.emprccc = emprccc;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codcco", referencedColumnName = "cdg")
	public Emprccos getEmprccos() {
		return this.emprccos;
	}

	public void setEmprccos(Emprccos emprccos) {
		this.emprccos = emprccos;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codemp", referencedColumnName = "cdg", nullable = false)
	public Empresa getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Empresa emprnif) {
		this.empresa = emprnif;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codper", nullable = false)
	public Persona getPersona() {
		return this.persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	
}
