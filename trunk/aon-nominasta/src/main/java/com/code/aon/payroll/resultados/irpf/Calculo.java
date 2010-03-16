package com.code.aon.payroll.resultados.irpf;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.Indirpf;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.principales.personas.Persona;

/**
 * Calculo
 */
@Entity
// @Lines(joinProperty="emprper")
@Table(name = "calculo")
public class Calculo implements ITransferObject {

	private CalculoPK id;
	private Indirpf indirpf;
	private String aplicado;
	private BigDecimal retrAnt;
	private BigDecimal retrAcuFij;
	private BigDecimal retrAcuVar;
	private BigDecimal retrPreFij;
	private BigDecimal retrPreVar;
	private BigDecimal retrEstimada;
	private BigDecimal retrConsid;
	private BigDecimal impAcuSs;
	private BigDecimal impPreSs;
	private BigDecimal impIrreg;
	private BigDecimal impRentas;
	private BigDecimal impPersonal;
	private BigDecimal impFamiliar;
	private BigDecimal impDescen;
	private BigDecimal impPension;
	private BigDecimal impCss;
	private BigDecimal impAscen;
	private BigDecimal baseCalculo;
	private BigDecimal impAnualid;
	private BigDecimal cuotaCalculo;
	private BigDecimal cuotaAnualid;
	private BigDecimal irpfAcu;
	private BigDecimal irpfCal;
	private BigDecimal irpf;
	private BigDecimal irpfAnterior;
	private Boolean regula;
	private Integer diascont;
	private String hijos;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private BigDecimal minforal;
	private BigDecimal impDiscapacidadt;
	private BigDecimal impCuidadohijo;
	private BigDecimal impDiscapacidad;
	private BigDecimal impPensionista;
	private BigDecimal impProlongacion;
	private BigDecimal impMovilidad;
	private BigDecimal impAsistencia;
	private BigDecimal retanualn;
	private BigDecimal retanualb;
	private BigDecimal difret;
	private BigDecimal irpfanual;
	private Trabajador emprper;
	private Empresa empresa;
	private Persona persona;
	
