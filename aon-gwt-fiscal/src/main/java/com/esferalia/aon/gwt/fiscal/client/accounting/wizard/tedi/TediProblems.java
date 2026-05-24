package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexGrid;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceMassagesList;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
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
		String[] widths = {"80px", "1fr"};
		AonFlexGrid mainPanel = new AonFlexGrid(widths,AON.CSS.aonMarginTopSep(),AON.CSS.aonMarginLeft());
		setWidget(mainPanel);
		if (callback.getResult().getAccountingInvoice().hasMessages()) {
			callback.getResult().getAccountingInvoice().messageStream()
				.forEach( err -> {
					InvoiceErrorLevel level = err.getLevel();
					String msg = err.getMessage();
					boolean blocked = false;
					if (err.getContext() != null && err.getContext().getKey() == InvoiceErrorKey.REGISTRY_STATUS ) {
						blocked = true;
						level = InvoiceErrorLevel.WRN;
					}


				    InlineLabel errLabel = new InlineLabel(level.getLabel());
					errLabel.getElement().getStyle().setColor(InvoiceMassagesList.getColor(level));
					mainPanel.addCell(errLabel  
						, AON.CSS.aonBold()
						, AON.CSS.aonAlignItemsCenter()
						, AON.CSS.aonJustifyContentCenter() );
					
				    FlowPanel container = new FlowPanel();
				    container.setStyleName(AON.CSS.aonPaddingLeft());

				    if (blocked) {
						String prefix = msg;
						String suffix = "";
						int start = AonStringUtils.indexOf(msg, '(');
					    int end = AonStringUtils.lastIndexOf(msg, ')');
					    if (start != -1 && end != -1 && end > start) {
					    	prefix = AonStringUtils.substring(msg, 0, start);
					    	suffix = AonStringUtils.substring(msg, start + 1, end);
					    }
					    
					    SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(prefix);
						HTML prefixLabel = new HTML(safeHtml);
						container.add(prefixLabel);
						
						Label suffixLabel = new Label(suffix);
						suffixLabel.setStyleName(AON.CSS.aonMarginTop());
						suffixLabel.addStyleName(AON.CSS.aonBold());
						container.add(suffixLabel);
						
					} else {
						SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(msg);
						HTML msgLabel = new HTML(safeHtml);
						msgLabel.setStyleName(AON.CSS.aonPaddingLeft());
						container.add(msgLabel);
					}
				    mainPanel.addCell(container);
	
	
					if (err.canBeFixed()) {
						TediContextVisitor tediContextVisitor = new TediContextVisitor(callback.getModuleOptions(), container);
						err.getContext().getKey().visit(tediContextVisitor, new ICallback() {
	
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
				}
			);
		}
	}

}
