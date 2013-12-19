package com.esferalia.aon.gwt.fiscal.client.mod180;

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
import com.esferalia.aon.gwt.fiscal.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.fiscal.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.gwt.fiscal.client.widget.IntegerTextBox;
import com.esferalia.aon.gwt.fiscal.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.fiscal.client.widget.ShowMorePagerPanel;
import com.esferalia.aon.gwt.fiscal.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Mod180;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Receiver;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
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

public class Model180 extends MainEntryPoint {

	interface Model180Binder extends UiBinder<Widget, Model180> {
	}

	private static final Model180Binder MODEL_180_BINDER = GWT
			.create(Model180Binder.class);

	public static final NumberFormat INTEGER_FORMAT = NumberFormat
			.getFormat("#,##0.00");
	public static final FiscalMessages FISCAL_MESSAGES = (FiscalMessages) GWT
			.create(FiscalMessages.class);

	private Mod180 mod180;
	private FiscalServiceAsync mod180Service;
	private final static FiscalMessages MSG = GWT.create(FiscalMessages.class);
	private final static AonResources AON_RESOURCES = GWT
			.create(AonResources.class);
	private Map<Integer, Mod180Receiver> modified = new HashMap<Integer, Mod180Receiver>();

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	Panel listPanel;
	@UiField
	Panel perceptorHeaderPanel;
	@UiField
	DockLayoutPanel formPanel;
	@UiField
	HorizontalPanel messagesPanel;
	@UiField
	Panel formContainer;

	@UiField(provided = true)
	CellTable<Mod180> table;

	private NoSelectionModel<Mod180> model;
	private Mod180DetailDataProvider dataProvider;
	private CellList<Mod180Detail> detailList;
	private SingleSelectionModel<Mod180Detail> detailModel;

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
	DoubleTextBox perception;
	@UiField
	DoubleTextBox retention;
	@UiField
	DoubleTextBox percent;
	@UiField
	CheckBox inKind;

