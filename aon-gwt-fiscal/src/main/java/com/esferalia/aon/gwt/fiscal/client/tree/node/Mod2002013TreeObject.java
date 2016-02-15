package com.esferalia.aon.gwt.fiscal.client.tree.node;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalTreeCallback;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.DoubleVariable2013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002013TreeObject implements Serializable {
	
	public static interface IMod200ChangeListener {
		void mod200Changed( Mod2002013 mod200 );
	}

	private static final long serialVersionUID = 1L;
	
	private List<IMod200ChangeListener> changeListeners;
	
	private boolean initialized;
	private FiscalTreeCallback<Mod2002013TreeObject> fiscalTreeCallback;	

	private String domainName;
	private int domain;
	private int year;
	private Integer id;
	private Mod2002013 mod200;
	
	private boolean authomaticCalculation = true;
	public Mod2002013TreeObject(String currentDomainName,int currentDomain,int year,Integer id) {
		this(currentDomainName, currentDomain, year);
		this.id = id; 
	}
	
	public Mod2002013TreeObject(String currentDomainName,int currentDomain,int year) {
		this.domainName = currentDomainName;
		this.domain = currentDomain;
		this.year = year;
	}
	public Integer getId() {
		return id;
	}
	public void setFiscalTreeCallback(
			FiscalTreeCallback<Mod2002013TreeObject> fiscalTreeCallback) {
		this.fiscalTreeCallback = fiscalTreeCallback;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void register(IMod200ChangeListener listener) {
		if (changeListeners == null) {
			changeListeners = new LinkedList<IMod200ChangeListener>();
		}
		changeListeners.add(listener);
	}
	
	private void fireMod200Changed(Mod2002013 mod2002) {
		if (changeListeners != null) {
			for (IMod200ChangeListener listener : changeListeners) {
				listener.mod200Changed(mod2002);
			}
		}
		
	}

	// ************************************
	public void initializeMod200(final AsyncCallback<Mod2002013> callback) {
		FiscalTree.FISCAL_SERVICE.initializeMod2002013(domainName,domain,mod200, new AsyncCallback<Mod2002013>() {
			
			@Override
			public void onSuccess(Mod2002013 result) {
				mod200 = result;
				initialized = true;
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				initialized = false;
				callback.onFailure(caught);
			}
		});
	}

	public void save(final AsyncCallback<Mod2002013> callback) {
		FiscalTree.FISCAL_SERVICE.saveMod2002013(domainName,domain,mod200, new AsyncCallback<Mod2002013>() {
			
			@Override
			public void onSuccess(Mod2002013 result) {
				mod200 = result;
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public void delete(final AsyncCallback<Void> callback) {
		FiscalTree.FISCAL_SERVICE.deleteMod2002013(domainName,domain,mod200.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				callback.onSuccess(result);
				if (fiscalTreeCallback != null) {
					fiscalTreeCallback.remove(Mod2002013TreeObject.this);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public void getMod200ById(final AsyncCallback<Mod2002013> callback) {
		FiscalTree.FISCAL_SERVICE.getMod2002013ById(domainName,domain, getId(), new AsyncCallback<Mod2002013>() {
			
			@Override
			public void onSuccess(Mod2002013 result) {
				mod200 = result;
				initialized = mod200.getId()!=null;
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				initialized = false;
				callback.onFailure(caught);
			}
		});
	}

	public void getMod200ByYear(final AsyncCallback<Mod2002013> callback) {
		FiscalTree.FISCAL_SERVICE.getMod2002013ByYear(domainName,domain, year, new AsyncCallback<Mod2002013>() {
			
			@Override
			public void onSuccess(Mod2002013 result) {
				mod200 = result;
				initialized = mod200.getId()!=null;
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				initialized = false;
				callback.onFailure(caught);
			}
		});
	}
	// ************************************
	
	public Administration getAdministration() {
		return mod200.getAdministration();
	} 
	public boolean isAuthomaticCalculation() {
		return authomaticCalculation;
	}

	public void setAuthomaticCalculation(boolean authomaticCalculation) {
		this.authomaticCalculation = authomaticCalculation;
	}

	public Mod2002013 getMod200() {
		return mod200;
	}

	private void setMod200(Mod2002013 mod200) {
		this.mod200 = mod200;
		fireMod200Changed(mod200);
	}

	public Double getDoubleValue(Mod2002013Key key) {
		if (mod200 == null ) throw new IllegalStateException("Mod. 200 no inicializado." );
		return mod200.getDoubleValue(key);
	}
	public boolean isVisible(Mod2002013Key key) {
		return  mod200.getVisibleMap().containsKey(key);
	}
	
	public void doubleValueChanged(Mod2002013Key key, double value) {
		DoubleVariable2013 oldVar = getMod200().getKey(key);
		if (oldVar == null) {
			oldVar = new DoubleVariable2013(key);
		}
		DoubleVariable2013 newVar = oldVar.clone();
		newVar.setValue( value );
		newVar.setChangedByUser(true);
		mod200.addDraftVariable(newVar);
		if (isAuthomaticCalculation()) {
			calculate();
		}
	}

	public void calculate() {
		FiscalTree.FISCAL_SERVICE.calculateMod2002013(mod200, new AsyncCallback<Mod2002013>() {
			
			@Override
			public void onSuccess(Mod2002013 result) {
				setMod200(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error curante el c\u00E1lculo del impuesto.");
			}
		});
	}

	public void validate(final AsyncCallback<Mod2002013> callback) {
		FiscalTree.FISCAL_SERVICE.validateMod2002013(mod200, new AsyncCallback<Mod2002013>() {
			
			@Override
			public void onSuccess(Mod2002013 result) {
				mod200 = result;
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public void dumpAEAT(final AsyncCallback<String> callback) {
		FiscalTree.FISCAL_SERVICE.dumpAEATMod2002013(mod200, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	public void getCompanyBanks(final AsyncCallback<LinkedList<CompanyBank>> callback) {
		FiscalTree.COMMON_SERVICE.getCompanyBanks(domainName,domain,new AsyncCallback<LinkedList<CompanyBank>>() {
			
			@Override
			public void onSuccess(LinkedList<CompanyBank> result) {
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
}
