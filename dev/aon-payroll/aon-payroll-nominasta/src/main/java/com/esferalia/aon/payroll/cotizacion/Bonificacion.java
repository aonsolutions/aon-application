package com.esferalia.aon.payroll.cotizacion;

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

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.core.IBonificacion;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;

/**
 * Bonificacion
 */
@Entity
@Table(name = "bonifica")
public class Bonificacion implements ITransferObject, IBonificacion {
	
	private static final long serialVersionUID = 1602249555827095448L;
	
	private BonificacionPK id;
	private Date fechaFin;
	private Integer horas;
	private Double importe;
	private IEmpleado empleado;
	private ITipoBonificacion tipoBonificacion;
	
	
	@EmbeddedId
	@Override
	public BonificacionPK getId() {
		return this.id;
	}
	public void setId(BonificacionPK id) {
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

	@Column(name = "horas", nullable = false, length = 2)
	@Override
	public Integer getHoras() {
		return this.horas;
	}
	@Override
	public void setHoras(Integer horas) {
		this.horas = horas;
	}

	@Column(name = "importe", nullable = false, scale=2, precision=8)
	@Override
	public Double getImporte() {
		return this.importe;
	}
	@Override
	public void setImporte(Double importe) {
		this.importe = importe;
	}
	
	@ManyToOne(targetEntity = Empleado.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "numero", insertable = false, updatable = false)
	@Override
	public IEmpleado getEmpleado() {
		return this.empleado;
	}

	@Override
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
		if(empleado!=null && empleado.getId()!=null){
			if(getId()==null){
				setId(new BonificacionPK());
			}
			getId().setNumero(empleado.getId());
		}
	}
	
	@ManyToOne(targetEntity = TipoBonificacion.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	@Override
	public ITipoBonificacion getTipoBonificacion() {
		return this.tipoBonificacion;
	}
	@Override
	public void setTipoBonificacion(ITipoBonificacion tipoBonificacion) {
		this.tipoBonificacion = tipoBonificacion;
		if(tipoBonificacion!=null && tipoBonificacion.getId()!=null){
			if(getId()==null){
				setId(new BonificacionPK());
			}
			getId().setCdg(tipoBonificacion.getId());
		}
	}
}
