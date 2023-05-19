package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.AccountEntryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class AccountChanger extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	
	 
	private String domainName;
	private String user;
	private Domain domain;
	
	private static CommonServiceAsync commonService;

	private DockLayoutPanel content;
	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanelContainer;
	private ScrollPanel centerPanel;
	
	private AonToolbarButton chakgeAll;
	private AonToolbarButton uncheckAll;
	private AonToolbarButton checkAll;
	private AccUtilitiesResult result;
	
	private AonAccountBox oldAccount;
	private AonAccountBox newAccount;
	private CheckBox changeInEntriesEnabled;
	private CheckBox changeInMastersEnabled;


	private FlexTable tab;
	private AccountPeriodBox period;
	private FlowPanel datePanel; 
	private AonDateBox fromDate;
	private AonDateBox toDate;
	private AccountEntryListBox entryListBox;
	private ListBox confidential;
	private AonIntegerBox journal;
	private AonIntegerBox fromJournal;
	private AonIntegerBox toJournal;
	private AonDoubleBox debit;
	private AonDoubleBox credit;
	private AonTextBox concept;
	private AonTextBox document;
	private ListBox activity;
	private AonTextBox comments;
	private AonIntegerBox id;
	private ListBox order;
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;

	private AonDateBox fromCreationDate;
	private AonDateBox toCreationDate;
	private AonTextBox creationUser;
	
	private AonDateBox fromModificationDate;
	private AonDateBox toModificationDate;
	private AonTextBox modificationUser;

	private boolean activitiesListBoxEnabled;

	protected AccountChanger(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);

		content = new DockLayoutPanel(Unit.PX);
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		commonService.getAonConfiguration(domainName, domain.getId(), user
			,new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					fill(result);
					setContent(content);
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error al leer la configuraci\u00F3n");
				}
		});
	}
	
	private void fill(AonConfiguration config) {
		activitiesListBoxEnabled = (config != null && config.hasActivities());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		northPanel = new SimpleLayoutPanel();
		fillNorthPanel(config);
		content.addNorth(northPanel, 220);
		centerPanelContainer = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanelContainer.setWidget(centerPanel);
		content.add(centerPanelContainer);
	}

	private void fillNorthPanel(final AonConfiguration config) {
		oldAccount = new AonAccountBox(this.domainName,this.domain.getId(), this.user);
		oldAccount.setRequired(false);
		changeInEntriesEnabled = new CheckBox("Habilitar cambio en asientos contables");
		changeInEntriesEnabled.setValue(true);
		

		newAccount= new AonAccountBox(this.domainName,this.domain.getId(), this.user);
		newAccount.setRequired(false);
		changeInMastersEnabled = new CheckBox("Habilitar cambio en ficheros maestros");
		changeInMastersEnabled.setValue(true);
		
		period = new AccountPeriodBox();
		period.fill(config.accounting().getPeriods(),true);
		period.setSelectedIndex(0);
		
		fromDate = new AonDateBox();
		toDate = new AonDateBox();
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run(false,true);
			}
		});
		
		confidential = new ListBox();
		confidential.setWidth("100px");
		confidential.addItem( "Asientos NO confidenciales" );
		confidential.addItem( "Asientos confidenciales" );
		confidential.addItem(" Todos ");
		confidential.setSelectedIndex(2);
		
		debit = new AonDoubleBox();
		debit.setVisibleLength(8);

		credit = new AonDoubleBox();
		credit.setVisibleLength(8);

		entryListBox = new AccountEntryListBox();
		entryListBox.setWidth("120px");
		
		journal = new AonIntegerBox();
		journal.setVisibleLength(8);
		
		fromJournal = new AonIntegerBox();
		fromJournal.setVisibleLength(8);
		
		toJournal = new AonIntegerBox();
		toJournal.setVisibleLength(8);
		
		concept = new AonTextBox();
		concept.setVisibleLength(10);

		document = new AonTextBox();
		document.setVisibleLength(10);
		
		comments = new AonTextBox();
		comments.setVisibleLength(30);
		
		if (activitiesListBoxEnabled) {
			activity = new ListBox();
			activity.setWidth("150px");
			activity.addItem("-- Todas --", "");
			activity.addItem("-- Sin actividad --", "-1");
			activity.setSelectedIndex(0);
			int i = 2;
			for (EnterpriseActivity ea : config.getActivities()) {
				activity.addItem(ea.getDescription(), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
		}
		
		id = new AonIntegerBox();
		id.setVisibleLength(8);

		fromCreationDate = new AonDateBox();

		toCreationDate = new AonDateBox();

		creationUser = new AonTextBox();
		creationUser.setVisibleLength(15);
		
		fromModificationDate = new AonDateBox();

		toModificationDate = new AonDateBox();

		modificationUser = new AonTextBox();
		modificationUser.setVisibleLength(15);

		order = new ListBox();
		order.setWidth("200px");
		order.addItem("Ejerc., n\u00BA diario, fecha");
		order.addItem("Fecha creaci\u00F3n, descendente");
		order.addItem("Fecha modificaci\u00F3n, descendente");
		order.setSelectedIndex(0);
	
		FlowPanel filterContainer = new FlowPanel();
		AonDisplayTable acccountsTable = new AonDisplayTable();
		acccountsTable.addStyleName(AON.CSS.aonPaddingBottom());
		acccountsTable.addStyleName(AON.CSS.aonBorderBottom());
		acccountsTable.addStyleName(AON.CSS.aonBlockCenter());
		acccountsTable.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		AonDisplayTableRow row = acccountsTable.addRow();
		AonDisplayTableCell cell1 =  row.addCell(AON.CSS.aonTableLabel());
		cell1.setWidth("150px");
		cell1.add(new Label("Cuenta origen"));
		AonDisplayTableCell cell2 =  row.addCell();
		cell2.add(oldAccount);
		cell2.setWidth("300px");
		AonDisplayTableCell cell3 =  row.addCell(AON.CSS.aonTableLabel());
		cell3.add(new Label("Cuenta destino"));
		cell3.setWidth("150px");
		AonDisplayTableCell cell4 =  row.addCell();
		cell4.add(newAccount);
		cell4.setWidth("auto");
		
		acccountsTable.addRow()
			.addCell(new InlineLabel())
			.addCell(changeInEntriesEnabled)
			.addCell(new InlineLabel())
			.addCell(changeInMastersEnabled)
			;
		;
		
		filterContainer.add(acccountsTable);
		
		tab = new FlexTable();
		tab.setWidth("95%");
		tab.setStyleName(AON.CSS.aonSearchPanel());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "1%");
		tab.getColumnFormatter().setWidth(5, "1%");
		tab.getColumnFormatter().setWidth(6, "1%");
		tab.getColumnFormatter().setWidth(7, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.CSS.aonSearchPanelLabel());
		
		datePanel = new FlowPanel();
		datePanel.setStyleName(AON.CSS.aonNowrap());
		datePanel.add(period);
		period.addStyleName(AON.CSS.aonMarginRight());
		datePanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.CSS.aonItalic());
		to.addStyleName(AON.CSS.aonMarginRight());
		to.addStyleName(AON.CSS.aonMarginLeft());
		datePanel.add(to);
		datePanel.add(toDate);
		tab.setWidget(0, 1, datePanel);
		
		tab.setWidget(0, 2, new Label(AON.MSG.type()));
		tab.getCellFormatter().setStyleName(0,2, AON.CSS.aonSearchPanelLabel());
		
		FlowPanel entryTypePanel = new FlowPanel();
		entryTypePanel.add(entryListBox);
		if (datePanel != null && config.getUser() != null && config.getUser().hasConfidentialityRole()) {
			confidential.addStyleName(AON.CSS.aonMarginLeft());
			entryTypePanel.add(confidential);
			entryTypePanel.addStyleName(AON.CSS.aonNowrap());
		}
		tab.setWidget(0, 3, entryTypePanel);
		
		tab.setWidget(0, 4, new Label(AON.MSG.journal()));
		tab.getCellFormatter().setStyleName(0,4, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(0, 5, journal);
		
		
		cleanButton = new AonSearchPanelButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		cleanButton.addStyleName(AON.CSS.aonMarginLeft());
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				oldAccount.setAccount(null, false);;
				newAccount.setAccount(null, false);;
				fromDate.setValue(null,false);
				toDate.setValue(null,false);
				entryListBox.setValue(null);
				confidential.setSelectedIndex(2);
				journal.setValue(null,false);
				debit.setValue(null,false);
				credit.setValue(null,false);
				concept.setValue(null,false);
				document.setValue(null,false);
				if (activitiesListBoxEnabled) {
					activity.setSelectedIndex(0);
				}
				period.setFocus(true);
				reset();
			}

		});

		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(), AON.CSS.aonIconSearch());
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				run(false,true);
			}
		});

		tab.setWidget(1, 0, new Label(AON.MSG.concept()));
		tab.getCellFormatter().setStyleName(1,0, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(1, 1, concept);
		
		
		tab.setWidget(1, 2, new Label(AON.MSG.debit()));
		tab.getCellFormatter().setStyleName(1,2, AON.CSS.aonSearchPanelLabel());
		
		FlowPanel amountsPanel = new FlowPanel();
		amountsPanel.setStyleName(AON.CSS.aonNowrap());
		amountsPanel.add(debit);
		InlineLabel cre= new InlineLabel(AON.MSG.credit());
		cre.setStyleName(AON.CSS.aonBold());
		cre.addStyleName(AON.CSS.aonMarginRight());
		cre.addStyleName(AON.CSS.aonMarginLeft());
		amountsPanel.add(cre);
		amountsPanel.add(credit);
		tab.setWidget(1, 3, amountsPanel);
		
		tab.setWidget(2, 0, new Label( AON.MSG.journal()));
		tab.getCellFormatter().setStyleName(2,0, AON.CSS.aonSearchPanelLabel());
		FlowPanel journalPanel = new FlowPanel();
		journalPanel.setStyleName(AON.CSS.aonNowrap());
		journalPanel.add(fromJournal);
		InlineLabel to0 = new InlineLabel("al");
		to0.setStyleName(AON.CSS.aonItalic());
		to0.addStyleName(AON.CSS.aonMarginRight());
		to0.addStyleName(AON.CSS.aonMarginLeft());
		journalPanel.add(to0);
		journalPanel.add(toJournal);
		tab.setWidget(2, 1, journalPanel);
		
		tab.setWidget(2, 2, new Label("Id Interno"));
		tab.getCellFormatter().setStyleName(2,2, AON.CSS.aonSearchPanelLabel());
		
		tab.setWidget(2, 3, id);

		tab.setWidget(2, 4, new Label(AON.MSG.document()));
		tab.getCellFormatter().setStyleName(2,4, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(2, 5, document);


		tab.setWidget(3, 0, new Label("Creado entre el "));
		tab.getCellFormatter().setStyleName(3,0, AON.CSS.aonSearchPanelLabel());
		
		FlowPanel creationDatePanel = new FlowPanel();
		creationDatePanel.setStyleName(AON.CSS.aonNowrap());
		creationDatePanel.add(fromCreationDate);
		InlineLabel to1 = new InlineLabel("y el");
		to1.setStyleName(AON.CSS.aonItalic());
		to1.addStyleName(AON.CSS.aonMarginRight());
		to1.addStyleName(AON.CSS.aonMarginLeft());
		creationDatePanel.add(to1);
		creationDatePanel.add(toCreationDate);
		tab.setWidget(3, 1, creationDatePanel);

		tab.setWidget(3, 2,  new Label("por"));
		tab.getCellFormatter().setStyleName(3,2, AON.CSS.aonSearchPanelLabel());
		
		tab.setWidget(3, 3, creationUser);
		
		if (activitiesListBoxEnabled) {
			tab.setWidget(3, 4, new Label(AON.MSG.activity()));
			tab.setWidget(3, 5, activity);
		} else {
			tab.setWidget(3, 4, new Label());
			tab.setWidget(3, 5, new Label());
		}
		tab.getCellFormatter().setStyleName(3,4, AON.CSS.aonSearchPanelLabel());


		tab.setWidget(4, 0, new Label("Modificado entre"));
		tab.getCellFormatter().setStyleName(4,0, AON.CSS.aonSearchPanelLabel());

		FlowPanel modificationDatePanel = new FlowPanel();
		modificationDatePanel.setStyleName(AON.CSS.aonNowrap());
		modificationDatePanel.add(fromModificationDate);
		InlineLabel to2 = new InlineLabel("y el");
		to2.setStyleName(AON.CSS.aonItalic());
		to2.addStyleName(AON.CSS.aonMarginRight());
		to2.addStyleName(AON.CSS.aonMarginLeft());
		modificationDatePanel.add(to2);
		modificationDatePanel.add(toModificationDate);
		
		tab.setWidget(4, 1, modificationDatePanel);

		tab.setWidget(4, 2, new Label("por"));
		tab.getCellFormatter().setStyleName(4,2, AON.CSS.aonSearchPanelLabel());
		
		tab.setWidget(4, 3, modificationUser);
		
		tab.setWidget(5, 0, new Label(AON.MSG.comments()));
		tab.getCellFormatter().setStyleName(5,0, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(5, 1, comments);

		tab.setWidget(5, 2, new Label("Orden"));
		tab.getCellFormatter().setStyleName(5,2, AON.CSS.aonSearchPanelLabel());
		
		tab.setWidget(5, 3, order);
		tab.getFlexCellFormatter().setColSpan(5, 3, 2);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonNowrap());
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(5, 4, buttonsPanel);
		filterContainer.add(tab);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(filterContainer);
		northPanel.setWidget(scrollPanel);
	}

	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Cambio de cuenta contable";
	}

	protected Widget getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar(getOptionDescription());
		
		AonToolbarButton searchButton  = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run(false,true);
			}
		});
		toolbar.add(searchButton);

		checkAll  = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll .addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run(true,true);
			}
		});
		toolbar.add(checkAll );
		
		uncheckAll  = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run(false,true);
			}
		});
		toolbar.add(uncheckAll);
		
		chakgeAll  = new AonToolbarButton( AON.MSG.changeSelected(), AON.CSS.aonIconSwap() );
		chakgeAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				changeAll(false);
			}
		});
		toolbar.add(chakgeAll);
		return toolbar;
	}
	
	protected void changeAll(boolean b) {
		int count = 0;
		if (this.result != null) {
			for (IAccUtilitiesItem item : this.result.getItems()) {
				AccUtilitiesAccountChangeItem  it = (AccUtilitiesAccountChangeItem) item;
				count = count + (it.isSelected()?1:0);
			}
		}
		if (count > 0 ) {
			if (check()) {
				final int zcount = count;
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm("Cambio de cuentas"
						,"Continuar con el cambio de cuentas de " + count + " registros?"
						, new AonConfirmDialogCallback() {
							
							@Override
							public void onCancel() {
							}
							
							@Override
							public void onAccept() {
								cd.hide();
								final PopupPanel popup = new PopupPanel(false, true);
								popup.add(getSplashWidget());
								popup.setGlassEnabled(true);
								popup.setAnimationEnabled(true);
								popup.center();

								prepareInfoPanel();
								int x = 0;
								for (IAccUtilitiesItem item : AccountChanger.this.result.getItems()) {
									AccUtilitiesAccountChangeItem it = (AccUtilitiesAccountChangeItem) item;
									if (it.isSelected()) {
										x++;
										
										final int z = x;
										SERVICE.fixAccountChange(domainName, AccountChanger.this.user, it.getDomain(), getWidgetParams(), it, new AsyncCallback<AccUtilitiesResult>() {
											
											@Override
											public void onFailure(Throwable caught) {
												addErrorPanel("[ERROR] " + item.getMessage() + " - " + caught.getMessage());
												if (z == zcount) {
													popup.hide();
													run(false,false);
												}
											}
											
											@Override
											public void onSuccess(AccUtilitiesResult result) {
												addInfoPanel("[CAMBIADO] " + item.getMessage());
												if (z == zcount) {
													popup.hide();
													run(false,false);
												}
											}
										});
									}
								}
							}
						});
			}
		} else {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm( "", "No se ha seleccionado ning\u00FAn registro. Nada que cambiar." , new AonConfirmDialogCallback() {
				@Override public void onAccept() {}
				@Override public void onCancel() {}
				
			});
		}
	}

	public void run() {
		run(false,true);
	}
	
	public void run(boolean check, boolean cleanFooter) {
		if (check()) {
			AccUtilitiesAccountChangeParams params = getWidgetParams();
			final PopupPanel popup = new PopupPanel(false, true);
			popup.add(getSplashWidget());
			popup.setGlassEnabled(true);
			popup.setAnimationEnabled(true);
			popup.center();
			SERVICE.searchAccountChange(domainName, user, domain.getId(), params, new AsyncCallback<AccUtilitiesResult>(){
	
				@Override
				public void onFailure(Throwable caught) {
					openFootPanelIfNeeded();
					showErrorPanel(caught.getMessage());
					popup.hide();
				}
	
				@Override
				public void onSuccess(AccUtilitiesResult result) {
					popup.hide();
					if (cleanFooter) {
						cleanErrorPanel();
					}
					centerPanel.setWidget( paintResults(result, check) );
				}
			});
		}
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		return paintResults(result, false);
	}
	
	private Widget paintResults(AccUtilitiesResult accUtilitiesResult, boolean check) {
		this.result = accUtilitiesResult;
		AonDisplayGrid log = new AonDisplayGrid( );
		log.addStyleName(AON.CSS.aonWidthAlmostAll());
		log.addStyleName(AON.CSS.aonMarginBottom());
		if (this.result != null && !this.result.isEmpty()) {
			AonDisplayGridHeaderRow headerRow = log.addHeaderRow();
			AonDisplayGridCell headerCell1 =  headerRow.addCell();
			headerCell1.setWidth("50px");
			AonDisplayGridCell headerCell2 =  headerRow.addCell();
			headerCell2.setWidth("50px");
			Label headerCell3Label = new Label(AON.MSG.message());
			AonDisplayGridCell headerCell3 =  headerRow.addCell(AON.CSS.aonFlexGrow1());
			headerCell3.add(headerCell3Label);
			headerCell3.setWidth("auto");
			
			for (IAccUtilitiesItem item : this.result.getItems()) {
				AccUtilitiesAccountChangeItem it = (AccUtilitiesAccountChangeItem) item;
				it.setSelected(check);
				item.getType().visit( new AccountChangeVisitor(log, it) );
			}
		} else {
			Label label = new Label(AON.MSG.noData());
			log.add(label);
		}
		return log;
	}

	private class AccountChangeVisitor implements IAccUtilitiesItemTypeVisitor {
		private AonDisplayGrid logPanel;
		private AccUtilitiesAccountChangeItem item;
		
		public AccountChangeVisitor(AonDisplayGrid logPanel, AccUtilitiesAccountChangeItem item) {
			this.logPanel = logPanel;
			this.item = item;
		}
		
		@Override public void visitUnbalancedEntry(AccUtilitiesItemType type) {}
		@Override public void visitParentAccountLinker(AccUtilitiesItemType type) {}
		@Override public void visitOther(AccUtilitiesItemType type) {}
		@Override public void visitInfoMessage(AccUtilitiesItemType type) {}
		@Override public void visitErrorMessage(AccUtilitiesItemType type) {}
		@Override public void visitCustomerAccount(AccUtilitiesItemType type) {}
		@Override public void visitSupplierAccount(AccUtilitiesItemType type) {}
		@Override public void visitCreditorAccount(AccUtilitiesItemType type) {}
		@Override public void visitAccountIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitDomainIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitNoLowLevelAccount(AccUtilitiesItemType type) {}
		@Override public void visitWrongRecordedInvoices(AccUtilitiesItemType type) {}
		@Override public void visitEmptyEntry(AccUtilitiesItemType type) {}
		@Override public void visitInvoiceIntegrity(AccUtilitiesItemType type) {}
		@Override public void visitDeleteEntries(AccUtilitiesItemType type) {}
		@Override public void visitOutOfDateEntry(AccUtilitiesItemType type) {}
		
		@Override public void visitAccountChange(AccUtilitiesItemType type) {
			
			InlineLabel messageLabel = new InlineLabel(item.getMessage());
			
			AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction()
					, item.isSelected()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
			checkButton.addStyleName(AON.CSS.aonMarginLeft());
			
			checkButton.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					item.setSelected(!item.isSelected());
					if (item.isSelected()) {
						checkButton.addStyleName(AON.CSS.aonIconChecked());
						checkButton.removeStyleName(AON.CSS.aonIconCheck());
					} else {
						checkButton.addStyleName(AON.CSS.aonIconCheck());			
						checkButton.removeStyleName(AON.CSS.aonIconChecked());
					}
				}
			});

			AonTableButton changeButton = new AonTableButton("Cambiar", AON.CSS.aonIconSwap());
			changeButton.addStyleName(AON.CSS.aonMarginLeft());
			changeButton.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (check()) {
						changeButton.setEnabled(false);
						AonConfirmDialog cd = new AonConfirmDialog();
						cd.confirm("Continuar?", "Cambiar cuenta", new AonConfirmDialogCallback() {
							
							@Override
							public void onCancel() {
								changeButton.setEnabled(true);
							}
							
							@Override
							public void onAccept() {
								prepareInfoPanel();
								SERVICE.fixAccountChange(domainName, AccountChanger.this.user, item.getDomain(), getWidgetParams(), item, new AsyncCallback<AccUtilitiesResult>() {
									
									@Override
									public void onFailure(Throwable caught) {
										changeButton.setEnabled(true);
										addErrorPanel("[ERROR] " + item.getMessage() + " - " + caught.getMessage());
									}
									
									@Override
									public void onSuccess(AccUtilitiesResult result) {
										if (result != null && result.getItems() != null && !result.getItems().isEmpty()) {
											messageLabel.setText( result.getItems().getFirst().getMessage());
											messageLabel.addStyleName(AON.CSS.aonColorBlue());
										}
										checkButton.setVisible(false);
										changeButton.setEnabled(false);
										changeButton.removeStyleName(AON.CSS.aonIconSwap());
										changeButton.addStyleName(AON.CSS.aonIconValid());
									}
									
								});
							}
						});
					}
				}
			});
			logPanel.addRow()
				.addCell(checkButton)
				.addCell(changeButton)
				.addCell(messageLabel);
		}
		
	}
	public boolean check(){
		if (oldAccount.getId() == null) {
			prepareInfoPanel();
			addErrorPanel("No ha indicado cuanta origen");
			return false;
		} else if (newAccount.getId() == null) {
			prepareInfoPanel();
			addErrorPanel("No ha indicado cuanta destino");
			return false;
		} else if (AonNumberUtils.equals( newAccount.getId(), oldAccount.getId())) {
			prepareInfoPanel();
			addErrorPanel("La cuenta origen y destino so iguales");
			return false;
		}
		return true;
	}
	
	private void reset() {
		this.result = null;
		centerPanel.setWidget(new FlowPanel());	
	}

	public AccUtilitiesAccountChangeParams getWidgetParams() {
		Integer activityId = null;
		if (activitiesListBoxEnabled) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		return new AccUtilitiesAccountChangeParams()
				.setDomain(this.domain.getId())
				.setOldAccount( new Account().setId(oldAccount.getId()).setCode(oldAccount.getCode()).setDescription(oldAccount.getDescription()))
				.setNewAccount( new Account().setId(newAccount.getId()).setCode(newAccount.getCode()).setDescription(newAccount.getDescription()))
				.setChangeInEntriesEnabled(changeInEntriesEnabled.getValue())
				.setChangeInMastersEnabled(changeInMastersEnabled.getValue())
				.setAccountEntryParams(new AccountEntryParams()
					.setDomain(this.domain.getId())
					.setAccountEntryId(id.getValue())
					.setPeriod(period.getValue())
					.setFromDate(fromDate.getValue())
					.setToDate(toDate.getValue())
					.setType(entryListBox.getValue())
					.setJournal(journal.getValue())
					.setFromJournal(fromJournal.getValue())
					.setToJournal(toJournal.getValue())
					.setActivity(activityId)
					.setAccount(oldAccount.getId())
					.setBalancingAccount(oldAccount.getId())
					.setDebit(debit.getValue())
					.setCredit(credit.getValue())
					.setConcept(concept.getValue())
					.setDocument(document.getValue())
					.setComments(comments.getValue())
					.setSecurityLevel(confidential!=null?SecurityLevel.safeValueOf(confidential.getSelectedIndex()):SecurityLevel.OFFICIAL)
					.setOrder(order.getSelectedIndex())
					.setFromCreationDate(fromCreationDate.getValue())
					.setToCreationDate(toCreationDate.getValue())
					.setCreationUser(creationUser.getValue())
					.setFromModificationDate(fromModificationDate.getValue())
					.setToModificationDate(toModificationDate.getValue())
					.setModificationUser(modificationUser.getValue())
				)
			;
	}
}
