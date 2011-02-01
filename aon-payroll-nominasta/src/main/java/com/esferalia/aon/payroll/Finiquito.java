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
import com.esferalia.aon.payroll.core.IFiniquito;

@Entity
@Table(name = "finiquito")
public class Finiquito implements ITransferObject, IFiniquito {

	private static final long serialVersionUID = -262895178660687226L;

	private Integer id;
	private Date fechaBaja;
	private Double importeVacaciones;
	private Integer diasVacaciones;
	private Double baseContingenciasGenerales;
	private Double baseAccidentesTrabajo;
	private IEmpleado empleado;


	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	@Override
	public Integer getId() {
		return this.id;
	}
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecbaj", nullable = false)
	@Override
	public Date getFechaBaja() {
		return this.fechaBaja;
	}
	@Override
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	@Column(name = "vacimporte", nullable = false, scale=2, precision=11)
	@Override
	public Double getImporteVacaciones() {
		return this.importeVacaciones;
	}
	@Override
	public void setImporteVacaciones(Double importeVacaciones) {
		this.importeVacaciones = importeVacaciones;
	}

	@Column(name = "diasvac", nullable = false, length = 2)
	@Override
	public Integer getDiasVacaciones() {
		return this.diasVacaciones;
	}
	@Override
	public void setDiasVacaciones(Integer diasVacaciones) {
		this.diasVacaciones = diasVacaciones;
	}

	@Column(name = "basecg", nullable = false, scale=2, precision=11)
	@Override
	public Double getBaseContingenciasGenerales() {
		return this.baseContingenciasGenerales;
	}
	@Override
	public void setBaseContingenciasGenerales(Double baseContingenciasGenerales) {
		this.baseContingenciasGenerales = baseContingenciasGenerales;
	}

	@Column(name = "baseacc", nullable = false, scale=2, precision=11)
	@Override
	public Double getBaseAccidentesTrabajo() {
		return this.baseAccidentesTrabajo;
	}
	@Override
	public void setBaseAccidentesTrabajo(Double baseAccidentesTrabajo) {
		this.baseAccidentesTrabajo = baseAccidentesTrabajo;
	}

	@ManyToOne(targetEntity = Empleado.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "codper")
	@Override
	public IEmpleado getEmpleado() {
		return this.empleado;
	}
	@Override
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}


}