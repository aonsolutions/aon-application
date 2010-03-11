package com.esferalia.aon.payroll;


import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.core.IDocument;
import com.esferalia.aon.core.IRegistry;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.enumeration.Prorrateo;
import com.esferalia.aon.payroll.enumeration.TipoIT;

@Entity
@Table(name = "parteit")
public class ParteIT<E extends IEmpleado<IEmpresa<IRegistry<IDocument>>, IPersona<IRegistry<IDocument>>>,
P extends IParteIT<E, P>> 
implements ITransferObject, 
	IParteIT<E,P> {

	private static final long serialVersionUID = -2204224372352983127L;
	
	private ParteITPK id;
	private String numeroColegiadoBaja;
	private String ciasBaja;
	private Boolean bajaProcesada;
	private Date fechaAlta;
	private String numeroColegiadoAlta;
	private String ciasAlta;
	private Boolean altaProcesada;
	private TipoIT tipoIT;
	private Boolean recaida;
	private Prorrateo prorrateo;
	private Double baseRetribucionPeriodoAnterior;
	private Integer diasPeriodoAnterior;
	private Double baseReguladoraDiaria;
	private Double baseDiariaContingenciasComunes;
	private Double baseDiariaAccidentes;
	private Double prestacionDiaria60;
	private Double prestacionDiaria75;
	private Boolean procesada;
	private Boolean riesgo;
	private E empleado;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)) })
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

	@Type(type = "siNoType")
	@Column(name = "bajproc", nullable = false, length = 1)
	public boolean isBajaProcesada() {
		return this.bajaProcesada;
	}

	public void setBajaProcesada(boolean bajaProcesada) {
		this.bajaProcesada = bajaProcesada;
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

	@Type(type = "siNoType")
	@Column(name = "altproc", nullable = false, length = 1)
	@Override
	public boolean isAltaProcesada() {
		return this.altaProcesada;
	}
	@Override
	public void setAltaProcesada(boolean altaProcesada) {
		this.altaProcesada = altaProcesada;
	}

	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.payroll.enumeration.Tipoit") })
	@Column(name = "tipoit", nullable = false, length = 1)
	public TipoIT getTipoIT() {
		return this.tipoIT;
	}

	public void setTipoIT(TipoIT tipoIT) {
		this.tipoIT = tipoIT;
	}
	
	@Override
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
	

	@Type(type = "siNoType")
	@Column(name = "recaida", length = 1)
	@Override
	public boolean isRecaida() {
		return this.recaida;
	}
	@Override
	public void setRecaida(boolean recaida) {
		this.recaida = recaida;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns( {
			@JoinColumn(name = "cdg", referencedColumnName = "cdg", nullable = false),
			@JoinColumn(name = "fecini", referencedColumnName = "feciniori", nullable = false) })
	@Override
	public P getParteITRecaida() {
		return null;
	}
	@Override
	public void setParteITRecaida(P ParteITRecaida) {
		
	}

	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.payroll.enumeration.Prorrateo") })
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

	@Type(type = "siNoType")
	@Column(name = "procesado", length = 1)
	@Override
	public boolean isProcesada() {
		return this.procesada;
	}
	@Override
	public void setProcesada(boolean procesada) {
		this.procesada = procesada;
	}

	@Type(type = "siNoType")
	@Column(name = "riesgo", length = 1)
	public Boolean getRiesgo() {
		return this.riesgo;
	}

	public void setRiesgo(Boolean riesgo) {
		this.riesgo = riesgo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	@Override
	public E getEmpleado() {
		return this.empleado;
	}

	@Override
	public void setEmpleado(E empleado) {
		this.empleado = empleado;
	}

}
