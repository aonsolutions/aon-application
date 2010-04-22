package com.code.aon.payroll.cotizacion;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;


/**
 * Agrupador, Maestro de Elementos de Cotización.
 */
@Entity
@Table(name="elemcoti")
public class ElementoMaestro  implements ITransferObject {

     private String cdg;
     private String description;
     
     /** The element. */
  	private Set<Elemento> elementos = new HashSet<Elemento>();

     /**
      * Codigo de Elemento
      * 
      * @return
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
     * 	Descripcion de Elemento 
     * 
     * @return
     */

    @Column(name="descripcion", length=50)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @OneToMany(mappedBy = "elementoMaestro", cascade={CascadeType.REMOVE})
	public Set<Elemento> getElementos() {
		return elementos;
	}

	public void setElementos(Set<Elemento> elementos) {
		this.elementos = elementos;
	}

}


