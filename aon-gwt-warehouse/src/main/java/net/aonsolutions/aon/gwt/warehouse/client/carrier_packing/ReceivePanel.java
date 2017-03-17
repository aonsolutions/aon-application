package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.HashMap;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

public class ReceivePanel extends Composite{
	
	interface Binder extends UiBinder<Widget, ReceivePanel> {
		
	}

	private API API;
	private static final Binder binder = GWT.create(Binder.class);
	@UiField SplitLayoutPanel contentSplitLayoutPanel;
	@UiField SimpleLayoutPanel supplierPanel;
	@UiField ScrollPanel receivePanel;
	@UiField PaperFab addButton;
	public ReceivePanel(API API, AonJsArray<JsOrder> orders) {
		initWidget(binder.createAndBindUi(this));
		this.API = API;
		
		buildSupplierPanel(orders);
		buildReceivePanel(orders.get(0));
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

			supplierSelector.add(item);
		}			
		supplierPanel.setWidget(supplierSelector);
	}
	
	private void buildReceivePanel(JsOrder order) {
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		receivePanel.setWidget(new Label("prueba2"));
		
		API.getWarehouse().getIncomes(new HashMap<>(), new AsyncCallback<JSON<JsOrder>>() {
			
			@Override
			public void onSuccess(JSON<JsOrder> result) {
				result.getData().stream().forEach(income ->{
					PaperItem pincome = buildIncome(income);
					SimplePanel sp = new SimplePanel();
					sp.setVisible(false);
					buildIncomeDetail(sp, income.getId());
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
	
	private void buildIncomeDetail(SimplePanel sp , Integer incomeId) {
		API.getWarehouse().getDetails(incomeId, "income", new AsyncCallback<JSON<JsOrderDetail>>() {
			
			@Override
			public void onSuccess(JSON<JsOrderDetail> result) {
				VerticalPanel details = new VerticalPanel();
				details.getElement().getStyle().setPaddingLeft(40, Unit.PX);
				
				result.getData().stream().forEach(detail -> {
					HorizontalPanel hp = new HorizontalPanel();
					PaperIconButton edit = new PaperIconButton();
				    edit.setStyle("height:20px;padding:0px;");

					edit.setIcon("add");
					hp.add(edit);
					Label label = new Label(detail.getDescription());
					label.getElement().getStyle().setPaddingRight(10, Unit.PX);
					label.getElement().getStyle().setMarginTop(3, Unit.PX);
					hp.add(label);
					DoubleBox  db1 = new DoubleBox();
					db1.setWidth("50px");
					db1.getElement().getStyle().setHeight(14, Unit.PX);
					db1.setStyleName(AON.AON_CSS.aonTextBox());
					db1.setValue(detail.getQuantity());
					hp.add(db1);
					
					details.add(hp);
					
				});
				sp.setWidget(details);
				/*API.getWarehouse().getDetails(purchaseId, "purchase", new AsyncCallback<JSON<JsOrderDetail>>() {
					
					@Override
					public void onSuccess(JSON<JsOrderDetail> result) {
						
					}
					
					@Override public void onFailure(Throwable caught) {}
				});*/	
		
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
		PaperInput param = new PaperInput();
		param.setLabel("Fecha");
		param.setMaxlength(20);
		panel.add(param);
		
		
    	AonDialog dialog = new AonDialog("Nuevo Albaran", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
}
