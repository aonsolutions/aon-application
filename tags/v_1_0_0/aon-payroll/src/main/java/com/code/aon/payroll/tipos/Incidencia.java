package com.code.aon.payroll.tipos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;

/**
 * Tipos de Incidencias.
 * 
 * @author alatorre & eagirrezabal
 *
 */
@Entity
@Table(name="tipinc")
public class Incidencia  implements ITransferObject {

	private String cdg;
	private String description;
	private String indresta;
	private String inddto;

	/**
	 * Devuelve el codigo del tipo de incidencia.
	 * 
	 *@return
	 */	
	@Id     
	@Column(name="cdg", unique=true, nullable=false, length=10)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	/**
	 * Devuelve la descripcion del tipo de registro.
	 * 
	 * @return
	 */

	@Column(name="descripcion", nullable=false, length=25)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	/**
	 * Indica si resta dias en el calculo de la nomina
	 * 
	 * @return
	 */	
	@Column(name="indresta", length=1)
	public String getIndresta() {
		return this.indresta;
	}

	public void setIndresta(String indresta) {
		this.indresta = indresta;
	}

	/**
	 * Indica si Descuenta Dias en Paga Extra
	 * 
	 * @return
	 */	

	@Column(name="inddto", length=1)
	public String getInddto() {
		return this.inddto;
	}

	public void setInddto(String inddto) {
		this.inddto = inddto;
	}

//TODO A la espera de implementar un SelectBooleanCheckboxRenderer.
	@Transient 
	public Boolean getIndrestabol() {
		return (getIndresta() != null && getIndresta().equals("S")?true:false );
	}

	public void setIndrestabol(Boolean bol) {
		setIndresta( (bol!=null && bol)? "S":"N" );
	}
	@Transient 
	public Boolean getInddtobol() {
		return (getInddto() != null && getInddto().equals("S")?true:false );
	}

	public void setInddtobol(Boolean bol) {
		setInddto( (bol!=null && bol)? "S":"N" );
	}
//	******************************************************************
}