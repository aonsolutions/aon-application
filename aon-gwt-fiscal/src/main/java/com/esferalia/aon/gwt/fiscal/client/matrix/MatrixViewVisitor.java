package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303ModuleOptions;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;

public class MatrixViewVisitor implements IFiscalModelTypeVisitor {
	
	private static final Logger LOGGER = Logger.getLogger(MatrixViewVisitor.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private class AonModuleCallbackWrapper<T extends FiscalModel> implements AonModuleCallback<T> {
		
		private static final long serialVersionUID = 1L;
		
		private AonCustomPopup dialog;
		private AonModuleCallback<FiscalModel> callback;
		
		private AonModuleCallbackWrapper(AonCustomPopup entryDialog, AonModuleCallback<FiscalModel> cbk) {
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

	private MatrixModuleOptions opt;
	private FiscalModel model;
	private AonModuleCallback<FiscalModel> callback;
	
	public MatrixViewVisitor(MatrixModuleOptions opt, FiscalModel model, AonModuleCallback<FiscalModel> callback) {
		this.opt= opt;
		this.model = model;
		this.callback = callback;
	}
	
	private AonCustomPopup getModelDialog(String caption) {
		AonCustomPopup modelDialog = new AonCustomPopup( false );
		modelDialog.setWidth((Window.getClientWidth() - 50) + "px");
		modelDialog.setHeight((Window.getClientHeight() - 50) + "px");
		modelDialog.setAnimationEnabled(true);
		modelDialog.setGlassEnabled(true);
		modelDialog.setModal(true);
		modelDialog.setCaption( AonStringUtils.abbreviate( caption , 60 ));
		return modelDialog;
	}

	@Override 
	public void visitM303() {
		LOGGER.info("Before visitM303");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model303 model303 = new Model303();
			Model303ModuleOptions options = new Model303ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( opt.getConfiguration().getDomain().getId() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallbackWrapper<>(modelDialog, callback) );
			model303.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************

	@Override public void visitM111() {}
	@Override public void visitM115() {}
	@Override public void visitM123() {}
	@Override public void visitM130() {}
	@Override public void visitM131() {}
	@Override public void visitM347() {}
	@Override public void visitM349() {}
	@Override public void visitM390() {}
	@Override public void visitM390HF() {}
	@Override public void visitM180() {}
	@Override public void visitM184() {}
	@Override public void visitM190() {}
	@Override public void visitM193() {}
	@Override public void visitM200() {}
	@Override public void visitM202() {}

}