	FormPanel diskForm;
	Hidden mod180Hidden;

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		AON_RESOURCES.css().ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync mod180ServiceRaw = GWT.create(FiscalService.class);
		mod180Service = new FiscalServiceAsyncDecorator(mod180ServiceRaw);

		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);
		DataGrid.Resources dataGridStyle = GWT.create(AonDataGrid.class);

		table = new CellTable<Mod180>(1, tableStyle, Mod180.PROVIDES_KEY);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addYearColumn();
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();

		model = new NoSelectionModel<Mod180>(Mod180.PROVIDES_KEY);
		model.addSelectionChangeHandler(new Mod180SelectionHandler());
		table.setSelectionModel(model);
		table.setEmptyTableWidget(new HTML(MSG.noData()));

		Mod180DetailCell mod180DetailCell = new Mod180DetailCell();

		CellList.Resources cellListStyle = GWT.create(AonCellList.class);
		detailList = new CellList<Mod180Detail>(mod180DetailCell,
				cellListStyle, Mod180Detail.PROVIDES_KEY);

		detailList.setStylePrimaryName(dataGridStyle.dataGridStyle()
				.dataGridWidget());
		detailList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailList
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		detailModel = new SingleSelectionModel<Mod180Detail>(
				Mod180Detail.PROVIDES_KEY);
		detailModel
				.addSelectionChangeHandler(new Mod180DetailSelectionHandler());

		detailList.setSelectionModel(detailModel);
		detailList.setEmptyListWidget(new HTML(MSG.noData()));

		dataProvider = new Mod180DetailDataProvider(Mod180Detail.PROVIDES_KEY);
		dataProvider.addDataDisplay(detailList);
		detailList.setVisible(true);

		// Create the UI defined in Employee.ui.xml.
		Widget ui = MODEL_180_BINDER.createAndBindUi(this);

		pagerPanel.setDisplay(detailList);
		pagerPanel.setIncrementSize(0);

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		mod180Hidden = new Hidden("mod180");
		diskForm.add(mod180Hidden);
		formContainer.add(diskForm);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		// http://code.google.com/p/google-web-toolkit/issues/detail?id=6889
		deckPanel.onResize();
	}

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	private void addNameColumn() {
		final TextColumn<Mod180> nameColumn = new TextColumn<Mod180>() {
			@Override
			public String getValue(Mod180 mod180) {
				return mod180.getName();
			}
		};
		table.addColumn(nameColumn, FISCAL_MESSAGES.name());
		table.setColumnWidth(nameColumn, 100, Unit.PCT);
	}

	private void addDocumentColumn() {
		final TextColumn<Mod180> documentColumn = new TextColumn<Mod180>() {
			@Override
			public String getValue(Mod180 mod180) {
				return mod180.getDocument();
			}
		};
		table.addColumn(documentColumn, FISCAL_MESSAGES.document());
		table.setColumnWidth(documentColumn, 150, Unit.PX);
	}

	private void addReplacementColumn() {
		Column<Mod180, ImageResource> replacementColumn = new Column<Mod180, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod180 mod180) {
				return mod180.isReplacement() ? AON_RESOURCES.aonIconChecked()
						: AON_RESOURCES.aonIconCheck();
			}
		};
		table.addColumn(replacementColumn, FISCAL_MESSAGES.replacement());
		replacementColumn.setCellStyleNames(AON_RESOURCES.css()
				.aonDataTableIconColumn());
		table.setColumnWidth(replacementColumn, 100, Unit.PX);
	}

	private void addYearColumn() {
		final TextColumn<Mod180> yearColumn = new TextColumn<Mod180>() {
			@Override
			public String getValue(Mod180 mod180) {
				return Integer.toString(mod180.getYear());
			}
		};
		table.addColumn(yearColumn, FISCAL_MESSAGES.fiscalYear());
		yearColumn.setCellStyleNames(AON_RESOURCES.css().aonTextCenter());
		table.setColumnWidth(yearColumn, 100, Unit.PX);
	}

	private void addSelectorColumn() {
		final Column<Mod180, ImageResource> selectorColumn = new Column<Mod180, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Mod180 mod180) {
				return AON_RESOURCES.aonIconRowSelector();
			}
		};
		table.addColumn(selectorColumn);
		table.setColumnWidth(selectorColumn, 20, Unit.PX);
	}

	class Mod180SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod180 sel = model.getLastSelectedObject();
			mod180Service.getMod180(sel.getId(), new AsyncCallback<Mod180>() {
				@Override
				public void onSuccess(Mod180 selected) {
					if (selected == null) {
						DialogMessages.alertErrorWidget(MSG
								.unableToFindMod180());
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
							.unableToReadMod180(caught.getMessage()));
				}
			});
		}
	}

	class Mod180DetailSelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			final Mod180Detail selected = detailModel.getSelectedObject();
			if (modified.containsKey(selected.getId())) {
				populatePerceptor(modified.get(selected.getId()));
			} else {
				mod180Service.getMod180Detail(selected.getId(),
						new AsyncCallback<Mod180Receiver>() {
							@Override
							public void onSuccess(Mod180Receiver perceptor) {
								if (perceptor == null) {
									DialogMessages.alertErrorWidget(MSG.unableToFindMod180Detail(MSG
											.unableToFindPerceptor(selected
													.getId())));
								} else {
									populatePerceptor(perceptor);
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								DialogMessages.alertErrorWidget(MSG
										.unableToFindMod180Detail(caught
												.getMessage()));
							}
						});
			}

		}
	}

	private void populatePerceptor(Mod180Receiver perceptor) {
		receiverDocument.setValue(perceptor.getDocument());
		representativeDocument.setValue(perceptor.getRepresentativeDocument());
		fullName.setValue(perceptor.getName());
		accrualYear.setValue(perceptor.getAccrualYear());
		province.setSelectedIndex(perceptor.getProvince());
		perception.setValue(perceptor.getPerception());
		retention.setValue(perceptor.getRetention());
		percent.setValue(perceptor.getPercent());
		inKind.setValue(perceptor.isInKind());
		restoreDeletedButton.setVisible(perceptor.isDeleted());
		deleteDetailButton.setVisible(!perceptor.isDeleted());
	}

	static class Mod180DetailCell extends AbstractCell<Mod180Detail> {
		@Override
		public void render(Cell.Context context, Mod180Detail value,
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

	private void select(Mod180 selected) {
		mod180 = selected;
		year.setValue(Integer.toString(mod180.getYear()));
		administration.setSelectedIndex(mod180.getAdministration());
		replacement.setValue(mod180.isReplacement());
		confidential.setValue(mod180.isConfidential());
		comments.setValue(mod180.getComments());
		enterpriseSuggest.setValue(mod180.getDocument(), mod180.getName());
		contactPhone.setValue(mod180.getContactPhone());
		contactPerson.setValue(mod180.getContactPerson());
		receipt.setValue(mod180.getReceipt());
		replacedReceipt.setValue(mod180.getReplacedReceipt());
		domain = mod180.getDomain();
		enterprise = mod180.getEnterprise();
		// Toolbar states
		deleteButton.setVisible(mod180.getId() != null);
		newButton.setVisible(mod180.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(mod180.getId() != null);
		printButton.setVisible(mod180.getId() != null);

		pagerPanel.setVisible(mod180.getId() != null);
		perceptorPanel.setVisible(mod180.getId() != null);
		perceptorHeaderPanel.setVisible(mod180.getId() != null);

		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),
				true);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		mod180Service.getMod180s(getCurrentDomain(),
				new AsyncCallback<ArrayList<Mod180>>() {
					@Override
					public void onSuccess(ArrayList<Mod180> result) {
						if (result == null || result.size() == 0) {
							onNewButtonClick(null);
							// int i = deckPanel.getWidgetIndex(formPanel);
							// deckPanel.showWidget(i);
							// mod180 = new Mod180();
							// mod180.setDomain(getCurrentDomain());
							// year.setFocus(true);
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
						DialogMessages.alertErrorWidget(MSG
								.unableToReadMod180(caught.getMessage()));
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

		populateMod180();
		mod180Service.saveMod180(this.mod180, new ArrayList<Mod180Receiver>(
				modified.values()), new AsyncCallback<Mod180>() {
			@Override
			public void onSuccess(Mod180 result) {
				modified = new HashMap<Integer, Mod180Receiver>();
				select(result);
				popup.hide();
				cleanErrorMessage();
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				addErrorMessage(MSG.unableToSaveMod180(caught.getMessage()));
			}
		});
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(MSG.confirmDeleteAction())) {
			mod180Service.deleteMod180(this.mod180, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					select(new Mod180());
					table.setVisibleRangeAndClearData(table.getVisibleRange(),
							true);
				}

				@Override
				public void onFailure(Throwable caught) {
					DialogMessages.alertErrorWidget(MSG
							.unableToDeleteMod180(caught.getMessage()));
				}
			});
		}
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		cleanErrorMessage();
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),
				true);
		modified = new HashMap<Integer, Mod180Receiver>();
		mod180Service.getFiscalParameters(getCurrentDomain(),
				new AsyncCallback<FiscalParameters>() {
					@Override
					public void onSuccess(FiscalParameters params) {
						Mod180 mod180 = new Mod180();
						enterprise = params.getCompany();
						domain = getCurrentDomain();
						mod180.setEnterprise(params.getCompany());
						mod180.setDomain(getCurrentDomain());
						mod180.setDocument(params.getDocument());
						mod180.setName(params.getName());
						mod180.setYear(params.getDefaultYear() != null ? params
								.getDefaultYear() : 2013);
						mod180.setAdministration(params.getAdministration() != null ? params
								.getAdministration() : 4);
						mod180.setContactPerson(params.getContactPerson());
						mod180.setContactPhone(params.getContactPhone());
						select(mod180);

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
		modified = new HashMap<Integer, Mod180Receiver>();
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
		final Mod180Receiver perceptor = new Mod180Receiver();
		perceptor.setId(newKey);
		modified.put(newKey, perceptor);

		ArrayList<Mod180Detail> list = new ArrayList<Mod180Detail>();
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
			public void perceptorChanged(Mod180Receiver p) {
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
			public void perceptorChanged(Mod180Receiver p) {
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
		mod180.setDomain(domain);
		enterprise = enterpriseSuggest.getEnterpriseId();
		mod180.setEnterprise(enterprise);
		mod180.setName(enterpriseSuggest.getName().getValue());
		year.selectAll();
		year.setFocus(true);
	}

	private void populateMod180() {
		try {
			mod180.setYear(Integer.parseInt(year.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(MSG.unableToParseYear());
		}
		mod180.setDomain(domain);
		mod180.setEnterprise(enterprise);
		mod180.setAdministration(administration.getSelectedIndex());
		mod180.setReplacement(replacement.getValue());
		mod180.setConfidential(confidential.getValue());
		mod180.setComments(comments.getValue());
		mod180.setDocument(enterpriseSuggest.getValue());
		mod180.setName(enterpriseSuggest.getName().getValue());
		mod180.setContactPhone(contactPhone.getValue());
		mod180.setContactPerson(contactPerson.getValue());
		mod180.setReceipt(receipt.getValue());
		mod180.setReplacedReceipt(replacedReceipt.getValue());
	}

	class Mod180DetailDataProvider extends AsyncDataProvider<Mod180Detail> {

		public Mod180DetailDataProvider(
				ProvidesKey<Mod180Detail> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod180Detail> display) {
			if (mod180 != null && mod180.getId() != null) {
				detailModel.setSelected(detailModel.getSelectedObject(), false);
				mod180Service.getMod180DetailByMod180(mod180.getId(), 0,
						Integer.MAX_VALUE,
						new AsyncCallback<ArrayList<Mod180Detail>>() {

							@Override
							public void onSuccess(ArrayList<Mod180Detail> result) {
								int selectIdx = 0;

								if (result.size() == 0) {
									Mod180Receiver perceptor = new Mod180Receiver();
									perceptor.setId(-1);
									modified.put(-1, perceptor);
								}
								for (Mod180Detail det : modified.values()) {
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
										.unableToReadMod180(caught.getMessage()));
							}
						});
			}
		}
	}

	private Mod180Receiver getModifiedPerceptor(final IPerceptorChanged pc) {
		final Mod180Detail selected = detailModel.getSelectedObject();
		if (!modified.containsKey(selected.getId())) {
			mod180Service.getMod180Detail(selected.getId(),
					new AsyncCallback<Mod180Receiver>() {
						@Override
						public void onSuccess(Mod180Receiver per) {
							if (per == null) {
								DialogMessages.alertErrorWidget(MSG
										.unableToFindMod180Detail(MSG
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
									.unableToFindMod180Detail(caught
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
		void perceptorChanged(Mod180Receiver p);
	}

	@UiHandler("receiverDocument")
	void onChangeReceiverDocument(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Receiver p) {
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
			public void perceptorChanged(Mod180Receiver p) {
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
			public void perceptorChanged(Mod180Receiver p) {
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
			public void perceptorChanged(Mod180Receiver p) {
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
			public void perceptorChanged(Mod180Receiver p) {
				p.setProvince(province.getSelectedIndex());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("perception")
	void onChangePerception(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Receiver p) {
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
			public void perceptorChanged(Mod180Receiver p) {
				p.setRetention(retention.getDoubleValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("percent")
	void onChangePercent(ChangeEvent event) {
		double ret = retention.getDoubleValue();
		boolean retChanged = false;
		if (ret == 0) {
			ret = AonUtil.round(perception.getDoubleValue()
					* percent.getDoubleValue() / 100);
			retention.setValue(ret);
			retChanged = true;
		}
		final boolean retentionChanged = retChanged;		
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Receiver p) {
				p.setPercent(percent.getDoubleValue());
				if (retentionChanged) {
					p.setRetention(retention.getDoubleValue());
				}
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("inKind")
	void onChangeInKind(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Receiver p) {
				p.setInKind(inKind.getValue());
				p.setDirty(true);
				detailModel.getSelectedObject().setDirty(true);
			}
		});
	}

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL() + "/aon_gwt_fiscal/Model180File");
		mod180Hidden.setValue(String.valueOf(mod180.getId()));
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL() + "/aon_gwt_fiscal/Model180Print");
		mod180Hidden.setValue(String.valueOf(mod180.getId()));
		diskForm.submit();
	}
	
}
