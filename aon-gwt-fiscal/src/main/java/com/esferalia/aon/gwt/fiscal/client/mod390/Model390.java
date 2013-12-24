package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.ArrayList;

import com.esferalia.aon.gwt.fiscal.client.DialogMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.esferalia.aon.gwt.fiscal.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.gwt.fiscal.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model390 extends MainEntryPoint {

	interface Model390Binder extends UiBinder<Widget, Model390> {
	}

	private static final Model390Binder MODEL_390_BINDER = GWT
			.create(Model390Binder.class);

	private Mod390 mod390;
	private FiscalServiceAsync fiscalService;
	private final static FiscalMessages MSG = GWT.create(FiscalMessages.class);
	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	DeckPanel pagesPanel;
	
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
	@UiField
	Button printButton;

	@UiField
	TextBox year;
	@UiField
	EnterpriseSuggestBox enterpriseSuggest;
	@UiField
	CheckBox replacement;
	@UiField
	TextBox replacedReceipt;

	FormPanel diskForm;
	Hidden mod190Hidden;
	
	
	// PAGE 1
	@UiField
	CheckBox taxRefund;
	@UiField
	CheckBox specialGroupRegime;
	@UiField
	TextBox groupNumber;
	@UiField
	CheckBox groupDependent;
	@UiField
	CheckBox groupDeclarations;
	
	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		CellTable.Resources tableStyle = GWT.create(AonCellTable.class);

		table = new CellTable<Mod390>(1, tableStyle, Mod390.PROVIDES_KEY);
		table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		addSelectorColumn();
		addYearColumn();
		addReplacementColumn();
		addDocumentColumn();
		addNameColumn();

		model = new NoSelectionModel<Mod390>(Mod390.PROVIDES_KEY);
		model.addSelectionChangeHandler(new Mod390SelectionHandler());
		table.setSelectionModel(model);
		table.setEmptyTableWidget(new HTML(MSG.noData()));

		// Create the UI defined in Employee.ui.xml.
		Widget ui = MODEL_390_BINDER.createAndBindUi(this);

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

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	private void addNameColumn() {
		final TextColumn<Mod390> nameColumn = new TextColumn<Mod390>() {
			@Override
			public String getValue(Mod390 mod390) {
				return mod390.getName();
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
			fiscalService.getMod390(sel.getId(), new AsyncCallback<Mod390>() {
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
		enterpriseSuggest.setValue(m390.getDocument(), m390.getName());
		replacement.setValue(m390.isReplacement());
		replacedReceipt.setValue(m390.getReplacedReceipt());
		
		taxRefund.setValue(m390.isTaxRefund());
		specialGroupRegime.setValue(m390.isSpecialGroupRegime(),true);
		groupNumber.setValue(m390.getGroupNumber()==null?null:Integer.toString(m390.getGroupNumber()));
		groupDependent.setValue(m390.isGroupDependent());
		onClickSpecialGroupRegime(null);
		groupDeclarations.setValue(m390.isGroupDeclarations());
		
		
		// Toolbar states
		deleteButton.setVisible(mod390.getId() != null);
		newButton.setVisible(mod390.getId() != null);
		cancelButton.setVisible(table.getRowCount() > 0);
		saveButton.setVisible(true);
		generateFileButton.setVisible(mod390.getId() != null);
		printButton.setVisible(mod390.getId() != null);

	}

	@UiHandler("table")
	void onTableRangeChange(RangeChangeEvent event) {
		fiscalService.getMod390s(getCurrentDomain(),
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
							printButton.setVisible(false);
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
		if (AonUtil.isEmpty(year.getValue())) {
			throw new IllegalArgumentException(MSG.requiredField(MSG
					.fiscalYear()));
		}

		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(MSG.processing());
		label.addStyleName(RESOURCES.css().aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();

		populateMod190();
		fiscalService.saveMod390(this.mod390, new AsyncCallback<Mod390>() {
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
						mod390.setName(params.getName());
						mod390.setYear(params.getDefaultYear() != null ? params
								.getDefaultYear() : 2013);
						mod390.setContactPhone(params.getContactPhone());
						
						select(mod390);

						int i = deckPanel.getWidgetIndex(formPanel);
						deckPanel.showWidget(i);
						i = pagesPanel.getWidgetIndex(panel1);
						pagesPanel.showWidget(i);
						
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
		mod390.setDomain(domain);
		enterprise = enterpriseSuggest.getEnterpriseId();
		mod390.setEnterprise(enterprise);
		mod390.setName(enterpriseSuggest.getName().getValue());
		year.selectAll();
		year.setFocus(true);
	}

	private void populateMod190() {
		try {
			mod390.setYear(Integer.parseInt(year.getValue()));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(MSG.unableToParseYear());
		}
		mod390.setDomain(domain);
		mod390.setEnterprise(enterprise);
		mod390.setDocument(enterpriseSuggest.getValue());
		mod390.setName(enterpriseSuggest.getName().getValue());
	}

	// -------------------------------------------------------------- UiHandler

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model390File");
		mod190Hidden.setValue(String.valueOf(mod390.getId()));
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model390Print");
		mod190Hidden.setValue(String.valueOf(mod390.getId()));
		diskForm.submit();
	}

	private void clearLinks() {
		Panel[] panels = new Panel[] { linkPage1, linkPage3, linkPage4,
				linkPage5, linkPage6, linkPage7, linkPage8, linkPage9,
				linkPage10, linkPage11, linkPage12, linkPage13 };
		for (Panel p : panels) {
			p.getElement().getStyle().setBackgroundColor("");
			p.getElement().getStyle().setColor("");
		}
		
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
	
	@UiHandler("specialGroupRegime")
	void onClickSpecialGroupRegime(ClickEvent event) {
		groupNumber.setEnabled(specialGroupRegime.getValue());
		groupDependent.setEnabled(specialGroupRegime.getValue());
		if (!specialGroupRegime.getValue()) {
			groupNumber.setValue(null);
			groupDependent.setValue(false);
		}
	}
	
}
