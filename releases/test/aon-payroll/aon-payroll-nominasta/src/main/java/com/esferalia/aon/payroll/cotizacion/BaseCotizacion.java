package com.esferalia.aon.payroll.cotizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.cotizacion.IBaseCotizacion;
import com.esferalia.aon.payroll.core.enumeration.TipoProrrateo;

/**
 * Base de cotizacion
 */
@Entity
@Table(name = "basecoti")
public class BaseCotizacion implements ITransferObject, IBaseCotizacion {

	private static final long serialVersionUID = 6296476284324168767L;
	
	private String cdg;
	private String description;
	private TipoProrrateo tipoProrrateo;

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 2)
	@Override
	public String getCdg() {
		return this.cdg;
	}
	@Override
	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	@Column(name = "descripcion", length = 50)
	@Override
	public String getDescription() {
		return this.description;
	}
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.esferalia.aon.payroll.core.enumeration.TipoProrrateo") })
	@Column(name = "indpro", length = 5)
	@Override
	public TipoProrrateo getTipoProrrateo() {
		return this.tipoProrrateo;
	}
	@Override
	public void setTipoProrrateo(TipoProrrateo tipoProrrateo) {
		this.tipoProrrateo = tipoProrrateo;

	}

}
