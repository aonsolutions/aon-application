package com.esferalia.aon.payroll;

import java.util.Date;

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

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.it.IConfirmacionParteIT;
import com.esferalia.aon.payroll.core.it.IParteIT;

@Entity
@Table(name = "parteconf")
public class ConfirmacionParteIT implements IConfirmacionParteIT, ITransferObject {

	private ConfirmacionParteITPK id;
	private String numeroColegiado;
	private String cias;
	private Date fecha;
	private String procesadoBD;
	private IParteIT parteIT;

//	@EmbeddedId
//	@AttributeOverrides( {
//			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
//			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)),
//			@AttributeOverride(name = "numero", column = @Column(name = "numero", nullable = false, length = 2)) })
//	public ParteconfId getId() {
//		return this.id;
//	}
//
//	public void setId(ParteconfId id) {
//		this.id = id;
//	}
	
	@EmbeddedId
	@Override
	public ConfirmacionParteITPK getId() {
		return this.id;
	}
	public void setId(ConfirmacionParteITPK id) {
		this.id = id;
	}

	@Override
	@Transient
	public Integer getNumero() {
		return getId().getNumero();
	}
	@Transient
	public void setNumero(Integer numero) {
		getId().setNumero(numero);
	}

	@Column(name = "numcol", length = 8)
	@Override
	public String getNumeroColegiado() {
		return this.numeroColegiado;
	}

	@Override
	public void setNumeroColegiado(String numeroColegiado) {
		this.numeroColegiado = numeroColegiado;
	}

	@Column(name = "cias", length = 11)
	@Override
	public String getCias() {
		return this.cias;
	}

	@Override
	public void setCias(String cias) {
		this.cias = cias;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecconf", nullable = false)
	@Override
	public Date getFecha() {
		return this.fecha;
	}

	@Override
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Column(name = "parproc", nullable = false, length = 1)
	public String getProcesadoBD() {
		return this.procesadoBD;
	}

	public void setProcesadoBD(String procesadoBD) {
		this.procesadoBD = procesadoBD;
	}

	@Override
	@Transient
	public boolean isProcesado() {
		return ("S".equals(getProcesadoBD()));
	}
	@Override
	public void setProcesado(boolean procesado) {
		setProcesadoBD((procesado)?"S":"N");
	}
	
	@ManyToOne(targetEntity = ParteIT.class,fetch = FetchType.EAGER)
	@JoinColumns( {
			@JoinColumn(name = "cdg", referencedColumnName = "cdg", insertable = false, updatable = false),
			@JoinColumn(name = "fecini", referencedColumnName = "fecini", insertable = false, updatable = false) })
	@Override
	public IParteIT getParteIT() {
		return this.parteIT;
	}
	@Override
	public void setParteIT(IParteIT parteIT) {
		this.parteIT = parteIT;
	}

}
