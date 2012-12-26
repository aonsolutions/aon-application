package com.code.aon.fiscal.model;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.mod111.Mod111Manager;
import com.code.aon.fiscal.mod115.Mod115Manager;
import com.code.aon.fiscal.mod123.Mod123Manager;
import com.code.aon.fiscal.mod130.Mod130Manager;

public class FiscalModelManagerFactory {

	private List<IFiscalModelManager> managers;
	
	public FiscalModelManagerFactory() {
		managers = new LinkedList<IFiscalModelManager>();
		managers.add(new Mod111Manager() );
		managers.add(new Mod115Manager() );
		managers.add(new Mod123Manager() );
		managers.add(new Mod130Manager() );
	}
	
	public List<IFiscalModelManager> getManagers() {
		return managers;
	}
	public void setManagers(List<IFiscalModelManager> managers) {
		this.managers = managers;
	}

	public IFiscalModelManager getManager( FiscalModelType type ) throws AonException {
		for ( IFiscalModelManager manager : getManagers() ) {
			if ( manager.accept( type ) ) {
				return manager;
			}
		}
		throw new AonException("No existe proveedor de modelo para el typo " + type);
	}
	
}
