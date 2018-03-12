package net.aonsolutions.aon.gwt.warehouse.client.movementList;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.esferalia.aon.gwt.api.client.warehouse.JsDeliveryDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.api.client.warehouse.JsIncomeDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsStockStat;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FooterPanel extends Composite {

	interface Binder extends UiBinder<Widget, FooterPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	enum FooterTabs {
		ITEM,
		INCOME,
		DELIVERY,
		PURCHASE_INVOICE,
		SALE_INVOICE,
		WAREHOUSE_TRANSFER,
		ELABORATION,
		;
	}

	private API API;
	
	private Main parent;
	private JsStockStat jsStockStat;

	
	@UiField
	MinimizePanel footerPanel;
	@UiField
	TabLayoutPanel tabPanel;

	/* TABS CONTENT */
	@UiField
	ScrollPanel itemPanel;
	@UiField
	ScrollPanel incomePanel;
	@UiField
	ScrollPanel deliveryPanel;
	@UiField
	ScrollPanel purchaseInvoicePanel;
	@UiField
	ScrollPanel saleInvoicePanel;
	@UiField
	ScrollPanel warehouseTransferPanel;
	@UiField
	ScrollPanel elaborationPanel;

		
	public FooterPanel(Main parent) {
		this(parent, null);
	}
	
	public FooterPanel(Main parent, JsStockStat jsStockStat) {
		this.parent = parent;
		this.API = parent.getAPI();
		this.jsStockStat = jsStockStat;
		
		initWidget(binder.createAndBindUi(this));
		
		tabPanel.getTabWidget(FooterTabs.ITEM.ordinal()).getParent().setVisible(false);
		tabPanel.getTabWidget(FooterTabs.INCOME.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.DELIVERY.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.PURCHASE_INVOICE.ordinal()).getParent().setVisible(false);
		tabPanel.getTabWidget(FooterTabs.SALE_INVOICE.ordinal()).getParent().setVisible(false);
		tabPanel.getTabWidget(FooterTabs.WAREHOUSE_TRANSFER.ordinal()).getParent().setVisible(false);
		tabPanel.getTabWidget(FooterTabs.ELABORATION.ordinal()).getParent().setVisible(false);
		
		tabPanel.selectTab(FooterTabs.INCOME.ordinal());
		
		parent.closeFooterPanel();
		
//		loadItemTab(jsStockStat);
		loadIncomeTab(jsStockStat);
		loadDeliveryTab(jsStockStat);
		loadPurchaseInvoiceTab(jsStockStat);
		loadSaleInvoiceTab(jsStockStat);
		loadWarehouseTransferTab(jsStockStat);
		loadElaborationTab(jsStockStat);
		
//		if(jsStockStat!=null){
//			tabPanel.getTabWidget(FooterTabs.PENDING.ordinal()).getParent().setVisible(false);
//			loadSourceTab();
//			loadCommetsTab();
//			loadRemarksTab();
//		} else {
//			tabPanel.getTabWidget(FooterTabs.PENDING.ordinal()).getParent().setVisible(false);
//			tabPanel.getTabWidget(FooterTabs.SOURCE.ordinal()).getParent().setVisible(false); 
//			tabPanel.getTabWidget(FooterTabs.COMMENTS.ordinal()).getParent().setVisible(false); 
//			tabPanel.getTabWidget(FooterTabs.REMARKS.ordinal()).getParent().setVisible(false); 
//			loadPendingOrderTab();
//		}
		
		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				parent.openFooterPanel();
			}
		});
		
		
		for(int i=0;i>tabPanel.getWidgetCount();i++)
