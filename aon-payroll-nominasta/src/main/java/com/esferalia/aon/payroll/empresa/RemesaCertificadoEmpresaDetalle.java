package com.esferalia.aon.payroll.empresa;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;



@Entity
@Table(name="rem_cert_empr_det")
public class RemesaCertificadoEmpresaDetalle implements ITransferObject, IRemesaCertificadoEmpresaDetalle  {

	private static final long serialVersionUID = -3753104036316233484L;
	
	private Integer id;
	private IRemesaCertificadoEmpresa remesaCertificado;	
	private IEmpleado empleado;
	private Date fechaBaja;

	@Id     
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, length=10)
	@Override
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity = RemesaCertificadoEmpresa.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "rem_cert_empr", nullable = false)
	@Override
	public IRemesaCertificadoEmpresa getRemesaCertificado() {
		return remesaCertificado;
	}
	@Override
	public void setRemesaCertificado(IRemesaCertificadoEmpresa remesaCertificado) {
		this.remesaCertificado = remesaCertificado;
	}
	
	@ManyToOne(targetEntity = Empleado.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "empleado", insertable = false, updatable = false)
	@Override
	public IEmpleado getEmpleado() {
		return empleado;
	}
	@Override
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_baja", nullable = false)
	@Override
	public Date getFechaBaja() {
		return fechaBaja;
	}
	@Override
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	
	@Override
	public String getCausaSuspension() {
		return null;
	}
	@Override
	public void setCausaSuspension(String causaSuspension) {
		
	}
	
	
	
}


