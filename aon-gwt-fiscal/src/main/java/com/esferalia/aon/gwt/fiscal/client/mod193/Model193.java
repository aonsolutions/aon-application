package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonCellList;
import com.esferalia.aon.gwt.common.client.css.AonDataGrid;
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
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193Detail2014.ICallBack;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
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

public class Model193 extends MainEntryPoint {

	public static final ProvidesKey<Mod193Detail> MOD193_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod193Detail>() {
		@Override
		public Object getKey(Mod193Detail mod193Detail) {
			return mod193Detail == null ? null : mod193Detail.getId();
		}
	};

	interface Model193Binder extends UiBinder<Widget, Model193> {
	}

	static class Mod193DetailCell extends AbstractCell<Mod193Detail> {
		@Override
		public void render(Cell.Context context, Mod193Detail value,
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
			String newLabel = AON.MSG.newPerceptor() + " ("
					+ (value.getId() * (-1)) + ")";
			sb.appendEscaped(AonUtil.isEmpty(value.getName()) ? newLabel
					: value.getName());

			if (value.isDirty() || value.isDeleted()) {
				sb.appendHtmlConstant("</div>");
			}
		}
	}

	static interface IModel193Detail extends HasVisibility {
		public void populatePerceptor(Mod193Detail perceptor);
	}

	private static final Model193Binder MODEL_193_BINDER = GWT
			.create(Model193Binder.class);

	private Mod193 currentMod193;
	private FiscalServiceAsync mod193Service;
	
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
	Model193Table table;

	private Mod193DetailDataProvider dataProvider;
	private CellList<Mod193Detail> detailList;
	private SingleSelectionModel<Mod193Detail> detailModel;

	private int domain;
	private int enterprise;

	@UiField
	ShowMorePagerPanel pagerPanel;
	@UiField
	Model193Detail2014 perceptorPanel;

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
	Hidden mod193Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync mod193ServiceRaw = GWT.create(FiscalService.class);
		mod193Service = new FiscalServiceAsyncDecorator(mod193ServiceRaw);
		
		table = new Model193Table(new Mod193SelectionHandler());

