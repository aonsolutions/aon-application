package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.IntegerTextBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model390 extends MainEntryPoint {

	public static final ProvidesKey<Mod390> MOD390_PROVIDES_KEY = new ProvidesKey<Mod390>() {
		@Override
		public Object getKey(Mod390 mod390) {
			return mod390 == null ? null : mod390.getId();
		}
	};
	
	interface Model390Binder extends UiBinder<Widget, Model390> {
	}

	private static final Model390Binder MODEL_390_BINDER = GWT
			.create(Model390Binder.class);

	private Mod390 mod390;
	private FiscalServiceAsync fiscalService;
	
	protected final static CommonMessages MSG = GWT.create(CommonMessages.class);
	protected final static AonResources RESOURCES = GWT.create(AonResources.class);
	protected static final FiscalMessages FISCAL_MSG = GWT.create(FiscalMessages.class);
	protected static final NumberFormat FMT = NumberFormat.getFormat( MSG.decimalPattern(), MSG.currencyCode());

	private static final Integer DEFAULT_YEAR = 2014;
	

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

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	DeckLayoutPanel deckPanel;
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

	@UiField(provided = true)
	CellTable<Mod390> table;

	private NoSelectionModel<Mod390> model;

	private int domain;
	private int enterprise;

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
	// @UiField
	// Button printButton;

	@UiField
	IntegerTextBox year;
	@UiField
	EnterpriseSuggestBox enterpriseSuggest;

	FormPanel diskForm;
	Hidden mod390Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	@UiField
	Page0 page0;
	@UiField
	Page3 page3;
	@UiField
	Page4 page4;
	@UiField
	Page5 page5;
	@UiField
	Page6 page6;
	@UiField
	Page7 page7;
	@UiField
	Page8 page8;
	@UiField
	Page9 page9;
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
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);

		table = new CellTable<Mod390>(1, tableStyle, MOD390_PROVIDES_KEY);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addYearColumn();
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();

		model = new NoSelectionModel<Mod390>(MOD390_PROVIDES_KEY);
		model.addSelectionChangeHandler(new Mod390SelectionHandler());
		table.setSelectionModel(model);
		table.setEmptyTableWidget(new HTML(MSG.noData()));

		// Create the UI defined in Employee.ui.xml.
		Widget ui = MODEL_390_BINDER.createAndBindUi(this);

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		
		domain = getCurrentDomain();
		year.setValue( DEFAULT_YEAR );
		
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

	private void addNameColumn() {
		final TextColumn<Mod390> nameColumn = new TextColumn<Mod390>() {
			@Override
			public String getValue(Mod390 mod390) {
				return mod390.getEnterpriseName();
			}
		};
		table.addColumn(nameColumn, MSG.name());
		table.setColumnWidth(nameColumn, 100, Unit.PCT);
	}

	private void addDocumentColumn() {
		final TextColumn<Mod390> documentColumn = new TextColumn<Mod390>() {
			@Override
			public String getValue(Mod390 mod390) {
				return mod390.getDocument();
			}
		};
		table.addColumn(documentColumn, MSG.document());
		table.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	private void addReplacementColumn() {
		Column<Mod390, ImageResource> replacementColumn = new Column<Mod390, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod390 mod390) {
				return mod390.isReplacement() ? RESOURCES.aonIconChecked()
						: RESOURCES.aonIconCheck();
			}
		};
		table.addColumn(replacementColumn, MSG.replacement());
		replacementColumn.setCellStyleNames(RESOURCES.css()
				.aonDataTableIconColumn());
		table.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<Mod390> yearColumn = new TextColumn<Mod390>() {
			@Override
			public String getValue(Mod390 mod390) {
				return Integer.toString(mod390.getYear());
			}
		};
		table.addColumn(yearColumn, MSG.fiscalYear());
		yearColumn.setCellStyleNames(RESOURCES.css().aonTextCenter());
		table.setColumnWidth(yearColumn, 100, Unit.PX);
	}

	private void addSelectorColumn() {
		final Column<Mod390, ImageResource> selectorColumn = new Column<Mod390, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod390 mod390) {
				return RESOURCES.aonIconRowSelector();
			}
		};
		table.addColumn(selectorColumn);
		table.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	class Mod390SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod390 sel = model.getLastSelectedObject();
			fiscalService.getMod390(getCurrentDomainName(), getCurrentDomain(),
					sel.getId(), new AsyncCallback<Mod390>() {
				@Override
				public void onSuccess(Mod390 selected) {
					if (selected == null) {
						DialogMessages.alertErrorWidget(MSG.unableToFindMod190());
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
					DialogMessages.alertErrorWidget(MSG
							.unableToReadMod190(caught.getMessage()));
				}
			});
		}
	}

	private void refreshPages(Mod390 m390) {
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
	
	private void select(Mod390 m390) {
		mod390 = m390;
		onLinkPage0(null);
		enterprise = m390.getEnterprise();
		domain = m390.getDomain();
		year.setValue(Integer.toString(m390.getYear()));
		enterpriseSuggest.setValue(m390.getDocument(), m390.getEnterpriseName());
		refreshPages(m390);
		// Toolbar states
		deleteButton.setVisible(mod390.getId() != null);
		newButton.setVisible(mod390.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(mod390.getId() != null);
		// printButton.setVisible(mod390.getId() != null);
		simplifiedRegime.setVisible(mod390.isSimplifiedRegime());
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod390s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<ArrayList<Mod390>>() {
					@Override
					public void onSuccess(ArrayList<Mod390> result) {
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
							// printButton.setVisible(false);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(MSG.unableToReadMod190(caught.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		try {
			populateMod390();
			validate(this.mod390);
			fiscalService.saveMod390(getCurrentDomainName(),getCurrentDomain(),this.mod390
					, new AsyncCallback<Mod390>() {
						@Override
						public void onSuccess(Mod390 result) {
							select(result);
							popup.hide();
							cleanErrorMessage();
						}

						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							addErrorMessage(MSG.unableToSaveMod190(caught.getMessage()));
						}
					});
		} catch (IllegalArgumentException e) {
			popup.hide();
			DialogMessages.alertErrorWidget(e.getMessage()).center();
		}
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(MSG.confirmDeleteAction())) {
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
					DialogMessages.alertErrorWidget(MSG
							.unableToDeleteMod190(caught.getMessage()));
				}
			});
		}
	}
	
	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		cleanErrorMessage();
		fiscalService.initializeMod390(getCurrentDomainName(),domain, year.getIntValue(),
				new AsyncCallback<Mod390>() {
			@Override
			public void onSuccess(Mod390 mod390) {
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
				DialogMessages.alertErrorWidget(MSG
						.unableToReadFiscalParameters(caught
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
			try {
				mod390.setYear(Integer.parseInt(year.getValue()));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(MSG.unableToParseYear());
			}
			onNewButtonClick(null);
		};
	}

	private void populateMod390() {
		try {
			mod390.setYear(Integer.parseInt(year.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(MSG.unableToParseYear());
		}
		mod390.setDomain(domain);
		mod390.setEnterprise(enterprise);
		mod390.setDocument(enterpriseSuggest.getValue());
		mod390.setEnterpriseName(enterpriseSuggest.getName().getValue());
		mod390.setYear(Integer.parseInt(year.getValue()));
		mod390.setAdministration( (byte) Administration.COMMON_TERRITORY.ordinal());
		mod390.setConfidential(false);
		mod390.setComments(null);

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

	// @UiHandler("printButton")
	// void onPrintButtonClick(ClickEvent event) {
	// diskForm.setAction(GWT.getHostPageBaseURL()
	// + "/aon_gwt_fiscal/Model390Print");
	// mod390Hidden.setValue(String.valueOf(mod390.getId()));
	// diskForm.submit();
	// }

	private void clearLinks() {
		Panel[] panels = new Panel[] { linkPage0, linkPage3,
				linkPage4, linkPage5, linkPage6, linkPage7, linkPage8,
				linkPage9, linkPage10, linkPage11, linkPage12, linkPage13 };
		for (Panel p : panels) {
			p.getElement().getStyle().setBackgroundColor("");
			p.getElement().getStyle().setColor("");
		}

	}

	@UiHandler("linkPage0")
	void onLinkPage0(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage0);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel0));
	}

	@UiHandler("linkPage3")
	void onLinkPage3(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage3);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel3));
	}

	@UiHandler("linkPage4")
	void onLinkPage4(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage4);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel4));
	}

	@UiHandler("linkPage5")
	void onLinkPage5(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage5);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel5));
	}

	@UiHandler("linkPage6")
	void onLinkPage6(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage6);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel6));
	}

	@UiHandler("linkPage7")
	void onLinkPage7(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage7);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel7));
	}

	@UiHandler("linkPage8")
	void onLinkPage8(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage8);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel8));
	}

	@UiHandler("linkPage9")
	void onLinkPage9(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage9);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel9));
	}

	@UiHandler("linkPage10")
	void onLinkPage10(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage10);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel10));
	}

	@UiHandler("linkPage11")
	void onLinkPage11(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage11);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel11));
	}

	@UiHandler("linkPage12")
	void onLinkPage12(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage12);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel12));
	}

	@UiHandler("linkPage13")
	void onLinkPage13(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage13);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel13));
	}

	private void applySelectedStyle(FocusPanel panel) {
		panel.getElement().getStyle().setBackgroundColor("#999");
		panel.getElement().getStyle().setColor("white");
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
		resultsPanel.clearFlowPanel();
		closeFootPanel();
	}

	private void addErrorMessage(String msg) {
		showResultsPanel();
		SimplePanel panel = new SimplePanel();
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		panel.add(label);
		resultsPanel.setWidget(panel);
	}
	
	private void validate(Mod390 m390) {
		if (m390.getYear() != 2014) {
			throw new IllegalArgumentException(MSG.requiredField(MSG.fiscalYear()));
		}
		if (!m390.isLegalEntity()) {
			if (AonStringUtils.isEmpty(m390.getDocument())) {
				throw new IllegalArgumentException(MSG.requiredField(" Apart. 0: " + MSG.document()));
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
			if (AonStringUtils.isEmpty(m390.getSecondSurname())) {
				throw new IllegalArgumentException("Para personas f\u00EDsicas, el segundo apellido es obligatorio (Apartado 0)");
			}
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
}
