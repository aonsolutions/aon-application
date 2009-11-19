package com.code.aon.payroll.resultados.salarios;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Finidto
 */
@Entity
// @Lines(joinProperty="finiquito")
@Table(name = "finidto")
public class Finidto implements ITransferObject {

	private FinidtoPK id;
	private String texto;
	private BigDecimal importe;
	private Finiquito finiquito;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "orden", column = @Column(name = "orden", nullable = false, length = 2)) })
	public FinidtoPK getId() {
		return this.id;
	}

	public void setId(FinidtoPK id) {
		this.id = id;
	}

	/**
	 * Texto Descuento
	 * @return
	 */
	@Column(name = "texto", nullable = false, length = 40)
	public String getTexto() {
		return this.texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	/**
	 * Importe Descuento
	 * @return
	 */
	@Column(name = "importe", scale=2, precision=11)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Finiquito getFiniquito() {
		return this.finiquito;
	}

	public void setFiniquito(Finiquito finiquito) {
		this.finiquito = finiquito;
	}

}
