package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTemplate;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperFabElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;
import net.aonsolutions.aon.gwt.udapa.client.IUdapaAsync;
import net.aonsolutions.aon.gwt.udapa.client.Utils;
import net.aonsolutions.aon.gwt.vaadin.client.FilterPanel;
import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;
import net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEvent;
import net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEventHandler;

public class UdapaQuality extends AonTemplate {

	final IUdapaAsync impl = GWT.create(IUdapa.class);
	private API API;
	private UdapaQuality me = this;
	
	private AonData aonData;
	private FilterPanel filterPanel;
	private Boolean isBack = false;
	
	private AonToolbarButton backButton;
	private AonToolbarButton newButton;
	private AonToolbarButton deleteButton;
	private AonToolbarButton printPdfButton;
	private AonToolbarButton downloadExcelButton;
	private AonToolbarButton downloadPdfButton;
	private AonToolbarButton downloadLiqButton;
	private AonToolbarButton previousButton;
	private AonToolbarButton nextButton;
	
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
	
	public UdapaQuality(AonData aonData) {
		super("Ficha de Calidad");
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
		content();
	}
	
	private void toolbar() {
		getToolbar().getButtonContainer().clear();
		
		backButton = new AonToolbarButton("Volver",
			AON.CSS.aonIconBack(), false, e -> back());
		getToolbar().add(backButton);
		
		newButton = new AonToolbarButton(AON.MSG.newAction(),
			AON.CSS.aonIconAdd(), true, e -> resetQuality());
		getToolbar().add(newButton);
		
		deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),
			AON.CSS.aonIconDelete(), false, e -> deleteQuality());
		getToolbar().add(deleteButton);

		printPdfButton = new AonToolbarButton("Impresi\u00f3n",
			AON.CSS.aonIconPdf(), false, e -> printQuality());
		getToolbar().add(printPdfButton);
			
		downloadExcelButton = new AonToolbarButton("Descargar Excel",
			AON.CSS.aonIconExcel(), true, e -> printQualityList("excel"));
		getToolbar().add(downloadExcelButton);
			
		downloadPdfButton = new AonToolbarButton("Descargar Pdf",
			AON.CSS.aonIconPdf(), true, e -> printQualityList("pdf"));
		getToolbar().add(downloadPdfButton);
		
		downloadLiqButton = new AonToolbarButton("Liquidaci\u00f3n",
			AON.CSS.aonIconExcel(), true, e -> printLiqList("excel"));
		getToolbar().add(downloadLiqButton);
		
		previousButton = new AonToolbarButton("Anterior",
			AON.CSS.aonIconPrev(), false, e -> previous());
		getToolbar().add(previousButton);
			
		nextButton = new AonToolbarButton("Siguiente",
			AON.CSS.aonIconNext(), false, e -> next());
		getToolbar().add(nextButton);
	}
	
	private void content() {
		setContent(new QualityPrincipal(me));
	}
	
	public FilterPanel filterPanel() {
		HashMap<String, LinkedList<String>> filterMap = getFilterMap();
		FilterPanel fp = new FilterPanel(initializeFilterMap()) {
			
			@Override
			protected void onClean() {
				super.onClean();
				QualityPrincipal qp = (QualityPrincipal) getContent().getWidget();
				qp.northContent.setWidget(filterPanel());	
			}
			
			@Override
			protected void refresh() {
				QualityPrincipal qp = (QualityPrincipal) getContent().getWidget();
				qp.gridContent();
			}
		};
		if(isBack) {
			isBack = false;
			fp.setFilterMap(filterMap);	
		}
		fp.addDateFilter("Desde", "from");
		fp.addDateFilter("Hasta", "to");
		fp.addTextFilter("N\u00BA Pedido", "code");
		getAPI().getRegistry().getSuppliers(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				fp.addPaperButton("Proveedor", result.getData().cast(), "supplier");
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		setFilterPanel(fp);
		return getFilterPanel();
	}
	
	public void sheetContent(JsDataResponse js, HashMap<String, LinkedList<String>> map) {
		setFilterMap(map);
		
		backButton.setVisible(true);
		deleteButton.setVisible(true);
		printPdfButton.setVisible(true);		
		downloadExcelButton.setVisible(false);
		downloadPdfButton.setVisible(false);
		downloadLiqButton.setVisible(false);
		previousButton.setVisible(true);
		nextButton.setVisible(true);

		setContent(new QualitySheet(this, js));
	}

	public HashMap<String, LinkedList<String>> initializeFilterMap() {
		HashMap<String, LinkedList<String>> filterMap = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add("quality");
		filterMap.put("type", list);
		if(getFilterPanel() != null) setFilterMap(filterMap);
		return filterMap;
	}
	
	private void printQuality() {
		QualitySheet sheet = (QualitySheet) getContent().getWidget();
		getAPI().getWarehouse().downloadUdapaQuality(sheet.getDataResponse().getId());
	}
	
	private void printQualityList(String type) {
		getAPI().getWarehouse().downloadUdapaQualityList(getFilterMap(), type);
	}
	
	private void printLiqList(String type) {
		getAPI().getWarehouse().downloadUdapaLiqList(getFilterMap(), type);
	}

	private void deleteQuality() {
		Label label = new Label("Est\u00e1 seguro que quiere borrar el Carrier Packing ");
		
		AonDialog dialog = new AonDialog("Borrar Carrier Packing", label) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				QualitySheet sheet = (QualitySheet) getContent().getWidget();
				String idStr = sheet.getDataResponse().getId() + "";
				Integer id = Integer.parseInt(idStr);
				impl.deleteQuality(getAonData().getDomain().getName(), getAonData().getDomain().getId(),
					id, new AsyncCallback<Void>() {
					
					@Override public void onSuccess(Void result) {
						hide();
						startApplication();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});							
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();	
	}
	

	
	private void resetQuality() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(AON.AON_CSS.aonWidthAll());
		AonComboBox incomeBox = new AonComboBox();
		incomeBox.setLabel("Albar\u00e1n");
		incomeBox.setItemLabelPath("reference_code");
		incomeBox.setItemValuePath("reference_code");
		
		// TODO
		HashMap<String,LinkedList<String>> map = new HashMap<>();
		API.getWarehouse().getOrders("incomeQ", map ,new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				incomeBox.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(incomeBox);
		
		AonComboBox detailBox = new AonComboBox();
		detailBox.setLabel("Detalle");
		detailBox.setItemLabelPath("description");
		detailBox.setItemValuePath("description");
		panel.add(detailBox);
		
		incomeBox.addSelectedItemChangedHandler(new SelectedItemChangedEventHandler() {
			
			@Override
			public void onSelectedItemChanged(SelectedItemChangedEvent event) {
				JsOrder order = (JsOrder) incomeBox.getSelectedItem();
				
				//TODO
				LinkedList<String>list = new LinkedList<>();
				list.add(order.getId() + "");
				map.put("income", list);
				map.put("quality", list);
				getAPI().getWarehouse().getDetails("income", map, new AsyncCallback<JSON<JsOrderDetail>>() {
					
					@Override
					public void onSuccess(JSON<JsOrderDetail> result) {
						detailBox.setItems(result.getData());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		
    	AonDialog dialog = new AonDialog("Nueva Ficha de Calidad", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {
				JsOrderDetail orderDetail = (JsOrderDetail) detailBox.getSelectedItem();
				JsOrder order = (JsOrder) incomeBox.getSelectedItem();

				JSONObject dataResponse = new JSONObject();
				dataResponse.put("number", new JSONString(order.getReferenceCode()));	
		
				Date date = Utils.parseDateTime(order.getIssueDate());
				String dateTime = Utils.formatDateTime(date);
				dataResponse.put("issue_date", new JSONString(dateTime));
				dataResponse.put("source", new JSONString("income_detail@" + orderDetail.getId()));
				getAPI().getCommon().insertDataResponse(JsonUtils.stringify(dataResponse.getJavaScriptObject()), new AsyncCallback<JsDataResponse>() {
					
					@Override
					public void onSuccess(JsDataResponse result) {
						JSONObject dataResponseDetail = new JSONObject();
						dataResponseDetail.put("data_response", new JSONString(result.getId() + ""));
						dataResponseDetail.put("type", new JSONString("quality"));
						dataResponseDetail.put("source", new JSONString("income_detail@" + orderDetail.getId()));			
						dataResponseDetail.put("product_price", new JSONString(orderDetail.getPrice() + ""));
						getAPI().getCommon().insertDataResponseDetail(JsonUtils.stringify(dataResponseDetail.getJavaScriptObject()));
						hide();
						sheetContent(result, getFilterMap());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		dialog.addAutoHidePartner(incomeBox.getElementById("overlay"));
		dialog.addAutoHidePartner(detailBox.getElementById("overlay"));
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void back() {
		isBack = true;
		startApplication();
	}
	
	private void previous() {
		Integer page = Integer.parseInt(getFilterMap().get("page").get(0));
				
		if(page > 1 ){
			page = page - 1;
			LinkedList<String> list = new LinkedList<>();
			list.add(page.toString());
			getFilterMap().put("page", list);
			list = new LinkedList<>();
			list.add("1");
			getFilterMap().put("per_page", list);
			getAPI().getCommon().getDataResponseQuality(getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
					
				@Override
				public void onSuccess(JSON<JsDataResponse> result) {
					setContent(new QualitySheet(me, result.getData().get(0)));
				}
					
				@Override public void onFailure(Throwable caught) {}
			});
		}
	}
	
	private void next() {
		Integer page = Integer.parseInt(getFilterMap().get("page").get(0));
		page = page + 1;
		LinkedList<String> list = new LinkedList<>();
		list.add(page.toString());
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("per_page", list);
		getAPI().getCommon().getDataResponseQuality(getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
					
			@Override
			public void onSuccess(JSON<JsDataResponse> result) {
				if(result.getData().length() == 0){
					Integer page = Integer.parseInt(getFilterMap().get("page").get(0));
					page = page < 2 ? 1 : page - 1;
					LinkedList<String> list = new LinkedList<>();
					list.add(page.toString());
					getFilterMap().put("page", list);
				}
				setContent(new QualitySheet(me, result.getData().get(0)));						
			}
					
			@Override public void onFailure(Throwable caught) {}
		});
	}
}
