package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards.AonCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_REQUISITION_STATUS;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class NordigenModule extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(NordigenModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	private static final String EURO = "\u20AC";
	private static final DateTimeFormat DATE_HOURS = DateTimeFormat.getFormat("hh:mm");
	private static final String AON_BLUE = "#002469";
	private static final String HOVER_COLOR = "#7A9AD7";
	
	
	private static NordigenServiceAsync NORDIGEN_SERVICE;
	
	private DockLayoutPanel dockLayoutPanel;
	private SplitLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonToolbar toolbar;
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private FlowPanel sessionLog;
	boolean minimizedByUser;
	
	private Widget linkedBanks;
	private Widget unlinkedBanks;
	FlowPanel enterpriseData;
	private boolean firstTime;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		NordigenModuleOptions options = new NordigenModuleOptions()
				.setParentWidget(root)
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser())
		;
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final NordigenModuleOptions opt ) {
		ensureGwtSelector();
		AON.ensureInjected();
		NordigenServiceAsync serviceRaw = GWT.create(NordigenService.class);
		NORDIGEN_SERVICE = new NordigenServiceAsyncDecorator(serviceRaw);
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		if ( opt.getConfiguration() == null) {
			NORDIGEN_SERVICE.getConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<NordigenConfiguration>() {
				@Override
				public void onSuccess(NordigenConfiguration result) {
					opt.setConfiguration(result);
					loadModule( opt, false );
				}
				
				@Override
				public void onFailure(Throwable caught) {
					opt.setConfiguration(new NordigenConfiguration());
					loadModule( opt, false );
				}
			});
		} else {
			loadModule( opt, false );
		}
	}
	
	private void loadModule( final NordigenModuleOptions opt , boolean reload) {
		if (isMobile() && reload)
			dockLayoutPanel.clear();
		toolbar = new AonToolbar(AON.MSG.chekItModule());
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH );
		centerLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(centerLayoutPanel);
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.add(centerPanel);
		if (!isMobile()) {
			centerLayoutPanel.addSouth(getMinimizePanel(), 30);
		}
		dockLayoutPanel.add(centerLayoutPanel);
		enterpriseData = paintEnterpiseData( opt );
		container.add( enterpriseData );
		
		if (firstTime) {
			Label loadingLabel = new Label("El agregador bancario ha sido inicializado con \u00E9xito");
			loadingLabel.setStyleName(AON.CSS.aonColorGreen());
			sessionLog.add(loadingLabel);
			openFootPanel();
		}
