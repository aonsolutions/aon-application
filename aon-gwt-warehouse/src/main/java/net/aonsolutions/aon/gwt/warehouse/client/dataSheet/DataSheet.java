package net.aonsolutions.aon.gwt.warehouse.client.dataSheet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.product.JsProduct;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.FilterPanel;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperFabElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;

import net.aonsolutions.polymer.aon.AonComboBoxElement;

public class DataSheet extends AonTemplate2{

	private API API;
	private AonData aonData;
	private FilterPanel filterPanel;
	
	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public FilterPanel getFilterPanel() {
		return filterPanel;
	}
	
	public void setFilterPanel(FilterPanel filterPanel) {
		this.filterPanel = filterPanel;
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return getFilterPanel() != null ? getFilterPanel().getFilterMap() : new HashMap<>();
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		getFilterPanel().setFilterMap(filterMap);
	}
	
	public DataSheet(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
			aonData.getDomain().getName(), aonData.getDomain().getId(),
			aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
			IronIconsElement.SRC,
			"iron-icons/image-icons.html",
			"iron-icons/editor-icons.html",
			AonComboBoxElement.SRC,
			PaperButtonElement.SRC,
			PaperRadioButtonElement.SRC,
			PaperItemElement.SRC,
			PaperFabElement.SRC,
			PaperInputElement.SRC,
			PaperToggleButtonElement.SRC,
			"vaadin-mock-xhr"
		), o -> {
			startApplication();
			return null;
		});
		
		Polymer.whenReady(o -> {
			super.onModuleLoad();
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		toolbar();
		westContent();
		content();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		
		Toolbar toolbar = new Toolbar("Ficha T\u00e9cnica");
		toolbar.addButton("Volver", AON.AON_CSS.aonIconCancel(), false).addClickHandler(backClickHandler());
		
		setToolbar(toolbar);
	}
	
	private void westContent() {

	}
	
	private void content() {
		DockLayoutPanel dlp = new DockLayoutPanel(Unit.PX);
		SimpleLayoutPanel north = new SimpleLayoutPanel();
		north.addStyleName("aon-margin-left10 aon-margin-right10");
		north.setWidget(filterPanel());
		dlp.addNorth(north, 85);
		SimpleLayoutPanel center = new SimpleLayoutPanel();
		center.addStyleName("aon-margin-left10 aon-margin-right10");
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		getAPI().getProduct().getProductList(getFilterMap(), new AsyncCallback<JSON<JsProduct>>() {
			
			@Override
			public void onSuccess(JSON<JsProduct> result) {
				center.setWidget(gridPanel(result));
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		dlp.add(center);
		
		setContent(dlp);
	}
	
	public FilterPanel filterPanel() {
		return filterPanel(getFilterMap());
	}
	
	public FilterPanel filterPanel(HashMap<String, LinkedList<String>> map) {
		FilterPanel fp = new FilterPanel(map) {
			@Override
			protected void onClean() {
				content();
			}
			
			@Override
			protected void refresh() {
				DockLayoutPanel dlp = (DockLayoutPanel) getContent().getWidget();
				SimpleLayoutPanel center = (SimpleLayoutPanel) dlp.getWidget(1);
				LinkedList<String> list = new LinkedList<>();
				list.add("1");
				getFilterMap().put("page", list);
				list = new LinkedList<>();
				list.add("40");
				getFilterMap().put("per_page", list);
				getAPI().getProduct().getProductList(getFilterMap(), new AsyncCallback<JSON<JsProduct>>() {
					
					@Override
					public void onSuccess(JSON<JsProduct> result) {
						center.setWidget(gridPanel(result));
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		fp.getElement().getStyle().setLeft(10, Unit.PX);
		fp.getElement().getStyle().setRight(10, Unit.PX);
		
		fp.addTextFilter("text");
		
		setFilterPanel(fp);
		return getFilterPanel();
	}
	
	public DataSheetGridPanel gridPanel(JSON<JsProduct> result) {
		return new DataSheetGridPanel(this, result.getData().toLinkedList());
	}
	
	public void sheetContent(JsProduct js, HashMap<String, LinkedList<String>> map) {
		setFilterMap(map);
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		
		toolbar.getButtonPanel().getWidget(0).setVisible(true);

		setContent(new PaturpatDataSheet(this, js));
	}
	
	/***** BUTTON CLICK HANDLER *****/
	
	private ClickHandler backClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				startApplication();
			}
		};
	}

}
