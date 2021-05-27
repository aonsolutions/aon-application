package net.aonsolutions.aon.gwt.udapa.client.quality.paturpat;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaborationDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperItem;

import net.aonsolutions.aon.gwt.udapa.client.Utils;

public class PrincipalFootPanel extends Composite {

	interface Binder extends UiBinder<Widget, PrincipalFootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
		
	
	PaturpatQuality parent;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public PrincipalFootPanel(PaturpatQuality parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		incomePanel();
	}

	public void incomePanel() {
		HashMap<String,LinkedList<String>> map = new HashMap<>();

		LinkedList<String>list = new LinkedList<>();
		list.add("quality");
		map.put("quality", list);
		map.put("cpnotnull", list);
		
		getAPI().getWarehouse().getElaborationDetail( map, new AsyncCallback<JSON<JsElaborationDetail>>() {
			
			@Override
			public void onSuccess(JSON<JsElaborationDetail> result) {
				VerticalPanel vp = new VerticalPanel();	
				vp.addStyleName(AON.AON_CSS.aonWidthAll());
				result.getData().stream().forEach(r -> {
					HorizontalPanel hp = new HorizontalPanel();
					hp.addStyleName(AON.AON_CSS.aonWidthAll());
					PaperItem pi = new PaperItem();
					IronIcon ironIcon = new IronIcon();
					ironIcon.setIcon("receipt");
					pi.add(ironIcon);
					
					pi.add(new Label(r.getItem().getName() + " #" + r.getItem().getSerialNumber()));
				    pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
				    
				    PaperIconButton nuevo = new PaperIconButton();
				    nuevo.setStyle("height: 24px;padding: 0px;position: absolute;right: 30px;");
				    nuevo.setIcon("add");
				    
				    nuevo.addClickHandler(new ClickHandler() {
					
				    	@Override public void onClick(ClickEvent event) {
							// TODO 
				    		JSONObject dataResponse = new JSONObject();
							dataResponse.put("code", new JSONString(r.getItem().getSerialNumber()));	
							Date date = Utils.parse("yyyy/MM/dd", r.getDate());
							String dateTime = Utils.formatDateTime(date);
							dataResponse.put("date", new JSONString(dateTime));
							dataResponse.put("source", new JSONNumber(DataResponseSource.PATURPAT_QUALITY.value()));
							dataResponse.put("source_id", new JSONString(r.getId() + ""));
							getAPI().getCommon().insertDataResponse2(JsonUtils.stringify(dataResponse.getJavaScriptObject()), new AsyncCallback<JsDataResponse>() {
								
								@Override
								public void onSuccess(JsDataResponse result) {
									parent.sheetContent(result, parent.getFilterMap());
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
						}
					});
				    
					hp.add(pi);
					hp.add(nuevo);
					vp.add(hp);
				});
				incomePanel.setWidget(vp);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabPanel;
	@UiField ScrollPanel incomePanel;
	
	public TabLayoutPanel getTabPanel() {
		return tabPanel;
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	public void openFootPanel() {
		Integer clientHeight = Window.getClientHeight();
		parent.southContentSize(clientHeight.doubleValue() / 3);
	}
	
	public void closeFootPanel() {
		parent.southContentSize(30.0);
	}
}
