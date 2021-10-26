package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards.AonCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLog;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.sun.tools.sjavac.Log;

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
					loadModule( opt );					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					opt.setConfiguration(new CheckItConfiguration().setDown(true));
					loadModule( opt );	
				}
			});
		} else {
			loadModule( opt );
		}
	}
	
	private void loadModule( final CheckItModuleOptions opt ) {
		toolbar = new AonToolbar(AON.MSG.chekItModule());
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH );
		centerLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(centerLayoutPanel);
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.add(centerPanel);
		centerLayoutPanel.addSouth(getMinimizePanel(), 30);
		dockLayoutPanel.add(centerLayoutPanel);
		enterpriseData = paintEnterpiseData( opt );
		container.add( enterpriseData );
		
		if (firstTime) {
			Label loadingLabel = new Label("El agregador bancario ha sido inicializado con \u00E9xito");
			loadingLabel.setStyleName(AON.CSS.aonColorGreen());
			sessionLog.add(loadingLabel);
			openFootPanel();
		}
	}

	private FlowPanel paintEnterpiseData(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		CheckItConfiguration conf = opt.getConfiguration();
		if (conf.getEnterpriseId() != null) {
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
						}
						
						@Override
						public void onFailure(Throwable caught) {
							opt.getConfiguration().setRegistrationFailed(true);
						}	
				});
					dockLayoutPanel.clear();
					onModuleLoad();
					firstTime = true;
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
		panel.addStyleName(AON.CSS.aonPadding());
		Label linkedTitle = new Label("Cuentas vinculadas");
		linkedTitle.addStyleName(AON.CSS.aonTextLeft());
		linkedTitle.addStyleName(AON.CSS.aonFontMedium());
		linkedTitle.addStyleName(AON.CSS.aonBold());
		panel.add(linkedTitle);
		if (opt.getConfiguration().getCheItBanks() != null && !opt.getConfiguration().getCheItBanks().isEmpty()) {
			AonCards cards = new AonCards();
			double balanceTotal = 0;
			double remainderTotal = 0;
			boolean logs = false;
			for (CheckItBankAccount bankAccount : opt.getConfiguration().getCheItBanks()) {
				if(bankAccount.getLogs() != null && !bankAccount.getLogs().isEmpty()) {
					for (CheckItLog log : bankAccount.getLogs()) {
						Label logLabel = new Label("Error en " + bankAccount.getBank() + " " + bankAccount.getCcc() + " - " + log.getErrorMessage());
						logLabel.setStyleName(AON.CSS.aonColorRed());
						sessionLog.add(logLabel);
					}
					logs = true;
				}
				balanceTotal = balanceTotal + bankAccount.getBalance();
				remainderTotal = remainderTotal + bankAccount.getRemainder();
				cards.addCard( new AonCheckItBankCard(opt, bankAccount) );
			}
			if (logs) {
				openFootPanel();
			}
			panel.add(cards);
			
			balanceTotal = AonMathUtils.round( balanceTotal );
			remainderTotal = AonMathUtils.round( remainderTotal );
			
			FlowPanel totals = new FlowPanel();
			panel.setStyleName(AON.CSS.aonTextCenter());
			panel.addStyleName(AON.CSS.aonWidthAlmostAll());
			panel.addStyleName(AON.CSS.aonMarginTop());
			panel.addStyleName(AON.CSS.aonBlockCenter());
			panel.addStyleName(AON.CSS.aonPadding());
			
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
			panel.add(totals);
		}
		return panel;
	}
	
	private Widget paintUnlinkedBanks(CheckItModuleOptions opt) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonTextCenter());
		panel.addStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonBlockCenter());
		panel.addStyleName(AON.CSS.aonPadding());
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
			boolean areLogs = (checkItBankAccount.getLogs() != null && !checkItBankAccount.getLogs().isEmpty());
			
			FlowPanel titlePanel = new FlowPanel();
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
			
			
			if (checkItBankAccount.getAtDate() != null && CalendarUtil.getDaysBetween(checkItBankAccount.getAtDate(), new Date()) >= 3) {
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
				sessionLog.add(updateErrorPanel);
				openFootPanel();
				
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
			
			int pendingMovements = checkItBankAccount.getPending() != null ? checkItBankAccount.getPending().size() : 0;
			
			FlowPanel movText = new FlowPanel();
			
			ClickHandler clickHandler = event -> {
				FlowPanel panel = new FlowPanel();
				panel.getElement().getStyle().setProperty("minWidth", "230px");
				
				CustomDialog dialog = new CustomDialog();
				dialog.setAutoHideEnabled(true);
				
				dialog.setCaption("MOVIMIENTOS PENDIENTES");
				
				HorizontalPanel closeImport = new HorizontalPanel();
				closeImport.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				closeImport.addStyleName(AON.CSS.aonPaddingTop());
				closeImport.addStyleName(AON.CSS.aonBlockCenter());
				closeImport.addStyleName(AON.CSS.aonPaddingBottom());
				
				
//				Button close = new Button(AON.MSG.close());
//				close.setStyleName(AON.CSS.aonMarginRight());
//				close.addClickHandler(e -> dialog.hide());
				Button importBtn = new Button("Importar");
				importBtn.setStyleName(AON.CSS.aonMarginLeft());
				
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
//										getBottomCardMessage(new Label("No hay movimientos pendientes"), Optional.empty(), updateError);
//										if (!updateError) {
										getBottomCardMessage(bottomTable
												, new Label("No hay movimientos pendientes")
												, updateError
												, areLogs);
//										body.remove(bottomTable);											
//										body.add(new Label("No hay movimientos pendientes"));
										
//										}
										checkItBankAccount.setPending(Collections.emptyList());
									}
									dialog.hide();
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Label errLabel = new Label(caught.getMessage());
									errLabel.setStyleName(AON.CSS.aonColorRed());
									sessionLog.add(errLabel);
									openFootPanel();
									dialog.hide();
								}	
						}));
				
