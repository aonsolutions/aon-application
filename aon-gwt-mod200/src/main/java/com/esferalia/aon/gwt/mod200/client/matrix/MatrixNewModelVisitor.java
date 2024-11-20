package com.esferalia.aon.gwt.mod200.client.matrix;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
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
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HFModuleOptions;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200ModuleOptions;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200Table;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;

public class MatrixNewModelVisitor implements IFiscalModelTypeVisitor {
	
	private AonConfiguration config;
	private IFiscalModel model;
	private final AonModuleCallback<IFiscalModel> callback;
	
	public MatrixNewModelVisitor(AonConfiguration config, IFiscalModel model, AonModuleCallback<IFiscalModel> callback) {
		this.config = config;
		this.model = model;
		this.callback = callback;
	}
	
	private static final Logger LOGGER = Logger.getLogger(MatrixNewModelVisitor.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
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
			Mod111 mod111 = new Mod111();
			MatrixUtils.map(model,mod111);
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
			options.setExternalCallback( new AonModuleCallback<Mod111>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod111 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod111 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod111 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
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
			MatrixUtils.map(model,mod115);
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
			options.setExternalCallback( new AonModuleCallback<Mod115>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod115 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod115 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod115 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
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
			MatrixUtils.map(model,mod123);
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
			options.setExternalCallback( new AonModuleCallback<Mod123>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod123 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod123 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod123 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
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
			MatrixUtils.map(model,mod130);
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
			options.setExternalCallback( new AonModuleCallback<Mod130>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod130 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod130 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod130 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
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
			MatrixUtils.map(model,mod131);
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
			options.setExternalCallback( new AonModuleCallback<Mod131>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod131 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod131 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod131 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
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
			MatrixUtils.map(model,mod202);
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
			options.setExternalCallback( new AonModuleCallback<Mod202>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod202 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod202 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod202 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model202.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	@Override 
	public void visitM180()  { 
		LOGGER.info("Before visit180");
		final AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod180 mod180 = new Mod180();
			MatrixUtils.map(model,mod180);
			Model180 model180 = new Model180();
			Model180ModuleOptions options = new Model180ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod180);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			final AonModuleCallback<Mod180> extCallback = new AonModuleCallback<Mod180>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod180 removed) {
					hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod180 edited) {
					hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod180 changed) {
					hide();
					callback.onChange(changed);
				}
				
				private void hide() {
					modelDialog.hide();
					modelDialog.clear();
				}
			};
			options.setExternalCallback( extCallback );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			LOGGER.info("Before visitM180 model180.onModuleLoad( options )");
			model180.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	@Override 
	public void visitM184()  { 
		LOGGER.info("Before visit184");
		final AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod184 mod184 = new Mod184();
			MatrixUtils.map(model,mod184);
			Model184 model184 = new Model184();
			Model184ModuleOptions options = new Model184ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod184);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			final AonModuleCallback<Mod184> extCallback = new AonModuleCallback<Mod184>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod184 removed) {
					hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod184 edited) {
					hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod184 changed) {
					hide();
					callback.onChange(changed);
				}
				
				private void hide() {
					modelDialog.hide();
					modelDialog.clear();
				}
			};
			options.setExternalCallback( extCallback );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			LOGGER.info("Before visitM184 model184.onModuleLoad( options )");
			model184.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM190()  { 
		LOGGER.info("Before visitM190");
		final AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod190 mod190 = new Mod190();
			MatrixUtils.map(model,mod190);
			Model190 model190 = new Model190();
			Model190ModuleOptions options = new Model190ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod190);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			final AonModuleCallback<Mod190> extCallback = new AonModuleCallback<Mod190>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod190 removed) {
					hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod190 edited) {
					hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod190 changed) {
					hide();
					callback.onChange(changed);
				}
				
				private void hide() {
					modelDialog.hide();
					modelDialog.clear();
				}
			};
			options.setExternalCallback( extCallback );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			LOGGER.info("Before visitM190 model190.onModuleLoad( options )");
			model190.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM193()  { 
		LOGGER.info("Before visit193");
		final AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod193 mod193 = new Mod193();
			MatrixUtils.map(model,mod193);
			Model193 model193 = new Model193();
			Model193ModuleOptions options = new Model193ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod193);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			final AonModuleCallback<Mod193> extCallback = new AonModuleCallback<Mod193>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod193 removed) {
					hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod193 edited) {
					hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod193 changed) {
					hide();
					callback.onChange(changed);
				}
				
