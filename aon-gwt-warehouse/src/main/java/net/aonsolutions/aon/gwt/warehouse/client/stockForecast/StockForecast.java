package net.aonsolutions.aon.gwt.warehouse.client.stockForecast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsStockForecast;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;

import net.aonsolutions.polymer.aon.AonComboBoxElement;

public class StockForecast extends AonTemplate2 {

	protected API API;
	private HashMap<String, LinkedList<String>> filterMap;
	private StockForecast me = this;

	public StockForecast(AonData aonData) {
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(), aonData.getDomain().getName(),
				aonData.getDomain().getId(), aonData.getUser().getLogin());
	}

	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		Polymer.importHref(Arrays.asList(IronIconsElement.SRC, AonComboBoxElement.SRC, PaperButtonElement.SRC,
				PaperRadioButtonElement.SRC, PaperItemElement.SRC)
		,o -> {
			load();
			return null;
		});
		Polymer.whenReady(o -> {
			load();
			return null;
		});
	}

	private void load() {
		loadToolbar();
		loadContent();
	}

	private void loadToolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar() {
			
			@Override
			protected void search() {
				Integer page = Integer.parseInt(filterMap.get("page").get(0));
				page = page + 1;
				LinkedList<String> list = new LinkedList<>();
				list.add(page.toString());
				filterMap.put("page", list);
				list = new LinkedList<>();
				list.add("1");
				filterMap.put("per_page", list);
				
				getAPI().getWarehouse().getStockForecast(filterMap, new AsyncCallback<JSON<JsStockForecast>>() {

					@Override
					public void onSuccess(JSON<JsStockForecast> result) {
						getContent().setWidget(new GridPanel(me, result.getData().toLinkedList()));
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}
			
			@Override
			protected void accept() {
			}

			@Override
			protected void reset() {
			}

			@Override
			protected void remove() {
			}

			@Override
			protected void back() {
			}

			@Override
			protected void download() {
				downloadStockforecast();
			}
			
			@Override
			protected void backward() {
			}

			@Override
			protected void forward() {
			}
		};
		
		toolbar.search.setVisible(false);
		toolbar.back.setVisible(false);
		toolbar.reset.setVisible(false);
		toolbar.accept.setVisible(false);
		toolbar.remove.setVisible(false);
		toolbar.download.setVisible(true);
		toolbar.setBackwardVisible(false);
		toolbar.setForwardVisible(false);
		toolbar.title.setText("Aprovisionamiento segun consumo");
		toolbar.subtitle.setText("Listado");
		setToolbar(toolbar);
	}

	public void loadContent() {
		setContent(new Main(this, filterMap));
	}
	
	private void downloadStockforecast() {
//		consoleLog("StockForecast/" + getFilter(getFilterMap()));
		HashMap<String, LinkedList<String>> map = ((Main)getContent().getWidget()).getFilterMap();
//		consoleLog("Main         /" + getFilter(map));
		getAPI().getWarehouse().downloadStockForecast(map);
	}
	
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		if(filterMap==null) {
			filterMap = new HashMap<>();
		}
		return filterMap;
	}

	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}

	public API getAPI() {
		return API;
	}

	public static native void consoleLog( String message) 
	/*-{
	    console.log( message );
	}-*/;
	
}
