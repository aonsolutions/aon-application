package com.code.aon.payroll.auxiliares.convenios;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

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
	private IndicadorDias inddia;
	private TipoConvenio tipcon;
	
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
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.IndicadorDias")} )
	@Column(name = "inddia", nullable = false, length = 1)
	public IndicadorDias getInddia() {
		return this.inddia;
	}

	public void setInddia(IndicadorDias inddia) {
		this.inddia = inddia;
	}

	/**
	 * Devuelve Tipo de Convenio
	 * 
	 * @return
	 */
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipoConvenio")} )
	@Column(name = "tipcon", length = 1)
	public TipoConvenio getTipcon() {
		return this.tipcon;
	}

	public void setTipcon(TipoConvenio tipcon) {
		this.tipcon = tipcon;
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
