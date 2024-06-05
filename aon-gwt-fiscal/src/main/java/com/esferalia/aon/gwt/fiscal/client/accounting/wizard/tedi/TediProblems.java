package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediService;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TediProblems extends ScrollPanel {
	private static TediServiceAsync TEDI_SERVICE;

	public interface ITediProblemsCallback  extends IAccountEntryModuleCallback {
		public TediResult getResult();
		public void onChanged(TediResult result);
		public void onError(Throwable caught);
	}

	public TediProblems( ITediProblemsCallback callback) {
		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		TEDI_SERVICE = new TediServiceAsyncDecorator(serviceRaw);
		setStyleName(AON.CSS.aonScrollArea());
		FlowPanel mainPanel = new FlowPanel();
		setWidget(mainPanel);
		if (callback.getResult().getAccountingInvoice().getMessages() != null && callback.getResult().getAccountingInvoice().getMessages().size() > 0) {
			mainPanel.setStyleName(AON.CSS.aonMarginTopSep());
			mainPanel.addStyleName(AON.CSS.aonMarginLeft());
			mainPanel.addStyleName(AON.CSS.aonFixedFont());
			for (InvoiceError error : callback.getResult().getAccountingInvoice().getMessages()) {
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
					SimplePanel container = new SimplePanel();
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

						}

						@Override
						public void onAccept(TediResult result) {
							TEDI_SERVICE.validateInvoice(callback.getCurrentDomainName(),
								callback.getCurrentUser(), callback.getCurrentDomainId(), result
								, new AsyncCallback<TediResult>() {
									
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
