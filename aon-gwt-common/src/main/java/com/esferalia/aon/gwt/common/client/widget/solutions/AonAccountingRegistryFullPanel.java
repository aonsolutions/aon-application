package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AccountingRegistryService;
import com.esferalia.aon.gwt.common.client.AccountingRegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.AccountingRegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel.AonRegistryFullPanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class AonAccountingRegistryFullPanel extends DockLayoutPanel implements Focusable {
	
	private RegistryServiceAsync SERVICE;
	private static AccountingRegistryServiceAsync ACC_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(AonAccountingRegistryFullPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	public static interface AonAccountingRegistryFullPanelCallback {
		void onAccept(AccountingRegistry registry);
		void onCancel();
		void setFocus(boolean b);
	}

	private static class AonAccountingRegistryListBox extends ListBox {
		
		public AonAccountingRegistryListBox() {
			setWidth("120px");
			for (AccountingRegistryType d : AccountingRegistryType.values()) {
				if (d != AccountingRegistryType.UNDED_CREDITOR) {
					addItem(d.getDescription());	
				}
			}
		}

		public void setValue(AccountingRegistryType type) {
			setSelectedIndex(type.ordinal());
		}

		public AccountingRegistryType getValue() {
			return AccountingRegistryType.values()[getSelectedIndex()];
		}
		
	}

	private final AonAccountingRegistryListBox typeBox = new AonAccountingRegistryListBox();
	private final FlowPanel documentWarningContainer = new FlowPanel();
	private final AonErrorPanel errorPanel = new AonErrorPanel();
	private final SimpleLayoutPanel container = new SimpleLayoutPanel();
	
	public AonAccountingRegistryFullPanel(AonModuleOptions<?> options, final AccountingRegistryType type, final Integer id, AonAccountingRegistryFullPanelCallback callback) {
		
		super(Unit.PX);

		AccountingRegistryServiceAsync accServiceRaw = GWT.create(AccountingRegistryService.class);
		ACC_SERVICE = new AccountingRegistryServiceAsyncDecorator(accServiceRaw);
		
		RegistryServiceAsync serviceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(serviceRaw);

		
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		errorPanel.addStyleName(AON.CSS.aonMarginBottom());
		add(errorPanel);

		FlowPanel header = new FlowPanel();
		header.setStyleName(AON.CSS.aonTextCenter());
		
		InlineLabel typeLabel = new InlineLabel(AON.MSG.titularType());
		typeLabel.setStyleName(AON.CSS.aonTableLabel());
		typeLabel.addStyleName(AON.CSS.aonMarginRight());
		header.add(typeLabel);
		typeBox.setValue( type );
		typeBox.setEnabled(id == null);
		header.add(typeBox);
		addNorth(header,30);
		documentWarningContainer.getElement().getStyle().setProperty("display", "inline-grid");
		documentWarningContainer.getElement().getStyle().setProperty("max-height", "160px");
		documentWarningContainer.getElement().getStyle().setProperty("overflow-y", "auto");
		documentWarningContainer.addStyleName(AON.CSS.aonFlexBlock());
		documentWarningContainer.addStyleName(AON.CSS.aonFontSmall());
		documentWarningContainer.addStyleName(AON.CSS.aonWidthAlmostAll()); 
		documentWarningContainer.addStyleName(AON.CSS.aonBackgroundLigthGray());
		addNorth(documentWarningContainer,0);
		
		add(container);
		
		populate(options,id,callback);
	}

	private AonRegistryFullPanelCallback<SupplierFull> getSupplierVisitor(AonModuleOptions<?> options, AonAccountingRegistryFullPanelCallback callback) {
		return new AonRegistryFullPanelCallback<SupplierFull>() {
			
			@Override
			public void setFocus(boolean b) {
				callback.setFocus(b);
			}
			
			@Override
			public void onCancel() {
				callback.onCancel();
			}
			
			@Override
			public void onError(Throwable caught) {
				errorPanel.showError(caught.getMessage());	
			};
			
			@Override
			public void onAccept(SupplierFull rf) {
				AccountingRegistry ar = getFromSupplierFull( rf );
				callback.onAccept( ar );
			}

			@Override
			public void onDocumenthanged(SupplierFull rf) {
				checkSupplierDocument( options,callback,rf);
			}
		};
	}
	
	private AonRegistryFullPanelCallback<CustomerFull> getCustomerVisitor(AonModuleOptions<?> options, AonAccountingRegistryFullPanelCallback callback) {
		return new AonRegistryFullPanelCallback<CustomerFull>() {
			
			@Override
			public void setFocus(boolean b) {
				callback.setFocus(b);
			}
			
			@Override
			public void onError(Throwable caught) {
				errorPanel.showError(caught.getMessage());	
			};
	
			@Override
			public void onCancel() {
				callback.onCancel();
			}
			
			@Override
			public void onAccept(CustomerFull rf) {
				AccountingRegistry ar = getFromCustomerFull( rf );
				callback.onAccept( ar );
			}
			
			@Override
			public void onDocumenthanged(CustomerFull customerFull) {
				checkCustomerDocument(options, callback, customerFull);
			}
		};
	}
	
	private AonRegistryFullPanelCallback<CreditorFull> getCreditorVisitor(AonModuleOptions<?> options, AonAccountingRegistryFullPanelCallback callback) {
		return new AonRegistryFullPanelCallback<CreditorFull>() {
			
			@Override
			public void setFocus(boolean b) {
				callback.setFocus(b);
			}
			
			@Override
			public void onError(Throwable caught) {
				errorPanel.showError(caught.getMessage());	
			};
			
			@Override
			public void onCancel() {
				callback.onCancel();
			}
			
			@Override
			public void onAccept(CreditorFull rf) {
				AccountingRegistry ar = getFromCreditorFull( rf );
				callback.onAccept( ar );
			}
			
			@Override
			public void onDocumenthanged(CreditorFull creditorFull) {
				LOGGER.info("onDocumenthanged --> checkCreditorDocument");
				checkCreditorDocument( options,callback,creditorFull);
			}
			
		};
	}


	private void populate(AonModuleOptions<?> options, final Integer id,AonAccountingRegistryFullPanelCallback callback) {
		final IAccountingRegistryTypeVisitor visitor = new IAccountingRegistryTypeVisitor() {
			@Override
			public void visitSupplier(AccountingRegistry reg) {
				if ( id == null) {
					SupplierFull full = SupplierFull.initialize( options.getDomain() );
					container.setWidget(new AonSupplierFullPanel(options, full, getSupplierVisitor(options,callback)));
				} else {
					SERVICE.getSupplierFull(options.getDomainName(), options.getDomain(), options.getUser(), id, new AsyncCallback<SupplierFull>() {
						
						@Override
						public void onSuccess(SupplierFull result) {
							container.setWidget(new AonSupplierFullPanel(options, result, getSupplierVisitor(options,callback)));
						}
						
						@Override
						public void onFailure(Throwable caught) {
							errorPanel.showError(caught.getMessage());	
						}
					});					
				}
			}
			
			@Override
			public void visitCustomer(AccountingRegistry reg) {
				if ( id == null) {
					CustomerFull full = CustomerFull.initialize( options.getDomain() );
					container.setWidget(new AonCustomerFullPanel(options, full, getCustomerVisitor(options, callback)));
				} else {
					SERVICE.getCustomerFull(options.getDomainName(), options.getDomain(), options.getUser(), id, new AsyncCallback<CustomerFull>() {
						
						@Override
						public void onSuccess(CustomerFull result) {
							container.setWidget(new AonCustomerFullPanel(options, result, getCustomerVisitor(options, callback)));
						}
						
						@Override
						public void onFailure(Throwable caught) {
							errorPanel.showError(caught.getMessage());	
						}
					});					
				}
			}
			
			@Override
			public void visitCreditor(AccountingRegistry reg) {
				if ( id == null) {
					CreditorFull full = CreditorFull.initialize( options.getDomain() );
					container.setWidget(new AonCreditorFullPanel(options, full, getCreditorVisitor(options, callback)));
				} else {
					SERVICE.getCreditorFull(options.getDomainName(), options.getDomain(), options.getUser(), id, new AsyncCallback<CreditorFull>() {
						
						@Override
						public void onSuccess(CreditorFull result) {
							container.setWidget(new AonCreditorFullPanel(options, result, getCreditorVisitor(options, callback)));
						}
						
						@Override
						public void onFailure(Throwable caught) {
							errorPanel.showError(caught.getMessage());	
						}
					});					
				}
			}
			
			@Override
			public void visitUndedCreditor(AccountingRegistry reg) {
				visitCreditor(reg);
			}
		};
		typeBox.getValue().visit(null, visitor );	
		typeBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				documentWarningContainer.clear();
				setWidgetSize(documentWarningContainer, 0);
				typeBox.getValue().visit(null, visitor);	
			}
		});
	}

	@Override
	public int getTabIndex() {
		return typeBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		typeBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		typeBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		typeBox.setTabIndex(index);
	}
	private void checkSupplierDocument(AonModuleOptions<?> options,AonAccountingRegistryFullPanelCallback callback,SupplierFull supplierFull) {
		checkRegistryDocument( options, callback, AccountingRegistryType.SUPPLIER, supplierFull );	
	}
	private void checkCreditorDocument(AonModuleOptions<?> options,AonAccountingRegistryFullPanelCallback callback,CreditorFull creditorFull) {
		checkRegistryDocument( options, callback, AccountingRegistryType.CREDITOR, creditorFull );
	}
	private void checkCustomerDocument(AonModuleOptions<?> options,AonAccountingRegistryFullPanelCallback callback,CustomerFull customerFull) {
		checkRegistryDocument( options, callback, AccountingRegistryType.CUSTOMER, customerFull );
	}
	
	private void checkRegistryDocument(AonModuleOptions<?> options
			, AonAccountingRegistryFullPanelCallback callback
			, AccountingRegistryType accountingRegistryType
			, RegistryFull<?> rf) {
		LOGGER.info("checkCreditorDocument " + (rf.getId() == null) + " : " + AonStringUtils.isNotBlank( rf.getRegistry().getDocument()));
		if ( rf.getId() == null && AonStringUtils.isNotBlank( rf.getRegistry().getDocument())) {
			ACC_SERVICE.getAccountingRegistries(options.getDomainName(), options.getDomain(), options.getUser(),
					new AccountingRegistryParams()
						.setType(accountingRegistryType)
						.setId(rf.getId())
						.setDocument(rf.getRegistry().getDocument())
					,new AsyncCallback<LinkedList<AccountingRegistry>>() {
				
				@Override
				public void onSuccess(LinkedList<AccountingRegistry> result) {
					LOGGER.info("getAccountingRegistries --> " + (result == null?"NULL":" " + result.size()) );
					int count = 0;
					documentWarningContainer.clear();
					documentWarningContainer.removeStyleName(AON.CSS.aonMargin());
					documentWarningContainer.removeStyleName(AON.CSS.aonBorder());
					documentWarningContainer.removeStyleName(AON.CSS.aonPaddingLeft());
					Boolean sameType = false;
					for (AccountingRegistry reg : result) {
						String icon = AON.CSS.aonIconCustomer();
						sameType = sameType || accountingRegistryType == reg.getType();
						if (reg.getType() == AccountingRegistryType.SUPPLIER) {
							icon = AON.CSS.aonIconSupplier();
						} else if (reg.getType() == AccountingRegistryType.CREDITOR) {
							icon = AON.CSS.aonIconCreditor();
						}
						if (!AonNumberUtils.equals(reg.getId(),rf.getId()))  {
							++count;
							Label label = new Label(AccountingRegistry.getFullDescription(reg) );
							label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
							label.setTitle(AON.MSG.selectAction());
							label.setStyleName(AON.CSS.aonLabelWithIcon());
							label.addStyleName(icon);
							label.addStyleName(AON.CSS.aonClickableLabel());
							label.addStyleName(AON.CSS.aonEllipsis());
							label.addClickHandler( new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									if (reg.getType() == accountingRegistryType) {
										callback.onAccept(reg);
									} else {
										AonConfirmDialog cd = new AonConfirmDialog();
										cd.confirm("Seleccionar", "Ha seleccionado un "+
												reg.getType().getDescription()	
											+". Continuar?",new AonConfirmDialogCallback() {
											
											@Override
											public void onCancel() {
											}
											
											@Override
											public void onAccept() {
												callback.onAccept(reg);
											}
										});
									}
								}
							});
							documentWarningContainer.add( label );
							if (getWidgetSize(documentWarningContainer) < 150) {
								setWidgetSize(documentWarningContainer, (getWidgetSize(documentWarningContainer) + 35));
							}
						}
					} 
					
					if(!sameType) {
						// To avoid final error.
						LinkedList<AccountingRegistry>  arList = new LinkedList<AccountingRegistry>();
						accountingRegistryType.visit(null, new IAccountingRegistryTypeVisitor() {
							@Override public void visitCustomer(AccountingRegistry reg) {arList.add(getFromCustomerFull((CustomerFull) rf));}
							@Override public void visitCreditor(AccountingRegistry reg) {arList.add(getFromCreditorFull((CreditorFull) rf));}
							@Override public void visitSupplier(AccountingRegistry reg) {arList.add(getFromSupplierFull((SupplierFull) rf));}
							@Override public void visitUndedCreditor(AccountingRegistry reg) {arList.add(getFromCreditorFull((CreditorFull) rf));}
						});
						AccountingRegistry ar = arList.get(0);
						LOGGER.info("INITIALIZE" );	
						ACC_SERVICE.initialize(options.getDomainName(), options.getDomain(), options.getUser(), 
								ar,  new AsyncCallback<AccountingRegistry>() {
									
							@Override
							public void onSuccess(AccountingRegistry result) {
								LOGGER.info("INITIALIZE onSuccess " + (result==null?"NULL":result.getName()) );
								if (result != null) {
									accountingRegistryType.visit(result, new IAccountingRegistryTypeVisitor() {
										@Override public void visitCustomer(AccountingRegistry reg) {
											container.setWidget(new AonCustomerFullPanel(options, toCustomerFull(reg), getCustomerVisitor(options, callback)));
										}
										@Override public void visitCreditor(AccountingRegistry reg) {
											LOGGER.info("INITIALIZE onSuccess visitCreditor 1 " + (reg==null?"NULL":reg.getName()) );
											CreditorFull creditorFull = toCreditorFull(reg);
											LOGGER.info("INITIALIZE onSuccess visitCreditor 2" + (creditorFull==null?"NULL":creditorFull.getRegistry().getName()) );
											container.setWidget(new AonCreditorFullPanel(options, creditorFull, getCreditorVisitor(options, callback)));
										}
										@Override public void visitSupplier(AccountingRegistry reg) {
											container.setWidget(new AonSupplierFullPanel(options, toSupplierFull(reg), getSupplierVisitor(options, callback)));
										}
										@Override public void visitUndedCreditor(AccountingRegistry reg) {
											container.setWidget(new AonCreditorFullPanel(options, toCreditorFull(reg), getCreditorVisitor(options, callback)));
										}
									});
								}
							}
									
							@Override
							public void onFailure(Throwable caught) {
										
							}
						});
					}
					if (count > 0) {
						Label errorLabel = new Label( AON.MSG.existingRegistryWarning(result.size()));
						errorLabel.setStyleName(AON.CSS.aonLabelWithIcon());
						errorLabel.addStyleName(AON.CSS.aonIconWarning());
						errorLabel.addStyleName(AON.CSS.aonBold());
						errorLabel.addStyleName(AON.CSS.aonBorderBottom());
						documentWarningContainer.insert(errorLabel, 0 );
						documentWarningContainer.addStyleName(AON.CSS.aonMargin());
						documentWarningContainer.addStyleName(AON.CSS.aonBorder());
						documentWarningContainer.addStyleName(AON.CSS.aonPaddingLeft());
						if (getWidgetSize(documentWarningContainer) < 150) {
							setWidgetSize(documentWarningContainer, (getWidgetSize(documentWarningContainer) + 35));
						}
					} 
				}
				
				@Override
				public void onFailure(Throwable caught) {
					errorPanel.showError(caught.getMessage());
				}
			});
		}
	}
	
	private void fill(RegistryFull<?> registryFull, AccountingRegistry rf) {
		Registry registry = registryFull.getRegistry();
		registry
			.setId(rf.getId())
			.setDomain(new Domain().setId(rf.getDomain()))
			.setDocument( rf.getDocument() )
			.setDocumentCountry(rf.getDocumentCountry())
			.setDocumentType(rf.getDocumentType())
			.setNationality(rf.getNationality())
			.setName(rf.getName())
			.setAlias(rf.getAlias());
		if (rf.getAddressId() != null || AonStringUtils.isNotBlank(rf.getAddress())) {
			RegistryAddress address = new RegistryAddress(); 
			registryFull.addAddress(address);
			address.setId(rf.getAddressId())
				.setMain( true )
				.setStreetType( rf.getAddressStreetType())
				.setAddress( rf.getAddress() )
				.setNumber(rf.getAddressNumber())
				.setCity(rf.getAddressTown())
				.setZip(rf.getAddressZIP())
				.setGeozone( rf.getGeozone() )
			;
		}
	}
	
	private CustomerFull toCustomerFull(AccountingRegistry rf) {
		CustomerFull cf = new CustomerFull();
		Customer customer = new Customer();
		cf.setRegistry(customer);
		fill(cf, rf);
		customer
		.setScope(rf.getScope())
		.setTransaction(rf.getTransaction())
		.setSurcharge(rf.isSurcharge())
		.setWithholding(rf.isWithholding())
		.setAccount(rf.getAccountId() )
		;
		if (rf.getAccountId() != null) {
			cf.setAccount( new Account()
					.setId(rf.getAccountId())
					.setCode(rf.getAccountCode())
					.setDescription(rf.getAccountDescription())
					);
		}
		return cf;
	}
	
	private CreditorFull toCreditorFull(AccountingRegistry rf) {
		CreditorFull customerFull = new CreditorFull();
		Creditor creditor = new Creditor();
		customerFull.setRegistry(creditor);
		fill(customerFull, rf);		
		creditor
			.setScope(rf.getScope())
			.setTransaction(rf.getTransaction())
			.setWithholding(rf.isWithholding())
			.setVatAccrualPayment(rf.isVatAccrualPayment())
			.setAccount(rf.getAccountId() )
		;
		if (rf.getAccountId() != null) {
			customerFull.setAccount( new Account()
				.setId(rf.getAccountId())
				.setCode(rf.getAccountCode())
				.setDescription(rf.getAccountDescription())
			);
		}
		return customerFull;
	}
	
	private SupplierFull toSupplierFull(AccountingRegistry rf) {
		SupplierFull supplierFull = new SupplierFull();
		Supplier supplier = new Supplier();
		supplierFull.setRegistry(supplier);
		fill(supplierFull, rf);
		supplier
			.setScope(rf.getScope())
			.setTransaction(rf.getTransaction())
			.setWithholding(rf.isWithholding())
			.setWithholdingFarmer(rf.isWithholdingFarmer())
			.setVatAccrualPayment(rf.isVatAccrualPayment())
			.setAccount(rf.getAccountId() )
		;
		if (rf.getAccountId() != null) {
			supplierFull.setAccount( new Account()
				.setId(rf.getAccountId())
				.setCode(rf.getAccountCode())
				.setDescription(rf.getAccountDescription())
			);
		}
		return supplierFull;
	}

	private AccountingRegistry getFromCustomerFull(CustomerFull rf) {
		Customer customer = rf.getRegistry();
		return new AccountingRegistry()
			.setType(AccountingRegistryType.CUSTOMER)
			.setId( rf.getId() )
			.setDomain( rf.getDomain())
			.setDocument( customer.getDocument() )
			.setDocumentCountry(customer.getDocumentCountry())
			.setDocumentType(customer.getDocumentType())
			.setNationality(customer.getNationality())
			.setName(customer.getName())
			.setScope(customer.getScope())
			.setTransaction(customer.getTransaction())
			.setSurcharge(customer.isSurcharge())
			.setWithholding(customer.isWithholding())
			.setWithholdingFarmer(false)
			.setVatAccrualPayment(false)
			.setAccountId(rf.getAccount() == null ? null : rf.getAccount().getId() )
			.setAccountCode(rf.getAccount() == null ? null : rf.getAccount().getCode() )
			.setAccountDescription(rf.getAccount() == null ? null : rf.getAccount().getDescription() );
	}
	
	private AccountingRegistry getFromSupplierFull(SupplierFull rf) {
		Supplier supplier = rf.getRegistry();
		return new AccountingRegistry()
			.setType(AccountingRegistryType.SUPPLIER)
			.setId( rf.getId() )
			.setDomain( rf.getDomain())
			.setDocument( supplier.getDocument() )
			.setDocumentCountry(supplier.getDocumentCountry())
			.setDocumentType(supplier.getDocumentType())
			.setNationality(supplier.getNationality())
			.setName(supplier.getName())
			.setScope(supplier.getScope())
			.setTransaction(supplier.getTransaction())
			.setSurcharge(false)
			.setWithholding(supplier.isWithholding())
			.setWithholdingFarmer(supplier.isWithholdingFarmer())
			.setVatAccrualPayment(supplier.isVatAccrualPayment())
			.setAccountId(rf.getAccount() == null ? null : rf.getAccount().getId() )
			.setAccountCode(rf.getAccount() == null ? null : rf.getAccount().getCode() )
			.setAccountDescription(rf.getAccount() == null ? null : rf.getAccount().getDescription() );
		 
	}
	
	private AccountingRegistry getFromCreditorFull(CreditorFull rf) {
		Creditor creditor = rf.getRegistry();
		return new AccountingRegistry()
			.setType(AccountingRegistryType.CREDITOR)
			.setId(rf.getId())
			.setDomain(rf.getDomain())
			.setDocument(creditor.getDocument())
			.setDocumentCountry(creditor.getDocumentCountry())
			.setDocumentType(creditor.getDocumentType())
			.setNationality(creditor.getNationality())
			.setName(creditor.getName())
			.setScope(creditor.getScope())
			.setTransaction(creditor.getTransaction())
			.setSurcharge(false)
			.setWithholding(creditor.isWithholding())
			.setWithholdingFarmer(false)
			.setVatAccrualPayment(creditor.isVatAccrualPayment())
			.setAccountId(rf.getAccount() == null ? null : rf.getAccount().getId())
			.setAccountCode(rf.getAccount() == null ? null : rf.getAccount().getCode())
			.setAccountDescription(rf.getAccount() == null ? null : rf.getAccount().getDescription());
	}
	
}

