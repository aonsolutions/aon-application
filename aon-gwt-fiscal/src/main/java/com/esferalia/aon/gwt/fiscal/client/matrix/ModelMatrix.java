package com.esferalia.aon.gwt.fiscal.client.matrix;


import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
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
	
	private Panel dataPanel;
	

	@Override
	public void onModuleLoad() {
		onModuleLoad((String) null);
	}
	
	public void onModuleLoad(String elementTarget) {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {

			@Override public void onFailure(Throwable caught) { /* Nothing */ }

			@Override
			public void onSuccess(AonConfiguration aonConfiguration) {
				MatrixModuleOptions options = new MatrixModuleOptions();
				if (AonStringUtils.isBlank(elementTarget)) {
					RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
					options.setParentWidget(rootLayoutPanel);
				} else {
					RootPanel rootPanel = RootPanel.get(elementTarget);
					options.setParentWidget(rootPanel);
					options.setCompactMode(true);
				}
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
		ModelMatrixFilterPanel filterPanel = new ModelMatrixFilterPanel(options);
		filterPanel.addValueChangeHandler( event -> search( options, event.getValue() ));
		if (options.isCompactMode()) {
			
			ScrollPanel mainScroll = new ScrollPanel();
			FlowPanel contentPanel = new FlowPanel();
			mainScroll.setWidget(contentPanel);
			contentPanel.add(filterPanel);
			dataPanel = new FlowPanel();			
			if (!options.isCompactMode()) {
				dataPanel.setHeight( "320px" );
			}
			dataPanel.getElement().getStyle().setOverflowY(Overflow.SCROLL);
			contentPanel.add(dataPanel);
			options.getParentWidget().add(contentPanel);
		} else {
			DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
			dockLayout.addNorth(filterPanel, 100);
			dataPanel = new ScrollPanel();
			dockLayout.add(dataPanel);
			options.getParentWidget().add(dockLayout);
		}
		
		
	}

	private void search(MatrixModuleOptions options, FiscalMatrixParams params) {
		dataPanel.clear();
		dataPanel.add( new ModelMatrixPanel(options, params));
	}
	
}
