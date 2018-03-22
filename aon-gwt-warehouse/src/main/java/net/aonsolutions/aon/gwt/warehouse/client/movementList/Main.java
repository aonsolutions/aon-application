package net.aonsolutions.aon.gwt.warehouse.client.movementList;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsStockStat;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class Main extends Composite {

	interface Binder extends UiBinder<Widget, Main> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	DockLayoutPanel contentDockLayoutPanel;
	@UiField
	SplitLayoutPanel contentSplitLayoutPanel;
	@UiField
	SimpleLayoutPanel northContent;
	@UiField
	SimpleLayoutPanel content;
	@UiField
	SimpleLayoutPanel southContent;
	
	private MovementList parent;
	private HashMap<String, LinkedList<String>> filterMap;
	
	public API getAPI() {
		return parent.getAPI();
	}
	

	public Main(MovementList parent, HashMap<String, LinkedList<String>> filterMap) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		if(filterMap==null){
			filterMap = new HashMap<>();
		}
		this.filterMap = filterMap;
		
		load();
	}
	
	public Main(MovementList parent) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		filterMap = new HashMap<>();
		
		load();
	}

	private void load() {
		loadWestContent();
		loadNorthContent();
		loadContent();
		loadSouthContent();
	}


	private void loadWestContent() {

	}

	private void loadNorthContent() {
		contentDockLayoutPanel.setWidgetSize(northContent, 85);
		northContent.setWidget(new FilterPanel(this));
	}

	public void loadContent() {
		final AonToast toast = new AonToast();
		final InlineLabel label =  new InlineLabel("Cargando el contenido...");
		toast.show("Procesando ...", label);
		
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		
		getAPI().getWarehouse().getProductMovements(filterMap, new AsyncCallback<JSON<JsStockStat>>() {

			@Override
			public void onSuccess(JSON<JsStockStat> result) {
				loadSouthContent();
				hideFooterPanel();
				content.setWidget(new GridPanel(parent, result.getData().toLinkedList()));
				toast.hide();
			}

			@Override
			public void onFailure(Throwable caught) {
				toast.hide();
				Window.alert("No se han podido recuperar los datos.");
			}
		});
	}
	
	public void cleanContent() {
		hideFooterPanel();
		content.setWidget(new GridPanel(parent, null));
	}

	public void loadSouthContent() {
		FooterPanel fp = new FooterPanel(this);
		southContent.setWidget(fp);
		hideFooterPanel();
	}
	
	public void loadSouthContent(JsStockStat product) {
		FooterPanel footer = new FooterPanel(this, product);
		southContent.setWidget(footer);
		openFooterPanel();
	}
	
	public void openFooterPanel() {
		Integer clientHeight = Window.getClientHeight();
		changeSouthContentSize(clientHeight.doubleValue() / 3);
	}

	public void closeFooterPanel() {
		changeSouthContentSize(30.0);
	}
	
	public void hideFooterPanel() {
		changeSouthContentSize(0.0);
	}
	
	private void changeSouthContentSize(Double value) {
		contentSplitLayoutPanel.setWidgetSize(southContent, value);	
	}


	public void onSelectProduct(JsStockStat object) {
		FilterPanel filter = (FilterPanel)northContent.getWidget();
		filter.selectProduct(object);
		
		loadSouthContent(object);
	}
	
	public void onSelectItem(JsStockStat item) {
		FilterPanel filter = (FilterPanel)northContent.getWidget();
		filter.selectItem(item);
		FooterPanel footer = (FooterPanel)southContent.getWidget();
		footer.selectItem(item);
	}
	
	
	
	/*
	 * FILTER
	 */
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}



		
}
