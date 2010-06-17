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

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IContratosTc2;
import com.esferalia.aon.payroll.core.ITrabajo;
import com.esferalia.aon.payroll.core.cotizacion.IBaseCotizacion;

/**
 * Trabajo
 */
@Entity
@Table(name = "trabajo")
public class Trabajo implements ITransferObject, ITrabajo {

	private static final long serialVersionUID = 232006768210590160L;

	private TrabajoPK id;
	private IBaseCotizacion baseCotizacion;
	private IContratosTc2 contratoTc2;
	private Date fechaInicioCont;
	private Date fechaFinCont;
	private String cno;

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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codbas", nullable = false)
	@Override
	public IBaseCotizacion getBaseCotizacion() {
		return this.baseCotizacion;
	}
	@Override
	public void setBaseCotizacion(IBaseCotizacion baseCotizacion) {
		this.baseCotizacion = baseCotizacion;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
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

	
	
	
	
	
}
