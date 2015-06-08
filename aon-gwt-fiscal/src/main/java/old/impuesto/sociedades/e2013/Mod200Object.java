package old.impuesto.sociedades.e2013;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.UndoManager;
import com.esferalia.aon.gwt.common.client.Undoable;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.DoubleVariable2013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Mod200Object implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;
	private List<IMod200ChangeListener> changeListeners;

	abstract private class UndoableEdit<T> implements Undoable {

		T oldT;
		T newT;

		public UndoableEdit(T oldT, T newT) {
			this.newT = newT;
			this.oldT = oldT;
		}

		@Override
		public void redo() {
			addDraft(newT);
		}

		@Override
		public void undo() {
			if (oldT != null)
				addDraft(oldT);
			else
				removeDraft(newT);
		}

		abstract void addDraft(T t);

		abstract void removeDraft(T t);
	}
	
	class UndoableVariableEdit extends UndoableEdit<DoubleVariable2013> {

		public UndoableVariableEdit(DoubleVariable2013 oldT, DoubleVariable2013 newT) {
			super(oldT, newT);
		}

		@Override
		void addDraft(DoubleVariable2013 t) {
			mod200.addVariable(t);
		}

		@Override
		void removeDraft(DoubleVariable2013 t) {
			mod200.removeVariable(t);
		}
	}
	
	private String domainName;
	private int domain;
	private int year;
	private Mod2002013 mod200;
	private UndoManager<UndoableEdit<?>> undoManager;
	
	private boolean authomaticCalculation = true;
	
	public Mod200Object(String currentDomainName,int currentDomain,int year) {
		this.domainName = currentDomainName;
		this.domain = currentDomain;
		this.year = year;
		this.undoManager = new UndoManager<UndoableEdit<?>>();
	}
	
	// ************************************
	public void initializeMod200(final AsyncCallback<Mod2002013> callback) {
		Model200.FISCAL_SERVICE.initializeMod2002013(domainName,domain,mod200, new AsyncCallback<Mod2002013>() {
			
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

	public void save(final AsyncCallback<Mod2002013> callback) {
		Model200.FISCAL_SERVICE.saveMod2002013(domainName,domain,mod200, new AsyncCallback<Mod2002013>() {
			
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
		Model200.FISCAL_SERVICE.deleteMod2002013(domainName,domain,mod200.getId(), new AsyncCallback<Void>() {
			
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
	
	public void getMod200(final AsyncCallback<Mod2002013> callback) {
		Model200.FISCAL_SERVICE.getMod2002013ByYear(domainName,domain, year, new AsyncCallback<Mod2002013>() {
			
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

	public void redo() {
		undoManager.redo();
	}
	public void undo() {
		undoManager.undo();
	}
	public void add(UndoableEdit<?> undoable) {
		undoManager.add(undoable);
	}
	public final boolean canUndo() {
		return undoManager.canUndo();
	}
	public final boolean canRedo() {
		return undoManager.canRedo();
	}
	public Mod2002013 getMod200() {
		return mod200;
	}

	private void changeMod200(Mod2002013 mod200) {
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
		UndoableVariableEdit eve = new UndoableVariableEdit(oldVar, newVar);
		undoManager.add(eve);
		newVar.setChangedByUser(true);
		mod200.addDraftVariable(newVar);
		if (isAuthomaticCalculation()) {
			calculate();
		}
	}

	public void calculate() {
		Model200.FISCAL_SERVICE.calculateMod2002013(mod200, new AsyncCallback<Mod2002013>() {
			
			@Override
			public void onSuccess(Mod2002013 result) {
				changeMod200(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error curante el c\u00E1lculo del impuesto.");
			}
		});
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

	public void validate(final AsyncCallback<Mod2002013> callback) {
		Model200.FISCAL_SERVICE.validateMod2002013(mod200, new AsyncCallback<Mod2002013>() {
			
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
		Model200.FISCAL_SERVICE.dumpAEATMod2002013(mod200, new AsyncCallback<String>() {
			
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

	public void getCompanyBanks(final AsyncCallback<ArrayList<CompanyBank>> callback) {
		Model200.COMMON_SERVICE.getCompanyBanks(domainName,domain,new AsyncCallback<ArrayList<CompanyBank>>() {
			
			@Override
			public void onSuccess(ArrayList<CompanyBank> result) {
				callback.onSuccess(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
}
