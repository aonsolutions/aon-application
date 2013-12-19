package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.fiscal.client.DialogMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.css.AonCellList;
import com.esferalia.aon.gwt.fiscal.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.css.AonDataGrid;
import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.esferalia.aon.gwt.fiscal.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190Enum.Key;
import com.esferalia.aon.gwt.fiscal.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.fiscal.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.gwt.fiscal.client.widget.IntegerTextBox;
import com.esferalia.aon.gwt.fiscal.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.fiscal.client.widget.ShowMorePagerPanel;
import com.esferalia.aon.gwt.fiscal.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.IrpfData;
import com.esferalia.aon.gwt.fiscal.shared.IrpfResult;
import com.esferalia.aon.gwt.fiscal.shared.Mod190;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Receiver;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class Model190 extends MainEntryPoint {

	interface Model190Binder extends UiBinder<Widget, Model190> {
	}

	private static final Model190Binder MODEL_190_BINDER = GWT
			.create(Model190Binder.class);

	public static final NumberFormat INTEGER_FORMAT = NumberFormat
			.getFormat("#,##0.00");
	public static final FiscalMessages FISCAL_MESSAGES = (FiscalMessages) GWT
			.create(FiscalMessages.class);

	private Mod190 mod190;
	private FiscalServiceAsync mod190Service;
	private final static FiscalMessages MSG = GWT.create(FiscalMessages.class);
	private final static AonResources AON_RESOURCES = GWT
			.create(AonResources.class);
	private Map<Integer, Mod190Receiver> modified = new HashMap<Integer, Mod190Receiver>();

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	Panel listPanel;
	@UiField
	Panel perceptorHeaderPanel;
	@UiField
	DockLayoutPanel formPanel;
	@UiField
	Panel additionalDataPanel;
	@UiField
	HorizontalPanel messagesPanel;
	@UiField
	Panel formContainer;

	@UiField(provided = true)
	CellTable<Mod190> table;

	private NoSelectionModel<Mod190> model;
	private Mod190DetailDataProvider dataProvider;
	private CellList<Mod190Detail> detailList;
	private SingleSelectionModel<Mod190Detail> detailModel;

	private int domain;
	private int enterprise;

	@UiField
	ShowMorePagerPanel pagerPanel;
	@UiField
	Panel perceptorPanel;

	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
	@UiField
	Button newButton;
	@UiField
	Button cancelButton;
	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;
	@UiField
	Button newDetailButton;
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
	DocumentTextBox receiverDocument;
	@UiField
	DocumentTextBox representativeDocument;
	@UiField
	TextBox fullName;
	@UiField
	IntegerTextBox accrualYear;
	@UiField
	ProvinceListBox province;
	@UiField
	CheckBox ceutaMelilla;
	@UiField(provided = true)
	KeyListBox key;
	@UiField(provided = true)
	ListBox subkey;
	@UiField
	DoubleTextBox perception;
	@UiField
	DoubleTextBox retention;
	@UiField
	DoubleTextBox inKindPerception;
	@UiField
	DoubleTextBox inKindDeposit;
	@UiField
	DoubleTextBox inKindOutputDeposit;
	@UiField
	IntegerTextBox birthYear;
	@UiField
	ListBox familySituation;
	@UiField
	DocumentTextBox spouseDocument;
	@UiField
	ListBox disability;
	@UiField
	ListBox contract;
	@UiField
	CheckBox workActivityExtension;
	@UiField
	CheckBox geographicMobility;
	@UiField
	CheckBox homeLoanCommunnication;
	@UiField
	DoubleTextBox applicableReduction;
	@UiField
	DoubleTextBox deducibleExpense;
	@UiField
	DoubleTextBox compensatoryPension;
	@UiField
	DoubleTextBox foodAnnuality;

	@UiField
	IntegerTextBox lessThan3Descendent;
	@UiField
	IntegerTextBox lessThan3DescendentRatio;
	@UiField
	IntegerTextBox otherDescendent;
	@UiField
	IntegerTextBox otherDescendentRatio;
	@UiField
	ListBox firstChildCalculation;
	@UiField
	ListBox secondChildCalculation;
	@UiField
	ListBox thirdChildCalculation;

	@UiField
	IntegerTextBox disabilityDescendent33;
	@UiField
	IntegerTextBox disabilityDescendent33Ratio;
	@UiField
	IntegerTextBox disabilityDescendentDependence;
	@UiField
	IntegerTextBox disabilityDescendentDependenceRatio;
	@UiField
	IntegerTextBox disabilityDescendent65;
	@UiField
	IntegerTextBox disabilityDescendent65Ratio;
	@UiField
	IntegerTextBox lessThan75Ascendant;
	@UiField
	IntegerTextBox lessThan75AscendantRatio;
	@UiField
	IntegerTextBox ascendant;
	@UiField
	IntegerTextBox ascendantRatio;
	@UiField
	IntegerTextBox disabilityAscendant33;
	@UiField
	IntegerTextBox disabilityAscendant33Ratio;
	@UiField
	IntegerTextBox disabilityAscendantDependence;
	@UiField
	IntegerTextBox disabilityAscendantDependenceRatio;
	@UiField
	IntegerTextBox disabilityAscendant65;
	@UiField
	IntegerTextBox disabilityAscendant65Ratio;

	FormPanel diskForm;
	Hidden mod190Hidden;

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		AON_RESOURCES.css().ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync mod190ServiceRaw = GWT.create(FiscalService.class);
		mod190Service = new FiscalServiceAsyncDecorator(mod190ServiceRaw);

		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);
		DataGrid.Resources dataGridStyle = GWT.create(AonDataGrid.class);

		table = new CellTable<Mod190>(1, tableStyle, Mod190.PROVIDES_KEY);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addYearColumn();
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();

		model = new NoSelectionModel<Mod190>(Mod190.PROVIDES_KEY);
		model.addSelectionChangeHandler(new Mod190SelectionHandler());
		table.setSelectionModel(model);
		table.setEmptyTableWidget(new HTML(MSG.noData()));

		Mod190DetailCell mod190DetailCell = new Mod190DetailCell();

		CellList.Resources cellListStyle = GWT.create(AonCellList.class);
		detailList = new CellList<Mod190Detail>(mod190DetailCell,
				cellListStyle, Mod190Detail.PROVIDES_KEY);

		detailList.setStylePrimaryName(dataGridStyle.dataGridStyle()
				.dataGridWidget());
		detailList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailList
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		detailModel = new SingleSelectionModel<Mod190Detail>(
				Mod190Detail.PROVIDES_KEY);
		detailModel
				.addSelectionChangeHandler(new Mod190DetailSelectionHandler());

		detailList.setSelectionModel(detailModel);
		detailList.setEmptyListWidget(new HTML(MSG.noData()));

		dataProvider = new Mod190DetailDataProvider(Mod190Detail.PROVIDES_KEY);
		dataProvider.addDataDisplay(detailList);
		detailList.setVisible(true);

		key = new KeyListBox();
		// subkey = key.getSubkey();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = MODEL_190_BINDER.createAndBindUi(this);

		initializeMode190DetailWidgets();
		pagerPanel.setDisplay(detailList);
		pagerPanel.setIncrementSize(0);

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		mod190Hidden = new Hidden("mod190");
		diskForm.add(mod190Hidden);
		formContainer.add(diskForm);
		
		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		// http://code.google.com/p/google-web-toolkit/issues/detail?id=6889
		deckPanel.onResize();
	}

	private void initializeMode190DetailWidgets() {
		familySituation.addItem("-");
		familySituation.addItem("1");
		familySituation.addItem("2");
		familySituation.addItem("3");

		disability.addItem("0");
		disability.addItem("1");
		disability.addItem("2");
		disability.addItem("3");

		contract.addItem("-");
		contract.addItem("1");
		contract.addItem("2");
		contract.addItem("3");
		contract.addItem("4");

		firstChildCalculation.addItem("-");
		firstChildCalculation.addItem("1");
		firstChildCalculation.addItem("2");

		secondChildCalculation.addItem("-");
		secondChildCalculation.addItem("1");
		secondChildCalculation.addItem("2");

		thirdChildCalculation.addItem("-");
		thirdChildCalculation.addItem("1");
		thirdChildCalculation.addItem("2");
	}

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	private void addNameColumn() {
		final TextColumn<Mod190> nameColumn = new TextColumn<Mod190>() {
			@Override
			public String getValue(Mod190 mod190) {
				return mod190.getName();
			}
		};
		table.addColumn(nameColumn, FISCAL_MESSAGES.name());
		table.setColumnWidth(nameColumn, 100, Unit.PCT);
	}

	private void addDocumentColumn() {
		final TextColumn<Mod190> documentColumn = new TextColumn<Mod190>() {
			@Override
			public String getValue(Mod190 mod190) {
				return mod190.getDocument();
			}
		};
		table.addColumn(documentColumn, FISCAL_MESSAGES.document());
		table.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	private void addReplacementColumn() {
		Column<Mod190, ImageResource> replacementColumn = new Column<Mod190, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod190 mod190) {
				return mod190.isReplacement() ? AON_RESOURCES.aonIconChecked()
						: AON_RESOURCES.aonIconCheck();
			}
		};
		table.addColumn(replacementColumn, FISCAL_MESSAGES.replacement());
		replacementColumn.setCellStyleNames(AON_RESOURCES.css()
				.aonDataTableIconColumn());
		table.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<Mod190> yearColumn = new TextColumn<Mod190>() {
			@Override
			public String getValue(Mod190 mod190) {
				return Integer.toString(mod190.getYear());
			}
		};
		table.addColumn(yearColumn, FISCAL_MESSAGES.fiscalYear());
		yearColumn.setCellStyleNames(AON_RESOURCES.css().aonTextCenter());
		table.setColumnWidth(yearColumn, 100, Unit.PX);
	}

	private void addSelectorColumn() {
		final Column<Mod190, ImageResource> selectorColumn = new Column<Mod190, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod190 mod190) {
				return AON_RESOURCES.aonIconRowSelector();
			}
		};
		table.addColumn(selectorColumn);
		table.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	class Mod190SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod190 sel = model.getLastSelectedObject();
			mod190Service.getMod190(sel.getId(), new AsyncCallback<Mod190>() {
				@Override
				public void onSuccess(Mod190 selected) {
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
					DialogMessages.alertErrorWidget(MSG.unableToReadMod190(caught
							.getMessage()));
				}
			});
		}
	}

	class Mod190DetailSelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			final Mod190Detail selected = detailModel.getSelectedObject();
			if (modified.containsKey(selected.getId())) {
				populatePerceptor(modified.get(selected.getId()));
			} else {
				mod190Service.getMod190Detail(selected.getId(),
						new AsyncCallback<Mod190Receiver>() {
							@Override
							public void onSuccess(Mod190Receiver perceptor) {
								if (perceptor == null) {
									DialogMessages.alertErrorWidget(MSG.unableToFindMod190Detail(MSG
											.unableToFindPerceptor(selected
													.getId())));
								} else {
									populatePerceptor(perceptor);
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								DialogMessages.alertErrorWidget(MSG
										.unableToFindMod190Detail(caught
												.getMessage()));
							}
						});
			}

		}
	}

	private void populatePerceptor(Mod190Receiver perceptor) {
		receiverDocument.setValue(perceptor.getDocument());
		representativeDocument.setValue(perceptor.getRepresentativeDocument());
		fullName.setValue(perceptor.getName());
		accrualYear.setValue(perceptor.getAccrualYear());
		province.setSelectedIndex(perceptor.getProvince());
		key.setValue(perceptor.getKey(), perceptor.getSubKey());
		perception.setValue(perceptor.getPerception());
		retention.setValue(perceptor.getRetention());
		inKindPerception.setValue(perceptor.getInKindPerception());
		inKindDeposit.setValue(perceptor.getInKindDeposit());
		inKindOutputDeposit.setValue(perceptor.getInKindOutputDeposit());

		IrpfData irpfData = perceptor.getIrpfData();
		if (irpfData != null) {
			ceutaMelilla.setValue(irpfData.isCeutaMelilla());
			birthYear.setValue(irpfData.getBirthYear());
			familySituation.setSelectedIndex(irpfData.getFamilySituation());
			spouseDocument.setValue(irpfData.getSpouseDocument());
			disability.setSelectedIndex(irpfData.getDisability());
			contract.setSelectedIndex(irpfData.getContract());
			workActivityExtension.setValue(irpfData.isWorkActivityExtension());
			geographicMobility.setValue(irpfData.isGeographicMobility());
		}
		IrpfResult irpfResult = perceptor.getIrpfResult();
		if (irpfResult != null) {
			applicableReduction.setValue(irpfResult.getApplicableReduction());
			deducibleExpense.setValue(irpfResult.getDeducibleExpense());
			compensatoryPension.setValue(irpfResult.getCompensatoryPension());
			foodAnnuality.setValue(irpfResult.getFoodAnnuality());
			homeLoanCommunnication.setValue(irpfResult
					.isHomeLoanCommunnication());
			lessThan3Descendent.setValue(irpfResult.getLessThan3Descendent());
			lessThan3DescendentRatio.setValue(irpfResult
					.getLessThan3DescendentRatio());
			otherDescendent.setValue(irpfResult.getOtherDescendent());
			otherDescendentRatio.setValue(irpfResult.getOtherDescendentRatio());
			firstChildCalculation.setSelectedIndex(irpfResult
					.getFirstChildCalculation());
			secondChildCalculation.setSelectedIndex(irpfResult
					.getSecondChildCalculation());
			thirdChildCalculation.setSelectedIndex(irpfResult
					.getThirdChildCalculation());
			disabilityDescendent33.setValue(irpfResult
					.getDisabilityDescendent33());
			disabilityDescendent33Ratio.setValue(irpfResult
					.getDisabilityDescendent33Ratio());
			disabilityDescendentDependence.setValue(irpfResult
					.getDisabilityDescendentDependence());
			disabilityDescendentDependenceRatio.setValue(irpfResult
					.getDisabilityDescendentDependenceRatio());
			disabilityDescendent65.setValue(irpfResult
					.getDisabilityDescendent65());
			disabilityDescendent65Ratio.setValue(irpfResult
					.getDisabilityDescendent65Ratio());
			lessThan75Ascendant.setValue(irpfResult.getLessThan75Ascendant());
			lessThan75AscendantRatio.setValue(irpfResult
					.getLessThan75AscendantRatio());
			ascendant.setValue(irpfResult.getAscendant());
			ascendantRatio.setValue(irpfResult.getAscendantRatio());
			disabilityAscendant33.setValue(irpfResult
					.getDisabilityAscendant33());
			disabilityAscendant33Ratio.setValue(irpfResult
					.getDisabilityAscendant33Ratio());
			disabilityAscendantDependence.setValue(irpfResult
					.getDisabilityAscendantDependence());
			disabilityAscendantDependenceRatio.setValue(irpfResult
					.getDisabilityAscendantDependenceRatio());
			disabilityAscendant65.setValue(irpfResult
					.getDisabilityAscendant65());
			disabilityAscendant65Ratio.setValue(irpfResult
					.getDisabilityAscendant65Ratio());
		}
		restoreDeletedButton.setVisible(perceptor.isDeleted());
		deleteDetailButton.setVisible(!perceptor.isDeleted());
		enableOrDisableAdditionalDataPanel();
	}

	static class Mod190DetailCell extends AbstractCell<Mod190Detail> {
		@Override
		public void render(Cell.Context context, Mod190Detail value,
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
			String newLabel = FISCAL_MESSAGES.newPerceptor() + " ("
					+ (value.getId() * (-1)) + ")";
			sb.appendEscaped(AonUtil.isEmpty(value.getName()) ? newLabel
					: value.getName());

			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("</div>");
			}
		}
	}

	private void select(Mod190 selected) {
		mod190 = selected;
		year.setValue(Integer.toString(mod190.getYear()));
		administration.setSelectedIndex(mod190.getAdministration());
		replacement.setValue(mod190.isReplacement());
		confidential.setValue(mod190.isConfidential());
		comments.setValue(mod190.getComments());
		enterpriseSuggest.setValue(mod190.getDocument(), mod190.getName());
		contactPhone.setValue(mod190.getContactPhone());
		contactPerson.setValue(mod190.getContactPerson());
		receipt.setValue(mod190.getReceipt());
		replacedReceipt.setValue(mod190.getReplacedReceipt());
		domain = mod190.getDomain();
		enterprise = mod190.getEnterprise();
		// Toolbar states
		deleteButton.setVisible(mod190.getId() != null);
		newButton.setVisible(mod190.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(mod190.getId() != null);
		printButton.setVisible(mod190.getId() != null);

		pagerPanel.setVisible(mod190.getId() != null);
		perceptorPanel.setVisible(mod190.getId() != null);
		perceptorHeaderPanel.setVisible(mod190.getId() != null);

		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),
				true);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		mod190Service.getMod190s(getCurrentDomain(),
				new AsyncCallback<ArrayList<Mod190>>() {
					@Override
					public void onSuccess(ArrayList<Mod190> result) {
						if (result == null || result.size() == 0) {
							onNewButtonClick(null);
//							int i = deckPanel.getWidgetIndex(formPanel);
//							deckPanel.showWidget(i);
//							mod190 = new Mod190();
//							mod190.setDomain(getCurrentDomain());
//							year.setFocus(true);
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
						DialogMessages.alertErrorWidget(MSG.unableToReadMod190(caught
								.getMessage()));
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
		if (AonUtil.isEmpty(year.getValue())) {
			throw new IllegalArgumentException(MSG.requiredField(MSG
					.fiscalYear()));
		}

		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(FISCAL_MESSAGES.processing());
		label.addStyleName(AON_RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();

		populateMod190();
		mod190Service.saveMod190(this.mod190,
				new ArrayList<Mod190Receiver>(modified.values()),
				new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 result) {
						modified = new HashMap<Integer, Mod190Receiver>();
						select(result);
						popup.hide();
						cleanErrorMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						addErrorMessage(MSG.unableToSaveMod190(caught
								.getMessage()));
					}
				});
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(MSG.confirmDeleteAction())) {
			mod190Service.deleteMod190(this.mod190, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					select(new Mod190());
					table.setVisibleRangeAndClearData(table.getVisibleRange(),
							true);
				}

				@Override
				public void onFailure(Throwable caught) {
					DialogMessages.alertErrorWidget(MSG.unableToDeleteMod190(caught
							.getMessage()));
				}
			});
		}
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		cleanErrorMessage();
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
		modified = new HashMap<Integer, Mod190Receiver>();
		mod190Service.getFiscalParameters(getCurrentDomain(),
				new AsyncCallback<FiscalParameters>() {
					@Override
					public void onSuccess(FiscalParameters params) {
						Mod190 mod190 = new Mod190();
						enterprise = params.getCompany();
						domain = getCurrentDomain();
						mod190.setEnterprise(params.getCompany());
						mod190.setDomain(getCurrentDomain());
						mod190.setDocument(params.getDocument());
						mod190.setName(params.getName());
						mod190.setYear(params.getDefaultYear()!=null?params.getDefaultYear():2013);
						mod190.setAdministration(params.getAdministration()!=null?params.getAdministration():4);
						mod190.setContactPerson(params.getContactPerson());
						mod190.setContactPhone(params.getContactPhone());
						select(mod190);

						int i = deckPanel.getWidgetIndex(formPanel);
						deckPanel.showWidget(i);
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
		modified = new HashMap<Integer, Mod190Receiver>();
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),
				true);
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	@UiHandler("newDetailButton")
	void onNewDetailButtonClick(ClickEvent event) {
		int newKey = -1;
		for (Integer key : modified.keySet()) {
			newKey = newKey + ((key < 0) ? (-1) : 0);
		}
		final Mod190Receiver perceptor = new Mod190Receiver();
		perceptor.setId(newKey);
		perceptor.setKey(Key.A.getValue());
		perceptor.setIrpfData(new IrpfData());
		perceptor.setIrpfResult(new IrpfResult());
		modified.put(newKey, perceptor);

		ArrayList<Mod190Detail> list = new ArrayList<Mod190Detail>();
		list.add(perceptor);
		detailList.setRowCount(detailList.getRowCount() + 1);
		detailList.setPageSize(detailList.getRowCount());
		detailList.setRowData(detailList.getRowCount(), list);

		detailList.redraw();
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setDeleted(true);
				detailModel.getSelectedObject().setDeleted(true);
				restoreDeletedButton.setVisible(true);
				deleteDetailButton.setVisible(false);
				detailList.redraw();
			}
		});
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setDeleted(false);
				if (!p.isDirty()) {
					modified.remove(detailModel.getSelectedObject().getId());
					detailModel.getSelectedObject().setDeleted(false);
					restoreDeletedButton.setVisible(false);
					deleteDetailButton.setVisible(true);
					detailList.redraw();
				}
			}
		});
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> event) {
		domain = enterpriseSuggest.getDomainId();
		mod190.setDomain(domain);
		enterprise = enterpriseSuggest.getEnterpriseId();
		mod190.setEnterprise(enterprise);
		mod190.setName(enterpriseSuggest.getName().getValue());
		year.selectAll();
		year.setFocus(true);
	}

	private void populateMod190() {
		try {
			mod190.setYear(Integer.parseInt(year.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(MSG.unableToParseYear());
		}
		mod190.setDomain(domain);
		mod190.setEnterprise(enterprise);
		mod190.setAdministration(administration.getSelectedIndex());
		mod190.setReplacement(replacement.getValue());
		mod190.setConfidential(confidential.getValue());
		mod190.setComments(comments.getValue());
		mod190.setDocument(enterpriseSuggest.getValue());
		mod190.setName(enterpriseSuggest.getName().getValue());
		mod190.setContactPhone(contactPhone.getValue());
		mod190.setContactPerson(contactPerson.getValue());
		mod190.setReceipt(receipt.getValue());
		mod190.setReplacedReceipt(replacedReceipt.getValue());
	}

	class Mod190DetailDataProvider extends AsyncDataProvider<Mod190Detail> {

		public Mod190DetailDataProvider(
				ProvidesKey<Mod190Detail> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod190Detail> display) {
			if (mod190 != null && mod190.getId() != null) {
				detailModel.setSelected(detailModel.getSelectedObject(), false);
				mod190Service.getMod190DetailByMod190(mod190.getId(), 0,
						Integer.MAX_VALUE,
						new AsyncCallback<ArrayList<Mod190Detail>>() {

							@Override
							public void onSuccess(ArrayList<Mod190Detail> result) {
								int selectIdx = 0;

								if (result.size() == 0) {
									Mod190Receiver perceptor = new Mod190Receiver();
									perceptor.setId(-1);
									perceptor.setKey(Key.A.getValue());
									perceptor.setIrpfData(new IrpfData());
									perceptor.setIrpfResult(new IrpfResult());
									modified.put(-1, perceptor);
								}
								for (Mod190Detail det : modified.values()) {
									if (det.getId() < 0) {
										result.add(det);
										selectIdx = result.size() - 1;
									}
								}

								detailList.setPageSize(result.size());
								updateRowCount(result.size(), true);
								updateRowData(0, result);
								detailModel.setSelected(result.get(selectIdx),
										true);
								detailList.getRowElement(selectIdx)
										.scrollIntoView();
								pagerPanel.scrollToLeft();
							}

							@Override
							public void onFailure(Throwable caught) {
								DialogMessages.alertErrorWidget(MSG
										.unableToReadMod190(caught.getMessage()));
							}
						});
			}
		}
	}

	private void enableOrDisableAdditionalDataPanel() {
		Key keyEnum = Key.values()[key.getSelectedIndex()];
		String subk = ((key.getSubkey().getSelectedIndex() == -1) ? null : key
				.getSubkey().getValue(key.getSubkey().getSelectedIndex()));
		if (Key.A == keyEnum || Key.C == keyEnum || Key.D == keyEnum
				|| (Key.B == keyEnum && "01".equals(subk))
				|| (Key.B == keyEnum && "02".equals(subk))) {
			additionalDataPanel.setVisible(true);
		} else {
			additionalDataPanel.setVisible(false);
		}
	}

	private Mod190Receiver getModifiedPerceptor(final IPerceptorChanged pc) {
		final Mod190Detail selected = detailModel.getSelectedObject();
		if (!modified.containsKey(selected.getId())) {
			mod190Service.getMod190Detail(selected.getId(),
					new AsyncCallback<Mod190Receiver>() {
						@Override
						public void onSuccess(Mod190Receiver per) {
							if (per == null) {
								DialogMessages.alertErrorWidget(MSG
										.unableToFindMod190Detail(MSG
												.unableToFindPerceptor(selected
														.getId())));
							} else {
								modified.put(selected.getId(), per);
								pc.perceptorChanged(per);
								detailList.redraw();
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(MSG
									.unableToFindMod190Detail(caught
											.getMessage()));
						}
					});

		} else {
			pc.perceptorChanged(modified.get(selected.getId()));
			detailList.redraw();
		}
		return modified.get(selected.getId());
	}

	// -------------------------------------------------------------- UiHandler
	interface IPerceptorChanged {
		void perceptorChanged(Mod190Receiver p);
	}

	@UiHandler("receiverDocument")
	void onChangeReceiverDocument(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setDocument(receiverDocument.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("representativeDocument")
	void onChangeRepresentativeDocument(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setRepresentativeDocument(representativeDocument.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("fullName")
	void onChangeFullName(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setName(fullName.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
				detailModel.getSelectedObject().setName(fullName.getValue());
			}
		});
	}

	@UiHandler("accrualYear")
	void onChangeAccrualYear(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				try {
					p.setAccrualYear(accrualYear.getIntValue());
					p.setDirty(true);
					detailModel.getSelectedObject().setDirty(true);
				} catch (NumberFormatException e) {
					accrualYear.addStyleName(AON_RESOURCES.css()
							.aonTextBoxError());
				}
			}
		});
	}

	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setProvince(province.getSelectedIndex());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("ceutaMelilla")
	void onChangeCeutaMelilla(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfData().setCeutaMelilla(ceutaMelilla.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("key")
	void onChangeKey(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setKey(Key.values()[key.getSelectedIndex()].getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
				enableOrDisableAdditionalDataPanel();
			}
		});
	}

	@UiHandler("subkey")
	void onChangeSubkey(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				String subk = ((key.getSubkey().getSelectedIndex() == -1) ? null
						: key.getSubkey().getValue(
								key.getSubkey().getSelectedIndex()));
				p.setSubKey(subk);
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
				enableOrDisableAdditionalDataPanel();
			}
		});
	}

	@UiHandler("perception")
	void onChangePerception(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setPerception(perception.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setRetention(retention.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("inKindPerception")
	void onChangeValuation(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setInKindPerception(inKindPerception.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("inKindDeposit")
	void onChangeInKindDeposit(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setInKindDeposit(inKindDeposit.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("inKindOutputDeposit")
	void onChangeInKindOutputDeposit(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.setInKindOutputDeposit(inKindOutputDeposit.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("birthYear")
	void onChangeBirthYear(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfData().setBirthYear(birthYear.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("familySituation")
	void onChangeFamilySituation(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfData().setFamilySituation(
						familySituation.getSelectedIndex());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("spouseDocument")
	void onChangeSpouseDocument(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfData().setSpouseDocument(spouseDocument.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disability")
	void onChangeDisability(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfData().setDisability(disability.getSelectedIndex());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("contract")
	void onChangeContract(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfData().setContract(contract.getSelectedIndex());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("workActivityExtension")
	void onChangeWorkActivityExtension(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfData().setWorkActivityExtension(
						workActivityExtension.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("geographicMobility")
	void onChangeGeographicMobility(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfData().setGeographicMobility(
						geographicMobility.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("homeLoanCommunnication")
	void onChangeHomeLoanCommunnication(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setHomeLoanCommunnication(
						homeLoanCommunnication.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("applicableReduction")
	void onChangeApplicableReduction(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setApplicableReduction(
						applicableReduction.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("deducibleExpense")
	void onChangeDeducibleExpense(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDeducibleExpense(
						deducibleExpense.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("compensatoryPension")
	void onChangeCompensatoryPension(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setCompensatoryPension(
						compensatoryPension.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("foodAnnuality")
	void onChangeFoodAnnuality(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setFoodAnnuality(
						foodAnnuality.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("lessThan3Descendent")
	void onChangeLessThan3Descendent(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setLessThan3Descendent(
						lessThan3Descendent.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("lessThan3DescendentRatio")
	void onChangeLessThan3DescendentRatio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setLessThan3DescendentRatio(
						lessThan3DescendentRatio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("otherDescendent")
	void onChangeOtherDescendent(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setOtherDescendent(
						otherDescendent.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("otherDescendentRatio")
	void onChangeOtherDescendentRatio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setOtherDescendentRatio(
						otherDescendentRatio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("firstChildCalculation")
	void onChangeFirstChildCalculation(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setFirstChildCalculation(
						firstChildCalculation.getSelectedIndex());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("secondChildCalculation")
	void onChangeSecondChildCalculation(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setSecondChildCalculation(
						secondChildCalculation.getSelectedIndex());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("thirdChildCalculation")
	void onChangeThirdChildCalculation(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setThirdChildCalculation(
						thirdChildCalculation.getSelectedIndex());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityDescendent33")
	void onChangeDisabilityDescendent33(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityDescendent33(
						disabilityDescendent33.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityDescendent33Ratio")
	void onChangeDisabilityDescendent33Ratio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityDescendent33Ratio(
						disabilityDescendent33Ratio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityDescendentDependence")
	void onChangeDisabilityDescendentDependence(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityDescendentDependence(
						disabilityDescendentDependence.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityDescendentDependenceRatio")
	void onChangeDisabilityDescendentDependenceRatio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityDescendentDependenceRatio(
						disabilityDescendentDependenceRatio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityDescendent65")
	void onChangeDisabilityDescendent65(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityDescendent65(
						disabilityDescendent65.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityDescendent65Ratio")
	void onChangeDisabilityDescendent65Ratio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityDescendent65Ratio(
						disabilityDescendent65Ratio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("lessThan75Ascendant")
	void onChangeLessThan75Ascendant(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setLessThan75Ascendant(
						lessThan75Ascendant.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("lessThan75AscendantRatio")
	void onChangeLessThan75AscendantRatio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setLessThan75AscendantRatio(
						lessThan75AscendantRatio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("ascendant")
	void onChangeAscendant(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setAscendant(ascendant.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("ascendantRatio")
	void onChangeAscendantRatio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setAscendantRatio(
						ascendantRatio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityAscendant33")
	void onChangeDisabilityAscendant33(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityAscendant33(
						disabilityAscendant33.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityAscendant33Ratio")
	void onChangeDisabilityAscendant33Ratio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityAscendant33Ratio(
						disabilityAscendant33Ratio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityAscendantDependence")
	void onChangeDisabilityAscendantDependence(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityAscendantDependence(
						disabilityAscendantDependence.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityAscendantDependenceRatio")
	void onChangeDisabilityAscendantDependenceRatio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityAscendantDependenceRatio(
						disabilityAscendantDependenceRatio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityAscendant65")
	void onChangeDisabilityAscendant65(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityAscendant65(
						disabilityAscendant65.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("disabilityAscendant65Ratio")
	void onChangeDisabilityAscendant65Ratio(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod190Receiver p) {
				p.getIrpfResult().setDisabilityAscendant65Ratio(
						disabilityAscendant65Ratio.getIntValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	public class KeyListBox extends ListBox {

		public KeyListBox() {
			subkey = new ListBox();
			subkey.setWidth("35px");

			setWidth("30px");
			for (Key key : Key.values()) {
				this.addItem(key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					subkey.clear();
					Key keyEnum = Key.values()[getSelectedIndex()];
					if (keyEnum.hasSubkeys()) {
						subkey.setEnabled(true);
						for (int i = 0; i < keyEnum.getSubKeys().length; i++) {
							subkey.addItem(keyEnum.getSubKeys()[i]);
						}
					} else {
						subkey.setEnabled(false);
					}
				}
			});
		}

		public boolean hasSubkeys() {
			Key keyEnum = Key.values()[getSelectedIndex()];
			return keyEnum.hasSubkeys();
		}

		public ListBox getSubkey() {
			return subkey;
		}

		public void setValue(String key, String subKey) {
			Key keyEnum = Key.valueOf(key);
			setSelectedIndex(keyEnum.ordinal());
			getSubkey().clear();
			if (hasSubkeys()) {
				getSubkey().setEnabled(true);
				for (int i = 0; i < keyEnum.getSubKeys().length; i++) {
					getSubkey().addItem(keyEnum.getSubKeys()[i]);
					if (keyEnum.getSubKeys()[i].equals(subKey)) {
						getSubkey().setSelectedIndex(i);
					}
				}
				getSubkey().setEnabled(true);
			} else {
				getSubkey().setEnabled(false);
			}
		}
	}

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model190File");
		mod190Hidden.setValue( String.valueOf(mod190.getId()) );
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model190Print");
		mod190Hidden.setValue( String.valueOf(mod190.getId()) );
		diskForm.submit();
	}


}
