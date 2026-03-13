package com.esferalia.aon.gwt.mod200.client.matrix;

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
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421ModuleOptions;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200ModuleOptions;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;

public class MatrixViewVisitor implements IFiscalModelTypeVisitor {
	
	private static final Logger LOGGER = Logger.getLogger(MatrixViewVisitor.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private MatrixModuleOptions opt;
	private IFiscalModel model;
	private AonModuleCallback<IFiscalModel> callback;
	
	public MatrixViewVisitor(MatrixModuleOptions opt, IFiscalModel model, AonModuleCallback<IFiscalModel> callback) {
		this.opt= opt;
		this.model = model;
		this.callback = callback;
	}
	private AonCustomPopup getModelDialog() {
		return getModelDialog(null);
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
		AonCustomPopup modelDialog = getModelDialog();
		try {
			Model111 model111 = new Model111();
			Model111ModuleOptions options = new Model111ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
			Model115 model115 = new Model115();
			Model115ModuleOptions options = new Model115ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
	
	@Override public void visitM123() {
		LOGGER.info("Before visitM123");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model123 model123 = new Model123();
			Model123ModuleOptions options = new Model123ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
			Model130 model130 = new Model130();
			Model130ModuleOptions options = new Model130ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
			Model131 model131 = new Model131();
			Model131ModuleOptions options = new Model131ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
			Model202 model202 = new Model202();
			Model202ModuleOptions options = new Model202ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model180 model180 = new Model180();
			Model180ModuleOptions options = new Model180ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod180>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod180 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod180 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod180 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
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
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model184 model184 = new Model184();
			Model184ModuleOptions options = new Model184ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod184>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod184 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod184 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod184 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			model184.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM190()  { 
		LOGGER.info("Before visit190");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model190 model190 = new Model190();
			Model190ModuleOptions options = new Model190ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod190>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod190 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod190 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod190 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
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
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model193 model193 = new Model193();
			Model193ModuleOptions options = new Model193ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod193>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod193 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod193 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod193 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			model193.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
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
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
			model303.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM347()  { 
		LOGGER.info("Before visit347");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model347 model347 = new Model347();
			Model347ModuleOptions options = new Model347ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
			model347.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM349()  { 
		LOGGER.info("Before visit349");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model349 model349 = new Model349();
			Model349ModuleOptions options = new Model349ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model390 model390 = new Model390();
			Model390ModuleOptions options = new Model390ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod390>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod390 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod390 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod390 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			model390.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM390HF() {
		LOGGER.info("Before visitM390HF");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model390HF model390HF = new Model390HF();
			Model390HFModuleOptions options = new Model390HFModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
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
			model390HF.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

	@Override 
	public void visitM200() {
		LOGGER.info("Before visitM200");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model200 model200 = new Model200();
			Model200ModuleOptions options = new Model200ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod200>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod200 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod200 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod200 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			model200.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}
	
	@Override
	public void visitM369() {
		// EL MODELO 369 AUN NO ESTA EN LA MATRIZ		
	}
	@Override
	public void visitM421() {
		LOGGER.info("Before visitM421");
		AonCustomPopup modelDialog = getModelDialog(AON.MSG.fiscalModelDescriptionlong(model.getModel()));
		try {
			Model421 model421 = new Model421();
			Model421ModuleOptions options = new Model421ModuleOptions();
			options.setParentWidget(modelDialog);
			options.setDomainName(opt.getConfiguration().getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(opt.getConfiguration().getUser().getLogin());
			options.setConfiguration(opt.getConfiguration());
			options.setFiscalModelId( model.getId() );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod421>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onRemove(Mod421 removed) {
					modelDialog.hide();
					callback.onRemove(removed);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				
				@Override
				public void onExit(Mod421 edited) {
					modelDialog.hide();
					callback.onExit(edited);
				}
				
				@Override
				public void onChange(Mod421 changed) {
					modelDialog.hide();
					callback.onChange(changed);
				}
				
			} );
			model421.onModuleLoad( options );
			modelDialog.center();
			modelDialog.show();
		} catch (Exception t) {
			callback.onFailure(t);
		}
	}

}
