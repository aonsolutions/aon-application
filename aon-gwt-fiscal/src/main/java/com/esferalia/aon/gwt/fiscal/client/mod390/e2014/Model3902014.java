package com.esferalia.aon.gwt.fiscal.client.mod390.e2014;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.mod390.ErrorPage;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Model390Callback;
import com.esferalia.aon.gwt.fiscal.client.mod390.ValidationMessage;
import com.esferalia.aon.gwt.fiscal.client.mod390.ValidationMessage.ValidationMessages;
import com.esferalia.aon.gwt.fiscal.client.widget.EnterpriseSuggestBox;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class Model3902014 extends ResizeComposite {
	
	private static final Integer DEFAULT_YEAR = 2014;

	interface Model390Binder extends UiBinder<Widget, Model3902014> {
	}

	private static final Model390Binder MODEL_390_BINDER = GWT.create(Model390Binder.class);

	private Mod3902014 mod390;
	private FiscalServiceAsync FISCAL_SERVICE;
	private Mod3902014ServiceAsync MOD390_SERVICE;
	
	private Model390Callback mod390CallBack;

	static interface IMod3902014CallBack {
		void calculateAndRefresh();
	}	

	IMod3902014CallBack callback = new IMod3902014CallBack() {

		@Override
		public void calculateAndRefresh() {
			mod390.calculate();
			((WestFocusPanel) linkContainer.getWidget(3)).setValue(mod390);
			((WestFocusPanel) linkContainer.getWidget(4)).setValue(mod390);
			((WestFocusPanel) linkContainer.getWidget(5)).setValue(mod390);
			((WestFocusPanel) linkContainer.getWidget(6)).setValue(mod390);
			((WestFocusPanel) linkContainer.getWidget(7)).setValue(mod390);
			((WestFocusPanel) linkContainer.getWidget(8)).setValue(mod390);
		}
		
	};
	
	static interface IMod3902014Page extends IsWidget {
		void setValue(Mod3902014 m390);
		void populate(Mod3902014 m390);
		void setCallback( IMod3902014CallBack callback );
	}	
	
	@UiField
	Label simplifiedRegime;

	private int domain;
	private int enterprise;

	@UiField
	DockLayoutPanel dockLayoutPanel;
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
	ResultsPanel resultsPanel;
	ErrorPage errorPage;
	
	@UiField
	MinimizePanel footPanel;
	@UiField
	DeckPanel pagesPanel;

	@UiField
	FlowPanel linkContainer;
	
	@UiField
	Panel formContainer;

	@UiField
	EnterpriseSuggestBox enterpriseSuggest;

	FormPanel diskForm;
	Hidden mod390Hidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;

	public Model3902014(final Model390Callback mod390CallBack) {
		this.mod390CallBack = mod390CallBack;
		
		AON.ensureInjected();

		
		Mod3902014ServiceAsync mod3902014ServiceRaw = GWT.create(Mod3902014Service.class);
		MOD390_SERVICE = new Mod3902014ServiceAsyncDecorator(mod3902014ServiceRaw);
		
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		// Create the UI defined in Employee.ui.xml.
		Widget ui = MODEL_390_BINDER.createAndBindUi(this);
		
		linkContainer.add(new WestFocusPanel(AON.MSG.pasiveSubjectAndAccrual(), new Page00(), callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.stadisticalData(), new Page01(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.representativeData(), new Page02(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.generalRegimeOperations(), new Page03(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.simplifiedRegimeOperations(), new Page04(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.annualLiquidationResult(), new Page05(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.taxByTerritory(), new Page06(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.liquidationsResult(), new Page07(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.operationsVolume(), new Page08(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.specificOperations(), new Page09(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.prorrata(), new Page10(),callback));
		linkContainer.add(new WestFocusPanel(AON.MSG.difActivitiesRegime(), new Page11(),callback));

		errorPage = new ErrorPage();
		errorPage.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				ValidationMessage v =  errorPage.getSelected();
				if (v != null) {
					showPage(v.getPage());
				}
			}
		});
		
		domain = getCurrentDomain();
		
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
		
		initWidget(ui);
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	private void refreshPages(Mod3902014 m390) {
		for (int i = 0 ; i < linkContainer.getWidgetCount(); i ++) {
			WestFocusPanel page = (WestFocusPanel) linkContainer.getWidget(i);
			page.setValue(m390);	
		}
	}
	private void showPage(int page) {
		WestFocusPanel panel = (WestFocusPanel) linkContainer.getWidget(page);
		panel.showPage();	
	}
	
	public void select(Mod390 m390) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		try {
			MOD390_SERVICE.getMod3902014(getCurrentDomainName(), getCurrentDomain(),m390, new AsyncCallback<Mod3902014>() {
				@Override
				public void onSuccess(Mod3902014 selected) {
					if (selected == null) {
						showErrorMessage(AON.MSG.unableToFindDeclaration());
					} else {
						select(selected);
					}
					popup.hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					popup.hide();
					DialogMessages.alertErrorWidget(AON.MSG.unableToReadDeclaration(caught.getMessage()));
				}
			});
		} catch (IllegalArgumentException e) {
			popup.hide();
			showErrorMessage(e.getMessage());
		}
	}
	public void select(Mod3902014 m390) {
		mod390 = m390;
		cleanErrorMessage();		
		WestFocusPanel wfp = (WestFocusPanel) linkContainer.getWidget(0);
		wfp.showPage();
		enterprise = m390.getEnterprise();
		domain = m390.getDomain();
		enterpriseSuggest.setValue(mod390.getDocument(), m390.getEnterpriseName());
		refreshPages(mod390);
		// Toolbar states
		deleteButton.setVisible(mod390.getId() != null);
		newButton.setVisible(mod390.getId() != null);
		//cancelButton.setVisible(table.getRowCount() > 0);
		cancelButton.setVisible(true);
		saveButton.setVisible(true);
		generateFileButton.setVisible(mod390.getId() != null);
		printButton.setVisible(mod390.getId() != null);
		simplifiedRegime.setVisible(mod390.isSimplifiedRegime());
		paintHeaderTable();
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
			cleanErrorMessage();
			validate(this.mod390);
			MOD390_SERVICE.saveMod3902014(getCurrentDomainName(),getCurrentDomain(),this.mod390
					, new AsyncCallback<Mod3902014>() {
						@Override
						public void onSuccess(Mod3902014 result) {
							select(result);
							popup.hide();
						}

						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							showErrorMessage(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
		} catch (IllegalArgumentException e) {
			popup.hide();
			showErrorMessage(e.getMessage());
		}
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm(AON.MSG.confirmDeleteAction())) {
			MOD390_SERVICE.deleteMod3902014(getCurrentDomainName(),getCurrentDomain(),
					this.mod390, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					cleanErrorMessage();
					mod390CallBack.onCancel();
				}

				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
				}
			});
		}
	}
	
	@UiHandler("newButton")
	void onNewButtonClick(ClickEvent event) {
		onNew( DEFAULT_YEAR );
	}
	
	public void onNew( int year) {
		cleanErrorMessage();
		Model390.MOD390_SERVICE.initialize(getCurrentDomainName(),domain, year,
				new AsyncCallback<Mod390>() {
			@Override
			public void onSuccess(Mod390 mod390) {
				select( mod390 );
				pagesPanel.showWidget(0);
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
			}
		});
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		cleanErrorMessage();
		mod390CallBack.onCancel();
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> event) {
		domain = enterpriseSuggest.getDomainId();
		onNewButtonClick(null);
	}

	private void populateMod390() {
		mod390.setDomain(domain);
		mod390.setEnterprise(enterprise);
		mod390.setDocument(enterpriseSuggest.getValue());
		mod390.setEnterpriseName(enterpriseSuggest.getName().getValue());
		mod390.setAdministration( Administration.COMMON_TERRITORY);
		mod390.setConfidential(false);
		mod390.setComments(null);
		
		paintHeaderTable();

		// Populate Pages
		for (int i = 0 ; i < linkContainer.getWidgetCount(); i ++) {
			WestFocusPanel page = (WestFocusPanel) linkContainer.getWidget(i);
			page.populate(mod390);	
		}
	}

	// -------------------------------------------------------------- UiHandler

	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		Window.alert("Se va a proceder a la generaci\u00F3n del fichero.\n"
				+ " Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n"
				+ " El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL()
				+ "/aon_gwt_fiscal/Model3902014File");
		mod390Hidden.setValue(String.valueOf(mod390.getId()));
		domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
		domainNameHidden.setValue(getCurrentDomainName());
		diskForm.submit();
	}

	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		diskForm.setAction(GWT.getHostPageBaseURL()	+ "/aon_gwt_fiscal/Model3902014Print");
		mod390Hidden.setValue(String.valueOf(mod390.getId()));
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

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	
	private void cleanErrorMessage() {
		errorPage.addErrorMsg(new LinkedList<ValidationMessage>());
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		errorPage.addErrorMsg(msg);
		resultsPanel.setWidget(errorPage);
		openFootPanel();
	}

//	private void addErrorMessage(String msg) {
//		SimplePanel panel = new SimplePanel();
//		Label label = new Label(msg);
//		label.setStyleName(AON.AON_CSS.aonIconError());
//		label.addStyleName(AON.AON_CSS.aonMarginLeft());
//		label.addStyleName(AON.AON_CSS.aonColorRed());
//		label.addStyleName(AON.AON_CSS.aonBold());
//		panel.add(label);
//		resultsPanel.setWidget(errorPage);
//	}

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

	private class WestFocusPanel extends FocusPanel {
		
		private IMod3902014Page content;
		
		public WestFocusPanel(String label, final IMod3902014Page content,IMod3902014CallBack callback) {
			super();
			this.content = content;
			setStyleName(AON.AON_CSS.aonLinkItem());
			FlowPanel fp = new FlowPanel();
			fp.setStyleName(AON.AON_CSS.aonLinkListItem());
			InlineLabel lb = new  InlineLabel(label);
			fp.add(lb);
			setWidget(fp);
			
			pagesPanel.add(content);
			content.setCallback(callback);
			
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showPage();
				}
			});
		}

		public void showPage() {
			for (int i = 0 ; i < linkContainer.getWidgetCount(); i ++) {
				linkContainer.getWidget(i).removeStyleName(AON.AON_CSS.aonLinkItemSelected());
			}
			addStyleName(AON.AON_CSS.aonLinkItemSelected());
			pagesPanel.showWidget(pagesPanel.getWidgetIndex(content));
		}

		public void setValue(Mod3902014 m390) {
			content.setValue(m390);
		}
		public void populate(Mod3902014 m390) {
			content.populate(m390);
		}
		
	}
	
	private void validate(Mod3902014 m390) {
		LinkedList<ValidationMessage> msg = new LinkedList<ValidationMessage>();
		if (m390.getYear() != 2014 && m390.getYear() != 2013) msg.add(ValidationMessages.EMPTY_YEAR.getMsg());
		if (!m390.isLegalEntity()) {
			if (AonStringUtils.isEmpty(m390.getDocument())) msg.add(ValidationMessages.EMPTY_DOCUMENT.getMsg());
			if (!AonDocumentUtil.isValid(m390.getDocument())) msg.add(ValidationMessages.WRONG_DOCUMENT.getMsg());
			if (AonStringUtils.isEmpty(m390.getName())) msg.add(ValidationMessages.REQ_NAME.getMsg());
			if (AonStringUtils.isEmpty(m390.getFirstSurname())) msg.add(ValidationMessages.REQ_SURNAME.getMsg());
		} else {
			if (AonStringUtils.isEmpty(m390.getName())) msg.add(ValidationMessages.EMPTY_NAME.getMsg());
		}
		if (m390.getMainActivity() == null || AonStringUtils.isEmpty(m390.getMainActivity().getKey())) 
			msg.add(ValidationMessages.EMPTY_ACTI.getMsg());
		if (!m390.isLegalEntity()) {
			if (m390.getAddress() == null) msg.add(ValidationMessages.EMPTY_REPR.getMsg());
			if (AonStringUtils.isEmpty(m390.getAddress().getRdocument())) msg.add(ValidationMessages.EMPTY_REPR_DOC.getMsg());
			if (!AonDocumentUtil.isValid(m390.getAddress().getRdocument())) msg.add(ValidationMessages.WRONG_REPR_DOC.getMsg());
		} else {
			if (m390.getLegalRepr1() != null &&
				!AonDocumentUtil.isValid(m390.getLegalRepr1().getDocument())) msg.add(ValidationMessages.LG1_WRONG_DOC.getMsg());
			if (m390.getLegalRepr2() != null &&
				!AonDocumentUtil.isValid(m390.getLegalRepr2().getDocument())) msg.add(ValidationMessages.LG2_WRONG_DOC.getMsg());
			if (m390.getLegalRepr3() != null &&
				!AonDocumentUtil.isValid(m390.getLegalRepr3().getDocument())) msg.add(ValidationMessages.LG3_WRONG_DOC.getMsg());
		}
		if (!msg.isEmpty()) {
			errorPage.addErrorMsg( msg );
			resultsPanel.setWidget(errorPage);
			openFootPanel();
		}		
	}
	
}

