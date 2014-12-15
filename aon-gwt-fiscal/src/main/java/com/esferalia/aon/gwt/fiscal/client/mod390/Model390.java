package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.shared.DocumentUtil;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Administration;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.occam.api.model.Mod390;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
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
	private final static CommonMessages MSG = GWT.create(CommonMessages.class);
	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	@UiField
	Label simplifiedRegime;

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	DeckPanel pagesPanel;

	@UiField
	Panel panel0;
	@UiField
	FocusPanel linkPage0;

	@UiField
	Panel panel1;
	@UiField
	FocusPanel linkPage1;

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
	HorizontalPanel messagesPanel;
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
	TextBox year;
	@UiField
	EnterpriseSuggestBox enterpriseSuggest;
	@UiField
	CheckBox replacement;
	@UiField
	TextBox replacedReceipt;

	FormPanel diskForm;
	Hidden mod390Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	// PAGE 0
	@UiField
	Page0 page0;

	// PAGE 1
	@UiField
	Page1 page1;

	// PAGE 3
	@UiField
	Page3 page3;

	// PAGE 4
	@UiField
	Page4 page4;

	// PAGE 5
	@UiField
	Page5 page5;

	// PAGE 6
	@UiField
	Page6 page6;

	// PAGE 7
	@UiField
	Page7 page7;

	// PAGE 8
	@UiField
	Page8 page8;

	// PAGE
	@UiField
	Page9 page9;

	// PAGE 10
	@UiField
	Page10 page10;

	// PAGE 11
	@UiField
	Page11 page11;

	// PAGE 12
	@UiField
	Page12 page12;

	// PAGE 13
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
		

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		// http://code.google.com/p/google-web-toolkit/issues/detail?id=6889
		deckPanel.onResize();

		page5.setPage10(page10);
		page7.setPage5(page5);
		page7.setPage6(page6);
		page8.setPage5(page5);
		page8.setPage6(page6);

		// HABILITAR

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
						DialogMessages.alertErrorWidget(MSG
								.unableToFindMod190());
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

	private void select(Mod390 m390) {
		mod390 = m390;
		onLinkPage1(null);
		enterprise = m390.getEnterprise();
		domain = m390.getDomain();

		year.setValue(Integer.toString(m390.getYear()));
		enterpriseSuggest
				.setValue(m390.getDocument(), m390.getEnterpriseName());
		replacement.setValue(m390.isReplacement());
		replacedReceipt.setValue(m390.getReplacedReceipt());

		page0.setValue(m390);

		page1.setValue(m390);
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
						DialogMessages.alertErrorWidget(MSG
								.unableToReadMod190(caught.getMessage()));
					}
				});
	}

	private void cleanErrorMessage() {
		for (int i = 0; i < messagesPanel.getWidgetCount(); i++) {
			messagesPanel.remove(messagesPanel.getWidget(i));
		}
	}

	private void addErrorMessage(String msg) {
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		messagesPanel.add(label);
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		accept(new AcceptAsyncCallback());
	}

	private void accept(AcceptAsyncCallback callback) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		callback.setPopup(popup);
		try {
			populateMod390();
			validate(this.mod390);
			fiscalService.saveMod390(this.mod390, callback);
		} catch (IllegalArgumentException e) {
			popup.hide();
			DialogMessages.alertErrorWidget(e.getMessage()).center();
		}
	}

	private class AcceptAsyncCallback implements AsyncCallback<Mod390> {
		PopupPanel popup;

		public void setPopup(PopupPanel popup) {
			this.popup = popup;
		}

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

	}

	private void validate(Mod390 m390) {
		if (AonUtil.isEmpty(year.getValue())) {
			throw new IllegalArgumentException(MSG.requiredField(MSG
					.fiscalYear()));
		}
		if (!m390.isLegalEntity()) {
			if (AonUtil.isEmpty(m390.getDocument())) {
				throw new IllegalArgumentException(
						MSG.requiredField(" Apart. 0: " + MSG.document()));
			}
			if (!DocumentUtil.isValid(m390.getDocument())) {
				throw new IllegalArgumentException("El NIF/DNI no es correcto");
			}
			if (AonUtil.isEmpty(m390.getName())) {
				throw new IllegalArgumentException(
						"Para personas f\u00EDsicas, el nombre es obligatorio (Apartado 0)");
			}
			if (AonUtil.isEmpty(m390.getFirstSurname())) {
				throw new IllegalArgumentException(
						"Para personas f\u00EDsicas, el primer apellido es obligatorio (Apartado 0)");
			}
			if (AonUtil.isEmpty(m390.getSecondSurname())) {
				throw new IllegalArgumentException(
						"Para personas f\u00EDsicas, el segundo apellido es obligatorio (Apartado 0)");
			}
		} else {
			if (AonUtil.isEmpty(m390.getName())) {
				throw new IllegalArgumentException(
						"No se ha indicado el nombre del declarante. (Apartado 0)");
			}
		}
		if (m390.getMainActivity() == null
				|| AonUtil.isEmpty(m390.getMainActivity().getKey())) {
			throw new IllegalArgumentException(
					"No se ha indicado actividad principal (Apartado 3)");
		}
		if (!m390.isLegalEntity()) {
			if (m390.getAddress() == null) {
				throw new IllegalArgumentException(
						"Indique datos del represante (Apartado 4)");
			}
			if (AonUtil.isEmpty(m390.getAddress().getRdocument())) {
				throw new IllegalArgumentException(
						"Para personas f\u00EDsicas, el NIF/DNI del representante es obligatorio. (Apartado 4)");
			}
			if (!DocumentUtil.isValid(m390.getAddress().getRdocument())) {
				throw new IllegalArgumentException(
						"El NIF/DNI del representante no es correcto. (Apartado 4)");
			}
		} else {
			if (m390.getLegalRepr1() != null) {
				if (!DocumentUtil.isValid(m390.getLegalRepr1().getDocument())) {
					throw new IllegalArgumentException(
							"El NIF del primer representante para personas jurídicas no es correcto. (Apartado 4)");
				}
			}
			if (m390.getLegalRepr2() != null) {
				if (!DocumentUtil.isValid(m390.getLegalRepr2().getDocument())) {
					throw new IllegalArgumentException(
							"El NIF del segundo representante para personas jurídicas no es correcto. (Apartado 4)");
				}
			}
			if (m390.getLegalRepr3() != null) {
				if (!DocumentUtil.isValid(m390.getLegalRepr3().getDocument())) {
					throw new IllegalArgumentException(
							"El NIF del tercer representante para personas jurídicas no es correcto. (Apartado 4)");
				}
			}
		}
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(MSG.confirmDeleteAction())) {
			fiscalService.deleteMod390(this.mod390, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					select(new Mod390());
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
		fiscalService.getFiscalParameters(getCurrentDomain(),
				new AsyncCallback<FiscalParameters>() {
					@Override
					public void onSuccess(FiscalParameters params) {
						Mod390 mod390 = new Mod390();
						enterprise = params.getCompany();
						domain = getCurrentDomain();
						mod390.setEnterprise(params.getCompany());
						mod390.setDomain(getCurrentDomain());
						mod390.setDocument(params.getDocument());
						mod390.setEnterpriseName(params.getName());
						mod390.setYear(params.getDefaultYear() != null ? params
								.getDefaultYear() : 2013);
						if (mod390.isLegalEntity()) {
							mod390.setName(mod390.getEnterpriseName());
						} else {
							String tmpName = mod390.getEnterpriseName();
							if (AonUtil.contains(tmpName, ',')) {
								mod390.setName(AonUtil.trim(AonUtil
										.substringAfter(tmpName, ",")));
								mod390.setFirstSurname(AonUtil.trim(AonUtil
										.substringBefore(tmpName, ",")));
							} else {
								mod390.setName(AonUtil.trim(AonUtil
										.substringBefore(tmpName, " ")));
								mod390.setFirstSurname(AonUtil.trim(AonUtil
										.substringAfter(tmpName, " ")));
							}
						}
						select(mod390);

						page0.setValue(mod390);
						initializePages(mod390);
						int i = deckPanel.getWidgetIndex(formPanel);
						deckPanel.showWidget(i);
						i = pagesPanel.getWidgetIndex(panel1);
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

	private void initializePages(final Mod390 mod390) {
		try {
			final int y = Integer.parseInt(year.getValue());
			fiscalService.getMod311Results(domain, y,
					new AsyncCallback<ArrayList<Mod311Results>>() {
						@Override
						public void onSuccess(ArrayList<Mod311Results> result) {
							int a = 0;
							int f = 0;
							for (Mod311Results re : result) {
								if (re.isFarmer()) {
									if (f >= 0 && f <= 4) {
										page6.setFarmerValue(f, re);
										f++;
									}
								} else {
									if (a == 0) {
										page6.getActivity1().setValue(re);
										a++;
									} else if (a == 1) {
										page6.getActivity2().setValue(re);
										a++;
									}
								}
							}

							page6.refresh();
							page6.populate(mod390);
							page5.initialize(domain, y, mod390);
							initializeMod303Values(domain, y, mod390);
							simplifiedRegime.setVisible(mod390
									.isSimplifiedRegime());
						}

						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(MSG
									.unableToFindMod190Detail(caught
											.getMessage()));
						}
					});

		} catch (NumberFormatException e) {
			// nothing
		}
	}

	private void initializeMod303Values(int domain, int year,
			final Mod390 mod390) {
		fiscalService.getMod303Results(domain, year,
				new AsyncCallback<Mod303Results>() {
					@Override
					public void onSuccess(Mod303Results result) {
						page9.setValue(result);
						page9.populate(mod390);
						page10.setValue(result);
						page10.populate(mod390);
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(MSG
								.unableToFindMod190Detail(caught.getMessage()));
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
		mod390.setDomain(domain);
		enterprise = enterpriseSuggest.getEnterpriseId();
		mod390.setEnterprise(enterprise);
		mod390.setDocument(enterpriseSuggest.getDocument().getValue());
		mod390.setEnterpriseName(enterpriseSuggest.getName().getValue());
		if (AonUtil.isEmpty(mod390.getName())) {
			if (mod390.isLegalEntity()) {
				mod390.setName(enterpriseSuggest.getName().getValue());
			} else {
				String tmpName = enterpriseSuggest.getName().getValue();
				if (AonUtil.contains(tmpName, ',')) {
					mod390.setName(AonUtil.trim(AonUtil.substringAfter(tmpName,
							",")));
					mod390.setFirstSurname(AonUtil.trim(AonUtil
							.substringBefore(tmpName, ",")));
				} else {
					mod390.setName(AonUtil.trim(AonUtil.substringBefore(
							tmpName, " ")));
					mod390.setFirstSurname(AonUtil.trim(AonUtil.substringAfter(
							tmpName, " ")));
				}
			}
		}
		year.selectAll();
		year.setFocus(true);
		page0.setValue(mod390);
		initializePages(mod390);
	}

	@UiHandler("year")
	void onChangeYear(ChangeEvent event) {
		if (Window
				.confirm("El ejercicio ha cambiado, desea recalcular los datos?")) {
			if (domain != 0) {
				try {
					page5.initialize(domain, Integer.parseInt(year.getValue()),
							mod390);
				} catch (NumberFormatException e) {
					// nothing
				}
			}
		}
		;
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
		mod390.setReplacement(replacement.getValue());
		mod390.setReplacedReceipt(replacedReceipt.getValue());
		mod390.setComments(null);

		// Populate Pages
		page0.populate(mod390);
		page1.populate(mod390);
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
		Panel[] panels = new Panel[] { linkPage0, linkPage1, linkPage3,
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

	@UiHandler("linkPage1")
	void onLinkPage1(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage1);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel1));
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
		page7.refresh();
	}

	@UiHandler("linkPage8")
	void onLinkPage8(ClickEvent event) {
		clearLinks();
		applySelectedStyle(linkPage8);
		pagesPanel.showWidget(pagesPanel.getWidgetIndex(panel8));
		page8.refresh();
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
}
