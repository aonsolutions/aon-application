package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.finance.FinancePrinter;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


public class FinanceSearchPanel extends DockLayoutPanel implements Focusable, HasSelectionHandlers<Finance>{

	static final FinanceServiceAsync FINANCE_SERVICE;
	static {
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
	}
	
	private static final int LIMIT = 100;
	
	private final IFinancePanelCallback callback;
	private User user;
	
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 ); 

	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	
	private FlowPanel container;
	private FlexTable tab;
	private FlowPanel datePanel;
	private FlowPanel amountPanel; 
	private FlowPanel paymentPanel; 
	
	private AonDateBox fromDate;
	private AonDateBox toDate;
	
	private CheckBox confidential;
	private ListBox payment;
	private AonAccountingRegistryBox registryBox;
	private AonDoubleBox amount;
	private CheckBox nearbyNumbers;
	private AonTextBox concept;
	private AonTextBox referenceCode;
	private ListBox payMethod;
	private ListBox order;
	
	private int lastScrollPos = 0;
	
	public static interface IFinancePanelCallback {
		boolean isSelected( Finance finance);
		AccountEntryModuleOptions getModuleModuleOptions();
	}
	
	public FinanceSearchPanel(IFinancePanelCallback callback) {
		super(Unit.PX);
		this.callback = callback;
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		
		northPanel = new SimpleLayoutPanel();

		amount = new AonDoubleBox();
		amount.setVisibleLength(6);
		amount.setValue(null,false);
		amount.addValueChangeHandler(event -> search(callback));
		
		nearbyNumbers = new CheckBox(AON.MSG.nearbyNumbers());
		nearbyNumbers.setStyleName(AON.AON_CSS.aonPadding2Left());
		nearbyNumbers.addClickHandler(event -> search(callback));

		concept = new AonTextBox();
		concept.setStyleName(AON.AON_CSS.aonInputText());
		concept.addValueChangeHandler(event -> search(callback));

		fromDate = new AonDateBox();
		fromDate.addValueChangeHandler(event -> search(callback));
		
		toDate = new AonDateBox();
		toDate.addValueChangeHandler(event -> search(callback));
		
		confidential = new CheckBox( AON.MSG.confidential());
		confidential.addStyleName(AON.AON_CSS.aonMarginLeft());
		confidential.addClickHandler(event -> search(callback));
		
		referenceCode = new AonTextBox();
		referenceCode.setStyleName(AON.AON_CSS.aonInputText());
		referenceCode.addValueChangeHandler(event -> search(callback));

		registryBox = new AonAccountingRegistryBox(callback.getModuleModuleOptions(), false);
		registryBox.setRequired(false);
		registryBox.addSelectionHandler(event -> search(callback));

		payment = new ListBox();
		payment.addItem(" --- "," --- ");
		payment.addItem("Pago" ,"Pago");
		payment.addItem("Cobro", "Cobro");
		payment.addChangeHandler(event -> search(callback));
		
		
		payMethod = new ListBox();
		payMethod.setWidth("120px");
		payMethod.addItem(" ---- ", "") ;
		if (callback.getModuleModuleOptions().getConfiguration() != null && callback.getModuleModuleOptions().getConfiguration().getPayMethods() != null) {
			for (PayMethod pm : callback.getModuleModuleOptions().getConfiguration().getPayMethods() ) {
				payMethod.addItem( pm.getName(), AonNumberUtils.toString(pm.getId()));
			}
		}
		payMethod.addChangeHandler(event -> search(callback));

		order = new ListBox();
		order.setWidth("150px");
		order.addItem("Fecha vencimiento");
		order.addItem("Fecha vencimiento, descendente");
		order.addItem("Nombre titular");
		order.addItem("Importe");
		order.addItem("Forma de pago");
		order.addItem("Fecha creaci\u00F3n");
		order.addItem("Fecha creaci\u00F3n, descendente");
		order.addItem("Fecha modificaci\u00F3n, descendente");
		order.setSelectedIndex(0);
		order.addChangeHandler(event -> search(callback));
		
		
		FlowPanel flowNorthPanel = new FlowPanel();
		flowNorthPanel.setStyleName(AON.AON_CSS.aonBorderBottom());
		Label label = new Label(AON.MSG.financeSelection());
		label.setStyleName(AON.AON_CSS.aonWidthAll());
		label.addStyleName(AON.AON_CSS.aonPaddingLeft());
		label.addStyleName(AON.AON_CSS.aonBorderBottom());
		label.addStyleName(AON.AON_CSS.aonBold());
		label.addStyleName(AON.AON_CSS.aonTextCenter());
		flowNorthPanel.add(label);

		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPadding2());
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		
		tab.getColumnFormatter().setWidth(0, "60px");
		tab.getColumnFormatter().setWidth(1, "180px");
		tab.getColumnFormatter().setWidth(2, "85px");
		tab.getColumnFormatter().setWidth(3, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.amount()));
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonBold());
		amountPanel = new FlowPanel();
		amountPanel.setStyleName(AON.AON_CSS.aonNowrap());
		amountPanel.add(amount);
		amountPanel.add(nearbyNumbers);
		tab.setWidget(0, 1, amountPanel);


		tab.setWidget(0, 2, new Label(AON.MSG.concept()));
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonBold());
		tab.setWidget(0, 3, concept);
		
		tab.setWidget(1, 0, new Label(AON.MSG.date()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonBold());
		
		datePanel = new FlowPanel();
		datePanel.setStyleName(AON.AON_CSS.aonNowrap());
		datePanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		datePanel.add(to);
		datePanel.add(toDate);
		tab.setWidget(1, 1, datePanel);

		tab.setWidget(1, 2, new Label(AON.MSG.invoiceNumberAbbr()));
		tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonBold());
		tab.setWidget(1, 3, referenceCode);

		tab.setWidget(2, 0, new Label(AON.MSG.titular()));
		tab.getCellFormatter().setStyleName(2,0, AON.AON_CSS.aonBold());
		tab.setWidget(2, 1, registryBox);
		
		paymentPanel = new FlowPanel();
		paymentPanel.add(payment);
		tab.setWidget(2, 2, new Label(AON.MSG.type()));
		tab.getCellFormatter().setStyleName(2,2, AON.AON_CSS.aonBold());
		tab.setWidget(2, 3, paymentPanel);

		tab.setWidget(3, 0, new Label(AON.MSG.payMethodAbbr()));
		tab.getCellFormatter().setStyleName(3,0, AON.AON_CSS.aonBold());
		tab.setWidget(3, 1, payMethod);

		tab.setWidget(3, 2, new Label(AON.MSG.orderBy()));
		tab.getCellFormatter().setStyleName(3,2, AON.AON_CSS.aonBold());
		tab.setWidget(3, 3, order);

		flowNorthPanel.add(tab);
		northPanel.setWidget(flowNorthPanel);
		
		
		
		addNorth(northPanel, 135);
		
		centerLayoutPanel = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.addStyleName(AON.AON_CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.setWidget(centerPanel);
		add(centerLayoutPanel);
		
		centerPanel.addScrollHandler(event -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = centerPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = centerPanel.getWidget().getOffsetHeight() - centerPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					search(callback, offset.getValue());
				}
			}
		});
	}
	public void setUser(User user) {
		this.user = user;
		if (paymentPanel != null && user != null && user.hasConfidentialityRole()) {
			paymentPanel.add(confidential);
		}
	}
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Finance> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public int getTabIndex() {
		return amount.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		amount.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		amount.setFocus(true);
		amount.selectAll();
	}

	@Override
	public void setTabIndex(int index) {
		fromDate.setTabIndex(index);
	}
	
	private void search(final IFinancePanelCallback callback) {
		enableMoreData();
		container.clear();
		offset.setValue(0);
		search(callback, offset.getValue());
	}
	
	private void search(final IFinancePanelCallback callback, final int ofs) {
		if (!isMoreData()) return; 
		
		FinanceParams params = new FinanceParams()
			.setDomain(callback.getModuleModuleOptions().getDomain())
			.setFromDueDate(fromDate.getValue())
			.setToDueDate(toDate.getValue())
			.setRegistry(registryBox.getId())
			.setAmount((amount.getValue() != null && amount.getValue()!=0)?amount.getValue():null)
			.setNearbyNumbers(nearbyNumbers.getValue())
			.setConcept(concept.getValue())
			.setReferenceCode(referenceCode.getValue())
			.setPayment((payment.getSelectedIndex() == 0)?null:(payment.getSelectedIndex() == 1))
			.setConfidential(confidential.getValue())
			.setHasConfidentialityRole(user != null && user.hasConfidentialityRole())
			.setPayMethod( AonNumberUtils.toInteger( payMethod.getSelectedValue()) )
			.setOrder(order.getSelectedIndex())
			;
		
		FINANCE_SERVICE.getAccountFinances(
				 callback.getModuleModuleOptions().getDomainName()
				,callback.getModuleModuleOptions().getDomain()
				,callback.getModuleModuleOptions().getUser(), params, ofs, LIMIT
				, new AsyncCallback<LinkedList<Finance>>() {
					
					@Override
					public void onSuccess(LinkedList<Finance> result) {
						if (result != null && !result.isEmpty()) {
							result.stream().forEach(this::addFinance);
							offset.setValue(ofs + result.size());
							enableMoreData();
						} else {
							FlowPanel line = new FlowPanel();
							InlineLabel label = new InlineLabel(AON.MSG.noData());
							line.add(label);
							container.add(line);
							disableMoreData();
						}
						enableSearch();
					}
					

					private void addFinance(Finance finance) {
						finance.setSelected(callback.isSelected(finance));
						final FocusPanel financePanel = FinancePrinter.print(finance);
						financePanel.getElement().getStyle().setPaddingTop(3, Unit.PX);
						financePanel.setStyleName(AON.AON_CSS.aonClickableBlock());
						financePanel.addStyleName(finance.isSelected()
								?AON.AON_CSS.aonIconCheckYes()
								:AON.AON_CSS.aonIconCheckNo());
						financePanel.addStyleName(AON.AON_CSS.aonPaddingLeft());
						container.add(financePanel);
						financePanel.addClickHandler(event -> {
							finance.setSelected(!finance.isSelected());
							if (finance.isSelected()) {
								financePanel.removeStyleName(AON.AON_CSS.aonIconCheckNo());
								financePanel.addStyleName(AON.AON_CSS.aonIconCheckYes());
							} else {
								financePanel.addStyleName(AON.AON_CSS.aonIconCheckNo());
								financePanel.removeStyleName(AON.AON_CSS.aonIconCheckYes());
							}
							SelectionEvent.<Finance>fire( FinanceSearchPanel.this, finance);
						});
					}

					@Override
					public void onFailure(Throwable caught) {
						FlowPanel line = new FlowPanel();
						InlineLabel label = new InlineLabel(AON.MSG.noData());
						line.add(label);
						container.add(line);
						enableSearch();
					}
				});
	}

	public void initialize() {
		container.clear();
		fromDate.setValue(null);
		toDate.setValue(null);
		confidential.setValue(false);
		registryBox.setValue( (AccountingRegistry) null, false);
		amount.setValue(null);
		concept.setValue(null);
		payMethod.setSelectedIndex(0);
		order.setSelectedIndex(0);
		search(this.callback);
	}
	
	public void uncheck(Finance finance) {
		search(this.callback);
	}
	public void enable() {
		northPanel.setVisible(true);
		centerLayoutPanel.setVisible(true);
	}
	public void disable() {
		northPanel.setVisible(false);
		centerLayoutPanel.setVisible(false);
	}
	
}
