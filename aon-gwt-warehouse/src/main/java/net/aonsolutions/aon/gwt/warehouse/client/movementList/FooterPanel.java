package net.aonsolutions.aon.gwt.warehouse.client.movementList;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.finance.JsInvoiceDetail;
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

		
	public FooterPanel(Main parent) {
		this(parent, null);
	}
	
	public FooterPanel(Main parent, JsStockStat product) {
		this.parent = parent;
		this.API = parent.getAPI();
		this.product = product;
		this.item = null;
		parent.getFilterMap().remove("item");
		
		initWidget(binder.createAndBindUi(this));
		
		tabPanel.getTabWidget(FooterTabs.ITEM.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.INCOME.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.DELIVERY.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.PURCHASE_INVOICE.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.SALE_INVOICE.ordinal()).getParent().setVisible(true);
		tabPanel.getTabWidget(FooterTabs.WAREHOUSE_TRANSFER.ordinal()).getParent().setVisible(false);
		tabPanel.getTabWidget(FooterTabs.ELABORATION.ordinal()).getParent().setVisible(false);
		tabPanel.selectTab(FooterTabs.ITEM.ordinal());
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
	
	protected void loadTabs() {
		if (item!=null && item.getId()!=null)
			parent.getFilterMap().put("item", new LinkedList<>(Arrays.asList(item.getId()+"")));
		if (product!=null && product.getId()!=null)
			parent.getFilterMap().put("product", new LinkedList<>(Arrays.asList(product.getId()+"")));
		
		loadIncomeTab();
		loadDeliveryTab();
		loadPurchaseInvoiceTab();
		loadSaleInvoiceTab();
		loadWarehouseTransferTab();
		loadElaborationTab();
	}


	private void loadItemTab() {
		if (product!=null && product.getId()!=null) {
			parent.getFilterMap().put("product", new LinkedList<>(Arrays.asList(product.getId()+"")));

			API.getWarehouse().getItemMovements(parent.getFilterMap(), new AsyncCallback<JSON<JsStockStat>>() {
				
				@Override
				public void onSuccess(JSON<JsStockStat> result) {
					if(!result.getData().toLinkedList().isEmpty()) {
						itemPanel.add( new DetailPanel(parent, result.getData().toLinkedList()) );
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
	
	private boolean isFilterDefined() {
		return (parent.getFilterMap().containsKey("product") && !parent.getFilterMap().get("product").isEmpty())
				|| (parent.getFilterMap().containsKey("item") && !parent.getFilterMap().get("item").isEmpty());
	}
	
	private void loadIncomeTab() {
		incomePanel.clear();
		if(isFilterDefined()) {
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
	
	private void loadDeliveryTab() {
		deliveryPanel.clear();
		if(isFilterDefined()) {
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
	
	private void loadPurchaseInvoiceTab() {
		purchaseInvoicePanel.clear();
//		if(isFilterDefined()) {
//			API.getFinance().getInvoiceMovements(parent.getFilterMap(), new AsyncCallback<JSON<JsInvoiceDetail>>() {
//				
//				@Override
//				public void onSuccess(JSON<JsInvoiceDetail> result) {
//					if(!result.getData().toLinkedList().isEmpty()) {
//						purchaseInvoicePanel.add( createInvoicePanel(result.getData()) );
//					} else {
//						purchaseInvoicePanel.add(new Label("No se han encontrado resultados. "));
//					}
//				}
//				
//				@Override public void onFailure(Throwable caught) {}
//			});
//		} else {
//			purchaseInvoicePanel.add(new Label("No se han encontrado resultados. "));
//		}
		// TODO loadPurchaseInvoiceTab
		purchaseInvoicePanel.add(new Label("Disponible pr\u00F3ximamente"));
	}
	
	private void loadSaleInvoiceTab() {
		// TODO loadSaleInvoiceTab
		saleInvoicePanel.clear();
		saleInvoicePanel.add(new Label("Disponible pr\u00F3ximamente"));
	}
	
	private void loadWarehouseTransferTab() {
		// TODO loadWarehouseTransferTab
		warehouseTransferPanel.clear();
		warehouseTransferPanel.add(new Label("Disponible pr\u00F3ximamente"));
	}
	
	private void loadElaborationTab() {
		// TODO loadElaborationTab
		elaborationPanel.clear();
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
			o.date = detail.getInvoice().getTaxDate();
			o.referenceCode = detail.getInvoice().getReferenceCode();
			o.registryName = detail.getInvoice().getRegistryName();
			o.quantity = detail.getQuantity();
			list.add(o);
		});
		return createPanel(list);
	}
	
	protected FlowPanel createWarehouseTransferPanel(JsObject jsWarehouseTransfer) {
		// TODO createWarehouseTransferPanel
		return null;
	}
	
	protected FlowPanel createElaborationPanel(JsElaboration jsElaboration) {
		// TODO createElaborationPanel
		return null;
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