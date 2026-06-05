package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMailAccountPanel.AonMailAccountPanelCallback;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailAccountType;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private String domainName;
	private Integer domain;
	private String user;
	
	private LinkedList<Signature> signatures;
	
	private HashMap<Integer, AonTableButton> veficationStatus = new HashMap<Integer, AonTableButton>();
	private HashMap<Integer, MailAccount> mailAccounts = new HashMap<Integer, MailAccount>();
	
	private static enum COLS {
		  NAM(AON.MSG.name()						, "-moz-available"	, "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SHA("Mostrar como"						, "13rem"			, "max-width: 13rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, EMA("Email"								, "13rem"			, "max-width: 13rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SIG("Firma"								, "8rem"			, "max-width: 8rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP("Tipo"								, "5rem"			, "max-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
		setWidget(container);
		
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
		
		getSignatures(end -> onSearch());
	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
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
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		}
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		getList(mailAccountList -> {
			boolean something = false;
			
			for(MailAccount mailAccount : mailAccountList) {
				something = true;
				paintRow(mailAccount);
			}
			
			if (mailAccountList.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + mailAccountList.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				HTMLPanel row = tab.createRow();
				
				Label empty = new Label("No exiten datos");
				tab.addInlineStyle(empty, COLS.NAM.getStyles());
				tab.addRow(row, empty, COLS.NAM.getColWidth());
				disableMoreData();
			}
			enableSearch();
			
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
		
		Label type = new Label(mailAccount.getType() == MailAccountType.USER ? "Usuario" : "Empresa");
		type.setTitle(mailAccount.getName());
		tab.addInlineStyle(type, COLS.TYP.getStyles());
		tab.addRow(row, type, COLS.TYP.getColWidth());
		
		AonTableButton status = new AonTableButton("Verificando", AON.CSS.aonIconRefresh());
		status.addStyleName(AON.CSS.aonSpin());
		tab.addInlineStyle(status, COLS.STA.getStyles());
		tab.addRow(row, status, COLS.STA.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		veficationStatus.put(mailAccount.getId(), status);
	}

	private void getList(Consumer<List<MailAccount>> success) {
		COMMON_SERVICE.getMailAccounts(domainName, domain, user, new AsyncCallback<LinkedList<MailAccount>>() {
			
			@Override
			public void onSuccess(LinkedList<MailAccount> mailAccountsDB) {
				mailAccountsDB.forEach(m -> mailAccounts.put(m.getId(), m));
				success.accept(mailAccountsDB);
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
				onSearch();
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
						onSearch();
					}
				});
		
		dialog.add(aonSignaturePanel);
		dialog.showLoaded();
		
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	
}

