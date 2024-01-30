package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class NordigenModule extends MainEntryPoint {

	private static final String GWT_SELECTOR_CLASS = "gwt-Selector";
	private static final String WHITE = "white";
	private static final String CENTER = "center";
	private static final String MIN_WIDTH = "minWidth";
	
	private static final Logger LOGGER = Logger.getLogger(NordigenModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	private static final String EURO = "\u20AC";
	private static final String AON_BLUE = "#002469";
	private static final String HOVER_COLOR = "#7A9AD7";
	
	
	private static final NordigenServiceAsync NORDIGEN_SERVICE;
	static {
		NordigenServiceAsync serviceRaw = GWT.create(NordigenService.class);
		NORDIGEN_SERVICE = new NordigenServiceAsyncDecorator(serviceRaw);
	}
	
	private DockLayoutPanel dockLayoutPanel;
	private SplitLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private AonToolbar toolbar;
	private AonMinimizePanel footPanel;
	private FlowPanel sessionLog;
	boolean minimizedByUser;
	
	private Map<Integer, List<NordigenAccountBalance>> balancesMap;
	private InlineLabel remainderBox;
	private InlineLabel balanceBox;
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
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		if ( opt.getConfiguration() == null) {
			NORDIGEN_SERVICE.getConfiguration(opt.getOccam(),new AsyncCallback<NordigenConfiguration>() {
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
		FlowPanel container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.add(centerPanel);
		if (!isMobile()) {
			centerLayoutPanel.addSouth(getMinimizePanel(), 30);
		}
		dockLayoutPanel.add(centerLayoutPanel);
		FlowPanel enterpriseData = paintEnterpiseData( opt );
		container.add( enterpriseData );
		
		if (firstTime) {
			Label loadingLabel = new Label("El agregador bancario ha sido inicializado con \u00E9xito");
			loadingLabel.setStyleName(AON.CSS.aonColorGreen());
			sessionLog.add(loadingLabel);
			openFootPanel();
		}
	}
	
	private FlowPanel paintEnterpiseData(NordigenModuleOptions opt) {
		if (!isMobile()) {
			// PURGADO DE REQUISITIONS 'HUÉRFANAS'
			AonToolbarButton config = new AonToolbarButton("Gestionar vinculaciones", AON.CSS.aonIconSettings());
			config.addClickHandler(ev -> paintManageRequisitionsDialog(opt) );
			toolbar.add(config);
		}
		FlowPanel panel = new FlowPanel();
		Widget linkedBanks = paintBanks(opt);
		Widget unlinkedBanks = paintUnlinkedBanks(opt);
		panel.add(linkedBanks);
		panel.add(unlinkedBanks);
		return panel;
	}
	
	@FunctionalInterface
	private static interface LoadCallback {
		void onLoad();
	}
	
	private void paintManageRequisitionsDialog(NordigenModuleOptions opt) {
		CustomDialog reqDialog = new CustomDialog();
		reqDialog.setCaption("Solicitudes de vinculaci\u00F3n perdidas");
		FlowPanel manageRequisitionsMenu = manageRequisitionsMenu(opt, reqDialog::center);			
		manageRequisitionsMenu.getElement().getStyle().setProperty(MIN_WIDTH, "600px");
		manageRequisitionsMenu.getElement().getStyle().setProperty("maxHeight", "400px");
		manageRequisitionsMenu.getElement().getStyle().setProperty("overflowY", "auto");
		reqDialog.setWidget(manageRequisitionsMenu);
		reqDialog.setAutoHideEnabled(true);
		reqDialog.center();
		reqDialog.show();
	}
	
	private FlowPanel manageRequisitionsMenu(NordigenModuleOptions opt, LoadCallback callback) {
		FlowPanel requisitionsPanel = new FlowPanel();
		Label loadingLabel = new Label();
		loadingLabel.addStyleName(AON.CSS.aonLoader());
		requisitionsPanel.add(loadingLabel);
		NORDIGEN_SERVICE.getDomainRequisitions(opt.getConfiguration().getToken(), opt.getDomainName(), new AsyncCallback<List<NordigenRequisition>>() {

			@Override
			public void onFailure(Throwable caught) {
				Label errLabel = new Label("Se produjo un error al obtener los datos");
				errLabel.addStyleName(AON.CSS.aonColorRed());
				loadingLabel.removeFromParent();
				requisitionsPanel.add(errLabel);
			}

			@Override
			public void onSuccess(List<NordigenRequisition> result) {
				loadingLabel.removeFromParent();
				FlowPanel resultsPanel = new FlowPanel();
				FlowPanel tableBodyPanel = new FlowPanel();
				tableBodyPanel.getElement().getStyle().setProperty("overflowY", "scroll");
				requisitionsPanel.add(resultsPanel);
				if (result != null && !result.isEmpty()) {
					FlexTable reqHeaderTable = new FlexTable();
					reqHeaderTable.setWidth("100%");
					reqHeaderTable.getColumnFormatter().setWidth(0, "60%");
					reqHeaderTable.getColumnFormatter().setWidth(1, "15%");
					reqHeaderTable.getColumnFormatter().setWidth(2, "25%");
					Label bankHeader = new Label("Banco");					
					bankHeader.addStyleName(AON.CSS.aonBold());
					bankHeader.addStyleName(AON.CSS.aonTextCenter());
					Label linkedHeader = new Label("Completada");
					linkedHeader.addStyleName(AON.CSS.aonBold());
					linkedHeader.addStyleName(AON.CSS.aonTextCenter());
					Label deleteHeader = new Label("Eliminar");
					deleteHeader.addStyleName(AON.CSS.aonBold());
					deleteHeader.addStyleName(AON.CSS.aonTextCenter());
					reqHeaderTable.setWidget(0, 0, bankHeader);
					reqHeaderTable.setWidget(0, 1, linkedHeader);
					reqHeaderTable.setWidget(0, 2, deleteHeader);
					resultsPanel.add(reqHeaderTable);
					resultsPanel.add(tableBodyPanel);
					Set<String> ignoredRequisitions = new HashSet<>();
					List<NordigenBankAccount> linkedAccounts = opt.getConfiguration().getLinkedAccounts();
					if (linkedAccounts != null) {
						opt.getConfiguration().getLinkedAccounts().forEach(a -> {
							if (a != null && a.getRequisition() != null && AonStringUtils.isNotBlank(a.getRequisition().getId())) {
								ignoredRequisitions.add(a.getRequisition().getId());
							}
						});
					}
					int i = 0;
					for (NordigenRequisition req : result) {
						if (req != null && !ignoredRequisitions.contains(req.getId())) {
							FlexTable reqTable = new FlexTable();
							reqTable.setWidth("100%");
							reqTable.getColumnFormatter().setWidth(0, "60%");
							reqTable.getColumnFormatter().setWidth(1, "15%");
							reqTable.getColumnFormatter().setWidth(2, "25%");
							tableBodyPanel.add(reqTable);
							Label bankName = new Label(req.getInstitutionId());
							boolean linked = NordigenRequisitionStatus.LINKED.equals(req.getStatus());
							Label vinculado = new Label(linked ? "S\u00ED" : "No");
							vinculado.addStyleName(AON.CSS.aonTextCenter());
							Label delete = new Label();
							delete.setWidth("25px");
							delete.setHeight("25px");
							delete.addStyleName(AON.CSS.aonIconDelete());
							delete.addStyleName(AON.CSS.aonBlockCenter());
							delete.getElement().getStyle().setProperty("backgroundRepeat", "no_repeat");
							delete.getElement().getStyle().setProperty("backgroundPosition", CENTER);
							delete.getElement().getStyle().setProperty("cursor", "pointer");
							delete.addStyleName(AON.CSS.aonTextCenter());
							reqTable.setWidget(0, 0, bankName);
							reqTable.setWidget(0, 1, vinculado);
							reqTable.setWidget(0, 2, delete);
							if (i++ % 2 == 0) {
								reqTable.getElement().getStyle().setBackgroundColor("ghostWhite");
							}
							
							delete.addClickHandler(event -> 
								NORDIGEN_SERVICE.deleteRequisitionById(opt.getConfiguration().getToken(), opt.getOccam(), req.getId()
									, new AsyncCallback<Boolean>() {
										@Override
										public void onFailure(Throwable caught) {
											if (!isMobile()) {
												Label label = new Label("No se pudo eliminar la vinculación: " + caught.getMessage());
												label.addStyleName(AON.CSS.aonColorRed());
												sessionLog.add(label);
												openFootPanel();							
											}
										}
										
										@Override
										public void onSuccess(Boolean result) {
											reqTable.removeFromParent();
											int count = resultsPanel.getWidgetCount();
											if (count > 1) {
												for (int ind = 1; ind < count; ind++) {
													if (ind % 2 == 0) {
														resultsPanel.getWidget(ind).getElement().getStyle().setBackgroundColor("ghostWhite");
													} else {
														resultsPanel.getWidget(ind).getElement().getStyle().setBackgroundColor(WHITE);
													}
												}
											}
										}
									}
								)
							);
						}
						
					}
				} else {
					Label noReqsLabel = new Label("No hay ninguna solicitud de vinculacón guardada");
					resultsPanel.add(noReqsLabel);
				}
				callback.onLoad();
			}
		});
		
		
		return requisitionsPanel;
	}
	

	private static NordigenAccountBalance filterConsolidado(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance consolidado = null;
		
		consolidado = balances.stream()
				.filter(bal -> NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);
		
		if (consolidado == null && !balances.isEmpty()) {
			return balances.get(0);
		}
		
		return consolidado;
	}
	
	private static NordigenAccountBalance filterReal(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance real = null;
		
		real = balances.stream()
				.filter(bal -> !NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);
		
		if (real == null) {
			return filterConsolidado(balances);
		}
		
		return real;
	}

	private Widget paintBanks(NordigenModuleOptions opt) {
		balancesMap = new HashMap<>();
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
				
				List<NordigenAccountBalance> balances = bankAccount.getBalances();
				
				NordigenAccountBalance consolidado = filterConsolidado(balances);
				NordigenAccountBalance real = filterReal(balances);
				
				double bankBalance = 0;
				double remainder = 0;
				
				if (bankAccount.getBalances() != null && bankAccount.getBalances().size() == 1) {
					NordigenAccountBalance bal = bankAccount.getBalances().get(0);
					bankBalance = bal.getBalanceAmount().getAmount();
					remainder = bal.getBalanceAmount().getAmount();
				} else {
					bankBalance = consolidado != null &&
							consolidado.getBalanceAmount() != null ? consolidado.getBalanceAmount().getAmount() : 0;
					
					remainder = bankBalance;
					if (real != null && real.getBalanceAmount() != null) {
						remainder = AonNumberUtils.zeroIfNull(real.getBalanceAmount().getAmount());					
					}					
				}
				
				
				
				balanceTotal = balanceTotal + bankBalance;
				remainderTotal = remainderTotal + remainder;
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
			
			balanceBox = new InlineLabel();
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
			
			remainderBox = new InlineLabel();
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
		
		FlexTable bottomTable;
		AonTableButton continueLinkButton;

		private AonNordigenBankCard(final NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount) {
			
			FlowPanel titlePanel = new FlowPanel();
			titlePanel.addStyleName(AON.CSS.aonTextCenter());
			
			RegistryBank rbank = nordigenBankAccount.getRbank();
			String bankName = rbank != null ? rbank.getAlias() : "";
			String iban = rbank != null ? rbank.getBankAccount().getIban() : "";
			
			Label title = new Label();
			title.addStyleName(AON.CSS.aonBorderNone());
			title.setText(AonStringUtils.abbreviate(bankName, 26));
			String bankTitle = bankName;
			title.setTitle(bankTitle);
			titlePanel.add(title);
			
			this.setTitle(titlePanel);
			if (Window.getClientWidth() < 675) {				
				this.getElement().getStyle().setProperty("marginLeft", "auto");
				this.getElement().getStyle().setProperty("marginRight", "auto");
			}
			
			FlowPanel body = new FlowPanel();
			bottomTable = new FlexTable();
			FlowPanel ibanPanel = new FlowPanel();
			InlineLabel ibanBox = new InlineLabel();
			ibanBox.setText(formatIban(iban));
			ibanPanel.addStyleName(AON.CSS.aonMarginBottom());
			ibanPanel.add(ibanBox);
			
			FlowPanel lastDateLabelPanel = new FlowPanel();
			InlineLabel lastDateLabel = new InlineLabel("\u00DAltimo consulta realizada");
			lastDateLabel.setStyleName(AON.CSS.aonTableLabel());
			lastDateLabelPanel.add(lastDateLabel);

			FlowPanel lastDatePanel = new FlowPanel();
			InlineLabel lastDateBox = new InlineLabel();
			lastDateBox.addStyleName(AON.CSS.aonFontMedium());
			lastDatePanel.addStyleName(AON.CSS.aonMarginBottom());
			lastDatePanel.add(lastDateBox);

			FlowPanel atDateLabelPanel = new FlowPanel();
			InlineLabel atDateLabel = new InlineLabel("\u00DAltimo movimiento insertado");
			atDateLabel.setStyleName(AON.CSS.aonTableLabel());
			atDateLabelPanel.add(atDateLabel);
			
			FlowPanel atDatePanel = new FlowPanel();
			InlineLabel atDateBox = new InlineLabel();
			atDateBox.addStyleName(AON.CSS.aonFontMedium());
			atDatePanel.addStyleName(AON.CSS.aonMarginBottom());
			atDatePanel.add(atDateBox);
			
			FlowPanel balanceLabelPanel = new FlowPanel();
			InlineLabel balanceLabel = new InlineLabel("Saldo ");
			balanceLabel.setStyleName(AON.CSS.aonTableLabel());
			balanceLabelPanel.add(balanceLabel);
			
			FlowPanel balancePanel = new FlowPanel();
			InlineLabel balanceBox = new InlineLabel();
			balanceBox.addStyleName(AON.CSS.aonFontMedium());
			balanceBox.addStyleName(AON.CSS.aonBold());
			balancePanel.addStyleName(AON.CSS.aonMarginBottom());
			balancePanel.add(balanceBox);
			
			FlowPanel availableLabelPanel = new FlowPanel();
			InlineLabel availableLabel = new InlineLabel("Disponible");
			availableLabel.setStyleName(AON.CSS.aonTableLabel());
			availableLabelPanel.add(availableLabel);
			
			FlowPanel availablePanel = new FlowPanel();
			InlineLabel availableBox = new InlineLabel();
			availableBox.addStyleName(AON.CSS.aonFontMedium());
//			availableBox.addStyleName(AON.CSS.aonBold());
			availablePanel.addStyleName(AON.CSS.aonMarginBottom());
			availablePanel.add(availableBox);

			body.add(ibanPanel);
			
			body.add(lastDateLabelPanel);
			body.add(lastDatePanel);

			body.add(atDateLabelPanel);
			body.add(atDatePanel);

			body.add(balanceLabelPanel);
			body.add(balancePanel);
			body.add(availableLabelPanel);
			body.add(availablePanel);
			
			if (Window.getClientWidth() < 675) {				
				this.getElement().getStyle().setProperty(MIN_WIDTH, "100%");
			} else {
				this.getElement().getStyle().setProperty(MIN_WIDTH, "260px");
			}
			body.addStyleName(AON.CSS.aonTextCenter());
			
			
			
			bottomTable.addStyleName(AON.CSS.aonBlockCenter());
			
			body.add(bottomTable);
			
			
			this.setBody(body);
			
			AonTableButton saveButton = new AonTableButton(AON.MSG.importAction(), AON.CSS.aonIconImport());
			saveButton.setTabIndex(-2);
			
			AonTableButton unlinkButton = new AonTableButton("Desvincular", AON.CSS.aonIconDelete());
			unlinkButton.setTabIndex(-4);
			unlinkButton.addClickHandler((ev) -> {
				FlexTable unlinkTable = new FlexTable();
				unlinkTable.addStyleName(AON.CSS.aonBlockCenter());
				CustomDialog dialog = new CustomDialog();
				dialog.setAutoHideEnabled(true);
				dialog.setCaption("DESVINCULAR CUENTA");
//				dialog.setTitle("DESVINCULAR CUENTA");
				dialog.setWidget(unlinkTable);
				
				if (isMobile()) {
					unlinkTable.getElement().getStyle().setWidth(350, Unit.PX);
					unlinkTable.getElement().getStyle().setProperty("minHeight", "150px");
				} else {
					unlinkTable.getElement().getStyle().setProperty(MIN_WIDTH, "350px");
					unlinkTable.getElement().getStyle().setProperty("minHeight", "110px");
				}
				
				dialog.getElement().getStyle().clearLeft();
				dialog.getElement().getStyle().clearRight();
				Label unlinkLabel = new Label("\u00BFDESEA DESVINCULAR ESTA CUENTA?");
				unlinkTable.getElement().getStyle().setTextAlign(TextAlign.CENTER);
				unlinkTable.setWidget(0, 0, unlinkLabel);
				HorizontalPanel hp = new HorizontalPanel();
				hp.addStyleName(AON.CSS.aonBlockCenter());
				Button hai = new Button(AON.MSG.accept());
				
				hai.addClickHandler((event) -> {
					NORDIGEN_SERVICE.cancelRequisition(opt.getConfiguration().getToken(), opt.getOccam(),
						rbank != null ? rbank.getId() : null, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							if (!isMobile()) {
								Label label = new Label(caught.getMessage());
								label.addStyleName(AON.CSS.aonColorRed());
								sessionLog.add(label);
								openFootPanel();							
							}
						}

						@Override
						public void onSuccess(Void result) {
							reloadPage(opt, dialog::hide);
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
				} else {
					mobileCancelButton(iie);
					iie.getElement().getStyle().setMarginRight(1, Unit.EM);
					hp.add(iie);
				}
				hp.add(hai);
				hp.addStyleName(AON.CSS.aonBlockCenter());
				unlinkTable.setWidget(1, 0, hp);
				
				dialog.center();
				dialog.show();
				
			});
			
			
			AonTableButton allMovementsButton = new AonTableButton("Todos los movimientos", AON.CSS.aonIconList());
			allMovementsButton.setTabIndex(-3);
			
			ClickHandler movementsClickHandler = new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					showAllMovements(opt, nordigenBankAccount, iban);
				}
			};
			
			
			if (isMobile()) {
				body.addDomHandler(movementsClickHandler, ClickEvent.getType());			
			} else {
				allMovementsButton.addClickHandler(movementsClickHandler);
			}

			AonTableButton insertMovementsButton = new AonTableButton("Insertar movimientos pendientes", AON.CSS.aonIconSave());
			insertMovementsButton.setTabIndex(-6);
			
			AonTableButton balanceJsonButton = new AonTableButton("JSON de los saldos", AON.CSS.aonIconInfo());
			balanceJsonButton.setTabIndex(-7);
			
			
			balanceJsonButton.addClickHandler(event -> {
				showBalancesJson(nordigenBankAccount);
			});
			
			insertMovementsButton.addClickHandler(event -> {
				NORDIGEN_SERVICE.insertTransactions(opt.getOccam(), nordigenBankAccount, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						if (!isMobile()) {
							Label label = new Label(caught.getMessage());
							label.addStyleName(AON.CSS.aonColorRed());
							sessionLog.add(label);
							openFootPanel();
						}
						refreshCard(opt, nordigenBankAccount, atDateBox, lastDateBox, balanceBox, availableBox, title, titlePanel, allMovementsButton, insertMovementsButton, balanceJsonButton);
					}

					@Override
					public void onSuccess(Integer result) {
						if (!isMobile()) {							
							Label label = new Label(result + " movimientos insertados");
							label.addStyleName(AON.CSS.aonColorGreen());
							sessionLog.add(label);
							openFootPanel();
						}
						refreshCard(opt, nordigenBankAccount, atDateBox, lastDateBox, balanceBox, availableBox, title, titlePanel, allMovementsButton, insertMovementsButton, balanceJsonButton);
					}
				});
			});
			
			
			getMenuPanel().add(unlinkButton);

			AonTableButton updateButton = new AonTableButton("Actualizar", AON.CSS.aonIconRefresh());
			updateButton.setTabIndex(-5);
			updateButton.addClickHandler((ev) -> {
				refreshCard(opt, nordigenBankAccount, atDateBox, lastDateBox, balanceBox, availableBox, title, titlePanel, allMovementsButton, insertMovementsButton, balanceJsonButton);
			});
			getMenuPanel().add(updateButton);
			
			refreshCard(opt, nordigenBankAccount, atDateBox, lastDateBox, balanceBox, availableBox, title, titlePanel, allMovementsButton, insertMovementsButton, balanceJsonButton);
		}

		private void mobileCancelButton(Button hai) {
			hai.setWidth("80px");
			hai.setHeight("35px");
			
			Style style = hai.getElement().getStyle();
			style.setProperty("background", "gray");
			style.setProperty("color", WHITE);
			style.setProperty("fontSize", "0.8rem");
			style.setProperty("fontWeight", "700");
			style.setProperty("paddingLeft", "1em");
			style.setProperty("paddingRight", "1em");
			style.setProperty("borderRadius", "6px");
			style.setProperty("boxShadow", "0 2px 4px rgb(0 0 0 / 15%)");
			style.setProperty("transition", "background .25s ease-in-out,transform .15s ease");
		}

		private void showBottomMessage(Label... labels) {
			this.bottomTable.clear();
			this.bottomTable.removeStyleName(AON.CSS.aonLoader());
			this.bottomTable.addStyleName(AON.CSS.aonBlockCenter());
			int row = 0;
			if (labels != null) {
				for (Label label : labels) {
					if (label != null) {						
						this.bottomTable.setWidget(row++, 0, label);
					}
				}
			}
		}
		
		private void showBottomMessage(String color, String... message) {
			this.bottomTable.clear();
			this.bottomTable.removeStyleName(AON.CSS.aonLoader());
			this.bottomTable.addStyleName(AON.CSS.aonBlockCenter());
			
			int row = 0;
			if (message != null) {
				for (String msg : message) {
					if (AonStringUtils.isNotBlank(msg)) {						
						Label errLabel = new Label();
						
						if (AonStringUtils.equals(AON.CSS.aonLoader(), msg)) {
							this.bottomTable.addStyleName(AON.CSS.aonLoader());				
						} else {
							errLabel.setText(msg);
						}
						this.bottomTable.setWidget(row++, 0, errLabel);
						if (AonStringUtils.isNotEmpty(color)) {
							errLabel.getElement().getStyle().setColor(color);
						}
					}
					
				}
			}
			
			
			
		}
		
		private void clearBottomMessage() {
			this.bottomTable.removeStyleName(AON.CSS.aonLoader());
			this.bottomTable.getElement().getStyle().clearColor();
//			this.bottomTable.clear();
		}
		
		private void showBalancesJson(NordigenBankAccount nordigenBankAccount) {
			List<NordigenAccountBalance> balances = nordigenBankAccount.getBalances();
			if (balances != null) {
				Label lbl1 = new Label("[");
				sessionLog.add(lbl1);
				balances.forEach(bal -> {
					String raw = bal.getOriginalJson();
					if (raw != null) {
						String[] spl = raw.split("\n");
						for (String line : spl) {
							Label lbl = new Label(line);
							sessionLog.add(lbl);
						}
					}
					Label lbl2 = new Label(",");
					sessionLog.add(lbl2);
				});
				Label lbl3 = new Label("]");
				sessionLog.add(lbl3);
				openFootPanel();
			}
		}
		
		private void showAllMovements(final NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount, String iban) {
			FlowPanel panel = new FlowPanel();
			panel.getElement().getStyle().setProperty(MIN_WIDTH, "230px");
			
			
			List<NordigenAccountBalance> balances = nordigenBankAccount.getBalances();
			Double availableAmount = 0d;
			
			NordigenAccountBalance consolidado = filterConsolidado(balances);
			NordigenAccountBalance real = filterReal(balances);
			
			if (real != null && real.getBalanceAmount() != null) {
				availableAmount = real.getBalanceAmount().getAmount();
			} else if (consolidado != null && consolidado.getBalanceAmount() != null) {
				availableAmount = consolidado.getBalanceAmount().getAmount();
			}
			
			Double balanceAmount;
			if (consolidado != null && consolidado.getBalanceAmount() != null) {
				balanceAmount = consolidado.getBalanceAmount().getAmount();
			} else {
				balanceAmount = availableAmount;				
			}
			
			String logo = nordigenBankAccount.getInstitution() != null ? nordigenBankAccount.getInstitution().getLogo() : "";
			Image logoImg = new Image(logo);
			logoImg.setHeight("40px");
			logoImg.addStyleName(AON.AON_CSS.aonDisplayBlock());
			logoImg.addStyleName(AON.CSS.aonBlockCenter());
			CustomDialog dialog = null;
			
			if (!isMobile()) {
				dialog = new CustomDialog();
				dialog.setAutoHideEnabled(true);
				
				dialog.setCaption("MOVIMIENTOS");
				dialog.getElement().getStyle().setProperty("maxWidth", "90%");
				
			}
			
			
			HorizontalPanel closeImport = new HorizontalPanel();
			closeImport.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			closeImport.addStyleName(AON.CSS.aonPaddingTop());
			closeImport.addStyleName(AON.CSS.aonBlockCenter());
			closeImport.addStyleName(AON.CSS.aonPaddingBottom());
			
			FlowPanel movementsFlow = new FlowPanel();
			movementsFlow.setWidth(isMobile() ? "100%" : "90%");
			movementsFlow.setStyleName(AON.CSS.aonBlockCenter());
			
			FlowPanel topInfo = new FlowPanel();
			topInfo.setWidth("100%");
			
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
			
			movBalancePanel.addStyleName(AON.CSS.aonMarginBottom());
			movBalancePanel.addStyleName(AON.CSS.aonTextCenter());
			movBalancePanel.add(movBalanceBox);
			
			if (AonMathUtils.isLessThanZero(balanceAmount)) {
				movBalanceBox.addStyleName(AON.CSS.aonColorRed());
			}
			if (balanceAmount != null) {				
				movBalanceBox.setText( AON.FMT.format(balanceAmount) + " " + EURO);
			}
			
			topInfo.add(movBalancePanel);
			
			
			topInfo.addStyleName(AON.CSS.aonMarginBottom());
			
			movementsFlow.add(topInfo);
			
			
			/**Filtros de fecha**/
			
			FlowPanel periodMovContainer = new FlowPanel();
			
			buildCustomMovementsPanel(periodMovContainer, opt, nordigenBankAccount, dialog);
			
			
			movementsFlow.add(periodMovContainer);
			
			ScrollPanel movementsPanel = new ScrollPanel(movementsFlow);
			if (!isMobile()) {				
				movementsPanel.getElement().getStyle().setProperty(MIN_WIDTH, "700px");
				movementsPanel.getElement().getStyle().setProperty("maxHeight", "40vh");
			}
			movementsPanel.addStyleName(AON.CSS.aonMarginTop());
			movementsPanel.addStyleName(AON.CSS.aonCustomScroll());
			
			panel.add(movementsPanel);
			
			
			
			if (isMobile()) {
				Widget previousScreen = centerPanel.getWidget();
				AonToolbarButton back = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack()); 
				toolbar.setTitle("MOVIMIENTOS");
				toolbar.add(back);
				centerPanel.clear();
				back.addClickHandler(h -> {
					back.removeFromParent();
					centerPanel.clear();
					centerPanel.setWidget(previousScreen);
					toolbar.setTitle("AGREGADOR BANCARIO");
//					loadModule(opt, true);
				});
				
				centerPanel.add(panel);
			} else {
				dialog.add(panel);
				dialog.center();
				dialog.show();
			}

		}

		private void refreshCard(final NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount,
				InlineLabel atDateBox, InlineLabel lastDateBox, InlineLabel balanceBox, InlineLabel availableBox, Label title, FlowPanel titlePanel,
				AonTableButton allMovementsButton, AonTableButton insertMovementsButton, AonTableButton balanceJsonButton) {
			balanceJsonButton.setVisible(false);
			allMovementsButton.setVisible(false);
			insertMovementsButton.setVisible(false);
			showBottomMessage("red", AON.CSS.aonLoader());
			NORDIGEN_SERVICE.setAccountValues(opt.getConfiguration().getToken(), opt.getOccam(), nordigenBankAccount, new AsyncCallback<NordigenBankAccount>() {

				@Override
				public void onFailure(Throwable caught) {
					LOGGER.info(caught.getMessage());
					showBottomMessage("red", caught.getMessage());					
				}

				@Override
				public void onSuccess(NordigenBankAccount result) {
					copyBankAccount(nordigenBankAccount, result);
					allMovementsButton.setVisible(true);
					updateCard(opt, result, atDateBox, lastDateBox, balanceBox, availableBox, title, titlePanel, allMovementsButton, insertMovementsButton, balanceJsonButton);
					clearBottomMessage();
				}
			});
		}
		
		private void copyBankAccount(NordigenBankAccount original, NordigenBankAccount updated) {
			original.setBalances(updated.getBalances());
			original.setBankAlias(updated.getBankAlias());
//			original.setDetail(updated.getDetail());
			original.setIban(updated.getIban());
			original.setInstitution(updated.getInstitution());
			original.setLinked(updated.isLinked());
			original.setMetadata(updated.getMetadata());
			original.setRaddInfo(updated.getRaddInfo());
			original.setRbank(updated.getRbank());
			original.setRequisition(updated.getRequisition());
			original.setLastMovementDate(updated.getLastMovementDate());
			original.setNotInsertedMovements(updated.getNotInsertedMovements());
		}
		
		private void updateBalances() {
			if (balancesMap != null && balanceBox != null && remainderBox != null) {
				double balance = 0;
				double remainder = 0;
				for (Entry<Integer, List<NordigenAccountBalance>> entry : balancesMap.entrySet()) {
					List<NordigenAccountBalance> balances = entry.getValue();
					
					NordigenAccountBalance consolidado = filterConsolidado(balances);
					NordigenAccountBalance real = filterReal(balances);
					
					if (consolidado != null && consolidado.getBalanceAmount() != null) {
						balance += AonNumberUtils.zeroIfNull(consolidado.getBalanceAmount().getAmount());
					}
					if (real == null || real.getBalanceAmount() == null) {
						real = consolidado;
					}
					
					if (real != null && real.getBalanceAmount() != null) {
						remainder += AonNumberUtils.zeroIfNull(real.getBalanceAmount().getAmount());
					}
				}
				
				balanceBox.setText( AON.FMT.format(balance) + " " + EURO);
				remainderBox.setText( AON.FMT.format(remainder) + " " + EURO);

				if (balance < 0) {
					balanceBox.addStyleName(AON.CSS.aonColorRed());
				} else {
					balanceBox.removeStyleName(AON.CSS.aonColorRed());
				}
				
				if (remainder < 0) {
					remainderBox.addStyleName(AON.CSS.aonColorRed());
				} else {
					remainderBox.removeStyleName(AON.CSS.aonColorRed());
				}
				
			}
		}

		private void updateCard(NordigenModuleOptions opt, NordigenBankAccount result, InlineLabel atDateBox, InlineLabel lastDateBox,
				InlineLabel balanceBox, InlineLabel availableBox, Label title, FlowPanel titlePanel,
				AonTableButton allMovementsButton, AonTableButton insertMovementsButton, AonTableButton balanceJsonButton) {
			
			for (String log : result.getLogs()) {
				clearBottomMessage();
				if (!isMobile()) {					
					Label label = new Label(result.getIban() + " : " + log);
					label.addStyleName(AON.CSS.aonColorRed());
					sessionLog.add(label);
					openFootPanel();
				}
			}
			
			String logo = result.getInstitution() != null ? result.getInstitution().getLogo() : "";
			titlePanel.clear();
			
			if (AonStringUtils.isNotBlank(logo)) {
				Image logoImg = new Image(logo);
				logoImg.setHeight("40px");
				logoImg.addStyleName(AON.AON_CSS.aonDisplayBlock());
				logoImg.addStyleName(AON.CSS.aonBlockCenter());
				titlePanel.add(logoImg);
			} else {
				titlePanel.add(title);
			}
			
			List<NordigenAccountBalance> balances = result.getBalances();

			NordigenAccountBalance consolidado = filterConsolidado(balances);
			NordigenAccountBalance real = filterReal(balances);
			
			if (result.getRbank() != null) {
				balancesMap.put(result.getRbank().getId(), balances);
				updateBalances();
			}
			
			NordigenAccountBalance balance = consolidado != null ? consolidado : real;
			NordigenAccountBalance available = real != null ? real : consolidado;
			
			Date lastDate = result != null && result.getRbank() != null?result.getRbank().getBalanceDate() : null;
			lastDateBox.setText(balance == null || lastDate == null ? "----" : AON.TIME_FORMAT.format(lastDate));
			Date atDate = result.getLastMovementDate();
			Double balanceAmount = balance != null && balance.getBalanceAmount() != null ? balance.getBalanceAmount().getAmount() : null;
			Double availableAmount = available != null && available.getBalanceAmount() != null ? available.getBalanceAmount().getAmount() : null;
			atDateBox.setText(balance == null || atDate == null ? "----" : AON.DATE_FORMAT.format(atDate));
			if (balanceAmount != null && AonMathUtils.isLessThanZero(balanceAmount)) {
				balanceBox.addStyleName(AON.CSS.aonColorRed());
			}
			if (balanceAmount != null) {				
				balanceBox.setText( AON.FMT.format(balanceAmount) + " " + EURO);
			}
			
			if (availableAmount != null && AonMathUtils.isLessThanZero(availableAmount)) {
				availableBox.addStyleName(AON.CSS.aonColorRed());
			}
			if (availableAmount != null) {				
				availableBox.setText( AON.FMT.format(availableAmount) + " " + EURO);
			}
			
			NordigenRequisition requisition = result.getRequisition();
			if (requisition != null) {
				
				if (NordigenRequisitionStatus.LINKED.equals(requisition.getStatus())) {
					List<NordigenBankStatement> movs = result.getNotInsertedMovements();
					long movCount = AonCollectionUtils.stream(movs).filter(m -> m != null && !m.isPending()).count();
					long pendingMovCount = AonCollectionUtils.stream(movs).filter(m -> m != null && m.isPending()).count();
					String pendingString = pendingMovCount + " movimientos no consolidados";
					Label noConsLabel = pendingMovCount > 0 ? new Label(pendingString) : null;
					if (noConsLabel != null) {
						noConsLabel.getElement().getStyle().setColor("darkOrange");						
					}
					if (AonCollectionUtils.isNotEmpty( result.getNotInsertedMovements() )) {
						Label pendingLabel = new Label("Hay " + movCount + " movimientos pendientes");
						pendingLabel.getElement().getStyle().setColor("green");
						showBottomMessage(pendingLabel, noConsLabel);
					} else {
						Label noMovsLabel = new Label("No hay movimientos pendientes");
						noMovsLabel.getElement().getStyle().setColor("green");
						showBottomMessage(noMovsLabel, noConsLabel);
					}
					if (!isMobile()) {
						getMenuPanel().add(allMovementsButton);
						if (movCount > 0) {
							getMenuPanel().add(insertMovementsButton);
							insertMovementsButton.setVisible(true);
						} else {
							insertMovementsButton.setVisible(false);
						}
					}
//						getMenuPanel().add(balanceJsonButton);
//						balanceJsonButton.setVisible(true);
				} else if (NordigenRequisitionStatus.EXPIRED.equals(requisition.getStatus())) {
					showBottomMessage("red", "Las credenciales expiraron, debe volver a vincular la cuenta");
				} else if (NordigenRequisitionStatus.REJECTED.equals(requisition.getStatus())) {
					showBottomMessage("red", "El proceso de vinclaci\u00F3n fall\u00F3");
				} else {
					showBottomMessage("red", "La vinculaci\u00F3n no se ha completado a\u00FAn");
					if (continueLinkButton == null) {
						continueLinkButton = new AonTableButton("Continuar la vinculaci\u00F3n", AON.CSS.aonIconRestore());
						continueLinkButton.setTabIndex(-5);
						continueLinkButton.addClickHandler((ev) -> {
							FlexTable flexTable = new FlexTable();
							AonDialog dialog = null;
							FlowPanel mobilePanel = null;
							
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
								
								mobilePanel.add(flexTable);
								centerPanel.add(mobilePanel);
							} else {				
								dialog = new AonDialog("Continuar vinculaci\u00F3n", flexTable);
								dialog.setAutoHideEnabled(true);
							}
							
							
							drawShit(opt, dialog, result, requisition, flexTable);
							if (dialog != null) {								
								dialog.setAutoHideEnabled(false);
								dialog.center();
								dialog.show();
							}
							
							Window.open(requisition.getLink(), "REGISTRO DE CUENTA", "_blank");
						});
						
						getMenuPanel().add(continueLinkButton);
					}
					
				}
				
			}
			
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
		
		Date lastMovDate = noridgenankAccount.getLastMovementDate();
		
		FlowPanel perTopFlow = new FlowPanel();
		Label perLbl = new InlineLabel("PER\u00CDODO: ");
		perLbl.addStyleName(AON.CSS.aonFontSmall());
		perLbl.addStyleName(AON.CSS.aonBold());
		perLbl.getElement().getStyle().setColor(AON_BLUE);
		perTopFlow.add(perLbl);
		
		
		ListBox periodSelector = new ListBox();
		
		periodDropdownStyle(periodSelector);
		
		
		FlexTable ft = new FlexTable();
		ColumnFormatter ftf = ft.getColumnFormatter();
		ftf.setWidth(0, "32.5%");
		ftf.setWidth(1, "35%");
		ftf.setWidth(2, "32.5%");
		
		perTopFlow.addStyleName(AON.AON_CSS.aonDisplayBlock());
		perTopFlow.addStyleName(AON.AON_CSS.aonBlockCenter());
		perTopFlow.addStyleName(AON.AON_CSS.aonTextCenter());
		
		FlowPanel onlinePanel = new FlowPanel();
		
		
		if (lastMovDate != null && AonCollectionUtils.isNotEmpty( noridgenankAccount.getNotInsertedMovements() )) {
			long diffGap = new Date().getTime() - lastMovDate.getTime();
			long diffDays = diffGap / (24 * 60 * 60 * 1000) - 1;
			periodSelector.addItem("Movimientos pendientes", "" + diffDays);
			onlinePanel.setVisible(false);
		}
		
		periodSelector.addItem("\u00DAltimos 10 d\u00EDas", "10");
		periodSelector.addItem("\u00DAltimos 30 d\u00EDas", "30");
		periodSelector.addItem("\u00DAltimos 60 d\u00EDas", "60");
		periodSelector.addItem("Personalizado", "0");
		periodSelector.setSelectedIndex(0);
		
		Label toggle = new Label();
		toggle.addStyleName(AON.CSS.aonIconToggleOff());
		toggle.setHeight("1em");
		toggle.setWidth("2em");
		toggle.getElement().getStyle().setProperty("backgroundRepeat", "no-repeat");
		toggle.getElement().getStyle().setProperty("backgroundPosition", CENTER);
		toggle.getElement().getStyle().setProperty("cursor", "pointer");

		Label onlineCheckboxLabel = new Label("Activar para consulta online");
		onlinePanel.getElement().getStyle().setProperty("alignItems", CENTER);
		onlinePanel.addStyleName(AON.CSS.aonDisplayFlex());
		onlinePanel.add(toggle);
		onlinePanel.add(onlineCheckboxLabel);
		
		FlowPanel periodPanel = new FlowPanel();
		periodPanel.setWidth("100%");
		periodPanel.addStyleName(AON.CSS.aonDisplayFlex());
		periodPanel.getElement().getStyle().setProperty("justifyContent", CENTER);
		periodPanel.add(periodSelector);
		if (isMobile()) {
			periodSelector.setWidth("70%");
			ft.setWidget(0, 0, periodPanel);
			ft.getFlexCellFormatter().setColSpan(0, 0, 3);
			ft.getFlexCellFormatter().setColSpan(1, 0, 3);
			onlinePanel.getElement().getStyle().setProperty("justifyContent", CENTER);
			onlinePanel.addStyleName(AON.CSS.aonFontMedium());
			ft.setWidget(1, 0, onlinePanel);
		} else {
			periodSelector.setWidth("95%");
			ft.setWidget(0, 1, periodPanel);
			onlineCheckboxLabel.setWidth("160px");
			onlinePanel.setWidth("100%");
			ft.setWidget(0, 2, onlinePanel);			
		}
		ft.setWidth("100%");
		
		perTopFlow.add(ft);
		
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
		
		Label loadingLabel = new Label();
		
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
			if (periodSelector.getSelectedItemText().equalsIgnoreCase("Movimientos pendientes")) {
				onlinePanel.setVisible(false);
			} else {
				onlinePanel.setVisible(true);				
			}
			movsChangeHandler(opt, noridgenankAccount, firstYear, periodSelector, toggle, monthNames,
					listBoxYearFrom, listBoxYearTo, listBoxMonthFrom, listBoxMonthTo, loadingLabel, movementContainer,
					customPeriod, periodFlow, dialog);
		});
		
		toggle.addClickHandler(ev -> {
			if (toggle.getStyleName().contains(AON.CSS.aonIconToggleOff())) {
				toggle.removeStyleName(AON.CSS.aonIconToggleOff());
				toggle.addStyleName(AON.CSS.aonIconToggleOn());
				onlineCheckboxLabel.setText("Consulta online");
				onlineCheckboxLabel.getElement().getStyle().setColor("green");
				if (!isMobile()) {					
					onlineCheckboxLabel.setWidth("90px");
				}
			} else {
				toggle.removeStyleName(AON.CSS.aonIconToggleOn());
				toggle.addStyleName(AON.CSS.aonIconToggleOff());
				onlineCheckboxLabel.setText("Activar para consulta online");
				onlineCheckboxLabel.getElement().getStyle().setColor("black");
				if (!isMobile()) {					
					onlineCheckboxLabel.setWidth("160px");
				}
			}
			movsChangeHandler(opt, noridgenankAccount, firstYear, periodSelector, toggle, monthNames,
					listBoxYearFrom, listBoxYearTo, listBoxMonthFrom, listBoxMonthTo, loadingLabel, movementContainer,
					customPeriod, periodFlow, dialog);
		});
		
		int days = Integer.parseInt(periodSelector.getSelectedValue());
		Date from = new Date();
		CalendarUtil.addDaysToDate(from, -days);
		String periodStr = "en los \u00FAltimos " + days + "d\u00EDas";
		getMovements(loadingLabel, movementContainer, opt, noridgenankAccount, from, new Date(), periodStr, toggle.getStyleName().contains(AON.CSS.aonIconToggleOn()), dialog);
		
		ChangeHandler onDateChange = ev -> customMovChange(loadingLabel, movementContainer, opt, noridgenankAccount, listBoxMonthFrom, listBoxYearFrom, listBoxMonthTo, listBoxYearTo, firstYear, monthNames, toggle.getStyleName().contains(AON.CSS.aonIconToggleOn()), dialog);
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
		
		container.add(loadingLabel);
		container.add(movementContainer);
	}

	private void movsChangeHandler(NordigenModuleOptions opt, NordigenBankAccount noridgenankAccount, int firstYear,
			ListBox periodSelector, Label onlineToggle, String[] monthNames, ListBox listBoxYearFrom,
			ListBox listBoxYearTo, ListBox listBoxMonthFrom, ListBox listBoxMonthTo, Label loadingLabel,
			FlexTable movementContainer, FlexTable customPeriod, FlowPanel periodFlow, CustomDialog... dialog) {
		if (periodSelector.getSelectedIndex() == periodSelector.getItemCount() - 1) {
			periodFlow.add(customPeriod);				
			customMovChange(loadingLabel, movementContainer, opt, noridgenankAccount, listBoxMonthFrom, listBoxYearFrom, listBoxMonthTo, listBoxYearTo, firstYear, monthNames, onlineToggle.getStyleName().contains(AON.CSS.aonIconToggleOn()), dialog);
		} else {
			if (periodFlow.getWidgetCount() > 1)
				periodFlow.remove(1);
			int days = Integer.parseInt(periodSelector.getSelectedValue());
			Date from = new Date();
			CalendarUtil.addDaysToDate(from, -days);
			
			String periodStr = "en los \u00FAltimos " + days + "d\u00EDas";
			
			getMovements(loadingLabel, movementContainer, opt, noridgenankAccount, from, new Date(), periodStr, onlineToggle.getStyleName().contains(AON.CSS.aonIconToggleOn()), dialog);
		}
	}
	
	
	private void customMovChange(Label loadingLabel, FlexTable movementContainer, NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount, ListBox listBoxMonthFrom, ListBox listBoxYearFrom, ListBox listBoxMonthTo, ListBox listBoxYearTo, int firstYear, String[] monthNames, boolean online, CustomDialog ...dialog) {
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
		
		getMovements(loadingLabel, movementContainer, opt, nordigenBankAccount, dateFrom, dateTo, periodStr, online, dialog);
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
				this.getElement().getStyle().setProperty(MIN_WIDTH, "100%");
			} else {				
				this.getElement().getStyle().setProperty(MIN_WIDTH, "260px");
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
			NORDIGEN_SERVICE.getInstitutions(opt.getConfiguration().getToken(), Country.valueOf(countryIso2), new AsyncCallback<List<NordigenInstitution>>() {

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
			
			registrationTable.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			registrationTable.getElement().getStyle().setProperty("margin", "auto");
			
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
				NORDIGEN_SERVICE.addAccount(opt.getConfiguration().getToken(), opt.getOccam(), nordigenBankAccount, new AsyncCallback<NordigenRequisition>() {

					@Override
					public void onFailure(Throwable caught) {
						
						RegistryBank rbank = nordigenBankAccount.getRbank();
						String bic = AonStringUtils.substring(rbank != null ? rbank.getBic() : "", 0, 8);
						
						errLabel.setText("No se pudo detectar la entidad bancaria, por favor, el\u00EDjala manualmente:");
						registrationTable.setWidget(4, 0, errLabel);
						
						NORDIGEN_SERVICE.getInstitutionsByBic(opt.getConfiguration().getToken(), bic, new AsyncCallback<List<NordigenInstitution>>() {

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
								String instId = availableBanksList.getSelectedValue();
								NordigenInstitution selectedInst = result.stream().filter(inst -> AonStringUtils.equals(instId, inst.getId())).findFirst().orElse(null);
								nordigenBankAccount.setInstitution(selectedInst);
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
		if (dial != null) {			
			dial.setAutoHideEnabled(false);
		}
		int rowSize = registrationTable.getRowCount();
		for (int i=1; i<rowSize; i++) {
			try {
				registrationTable.removeRow(i);
			} catch (Exception e) {}
		}
		Label bankCheckLabel = new Label("Esperando por la vinculaci\u00F3n del banco");
		registrationTable.setWidget(2, 0, bankCheckLabel);
		
		int maxTimes = 100;
		AtomicInteger counter = new AtomicInteger(0);
		
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				NORDIGEN_SERVICE.getRequisition(opt.getConfiguration().getToken(), result.getId(), new AsyncCallback<NordigenRequisition>() {

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.info(caught.getMessage());
						onFinalize(true);
					}

					@Override
					public void onSuccess(NordigenRequisition result) {
						onFinalize(
							NordigenRequisitionStatus.LINKED == result.getStatus() ||
							NordigenRequisitionStatus.REJECTED == result.getStatus()
						);							
					}
					
					private void onFinalize(boolean finalize) {
						LOGGER.info("ONFINALIZE");
						counter.addAndGet(1);
						if (finalize || counter.get() >= maxTimes) {
							cancel();
							reloadPage(opt, () -> {
								if (dial != null) {									
									dial.hide();
								}
							});
						} else {
							schedule(3000);
						}
					}
				});
			}
		};
		
		timer.schedule(3000);
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
			NORDIGEN_SERVICE.cancelRequisition(opt.getConfiguration().getToken(), opt.getOccam()
				, nordigenBankAccount.getRbank().getId(),new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						reloadPage(opt, () -> {
							if (dial != null) {
								dial.hide();
							}								
						});
					}

					@Override
					public void onSuccess(Void result) {
						reloadPage(opt, () -> {
							if (dial != null) {
								dial.hide();
							}								
						});
					}
				}
			);
		});
	}
	
	@FunctionalInterface
	private interface ReloadPageCallback {
		void run();
	}
	
	private void reloadPage(NordigenModuleOptions opt, ReloadPageCallback callback) {
		NORDIGEN_SERVICE.getConfiguration(opt.getOccam(),new AsyncCallback<NordigenConfiguration>() {
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
	
	private void mobileAcceptButton(Button hai) {
		hai.setWidth("80px");
		hai.setHeight("35px");
		
		Style style = hai.getElement().getStyle();
		style.setProperty("background", AON_BLUE);
		style.setProperty("color", WHITE);
		style.setProperty("fontSize", "0.8rem");
		style.setProperty("fontWeight", "700");
		style.setProperty("paddingLeft", "1em");
		style.setProperty("paddingRight", "1em");
		style.setProperty("borderRadius", "6px");
		style.setProperty("boxShadow", "0 2px 4px rgb(0 0 0 / 15%)");
		style.setProperty("transition", "background .25s ease-in-out,transform .15s ease");
	}
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(event -> {
			minimizedByUser = true;
			closeFootPanel();
		});
		footPanel.addMaximizeHandler(event -> openFootPanel());
		footPanel.setStyleName(AON.CSS.aonSelector());
		AonTabLayoutPanel tabLayout = new AonTabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		sessionLog = new FlowPanel();
		sessionLog.getElement().getStyle().setOverflowY(Overflow.SCROLL);
		footPanel.addStyleName(AON.AON_CSS.aonBackgroundWhite());
			
		tabLayout.add(sessionLog, AON.MSG.information());
		
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
	
	
	private void onLoadingMovs(FlexTable tab, Label loadingLabel, boolean loading) {
		tab.removeAllRows();
		loadingLabel.removeStyleName(AON.CSS.aonLoader());
		loadingLabel.setText("");
//		loadingLabel.setWidth("100%");
		if (loading) {
			loadingLabel.addStyleName(AON.CSS.aonMarginTop());
			loadingLabel.addStyleName(AON.CSS.aonMarginBottom());
			loadingLabel.addStyleName(AON.CSS.aonLoader());
			loadingLabel.addStyleName(AON.CSS.aonTextCenter());
			loadingLabel.addStyleName(AON.CSS.aonBlockCenter());
		}
	}
	
	private void getMovements(Label loadingLabel, FlexTable tab, NordigenModuleOptions opt, NordigenBankAccount nordigenBankAccount, Date startDate, Date endDate, String periodStr, boolean online, CustomDialog ...dialog) {
		NordigenConfiguration conf = opt.getConfiguration();
		NordigenAccessToken token = conf != null ? conf.getToken() : null;
		onLoadingMovs(tab, loadingLabel, true);
		
		if (nordigenBankAccount.getLastMovementDate() != null) {
			Date lmd = new Date(nordigenBankAccount.getLastMovementDate().getTime());
			CalendarUtil.addDaysToDate(lmd, 1);
			if (CalendarUtil.isSameDate(startDate, lmd)) {
				onLoadingMovs(tab, loadingLabel, false);
				tab.removeAllRows();
				completeMovementsTable(tab, nordigenBankAccount.getNotInsertedMovements(), periodStr, nordigenBankAccount);
				if (dialog != null && dialog.length > 0) {
					for (CustomDialog dial : dialog) {
						if (dial != null) {							
							dial.center();
						}
					}
				}
				return;
			}
		}
		
		NORDIGEN_SERVICE.getMovements(token, opt.getOccam(), nordigenBankAccount, startDate, online, new AsyncCallback<List<NordigenBankStatement>>() {

			@Override
			public void onFailure(Throwable caught) {
				onLoadingMovs(tab, loadingLabel, false);
				LOGGER.info(caught.getMessage());
				if (!isMobile()) {
					Label errorLabel = new Label("Se produjo un error al obtener los movimientos " + (periodStr != null ? periodStr : ""));
					Label errorLabel2 = new Label("    " + caught.getMessage());
					errorLabel.addStyleName(AON.CSS.aonColorRed());
					errorLabel2.addStyleName(AON.CSS.aonColorRed());
					sessionLog.add(errorLabel);
					sessionLog.add(errorLabel2);
					openFootPanel();
				}
			}

			@Override
			public void onSuccess(List<NordigenBankStatement> result) {
				onLoadingMovs(tab, loadingLabel, false);
				tab.removeAllRows();
				if (dialog != null) {					
					for (CustomDialog dial : dialog) {
						if (dial != null) {							
							dial.center();
						}
					}
				}
				completeMovementsTable(tab, result, periodStr, nordigenBankAccount);
				if (dialog != null && dialog.length > 0) {
					for (CustomDialog dial : dialog) {
						if (dial != null) {
							dial.center();							
						}
					}
				}
			}

		});
	}
	
	private void completeMovementsTable(FlexTable tab, List<NordigenBankStatement> statements, String periodStr, NordigenBankAccount nordigenBankAccount) {
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
				.collect(Collectors.toList());
				
				for (int i=0; i< st.size(); i++) {
					NordigenBankStatement mov = st.get(i);
					getPendingMovementTag(tab, mov, i == st.size() - 1, nordigenBankAccount);
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
	
	private void getPendingMovementTag(FlexTable table, NordigenBankStatement bankStatement, boolean last, NordigenBankAccount nordigenBankAccount) {
		
		String description = bankStatement.getDescription();
		Double amount = !bankStatement.isPayment() ? bankStatement.getAmount() : bankStatement.getAmount() * (-1);
		Double balance = bankStatement.getCurrentBalance();
		
		FlowPanel amountsFlow = new FlowPanel();
		
		Label amountLabel = new Label();
		if (amount != null) {
			amountLabel.setText(AON.FMT.format(amount) + " " + EURO);
		}
		amountLabel.addStyleName(AON.CSS.aonBold());
		amountLabel.addStyleName(AON.CSS.aonTextRight());
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
//			if (!bankStatement.isPending()) {
				balanceLabel.setText(AON.FMT.format(balance) + " " + EURO);
//			} else {
//				balanceLabel.setText("No consolidado");
//			}
			amountsFlow.add(balanceLabel);
			balanceLabel.addStyleName(AON.CSS.aonTextRight());
			balanceLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
			balanceLabel.setWidth("100%");
		}
		
		
		Label descriptionLabel = new Label(description);
		Date lastOp = nordigenBankAccount.getLastMovementDate();
		if (lastOp != null &&
			!CalendarUtil.isSameDate(lastOp, bankStatement.getOperationDate()) &&
			lastOp.compareTo(bankStatement.getOperationDate()) < 1
		) {
			descriptionLabel.getElement().getStyle().setColor("green");			
		}
		if (bankStatement.isPending()) {
			descriptionLabel.getElement().getStyle().setColor("darkOrange");
		}
		
		if (isMobile()) {
			balanceLabel.getElement().getStyle().setFontSize(11, Unit.PX);
			descriptionLabel.getElement().getStyle().setFontSize(13, Unit.PX);
			amountLabel.getElement().getStyle().setFontSize(14, Unit.PX);
		} else {				
			balanceLabel.addStyleName(AON.CSS.aonFontLarger());
			descriptionLabel.setStyleName(AON.CSS.aonFontLarger());
			amountLabel.addStyleName(AON.CSS.aonFontMedium());			
		}
		
		descriptionLabel.setWidth("100%");
		if (!isMobile()) {			
		amountLabel.getElement().getStyle().setPaddingRight(1, Unit.EM);
		balanceLabel.getElement().getStyle().setPaddingRight(1, Unit.EM);
		descriptionLabel.getElement().getStyle().setMarginLeft(1, Unit.EM);
		descriptionLabel.getElement().getStyle().setMarginTop(1, Unit.EM);
		descriptionLabel.getElement().getStyle().setMarginBottom(1, Unit.EM);
		}
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
			if (!isMobile()) {				
				euskoLabel.getElement().getStyle().setMarginLeft(1, Unit.EM);
				euskoLabel.getElement().getStyle().setMarginRight(1, Unit.EM);
			}
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
		style.setProperty("background", WHITE);
		style.setProperty("color", AON_BLUE);
		style.setProperty("fontSize", "1rem");
		style.setProperty("fontWeight", "700");
		style.setProperty("border", "2px solid " + AON_BLUE);
		style.setProperty("borderRadius", "6px");
		style.setProperty("transition", "background .25s ease-in-out,transform .15s ease");
		style.setProperty("margin-right", "2.5px");
		
		iie.addMouseOverHandler(ev -> {
			style.setProperty("background", HOVER_COLOR);
			style.setProperty("color", WHITE);
			style.setProperty("border", "2px solid " + HOVER_COLOR);
		});
		
		iie.addMouseOutHandler(ev -> {
			style.setProperty("background", WHITE);
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
		style.setProperty("color", WHITE);
		style.setProperty("fontSize", "1rem");
		style.setProperty("fontWeight", "700");
		style.setProperty("border", "none");
		style.setProperty("borderRadius", "6px");
		style.setProperty("border", "2px solid " + AON_BLUE);
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
	
	private static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (AonStringUtils.isBlank(className) || (className.indexOf(GWT_SELECTOR_CLASS) == -1)) {
			body.addClassName(GWT_SELECTOR_CLASS);
		}
	}

	private static boolean isMobile() {
		String userAgent = AonStringUtils.trimToEmpty(Window.Navigator.getUserAgent());
		String platform = AonStringUtils.trimToEmpty(Window.Navigator.getPlatform());
		return AonStringUtils.containsIgnoreCase(userAgent, "mobile") || AonStringUtils.containsIgnoreCase(platform, "mobile");
	}

}		