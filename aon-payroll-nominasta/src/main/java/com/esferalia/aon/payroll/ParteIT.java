package com.esferalia.aon.payroll;


import java.util.Date;

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
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.enumeration.Prorrateo;
import com.esferalia.aon.payroll.enumeration.TipoIT;

@Entity
@Table(name = "parteit")
public class ParteIT implements IParteIT,ITransferObject  {

	private static final long serialVersionUID = -2204224372352983127L;
	
	private ParteITPK id;
	private String numeroColegiadoBaja;
	private String ciasBaja;
	private String bajaProcesadaBD;
	private Date fechaAlta;
	private String numeroColegiadoAlta;
	private String ciasAlta;
	private String altaProcesadaBD;
	private TipoIT tipoIT;
	private String recaidaBD;
	private IParteIT parteITRecaida;
	private Prorrateo prorrateo;
	private Double baseRetribucionPeriodoAnterior;
	private Integer diasPeriodoAnterior;
	private Double baseReguladoraDiaria;
	private Double baseDiariaContingenciasComunes;
	private Double baseDiariaAccidentes;
	private Double prestacionDiaria60;
	private Double prestacionDiaria75;
	private String procesadaBD;
	private Boolean riesgo;
	private IEmpleado empleado;

	@EmbeddedId
	public ParteITPK getId() {
		return this.id;
	}
	public void setId(ParteITPK id) {
		this.id = id;
	}

	@Transient
	@Override
	public Date getFechaBaja() {
		return getId() == null? null : getId().getFechaBaja();
	}

	@Override
	public void setFechaBaja(Date fechaBaja) {
		if (getId() == null) {
			setId( new ParteITPK() );
		}
		getId().setFechaBaja( fechaBaja );
	}

	
	@Column(name = "numcolbaj", length = 8)
	@Override
	public String getNumeroColegiadoBaja() {
		return this.numeroColegiadoBaja;
	}
	@Override
	public void setNumeroColegiadoBaja(String numeroColegiadoBaja) {
		this.numeroColegiadoBaja = numeroColegiadoBaja;
		
	}

	@Column(name = "ciasbaj", length = 11)
	public String getCiasBaja() {
		return this.ciasBaja;
	}

	public void setCiasBaja(String ciasBaja) {
		this.ciasBaja = ciasBaja;
	}

	@Column(name = "bajproc", nullable = false, length = 1)
	public String getBajaProcesadaBD() {
		return this.bajaProcesadaBD;
	}
	public void setBajaProcesadaBD(String bajaProcesadaBD) {
		this.bajaProcesadaBD = bajaProcesadaBD;
	}

