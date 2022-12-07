package com.esferalia.aon.gwt.payroll.client;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;

public class MainCCC extends MainEntryPoint{
	
	// ----------------------------------------------- CCC Implementation
	
	private class CCCWidgetImpl extends CCC {

		@Override
		protected void onInsertRow() {
			// Not use in this case
		}
		
		@Override
		protected void onInsertRows() {
			mainCCCObject.getCCCs().forEach(ccc -> cccWidget.insertRow(ccc));
		}

		@Override
		protected void onDeleteCCC(Integer cccId) {
			mainCCCObject.deleteCCC(cccId);
		}

		@Override
		protected void onInsertCCC(EnterpriseCCC ccc) {
			mainCCCObject.insertCCC(ccc);
		}

		@Override
		protected Set<Entry<Integer, String>> getActivities() {
			return mainCCCObject.getActivities();
		}

		@Override
		protected void fireWarningMessage(Map<String, String> warningMap) {
			AonMessagePanel.showWarning(messagePanel, warningMap);
		}
		
	}

	// ----------------------------------------------- UiBinder
	
	interface Binder extends UiBinder<Widget, MainCCC> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
		String widthAll();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel centerContainer;
	
	@UiField 
	HTMLPanel messagePanel;
	
	// ----------------------------------------------- Variables
	
	private MainCCCObject mainCCCObject;
	
	private CCC cccWidget;
	
	private AonToolbar toolbar;
	
	// ----------------------------------------------- Constructor

	public MainCCC() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		getToolbarPanel();
		cccWidget = new CCCWidgetImpl();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		centerContainer.add(cccWidget);
		centerContainer.addStyleName(style.widthAll());
		centerContainer.getElement().getStyle().setMarginTop(40, Unit.PX);
	}
	
	// ----------------------------------------------- onModuleLoad
	
	@Override
	public void onModuleLoad() {
		onModuleLoad( new MainCCCObject());
	}

	public void onModuleLoad(MainCCCObject mainCCCObject) {
		this.mainCCCObject = mainCCCObject;
		this.mainCCCObject.getMainCCCInfo(
				s -> {
					cccWidget.setDomain(mainCCCObject.getDomain());
					cccWidget.onInsertRows();
					cccWidget.calculateScrollPanelHeightMainCCC();
				}, f -> {});
	}
	
	// ----------------------------------------------- Toolbar

	private void getToolbarPanel() {
		
		this.toolbar = new AonToolbar("C\u00F3digo Cuentas Cotizaci\u00F3n");

		AonToolbarButton accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.addClickHandler(e -> onAccept());
		toolbar.add(accept);
		
		AonToolbarButton checkUpdateCert = new AonToolbarButton("Cert. de estar al corriente con TGSS", AON.CSS.aonIconTgss() );
		checkUpdateCert.addClickHandler(e -> onCheckUpdateCert());
		toolbar.add(checkUpdateCert);
	}
	
	private void onAccept() {
		this.mainCCCObject.setMainCCCInfo(s -> {
			cccWidget.resetPreview();
			cccWidget.onInsertRows();
		}, f -> {});
	}
	
	// ----------------------------------------------- Toolbar.Methods TGSS
	
	private void onCheckUpdateCert() {
		submitForm(0);
	}
	
	private void submitForm(int type) {
		Pair<String, String> completeCCC = mainCCCObject.getPrincipalAccount();
		
		String fileDownloadURL = GWT.getModuleBaseURL() + "sistema_red_ccc";

		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(fileDownloadURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden("ccc", completeCCC.getValue()));
		flowPanel.add(new Hidden("regime", completeCCC.getKey()));
		flowPanel.add(new Hidden("type", Integer.toString(type)));
		flowPanel.add(new Hidden("userLogin", Wnd.getCurrentUser()));
		flowPanel.add(new Hidden("domainName", Wnd.getCurrentDomainNameURL()));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> centerContainer.remove(formPanel));

		centerContainer.add(formPanel);

		formPanel.submit();
	}

}
