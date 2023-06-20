package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCreditorFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel.AonRegistryFullPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistrySelectionDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSimpleDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSupplierFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class AccountPanel extends ScrollPanel implements HasSelectionHandlers<Account> {

	private static final int CHANGE_DISPLAY_MILLIS = 1000;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(AccountPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static final String ACCOUNT_STREAM_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/AccountStreamServlet");
	
	private final int limit = 100;
	private final MutableInt row = new MutableInt(0);
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private SimplePanel container;
	private FlexTable tab;
	private int lastScrollPos = 0;
	
	private static enum COLS {
		  NUM(AonStringUtils.EMPTY		,"20px"  ,AON.CSS.aonTextCenter())
		, COD(AON.MSG.code()			,"150px" ,null)
		, DES(AON.MSG.description()		,"auto"  ,null)
		, ALI(AON.MSG.aliasAbbr()		,"170px" ,null)
		, CCE(AON.MSG.costCenter()		,"150px" ,null)
		, ACT(AON.MSG.status()			,"20px"  ,null)
		, BUT(AonStringUtils.EMPTY		,"20px"  ,null)
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	public AccountPanel(AccountParams params) {
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingBottom());
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		container = new SimplePanel();
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
						search(offset.getValue(),params);
					}
				}
			}
		});
		onSearch(params);
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
	
	private void onSearch(AccountParams params) {
		enableMoreData();
		search(params);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	private void search(AccountParams params) {
		container.clear();
		tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonGrid());
		
		paintHeader();
		container.setWidget(tab);
		row.setValue(1);
		offset.setValue(0);
		search(offset.getValue(),params);
	}
	
	private void paintHeader() {
		for ( COLS col : COLS.values()) {
			tab.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth());	
			tab.setWidget(0, col.ordinal(), new Label( col.getHeaderLabel() ));
			tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
			if ( col.getCellStyleClass() != null) {
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
			}
		}
	}
	
	private void search(final int ofs,AccountParams params) {
		if (!isMoreData()) return;
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, ACCOUNT_STREAM_SERVLET);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				boolean something = false;
				if (state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						int count = 0;
						if (!JsonUtils.safeToEval(text)) {
							Window.alert("ERROR de evaluación");
						}
						JavaScriptObject unk = JsonUtils.safeEval(text);
						JsArray<JsAccount> array = unk.cast();
						for (; count < array.length(); count++ ) {
							something = true;
							Account account = newAccount (array.get(count));
							paintRow(  params.getDomain(), account );
						}
						
						if (array.length() < limit) {
							disableMoreData();
						} else {
							offset.setValue(ofs + count - 1);
							enableMoreData();
						}
					} catch (IndexOutOfBoundsException e) {
						FlowPanel line = new FlowPanel();
						InlineLabel label = new InlineLabel(e.getMessage());
						line.add(label);
						container.add(line);
						enableSearch();
					}
				}
				if (state == XMLHttpRequest.DONE) {
					if (!something) {
						FlowPanel line = new FlowPanel();
						InlineLabel label = new InlineLabel(AON.MSG.noData());
						line.add(label);
						container.add(line);
						disableMoreData();
					}
					enableSearch();
				}
			}
			
			
			private void paintRow(int domain, Account account) {
				final int r = row.getValue();
				paintRow(domain, r, account); 
				row.increment();
			}
			
			private void paintRow(int domain, final int r, Account account) {
				int col = 0;
				boolean myAccount =  account == null || AonNumberUtils.equals( account.getDomain() , domain); 
				if (myAccount) {
					paintActiveRow(r,col,account);
				} else {
					paintInactiveRow(r,col,account);
				}
			}

			private void paintInactiveRow(final int r, int col, Account account) {
				Label msg = new Label("");
				msg.setStyleName(AON.CSS.aonTabIcon());
				msg.addStyleName(AON.CSS.aonIconLevelTop());
				tab.setWidget(r, col, msg);
				col++;
				
				Label codeLabel = new Label(account.getCode());
				codeLabel.getElement().getStyle().setPaddingLeft( ((account.getLevel()-1)) , Unit.EM);
				tab.setWidget(r, col, codeLabel);
				col++;
				
				tab.setWidget(r, col, new Label(account.getDescription()));
				col++;

				tab.setWidget(r, col, new Label(account.getAlias()));
				col++;

				tab.setWidget(r, col, new Label(account.getCostCenter()));
				col++;

				CheckBox activeCheck = new CheckBox();
				activeCheck.addStyleName(AON.CSS.aonWidthAll());
				activeCheck.setValue(account.isActive());
				activeCheck.setEnabled(false);
				tab.setWidget(r, col, activeCheck);
				col++;
			}

			private void paintDeletedRow(final int r, int col, Account account) {
				Label msg = new Label("");
				msg.setStyleName(AON.CSS.aonTabIcon());
				msg.addStyleName(AON.CSS.aonIconLevelThis());
				tab.setWidget(r, col, msg);
				col++;
				
				Label codeLabel = new Label(account.getCode());
				codeLabel.setStyleName(AON.CSS.aonTextLineThrough());
				codeLabel.getElement().getStyle().setPaddingLeft( ((account.getLevel()-1)) , Unit.EM);
				tab.setWidget(r, col, codeLabel);
				col++;
				
				Label descriptionLabel = new Label(account.getDescription());
				descriptionLabel.setStyleName(AON.CSS.aonTextLineThrough());
				tab.setWidget(r, col, descriptionLabel);
				col++;

				Label aliasLabel = new Label(account.getAlias());
				aliasLabel.setStyleName(AON.CSS.aonTextLineThrough());
				tab.setWidget(r, col, aliasLabel);
				col++;

				Label costCenterLabel = new Label(account.getCostCenter());
				costCenterLabel.setStyleName(AON.CSS.aonTextLineThrough());
				tab.setWidget(r, col, costCenterLabel);
				col++;

				CheckBox activeCheck = new CheckBox();
				activeCheck.setStyleName(AON.CSS.aonBorderNone());
				activeCheck.addStyleName(AON.CSS.aonWidthAll());
				activeCheck.setValue(account.isActive());
				activeCheck.setEnabled(false);
				tab.setWidget(r, col, activeCheck);
				col++;
				
				FlowPanel buttonContainer = new FlowPanel();
				AonTableButton undoButton = new AonTableButton(AON.MSG.restoreAction(), AON.CSS.aonIconUndo());
				undoButton.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						undoButton.setEnabled(false);
						ConfirmDialog cd = new ConfirmDialog();
						cd.confirm(AON.MSG.restoreAction(), new ConfirmDialogCallback() {

							@Override
							public void onCancel() {
								undoButton.setEnabled(true);
							}

							@Override
							public void onAccept() {
								account.setId(null);
					        	COMMON_SERVICE.save(params.getDomainName(), params.getDomain(), params.getUser(), account, new AsyncCallback<Account>() {
									
									@Override
									public void onSuccess(Account result) {
										paintActiveRow(r, 0, result);
									}
									
									@Override
									public void onFailure(Throwable caught) {
										MessageDialog.error(caught.getMessage());
									}
								});
							}
						});
						
					}
				});
				buttonContainer.add(undoButton);
				tab.setWidget(r, col, buttonContainer);
				col++;
				
			}

			private void paintActiveRow(final int r, int col, Account account) {
				Label msg = new Label("");
				TextBox codeBox = new TextBox();
				TextBox descriptionBox = new TextBox();
				TextBox aliasBox = new TextBox();
				TextBox costCenterBox = new TextBox();
				CheckBox activeCheck = new CheckBox();

				ValueChangeHandler<String> valueChangeHandler = new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						account.setCode(codeBox.getValue());
						account.setDescription(descriptionBox.getValue());
						account.setAlias(aliasBox.getValue());
						account.setCostCenter(costCenterBox.getValue());
						account.setActive(activeCheck.getValue());
						saveAccount(params, account, msg);
					}
				};
				
				codeBox.addValueChangeHandler(valueChangeHandler);
				descriptionBox.addValueChangeHandler(valueChangeHandler);
				aliasBox.addValueChangeHandler(valueChangeHandler);
				costCenterBox.addValueChangeHandler(valueChangeHandler);
				
				
				activeCheck.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						account.setCode(codeBox.getValue());
						account.setDescription(descriptionBox.getValue());
						account.setAlias(aliasBox.getValue());
						account.setCostCenter(costCenterBox.getValue());
						account.setActive(activeCheck.getValue());
						saveAccount(params, account, msg);
					}
				});
				
				msg.setStyleName(AON.CSS.aonTabIcon());
				tab.setWidget(r, col, msg);
				col++;
				
				codeBox.setStyleName(AON.CSS.aonBorderNone());
				codeBox.addStyleName(AON.CSS.aonWidthAll());
				codeBox.getElement().getStyle().setPaddingLeft( ((account.getLevel()-1)) , Unit.EM);
				codeBox.setMaxLength(9);
				codeBox.setValue(account.getCode());
				tab.setWidget(r, col, codeBox);
				col++;
				
				descriptionBox.setStyleName(AON.CSS.aonBorderNone());
				descriptionBox.addStyleName(AON.CSS.aonWidthAll());
				descriptionBox.setMaxLength(128);
				descriptionBox.setValue(account.getDescription());
				tab.setWidget(r, col, descriptionBox);
				col++;

				aliasBox.setStyleName(AON.CSS.aonBorderNone());
				aliasBox.addStyleName(AON.CSS.aonWidthAll());
				aliasBox.setMaxLength(32);
				aliasBox.setValue(account.getAlias());
				tab.setWidget(r, col, aliasBox);
				col++;

				costCenterBox.setStyleName(AON.CSS.aonBorderNone());
				costCenterBox.addStyleName(AON.CSS.aonWidthAll());
				costCenterBox.setMaxLength(32);
				costCenterBox.setValue(account.getCostCenter());
				tab.setWidget(r, col, costCenterBox);
				col++;

				activeCheck.setStyleName(AON.CSS.aonBorderNone());
				activeCheck.addStyleName(AON.CSS.aonWidthAll());
				activeCheck.setValue(account.isActive());
				tab.setWidget(r, col, activeCheck);
				col++;
				
				FlowPanel buttonContainer = new FlowPanel();
				
				AonTableButton clientButton = new AonTableButton(account.hasRegistry() ? "Vinculado" : "No Vinculado", account.hasRegistry() ? AON.CSS.aonIconPerson() : AON.CSS.aonIconPersonOff());
				clientButton.addClickHandler(e -> registryDialog(account, clientButton, params, msg));
				
				AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
				deleteButton.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						deleteButton.setEnabled(false);
						ConfirmDialog cd = new ConfirmDialog();
						cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {

							@Override
							public void onCancel() {
								deleteButton.setEnabled(true);
							}

							@Override
							public void onAccept() {
					        	COMMON_SERVICE.delete(params.getDomainName(), params.getDomain(), params.getUser(), account, new AsyncCallback<Account>() {
									
									@Override
									public void onSuccess(Account result) {
						        		paintDeletedRow(r, 0, account);
									}
									
									@Override
									public void onFailure(Throwable caught) {
										MessageDialog.error(caught.getMessage());
									}
								});
							}
						});
						
					}
				});
				
				// Solo mostrar para Proveedores (40*), Acreedores (41*), Clientes (43*)
				if(account.getCode().length() == 9 && (account.getCode().startsWith("40") || account.getCode().startsWith("41") || account.getCode().startsWith("43")))
					buttonContainer.add(clientButton);
				
				buttonContainer.add(deleteButton);
				tab.setWidget(r, col, buttonContainer);
				col++;
			}

			private Account newAccount(JsAccount account) {
				return  new Account()
						.setDomain(account.getDomain())
						.setId(account.getId())
						.setCode(account.getCode())
						.setDescription(account.getDescription())
						.setAlias(account.getAlias())
						.setActive(account.isActive())
						.setLevel((byte) account.getLevel())
						.setCostCenter(account.getCostCenter())
						.setHasRegistry(account.hasRegistry())
				;
			}
			
		});
		StringBuffer requestData = new StringBuffer();
		params.setOffset(ofs);
		params.setLimit(limit);
		requestData.append("&"+IRequestParamsNames.ACCOUNT_PARAMS +"=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
	}
	
	private void registryDialog(Account account, AonTableButton clientButton, AccountParams params, Label msg) {
		RegistryModuleOptions options = new RegistryModuleOptions();
		options.setDomainName(params.getDomainName());
		options.setDomain(params.getDomain());
		options.setUser(params.getUser());
		
		COMMON_SERVICE.getAonConfiguration(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<AonConfiguration>() {

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(AonConfiguration result) {
				options.setConfiguration(result);
				
				if(!account.hasRegistry()) {
					if(account.getCode().startsWith("40")) { // Proveedores
						final AonSimpleDialog dialog = createDialog(AON.MSG.supplier());
						
						SupplierFull supplierFull = SupplierFull.initialize(options.getDomain());
						supplierFull.setAccount(account);
						supplierFull.getRegistry().setName(account.getDescription());
						
						AonSupplierFullPanel supplierPanel = new AonSupplierFullPanel(options, supplierFull, new AonRegistryFullPanelCallback<SupplierFull>() {
							
							@Override public void setFocus(boolean b) { /* callback.setFocus(b); */ }
							
							@Override public void onError(Throwable caught) { /* Que habria que hacer aqui? */ };
							
							@Override public void onCancel() { dialog.hide(); }
							
							@Override
							public void onAccept(SupplierFull rf) {
								dialog.hide();
								updateAccountRegistry(params, account, msg, clientButton, true);
							}

							@Override public void onDocumenthanged(SupplierFull registryFull) { /* Nothing to do here */ }
						
						});
						
						showDialog(dialog, supplierPanel);
						
					} else if(account.getCode().startsWith("41")) { // Acreedores
						final AonSimpleDialog dialog = createDialog(AON.MSG.creditor());

						CreditorFull creditorFull = CreditorFull.initialize(options.getDomain());
						creditorFull.setAccount(account);
						creditorFull.getRegistry().setName(account.getDescription());
						
						AonCreditorFullPanel creditorPanel = new AonCreditorFullPanel(options, creditorFull, new AonRegistryFullPanelCallback<CreditorFull>() {
							
							@Override public void setFocus(boolean b) { /* callback.setFocus(b); */ }
							
							@Override public void onError(Throwable caught) { /* Que habria que hacer aqui? */ };
							
							@Override public void onCancel() { dialog.hide(); }
							
							@Override
							public void onAccept(CreditorFull rf) {
								dialog.hide();
								updateAccountRegistry(params, account, msg, clientButton, true);
							}

							@Override public void onDocumenthanged(CreditorFull registryFull) { /* Nothing to do here */ }
							
						});
						
						showDialog(dialog, creditorPanel);

					} else if(account.getCode().startsWith("43")) { // Clientes
						final AonSimpleDialog dialog = createDialog(AON.MSG.customer());

						CustomerFull customerFull = CustomerFull.initialize(options.getDomain());
						customerFull.setAccount(account);
						customerFull.getRegistry().setName(account.getDescription());
						
						AonCustomerFullPanel customerPanel = new AonCustomerFullPanel(options, customerFull, new AonRegistryFullPanelCallback<CustomerFull>() {
							
							@Override public void setFocus(boolean b) { /* callback.setFocus(b); */ }
							
							@Override public void onError(Throwable caught) { /* Que habria que hacer aqui? */ };
							
							@Override public void onCancel() { dialog.hide(); }
							
							@Override
							public void onAccept(CustomerFull rf) {
								dialog.hide();
								updateAccountRegistry(params, account, msg, clientButton, true);
							}

							@Override public void onDocumenthanged(CustomerFull registryFull) { /* Nothing to do here */ }
							
						});
						
						showDialog(dialog, customerPanel);
						
					}
				} else { // Editor 
					if(account.getCode().startsWith("40")) { // Proveedores
						
						getSuppliers(account.getId(), options, suppliers -> {
							if(suppliers.size() == 1) getSupplier(suppliers.get(0).getId(), options, suppliersFull -> showSupplierEditor(suppliersFull, options, params, account, msg, clientButton));
							else {
								new AonRegistrySelectionDialog(AON.MSG.supplier(), suppliers.stream().map(supplier -> supplier.get()).collect(Collectors.toList())) {
									
									@Override public void onAccept(Integer registry) {
										getSupplier(registry, options, suppliersFull -> showSupplierEditor(suppliersFull, options, params, account, msg, clientButton));
									}
									
								};
							}
						});
						
					} else if(account.getCode().startsWith("41")) { // Acreedores
						
						getCreditors(account.getId(), options, creditors -> {
							if(creditors.size() == 1) getCreditor(creditors.get(0).getId(), options, creditorFull -> showCreditorEditor(creditorFull, options, params, account, msg, clientButton));
							else {
								new AonRegistrySelectionDialog(AON.MSG.creditor(), creditors.stream().map(creditor -> creditor.get()).collect(Collectors.toList())) {
									
									@Override public void onAccept(Integer registry) {
										getCreditor(registry, options, creditorFull -> showCreditorEditor(creditorFull, options, params, account, msg, clientButton));
									}
									
								};
							}
						});
						
					} else if(account.getCode().startsWith("43")) { // Clientes

						getCustomers(account.getId(), options, customers -> {
							if(customers.size() == 1) getCustomer(customers.get(0).getId(), options, customerFull -> showCustomerEditor(customerFull, options, params, account, msg, clientButton));
							else {
								new AonRegistrySelectionDialog(AON.MSG.customer(), customers.stream().map(customer -> customer.get()).collect(Collectors.toList())) {
									
									@Override public void onAccept(Integer registry) {
										getCustomer(registry, options, customerFull -> showCustomerEditor(customerFull, options, params, account, msg, clientButton));
									}
									
								};
							}
						});
						
					}
				}
			}
			
		});
	}
	
	private void showSupplierEditor(SupplierFull supplierFull, RegistryModuleOptions options, AccountParams params, Account account, Label msg, AonTableButton clientButton) {
		final AonSimpleDialog dialog = createDialog(AON.MSG.supplier());
		
		AonSupplierFullPanel supplierPanel = new AonSupplierFullPanel(options, supplierFull, new AonRegistryFullPanelCallback<SupplierFull>() {
			
			@Override public void setFocus(boolean b) { /* callback.setFocus(b); */ }
			
			@Override public void onError(Throwable caught) { /* Que habria que hacer aqui? */ };
			
			@Override public void onCancel() { dialog.hide(); }
			
			@Override public void onAccept(SupplierFull rf) {
				dialog.hide();
				if(null == rf.getAccount()) updateAccountRegistry(params, account, msg, clientButton, false);
			}

			@Override public void onDocumenthanged(SupplierFull registryFull) { /* Nothing to do here */ }
		
		});
		
		showDialog(dialog, supplierPanel);
	}
	
	private void showCreditorEditor(CreditorFull creditorFull, RegistryModuleOptions options, AccountParams params, Account account, Label msg, AonTableButton clientButton) {
		final AonSimpleDialog dialog = createDialog(AON.MSG.creditor());

		AonCreditorFullPanel creditorPanel = new AonCreditorFullPanel(options, creditorFull, new AonRegistryFullPanelCallback<CreditorFull>() {
			
			@Override public void setFocus(boolean b) { /* callback.setFocus(b); */ }
			
			@Override public void onError(Throwable caught) { /* Que habria que hacer aqui? */ };
			
			@Override public void onCancel() { dialog.hide(); }
			
			@Override public void onAccept(CreditorFull rf) { 
				dialog.hide(); 
				if(null == rf.getAccount()) updateAccountRegistry(params, account, msg, clientButton, false);
			}

			@Override public void onDocumenthanged(CreditorFull registryFull) { /* Nothing to do here */ }
			
		});
		
		showDialog(dialog, creditorPanel);
	}
	
	private void showCustomerEditor(CustomerFull customerFull, RegistryModuleOptions options, AccountParams params, Account account, Label msg, AonTableButton clientButton) {
		final AonSimpleDialog dialog = createDialog(AON.MSG.customer());

		AonCustomerFullPanel customerPanel = new AonCustomerFullPanel(options, customerFull, new AonRegistryFullPanelCallback<CustomerFull>() {
			
			@Override public void setFocus(boolean b) { /* callback.setFocus(b); */ }
			
			@Override public void onError(Throwable caught) { /* Que habria que hacer aqui? */ };
			
			@Override public void onCancel() { dialog.hide(); }
			
			@Override public void onAccept(CustomerFull rf) { 
				dialog.hide(); 
				if(null == rf.getAccount()) updateAccountRegistry(params, account, msg, clientButton, false);
			}

			@Override public void onDocumenthanged(CustomerFull registryFull) { /* Nothing to do here */ }
			
		});
		
		showDialog(dialog, customerPanel);
	}
	
	private AonSimpleDialog createDialog(String caption) {
		AonSimpleDialog dialog = new AonSimpleDialog();
		dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
		dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
		dialog.setCaption(caption);
		return dialog;
	}
	
	private void showDialog(AonSimpleDialog dialog, AonRegistryFullPanel<?> panel) {
		dialog.add( panel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	panel.setFocus(true);
	        }
	    });		
	}
	
	private void updateAccountRegistry(AccountParams params, Account account, Label msg, AonTableButton clientButton, boolean hasRegistry) {
		account.setHasRegistry(hasRegistry);
		setActivePersonButton(hasRegistry, clientButton);
		saveAccount(params, account, msg);
	}
	
	private void setActivePersonButton(boolean hasRegistry, AonTableButton button) {
		button.setTitle(hasRegistry ? "Vinculado" : "No Vinculado");
		button.removeStyleName(hasRegistry ? AON.CSS.aonIconPersonOff() : AON.CSS.aonIconPerson());
		button.addStyleName(hasRegistry ? AON.CSS.aonIconPerson() : AON.CSS.aonIconPersonOff());
	}
	
	private void getSuppliers(Integer account, RegistryModuleOptions options, Consumer<List<Supplier>> success) {
		COMMON_SERVICE.getSuppliers(options.getDomainName(), options.getDomain(), options.getUser(), account, new AsyncCallback<List<Supplier>>() {
			
			@Override
			public void onSuccess(List<Supplier> suppliers) {
				success.accept(suppliers);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void getCreditors(Integer account, RegistryModuleOptions options, Consumer<List<Creditor>> success) {
		COMMON_SERVICE.getCreditors(options.getDomainName(), options.getDomain(), options.getUser(), account, new AsyncCallback<List<Creditor>>() {
			
			@Override
			public void onSuccess(List<Creditor> creditors) {
				success.accept(creditors);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void getCustomers(Integer account, RegistryModuleOptions options, Consumer<List<Customer>> success) {
		COMMON_SERVICE.getCustomers(options.getDomainName(), options.getDomain(), options.getUser(), account, new AsyncCallback<List<Customer>>() {
			
			@Override
			public void onSuccess(List<Customer> customers) {
				success.accept(customers);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void getSupplier(Integer registry, RegistryModuleOptions options, Consumer<SupplierFull> success) {
		COMMON_SERVICE.getSupplier(options.getDomainName(), options.getDomain(), options.getUser(), registry, new AsyncCallback<SupplierFull>() {
			
			@Override
			public void onSuccess(SupplierFull supplier) {
				success.accept(supplier);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void getCreditor(Integer registry, RegistryModuleOptions options, Consumer<CreditorFull> success) {
		COMMON_SERVICE.getCreditor(options.getDomainName(), options.getDomain(), options.getUser(), registry, new AsyncCallback<CreditorFull>() {
			
			@Override
			public void onSuccess(CreditorFull creditor) {
				success.accept(creditor);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void getCustomer(Integer registry, RegistryModuleOptions options, Consumer<CustomerFull> success) {
		COMMON_SERVICE.getCustomer(options.getDomainName(), options.getDomain(), options.getUser(), registry, new AsyncCallback<CustomerFull>() {
			
			@Override
			public void onSuccess(CustomerFull customer) {
				success.accept(customer);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void saveAccount(AccountParams params, Account account, Label msg) {
		COMMON_SERVICE.save(params.getDomainName(), params.getDomain(), params.getUser(), account, new AsyncCallback<Account>() {
			
			@Override
			public void onSuccess(Account result) {
				msg.addStyleName(AON.CSS.aonIconValid());
				new Timer() {
					@Override
					public void run() {
						msg.removeStyleName(AON.CSS.aonIconValid());
					}
				}.schedule(CHANGE_DISPLAY_MILLIS);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageDialog.error(caught.getMessage());
			}
		});
	}
	
}