//				closeImport.add(close);
				
				if (checkItBankAccount.getPending() != null && !checkItBankAccount.getPending().isEmpty())
					closeImport.add(importBtn);
				
				FlowPanel movementsFlow = new FlowPanel();
				movementsFlow.setWidth("90%");
				movementsFlow.setStyleName(AON.CSS.aonBlockCenter());
				if (checkItBankAccount.getPending() != null && !checkItBankAccount.getPending().isEmpty()) {
					int i = 1;
					for (BankStatement bankStatement : checkItBankAccount.getPending()) {
						FlexTable tag = getPendingMovementTag(bankStatement);
						if (i++%2!=0)
							tag.setStyleName(AON.CSS.aonBackgroundLigthGray());
						tag.setWidth("100%");
						movementsFlow.add(tag);
					}
				} else {
					Label noMovLbl = new Label("No hay movimientos pendientes");
					noMovLbl.setWidth("100%");
					noMovLbl.setStyleName(AON.CSS.aonTextCenter());
					movementsFlow.add(noMovLbl);
						
				}
				ScrollPanel movementsPanel = new ScrollPanel(movementsFlow);
				movementsPanel.getElement().getStyle().setProperty("minWidth", "700px");
				movementsPanel.addStyleName(AON.CSS.aonMarginTop());
				movementsPanel.getElement().getStyle().setProperty("maxHeight", "40vh");
				panel.add(movementsPanel);
				panel.add(closeImport);
				dialog.add(panel);
				dialog.center();
				dialog.show();
			};
			body.addDomHandler(clickHandler, ClickEvent.getType());