		Mod193DetailCell mod193DetailCell = new Mod193DetailCell();
		CellList.Resources cellListStyle = GWT.create(AonCellList.class);
		detailList = new CellList<Mod193Detail>(mod193DetailCell,cellListStyle, MOD193_DETAIL_PROVIDES_KEY);
		detailList.setStylePrimaryName(DATA_GRID_STYLE.dataGridStyle().dataGridWidget());
		detailList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		detailModel = new SingleSelectionModel<Mod193Detail>(MOD193_DETAIL_PROVIDES_KEY);
		detailModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				final Mod193Detail selected = detailModel.getSelectedObject();
				perceptorPanel.setDetail(selected);
			}
			
		});
		detailList.setSelectionModel(detailModel);
		detailList.setEmptyListWidget(new HTML(AON.MSG.noData()));
		
		dataProvider = new Mod193DetailDataProvider(MOD193_DETAIL_PROVIDES_KEY);
		dataProvider.addDataDisplay(detailList);
		detailList.setVisible(true);

		Widget ui = MODEL_193_BINDER.createAndBindUi(this);
		perceptorPanel.setCallback(new ICallBack() {

			@Override
			public void redrawList(Mod193Detail detail) {
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
		mod193Hidden = new Hidden("mod193");
		formFlowPanel.add(mod193Hidden);
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

	class Mod193SelectionHandler implements SelectionChangeEvent.Handler {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Mod193 sel = table.getSelected();
			mod193Service.getMod193(getCurrentDomainName(), getCurrentDomain()
					,sel.getId(), new AsyncCallback<Mod193>() {
				@Override
				public void onSuccess(Mod193 selected) {
					if (selected == null) {
						DialogMessages.alertErrorWidget(AON.MSG.unableToFindMod193());
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
					DialogMessages.alertErrorWidget(AON.MSG.unableToReadMod193(caught
							.getMessage()));
				}
			});
		}
	}

	private void select(Mod193 selected) {
		currentMod193 = selected;
		year.setValue(Integer.toString(currentMod193.getYear()));
		administration.setSelectedIndex(currentMod193.getAdministration());
		replacement.setValue(currentMod193.isReplacement());
		confidential.setValue(currentMod193.isConfidential());
		comments.setValue(currentMod193.getComments());
		enterpriseSuggest.setValue(currentMod193.getDocument(), currentMod193.getName());
		contactPhone.setValue(currentMod193.getContactPhone());
		contactPerson.setValue(currentMod193.getContactPerson());
		receipt.setValue(currentMod193.getReceipt());
		replacedReceipt.setValue(currentMod193.getReplacedReceipt());
		domain = currentMod193.getDomain();
		enterprise = currentMod193.getEnterprise();
		replacementPanel.setVisible(currentMod193.isReplacement());
		pagerPanel.setVisible(currentMod193.getId() != null);
		perceptorPanel.setVisible(currentMod193.getId() != null);
		perceptorHeaderPanel.setVisible(currentMod193.getId() != null);
		// Toolbar states
		deleteButton.setVisible(currentMod193.getId() != null);
		newButton.setVisible(currentMod193.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(currentMod193.getId() != null);
		printButton.setVisible(currentMod193.getId() != null);

		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		mod193Service.getMod193s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<ArrayList<Mod193>>() {
					@Override
					public void onSuccess(ArrayList<Mod193> result) {
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
						DialogMessages.alertErrorWidget(AON.MSG.unableToReadMod193(caught
								.getMessage()));
					}
				});
	}

	@UiHandler("saveButton")
	void onAcceptButtonClick(ClickEvent event) {
		if (AonUtil.isEmpty(year.getValue())) {
			throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG
					.fiscalYear()));
		}

		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();

		populateMod193();
		mod193Service.saveMod193(getCurrentDomainName(), getCurrentDomain()
				,this.currentMod193, new AsyncCallback<Mod193>() {
					@Override
					public void onSuccess(Mod193 result) {
						select(result);
						popup.hide();
						cleanErrorMessage();
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						showErrorMessage(AON.MSG.unableToSaveMod193(caught.getMessage()));
					}
				});
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(AON.MSG.confirmDeleteAction())) {
			mod193Service.deleteMod193(getCurrentDomainName(), getCurrentDomain()
					,this.currentMod193, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					select(new Mod193());
					table.setVisibleRangeAndClearData(table.getVisibleRange(),
							true);
				}

				@Override
				public void onFailure(Throwable caught) {
					DialogMessages.alertErrorWidget(AON.MSG.unableToDeleteMod193(caught
							.getMessage()));
				}
			});
		}
	}

	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		cleanErrorMessage();
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
		mod193Service.initializeMod193(getCurrentDomainName(), getCurrentDomain(),2014 ,
				new AsyncCallback<Mod193>() {
			@Override
			public void onSuccess(Mod193 m193) {
				select(m193);
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
		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),
				true);
		int i = deckPanel.getWidgetIndex(listPanel);
		deckPanel.showWidget(i);
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}

	@UiHandler("newDetailButton")
	void onNewDetailButtonClick(ClickEvent event) {
		int newKey = (currentMod193.getDetails().size() + 1) * (-1);
		final Mod193Detail perceptor = new Mod193Detail();
		perceptor.setId(newKey);
		perceptor.setKey("A");
		perceptor.setType( Mod193Detail.DETAIL_TYPE);
		currentMod193.getDetails().add(perceptor);
		perceptorPanel.setDetail(perceptor);
		detailList.setRowCount(detailList.getRowCount() + 1);
		detailList.setPageSize(detailList.getRowCount());
		selectInList(currentMod193.getDetails().size() - 1);
		detailList.redraw();
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> event) {
		domain = enterpriseSuggest.getDomainId();
		currentMod193.setDomain(domain);
		enterprise = enterpriseSuggest.getEnterpriseId();
		currentMod193.setEnterprise(enterprise);
		currentMod193.setName(enterpriseSuggest.getName().getValue());
		year.selectAll();
		year.setFocus(true);
	}

	private void populateMod193() {
		try {
			currentMod193.setYear(Integer.parseInt(year.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(AON.MSG.unableToParseYear());
		}
		currentMod193.setDomain(domain);
		currentMod193.setEnterprise(enterprise);
		currentMod193.setAdministration((byte) administration.getSelectedIndex());
		currentMod193.setReplacement(replacement.getValue());
		currentMod193.setConfidential(confidential.getValue());
		currentMod193.setComments(comments.getValue());
		currentMod193.setDocument(enterpriseSuggest.getValue());
		currentMod193.setName(enterpriseSuggest.getName().getValue());
		currentMod193.setContactPhone(contactPhone.getValue());
		currentMod193.setContactPerson(contactPerson.getValue());
		currentMod193.setReceipt(receipt.getValue());
		currentMod193.setReplacedReceipt(replacedReceipt.getValue());
	}

	private void selectInList(int i) {
		detailModel.setSelected(currentMod193.getDetails().get(i),true);
		detailList.getRowElement(i).scrollIntoView();
		pagerPanel.scrollToLeft();
	}

	class Mod193DetailDataProvider extends AsyncDataProvider<Mod193Detail> {

		public Mod193DetailDataProvider(
				ProvidesKey<Mod193Detail> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod193Detail> display) {
			if (currentMod193 != null && currentMod193.getId() != null) {
				if (currentMod193.getDetails().size() == 0) {
					onNewDetailButtonClick(null);					
				} else {
					updateRowCount(currentMod193.getDetails().size(), true);
					updateRowData(0, currentMod193.getDetails());
					detailList.setPageSize(currentMod193.getDetails().size());
					selectInList(0);
					perceptorPanel.setDetail(detailModel.getSelectedObject());
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
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos de la declaraci\u00F3n, para su \n"
				+ "presentaci\u00F3n en Hacienda.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model193File");
		mod193Hidden.setValue( String.valueOf(currentMod193.getId()) );
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
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model193Print");
		mod193Hidden.setValue( String.valueOf(currentMod193.getId()) );
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
