package com.esferalia.aon.payroll;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.remesa.IRemesaINSS;

@Entity
@Table(name = "remesa_inss")
public class RemesaINSS implements IRemesaINSS,ITransferObject  {

	private static final long serialVersionUID = 6607335535833243469L;
	
	private Integer id;
	private Date fecha;
	private Date hora;

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
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha")
	@Override
	public Date getFecha() {
		return fecha;
	}
	@Override
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	@Temporal(TemporalType.TIME)
	@Column(name = "hora")
	@Override
	public Date getHora() {
		return hora;
	}
	@Override
	public void setHora(Date hora) {
		this.hora = hora;
	}

}
