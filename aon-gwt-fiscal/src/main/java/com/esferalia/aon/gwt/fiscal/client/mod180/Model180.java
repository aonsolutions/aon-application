package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonCellList;
import com.esferalia.aon.gwt.common.client.css.AonDataGrid;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.ShowMorePagerPanel;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Detail2014.ICallBack;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
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

public class Model180 extends MainEntryPoint {

	public static final ProvidesKey<Mod180Detail> MOD180_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod180Detail>() {
		@Override
		public Object getKey(Mod180Detail mod190Detail) {
			return mod190Detail == null ? null : mod190Detail.getId();
		}
	};

	static interface IModel180Detail extends HasVisibility {
		public void populatePerceptor(Mod180Detail perceptor);
	}

	static FiscalServiceAsync mod180Service;
	
	final static CommonMessages MSG = GWT.create(CommonMessages.class);
	final static AonResources AON_RESOURCES = GWT.create(AonResources.class);
	final static DataGrid.Resources DATA_GRID_STYLE = GWT.create(AonDataGrid.class);
	
	interface Model180Binder extends UiBinder<Widget, Model180> {
	}

	private static final Model180Binder MODEL_180_BINDER = GWT
			.create(Model180Binder.class);

	private Mod180 currentMod180;

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	Panel listPanel;
	@UiField
	Panel perceptorHeaderPanel;
	@UiField
	DockLayoutPanel formPanel;
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	@UiField
	Panel formContainer;

	@UiField(provided = true)
	Model180Table table;

	// private NoSelectionModel<Mod180> model;
	private Mod180DetailDataProvider dataProvider;
	private CellList<Mod180Detail> detailList;
	private SingleSelectionModel<Mod180Detail> detailModel;

	private int domain;
	private int enterprise;

	@UiField
	ShowMorePagerPanel pagerPanel;
	@UiField
	Model180Detail2014 perceptorPanel;

	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
	@UiField
	Button newButton;
	@UiField
	Button cancelButton;
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
	FlowPanel replacementPanel;

	FormPanel diskForm;
	Hidden mod180Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		AON_RESOURCES.css().ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync mod180ServiceRaw = GWT.create(FiscalService.class);
		mod180Service = new FiscalServiceAsyncDecorator(mod180ServiceRaw);

		table = new Model180Table(new Mod180SelectionHandler());

