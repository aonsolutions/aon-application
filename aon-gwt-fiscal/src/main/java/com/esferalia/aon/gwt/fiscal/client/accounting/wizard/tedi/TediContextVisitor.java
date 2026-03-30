package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryPanel.AonAccountingRegistryPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKeyVisitor;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.ITediCallback;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediRegistry;

public class TediContextVisitor implements InvoiceErrorKeyVisitor<ICallback> {

	private static final Logger LOGGER = Logger.getLogger(TediContextVisitor.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final AccountEntryServiceAsync SERVICE;
	static {
		AccountEntryServiceAsync serviceRaw = GWT.create(AccountEntryService.class);
		SERVICE = new AccountEntryServiceAsyncDecorator(serviceRaw);
	}
	
	private AccountEntryModuleOptions options;
	private FlowPanel container;

	
	public TediContextVisitor(AccountEntryModuleOptions moduleOptions, FlowPanel container) {
		this.options = moduleOptions;
		this.container = container;
	}

	private AccountEntryModuleOptions getOptions() {
		return this.options;
	}

	public FlowPanel getContainer() {
		return container;
	}

	@Override
	public void visitIssueDate(ICallback callback) {
		showDateDialog(AON.MSG.issueDate(), callback.getResult().getInvoice().getIssueDate(), new ITediCallback<Date>() {
			@Override
			public ICallback getCallback() {
				return callback;
			}
			@Override
			public void onAccept(Date date) {
				callback.getResult().getTedi().setDate(date);
				callback.getResult().getAccountingInvoice().getAccountEntry().setEntryDate(date);
				callback.getResult().getInvoice().setIssueDate(date);
				callback.getResult().getInvoice().setTaxDate(date);
				if (date != null) {
					for (AccountPeriod period : getCallback().getConfiguration().accounting().getPeriods()) {
						if (!date.before(period.getInitiationDate()) && !date.after(period.getDeadline())) {
							callback.getResult().getAccountingInvoice().getAccountEntry().setPeriod(period.getId());		
							callback.getResult().getAccountingInvoice().getAccountEntry().setPeriodName(period.getName());
							callback.getResult().getAccountingInvoice().getAccountEntry().setPeriodStatus(period.getStatus());
						}
					}
				} else {
					callback.getResult().getAccountingInvoice().getAccountEntry().setPeriod(null);		
					callback.getResult().getAccountingInvoice().getAccountEntry().setPeriodName(null);
					callback.getResult().getAccountingInvoice().getAccountEntry().setPeriodStatus(null);
				}
				callback.onAccept(callback.getResult());
			}

			@Override
			public void onCancel() {
				callback.onCancel();
			}
		});
	}

	@Override
	public void visitTaxDate(ICallback callback) {
		showDateDialog(AON.MSG.issueDate(), callback.getResult().getInvoice().getTaxDate(), new ITediCallback<Date>() {
			@Override
			public ICallback getCallback() {
				return callback;
			}
			@Override
			public void onAccept(Date date) {
				callback.getResult().getInvoice().setTaxDate(date);
				callback.onAccept(callback.getResult());
			}

			@Override
			public void onCancel() {
				callback.onCancel();
			}
		});
	}

	@Override
	public void visitAmbiguousRegistry(ICallback callback) {
		if ( callback.getResult().getInvoice().getIssueDate() != null) {
			showAmbiguousRegistryDialog(AON.MSG.titular(), new ITediCallback<AccountingRegistry>() {
				@Override
				public ICallback getCallback() {
					return callback;
				}
				@Override
				public void onAccept(AccountingRegistry registry) {
					final AccountingRegistry ar = registry;
					if (ar != null) {
						LOGGER.info( ar.getId() + " " + ar.getAccountCode() + " " + ar.getName());
					}
					SERVICE.initializeInvoice(getOptions().getOccam(), ar, null,
							callback.getResult().getInvoice().getIssueDate(), new AsyncCallback<AccountingInvoice>() {
						
						@Override
						public void onSuccess(AccountingInvoice ai) {
							callback.getResult().getAccountingInvoice().setRegistry(ai.getRegistry());
							callback.getResult().getInvoice().setRegistry(ai.getRegistry().getId());
							callback.getResult().getInvoice().setRegistryDocumentType(ai.getRegistry().getDocumentType());
							callback.getResult().getInvoice().setRegistryDocumentCountry(ai.getRegistry().getDocumentCountry());
							callback.getResult().getInvoice().setRegistryDocument(ai.getRegistry().getDocument());
							callback.getResult().getInvoice().setRegistryName(ai.getRegistry().getName());
							callback.getResult().getInvoice().setScope(new Scope().setId(ai.getRegistry().getScope()));
							callback.getResult().getInvoice().setTransaction(ai.getTransaction());
							if (callback.getResult().getTedi().getType() == TediInvoiceType.TICKET
								&& registry.getType() == AccountingRegistryType.CREDITOR ) {
								callback.getResult().getInvoice().setType(InvoiceType.UNDEDUCTIBLE);
							} else {
								callback.getResult().getInvoice().setType( ai.getInvoiceType());
							}
							callback.getResult().getAccountingInvoice().setSuggestedAccounts(ai.getSuggestedAccounts());
							callback.onAccept(callback.getResult());
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}
					});
				}
				
				@Override
				public void onCancel() {
					callback.onCancel();
				}
			});
		}
	}
	
	@Override
	public void visitRegistry(ICallback callback) {
		if ( callback.getResult().getInvoice().getIssueDate() != null) {
			showRegistryDialog(AON.MSG.titular(), new ITediCallback<AccountingRegistry>() {
				@Override
				public ICallback getCallback() {
					return callback;
				}
				@Override
				public void onAccept(AccountingRegistry registry) {
					final AccountingRegistry ar = registry;
					SERVICE.initializeInvoice(getOptions().getOccam(), ar, null,
							callback.getResult().getInvoice().getIssueDate(), new AsyncCallback<AccountingInvoice>() {
	
								@Override
								public void onSuccess(AccountingInvoice ai) {
									callback.getResult().getAccountingInvoice().setRegistry(ai.getRegistry());
									callback.getResult().getInvoice().setRegistry(ai.getRegistry().getId());
									callback.getResult().getInvoice().setRegistryDocumentType(ai.getRegistry().getDocumentType());
									callback.getResult().getInvoice().setRegistryDocumentCountry(ai.getRegistry().getDocumentCountry());
									callback.getResult().getInvoice().setRegistryDocument(ai.getRegistry().getDocument());
									callback.getResult().getInvoice().setRegistryName(ai.getRegistry().getName());
									callback.getResult().getInvoice().setScope(new Scope().setId(ai.getRegistry().getScope()));
									// ****
									callback.getResult().getInvoice().setTransaction(ai.getTransaction());
									callback.getResult().getInvoice().setSurcharge(ai.isSurcharge());
									callback.getResult().getInvoice().setWithholding(ai.isWithholding());
									callback.getResult().getInvoice().setWithholdingFarmer(ai.isWithholdingFarmer());
									callback.getResult().getInvoice().setVatAccrualPayment(ai.isVatAccrualPayment());
									// ****
									if (callback.getResult().getTedi().getType() == TediInvoiceType.TICKET
									 && registry.getType() == AccountingRegistryType.CREDITOR ) {
										callback.getResult().getInvoice().setType(InvoiceType.UNDEDUCTIBLE);
									} else {
										callback.getResult().getInvoice().setType(ai.getInvoiceType());	
									}
									callback.onAccept(callback.getResult());
								}
	
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
								}
							});
				}
	
				@Override
				public void onCancel() {
					callback.onCancel();
				}
			});
		}
	}

