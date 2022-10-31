package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLoadingPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankStatement;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLog;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
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

public class CheckItModule extends MainEntryPoint {
	private static final Logger LOGGER = Logger.getLogger(CheckItModule.class.getName());
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
	
	
	private static CheckItServiceAsync CHECKIT_SERVICE;
	
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
		CheckItModuleOptions options = new CheckItModuleOptions()
				.setParentWidget(root)
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser())
		;
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final CheckItModuleOptions opt ) {
		ensureGwtSelector();
		AON.ensureInjected();
		CheckItServiceAsync serviceRaw = GWT.create(CheckItService.class);
		CHECKIT_SERVICE = new CheckItServiceAsyncDecorator(serviceRaw);
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		if ( opt.getConfiguration() == null) {
			CHECKIT_SERVICE.getConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<CheckItConfiguration>() {
				@Override
				public void onSuccess(CheckItConfiguration result) {
					opt.setConfiguration(result);
					loadModule( opt, false );
				}
				
				@Override
				public void onFailure(Throwable caught) {
					opt.setConfiguration(new CheckItConfiguration().setDown(true));
					loadModule( opt, false );
				}
			});
		} else {
			loadModule( opt, false );
		}
	}
	
	private void loadModule( final CheckItModuleOptions opt , boolean reload) {
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

	private FlowPanel paintEnterpiseData(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		CheckItConfiguration conf = opt.getConfiguration();
		if (conf.getEnterpriseId() != null) {
			if (!isMobile())
				paintRegistrationData( opt );
			linkedBanks = paintBanks(opt);
			unlinkedBanks = paintUnlinkedBanks(opt);
			panel.add(linkedBanks);
			panel.add(unlinkedBanks);
		} else {
			panel.add( paintRegistration( opt ) );
		}
		return panel;
	}
	

	private Widget paintRegistration(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonTextCenter());
		panel.addStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonBlockCenter());
		panel.addStyleName(AON.CSS.aonBorder());
		panel.addStyleName(AON.CSS.aonPadding());
		if (!opt.getConfiguration().isDown() && !opt.getConfiguration().isRegistrationFailed()) {
			CHECKIT_SERVICE.saveEnterpriseData(
					opt.getDomainName()
					, opt.getDomain()
					, opt.getUser()
					, new AsyncCallback<Integer>() {
						@Override
						public void onSuccess(Integer result) {
							opt.getConfiguration().setEnterpriseId(result);
							dockLayoutPanel.clear();
							onModuleLoad();
							firstTime = true;
						}
						
						@Override
						public void onFailure(Throwable caught) {
							opt.getConfiguration().setRegistrationFailed(true);
							Label errLabel =new Label(caught.getMessage());
							errLabel.addStyleName(AON.CSS.aonColorRed());
							sessionLog.add(errLabel);
							openFootPanel();
						}	
				});
		} else if (opt.getConfiguration().isRegistrationFailed()){
			InlineLabel label = new InlineLabel("Se produjo un error al registrar la empresa en el servicio de agregador bancario.");
			panel.add( label );
		} else {
			InlineLabel label = new InlineLabel("Servicio temporalmente no disponible. Disculpe las molestias.");
			panel.add( label );			
		}
		return panel;
	}

	private void paintRegistrationData(CheckItModuleOptions opt) {
		AonToolbarButton config = new AonToolbarButton(AON.MSG.information(), AON.CSS.aonIconAudit());
		toolbar.add(config);
		config.addClickHandler(event -> {
			
			CustomDialog dialog = new CustomDialog();
			
			dialog.setAutoHideEnabled(true);
			dialog.setCaption(AON.MSG.information());
			
			FlowPanel panel = new FlowPanel();
			panel.getElement().getStyle().setProperty("minWidth", "175px");
			panel.getElement().getStyle().setProperty("minHeight", "80px");
			FlexTable idTable = new FlexTable();
			idTable.addStyleName(AON.CSS.aonMargin());
			idTable.setWidth("100%");
			InlineLabel label = new InlineLabel("Identificador de empresa:");
			label.addStyleName(AON.CSS.aonTextLeft());
			label.setStyleName(AON.CSS.aonBold());
			label.setStyleName(AON.CSS.aonTableLabel());
			idTable.setWidget(0, 0, label );
			InlineLabel enterpriseIdLabel = new InlineLabel(AonNumberUtils.toString(opt.getConfiguration().getEnterpriseId()));
			enterpriseIdLabel.addStyleName(AON.CSS.aonTextRight());
			enterpriseIdLabel.addStyleName(AON.CSS.aonMarginRight());
			idTable.setWidget(0, 1, enterpriseIdLabel);
			panel.add(idTable);
			
			dialog.add(panel);
			dialog.center();
			dialog.show();
		});
	}

	private Widget paintBanks(CheckItModuleOptions opt) {
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
		if (opt.getConfiguration().getCheItBanks() != null && !opt.getConfiguration().getCheItBanks().isEmpty()) {
			AonCards cards = new AonCards();
			cards.addStyleName(AON.CSS.aonBlockCenter());
			double balanceTotal = 0;
			double remainderTotal = 0;
			boolean logs = false;
			for (CheckItBankAccount bankAccount : opt.getConfiguration().getCheItBanks()) {
				
				boolean areLogs = (bankAccount.getLogs() != null
						&& !bankAccount.getLogs().isEmpty()
						&& bankAccount.getLogs().stream().anyMatch(f -> AonDateUtils.compare(f.getCreated(), bankAccount.getAtDate()) > 0));
				
				if(areLogs) {
					if (!isMobile()) {
						for (CheckItLog log : bankAccount.getLogs()) {
							if (log.getCreated() != null) {
								if (AonDateUtils.compare(bankAccount.getAtDate(), log.getCreated()) < 0) {									
									String date = AON.DATE_FORMAT.format(log.getCreated());
									Label logLabel = new Label(date +": Error en " + bankAccount.getBank() + " " + bankAccount.getCcc() + " - " + log.getErrorMessage());
									logLabel.setStyleName(AON.CSS.aonColorRed());
									sessionLog.add(logLabel);
								}
							}
						}
					}
					logs = true;
				}
				balanceTotal = balanceTotal + bankAccount.getBalance();
				remainderTotal = remainderTotal + bankAccount.getRemainder();
				cards.addCard( new AonCheckItBankCard(opt, bankAccount) );
				
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
			table.setWidget(1, 0, remainderLabel);
			table.setWidget(1, 1, remainderBox);
			
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
	
	private Widget paintUnlinkedBanks(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonTextCenter());
		panel.addStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonBlockCenter());
		panel.addStyleName(AON.CSS.aonPaddingBottom());
		panel.addStyleName(AON.CSS.aonPaddingTop());
		if (opt.getConfiguration().getCheItBanks() != null && !opt.getConfiguration().getCheckItUnlinkedBanks().isEmpty()) {
			
			Label unlinkedTitle = new Label("Cuentas no vinculadas");
			unlinkedTitle.addStyleName(AON.CSS.aonTextLeft());
			unlinkedTitle.addStyleName(AON.CSS.aonFontMedium());
			unlinkedTitle.addStyleName(AON.CSS.aonBold());
			panel.add(unlinkedTitle);
			
			AonCards cards = new AonCards();
			for (CheckitUnlinkedBankAccount unlinkedBankAccount : opt.getConfiguration().getCheckItUnlinkedBanks()) {
				cards.addCard( new AonCheckItUnlinkedBankCard(opt, unlinkedBankAccount) );
			}
			panel.add(cards);
		}
		return panel;
	}
	
	private class AonCheckItBankCard extends AonCard {
		
		boolean updateError;

		private AonCheckItBankCard(final CheckItModuleOptions opt, CheckItBankAccount checkItBankAccount) {
			
			updateError = false;
			
			
			boolean areLogs = (checkItBankAccount.getLogs() != null
					&& !checkItBankAccount.getLogs().isEmpty()
					&& checkItBankAccount.getLogs().stream().anyMatch(f -> AonDateUtils.compare(f.getCreated(), checkItBankAccount.getAtDate()) > 0));
			
			FlowPanel titlePanel = new FlowPanel();
			titlePanel.addStyleName(AON.CSS.aonTextCenter());
			if (checkItBankAccount.getLogo() != null && !checkItBankAccount.getLogo().isEmpty()) {
				Image logoImg = new Image(checkItBankAccount.getLogo());
				logoImg.setHeight("40px");
				titlePanel.add(logoImg);
			} else {
				Label title = new Label();
				title.addStyleName(AON.CSS.aonBorderNone());
				title.setText(AonStringUtils.abbreviate(checkItBankAccount.getBank(), 26));
				String bankTitle = checkItBankAccount.getBank();
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
			ibanBox.setText(formatIban(checkItBankAccount.getCcc()));
			ibanPanel.addStyleName(AON.CSS.aonMarginBottom());
			ibanPanel.add(ibanBox);
			
			FlowPanel atDateLabelPanel = new FlowPanel();
			InlineLabel atDateLabel = new InlineLabel("\u00DAltima actualizaci\u00F3n ");
			atDateLabel.setStyleName(AON.CSS.aonTableLabel());
			atDateLabelPanel.add(atDateLabel);
			
			FlowPanel atDatePanel = new FlowPanel();
			InlineLabel atDateBox = new InlineLabel();
			InlineLabel atDateBox2 = new InlineLabel();
			atDateBox.addStyleName(AON.CSS.aonFontMedium());
			atDateBox.setText( checkItBankAccount.getAtDate() == null ? "----" : AON.DATE_FORMAT.format( checkItBankAccount.getAtDate()));
			atDateBox2.setText( checkItBankAccount.getAtDate() == null ? "" : " hora: " + DATE_HOURS.format( checkItBankAccount.getAtDate()));
			
			atDatePanel.addStyleName(AON.CSS.aonMarginBottom());
			
			
			if (checkItBankAccount.getAtDate() != null && CalendarUtil.getDaysBetween(checkItBankAccount.getAtDate(), new Date()) >= 4) {
				atDateBox.addStyleName(AON.CSS.aonColorRed());
				updateError = true;
				int noUpDays = CalendarUtil.getDaysBetween(checkItBankAccount.getAtDate(), new Date());
				FlowPanel updateErrorPanel = new FlowPanel();
				Label updateErrorLbl = new Label("Error de actualizaci\u00F3n en cuenta " + checkItBankAccount.getBank() + " - " +checkItBankAccount.getCcc() + ": la cuenta lleva " + noUpDays + " d\u00EDas sin actualizarse");
				updateErrorLbl.addStyleName(AON.CSS.aonColorRed());
				Label updateErrorLbl2 = new Label("Acceda a su banca online para verificar el acceso");
				updateErrorLbl2.addStyleName(AON.CSS.aonMarginLeft());
				updateErrorPanel.add(updateErrorLbl);
				updateErrorPanel.add(updateErrorLbl2);
				if (!isMobile()) {
					sessionLog.add(updateErrorPanel);
					openFootPanel();
				}
				
			} else if (checkItBankAccount.getAtDate() != null){
				atDateBox.addStyleName(AON.CSS.aonColorGreen());				
			}
			atDatePanel.add(atDateBox);
			atDatePanel.add(atDateBox2);
			
			FlowPanel balanceLabelPanel = new FlowPanel();
			InlineLabel balanceLabel = new InlineLabel("Saldo ");
			balanceLabel.setStyleName(AON.CSS.aonTableLabel());
			balanceLabelPanel.add(balanceLabel);
			
			FlowPanel balancePanel = new FlowPanel();
			InlineLabel balanceBox = new InlineLabel();
			balanceBox.addStyleName(AON.CSS.aonFontMedium());
			balanceBox.addStyleName(AON.CSS.aonBold());
			if (AonMathUtils.isLessThanZero( checkItBankAccount.getBalance())) {
				balanceBox.addStyleName(AON.CSS.aonColorRed());	
			}
			balanceBox.setText( AON.FMT.format( checkItBankAccount.getBalance()) + " " + EURO);
			balancePanel.addStyleName(AON.CSS.aonMarginBottom());
			balancePanel.add(balanceBox);

			FlowPanel remainderLabelPanel = new FlowPanel();
			InlineLabel remainderLabel = new InlineLabel("Disponible ");
			remainderLabel.setStyleName(AON.CSS.aonTableLabel());
			remainderLabelPanel.add(remainderLabel);

			FlowPanel remainderPanel = new FlowPanel();
			InlineLabel remainderBox = new InlineLabel();
			if (AonMathUtils.isLessThanZero( checkItBankAccount.getRemainder())) {
				balanceBox.addStyleName(AON.CSS.aonColorRed());	
			}
			remainderBox.setText( AON.FMT.format( checkItBankAccount.getRemainder()) + " " + EURO);
			remainderPanel.addStyleName(AON.CSS.aonMarginBottom());
			remainderPanel.add(remainderBox);

			body.add(ibanPanel);
			
			body.add(atDateLabelPanel);
			body.add(atDatePanel);
			
			body.add(balanceLabelPanel);
			body.add(balancePanel);
			
			body.add(remainderLabelPanel);
			body.add(remainderPanel);
			
			if (Window.getClientWidth() < 675) {				
				this.getElement().getStyle().setProperty("minWidth", "100%");
			} else {
				this.getElement().getStyle().setProperty("minWidth", "260px");
			}
			body.addStyleName(AON.CSS.aonTextCenter());
			
			int pendingMovements = checkItBankAccount.getPending() != null ? checkItBankAccount.getPending().size() : 0;
			
			FlowPanel movText = new FlowPanel();
			
			ClickHandler clickHandler = event -> {
				FlowPanel panel = new FlowPanel();
				panel.getElement().getStyle().setProperty("minWidth", "230px");
				
				FlowPanel movFlow = null;
				CustomDialog dialog = null;
				
				if (isMobile()) {
					AonToolbarButton back = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack()); 
					toolbar.setTitle("MOVIMIENTOS");
					toolbar.add(back);
					centerPanel.clear();
					back.addClickHandler(h -> {
						toolbar.remove(back);
						centerPanel.clear();
						loadModule(opt, true);
					});
					
					
					movFlow = new FlowPanel();
					centerPanel.add(movFlow);
				} else {					
					dialog = new CustomDialog();
					dialog.setAutoHideEnabled(true);
					
					dialog.setCaption("MOVIMIENTOS PENDIENTES");
				}
				
				
				HorizontalPanel closeImport = new HorizontalPanel();
				closeImport.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				closeImport.addStyleName(AON.CSS.aonPaddingTop());
				closeImport.addStyleName(AON.CSS.aonBlockCenter());
				closeImport.addStyleName(AON.CSS.aonPaddingBottom());
				
				
//				Button close = new Button(AON.MSG.close());
//				close.setStyleName(AON.CSS.aonMarginRight());
//				close.addClickHandler(e -> dialog.hide());
				Button importBtn = aonImportButton();
				
				
				CustomDialog dial = dialog;
				importBtn.addClickHandler(e ->
					CHECKIT_SERVICE.insertTransactions(
							opt.getDomainName()
							, opt.getDomain()
							, opt.getUser()
							, opt.getConfiguration().getEnterpriseId()
							, checkItBankAccount
							, new AsyncCallback<Integer>() {
								@Override
								public void onSuccess(Integer result) {
									String singPlur = (result != 1) ? " nuevos movimientos insertados en " : " nuevo movimiento insertado en ";
									
									Label label = new Label(result + singPlur + checkItBankAccount.getBank() + " - " + formatIban(checkItBankAccount.getCcc()));
									label.addStyleName(AON.CSS.aonColorGreen());
									sessionLog.add(label);
									openFootPanel();										
									
									if (result != null && result != 0) {
										getBottomCardMessage(bottomTable
												, new Label("No hay movimientos pendientes")
												, updateError
												, areLogs);
										checkItBankAccount.setPending(Collections.emptyList());
									}
									if (!isMobile())
										dial.hide();
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Label errLabel = new Label(caught.getMessage());
									errLabel.setStyleName(AON.CSS.aonColorRed());
									sessionLog.add(errLabel);
									openFootPanel();
									if (!isMobile())
										dial.hide();
								}	
						}));
				
//				closeImport.add(close);
				
				if (checkItBankAccount.getPending() != null && !checkItBankAccount.getPending().isEmpty())
					closeImport.add(importBtn);
				
				FlowPanel movementsFlow = new FlowPanel();
				movementsFlow.setWidth(isMobile() ? "100%" : "90%");
				movementsFlow.setStyleName(AON.CSS.aonBlockCenter());
				
				FlowPanel topInfo = new FlowPanel();
				topInfo.setWidth("100%");
				
				Image logoImg = new Image(checkItBankAccount.getLogo());
				logoImg.setHeight("40px");
				logoImg.addStyleName(AON.AON_CSS.aonDisplayBlock());
				logoImg.addStyleName(AON.CSS.aonBlockCenter());
				Label ibanLbl = new Label(formatIban(checkItBankAccount.getCcc()));
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
				if (AonMathUtils.isLessThanZero(checkItBankAccount.getBalance())) {
					movBalanceBox.addStyleName(AON.CSS.aonColorRed());
				}
				movBalanceBox.setText( AON.FMT.format(checkItBankAccount.getBalance()) + " " + EURO);
				movBalancePanel.addStyleName(AON.CSS.aonMarginBottom());
				movBalancePanel.addStyleName(AON.CSS.aonTextCenter());
				movBalancePanel.add(movBalanceBox);
				topInfo.add(movBalancePanel);
				
				topInfo.addStyleName(AON.CSS.aonMarginBottom());
				
				movementsFlow.add(topInfo);
				
				if (isMobile() || (checkItBankAccount.getPending() != null && !checkItBankAccount.getPending().isEmpty())) {
					FlowPanel periodMovContainer = new FlowPanel();
					if (isMobile())
						buildCustomMovementsPanel(periodMovContainer, opt, checkItBankAccount);
					else
						periodMovContainer.add(getMovements(checkItBankAccount));
					movementsFlow.add(periodMovContainer);
//					movementsFlow.add(getMovements(checkItBankAccount, isMobile()));
				} else {
					Label noMovLbl = new Label("No hay movimientos pendientes");
					noMovLbl.setWidth("100%");
					noMovLbl.setStyleName(AON.CSS.aonTextCenter());
					movementsFlow.add(noMovLbl);
						
				}
				ScrollPanel movementsPanel = new ScrollPanel(movementsFlow);
				movementsPanel.addStyleName(AON.CSS.aonCustomScroll());
				if (isMobile()) {
					movementsPanel.setWidth("100%");
				} else {
					
					movementsPanel.getElement().getStyle().setProperty("minWidth", "700px");
					movementsPanel.addStyleName(AON.CSS.aonMarginTop());
					movementsPanel.getElement().getStyle().setProperty("maxHeight", "40vh");
				}
				panel.add(movementsPanel);
				if (isMobile()) {
					movFlow.add(panel);
					centerPanel.getElement().getStyle().setPadding(5, Unit.PX);
				} else {					
					panel.add(closeImport);
					dialog.add(panel);
					dialog.center();
					dialog.show();
				}
			};
			
			
			
			
//			if (!isMobile())
			body.addDomHandler(clickHandler, ClickEvent.getType());
//			title.addDomHandler(clickHandler, ClickEvent.getType());
			
			bottomTable.addStyleName(AON.CSS.aonBlockCenter());
			
			Widget msgWidget = null;
			if (!isMobile() && pendingMovements > 0) {
				
				String singPlur = pendingMovements != 1 ? " nuevos movimientos" : " nuevo movimiento";
				
				InlineLabel pending1Label = new InlineLabel("Hay ");
				InlineLabel pending2Label = new InlineLabel(AonNumberUtils.toString(pendingMovements));
				pending2Label.setStyleName(AON.CSS.aonBold());
				InlineLabel pending3Label = new InlineLabel(singPlur);
				
				
				movText.add(pending1Label);
				movText.add(pending2Label);
				movText.add(pending3Label);
				
				movText.addStyleName(AON.CSS.aonTextCenter());
				movText.addStyleName(AON.CSS.aonColorGreen());
				msgWidget = movText;
//				bottomTable.setWidget(0, 0, movText);
			} else if (!isMobile()) {
				Label noMovLbl = new Label("No hay movimientos pendientes");
				msgWidget = noMovLbl;
				
//				bottomTable.setWidget(0, 0, noMovLbl);
				
			}
			
			getBottomCardMessage(bottomTable, msgWidget, updateError, areLogs);
			body.add(bottomTable);				
			
			
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton(AON.MSG.importAction(), AON.CSS.aonIconImport());
			saveButton.setTabIndex(-2);
			saveButton.addClickHandler(event ->
				CHECKIT_SERVICE.insertTransactions(
						opt.getDomainName()
						, opt.getDomain()
						, opt.getUser()
						, opt.getConfiguration().getEnterpriseId()
						, checkItBankAccount
						, new AsyncCallback<Integer>() {
							@Override
							public void onSuccess(Integer result) {
									String singPlur = (result != 1) ? " nuevos movimientos insertados en " : " nuevo movimiento insertado en ";
									
									Label label = new Label(result + singPlur + checkItBankAccount.getBank() + " - " + formatIban(checkItBankAccount.getCcc()));
									label.addStyleName(AON.CSS.aonColorGreen());
									sessionLog.add(label);
									openFootPanel();
								
								if (result != null && result != 0) {
									getBottomCardMessage(bottomTable
											, new Label("No hay movimientos pendientes")
											, updateError
											, areLogs);
//									if (!updateError) {
//										body.remove(bottomTable);
//										body.add(new Label("No hay movimientos pendientes"));										
//									}
									checkItBankAccount.setPending(Collections.emptyList());
								}
								
							}
							
							@Override
							public void onFailure(Throwable caught) {
								Label errLabel = new Label(caught.getMessage());
								errLabel.setStyleName(AON.CSS.aonColorRed());
								sessionLog.add(errLabel);
								openFootPanel();
							}	
					}));
			
			AonTableButton modifyButton = new AonTableButton("Editar credenciales", AON.CSS.aonIconEdit());
			modifyButton.setTabIndex(-2);
			
			ClickHandler credentialHandler = (event -> {
				Integer loginType = checkItBankAccount.getBankLoginType();
				Integer enterpriseId = opt.getConfiguration().getEnterpriseId();
				
				CHECKIT_SERVICE.getCredentials(enterpriseId, loginType, new AsyncCallback<CheckItLoginFields>() {

					@Override
					public void onFailure(Throwable caught) {
						paintCredentialAdd(checkItBankAccount, opt);
						
					}

					@Override
					public void onSuccess(CheckItLoginFields result) {
						paintCredentialEdit(result, opt);
						
					}
					
					private void paintCredentialAdd (CheckItBankAccount checkItBankAccount, CheckItModuleOptions opt) {
						AonTextBox userID = new AonTextBox();
						AonTextBox userPassword = new AonTextBox();
						AonTextBox userPIN = new AonTextBox();
						
						
						FlowPanel flow = new FlowPanel();
						AonDialog dialog = null;
						FlowPanel newCredFlow = null;
						
						if (isMobile()) {
							AonToolbarButton back = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack()); 
							
							toolbar.setTitle("CREDENCIALES");
							toolbar.add(back);
							centerPanel.clear();
							back.addClickHandler(h -> {
//								toolbar.remove(back);
								centerPanel.clear();
								loadModule(opt, true);
							});
							
							
							newCredFlow = new FlowPanel();
							
							FlowPanel topInfo = new FlowPanel();
							topInfo.setWidth("100%");
							
							Image logoImg = new Image(checkItBankAccount.getLogo());
							logoImg.setHeight("40px");
							logoImg.addStyleName(AON.AON_CSS.aonDisplayBlock());
							logoImg.addStyleName(AON.CSS.aonBlockCenter());
							Label ibanLbl = new Label(formatIban(checkItBankAccount.getCcc()));
							ibanLbl.addStyleName(AON.CSS.aonTextCenter());
							topInfo.add(logoImg);
							topInfo.add(ibanLbl);
							topInfo.addStyleName(AON.CSS.aonMarginBottom());
							
							
							
							newCredFlow.add(topInfo);
							newCredFlow.add(flow);
							newCredFlow.addStyleName(AON.AON_CSS.aonDisplayBlock());
							newCredFlow.addStyleName(AON.CSS.aonBlockCenter());
							if (isMobile() && Window.getClientWidth() > 500) {
								newCredFlow.setWidth("60%");
							}
							centerPanel.add(newCredFlow);
							
							
							
						} else {
							dialog = new AonDialog("A\u00F1adir credenciales", flow);
							dialog.setAutoHideEnabled(true);							
						}
						AonDialog dial = dialog;
						CHECKIT_SERVICE.getFields(checkItBankAccount.getBankLoginType(), new AsyncCallback<CheckItLoginFields>() {

							@Override
							public void onFailure(Throwable caught) {
								Label errLabel = new Label("Error: se produjo un error inesperado al obtener las credenciales");
								errLabel.addStyleName(AON.CSS.aonColorRed());
								if (!isMobile()) {									
									sessionLog.add(errLabel);
									openFootPanel();
								}
							}

							@Override
							public void onSuccess(CheckItLoginFields result) {
								int nextRow = 0;
								
								FlexTable fieldsTable = new FlexTable();
								
								
								
								if (result.getUserID() != null && !result.getUserID().isEmpty()) {
									if (result.getUserIDInput() != null)
										userID.setValue(result.getUserIDInput());
									
									if (isMobile()) {
										fieldsTable.setWidth("100%");
										FlowPanel option = new FlowPanel();
										Label optionName = new Label(result.getUserID() + ":");
										optionName.addStyleName(AON.CSS.aonMarginTop());
										optionName.setWidth("100%");
										userID.setWidth("100%");
										userID.setHeight("3em");
										userID.getElement().getStyle().setProperty("borderRadius", "5px");
										option.add(optionName);
										option.add(userID);
										fieldsTable.setWidget(nextRow++, 0, option);
									} else {										
										fieldsTable.setWidget(nextRow, 0, new Label(result.getUserID() + ":"));
										fieldsTable.setWidget(nextRow++, 1, userID);
									}
									
								}
								if (result.getUserPassword() != null && !result.getUserPassword().isEmpty()) {
									if (isMobile()) {
										fieldsTable.setWidth("100%");
										FlowPanel option = new FlowPanel();
										Label optionName = new Label(result.getUserPassword() + ":");
										optionName.addStyleName(AON.CSS.aonMarginTop());
										optionName.setWidth("100%");
										userPassword.setWidth("100%");
										userPassword.setHeight("3em");
										userPassword.getElement().getStyle().setProperty("borderRadius", "5px");
										option.add(optionName);
										option.add(userPassword);
										fieldsTable.setWidget(nextRow++, 0, option);
									} else {										
										fieldsTable.setWidget(nextRow, 0, new Label(result.getUserPassword() + ":"));
										fieldsTable.setWidget(nextRow++, 1, userPassword);
									}
								}
								if (result.getUserPIN() != null && !result.getUserPIN().isEmpty()) {
									if (isMobile()) {
										fieldsTable.setWidth("100%");
										FlowPanel option = new FlowPanel();
										Label optionName = new Label(result.getUserPIN() + ":");
										optionName.addStyleName(AON.CSS.aonMarginTop());
										optionName.setWidth("100%");
										userPIN.setWidth("100%");
										userPIN.setHeight("3em");
										userPIN.getElement().getStyle().setProperty("borderRadius", "5px");
										option.add(optionName);
										option.add(userPIN);
										fieldsTable.setWidget(nextRow, 0, option);
									} else {										
										fieldsTable.setWidget(nextRow, 0, new Label(result.getUserPIN() + ":"));
										fieldsTable.setWidget(nextRow, 1, userPIN);
									}
								}	
								
								flow.add(fieldsTable);
								
								
								HorizontalPanel hp = new HorizontalPanel();
								Button hai = new Button(AON.MSG.accept());
								Button iie = new Button(AON.MSG.cancelAction());
								hp.setWidth("50%");
								hp.addStyleName(AON.CSS.aonBlockCenter());
								hp.addStyleName(AON.CSS.aonMarginTop());
								hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
								if (!isMobile())
									hp.add(iie);
								hp.add(hai);
								if (isMobile())
									mobileAcceptButton(hai);
								flow.add(hp);
								
								Label errorLbl = new Label();
								errorLbl.addStyleName(AON.CSS.aonColorRed());
								errorLbl.addStyleName(AON.CSS.aonBlockCenter());
								errorLbl.addStyleName(AON.CSS.aonTextCenter());
								errorLbl.addStyleName(AON.CSS.aonMarginTop());
								flow.add(errorLbl);
								
								iie.addClickHandler(e -> dial.hide());
								hai.addClickHandler(e -> {
									result.setUserIDInput(userID.getValue());
									result.setUserPasswordInput(userPassword.getValue());
									result.setUserPINInput(userPIN.getValue());
									CHECKIT_SERVICE.editCredentials(opt.getConfiguration().getEnterpriseId(), result, new AsyncCallback<Boolean>() {

										@Override
										public void onFailure(Throwable caught) {
											String errMsg = caught.getMessage();
											errorLbl.setText(errMsg);
										}

										@Override
										public void onSuccess(Boolean result) {
											Label successLabel = null;
											if (result != null && result) {
												successLabel = new Label("Se han actualizado correctamente las credenciales");
												successLabel.addStyleName(AON.CSS.aonColorGreen());
											} else {
												
												successLabel = new Label("Error: no se pudieron actualizar las credenciales");
												successLabel.addStyleName(AON.CSS.aonColorRed());
												
											}
											if (!isMobile()) {
												sessionLog.add(successLabel);
												openFootPanel();
												dial.hide();
											} else {
												centerPanel.clear();
												loadModule(opt, true);
											}
										}
									});
								});
								if (!isMobile()) {
									dial.center();
									dial.show();
								}
								
							}
						});
						
						
					}
					
					private void paintCredentialEdit (CheckItLoginFields checkitLoginFields, CheckItModuleOptions opt) {
						AonTextBox userID = new AonTextBox();
						AonTextBox userPassword = new AonTextBox();
						AonTextBox userPIN = new AonTextBox();
						
						FlowPanel flow = new FlowPanel();
						FlowPanel credentialFlow = null;
						AonDialog dialog = null;
						
						if (isMobile()) {
							
							AonToolbarButton back = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack()); 
							toolbar.setTitle("CREDENCIALES");
							toolbar.add(back);
							centerPanel.clear();
							back.addClickHandler(h -> {
								toolbar.remove(back);
								centerPanel.clear();
								loadModule(opt, true);
							});
							
							
							credentialFlow = new FlowPanel();
							
							FlowPanel topInfo = new FlowPanel();
							topInfo.setWidth("100%");
							
							Image logoImg = new Image(checkItBankAccount.getLogo());
							logoImg.setHeight("40px");
							logoImg.addStyleName(AON.AON_CSS.aonDisplayBlock());
							logoImg.addStyleName(AON.CSS.aonBlockCenter());
							Label ibanLbl = new Label(formatIban(checkItBankAccount.getCcc()));
							ibanLbl.addStyleName(AON.CSS.aonTextCenter());
							topInfo.add(logoImg);
							topInfo.add(ibanLbl);
							topInfo.addStyleName(AON.CSS.aonMarginBottom());
							
							
							
							credentialFlow.add(topInfo);
							credentialFlow.add(flow);
							credentialFlow.addStyleName(AON.AON_CSS.aonDisplayBlock());
							credentialFlow.addStyleName(AON.CSS.aonBlockCenter());
							if (isMobile() && Window.getClientWidth() > 500) {
								credentialFlow.setWidth("60%");
							}
							centerPanel.add(credentialFlow);
						} else {							
							dialog = new AonDialog("Editar credenciales", flow);
							dialog.setAutoHideEnabled(true);
						}
						
						int nextRow = 0;
						
						FlexTable fieldsTable = new FlexTable();
						if (!isMobile()) {
							fieldsTable.getElement().getStyle().setProperty("marginLeft", "auto");
							fieldsTable.getElement().getStyle().setProperty("marginRight", "auto");
						}
						
						if (checkitLoginFields.getUserID() != null && !checkitLoginFields.getUserID().isEmpty()) {
							if (checkitLoginFields.getUserIDInput() != null)
								userID.setValue(checkitLoginFields.getUserIDInput());
							
							if (isMobile()) {
								fieldsTable.setWidth("100%");
								FlowPanel option = new FlowPanel();
								Label optionName = new Label(checkitLoginFields.getUserID() + ":");
								optionName.addStyleName(AON.CSS.aonMarginTop());
								optionName.setWidth("100%");
								userID.setWidth("100%");
								userID.setHeight("3em");
								userID.getElement().getStyle().setProperty("borderRadius", "5px");
								option.add(optionName);
								option.add(userID);
								fieldsTable.setWidget(nextRow++, 0, option);
							} else {
								Label userIdLabel = new Label(checkitLoginFields.getUserID() + ":");
								userIdLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
								fieldsTable.setWidget(nextRow, 0, userIdLabel);
								fieldsTable.setWidget(nextRow++, 1, userID);
							}
							
						}
						if (checkitLoginFields.getUserPassword() != null && !checkitLoginFields.getUserPassword().isEmpty()) {
							if (isMobile()) {
								fieldsTable.setWidth("100%");
								FlowPanel option = new FlowPanel();
								Label optionName = new Label(checkitLoginFields.getUserPassword() + ":");
								optionName.addStyleName(AON.CSS.aonMarginTop());
								optionName.setWidth("100%");
								userPassword.setWidth("100%");
								userPassword.setHeight("3em");
								userPassword.getElement().getStyle().setProperty("borderRadius", "5px");
								option.add(optionName);
								option.add(userPassword);
								fieldsTable.setWidget(nextRow++, 0, option);
							} else {			
								Label userPasswordLabel = new Label(checkitLoginFields.getUserPassword() + ":");
								userPasswordLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
								fieldsTable.setWidget(nextRow, 0, userPasswordLabel);
								fieldsTable.setWidget(nextRow++, 1, userPassword);
							}
						}
						if (checkitLoginFields.getUserPIN() != null && !checkitLoginFields.getUserPIN().isEmpty()) {
							if (isMobile()) {
								fieldsTable.setWidth("100%");
								FlowPanel option = new FlowPanel();
								Label optionName = new Label(checkitLoginFields.getUserPIN() + ":");
								optionName.addStyleName(AON.CSS.aonMarginTop());
								optionName.setWidth("100%");
								userPIN.setWidth("100%");
								userPIN.setHeight("3em");
								userPIN.getElement().getStyle().setProperty("borderRadius", "5px");
								option.add(optionName);
								option.add(userPIN);
								fieldsTable.setWidget(nextRow, 0, option);
							} else {
								Label userPINLabel = new Label(checkitLoginFields.getUserPIN() + ":");
								userPINLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
								fieldsTable.setWidget(nextRow, 0, userPINLabel);
								fieldsTable.setWidget(nextRow, 1, userPIN);
							}
						}	
						
						flow.add(fieldsTable);
						
						HorizontalPanel hp = new HorizontalPanel();
						Button hai = new Button(AON.MSG.accept());
						if (isMobile()) {
							mobileAcceptButton(hai);
						} else {
							desktopAcceptButton(hai);
						}
						
						Button iie = new Button(AON.MSG.cancelAction());
						hp.setWidth(isMobile() ? "100%" : "50%");
						hp.addStyleName(AON.CSS.aonBlockCenter());
						hp.addStyleName(AON.CSS.aonMarginTop());
						hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
						if (!isMobile()) {
							desktopCancelButton(iie);	
							hp.add(iie);
						}
						
						hp.add(hai);
						flow.add(hp);
						if (dialog != null)
							dialog.center();
						
						Label errorLbl = new Label();
						errorLbl.addStyleName(AON.CSS.aonColorRed());
						errorLbl.addStyleName(AON.CSS.aonBlockCenter());
						errorLbl.addStyleName(AON.CSS.aonTextCenter());
						errorLbl.addStyleName(AON.CSS.aonMarginTop());		
						flow.add(errorLbl);
						AonDialog dial = dialog;
						if (dialog != null) {
							iie.addClickHandler(e -> dial.hide());
						}
							
							
						hai.addClickHandler(e -> {
							checkitLoginFields.setUserIDInput(userID.getValue());
							checkitLoginFields.setUserPasswordInput(userPassword.getValue());
							checkitLoginFields.setUserPINInput(userPIN.getValue());
							
							CHECKIT_SERVICE.editCredentials(opt.getConfiguration().getEnterpriseId(), checkitLoginFields, new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									String errMsg = caught.getMessage();
									errorLbl.setText(errMsg);
								}

								@Override
								public void onSuccess(Boolean result) {
									if (result != null && result) {
										if (isMobile()) {
											centerPanel.clear();
											loadModule(opt, true);
										} else {											
											Label successLabel = new Label("Se han actualizado correctamente las credenciales");
											successLabel.addStyleName(AON.CSS.aonColorGreen());
											sessionLog.add(successLabel);
											openFootPanel();
										}
									} else {
										Label successLabel = new Label("Error: no se pudieron actualizar las credenciales");
										successLabel.addStyleName(AON.CSS.aonColorGreen());
										sessionLog.add(successLabel);
										openFootPanel();
									}
									if (dial != null)
										dial.hide();
								}
							
							});
						});
						if (dialog != null)
							dialog.show();
					}
					
				});
				
				
			});
			
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
				importBtn.addClickHandler(e ->
					CHECKIT_SERVICE.insertTransactions(
							opt.getDomainName()
							, opt.getDomain()
							, opt.getUser()
							, opt.getConfiguration().getEnterpriseId()
							, checkItBankAccount
							, new AsyncCallback<Integer>() {
								@Override
								public void onSuccess(Integer result) {
									String singPlur = (result != 1) ? " nuevos movimientos insertados en " : " nuevo movimiento insertado en ";
									
									Label label = new Label(result + singPlur + checkItBankAccount.getBank() + " - " + formatIban(checkItBankAccount.getCcc()));
									label.addStyleName(AON.CSS.aonColorGreen());
									sessionLog.add(label);
									openFootPanel();										
									
									if (result != null && result != 0) {
										getBottomCardMessage(bottomTable
												, new Label("No hay movimientos pendientes")
												, updateError
												, areLogs);
										checkItBankAccount.setPending(Collections.emptyList());
									}
									dial.hide();
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Label errLabel = new Label(caught.getMessage());
									errLabel.setStyleName(AON.CSS.aonColorRed());
									sessionLog.add(errLabel);
									openFootPanel();
										dial.hide();
								}	
						}));
				
				
				FlowPanel movementsFlow = new FlowPanel();
				movementsFlow.setWidth(isMobile() ? "100%" : "90%");
				movementsFlow.setStyleName(AON.CSS.aonBlockCenter());
				
				FlowPanel topInfo = new FlowPanel();
				topInfo.setWidth("100%");
				
				Image logoImg = new Image(checkItBankAccount.getLogo());
				logoImg.setHeight("40px");
				logoImg.addStyleName(AON.AON_CSS.aonDisplayBlock());
				logoImg.addStyleName(AON.CSS.aonBlockCenter());
				Label ibanLbl = new Label(formatIban(checkItBankAccount.getCcc()));
				ibanLbl.addStyleName(AON.CSS.aonTextCenter());
				topInfo.add(logoImg);
				topInfo.add(ibanLbl);
				topInfo.addStyleName(AON.CSS.aonMarginBottom());
				
				movementsFlow.add(topInfo);
				
				
				/**Filtros de fecha**/
				
				FlowPanel periodMovContainer = new FlowPanel();
				
				buildCustomMovementsPanel(periodMovContainer, opt, checkItBankAccount, dialog);
				
				
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
			
			
			
			modifyButton.addClickHandler(credentialHandler);
			
			if (!isMobile())
				getMenuPanel().add(saveButton);
			getMenuPanel().add(modifyButton);
			if (!isMobile())
				getMenuPanel().add(allMovementsButton);
			
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
	
	private void buildCustomMovementsPanel(FlowPanel container, CheckItModuleOptions opt, CheckItBankAccount checkItBankAccount, CustomDialog ...dialog) {
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
				customMovChange(movementContainer, opt, checkItBankAccount, listBoxMonthFrom, listBoxYearFrom, listBoxMonthTo, listBoxYearTo, firstYear, monthNames, dialog);
			} else {
				if (periodFlow.getWidgetCount() > 1)
					periodFlow.remove(1);
				int days = Integer.parseInt(periodSelector.getSelectedValue());
				Date from = new Date();
				CalendarUtil.addDaysToDate(from, -days);
				
				String periodStr = "en los \u00FAltimos " + days + "d\u00EDas";
				
				getMovements(movementContainer, opt, checkItBankAccount, from, new Date(), periodStr, dialog);
			}
		});
		
		
		int days = Integer.parseInt(periodSelector.getSelectedValue());
		Date from = new Date();
		CalendarUtil.addDaysToDate(from, -days);
		String periodStr = "en los \u00FAltimos " + days + "d\u00EDas";
		getMovements(movementContainer, opt, checkItBankAccount, from, new Date(), periodStr, dialog);
		
		ChangeHandler onDateChange = ev -> customMovChange(movementContainer, opt, checkItBankAccount, listBoxMonthFrom, listBoxYearFrom, listBoxMonthTo, listBoxYearTo, firstYear, monthNames, dialog);
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
	
	
	private void customMovChange(FlexTable movementContainer, CheckItModuleOptions opt, CheckItBankAccount checkItBankAccount, ListBox listBoxMonthFrom, ListBox listBoxYearFrom, ListBox listBoxMonthTo, ListBox listBoxYearTo, int firstYear, String[] monthNames, CustomDialog ...dialog) {
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
		
		getMovements(movementContainer, opt, checkItBankAccount, dateFrom, dateTo, periodStr, dialog);
	}
	
	private void drawBankLoginFields(AonTextBox userID, AonTextBox userPassword,
			AonTextBox userPIN, CheckitUnlinkedBankAccount bankAccount, FlexTable fieldsTable, CheckItModuleOptions opt) {
		bankAccount.setCredentials(true);
		CheckItLoginFields loginType = bankAccount.getLogin();
		
		Set<Integer> savedLogins = opt.getConfiguration()
		.getCheItBanks()
		.stream()
		.map(CheckItBankAccount::getBankLoginType)
		.collect(Collectors.toSet());

		int nextRow = fieldsTable.getRowCount();
		
		if (savedLogins.contains(loginType.getId())) {
			bankAccount.setCredentials(false);
			FlowPanel credPanel = new FlowPanel();
			credPanel.setStyleName(AON.CSS.aonTextCenter());
			credPanel.addStyleName(AON.CSS.aonMarginBottom());
			Label existsLabel = new Label("Ya hay credenciales guardadas para este login.");
			Anchor updateAnchor = new Anchor("Pulse aqu\u00ED si desea actualizarlas");
			updateAnchor.addStyleName(AON.CSS.aonTextUnderline());
			updateAnchor.addClickHandler(event -> {
				bankAccount.setCredentials(true);
				fieldsTable.removeRow(nextRow);
				fieldsTable.remove(credPanel);
				drawLoginFields(userID, userPassword, userPIN, loginType, fieldsTable, nextRow);
			});
			credPanel.add(existsLabel);
			credPanel.add(updateAnchor);
			
			fieldsTable.getFlexCellFormatter().setColSpan(nextRow, 0, 2);
			fieldsTable.setWidget(nextRow, 0, credPanel);
		} else {
			bankAccount.setCredentials(true);
			drawLoginFields(userID, userPassword, userPIN, loginType, fieldsTable, nextRow);
		}
	}

	private void drawLoginFields(AonTextBox userID, AonTextBox userPassword, AonTextBox userPIN,
			CheckItLoginFields loginType, FlexTable fieldsTable, int nextRow) {
		if (loginType.getUserID() != null && !loginType.getUserID().isEmpty()) {
			
			if (isMobile()) {
				if (Window.getClientWidth() > 500) {					
					fieldsTable.setWidth("60%");
				} else {					
					fieldsTable.setWidth("100%");
				}
				FlowPanel option = new FlowPanel();
				Label optionName = new Label(loginType.getUserID() + ":");
				optionName.addStyleName(AON.CSS.aonMarginTop());
				optionName.setWidth("100%");
				userID.setWidth("100%");
				userID.setHeight("3em");
				userID.getElement().getStyle().setProperty("borderRadius", "5px");
				option.add(optionName);
				option.add(userID);
				fieldsTable.setWidget(nextRow++, 0, option);
			} else {								
				fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserID() + ":"));
				fieldsTable.setWidget(nextRow++, 1, userID);
			}
			
		}
		if (loginType.getUserPassword() != null && !loginType.getUserPassword().isEmpty()) {
			if (isMobile()) {
				if (Window.getClientWidth() > 500) {					
					fieldsTable.setWidth("60%");
				} else {					
					fieldsTable.setWidth("100%");
				}
				FlowPanel option = new FlowPanel();
				Label optionName = new Label(loginType.getUserPassword() + ":");
				optionName.addStyleName(AON.CSS.aonMarginTop());
				optionName.setWidth("100%");
				userPassword.setWidth("100%");
				userPassword.setHeight("3em");
				userPassword.getElement().getStyle().setProperty("borderRadius", "5px");
				option.add(optionName);
				option.add(userPassword);
				fieldsTable.setWidget(nextRow++, 0, option);
			} else {								
				fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserPassword() + ":"));
				fieldsTable.setWidget(nextRow++, 1, userPassword);
			}
		}
		if (loginType.getUserPIN() != null && !loginType.getUserPIN().isEmpty()) {
			
			if (isMobile()) {
				if (Window.getClientWidth() > 500) {					
					fieldsTable.setWidth("60%");
				} else {					
					fieldsTable.setWidth("100%");
				}
				FlowPanel option = new FlowPanel();
				Label optionName = new Label(loginType.getUserPIN() + ":");
				optionName.addStyleName(AON.CSS.aonMarginTop());
				optionName.setWidth("100%");
				userPIN.setWidth("100%");
				userPIN.setHeight("3em");
				userPIN.getElement().getStyle().setProperty("borderRadius", "5px");
				option.add(optionName);
				option.add(userPIN);
				fieldsTable.setWidget(nextRow, 0, option);
			} else {								
				fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserPIN() + ":"));
				fieldsTable.setWidget(nextRow, 1, userPIN);
			}
			
		}
	}
	
	
	private class AonCheckItUnlinkedBankCard extends AonCard {
		
		
		private AonCheckItUnlinkedBankCard(final CheckItModuleOptions opt, CheckitUnlinkedBankAccount checkItUnlinkedBankAccount) {
			InlineLabel title = new InlineLabel();
			title.addStyleName(AON.CSS.aonBorderNone());
			title.setText(checkItUnlinkedBankAccount.getBank());
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
			ibanBox.setText(formatIban(checkItUnlinkedBankAccount.getIban()));
			ibanPanel.add(ibanBox);
			
			
			body.add(ibanPanel);
			
			
			
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton("Vincular", AON.CSS.aonIconLink());
			
			saveButton.setTabIndex(-2);
			saveButton.addClickHandler(event -> paintBankRegistration(opt, checkItUnlinkedBankAccount));
			getMenuPanel().add(saveButton);
		}
		
		private void paintBankRegistration(CheckItModuleOptions opt, CheckitUnlinkedBankAccount checkItUnlinkedBankAccount) {
			
			AonTextBox userID = new AonTextBox();
			AonTextBox userPassword = new AonTextBox();
			AonTextBox userPIN = new AonTextBox();
			
			Label errLabel = new Label();
			
			
			AonDialog dialog = null;
			FlowPanel mobilePanel = null;
			
			
			
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
			
			
			Label bankLabel = new Label(checkItUnlinkedBankAccount.getBank());
			bankLabel.addStyleName(AON.AON_BOLD);
			bankLabel.getElement().getStyle().setProperty("margin-bottom", "1.5em");
			bankLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			bankLabel.getElement().getStyle().setFontSize(isMobile() ? 1.5 : 2, Unit.EM);
			bankLabel.addStyleName(AON.AON_NO_MARGIN);
			registrationTable.setWidget(0, 0, bankLabel);
			
			Label ibanLabel = new Label(formatIban(checkItUnlinkedBankAccount.getIban()));
			ibanLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			ibanLabel.getElement().getStyle().setFontSize(isMobile() ? 1 : 1.75, Unit.EM);
			ibanLabel.addStyleName(AON.CSS.aonMarginBottom());
			registrationTable.setWidget(1, 0, ibanLabel);
			
			FlexTable loginTable = new FlexTable();
			loginTable.setWidth("100%");
			
			
			AonCheckItBankBox bankBox = new AonCheckItBankBox(opt.getDomainName(), opt.getDomain(), opt.getUser(), opt.getConfiguration().getBankIds(), isMobile());
			
			bankBox.setWidth("100%");
			
			FlexTable fieldsTable = new FlexTable();
			if (isMobile()) {
				if (Window.getClientWidth() > 500) {					
					fieldsTable.setWidth("60%");
				} else {					
					fieldsTable.setWidth("100%");
				}
				fieldsTable.addStyleName(AON.CSS.aonBlockCenter());
				registrationTable.setWidth("100%");
				FlowPanel option = new FlowPanel();
				Label optionName = new Label("Entidad:");
				optionName.addStyleName(AON.CSS.aonBold());				
				optionName.addStyleName(AON.CSS.aonMarginTop());
				optionName.setWidth("100%");
				bankBox.setWidth("100%");
				bankBox.setHeight("3em");
				bankBox.getElement().getStyle().setProperty("borderRadius", "5px");
				option.add(optionName);
				option.add(bankBox);
				fieldsTable.setWidget(0, 0, option);
			} else {				
				fieldsTable.addStyleName(AON.AON_CSS.aonJustifyContentSpaceBetween());
				fieldsTable.addStyleName(AON.CSS.aonBlockCenter());
				fieldsTable.setWidget(0, 0, new Label("Entidad:"));
				fieldsTable.setWidget(0, 1, bankBox);
			}
			registrationTable.setWidget(2, 0, fieldsTable);
			
			
			bankBox.addSelectionHandler(e -> {
				errLabel.setText("");
				if (bankBox.getId() != null) {
					CHECKIT_SERVICE.getLogins(bankBox.getId(), new AsyncCallback<List<CheckItLoginFields>>() {

						@Override
						public void onFailure(Throwable caught) {
							toolbar.showErrorMessage(caught.getMessage());
							
						}

						@Override
						public void onSuccess(List<CheckItLoginFields> result) {
							if (result != null && !result.isEmpty()) {
								
								fieldsTable.removeAllRows();
								fieldsTable.clear();
								
								
								if (isMobile()) {
									if (Window.getClientWidth() > 500) {					
										fieldsTable.setWidth("60%");
									} else {					
										fieldsTable.setWidth("100%");
									}
									FlowPanel option = new FlowPanel();
									Label optionName = new Label("Entidad:");
									optionName.addStyleName(AON.CSS.aonBold());				
									optionName.addStyleName(AON.CSS.aonMarginTop());
									optionName.setWidth("100%");
									bankBox.setWidth("100%");
									bankBox.setHeight("3em");
									bankBox.getElement().getStyle().setProperty("borderRadius", "5px");
									option.add(optionName);
									option.add(bankBox);
									fieldsTable.setWidget(0, 0, option);
								} else {				
									fieldsTable.addStyleName(AON.AON_CSS.aonJustifyContentSpaceBetween());
									fieldsTable.addStyleName(AON.CSS.aonBlockCenter());
									fieldsTable.setWidget(0, 0, new Label("Entidad:"));
									fieldsTable.setWidget(0, 1, bankBox);
								}
								
								if (result.size() > 1) {
									ListBox loginDropdown = new ListBox();
									loginDropdown.addItem("Seleccione un login");
									if (checkItUnlinkedBankAccount.getLogin() != null) {
										checkItUnlinkedBankAccount.getLogin().clear();
									}
									
									for (CheckItLoginFields item : result) {
										loginDropdown.addItem(item.getType(), AonNumberUtils.toString(item.getId()));										
									}
									
									
									
									
									Label marginLabel = new Label();
									marginLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
									if (isMobile()) {
										if (Window.getClientWidth() > 500) {					
											fieldsTable.setWidth("60%");
										} else {					
											fieldsTable.setWidth("100%");
										}
										FlowPanel option = new FlowPanel();
										Label optionName = new Label("Tipo de login:");				
										optionName.addStyleName(AON.CSS.aonMarginTop());
										optionName.setWidth("100%");
										loginDropdown.setWidth("100%");
										loginDropdown.setHeight("3em");
										loginDropdown.getElement().getStyle().setProperty("borderRadius", "5px");
										option.add(optionName);
										option.add(loginDropdown);
										fieldsTable.setWidget(1, 0, option);
										fieldsTable.setWidget(2, 0, marginLabel);
									} else {				
										fieldsTable.setWidget(1, 0, new Label("Tipo de login:"));
										fieldsTable.setWidget(1, 1, loginDropdown);
										fieldsTable.setWidget(2, 0, marginLabel);
									}
									
									
									loginDropdown.addChangeHandler(event -> {
										
										while (fieldsTable.getRowCount() > 3) {
											fieldsTable.removeRow(fieldsTable.getRowCount() - 1);
										}
										
										if (loginDropdown.getSelectedIndex() != 0) {
											Integer typeId = AonNumberUtils.toint(loginDropdown.getSelectedValue());
											Optional<CheckItLoginFields> selected = result.stream().filter(f -> f.getId().equals(typeId)).findAny();
											if (selected.isPresent()) {
												CheckItLoginFields loginType = new CheckItLoginFields();
												loginType.clone(selected.get());
												checkItUnlinkedBankAccount.setLogin(loginType);
												
												
												drawBankLoginFields(userID, userPassword, userPIN, checkItUnlinkedBankAccount,
														fieldsTable, opt);	

											}
										} else {
											if (checkItUnlinkedBankAccount != null) {
												checkItUnlinkedBankAccount.getLogin().clear();
											}
										}
										
										
									});
									
								} else {
									CheckItLoginFields loginType = result.get(0);
									checkItUnlinkedBankAccount.setLogin(loginType);
									
									
									Label marginLabel = new Label();
									marginLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
									fieldsTable.setWidget(fieldsTable.getRowCount(), 0, marginLabel);
									
									drawBankLoginFields(userID, userPassword, userPIN, checkItUnlinkedBankAccount,
											fieldsTable, opt);	
									
									
								}
							}
						}
					});
					checkItUnlinkedBankAccount.setBankId(bankBox.getId());
				}
			});
			
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
			errLabel.setStyleName(AON.CSS.aonColorRed());
			errLabel.addStyleName(AON.CSS.aonTextCenter());
			errLabel.addStyleName(AON.CSS.aonMarginTop());
			registrationTable.setWidget(4, 0, errLabel);
			
			AonDialog dial = dialog;
			
			hai.addClickHandler(handler -> {
				//AÑADIR LA CUNETA
				Integer enterpriseId = opt.getConfiguration().getEnterpriseId();
				String user = userID.getValue();
				String pass = userPassword.getValue();
				String pin = userPIN.getValue();
				AonLoadingPanel loadingPanel = new AonLoadingPanel("PROCESANDO...");
				container.add(loadingPanel);
				loadingPanel.show();
				CHECKIT_SERVICE.addAccount(enterpriseId, checkItUnlinkedBankAccount, user, pass, pin, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						String error = caught.getMessage();
						errLabel.setText(error);
						Label errorLabel = new Label("Se ha producido un error al a\u00F1adir la cuenta: \"" + error + "\"");
						errorLabel.setStyleName(AON.CSS.aonColorRed());
						if (!isMobile()) {
							sessionLog.add(errorLabel);
							openFootPanel();
						}
						loadingPanel.hide();
					}

					@Override
					public void onSuccess(String result) {
						if (result != null && AonStringUtils.containsIgnoreCase(result, "cuenta creada")) {
							Label sccsLabel = new Label("A\u00F1adida la cuenta " + checkItUnlinkedBankAccount.getIban());
							Label res = new Label(result);
							sccsLabel.setStyleName(AON.CSS.aonColorGreen());
							if (!isMobile()) {
								sessionLog.add(sccsLabel);								
								sessionLog.add(res);								
								openFootPanel();
							}
							
							CHECKIT_SERVICE.getConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<CheckItConfiguration>() {
								@Override
								public void onSuccess(CheckItConfiguration result) {
									loadingPanel.hide();
									opt.setConfiguration(result);
									if (!isMobile()) {							
										enterpriseData.remove(unlinkedBanks);
										enterpriseData.remove(linkedBanks);
										linkedBanks = paintBanks(opt);
										unlinkedBanks = paintUnlinkedBanks(opt);
										enterpriseData.add(linkedBanks);
										enterpriseData.add(unlinkedBanks);
										dial.hide();
									} else {
										toolbar.remove(0);
										centerPanel.clear();
										loadModule(opt, true);
									}
								}
								
								@Override
								public void onFailure(Throwable caught) {
									loadingPanel.hide();
									dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
									if (!isMobile()) {							
										dial.hide();
									} else {
										toolbar.remove(0);
										centerPanel.clear();
										loadModule(opt, true);
									}
								}
							});
							
							
						} else {
							JSONValue jsonValue = JSONParser.parseStrict(result);
							JSONObject response = jsonValue.isObject();
							
							if (response != null && response.get("extrafield") != null) {
								if (dial != null && dial.isShowing()) {									
									dial.hide();
								}
								chooseContractDialog(response, enterpriseId, checkItUnlinkedBankAccount.getIban(), () -> {
									CHECKIT_SERVICE.getConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<CheckItConfiguration>() {
										@Override
										public void onSuccess(CheckItConfiguration result) {
											loadingPanel.hide();
											opt.setConfiguration(result);
											if (!isMobile()) {							
												enterpriseData.remove(unlinkedBanks);
												enterpriseData.remove(linkedBanks);
												linkedBanks = paintBanks(opt);
												unlinkedBanks = paintUnlinkedBanks(opt);
												enterpriseData.add(linkedBanks);
												enterpriseData.add(unlinkedBanks);
												dial.hide();
											} else {
												toolbar.remove(0);
												centerPanel.clear();
												loadModule(opt, true);
											}
										}
										
										@Override
										public void onFailure(Throwable caught) {
											loadingPanel.hide();
											dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
											if (!isMobile()) {							
												dial.hide();
											} else {
												toolbar.remove(0);
												centerPanel.clear();
												loadModule(opt, true);
											}
										}
									});
								});
								
								
								
								
							} else {
								String error = "Se ha producido un error desconocido";
								errLabel.setText(error);
								Label errorLabel = new Label("Se ha producido un error desconocido al a\u00F1adir la cuenta");
								errorLabel.setStyleName(AON.CSS.aonColorRed());
								if (!isMobile()) {								
									sessionLog.add(errorLabel);
									openFootPanel();
								}	
							}
							loadingPanel.hide();
						}
					}
				});
			});
			
			iie.addClickHandler(handler -> {
				if (dial != null)
					dial.hide();
				checkItUnlinkedBankAccount.setBankId(null);
				checkItUnlinkedBankAccount.setLogin(null);
				
			});
			if (dial != null) {
				dialog.center();
				dialog.show();
			}
		}
		
	}
	
	private static interface ChooseContractCallback {
		public void clearScreen();
	}
	
	private ListBox chooseContractDialog(JSONObject response, Integer enterpriseId, String iban, ChooseContractCallback callback) {
		FlowPanel pnl = new FlowPanel();
		AonDialog dialog = new AonDialog("Seleccione el contrato", pnl);
		JSONValue contractArrayValue = response.get("extrafield");
		JSONArray contractArray = contractArrayValue.isArray();
		ListBox listBox = new ListBox();
		listBox.setWidth("100%");
		if (contractArray != null) {
			for (int i=0; i< contractArray.size(); i++) {
				JSONValue value = contractArray.get(i);
				JSONObject obj = value.isObject();
				Integer id = obtainIntProperty(obj, "account_id");
				String desc = obtainStringProperty(obj, "description");
				listBox.addItem(desc, String.valueOf(id));
			}
		}
		
		Button hai = new Button(AON.MSG.accept());
		if (isMobile()) {
			mobileAcceptButton(hai);
		} else {
			desktopAcceptButton(hai);
		}
		hai.addClickHandler(event -> {
			callback.clearScreen();
			CHECKIT_SERVICE.addExtraField(enterpriseId, iban, listBox.getSelectedValue(), new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					Label errorLabel = new Label("No se ha podido seleccionar el contrato");
					errorLabel.setStyleName(AON.CSS.aonColorRed());
					if (!isMobile()) {
						sessionLog.add(errorLabel);
						openFootPanel();
					}
					dialog.hide();
				}

				@Override
				public void onSuccess(Boolean result) {
					Label errorLabel = new Label();
					if (result != null && result) {
						errorLabel.setText("Se ha seleccionado el contrato");
						errorLabel.setStyleName(AON.CSS.aonColorGreen());						
					} else {
						errorLabel.setText("No se ha podido seleccionar el contrato");
						errorLabel.setStyleName(AON.CSS.aonColorRed());
					}
					if (!isMobile()) {
						sessionLog.add(errorLabel);
						openFootPanel();
					}
					dialog.hide();
				}
			});
		});
		
		hai.getElement().getStyle().setDisplay(Display.BLOCK);
		hai.getElement().getStyle().setMarginTop(1, Unit.EM);
		hai.getElement().getStyle().setProperty("marginLeft", "auto");
		hai.getElement().getStyle().setProperty("marginRight", "auto");
		
		pnl.add(listBox);
		pnl.add(hai);
		
		dialog.center();
		dialog.show();
		
		return listBox;
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
		hai.setWidth("50&");
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
	
	private FlexTable getBottomCardMessage(FlexTable bottomTable, Widget message, boolean updateError, boolean logs) {
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
		if (logs) {
			Label warn = new Label();
			warn.setTitle("Existen errores con esta cuenta");
			warn.addStyleName(AON.CSS.aonIconLabel());
			warn.addStyleName(AON.CSS.aonIconWarning());
			warn.addStyleName(AON.CSS.aonColorRed());
			bottomTable.setWidget(0, 1, warn);
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
	
	private void getMovements(FlexTable tab, CheckItModuleOptions opt, CheckItBankAccount checkItBankAccount, Date startDate, Date endDate, String periodStr, CustomDialog ...dialog) {
		CHECKIT_SERVICE.getMovements(opt.getDomainName(), opt.getDomain(), opt.getUser(), opt.getConfiguration().getEnterpriseId(), checkItBankAccount, startDate, endDate, new AsyncCallback<List<CheckItBankStatement>>() {

			@Override
			public void onFailure(Throwable caught) {
				if (!isMobile()) {
					Label errorLabel = new Label("Se produjo un error al obtener los movimientos " + (periodStr != null ? periodStr : ""));
					errorLabel.addStyleName(AON.CSS.aonColorRed());
					sessionLog.add(errorLabel);
					openFootPanel();
				}
			}

			@Override
			public void onSuccess(List<CheckItBankStatement> result) {
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
	
	private void completeMovementsTable(FlexTable tab, List<CheckItBankStatement> statements, String periodStr) {
		periodStr = periodStr != null ? periodStr : "";
		if (statements != null && !statements.isEmpty()) {
			DateTimeFormat dtf = DateTimeFormat.getFormat("d MMM | EEEE");
			
			Stream<Date> orderedDates = statements.stream().map(CheckItBankStatement::getOperationDate).sorted((d1, d2) -> {
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
				
				List<CheckItBankStatement> st = statements.stream()
				.filter(pen -> {
					if (pen != null && dte != null)
						return dte.equals(pen.getOperationDate());
					else {
						return pen == null && dte == null;
					}
				})
				.sorted((o1, o2) -> o1.getCheckitMovementId().compareTo(o2.getCheckitMovementId()))
				.collect(Collectors.toList());
				
				for (int i=0; i< st.size(); i++) {
					CheckItBankStatement mov = st.get(i);
					getPendingMovementTag(tab, mov, i == st.size() - 1);
				}
			});
			
			boolean sugoiChiisai = Window.getClientWidth() < 350;
			
			tab.setWidth("100%");
			tab.getColumnFormatter().setWidth(0, sugoiChiisai ? "55%" : "60%");
			tab.getColumnFormatter().setWidth(1, sugoiChiisai ? "45" : "40%");
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
	
	private FlexTable getMovements(CheckItBankAccount checkItBankAccount) {
		FlexTable tab = new FlexTable();
		List<CheckItBankStatement> statements = checkItBankAccount.getPending();
		completeMovementsTable(tab, statements, null);
		return tab;
	}
	
	private void getPendingMovementTag(FlexTable table, CheckItBankStatement bankStatement, boolean last) {
		
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
		if (balance != null) {			
			Label balanceLabel = new Label(AON.FMT.format(balance) + " " + EURO);
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
