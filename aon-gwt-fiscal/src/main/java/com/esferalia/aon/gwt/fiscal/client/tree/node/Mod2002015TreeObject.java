package com.esferalia.aon.gwt.fiscal.client.tree.node;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalTreeCallback;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.DoubleVariable2015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002015TreeObject implements Serializable {
	
	public static interface IMod200ChangeListener {
		void mod200Changed( Mod2002015 mod200 );
	}

	private static final long serialVersionUID = 1L;
	
	private List<IMod200ChangeListener> changeListeners;
	
	private boolean initialized;
	private FiscalTreeCallback<Mod2002015TreeObject> fiscalTreeCallback;	

	private String domainName;
	private int domain;
	private int year;
	private Integer id;
	private boolean complementary;
	private Mod2002015 mod200;
	
	private boolean authomaticCalculation = true;
	
	public Mod2002015TreeObject(String currentDomainName,int currentDomain,int year,Integer id, boolean complementary) {
		this(currentDomainName, currentDomain, year);
		this.id = id;
		this.complementary = complementary;
	}

	public Mod2002015TreeObject(String currentDomainName,int currentDomain,int year) {
		this.domainName = currentDomainName;
		this.domain = currentDomain;
		this.year = year;
	}
	public void setFiscalTreeCallback(
			FiscalTreeCallback<Mod2002015TreeObject> fiscalTreeCallback) {
		this.fiscalTreeCallback = fiscalTreeCallback;
	}
	public Integer getId() {
		return id;
	}
	public boolean isInitialized() {
		return initialized;
	}
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}
	public void register(IMod200ChangeListener listener) {
		if (changeListeners == null) {
			changeListeners = new LinkedList<IMod200ChangeListener>();
		}
		changeListeners.add(listener);
	}
	
	private void fireMod200Changed(Mod2002015 mod2002) {
		if (changeListeners != null) {
			for (IMod200ChangeListener listener : changeListeners) {
				listener.mod200Changed(mod2002);
			}
		}
		
	}

	// ************************************
	public void initializeMod200(final AsyncCallback<Mod2002015> callback) {
		FiscalTree.FISCAL_SERVICE.initializeMod2002015(domainName,domain,mod200, new AsyncCallback<Mod2002015>() {
			
			@Override
			public void onSuccess(Mod2002015 result) {
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

	public void save(final AsyncCallback<Mod2002015> callback) {
		FiscalTree.FISCAL_SERVICE.saveMod2002015(domainName,domain,mod200, new AsyncCallback<Mod2002015>() {
			
			@Override
			public void onSuccess(Mod2002015 result) {
				mod200 = result;
				id = mod200.getId();
				callback.onSuccess(result);
				changeNodeLabel();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public void delete(final AsyncCallback<Void> callback) {
		FiscalTree.FISCAL_SERVICE.deleteMod2002015(domainName,domain,mod200.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				callback.onSuccess(result);
				if (fiscalTreeCallback != null) {
					fiscalTreeCallback.remove(Mod2002015TreeObject.this);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public void getMod200ById(final AsyncCallback<Mod2002015> callback) {
		FiscalTree.FISCAL_SERVICE.getMod2002015ById(domainName,domain, getId(), new AsyncCallback<Mod2002015>() {
			
			@Override
			public void onSuccess(Mod2002015 result) {
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

	public void getMod200ByYear(final AsyncCallback<Mod2002015> callback) {
		FiscalTree.FISCAL_SERVICE.getMod2002015ByYear(domainName,domain, year, new AsyncCallback<Mod2002015>() {
			
			@Override
			public void onSuccess(Mod2002015 result) {
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
	
	public void createMod200(final AsyncCallback<Mod2002015> callback) {
		FiscalTree.FISCAL_SERVICE.createMod2002015(domainName,domain, year, new AsyncCallback<Mod2002015>() {
			
			@Override
			public void onSuccess(Mod2002015 result) {
				mod200 = result;
				initialized = false;
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				initialized = false;
				callback.onFailure(caught);
			}
		});
	}
	
	public void fillMod2002015AccountingData(final AsyncCallback<Mod2002015> callback) {
		FiscalTree.FISCAL_SERVICE.fillMod2002015AccountingData(mod200, new AsyncCallback<Mod2002015>() {
			
			@Override
			public void onSuccess(Mod2002015 result) {
				mod200 = result;
				calculate();
				fireMod200Changed(mod200);
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

	public Mod2002015 getMod200() {
		return mod200;
	}

	private void setMod200(Mod2002015 mod200) {
		this.mod200 = mod200;
		fireMod200Changed(mod200);
	}

	public Double getDoubleValue(Mod2002015Key key) {
		if (mod200 == null ) throw new IllegalStateException("Mod. 200 no inicializado." );
		return mod200.getDoubleValue(key);
	}
	public boolean isVisible(Mod2002015Key key) {
		return  mod200.getVisibleMap().containsKey(key);
	}
	
	public void doubleValueChanged(Mod2002015Key key, double value) {
		DoubleVariable2015 oldVar = getMod200().getKey(key);
		if (oldVar == null) {
			oldVar = new DoubleVariable2015(key);
		}
		DoubleVariable2015 newVar = oldVar.clone();
		newVar.setValue( value );
		newVar.setChangedByUser(true);
		mod200.addDraftVariable(newVar);
		if (isAuthomaticCalculation()) {
			calculate();
		}
	}
	public void mathExpression(String expression,AsyncCallback<Double> callback) {
		FiscalTree.FISCAL_SERVICE.mathExpression(expression, callback);
	}

	public void calculate() {
		FiscalTree.FISCAL_SERVICE.calculateMod2002015(mod200, new AsyncCallback<Mod2002015>() {
			
			@Override
			public void onSuccess(Mod2002015 result) {
				setMod200(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error durante el c\u00E1lculo del impuesto.");
			}
		});
	}

	public void validate(final AsyncCallback<Mod2002015> callback) {
		FiscalTree.FISCAL_SERVICE.validateMod2002015(mod200, new AsyncCallback<Mod2002015>() {
			
			@Override
			public void onSuccess(Mod2002015 result) {
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
		FiscalTree.FISCAL_SERVICE.dumpAEATMod2002015(mod200, new AsyncCallback<String>() {
			
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
	
	public void deleteFromTree() {
		fiscalTreeCallback.remove(this);
	}
	public void changeNodeLabel() {
		if (fiscalTreeCallback != null) {
			fiscalTreeCallback.changeLabel(Mod2002015TreeObject.this);
		}
	}

}
