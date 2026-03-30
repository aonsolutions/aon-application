package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TediProblems extends ScrollPanel {
	private static final AccountEntryServiceAsync SERVICE;
	static {
		AccountEntryServiceAsync serviceRaw = GWT.create(AccountEntryService.class);
		SERVICE = new AccountEntryServiceAsyncDecorator(serviceRaw);
	}

	public interface ITediProblemsCallback  extends IAccountEntryModuleCallback {
		public TediResult getResult();
		public void onChanged(TediResult result);
		public void onError(Throwable caught);
	}

	public TediProblems( ITediProblemsCallback callback) {
		setStyleName(AON.CSS.aonScrollArea());
		FlowPanel mainPanel = new FlowPanel();
		setWidget(mainPanel);
		if (callback.getResult().getAccountingInvoice().hasMessages()) {
			mainPanel.setStyleName(AON.CSS.aonMarginTopSep());
			mainPanel.addStyleName(AON.CSS.aonMarginLeft());
			mainPanel.addStyleName(AON.CSS.aonFixedFont());
			callback.getResult().getAccountingInvoice().messageStream()
				.forEach( error -> {
					FlowPanel flowPanel = new FlowPanel();
					InlineLabel colorLabel = new InlineLabel("");
					colorLabel.setStyleName(AON.CSS.aonPaddingLeft());
					colorLabel.addStyleName(AON.CSS.aonPaddingRight());
					colorLabel.getElement().getStyle().setBackgroundColor(getBackgroundColor(error.getLevel()));
					flowPanel.add(colorLabel);
	
					InlineLabel errLabel = new InlineLabel(error.getLevel().getLabel());
					errLabel.setStyleName(AON.CSS.aonPaddingLeft());
					errLabel.addStyleName(AON.CSS.aonPaddingRight());
					errLabel.addStyleName(AON.CSS.aonBold());
					flowPanel.add(errLabel);
	
					InlineLabel msgLabel = new InlineLabel(error.getMessage());
					msgLabel.setStyleName(AON.CSS.aonMarginLeft());
					flowPanel.add(msgLabel);
	
					if (error.canBeFixed()) {
						FlowPanel container = new FlowPanel();
						container.setStyleName(AON.CSS.aonMarginTop());
						container.addStyleName(AON.CSS.aonMarginBottom());
						flowPanel.add(container);
						TediContextVisitor tediContextVisitor = new TediContextVisitor(callback.getModuleOptions(), container);
						error.getContext().getKey().visit(tediContextVisitor, new ICallback() {
	
							@Override
							public TediResult getResult() {
								return callback.getResult();
							}
	
							@Override
							public AonConfiguration getConfiguration() {
								return callback.getConfiguration();
							}
	
							@Override
							public void onCancel() {
								// Nothing
							}
	
							@Override
							public void onAccept(TediResult result) {
								AccountEntry ae = InvoiceRecorder.getInvoiceEntry(result.getAccountingInvoice());
								result.getAccountingInvoice().setAccountEntry(ae);
								SERVICE.validateInvoice(
									callback.getOccam().getDomainName()
									,callback.getOccam().getUser()
									,callback.getOccam().getDomain()
									,result
									,new AsyncCallback<TediResult>() {
										
										@Override
										public void onSuccess(TediResult result) {
											callback.onChanged(result);
										}
										
										@Override
										public void onFailure(Throwable caught) {
											callback.onError(caught);
										}
									});
							}
						});
					}
					mainPanel.add(flowPanel);
				}
			);
		}
	}

	private String getBackgroundColor(InvoiceErrorLevel curLevel) {
		String color = null;
		if (curLevel == null) {
			color = "#c1f9ba";
		} else if (curLevel == InvoiceErrorLevel.INF) {
			color = "RoyalBlue";
		} else if (curLevel == InvoiceErrorLevel.WRN) {
			color = "#ffa54f"; 
		} else if (curLevel == InvoiceErrorLevel.ERR) {
			color = "red";
		}
		return color;
	}

}