	@Override
	@Transient
	public boolean isBajaProcesada() {
		return ("S".equals(getBajaProcesadaBD()));
	}
	@Override
	public void setBajaProcesada(boolean bajaProcesada) {
		setBajaProcesadaBD((bajaProcesada)?"S":"N");
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecfin")
	@Override
	public Date getFechaAlta() {
		return this.fechaAlta;
	}
	@Override
	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	@Column(name = "numcolalt", length = 8)
	@Override
	public String getNumeroColegiadoAlta() {
		return this.numeroColegiadoAlta;
	}
	@Override
	public void setNumeroColegiadoAlta(String numeroColegiadoAlta) {
		this.numeroColegiadoAlta = numeroColegiadoAlta;
	}

	@Column(name = "ciasalt", length = 11)
	public String getCiasAlta() {
		return this.ciasAlta;
	}

	public void setCiasAlta(String ciasAlta) {
		this.ciasAlta = ciasAlta;
	}

	@Column(name = "altproc", nullable = false, length = 1)
	public String getAltaProcesadaBD() {
		return this.altaProcesadaBD;
	}
	public void setAltaProcesadaBD(String altaProcesadaBD) {
		this.altaProcesadaBD = altaProcesadaBD;
	}

	@Override
	@Transient
	public boolean isAltaProcesada() {
		return ("S".equals(getAltaProcesadaBD()));
	}
	@Override
	public void setAltaProcesada(boolean altaProcesada) {
		setAltaProcesadaBD((altaProcesada)?"S":"N");
	}

	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.esferalia.aon.payroll.enumeration.TipoIT") })
	@Column(name = "tipoit", nullable = false, length = 1)
	public TipoIT getTipoIT() {
		return this.tipoIT;
	}

	public void setTipoIT(TipoIT tipoIT) {
		this.tipoIT = tipoIT;
	}
	
	@Override
	@Transient
	public TipoContingencia getTipoContingencia() {
		if (getTipoIT() == TipoIT.ACCIDENTE) {
			return TipoContingencia.ACCIDENTE_LABORAL;
		} else if (getTipoIT() == TipoIT.EMBARAZO) {
			return TipoContingencia.EMBARAZO;
		} else if (getTipoIT() == TipoIT.ENFERMEDAD) {
			return TipoContingencia.ENFERMEDAD_COMUN;
		} else if (getTipoIT() == TipoIT.LACTANCIA) {
			return TipoContingencia.LACTANCIA;
		} else if (getTipoIT() == TipoIT.MATERNIDAD) {
			return TipoContingencia.MATERNIDAD;
		} else if (getTipoIT() == TipoIT.NOLABORAL) {
			return TipoContingencia.ACCIDENTE_NO_LABORAL;
		} else if (getTipoIT() == TipoIT.PATERNIDAD) {
			return TipoContingencia.PATERNIDAD;
		}
		return null;
	}

	@Override
	public void setTipoContingencia(TipoContingencia tipoContingencia) {
		if (tipoContingencia == TipoContingencia.ACCIDENTE_LABORAL) {
			setTipoIT(TipoIT.ACCIDENTE);
		} else if (tipoContingencia == TipoContingencia.EMBARAZO) {
			setTipoIT(TipoIT.EMBARAZO);
		} else if (tipoContingencia == TipoContingencia.ENFERMEDAD_COMUN) {
			setTipoIT(TipoIT.ENFERMEDAD);
		} else if (tipoContingencia == TipoContingencia.LACTANCIA) {
			setTipoIT(TipoIT.LACTANCIA);
		} else if (tipoContingencia == TipoContingencia.MATERNIDAD) {
			setTipoIT(TipoIT.MATERNIDAD);
		} else if (tipoContingencia == TipoContingencia.ACCIDENTE_NO_LABORAL) {
			setTipoIT(TipoIT.NOLABORAL);
		} else if (tipoContingencia == TipoContingencia.PATERNIDAD) {
			setTipoIT(TipoIT.PATERNIDAD);
		} else {
			setTipoIT(null);
		}
	}
	

	@Column(name = "recaida", length = 1)
	public String getRecaidaBD() {
		return recaidaBD;
	}
	public void setRecaidaBD(String recaidaBD) {
		this.recaidaBD = recaidaBD;
	}
	
	@Override
	@Transient
	public boolean isRecaida() {
		return ("S".equals(getRecaidaBD()));
	}
	@Override
	public void setRecaida(boolean recaida) {
		setRecaidaBD((recaida)?"S":"N");
	}

	//@ManyToOne(targetEntity = ParteIT.class,fetch = FetchType.EAGER)
	//@JoinColumns( {
	//		@JoinColumn(name = "cdg", referencedColumnName = "cdg", nullable = false, insertable=false, updatable=false),
	//		@JoinColumn(name = "feciniori", referencedColumnName = "fecini", nullable = false, insertable=false, updatable=false) })
	// 	@TODO la columna esta marcada como insertable False, lo cual es un error
	// Es necesario decir aHibernate que cdg no es modificable pero la fecha si.
	@Override
	@Transient
	public IParteIT getParteITRecaida() {
		return parteITRecaida;
	}

	@Override
	public void setParteITRecaida(IParteIT parteITRecaida) {
		this.parteITRecaida = parteITRecaida;
	}

	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.esferalia.aon.payroll.enumeration.Prorrateo") })
	@Column(name = "proret", nullable = false, length = 1)
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
	

	@Column(name = "baseant", scale = 2, precision = 11)
	@Override
	public Double getBaseRetribucionPeriodoAnterior() {
		return this.baseRetribucionPeriodoAnterior;
	}
	@Override
	public void setBaseRetribucionPeriodoAnterior(Double baseRetribucionPeriodoAnterior) {
		this.baseRetribucionPeriodoAnterior = baseRetribucionPeriodoAnterior;
	}

	@Column(name = "diasant", length = 2)
	@Override
	public Integer getDiasPeriodoAnterior() {
		return this.diasPeriodoAnterior;
	}
	@Override
	public void setDiasPeriodoAnterior(Integer diasPeriodoAnterior) {
		this.diasPeriodoAnterior = diasPeriodoAnterior;
	}

	@Column(name = "baseregdia", scale = 2, precision = 11)
	@Override
	public Double getBaseReguladoraDiaria() {
		return this.baseReguladoraDiaria;
	}
	@Override
	public void setBaseReguladoraDiaria(Double baseReguladoraDiaria) {
		this.baseReguladoraDiaria = baseReguladoraDiaria;
	}


	@Column(name = "basediacg", scale = 2, precision = 11)
	@Override
	public Double getBaseDiariaContingenciasComunes() {
		return this.baseDiariaContingenciasComunes;
	}

	@Override
	public void setBaseDiariaContingenciasComunes(Double baseDiariaContingenciasComunes) {
		this.baseDiariaContingenciasComunes = baseDiariaContingenciasComunes;
	}

	@Column(name = "basediaacc", scale = 2, precision = 11)
	@Override
	public Double getBaseDiariaAccidentesTrabajo() {
		return this.baseDiariaAccidentes;
	}
	@Override
	public void setBaseDiariaAccidentesTrabajo(Double baseDiariaAccidentes) {
		this.baseDiariaAccidentes = baseDiariaAccidentes;
	}

	@Column(name = "prest60", scale = 2, precision = 11)
	@Override
	public Double getPrestacionDiaria60() {
		return prestacionDiaria60;
	}
	@Override
	public void setPrestacionDiaria60(Double prestacionDiaria60) {
		this.prestacionDiaria60 = prestacionDiaria60;
	}


	@Column(name = "prest75", scale = 2, precision = 11)
	@Override
	public Double getPrestacionDiaria75() {
		return prestacionDiaria75;
	}
	@Override
	public void setPrestacionDiaria75(Double prestacionDiaria75) {
		this.prestacionDiaria75 = prestacionDiaria75;
		
	}

	@Column(name = "procesado", length = 1)
	public String getProcesadaBD() {
		return this.procesadaBD;
	}
	public void setProcesadaBD(String procesadaBD) {
		this.procesadaBD = procesadaBD;
	}
	@Override
	@Transient
	public boolean isProcesada() {
		return ("S".equals(getProcesadaBD()));
	}
	@Override
	public void setProcesada(boolean procesada) {
		setProcesadaBD((procesada)?"S":"N");
	}

	@Type(type = "siNoType")
	@Column(name = "riesgo", length = 1)
	public Boolean getRiesgo() {
		return this.riesgo;
	}

	public void setRiesgo(Boolean riesgo) {
		this.riesgo = riesgo;
	}

	@ManyToOne(targetEntity = Empleado.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	@Override
	public IEmpleado getEmpleado() {
		return this.empleado;
	}

	@Override
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}

}
