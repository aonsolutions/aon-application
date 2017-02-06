package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model184 extends MainEntryPoint {

	static FiscalServiceAsync fiscalService;
	
	interface Model184Binder extends UiBinder<Widget, Model184> {
	}

	private static final Model184Binder MODEL_184_BINDER = GWT
			.create(Model184Binder.class);

	private Mod184 currentMod184;

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	SimplePanel headerPanel;
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	Panel listPanel;
	@UiField
	DockLayoutPanel formPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	Panel formContainer;
	
	@UiField
	TabLayoutPanel tabPanel;

	@UiField(provided = true)
	Model184Table table;

	private int domain;
	private int enterprise;

	@UiField
	SimpleLayoutPanel incomesPanel;
	@UiField
	SimpleLayoutPanel partnersPanel;

//	@UiField
//	Model184Income2014 incomesPanel;
//	@UiField
//	Model184Partner2014 partnersPanel;

	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
	@UiField
	Button newButton;
	@UiField
	Button cancelButton;
	@UiField
	Button generateFileButton;
	@UiField
	Button printButton;

	@UiField
	IntegerBox year;
	@UiField
	AdministrationListBox administration;
	@UiField
	CheckBox replacement;
	@UiField
	CheckBox confidential;
	@UiField
	EnterpriseSuggestBox enterpriseSuggest;
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
	DoubleBox residentPercent;
	@UiField
	CheckBox taxIS;
	@UiField
	DoubleBox netSalesAmount;
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
		fiscalService = new FiscalServiceAsyncDecorator(mod184ServiceRaw);

		table = new Model184Table(new Mod184SelectionHandler());

		Widget ui = MODEL_184_BINDER.createAndBindUi(this);
		
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
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		/*		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int tabIdx = event.getSelectedItem();
				if (tabIdx == 1) {
					incomesPanel.setMod184(currentMod184);
				}
				if (tabIdx == 2) {
					partnersPanel.setMod184(currentMod184);
				}
			}
			
		});
		*/

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
	
	protected void paintHeaderTable() {
		headerPanel.clear();
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(getAdministrationImage());
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		headerTable.setWidget(0, 1, new Label(AON.MSG.fiscalModelDescriptionlong(FiscalModelType.M184)));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, getAdministrationBG());
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		headerTable.setWidget(0, 2, new Label(FiscalModelType.M184.getName()));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, getAdministrationBG());
		
		headerTable.setWidget(1, 0, new Label(""+currentMod184.getYear()));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, getAdministrationBG());
		
		headerPanel.setWidget(headerTable);
	}
	
	public String getAdministrationBG() {
		int admon = (currentMod184 == null 
				?Administration.COMMON_TERRITORY.ordinal()
				:currentMod184.getAdministration());
		if (admon == Administration.ALAVA.ordinal()) {
			return AON.AON_CSS.aonFiscalArabaBg();
		} else if (admon == Administration.BIZKAIA.ordinal()) {
			return AON.AON_CSS.aonFiscalBizkaiaBg();
		} else if (admon == Administration.GIPUZKOA.ordinal()) {
			return AON.AON_CSS.aonFiscalGipuzkoaBg();
		} else if (admon == Administration.NAVARRA.ordinal()) {
			return AON.AON_CSS.aonFiscalNavarraBg();
		} else {
			return AON.AON_CSS.aonFiscalAeatBg();
		}
	}
	public String getAdministrationImage() {
		int adm = (currentMod184 == null 
				?Administration.COMMON_TERRITORY.ordinal()
				:currentMod184.getAdministration());
		if (adm == Administration.ALAVA.ordinal()) {
			return AON.AON_CSS.aonArabaHeaderImage();
		} else if (adm == Administration.BIZKAIA.ordinal()) {
			return AON.AON_CSS.aonBizkaiaHeaderImage();
		} else if (adm == Administration.GIPUZKOA.ordinal()) {
			return AON.AON_CSS.aonGipuzkoaHeaderImage();
		} else if (adm == Administration.NAVARRA.ordinal()) {
			return AON.AON_CSS.aonNavarraHeaderImage();
		} else {
			return AON.AON_CSS.aonAeatHeaderImage();
		}
	}

	
	class Mod184SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod184 sel = table.getSelected();
			fiscalService.getMod184(getCurrentDomainName(), getCurrentDomain(),
					sel.getId(), new AsyncCallback<Mod184>() {
						@Override
						public void onSuccess(Mod184 selected) {
							if (selected == null) {
								DialogMessages.alertErrorWidget(AON.MSG.unableToFindDeclaration());
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
									.unableToReadDeclaration(caught.getMessage()));
						}
					});
		}
	}

	private void select(Mod184 selected) {
		currentMod184 = selected;
		year.setValue(currentMod184.getYear());
		administration.setSelectedIndex(currentMod184.getAdministration());
		replacement.setValue(currentMod184.isReplacement());
		confidential.setValue(currentMod184.isConfidential());
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

		incomesPanel.setVisible(currentMod184.getId() != null);
		partnersPanel.setVisible(currentMod184.getId() != null);
		tabPanel.setVisible(currentMod184.getId() != null);
		replacementPanel.setVisible(currentMod184.isReplacement());
		showDetail(currentMod184);
		paintHeaderTable();		
	}

	private void showDetail(Mod184 currentMod1842) {
		if ( currentMod184.getYear() == 2016) {
			Model184Income2016 income = new Model184Income2016();
			income.setMod184(currentMod184);	
			incomesPanel.setWidget(income);
			Model184Partner2016 partner = new Model184Partner2016();
			partner.setMod184(currentMod184);	
			partnersPanel.setWidget(partner);
		} else if ( currentMod184.getYear() == 2015) {
			Model184Income2015 income = new Model184Income2015();
			income.setMod184(currentMod184);	
			incomesPanel.setWidget(income);
			Model184Partner2015 partner = new Model184Partner2015();
			partner.setMod184(currentMod184);	
			partnersPanel.setWidget(partner);
		} else {
			Model184Income2014 income = new Model184Income2014();
			income.setMod184(currentMod184);	
			incomesPanel.setWidget(income);
			Model184Partner2014 partner = new Model184Partner2014();
			partner.setMod184(currentMod184);	
			partnersPanel.setWidget(partner);
		}
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod184s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<LinkedList<Mod184>>() {
					@Override
					public void onSuccess(LinkedList<Mod184> result) {
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
								.unableToReadDeclaration(caught.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		if (year.getValue() == 0) {
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
		fiscalService.saveMod184(getCurrentDomainName(), getCurrentDomain(),
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
						showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(AON.MSG.confirmDeclarationDeleteAction())) {
			fiscalService.deleteMod184(getCurrentDomainName(),
					getCurrentDomain(), this.currentMod184, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							select(new Mod184());
							table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
						}

						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(AON.MSG
									.unableToDeleteDeclaration(caught.getMessage()));
						}
					});
		}
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		cleanErrorMessage();
		fiscalService.initializeMod184(getCurrentDomainName(),getCurrentDomain(), 2016,
				new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 m184) {
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
		incomesPanel.remove(incomesPanel.getWidget());
		partnersPanel.remove(partnersPanel.getWidget());
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
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
		currentMod184.setYear(year.getValue());
		currentMod184.setDomain(domain);
		currentMod184.setEnterprise(enterprise);
		currentMod184.setAdministration((byte)administration.getSelectedIndex());
		currentMod184.setReplacement(replacement.getValue());
		currentMod184.setConfidential(confidential.getValue());
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
		currentMod184.setResidentPercent(residentPercent.getValue());
		currentMod184.setTaxIS(taxIS.getValue());
		currentMod184.setNetSalesAmount(netSalesAmount.getValue());
		currentMod184.setLrDocument(lrDocument.getValue());
		currentMod184.setLrName(lrName.getValue());
		
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
	void onFootMaximize(MaximizeEvent event) {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		splitLayoutPanel.animate(500);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
		splitLayoutPanel.animate(500);
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
