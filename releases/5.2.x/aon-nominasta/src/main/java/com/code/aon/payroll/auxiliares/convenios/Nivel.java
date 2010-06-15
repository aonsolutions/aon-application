package com.code.aon.payroll.auxiliares.convenios;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	
	/** categorias. */
	private Set<Categoria> categorias = new HashSet<Categoria>();
	/** percepciones. */
	private Set<Percniv> percepciones = new HashSet<Percniv>();

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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codcon", insertable = false, updatable = false)
	public Convenio getConvenio() {
		return this.convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}
	
	@OneToMany(mappedBy = "nivel", cascade={CascadeType.REMOVE})
	public Set<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(Set<Categoria> categorias) {
		this.categorias = categorias;
	}
	
	@OneToMany(mappedBy = "nivel", cascade={CascadeType.REMOVE})
	public Set<Percniv> getPercepciones() {
		return percepciones;
	}

	public void setPercepciones(Set<Percniv> percepciones) {
		this.percepciones = percepciones;
	}
}
