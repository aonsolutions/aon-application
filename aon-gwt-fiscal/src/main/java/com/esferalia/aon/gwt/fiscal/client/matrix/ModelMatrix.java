package com.esferalia.aon.gwt.fiscal.client.matrix;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.fiscal.JsFiscalMenuItem;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ModelMatrix extends MainEntryPoint {

	
	private static final Logger LOGGER = Logger.getLogger(ModelMatrix.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	protected static final FiscalModelServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		FiscalModelServiceAsync serviceRaw = GWT.create(FiscalModelService.class);
		SERVICE = new FiscalModelServiceAsyncDecorator(serviceRaw);
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}
	
	private ScrollPanel scrollPanel; 
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {

			@Override public void onFailure(Throwable caught) { /* Nothing */ }

			@Override
			public void onSuccess(AonConfiguration aonConfiguration) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				MatrixModuleOptions options = new MatrixModuleOptions();
				options.setParentWidget(root);
				options.setDomainName(getCurrentDomainName());
				options.setDomain(getCurrentDomain());
				options.setUser(getCurrentUser());
				options.setConfiguration(aonConfiguration);
				onModuleLoad(options);
			}
		});
	}
	
	public void onModuleLoad(MatrixModuleOptions options) {
		AON.ensureInjected();
		
		DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
		ModelMatrixFilterPanel filterPanel = new ModelMatrixFilterPanel(options);
		filterPanel.addValueChangeHandler( event -> search( options, event.getValue() ));
		dockLayout.addNorth(filterPanel, 100);
		scrollPanel = new ScrollPanel();
		dockLayout.add(scrollPanel);
		options.getParentWidget().add(dockLayout);
	}

	private MatrixData sortInfo( AonJsArray<JsFiscalMenuItem> aonJsArray) {
		MatrixData matrixData = new MatrixData();
		aonJsArray.stream().forEach( matrixData::add );
		return matrixData;
	}

	private void search(MatrixModuleOptions options, FiscalMatrixParams params) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		API api = new API(GWT.getHostPageBaseURL(), 
				options.getConfiguration().getMd5(),
				options.getConfiguration().getDomain().getName(), 
				options.getConfiguration().getDomain().getId(),
				options.getConfiguration().getUser().getLogin());
		HashMap<String, LinkedList<String>> filterMap = new HashMap<>();
		LinkedList<String> yearListt = new LinkedList<>();
		yearListt.add(AonNumberUtils.toString(params.getYear()) );
		filterMap.put(IJsonNames.YEAR, yearListt);
		LinkedList<String> modelListt = new LinkedList<>();
		modelListt.add(params.getModel() == null ? "" : params.getModel().toString());
		filterMap.put(IJsonNames.MODEL, modelListt);
		LinkedList<String> admonListt = new LinkedList<>();
		admonListt.add(params.getAdministration()==null?"":params.getAdministration().toString());
		filterMap.put(IJsonNames.ADMINISTRATION, admonListt);
		LinkedList<String> scopeListt = new LinkedList<>();
		scopeListt.add(AonNumberUtils.toString(params.getScope()) );
		filterMap.put(IJsonNames.SCOPE, scopeListt);
		LinkedList<String> configuredVisibleListt = new LinkedList<>();
		configuredVisibleListt.add( params.isConfiguredVisible()?Boolean.TRUE.toString() : Boolean.FALSE.toString() );
		filterMap.put(IJsonNames.CONFIGURED_VISIBLE, configuredVisibleListt);
		LinkedList<String> madeModelsVisibleList = new LinkedList<>();
		madeModelsVisibleList.add( params.isMadeModelsVisible()?Boolean.TRUE.toString() : Boolean.FALSE.toString() );
		filterMap.put(IJsonNames.MADE_MODELS_VISIBLE, madeModelsVisibleList);
		api.getFiscal().getFiscalModels( filterMap, new AsyncCallback<JSON<JsFiscalMenuItem>>() {
			
			@Override
			public void onSuccess(JSON<JsFiscalMenuItem> result) {
				AonJsArray<JsFiscalMenuItem> aonJsArray = result.getData();
				if (aonJsArray == null || aonJsArray.length() == 0) {
					FlowPanel content = new FlowPanel();
					content.setStyleName(AON.CSS.aonMarginRight());
					content.addStyleName(AON.CSS.aonMarginLeft());
					content.addStyleName(AON.CSS.aonBlockCenter());
					
					Label noData = new Label(AON.MSG.noData());
					noData.setStyleName(AON.CSS.aonTextCenter());
					noData.addStyleName(AON.CSS.aonMarginTop());
					noData.addStyleName(AON.CSS.aonPadding());
					noData.addStyleName(AON.CSS.aonColorRed());
					noData.addStyleName(AON.CSS.aonBold());
					content.add(noData);
					scrollPanel.setWidget( content );
				} else {
					MatrixData matrixData = sortInfo(aonJsArray);
					scrollPanel.setWidget( new ModelMatrixPanel(options, matrixData , params));
				}
				popup.hide();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
			}
		});
	}
}