	@Override
	public void visitRdocument(ICallback callback) {
		showDocumentDialog(AON.MSG.document(), callback.getResult().getInvoice().getRegistryDocument(), new ITediCallback<String>() {
			@Override
			public ICallback getCallback() {
				return callback;
			}
			@Override
			public void onAccept(String document) {
				callback.getResult().getInvoice().setRegistryDocument(document);
				callback.onAccept(callback.getResult());
			}

			@Override
			public void onCancel() {
				callback.onCancel();
			}
		});
	}

	@Override
	public void visitFinanceAccountBank(ICallback callback) {
		LinkedHashSet<String> banks = new LinkedHashSet<>();
		for ( Finance finance : callback.getResult().getAccountingInvoice().getInvoice().getFinances()) {
			banks.add(finance.getBankAccount().toString());
		}
		if (AonCollectionUtils.isNotEmpty( banks )) {
			showBankAccountDialog(AON.MSG.bankAccount(), banks.iterator().next() , new ITediCallback<String>() {
				
				@Override
				public void onCancel() {
					callback.onCancel();
				}
				
				@Override
				public void onAccept(String t) {
					for ( Finance finance : callback.getResult().getAccountingInvoice().getInvoice().getFinances()) {
						BankAccount bankAccount = new BankAccount( t ); 
						finance.setBankAccount(bankAccount);
					}
					callback.onAccept(callback.getResult());
				}
				
				@Override
				public ICallback getCallback() {
					return callback;
				}
			});
		}
	}

