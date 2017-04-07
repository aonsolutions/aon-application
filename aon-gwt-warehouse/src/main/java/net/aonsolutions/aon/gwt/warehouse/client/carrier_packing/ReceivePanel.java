package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsWarehouse;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.iron.widget.IronSelector;
import com.vaadin.polymer.paper.widget.PaperFab;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;

import net.aonsolutions.aon.gwt.warehouse.client.Utils;
import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class ReceivePanel extends Composite{
	
	interface Binder extends UiBinder<Widget, ReceivePanel> {
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField SplitLayoutPanel contentSplitLayoutPanel;
	@UiField SimpleLayoutPanel supplierPanel;
	@UiField ScrollPanel receivePanel;
	@UiField PaperFab addButton;
	
	private CarrierPackingDetail parent;
	private Integer supplier;
	private Integer selectedIncome;
	
	public API getApi(){
		return parent.getAPI();
	}
	
	public JsCarrierPacking getJsCarrierPacking(){
		return parent.getJsCarrierPacking();
	}
	
	public ReceivePanel(CarrierPackingDetail parent){
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> list = new LinkedList<>();
		list.add(getJsCarrierPacking().getId() + "");
		map.put("carrier_packing", list);
		getApi().getWarehouse().getPurchases(map, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				buildSupplierPanel(result.getData());
				buildReceivePanel(result.getData().get(0));
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	private void buildSupplierPanel(AonJsArray<JsOrder> orders) {		
		IronSelector supplierSelector = new IronSelector();
		for(JsOrder label : orders.toLinkedList()){
			PaperItem item = new PaperItem();
			
			IronIcon ii = new IronIcon();
			ii.setIcon("label");
			
			item.add(ii);
			item.add(new Label(label.getRegistry().getName()));
			item.setStyle("min-height: 30px;");
			item.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					supplier = label.getRegistry().getId();
					buildReceivePanel();
				}
			});
			supplierSelector.add(item);
		}			
		supplierPanel.setWidget(supplierSelector);
	}
	
	private void buildReceivePanel(JsOrder order) {
		supplier = order.getRegistry().getId();
		buildReceivePanel();
	}
	private void buildReceivePanel() {
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		receivePanel.setWidget(new Label("prueba2"));
		HashMap<String, LinkedList<String>> map = new HashMap<String, LinkedList<String>>();
		LinkedList<String> l = new LinkedList<>();
		l.add(getJsCarrierPacking().getId() + "");
		map.put("carrier_packing", l);
		LinkedList<String> l2 = new LinkedList<>();
		l2.add(supplier + "");
		map.put("supplier", l2);
		getApi().getWarehouse().getIncomes(map, new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				result.getData().stream().forEach(income ->{
					PaperItem pincome = buildIncome(income);
					SimplePanel sp = new SimplePanel();
					sp.setVisible(selectedIncome != null && income.getId() == selectedIncome);
					buildIncomeDetail(sp, income);
					pincome.addClickHandler(new ClickHandler() {
	        			
	        			@Override
	        			public void onClick(ClickEvent arg0) {
	        				sp.setVisible(!sp.isVisible());
	        			}
	        		});
					verticalPanel.add(pincome);
					verticalPanel.add(sp);
				});
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		receivePanel.setWidget(verticalPanel);
	}
	
	private PaperItem buildIncome(JsOrder js){
		PaperItem pi = new PaperItem();
	  	IronIcon ironIcon = new IronIcon();
	   	ironIcon.setIcon("arrow-drop-down");
	    pi.add(ironIcon);
	    String title = (js.getIssueDate() != null 
	    		? Utils.formatDate(Utils.parseDateTime(js.getIssueDate())) + " - "
	    		: "") + js.getReferenceCode(); // + WORKPLACE!
	    pi.add(new Label(title));
	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;font-weight: bold;");
	    return pi;
	}
	
	private void buildIncomeDetail(SimplePanel sp , JsOrder	income) {
		getApi().getWarehouse().getDetails(income.getId(), "income", new AsyncCallback<JSON<JsOrderDetail>>() {
			
			@Override
			public void onSuccess(JSON<JsOrderDetail> result) {
				VerticalPanel details = new VerticalPanel();
				details.getElement().getStyle().setPaddingLeft(40, Unit.PX);
				result.getData().stream().forEach(detail -> {
					HorizontalPanel hp = new HorizontalPanel();
					PaperIconButton remove = new PaperIconButton();
				    remove.setStyle("height:20px;padding:0px;");
					remove.setIcon("remove");
					hp.add(remove);
					Label label = new Label(detail.getDescription());
					label.getElement().getStyle().setPaddingRight(10, Unit.PX);
					label.getElement().getStyle().setMarginTop(3, Unit.PX);
					hp.add(label);
					DoubleBox  db1 = new DoubleBox();
					db1.setWidth("50px");
					db1.getElement().getStyle().setHeight(14, Unit.PX);
					db1.setStyleName(AON.AON_CSS.aonTextBox());
					db1.setValue(detail.getQuantity());
					db1.addValueChangeHandler(new ValueChangeHandler<Double>() {
						
						@Override
						public void onValueChange(ValueChangeEvent<Double> event) {
							if(db1.getValue() > detail.getQuantity() || db1.getValue() <= 0){
								db1.getElement().getStyle().setBorderColor("red");
							} else db1.getElement().getStyle().setBorderColor("#a9a9a9");
						}
					});
					hp.add(db1);
					remove.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							removeIncomeDetail(income,  detail, db1.getValue());
						}
					});
					details.add(hp);
				});
				HashMap<String, LinkedList<String>> map2 = new HashMap<String, LinkedList<String>>();
				LinkedList<String> l = new LinkedList<>();
				l.add(getJsCarrierPacking().getId() + "");
				map2.put("carrier_packing", l);

				LinkedList<String> l2 = new LinkedList<>();
				l2.add(supplier + "");
				map2.put("supplier", l2);

				getApi().getWarehouse().getDetails("purchase", map2, new AsyncCallback<JSON<JsOrderDetail>>() {
					
					@Override
					public void onSuccess(JSON<JsOrderDetail> result2) {
						result2.getData().stream().forEach(detail2 -> {
							if(detail2.getDelivered() < detail2.getQuantity()){
							// TODO Añadr si delivered es < quantity!!!!!
								HorizontalPanel hp2 = new HorizontalPanel();
								PaperIconButton add = new PaperIconButton();
								add.setStyle("height:20px;padding:0px;");
								add.setIcon("add");

								hp2.add(add);
								Label label2 = new Label(detail2.getDescription());
								label2.getElement().getStyle().setPaddingRight(10, Unit.PX);
								label2.getElement().getStyle().setMarginTop(3, Unit.PX);
								hp2.add(label2);

								DoubleBox  db2 = new DoubleBox();
								db2.setWidth("50px");
								db2.getElement().getStyle().setHeight(14, Unit.PX);
								db2.setStyleName(AON.AON_CSS.aonTextBox());
								db2.setValue(detail2.getQuantity() - detail2.getDelivered());

								db2.addValueChangeHandler(new ValueChangeHandler<Double>() {
								
									@Override
									public void onValueChange(ValueChangeEvent<Double> event) {
										if(db2.getValue() > detail2.getQuantity() || db2.getValue() <= 0){
											db2.getElement().getStyle().setBorderColor("red");
										} else db2.getElement().getStyle().setBorderColor("#a9a9a9");
									}
								});
								hp2.add(db2);

								add.addClickHandler(new ClickHandler() {
								
									@Override
									public void onClick(ClickEvent event) {
										addIncomeDetail(income,  detail2, db2.getValue());
									}
								});

								details.add(hp2);
							}
						});
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
				sp.setWidget(details);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}

	private void addIncomeDetail(JsOrder income, JsOrderDetail detail, Double quantity){
		selectedIncome = income.getId();
		Double pq = detail.getQuantity();
		if(quantity > 0 && quantity <= pq){
			String requestData = "{\"item\":\""+ detail.getItem() +"\","
				+ "\"description\":\""+ detail.getDescription() +"\","
				+ "\"workplace\":\""+ income.getWorkplace() +"\","
				+ "\"quantity\":\""+ quantity +"\","
				+ "\"income\":\""+ income.getId() +"\","
				+ "\"purchase_detail\":\"" + detail.getId() + "\"" + "}";
			getApi().getWarehouse().insertDetail("income", requestData, new AsyncCallback<JsOrderDetail>() {
			
				@Override
				public void onSuccess(JsOrderDetail result) {
					updatePurchaseDetailDelivered(detail.getId(), quantity);
				}
			
				@Override public void onFailure(Throwable caught) {}
			});			
		}
	}
	
	private void removeIncomeDetail(JsOrder income, JsOrderDetail incomeDetail, Double quantity){
		selectedIncome = income.getId();

		Double pq = incomeDetail.getQuantity();
		if(quantity > 0 && quantity <= pq){
			if(quantity == pq){
				String requestData = "{"
						+ "\"id\":\""+ incomeDetail.getId() +"\""
						+ "}";
				getApi().getWarehouse().deleteDetail("income", requestData, new AsyncCallback<JsOrder>() {
					@Override
					public void onSuccess(JsOrder result) {
						updatePurchaseDetailDelivered(incomeDetail.getPurchaseDetail(), -quantity);
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			} else {
				String requestData = "{\"quantity\":\""+ (incomeDetail.getQuantity() - quantity) +"\","
						+ "\"id\":\""+ incomeDetail.getId() +"\""
						+ "}";
				getApi().getWarehouse().updateDetail("income", requestData, new AsyncCallback<JsOrder>() {
					
					@Override
					public void onSuccess(JsOrder result) {
						updatePurchaseDetailDelivered(incomeDetail.getPurchaseDetail(), -quantity);						
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		}
	}
	
	private void updatePurchaseDetailDelivered(Integer purchaseDetailId, Double quantity){
		String purchaseDetailRD = "{\"delivered\":\""+ quantity +"\","
				+ "\"id\":\""+ purchaseDetailId +"\""
				+ "}";
		getApi().getWarehouse().updateDetail("purchase", purchaseDetailRD, new AsyncCallback<JsOrder>() {
			
			@Override
			public void onSuccess(JsOrder result) {
				buildReceivePanel();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("addButton")
	void onClickParamater(ClickEvent event) {
		clickParameter();
	}
	
	private void clickParameter(){
		VerticalPanel panel = new VerticalPanel();
		
		PaperInput ref = new PaperInput();
		ref.setLabel("Numero");
		
		panel.add(ref);
		
		AonComboBox warehouse = new AonComboBox();
		warehouse.setLabel("Almacen");
		warehouse.setItemLabelPath("name");
		warehouse.setItemValuePath("name");
		getApi().getWarehouse().getWarehouses(new AsyncCallback<JSON<JsWarehouse>>() {
			
			@Override
			public void onSuccess(JSON<JsWarehouse> result) {
				warehouse.setItems(result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		panel.add(warehouse);
		PaperInput param = new PaperInput();
		param.setLabel("Fecha");
		param.setValue(getJsCarrierPacking().getDeliveryDate());
		param.setMaxlength(10);

		panel.add(param);
		
    	AonDialog dialog = new AonDialog("Nuevo Albaran", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				JsWarehouse js = (JsWarehouse) warehouse.getSelectedItem();
				String number = ref.getValue();
				String date = Utils.formatDateTime(Utils.parseDate(param.getValue()));
				String requestData = "{\"reference_code\":\""+ number +"\","
						+ "\"issue_time\":\""+ date +"\","
						+ "\"supplier\":\""+ supplier +"\","
						+ "\"carrier_packing\":\""+ getJsCarrierPacking().getId() +"\","
						+ "\"workplace\":\"" + js.getWorkplace() + "\"" + "}";
				getApi().getWarehouse().insertIncome(requestData, new AsyncCallback<JsOrder>() {
					
					@Override
					public void onSuccess(JsOrder result) {
						selectedIncome = result.getId();
						VerticalPanel vp = (VerticalPanel)receivePanel.getWidget();
						
						PaperItem pincome = buildIncome(result);
						SimplePanel sp = new SimplePanel();
						Window.alert(result.getId() + ", " + selectedIncome + " -> " + (result.getId() == selectedIncome));
						sp.setVisible(result.getId() == selectedIncome);
						buildIncomeDetail(sp, result);
						pincome.addClickHandler(new ClickHandler() {
		        			
		        			@Override
		        			public void onClick(ClickEvent arg0) {
		        				sp.setVisible(!sp.isVisible());
		        			}
		        		});
						vp.insert(pincome, 0);
						vp.insert(sp, 1);
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
		dialog.addAutoHidePartner(warehouse.getElementById("overlay"));
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
}
