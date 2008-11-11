package com.code.aon.payroll.auxiliares.convenios;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.IndicadorDias;
import com.code.aon.payroll.enumeration.TipoConvenio;

/**
 * Complementos
 */
@Entity
@Table(name = "convenio")
public class Convenio implements ITransferObject {

	private String cdg;
	private String description;
	private String inddia;
	private String tipcon;
	
	/** niveles. */
	private Set<Nivel> niveles = new HashSet<Nivel>();
	/** pagas. */
	private Set<Pagaext> pagas = new HashSet<Pagaext>();

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 2)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	@Column(name = "descripcion", nullable = false, length = 35)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Devuelve el Indicador Dias Descuento
	 * 
	 * @return
	 */
	@Column(name = "inddia", nullable = false, length = 1)
	public String getInddia() {
		return this.inddia;
	}

	public void setInddia(String inddia) {
		this.inddia = inddia;
		this.inddiaenum = (this.inddia != null) ? IndicadorDias
				.valueOf("Ind" + this.inddia) : null;
	}

	/**
	 * Devuelve Tipo de Convenio
	 * 
	 * @return
	 */
	@Column(name = "tipcon", length = 1)
	public String getTipcon() {
		return this.tipcon;
	}

	public void setTipcon(String tipcon) {
		this.tipcon = tipcon;
		this.tipconenum = (this.tipcon != null) ? TipoConvenio
				.valueOf("Con" + this.tipcon) : null;
	}

	// TODO Problemas en la creacion del enumerado a partir de un String.
	private IndicadorDias inddiaenum;

	@Transient
	public IndicadorDias getInddiaenum() {
		return inddiaenum;
	}

	public void setInddiaenum(IndicadorDias inddiaenum) {
		this.inddiaenum = inddiaenum;
		setInddia((this.inddiaenum != null) ? this.inddiaenum.name().substring(3) : null);
	}

	// TODO Problemas en la creacion del enumerado a partir de un String.
	private TipoConvenio tipconenum;

	@Transient
	public TipoConvenio getTipconenum() {
		return tipconenum;
	}

	public void setTipconenum(TipoConvenio tipconenum) {
		this.tipconenum = tipconenum;
		setTipcon((this.tipconenum != null) ? this.tipconenum.name().substring(3) : null);
	}
	
	@OneToMany(mappedBy = "convenio", cascade={CascadeType.REMOVE})
	public Set<Nivel> getNiveles() {
		return niveles;
	}

	public void setNiveles(Set<Nivel> niveles) {
		this.niveles = niveles;
	}
	
	@OneToMany(mappedBy = "convenio", cascade={CascadeType.REMOVE})
	public Set<Pagaext> getPagas() {
		return pagas;
	}

	public void setPagas(Set<Pagaext> pagas) {
		this.pagas = pagas;
	}

}
