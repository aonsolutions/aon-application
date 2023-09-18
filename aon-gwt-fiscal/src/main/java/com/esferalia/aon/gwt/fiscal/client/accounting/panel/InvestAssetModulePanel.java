package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountModuleOptions;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.InvestAssetRegime;
import com.esferalia.aon.occam.api.model.InvestAssetType;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class InvestAssetModulePanel extends DockLayoutPanel implements HasAccountEntrySelectionHandlers{

	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanel;
	
	private FlexTable tab;
	
	private TextBox description;
	private ListBox activity;
	private ListBox type;
	private ListBox regimen;
	private DoubleBox iva;
	private DoubleBox retention;
	private AonDateBox startDate;
	private AonDateBox endDate;
	
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;
	
	private static CommonServiceAsync COMMON_SERVICE;

	public InvestAssetModulePanel(AccountModuleOptions options) {
		super(Unit.PX);
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		northPanel = new SimpleLayoutPanel();
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		description= new TextBox();
		description.setVisibleLength(20);
		description.setStyleName(AON.CSS.aonInputText());
		description.addValueChangeHandler(event -> onSearch( options ));
		
		activity = new ListBox();
		activity.addItem( "-", "");
		getActivities(options, activities -> activities.forEach(activityIt -> activity.addItem( activityIt.getDescription(), activityIt.getId().toString())));
		activity.setSelectedIndex(0);
		activity.setStyleName(AON.CSS.aonInputText());
		activity.addChangeHandler(event -> onSearch( options ));
		
		type = new ListBox();
		type.addItem( "-", "-1");
		for(int i=0; i < InvestAssetType.values().length; i++)
			type.addItem(InvestAssetType.values()[i].description(), i + "");
		type.setSelectedIndex(0);
		type.setStyleName(AON.CSS.aonInputText());
		type.addChangeHandler(event -> onSearch( options ));
		
		regimen = new ListBox();
		regimen.addItem( "-", "-1");
		for(int i=0; i < InvestAssetRegime.values().length; i++)
			regimen.addItem(InvestAssetRegime.values()[i].description(), i + "");
		regimen.setSelectedIndex(0);
		regimen.setStyleName(AON.CSS.aonInputText());
		regimen.addChangeHandler(event -> onSearch( options ));
		
		iva = new DoubleBox();
		iva.setStyleName(AON.CSS.aonInputText());
		iva.addValueChangeHandler(event -> onSearch( options ));
		
		retention = new DoubleBox();
		retention.setStyleName(AON.CSS.aonInputText());
		retention.addValueChangeHandler(event -> onSearch( options ));
		
		startDate = new AonDateBox();
		startDate.setStyleName(AON.CSS.aonInputText());
		startDate.addValueChangeHandler(event -> onSearch( options ));
		
		endDate = new AonDateBox();
		endDate.setStyleName(AON.CSS.aonInputText());
		endDate.addValueChangeHandler(event -> onSearch( options ));

		tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonSearchPanel());
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginRight());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		int row = 0;
		int col = 0;
		
		tab.setWidget(row, col, new Label(AON.MSG.description()));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, description);
		col++;
		
		tab.setWidget(row, col, new Label(AON.MSG.activity()));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, activity);
		col++;
		
		tab.setWidget(row, col, new Label(AON.MSG.type()));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, type);
		col++;

		tab.setWidget(row, col, new Label("Regimen"));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, regimen);
		col++;
		
		cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			description.setValue(null,false);
			activity.setSelectedIndex(0);
			type.setSelectedIndex(0);
			regimen.setSelectedIndex(0);
			iva.setValue(null,false);
			retention.setValue(null,false);
			startDate.setValue(null,false);
			endDate.setValue(null,false);
			
			description.setFocus(true);
			onSearch( options );
		});

		refreshButton = new AonSearchPanelButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(event -> onSearch( options ));

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(row, col, buttonsPanel);
		col++;
		
		row = 1;
		col = 0;

		tab.setWidget(row, col, new Label("% IVA"));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, iva);
		col++;

		tab.setWidget(row, col, new Label("% Imp. Directa"));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, retention);
		col++;
		
		tab.setWidget(row, col, new Label("F. Inicio"));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, startDate);
		col++;
		
		tab.setWidget(row, col, new Label("F. Fin"));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, endDate);
		col++;
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		northPanel.setWidget(scrollPanel);

		addNorth(northPanel, 80);
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		onSearch( options );
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}
	
	public void onSearch( AccountModuleOptions options ) {
		InvestAssetParams params = getWidgetParams( options );
		InvestAssetPanel investAssetPanel = new InvestAssetPanel(params);
		centerPanel.setWidget(investAssetPanel);
	}

	public InvestAssetParams getWidgetParams( AccountModuleOptions options) {
		return new InvestAssetParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(description.getValue())
			.setActivity(activity.getSelectedIndex() == 0 ? null : Integer.parseInt(activity.getSelectedValue()))
			.setType(type.getSelectedIndex() == 0 ? null : Byte.parseByte(type.getSelectedValue()))
			.setRegime(regimen.getSelectedIndex() == 0 ? null : Byte.parseByte(regimen.getSelectedValue()))
			.setVatPercent(iva.getValue())
			.setRetentionPercent(retention.getValue())
			.setStartDate(startDate.getValue())
			.setEndDate(endDate.getValue())
			;
	}
	
	private void getActivities(AccountModuleOptions options, Consumer<List<Activity>> success) {
		COMMON_SERVICE.getActivities(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Activity>>() {
			
			@Override
			public void onSuccess(List<Activity> activities) {
				success.accept(activities);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
}
