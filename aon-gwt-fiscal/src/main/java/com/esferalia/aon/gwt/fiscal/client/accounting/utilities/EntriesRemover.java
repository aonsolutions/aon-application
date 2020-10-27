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
import com.google.gwt.user.client.ui.Button;
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
	
	
	private FlexTable tab;
	private AccountPeriodBox period;
	private FlowPanel datePanel; 
	private DateBoxEx fromDate;
	private DateBoxEx toDate;
	private AccountEntryListBox entryListBox;
	private ListBox confidential;
	private IntegerBox journal;
	private AccountBox account;
	private DoubleBox debit;
	private DoubleBox credit;
	private TextBox concept;
	private TextBox document;
	private ListBox activity;
	private TextBox comments;
	private IntegerBox id;
	private ListBox order;
	private Button cleanButton;
	private Button refreshButton;

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
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		northPanel = new SimpleLayoutPanel();
		fillNorthPanel(config);
		content.addNorth(northPanel, 120);
		centerPanelContainer = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanelContainer.setWidget(centerPanel);
		content.add(centerPanelContainer);
	}

	private void fillNorthPanel(final AonConfiguration config) {
		period = new AccountPeriodBox();
		period.fill(config.getPeriods(),true);
		period.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				run();
			}
		});
		
		fromDate = new DateBoxEx();
		fromDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run();
			}
		});
		toDate = new DateBoxEx();
		toDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				run();
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
				run();
			}
		});
		
		account = new AccountBox(this.domainName,this.domain.getId(), this.user);
		account.setRequired(false);
		account.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				run();
			}
		});
		debit = new DoubleBox();
		debit.setVisibleLength(8);
		debit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				run();
			}
		});
		credit = new DoubleBox();
		credit.setVisibleLength(8);
		credit.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				run();
			}
		});
		entryListBox = new AccountEntryListBox();
		entryListBox.setValue(AccountEntryType.MANUAL);
		entryListBox.setEnabled(false);
		entryListBox.setWidth("120px");
		entryListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				run();
			}
		});
		
		journal = new IntegerBox();
		journal.setVisibleLength(8);
		journal.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				run();
			}
		});
		concept = new TextBox();
		concept.setVisibleLength(10);
		concept.setStyleName(AON.AON_CSS.aonInputText());
		concept.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				run();
			}
		});
		document = new TextBox();
		document.setVisibleLength(10);
		document.setStyleName(AON.AON_CSS.aonInputText());
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				run();
			}
		});
		
		comments = new TextBox();
		comments.setVisibleLength(30);
		comments.setStyleName(AON.AON_CSS.aonInputText());
		comments.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				run();
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
					run();
				}
			});
		}
		
		id = new IntegerBox();
		id.setVisibleLength(8);
		id.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				run();
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
				run();
			}
		});
		
		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		
		tab.getColumnFormatter().setWidth(0, "1%");
		tab.getColumnFormatter().setWidth(1, "1%");
		tab.getColumnFormatter().setWidth(2, "1%");
		tab.getColumnFormatter().setWidth(3, "1%");
		tab.getColumnFormatter().setWidth(4, "1%");
		tab.getColumnFormatter().setWidth(5, "1%");
		tab.getColumnFormatter().setWidth(6, "1%");
		tab.getColumnFormatter().setWidth(7, "auto");
		
		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear() +"/"+ AON.MSG.date()));
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonPanelGridOdd());
		
		datePanel = new FlowPanel();
		datePanel.setStyleName(AON.AON_CSS.aonNowrap());
		datePanel.add(period);
		period.addStyleName(AON.AON_CSS.aonMarginRight());
		datePanel.add(fromDate);
		InlineLabel to = new InlineLabel(AON.MSG.to());
		to.setStyleName(AON.AON_CSS.aonItalic());
		to.addStyleName(AON.AON_CSS.aonMarginRight());
		to.addStyleName(AON.AON_CSS.aonMarginLeft());
		datePanel.add(to);
		datePanel.add(toDate);
		tab.setWidget(0, 1, datePanel);
		tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(0, 2, new Label(AON.MSG.type()));
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonPanelGridOdd());
		
		FlowPanel entryTypePanel = new FlowPanel();
		entryTypePanel.add(entryListBox);
		if (datePanel != null && config.getUser() != null && config.getUser().hasConfidentialityRole()) {
			confidential.addStyleName(AON.AON_CSS.aonMarginLeft());
			entryTypePanel.add(confidential);
			entryTypePanel.addStyleName(AON.AON_CSS.aonNowrap());
		}
		tab.setWidget(0, 3, entryTypePanel);
		tab.getCellFormatter().setStyleName(0,3, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(0, 4, new Label(AON.MSG.journal()));
		tab.getCellFormatter().setStyleName(0,4, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(0, 5, journal);
		tab.getCellFormatter().setStyleName(0,5, AON.AON_CSS.aonPanelGridEven());
		
		if (activitiesListBoxEnabled) {
			tab.setWidget(0, 6, new Label(AON.MSG.activity()));
			tab.setWidget(0, 7, activity);
		} else {
			tab.setWidget(0, 6, new Label());
			tab.setWidget(0, 7, new Label());
		}
		tab.getCellFormatter().setStyleName(0,6, AON.AON_CSS.aonPanelGridOdd());
		tab.getCellFormatter().setStyleName(0,7, AON.AON_CSS.aonPanelGridEven());	
		
		cleanButton = new Button();
		cleanButton.setTitle(AON.MSG.clean());
		cleanButton.setStyleName(AON.AON_CSS.aonIconDelete());
		cleanButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
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
				run();
			}
		});

		refreshButton = new Button();
		refreshButton.setTitle(AON.MSG.refresh());
		refreshButton.setStyleName(AON.AON_CSS.aonIconRefresh());
		refreshButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		refreshButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		refreshButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});

		tab.setWidget(1, 0, new Label(AON.MSG.account()));
		tab.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 1, account);
		tab.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		
		tab.setWidget(1, 2, new Label(AON.MSG.debit()));
		tab.getCellFormatter().setStyleName(1,2, AON.AON_CSS.aonPanelGridOdd());
		
		FlowPanel amountsPanel = new FlowPanel();
		amountsPanel.setStyleName(AON.AON_CSS.aonNowrap());
		amountsPanel.add(debit);
		InlineLabel cre= new InlineLabel(AON.MSG.credit());
		cre.setStyleName(AON.AON_CSS.aonBold());
		cre.addStyleName(AON.AON_CSS.aonMarginRight());
		cre.addStyleName(AON.AON_CSS.aonMarginLeft());
		amountsPanel.add(cre);
		amountsPanel.add(credit);
		tab.setWidget(1, 3, amountsPanel);
		tab.getCellFormatter().setStyleName(1,3, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget(1, 4, new Label(AON.MSG.concept()));
		tab.getCellFormatter().setStyleName(1,4, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(1, 5, concept);
		tab.getCellFormatter().setStyleName(1,5, AON.AON_CSS.aonPanelGridEven());

		tab.setWidget(2, 0, new Label(AON.MSG.comments()));
		tab.getCellFormatter().setStyleName(2,0, AON.AON_CSS.aonPanelGridOdd());
		
		tab.setWidget(2, 1, comments);
		tab.getCellFormatter().setStyleName(2, 1, AON.AON_CSS.aonPanelGridEven());

		tab.setWidget(2, 2, new Label("Id Interno"));
		tab.getCellFormatter().setStyleName(2,2, AON.AON_CSS.aonPanelGridOdd());
		tab.getCellFormatter().addStyleName(2,2, AON.AON_CSS.aonTextRight());
		
		tab.setWidget(2, 3, id);
		tab.getCellFormatter().setStyleName(2,3, AON.AON_CSS.aonPanelGridEven());

		tab.setWidget(3, 0, new Label(AON.MSG.document()));
		tab.getCellFormatter().setStyleName(3,0, AON.AON_CSS.aonPanelGridOdd());

		tab.setWidget(3, 1, document);
		tab.getCellFormatter().setStyleName(3,1, AON.AON_CSS.aonPanelGridEven());

		tab.setWidget(3, 2, new Label("Orden"));
		tab.getCellFormatter().setStyleName(3,2, AON.AON_CSS.aonPanelGridOdd());
		
		tab.setWidget(3, 3, order);
		tab.getCellFormatter().setStyleName(3,3, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(3, 3, 2);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.AON_CSS.aonNowrap());
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(3, 4, buttonsPanel);
		tab.getCellFormatter().setStyleName(3,4, AON.AON_CSS.aonPanelGridEven());

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		northPanel.setWidget(scrollPanel);
	}

	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Borrado de apuntes manuales";
	}

	protected Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label(getOptionDescription()));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		final Button refresh = new Button();
		refresh.setText(AON.MSG.refresh());
		refresh.setTitle(AON.MSG.refresh());
		refresh.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		refresh.addStyleName(AON.AON_CSS.aonIconRedo());
		refresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		buttonContainer.add(refresh);
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	

	public void run() {
		AccountEntryParams params = getWidgetParams();
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
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
				centerPanel.setWidget( paintResults(result) );
			}
		});
	}

	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		FlowPanel log = new FlowPanel( PreElement.TAG );
		log.setStyleName(AON.AON_CSS.aonFixedFont());
		log.addStyleName(AON.AON_CSS.aonMarginBottom());
		log.addStyleName(AON.AON_CSS.aonFontSmall());
		log.addStyleName(AON.AON_CSS.aonReport());
		if (result != null && !result.isEmpty()) {
			StringBuffer buf = new StringBuffer();
			buf.append(AonStringUtils.repeat(AonStringUtils.SPACE,26));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Diario",10));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Fecha",10));
			buf.append(AonStringUtils.SPACE);
			buf.append(" C ");
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Tipo apunte",20));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Fecha creac.",19));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Usuario",15));
			buf.append(AonStringUtils.SPACE);
			buf.append(AonStringUtils.rightPad("Comentarios",38));
			InlineLabel headerLabel = new InlineLabel(buf.toString());
			headerLabel.setStyleName(AON.AON_CSS.aonFixedFont());
			headerLabel.addStyleName(AON.AON_CSS.aonTextUnderline());
 			log.add(headerLabel);
			for (IAccUtilitiesItem item : result.getItems()) {
				item.getType().visit( new RemoverVisitor(log, (AccUtilitiesRemoveEntryItem) item) );
			}
		} else {
			Label label = new Label(AON.MSG.noData());
			log.add(label);
		}
		return log;
	}

	private void showEntry(int domain,Integer entryId) {
		CustomPopup entryDialog = new CustomPopup();
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
			.setExternalCallback( new ModuleCallback() {
			
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
		
		@Override public void visitDeleteEntries(AccUtilitiesItemType type) {
			FlowPanel itemPanel = new FlowPanel();
			InlineLabel clickLabel = new InlineLabel("Ver/Editar");
			clickLabel.setTitle("Click para Ver/Editar");
			clickLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			clickLabel.addStyleName(AON.AON_CSS.aonIconLoupe());
			clickLabel.addStyleName(AON.AON_CSS.aonClickableBlock());
			clickLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
			itemPanel.add(clickLabel);
			clickLabel.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showEntry(item.getDomain(),item.getEntryId());
				}
			});
			
			InlineLabel removeLabel = new InlineLabel(AON.MSG.deleteAction());
			removeLabel.setTitle("Borrar asiento");
			removeLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			removeLabel.addStyleName(AON.AON_CSS.aonIconDelete());
			removeLabel.addStyleName(AON.AON_CSS.aonClickableBlock());
			removeLabel.addStyleName(AON.AON_CSS.aonMarginLeft());
			itemPanel.add(removeLabel);
			removeLabel.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ConfirmDialog cd = new ConfirmDialog();
					cd.confirm("Continuar?", "Borrar", new ConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
						}
						
						@Override
						public void onAccept() {
							ACCOUNT_ENTRY_SERVICE.deleteAccountEntry(domainName, item.getDomain(), EntriesRemover.this.user, item.getEntryId(), new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									openFootPanelIfNeeded();
									showErrorPanel(caught.getMessage());
								}

								@Override
								public void onSuccess(Void result) {
									run();
								}
								
							});
						}
					});
				}
			});
			
			InlineLabel msgLabel = new InlineLabel(item.getMessage());
			msgLabel.setStyleName(AON.AON_CSS.aonFixedFont());
			itemPanel.add(msgLabel);

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
			.setActivity(activityId)
			.setAccount(account.getId())
			.setBalancingAccount(account.getId())
			.setDebit(debit.getValue())
			.setCredit(credit.getValue())
			.setConcept(concept.getValue())
			.setDocument(document.getValue())
			.setComments(comments.getValue())
			.setSecurityLevel(confidential!=null?SecurityLevel.safeValueOf(confidential.getSelectedIndex()):SecurityLevel.OFFICIAL)
			.setOrder(order.getSelectedIndex());
	}
}
