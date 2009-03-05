package com.code.aon.payroll.resultados.salarios;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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

import com.code.aon.common.ITransferObject;

/**
 * Nomdtoex
 */
@Entity
@Table(name = "nomdtoex")
public class Nomdtoex implements ITransferObject {

	private NomdtoexPK id;
	private String concepto;
	private BigDecimal importe;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private Nominaex nominaex;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "numero", column = @Column(name = "numero", nullable = false, length = 2)),
			@AttributeOverride(name = "linea", column = @Column(name = "linea", nullable = false, length = 2)) })
	public NomdtoexPK getId() {
		return this.id;
	}

	public void setId(NomdtoexPK id) {
		this.id = id;
	}

	/**
	 * Concepto de Minoracion
	 * @return
	 */
	@Column(name = "concepto", nullable = false, length = 30)
	public String getConcepto() {
		return this.concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	/**
	 * Importe Minoracion
	 * @return
	 */
	@Column(name = "importe", nullable = false, precision = 11)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	/**
	 * Fecha Creacion Fila
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecnew")
	public Date getFecnew() {
		return this.fecnew;
	}

	public void setFecnew(Date fecnew) {
		this.fecnew = fecnew;
	}

	/**
	 * Hora Creacion Fila
	 * @return
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "hornew")
	public Date getHornew() {
		return this.hornew;
	}

	public void setHornew(Date hornew) {
		this.hornew = hornew;
	}

	/**
	 * Fecha Modificacion Fila
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecmod")
	public Date getFecmod() {
		return this.fecmod;
	}

	public void setFecmod(Date fecmod) {
		this.fecmod = fecmod;
	}

	/**
	 * Hora Modificacion Fila
	 * @return
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "hormod")
	public Date getHormod() {
		return this.hormod;
	}

	public void setHormod(Date hormod) {
		this.hormod = hormod;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns( {
			@JoinColumn(name = "cdg", referencedColumnName = "cdg", insertable = false, updatable = false),
			@JoinColumn(name = "numero", referencedColumnName = "numero", insertable = false, updatable = false) })
	public Nominaex getNominaex() {
		return this.nominaex;
	}

	public void setNominaex(Nominaex nominaex) {
		this.nominaex = nominaex;
	}

}
