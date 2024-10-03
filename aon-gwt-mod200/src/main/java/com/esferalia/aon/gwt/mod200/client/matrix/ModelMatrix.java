package com.esferalia.aon.gwt.mod200.client.matrix;


import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.mod200.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class ModelMatrix extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(ModelMatrix.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

//	protected static final FiscalModelServiceAsync SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
//		FiscalModelServiceAsync serviceRaw = GWT.create(FiscalModelService.class);
//		SERVICE = new FiscalModelServiceAsyncDecorator(serviceRaw);
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}
	
	private Panel dataPanel;
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;	
	private SimpleLayoutPanel aeatPanel;
	
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
					options.setCompactMode(false);
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
		filterPanel.addValueChangeHandler( event -> search( options, event.getValue(), filterPanel.getRefreshButton() ));
		if (options.isCompactMode()) {			
			//ScrollPanel mainScroll = new ScrollPanel();
			FlowPanel contentPanel = new FlowPanel();
			//mainScroll.setWidget(contentPanel);
			contentPanel.add(filterPanel);
			dataPanel = new FlowPanel();
			//if (!options.isCompactMode()) {
				dataPanel.setHeight( "320px" );
			//}
			dataPanel.getElement().getStyle().setOverflowY(Overflow.SCROLL);
			contentPanel.add(dataPanel);
			options.getParentWidget().add(contentPanel);
		} else {
			DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
			dockLayout.addNorth(filterPanel, 100);
			
			splitLayoutPanel = new SplitLayoutPanel(2);
			dockLayout.add(splitLayoutPanel);
			
			AonMinimizePanel minimizePanel = getMinimizePanel();
			minimizePanel.addStyleName("aon-Model-Info");
			splitLayoutPanel.addSouth(minimizePanel, 30);			
			
			dataPanel = new ScrollPanel();
			splitLayoutPanel.add(dataPanel);
			
			options.getParentWidget().add(dockLayout);
		}		
		
	}

	private void search(MatrixModuleOptions options, FiscalMatrixParams params, AonSearchPanelButton refreshButton) {
		
		if (!params.isMultiplePresentation()) {
			options.getSelected().clear();			
			options.setResult(null);
		}
		
		dataPanel.clear();
		dataPanel.add(new ModelMatrixPanel(options, params, refreshButton));
		
		if (!options.isCompactMode()) {			
			footPanel.setVisible(false);
			splitLayoutPanel.setWidgetSize(footPanel, 0);
			
			if (options.getResult() != null) {
				showHtml(options.getResult());
				footPanel.setVisible(true);
				maximizeFootPanel();
			}			
		}			
		
	}
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();		
		footPanel.addMinimizeHandler( event -> closeFootPanel() );
		footPanel.addMaximizeHandler( event -> maximizeFootPanel());
		footPanel.setStyleName(AON.CSS.aonSelector());		

		AonTabLayoutPanel tabLayout = new AonTabLayoutPanel(30, Unit.PX);
		tabLayout.setWidth("100%");
		footPanel.add(tabLayout);
		
		aeatPanel = new SimpleLayoutPanel();

		Label labelResult = new Label("RESULTADO DE LA PRESENTACION MULTIPLE");
		labelResult.setStyleName(AON.CSS.aonFontMedium());
		tabLayout.add(aeatPanel, labelResult);
		
		tabLayout.setAnimationDuration(300);
		
		return footPanel; 
	}
	
	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2.0);
		splitLayoutPanel.animate(500);
	}
	
	protected void showHtml(String dataURI) {
		aeatPanel.clear();
		Frame aeatFrame = new Frame( "data:text/html;base64," + dataURI);
		aeatFrame.setWidth("100%");
		aeatFrame.setHeight("100%");
		aeatFrame.setStyleName(AON.CSS.aonWidthAll());
		aeatFrame.addStyleName(AON.CSS.aonHeightAll());
		aeatFrame.addStyleName(AON.CSS.aonBlockCenter());
		aeatFrame.addStyleName(AON.CSS.aonBorderNone());
		aeatFrame.addStyleName(AON.CSS.aonBorderTop());
//		aeatFrame.addStyleName(AON.CSS.aonMarginTop());
		aeatPanel.add(aeatFrame);
	}

	
	
}