	private void noVisit() {
		// Nothing
	}
	
	@Override public void visitType(ICallback callback) {noVisit();}
	@Override public void visitTransaction(ICallback callback) {noVisit();}
	@Override public void visitSeries(ICallback callback) {noVisit();}
	@Override public void visitDuplicatedSeriesNumber(ICallback callback) {noVisit();}
	@Override public void visitDuplicatedReferenceCode(ICallback callback) {noVisit();}
	@Override public void visitScope(ICallback callback) {noVisit();}
	@Override public void visitRname(ICallback callback) {noVisit();}
	@Override public void visitReferenceCode(ICallback callback) {noVisit();}
	@Override public void visitRdocumentCountry(ICallback callback) {noVisit();}
	@Override public void visitNumber(ICallback callback) {noVisit();}
	@Override public void visitDomain(ICallback callback) {noVisit();}
	@Override public void visitWorkplace(ICallback callback) {noVisit();}
	@Override public void visitBasesQuotas(ICallback callback) {noVisit();}
	@Override public void visitDetailDescription(ICallback callback) {noVisit();}
	@Override public void visitDetails(ICallback callback) {noVisit();}
	@Override public void visitAccountEntry(ICallback callback) {noVisit();}
	@Override public void visitAddress(ICallback callback) {noVisit();}
	@Override public void visitFinanceWrongDate(ICallback callback) {noVisit();}
	@Override public void visitFinanceAmountZero(ICallback callback) {noVisit();}
	@Override public void visitFinanceTotalAmount(ICallback callback) {noVisit();}
	@Override public void visitTotal(ICallback callback) {noVisit();}
	@Override public void visitTaxRate(ICallback callback ) {noVisit();}
	@Override public void visitTaxBase(ICallback callback ) {noVisit();}
	@Override public void visitTaxQuota(ICallback callback ) {noVisit();}
	@Override public void visitIrpfRate(ICallback callback ){noVisit();}
	@Override public void visitPayMethod(ICallback callback){noVisit();}
	@Override public void visitIrpfQuota(ICallback callback ){noVisit();}
	@Override public void visitInvestment(ICallback callback ){noVisit();}
	@Override public void visitSurcharge(ICallback callback ){noVisit();}
	@Override public void visitGeneric(ICallback callback ){noVisit();}
	@Override public void visitWithholding(ICallback callback ){noVisit();}
	@Override public void visitExpenseAccount(ICallback callback ){noVisit();}
	@Override public void visitCommunication(ICallback t) {noVisit();}
	@Override public void visitOCR(ICallback t) {noVisit();}

