package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
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
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model190 extends MainEntryPoint {

	interface Model190Binder extends UiBinder<Widget, Model190> {
	}

	static interface IModel190Detail extends HasVisibility {
		public void populatePerceptor(Mod190Detail perceptor);

		public void setMod190(Mod190 currentMod190);
	}

	private static final Model190Binder MODEL_190_BINDER = GWT
			.create(Model190Binder.class);

	private Mod190 currentMod190;
	private FiscalServiceAsync mod190Service;
	
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

	@UiField(provided = true)
	Model190Table table;

	private int domain;
	private int enterprise;

	@UiField
	SimpleLayoutPanel perceptorPanel;

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
	Button printMod190Button;
	
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
	FlowPanel replacementPanel;

	FormPanel diskForm;
	Hidden mod190Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync mod190ServiceRaw = GWT.create(FiscalService.class);
		mod190Service = new FiscalServiceAsyncDecorator(mod190ServiceRaw);
		
		table = new Model190Table(new Mod190SelectionHandler());

		Widget ui = MODEL_190_BINDER.createAndBindUi(this);

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		mod190Hidden = new Hidden("mod190");
		formFlowPanel.add(mod190Hidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;
	
	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	class Mod190SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod190 sel = table.getSelected();
			mod190Service.getMod190(getCurrentDomainName(), getCurrentDomain()
					,sel.getId(), new AsyncCallback<Mod190>() {
				@Override
				public void onSuccess(Mod190 selected) {
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
					showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
				}
			});
		}
	}

	private void select(Mod190 selected) {
		currentMod190 = selected;
		year.setValue( currentMod190.getYear());
		administration.setSelectedIndex(currentMod190.getAdministration());
		replacement.setValue(currentMod190.isReplacement());
		confidential.setValue(currentMod190.isConfidential());
		enterpriseSuggest.setValue(currentMod190.getDocument(), currentMod190.getName());
		contactPhone.setValue(currentMod190.getContactPhone());
		contactPerson.setValue(currentMod190.getContactPerson());
		receipt.setValue(currentMod190.getReceipt());
		replacedReceipt.setValue(currentMod190.getReplacedReceipt());
		domain = currentMod190.getDomain();
		enterprise = currentMod190.getEnterprise();
		replacementPanel.setVisible(currentMod190.isReplacement());
		perceptorPanel.setVisible(currentMod190.getId() != null);
		// Toolbar states
		deleteButton.setVisible(currentMod190.getId() != null);
		newButton.setVisible(currentMod190.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(currentMod190.getId() != null);
		printButton.setVisible(currentMod190.getId() != null);
		printMod190Button.setVisible(currentMod190.getId() != null);
		showDetail(currentMod190);
		paintHeaderTable();		
	}
	
	private void showDetail(Mod190 currentMod190) {
		if ( currentMod190.getYear() == 2014) {
			Model190Detail2014 detail = new Model190Detail2014();
			detail.setMod190(currentMod190);	
			perceptorPanel.setWidget(detail);
		} else if ( currentMod190.getYear() == 2015) { 
			Model190Detail2015 detail = new Model190Detail2015();
			detail.setMod190(currentMod190);	
			perceptorPanel.setWidget(detail);
		} else {
			if (currentMod190.getAdministration() == Administration.BIZKAIA.ordinal()) {
				Model190Detail2016Bizkaia detail = new Model190Detail2016Bizkaia();
				detail.setMod190(currentMod190);	
				perceptorPanel.setWidget(detail);
			} else {
				Model190Detail2016 detail = new Model190Detail2016();
				detail.setMod190(currentMod190);	
				perceptorPanel.setWidget(detail);
			}
		}
		printButton.setVisible(currentMod190.getAdministration() == Administration.COMMON_TERRITORY.ordinal() );
		printMod190Button.setVisible(currentMod190.getAdministration() == Administration.COMMON_TERRITORY.ordinal() );
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		mod190Service.getMod190s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<LinkedList<Mod190>>() {
					@Override
					public void onSuccess(LinkedList<Mod190> result) {
						if (result == null || result.size() == 0) {
							onNewButtonClick(null);
						} else if ( result.size() == 1) {
							select(result.get(0));
							int i = deckPanel.getWidgetIndex(formPanel);
							deckPanel.showWidget(i);
						} else {
							int i = deckPanel.getWidgetIndex(listPanel);
							table.setRowData(result);
							deckPanel.showWidget(i);
							cancelButton.setVisible(false);
							saveButton.setVisible(false);
							deleteButton.setVisible(false);
							newButton.setVisible(true);
							generateFileButton.setVisible(false);							
							printButton.setVisible(false);
							printMod190Button.setVisible(false);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
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

		populateMod190();
		mod190Service.saveMod190(getCurrentDomainName(), getCurrentDomain()
				,this.currentMod190, new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 result) {
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
		deleteButton.setEnabled(false);
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new ConfirmDialogCallback() {

			@Override
			public void onAccept() {
				mod190Service.deleteMod190(getCurrentDomainName(), getCurrentDomain()
						,currentMod190, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						select(new Mod190());
						table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
						deleteButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						deleteButton.setEnabled(true);
						showErrorMessage(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
					}
				});
			}

			@Override
			public void onCancel() {
				deleteButton.setEnabled(true);
			}
		});
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		cleanErrorMessage();
		mod190Service.initializeMod190(getCurrentDomainName(), getCurrentDomain(),2016 ,
				new AsyncCallback<Mod190>() {
			@Override
			public void onSuccess(Mod190 m190) {
				select(m190);
				int i = deckPanel.getWidgetIndex(formPanel);
				deckPanel.showWidget(i);
				showDetail(m190);
			}

			@Override
			public void onFailure(Throwable caught) {
				DialogMessages.alertErrorWidget(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
			}
		});
	}

	@UiHandler("year")
	void onYearChanged(ChangeEvent event) {
		currentMod190.setYear(year.getValue());
		paintHeaderTable();	
	}
	@UiHandler("administration")
	void onAdministrationChanged(ChangeEvent event) {
		currentMod190.setAdministration((byte) administration.getSelectedIndex());
		printButton.setVisible(currentMod190.getAdministration() == Administration.COMMON_TERRITORY.ordinal() );
		printMod190Button.setVisible(currentMod190.getAdministration() == Administration.COMMON_TERRITORY.ordinal() );
		paintHeaderTable();	
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		cleanErrorMessage();
		perceptorPanel.remove(perceptorPanel.getWidget());
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> event) {
		domain = enterpriseSuggest.getDomainId();
		currentMod190.setDomain(domain);
		enterprise = enterpriseSuggest.getEnterpriseId();
		currentMod190.setEnterprise(enterprise);
		currentMod190.setName(enterpriseSuggest.getName().getValue());
		year.selectAll();
		year.setFocus(true);
	}

	private void populateMod190() {
		currentMod190.setYear(year.getValue());
		currentMod190.setDomain(domain);
		currentMod190.setEnterprise(enterprise);
		currentMod190.setAdministration((byte) administration.getSelectedIndex());
		currentMod190.setReplacement(replacement.getValue());
		currentMod190.setConfidential(confidential.getValue());
		currentMod190.setDocument(enterpriseSuggest.getValue());
		currentMod190.setName(enterpriseSuggest.getName().getValue());
		currentMod190.setContactPhone(contactPhone.getValue());
		currentMod190.setContactPerson(contactPerson.getValue());
		currentMod190.setReceipt(receipt.getValue());
		currentMod190.setReplacedReceipt(replacedReceipt.getValue());
	}


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
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model190File");
		mod190Hidden.setValue( String.valueOf(currentMod190.getId()) );
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
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model190Print");
		mod190Hidden.setValue( String.valueOf(currentMod190.getId()) );
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
	
	@UiHandler("printMod190Button")
	public void on10TGeneration(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model190CertificatePrint");
		mod190Hidden.setValue( String.valueOf(currentMod190.getId()) );
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
		
	}
	
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
		
		headerTable.setWidget(0, 1, new Label(AON.MSG.fiscalModelDescriptionlong(FiscalModelType.M190)));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, getAdministrationBG());
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		headerTable.setWidget(0, 2, new Label(FiscalModelType.M190.getName()));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, getAdministrationBG());
		
		headerTable.setWidget(1, 0, new Label(""+currentMod190.getYear()));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, getAdministrationBG());
		
		headerPanel.setWidget(headerTable);
	}
	public String getAdministrationBG() {
		int admon = (currentMod190 == null 
				?Administration.COMMON_TERRITORY.ordinal()
				:currentMod190.getAdministration());
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
		int adm = (currentMod190 == null 
				?Administration.COMMON_TERRITORY.ordinal()
				:currentMod190.getAdministration());
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

}
