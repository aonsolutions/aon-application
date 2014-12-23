package com.esferalia.aon.gwt.fiscal.client.mod190;

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
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190Detail2014.ICallBack;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
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

public class Model190 extends MainEntryPoint {

	public static final ProvidesKey<Mod190Detail> MOD190_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod190Detail>() {
		@Override
		public Object getKey(Mod190Detail mod190Detail) {
			return mod190Detail == null ? null : mod190Detail.getId();
		}
	};

	interface Model190Binder extends UiBinder<Widget, Model190> {
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
			String newLabel = MSG.newPerceptor() + " ("
					+ (value.getId() * (-1)) + ")";
			sb.appendEscaped(AonUtil.isEmpty(value.getName()) ? newLabel
					: value.getName());

			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("</div>");
			}
		}
	}

	static interface IModel190Detail extends HasVisibility {
		public void populatePerceptor(Mod190Detail perceptor);
	}

	private static final Model190Binder MODEL_190_BINDER = GWT
			.create(Model190Binder.class);

	private Mod190 currentMod190;
	private FiscalServiceAsync mod190Service;
	protected final static CommonMessages MSG = GWT.create(CommonMessages.class);
	protected final static AonResources AON_RESOURCES = GWT.create(AonResources.class);
	final static DataGrid.Resources DATA_GRID_STYLE = GWT.create(AonDataGrid.class);
	
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
	Model190Table table;

	private Mod190DetailDataProvider dataProvider;
	private CellList<Mod190Detail> detailList;
	private SingleSelectionModel<Mod190Detail> detailModel;

	private int domain;
	private int enterprise;

	@UiField
	ShowMorePagerPanel pagerPanel;
	@UiField
	Model190Detail2014 perceptorPanel;

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
	Hidden mod190Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		AON_RESOURCES.css().ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync mod190ServiceRaw = GWT.create(FiscalService.class);
		mod190Service = new FiscalServiceAsyncDecorator(mod190ServiceRaw);
		
		table = new Model190Table(new Mod190SelectionHandler());

		Mod190DetailCell mod190DetailCell = new Mod190DetailCell();
		CellList.Resources cellListStyle = GWT.create(AonCellList.class);
		detailList = new CellList<Mod190Detail>(mod190DetailCell,cellListStyle, MOD190_DETAIL_PROVIDES_KEY);
		detailList.setStylePrimaryName(DATA_GRID_STYLE.dataGridStyle().dataGridWidget());
		detailList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		detailModel = new SingleSelectionModel<Mod190Detail>(MOD190_DETAIL_PROVIDES_KEY);
		detailModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				final Mod190Detail selected = detailModel.getSelectedObject();
				perceptorPanel.setDetail(selected);
			}
			
		});
		detailList.setSelectionModel(detailModel);
		detailList.setEmptyListWidget(new HTML(MSG.noData()));
		
		dataProvider = new Mod190DetailDataProvider(MOD190_DETAIL_PROVIDES_KEY);
		dataProvider.addDataDisplay(detailList);
		detailList.setVisible(true);

		Widget ui = MODEL_190_BINDER.createAndBindUi(this);
		perceptorPanel.setCallback(new ICallBack() {

			@Override
			public void redrawList(Mod190Detail detail) {
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
		mod190Hidden = new Hidden("mod190");
		formFlowPanel.add(mod190Hidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		
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

	class Mod190SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod190 sel = table.getSelected();
			mod190Service.getMod190(getCurrentDomainName(), getCurrentDomain()
					,sel.getId(), new AsyncCallback<Mod190>() {
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

	private void select(Mod190 selected) {
		currentMod190 = selected;
		year.setValue(Integer.toString(currentMod190.getYear()));
		administration.setSelectedIndex(currentMod190.getAdministration());
		replacement.setValue(currentMod190.isReplacement());
		confidential.setValue(currentMod190.isConfidential());
		comments.setValue(currentMod190.getComments());
		enterpriseSuggest.setValue(currentMod190.getDocument(), currentMod190.getName());
		contactPhone.setValue(currentMod190.getContactPhone());
		contactPerson.setValue(currentMod190.getContactPerson());
		receipt.setValue(currentMod190.getReceipt());
		replacedReceipt.setValue(currentMod190.getReplacedReceipt());
		domain = currentMod190.getDomain();
		enterprise = currentMod190.getEnterprise();
		replacementPanel.setVisible(currentMod190.isReplacement());
		pagerPanel.setVisible(currentMod190.getId() != null);
		perceptorPanel.setVisible(currentMod190.getId() != null);
		perceptorHeaderPanel.setVisible(currentMod190.getId() != null);
		// Toolbar states
		deleteButton.setVisible(currentMod190.getId() != null);
		newButton.setVisible(currentMod190.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(currentMod190.getId() != null);
		printButton.setVisible(currentMod190.getId() != null);

		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		mod190Service.getMod190s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<ArrayList<Mod190>>() {
					@Override
					public void onSuccess(ArrayList<Mod190> result) {
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
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(MSG.unableToReadMod190(caught
								.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		if (AonUtil.isEmpty(year.getValue())) {
			throw new IllegalArgumentException(MSG.requiredField(MSG
					.fiscalYear()));
		}

		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(AON_RESOURCES.css().aonTimer());
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
						showErrorMessage(MSG.unableToSaveMod190(caught.getMessage()));
					}
				});
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(MSG.confirmDeleteAction())) {
			mod190Service.deleteMod190(getCurrentDomainName(), getCurrentDomain()
					,this.currentMod190, new AsyncCallback<Void>() {
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
		mod190Service.initializeMod190(getCurrentDomainName(), getCurrentDomain(),2014 ,
				new AsyncCallback<Mod190>() {
			@Override
			public void onSuccess(Mod190 m190) {
				select(m190);
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
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),
				true);
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	@UiHandler("newDetailButton")
	void onNewDetailButtonClick(ClickEvent event) {
		int newKey = (currentMod190.getDetails().size() + 1) * (-1);
		final Mod190Detail perceptor = new Mod190Detail();
		perceptor.setId(newKey);
		perceptor.setKey("A");
		currentMod190.getDetails().add(perceptor);
		perceptorPanel.setDetail(perceptor);
		detailList.setRowCount(detailList.getRowCount() + 1);
		detailList.setPageSize(detailList.getRowCount());
		selectInList(currentMod190.getDetails().size() - 1);
		detailList.redraw();
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
		try {
			currentMod190.setYear(Integer.parseInt(year.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(MSG.unableToParseYear());
		}
		currentMod190.setDomain(domain);
		currentMod190.setEnterprise(enterprise);
		currentMod190.setAdministration((byte) administration.getSelectedIndex());
		currentMod190.setReplacement(replacement.getValue());
		currentMod190.setConfidential(confidential.getValue());
		currentMod190.setComments(comments.getValue());
		currentMod190.setDocument(enterpriseSuggest.getValue());
		currentMod190.setName(enterpriseSuggest.getName().getValue());
		currentMod190.setContactPhone(contactPhone.getValue());
		currentMod190.setContactPerson(contactPerson.getValue());
		currentMod190.setReceipt(receipt.getValue());
		currentMod190.setReplacedReceipt(replacedReceipt.getValue());
	}

	private void selectInList(int i) {
		detailModel.setSelected(currentMod190.getDetails().get(i),true);
		detailList.getRowElement(i).scrollIntoView();
		pagerPanel.scrollToLeft();
	}

	class Mod190DetailDataProvider extends AsyncDataProvider<Mod190Detail> {

		public Mod190DetailDataProvider(
				ProvidesKey<Mod190Detail> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod190Detail> display) {
			if (currentMod190 != null && currentMod190.getId() != null) {
				if (currentMod190.getDetails().size() == 0) {
					onNewDetailButtonClick(null);					
				} else {
					updateRowCount(currentMod190.getDetails().size(), true);
					updateRowData(0, currentMod190.getDetails());
					detailList.setPageSize(currentMod190.getDetails().size());
					selectInList(0);
				}
			}
		}
	}

	@UiHandler("replacement")
	void onChangeReplacement(ClickEvent event) {
		replacementPanel.setVisible(replacement.getValue());
	}

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model190File");
		mod190Hidden.setValue( String.valueOf(currentMod190.getId()) );
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model190Print");
		mod190Hidden.setValue( String.valueOf(currentMod190.getId()) );
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
