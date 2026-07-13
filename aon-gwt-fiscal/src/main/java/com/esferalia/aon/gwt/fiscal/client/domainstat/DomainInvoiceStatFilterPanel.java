package com.esferalia.aon.gwt.fiscal.client.domainstat;

import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class DomainInvoiceStatFilterPanel extends SimpleLayoutPanel implements HasValueChangeHandlers<DomainInvoiceStatParams>{
	
	public static final double HEIGTH = 80;
	
	private AonIntegerBox yearBox;
	private PeriodListBox periodBox;
	private AonDateBox fromDateBox;
	private AonDateBox toDateBox;
	private ListBox impersonatedUserBox;
	private ListBox scopeBox;
	private ListBox activeBox;
	private AonTextBox queryBox;
	private ListBox scoredFilterBox;
	
	DomainInvoiceStatFilterPanel( final DomainInvoiceStatModuleOptions opts ) {
		initYearBox(opts);
		initPeriodBox(opts);
		initFromDateBox(opts);
		initToDateBox(opts);
		initImpersonatedUser(opts);
		initScopeBox(opts);
		initActiveBox(opts);
		initQueryBox(opts);
		initScoredFilterBox(opts);
		initialize();
		paint( opts);
	}
	
	private void paint(DomainInvoiceStatModuleOptions opts) {
		FlowPanel rowsContainier = new FlowPanel();
		rowsContainier.setStyleName( AON.CSS.aonSearchPanel());
		
		AonDisplayTable row1Tab = new AonDisplayTable(AON.CSS.aonBlockCenter(),AON.CSS.aonWidthAlmostAll());
		
		FlowPanel datePanel = new FlowPanel();
		datePanel.addStyleName(AON.CSS.aonNowrap());
		datePanel.add(fromDateBox);
		InlineLabel toDateLabel = new InlineLabel( AON.MSG.to());
		toDateLabel.setStyleName(AON.CSS.aonItalic());
		toDateLabel.addStyleName(AON.CSS.aonMarginLeft());
		toDateLabel.addStyleName(AON.CSS.aonMarginRight());
		datePanel.add(toDateLabel);
		datePanel.add(toDateBox);
		
		row1Tab.addRow()
			.addCell( new Label(AON.MSG.fiscalYear()), AON.CSS.aonWidth80(), AON.CSS.aonBold(), AON.CSS.aonTextRight() )
			.addCell( yearBox , AON.CSS.aonWidth60() )
			.addCell( new Label(AON.MSG.period()), AON.CSS.aonWidth80(), AON.CSS.aonBold(), AON.CSS.aonTextRight())
			.addCell( periodBox , AON.CSS.aonWidth100() )
			.addCell( new Label(AON.MSG.date()), AON.CSS.aonWidth80(), AON.CSS.aonBold() , AON.CSS.aonTextRight())
			.addCell( datePanel , AON.CSS.aonWidth200() )
			.addCell( new Label(AON.MSG.recorded()), AON.CSS.aonWidth80(), AON.CSS.aonBold() , AON.CSS.aonTextRight())
			.addCell( scoredFilterBox, AON.CSS.aonPaddingLeft(), AON.CSS.aonWidth100(), AON.CSS.aonPaddingRight())
			.addCell( new Label(AON.MSG.show()), AON.CSS.aonWidth80(), AON.CSS.aonBold() , AON.CSS.aonTextRight())
			.addCell( activeBox, AON.CSS.aonWidth100() )
			.addCell( new Label(), AON.CSS.aonWidthAuto())
		;
		rowsContainier.add(row1Tab);
		
		AonDisplayTable row2Tab = new AonDisplayTable(AON.CSS.aonBlockCenter(),AON.CSS.aonWidthAlmostAll());
		row2Tab.addRow()
			.addCell( new Label(AON.MSG.enterprise()), AON.CSS.aonBold(), AON.CSS.aonWidth80(), AON.CSS.aonTextRight())
			.addCell( queryBox, AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.user()), AON.CSS.aonWidth80(), AON.CSS.aonBold() , AON.CSS.aonTextRight())
			.addCellIf( isAdminUser(opts), impersonatedUserBox, AON.CSS.aonPaddingLeft(), AON.CSS.aonWidth100(), AON.CSS.aonPaddingRight())
			.addCellIf( isAdminUser(opts), new Label(AON.MSG.scope()), AON.CSS.aonWidth80(), AON.CSS.aonBold() , AON.CSS.aonTextRight())
			.addCell( scopeBox, AON.CSS.aonPaddingLeft(), AON.CSS.aonWidth100(), AON.CSS.aonPaddingRight())
			.addCell( new Label(), AON.CSS.aonWidthAuto())
		;
		rowsContainier.add(row2Tab);

		this.setWidget(rowsContainier);
	}
 
	private boolean isAdminUser(DomainInvoiceStatModuleOptions opts) {
		return opts.optConfiguration()
		 	.map(c -> c.getDur())
		 	.filter(d -> d != null)
		 	.map(d -> d.isAdmin())
		 	.orElse(false)
	 	;
	}

	void clear(DomainInvoiceStatModuleOptions opts) {
		initialize();
		fire(opts);
	}

	void refresh(DomainInvoiceStatModuleOptions opts) {
		fire(opts);
	}

	private void initYearBox(DomainInvoiceStatModuleOptions opts) {
		yearBox = new AonIntegerBox();
		yearBox.addStyleName(AON.CSS.aonMarginLeft());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(5);
		yearBox.addValueChangeHandler(event -> {
			fillDates();
			fire(opts);
		});
	}

	private void initPeriodBox(DomainInvoiceStatModuleOptions opts) {
		periodBox = new PeriodListBox();
		periodBox.addStyleName(AON.CSS.aonMarginLeft());
		periodBox.addChangeHandler(event -> {
			fillDates();
			fire(opts);
		});
	}

	private void fillDates() {
		Integer y = yearBox.getValue();
		Period p = periodBox.getValue();
		if ( y == null) {
			fromDateBox.setValue(null,false);
			toDateBox.setValue(null,false);
		} else {
			if (p == null) {
				fromDateBox.setValue(DateUtils.getFirstDayOfYear(y - 1900),false);
				toDateBox.setValue(DateUtils.getLastDayOfYear(y - 1900),false);
			} else {
				fromDateBox.setValue(DateUtils.getDate(p.getStartMonth(), y),false);
				toDateBox.setValue(DateUtils.getLastDayOfMonth(DateUtils.getDate(p.getDueMonth(), y)),false);
			}
		}
	}
	
	private void checkDatesAndFire(DomainInvoiceStatModuleOptions opts) {
		Date from = fromDateBox.getValue();
		Date to = toDateBox.getValue();
		if (from != null && to != null) {
			int fromYear = DateUtils.getYear( from );
			int toYear = DateUtils.getYear( to );
			if ( fromYear == toYear ) {
				yearBox.setValue(fromYear,false);
			} else {
				yearBox.setValue(null,false);
				periodBox.setSelectedIndex(0);
			}
		} else {
			yearBox.setValue(null,false);
			periodBox.setSelectedIndex(0);
		}
		fire(opts);
	}
	
	private void initFromDateBox(DomainInvoiceStatModuleOptions opts) {
		fromDateBox = new AonDateBox();
		fromDateBox.addValueChangeHandler(event -> checkDatesAndFire(opts));
	}
	
	private void initToDateBox(DomainInvoiceStatModuleOptions opts) {
		toDateBox = new AonDateBox();
		toDateBox.addValueChangeHandler(event -> checkDatesAndFire(opts));
	}
	
	private void initImpersonatedUser(DomainInvoiceStatModuleOptions opts) {
		impersonatedUserBox = new ListBox();
		impersonatedUserBox.addStyleName(AON.CSS.aonMarginLeft());
		DomainInvoiceStatModule.COMMON_SERVICE.getUsers(
			opts.getDomainName(), opts.getDomain(), opts.getUser(), new AsyncCallback<ArrayList<User>>() {
				
				@Override
				public void onSuccess(ArrayList<User> users) {
					AonCollectionUtils.stream(users)
						.forEach(u -> {
							String userLabel = u.getLogin() + " (" + u.getName() + ")";
							impersonatedUserBox.addItem(userLabel, AonNumberUtils.toString(u.getId()));
							if (AonStringUtils.equals(opts.getUser(),u.getLogin())) {
								impersonatedUserBox.setSelectedIndex(impersonatedUserBox.getItemCount() - 1);
							}
						});
					impersonatedUserBox.addChangeHandler(event -> fire(opts));
				}
				
				@Override
				public void onFailure(Throwable arg0) {
					impersonatedUserBox.addItem("<No resuelto>");
					impersonatedUserBox.setEnabled(false);
				}
			});
	}
	
	private void initScopeBox(DomainInvoiceStatModuleOptions opts) {
		scopeBox = new ListBox();
		scopeBox.addStyleName(AON.CSS.aonMarginLeft());
		String scopesLabel = (isAdminUser(opts))
			? "\u00C1mbitos del usuario"
			: "Mis \u00E1mbitos";
		scopeBox.addItem("Todos");
		scopeBox.addItem( scopesLabel );
		scopeBox.addItem("Sin \u00E1mbito");
		scopeBox.setSelectedIndex(1);
		scopeBox.addChangeHandler(event -> fire(opts));
	}
	
	private void initActiveBox(DomainInvoiceStatModuleOptions opts) {
		activeBox = new ListBox();
		activeBox.addStyleName(AON.CSS.aonMarginLeft());
		activeBox.addItem("Todas");
		activeBox.addItem("Activas");
		activeBox.addItem("Inactivas");
		activeBox.addItem("Expiradas");
		activeBox.setSelectedIndex(1);
		activeBox.addChangeHandler(event -> fire(opts));
	}

	private void initQueryBox(DomainInvoiceStatModuleOptions opts) {
		queryBox = new AonTextBox();
		queryBox.addStyleName(AON.CSS.aonMarginLeft());
		queryBox.setMaxLength(30);
		queryBox.setVisibleLength(30);
		queryBox.addValueChangeHandler(event -> fire(opts));
	}
	
	private void initScoredFilterBox(DomainInvoiceStatModuleOptions opts) {
		scoredFilterBox = new ListBox();
		scoredFilterBox.addStyleName(AON.CSS.aonMarginLeft());
		scoredFilterBox.addItem("Todas");
		scoredFilterBox.addItem("Pendiente de declarar");
		scoredFilterBox.addItem("Algo pendiente");
		scoredFilterBox.addItem("Facturas pendientes");
		scoredFilterBox.addItem("Documentos pendientes");
		scoredFilterBox.setSelectedIndex(2);
		scoredFilterBox.addChangeHandler(event -> fire(opts));
	}

	void initialize() {
		Date toDate = new Date();
		Date fromDate = DateUtils.addMonths2Date(new Date(), -4);
		fromDateBox.setValue(fromDate,false);
		toDateBox.setValue(toDate,false);
		int fromYear = DateUtils.getYear( fromDate );
		int toYear = DateUtils.getYear( toDate );
		if ( fromYear == toYear ) {
			yearBox.setValue(fromYear,false);
		} else {
			yearBox.setValue(null,false);
		}
		periodBox.setSelectedIndex(0);
		scopeBox.setSelectedIndex(1);
		activeBox.setSelectedIndex(1);
		queryBox.setValue(null,false);
		scoredFilterBox.setSelectedIndex(2);
	}

	DomainInvoiceStatParams getParams(DomainInvoiceStatModuleOptions opts) {
		DomainInvoiceStatParams params = new DomainInvoiceStatParams();
		params.setDomain(opts.getDomain());
		params.setActive(activeBox.getSelectedIndex() );
		params.setInvoices(true);
		params.setAlcatraz(true);
		params.setRawdoc(true);
		params.setFromDate(fromDateBox.getValue());
		params.setToDate(toDateBox.getValue());
		params.setQuery(queryBox.getValue());
		params.setImpersonatedUser( AonNumberUtils.toInteger( impersonatedUserBox.getSelectedValue()) );
		params.setScope( scopeBox.getSelectedIndex() );
		params.setScoredFilter( scoredFilterBox.getSelectedIndex() );
		params.setLimit(100);
		params.setOffset(0);
		return params;
	}
	
	private void fire(final DomainInvoiceStatModuleOptions opts) {
		ValueChangeEvent.<DomainInvoiceStatParams>fire( DomainInvoiceStatFilterPanel.this, getParams( opts ) ); 
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DomainInvoiceStatParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

}
