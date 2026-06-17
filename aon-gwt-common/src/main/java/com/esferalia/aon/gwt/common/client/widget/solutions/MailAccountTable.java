package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Comparator;
import java.util.HashMap;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMailAccountPanel.AonMailAccountPanelCallback;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class MailAccountTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MailAccountTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private HTMLPanel content;
	
	private HTMLPanel filterPanel;
	private AonCustomTextBox textFilter = new AonCustomTextBox("Filtrar (descripci\u00f3n, email...)");
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	
	private String domainName;
	private Integer domain;
	private String user;
	
	private LinkedList<Signature> signatures;
	private List<MailAccount> mailAccountList;
	
	private HashMap<Integer, AonTableButton> veficationStatus = new HashMap<Integer, AonTableButton>();
	private HashMap<Integer, MailAccount> mailAccounts = new HashMap<Integer, MailAccount>();
	private HashMap<Integer, Boolean> checkVeficationStatus = new HashMap<Integer, Boolean>();
	
	private boolean showUserEmails = true;
	
	private static enum COLS {
		  TYP("Tipo"								, "5rem"			, "max-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, NAM(AON.MSG.description()					, "-moz-available"	, "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SHA("Mostrar como"						, "13rem"			, "max-width: 13rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, EMA("Email"								, "13rem"			, "max-width: 13rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SIG("Firma"								, "8rem"			, "max-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("Estado"								, "5rem"			, "")
		, BUT(AonStringUtils.EMPTY					, "3rem"			, "")
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel,String colWidth, String styles) {
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
	
	public MailAccountTable(String domainName, int domain, String user) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		
		content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn2());
		
		filterPanel = new HTMLPanel("");
		filterPanel.addStyleName(AON.CSS.aonItemFlex());
		filterPanel.getElement().getStyle().setProperty("margin-left", "auto");
		filterPanel.getElement().getStyle().setProperty("width", "16rem");
		content.add(filterPanel);

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");		
		content.add(container);
		
		setWidget(content);
		
		loadData();
	}
	
	private void loadData() {
		getSignatures(end -> {
			getList(mails -> {
				
				if(mails.size() > 9) {
					filterPanel.getElement().getStyle().clearDisplay();
				} else {
					filterPanel.getElement().getStyle().setDisplay(Display.NONE);
				}
				
				createFilterPanel();
				onSearch();
				
				checkMailAccounts(checkMailAccounts -> {
					checkMailAccounts.entrySet().forEach(e -> {
						AonTableButton status = veficationStatus.get(e.getKey());
						
						if(null != status) {
							status.removeStyleName(AON.CSS.aonSpin());
							status.removeStyleName(AON.CSS.aonIconRefresh());
							
							if(e.getValue()) {
								status.addStyleName(AON.CSS.aonIconCheckCircle());
								status.setTitle("Verificado");
							} else {
								status.addStyleName(AON.CSS.aonIconCancelCircle());
								status.setTitle("No Verificado");
							}
						}
						
						mailAccounts.get(e.getKey()).setSESVerified(e.getValue());
							
					});
				});
			});
		});
	}
	
	private void createFilterPanel() {
		filterPanel.clear();
		
		filterPanel.add(textFilter);
		
		textFilter.getTextBox().addKeyUpHandler(e -> {
			String value = textFilter.getValue();
			if(e.getNativeKeyCode() == KeyCodes.KEY_ENTER || e.getNativeKeyCode() == KeyCodes.KEY_MAC_ENTER) return;
			
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
	}
	
	public void onSearch() {
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		tab.getElement().getStyle().setProperty("padding", "1rem 0");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) {
			if(col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
				
				AonTableButton button = new AonTableButton("Nueva firma", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.getElement().getStyle().setProperty("border", "2px solid #434548");
				button.getElement().getStyle().setProperty("padding", "10px");
				button.getElement().getStyle().setProperty("border-radius", "50%");
				button.addClickHandler(e -> createMailAccount());
				buttonContainer.add(button);
				
				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else if (col.equals(COLS.TYP)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.LEFT);

				CheckBox showActive = new CheckBox("Usr.");
				showActive.setTitle("Todos");
				showActive.addStyleName(AON.CSS.aonCustomRowButtom());
				showActive.setValue(showUserEmails);
				showActive.getElement().getStyle().setProperty("flex-direction", "row-reverse");
				showActive.addValueChangeHandler(e -> {
					showUserEmails = !showUserEmails;
					onSearch();
				});
				buttonContainer.add(showActive);

				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		}
	}
	
	private void searchData() {
		boolean something = false;
		
		List<MailAccount> mailAccountListTable = mailAccountList.stream()
				.filter(ma -> showUserEmails || (!showUserEmails && null == ma.getUserId()) )
				.filter(ma -> 
					AonStringUtils.isBlank(textFilter.getValue()) ||
					(
						AonStringUtils.containsIgnoreCase(ma.getDisplayName(), textFilter.getValue()) ||
						AonStringUtils.containsIgnoreCase(ma.getEmail(), textFilter.getValue()) ||
						AonStringUtils.containsIgnoreCase(ma.getName(), textFilter.getValue())
					)
				)
				.collect(Collectors.toList());
		
		for(MailAccount mailAccount : mailAccountListTable) {
			something = true;
			paintRow(mailAccount);
		}
		
		if (!something) {
			HTMLPanel row = tab.createRow();
			
			Label empty = new Label("No exiten datos");
			tab.addInlineStyle(empty, COLS.NAM.getStyles());
			tab.addRow(row, empty, COLS.NAM.getColWidth());
		}
	}
	
	private void paintRow(MailAccount mailAccount) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton("Borrar email", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(e -> {
			e.stopPropagation();
			button.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Firma",
					new HTML("Se va a proceder a eliminar el email <b>" + mailAccount.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					button.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(mailAccount);
				}
			});
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateMailAccount(mailAccounts.get(mailAccount.getId())), ClickEvent.getType());
		
		Label type = new Label(mailAccount.getUserId() != null ? "Usuario" : "Empresa");
		type.setTitle(mailAccount.getName());
		tab.addInlineStyle(type, COLS.TYP.getStyles());
		tab.addRow(row, type, COLS.TYP.getColWidth());
		
		Label name = new Label(mailAccount.getName());
		name.setTitle(mailAccount.getName());
		tab.addInlineStyle(name, COLS.NAM.getStyles());
		tab.addRow(row, name, COLS.NAM.getColWidth());

		Label showAs = new Label(mailAccount.getDisplayName());
		showAs.setTitle(mailAccount.getDisplayName());
		tab.addInlineStyle(showAs, COLS.SHA.getStyles());
		tab.addRow(row, showAs, COLS.SHA.getColWidth());
		
		Label email = new Label(mailAccount.getEmail());
		email.setTitle(mailAccount.getEmail());
		tab.addInlineStyle(email, COLS.EMA.getStyles());
		tab.addRow(row, email, COLS.EMA.getColWidth());
		
		Signature mailAccountSignature = signatures.stream().filter(s -> s.getId().equals(mailAccount.getSignatureId())).findFirst().orElse(null);
		
		Label signature = new Label(null == mailAccountSignature ? "" : mailAccountSignature.getName());
		signature.setTitle(null == mailAccountSignature ? "" : mailAccountSignature.getName());
		tab.addInlineStyle(signature, COLS.SIG.getStyles());
		tab.addRow(row, signature, COLS.SIG.getColWidth());
		
		AonTableButton status = new AonTableButton("Verificando", AON.CSS.aonIconRefresh());
		status.addStyleName(AON.CSS.aonSpin());
		tab.addInlineStyle(status, COLS.STA.getStyles());
		tab.addRow(row, status, COLS.STA.getColWidth());
		
		if(null != mailAccount.getId() && null != veficationStatus.get(mailAccount.getId())) {
			status.removeStyleName(AON.CSS.aonSpin());
			status.removeStyleName(AON.CSS.aonIconRefresh());
			
			if(checkVeficationStatus.get(mailAccount.getId())) {
				status.addStyleName(AON.CSS.aonIconCheckCircle());
				status.setTitle("Verificado");
			} else {
				status.addStyleName(AON.CSS.aonIconCancelCircle());
				status.setTitle("No Verificado");
			}
		}
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		veficationStatus.put(mailAccount.getId(), status);
	}

	private void getList(Consumer<List<MailAccount>> success) {
		COMMON_SERVICE.getMailAccounts(domainName, domain, user, new AsyncCallback<LinkedList<MailAccount>>() {
			
			@Override
			public void onSuccess(LinkedList<MailAccount> mailAccountsDB) {
				mailAccountsDB.sort(
					    Comparator.comparing(
					        MailAccount::getUserId,
					        Comparator.nullsFirst((u1, u2) -> 0) // solo agrupa null primero
					    ).thenComparing(MailAccount::getName)
					);
				
				mailAccountsDB.forEach(m -> mailAccounts.put(m.getId(), m));
				mailAccountList = mailAccountsDB;
				success.accept(mailAccountList);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error emails: " + caught.getMessage());
			}
		});
	}
	
	private void checkMailAccounts(Consumer<HashMap<Integer, Boolean>> success) {
		COMMON_SERVICE.checkMailAccounts(domainName, domain, user, new AsyncCallback<HashMap<Integer, Boolean>>() {
			
			@Override
			public void onSuccess(HashMap<Integer, Boolean> checkMailAccounts) {
				checkVeficationStatus = checkMailAccounts;
				success.accept(checkMailAccounts);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error check mails: " + caught.getMessage());
			}
		});
	}

	private void getSignatures(Consumer<List<Signature>> success) {
		COMMON_SERVICE.getSignatures(domainName, domain, user, new AsyncCallback<LinkedList<Signature>>() {
			
			@Override
			public void onSuccess(LinkedList<Signature> signaturesDB) {
				signatures = signaturesDB;
				success.accept(signaturesDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error firmas: " + caught.getMessage());
			}
		});
	}
	
	private void delete(MailAccount mailAccount) {
		COMMON_SERVICE.deleteMailAccount(domainName, domain, user, mailAccount.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void onUpdateMailAccount(MailAccount mailAccount) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Editar Email" );
		
		final AonMailAccountPanel aonSignaturePanel = new AonMailAccountPanel( domainName, domain, user, signatures, mailAccount, new AonMailAccountPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(MailAccount mailAccount) {
				dialog.hide();
				loadData();
			}
		});
		
		dialog.add( aonSignaturePanel );
		dialog.showLoaded();
	}

	private void createMailAccount() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Nuevo Email");

		final AonMailAccountPanel aonSignaturePanel = new AonMailAccountPanel(domainName, domain, user, signatures, new AonMailAccountPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(MailAccount mailAccount) {
						dialog.hide();
						loadData();
					}
				});
		
		dialog.add(aonSignaturePanel);
		dialog.showLoaded();
		
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	
}

