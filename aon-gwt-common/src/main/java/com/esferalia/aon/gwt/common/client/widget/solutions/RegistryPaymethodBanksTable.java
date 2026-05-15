package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryBankPanel.AonRegistryBankPanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class RegistryPaymethodBanksTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(RegistryPaymethodBanksTable.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt(0);
	
	private HTMLPanel content;
	
	private HTMLPanel registryPaymethodPanel;
	private AonCustomListBox paymethod = new AonCustomListBox("Forma de pago");
	private AonCustomListBox banks = new AonCustomListBox("Cuenta Bancaria");
	private AonCustomIntegerBox pays = new AonCustomIntegerBox("Pagos");
	private AonCustomIntegerBox firstPay = new AonCustomIntegerBox("1er Pago");
	private AonCustomIntegerBox betweenDays = new AonCustomIntegerBox("Resto");
	private AonCustomTextBox payDayss = new AonCustomTextBox("Dias");
	

	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;

	private String domainName;
	private Integer domain;
	private String user;
	private Integer registry;
	
	private LinkedList<PayMethod> payMethods;
	private LinkedList<RegistryBank> rBanks;
	private List<Account> accounts;
	
	private RegistryPayMethod rPayMethod;
	
	private boolean showActiveBanks = true;

	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final String ATTACH_RECORDDATA_URL = "/ms/api/attach/recordData";
	private static final String SESSION_API = "AONd95770f269e711eb94390242ac130002";

	private static enum COLS {
		STA("Activo", "4rem", "max-width: 4rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		BAN("Banco", "-moz-available", "min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		IBA("IBAN", "-moz-available", "min-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		SWI("SWIFT", "10rem", "max-width: 10rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		SUF("Sufijo", "4rem", "max-width: 4rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		CUE("Cuenta Contable", "12rem", "max-width: 12rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		BUT(AonStringUtils.EMPTY, "4rem", "");

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel, String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}

		public String getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getStyles() {
			return styles;
		}
	}

	public RegistryPaymethodBanksTable(String domainName, int domain, String user, Integer registry) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.registry = registry;
		
		content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn2());
		
		registryPaymethodPanel = new HTMLPanel("");
		registryPaymethodPanel.addStyleName(AON.CSS.aonItemFlex());
		registryPaymethodPanel.getElement().getStyle().setProperty("padding", "1rem 1rem 0");
		content.add(registryPaymethodPanel);

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
		
		content.add(container);
		
		setWidget(content);

		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});

		getContext(end -> {
			createRPaymethodPanel();
			onSearch();
		});
		

	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0);
	}

	public void disableSearch() {
		searchEnabled.setValue(-1);
	}

	public void enableSearch() {
		searchEnabled.setValue(0);
	}

	public boolean isMoreData() {
		return (moreData.getValue() == 0);
	}

	public void disableMoreData() {
		moreData.setValue(-1);
	}

	public void enableMoreData() {
		moreData.setValue(0);
	}

	public void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		container.clear();
		
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		scrollPanel = new ScrollPanel(tab);

		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}

	private void createRPaymethodPanel() {
		registryPaymethodPanel.clear();
		
		paymethod.clearItems();
		paymethod.addItem("-" , "");
		payMethods.forEach(pm -> paymethod.addItem(pm.getName(), pm.getId().toString()));
		paymethod.getElement().getStyle().setProperty("max-width", "12rem");
		
		banks.clearItems();
		banks.addItem("-" , "");
		rBanks.forEach(b -> banks.addItem("(" + b.getAlias() + ") " + b.getBankAccount().toString(), b.getId().toString()));
		
		pays.setTitle("Numero de vencimiento por factura");
		firstPay.setTitle("Plazo en dias al primer pago");
		betweenDays.setTitle("Plazo en dias entre el resto de pagos");
		payDayss.setTitle("Dias fijos de pago separados por espacios");
		
		pays.hideNearBy();
		firstPay.hideNearBy();
		betweenDays.hideNearBy();
		
		pays.getElement().getStyle().setProperty("max-width", "4rem");
		firstPay.getElement().getStyle().setProperty("max-width", "4rem");
		betweenDays.getElement().getStyle().setProperty("max-width", "4rem");
		payDayss.getElement().getStyle().setProperty("max-width", "4rem");
		
		payDayss.getTextBox().getElement().setPropertyString("placeholder", "");
		
		AonTableButton saveRpaymethod = new AonTableButton("Guardar Metodo de Pago", AON.CSS.aonIconSave());
		saveRpaymethod.setEnabled(false);
		saveRpaymethod.getElement().getStyle().setProperty("margin-top", ".5rem");
		saveRpaymethod.addClickHandler(e -> {
			rPayMethod.setPayMethod(AonStringUtils.isBlank(paymethod.getValue()) ? null : payMethods.stream().filter(pm -> pm.getId().equals(Integer.parseInt(paymethod.getValue()))).findFirst().orElse(null));
			rPayMethod.setRbank(AonStringUtils.isBlank(banks.getValue()) ? null : rBanks.stream().filter(b -> b.getId().equals(Integer.parseInt(banks.getValue()))).findFirst().orElse(null));
			rPayMethod.setNumberOfPymnts(pays.getValue().shortValue());
			rPayMethod.setDaysToFirstPymnt(firstPay.getValue().shortValue());
			rPayMethod.setDaysBetwenPymnts(betweenDays.getValue().shortValue());
			rPayMethod.setPymntDays(payDayss.getValue());
			
			COMMON_SERVICE.saveRegistryPayMethod(domainName, domain, user, rPayMethod, new AsyncCallback<RegistryPayMethod>() {

				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error bancos: " + caught.getMessage());
				}

				@Override
				public void onSuccess(RegistryPayMethod registryPayMethod) {
					rPayMethod = registryPayMethod;
					onShowSuccess("Forma de pago actualizada");
					saveRpaymethod.setEnabled(false);
				}
			});
			
		});
		
		paymethod.addChangeHandler(e -> saveRpaymethod.setEnabled(true));
		banks.addChangeHandler(e -> saveRpaymethod.setEnabled(true));
		pays.addValueChangeHandler(e ->  saveRpaymethod.setEnabled(true));
		firstPay.addValueChangeHandler(e ->  saveRpaymethod.setEnabled(true));
		betweenDays.addValueChangeHandler(e ->  saveRpaymethod.setEnabled(true));
		payDayss.addValueChangeHandler(e ->  saveRpaymethod.setEnabled(true));
		
		registryPaymethodPanel.add(paymethod);
		registryPaymethodPanel.add(banks);
		registryPaymethodPanel.add(pays);
		registryPaymethodPanel.add(firstPay);
		registryPaymethodPanel.add(betweenDays);
		registryPaymethodPanel.add(payDayss);
		registryPaymethodPanel.add(saveRpaymethod);
		
		if(null != rPayMethod.getId()) {
			paymethod.setValue(null == rPayMethod.getPayMethod().getId() ? "" : rPayMethod.getPayMethod().getId().toString());
			banks.setValue(null == rPayMethod.getRbank().getId() ? "" : rPayMethod.getRbank().getId().toString());
			pays.setValueShort(rPayMethod.getNumberOfPymnts());
			firstPay.setValueShort(rPayMethod.getDaysToFirstPymnt());
			betweenDays.setValueShort(rPayMethod.getDaysBetwenPymnts());
			payDayss.setValue(rPayMethod.getPymntDays());
		} else {
			rPayMethod = new RegistryPayMethod()
					.setDomain(domain)
					.setRegistry(registry)
					;
		}
			
	}

	private void paintHeader() {
		tab.createHeader();
		for (COLS col : COLS.values()) {
			if (col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);

				AonTableButton button = new AonTableButton("Nueva Cuenta Bancaria", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.addClickHandler(e -> createonRegistryBank());
				buttonContainer.add(button);

				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else if (col.equals(COLS.STA)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.LEFT);

				CheckBox showActive = new CheckBox("Act.");
				showActive.setTitle("Solo activos");
				showActive.addStyleName(AON.CSS.aonCustomRowButtom());
				showActive.setValue(showActiveBanks);
				showActive.getElement().getStyle().setProperty("flex-direction", "row-reverse");
				showActive.addValueChangeHandler(e -> {
					showActiveBanks = !showActiveBanks;
					onSearch();
				});
				buttonContainer.add(showActive);

				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());

		}
	}

	private void searchData() {
		if (!isMoreData())
			return;

		getList(registryBanks -> {
			boolean something = false;

			for (RegistryBank registryBank : registryBanks) {
				something = true;
				paintRow(registryBank);
			}

			if (registryBanks.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + registryBanks.size() - 1);
				enableMoreData();
			}

			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
				disableMoreData();
			}
			enableSearch();

		});
	}

	private void paintRow(RegistryBank registryBank) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		buttonContainer.addStyleName(AON.CSS.aonItemFlex());
		buttonContainer.getElement().getStyle().setProperty("justify-content", "end");
		
		AonTableButton button;
		button = new AonTableButton("Borrar Dato Registral", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cuenta Bancaria",
						new HTML("Se va a proceder a eliminar la cuenta bancaria <b>" + registryBank.getAlias()
								+ "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));

				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(registryBank);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateRegistryBank(registryBank), ClickEvent.getType());
		
		Button status = new Button();
		getEnableDisableButton(status, registryBank.isActive());
		status.setTitle(registryBank.isActive() ? "Activo" : "Inactivo");
		status.getElement().getStyle().setProperty("background-position-x", "center");
		tab.addInlineStyle(status, COLS.STA.getStyles());
		tab.addRow(row, status, COLS.STA.getColWidth());

		Label bank = new Label(registryBank.getAlias());
		bank.setTitle(registryBank.getAlias());
		tab.addInlineStyle(bank, COLS.BAN.getStyles());
		tab.addRow(row, bank, COLS.BAN.getColWidth());
		
		Label iban = new Label(registryBank.getBankAccount().toString());
		iban.setTitle(registryBank.getBankAccount().toString());
		tab.addInlineStyle(iban, COLS.IBA.getStyles());
		tab.addRow(row, iban, COLS.IBA.getColWidth());
		
		Label swift = new Label(registryBank.getBic());
		swift.setTitle(registryBank.getBic());
		tab.addInlineStyle(swift, COLS.SWI.getStyles());
		tab.addRow(row, swift, COLS.SWI.getColWidth());
		
		Label suffix = new Label(registryBank.getSuffix());
		suffix.setTitle(registryBank.getSuffix());
		tab.addInlineStyle(suffix, COLS.SUF.getStyles());
		tab.addRow(row, suffix, COLS.SUF.getColWidth());
		
		Label account = new Label(null == registryBank.getAccount() ? "" : registryBank.getAccount().getFullName());
		account.setTitle(null == registryBank.getAccount() ? "" : registryBank.getAccount().getFullName());
		tab.addInlineStyle(account, COLS.CUE.getStyles());
		tab.addRow(row, account, COLS.CUE.getColWidth());

		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}

	public static void downloadFile(String jsonParam, String sessionId) {
		String url = "/ms/api/download?json=" + URL.encodeQueryString(jsonParam);
		Window.open(url, "_blank", "");
	}

	private static native String base64Encode(String text) /*-{
	    return btoa(text);
	}-*/;

	private void getList(Consumer<List<RegistryBank>> success) {
		COMMON_SERVICE.getRregistryBanks(domainName, domain, user, registry, new AsyncCallback<List<RegistryBank>>() {

			@Override
			public void onSuccess(List<RegistryBank> registryBanks) {
				List<RegistryBank> parseRegistryBanks = registryBanks.stream().filter(b -> (showActiveBanks && b.isActive()) || !showActiveBanks).collect(Collectors.toList());
				success.accept(parseRegistryBanks);
			}

			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

	private void delete(RegistryBank registryBank) {
		COMMON_SERVICE.deleteRregistryBank(domainName, domain, user, registryBank.getId(), new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				getContext(end -> {
					createRPaymethodPanel();
					onSearch();
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void getContext(Consumer<Void> end) {
		COMMON_SERVICE.getPayMethods(domainName, domain, user, new AsyncCallback<LinkedList<PayMethod>>() {

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error metodos de pago: " + caught.getMessage());
			}

			@Override
			public void onSuccess(LinkedList<PayMethod> payMethodsDB) {
				payMethods = payMethodsDB;
				
				COMMON_SERVICE.getCompanyBanks(domainName, domain, user, new AsyncCallback<LinkedList<RegistryBank>>() {

					@Override
					public void onFailure(Throwable caught) {
						onShowErrorMessage("Error bancos: " + caught.getMessage());
					}

					@Override
					public void onSuccess(LinkedList<RegistryBank> rBanksDB) {
						rBanks = rBanksDB;
						
						COMMON_SERVICE.getAccountsForBank(domainName, domain, user, new AsyncCallback<List<Account>>() {

							@Override
							public void onFailure(Throwable caught) {
								onShowErrorMessage("Error bancos: " + caught.getMessage());
							}

							@Override
							public void onSuccess(List<Account> accountsDB) {
								accounts = accountsDB;
								
								COMMON_SERVICE.getRegistryPayMethods(domainName, domain, user, registry, new AsyncCallback<List<RegistryPayMethod>>() {

									@Override
									public void onFailure(Throwable caught) {
										onShowErrorMessage("Error bancos: " + caught.getMessage());
									}

									@Override
									public void onSuccess(List<RegistryPayMethod> registryPayMethodsDB) {
										if(!registryPayMethodsDB.isEmpty())
											rPayMethod = registryPayMethodsDB.get(0);
										
										end.accept(null);
									}
								});
							}
						});
					}
				});
			}
		});
	}

	private void onUpdateRegistryBank(RegistryBank registryBank) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Editar Cuenta Bancaria");

		final AonRegistryBankPanel aonRegistryBankPanel = new AonRegistryBankPanel(domainName, domain, user, registryBank, accounts,
				new AonRegistryBankPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(RegistryBank registryBank) {
						dialog.hide();
						getContext(end -> {
							createRPaymethodPanel();
							onSearch();
						});
					}
				});

		dialog.add(aonRegistryBankPanel);
		dialog.showLoaded();
	}

	private void createonRegistryBank() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Nueva Cuenta Bancaria");

		final AonRegistryBankPanel aonRegistryBankPanel = new AonRegistryBankPanel(domainName, domain, user, registry, accounts,
				new AonRegistryBankPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(RegistryBank registryBank) {
						dialog.hide();
						getContext(end -> {
							createRPaymethodPanel();
							onSearch();
						});
					}
				});

		dialog.add(aonRegistryBankPanel);
		dialog.showLoaded();

	}

	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowSuccess(String successMessage);

}
