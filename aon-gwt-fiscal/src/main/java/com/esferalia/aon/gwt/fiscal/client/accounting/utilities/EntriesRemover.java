package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.AccountEntryListBox;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountPeriodBox;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesRemoveEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.IAccUtilitiesItemTypeVisitor;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

class EntriesRemover extends OptionBase {

	private static AccountEntryServiceAsync ACCOUNT_ENTRY_SERVICE;
	private static AccountingUtilitiesServiceAsync SERVICE;
	
	 
	private String domainName;
	private String user;
	private Domain domain;
	
	private static CommonServiceAsync commonService;

	private DockLayoutPanel content;
	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanelContainer;
	private ScrollPanel centerPanel;
	
	private AonToolbarButton deleteAll;
	private AonToolbarButton uncheckAll;
	private AonToolbarButton checkAll;
	private AccUtilitiesResult result;
	
	private FlexTable tab;
	private AccountPeriodBox period;
	private FlowPanel datePanel; 
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private AccountEntryListBox entryListBox;
	private ListBox confidential;
	private IntegerBox journal;
	private IntegerBox fromJournal;
	private IntegerBox toJournal;
	private AccountBox account;
	private DoubleBox debit;
	private DoubleBox credit;
	private TextBox concept;
	private TextBox document;
	private ListBox activity;
	private TextBox comments;
	private IntegerBox id;
	private ListBox order;
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;

	private DateBoxEx fromCreationDate;
	private DateBoxEx toCreationDate;
	private TextBox creationUser;
	
	private DateBoxEx fromModificationDate;
	private DateBoxEx toModificationDate;
	private TextBox modificationUser;

	private boolean activitiesListBoxEnabled;

	protected EntriesRemover(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);

