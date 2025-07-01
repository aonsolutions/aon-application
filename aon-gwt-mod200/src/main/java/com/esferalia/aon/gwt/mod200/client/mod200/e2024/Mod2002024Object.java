package com.esferalia.aon.gwt.mod200.client.mod200.e2024;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.mod200.client.mod200.Model200;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200ModuleOptions;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024Key;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002024Object implements Serializable {

	public static interface IMod200ChangeListener {
		void mod200Changed( Mod2002024 mod200 );
	}

	private static final long serialVersionUID = 1L;
	
	private List<IMod200ChangeListener> changeListeners;
	
	private boolean initialized;
	private Mod2002024 mod200;
	
	private Model200ModuleOptions options;	

	public Mod2002024Object(Model200ModuleOptions options, Mod2002024 mod200) {
		
		this.options = options;
		this.mod200 = mod200;
		initialized = (mod200.getId() != null);
		
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
	
	private void fireMod200Changed(Mod2002024 mod2002) {
		if (changeListeners != null) {
			for (IMod200ChangeListener listener : changeListeners) {
				listener.mod200Changed(mod2002);
			}
		}		
	}

	public void initializeMod200(final AsyncCallback<Mod2002024> callback) {
		Model200.getMod2002024Service().initializeMod2002024(options.getOccam(), mod200, new AsyncCallback<Mod2002024>() {
			
			@Override
			public void onSuccess(Mod2002024 result) {
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

	public void save(final AsyncCallback<Mod2002024> callback) {
		Model200.getMod2002024Service().saveMod2002024(options.getOccam(), mod200, new AsyncCallback<Mod2002024>() {
			
			@Override
			public void onSuccess(Mod2002024 result) {
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
		Model200.getMod2002024Service().deleteMod2002024(options.getOccam(), mod200, new AsyncCallback<Void>() {
			
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
	
	public void fillMod2002024AccountingData(String data, final AsyncCallback<Mod2002024> callback) {
		Model200.getMod2002024Service().fillMod2002024AccountingData(options.getOccam(), mod200, data, new AsyncCallback<Mod2002024>() {
			
			@Override
			public void onSuccess(Mod2002024 result) {				
				mod200 = result;
				calculate();
				fireMod200Changed(mod200);
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {				
				callback.onFailure(caught);				
			}
		});
	}
	
	public Mod2002024 getMod200() {
		return mod200;
	}

	public void setMod200(Mod2002024 mod200) {
		this.mod200 = mod200;
		fireMod200Changed(mod200);
	}

	public Double getDoubleValue(IMod200Key k) {
		if (mod200 == null ) throw new IllegalStateException("Mod. 200 no inicializado." );
		DoubleVariableEx dv = mod200.getVariable(k);
		if (dv != null)
			return AonNumberUtils.todouble(dv.getValue());
		else 
			return 0.0;
			
	}

	public boolean isVisible(Mod2002024Key key) {
		return mod200.getVisibleMap().containsKey(key);		
	}
	
	public void doubleValueChanged(IMod200Key k, double value) {
		DoubleVariableEx oldVar = getMod200().getKey(k);
		if (oldVar == null) {
			oldVar = new DoubleVariableEx(k);
		}
		DoubleVariableEx newVar = oldVar.clone();
		newVar.setValue( value );
		newVar.setChangedByUser(true);
		mod200.addDraftVariable(newVar);
		calculate();
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
		Model200.getMod2002024Service().calculateMod2002024(mod200, new AsyncCallback<Mod2002024>() {
			
			@Override
			public void onSuccess(Mod2002024 result) {
				setMod200(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error durante el c\u00E1lculo del impuesto.");
			}
		});
	}

	public void getCompanyBanks(final AsyncCallback<LinkedList<CompanyBank>> callback) {
		Model200.getMod2002024Service().getCompanyBanks(options.getOccam(), new AsyncCallback<LinkedList<CompanyBank>>() {
			
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
