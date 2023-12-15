package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.ArrayList;

import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;

public class MatrixModuleOptions extends  ModuleOptions<MatrixModuleOptions> {

	private static final long serialVersionUID = -1477889480738439699L;
	
	private boolean compactMode;
	private ArrayList<String> selected; // Seleccionados para la presentación múltiple
	private String result; // Resultado de la presentación multiple

	public boolean isCompactMode() {
		return compactMode;
	}
	public MatrixModuleOptions setCompactMode(boolean compactMode) {
		this.compactMode = compactMode;
		return this;
	}
	
	public ArrayList<String> getSelected() {
		if (selected == null)
			selected = new ArrayList<>();
		return selected;
	}	
	public String getSelectedKey(IFiscalModel model) {
		return model.getModel().toString() + "_" + model.getId().toString();
	}
	public void addSelected(IFiscalModel model) {
		getSelected().add(getSelectedKey(model));
	}	
	public void removeSelected(IFiscalModel model) {
		getSelected().remove(getSelectedKey(model));
	}
	public boolean isSelected(IFiscalModel model) {
		return getSelected().contains(getSelectedKey(model));  
	}
	// FALTA - RESULTADO DE LA PRESENTACION MULTIPLE
	public String getResult() {
		return result;
	}
	public MatrixModuleOptions setResult(String result) {
		this.result = result;
		return this;
	}	

}
