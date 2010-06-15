package com.code.aon.payroll.principales.personas;

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
import com.code.aon.payroll.principales.persona.Trabajador;

/**
 * Mantenimiento de Prcdivtrab
 */
@Entity
// @Lines(joinProperty="emprper")
@Table(name = "prcdivtrab")
public class Prcdivtrab implements ITransferObject {

	private PrcdivtrabPK id;
	private Date fecfin;
	private BigDecimal prc;
	private String texto;
	private Trabajador emprper;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)),
			@AttributeOverride(name = "orden", column = @Column(name = "orden", nullable = false, length = 2)) })
	public PrcdivtrabPK getId() {
		return this.id;
	}

	public void setId(PrcdivtrabPK id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Fin")
	@Column(name = "fecfin", nullable = false)
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	// @DataDefinition(label="Porcentaje")
	@Column(name = "prc", nullable = false, scale=2, precision=5)
	public BigDecimal getPrc() {
		return this.prc;
	}

	public void setPrc(BigDecimal prc) {
		this.prc = prc;
	}

	// @DataDefinition(label="Texto",descriptionColumn=true)
	@Column(name = "texto", length = 30)
	public String getTexto() {
		return this.texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Trabajador getEmprper() {
		return this.emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}

}
