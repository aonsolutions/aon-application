package com.code.aon.payroll.cotizacion;

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

/**
 * Agrupador, Maestro de Ocupaciones.
 */
@Entity
@Table(name = "ocupacion")
public class OcupacionMaestro implements ITransferObject {

	private String cdg;
	private String description;
	private String exclusivo;

	/**
	 * Devuelve el código de ocupación maestro
	 * 
	 * @return
	 */
	
	/** ocupaciones */
	private Set<Ocupacion> ocupaciones = new HashSet<Ocupacion>();

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 1)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	/**
	 * Devuelve la descripcion de ocupación maestro
	 * 
	 * @return
	 */
	@Column(name = "descripcion", length = 250)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Devuelve si es exclusivo a CNAE
	 * 
	 * @return
	 */
	@Column(name = "exclusivo", length = 1)
	public String getExclusivo() {
		return this.exclusivo;
	}

	public void setExclusivo(String exclusivo) {
		this.exclusivo = exclusivo;
	}
	
	//TODO A la espera de implementar un SelectBooleanCheckboxRenderer.
	@Transient 
	public Boolean getExclusivobol() {
		return (getExclusivo() != null && getExclusivo().equals("S")?true:false );
	}

	public void setExclusivobol(Boolean bol) {
		setExclusivo( (bol!=null && bol)? "S":"N" );
	}
	
	@OneToMany(mappedBy = "ocupacionMaestro", cascade={CascadeType.REMOVE})
	public Set<Ocupacion> getOcupaciones() {
		return ocupaciones;
	}

	public void setOcupaciones(Set<Ocupacion> ocupaciones) {
		this.ocupaciones = ocupaciones;
	}

}
