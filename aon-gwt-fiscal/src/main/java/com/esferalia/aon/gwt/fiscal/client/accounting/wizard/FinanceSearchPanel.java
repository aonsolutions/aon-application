package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.finance.FinancePrinter;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.TextBox;


public class FinanceSearchPanel extends DockLayoutPanel implements Focusable, HasSelectionHandlers<Finance>{

	static FiscalServiceAsync fiscalService;
	
	private String domainName;
	private int domainId;
	private User user;
	
	final private int limit = 40;
	
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt( 0 ); 

	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	
	private FlowPanel container;
	private FlexTable tab;
	private FlowPanel datePanel;
	private FlowPanel amountPanel; 
	private FlowPanel paymentPanel; 
	
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	
	private CheckBox confidential;
	private ListBox payment;
	private AccountingRegistryBox registryBox;
	private DoubleBox amount;
	private CheckBox nearbyNumbers;
	private TextBox concept;
	private TextBox referenceCode;
	
	private IFinancePanelCallback callback;
	
	
	private int lastScrollPos = 0;
	
	public static interface IFinancePanelCallback {
		boolean isSelected( Finance finance);
	}
	
	public FinanceSearchPanel(String domainName,int domainId, IFinancePanelCallback callback, int tabIndex) {
		super(Unit.PX);
		this.domainName = domainName;
		this.domainId = domainId;
		this.callback = callback; 
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		northPanel = new SimpleLayoutPanel();
		fillNorthPanel(tabIndex);
		addNorth(northPanel, 125);
		
		centerLayoutPanel = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.addStyleName(AON.AON_CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.setWidget(centerPanel);
		add(centerLayoutPanel);
		
		centerPanel.addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
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
						search(offset.getValue());
					}
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
	

	private void fillNorthPanel(int tabIndex) {
		amount = new DoubleBox();
		amount.setVisibleLength(6);
		amount.setValue(null,false);
		amount.setTabIndex(++tabIndex);
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> arg0) {
				search();
			}
		});
		
		nearbyNumbers = new CheckBox(AON.MSG.nearbyNumbers());
		nearbyNumbers.setStyleName(AON.AON_CSS.aonPadding2Left());
		nearbyNumbers.setTabIndex(++tabIndex);
		nearbyNumbers.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				search();
			}
		});

		concept = new TextBox();
		concept.setTabIndex(++tabIndex);
		concept.setStyleName(AON.AON_CSS.aonInputText());
		concept.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search();
			}
		});

		fromDate = new DateBoxEx();
		fromDate.setTabIndex(++tabIndex);
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search();
			}
		});
		
		toDate = new DateBoxEx();
		toDate.setTabIndex(++tabIndex);
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search();
			}
		});
		
		confidential = new CheckBox( AON.MSG.confidential());
		confidential.addStyleName(AON.AON_CSS.aonMarginLeft());
		confidential.setTabIndex(++tabIndex);
		confidential.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				search();
			}
		});
		
		referenceCode = new TextBox();
		referenceCode.setTabIndex(++tabIndex);
		referenceCode.setStyleName(AON.AON_CSS.aonInputText());
		referenceCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search();
			}
		});

		registryBox = new AccountingRegistryBox(this.domainName,this.domainId, null, false);
		registryBox.setRequired(false);
		
		registryBox.setTabIndex(++tabIndex);
		registryBox.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> arg0) {
				search();
			}
		});

		payment = new ListBox();
		payment.addItem(" --- "," --- ");
		payment.addItem("Pago" ,"Pago");
		payment.addItem("Cobro", "Cobro");
		payment.setTabIndex(++tabIndex);
		payment.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				search();
			}
		});
		
		FlowPanel flowNorthPanel = new FlowPanel();
		Label label = new Label(AON.MSG.financeSelection());
		label.setStyleName(AON.AON_CSS.aonWidthAll());
		label.addStyleName(AON.AON_CSS.aonPaddingLeft());
		label.addStyleName(AON.AON_CSS.aonBorderBottom());
		label.addStyleName(AON.AON_CSS.aonBold());
		label.addStyleName(AON.AON_CSS.aonTextCenter());
		flowNorthPanel.add(label);

		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonBorderBottom());
		tab.addStyleName(AON.AON_CSS.aonPadding());
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

		flowNorthPanel.add(tab);
		northPanel.setWidget(flowNorthPanel);
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
		amount.setAccessKey(key);;
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
	
	private void search() {
		enableMoreData();
		container.clear();
		offset.setValue(0);
		search(offset.getValue());
	}
	
	private void search(final int ofs) {
		if (!isMoreData()) return; 
		
		FinanceParams params = new FinanceParams()
			.setDomain(this.domainId)
			.setFrom(fromDate.getValue())
			.setTo(toDate.getValue())
			.setRegistry(registryBox.getId())
			.setAmount((amount.getValue() != null && amount.getValue()!=0)?amount.getValue():null)
			.setNearbyNumbers(nearbyNumbers.getValue())
			.setConcept(concept.getValue())
			.setReferenceCode(referenceCode.getValue())
			.setPayment((payment.getSelectedIndex() == 0)?null:(payment.getSelectedIndex() == 1))
			.setConfidential(confidential.getValue())
			.setHasConfidentialityRole(user != null && user.hasConfidentialityRole())
			;
		
		fiscalService.getAccountFinances(domainName,domainId, params, ofs, limit
				, new AsyncCallback<LinkedList<Finance>>() {
					
					@Override
					public void onSuccess(LinkedList<Finance> result) {
						if (result != null && !result.isEmpty()) {
							for (final Finance finance : result) {
								finance.setSelected(callback.isSelected(finance));
								final FocusPanel financePanel = FinancePrinter.print(finance);
								financePanel.addStyleName(finance.isSelected()
										?AON.AON_CSS.aonIconChecked()
										:AON.AON_CSS.aonIconCheck());
								financePanel.addStyleName(AON.AON_CSS.aonPaddingLeft());
								container.add(financePanel);
								financePanel.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										finance.setSelected(!finance.isSelected());
										if (finance.isSelected()) {
											financePanel.removeStyleName(AON.AON_CSS.aonIconCheck());
											financePanel.addStyleName(AON.AON_CSS.aonIconChecked());
										} else {
											financePanel.addStyleName(AON.AON_CSS.aonIconCheck());
											financePanel.removeStyleName(AON.AON_CSS.aonIconChecked());
										}
										SelectionEvent.<Finance>fire( FinanceSearchPanel.this, finance);
									}
								});
							}
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
		search();
	}
	
	public void uncheck(Finance finance) {
		search();		
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
