package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.enumeration.Regimen;

@Entity
@Table(name = "empract")
public class Actividad implements ITransferObject, IActividad {

	private static final long serialVersionUID = -7066918422648465571L;

	private Integer id;
	private String name;
	private String alias;
	private Date fecini;
	private Date fecfin;
	private String indregimen;
	private IEmpresa empresa;

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "descripcion", nullable = false, length = 60)
	public String getName() {
		return this.name;
	}

	public void setName(String descripcion) {
		this.name = descripcion;
	}

	@Column(name = "alias", length = 25)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecini")
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecfin")
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	@Column(name = "indregimen", length = 1)
	public String getIndregimen() {
		return indregimen;
	}
	public void setIndregimen(String indregimen) {
		this.indregimen = indregimen;
	}
	
	@Transient
	public Regimen getRegimen() {
		if ("A".equals(getIndregimen())) {
			return Regimen.AGRARIO;
		} else if ("G".equals(getIndregimen())) { 
			return Regimen.GENERAL;
		} else if ("R".equals(getIndregimen())) {
			return Regimen.ARTISTAS;
		} else if ("M".equals(getIndregimen())) {
			return Regimen.MARITIMO;
		}
		return null;
	}
	public void setRegimen(Regimen regimen) {
		if (regimen == Regimen.AGRARIO) {
			setIndregimen("A");
		} else if (regimen == Regimen.GENERAL) { 
			setIndregimen("G");
		} else if (regimen == Regimen.ARTISTAS) {
			setIndregimen("R");
		} else if (regimen == Regimen.MARITIMO) {
			setIndregimen("M");
		} else {
			setIndregimen(null);
		}
	}

	@ManyToOne(targetEntity = Empresa.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "codemp", referencedColumnName = "cdg", nullable = false)
	public IEmpresa getEmpresa() {
		return this.empresa;
	}
	public void setEmpresa(IEmpresa empresa) {
		this.empresa = empresa;
	}

}
