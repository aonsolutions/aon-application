package com.code.aon.fiscal.model;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.mod303.Mod303Manager;

public class FiscalModelManagerFactory {

	private List<IFiscalModelManager> managers;

//	public FiscalModelManagerFactory() {
//		
//	}
	
	public FiscalModelManagerFactory(String domainName) {
		managers = new LinkedList<IFiscalModelManager>();
		managers.add(new Mod303Manager(domainName) );
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
		throw new AonException("No existe proveedor de modelo para el tipo " + type);
	}
	
}
