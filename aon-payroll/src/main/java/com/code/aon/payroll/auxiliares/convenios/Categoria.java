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
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Epigrafe;

/**
 * Categoria
 */
@Entity
@Table(name = "categoria")
public class Categoria implements ITransferObject {

	private CategoriaPK id;
	private String nivel;
	private String description;
	private String cno;
	private Base base;
	private Convenio convenio;
	private Epigrafe epigrafe;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "codcon", column = @Column(name = "codcon", nullable = false, length = 2)),
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 2)) })
	public CategoriaPK getId() {
		return this.id;
	}

	public void setId(CategoriaPK id) {
		this.id = id;
	}

	/**
	 * Devuleve el Codigo de Nivel Retributivo
	 * 
	 * @return
	 */
	@Column(name = "nivel", nullable = false, length = 2)
	public String getNivel() {
		return this.nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	/**
	 * Devuelve la Descripcion de Categoria
	 * 
	 * @return
	 */
	@Column(name = "descripcion", length = 35)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Devuelve el Codigo Nacional O.
	 * 
	 * @return
	 */
	@Column(name = "cno", length = 4)
	public String getCno() {
		return this.cno;
	}

	public void setCno(String cno) {
		this.cno = cno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codbas")
	public Base getBase() {
		return this.base;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codcon", insertable = false, updatable = false)
	public Convenio getConvenio() {
		return this.convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codepi")
	public Epigrafe getEpigrafe() {
		return this.epigrafe;
	}

	public void setEpigrafe(Epigrafe epigrafe) {
		this.epigrafe = epigrafe;
	}

}
