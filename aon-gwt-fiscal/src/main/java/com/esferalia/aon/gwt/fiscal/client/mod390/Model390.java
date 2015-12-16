package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model390 extends MainEntryPoint {

	public static final ProvidesKey<Mod3902014> MOD390_PROVIDES_KEY = new ProvidesKey<Mod3902014>() {
		@Override
		public Object getKey(Mod3902014 mod390) {
			return mod390 == null ? null : mod390.getId();
		}
	};
	
	interface Model390Binder extends UiBinder<Widget, Model390> {
	}

	private static final Model390Binder MODEL_390_BINDER = GWT
			.create(Model390Binder.class);

	private Mod3902014 mod390;
	private FiscalServiceAsync fiscalService;

	private static final Integer DEFAULT_YEAR = 2015;
	

	class Mod390CallBack {
		void calculateAndRefresh() {
			mod390.calculate();
			page7.setValue(mod390);
			page8.setValue(mod390);
			page10.setValue(mod390);
		}
	}

	Mod390CallBack callback = new Mod390CallBack();
	
	@UiField
	Label simplifiedRegime;

	private int domain;
	private int enterprise;

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	SimplePanel headerPanel;
	
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
	DeckLayoutPanel deckPanel;


	@UiField(provided = true)
	Model390Table table;

	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	DeckPanel pagesPanel;

	@UiField
	Panel panel0;
	@UiField
	FocusPanel linkPage0;

	@UiField
	Panel panel3;
	@UiField
	FocusPanel linkPage3;

	@UiField
	Panel panel4;
	@UiField
	FocusPanel linkPage4;

	@UiField
	Panel panel5;
	@UiField
	FocusPanel linkPage5;

	@UiField
	Panel panel6;
	@UiField
	FocusPanel linkPage6;

	@UiField
	Panel panel7;
	@UiField
	FocusPanel linkPage7;

	@UiField
	Panel panel8;
	@UiField
	FocusPanel linkPage8;

	@UiField
	Panel panel9;
	@UiField
	FocusPanel linkPage9;

	@UiField
	Panel panel10;
	@UiField
	FocusPanel linkPage10;

	@UiField
	Panel panel11;
	@UiField
	FocusPanel linkPage11;

	@UiField
	Panel panel12;
	@UiField
	FocusPanel linkPage12;

	@UiField
	Panel panel13;
	@UiField
	FocusPanel linkPage13;

	@UiField
	Panel listPanel;
	@UiField
	DockLayoutPanel formPanel;
	@UiField
	Panel formContainer;


	@UiField
	IntegerBox year;
	@UiField
	EnterpriseSuggestBox enterpriseSuggest;

	FormPanel diskForm;
	Hidden mod390Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	@UiField
	Page00 page0;
	@UiField
	Page03 page3;
	@UiField
	Page04 page4;
	@UiField
	Page05 page5;
	@UiField
	Page06 page6;
	@UiField
	Page07 page7;
	@UiField
	Page08 page8;
	@UiField
	Page09 page9;
	@UiField
	Page10 page10;
	@UiField
	Page11 page11;
	@UiField
	Page12 page12;
	@UiField
	Page13 page13;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		table = new Model390Table(new Mod390SelectionHandler());

		// Create the UI defined in Employee.ui.xml.
		Widget ui = MODEL_390_BINDER.createAndBindUi(this);

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		
		domain = getCurrentDomain();
		year.setValue( DEFAULT_YEAR  );
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		mod390Hidden = new Hidden("mod390");
		formFlowPanel.add(mod390Hidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		
		page0.setCallback( callback );
		page3.setCallback( callback );
		page4.setCallback( callback );
		page5.setCallback( callback );
		page6.setCallback( callback );
		page7.setCallback( callback );
		page8.setCallback( callback );
		page9.setCallback( callback );
		page10.setCallback( callback );
		page11.setCallback( callback );
		page12.setCallback( callback );
		page13.setCallback( callback );

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
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

	class Mod390SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod3902014 sel = table.getSelected();
			fiscalService.getMod390(getCurrentDomainName(), getCurrentDomain(),
					sel.getId(), new AsyncCallback<Mod3902014>() {
				@Override
				public void onSuccess(Mod3902014 selected) {
					if (selected == null) {
						DialogMessages.alertErrorWidget(AON.MSG.unableToFindMod190());
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
					DialogMessages.alertErrorWidget(AON.MSG.unableToReadMod190(caught.getMessage()));
				}
			});
		}
	}

	private void refreshPages(Mod3902014 m390) {
		page0.setValue(m390);
		page3.setValue(m390);
		page4.setValue(m390);
		page5.setValue(m390);
		page6.setValue(m390);
		page7.setValue(m390);
		page8.setValue(m390);
		page9.setValue(m390);
		page10.setValue(m390);
		page11.setValue(m390);
		page12.setValue(m390);
		page13.setValue(m390);
	}
	
	private void select(Mod3902014 m390) {
		mod390 = m390;
		onLinkPage0(null);
		enterprise = m390.getEnterprise();
		domain = m390.getDomain();
		year.setValue(m390.getYear());
		enterpriseSuggest.setValue(m390.getDocument(), m390.getEnterpriseName());
		refreshPages(m390);
		// Toolbar states
		deleteButton.setVisible(mod390.getId() != null);
		newButton.setVisible(mod390.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(mod390.getId() != null);
		printButton.setVisible(mod390.getId() != null);
		simplifiedRegime.setVisible(mod390.isSimplifiedRegime());
		paintHeaderTable();
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod390s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<ArrayList<Mod3902014>>() {
					@Override
					public void onSuccess(ArrayList<Mod3902014> result) {
						if (result == null || result.size() == 0) {
							onNewButtonClick(null);
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
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(AON.MSG.unableToReadMod190(caught.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		try {
			populateMod390();
			validate(this.mod390);
			fiscalService.saveMod390(getCurrentDomainName(),getCurrentDomain(),this.mod390
					, new AsyncCallback<Mod3902014>() {
						@Override
						public void onSuccess(Mod3902014 result) {
							select(result);
							popup.hide();
							cleanErrorMessage();
						}

						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							addErrorMessage(AON.MSG.unableToSaveMod190(caught.getMessage()));
						}
					});
		} catch (IllegalArgumentException e) {
			popup.hide();
			DialogMessages.alertErrorWidget(e.getMessage()).center();
		}
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(AON.MSG.confirmDeleteAction())) {
			fiscalService.deleteMod390(getCurrentDomainName(),getCurrentDomain(),
					this.mod390, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					// select(new Mod390());
					table.setVisibleRangeAndClearData(table.getVisibleRange(),
							true);
				}

				@Override
				public void onFailure(Throwable caught) {
					DialogMessages.alertErrorWidget(AON.MSG.unableToDeleteMod190(caught.getMessage()));
				}
			});
		}
	}
	
	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		cleanErrorMessage();
		int numYear = 0;
		numYear = year.getValue();
		fiscalService.initializeMod390(getCurrentDomainName(),domain, numYear,
				new AsyncCallback<Mod3902014>() {
			@Override
			public void onSuccess(Mod3902014 mod390) {
				select( mod390 );
				int i = deckPanel.getWidgetIndex(formPanel);
				deckPanel.showWidget(i);
				i = pagesPanel.getWidgetIndex(panel0);
				pagesPanel.showWidget(i);
				year.selectAll();
				year.setFocus(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				DialogMessages.alertErrorWidget(AON.MSG.unableToReadFiscalParameters(caught
								.getMessage()));
			}
		});
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		cleanErrorMessage();
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> event) {
		domain = enterpriseSuggest.getDomainId();
		onNewButtonClick(null);
	}

	@UiHandler("year")
	void onChangeYear(ChangeEvent event) {
		if (Window.confirm("El ejercicio ha cambiado, desea recalcular los datos?")) {
			onNewButtonClick(null);
		};
		paintHeaderTable();		
	}

	private void populateMod390() {
		mod390.setYear(year.getValue());
		mod390.setDomain(domain);
		mod390.setEnterprise(enterprise);
		mod390.setDocument(enterpriseSuggest.getValue());
		mod390.setEnterpriseName(enterpriseSuggest.getName().getValue());
		mod390.setAdministration( (byte) Administration.COMMON_TERRITORY.ordinal());
		mod390.setConfidential(false);
		mod390.setComments(null);
		
		paintHeaderTable();

		// Populate Pages
		page0.populate(mod390);
		page3.populate(mod390);
		page4.populate(mod390);
		page5.populate(mod390);
		page6.populate(mod390);
		page7.populate(mod390);
		page8.populate(mod390);
		page9.populate(mod390);
		page10.populate(mod390);
		page11.populate(mod390);
		page12.populate(mod390);
		page13.populate(mod390);

	}

	// -------------------------------------------------------------- UiHandler

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		Window.alert("Se va a proceder a la generaci\u00F3n del fichero.\n"
				+ " Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n"
				+ " El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model390File");
		mod390Hidden.setValue(String.valueOf(mod390.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL()	+ "/aon_gwt_fiscal/Model390Print");
		mod390Hidden.setValue(String.valueOf(mod390.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	private void clearLinks() {
		linkPage0.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage3.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage4.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage5.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage6.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage7.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage8.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage9.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage10.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage11.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage12.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
		linkPage13.removeStyleName(AON.AON_CSS.aonLinkItemSelected());
	}

	@UiHandler("linkPage0")
	void onLinkPage0(ClickEvent event) {
		clearLinks();
		linkPage0.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel0));
	}

	@UiHandler("linkPage3")
	void onLinkPage3(ClickEvent event) {
		clearLinks();
		linkPage3.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel3));
	}

	@UiHandler("linkPage4")
	void onLinkPage4(ClickEvent event) {
		clearLinks();
		linkPage4.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel4));
	}

	@UiHandler("linkPage5")
	void onLinkPage5(ClickEvent event) {
		clearLinks();
		linkPage5.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel5));
	}

	@UiHandler("linkPage6")
	void onLinkPage6(ClickEvent event) {
		clearLinks();
		linkPage6.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel6));
	}

	@UiHandler("linkPage7")
	void onLinkPage7(ClickEvent event) {
		clearLinks();
		linkPage7.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel7));
	}

	@UiHandler("linkPage8")
	void onLinkPage8(ClickEvent event) {
		clearLinks();
		linkPage8.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel8));
	}

	@UiHandler("linkPage9")
	void onLinkPage9(ClickEvent event) {
		clearLinks();
		linkPage9.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel9));
	}

	@UiHandler("linkPage10")
	void onLinkPage10(ClickEvent event) {
		clearLinks();
		linkPage10.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel10));
	}

	@UiHandler("linkPage11")
	void onLinkPage11(ClickEvent event) {
		clearLinks();
		linkPage11.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel11));
	}

	@UiHandler("linkPage12")
	void onLinkPage12(ClickEvent event) {
		clearLinks();
		linkPage12.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel12));
	}

	@UiHandler("linkPage13")
	void onLinkPage13(ClickEvent event) {
		clearLinks();
		linkPage13.addStyleName(AON.AON_CSS.aonLinkItemSelected());
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel13));
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
		label.addStyleName(AON.AON_CSS.aonIconError());
		panel.add(label);
		resultsPanel.setWidget(panel);
	}
	
	private void validate(Mod3902014 m390) {
		if (m390.getYear() != 2014 && m390.getYear() != 2015) {
			throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
		}
		if (!m390.isLegalEntity()) {
			if (AonStringUtils.isEmpty(m390.getDocument())) {
				throw new IllegalArgumentException(AON.MSG.requiredField(" Apart. 0: " + AON.MSG.document()));
			}
			if (!AonDocumentUtil.isValid(m390.getDocument())) {
				throw new IllegalArgumentException("El NIF/DNI no es correcto");
			}
			if (AonStringUtils.isEmpty(m390.getName())) {
				throw new IllegalArgumentException("Para personas f\u00EDsicas, el nombre es obligatorio (Apartado 0)");
			}
			if (AonStringUtils.isEmpty(m390.getFirstSurname())) {
				throw new IllegalArgumentException("Para personas f\u00EDsicas, el primer apellido es obligatorio (Apartado 0)");
			}
//			if (AonStringUtils.isEmpty(m390.getSecondSurname())) {
//				throw new IllegalArgumentException("Para personas f\u00EDsicas, el segundo apellido es obligatorio (Apartado 0)");
//			}
		} else {
			if (AonStringUtils.isEmpty(m390.getName())) {
				throw new IllegalArgumentException("No se ha indicado el nombre del declarante. (Apartado 0)");
			}
		}
		if (m390.getMainActivity() == null
				|| AonStringUtils.isEmpty(m390.getMainActivity().getKey())) {
			throw new IllegalArgumentException(
					"No se ha indicado actividad principal (Apartado 3)");
		}
		if (!m390.isLegalEntity()) {
			if (m390.getAddress() == null) {
				throw new IllegalArgumentException(
						"Indique datos del represante (Apartado 4)");
			}
			if (AonStringUtils.isEmpty(m390.getAddress().getRdocument())) {
				throw new IllegalArgumentException(
						"Para personas f\u00EDsicas, el NIF/DNI del representante es obligatorio. (Apartado 4)");
			}
			if (!AonDocumentUtil.isValid(m390.getAddress().getRdocument())) {
				throw new IllegalArgumentException(
						"El NIF/DNI del representante no es correcto. (Apartado 4)");
			}
		} else {
			if (m390.getLegalRepr1() != null) {
				if (!AonDocumentUtil.isValid(m390.getLegalRepr1().getDocument())) {
					throw new IllegalArgumentException(
							"El NIF del primer representante para personas jurídicas no es correcto. (Apartado 4)");
				}
			}
			if (m390.getLegalRepr2() != null) {
				if (!AonDocumentUtil.isValid(m390.getLegalRepr2().getDocument())) {
					throw new IllegalArgumentException(
							"El NIF del segundo representante para personas jurídicas no es correcto. (Apartado 4)");
				}
			}
			if (m390.getLegalRepr3() != null) {
				if (!AonDocumentUtil.isValid(m390.getLegalRepr3().getDocument())) {
					throw new IllegalArgumentException(
							"El NIF del tercer representante para personas jurídicas no es correcto. (Apartado 4)");
				}
			}
		}
	}

	protected void paintHeaderTable() {
		headerPanel.clear();
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(AON.AON_CSS.aonAeatHeaderImage());
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		headerTable.setWidget(0, 1, new Label(AON.MSG.fiscalModelDescriptionlong(FiscalModelType.M390)));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonFiscalAeatBg());
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		headerTable.setWidget(0, 2, new Label(FiscalModelType.M390.getName()));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonFiscalAeatBg());
		
		headerTable.setWidget(1, 0, new Label(""+mod390.getYear()));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, AON.AON_CSS.aonFiscalAeatBg());
		
		headerPanel.setWidget(headerTable);
	}
}
