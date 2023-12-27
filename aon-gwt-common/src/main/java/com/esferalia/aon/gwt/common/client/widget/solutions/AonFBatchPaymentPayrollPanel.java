package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AonFBatchPaymentPayrollPanel extends SimplePanel {
	
	public static interface AonFBatchPaymentPayrollPanelCallback {
		void onAccept();
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	private TextBox description = new TextBox();
	private AonDateBox issueDate = new AonDateBox();
	private ListBox bank = new ListBox();
	private ListBox type = new ListBox();
	private AonTableButton confidential = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
	private Label totalAmount = new Label();
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private boolean isConfidential = false;
	
	public AonFBatchPaymentPayrollPanel(final String domainName, final int domain, final String user, final AonFBatchPaymentPayrollPanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		show(new FBatch(), callback);
	}
	
	public AonFBatchPaymentPayrollPanel(final String domainName,final int domain,final String user, Integer fbatchId, final AonFBatchPaymentPayrollPanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		commonService.getFBatch(this.domainName, this.domainId, this.user, fbatchId, new AsyncCallback<FBatch>() {
			
			@Override
			public void onSuccess(FBatch result) {
				if (result == null) result = new FBatch();
				show(result, callback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				show(new FBatch(), callback);
			}
		});
	}
	
	public void show(final FBatch fbatch, final AonFBatchPaymentPayrollPanelCallback callback) {
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.getElement().getStyle().setProperty("padding", "1rem 0");
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		
		table.setWidget(0, 0, new InlineLabel(AON.MSG.description()));
		description.setValue(fbatch.getDescription());
		description.setMaxLength(32);
		table.setWidget(0,1,description);
		
		table.setWidget(1,0,new InlineLabel(AON.MSG.date()));
		issueDate.setValue(fbatch.getIssueDate());
		table.setWidget(1,1,issueDate);
		
		table.setWidget(2,0,new InlineLabel("Tipo Fichero"));
		type.addItem("SEPA 34-14 N\u00f3mina (XML)", "10");
		type.setSelectedIndex(0);
		table.setWidget(2,1,type);
		
		table.setWidget(3,0,new InlineLabel(AON.MSG.bankAccount()));
		bank.addItem("-");
		getEnterpriseBanks(companyBanks -> {
			companyBanks.stream().filter(companyBank -> companyBank.isActive()).forEach(companyBank -> {
				bank.addItem("(" + companyBank.getAlias() + ") " + companyBank.getBankAccount().toString(), companyBank.getId().toString());
			});
			setSelectedValueLB(bank, null != fbatch.getRbank() ? fbatch.getRbank().getId().toString() : null);
		});
		table.setWidget(3,1,bank);
		
		table.setWidget(4,0,new InlineLabel(AON.MSG.confidential()));
		confidential.addClickHandler(e -> {
			isConfidential = !isConfidential;
			if (isConfidential) {
				confidential.addStyleName(AON.CSS.aonIconChecked());
				confidential.removeStyleName(AON.CSS.aonIconCheck());
			} else {
				confidential.addStyleName(AON.CSS.aonIconCheck());
				confidential.removeStyleName(AON.CSS.aonIconChecked());
			}
		});
		table.setWidget(4,1,confidential);
		
		if(fbatch.getBatchDetails() != null && !fbatch.getBatchDetails().isEmpty()) {
			table.setWidget(5,0,new InlineLabel(AON.MSG.amount() + " " + AON.MSG.total()));
			String amountSum = null == fbatch.getBatchDetails() || fbatch.getBatchDetails().isEmpty() ? "0.00 \u20ac" : AON.FMT.format(fbatch.getBatchDetails().stream().map(fBatchDetail -> fBatchDetail.getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac";
			totalAmount.setText(amountSum);
			table.setWidget(5,1,totalAmount);
		}
		
		// Apply Styles
		for(int i=0; i < table.getRowCount(); i++) {
			table.getCellFormatter().setStyleName(i, 0, AON.CSS.aonTableLabel());
			table.getCellFormatter().getElement(i, 0).setPropertyString("min-width", "135px");
			
			if(i != 5 && i != 4) table.getWidget(i, 1).setStyleName(AON.CSS.aonInputText());
		}
		
		tablePanel.add( table );
		
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				fbatch.setDescription(description.getValue());
				fbatch.setIssueDate(issueDate.getValue());
				fbatch.setType((byte) Integer.parseInt(type.getSelectedValue()));
				fbatch.setRbank(bank.getSelectedIndex() == 0 ? null : new RegistryBank().setId(Integer.parseInt(bank.getSelectedValue())));
				fbatch.setConfidential(isConfidential);
				
				if (fbatch.getId() == null) {
					fbatch.setDomain(domainId);
					fbatch.setPayment((byte)1); // Pago
					fbatch.setStatus(FBatchStatus.PENDING); // Pendiente
				}
				
				commonService.createUpdateFBatch(domainName, domainId, user, fbatch, new AsyncCallback<FBatch>() {

					@Override
					public void onSuccess(FBatch result) {
						callback.onAccept();
					}
					@Override
					public void onFailure(Throwable caught) {
						errorPanel.showError(caught.getMessage());
						okButton.setEnabled(true);
					}
				});
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	protected abstract void onResize();

	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	private void getEnterpriseBanks(Consumer<LinkedList<RegistryBank>> success) {
		commonService.getCompanyBanks(this.domainName, this.domainId, this.user, new AsyncCallback<LinkedList<RegistryBank>>() {
			
			@Override
			public void onSuccess(LinkedList<RegistryBank> rbanks) {
				success.accept(rbanks);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

}