		AccountEntryServiceAsync accountEntryServiceRaw = GWT.create(AccountEntryService.class);
		ACCOUNT_ENTRY_SERVICE = new AccountEntryServiceAsyncDecorator(accountEntryServiceRaw);

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
		content.addNorth(northPanel, 160);
		centerPanelContainer = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanelContainer.setWidget(centerPanel);
		content.add(centerPanelContainer);
	}

	private void fillNorthPanel(final AonConfiguration config) {
		period = new AccountPeriodBox();
		period.fill(config.accounting().getPeriods(),true);
		period.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				run(false);
			}
		});
		
		fromDate = new DateBoxEx();
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run(false);
			}
		});
		toDate = new DateBoxEx();
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run(false);
			}
		});
		
		confidential = new ListBox();
		confidential.setWidth("100px");
		confidential.addItem( "Asientos NO confidenciales" );
		confidential.addItem( "Asientos confidenciales" );
		confidential.addItem(" Todos ");
		confidential.setSelectedIndex(2);
		confidential.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				run(false);
			}
		});
		
		account = new AccountBox(this.domainName,this.domain.getId(), this.user);
		account.setRequired(false);
		account.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				run(false);
			}
		});
		debit = new DoubleBox();
		debit.setVisibleLength(8);
		debit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				run(false);
			}
		});
		credit = new DoubleBox();
		credit.setVisibleLength(8);
		credit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				run(false);
			}
		});
		entryListBox = new AccountEntryListBox();
		entryListBox.setValue(AccountEntryType.MANUAL);
		entryListBox.setEnabled(false);
		entryListBox.setWidth("120px");
		entryListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				run(false);
			}
		});
		
		journal = new IntegerBox();
		journal.setVisibleLength(8);
		journal.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				run(false);
			}
		});
		
		fromJournal = new IntegerBox();
		fromJournal.setVisibleLength(8);
		fromJournal.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				run(false);
			}
		});
		
		toJournal = new IntegerBox();
		toJournal.setVisibleLength(8);
		toJournal.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				run(false);
			}
		});
		
		concept = new TextBox();
		concept.setVisibleLength(10);
		concept.setStyleName(AON.CSS.aonInputText());
		concept.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				run(false);
			}
		});
		document = new TextBox();
		document.setVisibleLength(10);
		document.setStyleName(AON.CSS.aonInputText());
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				run(false);
			}
		});
		
		comments = new TextBox();
		comments.setVisibleLength(30);
		comments.setStyleName(AON.CSS.aonInputText());
		comments.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				run(false);
			}
		});
		
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
			activity.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					run(false);
				}
			});
		}
		
		id = new IntegerBox();
		id.setVisibleLength(8);
		id.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				run(false);
			}
		});

		fromCreationDate = new DateBoxEx();
		fromCreationDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run(false);
			}
		});
		toCreationDate = new DateBoxEx();
		toCreationDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run(false);
			}
		});
		creationUser = new TextBox();
		creationUser.setVisibleLength(15);
		creationUser.setStyleName(AON.CSS.aonInputText());
		creationUser.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				run(false);
			}
		});
		
		fromModificationDate = new DateBoxEx();
		fromModificationDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run(false);
			}
		});
		toModificationDate = new DateBoxEx();
		toModificationDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run(false);
			}
		});
		modificationUser = new TextBox();
		modificationUser.setVisibleLength(15);
		modificationUser.setStyleName(AON.CSS.aonInputText());
		modificationUser.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				run(false);
			}
		});

		order = new ListBox();
		order.setWidth("200px");
		order.addItem("Ejerc., n\u00BA diario, fecha");
		order.addItem("Fecha creaci\u00F3n, descendente");
		order.addItem("Fecha modificaci\u00F3n, descendente");
		order.setSelectedIndex(0);
		order.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				run(false);
			}
		});
		
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
				period.selectDefaultPeriod();
				fromDate.setValue(null,false);
				toDate.setValue(null,false);
				entryListBox.setValue(null);
				confidential.setSelectedIndex(2);
				journal.setValue(null,false);
				account.setAccount(null, false);;
				debit.setValue(null,false);
				credit.setValue(null,false);
				concept.setValue(null,false);
				document.setValue(null,false);
				if (activitiesListBoxEnabled) {
					activity.setSelectedIndex(0);
				}
				period.setFocus(true);
				run(false);
			}
		});

		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(), AON.CSS.aonIconSearch());
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				run(false);
			}
		});

		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1,0, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(1, 1, account);
		
		
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
		
		tab.setWidget(1, 4, new Label(AON.MSG.concept()));
		tab.getCellFormatter().setStyleName(1,4, AON.CSS.aonSearchPanelLabel());

		tab.setWidget(1, 5, concept);

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

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		northPanel.setWidget(scrollPanel);
	}

	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Borrado de apuntes manuales";
	}

	@Override
	protected Widget getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar(getOptionDescription());
		
		AonToolbarButton searchButton  = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run(false);
			}
		});
		toolbar.add(searchButton);

		checkAll  = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll .addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run(true);
			}
		});
		toolbar.add(checkAll );
		
		uncheckAll  = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run(false);
			}
		});
		toolbar.add(uncheckAll);
		
		deleteAll  = new AonToolbarButton( AON.MSG.deleteSelected(), AON.CSS.aonIconDelete() );
		deleteAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deleteAll(false);
			}
		});
		toolbar.add(deleteAll);
		return toolbar;
	}
	
	protected void deleteAll(boolean b) {
		double count = 0;
		if (this.result != null) {
			for (IAccUtilitiesItem item : this.result.getItems()) {
				AccUtilitiesRemoveEntryItem it = (AccUtilitiesRemoveEntryItem) item;
				count = count + (it.isSelected()?1:0);
			}
		}
		if (count > 0) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm("Borrado masivo de apuntes manuales"
					,"Continuar con el borrado de " + count + " apuntes? \n Esta operaci\u00F3n es irreversible."
					, new AonConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
						}
						
						@Override
						public void onAccept() {
							prepareInfoPanel();
							for (IAccUtilitiesItem item : EntriesRemover.this.result.getItems()) {
								AccUtilitiesRemoveEntryItem it = (AccUtilitiesRemoveEntryItem) item;
								if (it.isSelected()) {
									Occam occam = new Occam()
										.setDomainName(domainName)
										.setDomain(it.getDomain())
										.setUser(EntriesRemover.this.user);
									ACCOUNT_ENTRY_SERVICE.deleteAccountEntry(occam, it.getEntryId(), new AsyncCallback<Void>() {
										
										@Override
										public void onFailure(Throwable caught) {
											addErrorPanel("[ERROR] " + item.getMessage() + " - " + caught.getMessage());
										}
										
										@Override
										public void onSuccess(Void result) {
											addInfoPanel("[BORRADO] " + item.getMessage());
										}
									});
								}
							}
						}
					});
		} else {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm( "", "No se ha seleccionado ning\u00FAn apunte. Nada que borrar." , new AonConfirmDialogCallback() {
				@Override public void onAccept() {}
				@Override public void onCancel() {}
				
			});
		}
	}

	public void run() {
		run(false);
	}
	
	public void run(boolean check) {
		AccountEntryParams params = getWidgetParams();
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(getSplashWidget());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		SERVICE.removeEntries(domainName, user, domain, params, new AsyncCallback<AccUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
				popup.hide();
			}

			@Override
			public void onSuccess(AccUtilitiesResult result) {
				popup.hide();
				cleanErrorPanel();
				centerPanel.setWidget( paintResults(result, check) );
			}
		});
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		return paintResults(result, false);
	}
	
	private Widget paintResults(AccUtilitiesResult accUtilitiesResult, boolean check) {
		this.result = accUtilitiesResult;
		FlowPanel log = new FlowPanel( PreElement.TAG );
		log.setStyleName(AON.CSS.aonFontSmaller());
		log.addStyleName(AON.CSS.aonMarginBottom());
		if (this.result != null && !this.result.isEmpty()) {
			StringBuffer buf = new StringBuffer();
			buf.append(AonStringUtils.repeat(AonStringUtils.SPACE,20));
			buf.append(AonStringUtils.rightPad("Diario",10));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Fecha",10));
			buf.append(AonStringUtils.SPACE);
			buf.append(" C ");
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Tipo apunte",20));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Fecha creaci\u00F3n",19));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Usuario creac.",15));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Comentarios",38));
			InlineLabel headerLabel = new InlineLabel(buf.toString());
			headerLabel.setStyleName(AON.CSS.aonTextUnderline());
 			log.add(headerLabel);
			for (IAccUtilitiesItem item : this.result.getItems()) {
				AccUtilitiesRemoveEntryItem it = (AccUtilitiesRemoveEntryItem) item;
				it.setSelected(check);
				item.getType().visit( new RemoverVisitor(log, it) );
			}
		} else {
			Label label = new Label(AON.MSG.noData());
			log.add(label);
		}
		return log;
	}

	private void showEntry(int domain,Integer entryId) {
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad( new AccountEntryModuleOptions()
			.setParentWidget( entryDialog)
			.setDomainName( domainName)
			.setUser( user ) 
			.setDomain( domain )
			.setAccountEntryId( entryId )
			.setSessionLogTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setPreviewTabVisible(true)
			.setTrialBalanceFromPreviewEnabled(false)
			.setExternalCallback( new ModuleCallback() {
			
				private static final long serialVersionUID = 1496949140384557847L;
				@Override public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
				}
				@Override public void onFailure(Throwable caught) {}
				@Override public void onExit() {
					entryDialog.hide();
				}
				@Override public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}

	private class RemoverVisitor implements IAccUtilitiesItemTypeVisitor {
		private FlowPanel logPanel;
		private AccUtilitiesRemoveEntryItem item;
		
		public RemoverVisitor(FlowPanel logPanel, AccUtilitiesRemoveEntryItem item) {
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
		@Override public void visitAccountChange(AccUtilitiesItemType type) {}
		@Override public void visitOutOfDateEntry(AccUtilitiesItemType type) {}
		
		@Override public void visitDeleteEntries(AccUtilitiesItemType type) {
			FlowPanel itemPanel = new FlowPanel();
			AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction()
					, item.isSelected()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
			checkButton.addStyleName(AON.CSS.aonMarginLeft());
			itemPanel.add(checkButton);
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

			AonTableButton showEntryButton = new AonTableButton("Ver/Editar", AON.CSS.aonIconSearch());
			showEntryButton.addStyleName(AON.CSS.aonMarginLeft());
			itemPanel.add(showEntryButton);
			showEntryButton.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showEntry(item.getDomain(),item.getEntryId());
				}
			});
			
			AonTableButton removeButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			removeButton.addStyleName(AON.CSS.aonMarginLeft());
			itemPanel.add(removeButton);
			removeButton.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ConfirmDialog cd = new ConfirmDialog();
					cd.confirm("Continuar?", "Borrar", new ConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
						}
						
						@Override
						public void onAccept() {
							Occam occam = new Occam()
								.setDomainName(domainName)
								.setDomain(item.getDomain())
								.setUser(EntriesRemover.this.user);
							ACCOUNT_ENTRY_SERVICE.deleteAccountEntry(occam, item.getEntryId(), new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									openFootPanelIfNeeded();
									showErrorPanel(caught.getMessage());
								}

								@Override
								public void onSuccess(Void result) {
									run(false);
								}
								
							});
						}
					});
				}
			});
			
			itemPanel.add(new InlineLabel(item.getMessage()));
			logPanel.add(itemPanel);
		}

	}
	
	public AccountEntryParams getWidgetParams() {
		Integer activityId = null;
		if (activitiesListBoxEnabled) {
			if (activity.getSelectedIndex() > 0 ) {
				activityId = AonNumberUtils.toInteger(activity.getSelectedValue());
			}
		}
		return new AccountEntryParams()
			.setAccountEntryId(id.getValue())
			.setDomain(this.domain.getId())
			.setPeriod(period.getValue())
			.setFromDate(fromDate.getValue())
			.setToDate(toDate.getValue())
			.setType(entryListBox.getValue())
			.setJournal(journal.getValue())
			.setFromJournal(fromJournal.getValue())
			.setToJournal(toJournal.getValue())
			.setActivity(activityId)
			.setAccount(account.getId())
			.setBalancingAccount(account.getId())
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
			;
	}
}
