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
 * Finindem
 */
@Entity
// @Lines(joinProperty="finiquito")
@Table(name = "finindem")
public class Finindem implements ITransferObject {

	private FinindemPK id;
	private String texto;
	private BigDecimal importe;
	private String irpf;
	private Finiquito finiquito;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "orden", column = @Column(name = "orden", nullable = false, length = 2)) })
	public FinindemPK getId() {
		return this.id;
	}

	public void setId(FinindemPK id) {
		this.id = id;
	}

	/**
	 * Texto Indemnizacion
	 * 
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
	 * Importe Indemnizacion
	 * 
	 * @return
	 */
	@Column(name = "importe", precision = 11)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	/**
	 * Sujeto a I.R.P.F (S/N)
	 * 
	 * @return
	 */
	@Column(name = "irpf", nullable = false, length = 1)
	public String getIrpf() {
		return this.irpf;
	}

	public void setIrpf(String irpf) {
		this.irpf = irpf;
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