				private void hide() {
					modelDialog.hide();
					modelDialog.clear();
				}
			};
			options.setExternalCallback( extCallback );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			LOGGER.info("Before visitM193 model193.onModuleLoad( options )");
			model193.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM303() {
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod303 mod303 = new Mod303();
			MatrixUtils.map(model,mod303);
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
			options.setExternalCallback( new AonModuleCallback<Mod303>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod303 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod303 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod303 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model303.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM347() {
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod347 mod347 = new Mod347();
			MatrixUtils.map(model,mod347);
			Model347 model347 = new Model347();
			Model347ModuleOptions options = new Model347ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod347);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod347>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod347 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod347 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod347 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model347.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM349() {
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod349 mod349 = new Mod349();
			LOGGER.info("Before map249: model.getPeriod="+model.getPeriod());
			MatrixUtils.map(model,mod349);
			LOGGER.info("After map249: mod349.getPeriod="+mod349.getPeriod());
			Model349 model349 = new Model349();
			Model349ModuleOptions options = new Model349ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod349);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod349>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod349 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod349 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod349 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			model349.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM390()  { 
		LOGGER.info("Before visit390");
		final AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Mod390 mod390 = new Mod390();
			MatrixUtils.map(model,mod390);
			Model390 model390 = new Model390();
			Model390ModuleOptions options = new Model390ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(config.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(config.getUser().getLogin());
			options.setConfiguration(config);
			options.setNewModel(mod390);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			final AonModuleCallback<Mod390> extCallback = new AonModuleCallback<Mod390>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod390 removed) {
					hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod390 edited) {
					hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod390 changed) {
					hide();
					callback.onChange(changed);
				}
				
				private void hide() {
					modelDialog.hide();
					modelDialog.clear();
				}
			};
			options.setExternalCallback( extCallback );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			LOGGER.info("Before visitM390 model390.onModuleLoad( options )");
			model390.onModuleLoad( options );
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
			MatrixUtils.map(model,mod390HF);
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
			options.setExternalCallback( new AonModuleCallback<Mod390HF>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod390HF removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod390HF edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod390HF changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			modelDialog.addCloseHandler(event -> modelDialog.clear());
			LOGGER.info("Before visitM390HF model390HF.onModuleLoad( options )");
			model390HF.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM200()  { 
		// Se controla que el modelo 200 no se pueda añadir, si aún no está disponible
		if (model.getYear() <= Model200Table.MAX_YEAR && model.getYear() != Model200Table.BETA_YEAR) {
			LOGGER.info("Before visit200");
			final AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));			
			try {
				Mod200 mod200 = new Mod200();
				MatrixUtils.map(model,mod200);
				Model200 model200 = new Model200();
				Model200ModuleOptions options = new Model200ModuleOptions();
				options.setParentWidget(modelDialog);
				options.setDomainName(config.getDomain().getName());
				options.setDomain( model.getDomain() );
				options.setUser(config.getUser().getLogin());
				options.setConfiguration(config);
				options.setNewModel(mod200);
				options.setEmbedded(true);
				options.setBackButtonVisible(true);
				final AonModuleCallback<Mod200> extCallback = new AonModuleCallback<Mod200>() {
					
					private static final long serialVersionUID = 1L;
	
					@Override
					public void onRemove(Mod200 removed) {
						hide();
						callback.onRemove(removed);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onExit(Mod200 edited) {
						hide();
						callback.onExit(edited);
					}
					
					@Override
					public void onChange(Mod200 changed) {
						hide();
						callback.onChange(changed);
					}
					
					private void hide() {
						modelDialog.hide();
						modelDialog.clear();
					}
				};
				options.setExternalCallback( extCallback );
				modelDialog.addCloseHandler(event -> modelDialog.clear());
				LOGGER.info("Before visitM200 model200.onModuleLoad( options )");
				model200.onModuleLoad( options );
				modelDialog.center();
				modelDialog.show();
			} catch (Exception t) {
				callback.onFailure(t);
			}
		} else {
			AonMessageDialog.warning("El modelo 200 para el ejercicio " + model.getYear() + ", a\u00FAn no est\u00E1 disponible.");
		}
	}

	@Override
	public void visitM369() {
		// EL MODELO 369 AUN NO ESTA EN LA MATRIZ		
	}	

}
