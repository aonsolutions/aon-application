package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonCellList;
import com.esferalia.aon.gwt.common.client.css.AonDataGrid;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.ShowMorePagerPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Income2014.IIncomeCallBack;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Partner2014.IPartnerCallBack;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class Model184 extends MainEntryPoint {

	public static final ProvidesKey<Mod184Income> MOD184_INCOME_PROVIDES_KEY = new ProvidesKey<Mod184Income>() {
		@Override
		public Object getKey(Mod184Income mod184Income) {
			return mod184Income == null ? null : mod184Income.getId();
		}
	};

	public static final ProvidesKey<Mod184Partner> MOD184_PARTNER_PROVIDES_KEY = new ProvidesKey<Mod184Partner>() {
		@Override
		public Object getKey(Mod184Partner mod184Partner) {
			return mod184Partner == null ? null : mod184Partner.getId();
		}
	};

	static interface IModel184Detail extends HasVisibility {
		public void populatePerceptor(Mod184Income income);
	}

	static FiscalServiceAsync mod184Service;
	
	final static DataGrid.Resources DATA_GRID_STYLE = GWT.create(AonDataGrid.class);
	
	interface Model184Binder extends UiBinder<Widget, Model184> {
	}

	private static final Model184Binder MODEL_184_BINDER = GWT
			.create(Model184Binder.class);

	private Mod184 currentMod184;

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	Panel listPanel;
	@UiField
	TabLayoutPanel tabPanel;
	@UiField
	DockLayoutPanel formPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	Panel formContainer;

	@UiField(provided = true)
	Model184Table table;

	private Mod184IncomeDataProvider incomesDataProvider;
	private CellList<Mod184Income> incomesList;
	private SingleSelectionModel<Mod184Income> incomesModel;

	private Mod184PartnerDataProvider partnersDataProvider;
	private CellList<Mod184Partner> partnersList;
	private SingleSelectionModel<Mod184Partner> partnersModel;

	private int domain;
	private int enterprise;

	@UiField
	ShowMorePagerPanel incomesPagerPanel;
	@UiField
	Model184Income2014 incomesPanel;

	@UiField
	ShowMorePagerPanel partnersPagerPanel;
	@UiField
	Model184Partner2014 partnersPanel;

	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
	@UiField
	Button newButton;
	@UiField
	Button cancelButton;
	@UiField
	Button newIncomeButton;
	@UiField
	Button generateFileButton;
	@UiField
	Button printButton;

	@UiField
	TextBox year;
	@UiField
	AdministrationListBox administration;
	@UiField
	CheckBox replacement;
	@UiField
	CheckBox confidential;
	@UiField
	EnterpriseSuggestBox enterpriseSuggest;
	@UiField
	TextArea comments;
	@UiField
	TextBox contactPhone;
	@UiField
	TextBox contactPerson;
	@UiField
	TextBox receipt;
	@UiField
	TextBox replacedReceipt;

	@UiField
	ListBox entityType;
	@UiField
	ListBox mainActivity;
	@UiField
	ListBox foreignEntityType;
	@UiField
	ListBox foreignObject;
	@UiField
	CountryListBox country;
	@UiField
	DoubleTextBox residentPercent;
	@UiField
	CheckBox taxIS;
	@UiField
	DoubleTextBox netSalesAmount;
	@UiField
	DocumentTextBox lrDocument;
	@UiField
	TextBox lrName;
	
	@UiField
	FlowPanel replacementPanel;

	FormPanel diskForm;
	Hidden mod184Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync mod184ServiceRaw = GWT.create(FiscalService.class);
		mod184Service = new FiscalServiceAsyncDecorator(mod184ServiceRaw);

		table = new Model184Table(new Mod184SelectionHandler());

		Mod184IncomeCell mod184IncomeCell = new Mod184IncomeCell();
		CellList.Resources cellListStyle = GWT.create(AonCellList.class);
		incomesList = new CellList<Mod184Income>(mod184IncomeCell,cellListStyle, MOD184_INCOME_PROVIDES_KEY);
		incomesList.setStylePrimaryName(DATA_GRID_STYLE.dataGridStyle().dataGridWidget());
		incomesList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		incomesList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		incomesModel = new SingleSelectionModel<Mod184Income>(MOD184_INCOME_PROVIDES_KEY);
		incomesModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				final Mod184Income selected = incomesModel.getSelectedObject();
				incomesPanel.setIncome(selected);
			}
		});
		incomesList.setSelectionModel(incomesModel);
		incomesList.setEmptyListWidget(new HTML(AON.MSG.noData()));
		incomesDataProvider = new Mod184IncomeDataProvider(MOD184_INCOME_PROVIDES_KEY);
		incomesDataProvider.addDataDisplay(incomesList);
		incomesList.setVisible(true);

		Mod184PartnerCell mod184PartnerCell = new Mod184PartnerCell();
		partnersList = new CellList<Mod184Partner>(mod184PartnerCell,cellListStyle, MOD184_PARTNER_PROVIDES_KEY);
		partnersList.setStylePrimaryName(DATA_GRID_STYLE.dataGridStyle().dataGridWidget());
		partnersList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		partnersList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		partnersModel = new SingleSelectionModel<Mod184Partner>(MOD184_PARTNER_PROVIDES_KEY);
		partnersModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				final Mod184Partner selected = partnersModel.getSelectedObject();
				partnersPanel.setPartner(selected);
			}
		});
		partnersList.setSelectionModel(partnersModel);
		partnersList.setEmptyListWidget(new HTML(AON.MSG.noData()));
		partnersDataProvider = new Mod184PartnerDataProvider(MOD184_PARTNER_PROVIDES_KEY);
		partnersDataProvider.addDataDisplay(partnersList);
		partnersList.setVisible(true);

		Widget ui = MODEL_184_BINDER.createAndBindUi(this);
		incomesPanel.setCallback(new IIncomeCallBack() {
			@Override
			public void redrawList(Mod184Income income) {
				incomesList.redraw();
			}
		});

		partnersPanel.setCallback(new IPartnerCallBack() {
			@Override
			public void redrawList(Mod184Partner partner) {
				partnersList.redraw();
			}
		});
		
		incomesPagerPanel.setDisplay(incomesList);
		incomesPagerPanel.setIncrementSize(0);

		partnersPagerPanel.setDisplay(partnersList);
		partnersPagerPanel.setIncrementSize(0);

		tabPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		mod184Hidden = new Hidden("mod184");
		formFlowPanel.add(mod184Hidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		
		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int tabIdx = event.getSelectedItem();
				if (tabIdx == 1) {
					incomesList.redraw();
				}
				if (tabIdx == 2) {
					partnersList.redraw();
				}
			}
			
		});

		entityType.addItem(" - ","");
		entityType.addItem("1 - Sociedad civil.","1");
		entityType.addItem("2 - Comunidad de bienes.","2");
		entityType.addItem("3 - Herencia yacente.","3");
		entityType.addItem("4 - Comunidad de propietarios.","4");
		entityType.addItem("5 - Otros","5");
		entityType.setWidth("200px");
		
		mainActivity.addItem(" - ","");
		mainActivity.addItem("1 - Actividad empresarial.","1");
		mainActivity.addItem("2 - Actividad profesional.","2");
		mainActivity.addItem("3 - Tenencia y administraci\u00F3n de bienes inmuebles.","3");
		mainActivity.addItem("4 - Tenencia y administraci\u00F3n de valores o activos financieros.","4");
		mainActivity.addItem("5 - Otras.","5");
		mainActivity.setWidth("250px");
		
		foreignEntityType.addItem(" - ","");
		foreignEntityType.addItem("1- Corporaci\u00F3n, asociaci\u00F3n o ente con personalidad jur\u00EDdica propia.","1");
		foreignEntityType.addItem("2- Corporaci\u00F3n o ente independiente pero sin personalidad jur\u00EDdica propia.","2");
		foreignEntityType.addItem("3- Conjunto unitario de bienes pertenecientes a dos o m\u00E1s personas en com\u00FAn sin personalidad jur\u00EDdica propia.","3");
		foreignEntityType.addItem("4- Otras","4");
		foreignEntityType.setWidth("200px");

		foreignObject.addItem(" - ","");
		foreignObject.addItem("A - Actividad nat. empresarial.","A");
		foreignObject.addItem("B - Actividad nat. profesional.","B");
		foreignObject.setWidth("200px");
		
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=6889
		deckPanel.onResize();
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	class Mod184SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod184 sel = table.getSelected();
			mod184Service.getMod184(getCurrentDomainName(), getCurrentDomain(),
					sel.getId(), new AsyncCallback<Mod184>() {
						@Override
						public void onSuccess(Mod184 selected) {
							if (selected == null) {
								DialogMessages.alertErrorWidget(AON.MSG.unableToFindMod184());
							} else {
								select(selected);
								int i = deckPanel.getWidgetIndex(formPanel);
								deckPanel.showWidget(i);
								year.selectAll();
								year.setFocus(true);
								cleanErrorMessage();
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(AON.MSG
									.unableToReadMod184(caught.getMessage()));
						}
					});
		}
	}

	static class Mod184IncomeCell extends AbstractCell<Mod184Income> {
		@Override
		public void render(Cell.Context context, Mod184Income value,
				SafeHtmlBuilder sb) {
			if (value == null) {
				return;
			}
			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("<div style='");
			}
			if (value.isDirty()) {
				sb.appendHtmlConstant("font-style: italic; font-weight:bold;");
			}
			if (value.isDeleted()) {
				sb.appendHtmlConstant("text-decoration:line-through");
			}
			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("'>");
			}
			String newLabel = AON.MSG.newIncome() + " (" + (value.getId() * (-1)) + ")";
			sb.appendEscaped(AonStringUtils.isBlank(value.getKey()) 
					? newLabel
					: value.getKey() + (AonStringUtils.isBlank(value.getSubKey())?"":("-" + value.getSubKey())));

			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("</div>");
			}
		}
	}

	static class Mod184PartnerCell extends AbstractCell<Mod184Partner> {
		@Override
		public void render(Cell.Context context, Mod184Partner value,
				SafeHtmlBuilder sb) {
			if (value == null) {
				return;
			}
			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("<div style='");
			}
			if (value.isDirty()) {
				sb.appendHtmlConstant("font-style: italic; font-weight:bold;");
			}
			if (value.isDeleted()) {
				sb.appendHtmlConstant("text-decoration:line-through");
			}
			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("'>");
			}
			String newLabel = AON.MSG.newIncome() + " (" + (value.getId() * (-1)) + ")";
			sb.appendEscaped(AonStringUtils.isBlank(value.getName()) ? newLabel: value.getName());
			
			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("</div>");
			}
		}
	}

	private void select(Mod184 selected) {
		currentMod184 = selected;
		year.setValue(Integer.toString(currentMod184.getYear()));
		administration.setSelectedIndex(currentMod184.getAdministration());
		replacement.setValue(currentMod184.isReplacement());
		confidential.setValue(currentMod184.isConfidential());
		comments.setValue(currentMod184.getComments());
		enterpriseSuggest.setValue(currentMod184.getDocument(), currentMod184.getName());
		contactPhone.setValue(currentMod184.getContactPhone());
		contactPerson.setValue(currentMod184.getContactPerson());
		receipt.setValue(currentMod184.getReceipt());
		replacedReceipt.setValue(currentMod184.getReplacedReceipt());
		domain = currentMod184.getDomain();
		enterprise = currentMod184.getEnterprise();
		
		entityType.setSelectedIndex(AonStringUtils.isBlank(currentMod184.getEntityType())
				?0:Integer.parseInt(currentMod184.getEntityType()) );
		mainActivity.setSelectedIndex(AonStringUtils.isBlank(currentMod184.getMainActivity())
				?0:Integer.parseInt(currentMod184.getMainActivity()) );
		foreignEntityType.setSelectedIndex(AonStringUtils.isBlank(currentMod184.getForeignEntityType())
				?0:Integer.parseInt(currentMod184.getForeignEntityType()) );
		foreignObject.setSelectedIndex(AonStringUtils.isBlank(currentMod184.getForeignObject())
				?0:"A".equals(currentMod184.getForeignObject())?1:2);
		country.setSelectedIndex( 
				AonStringUtils.isBlank( currentMod184.getCountry() )
				? 0 
				: Country.valueOf(currentMod184.getCountry()).ordinal() + 1 );
		residentPercent.setValue(currentMod184.getResidentPercent());
		taxIS.setValue(currentMod184.isTaxIS());
		netSalesAmount.setValue(currentMod184.getNetSalesAmount());
		lrDocument.setValue(currentMod184.getLrDocument());
		lrName.setValue(currentMod184.getLrName());
		
		// Toolbar states
		deleteButton.setVisible(currentMod184.getId() != null);
		newButton.setVisible(currentMod184.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(currentMod184.getId() != null);
		printButton.setVisible(currentMod184.getId() != null);

		incomesPagerPanel.setVisible(currentMod184.getId() != null);
		partnersPagerPanel.setVisible(currentMod184.getId() != null);
		
		incomesPanel.setVisible(currentMod184.getId() != null);
		partnersPanel.setVisible(currentMod184.getId() != null);
		
		tabPanel.setVisible(currentMod184.getId() != null);
		
		replacementPanel.setVisible(currentMod184.isReplacement());
		
		incomesList.setVisibleRangeAndClearData(incomesList.getVisibleRange(),true);
		partnersList.setVisibleRangeAndClearData(partnersList.getVisibleRange(),true);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		mod184Service.getMod184s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<ArrayList<Mod184>>() {
					@Override
					public void onSuccess(ArrayList<Mod184> result) {
						if (result == null || result.size() == 0) {
							onNewButtonClick(null);
						} else {
							int i = deckPanel.getWidgetIndex(listPanel);
							table.setRowData(result);
							deckPanel.showWidget(i);
							cancelButton.setVisible(false);
							saveButton.setVisible(false);
							deleteButton.setVisible(false);
							generateFileButton.setVisible(false);
							printButton.setVisible(false);
							newButton.setVisible(true);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(AON.MSG
								.unableToReadMod184(caught.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		if (AonStringUtils.isBlank(year.getValue())) {
			throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
		}

		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();

		populateMod184();
		mod184Service.saveMod184(getCurrentDomainName(), getCurrentDomain(),
				this.currentMod184, new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 result) {
						select(result);
						popup.hide();
						cleanErrorMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						showErrorMessage(AON.MSG.unableToSaveMod184(caught.getMessage()));
					}
				});
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(AON.MSG.confirmDeclarationDeleteAction())) {
			mod184Service.deleteMod184(getCurrentDomainName(),
					getCurrentDomain(), this.currentMod184, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							select(new Mod184());
							table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
						}

						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(AON.MSG
									.unableToDeleteMod184(caught.getMessage()));
						}
					});
		}
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		cleanErrorMessage();
		mod184Service.initializeMod184(getCurrentDomainName(),getCurrentDomain(), 2014,
				new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 m184) {
						incomesList.setVisibleRangeAndClearData(incomesList.getVisibleRange(),true);
						select(m184);
						int i = deckPanel.getWidgetIndex(formPanel);
						deckPanel.showWidget(i);
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(AON.MSG
								.unableToReadFiscalParameters(caught
										.getMessage()));
					}
				});
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		cleanErrorMessage();
		incomesList.setVisibleRangeAndClearData(incomesList.getVisibleRange(),true);
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	@UiHandler("newIncomeButton")
	void onNewIncomeButtonClick(ClickEvent event) {
		int newKey = (currentMod184.getIncomes().size() + 1) * (-1);
		final Mod184Income income = new Mod184Income();
		income.setId(newKey);
		currentMod184.getIncomes().add(income);
		incomesPanel.setIncome(income);
		incomesList.setRowCount(incomesList.getRowCount() + 1);
		incomesList.setPageSize(incomesList.getRowCount());
		selectIncomeInList(currentMod184.getIncomes().size() - 1);
		incomesList.redraw();
	}

	@UiHandler("newPartnerButton")
	void onNewPartnerButtonClick(ClickEvent event) {
		int newKey = (currentMod184.getPartners().size() + 1) * (-1);
		final Mod184Partner partner = new Mod184Partner();
		partner.setId(newKey);
		currentMod184.getPartners().add(partner);
		partnersPanel.setPartner(partner);
		partnersList.setRowCount(partnersList.getRowCount() + 1);
		partnersList.setPageSize(partnersList.getRowCount());
		selectPartnerInList(currentMod184.getPartners().size() - 1);
		partnersList.redraw();
	}

	private void selectIncomeInList(int i) {
		incomesModel.setSelected(currentMod184.getIncomes().get(i),true);
		incomesList.getRowElement(i).scrollIntoView();
		incomesPagerPanel.scrollToLeft();
	}
	private void selectPartnerInList(int i) {
		partnersModel.setSelected(currentMod184.getPartners().get(i),true);
		partnersList.getRowElement(i).scrollIntoView();
		partnersPagerPanel.scrollToLeft();
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> event) {
		domain = enterpriseSuggest.getDomainId();
		currentMod184.setDomain(domain);
		enterprise = enterpriseSuggest.getEnterpriseId();
		currentMod184.setEnterprise(enterprise);
		currentMod184.setName(enterpriseSuggest.getName().getValue());
		year.selectAll();
		year.setFocus(true);
	}

	private void populateMod184() {
		try {
			currentMod184.setYear(Integer.parseInt(year.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(AON.MSG.unableToParseYear());
		}
		currentMod184.setDomain(domain);
		currentMod184.setEnterprise(enterprise);
		currentMod184.setAdministration((byte)administration.getSelectedIndex());
		currentMod184.setReplacement(replacement.getValue());
		currentMod184.setConfidential(confidential.getValue());
		currentMod184.setComments(comments.getValue());
		currentMod184.setDocument(enterpriseSuggest.getValue());
		currentMod184.setName(enterpriseSuggest.getName().getValue());
		currentMod184.setContactPhone(contactPhone.getValue());
		currentMod184.setContactPerson(contactPerson.getValue());
		currentMod184.setReceipt(receipt.getValue());
		currentMod184.setReplacedReceipt(replacedReceipt.getValue());
		
		currentMod184.setEntityType(entityType.getValue( entityType.getSelectedIndex() ));
		currentMod184.setMainActivity(mainActivity.getValue( mainActivity.getSelectedIndex() ));
		currentMod184.setForeignEntityType(foreignEntityType.getValue( foreignEntityType.getSelectedIndex() ));
		currentMod184.setForeignObject(foreignObject.getValue( foreignObject.getSelectedIndex() ));
		
		currentMod184.setCountry(country.getValue(country.getSelectedIndex()));
		currentMod184.setResidentPercent(residentPercent.getDoubleValue());
		currentMod184.setTaxIS(taxIS.getValue());
		currentMod184.setNetSalesAmount(netSalesAmount.getDoubleValue());
		currentMod184.setLrDocument(lrDocument.getValue());
		currentMod184.setLrName(lrName.getValue());
		
	}

	class Mod184IncomeDataProvider extends AsyncDataProvider<Mod184Income> {

		public Mod184IncomeDataProvider(ProvidesKey<Mod184Income> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod184Income> display) {
			if (currentMod184 != null && currentMod184.getId() != null) {
				if (currentMod184.getIncomes().size() == 0) {
					onNewIncomeButtonClick(null);					
				} else {
					updateRowCount(currentMod184.getIncomes().size(), true);
					updateRowData(0, currentMod184.getIncomes());
					incomesList.setPageSize(currentMod184.getIncomes().size());
					selectIncomeInList(0);
					incomesPanel.setIncome(incomesModel.getSelectedObject());
				}
			}
		}
	}

	class Mod184PartnerDataProvider extends AsyncDataProvider<Mod184Partner> {

		public Mod184PartnerDataProvider(ProvidesKey<Mod184Partner> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod184Partner> display) {
			if (currentMod184 != null && currentMod184.getId() != null) {
				if (currentMod184.getPartners().size() == 0) {
					onNewPartnerButtonClick(null);					
				} else {
					updateRowCount(currentMod184.getPartners().size(), true);
					updateRowData(0, currentMod184.getPartners());
					partnersList.setPageSize(currentMod184.getPartners().size());
					selectPartnerInList(0);
					partnersPanel.setPartner(partnersModel.getSelectedObject());
				}
			}
		}
	}

	// -------------------------------------------------------------- UiHandler

	@UiHandler("replacement")
	void onChangeReplacement(ClickEvent event) {
		replacementPanel.setVisible(replacement.getValue());
	}
	
	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos de la declaraci\u00F3n, para su \n"
				+ "presentaci\u00F3n en Hacienda.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model184File");
		mod184Hidden.setValue(String.valueOf(currentMod184.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la validaci\u00F3n en los servidores de la \n"
				+ "Agencia Tributaria. En el caso de validaci\u00F3n correcta,la Agencia \n"
				+ "Tributaria devolver\u00E1 un documento PDF borrador con la declarai\u00F3n\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "La petici\u00F3n se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model184Print");
		mod184Hidden.setValue(String.valueOf(currentMod184.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}
	
	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
	}

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	
	private void cleanErrorMessage() {
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		showResultsPanel();
		addErrorMessage(msg);
	}

	private void addErrorMessage(String msg) {
		SimplePanel panel = new SimplePanel();
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		panel.add(label);
		resultsPanel.setWidget(panel);
	}

}
