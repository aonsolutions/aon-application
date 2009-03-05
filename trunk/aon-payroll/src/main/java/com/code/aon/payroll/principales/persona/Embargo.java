package com.code.aon.payroll.principales.persona;

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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.Afectados;

/**
 * Embargo
 */
@Entity
@Table(name = "embargo")
public class Embargo implements ITransferObject {

	private EmbargoPK id;
	private Integer codper;
	private String concepto;
	private Afectados afecta;
	private BigDecimal importe;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private Trabajador trabajador;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "fecha", column = @Column(name = "fecha", nullable = false, length = 10)) })
	public EmbargoPK getId() {
		return this.id;
	}

	public void setId(EmbargoPK id) {
		this.id = id;
	}

	/**
	 * Devuelve el Código de Persona
	 * @return
	 */
	@Column(name = "codper", nullable = false, length = 4)
	public Integer getCodper() {
		return this.codper;
	}

	public void setCodper(Integer codper) {
		this.codper = codper;
	}

	/**
	 * Devuelve el Concepto de Embargo
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
	 * Devuelve las Nóminas a las que afecta
	 * @return
	 */
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Afectados")} )
	@Column(name = "afecta", nullable = false, length = 1)
	public Afectados getAfecta() {
		return this.afecta;
	}

	public void setAfecta(Afectados afecta) {
		this.afecta = afecta;
	}

	/**
	 * Devuelve el Importe Embargo
	 * @return
	 */
	@Column(name = "importe", nullable = false, precision = 10)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	/**
	 * Devuelve la Fecha Creacion Fila
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
	 * Devuelve la Hora Creacion Fila
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
	 * Devuelve la Fecha Modificacion Fila
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
	 * Devuelve la Hora Modificacion Fila
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
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Trabajador getTrabajador() {
		return this.trabajador;
	}

	public void setTrabajador(Trabajador trabajador) {
		this.trabajador = trabajador;
	}

}
