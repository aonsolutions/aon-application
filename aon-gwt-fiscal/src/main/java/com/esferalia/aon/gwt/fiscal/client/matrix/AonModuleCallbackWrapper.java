package com.esferalia.aon.gwt.fiscal.client.matrix;

import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

class AonModuleCallbackWrapper<T extends FiscalModel> implements AonModuleCallback<T> {
	
	private static final long serialVersionUID = 1L;
	
	private AonCustomPopup dialog;
	private AonModuleCallback<FiscalModel> callback;
	
	AonModuleCallbackWrapper(AonCustomPopup entryDialog, AonModuleCallback<FiscalModel> cbk) {
		this.dialog = entryDialog;
		this.callback = cbk;
	}

	@Override
	public void onRemove(T removed) {
		hide();
		callback.onRemove(removed);
	}

	@Override
	public void onFailure(Throwable caught) {
		callback.onFailure(caught);
	}
	
	@Override
	public void onExit(T edited) {
		hide();
		callback.onExit(edited);
	}
	
	@Override
	public void onChange(T changed) {
		hide();
		callback.onChange(changed);
	}
	
	private void hide() {
		dialog.hide();
		dialog.clear();
	}
}	
