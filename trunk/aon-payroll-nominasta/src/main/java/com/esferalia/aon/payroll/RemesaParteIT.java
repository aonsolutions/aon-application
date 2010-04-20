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

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.enumeration.TipoOperacionIT;
import com.esferalia.aon.payroll.core.remesa.IRemesaINSS;
import com.esferalia.aon.payroll.core.remesa.IRemesaParteIT;

@Entity
@Table(name = "remesa_parte_it")
public class RemesaParteIT implements IRemesaParteIT,ITransferObject  {
	
	private static final long serialVersionUID = 988988193460782131L;
	
	private Integer id;
	private IRemesaINSS remesaINSS;
	private IEmpleado empleado;
	private Date fechaBaja;
	private Date fechaParte;
	private String numeroColegiado;
	private String cias;
	private TipoOperacionIT tipoOperacionIT;
	private Integer numero;
	private boolean bajaProcesada;
	private String altaProcesada;
	private TipoContingencia tipoContingencia;
	private boolean recaida;
	private Periodicidad prorrateoCotizacion;
	private boolean procesado;
	private boolean riesgoEmbarazo;
	private Double baseRetribucionPeriodoAnterior;
	private Integer diasPeriodoAnterior;
	private Double baseReguladoraDiaria;
	private Double baseDiariaContingenciasComunes;
	private Double baseDiariaAccidentesTrabajo;
	private Double prestacionDiaria60;
	private Double prestacionDiaria75;

	@Id     
    @Column(name="id", unique=true, nullable=false, length=10)
	@Override
	public Integer getId() {
		return id;
	}
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	@ManyToOne(targetEntity = RemesaINSS.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "id", insertable = false, updatable = false)
	@Override
	public IRemesaINSS getRemesaINSS() {
		return remesaINSS;
	}
	@Override
	public void setRemesaINSS(IRemesaINSS remesaINSS) {
		this.remesaINSS = remesaINSS;
	}
	@ManyToOne(targetEntity = Empleado.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	@Override
	public IEmpleado getEmpleado() {
		return empleado;
	}
	@Override
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_baja")
	@Override
	public Date getFechaBaja() {
		return fechaBaja;
	}
	@Override
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_parte")
	@Override
	public Date getFechaParte() {
		return fechaParte;
	}
	@Override
	public void setFechaParte(Date fechaParte) {
		this.fechaParte = fechaParte;
	}
	@Column(name = "numero_colegiado", length = 8)
	@Override
	public String getNumeroColegiado() {
		return numeroColegiado;
	}
	@Override
	public void setNumeroColegiado(String numeroColegiado) {
		this.numeroColegiado = numeroColegiado;
	}
	@Column(name = "cias", length = 11)
	@Override
	public String getCias() {
		return cias;
	}
	@Override
	public void setCias(String cias) {
		this.cias = cias;
	}
	@Column(name = "tipo_parte_it", nullable = false, length = 1)
	@Override
	public TipoOperacionIT getTipoOperacionIT() {
		return tipoOperacionIT;
	}
	@Override
	public void setTipoOperacionIT(TipoOperacionIT tipoOperacionIT) {
		this.tipoOperacionIT = tipoOperacionIT;
	}
	@Column(name = "numero", nullable = false, length = 2)
	@Override
	public Integer getNumero() {
		return numero;
	}
	@Override
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	@Column(name = "baja_procesada", nullable = false, length = 1)
	@Override
	public boolean isBajaProcesada() {
		return bajaProcesada;
	}
	@Override
	public void setBajaProcesada(boolean bajaProcesada) {
		this.bajaProcesada = bajaProcesada;
	}
	@Column(name = "alta_procesada", nullable = false, length = 1)
	@Override
	public String getAltaProcesada() {
		return altaProcesada;
	}
	@Override
	public void setAltaProcesada(String altaProcesada) {
		this.altaProcesada = altaProcesada;
	}
	@Column(name = "tipo_it", nullable = false, length = 1)
	@Override
	public TipoContingencia getTipoContingencia() {
		return tipoContingencia;
	}
	@Override
	public void setTipoContingencia(TipoContingencia tipoContingencia) {
		this.tipoContingencia = tipoContingencia;
	}
	@Column(name = "recaida", length = 1)
	@Override
	public boolean isRecaida() {
		return recaida;
	}
	@Override
	public void setRecaida(boolean recaida) {
		this.recaida = recaida;
	}
	@Column(name = "prorrateo_coti", nullable = false, length = 1)
	@Override
	public Periodicidad getProrrateoCotizacion() {
		return prorrateoCotizacion;
	}
	@Override
	public void setProrrateoCotizacion(Periodicidad prorrateoCotizacion) {
		this.prorrateoCotizacion = prorrateoCotizacion;
	}
	@Column(name = "procesado", length = 1)
	@Override
	public boolean isProcesado() {
		return procesado;
	}
	@Override
	public void setProcesado(boolean procesado) {
		this.procesado = procesado;
	}
	@Column(name = "riesgo", length = 1)
	@Override
	public boolean isRiesgoEmbarazo() {
		return riesgoEmbarazo;
	}
	@Override
	public void setRiesgoEmbarazo(boolean riesgoEmbarazo) {
		this.riesgoEmbarazo = riesgoEmbarazo;
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
		return this.baseDiariaAccidentesTrabajo;
	}
	@Override
	public void setBaseDiariaAccidentesTrabajo(Double baseDiariaAccidentesTrabajo) {
		this.baseDiariaAccidentesTrabajo = baseDiariaAccidentesTrabajo;
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
	
	}
