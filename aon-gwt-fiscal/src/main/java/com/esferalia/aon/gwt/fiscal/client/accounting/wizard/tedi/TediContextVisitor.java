package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountingRegistryPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.ITediCallback;
import com.esferalia.aon.occam.api.model.tedi.ITediContextVisitor;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediRegistry;

public class TediContextVisitor implements ITediContextVisitor {

	private static final Logger LOGGER = Logger.getLogger(EditableInvoicePanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static AccountEntryServiceAsync SERVICE;
	private AccountEntryModuleOptions options;
	private SimplePanel container;

	
	public TediContextVisitor(AccountEntryModuleOptions moduleOptions, SimplePanel container) {
		this.options = moduleOptions;
		this.container = container;
		AccountEntryServiceAsync serviceRaw = GWT.create(AccountEntryService.class);
		SERVICE = new AccountEntryServiceAsyncDecorator(serviceRaw);
	}

	private AccountEntryModuleOptions getOptions() {
		return this.options;
	}

	public SimplePanel getContainer() {
		return container;
	}

	private void noVisit() {
		// MessageDialog.show("No hay ninguna utilidad para corregir el aviso/error.");
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
	public void visitType(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitTransaction(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitSeries(ICallback callback) {
		noVisit();
	}
	
	@Override
	public void visitDuplicatedSeriesNumber(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitDuplicatedReferenceCode(ICallback callback) {
		noVisit();
	}
	
	@Override
	public void visitScope(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitRname(ICallback callback) {
		noVisit();
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
							if (callback.getResult().getTedi().getType() == TediInvoiceType.TICKET) {
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
									if (callback.getResult().getTedi().getType() == TediInvoiceType.TICKET) {
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
	public void visitReferenceCode(ICallback callback) {
		noVisit();
//		showReferenceCodeDialog(AON.MSG.invoiceNumber(), callback.getResult().getInvoice().getReferenceCode(),
//				new ITediCallback<String>() {
//					@Override
//					public ICallback getCallback() {
//						return callback;
//					}
//					@Override
//					public void onAccept(String referenceCode) {
//						callback.getResult().getInvoice().setReferenceCode(referenceCode);
//						callback.onAccept(callback.getResult());
//					}
//
//					@Override
//					public void onCancel() {
//						callback.onCancel();
//					}
//				});
	}

	@Override
	public void visitRdocumentCountry(ICallback callback) {
		noVisit();
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
	public void visitNumber(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitDomain(ICallback callback) {
		noVisit();
	}
	
	@Override
	public void visitWorkplace(ICallback callback) {
		noVisit();
	}
	@Override
	public void visitBasesQuotas(ICallback callback) {
		noVisit();
	}
	@Override
	public void visitDetailDescription(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitDetails(ICallback callback) {
		noVisit();
	}
	@Override
	public void visitAccountEntry(ICallback callback) {
		noVisit();
	}
	
	@Override
	public void visitAddress(ICallback callback) {
		noVisit();
	}
	
	@Override
	public void visitFinanceWrongDate(ICallback callback) {
		noVisit();
	}
	
	@Override
	public void visitFinanceAccountBank(ICallback callback) {
		LinkedHashSet<String> banks = new LinkedHashSet<String>();
		for ( Finance finance : callback.getResult().getAccountingInvoice().getInvoice().getFinances()) {
			banks.add(finance.getBankAccount().toString());
		}
		if (banks.size() > 0) {
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
	
	@Override
	public void visitFinanceAmountZero(ICallback callback) {
		noVisit();
	}
	
	@Override
	public void visitTotal(ICallback callback) {
	}
	
	@Override
        public void visitTaxRate(ICallback callback ) {
	}
        
	@Override
	public void visitTaxBase(ICallback callback ) {
	}
        
	@Override
	public void visitTaxQuota(ICallback callback ) {
	}

	@Override
	public void visitIrpfRate(ICallback callback ){
	}
	
	@Override
	public void visitPayMethod(ICallback callback){
	}
	@Override
	public void visitIrpfQuota(ICallback callback ){
	}

	private void showDateDialog(String label, Date date, ITediCallback<Date> callback) {
		final AonDateBox dateBox = new AonDateBox();
		dateBox.setValue(date);
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				callback.onAccept(event.getValue());
			}
		});
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, dateBox);
		container.add(dialog);
	}

	private void showDocumentDialog(String label, String document, ITediCallback<String> callback) {
		final TextBox documentBox = new TextBox();
		documentBox.setStyleName(AON.CSS.aonInputText());
		documentBox.setValue(document);
		documentBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.onAccept(event.getValue());
			}
		});
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, documentBox);
		container.add(dialog);
	}

	private void showRegistryDialog(String label, ITediCallback<AccountingRegistry> callback) {
		final AonAccountingRegistryBox registryBox = new AonAccountingRegistryBox(getOptions(), false);
		registryBox.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> event) {
				callback.onAccept(event.getSelectedItem());
			}
		});
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
				defaultCreditor.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						callback.onAccept(dc);
					}
				});
				dialog.setContent("", defaultCreditor);		
			}
		}
		if (callback.getCallback().getResult().getInvoice().getRegistry() == null
//		  && AonStringUtils.isNotBlank( reg.getDocument() )
//		  && AonStringUtils.isNotBlank( reg.getName() ) 
		  ) {
			TediRegistry reg = callback.getCallback().getResult().getTedi().getRegistry();
			String d = reg==null?null:AonStringUtils.defaultString( reg.getDocument());
			String n = reg==null?null:AonStringUtils.defaultString(reg.getName());
			String t = "";
			AccountingRegistryType ty = null;
			if (callback.getCallback().getResult().getInvoice().isExpenses() || callback.getCallback().getResult().getInvoice().isUndeductible()) {
				t = "acreedor";
				ty = AccountingRegistryType.CREDITOR;
			} else if (callback.getCallback().getResult().getInvoice().isPurchase()) {
				t = "proveedor";
				ty = AccountingRegistryType.SUPPLIER;
			} else if (callback.getCallback().getResult().getInvoice().isSales()) {
				t = "cliente";
				ty = AccountingRegistryType.CUSTOMER;
			} else {
				t = "titular";
			}
			final AccountingRegistryType type = ty;
			
			Button newCreditor = new Button("Crear el " + t + AonStringUtils.defaultString(d) + " " + AonStringUtils.defaultString(n));
			newCreditor.setStyleName(AON.CSS.aonTabButton());
			newCreditor.addStyleName(AON.CSS.aonWidthAutoImportant());
			newCreditor.addStyleName(AON.CSS.aonIconAdd());
			newCreditor.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
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
//							ad.getCountry()
//							ad.getProvince()
							ar.setAddressZIP(ad.getPostalCode());
						}
						
					}
					// TODO Inicializar los datos del registry.
