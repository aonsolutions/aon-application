package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.Mod349DetailDB;

@Entity
@Table(name="fs_mod349_detail")
public class Mod349Detail extends Mod349DetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public Mod349Detail() {		
		super();
		// Aunque no se utiliza en el modelo viejo este campo, ya que 
		// no es posible editarlo en pantalla, como en la base de datos
		// está como no nulo, lo inicializo con cero, para que no de error
		// si metemos una linea de forma manual, en el modelo viejo
		this.setRectifiedAmount(0.0);
	}

}
