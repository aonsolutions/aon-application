package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

	private static FiscalServiceAsync FISCAL_SERVICE;
	private String currentDomainName;
	private int currentDomain;
	private AonConfiguration configuration;
	private SimplePanel container;

	public TediContextVisitor(String domainName, int domain, AonConfiguration configuration, SimplePanel container) {
		this.currentDomainName = domainName;
		this.currentDomain = domain;
		this.configuration = configuration;
		this.container = container;
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
	}

	private String getCurrentDomainName() {
		return currentDomainName;
	}

	private int getCurrentDomain() {
		return currentDomain;
	}

	public SimplePanel getContainer() {
		return container;
	}

	private void noVisit() {
		MessageDialog.show("No hay ninguna utilidad para corregir el aviso/error.");
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
				callback.getResult().getAccountingInvoice().getAccountEntry().setEntryDate(date);
				callback.getResult().getInvoice().setIssueDate(date);
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
	public void visitScope(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitRname(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitAmbiguousRegistry(ICallback callback) {
		showAmbiguousRegistryDialog(AON.MSG.titular(), new ITediCallback<AccountingRegistry>() {
			@Override
			public ICallback getCallback() {
				return callback;
			}
			@Override
			public void onAccept(AccountingRegistry registry) {
				final AccountingRegistry ar = registry;
				FISCAL_SERVICE.initializeInvoice(getCurrentDomainName(), getCurrentDomain(), ar, null,
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
								if (callback.getResult().getTedi().getType() == TediInvoiceType.TICKET) {
									callback.getResult().getInvoice().setType(InvoiceType.UNDEDUCTIBLE);
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
	
	@Override
	public void visitRegistry(ICallback callback) {
		showRegistryDialog(AON.MSG.titular(), new ITediCallback<AccountingRegistry>() {
			@Override
			public ICallback getCallback() {
				return callback;
			}
			@Override
			public void onAccept(AccountingRegistry registry) {
				final AccountingRegistry ar = registry;
				FISCAL_SERVICE.initializeInvoice(getCurrentDomainName(), getCurrentDomain(), ar, null,
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
								if (callback.getResult().getTedi().getType() == TediInvoiceType.TICKET) {
									callback.getResult().getInvoice().setType(InvoiceType.UNDEDUCTIBLE);
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

	@Override
	public void visitReferenceCode(ICallback callback) {
		showReferenceCodeDialog(AON.MSG.invoiceNumber(), callback.getResult().getInvoice().getReferenceCode(),
				new ITediCallback<String>() {
					@Override
					public ICallback getCallback() {
						return callback;
					}
					@Override
					public void onAccept(String referenceCode) {
						callback.getResult().getInvoice().setReferenceCode(referenceCode);
						callback.onAccept(callback.getResult());
					}

					@Override
					public void onCancel() {
						callback.onCancel();
					}
				});
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
	public void visitDetailDescription(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitDetails(ICallback callback) {
		noVisit();
	}

	@Override
	public void visitAddress(ICallback callback) {
		noVisit();
	}

	private void showDateDialog(String label, Date date, ITediCallback<Date> callback) {
		final DateBoxEx dateBox = new DateBoxEx();
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
		documentBox.setStyleName(AON.AON_CSS.aonInputText());
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
		final AccountingRegistryBox registryBox = new AccountingRegistryBox(getCurrentDomainName(), getCurrentDomain(),
				configuration, false);
		registryBox.addSelectionHandler(new SelectionHandler<AccountingRegistry>() {
			@Override
			public void onSelection(SelectionEvent<AccountingRegistry> event) {
				callback.onAccept(event.getSelectedItem());
			}
		});
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, registryBox);
		if (callback.getCallback().getResult().getInvoice().isExpenses()) {
			AccountingRegistry dc = callback.getCallback().getConfiguration().getDefaultCreditor();
			if (dc != null) {
				Label defaultCreditor = new Label("Asignar a " + dc.getName() + " (" + dc.getAccountCode() + ")");
				defaultCreditor.setStyleName(AON.AON_CSS.aonClickableLabel());
				defaultCreditor.addStyleName(AON.AON_CSS.aonIconRowSelector());
				defaultCreditor.addStyleName(AON.AON_CSS.aonPaddingLeft20());
				defaultCreditor.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						callback.onAccept(dc);
					}
				});
				dialog.setContent("", defaultCreditor);		
			}
		}
		if (AonStringUtils.isNotBlank( callback.getCallback().getResult().getTedi().getRdocument() )
		 && AonStringUtils.isNotBlank( callback.getCallback().getResult().getTedi().getRname() ) ) {
			String d = AonStringUtils.defaultString( callback.getCallback().getResult().getTedi().getRdocument());
			String n = AonStringUtils.defaultString(callback.getCallback().getResult().getTedi().getRname());
			Label newCreditor = new Label("Crear el acreedor (" + d + " " + n + ")");
			newCreditor.setStyleName(AON.AON_CSS.aonClickableLabel());
			newCreditor.addStyleName(AON.AON_CSS.aonIconReset());
			newCreditor.addStyleName(AON.AON_CSS.aonPaddingLeft20());
			newCreditor.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					AccountingRegistry ar = new AccountingRegistry();
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
					registryBox.showDialog(getCurrentDomainName(), getCurrentDomain(),configuration,ar);
				}
			});
			dialog.setContent("", newCreditor);		
		}
		container.add(dialog);
	}

	private void showAmbiguousRegistryDialog(String label, ITediCallback<AccountingRegistry> callback) {
		
		ListBox registryBox = new ListBox();
		registryBox.addItem("<Selecciones un valor>", (String) null);
		if (callback.getCallback().getResult().getPosibleRegistries() != null) {
			int index = 0;
			for (AccountingRegistry ar : callback.getCallback().getResult().getPosibleRegistries() ) {
				registryBox.addItem(ar.getDocument()  + " - " + ar.getName() + " (" + ar.getAccountCode() + ")", AonNumberUtils.toString(index));		
			}
		}
				
		registryBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Integer index = AonNumberUtils.toInteger( registryBox.getSelectedValue() );
				callback.onAccept(callback.getCallback().getResult().getPosibleRegistries().get(index));
			}
		});
		BasicDialog dialog = new BasicDialog();
		dialog.setContent(label, registryBox);
		container.add(dialog);
	}

	private void showReferenceCodeDialog(String label, String referenceCode, ITediCallback<String> callback) {
		final TextBox referenceBox = new TextBox();
		referenceBox.setStyleName(AON.AON_CSS.aonInputText());
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
			container.setStyleName(AON.AON_CSS.aonBlockCenter());
			container.addStyleName(AON.AON_CSS.aonPanelGrid());
			container.addStyleName(AON.AON_CSS.aonWidth90Percent());
			container.getColumnFormatter().setWidth(0, "100px");
			container.getColumnFormatter().setWidth(1, "auto");
			FlowPanel panel = new FlowPanel();
			panel.add(container);
			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(AON.AON_CSS.aonTextCenter());
			buttons.addStyleName(AON.AON_CSS.aonMarginTop());
			add(container);
		}

		public void setContent(String label, IsWidget child) {
			int row = container.getRowCount();
			container.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			container.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextLeft());
			container.setWidget(row, 0, new Label(label));

			container.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			container.setWidget(row, 1, child);
		}
	}
}
