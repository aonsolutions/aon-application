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
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HFModuleOptions;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;

public class MatrixNewModelVisitor implements IFiscalModelTypeVisitor {
	
	private AonConfiguration config;
	private FiscalModel model;
	private AonModuleCallback<FiscalModel> callback;
	
	public MatrixNewModelVisitor(AonConfiguration config, FiscalModel model, AonModuleCallback<FiscalModel> callback) {
		this.config = config;
		this.model = model;
		this.callback = callback;
	}
	
	private static final Logger LOGGER = Logger.getLogger(MatrixNewModelVisitor.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private <T extends FiscalModel> AonModuleCallback<T> getExternalCallback(AonCustomPopup modelDialog, AonModuleCallback<FiscalModel> callback) {
		return new AonModuleCallbackWrapper<>(modelDialog,callback);
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
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod303 mod303 = new Mod303();
			FiscalModel.map(model,mod303);
			Model303 model303 = new Model303();
			Model303ModuleOptions options = new Model303ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod303);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( getExternalCallback(modelDialog, callback) );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model303.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override
	public void visitM111() {
		LOGGER.info("Before visitM111");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod111 mod111 = new Mod111();
			FiscalModel.map(model,mod111);
			Model111 model111 = new Model111();
			Model111ModuleOptions options = new Model111ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel( mod111 );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( getExternalCallback(modelDialog, callback) );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model111.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM115() {
		LOGGER.info("Before visitM115");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod115 mod115 = new Mod115();
			FiscalModel.map(model,mod115);
			Model115 model115 = new Model115();
			Model115ModuleOptions options = new Model115ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod115);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( getExternalCallback(modelDialog, callback) );
			model115.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	@Override 
	public void visitM123() {
		LOGGER.info("Before visitM123");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod123 mod123 = new Mod123();
			FiscalModel.map(model,mod123);
			Model123 model123 = new Model123();
			Model123ModuleOptions options = new Model123ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod123);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( getExternalCallback(modelDialog, callback) );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model123.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	@Override 
	public void visitM130() {
		LOGGER.info("Before visitM130");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod130 mod130 = new Mod130();
			FiscalModel.map(model,mod130);
			Model130 model130 = new Model130();
			Model130ModuleOptions options = new Model130ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod130);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( getExternalCallback(modelDialog, callback) );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model130.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	@Override 
	public void visitM131() {
		LOGGER.info("Before visitM131");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod131 mod131 = new Mod131();
			FiscalModel.map(model,mod131);
			Model131 model131 = new Model131();
			Model131ModuleOptions options = new Model131ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod131);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( getExternalCallback(modelDialog, callback) );
			model131.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	@Override 
	public void visitM202() {
		LOGGER.info("Before visitM202");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod202 mod202 = new Mod202();
			FiscalModel.map(model,mod202);
			Model202 model202 = new Model202();
			Model202ModuleOptions options = new Model202ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod202);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( getExternalCallback(modelDialog, callback) );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model202.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	@Override 
	public void visitM390HF(){
		LOGGER.info("Before visitM390HF");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod390HF mod390HF = new Mod390HF();
			FiscalModel.map(model,mod390HF);
			Model390HF model390HF = new Model390HF();
			Model390HFModuleOptions options = new Model390HFModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod390HF);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( getExternalCallback(modelDialog, callback) );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			LOGGER.info("Before visitM303 model390HF.onModuleLoad( options )");
			model390HF.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	private static final String ERROR = "Operaci\u00F3n no soportada";
	@Override public void visitM347()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }
	@Override public void visitM349()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }
	@Override public void visitM390()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }
	@Override public void visitM180()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }
	@Override public void visitM184()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }
	@Override public void visitM190()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }
	@Override public void visitM193()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }
	@Override public void visitM200()  { callback.onFailure( new UnsupportedOperationException( ERROR )); }

}
