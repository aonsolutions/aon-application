package com.esferalia.aon.payroll;


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
import com.esferalia.aon.payroll.core.IContrato;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContrato;
import com.esferalia.aon.payroll.enumeration.Prorrateo;

/**
 * Mantenimiento de Trabajo
 */
@Entity
@Table(name = "trabajo")
public class Contrato implements ITransferObject, IContrato {

	private static final long serialVersionUID = 7479882002322960763L;
	
	private ContratoPK id;
	private Date fechaFin;
	private IEmpleado empleado;
	private String indtp;
	private Prorrateo prorrateo;
//	private Date fecant;
//	private String ctacar;
//	private String profesion;
//	private String codcat;
//	private String nivel;
//	private String descat;
	private Double irpf;
//	private String cno;
//	private Prorateo procot;
//	private Prorateo proret;
//	private Integer nummat;
//	private String destc2;
//	private Date fecinicont;
//	private Date fecfincont;
//	private Integer diascont;
//	private Date fecaut;
//	private String numcta;
//	private String plunumaut;
//	private Date plufecaut;
//	private BigDecimal pluprcmin;
//	private BigDecimal pluprcmax;
//	private BigDecimal coered;
//	private Integer semana;
//	private Integer semanatp;
//	private Integer cantp;
//	private BigDecimal baseant;
//	private Boolean indalt;
//	private Boolean inddtoit;
//	private Boolean inddtootr;
//	private TipIrpf indirpf;
//	private Integer concol;
//	private Boolean indactcon;
//	private String especial;
//	private Date fecnew;
//	private Date hornew;
//	private Date fecmod;
//	private Date hormod;
//	private String dc;
//	private String historico;
//	private Boolean indceutamelilla;
//	private RelacionLaboral relacion;
//	private String ocupacion;
//	private Autorizacion tipaut;
//	private Base basecoti;
//	private Colectivos colectivos;
//	private Convenio convenio;
//	private Tipocont tipocont;
//	private Entidad entidad;
//	private Epigrafe epigrafe;
//	private PorcentajeMaestro porcoti;
//	private Sucursal sucursal;
//	private ContratosTc2 contratoTC2;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)) })
	@Override
	public ContratoPK getId() {
		return this.id;
	}
	public void setId(ContratoPK id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecfin")
	@Override
	public Date getFechaFin() {
		return this.fechaFin;
	}
	@Override
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	@ManyToOne(targetEntity=Empleado.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	@Override
	public IEmpleado getEmpleado() {
		return this.empleado;
	}
	@Override
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}
	

	@Column(name = "indtp", length = 1)
	@Override
	public String getIndtp() {
		return indtp;
	}
	@Override
	public void setIndtp(String indtp) {
		this.indtp = indtp;
	}
	
	@Override
	@Transient
	public TipoContrato getTipoContrato() {
		if ("0".equals(getIndtp())) {
			return TipoContrato.TIEMPO_COMPLETO;	
		} else if ("1".equals(getIndtp())) {
			return TipoContrato.TEMPORAL_HORAS;
		} else if ("2".equals(getIndtp())) {
			return TipoContrato.TEMPORAL_DIAS;
		}
		return null;
	}
	@Override
	public void setTipoContrato(TipoContrato tipoContrato) {
		if (tipoContrato == TipoContrato.TIEMPO_COMPLETO) {
			setIndtp("0"); 	
		} else if (tipoContrato == TipoContrato.TEMPORAL_HORAS) {
			setIndtp("1");
		} else if (tipoContrato == TipoContrato.TEMPORAL_DIAS) {
			setIndtp("2");
		}
		setIndtp(null);
	}


	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.esferalia.aon.payroll.enumeration.Prorrateo") })
	@Column(name = "procot", nullable = false, length = 1)
	public Prorrateo getProrrateo() {
		return this.prorrateo;
	}
	public void setProrrateo(Prorrateo prorrateo) {
		this.prorrateo = prorrateo;
	}
	
	@Transient
	@Override
	public Periodicidad getProrrateoCotizacion() {
		if (getProrrateo() == Prorrateo.PRODIARIO) {
			return Periodicidad.DIARIO;	
		} else if (getProrrateo() == Prorrateo.PROMENSUAL) {
			return Periodicidad.MENSUAL;
		} 
		return null;
	}
	@Override
	public void setProrrateoCotizacion(Periodicidad period) {
		if (period == Periodicidad.DIARIO) {
			setProrrateo(Prorrateo.PRODIARIO);	
		} else if (period == Periodicidad.MENSUAL) {
			setProrrateo(Prorrateo.PROMENSUAL);
		} else {
			setProrrateo(null);
		}
	}
	
	@Column(name = "irpf", nullable = false, scale=2, precision=4)
	@Override
	public Double getIrpf() {
		return this.irpf;
	}
	@Override
	public void setIrpf(Double irpf) {
		this.irpf = irpf;
	}
	
	/*
	@Temporal(TemporalType.DATE)
	@Column(name = "fecant", nullable = false, length = 10)
	public Date getFecant() {
		return this.fecant;
	}

	public void setFecant(Date fecant) {
		this.fecant = fecant;
	}

	@Column(name = "ctacar", length = 12)
	public String getCtacar() {
		return this.ctacar;
	}

	public void setCtacar(String ctacar) {
		this.ctacar = ctacar;
	}

	@Column(name = "profesion", length = 25)
	public String getProfesion() {
		return this.profesion;
	}

	public void setProfesion(String profesion) {
		this.profesion = profesion;
	}

	@Column(name = "codcat", length = 2)
	public String getCodcat() {
		return this.codcat;
	}

	public void setCodcat(String codcat) {
		this.codcat = codcat;
	}

	@Column(name = "nivel", length = 2)
	public String getNivel() {
		return this.nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	@Column(name = "descat", length = 35)
	public String getDescat() {
		return this.descat;
	}

	public void setDescat(String descat) {
		this.descat = descat;
	}

	

	// @DataDefinition(label="Codigo Nacional Ocupaciones")
	@Column(name = "cno", length = 4)
	public String getCno() {
		return this.cno;
	}

	public void setCno(String cno) {
		this.cno = cno;
	}

	// @DataDefinition(label="Prorrateo Cotizacion")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Prorateo")} )
	@Column(name = "procot", nullable = false, length = 1)
	public Prorateo getProcot() {
		return this.procot;
	}

	public void setProcot(Prorateo procot) {
		this.procot = procot;
	}

	// @DataDefinition(label="Prorrateo Retribucion")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Prorateo")} )
	@Column(name = "proret", nullable = false, length = 1)
	public Prorateo getProret() {
		return this.proret;
	}

	public void setProret(Prorateo proret) {
		this.proret = proret;
	}

	// @DataDefinition(label="Numero de Matricula")
	@Column(name = "nummat", length = 2)
	public Integer getNummat() {
		return this.nummat;
	}

	public void setNummat(Integer nummat) {
		this.nummat = nummat;
	}

	// @DataDefinition(label="Descripcion Contrato TC2")
	@Column(name = "destc2", length = 30)
	public String getDestc2() {
		return this.destc2;
	}

	public void setDestc2(String destc2) {
		this.destc2 = destc2;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Inicio Contrato")
	@Column(name = "fecinicont")
	public Date getFecinicont() {
		return this.fecinicont;
	}

	public void setFecinicont(Date fecinicont) {
		this.fecinicont = fecinicont;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Fin Contrato")
	@Column(name = "fecfincont")
	public Date getFecfincont() {
		return this.fecfincont;
	}

	public void setFecfincont(Date fecfincont) {
		this.fecfincont = fecfincont;
	}

	// @DataDefinition(label="Duracion Contrato (Dias)")
	@Column(name = "diascont", length = 2)
	public Integer getDiascont() {
		return this.diascont;
	}

	public void setDiascont(Integer diascont) {
		this.diascont = diascont;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha autorización")
	@Column(name = "fecaut")
	public Date getFecaut() {
		return this.fecaut;
	}

	public void setFecaut(Date fecaut) {
		this.fecaut = fecaut;
	}

	// @DataDefinition(label="Numero Cuenta Bancaria")
	@Column(name = "numcta", length = 10)
	public String getNumcta() {
		return this.numcta;
	}

	public void setNumcta(String numcta) {
		this.numcta = numcta;
	}

	// @DataDefinition(label="Numero de Autorizacion Pluriempleo")
	@Column(name = "plunumaut", length = 11)
	public String getPlunumaut() {
		return this.plunumaut;
	}

	public void setPlunumaut(String plunumaut) {
		this.plunumaut = plunumaut;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Autorizacion Pluriempleo")
	@Column(name = "plufecaut")
	public Date getPlufecaut() {
		return this.plufecaut;
	}

	public void setPlufecaut(Date plufecaut) {
		this.plufecaut = plufecaut;
	}

	// @DataDefinition(label="Pluriempleo: % sobre Tope Minimo Cotizacion")
	@Column(name = "pluprcmin", scale=2, precision=5)
	public BigDecimal getPluprcmin() {
		return this.pluprcmin;
	}

	public void setPluprcmin(BigDecimal pluprcmin) {
		this.pluprcmin = pluprcmin;
	}

	// @DataDefinition(label="Pluriempleo: % sobre Tope Maximo Cotizacion")
	@Column(name = "pluprcmax", scale=2, precision=5)
	public BigDecimal getPluprcmax() {
		return this.pluprcmax;
	}

	public void setPluprcmax(BigDecimal pluprcmax) {
		this.pluprcmax = pluprcmax;
	}

	// @DataDefinition(label="Coeficiente Reductor Salarios")
	@Column(name = "coered", precision = 8, scale = 6)
	public BigDecimal getCoered() {
		return this.coered;
	}

	public void setCoered(BigDecimal coered) {
		this.coered = coered;
	}

	// @DataDefinition(label="Minutos Jornada Semanal Real")
	@Column(name = "semana", length = 2)
	public Integer getSemana() {
		return this.semana;
	}

	public void setSemana(Integer semana) {
		this.semana = semana;
	}

	// @DataDefinition(label="Minutos Jornada Semanal Tiempo Parcial")
	@Column(name = "semanatp", length = 2)
	public Integer getSemanatp() {
		return this.semanatp;
	}

	public void setSemanatp(Integer semanatp) {
		this.semanatp = semanatp;
	}

	// @DataDefinition(label="Cantidad Minutos/Dias Cotizacion Tiempo Parcial")
	@Column(name = "cantp", length = 2)
	public Integer getCantp() {
		return this.cantp;
	}

	public void setCantp(Integer cantp) {
		this.cantp = cantp;
	}

	// @DataDefinition(label="Base Calculo Antiguedad")
	@Column(name = "baseant", nullable = false, scale=2, precision=8)
	public BigDecimal getBaseant() {
		return this.baseant;
	}

	public void setBaseant(BigDecimal baseant) {
		this.baseant = baseant;
	}

	// @DataDefinition(label="Toma Fecha Alta como Fecha Antiguedad para Pagas")
	@Type(type="siNoType" )
	@Column(name = "indalt", length = 1)
	public Boolean getIndalt() {
		return this.indalt;
	}

	public void setIndalt(Boolean indalt) {
		this.indalt = indalt;
	}

	// @DataDefinition(label="Descontar Dias IT")
	@Type(type="siNoType" )
	@Column(name = "inddtoit", length = 1)
	public Boolean getInddtoit() {
		return this.inddtoit;
	}

	public void setInddtoit(Boolean inddtoit) {
		this.inddtoit = inddtoit;
	}

	// @DataDefinition(label="Descontar Dias Incidencias")
	@Type(type="siNoType" )
	@Column(name = "inddtootr", length = 1)
	public Boolean getInddtootr() {
		return this.inddtootr;
	}

	public void setInddtootr(Boolean inddtootr) {
		this.inddtootr = inddtootr;
	}

	// @DataDefinition(label="Indicador IRPF")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipIrpf")} )
    @Column(name = "indirpf", nullable = false, length = 1)
	public TipIrpf getIndirpf() {
		return this.indirpf;
	}

	public void setIndirpf(TipIrpf indirpf) {
		this.indirpf = indirpf;
	}

	// @DataDefinition(label="Codigo Convenio Colectivo TC2")
	@Column(name = "concol", length = 4)
	public Integer getConcol() {
		return this.concol;
	}

	public void setConcol(Integer concol) {
		this.concol = concol;
	}

	// @DataDefinition(label="Actualizar Percepciones segun Convenio")
	@Type(type="siNoType" )
	@Column(name = "indactcon", length = 1)
	public Boolean getIndactcon() {
		return this.indactcon;
	}

	public void setIndactcon(Boolean indactcon) {
		this.indactcon = indactcon;
	}

	// @DataDefinition(label="Marca Trabajador Especial")
	@Column(name = "especial", length = 1)
	public String getEspecial() {
		return this.especial;
	}

	public void setEspecial(String especial) {
		this.especial = especial;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Creacion Fila")
	@Column(name = "fecnew")
	public Date getFecnew() {
		return this.fecnew;
	}

	public void setFecnew(Date fecnew) {
		this.fecnew = fecnew;
	}

	@Temporal(TemporalType.TIME)
	// @DataDefinition(label="Hora Creacion Fila")
	@Column(name = "hornew")
	public Date getHornew() {
		return this.hornew;
	}

	public void setHornew(Date hornew) {
		this.hornew = hornew;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Modificacion Fila")
	@Column(name = "fecmod")
	public Date getFecmod() {
		return this.fecmod;
	}

	public void setFecmod(Date fecmod) {
		this.fecmod = fecmod;
	}

	@Temporal(TemporalType.TIME)
	// @DataDefinition(label="Hora Modificacion Fila")
	@Column(name = "hormod")
	public Date getHormod() {
		return this.hormod;
	}

	public void setHormod(Date hormod) {
		this.hormod = hormod;
	}

	// @DataDefinition(label="Digito de Control Cuenta Bancaria")
	@Column(name = "dc", length = 2)
	public String getDc() {
		return this.dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	// @DataDefinition(label="Historico de Modificaciones")
	@Column(name = "historico", length = 300)
	public String getHistorico() {
		return this.historico;
	}

	public void setHistorico(String historico) {
		this.historico = historico;
	}

	// @DataDefinition(label="Res. y Per. Ceuta Melilla")
	@Type(type="siNoType" )
	@Column(name = "indceutamelilla", length = 1)
	public Boolean getIndceutamelilla() {
		return this.indceutamelilla;
	}

	public void setIndceutamelilla(Boolean indceutamelilla) {
		this.indceutamelilla = indceutamelilla;
	}

	// @DataDefinition(label="Relación Laboral")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.RelacionLaboral")} )
	@Column(name = "relacion", nullable = false, length = 4)
	public RelacionLaboral getRelacion() {
		return this.relacion;
	}

	public void setRelacion(RelacionLaboral relacion) {
		this.relacion = relacion;
	}

	// @DataDefinition(label="Ocupación")
	@Column(name = "ocupacion", length = 1)
	public String getOcupacion() {
		return this.ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "autorizacion")
	public Autorizacion getTipaut() {
		return this.tipaut;
	}

	public void setTipaut(Autorizacion tipaut) {
		this.tipaut = tipaut;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codbas", nullable = false)
	public Base getBasecoti() {
		return this.basecoti;
	}

	public void setBasecoti(Base basecoti) {
		this.basecoti = basecoti;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "colectivo")
	public Colectivos getColectivos() {
		return this.colectivos;
	}

	public void setColectivos(Colectivos colectivos) {
		this.colectivos = colectivos;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codcon", nullable = false)
	public Convenio getConvenio() {
		return this.convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codcont")
	public Tipocont getTipocont() {
		return this.tipocont;
	}

	public void setTipocont(Tipocont tipocont) {
		this.tipocont = tipocont;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codent", insertable = false, updatable = false)
	public Entidad getEntidad() {
		return this.entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codepi")
	public Epigrafe getEpigrafe() {
		return this.epigrafe;
	}

	public void setEpigrafe(Epigrafe epigrafe) {
		this.epigrafe = epigrafe;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codpct")
	public PorcentajeMaestro getPorcoti() {
		return this.porcoti;
	}

	public void setPorcoti(PorcentajeMaestro porcoti) {
		this.porcoti = porcoti;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns( {
			@JoinColumn(name = "codent", referencedColumnName = "codent"),
			@JoinColumn(name = "codsuc", referencedColumnName = "cdg") })
	public Sucursal getSucursal() {
		return this.sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}
*/

//	@ManyToOne(targetEntity=Empleado.class,fetch = FetchType.EAGER)
//	@JoinColumn(name = "cdg", insertable = false, updatable = false)
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "codtc2")
//	public ContratosTc2 getContratoTC2() {
//		return this.contratoTC2;
//	}
//
//	public void setContratoTC2(ContratosTc2 contratoTC2) {
//		this.contratoTC2 = contratoTC2;
//	}
	
}
