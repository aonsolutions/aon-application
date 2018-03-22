package net.aonsolutions.aon.gwt.warehouse.client.movementList;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.finance.JsInvoiceDetail;
import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.esferalia.aon.gwt.api.client.warehouse.JsDeliveryDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.api.client.warehouse.JsIncomeDetail;
import com.esferalia.aon.gwt.api.client.warehouse.JsStockStat;
import com.esferalia.aon.gwt.api.client.warehouse.JsWarehouseTransferDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
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
	private JsStockStat product;
	private JsStockStat item;

	
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
	
	@UiField
	InlineLabel itemPanelHeader;
	@UiField
	InlineLabel incomePanelHeader;
	@UiField
	InlineLabel deliveryPanelHeader;
	@UiField
	InlineLabel purchaseInvoicePanelHeader;
	@UiField
	InlineLabel saleInvoicePanelHeader;
	@UiField
	InlineLabel warehouseTransferPanelHeader;
	@UiField
	InlineLabel elaborationPanelHeader;
	
		
	public FooterPanel(Main parent) {
		this(parent, null);
		hideTabs();
	}
	
	public FooterPanel(Main parent, JsStockStat product) {
		this.parent = parent;
		this.API = parent.getAPI();
		this.product = product;
		this.item = null;
		
		initWidget(binder.createAndBindUi(this));
		
		itemPanelHeader.setText("Detalle");
		incomePanelHeader.setText("Albs. compra");
		deliveryPanelHeader.setText("Albs. venta");
		purchaseInvoicePanelHeader.setText("Fras. compra");
		saleInvoicePanelHeader.setText("Fras. venta");
		warehouseTransferPanelHeader.setText("Traspasos almac\u00E9n");
		elaborationPanelHeader.setText("Elaboraciones");
		
		
		tabPanel.getTabWidget(FooterTabs.ITEM.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.INCOME.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.DELIVERY.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.PURCHASE_INVOICE.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.SALE_INVOICE.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.WAREHOUSE_TRANSFER.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.ELABORATION.ordinal()).getParent().setVisible(false);
		selectTab(FooterTabs.ITEM.ordinal());
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				parent.openFooterPanel();
			}
		});
		
		loadItemTab();
		loadTabs();
		parent.closeFooterPanel();		
	}
	
	public void selectItem(JsStockStat item) {
		this.item = item;
		loadTabs();
	}
	
	public void hideTabs() {
		for(FooterTabs tab: FooterTabs.values())
			tabPanel.getTabWidget(tab.ordinal()).getParent().setVisible(false);
	}
	
	protected void loadTabs() {
		HashMap<String, LinkedList<String>> filterMap = parent.getFilterMap();
		if (item!=null && item.getId()!=null)
			filterMap.put("item", new LinkedList<>(Arrays.asList(item.getId()+"")));
		if (product!=null && product.getId()!=null)
			filterMap.put("product_id", new LinkedList<>(Arrays.asList(product.getId()+"")));
		
		loadIncomeTab(filterMap);
		loadDeliveryTab(filterMap);
		loadPurchaseInvoiceTab(filterMap);
		loadSaleInvoiceTab(filterMap);
		loadWarehouseTransferTab(filterMap);
		loadElaborationTab(filterMap);
		
		filterMap.remove("item");
		filterMap.remove("product_id");
	}


	private void loadItemTab() {
		if (product!=null && product.getId()!=null) {
			HashMap<String, LinkedList<String>> filterMap = parent.getFilterMap();
			filterMap.put("product_id", new LinkedList<>(Arrays.asList(product.getId()+"")));

			API.getWarehouse().getItemMovements(filterMap, new AsyncCallback<JSON<JsStockStat>>() {
				
				@Override
				public void onSuccess(JSON<JsStockStat> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						itemPanel.add( new DetailPanel(parent, result.getData().toLinkedList()) );
					} else {
						itemPanel.add(new Label("No se han encontrado resultados. "));
					}
					updateTabTitle(itemPanelHeader, result.getData().length());
				}

				@Override public void onFailure(Throwable caught) {}
			});
			
			filterMap.remove("product_id");
		} else {
			itemPanel.add(new Label("Ning\u00FAn elemento seleccinado. "));
		}
	}
	
	private void updateTabTitle(InlineLabel inlineLabel, int length) {
		if(inlineLabel.getText().matches(".*\\(\\d+\\)"))
			inlineLabel.setText( inlineLabel.getText().replaceFirst("\\(\\d+\\)", " ("+length+")") );
		else
			inlineLabel.setText( inlineLabel.getText() + " ("+length+")" );
	}
	
	private boolean isFilterDefined(HashMap<String, LinkedList<String>> filterMap) {
		return (filterMap.containsKey("product_id") && !filterMap.get("product_id").isEmpty())
				|| (filterMap.containsKey("item") && !filterMap.get("item").isEmpty());
	}
	
	private void loadIncomeTab(HashMap<String, LinkedList<String>> filterMap) {
		incomePanel.clear();
		if(isFilterDefined(filterMap)) {
			API.getWarehouse().getIncomeMovements(filterMap, new AsyncCallback<JSON<JsIncomeDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsIncomeDetail> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						incomePanel.add( createIncomePanel(result.getData()) );
					} else {
						incomePanel.add(new Label("No se han encontrado resultados. "));
					}
					updateTabTitle(incomePanelHeader, result.getData().length());
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			incomePanel.add(new Label("El objeto seleccinado no es v\u00E1lido. "));
		}
	}
	
	private void loadDeliveryTab(HashMap<String, LinkedList<String>> filterMap) {
		deliveryPanel.clear();
		if(isFilterDefined(filterMap)) {
			API.getWarehouse().getDeliveryMovements(filterMap, new AsyncCallback<JSON<JsDeliveryDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsDeliveryDetail> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						deliveryPanel.add( createDeliveryPanel(result.getData()) );
					} else {
						deliveryPanel.add(new Label("No se han encontrado resultados. "));
					}
					updateTabTitle(deliveryPanelHeader, result.getData().length());
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			deliveryPanel.add(new Label("El objeto seleccinado no es v\u00E1lido. "));
		}
	}
	
	private void loadPurchaseInvoiceTab(HashMap<String, LinkedList<String>> filterMap) {
		purchaseInvoicePanel.clear();
		if(isFilterDefined(filterMap)) {
			filterMap.put("type", new LinkedList<>(Arrays.asList(InvoiceType.PURCHASE.ordinal()+"")));
			API.getFinance().getInvoiceMovements(filterMap, new AsyncCallback<JSON<JsInvoiceDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsInvoiceDetail> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						purchaseInvoicePanel.add( createInvoicePanel(result.getData()) );
					} else {
						purchaseInvoicePanel.add(new Label("No se han encontrado resultados. "));
					}
					updateTabTitle(purchaseInvoicePanelHeader, result.getData().length());
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			purchaseInvoicePanel.add(new Label("El objeto seleccinado no es v\u00E1lido. "));
		}
	}
	
	private void loadSaleInvoiceTab(HashMap<String, LinkedList<String>> filterMap) {
		saleInvoicePanel.clear();
		if(isFilterDefined(filterMap)) {
			filterMap.put("type", new LinkedList<>(Arrays.asList(InvoiceType.SALES.ordinal()+"")));
			API.getFinance().getInvoiceMovements(filterMap, new AsyncCallback<JSON<JsInvoiceDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsInvoiceDetail> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						saleInvoicePanel.add( createInvoicePanel(result.getData()) );
					} else {
						saleInvoicePanel.add(new Label("No se han encontrado resultados. "));
					}
					updateTabTitle(saleInvoicePanelHeader, result.getData().length());
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			saleInvoicePanel.add(new Label("El objeto seleccinado no es v\u00E1lido. "));
		}
	}
	
	private void loadWarehouseTransferTab(HashMap<String, LinkedList<String>> filterMap) {
		warehouseTransferPanel.clear();
		if(isFilterDefined(filterMap)) {
			API.getWarehouse().getWarehouseTransferDetail(filterMap, new AsyncCallback<JSON<JsWarehouseTransferDetail>>() {
				
				@Override
				public void onSuccess(JSON<JsWarehouseTransferDetail> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						warehouseTransferPanel.add( createWarehouseTransferPanel(result.getData()) );
					} else {
						warehouseTransferPanel.add(new Label("No se han encontrado resultados. "));
					}
					updateTabTitle(warehouseTransferPanelHeader, result.getData().length());
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else {
			warehouseTransferPanel.add(new Label("El objeto seleccinado no es v\u00E1lido. "));
		}
	}
	
	private void loadElaborationTab(HashMap<String, LinkedList<String>> filterMap) {
		elaborationPanel.clear();
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
	
	protected FlowPanel createDeliveryPanel(AonJsArray<JsDeliveryDetail> aonJsArray) {
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
	
	protected FlowPanel createInvoicePanel(AonJsArray<JsInvoiceDetail> aonJsArray) {
		List<MovementObject> list = new LinkedList<>();
		aonJsArray.toLinkedList().forEach(detail -> {
			MovementObject o = new MovementObject();
			o.date = detail.getInvoice().getIssueDate();
			o.referenceCode = detail.getInvoice().getReferenceCode();
			o.registryName = detail.getInvoice().getRegistryName();
			o.quantity = detail.getQuantity();
			list.add(o);
		});
		return createPanel(list);
	}
	
	protected FlowPanel createWarehouseTransferPanel(AonJsArray<JsWarehouseTransferDetail> aonJsArray) {
		List<MovementObject> list = new LinkedList<>();
		aonJsArray.toLinkedList().forEach(detail -> {
			MovementObject o = new MovementObject();
			o.date = detail.getWarehouseTransfer().getIssueTime();
			o.referenceCode = detail.getWarehouseTransfer().getSeries()+"/"+detail.getWarehouseTransfer().getNumber();
			o.registryName = "-";
			o.quantity = detail.getQuantity();
			list.add(o);
		});
		return createPanel(list);
	}
	
	protected FlowPanel createElaborationPanel(AonJsArray<JsElaboration> aonJsArray) {
		List<MovementObject> list = new LinkedList<>();
		aonJsArray.toLinkedList().forEach(elaboration -> {
			MovementObject o = new MovementObject();
			o.date = elaboration.getDate();
			o.referenceCode = elaboration.getSeries()+"/"+elaboration.getNumber();;
			o.registryName = "-";
			o.quantity = elaboration.getQuantity();
			list.add(o);
		});
		return createPanel(list);
	}
	
	
	protected FlowPanel createPanel(List<MovementObject> list) {
		
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