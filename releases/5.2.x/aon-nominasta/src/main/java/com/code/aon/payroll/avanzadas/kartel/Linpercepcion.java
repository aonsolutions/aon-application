package com.code.aon.payroll.avanzadas.kartel;

import java.math.BigDecimal;
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

/**
 * Linpercepcion
 */
@Entity
@Table(name = "linpercepcion")
public class Linpercepcion implements ITransferObject {

	private LinpercepcionPK id;
	private Date fecfin;
	private BigDecimal importe;
	private BigDecimal nocturno;
	private BigDecimal empresa;
	private Percepcion percepcion;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 8)),
			@AttributeOverride(name = "fecinicio", column = @Column(name = "fecinicio", nullable = false, length = 10)) })
	public LinpercepcionPK getId() {
		return this.id;
	}

	public void setId(LinpercepcionPK id) {
		this.id = id;
	}
	
	/**
	 * Devuelve la Fecha Fin Vigencia
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecfin", length = 10)
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	/**
	 * Devuelve el Salario Base
	 * @return
	 */
	@Column(name = "importe", scale=2, precision=11)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	/**
	 * Devuelve el Complemento Nocturnidad
	 * @return
	 */
	@Column(name = "nocturno", scale=2, precision=11)
	public BigDecimal getNocturno() {
		return this.nocturno;
	}

	public void setNocturno(BigDecimal nocturno) {
		this.nocturno = nocturno;
	}

	/**
	 * Devuelve el Complemento Empresa
	 * @return
	 */
	@Column(name = "empresa", scale=2, precision=11)
	public BigDecimal getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(BigDecimal empresa) {
		this.empresa = empresa;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Percepcion getPercepcion() {
		return this.percepcion;
	}

	public void setPercepcion(Percepcion percepcion) {
		this.percepcion = percepcion;
	}

}
