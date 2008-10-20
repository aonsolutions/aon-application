package com.code.aon.payroll.auxiliares.convenios;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
	 * Devuleve el Indicador Dias Descuento
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

}
