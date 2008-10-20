package com.code.aon.payroll.auxiliares.convenios;

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
 * Nivel
 */
@Entity
@Table(name = "nivel")
public class Nivel implements ITransferObject {
	
	private NivelPK id;
	private Convenio convenio;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "codcon", column = @Column(name = "codcon", nullable = false, length = 2)),
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 2)) })
	public NivelPK getId() {
		return this.id;
	}

	public void setId(NivelPK id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codcon", insertable = false, updatable = false)
	public Convenio getConvenio() {
		return this.convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

}
