package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountingRegistryBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
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
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import es.translogia.tedi.ewok.TediInvoiceType;

public class TediContextVisitor implements ITediContextVisitor {
	
	private static FiscalServiceAsync FISCAL_SERVICE;
	private String currentDomainName;
	private int currentDomain;
	private AonConfiguration configuration;
	
	public TediContextVisitor(String domainName, int domain,AonConfiguration configuration) {
		this.currentDomainName = domainName;
		this.currentDomain = domain;
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
	}
	
	private String getCurrentDomainName() {
		return currentDomainName;
	}
	private int getCurrentDomain() {
		return currentDomain;
	}
	
	private void noVisit(TediResult result) {
		MessageDialog.show("No hay ninguna utilidad para corregir el aviso/error.");
	}

	@Override
	public void visitIssueDate(TediResult result, ICallback callback) {
		showDateDialog(AON.MSG.issueDate(), result.getInvoice().getIssueDate(), new ITediCallback<Date>() {

			@Override
			public void onAccept(Date date) {
				result.getInvoice().setIssueDate(date);
				callback.onAccept(result);
			}

			@Override
			public void onCancel() {
				callback.onCancel();
			}
		});
	}

	@Override
	public void visitTaxDate(TediResult result, ICallback callback) {
		showDateDialog(AON.MSG.issueDate(), result.getInvoice().getTaxDate(), new ITediCallback<Date>() {

			@Override
			public void onAccept(Date date) {
				result.getInvoice().setTaxDate(date);
				callback.onAccept(result);
			}

			@Override
			public void onCancel() {
				callback.onCancel();
			}
		});
	}

