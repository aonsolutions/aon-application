package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class AonAccountPanel extends SimplePanel implements Focusable {
	
	public static interface AonAccountPanelCallback {
		void onAccept(Account account);
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	private TextBox codeBox = new TextBox();
	private TextBox descriptionBox = new TextBox();
	
	public AonAccountPanel(final String domainName,final int domain, final String user,final AonAccountPanelCallback callback) {
		initializeCommonService();
		show(domainName,domain, user,new Account(),callback);
	}
	
	public AonAccountPanel(final String domainName,final int domain,final String user, Integer id, final AonAccountPanelCallback callback) {
		initializeCommonService();
		commonService.getAccount(domainName, domain, user, id, new AsyncCallback<Account>() {
			
			@Override
			public void onSuccess(Account result) {
				if (result == null) result = new Account();
				show(domainName,domain, user,result,callback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				show(domainName,domain, user,new Account(),callback);
			}
		});
	}
	
	public void show(final String domainName,final int domain, final String user,final Account account, final AonAccountPanelCallback callback) {
		setWidth("500px");
		setHeight("200px");
		
		
		FlowPanel rootPanel = new FlowPanel();
		
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
		table.addStyleName(AON.CSS.aonWidthAll());

				
		table.setWidget(0,0,new InlineLabel(AON.MSG.account()));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		 
		codeBox.setValue(account.getCode());
		codeBox.setVisibleLength(9);
		codeBox.setMaxLength(9);
		codeBox.setEnabled(account.getId()==null);
		codeBox.setStyleName(AON.CSS.aonInputText());
		codeBox.addKeyUpHandler( keyUpHandler);
		codeBox.addKeyUpHandler(  new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.isShiftKeyDown() &&  
					  (event.getNativeKeyCode() == 222 //FIREFOX
					|| event.getNativeKeyCode() == 219 //CHROME
						)) {
					String prefix = codeBox.getValue();
					prefix = AonStringUtils.remove(prefix, '?');
					commonService.getAccountNextCode(domainName, domain, user, prefix, new AsyncCallback<String>() {

						@Override
						public void onSuccess(String result) {
							codeBox.setValue(result);
							descriptionBox.selectAll();
							descriptionBox.setFocus(true);
						}
						@Override
						public void onFailure(Throwable caught) {
							errorPanel.showError(caught.getMessage());
							codeBox.setFocus(true);
						}
					});
				}
			}
		});
		codeBox.addBlurHandler(e -> {
			String prefix = codeBox.getValue();
			if(AonStringUtils.contains(prefix, ".")) {
				Integer zeroOffset = 9 - (prefix.length() - 1); // Length 9 - code - dot
				String zeroPad = AonStringUtils.leftPad("", zeroOffset, '0');
				prefix = prefix.replace(".", zeroPad);
				codeBox.setValue(prefix);
			} else if(AonStringUtils.contains(prefix, "?")) {
				prefix = AonStringUtils.remove(prefix, '?');
				commonService.getAccountNextCode(domainName, domain, user, prefix, new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						codeBox.setValue(result);
						descriptionBox.selectAll();
						descriptionBox.setFocus(true);
					}
					@Override
					public void onFailure(Throwable caught) {
						errorPanel.showError(caught.getMessage());
						codeBox.setFocus(true);
					}
				});
			}
		});
		table.setWidget(0,1,codeBox);
		
		table.setWidget(1,0,new InlineLabel(AON.MSG.description()));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		descriptionBox.setValue(account.getDescription());
		descriptionBox.setVisibleLength(35);
		descriptionBox.setMaxLength(128);
		descriptionBox.setStyleName(AON.CSS.aonInputText());
		descriptionBox.addKeyUpHandler( keyUpHandler);
		table.setWidget(1,1,descriptionBox);
		
		table.setWidget(2,0,new InlineLabel(AON.MSG.alias()));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		final TextBox aliasBox = new TextBox();
		aliasBox.setValue(account.getAlias());
		aliasBox.setVisibleLength(25);
		aliasBox.setMaxLength(32);
		aliasBox.setStyleName(AON.CSS.aonInputText());
		aliasBox.addKeyUpHandler( keyUpHandler);
		table.setWidget(2,1,aliasBox);
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
				account.setCode(codeBox.getValue());
				account.setDescription(descriptionBox.getValue());
				account.setAlias(aliasBox.getValue());
				if (account.getId() == null) {
					account.setDomain(domain);
					account.setEntryEnabled(true);
					account.setActive(true);
				}
				commonService.save(domainName, domain, user, account, new AsyncCallback<Account>() {

					@Override
					public void onSuccess(Account result) {
						callback.onAccept(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						errorPanel.showError(caught.getMessage());
						okButton.setEnabled(true);
						codeBox.setFocus(true);
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
	        	setFocus(true);
	        }
	    });		
		
	}

	@Override
	public int getTabIndex() {
		return codeBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		codeBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		if (codeBox.isEnabled()) {
			codeBox.selectAll();
			codeBox.setFocus(focused);
		} else {
			descriptionBox.selectAll();
			descriptionBox.setFocus(focused);
		}		
	}

	@Override
	public void setTabIndex(int index) {
		codeBox.setTabIndex(index);
	}

}
