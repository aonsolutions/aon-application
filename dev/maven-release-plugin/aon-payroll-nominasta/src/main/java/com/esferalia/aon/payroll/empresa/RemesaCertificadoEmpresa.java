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
import com.esferalia.aon.payroll.Empresa;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.enumeration.FileStatus;

@Entity
@Table(name = "rem_cert_empr")
public class RemesaCertificadoEmpresa implements ITransferObject,
		IRemesaCertificadoEmpresa {

	private static final long serialVersionUID = 915773079558344718L;
	
	private Integer id;
	private IEmpresa empresa;
	private Date fecha;
	private FileStatus estado;
	private String huella;
	
	@Id     
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, length=10)
	@Override
	public Integer getId() {
		return id;
	}
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity = Empresa.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "empresa", nullable = false)
	@Override
	public IEmpresa getEmpresa() {
		return empresa;
	}
	@Override
	public void setEmpresa(IEmpresa empresa) {
		this.empresa = empresa;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", nullable = false)
	@Override
	public Date getFecha() {
		return fecha;
	}
	@Override
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	@Column(name = "estado", length = 1)
	@Override
	public FileStatus getEstado() {
		return estado;
	}
	@Override
	public void setEstado(FileStatus estado) {
		this.estado = estado;
	}

	@Column(name = "huella", length = 30)
	@Override
	public String getHuella() {
		return huella;
	}
	@Override
	public void setHuella(String huella) {
		this.huella = huella;
	}

	
	
}
