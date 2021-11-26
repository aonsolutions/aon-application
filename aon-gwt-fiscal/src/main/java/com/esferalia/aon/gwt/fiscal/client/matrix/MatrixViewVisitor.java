package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202ModuleOptions;
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
	public void visitM111() {
		LOGGER.info("Before visitM111");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model111 model111 = new Model111();
			Model111ModuleOptions options = new Model111ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( opt.getConfiguration().getDomain().getId() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallbackWrapper<>(modelDialog, callback) );
			model111.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM115() {
		LOGGER.info("Before visitM115");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model115 model115 = new Model115();
			Model115ModuleOptions options = new Model115ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( opt.getConfiguration().getDomain().getId() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallbackWrapper<>(modelDialog, callback) );
			model115.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override public void visitM123() {
		LOGGER.info("Before visitM123");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model123 model123 = new Model123();
			Model123ModuleOptions options = new Model123ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( opt.getConfiguration().getDomain().getId() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallbackWrapper<>(modelDialog, callback) );
			model123.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM130() {
		LOGGER.info("Before visitM130");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model130 model130 = new Model130();
			Model130ModuleOptions options = new Model130ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( opt.getConfiguration().getDomain().getId() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallbackWrapper<>(modelDialog, callback) );
			model130.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM131() {
		LOGGER.info("Before visitM131");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model131 model131 = new Model131();
			Model131ModuleOptions options = new Model131ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( opt.getConfiguration().getDomain().getId() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallbackWrapper<>(modelDialog, callback) );
			model131.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM202() {
		LOGGER.info("Before visitM202");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model202 model202 = new Model202();
			Model202ModuleOptions options = new Model202ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( opt.getConfiguration().getDomain().getId() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallbackWrapper<>(modelDialog, callback) );
			model202.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
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

	@Override public void visitM347() {}
	@Override public void visitM349() {}
	@Override public void visitM390() {}
	@Override public void visitM390HF() {}
	@Override public void visitM180() {}
	@Override public void visitM184() {}
	@Override public void visitM190() {}
	@Override public void visitM193() {}
	@Override public void visitM200() {}

}