	//--------------------------------------------------------------------------- 
	// ---------------------------------------------------------------- [PRIVATE]
	//--------------------------------------------------------------------------- 
	
	private void showDateDialog(String label, Date date, ITediCallback<Date> callback) {
		final AonDateBox dateBox = new AonDateBox();
		dateBox.setValue(date);
		dateBox.addValueChangeHandler(event -> callback.onAccept(event.getValue()));
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, dateBox);
		container.add(dialog);
	}

	private void showDocumentDialog(String label, String document, ITediCallback<String> callback) {
		final TextBox documentBox = new TextBox();
		documentBox.setStyleName(AON.CSS.aonInputText());
		documentBox.setValue(document);
		documentBox.addValueChangeHandler(event -> callback.onAccept(event.getValue()));
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, documentBox);
		container.add(dialog);
	}

	private void showRegistryDialog(String label, ITediCallback<AccountingRegistry> callback) {
		final AonAccountingRegistryBox registryBox = new AonAccountingRegistryBox(getOptions(), false, false);
		registryBox.addSelectionHandler(event -> callback.onAccept(event.getSelectedItem()));
		FlowPanel registryNewContainer = new FlowPanel();
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, registryBox);
		if (callback.getCallback().getResult().getInvoice().isExpenses() 
			|| callback.getCallback().getResult().getInvoice().isUndeductible() ) {
			AccountingRegistry dc = callback.getCallback().getConfiguration().getDefaultCreditor();
			if (dc != null) {
				Button defaultCreditor = new Button("Asignar a " + dc.getName() + " (" + dc.getAccountCode() + ")");
				defaultCreditor.setStyleName(AON.CSS.aonTabButton());
				defaultCreditor.addStyleName(AON.CSS.aonIconRight());
				defaultCreditor.addStyleName(AON.CSS.aonWidthAutoImportant());
				defaultCreditor.addClickHandler( event -> callback.onAccept(dc));
				dialog.setContent("", defaultCreditor);		
			}
		}
		if (callback.getCallback().getResult().getInvoice().getRegistry() == null) {
			TediRegistry reg = callback.getCallback().getResult().getTedi().getRegistry();
			String d = Optional.ofNullable( reg ).map( r -> AonStringUtils.defaultString(reg.getDocument())).orElse(null);
			String n = Optional.ofNullable( reg ).map( r -> AonStringUtils.defaultString(reg.getName())).orElse(null);
			InvoiceType invoiceType = callback.getCallback().getResult().getInvoice().getType();
			StringBuilder buf = new StringBuilder("Crear el ");
			AccountingRegistryType tempType = null; 
			if (invoiceType == null) {
				buf.append("titular");
			} else {
				tempType = invoiceType.visit(null, new IInvoiceTypeVisitor<AccountingRegistryType>() {
					
					@Override
					public AccountingRegistryType visitPurchase(Invoice invoice) {
						buf.append("proveedor");
						return AccountingRegistryType.SUPPLIER;
					}
					
					@Override
					public AccountingRegistryType visitSales(Invoice invoice) {
						buf.append("cliente");
						return AccountingRegistryType.CUSTOMER;
					}
					
					@Override
					public AccountingRegistryType visitExpenses(Invoice invoice) {
						buf.append("acreedor");
						return AccountingRegistryType.CREDITOR;
					}
					
					@Override
					public AccountingRegistryType visitUndeductible(Invoice invoice) {
						return visitExpenses(invoice);
					}
				});
			}
			buf.append(" - ")
				.append(AonStringUtils.defaultString(d))
				.append(" ")
				.append(AonStringUtils.defaultString(n));
			AccountingRegistryType type = tempType;
			Button newRegistry = new Button(buf.toString());
			newRegistry.setStyleName(AON.CSS.aonTabButton());
			newRegistry.addStyleName(AON.CSS.aonWidthAutoImportant());
			newRegistry.addStyleName(AON.CSS.aonIconAdd());
			newRegistry.addClickHandler( event -> {
				newRegistry.setEnabled( false );
				newRegistry.setVisible( false );
				AccountingRegistry ar = new AccountingRegistry();
				ar.setType(type);
				TediInvoice tedi = callback.getCallback().getResult().getTedi();
				TediRegistry tr = tedi.getRegistry();
				if (tr != null) {
					ar.setDocument(tr.getDocument());
					ar.setDocumentCountry(Country.safeValueOf(tr.getDocumentCountry()));
					ar.setName(tr.getName());
					TediAddress ad = tr.getAddress();
					if (ad != null) {
						ar.setAddress(ad.getAddress());
						ar.setAddressTown(ad.getCity());
						ar.setAddressZIP(ad.getPostalCode());
					}
					
				}
				AonAccountingRegistryPanel registryNewPanel = registryBox.getAonAccountingRegistryPanel(getOptions(),ar
					, new AonAccountingRegistryPanelCallback() {
						
						@Override public void setFocus(boolean b) { /*Nothing*/ }
						
						@Override
						public void onCancel() {
							registryNewContainer.clear();
							newRegistry.setEnabled( true );
							newRegistry.setVisible( true );
						}
						
						@Override
						public void onAccept(AccountingRegistry registry) {
							newRegistry.setEnabled( true );
							newRegistry.setVisible( true );
						}
					}
				);
				registryNewContainer.add(registryNewPanel);
			});
			dialog.setContent("", newRegistry);		
		}
		container.add(dialog);
		container.add(registryNewContainer);
	}

	private void showAmbiguousRegistryDialog(String label, ITediCallback<AccountingRegistry> callback) {
		ListBox registryBox = new ListBox();
		registryBox.addItem("<Selecciones un valor>", (String) null);
		if (callback.getCallback().getResult().getAccountingInvoice().getPosibleRegistries() != null) {
			int index = 0;
			for (AccountingRegistry ar : callback.getCallback().getResult().getAccountingInvoice().getPosibleRegistries() ) {
				registryBox.addItem("[" + ar.getType().getDescription().substring(0, 3)+"] " + ar.getDocument()  + " - " + ar.getName() + " (" + ar.getAccountCode() + ")", AonNumberUtils.toString(index));
				index++;
			}
		}
		registryBox.addChangeHandler(event -> {
			Integer index = AonNumberUtils.toInteger( registryBox.getSelectedValue() );
			AccountingRegistry selected = callback.getCallback().getResult().getAccountingInvoice().getPosibleRegistries().get(index);
			callback.onAccept(selected);
		});
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, registryBox);
		container.add(dialog);
	}

	private void showBankAccountDialog(String label, String referenceCode, ITediCallback<String> callback) {
		final TextBox referenceBox = new TextBox();
		referenceBox.setStyleName(AON.CSS.aonInputText());
		referenceBox.setValue(referenceCode);
		referenceBox.addValueChangeHandler(event -> callback.onAccept(event.getValue()));
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, referenceBox);
		container.add(dialog);
	}

	private static class BasicDialog extends FlowPanel {
		private AonDisplayTable container = new AonDisplayTable();

		public BasicDialog() {
			super();
			setStyleName(AON.CSS.aonPadding());
			addStyleName(AON.CSS.aonWidthAlmostAll());
			addStyleName(AON.CSS.aonBorder());
			
			container.addStyleName(AON.CSS.aonBlockCenter());
			container.addStyleName(AON.CSS.aonWidthAlmostAll());
			
			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(AON.CSS.aonTextCenter());
			buttons.addStyleName(AON.CSS.aonMarginTop());
			add(container);
		}

		public void setContent(String label, Widget child) {
			container.addRow()
				.addCell( new Label(label), AON.CSS.aonTableLabel(), AON.CSS.aonWidth150(), AON.CSS.aonTextRight())
				.addCell( child );
		}
	}

}