//					registryBox.showDialog(getOptions(),ar);
					AonAccountingRegistryPanel regitryNewPanel = registryBox.getAonAccountingRegistryPanel(getOptions(),ar);
					dialog.setContent("", regitryNewPanel);
				}
			});
			dialog.setContent("", newCreditor);		
		}
		container.add(dialog);
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
				
		registryBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Integer index = AonNumberUtils.toInteger( registryBox.getSelectedValue() );
				AccountingRegistry selected = callback.getCallback().getResult().getAccountingInvoice().getPosibleRegistries().get(index);
				callback.onAccept(selected);
			}
		});
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, registryBox);
		container.add(dialog);
	}

	private void showBankAccountDialog(String label, String referenceCode, ITediCallback<String> callback) {
		final TextBox referenceBox = new TextBox();
		referenceBox.setStyleName(AON.CSS.aonInputText());
		referenceBox.setValue(referenceCode);
		referenceBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.onAccept(event.getValue());
			}
		});
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, referenceBox);
		container.add(dialog);
	}

	private static class BasicDialog extends FlowPanel {
		private FlexTable container = new FlexTable();

		public BasicDialog() {
			super();
			setStyleName(AON.CSS.aonPadding());
			addStyleName(AON.CSS.aonWidthAlmostAll());
			addStyleName(AON.CSS.aonBorder());
			
			container.setStyleName(AON.CSS.aonBlockCenter());
			container.addStyleName(AON.CSS.aonTable());
			container.addStyleName(AON.CSS.aonWidthAlmostAll());
			container.getColumnFormatter().setWidth(0, "100px");
			container.getColumnFormatter().setWidth(1, "auto");
			FlowPanel panel = new FlowPanel();
			panel.add(container);
			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(AON.CSS.aonTextCenter());
			buttons.addStyleName(AON.CSS.aonMarginTop());
			add(container);
		}

		public void setContent(String label, IsWidget child) {
			int row = container.getRowCount();
			container.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			container.getCellFormatter().addStyleName(row, 0, AON.CSS.aonTextRight());
			container.setWidget(row, 0, new Label(label));

			container.setWidget(row, 1, child);
		}
	}

}