		Mod180DetailCell mod180DetailCell = new Mod180DetailCell();
		CellList.Resources cellListStyle = GWT.create(AonCellList.class);
		detailList = new CellList<Mod180Detail>(mod180DetailCell,cellListStyle, MOD180_DETAIL_PROVIDES_KEY);
		detailList.setStylePrimaryName(DATA_GRID_STYLE.dataGridStyle().dataGridWidget());
		detailList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		detailModel = new SingleSelectionModel<Mod180Detail>(MOD180_DETAIL_PROVIDES_KEY);
		detailModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				final Mod180Detail selected = detailModel.getSelectedObject();
				perceptorPanel.setDetail(selected);
			}
			
		});

		detailList.setSelectionModel(detailModel);
		detailList.setEmptyListWidget(new HTML(MSG.noData()));

		dataProvider = new Mod180DetailDataProvider(MOD180_DETAIL_PROVIDES_KEY);
		dataProvider.addDataDisplay(detailList);
		detailList.setVisible(true);

		Widget ui = MODEL_180_BINDER.createAndBindUi(this);
		perceptorPanel.setCallback(new ICallBack() {

			@Override
			public void redrawList(Mod180Detail detail) {
				detailList.redraw();
			}
		});

		pagerPanel.setDisplay(detailList);
		pagerPanel.setIncrementSize(0);

		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		mod180Hidden = new Hidden("mod180");
		formFlowPanel.add(mod180Hidden);
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
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	class Mod180SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod180 sel = table.getSelected();
			mod180Service.getMod180(getCurrentDomainName(), getCurrentDomain(),
					sel.getId(), new AsyncCallback<Mod180>() {
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
			String newLabel = MSG.newPerceptor() + " ("
					+ (value.getId() * (-1)) + ")";
			sb.appendEscaped(AonUtil.isEmpty(value.getName()) ? newLabel
					: value.getName());

			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("</div>");
			}
		}
	}

	private void select(Mod180 selected) {
		currentMod180 = selected;
		year.setValue(Integer.toString(currentMod180.getYear()));
		administration.setSelectedIndex(currentMod180.getAdministration());
		replacement.setValue(currentMod180.isReplacement());
		confidential.setValue(currentMod180.isConfidential());
		comments.setValue(currentMod180.getComments());
		enterpriseSuggest.setValue(currentMod180.getDocument(), currentMod180.getName());
		contactPhone.setValue(currentMod180.getContactPhone());
		contactPerson.setValue(currentMod180.getContactPerson());
		receipt.setValue(currentMod180.getReceipt());
		replacedReceipt.setValue(currentMod180.getReplacedReceipt());
		domain = currentMod180.getDomain();
		enterprise = currentMod180.getEnterprise();
		// Toolbar states
		deleteButton.setVisible(currentMod180.getId() != null);
		newButton.setVisible(currentMod180.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(currentMod180.getId() != null);
		printButton.setVisible(currentMod180.getId() != null);

		pagerPanel.setVisible(currentMod180.getId() != null);
		perceptorPanel.setVisible(currentMod180.getId() != null);
		perceptorHeaderPanel.setVisible(currentMod180.getId() != null);
		replacementPanel.setVisible(currentMod180.isReplacement());
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		mod180Service.getMod180s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<ArrayList<Mod180>>() {
					@Override
					public void onSuccess(ArrayList<Mod180> result) {
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
						DialogMessages.alertErrorWidget(MSG
								.unableToReadMod180(caught.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		if (AonUtil.isEmpty(year.getValue())) {
			throw new IllegalArgumentException(MSG.requiredField(MSG.fiscalYear()));
		}

		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(AON_RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();

		populateMod180();
		mod180Service.saveMod180(getCurrentDomainName(), getCurrentDomain(),
				this.currentMod180, new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 result) {
						select(result);
						popup.hide();
						cleanErrorMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						showErrorMessage(MSG.unableToSaveMod180(caught.getMessage()));
					}
				});
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(MSG.confirmDeclarationDeleteAction())) {
			mod180Service.deleteMod180(getCurrentDomainName(),
					getCurrentDomain(), this.currentMod180, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							select(new Mod180());
							table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
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
						mod180.setYear(params.getDefaultYear() != null ? params.getDefaultYear() : 2014);
						mod180.setAdministration(params.getAdministration() != null ? params.getAdministration() : 4);
						mod180.setContactPerson(params.getContactPerson());
						mod180.setContactPhone(params.getContactPhone());
						mod180.setDetails(new ArrayList<Mod180Detail>());
						detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
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
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	@UiHandler("newDetailButton")
	void onNewDetailButtonClick(ClickEvent event) {
		int newKey = (currentMod180.getDetails().size() + 1) * (-1);
		final Mod180Detail perceptor = new Mod180Detail();
		perceptor.setId(newKey);
		currentMod180.getDetails().add(perceptor);
		perceptorPanel.setDetail(perceptor);
		detailList.setRowCount(detailList.getRowCount() + 1);
		detailList.setPageSize(detailList.getRowCount());
		selectInList(currentMod180.getDetails().size() - 1);
		detailList.redraw();
	}

	private void selectInList(int i) {
		detailModel.setSelected(currentMod180.getDetails().get(i),true);
		detailList.getRowElement(i).scrollIntoView();
		pagerPanel.scrollToLeft();
		
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> event) {
		domain = enterpriseSuggest.getDomainId();
		currentMod180.setDomain(domain);
		enterprise = enterpriseSuggest.getEnterpriseId();
		currentMod180.setEnterprise(enterprise);
		currentMod180.setName(enterpriseSuggest.getName().getValue());
		year.selectAll();
		year.setFocus(true);
	}

	private void populateMod180() {
		try {
			currentMod180.setYear(Integer.parseInt(year.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(MSG.unableToParseYear());
		}
		currentMod180.setDomain(domain);
		currentMod180.setEnterprise(enterprise);
		currentMod180.setAdministration(administration.getSelectedIndex());
		currentMod180.setReplacement(replacement.getValue());
		currentMod180.setConfidential(confidential.getValue());
		currentMod180.setComments(comments.getValue());
		currentMod180.setDocument(enterpriseSuggest.getValue());
		currentMod180.setName(enterpriseSuggest.getName().getValue());
		currentMod180.setContactPhone(contactPhone.getValue());
		currentMod180.setContactPerson(contactPerson.getValue());
		currentMod180.setReceipt(receipt.getValue());
		currentMod180.setReplacedReceipt(replacedReceipt.getValue());
	}

	class Mod180DetailDataProvider extends AsyncDataProvider<Mod180Detail> {

		public Mod180DetailDataProvider(
				ProvidesKey<Mod180Detail> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod180Detail> display) {
			if (currentMod180 != null && currentMod180.getId() != null) {
				if (currentMod180.getDetails().size() == 0) {
					onNewDetailButtonClick(null);					
				} else {
					updateRowCount(currentMod180.getDetails().size(), true);
					updateRowData(0, currentMod180.getDetails());
					detailList.setPageSize(currentMod180.getDetails().size());
					selectInList(0);
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
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model180File");
		mod180Hidden.setValue(String.valueOf(currentMod180.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model180Print");
		mod180Hidden.setValue(String.valueOf(currentMod180.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}


	//*****************************************************************************
	//*****************************************************************************
	//*****************************************************************************
	//*****************************************************************************
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