//			title.addDomHandler(clickHandler, ClickEvent.getType());
			
			bottomTable.addStyleName(AON.CSS.aonBlockCenter());
			
			Widget msgWidget = null;
			if (pendingMovements > 0) {
				
				
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
			} else {
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
			modifyButton.addClickHandler(event -> {
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
						AonDialog dialog = new AonDialog("A\u00F1adir credenciales", flow);
						dialog.setAutoHideEnabled(true);
						
						CHECKIT_SERVICE.getFields(checkItBankAccount.getBankLoginType(), new AsyncCallback<CheckItLoginFields>() {

							@Override
							public void onFailure(Throwable caught) {
								Label errLabel = new Label("Error: se produjo un error inesperado al obtener las credenciales");
								errLabel.addStyleName(AON.CSS.aonColorRed());
								sessionLog.add(errLabel);
								openFootPanel();
							}

							@Override
							public void onSuccess(CheckItLoginFields result) {
								int nextRow = 0;
								
								FlexTable fieldsTable = new FlexTable();
								
								
								
								if (result.getUserID() != null && !result.getUserID().isEmpty()) {
									if (result.getUserIDInput() != null)
										userID.setValue(result.getUserIDInput());
									fieldsTable.setWidget(nextRow, 0, new Label(result.getUserID() + ":"));
									fieldsTable.setWidget(nextRow++, 1, userID);
								}
								if (result.getUserPassword() != null && !result.getUserPassword().isEmpty()) {
									fieldsTable.setWidget(nextRow, 0, new Label(result.getUserPassword() + ":"));
									fieldsTable.setWidget(nextRow++, 1, userPassword);
								}
								if (result.getUserPIN() != null && !result.getUserPIN().isEmpty()) {
									fieldsTable.setWidget(nextRow, 0, new Label(result.getUserPIN() + ":"));
									fieldsTable.setWidget(nextRow, 1, userPIN);
								}	
								
								flow.add(fieldsTable);
								
								HorizontalPanel hp = new HorizontalPanel();
								Button hai = new Button(AON.MSG.accept());
								Button iie = new Button(AON.MSG.cancelAction());
								hp.setWidth("50%");
								hp.addStyleName(AON.CSS.aonBlockCenter());
								hp.addStyleName(AON.CSS.aonMarginTop());
								hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
								hp.add(iie);
								hp.add(hai);
								flow.add(hp);
								dialog.center();
								
								Label errorLbl = new Label();
								errorLbl.addStyleName(AON.CSS.aonColorRed());
								
								iie.addClickHandler(e -> {
									dialog.hide();
									container.remove(dialog);
								});
								hai.addClickHandler(e ->
									CHECKIT_SERVICE.editCredentials(opt.getConfiguration().getEnterpriseId(), result, new AsyncCallback<Boolean>() {

										@Override
										public void onFailure(Throwable caught) {
											String errMsg = caught.getMessage();
											errorLbl.setText(errMsg);
										}

										@Override
										public void onSuccess(Boolean result) {
											if (result != null && result) {
												Label successLabel = new Label("Se han actualizado correctamente las credenciales");
												successLabel.addStyleName(AON.CSS.aonColorGreen());
												sessionLog.add(successLabel);
												openFootPanel();
											} else {
												Label successLabel = new Label("Error: no se pudieron actualizar las credenciales");
												successLabel.addStyleName(AON.CSS.aonColorGreen());
												sessionLog.add(successLabel);
												openFootPanel();
											}
											dialog.hide();
											container.remove(dialog);
										}
									}));
								
								container.add(dialog);
								dialog.show();
								
							}
						});
						
						
					}
					
					private void paintCredentialEdit (CheckItLoginFields checkitLoginFields, CheckItModuleOptions opt) {
						AonTextBox userID = new AonTextBox();
						AonTextBox userPassword = new AonTextBox();
						AonTextBox userPIN = new AonTextBox();
						
						
						FlowPanel flow = new FlowPanel();
						AonDialog dialog = new AonDialog("Editar credenciales", flow);
						dialog.setAutoHideEnabled(true);
						
						int nextRow = 0;
						
						FlexTable fieldsTable = new FlexTable();
						
						if (checkitLoginFields.getUserID() != null && !checkitLoginFields.getUserID().isEmpty()) {
							if (checkitLoginFields.getUserIDInput() != null)
								userID.setValue(checkitLoginFields.getUserIDInput());
							fieldsTable.setWidget(nextRow, 0, new Label(checkitLoginFields.getUserID() + ":"));
							fieldsTable.setWidget(nextRow++, 1, userID);
						}
						if (checkitLoginFields.getUserPassword() != null && !checkitLoginFields.getUserPassword().isEmpty()) {
							fieldsTable.setWidget(nextRow, 0, new Label(checkitLoginFields.getUserPassword() + ":"));
							fieldsTable.setWidget(nextRow++, 1, userPassword);
						}
						if (checkitLoginFields.getUserPIN() != null && !checkitLoginFields.getUserPIN().isEmpty()) {
							fieldsTable.setWidget(nextRow, 0, new Label(checkitLoginFields.getUserPIN() + ":"));
							fieldsTable.setWidget(nextRow, 1, userPIN);
						}	
						
						flow.add(fieldsTable);
						
						HorizontalPanel hp = new HorizontalPanel();
						Button hai = new Button(AON.MSG.accept());
						Button iie = new Button(AON.MSG.cancelAction());
						hp.setWidth("50%");
						hp.addStyleName(AON.CSS.aonBlockCenter());
						hp.addStyleName(AON.CSS.aonMarginTop());
						hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
						hp.add(iie);
						hp.add(hai);
						flow.add(hp);
						dialog.center();
						
						Label errorLbl = new Label();
						errorLbl.addStyleName(AON.CSS.aonColorRed());
						errorLbl.addStyleName(AON.CSS.aonBlockCenter());
						errorLbl.addStyleName(AON.CSS.aonTextCenter());
						errorLbl.addStyleName(AON.CSS.aonMarginTop());		
						flow.add(errorLbl);
						
						iie.addClickHandler(e -> {
							dialog.hide();
							container.remove(dialog);
						});
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
										Label successLabel = new Label("Se han actualizado correctamente las credenciales");
										successLabel.addStyleName(AON.CSS.aonColorGreen());
										sessionLog.add(successLabel);
										openFootPanel();
									} else {
										Label successLabel = new Label("Error: no se pudieron actualizar las credenciales");
										successLabel.addStyleName(AON.CSS.aonColorGreen());
										sessionLog.add(successLabel);
										openFootPanel();
									}
									dialog.hide();
									container.remove(dialog);
								}
							
							});
						});
						
						container.add(dialog);
						dialog.show();
					}
					
				});
				
				
			});
			
			getMenuPanel().add(saveButton);
			getMenuPanel().add(modifyButton);
		}
		
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
			fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserID() + ":"));
			fieldsTable.setWidget(nextRow++, 1, userID);
		}
		if (loginType.getUserPassword() != null && !loginType.getUserPassword().isEmpty()) {
			fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserPassword() + ":"));
			fieldsTable.setWidget(nextRow++, 1, userPassword);
		}
		if (loginType.getUserPIN() != null && !loginType.getUserPIN().isEmpty()) {
			fieldsTable.setWidget(nextRow, 0, new Label(loginType.getUserPIN() + ":"));
			fieldsTable.setWidget(nextRow, 1, userPIN);
		}
	}
	
	
	private class AonCheckItUnlinkedBankCard extends AonCard {
		
		
		private AonCheckItUnlinkedBankCard(final CheckItModuleOptions opt, CheckitUnlinkedBankAccount checkItUnlinkedBankAccount) {
			InlineLabel title = new InlineLabel();
			title.addStyleName(AON.CSS.aonBorderNone());
			title.setText(checkItUnlinkedBankAccount.getBank());
			this.setTitle(title);
			
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
			
			
			FlexTable registrationTable = new FlexTable();
			AonDialog dialog = new AonDialog("REGISTRAR CUENTA", registrationTable);
			dialog.setAutoHideEnabled(true);
			Label bankLabel = new Label(checkItUnlinkedBankAccount.getBank());
			bankLabel.addStyleName(AON.AON_BOLD);
			bankLabel.getElement().getStyle().setProperty("margin-bottom", "1.5em");
			bankLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			bankLabel.getElement().getStyle().setFontSize(2, Unit.EM);
			bankLabel.addStyleName(AON.AON_NO_MARGIN);
			registrationTable.setWidget(0, 0, bankLabel);
			
			Label ibanLabel = new Label(formatIban(checkItUnlinkedBankAccount.getIban()));
			ibanLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			ibanLabel.getElement().getStyle().setFontSize(1.75, Unit.EM);
			registrationTable.setWidget(1, 0, ibanLabel);
			
			FlexTable loginTable = new FlexTable();
			loginTable.setWidth("100%");
			
			
			AonCheckItBankBox bankBox = new AonCheckItBankBox(opt.getDomainName(), opt.getDomain(), opt.getUser(), opt.getConfiguration().getBankIds());
			
			bankBox.setWidth("100%");
			
			FlexTable fieldsTable = new FlexTable();
			fieldsTable.addStyleName(AON.AON_CSS.aonJustifyContentSpaceBetween());
			fieldsTable.addStyleName(AON.CSS.aonBlockCenter());
			fieldsTable.setWidget(0, 0, new Label("Entidad:"));
			fieldsTable.setWidget(0, 1, bankBox);
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
								fieldsTable.setWidget(0, 0, new Label("Entidad:"));
								fieldsTable.setWidget(0, 1, bankBox);
								if (result.size() > 1) {
									ListBox loginDropdown = new ListBox();
									loginDropdown.addItem("Seleccione un login");
									if (checkItUnlinkedBankAccount.getLogin() != null) {
										checkItUnlinkedBankAccount.getLogin().clear();									
									}
									
									for (CheckItLoginFields item : result) {
										loginDropdown.addItem(item.getType(), AonNumberUtils.toString(item.getId()));										
									}
									
									
									fieldsTable.setWidget(1, 0, new Label("Tipo de login:"));
									fieldsTable.setWidget(1, 1, loginDropdown);
									Label marginLabel = new Label();
									marginLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
									fieldsTable.setWidget(2, 0, marginLabel);
									
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
			Button iie = new Button(AON.MSG.cancelAction());
			hp.setWidth("50%");
			hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			hp.add(iie);
			hp.add(hai);
			registrationTable.setWidget(3, 0, hp);
			errLabel.setStyleName(AON.CSS.aonColorRed());
			errLabel.addStyleName(AON.CSS.aonTextCenter());
			errLabel.addStyleName(AON.CSS.aonMarginTop());
			registrationTable.setWidget(4, 0, errLabel);
			
			
			hai.addClickHandler(handler -> {
				//AÑADIR LA CUNETA
				Integer enterpriseId = opt.getConfiguration().getEnterpriseId();
				String user = userID.getValue();
				String pass = userPassword.getValue();
				String pin = userPIN.getValue();
				CHECKIT_SERVICE.addAccount(enterpriseId, checkItUnlinkedBankAccount, user, pass, pin, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						String error = caught.getMessage();
						errLabel.setText(error);
						Label errorLabel = new Label("Se ha producido un error al a\u00F1adir la cuenta: \"" + error + "\"");
						errorLabel.setStyleName(AON.CSS.aonColorRed());
						sessionLog.add(errorLabel);
						openFootPanel();
					}

					@Override
					public void onSuccess(Boolean result) {
						dialog.hide();
						if (result != null && result) {
							Label sccsLabel = new Label("A\u00F1adida la cuenta " + checkItUnlinkedBankAccount.getIban());
							sccsLabel.setStyleName(AON.CSS.aonColorGreen());
							sessionLog.add(sccsLabel);
							openFootPanel();
							
							CHECKIT_SERVICE.getConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<CheckItConfiguration>() {
								@Override
								public void onSuccess(CheckItConfiguration result) {
									opt.setConfiguration(result);
									enterpriseData.remove(unlinkedBanks);
									enterpriseData.remove(linkedBanks);
									linkedBanks = paintBanks(opt);
									unlinkedBanks = paintUnlinkedBanks(opt);
									enterpriseData.add(linkedBanks);
									enterpriseData.add(unlinkedBanks);
								}
								
								@Override
								public void onFailure(Throwable caught) {
									dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
								}
							});
							
							
						} else {
							String error = "Se ha producido un error desconocido";
							errLabel.setText(error);
							Label errorLabel = new Label("Se ha producido un error desconocido al a\u00F1adir la cuenta");
							errorLabel.setStyleName(AON.CSS.aonColorRed());
							sessionLog.add(errorLabel);
							openFootPanel();
						}
					}
				});
				
				
				
				
				
			});
			
			iie.addClickHandler(handler -> {
				dialog.hide();
				checkItUnlinkedBankAccount.setBankId(null);
				checkItUnlinkedBankAccount.setLogin(null);
				
			});
			dialog.center();
			container.add(dialog);
			dialog.show();
		}
		
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
	
	private FlexTable getPendingMovementTag(BankStatement bankStatement) {
		DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM");
		Date operationDate = bankStatement.getOperationDate();
		
		String description = bankStatement.getDescription();
		Double amount = !bankStatement.isPayment() ? bankStatement.getAmount() : bankStatement.getAmount() * (-1);
		
		FlexTable tag = new FlexTable();
		
		String dateString = dtf.format(operationDate);
		dateString = dateString != null ? dateString.toUpperCase() : "";
		Label dateLabel = new Label(dateString);
		dateLabel.setStyleName(AON.CSS.aonFontLarger());
		dateLabel.setWidth("100%");
		
		Label amountLabel = new Label(AON.FMT.format(amount) + " " + EURO);
		amountLabel.setStyleName(AON.CSS.aonFontMedium());
		amountLabel.addStyleName(AON.CSS.aonTextRight());
		amountLabel.setWidth("100%");
		if (amount < 0) {
			amountLabel.addStyleName(AON.CSS.aonColorRed());
		}
		
		Label descriptionLabel = new Label(description);
		descriptionLabel.setStyleName(AON.CSS.aonFontLarger());
		descriptionLabel.setWidth("100%");
		
		tag.getColumnFormatter().setWidth(0, "18%");
		tag.getColumnFormatter().setWidth(1, "60%");
		tag.getColumnFormatter().setWidth(2, "22%");
		
		tag.setWidget(0, 0, dateLabel);
		tag.setWidget(0, 1, descriptionLabel);
		tag.setWidget(0, 2, amountLabel);
			
		return tag;
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

	

}		