	@Override
	public void visitType(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitTransaction(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitSeries(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitScope(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitRname(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitRegistry(TediResult result, ICallback callback) {
		showRegistryDialog(AON.MSG.titular(), 
				new ITediCallback<AccountingRegistry>() {

					@Override
					public void onAccept(AccountingRegistry registry) {
						final AccountingRegistry ar = registry;
							FISCAL_SERVICE.initializeInvoice(
										getCurrentDomainName()
										,getCurrentDomain()
										,ar
										,null
										,result.getTedi().getDate()
										,new AsyncCallback<AccountingInvoice>() {
											
											@Override
											public void onSuccess(AccountingInvoice ai) {
												result.getAccountingInvoice().setRegistry(ai.getRegistry());
												result.getInvoice().setRegistry(ai.getRegistry().getId());
												result.getInvoice().setRegistryDocumentType(ai.getRegistry().getDocumentType());
												result.getInvoice().setRegistryDocumentCountry(ai.getRegistry().getDocumentCountry());
												result.getInvoice().setRegistryDocument(ai.getRegistry().getDocument());
												result.getInvoice().setRegistryName(ai.getRegistry().getName());
												result.getInvoice().setScope(new Scope().setId(ai.getRegistry().getScope()));
												if (result.getTedi().getType() == TediInvoiceType.TICKET) {
													result.getInvoice().setType( InvoiceType.UNDEDUCTIBLE );	
												}
												callback.onAccept(result);
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
	public void visitReferenceCode(TediResult result, ICallback callback) {
		showReferenceCodeDialog(AON.MSG.invoiceNumber(), result.getInvoice().getReferenceCode(),
				new ITediCallback<String>() {

					@Override
					public void onAccept(String referenceCode) {
						result.getInvoice().setReferenceCode(referenceCode);
						callback.onAccept(result);
					}

					@Override
					public void onCancel() {
						callback.onCancel();
					}
				});
	}

	@Override
	public void visitRdocumentCountry(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitRdocument(TediResult result, ICallback callback) {
		showDocumentDialog(AON.MSG.document(), result.getInvoice().getRegistryDocument(),
				new ITediCallback<String>() {

					@Override
					public void onAccept(String document) {
						result.getInvoice().setRegistryDocument(document);
						callback.onAccept(result);
					}

					@Override
					public void onCancel() {
						callback.onCancel();
					}
				});
	}

	@Override
	public void visitNumber(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitDomain(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitDetailDescription(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitDetails(TediResult result, ICallback callback) {
		noVisit(result);
	}

	@Override
	public void visitAddress(TediResult result, ICallback callback) {
		noVisit(result);
	}

	private void showDateDialog(String label, Date date, ITediCallback<Date> callback) {
	final DateBoxEx dateBox = new DateBoxEx();
	dateBox.setValue(date);
	BasicDialog<Date> dialog = new BasicDialog<Date>(callback) {

		@Override
		protected Date getValue() {
			return dateBox.getValue();
		}

	};
	dialog.setContent(label, dateBox);
	dialog.centerShow();
}

private void showDocumentDialog(String label, String document, ITediCallback<String> callback) {
	final TextBox documentBox = new TextBox();
	documentBox.setStyleName(AON.AON_CSS.aonInputText());
	documentBox.setValue(document);
	BasicDialog<String> dialog = new BasicDialog<String>(callback) {

		@Override
		protected String getValue() {
			return documentBox.getValue();
		}

	};
	dialog.setContent(label, documentBox);
	dialog.centerShow();
}

private void showRegistryDialog(String label, ITediCallback<AccountingRegistry> callback) {
	final AccountingRegistryBox registryBox = new AccountingRegistryBox(getCurrentDomainName(),getCurrentDomain(),configuration,false);
	BasicDialog<AccountingRegistry> dialog = new BasicDialog<AccountingRegistry>(callback) {
		private AccountingRegistry ar;

		@Override
		protected void afterConstruct() {
			registryBox.addSelectionHandler( new SelectionHandler<AccountingRegistry>() {
				
				@Override
				public void onSelection(SelectionEvent<AccountingRegistry> event) {
					ar = event.getSelectedItem();
				}
			});
		}

		@Override
		protected AccountingRegistry getValue() {
			return ar;
		}

	};
	dialog.setContent(label, registryBox);
	dialog.centerShow();
}

private void showReferenceCodeDialog(String label, String referenceCode, ITediCallback<String> callback) {
	final TextBox documentBox = new TextBox();
	documentBox.setStyleName(AON.AON_CSS.aonInputText());
	documentBox.setValue(referenceCode);
	BasicDialog<String> dialog = new BasicDialog<String>(callback) {

		@Override
		protected String getValue() {
			return documentBox.getValue();
		}

	};
	dialog.setContent(label, documentBox);
	dialog.centerShow();
}

private static abstract class BasicDialog<T> extends CustomDialog {
	private FlexTable container = new FlexTable();

	public BasicDialog(ITediCallback<T> callback) {

		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.inputData());
		setWidth("500px");
		setHeight("120px");

		container.setStyleName(AON.AON_CSS.aonBlockCenter());
		container.addStyleName(AON.AON_CSS.aonPanelGrid());
		container.addStyleName(AON.AON_CSS.aonWidth90Percent());
		container.getColumnFormatter().setWidth(0, "100px");
		container.getColumnFormatter().setWidth(1, "auto");

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());

		FlowPanel panel = new FlowPanel();

		panel.add(container);

		FlowPanel buttons = new FlowPanel();
		buttons.setStyleName(AON.AON_CSS.aonTextCenter());
		buttons.addStyleName(AON.AON_CSS.aonMarginTop());

		final Button okButton = new Button();
		okButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		okButton.setText(AON.MSG.accept());
		okButton.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
					callback.onCancel();
				}
			}
		});
		okButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				hide();
				callback.onAccept(getValue());
			}
		});
		buttons.add(okButton);

		final Button cancelButton = new Button();
		cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
		cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		cancelButton.setText(AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				hide();
				callback.onCancel();
			}
		});
		cancelButton.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
					callback.onCancel();
				}
			}
		});
		addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				callback.onCancel();
			}
		});
		buttons.add(cancelButton);
		panel.add(buttons);
		scrollPanel.setWidget(panel);
		setWidget(scrollPanel);
		afterConstruct();
	}

	protected void afterConstruct() {
	}

	public void setContent(String label, IsWidget child) {
		int row = container.getRowCount();
		container.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		container.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextLeft());
		container.setWidget(row, 0, new Label(label));

		container.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		container.setWidget(row, 1, child);
	}

	public void centerShow() {
		center();
		show();
	}

	protected abstract T getValue();
}
}
