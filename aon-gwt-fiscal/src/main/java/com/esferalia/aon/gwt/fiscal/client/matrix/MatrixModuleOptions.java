package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.ArrayList;

import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;

public class MatrixModuleOptions extends  ModuleOptions<MatrixModuleOptions> {

	private static final long serialVersionUID = -1477889480738439699L;
	
	private boolean compactMode;
	private ArrayList<String> selected;

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
// FALTA - CREO QUE NO LO NECESITO
//public FiscalMatrixParams setSelected(ArrayList<String> selected) {
//	this.selected = selected;
//	return this;
//}
// FALTA - AÑADIR Y BORRAR SELECCIONADO, POR AHORA SOLO SE GUARDA MODELO_ID
	public void addSelected(IFiscalModel model) {
		String modelAndId = model.getModel().toString() + "_" + model.getId().toString();
		getSelected().add(modelAndId);
	}	
	public void removeSelected(IFiscalModel model) {
		String modelAndId = model.getModel().toString() + "_" + model.getId().toString();
		getSelected().remove(modelAndId);
	}
	public boolean isSelected(IFiscalModel model) {
		String modelAndId = model.getModel().toString() + "_" + model.getId().toString();
		return getSelected().contains(modelAndId);  
	}	

}
