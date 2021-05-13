package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.shared.AonData;
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
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200ModuleOptions;
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
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HF;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HFModuleOptions;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

public class MatrixNewModelVisitor implements IFiscalModelTypeVisitor {
	
	private AonData aonData;
	private FiscalModel model;
	
	public MatrixNewModelVisitor(AonData aonData, FiscalModel model) {
		this.aonData = aonData;
		this.model = model;
	}
	
	private static final Logger LOGGER = Logger.getLogger(MatrixNewModelVisitor.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	@Override
	public void visitM111() {
		LOGGER.info("Before visitM111");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod111 mod111 = new Mod111();
			FiscalModel.map(model,mod111);
			Model111 model111 = new Model111();
			Model111ModuleOptions options = new Model111ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel( mod111 );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod111>() {
				
				@Override
				public void onRemove(Mod111 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod111 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod111 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			model111.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM115() {
		LOGGER.info("Before visitM115");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod115 mod115 = new Mod115();
			FiscalModel.map(model,mod115);
			Model115 model115 = new Model115();
			Model115ModuleOptions options = new Model115ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod115);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod115>() {
				
				@Override
				public void onRemove(Mod115 removed) {
					hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod115 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod115 changed) {
					hide();
				}
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			model115.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM123() {
		LOGGER.info("Before visitM123");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod123 mod123 = new Mod123();
			FiscalModel.map(model,mod123);
			Model123 model123 = new Model123();
			Model123ModuleOptions options = new Model123ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod123);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod123>() {
				
				@Override
				public void onRemove(Mod123 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod123 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod123 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			model123.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM130() {
		LOGGER.info("Before visitM130");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod130 mod130 = new Mod130();
			FiscalModel.map(model,mod130);
			Model130 model130 = new Model130();
			Model130ModuleOptions options = new Model130ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod130);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod130>() {
				
				@Override
				public void onRemove(Mod130 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod130 mod130) {
					hide();
				}
				
				@Override
				public void onChange(Mod130 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			model130.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM131() {
		LOGGER.info("Before visitM131");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod131 mod131 = new Mod131();
			FiscalModel.map(model,mod131);
			Model131 model131 = new Model131();
			Model131ModuleOptions options = new Model131ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod131);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod131>() {
				
				@Override
				public void onRemove(Mod131 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod131 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod131 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			model131.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM202() {
		LOGGER.info("Before visitM202");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod202 mod202 = new Mod202();
			FiscalModel.map(model,mod202);
			Model202 model202 = new Model202();
			Model202ModuleOptions options = new Model202ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod202);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod202>() {
				
				@Override
				public void onRemove(Mod202 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod202 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod202 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			model202.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM303() {
		LOGGER.info("Before visitM303");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod303 mod303 = new Mod303();
			FiscalModel.map(model,mod303);
			Model303 model303 = new Model303();
			Model303ModuleOptions options = new Model303ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod303);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod303>() {
				
				@Override
				public void onRemove(Mod303 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod303 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod303 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			model303.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM390HF(){
		LOGGER.info("Before visitM390HF");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod390HF mod390HF = new Mod390HF();
			FiscalModel.map(model,mod390HF);
			Model390HF model390HF = new Model390HF();
			Model390HFModuleOptions options = new Model390HFModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod390HF);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod390HF>() {
				
				@Override
				public void onRemove(Mod390HF removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod390HF edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod390HF changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model390HF.onModuleLoad( options )");
			model390HF.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM180() {
		LOGGER.info("Before visitM180");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod180 mod180 = new Mod180();
			mod180.setDomain(model.getDomain());
			mod180.setAdministration(model.getAdministration());
			mod180.setYear(model.getYear());
			Model180 model180 = new Model180();
			Model180ModuleOptions options = new Model180ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod180);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod180>() {
				
				@Override
				public void onRemove(Mod180 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod180 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod180 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model180.onModuleLoad( options )");
			model180.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	@Override 
	public void visitM184() {
		LOGGER.info("Before visitM184");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod184 mod184 = new Mod184();
			mod184.setDomain(model.getDomain());
			mod184.setAdministration(model.getAdministration());
			mod184.setYear(model.getYear());
			Model184 model184 = new Model184();
			Model184ModuleOptions options = new Model184ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel( mod184 );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod184>() {
				
				@Override
				public void onRemove(Mod184 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod184 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod184 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model184.onModuleLoad( options )");
			model184.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM190() {
		LOGGER.info("Before visitM190");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod190 mod190 = new Mod190();
			mod190.setDomain(model.getDomain());
			mod190.setAdministration(model.getAdministration());
			mod190.setYear(model.getYear());
			Model190 model190 = new Model190();
			Model190ModuleOptions options = new Model190ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel( mod190 );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod190>() {
				
				@Override
				public void onRemove(Mod190 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod190 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod190 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model190.onModuleLoad( options )");
			model190.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM193() {
		LOGGER.info("Before visitM193");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod193 mod193 = new Mod193();
			mod193.setDomain(model.getDomain());
			mod193.setAdministration(model.getAdministration());
			mod193.setYear(model.getYear());
			Model193 model193 = new Model193();
			Model193ModuleOptions options = new Model193ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod193);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod193>() {
				
				@Override
				public void onRemove(Mod193 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod193 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod193 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model193.onModuleLoad( options )");
			model193.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM347() {
		LOGGER.info("Before visitM347");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod347 mod347 = new Mod347();
			mod347.setDomain(model.getDomain());
			mod347.setAdministration(model.getAdministration());
			mod347.setYear(model.getYear());
			Model347 model347 = new Model347();
			Model347ModuleOptions options = new Model347ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod347);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod347>() {
				
				@Override
				public void onRemove(Mod347 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod347 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod347 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model347.onModuleLoad( options )");
			model347.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM349() {
		LOGGER.info("Before visitM349");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod349 mod349 = new Mod349();
			mod349.setDomain(model.getDomain());
			mod349.setAdministration(model.getAdministration());
			mod349.setYear(model.getYear());
			mod349.setPeriod(model.getPeriod());
			Model349 model349 = new Model349();
			Model349ModuleOptions options = new Model349ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod349);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod349>() {
				
				@Override
				public void onRemove(Mod349 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod349 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod349 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model349.onModuleLoad( options )");
			model349.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM390() {
		LOGGER.info("Before visitM390");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod390 mod390 = new Mod390();
			mod390.setDomain(model.getDomain());
			mod390.setAdministration(model.getAdministration());
			mod390.setYear(model.getYear());
			Model390 model390 = new Model390();
			Model390ModuleOptions options = new Model390ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel( mod390 );
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod390>() {
				
				@Override
				public void onRemove(Mod390 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod390 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod390 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model390.onModuleLoad( options )");
			model390.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}

	@Override 
	public void visitM200() {
		LOGGER.info("Before visitM200");
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(model.getModel()) , 60 ));
		try {
			Mod200 mod200 = new Mod200();
			mod200.setDomain(model.getDomain());
			mod200.setAdministration(model.getAdministration());
			mod200.setYear(model.getYear());
			Model200 model200 = new Model200();
			Model200ModuleOptions options = new Model200ModuleOptions();
			options.setParentWidget(entryDialog);
			options.setDomainName(aonData.getDomain().getName());
			options.setDomain( model.getDomain() );
			options.setUser(aonData.getUser().getLogin());
			options.setAonData(aonData);
			options.setNewModel(mod200);
			options.setEmbedded(true);
			options.setBackButtonVisible(true);
			options.setExternalCallback( new AonModuleCallback<Mod200>() {
				
				@Override
				public void onRemove(Mod200 removed) {
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit(Mod200 edited) {
					hide();
				}
				
				@Override
				public void onChange(Mod200 changed) {
					hide();
				}
				
				private void hide() {
					entryDialog.hide();
					entryDialog.clear();
				}
			});
			entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					entryDialog.clear();
				}
			});
			LOGGER.info("Before visitM303 model200.onModuleLoad( options )");
			model200.onModuleLoad( options );
			entryDialog.center();
			entryDialog.show();
		} catch (Throwable t) {
			Window.alert("Error inesperado! [" + t.getMessage() + "]");
		}
	}
	
	

}