//			tabPanel.getTabWidget(i).addHandler(new ClickHandler() {
			tabPanel.getWidget(i).addHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					MovementList.consoleLog("TAB clicked!");
					parent.openFooterPanel();
				}
			}, ClickEvent.getType());
	}


	private void loadItemTab(JsStockStat jsStockStat) {
		if (jsStockStat!=null && jsStockStat.getId()!=null) {
			parent.getFilterMap().put("product", new LinkedList<>(Arrays.asList(jsStockStat.getId()+"")));
			
			API.getProduct().getItemList(parent.getFilterMap(), new AsyncCallback<JSON<JsItem>>() {
				
				@Override
				public void onSuccess(JSON<JsItem> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						itemPanel.add( createItemPanel(result.getData()) );
					} else {
						itemPanel.add(new Label("No se han encontrado resultados. "));
					}
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
			
		} else {
			itemPanel.add(new Label("El objeto seleccinado no es valido. "));
		}
	}
	
	private void loadIncomeTab(JsStockStat jsStockStat) {
		if (jsStockStat!=null && jsStockStat.getId()!=null) {
			parent.getFilterMap().put("product", new LinkedList<>(Arrays.asList(jsStockStat.getId()+"")));
			
			API.getWarehouse().getIncomeMovements(parent.getFilterMap(), new AsyncCallback<JSON<JsIncomeDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsIncomeDetail> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						incomePanel.add( createIncomePanel(result.getData()) );
					} else {
						incomePanel.add(new Label("No se han encontrado resultados. "));
					}
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
			
		} else {
			incomePanel.add(new Label("El objeto seleccinado no es valido. "));
		}
	}
	
	private void loadDeliveryTab(JsStockStat jsStockStat) {
		if (jsStockStat!=null && jsStockStat.getId()!=null) {
			parent.getFilterMap().put("product", new LinkedList<>(Arrays.asList(jsStockStat.getId()+"")));
			
			API.getWarehouse().getDeliveryMovements(parent.getFilterMap(), new AsyncCallback<JSON<JsDeliveryDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsDeliveryDetail> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						deliveryPanel.add( createDeliveryPanel(result.getData()) );
					} else {
						deliveryPanel.add(new Label("No se han encontrado resultados. "));
					}
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
			
		} else {
			deliveryPanel.add(new Label("No se han encontrado resultados. "));
		}
	}
	
	private void loadPurchaseInvoiceTab(JsStockStat jsStockStat) {
		// TODO loadPurchaseInvoiceTab
		purchaseInvoicePanel.add(new Label("Disponible pr\u00F3ximamente"));
	}
	
	private void loadSaleInvoiceTab(JsStockStat jsStockStat) {
		// TODO loadSaleInvoiceTab
		saleInvoicePanel.add(new Label("Disponible pr\u00F3ximamente"));
	}
	
	private void loadWarehouseTransferTab(JsStockStat jsStockStat) {
		// TODO loadWarehouseTransferTab
		warehouseTransferPanel.add(new Label("Disponible pr\u00F3ximamente"));
	}
	
	private void loadElaborationTab(JsStockStat jsStockStat) {
		// TODO loadElaborationTab
		elaborationPanel.add(new Label("Disponible pr\u00F3ximamente"));
	}
	
	
	
	public class MovementObject {
		String date;
		String referenceCode;
		String registryName;
		Double quantity;
	}
	

	protected FlowPanel createItemPanel(AonJsArray<JsItem> aonJsArray) {
		List<MovementObject> list = new LinkedList<>();
		aonJsArray.toLinkedList().forEach(item -> {
			MovementObject o = new MovementObject();
			o.date = null;
			o.referenceCode = item.getSerialNumber();
			o.registryName = item.getName();
			o.quantity = null;
			list.add(o);
		});
		
		return createPanel(list);
	}
	
	protected FlowPanel createIncomePanel(AonJsArray<JsIncomeDetail> aonJsArray) {
		List<MovementObject> list = new LinkedList<>();
		aonJsArray.toLinkedList().forEach(detail -> {
			MovementObject o = new MovementObject();
			o.date = detail.getIncome().getIssueDate();
			o.referenceCode = detail.getIncome().getReferenceCode();
			o.registryName = detail.getIncome().getRegistry().getName();
			o.quantity = detail.getQuantity();
			list.add(o);
		});
		
		return createPanel(list);
	}
	
	protected FlowPanel createDeliveryPanel(AonJsArray<JsDeliveryDetail> aonJsArray){
		List<MovementObject> list = new LinkedList<>();
		aonJsArray.toLinkedList().forEach(detail -> {
			MovementObject o = new MovementObject();
			o.date = detail.getDelivery().getIssueDate();
			o.referenceCode = detail.getDelivery().getSeries()+"/"+detail.getDelivery().getNumber();
			o.registryName = detail.getDelivery().getRegistry().getName();
			o.quantity = detail.getQuantity();
			list.add(o);
		});
		return createPanel(list);
	}
	
	protected FlowPanel createInvoicePanel(JsInvoice jsInvoice) {
		// TODO createInvoicePanel
		return null;
	}
	
	protected FlowPanel createWarehouseTransferPanel(JsObject jsWarehouseTransfer){
		// TODO createWarehouseTransferPanel
		return null;
	}
	
	protected FlowPanel createElaborationPanel(JsElaboration jsElaboration){
		// TODO createElaborationPanel
		return null;
	}
	
	
	protected FlowPanel createPanel(List<MovementObject> list){
//		MovementList.consoleLog("result count -> " + list.size());
		
		final FlowPanel mainPanel = new FlowPanel("pre");
		
		final FlowPanel headerPanel = new FlowPanel("pre");
		headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header = new Label(""
				+ "FECHA          " //15
				+ "N.DOCUMENTO      " //17
				+ "TITULAR                            " //35
				+ "CANTIDAD         " //17
				+ "      ");  //10
		header.setStyleName(AON.AON_CSS.aonBold());
		header.addStyleName(AON.AON_CSS.aonMarginTop());
		header.addStyleName(AON.AON_CSS.aonBorderTop());
		header.addStyleName(AON.AON_CSS.aonBorderBottom());
		headerPanel.add(header);
		mainPanel.add(headerPanel);
		
		list.forEach(mo -> {
			final FlowPanel panel = new FlowPanel("pre");
			panel.setStyleName(AON.AON_CSS.aonFixedFont());
			panel.addStyleName(AON.AON_CSS.aonFontMedium());
			panel.addStyleName(AON.AON_CSS.aonMarginBottom());
			
			Date date = AonDateUtils.parseDateTime(mo.date);
			String issueDate = AonDateUtils.formatDate(date);
			final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
					+ AonStringUtils.rightPad("", 9)
					+ AonStringUtils.rightPad(
							AonStringUtils.defaultString(issueDate), 15)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							AonStringUtils.defaultString(mo.referenceCode), 16), 17)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							AonStringUtils.defaultString(mo.registryName), 34), 35)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(mo.quantity+""),
							17) + AonStringUtils.rightPad("", 17));
			acc.setTitle("");
			acc.setStyleName(AON.AON_CSS.aonBold());
			panel.add(acc);
			mainPanel.add(panel);
		});
		
		return mainPanel;
	}
	
	@UiHandler("footerPanel")
	void onFooterMinimize(MinimizeEvent event) {
		parent.closeFooterPanel();
	}

	@UiHandler("footerPanel")
	void onFooterMaximize(MaximizeEvent event) {
		parent.openFooterPanel();
	}

	public void selectTab(int index){
		tabPanel.selectTab(index);
	}
	
}