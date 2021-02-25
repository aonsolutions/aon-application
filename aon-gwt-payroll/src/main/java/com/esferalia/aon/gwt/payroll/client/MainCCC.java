package com.esferalia.aon.gwt.payroll.client;

import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MainCCC extends MainEntryPoint{
	
	private class CCCWidgetImpl extends CCC {

		@Override
		protected void onInsertRows() {
			for(CCCInfo cccInfo : mainCCCObject.getCCCs()) {
				cccWidget.insertRow(cccInfo);
			}
		}

		@Override
		protected void onDeleteCCC(Integer cccId) {
			mainCCCObject.deleteCCC(cccId);
		}

		@Override
		protected void onInsertCCC(Integer cccId, int activityId, byte cccRegime, String cccRegimeCode, String account, String province, String provinceCode) {
			mainCCCObject.insertCCC(
					cccId, 
					activityId, 
					cccRegime, 
					cccRegimeCode,  
					account, 
					province, 
					provinceCode);
		}

		@Override
		protected Set<Entry<Integer, String>> getActivities() {
			return mainCCCObject.getActivities();
		}
		
	}

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface Binder extends UiBinder<Widget, MainCCC> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private MainCCCObject mainCCCObject;
	private Integer newId;
	
	private CCC cccWidget;
	
	private AonToolbar toolbar;
	private AonToolbarButton accept;
	private AonToolbarButton newCCC;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public MainCCC() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		toolbar = getToolbarPanel();
		cccWidget = new CCCWidgetImpl();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.add(cccWidget);
		dockLayoutPanel.addStyleName(style.container());
		
		this.newId = -1;
	}
	
	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void onModuleLoad(MainCCCObject mainCCCObject) {
		this.mainCCCObject = mainCCCObject;
		this.mainCCCObject.getMainCCCInfo(
				s -> {
					cccWidget.onInsertRows();
					cccWidget.calculateScrollPanelHeightMainCCC();
				}, f -> {});
	}

	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("C" + String.valueOf("\u00F3") + "digo Cuentas Cotizaci" + String.valueOf("\u00F3") + "n");

		newCCC = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newCCC.setAccessKey('N');
		newCCC.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onNewCCC(event);
			}
		});
		toolbar.add(newCCC);
		
		accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.setAccessKey('G');
		accept.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept(event);
			}
		});
		toolbar.add(accept);

		return toolbar;

	}
	
	private void onAccept(ClickEvent event) {
		this.mainCCCObject.setMainCCCInfo(s -> {
			cccWidget.resetPreview();
			cccWidget.onInsertRows();
		}, f -> {});
	}
	
	private void onNewCCC(ClickEvent event) {
		if(0 != cccWidget.getRowCount()) {
			Label firstGeozone = (Label) cccWidget.getWidget(0, 3);
			if(null != firstGeozone && "" != firstGeozone.getText()) {
				this.newId = cccWidget.insertNewRow(this.newId);
			}
		}else
			this.newId = cccWidget.insertNewRow(this.newId);
	}

}
