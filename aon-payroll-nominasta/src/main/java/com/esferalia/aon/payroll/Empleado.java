package com.esferalia.aon.payroll;

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

import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;
import com.esferalia.aon.payroll.core.enumeration.TipoContrato;

/**
 * Trabajador
 */
@Entity
@Table(name = "emprper")
public class Empleado implements ITransferObject,IEmpleado {

	private static final long serialVersionUID = 5020902333687926636L;
	
	private Integer cdg;
//	private String codnsz;
	private Date fechaInicio;
	private Date fechaFin;
//	private Date fecnew;
//	private Date hornew;
//	private Date fecmod;
//	private Date hormod;
//	private Boolean contrTemp;
	private Boolean mayor65;
//	private Boolean afi;
//	private IndiceAgrario indagrario;
//	private IndiceGrupo indgrupo;
//	private Boolean pariente;
//	private Actividad actividad;
//	private Domicilio domicilio;	
//	private Emprccc emprccc;
//	private Emprccos emprccos;	
	private IEmpresa empresa;
	private IPersona persona;
	
	public Empleado() {
	}
	
	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getCdg() {
		return this.cdg;
	}
	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

//	/**
//	 * Devuelve el Codigo BBX
//	 * @return
//	 */
//	@Column(name = "codnsz", length = 8)
//	public String getCodnsz() {
//		return this.codnsz;
//	}
//
//	public void setCodnsz(String codnsz) {
//		this.codnsz = codnsz;
//	}
//

	@Temporal(TemporalType.DATE)
	@Column(name = "fecalt", nullable = false)
	public Date getFechaInicio() {
		return this.fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecbaj")
	public Date getFechaFin() {
		return this.fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
//
//	/**
//	 * Devuelve la Fecha Creacion Fila
//	 * @return
//	 */
//	@Temporal(TemporalType.DATE)
//	@Column(name = "fecnew")
//	public Date getFecnew() {
//		return this.fecnew;
//	}
//
//	public void setFecnew(Date fecnew) {
//		this.fecnew = fecnew;
//	}
//
//	/**
//	 * Devuelve la Hora Creacion Fila
//	 * @return
//	 */
//	@Temporal(TemporalType.TIME)
//	@Column(name = "hornew")
//	public Date getHornew() {
//		return this.hornew;
//	}
//
//	public void setHornew(Date hornew) {
//		this.hornew = hornew;
//	}
//
//	/**
//	 * Devuelve la Fecha Modificacion Fila
//	 * @return
//	 */
//	@Temporal(TemporalType.DATE)
//	@Column(name = "fecmod")
//	public Date getFecmod() {
//		return this.fecmod;
//	}
//
//	public void setFecmod(Date fecmod) {
//		this.fecmod = fecmod;
//	}
//	
//	/**
//	 * Devuelve la Hora Modificacion Fila
//	 * @return
//	 */
//	@Temporal(TemporalType.TIME)
//	@Column(name = "hormod")
//	public Date getHormod() {
//		return this.hormod;
//	}
//
//	public void setHormod(Date hormod) {
//		this.hormod = hormod;
//	}
//
//	/**
//	 * Devuelve el Contrato temporal
//	 * @return
//	 */
//	@Type(type="siNoType" )
//	@Column(name = "contr_temp", length = 1)
//	public Boolean getContrTemp() {
//		return this.contrTemp==null?false:this.contrTemp;
//	}
//
//	public void setContrTemp(Boolean contrTemp) {
//		this.contrTemp = contrTemp;
//	}
//

	@Type(type="siNoType" )
	@Column(name = "mayor65", length = 1)
	@Override
	public boolean isMayor65() {
		return this.mayor65;
	}
	@Override
	public void setMayor65(boolean mayor65) {
		this.mayor65 = mayor65;
	}
	
//
//	/**
//	 * Devuelve si Insertado en AFI 
//	 * @return
//	 */
//	@Type(type="siNoType" )
//	@Column(name = "afi", length = 1)
//	public Boolean getAfi() {
//		return this.afi==null?false:this.afi;
//	}
//
//	public void setAfi(Boolean afi) {
//		this.afi = afi;
//	}
//
//	/**
//	 * Devuelve el Tipo Contrato Agrario 
//	 * @return
//	 */
//	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.esferalia.aon.payroll.enumeration.IndiceAgrario")} )
//	@Column(name = "indagrario", length = 1)
//	public IndiceAgrario getIndagrario() {
//		return this.indagrario;
//	}
//
//	public void setIndagrario(IndiceAgrario indagrario) {
//		this.indagrario = indagrario;
//	}
//
//	/**
//	 * Devuelve el Artista 
//	 * @return
//	 */
//	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.esferalia.aon.payroll.enumeration.IndiceGrupo")} )
//	@Column(name = "indgrupo", length = 1)
//	public IndiceGrupo getIndgrupo() {
//		return this.indgrupo;
//	}
//
//	public void setIndgrupo(IndiceGrupo indgrupo) {
//		this.indgrupo = indgrupo;
//	}
//
//	/**
//	 * Devuelve el Pariente 1º o 2º Grado 
//	 * @return
//	 */
//	@Type(type="siNoType" )
//	@Column(name = "pariente", length = 1)
//	public Boolean getPariente() {
//		return this.pariente==null?false:this.pariente;
//		
//	}
//
//	public void setPariente(Boolean pariente) {
//		this.pariente = pariente;
//	}
//
//	
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "codact", nullable = false, insertable = false, updatable = false)
//	public Actividad getActividad() {
//		return this.actividad;
//	}
//
//	public void setActividad(Actividad actividad) {
//		this.actividad = actividad;
//	}
//
//	
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "domicilio", referencedColumnName = "cdg", nullable = false)
//	public Domicilio getDomicilio() {
//		return this.domicilio;
//	}
//
//	public void setDomicilio(Domicilio domicilio) {
//		this.domicilio = domicilio;
//	}
//	
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumns( {
//			@JoinColumn(name = "codact", referencedColumnName = "cdg", nullable = false),
//			@JoinColumn(name = "codccc", referencedColumnName = "tipccc", nullable = false) })
//	public Emprccc getEmprccc() {
//		return this.emprccc;
//	}
//
//	public void setEmprccc(Emprccc emprccc) {
//		this.emprccc = emprccc;
//	}
//
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "codcco", referencedColumnName = "cdg")
//	public Emprccos getEmprccos() {
//		return this.emprccos;
//	}
//
//	public void setEmprccos(Emprccos emprccos) {
//		this.emprccos = emprccos;
//	}
//

	@ManyToOne(targetEntity = Empresa.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "codemp", referencedColumnName = "cdg", nullable = false)
	public IEmpresa getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(IEmpresa emprnif) {
		this.empresa = emprnif;
	}

	@ManyToOne(targetEntity = Persona.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "codper", nullable = false)
	public IPersona getPersona() {
		return this.persona;
	}

	public void setPersona(IPersona persona) {
		this.persona = persona;
	}
	
	//*********************************************
	//*********************************************
	//*********************************************

	@Override
	public CuentaCotizacion getCuentaCotizacion() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setCuentaCotizacion(CuentaCotizacion CuentaCotizacion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TipoContrato getTipoContrato() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setTipoContrato(TipoContrato tipoContrato) {
		// TODO Auto-generated method stub
		
	}
}