//		ScriptInjector.fromString("if($doc.querySelector('aon-application')) {"
//				+ "$doc.querySelector('aon-application').stopLoader()"
//				+ "}").inject();
		
	}
	
	
	//TODO : SEGUIR POR AQUÍ
	private FlowPanel paintEnterpiseData(NordigenModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		linkedBanks = paintBanks(opt);
		unlinkedBanks = paintUnlinkedBanks(opt);
		panel.add(linkedBanks);
		panel.add(unlinkedBanks);
		return panel;
	}


	private Widget paintBanks(NordigenModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonTextCenter());
		panel.addStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonBlockCenter());
		panel.addStyleName(AON.CSS.aonBorder());
		panel.addStyleName(AON.CSS.aonPaddingBottom());
		panel.addStyleName(AON.CSS.aonPaddingTop());
		Label linkedTitle = new Label("Cuentas vinculadas");
		linkedTitle.addStyleName(AON.CSS.aonTextLeft());
		linkedTitle.addStyleName(AON.CSS.aonFontMedium());
		linkedTitle.addStyleName(AON.CSS.aonBold());
		linkedTitle.addStyleName(AON.CSS.aonMarginTop());
		if (opt.getConfiguration().getLinkedAccounts() != null && !opt.getConfiguration().getLinkedAccounts().isEmpty()) {
			AonCards cards = new AonCards();
			cards.addStyleName(AON.CSS.aonBlockCenter());
			double balanceTotal = 0;
			double remainderTotal = 0;
			boolean logs = false;
			for (NordigenBankAccount bankAccount : opt.getConfiguration().getLinkedAccounts()) {
				double bankBalance = bankAccount.getBalance() != null &&
						bankAccount.getBalance().getBalanceAmount() != null ?
						bankAccount.getBalance().getBalanceAmount().getAmount() : 0;
				balanceTotal = balanceTotal + bankBalance;
				cards.addCard( new AonNordigenBankCard(opt, bankAccount) );
				
				if (isMobile()) {
					cards.setWidth("100%");
				}
			}
			if (!isMobile() && logs) {
				openFootPanel();
			}
			
			balanceTotal = AonMathUtils.round( balanceTotal );
			remainderTotal = AonMathUtils.round( remainderTotal );
			
			FlowPanel totals = new FlowPanel();
			panel.setStyleName(AON.CSS.aonTextCenter());
			panel.addStyleName(AON.CSS.aonWidthAlmostAll());
			panel.addStyleName(AON.CSS.aonMarginTop());
			panel.addStyleName(AON.CSS.aonBlockCenter());
			panel.addStyleName(AON.CSS.aonPaddingBottom());
			panel.addStyleName(AON.CSS.aonPaddingTop());
			
			Label accumLabel = new Label("Acumulados ");
			accumLabel.setStyleName(AON.CSS.aonTableLabel());
			accumLabel.addStyleName(AON.CSS.aonFontMedium());
			accumLabel.addStyleName(AON.CSS.aonTextLeft());

			Label balanceLabel = new Label("Saldo: ");
			balanceLabel.setStyleName(AON.CSS.aonMarginLeft());
			balanceLabel.addStyleName(AON.CSS.aonTableLabel());
			
			InlineLabel balanceBox = new InlineLabel();
			balanceBox.setText( AON.FMT.format( balanceTotal ) + " " + EURO);
			balanceBox.setStyleName(AON.CSS.aonMarginLeft());
			balanceBox.addStyleName(AON.CSS.aonFontMedium());
			balanceBox.addStyleName(AON.CSS.aonTextRight());
			balanceBox.addStyleName(AON.CSS.aonBold());
			if (AonMathUtils.isLessThanZero( balanceTotal )) {
				balanceBox.addStyleName(AON.CSS.aonColorRed());	
			}
			
			Label remainderLabel = new Label("Disponible: ");
			remainderLabel.setStyleName(AON.CSS.aonTableLabel());
			remainderLabel.addStyleName(AON.CSS.aonMarginLeft());
			
			InlineLabel remainderBox = new InlineLabel();
			remainderBox.setText( AON.FMT.format( remainderTotal ) + " " + EURO);
			remainderBox.setStyleName(AON.CSS.aonMarginLeft());
			remainderBox.addStyleName(AON.CSS.aonFontMedium());
			remainderBox.addStyleName(AON.CSS.aonTextRight());
			remainderBox.addStyleName(AON.CSS.aonBold());
			if (AonMathUtils.isLessThanZero( remainderTotal )) {
				remainderBox.addStyleName(AON.CSS.aonColorRed());
			}
			
			FlexTable table = new FlexTable();
			table.setStyleName(AON.CSS.aonTextRight());
			table.setWidget(0, 0, balanceLabel);
			table.setWidget(0, 1, balanceBox);
//			table.setWidget(1, 0, remainderLabel);
//			table.setWidget(1, 1, remainderBox);
			
			totals.add(accumLabel);
			totals.add(table);
			totals.addStyleName(AON.CSS.aonMarginBottom());
			panel.add(totals);
			panel.add(linkedTitle);
			panel.add(cards);
		} else
			panel.add(linkedTitle);
		
		return panel;
	}
	
	private Widget paintUnlinkedBanks(NordigenModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonTextCenter());
		panel.addStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonBlockCenter());
		panel.addStyleName(AON.CSS.aonPaddingBottom());
		panel.addStyleName(AON.CSS.aonPaddingTop());
		if (opt.getConfiguration().getUnlinkedAccounts() != null && !opt.getConfiguration().getUnlinkedAccounts().isEmpty()) {
			
			Label unlinkedTitle = new Label("Cuentas no vinculadas");
			unlinkedTitle.addStyleName(AON.CSS.aonTextLeft());
			unlinkedTitle.addStyleName(AON.CSS.aonFontMedium());
			unlinkedTitle.addStyleName(AON.CSS.aonBold());
			panel.add(unlinkedTitle);
			
			AonCards cards = new AonCards();
			for (NordigenBankAccount unlinkedBankAccount : opt.getConfiguration().getUnlinkedAccounts()) {
				cards.addCard( new NordigenUnlinkedBankCard(opt, unlinkedBankAccount) );
			}
			panel.add(cards);
		}
		return panel;
	}
	
	private class AonNordigenBankCard extends AonCard {
		
		boolean updateError;

		private AonNordigenBankCard(final NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount) {
			
			updateError = false;
			
			FlowPanel titlePanel = new FlowPanel();
			titlePanel.addStyleName(AON.CSS.aonTextCenter());
			
			NordigenInstitution institution = nordigenBankAccount.getInstitution();
			NordigenAccountMetadata metadata = nordigenBankAccount.getMetadata();
			NordigenAccountBalance balance = nordigenBankAccount.getBalance();
			String logo = institution != null ? institution .getLogo() : null;
			String bankName = institution != null ? AonStringUtils.trimToEmpty(institution.getName()) : "";
			String iban = metadata != null ? AonStringUtils.trimToEmpty(metadata.getIban()) : "";
			Date atDate = balance != null ? balance.getReferenceDate() : null;
			Double balanceAmount = balance != null && balance.getBalanceAmount() != null ? balance.getBalanceAmount().getAmount() : null;
			if (logo != null && !logo.isEmpty()) {
				Image logoImg = new Image(logo);
				logoImg.setHeight("40px");
				titlePanel.add(logoImg);
			} else {
				Label title = new Label();
				title.addStyleName(AON.CSS.aonBorderNone());
				title.setText(AonStringUtils.abbreviate(bankName, 26));
				String bankTitle = bankName;
				title.setTitle(bankTitle);
				titlePanel.add(title);
			}
			this.setTitle(titlePanel);
			if (Window.getClientWidth() < 675) {				
				this.getElement().getStyle().setProperty("marginLeft", "auto");
				this.getElement().getStyle().setProperty("marginRight", "auto");
			}
			
			FlowPanel body = new FlowPanel();
			FlexTable bottomTable = new FlexTable();
			FlowPanel ibanPanel = new FlowPanel();
			InlineLabel ibanBox = new InlineLabel();
			ibanBox.setText(formatIban(iban));
			ibanPanel.addStyleName(AON.CSS.aonMarginBottom());
			ibanPanel.add(ibanBox);
			
			FlowPanel atDateLabelPanel = new FlowPanel();
			InlineLabel atDateLabel = new InlineLabel("\u00DAltima actualizaci\u00F3n ");
			atDateLabel.setStyleName(AON.CSS.aonTableLabel());
			atDateLabelPanel.add(atDateLabel);
			
			FlowPanel atDatePanel = new FlowPanel();
			InlineLabel atDateBox = new InlineLabel();
//			InlineLabel atDateBox2 = new InlineLabel();
			atDateBox.addStyleName(AON.CSS.aonFontMedium());
			atDateBox.setText(balance == null ? "----" : AON.DATE_FORMAT.format(atDate));
//			atDateBox2.setText(balance == null ? "" : " hora: " + DATE_HOURS.format(atDate));
			
			atDatePanel.addStyleName(AON.CSS.aonMarginBottom());
			
			if (atDate != null && CalendarUtil.getDaysBetween(atDate, new Date()) >= 4) {
				atDateBox.addStyleName(AON.CSS.aonColorRed());
				updateError = true;
				int noUpDays = CalendarUtil.getDaysBetween(atDate, new Date());
				FlowPanel updateErrorPanel = new FlowPanel();
				Label updateErrorLbl = new Label("Error de actualizaci\u00F3n en cuenta " + bankName + " - " + iban + ": la cuenta lleva " + noUpDays + " d\u00EDas sin actualizarse");
				updateErrorLbl.addStyleName(AON.CSS.aonColorRed());
				Label updateErrorLbl2 = new Label("Acceda a su banca online para verificar el acceso");
				updateErrorLbl2.addStyleName(AON.CSS.aonMarginLeft());
				updateErrorPanel.add(updateErrorLbl);
				updateErrorPanel.add(updateErrorLbl2);
				if (!isMobile()) {
					sessionLog.add(updateErrorPanel);
					openFootPanel();
				}
				
			} else if (atDate != null){
				atDateBox.addStyleName(AON.CSS.aonColorGreen());				
			}
			atDatePanel.add(atDateBox);
//			atDatePanel.add(atDateBox2);
			
			FlowPanel balanceLabelPanel = new FlowPanel();
			InlineLabel balanceLabel = new InlineLabel("Saldo ");
			balanceLabel.setStyleName(AON.CSS.aonTableLabel());
			balanceLabelPanel.add(balanceLabel);
			
			FlowPanel balancePanel = new FlowPanel();
			InlineLabel balanceBox = new InlineLabel();
			balanceBox.addStyleName(AON.CSS.aonFontMedium());
			balanceBox.addStyleName(AON.CSS.aonBold());
			if (balanceAmount != null && AonMathUtils.isLessThanZero(balanceAmount)) {
				balanceBox.addStyleName(AON.CSS.aonColorRed());
			}
			balanceBox.setText( AON.FMT.format(balanceAmount) + " " + EURO);
			balancePanel.addStyleName(AON.CSS.aonMarginBottom());
			balancePanel.add(balanceBox);
//			FlowPanel remainderLabelPanel = new FlowPanel();
//			InlineLabel remainderLabel = new InlineLabel("Disponible ");
//			remainderLabel.setStyleName(AON.CSS.aonTableLabel());
//			remainderLabelPanel.add(remainderLabel);
//
//			FlowPanel remainderPanel = new FlowPanel();
//			InlineLabel remainderBox = new InlineLabel();
//			if (AonMathUtils.isLessThanZero( nordigenBankAccount.getRemainder())) {
//				balanceBox.addStyleName(AON.CSS.aonColorRed());	
//			}
//			remainderBox.setText( AON.FMT.format( nordigenBankAccount.getRemainder()) + " " + EURO);
//			remainderPanel.addStyleName(AON.CSS.aonMarginBottom());
//			remainderPanel.add(remainderBox);

			body.add(ibanPanel);
			
			body.add(atDateLabelPanel);
			body.add(atDatePanel);
			
			body.add(balanceLabelPanel);
			body.add(balancePanel);
			
//			body.add(remainderLabelPanel);
//			body.add(remainderPanel);
			
			if (Window.getClientWidth() < 675) {				
				this.getElement().getStyle().setProperty("minWidth", "100%");
			} else {
				this.getElement().getStyle().setProperty("minWidth", "260px");
			}
			body.addStyleName(AON.CSS.aonTextCenter());
			
			int pendingMovements = 0;
			FlowPanel movText = new FlowPanel();
			
//			ClickHandler clickHandler = event -> {
//				FlowPanel panel = new FlowPanel();
//				panel.getElement().getStyle().setProperty("minWidth", "230px");
//				
//				FlowPanel movFlow = null;
//				CustomDialog dialog = null;
//				
//				if (isMobile()) {
//					AonToolbarButton back = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack()); 
//					toolbar.setTitle("MOVIMIENTOS");
//					toolbar.add(back);
//					centerPanel.clear();
//					back.addClickHandler(h -> {
//						toolbar.remove(back);
//						centerPanel.clear();
//						loadModule(opt, true);
//					});
//					
//					
//					movFlow = new FlowPanel();
//					centerPanel.add(movFlow);
//				} else {					
//					dialog = new CustomDialog();
//					dialog.setAutoHideEnabled(true);
//					
//					dialog.setCaption("MOVIMIENTOS PENDIENTES");
//				}
//				
//				
//				HorizontalPanel closeImport = new HorizontalPanel();
//				closeImport.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//				closeImport.addStyleName(AON.CSS.aonPaddingTop());
//				closeImport.addStyleName(AON.CSS.aonBlockCenter());
//				closeImport.addStyleName(AON.CSS.aonPaddingBottom());
//				
//				
////				Button close = new Button(AON.MSG.close());
////				close.setStyleName(AON.CSS.aonMarginRight());
////				close.addClickHandler(e -> dialog.hide());
//				Button importBtn = aonImportButton();
//				
//				
//				CustomDialog dial = dialog;
//				importBtn.addClickHandler(e ->
//					NORDIGEN_SERVICE.insertTransactions(
//							opt.getDomainName()
//							, opt.getDomain()
//							, opt.getUser()
//							, opt.getConfiguration().getEnterpriseId()
//							, nordigenBankAccount
//							, new AsyncCallback<Integer>() {
//								@Override
//								public void onSuccess(Integer result) {
//									String singPlur = (result != 1) ? " nuevos movimientos insertados en " : " nuevo movimiento insertado en ";
//									
//									Label label = new Label(result + singPlur + nordigenBankAccount.getBank() + " - " + formatIban(nordigenBankAccount.getCcc()));
//									label.addStyleName(AON.CSS.aonColorGreen());
//									sessionLog.add(label);
//									openFootPanel();										
//									
//									if (result != null && result != 0) {
//										getBottomCardMessage(bottomTable
//												, new Label("No hay movimientos pendientes")
//												, updateError
//												, areLogs);
//										nordigenBankAccount.setPending(Collections.emptyList());
//									}
//									if (!isMobile())
//										dial.hide();
//								}
//								
//								@Override
//								public void onFailure(Throwable caught) {
//									Label errLabel = new Label(caught.getMessage());
//									errLabel.setStyleName(AON.CSS.aonColorRed());
//									sessionLog.add(errLabel);
//									openFootPanel();
//									if (!isMobile())
//										dial.hide();
//								}	
//						}));
//				
////				closeImport.add(close);
//				
//				if (nordigenBankAccount.getPending() != null && !nordigenBankAccount.getPending().isEmpty())
//					closeImport.add(importBtn);
//				
//				FlowPanel movementsFlow = new FlowPanel();
//				movementsFlow.setWidth(isMobile() ? "100%" : "90%");
//				movementsFlow.setStyleName(AON.CSS.aonBlockCenter());
//				
//				FlowPanel topInfo = new FlowPanel();
//				topInfo.setWidth("100%");
//				
//				Image logoImg = new Image(nordigenBankAccount.getLogo());
//				logoImg.setHeight("40px");
//				logoImg.addStyleName(AON.AON_CSS.aonDisplayBlock());
//				logoImg.addStyleName(AON.CSS.aonBlockCenter());
//				Label ibanLbl = new Label(formatIban(nordigenBankAccount.getCcc()));
//				ibanLbl.addStyleName(AON.CSS.aonTextCenter());
//				topInfo.add(logoImg);
//				topInfo.add(ibanLbl);
//				topInfo.addStyleName(AON.CSS.aonMarginBottom());
//				
//				movementsFlow.add(topInfo);
//				
//				if (isMobile() || (nordigenBankAccount.getPending() != null && !nordigenBankAccount.getPending().isEmpty())) {
//					FlowPanel periodMovContainer = new FlowPanel();
//					if (isMobile())
//						buildCustomMovementsPanel(periodMovContainer, opt, nordigenBankAccount);
//					else
//						periodMovContainer.add(getMovements(nordigenBankAccount));
//					movementsFlow.add(periodMovContainer);
////					movementsFlow.add(getMovements(checkItBankAccount, isMobile()));
//				} else {
//					Label noMovLbl = new Label("No hay movimientos pendientes");
//					noMovLbl.setWidth("100%");
//					noMovLbl.setStyleName(AON.CSS.aonTextCenter());
//					movementsFlow.add(noMovLbl);
//						
//				}
//				ScrollPanel movementsPanel = new ScrollPanel(movementsFlow);
//				movementsPanel.addStyleName(AON.CSS.aonCustomScroll());
//				if (isMobile()) {
//					movementsPanel.setWidth("100%");
//				} else {
//					
//					movementsPanel.getElement().getStyle().setProperty("minWidth", "700px");
//					movementsPanel.addStyleName(AON.CSS.aonMarginTop());
//					movementsPanel.getElement().getStyle().setProperty("maxHeight", "40vh");
//				}
//				panel.add(movementsPanel);
//				if (isMobile()) {
//					movFlow.add(panel);
//					centerPanel.getElement().getStyle().setPadding(5, Unit.PX);
//				} else {					
//					panel.add(closeImport);
//					dialog.add(panel);
//					dialog.center();
//					dialog.show();
//				}
//			};
			
			
			
			
//			if (!isMobile())
//			body.addDomHandler(clickHandler, ClickEvent.getType());
//			title.addDomHandler(clickHandler, ClickEvent.getType());
			
			bottomTable.addStyleName(AON.CSS.aonBlockCenter());
			
			Widget msgWidget = null;
			NordigenRequisition requisition = nordigenBankAccount.getRequisition();
			if (requisition != null) {
				NORDIGEN_REQUISITION_STATUS requisitionStatus = requisition.getStatus();
				if (!NORDIGEN_REQUISITION_STATUS.LN.equals(requisitionStatus)) {
					Label errLabel = new Label();
					msgWidget = errLabel;
					errLabel.addStyleName(AON.CSS.aonColorRed());
					if (NORDIGEN_REQUISITION_STATUS.EX.equals(requisitionStatus)) {
						errLabel.setText("Las credenciales expiraron, debe volver a vincular la cuenta");
					} else if (NORDIGEN_REQUISITION_STATUS.RJ.equals(requisitionStatus)) {
						errLabel.setText("El proceso de vinclaci\u00F3n fall\u00F3");
					} else {
						errLabel.setText("La vinculaci\u00F3n no se ha completado a\u00FAn");
					}
				}
			}
//			if (!isMobile() && pendingMovements > 0) {
//				
//				String singPlur = pendingMovements != 1 ? " nuevos movimientos" : " nuevo movimiento";
//				
//				InlineLabel pending1Label = new InlineLabel("Hay ");
//				InlineLabel pending2Label = new InlineLabel(AonNumberUtils.toString(pendingMovements));
//				pending2Label.setStyleName(AON.CSS.aonBold());
//				InlineLabel pending3Label = new InlineLabel(singPlur);
//				
//				
//				movText.add(pending1Label);
//				movText.add(pending2Label);
//				movText.add(pending3Label);
//				
//				movText.addStyleName(AON.CSS.aonTextCenter());
//				movText.addStyleName(AON.CSS.aonColorGreen());
//				msgWidget = movText;
////				bottomTable.setWidget(0, 0, movText);
//			} else if (!isMobile()) {
//				Label noMovLbl = new Label("No hay movimientos pendientes");
//				msgWidget = noMovLbl;
//				
////				bottomTable.setWidget(0, 0, noMovLbl);
//				
//			}
			
			getBottomCardMessage(bottomTable, msgWidget, updateError);
			body.add(bottomTable);				
			
			
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton(AON.MSG.importAction(), AON.CSS.aonIconImport());
			saveButton.setTabIndex(-2);
//			saveButton.addClickHandler(event ->
//				NORDIGEN_SERVICE.insertTransactions(
//						opt.getDomainName()
//						, opt.getDomain()
//						, opt.getUser()
//						, opt.getConfiguration().getEnterpriseId()
//						, nordigenBankAccount
//						, new AsyncCallback<Integer>() {
//							@Override
//							public void onSuccess(Integer result) {
//									String singPlur = (result != 1) ? " nuevos movimientos insertados en " : " nuevo movimiento insertado en ";
//									
//									Label label = new Label(result + singPlur + nordigenBankAccount.getBank() + " - " + formatIban(nordigenBankAccount.getCcc()));
//									label.addStyleName(AON.CSS.aonColorGreen());
//									sessionLog.add(label);
//									openFootPanel();
//								
//								if (result != null && result != 0) {
//									getBottomCardMessage(bottomTable
//											, new Label("No hay movimientos pendientes")
//											, updateError
//											, areLogs);
////									if (!updateError) {
////										body.remove(bottomTable);
////										body.add(new Label("No hay movimientos pendientes"));										
////									}
//									nordigenBankAccount.setPending(Collections.emptyList());
//								}
//								
//							}
//							
//							@Override
//							public void onFailure(Throwable caught) {
//								Label errLabel = new Label(caught.getMessage());
//								errLabel.setStyleName(AON.CSS.aonColorRed());
//								sessionLog.add(errLabel);
//								openFootPanel();
//							}	
//					}));
			

			
			AonTableButton allMovementsButton = new AonTableButton("Todos los movimientos", AON.CSS.aonIconList());
			allMovementsButton.setTabIndex(-3);
			
			allMovementsButton.addClickHandler(event -> {
				FlowPanel panel = new FlowPanel();
				panel.getElement().getStyle().setProperty("minWidth", "230px");
				
				FlowPanel movFlow = null;
				CustomDialog dialog = new CustomDialog();
				dialog.setAutoHideEnabled(true);
				
				dialog.setCaption("MOVIMIENTOS");
				
				
				HorizontalPanel closeImport = new HorizontalPanel();
				closeImport.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				closeImport.addStyleName(AON.CSS.aonPaddingTop());
				closeImport.addStyleName(AON.CSS.aonBlockCenter());
				closeImport.addStyleName(AON.CSS.aonPaddingBottom());
				
				
				Button importBtn = aonImportButton();
				
				CustomDialog dial = dialog;
//				importBtn.addClickHandler(e ->
//					NORDIGEN_SERVICE.insertTransactions(
//							opt.getDomainName()
//							, opt.getDomain()
//							, opt.getUser()
//							, opt.getConfiguration().getEnterpriseId()
//							, nordigenBankAccount
//							, new AsyncCallback<Integer>() {
//								@Override
//								public void onSuccess(Integer result) {
//									String singPlur = (result != 1) ? " nuevos movimientos insertados en " : " nuevo movimiento insertado en ";
//									
//									Label label = new Label(result + singPlur + nordigenBankAccount.getBank() + " - " + formatIban(nordigenBankAccount.getCcc()));
//									label.addStyleName(AON.CSS.aonColorGreen());
//									sessionLog.add(label);
//									openFootPanel();										
//									
//									if (result != null && result != 0) {
//										getBottomCardMessage(bottomTable
//												, new Label("No hay movimientos pendientes")
//												, updateError);
//										nordigenBankAccount.setPending(Collections.emptyList());
//									}
//									dial.hide();
//								}
//								
//								@Override
//								public void onFailure(Throwable caught) {
//									Label errLabel = new Label(caught.getMessage());
//									errLabel.setStyleName(AON.CSS.aonColorRed());
//									sessionLog.add(errLabel);
//									openFootPanel();
//										dial.hide();
//								}	
//						}));
				
				
				FlowPanel movementsFlow = new FlowPanel();
				movementsFlow.setWidth(isMobile() ? "100%" : "90%");
				movementsFlow.setStyleName(AON.CSS.aonBlockCenter());
				
				FlowPanel topInfo = new FlowPanel();
				topInfo.setWidth("100%");
				
				Image logoImg = new Image(logo);
				logoImg.setHeight("40px");
				logoImg.addStyleName(AON.AON_CSS.aonDisplayBlock());
				logoImg.addStyleName(AON.CSS.aonBlockCenter());
				Label ibanLbl = new Label(formatIban(iban));
				ibanLbl.addStyleName(AON.CSS.aonTextCenter());
				topInfo.add(logoImg);
				topInfo.add(ibanLbl);
				
				Label movBalanceLabel = new Label("Saldo");
				movBalanceLabel.addStyleName(AON.CSS.aonTextCenter());
				movBalanceLabel.addStyleName(AON.CSS.aonMarginTop());
				
				
				
				topInfo.add(movBalanceLabel);
				
				
				FlowPanel movBalancePanel = new FlowPanel();
				InlineLabel movBalanceBox = new InlineLabel();
				movBalanceBox.addStyleName(AON.CSS.aonFontMedium());
				movBalanceBox.addStyleName(AON.CSS.aonBold());
				if (balanceAmount != null && AonMathUtils.isLessThanZero(balanceAmount)) {
					movBalanceBox.addStyleName(AON.CSS.aonColorRed());
				}
				movBalanceBox.setText( AON.FMT.format(balanceAmount) + " " + EURO);
				movBalancePanel.addStyleName(AON.CSS.aonMarginBottom());
				movBalancePanel.addStyleName(AON.CSS.aonTextCenter());
				movBalancePanel.add(movBalanceBox);
				topInfo.add(movBalancePanel);
				
				topInfo.addStyleName(AON.CSS.aonMarginBottom());
				
				movementsFlow.add(topInfo);
				
				
				/**Filtros de fecha**/
				
				FlowPanel periodMovContainer = new FlowPanel();
				
				buildCustomMovementsPanel(periodMovContainer, opt, nordigenBankAccount, dialog);
				
				
				movementsFlow.add(periodMovContainer);
				
				ScrollPanel movementsPanel = new ScrollPanel(movementsFlow);
				movementsPanel.getElement().getStyle().setProperty("minWidth", "700px");
				movementsPanel.addStyleName(AON.CSS.aonMarginTop());
				movementsPanel.getElement().getStyle().setProperty("maxHeight", "40vh");
				movementsPanel.addStyleName(AON.CSS.aonCustomScroll());
				
				panel.add(movementsPanel);
				dialog.add(panel);
				dialog.center();
				dialog.show();
			});
			
			AonTableButton unlinkButton = new AonTableButton("Desvincular", AON.CSS.aonIconDelete());
			unlinkButton.setTabIndex(-4);
			unlinkButton.addClickHandler((ev) -> {
				FlexTable unlinkTable = new FlexTable();
				unlinkTable.addStyleName(AON.CSS.aonBlockCenter());
				AonDialog dialog = new AonDialog("DESVINCULAR CUENTA", unlinkTable);
				Label unlinkLabel = new Label("\u00BFDESEA DESVINCULAR ESTA CUENTA?");
				unlinkTable.setWidget(0, 0, unlinkLabel);
				HorizontalPanel hp = new HorizontalPanel();
				hp.addStyleName(AON.CSS.aonBlockCenter());
				Button hai = new Button(AON.MSG.accept());
				RegistryBank rbank = nordigenBankAccount.getRbank();
				
				hai.addClickHandler((event) -> {
					NORDIGEN_SERVICE.cancelRequisition(opt.getConfiguration().getToken(),
							opt.getDomainName(),
							opt.getDomain(),
							opt.getUser(),
							rbank != null ? rbank.getId() : null, new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									
								}

								@Override
								public void onSuccess(Boolean result) {
									reloadPage(opt, () -> {
										dialog.hide();
									});
									
								}
							});
				});
				
				if (isMobile()) {
					mobileAcceptButton(hai);
				} else {
					desktopAcceptButton(hai);
				}
				
				Button iie = new Button(AON.MSG.cancelAction());
				iie.addClickHandler((event) -> {
					dialog.hide();
				});
				hp.setWidth("50%");
				hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				if (!isMobile()) {
					desktopCancelButton(iie);
					hp.add(iie);
				}
				hp.add(hai);
				hp.addStyleName(AON.CSS.aonBlockCenter());
				unlinkTable.setWidget(1, 0, hp);
				
				dialog.center();
				dialog.show();
				
			});
			if (requisition != null) {
				if (NORDIGEN_REQUISITION_STATUS.LN.equals(requisition.getStatus())) {
					if (!isMobile()) {					
//						getMenuPanel().add(saveButton);
						getMenuPanel().add(allMovementsButton);
					}
				} else if (NORDIGEN_REQUISITION_STATUS.EX.equals(requisition.getStatus())) {
					//TODO
				} else if (NORDIGEN_REQUISITION_STATUS.RJ.equals(requisition.getStatus())) {
					//TODO					
				} else {
					AonTableButton continueLinkButton = new AonTableButton("Continuar la vinculaci\u00F3n", AON.CSS.aonIconRestore());
					continueLinkButton.setTabIndex(-5);
					continueLinkButton.addClickHandler((ev) -> {
						FlexTable flexTable = new FlexTable();
						AonDialog dialog = new AonDialog("Continuar vinculaci\u00F3n", flexTable);
						drawShit(opt, dialog, nordigenBankAccount, requisition, flexTable);
						dialog.setAutoHideEnabled(false);
						dialog.center();
						dialog.show();
						
						Window.open(requisition.getLink(), "REGISTRO DE CUENTA", "_blank");
					});
					
					getMenuPanel().add(continueLinkButton);
					
				}
				
			}
			getMenuPanel().add(unlinkButton);
			
		}

		private Button aonImportButton() {
			Button importBtn = new Button("Importar");
//			importBtn.setStyleName(AON.CSS.aonMarginLeft());
			
			importBtn.setWidth("100px");
			importBtn.setHeight("30px");
			
			Style style = importBtn.getElement().getStyle();
			style.setProperty("padding", "2px");
			style.setProperty("text-transform", "none");
			style.setProperty("background", AON_BLUE);
			style.setProperty("color", "white");
			style.setProperty("fontSize", "1rem");
			style.setProperty("fontWeight", "700");
			style.setProperty("border", "none");
			style.setProperty("borderRadius", "6px");
			style.setProperty("transition", "background .25s ease-in-out,transform .15s ease");
			
			importBtn.addMouseOverHandler(ev -> {
				style.setProperty("background", HOVER_COLOR);				
			});
			
			importBtn.addMouseOutHandler(ev -> {
				style.setProperty("background", AON_BLUE);								
			});
			
			return importBtn;
		}
		
	}
	
	private static void periodDropdownStyle(ListBox listBox) {
		listBox.getElement().getStyle().setProperty("borderRadius", "5px");
		listBox.getElement().getStyle().setBorderColor(AON_BLUE);
		listBox.getElement().getStyle().setBorderWidth(2, Unit.PX);
		listBox.getElement().getStyle().setColor(AON_BLUE);
		listBox.getElement().getStyle().setPadding(2.5, Unit.PX);
		listBox.addStyleName(AON.CSS.aonFontSmall());
	}
	
	private void buildCustomMovementsPanel(FlowPanel container, NordigenModuleOptions opt, NordigenBankAccount noridgenankAccount, CustomDialog ...dialog) {
		int firstYear = 2020;
		
		
		FlowPanel perTopFlow = new FlowPanel();
		Label perLbl = new InlineLabel("PER\u00CDODO: ");
		perLbl.addStyleName(AON.CSS.aonFontSmall());
		perLbl.addStyleName(AON.CSS.aonBold());
		perLbl.getElement().getStyle().setColor(AON_BLUE);
		perTopFlow.add(perLbl);
		ListBox periodSelector = new ListBox();
		periodSelector.addItem("\u00DAltimos 10 d\u00EDas", "10");
		periodSelector.addItem("\u00DAltimos 30 d\u00EDas", "30");
		periodSelector.addItem("\u00DAltimos 60 d\u00EDas", "60");
		periodSelector.addItem("Personalizado", "0");
		periodSelector.setSelectedIndex(0);
		periodDropdownStyle(periodSelector);
		
		perTopFlow.add(periodSelector);
		perTopFlow.addStyleName(AON.AON_CSS.aonDisplayBlock());
		perTopFlow.addStyleName(AON.AON_CSS.aonBlockCenter());
		perTopFlow.addStyleName(AON.AON_CSS.aonTextCenter());
		
		
		int currentYear = AonDateUtils.getCurrentYear();
		String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
		
		ListBox listBoxYearFrom = new ListBox();
		for (int year=currentYear; year>=firstYear; year--) {
			listBoxYearFrom.addItem(String.valueOf(year), String.valueOf(year));
		}
		ListBox listBoxYearTo = new ListBox();
		for (int year=currentYear; year>=firstYear; year--) {
			listBoxYearTo.addItem(String.valueOf(year), String.valueOf(year));
		}
		
		Date thisMonth = new Date();
		
		ListBox listBoxMonthFrom = new ListBox();
		for (int month=0; month<=thisMonth.getMonth(); month++) {
			listBoxMonthFrom.addItem(monthNames[month], String.valueOf(month));
		}
		ListBox listBoxMonthTo = new ListBox();
		for (int month=0; month<12; month++) {
			listBoxMonthTo.addItem(monthNames[month], String.valueOf(month));
		}
		listBoxMonthFrom.setSelectedIndex(thisMonth.getMonth());
		listBoxMonthFrom.setWidth("100px");
		listBoxMonthTo.setSelectedIndex(thisMonth.getMonth());
		listBoxMonthTo.setWidth("100px");
		
		
		FlexTable movementContainer = new FlexTable();
		
		FlexTable customPeriod = new FlexTable();
		Label desdeLbl = new Label("DESDE:");
		desdeLbl.addStyleName(AON.CSS.aonFontSmall());
		desdeLbl.addStyleName(AON.CSS.aonBold());
		desdeLbl.getElement().getStyle().setColor(AON_BLUE);
		
		customPeriod.setWidget(0, 0, desdeLbl);
		customPeriod.setWidget(0, 1, listBoxMonthFrom);
		customPeriod.setWidget(0, 2, listBoxYearFrom);

		Label hastaLbl = new Label("HASTA:");
		hastaLbl.addStyleName(AON.CSS.aonFontSmall());
		hastaLbl.addStyleName(AON.CSS.aonBold());
		hastaLbl.getElement().getStyle().setColor(AON_BLUE);
		
		customPeriod.setWidget(1, 0, hastaLbl);
		customPeriod.setWidget(1, 1, listBoxMonthTo);
		customPeriod.setWidget(1, 2, listBoxYearTo);
		
		customPeriod.getElement().getStyle().setProperty("marginLeft", "auto");
		customPeriod.getElement().getStyle().setProperty("marginRight", "auto");
		
		FlowPanel periodFlow = new FlowPanel();
		periodFlow.add(perTopFlow);
		
		periodSelector.addChangeHandler(ev -> {
			if (periodSelector.getSelectedIndex() == periodSelector.getItemCount() - 1) {
				periodFlow.add(customPeriod);				
				customMovChange(movementContainer, opt, noridgenankAccount, listBoxMonthFrom, listBoxYearFrom, listBoxMonthTo, listBoxYearTo, firstYear, monthNames, dialog);
			} else {
				if (periodFlow.getWidgetCount() > 1)
					periodFlow.remove(1);
				int days = Integer.parseInt(periodSelector.getSelectedValue());
				Date from = new Date();
				CalendarUtil.addDaysToDate(from, -days);
				
				String periodStr = "en los \u00FAltimos " + days + "d\u00EDas";
				
				getMovements(movementContainer, opt, noridgenankAccount, from, new Date(), periodStr, dialog);
			}
		});
		
		
		int days = Integer.parseInt(periodSelector.getSelectedValue());
		Date from = new Date();
		CalendarUtil.addDaysToDate(from, -days);
		String periodStr = "en los \u00FAltimos " + days + "d\u00EDas";
		getMovements(movementContainer, opt, noridgenankAccount, from, new Date(), periodStr, dialog);
		
		ChangeHandler onDateChange = ev -> customMovChange(movementContainer, opt, noridgenankAccount, listBoxMonthFrom, listBoxYearFrom, listBoxMonthTo, listBoxYearTo, firstYear, monthNames, dialog);
		listBoxMonthFrom.addChangeHandler(onDateChange);
		listBoxYearFrom.addChangeHandler(onDateChange);
		listBoxMonthTo.addChangeHandler(onDateChange);
		listBoxYearTo.addChangeHandler(onDateChange);
		
		periodDropdownStyle(listBoxMonthFrom);
		periodDropdownStyle(listBoxYearFrom);
		periodDropdownStyle(listBoxMonthTo);
		periodDropdownStyle(listBoxYearTo);
		
		
		container.clear();
		container.add(periodFlow);
		
		container.add(movementContainer);
	}
	
	
	private void customMovChange(FlexTable movementContainer, NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount, ListBox listBoxMonthFrom, ListBox listBoxYearFrom, ListBox listBoxMonthTo, ListBox listBoxYearTo, int firstYear, String[] monthNames, CustomDialog ...dialog) {
		int fromMonth = Integer.parseInt(listBoxMonthFrom.getSelectedValue());
		int fromYear = Integer.parseInt(listBoxYearFrom.getSelectedValue());
		
		int toMonth = Integer.parseInt(listBoxMonthTo.getSelectedValue());
		int toYear = Integer.parseInt(listBoxYearTo.getSelectedValue());
		Date dateTo = new Date(toYear - 1900, toMonth, 1);
		CalendarUtil.addMonthsToDate(dateTo, 1);
		CalendarUtil.addDaysToDate(dateTo, -1);
		
		
		listBoxYearFrom.clear();
		for (int year=toYear; year>=firstYear; year--) {
			listBoxYearFrom.addItem(String.valueOf(year), String.valueOf(year));
		}
		int selectedIndex = (listBoxYearFrom.getItemCount() - 1) - (fromYear - firstYear);
		listBoxYearFrom.setSelectedIndex(selectedIndex > 0 ? selectedIndex : 0);
		
		fromYear = Integer.parseInt(listBoxYearFrom.getSelectedValue());
		
		if (fromYear == toYear) {
			listBoxMonthFrom.clear();
			for (int month=0; month<=toMonth; month++) {
				listBoxMonthFrom.addItem(monthNames[month], String.valueOf(month));
			}
		} else if (listBoxMonthFrom.getItemCount() != 12) {
			listBoxMonthFrom.clear();
			for (int month=0; month<12; month++) {
				listBoxMonthFrom.addItem(monthNames[month], String.valueOf(month));
			}
		}
		
		listBoxMonthFrom.setSelectedIndex(fromMonth < listBoxMonthFrom.getItemCount() ? fromMonth : toMonth);
		fromMonth = Integer.parseInt(listBoxMonthFrom.getSelectedValue());
		
		Date dateFrom = new Date(fromYear - 1900, fromMonth, 1);
		
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
		
		String periodStr = "del " + dtf.format(dateFrom) + " al " + dtf.format(dateTo);
		
		getMovements(movementContainer, opt, nordigenBankAccount, dateFrom, dateTo, periodStr, dialog);
	}
	
	
	private class NordigenUnlinkedBankCard extends AonCard {
		
		
		private NordigenUnlinkedBankCard (final NordigenModuleOptions opt, NordigenBankAccount nordigenUnlinkedBankAccount) {
			InlineLabel title = new InlineLabel();
			title.addStyleName(AON.CSS.aonBorderNone());
			
			RegistryBank rbank = nordigenUnlinkedBankAccount.getRbank();
			String bankName = rbank.getAlias();
			
			
			title.setText(bankName);
			this.setTitle(title);
			if (Window.getClientWidth() < 675) {				
				this.getElement().getStyle().setProperty("minWidth", "100%");
			} else {				
				this.getElement().getStyle().setProperty("minWidth", "260px");
			}
			if (Window.getClientWidth() < 675) {
				this.getElement().getStyle().setProperty("marginLeft", "auto");
				this.getElement().getStyle().setProperty("marginRight", "auto");
			}
			
			FlowPanel body = new FlowPanel();
			
			FlowPanel ibanPanel = new FlowPanel();
			InlineLabel ibanBox = new InlineLabel();
			ibanBox.setText(formatIban(nordigenUnlinkedBankAccount.getIban()));
			ibanPanel.add(ibanBox);
			
			
			body.add(ibanPanel);
			
			
			
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton("Vincular", AON.CSS.aonIconLink());
			
			saveButton.setTabIndex(-2);
			saveButton.addClickHandler(event -> {
				//TODO
				paintBankRegistration(opt, nordigenUnlinkedBankAccount);
			});
			getMenuPanel().add(saveButton);
		}
		
		private void countryChange(NordigenModuleOptions opt, ListBox countryList, FlexTable registrationTable, NordigenBankAccount nordigenBankAccount) {
			String countryIso2 = countryList.getSelectedValue();
			NORDIGEN_SERVICE.getNordigenInstitutions(opt.getConfiguration().getToken(), Country.valueOf(countryIso2), new AsyncCallback<List<NordigenInstitution>>() {

				@Override
				public void onFailure(Throwable caught) {
					if (registrationTable.getRowCount() >= 7) {
						registrationTable.removeRow(6);										
					}
				}

				@Override
				public void onSuccess(List<NordigenInstitution> result) {
					if (registrationTable.getRowCount() >= 7) {
						registrationTable.removeRow(6);										
					}
					AonNordigenBankBox bankBox = new AonNordigenBankBox(opt.getDomainName(), opt.getDomain(), opt.getUser(), result, isMobile());
					
					bankBox.setWidth("100%");
					
					
					bankBox.addSelectionHandler(e -> {
						if (e.getSelectedItem() != null) {
							nordigenBankAccount.setInstitution(e.getSelectedItem());											
						}
					});
					registrationTable.setWidget(6, 0, bankBox);
					
					
				}
			});
		}
		
		private void paintBankRegistration(NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount) {
			
			AonDialog dialog = null;
			FlowPanel mobilePanel = null;
			
			Label errLabel = new Label();
			
			FlexTable registrationTable = new FlexTable();
			
			if (isMobile()) {
				
				AonToolbarButton back = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack()); 
				
				toolbar.setTitle("VINCULAR");
				toolbar.add(back);
				centerPanel.clear();
				back.addClickHandler(h -> {
					toolbar.remove(back);
					centerPanel.clear();
					loadModule(opt, true);
				});
				
				
				mobilePanel = new FlowPanel();
				
				mobilePanel.add(registrationTable);
				centerPanel.add(mobilePanel);
			} else {				
				dialog = new AonDialog("REGISTRAR CUENTA", registrationTable);
				dialog.setAutoHideEnabled(true);
			}
			
			
			Label bankLabel = new Label(""+(nordigenBankAccount.getRbank() != null ? AonStringUtils.trimToEmpty(nordigenBankAccount.getRbank().getAlias()) : ""));
			bankLabel.addStyleName(AON.AON_BOLD);
			bankLabel.getElement().getStyle().setProperty("margin-bottom", "1.5em");
			bankLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			bankLabel.getElement().getStyle().setFontSize(isMobile() ? 1.5 : 2, Unit.EM);
			bankLabel.addStyleName(AON.AON_NO_MARGIN);
			registrationTable.setWidget(0, 0, bankLabel);
			
			Label ibanLabel = new Label(formatIban(nordigenBankAccount.getIban()));
			ibanLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			ibanLabel.getElement().getStyle().setFontSize(isMobile() ? 1 : 1.75, Unit.EM);
			ibanLabel.addStyleName(AON.CSS.aonMarginBottom());
			registrationTable.setWidget(1, 0, ibanLabel);
			
			FlexTable loginTable = new FlexTable();
			loginTable.setWidth("100%");
			
			
			HorizontalPanel hp = new HorizontalPanel();
			hp.addStyleName(AON.CSS.aonBlockCenter());
			Button hai = new Button(AON.MSG.accept());
			
			if (isMobile()) {
				mobileAcceptButton(hai);
			} else {
				desktopAcceptButton(hai);
			}
			
			Button iie = new Button(AON.MSG.cancelAction());
			hp.setWidth("50%");
			hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			if (!isMobile()) {
				desktopCancelButton(iie);
				hp.add(iie);
			}
			hp.add(hai);
			registrationTable.setWidget(3, 0, hp);
			
			AonDialog dial = dialog;
			
			hai.addClickHandler(handler -> {
				NORDIGEN_SERVICE.addAccount(opt.getDomainName(), opt.getDomain(), opt.getUser(), opt.getConfiguration().getToken(), nordigenBankAccount, new AsyncCallback<NordigenRequisition>() {

					@Override
					public void onFailure(Throwable caught) {
						
						RegistryBank rbank = nordigenBankAccount.getRbank();
						String bic = AonStringUtils.substring(rbank != null ? rbank.getBic() : "", 0, 8);
						
						errLabel.setText("No se pudo detectar la entidad bancaria, por favor, el\u00EDjala manualmente:");
						registrationTable.setWidget(4, 0, errLabel);
						
						NORDIGEN_SERVICE.getNordigenInstitutionsByBic(opt.getConfiguration().getToken(), bic, new AsyncCallback<List<NordigenInstitution>>() {

							@Override
							public void onFailure(Throwable caught) {
								
								ListBox countryList = new ListBox();
								for (Country country : Country.values()) {
									countryList.addItem(country.getName(), country.getIso2());
								}
								int spaIndex = Arrays.asList(Country.values()).indexOf(Country.ES);
								countryList.setSelectedIndex(spaIndex);
								countryChange(opt, countryList, registrationTable, nordigenBankAccount);
								
								countryList.addChangeHandler((ev) -> {
									countryChange(opt, countryList, registrationTable, nordigenBankAccount);
								});
								
								registrationTable.setWidget(5, 0, countryList);
							}

							@Override
							public void onSuccess(List<NordigenInstitution> result) {
								ListBox availableBanksList = new ListBox();
								for (NordigenInstitution institution : result) {
									availableBanksList.addItem(institution.getName() + " - " + institution.getBic(), institution.getId());
								}
								
								availableBanksList.addChangeHandler((ev) -> {
									String instId = availableBanksList.getSelectedValue();
									NordigenInstitution selectedInst = result.stream().filter(inst -> AonStringUtils.equals(instId, inst.getId())).findFirst().orElse(null);
									nordigenBankAccount.setInstitution(selectedInst);
								});
								
								registrationTable.setWidget(5, 0, availableBanksList);
							}
							
						});
						
					}

					@Override
					public void onSuccess(NordigenRequisition result) {
						if (registrationTable != null) {
							drawShit(opt, dial, nordigenBankAccount, result, registrationTable);
								
						}
						Window.open(result.getLink(), "REGISTRO DE CUENTA", "_blank");
						nordigenBankAccount.setInstitution(null);
						
					}
				
				
				});
			});
			
			iie.addClickHandler(handler -> {
				if (dial != null)
					dial.hide();
				
			});
			if (dial != null) {
				dialog.center();
				dialog.show();
			}
		}
		
	}
	
	private void drawShit(NordigenModuleOptions opt, AonDialog dial, NordigenBankAccount nordigenBankAccount,NordigenRequisition result, FlexTable registrationTable) {
		dial.setAutoHideEnabled(false);
		int rowSize = registrationTable.getRowCount();
		for (int i=1; i<rowSize; i++) {
			try {
				registrationTable.removeRow(i);
			} catch (Exception e) {}
		}
		Label bankCheckLabel = new Label("Esperando por la vinculaci\u00F3n del banco");
		registrationTable.setWidget(2, 0, bankCheckLabel);
		
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				NORDIGEN_SERVICE.getRequisition(opt.getConfiguration().getToken(), result.getId(), new AsyncCallback<NordigenRequisition>() {

					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(NordigenRequisition result) {
						if (NORDIGEN_REQUISITION_STATUS.LN.equals(result.getStatus())) {
							cancel();
							reloadPage(opt, () -> {
								dial.hide();
							});
						}
					}
				});
			}
		};
		
		timer.scheduleRepeating(5000);
		
		Button cancelLink = new Button(AON.MSG.cancelAction());
		HorizontalPanel hp = new HorizontalPanel();
		registrationTable.setWidget(3, 0, hp);
		hp.setWidth("50%");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		if (!isMobile()) {
			desktopCancelButton(cancelLink);
			hp.add(cancelLink);
		}
		registrationTable.setWidget(3, 0, cancelLink);
		cancelLink.addClickHandler((ev) -> {
			
			timer.cancel();
			NORDIGEN_SERVICE.cancelRequisition(
					opt.getConfiguration().getToken(),
					opt.getDomainName(),
					opt.getDomain(),
					opt.getUser(),
					nordigenBankAccount.getRbank().getId(),
					new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							reloadPage(opt, () -> {
								if (dial != null) {
									dial.hide();
								}								
							});
						}

						@Override
						public void onSuccess(Boolean result) {
							reloadPage(opt, () -> {
								if (dial != null) {
									dial.hide();
								}								
							});
						}
					});
			
		});
	}
	
	@FunctionalInterface
	private interface ReloadPageCallback {
		void run();
	}
	
	private void reloadPage(NordigenModuleOptions opt, ReloadPageCallback callback) {
		NORDIGEN_SERVICE.getConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<NordigenConfiguration>() {
			@Override
			public void onSuccess(NordigenConfiguration result) {
				callback.run();
				toolbar.removeFromParent();
				centerPanel.clear();
				opt.setConfiguration(result);
				loadModule( opt, true );
			}
			
			@Override
			public void onFailure(Throwable caught) {
				toolbar.removeFromParent();
				centerPanel.clear();
				opt.setConfiguration(new NordigenConfiguration());
				loadModule( opt, true );
			}
		});
	}
	
	private String obtainStringProperty(JSONObject jsonObj, String property) {
		if (jsonObj != null) {
			JSONValue idValue = jsonObj.get(property);
			if (idValue != null) {
				JSONString idStr = idValue.isString();
				if (idStr != null) {												
					return idStr.stringValue();
				}
			}
		}
		return null;
	}
	private Integer obtainIntProperty(JSONObject jsonObj, String property) {
		if (jsonObj != null) {
			JSONValue idValue = jsonObj.get(property);
			if (idValue != null) {
				JSONNumber idStr = idValue.isNumber();
				if (idStr != null) {												
					return (int) idStr.doubleValue();
				}
			}
		}
		return null;
	}
	
	private void mobileAcceptButton(Button hai) {
		hai.setWidth("50%");
		hai.setHeight("35px");
		
		Style style = hai.getElement().getStyle();
		style.setProperty("background", AON_BLUE);
		style.setProperty("color", "white");
		style.setProperty("fontSize", "1rem");
		style.setProperty("fontWeight", "700");
		style.setProperty("borderRadius", "6px");
		style.setProperty("boxShadow", "0 2px 4px rgb(0 0 0 / 15%)");
		style.setProperty("transition", "background .25s ease-in-out,transform .15s ease");
	}
	
	private FlexTable getBottomCardMessage(FlexTable bottomTable, Widget message, boolean updateError) {
		bottomTable.clear();
		if (updateError) {
			FlexTable updateTable = new FlexTable();
			Label updateErrorLabel = new Label("Error de actualizaci\u00F3n");
			updateErrorLabel.addStyleName(AON.CSS.aonColorRed());
			updateTable.setWidget(1, 1, updateErrorLabel);
			Label errorIcon = new Label();
			errorIcon.addStyleName(AON.CSS.aonIconLabel());
			errorIcon.addStyleName(AON.CSS.aonIconInvalid());
			updateTable.setWidget(1, 0, errorIcon);
			bottomTable.setWidget(1, 0, updateTable);
		}
		bottomTable.setWidget(0, 0, message);
		bottomTable.addStyleName(AON.CSS.aonBlockCenter());
		return bottomTable;
	}
	
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(event -> {
			minimizedByUser = true;
			closeFootPanel();
		});
		footPanel.addMaximizeHandler(event -> openFootPanel());
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		sessionLog = new FlowPanel();
		footPanel.addStyleName(AON.AON_CSS.aonBackgroundWhite());
		
		tabLayout.add(sessionLog, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.information(), AON.CSS.aonIconHistory()));
		
		footPanel.add(tabLayout);
		
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(event -> {
			minimizedByUser = false;
			openFootPanelIfNeeded();
		});
		
		return footPanel; 
	}
	
	private void closeFootPanel() {
		centerLayoutPanel.setWidgetSize(footPanel, 30);
		centerLayoutPanel.animate(500);
	}
	
	private void openFootPanelIfNeeded() {
		if (!minimizedByUser && centerLayoutPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel();
		}
	}
	
	private void openFootPanel() {
		
		int effectiveHeigth = 3;
		centerLayoutPanel.setWidgetSize(footPanel, (double)Window.getClientHeight() / effectiveHeigth);
		centerLayoutPanel.animate(500);
	}
	
	private void getMovements(FlexTable tab, NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount, Date startDate, Date endDate, String periodStr, CustomDialog ...dialog) {
		NordigenConfiguration conf = opt.getConfiguration();
		NordigenAccessToken token = conf != null ? conf.getToken() : null;
		NORDIGEN_SERVICE.getMovements(token, opt.getDomainName(), opt.getDomain(), opt.getUser(), nordigenBankAccount, startDate, new AsyncCallback<List<NordigenBankStatement>>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.info(caught.getMessage());
				if (!isMobile()) {
					Label errorLabel = new Label("Se produjo un error al obtener los movimientos " + (periodStr != null ? periodStr : ""));
					errorLabel.addStyleName(AON.CSS.aonColorRed());
					sessionLog.add(errorLabel);
					openFootPanel();
				}
			}

			@Override
			public void onSuccess(List<NordigenBankStatement> result) {
				tab.removeAllRows();
				completeMovementsTable(tab, result, periodStr);
				if (dialog != null && dialog.length > 0) {
					for (CustomDialog dial : dialog) {
						dial.center();
					}
				}
			}
		});
	}
	
	private void completeMovementsTable(FlexTable tab, List<NordigenBankStatement> statements, String periodStr) {
		periodStr = periodStr != null ? periodStr : "";
		if (statements != null && !statements.isEmpty()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat("d MMM | EEEE");
			
			Stream<Date> orderedDates = statements.stream().filter(Objects::nonNull).map(NordigenBankStatement::getOperationDate).sorted((d1, d2) -> {
				if (d1 == null || d2 == null)
					return -1;
				return d2.compareTo(d1);
			}).distinct();
			
			orderedDates.forEach(dte -> {
				String parsedDate = dte != null ? dtf.format(dte).toUpperCase() : "";
				
				int nextRow = tab.getRowCount();
				Label dateLabel = new Label(parsedDate);
				dateLabel.addStyleName(AON.CSS.aonFontMedium());
				dateLabel.addStyleName(AON.CSS.aonBold());
				dateLabel.getElement().getStyle().setColor(AON_BLUE);
				dateLabel.getElement().getStyle().setMarginLeft(1, Unit.EM);
				dateLabel.setWidth("100%");
				dateLabel.getElement().getStyle().setMarginTop(12, Unit.PX);
				dateLabel.getElement().getStyle().setMarginBottom(8, Unit.PX);
				tab.setWidget(nextRow, 0, dateLabel);
				tab.getElement().setAttribute("cellSpacing", "0");
				
				List<NordigenBankStatement> st = statements.stream()
				.filter(pen -> {
					if (pen != null && dte != null)
						return dte.equals(pen.getOperationDate());
					else {
						return pen == null && dte == null;
					}
				})
				.sorted((o1, o2) -> o1.getNordigenMovementId().compareTo(o2.getNordigenMovementId()))
				.collect(Collectors.toList());
				
				for (int i=0; i< st.size(); i++) {
					NordigenBankStatement mov = st.get(i);
					getPendingMovementTag(tab, mov, i == st.size() - 1);
				}
			});
			
			boolean sugoiChiisai = Window.getClientWidth() < 350;
			
			tab.setWidth("100%");
			tab.getColumnFormatter().setWidth(0, sugoiChiisai ? "55%" : "60%");
			tab.getColumnFormatter().setWidth(1, sugoiChiisai ? "45%" : "40%");
		} else {
			Label lbl = new Label("No hay movimientos disponibles " + periodStr);
			lbl.addStyleName(AON.CSS.aonTextCenter());
			lbl.addStyleName(AON.CSS.aonFontMedium());			
			lbl.addStyleName(AON.CSS.aonMargin());
			lbl.getElement().getStyle().setColor(AON_BLUE);
			tab.getFlexCellFormatter().setColSpan(0, 0, 2);
			tab.setWidget(0, 0, lbl);
		}
		
	}
	
