package com.esferalia.aon.gwt.fiscal.client.mod200.e2017;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.fiscal.client.mod200.Model200;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.DoubleVariable2017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Key;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002017Object implements Serializable {
	
	public static interface IMod200ChangeListener {
		void mod200Changed( Mod2002017 mod200 );
	}

	private static final long serialVersionUID = 1L;
	
	private List<IMod200ChangeListener> changeListeners;
	
	private String domainName;
	private boolean initialized;
	private Mod2002017 mod200;
	
	private boolean authomaticCalculation = true;
	
	public Mod2002017Object(String currentDomainName,Mod2002017 mod200) {
		this.domainName = currentDomainName;
		this.mod200 = mod200;
		initialized = mod200.getId() !=null;
	}

	public Integer getId() {
		return mod200.getId();
	}
	public boolean isInitialized() {
		return initialized;
	}
	public boolean isComplementary() {
		return mod200.isComplementary();
	}
	public void register(IMod200ChangeListener listener) {
		if (changeListeners == null) {
			changeListeners = new LinkedList<IMod200ChangeListener>();
		}
		changeListeners.add(listener);
	}
	
	private void fireMod200Changed(Mod2002017 mod2002) {
		if (changeListeners != null) {
			for (IMod200ChangeListener listener : changeListeners) {
				listener.mod200Changed(mod2002);
			}
		}
		
	}

	// ************************************
	public void initializeMod200(final AsyncCallback<Mod2002017> callback) {
		Model200.getMod2002017Service().initializeMod2002017(domainName,mod200.getDomain(),mod200, new AsyncCallback<Mod2002017>() {
			
			@Override
			public void onSuccess(Mod2002017 result) {
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

	public void save(final AsyncCallback<Mod2002017> callback) {
		Model200.getMod2002017Service().saveMod2002017(domainName,mod200.getDomain(),mod200, new AsyncCallback<Mod2002017>() {
			
			@Override
			public void onSuccess(Mod2002017 result) {
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
		Model200.getMod2002017Service().deleteMod2002017(domainName,mod200.getDomain(),mod200.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public void fillMod2002017AccountingData(final AsyncCallback<Mod2002017> callback) {
		Model200.getMod2002017Service().fillMod2002017AccountingData(mod200, new AsyncCallback<Mod2002017>() {
			
			@Override
			public void onSuccess(Mod2002017 result) {
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

	public Mod2002017 getMod200() {
		return mod200;
	}

	private void setMod200(Mod2002017 mod200) {
		this.mod200 = mod200;
		fireMod200Changed(mod200);
	}

	public Double getDoubleValue(Mod2002017Key key) {
		if (mod200 == null ) throw new IllegalStateException("Mod. 200 no inicializado." );
		return mod200.getDoubleValue(key);
	}
	public boolean isVisible(Mod2002017Key key) {
		return  mod200.getVisibleMap().containsKey(key);
	}
	
	public void doubleValueChanged(Mod2002017Key key, double value) {
		DoubleVariable2017 oldVar = getMod200().getKey(key);
		if (oldVar == null) {
			oldVar = new DoubleVariable2017(key);
		}
		DoubleVariable2017 newVar = oldVar.clone();
		newVar.setValue( value );
		newVar.setChangedByUser(true);
		mod200.addDraftVariable(newVar);
		if (isAuthomaticCalculation()) {
			calculate();
		}
	}
	public void mathExpression(String expression,AsyncCallback<Double> callback) {
		try {
			double ret = Model200.resolve(expression);
			callback.onSuccess(ret);
		} catch (Throwable t) {
			callback.onFailure(t);
		}
	}

	public void calculate() {
		Model200.getMod2002017Service().calculateMod2002017(mod200, new AsyncCallback<Mod2002017>() {
			
			@Override
			public void onSuccess(Mod2002017 result) {
				setMod200(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error durante el c\u00E1lculo del impuesto.");
			}
		});
	}

	public void validate(final AsyncCallback<Mod2002017> callback) {
		Model200.getMod2002017Service().validateMod2002017(mod200, new AsyncCallback<Mod2002017>() {
			
			@Override
			public void onSuccess(Mod2002017 result) {
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
		Model200.getMod2002017Service().dumpAEATMod2002017(mod200, new AsyncCallback<String>() {
			
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
		Model200.getMod2002017Service().getCompanyBanks(domainName,mod200.getDomain(),new AsyncCallback<LinkedList<CompanyBank>>() {
			
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