	public Calculo(){
		
		impAnualid = new BigDecimal(0);
		cuotaAnualid = new BigDecimal(0);
		retrAnt = new BigDecimal(0);
		retrAcuFij = new BigDecimal(0);
		retrAcuVar = new BigDecimal(0);
		retrPreFij = new BigDecimal(0);
		retrPreVar = new BigDecimal(0);
		retrEstimada = new BigDecimal(0);
		retrConsid = new BigDecimal(0);
		impAcuSs = new BigDecimal(0);
		impPreSs = new BigDecimal(0);
		aplicado = "N";
		impIrreg = new BigDecimal(0);
		impCss = new BigDecimal(0);
		impPersonal = new BigDecimal(0);
		impFamiliar = new BigDecimal(0);
		impRentas = new BigDecimal(0);
		impProlongacion = new BigDecimal(0);
		impMovilidad = new BigDecimal(0);
		impDiscapacidadt = new BigDecimal(0);
		impCuidadohijo = new BigDecimal(0);
		impAscen = new BigDecimal(0);
		impAsistencia = new BigDecimal(0);
		impDiscapacidad = new BigDecimal(0);
		impPensionista = new BigDecimal(0);
		impDescen = new BigDecimal(0);
		impPension = new BigDecimal(0);
		retanualn = new BigDecimal(0);
		retanualb = new BigDecimal(0);
		difret = new BigDecimal(0);
		irpfanual = new BigDecimal(0);
		baseCalculo = new BigDecimal(0);
		cuotaCalculo = new BigDecimal(0);
		irpfAcu = new BigDecimal(0);
		irpfCal = new BigDecimal(0);
		minforal = new BigDecimal(0);
		irpf = new BigDecimal(0);
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "anio", column = @Column(name = "anio", nullable = false, length = 2)),
			@AttributeOverride(name = "mes", column = @Column(name = "mes", nullable = false, length = 2)),
			@AttributeOverride(name = "dia", column = @Column(name = "dia", nullable = false, length = 2)) })
	public CalculoPK getId() {
		return this.id;
	}

	public void setId(CalculoPK id) {
		this.id = id;
	}

	/**
	 * Indicador Especial I.R.P.F.
	 * 
	 * @return
	 */
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Indirpf")} )
	@Column(name = "indirpf", length = 1)
	public Indirpf getIndirpf() {
		return this.indirpf;
	}

	public void setIndirpf(Indirpf indirpf) {
		this.indirpf = indirpf;
	}

	/**
	 * Indicador de modificacion segun indicador de irpf
	 * 
	 * @return
	 */
	@Column(name = "aplicado", nullable = false, length = 1)
	public String getAplicado() {
		return this.aplicado;
	}

	public void setAplicado(String aplicado) {
		this.aplicado = aplicado;
	}

	/**
	 * Retribucion Periodo Anterior
	 * 
	 * @return
	 */
	@Column(name = "retr_ant", nullable = false, scale=2, precision=11)
	public BigDecimal getRetrAnt() {
		return this.retrAnt;
	}

	public void setRetrAnt(BigDecimal retrAnt) {
		this.retrAnt = retrAnt;
	}

	/**
	 * Retribucion Acumulada Complementos Fijos
	 * 
	 * @return
	 */
	@Column(name = "retr_acu_fij", nullable = false, scale=2, precision=11)
	public BigDecimal getRetrAcuFij() {
		return this.retrAcuFij;
	}

	public void setRetrAcuFij(BigDecimal retrAcuFij) {
		this.retrAcuFij = retrAcuFij;
	}

	/**
	 * Retribucion Acumulada Complementos Variables
	 * 
	 * @return
	 */
	@Column(name = "retr_acu_var", nullable = false, scale=2, precision=11)
	public BigDecimal getRetrAcuVar() {
		return this.retrAcuVar;
	}

	public void setRetrAcuVar(BigDecimal retrAcuVar) {
		this.retrAcuVar = retrAcuVar;
	}

	/**
	 * Retribucion Prevista Complementos Fijos
	 * 
	 * @return
	 */
	@Column(name = "retr_pre_fij", nullable = false, scale=2, precision=11)
	public BigDecimal getRetrPreFij() {
		return this.retrPreFij;
	}

	public void setRetrPreFij(BigDecimal retrPreFij) {
		this.retrPreFij = retrPreFij;
	}

	/**
	 * Retribucion Prevista Complementos Variables
	 * 
	 * @return
	 */
	@Column(name = "retr_pre_var", nullable = false, scale=2, precision=11)
	public BigDecimal getRetrPreVar() {
		return this.retrPreVar;
	}

	public void setRetrPreVar(BigDecimal retrPreVar) {
		this.retrPreVar = retrPreVar;
	}

	/**
	 * Retribucion Estimada Ejercicio Actual
	 * 
	 * @return
	 */
	@Column(name = "retr_estimada", nullable = false, scale=2, precision=11)
	public BigDecimal getRetrEstimada() {
		return this.retrEstimada;
	}

	public void setRetrEstimada(BigDecimal retrEstimada) {
		this.retrEstimada = retrEstimada;
	}

	/**
	 * Retribucion a considerar
	 * 
	 * @return
	 */
	@Column(name = "retr_consid", nullable = false, scale=2, precision=11)
	public BigDecimal getRetrConsid() {
		return this.retrConsid;
	}

	public void setRetrConsid(BigDecimal retrConsid) {
		this.retrConsid = retrConsid;
	}

	/**
	 * Aportacion S.S. Acumulada
	 */
	@Column(name = "imp_acu_ss", nullable = false, scale=2, precision=11)
	public BigDecimal getImpAcuSs() {
		return this.impAcuSs;
	}

	public void setImpAcuSs(BigDecimal impAcuSs) {
		this.impAcuSs = impAcuSs;
	}

	/**
	 * Aportacion S.S. Prevista
	 * 
	 * @return
	 */
	@Column(name = "imp_pre_ss", nullable = false, scale=2, precision=11)
	public BigDecimal getImpPreSs() {
		return this.impPreSs;
	}

	public void setImpPreSs(BigDecimal impPreSs) {
		this.impPreSs = impPreSs;
	}

	/**
	 * Minoracion Rentas Irregulares
	 * 
	 * @return
	 */
	@Column(name = "imp_irreg", nullable = false, scale=2, precision=11)
	public BigDecimal getImpIrreg() {
		return this.impIrreg;
	}

	public void setImpIrreg(BigDecimal impIrreg) {
		this.impIrreg = impIrreg;
	}

	/**
	 * Minoracion Rentas Trabajo
	 * 
	 * @return
	 */
	@Column(name = "imp_rentas", nullable = false, scale=2, precision=11)
	public BigDecimal getImpRentas() {
		return this.impRentas;
	}

	public void setImpRentas(BigDecimal impRentas) {
		this.impRentas = impRentas;
	}

	/**
	 * Minoracion Personal
	 * 
	 * @return
	 */
	@Column(name = "imp_personal", nullable = false, scale=2, precision=11)
	public BigDecimal getImpPersonal() {
		return this.impPersonal;
	}

	public void setImpPersonal(BigDecimal impPersonal) {
		this.impPersonal = impPersonal;
	}

	/**
	 * Minoracion Familiar
	 * 
	 * @return
	 */
	@Column(name = "imp_familiar", nullable = false, scale=2, precision=11)
	public BigDecimal getImpFamiliar() {
		return this.impFamiliar;
	}

	public void setImpFamiliar(BigDecimal impFamiliar) {
		this.impFamiliar = impFamiliar;
	}

	/**
	 * Mas de 2 descendientes
	 * 
	 * @return
	 */
	@Column(name = "imp_descen", nullable = false, scale=2, precision=11)
	public BigDecimal getImpDescen() {
		return this.impDescen;
	}

	public void setImpDescen(BigDecimal impDescen) {
		this.impDescen = impDescen;
	}

	/**
	 * Pension Compensatoria
	 * 
	 * @return
	 */
	@Column(name = "imp_pension", nullable = false, scale=2, precision=11)
	public BigDecimal getImpPension() {
		return this.impPension;
	}

	public void setImpPension(BigDecimal impPension) {
		this.impPension = impPension;
	}

	/**
	 * Minoracion SS
	 * 
	 * @return
	 */
	@Column(name = "imp_css", nullable = false, scale=2, precision=11)
	public BigDecimal getImpCss() {
		return this.impCss;
	}

	public void setImpCss(BigDecimal impCss) {
		this.impCss = impCss;
	}

	/**
	 * Minoracion por Ascendientes
	 * 
	 * @return
	 */
	@Column(name = "imp_ascen", nullable = false, scale=2, precision=11)
	public BigDecimal getImpAscen() {
		return this.impAscen;
	}

	public void setImpAscen(BigDecimal impAscen) {
		this.impAscen = impAscen;
	}

	/**
	 * Base Calculo Retencion
	 * 
	 * @return
	 */
	@Column(name = "base_calculo", nullable = false, scale=2, precision=11)
	public BigDecimal getBaseCalculo() {
		return this.baseCalculo;
	}

	public void setBaseCalculo(BigDecimal baseCalculo) {
		this.baseCalculo = baseCalculo;
	}

	/**
	 * Anualidades por Alimentos
	 * 
	 * @return
	 */
	@Column(name = "imp_anualid", nullable = false, scale=2, precision=11)
	public BigDecimal getImpAnualid() {
		return this.impAnualid;
	}

	public void setImpAnualid(BigDecimal impAnualid) {
		this.impAnualid = impAnualid;
	}

	/**
	 * Cuota Calculo Retencion
	 * 
	 * @return
	 */
	@Column(name = "cuota_calculo", nullable = false, scale=2, precision=11)
	public BigDecimal getCuotaCalculo() {
		return this.cuotaCalculo;
	}

	public void setCuotaCalculo(BigDecimal cuotaCalculo) {
		this.cuotaCalculo = cuotaCalculo;
	}

	/**
	 * Cuota Anualidades
	 * 
	 * @return
	 */
	@Column(name = "cuota_anualid", nullable = false, scale=2, precision=11)
	public BigDecimal getCuotaAnualid() {
		return this.cuotaAnualid;
	}

	public void setCuotaAnualid(BigDecimal cuotaAnualid) {
		this.cuotaAnualid = cuotaAnualid;
	}

	/**
	 * Retencion I.R.P.F. acumulada
	 * 
	 * @return
	 */
	@Column(name = "irpf_acu", nullable = false, scale=2, precision=11)
	public BigDecimal getIrpfAcu() {
		return this.irpfAcu;
	}

	public void setIrpfAcu(BigDecimal irpfAcu) {
		this.irpfAcu = irpfAcu;
	}

	/**
	 * Tipo I.R.P.F. calculado
	 * 
	 * @return
	 */
	@Column(name = "irpf_cal", nullable = false, scale=2, precision=11)
	public BigDecimal getIrpfCal() {
		return this.irpfCal;
	}

	public void setIrpfCal(BigDecimal irpfCal) {
		this.irpfCal = irpfCal;
	}

	/**
	 * I.R.P.F. asignado
	 * 
	 * @return
	 */
	@Column(name = "irpf", scale=2, precision=5)
	public BigDecimal getIrpf() {
		return this.irpf;
	}

	public void setIrpf(BigDecimal irpf) {
		this.irpf = irpf;
	}

	/**
	 * I.R.P.F. Anterior
	 * 
	 * @return
	 */
	@Column(name = "irpf_anterior", scale=2, precision=5)
	public BigDecimal getIrpfAnterior() {
		return this.irpfAnterior;
	}

	public void setIrpfAnterior(BigDecimal irpfAnterior) {
		this.irpfAnterior = irpfAnterior;
	}

	/**
	 * I.R.P.F. Anterior Procede de Regularizacion
	 * 
	 * @return
	 */
	@Type(type="siNoType" )
	@Column(name = "regula", length = 1)
	public Boolean getRegula() {
		return this.regula;
	}

	public void setRegula(Boolean regula) {
		this.regula = regula;
	}

	/**
	 * Dias de contrato
	 * 
	 * @return
	 */
	@Column(name = "diascont", length = 2)
	public Integer getDiascont() {
		return this.diascont;
	}

	public void setDiascont(Integer diascont) {
		this.diascont = diascont;
	}

	/**
	 * Indicador de hijos
	 * 
	 * @return
	 */
	@Column(name = "hijos", length = 12)
	public String getHijos() {
		return this.hijos;
	}

	public void setHijos(String hijos) {
		this.hijos = hijos;
	}

	/**
	 * 
	 * Fecha Creacion Fila
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * Minoracion Foral por Minusvalia
	 * 
	 * @return
	 */
	@Column(name = "minforal", nullable = false, scale=2, precision=5)
	public BigDecimal getMinforal() {
		return this.minforal;
	}

	public void setMinforal(BigDecimal minforal) {
		this.minforal = minforal;
	}

	/**
	 * Discapacidad trabajador activos
	 * 
	 * @return
	 */
	@Column(name = "imp_discapacidadt", nullable = false, scale=2, precision=11)
	public BigDecimal getImpDiscapacidadt() {
		return this.impDiscapacidadt;
	}

	public void setImpDiscapacidadt(BigDecimal impDiscapacidadt) {
		this.impDiscapacidadt = impDiscapacidadt;
	}

	/**
	 * Cuidado de hijos
	 * 
	 * @return
	 */
	@Column(name = "imp_cuidadohijo", nullable = false, scale=2, precision=11)
	public BigDecimal getImpCuidadohijo() {
		return this.impCuidadohijo;
	}

	public void setImpCuidadohijo(BigDecimal impCuidadohijo) {
		this.impCuidadohijo = impCuidadohijo;
	}

	/**
	 * Discapacidad
	 * 
	 * @return
	 */
	@Column(name = "imp_discapacidad", nullable = false, scale=2, precision=11)
	public BigDecimal getImpDiscapacidad() {
		return this.impDiscapacidad;
	}

	public void setImpDiscapacidad(BigDecimal impDiscapacidad) {
		this.impDiscapacidad = impDiscapacidad;
	}

	/**
	 * Pensionistas
	 * 
	 * @return
	 */
	@Column(name = "imp_pensionista", nullable = false, scale=2, precision=11)
	public BigDecimal getImpPensionista() {
		return this.impPensionista;
	}

	public void setImpPensionista(BigDecimal impPensionista) {
		this.impPensionista = impPensionista;
	}

	/**
	 * Prolongacion actividad laboral
	 * 
	 * @return
	 */
	@Column(name = "imp_prolongacion", nullable = false, scale=2, precision=11)
	public BigDecimal getImpProlongacion() {
		return this.impProlongacion;
	}

	public void setImpProlongacion(BigDecimal impProlongacion) {
		this.impProlongacion = impProlongacion;
	}

	/**
	 * Movilidad geográfica
	 * 
	 * @return
	 */
	@Column(name = "imp_movilidad", nullable = false, scale=2, precision=11)
	public BigDecimal getImpMovilidad() {
		return this.impMovilidad;
	}

	public void setImpMovilidad(BigDecimal impMovilidad) {
		this.impMovilidad = impMovilidad;
	}

	/**
	 * Por asistencia
	 * 
	 * @return
	 */
	@Column(name = "imp_asistencia", nullable = false, scale=2, precision=11)
	public BigDecimal getImpAsistencia() {
		return this.impAsistencia;
	}

	public void setImpAsistencia(BigDecimal impAsistencia) {
		this.impAsistencia = impAsistencia;
	}

	/**
	 * Retención Anual Nueva
	 * 
	 * @return
	 */
	@Column(name = "retanualn", nullable = false, scale=2, precision=13)
	public BigDecimal getRetanualn() {
		return this.retanualn;
	}

	public void setRetanualn(BigDecimal retanualn) {
		this.retanualn = retanualn;
	}

	/**
	 * Retención Anual Base
	 * 
	 * @return
	 */
	@Column(name = "retanualb", nullable = false, scale=2, precision=13)
	public BigDecimal getRetanualb() {
		return this.retanualb;
	}

	public void setRetanualb(BigDecimal retanualb) {
		this.retanualb = retanualb;
	}

	/**
	 * Diferencia Retención
	 * 
	 * @return
	 */
	@Column(name = "difret", nullable = false, scale=2, precision=13)
	public BigDecimal getDifret() {
		return this.difret;
	}

	public void setDifret(BigDecimal difret) {
		this.difret = difret;
	}

	/**
	 * IRPF Anual
	 * 
	 * @return
	 */
	@Column(name = "irpfanual", nullable = false, scale=2, precision=5)
	public BigDecimal getIrpfanual() {
		return this.irpfanual;
	}

	public void setIrpfanual(BigDecimal irpfanual) {
		this.irpfanual = irpfanual;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Trabajador getEmprper() {
		return this.emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
		this.empresa = emprper.getEmpresa();
		this.persona = emprper.getPersona();
	}

	@Transient
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
		this.emprper.setEmpresa(empresa);
	}

	@Transient
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
		this.emprper.setPersona(persona);
	}

}