//	private FlexTable getMovements(NordigenBankAccount nordigenBankAccount) {
//		FlexTable tab = new FlexTable();
//		List<NordigenBankStatement> statements = nordigenBankAccount.getPending();
//		completeMovementsTable(tab, statements, null);
//		return tab;
//	}
	
	private void getPendingMovementTag(FlexTable table, NordigenBankStatement bankStatement, boolean last) {
		
		String description = bankStatement.getDescription();
		Double amount = !bankStatement.isPayment() ? bankStatement.getAmount() : bankStatement.getAmount() * (-1);
		Double balance = bankStatement.getCurrentBalance();
		
		FlowPanel amountsFlow = new FlowPanel();
		
		
		Label amountLabel = new Label(AON.FMT.format(amount) + " " + EURO);
		amountLabel.addStyleName(AON.CSS.aonFontMedium());
		amountLabel.addStyleName(AON.CSS.aonBold());
		amountLabel.addStyleName(AON.CSS.aonTextRight());
		amountLabel.getElement().getStyle().setPaddingRight(1, Unit.EM);
		amountLabel.getElement().getStyle().setMarginTop(1, Unit.EM);
		if (balance == null)
			amountLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
		amountLabel.setWidth("100%");
		if (amount < 0) {
			amountLabel.addStyleName(AON.CSS.aonColorRed());
		} else if (amount > 0) {
			amountLabel.addStyleName(AON.CSS.aonColorGreen());			
		}
		
		
		amountsFlow.add(amountLabel);
		Label balanceLabel = new Label();
		if (balance != null) {
			if (!bankStatement.isPending()) {
				balanceLabel.setText(AON.FMT.format(balance) + " " + EURO);				
			} else {
				balanceLabel.setText("No consolidado");
			}
			amountsFlow.add(balanceLabel);
			balanceLabel.addStyleName(AON.CSS.aonFontLarger());
			balanceLabel.addStyleName(AON.CSS.aonTextRight());
			balanceLabel.getElement().getStyle().setPaddingRight(1, Unit.EM);
			balanceLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
			balanceLabel.setWidth("100%");
		}
		
		
		Label descriptionLabel = new Label(description);
		descriptionLabel.setStyleName(AON.CSS.aonFontLarger());
		descriptionLabel.setWidth("100%");
		descriptionLabel.getElement().getStyle().setMarginLeft(1, Unit.EM);
		descriptionLabel.getElement().getStyle().setMarginTop(1, Unit.EM);
		descriptionLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
		AtomicBoolean multiple = new AtomicBoolean(true);
		ClickHandler linesHandler = event -> {
			descriptionLabel.getElement().getStyle().setProperty("overflow", multiple.get() ? "" : "hidden");
			descriptionLabel.getElement().getStyle().setProperty("text-overflow", multiple.get() ? "" : "ellipsis");
			descriptionLabel.getElement().getStyle().setProperty("display", multiple.get() ? "" : "-webkit-box");
			descriptionLabel.getElement().getStyle().setProperty("-webkit-line-clamp", multiple.get() ? "" : "2");
			descriptionLabel.getElement().getStyle().setProperty("-webkit-box-orient", multiple.get() ? "" : "vertical");
			multiple.set(!multiple.get());
		};
		
		
		
		if (isMobile()) {
			descriptionLabel.getElement().getStyle().setProperty("overflow", "hidden");
			descriptionLabel.getElement().getStyle().setProperty("text-overflow", "ellipsis");
			descriptionLabel.getElement().getStyle().setProperty("display", "-webkit-box");
			descriptionLabel.getElement().getStyle().setProperty("-webkit-line-clamp", "2");
			descriptionLabel.getElement().getStyle().setProperty("-webkit-box-orient", "vertical");
			
			descriptionLabel.addClickHandler(linesHandler);
			
		}
		
		
		int nextRow = table.getRowCount();
		
//		if (gray)
//			table.getRowFormatter().addStyleName(nextRow, AON.CSS.aonBackgroundLigthGray());
		table.setWidget(nextRow, 0, descriptionLabel);
		table.setWidget(nextRow, 1, amountsFlow);
		Label euskoLabel = new Label();
		euskoLabel.setStyleName(AON.CSS.aonBlockCenter());
		
		String borderStyle = "dotted";
		
		if (!last) {			
			euskoLabel.getElement().getStyle().setMarginLeft(1, Unit.EM);
			euskoLabel.getElement().getStyle().setMarginRight(1, Unit.EM);
		} else {
			borderStyle = "solid";
		}
		euskoLabel.getElement().getStyle().setProperty("borderBottom", "2px " + borderStyle + " DarkGray");
		table.setWidget(nextRow + 1, 0, euskoLabel);
		table.getFlexCellFormatter().setColSpan(nextRow + 1, 0, 2);
		
	}
	
	private void desktopCancelButton(Button iie) {
		iie.setWidth("100px");
		iie.setHeight("30px");
		
		Style style = iie.getElement().getStyle();
		style.setProperty("padding", "2px");
		style.setProperty("text-transform", "none");
		style.setProperty("background", "white");
		style.setProperty("color", AON_BLUE);
		style.setProperty("fontSize", "1rem");
		style.setProperty("fontWeight", "700");
		style.setProperty("border", "2px solid " + AON_BLUE);
		style.setProperty("borderRadius", "6px");
		style.setProperty("transition", "background .25s ease-in-out,transform .15s ease");
		style.setProperty("margin-right", "2.5px");
		
		iie.addMouseOverHandler(ev -> {
			style.setProperty("background", HOVER_COLOR);
			style.setProperty("color", "white");
			style.setProperty("border", "none");
		});
		
		iie.addMouseOutHandler(ev -> {
			style.setProperty("background", "white");
			style.setProperty("color", AON_BLUE);
			style.setProperty("border", "2px solid " + AON_BLUE);
		});
	}

	private void desktopAcceptButton(Button hai) {
		hai.setWidth("100px");
		hai.setHeight("30px");
		
		Style style = hai.getElement().getStyle();
		style.setProperty("padding", "2px");
		style.setProperty("text-transform", "none");
		style.setProperty("background", AON_BLUE);
		style.setProperty("color", "white");
		style.setProperty("fontSize", "1rem");
		style.setProperty("fontWeight", "700");
		style.setProperty("border", "none");
		style.setProperty("borderRadius", "6px");
		style.setProperty("transition", "background .25s ease-in-out,transform .15s ease");
		style.setProperty("margin-left", "2.5px");
		
		hai.addMouseOverHandler(ev -> {
			style.setProperty("background", HOVER_COLOR);
		});
		
		hai.addMouseOutHandler(ev -> {
			style.setProperty("background", AON_BLUE);
		});
	}
	
	private String formatIban(String iban) {
		if (iban != null && iban.length() == 24) {
			return iban.substring(0, 4) + " " 
					+ iban.substring(4, 8) + " " 
					+ iban.substring(8, 12) + " "
					+ iban.substring(12, 14) + " "
					+iban.substring(14);
		}
		return iban;
	}
	
	public static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (AonStringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}

	private static boolean isMobile() {
		String userAgent = AonStringUtils.trimToEmpty(Window.Navigator.getUserAgent());
		String platform = AonStringUtils.trimToEmpty(Window.Navigator.getPlatform());
		return AonStringUtils.containsIgnoreCase(userAgent, "mobile") || AonStringUtils.containsIgnoreCase(platform, "mobile");
	}

}		
