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
import com.esferalia.aon.payroll.core.IContratosTc2;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.ITrabajo;
import com.esferalia.aon.payroll.core.cotizacion.IBaseCotizacion;
import com.esferalia.aon.payroll.core.enumeration.TiempoContrato;
import com.esferalia.aon.payroll.core.enumeration.TipoTiempoParcial;
import com.esferalia.aon.payroll.cotizacion.BaseCotizacion;

/**
 * Trabajo
 */
@Entity
@Table(name = "trabajo")
public class Trabajo implements ITransferObject, ITrabajo {

	private static final long serialVersionUID = 232006768210590160L;

	private TrabajoPK id;
	private Date fecfin;
	private IBaseCotizacion baseCotizacion;
	private IContratosTc2 contratoTc2;
	private Date fechaInicioCont;
	private Date fechaFinCont;
	private String cno;
	private IEmpleado empleado;
	private TiempoContrato tiempoContrato;
	private TipoTiempoParcial tipoTP;
	private Integer diasTP;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)) })
	@Override
	public TrabajoPK getId() {
		return this.id;
	}

	public void setId(TrabajoPK id) {
		this.id = id;
	}
	
	@Transient
	@Override
	public Date getFecini() {
		return this.id.getFecini();
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecfin", nullable = false)
	@Override
	public Date getFecfin() {
		return this.fecfin;
	}
	@Override
	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}
	
	@ManyToOne(targetEntity = BaseCotizacion.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "codbas", nullable = false)
	@Override
	public IBaseCotizacion getBaseCotizacion() {
		return this.baseCotizacion;
	}
	@Override
	public void setBaseCotizacion(IBaseCotizacion baseCotizacion) {
		this.baseCotizacion = baseCotizacion;
	}
	
	@ManyToOne(targetEntity = ContratosTc2.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "codtc2")
	@Override
	public IContratosTc2 getContratoTc2() {
		return this.contratoTc2;
	}
	@Override
	public void setContratoTc2(IContratosTc2 contratoTc2) {
		this.contratoTc2 = contratoTc2;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecinicont")
	@Override
	public Date getFechaInicioCont() {
		return this.fechaInicioCont;
	}
	@Override
	public void setFechaInicioCont(Date fechaInicioCont) {
		this.fechaInicioCont = fechaInicioCont;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecfincont")
	@Override
	public Date getFechaFinCont() {
		return this.fechaFinCont;
	}
	@Override
	public void setFechaFinCont(Date fechaFinCont) {
		this.fechaFinCont = fechaFinCont;
	}

	@Column(name = "cno", length = 4)
	@Override
	public String getCno() {
		return this.cno;
	}
	@Override
	public void setCno(String cno) {
		this.cno = cno;
	}
	
	@ManyToOne(targetEntity = Empleado.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	@Override
	public IEmpleado getEmpleado() {
		return this.empleado;
	}
	@Override
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}
	
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.esferalia.aon.payroll.core.enumeration.TiempoContrato") })
	@Column(name = "indtp", length = 1)
	@Override
	public TiempoContrato getTiempoContrato() {
		return this.tiempoContrato;
	}
	@Override
	public void setTiempoContrato(TiempoContrato tiempoContrato) {
		this.tiempoContrato = tiempoContrato;
	}

	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.esferalia.aon.payroll.core.enumeration.TipoTiempoParcial") })
	@Column(name = "tipoTp", length = 1)
	@Override
	public TipoTiempoParcial getTipoTP() {
		return this.tipoTP;
	}
	@Override
	public void setTipoTP(TipoTiempoParcial tipoTP) {
		this.tipoTP = tipoTP;
	}

	@Column(name = "diasTp", length = 1)
	@Override
	public Integer getDiasTP() {
		return this.diasTP;
	}
	@Override
	public void setDiasTP(Integer diasTP) {
		this.diasTP = diasTP;
	}
	
	

}
