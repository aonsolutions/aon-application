package com.code.aon.payroll.resultados.salarios;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.principales.persona.Trabajador;

/**
 * Paga extra
 */
@Entity
@Table(name = "nominaex")
public class Nominaex implements ITransferObject {

	private NominaexPK id;
	private Integer anio;
	private Integer mes;
	private Date fecini;
	private Date fecfin;
	private BigDecimal importe;
	private FijoVariable fijovar;
	private BigDecimal irpf;
	private BigDecimal impirpf;
	private BigDecimal liquido;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private Date feccob;
	private String descom;
	private Date feccobreal;
	private Date fecemi;
	private String nomemp;
	private String nomper;
	private String direccion;
	private String localidad;
	private String descat;
	private String profesion;
	private Integer nummat;
	private Date fecant;
	private BigDecimal totalDeducir;
	private Complemento complemento;
	private Divisa divisa;
	private Trabajador emprper;
	
	private Set<Nomdtoex> nomdtoex = new HashSet<Nomdtoex>();

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "numero", column = @Column(name = "numero", nullable = false, length = 2)) })
	public NominaexPK getId() {
		return this.id;
	}

	public void setId(NominaexPK id) {
		this.id = id;
	}

	/**
	 * Anio Paga Extra
	 * @return
	 */
	@Column(name = "anio", nullable = false, length = 2)
	public Integer getAnio() {
		return this.anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	/**
	 * Mes Paga Extra
	 * @return
	 */
	@Column(name = "mes", nullable = false, length = 2)
	public Integer getMes() {
		return this.mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	/**
	 * Inicio Periodo Paga
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false
)
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	/**
	 * Fin Periodo Paga
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecfin", nullable = false)
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	/**
	 * Importe Paga Extra
	 * @return
	 */
	@Column(name = "importe", nullable = false, precision = 11)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	/**
	 * Fijo o Variable
	 * @return
	 */
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.FijoVariable")} )
	@Column(name = "fijovar", nullable = false, length = 1)
	public FijoVariable getFijovar() {
		return this.fijovar;
	}

	public void setFijovar(FijoVariable fijovar) {
		this.fijovar = fijovar;
	}

	/**
	 * % de I.R.P.F.
	 * @return
	 */
	@Column(name = "irpf", nullable = false, precision = 4)
	public BigDecimal getIrpf() {
		return this.irpf;
	}

	public void setIrpf(BigDecimal irpf) {
		this.irpf = irpf;
	}

	/**
	 * Importe I.R.P.F.
	 * @return
	 */
	@Column(name = "impirpf", nullable = false, precision = 11)
	public BigDecimal getImpirpf() {
		return this.impirpf;
	}

	public void setImpirpf(BigDecimal impirpf) {
		this.impirpf = impirpf;
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
	 * Fecha de Cobro
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "feccob")
	public Date getFeccob() {
		return this.feccob;
	}

	public void setFeccob(Date feccob) {
		this.feccob = feccob;
	}

	/**
	 * Descripcion Complemento
	 * @return
	 */
	@Column(name = "descom", length = 50)
	public String getDescom() {
		return this.descom;
	}

	public void setDescom(String descom) {
		this.descom = descom;
	}

	/**
	 * Fecha Cobro Real
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
	 * Fecha de emision
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecemi")
	public Date getFecemi() {
		return this.fecemi;
	}

	public void setFecemi(Date fecemi) {
		this.fecemi = fecemi;
	}

	/**
	 * Nombre Actividad
	 * @return
	 */
	@Column(name = "nomemp", length = 60)
	public String getNomemp() {
		return this.nomemp;
	}

	public void setNomemp(String nomemp) {
		this.nomemp = nomemp;
	}

	/**
	 * Nombre Trabajador
	 * @return
	 */
	@Column(name = "nomper", length = 60)
	public String getNomper() {
		return this.nomper;
	}

	public void setNomper(String nomper) {
		this.nomper = nomper;
	}

	/**
	 * Direccion
	 * @return
	 */
	@Column(name = "direccion", length = 60)
	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * Localidad
	 * @return
	 */
	@Column(name = "localidad", length = 60)
	public String getLocalidad() {
		return this.localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	/**
	 * Descripcion Categoria
	 * @return
	 */
	@Column(name = "descat", length = 35)
	public String getDescat() {
		return this.descat;
	}

	public void setDescat(String descat) {
		this.descat = descat;
	}

	/**
	 * Profesion
	 * @return
	 */
	@Column(name = "profesion", length = 25)
	public String getProfesion() {
		return this.profesion;
	}

	public void setProfesion(String profesion) {
		this.profesion = profesion;
	}

	/**
	 * Numero matricula
	 * @return
	 */
	@Column(name = "nummat", length = 2)
	public Integer getNummat() {
		return this.nummat;
	}

	public void setNummat(Integer nummat) {
		this.nummat = nummat;
	}

	/**
	 * Fecha de Antiguedad
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecant")
	public Date getFecant() {
		return this.fecant;
	}

	public void setFecant(Date fecant) {
		this.fecant = fecant;
	}

	/**
	 * Total a deducir
	 * @return
	 */
	@Column(name = "total_deducir", nullable = false, precision = 11)
	public BigDecimal getTotalDeducir() {
		return this.totalDeducir;
	}

	public void setTotalDeducir(BigDecimal totalDeducir) {
		this.totalDeducir = totalDeducir;
	}

	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codcom")
	public Complemento getComplemento() {
		return this.complemento;
	}

	public void setComplemento(Complemento complemento) {
		this.complemento = complemento;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "divisa")
	public Divisa getDivisa() {
		return this.divisa;
	}

	public void setDivisa(Divisa divisa) {
		this.divisa = divisa;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Trabajador getEmprper() {
		return this.emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}
	
	@OneToMany(mappedBy = "nominaex", cascade={CascadeType.REMOVE})
	public Set<Nomdtoex> getNomdtoex() {
		return nomdtoex;
	}

	public void setNomdtoex(Set<Nomdtoex> nomdtoex) {
		this.nomdtoex = nomdtoex;
	}

}
