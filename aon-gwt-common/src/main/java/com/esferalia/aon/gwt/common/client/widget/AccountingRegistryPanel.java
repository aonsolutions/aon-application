package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class AccountingRegistryPanel extends SimpleLayoutPanel implements Focusable {
	
	public static interface AccountingRegistryPanelCallback {
		void onAccept(AccountingRegistry registry);
		void onCancel();
		void setFocus(boolean b);
	}

	static CommonServiceAsync commonService;
	final AccountingRegistryTypeListBox type = new AccountingRegistryTypeListBox();
	FlowPanel documentWarningContainer = new FlowPanel();
	public AccountingRegistryPanel(final String domainName,final int domain,final String user
			, Integer id 
			, AonConfiguration config
			, final AccountingRegistryPanelCallback callback) {
		this(domainName, domain, user, id, config, null, callback);
	}
	
	public AccountingRegistryPanel(final String domainName,final int domain,final String user
			, Integer id 
			, AonConfiguration config
			, AccountingRegistry initial
			, final AccountingRegistryPanelCallback callback) {
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		setWidth("700px");
		setHeight("650px");
		documentWarningContainer.setStyleName(AON.AON_CSS.aonMarginBottom());
		if (id != null) {
			commonService.getAccountingRegistries(domainName, domain, user, id
					,new AsyncCallback<LinkedList<AccountingRegistry>>() {
				
				@Override
				public void onSuccess(LinkedList<AccountingRegistry> result) {
					if (result == null || result.size() == 0) {
						show(domainName,domain, user, config, newAccountingRegistry(domain,initial),callback);
					} else if (result.size() == 1) {
						show(domainName,domain, user, config, result.get(0),callback);
					} else {
						// MUY DIFICIL. Ej: proveedor + acreedor con el mismo registry.
						Window.alert("Encontrado más de un registry para el mismo id");
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					show(domainName,domain, user,config, newAccountingRegistry(domain,initial),callback);
				}
			});
		} else {
			show(domainName,domain, user, config, newAccountingRegistry(domain,initial),callback);
		}
	}
		
	protected AccountingRegistry newAccountingRegistry(int domain,AccountingRegistry initial) {
		AccountingRegistry ar = new AccountingRegistry()
			.setDomain(domain)
			.setType(AccountingRegistryType.CREDITOR)
			.setDocumentType(DocumentType.CIF)
			.setDocumentCountry(Country.ES)
			.setNationality(Country.ES)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			;
		if (initial != null) {
			ar
				.setType(initial.getType()!=null?initial.getType(): ar.getType())
				.setDocumentType(initial.getDocumentType()!=null?initial.getDocumentType():ar.getDocumentType())
				.setDocumentCountry(initial.getDocumentCountry()!=null?initial.getDocumentCountry():ar.getDocumentCountry())
				.setNationality(initial.getNationality()!=null?initial.getDocumentCountry():ar.getNationality())
				.setTransaction(initial.getTransaction()!=null?initial.getTransaction():ar.getTransaction())
				.setDocument(initial.getDocument())
				.setName(initial.getName())
				.setAlias(initial.getAlias())
				.setAddressStreetType(initial.getAddressStreetType())
				.setAddress(initial.getAddress())
				.setAddressNumber(initial.getAddressNumber())
				.setAddressTown(initial.getAddressTown())
				.setAddressZIP(initial.getAddressZIP())
				.setGeozone(initial.getGeozone())
				.setPhone(initial.getPhone())
				.setPhoneComments(initial.getPhoneComments())
				.setCellular(initial.getCellular())
				.setCellularComments(initial.getCellularComments())
				.setFax(initial.getFax())
				.setFaxComments(initial.getFaxComments())
				.setEmail(initial.getEmail())
				.setWeb(initial.getWeb())
			;
		}
		return ar.cleanDirty();
	}

	public void show(final String domainName,final int domain
			, final String user
			, final AonConfiguration config
			, final AccountingRegistry reg
			, final AccountingRegistryPanelCallback callback) {
		final CheckBox vatAccualPayment = new CheckBox(AON.MSG.vatAccrualPayment());
		final CheckBox surcharge = new CheckBox(AON.MSG.surcharge());
		final CheckBox withholdingFarmer = new CheckBox(AON.MSG.withholdingFarmer());
		final CheckBox withholding = new CheckBox(AON.MSG.withholding());
		final Button okButton = new Button();
		
		ScrollPanel scrollPanel = new ScrollPanel();
		FlowPanel rootPanel = new FlowPanel();
		scrollPanel.setWidget(rootPanel);
		
		final ErrorPanel errorPanel = new ErrorPanel();
		rootPanel.add(errorPanel);
		
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					if (reg.isDirty()) {
						ConfirmDialog cd = new ConfirmDialog();
						cd.confirm("Cancelar?", "Cancelar",new ConfirmDialogCallback() {
							
							@Override
							public void onCancel() {
							}
							
							@Override
							public void onAccept() {
								callback.onCancel();
							}
						});
					} else {
						callback.onCancel();
					}
				}
			}
		};

		int row = 0;
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidthAll());
				
		table.setWidget(row,0,new InlineLabel(AON.MSG.titularType()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		type.addKeyUpHandler( keyUpHandler);
		type.setValue( reg.getType() );
		type.setEnabled(reg.getId() == null);
		table.setWidget(row,1,type);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		type.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				documentWarningContainer.clear();
				type.getValue().visit(reg, new IAccountingRegistryTypeVisitor() {
					
					@Override
					public void visitSupplier(AccountingRegistry reg) {
						reg.setType(AccountingRegistryType.SUPPLIER);
						surcharge.setVisible(false);
						vatAccualPayment.setVisible(true);
						withholdingFarmer.setVisible(true);
						withholding.setVisible(true);
						okButton.setEnabled(reg.isDirty());
					}
					
					@Override
					public void visitCustomer(AccountingRegistry reg) {
						reg.setType(AccountingRegistryType.CUSTOMER);
						surcharge.setVisible(true);
						vatAccualPayment.setVisible(false);
						withholdingFarmer.setVisible(false);
						withholding.setVisible(true);
						okButton.setEnabled(reg.isDirty());
					}
					
					@Override
					public void visitCreditor(AccountingRegistry reg) {
						reg.setType(AccountingRegistryType.CREDITOR);
						surcharge.setVisible(false);
						vatAccualPayment.setVisible(true);
						withholdingFarmer.setVisible(false);
						withholding.setVisible(true);
						okButton.setEnabled(reg.isDirty());
					}
					
					@Override
					public void visitUndedCreditor(AccountingRegistry reg) {
						reg.setType(AccountingRegistryType.UNDED_CREDITOR);
						surcharge.setVisible(false);
						vatAccualPayment.setVisible(false);
						withholdingFarmer.setVisible(false);
						withholding.setVisible(false);
						okButton.setEnabled(reg.isDirty());
					}
				});	
			}
		});
		++row;
		
		table.setWidget(row,0,new InlineLabel("."));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		table.setWidget(row,1,documentWarningContainer);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.document()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());

		FullDocument fulldocument = new FullDocument();
		fulldocument.setValue(reg.getDocumentType(), reg.getDocumentCountry(), reg.getDocument());
		fulldocument.getTypeWidget().addKeyUpHandler( keyUpHandler);
		fulldocument.addTypeChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				reg.setDocumentType(fulldocument.getType());
				okButton.setEnabled(reg.isDirty());
				checkRegistryDocument(domainName,domain, user,config,callback,type.getValue(),reg.getId(),fulldocument,documentWarningContainer, false);
			}
		});
		fulldocument.getCountryWidget().addKeyUpHandler( keyUpHandler);
		fulldocument.addCountryChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				reg.setDocumentCountry(fulldocument.getCountry());
				okButton.setEnabled(reg.isDirty());
				checkRegistryDocument(domainName,domain,user,config,callback,type.getValue(),reg.getId(),fulldocument,documentWarningContainer, false);
			}
		});
		fulldocument.getDocumentWidget().addKeyUpHandler( keyUpHandler);
		fulldocument.addDocumentChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setDocument(fulldocument.getDocument());
				okButton.setEnabled(reg.isDirty());
				checkRegistryDocument(domainName,domain,user,config,callback,type.getValue(),reg.getId(),fulldocument,documentWarningContainer, true);
			}

		});
		table.setWidget(row,1,fulldocument);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		table.setWidget(row,0,new InlineLabel(AON.MSG.name()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox name = new TextBox();
		name.setValue(reg.getName());
		name.setStyleName(AON.AON_CSS.aonInputText());
		name.setVisibleLength(40);
		name.setMaxLength(64);
		name.addKeyUpHandler( keyUpHandler);
		name.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setName(name.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,name);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.alias()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox alias = new TextBox();
		alias.setValue(reg.getAlias());
		alias.setStyleName(AON.AON_CSS.aonInputText());
		alias.setVisibleLength(30);
		alias.setMaxLength(32);
		alias.addKeyUpHandler( keyUpHandler);
		alias.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setAlias(alias.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,alias);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.nationality()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final CountryListBox nation = new CountryListBox();
		nation.setValue(reg.getNationality());
		nation.addKeyUpHandler( keyUpHandler);
		nation.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				reg.setNationality(nation.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,nation);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		if ( config.getAvailableScopes() != null &&  config.getAvailableScopes().size() > 0) {
			
			table.setWidget(row,0,new InlineLabel(AON.MSG.scope()));
			table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			reg.setScope( config.getAvailableScopes().get(0).getId() ); 
			final ListBox scopeBox = new ListBox();
			for (Scope scope : config.getAvailableScopes()) {
				scopeBox.addItem(scope.getDescription(),AonNumberUtils.toString(scope.getId()));
			}
			scopeBox.addKeyUpHandler( keyUpHandler);
			scopeBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					reg.setScope(AonNumberUtils.toint(scopeBox.getSelectedValue()));
					okButton.setEnabled(reg.isDirty());
				}
			});
			
			table.setWidget(row,1,scopeBox);
			table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			++row;
			
		}
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.phone()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox phone = new TextBox();
		phone.setValue(reg.getPhone());
		phone.setEnabled(!(reg.getId() != null && reg.getPhone() != null && !reg.getPhone().equals("")));
		phone.setStyleName(AON.AON_CSS.aonInputText());
		phone.setVisibleLength(15);
		phone.setMaxLength(15);
		phone.addKeyUpHandler( keyUpHandler);
		phone.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setPhone(phone.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,phone);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		table.setWidget(row,0,new InlineLabel(AON.MSG.cellular()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox cellular = new TextBox();
		cellular.setValue(reg.getCellular());
		cellular.setEnabled(!(reg.getId() != null && reg.getCellular() != null && !reg.getCellular().equals("")));
		cellular.setStyleName(AON.AON_CSS.aonInputText());
		cellular.setVisibleLength(15);
		cellular.setMaxLength(15);
		cellular.addKeyUpHandler( keyUpHandler);
		cellular.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setCellular(cellular.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,cellular);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		table.setWidget(row,0,new InlineLabel(AON.MSG.fax()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox fax = new TextBox();
		fax.setValue(reg.getFax());
		fax.setEnabled(!(reg.getId() != null && reg.getFax() != null && !reg.getFax().equals("")));
		fax.setStyleName(AON.AON_CSS.aonInputText());
		fax.setVisibleLength(15);
		fax.setMaxLength(15);
		fax.addKeyUpHandler( keyUpHandler);
		fax.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setFax(fax.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,fax);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		table.setWidget(row,0,new InlineLabel(AON.MSG.email()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox email = new TextBox();
		email.setValue(reg.getEmail());
		email.setEnabled(!(reg.getId() != null && reg.getEmail() != null && !reg.getEmail().equals("")));
		email.setStyleName(AON.AON_CSS.aonInputText());
		email.setVisibleLength(40);
		email.setMaxLength(64);
		email.addKeyUpHandler( keyUpHandler);
		email.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setEmail(email.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,email);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		table.setWidget(row,0,new InlineLabel(AON.MSG.web()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox web = new TextBox();
		web.setValue(reg.getWeb());
		web.setEnabled(!(reg.getId() != null && reg.getWeb() != null && !reg.getWeb().equals("")));
		web.setStyleName(AON.AON_CSS.aonInputText());
		web.setVisibleLength(40);
		web.setMaxLength(64);
		web.addKeyUpHandler( keyUpHandler);
		web.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setWeb(web.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,web);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		table.setWidget(row,0,new InlineLabel(AON.MSG.transactionType()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final InvoiceTransactionListBox transaction = new InvoiceTransactionListBox();
		transaction.setValue(reg.getTransaction());
		transaction.addKeyUpHandler( keyUpHandler);
		transaction.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				reg.setTransaction(transaction.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,transaction);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.fiscalInformation()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		FlowPanel taxPanel0 = new  FlowPanel();
		
		vatAccualPayment.setValue(reg.isVatAccrualPayment());
		vatAccualPayment.setStyleName(AON.AON_CSS.aonMarginRight5());
		vatAccualPayment.addStyleName(AON.AON_CSS.aonNowrap());
		vatAccualPayment.addKeyUpHandler( keyUpHandler);
		vatAccualPayment.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				reg.setVatAccrualPayment(vatAccualPayment.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		taxPanel0.add(vatAccualPayment);
		
		surcharge.setValue(reg.isSurcharge());
		surcharge.setStyleName(AON.AON_CSS.aonMarginRight5());
		surcharge.addStyleName(AON.AON_CSS.aonNowrap());
		surcharge.addKeyUpHandler( keyUpHandler);
		surcharge.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				reg.setSurcharge(surcharge.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		taxPanel0.add(surcharge);
		
		table.setWidget(row,1,taxPanel0);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;
		
		table.setWidget(row,0,new InlineLabel("."));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		FlowPanel taxPanel1 = new  FlowPanel();

		withholdingFarmer.setValue(reg.isWithholdingFarmer());
		withholdingFarmer.setStyleName(AON.AON_CSS.aonMarginRight5());
		withholdingFarmer.addStyleName(AON.AON_CSS.aonNowrap());
		withholdingFarmer.addKeyUpHandler( keyUpHandler);
		withholdingFarmer.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				reg.setWithholdingFarmer(withholdingFarmer.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		taxPanel1.add(withholdingFarmer);
		
		withholding.setValue(reg.isWithholding());
		withholding.setStyleName(AON.AON_CSS.aonMarginRight5());
		withholding.addStyleName(AON.AON_CSS.aonNowrap());
		withholding.addKeyUpHandler( keyUpHandler);
		withholding.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				reg.setWithholding(withholding.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		taxPanel1.add(withholding);
		
		table.setWidget(row,1,taxPanel1);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		
		table.setWidget(row,0,new InlineLabel(AON.MSG.address()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		FlowPanel addressPanel = new  FlowPanel();
		
		final StreetTypeListBox addressStreetType = new StreetTypeListBox();
		addressStreetType.setValue(reg.getAddressStreetType());
		addressStreetType.setStyleName(AON.AON_CSS.aonMarginRight5());
		addressStreetType.addKeyUpHandler( keyUpHandler);
		addressStreetType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				reg.setAddressStreetType(addressStreetType.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		addressPanel.add(addressStreetType);
		final TextBox address = new TextBox();
		address.setValue(reg.getAddress());
		address.setStyleName(AON.AON_CSS.aonInputText());
		address.addStyleName(AON.AON_CSS.aonMarginRight5());
		address.setVisibleLength(35);
		address.setMaxLength(64);
		address.addKeyUpHandler( keyUpHandler);
		address.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setAddress(address.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		
		addressPanel.add(address);
		InlineLabel numberLabel = new InlineLabel(AON.MSG.number());
		numberLabel.setStyleName(AON.AON_CSS.aonMarginRight5());
		addressPanel.add(numberLabel);
		final TextBox addressNumber = new TextBox();
		addressNumber.setValue(reg.getAddressNumber());
		addressNumber.setStyleName(AON.AON_CSS.aonInputText());
		addressNumber.addStyleName(AON.AON_CSS.aonMarginRight5());
		addressNumber.setVisibleLength(5);
		addressNumber.setMaxLength(10);
		addressNumber.addKeyUpHandler( keyUpHandler);
		addressNumber.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setAddressNumber(addressNumber.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		addressPanel.add(addressNumber);
		table.setWidget(row,1,addressPanel);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		table.setWidget(row,0,new InlineLabel(AON.MSG.town()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox addressTown = new TextBox();
		addressTown.setValue(reg.getAddressTown());
		addressTown.setStyleName(AON.AON_CSS.aonInputText());
		addressTown.setVisibleLength(30);
		addressTown.setMaxLength(64);
		addressTown.addKeyUpHandler( keyUpHandler);
		addressTown.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setAddressTown(addressTown.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,addressTown);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		table.setWidget(row,0,new InlineLabel(AON.MSG.zip()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextBox addressZIP = new TextBox();
		addressZIP.setValue(reg.getAddressZIP());
		addressZIP.setStyleName(AON.AON_CSS.aonInputText());
		addressZIP.setVisibleLength(5);
		addressZIP.setMaxLength(10);
		addressZIP.addKeyUpHandler( keyUpHandler);
		addressZIP.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reg.setAddressZIP(addressZIP.getValue());
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,addressZIP);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;
		
		if (config.getGeozones() != null && !config.getGeozones().isEmpty()) {
			table.setWidget(row,0,new InlineLabel(AON.MSG.province()));
			table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			final ListBox provinceBox = new ListBox();
			addressZIP.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (!AonStringUtils.isBlank( addressZIP.getValue())) {
						String code = AonStringUtils.substring(AonStringUtils.trim(addressZIP.getValue()), 0, 2);
						int i = 1;
						for (GeoZone geozone : config.getGeozones()) {
							if (AonStringUtils.equals(geozone.getCode(),code)) {
								provinceBox.setSelectedIndex(i);
								int g = AonNumberUtils.toint(provinceBox.getSelectedValue());
								reg.setGeozone(g==Integer.MIN_VALUE?null:g);
							}
							i++;
						}
					}
				}
			});
			
			provinceBox.addItem("-----------",AonNumberUtils.toString(Integer.MIN_VALUE));
			int i = 1;
			for (GeoZone geozone : config.getGeozones()) {
				provinceBox.addItem(geozone.getName(),AonNumberUtils.toString(geozone.getId()));
				if (geozone.getId() == reg.getGeozone()) {
					provinceBox.setSelectedIndex(i);
				}
				i++;
			}
			provinceBox.addKeyUpHandler( keyUpHandler);
			provinceBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					int g = AonNumberUtils.toint(provinceBox.getSelectedValue());
					reg.setGeozone(g==Integer.MIN_VALUE?null:g);
					okButton.setEnabled(reg.isDirty());
				}
			});
			
			table.setWidget(row,1,provinceBox);
			table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			++row;
		}
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.account()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final AccountBox account = new AccountBox(domainName,domain,user);
		account.setValue(reg.getAccountId(),reg.getAccountCode(),reg.getAccountDescription());
		account.addKeyUpHandler( keyUpHandler);
		account.addSelectionHandler(new SelectionHandler<Account>() {
			
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				Account a = event.getSelectedItem();
				if (a == null) {
					reg.setAccountId(null);
					reg.setAccountCode(null);
					reg.setAccountDescription(null);
				} else {
					reg.setAccountId(a.getId());
					reg.setAccountCode(a.getCode());
					reg.setAccountDescription(a.getDescription());
					
				}
				okButton.setEnabled(reg.isDirty());
			}
		});
		table.setWidget(row,1,account);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		++row;

		tablePanel.add( table );
		rootPanel.add( tablePanel );
		
    	FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.AON_CSS.aonTextCenter());
    	
    	okButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.setEnabled(reg.getId() == null);
    	okButton.setVisible(reg.getId() == null);
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				if (AonStringUtils.isBlank(reg.getName())) {
					errorPanel.showError("Debe rellenar el nombre del titular" );
					okButton.setEnabled(true);
					name.setFocus(true);
				} else {
					commonService.insert(domainName, domain, user, reg, new AsyncCallback<AccountingRegistry>() {

						@Override
						public void onSuccess(AccountingRegistry result) {
							callback.onAccept(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							errorPanel.showError(caught.getMessage());
							okButton.setEnabled(true);
							callback.setFocus(true);
						}
					});
				}
			}
		});
    	buttons.add(okButton);

    	final Button updateButton = new Button();
    	updateButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
    	updateButton.setText( AON.MSG.accept());
    	updateButton.setEnabled(reg.getId() != null);
    	updateButton.setVisible(reg.getId() != null);
    	updateButton.addKeyUpHandler( keyUpHandler);
    	updateButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateButton.setEnabled(false);
				if (AonStringUtils.isBlank(reg.getName())) {
					errorPanel.showError("Debe rellenar el nombre del titular" );
					updateButton.setEnabled(true);
					name.setFocus(true);
				} else {
					commonService.update(domainName, domain, user, reg, new AsyncCallback<AccountingRegistry>() {

						@Override
						public void onSuccess(AccountingRegistry result) {
							callback.onAccept(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							errorPanel.showError(caught.getMessage());
							updateButton.setEnabled(true);
							callback.setFocus(true);
						}
					});
				}
			}
		});
    	buttons.add(updateButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
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
		type.setFocus(true);
	}

	@Override
	public int getTabIndex() {
		return type.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		type.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		type.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		type.setTabIndex(index);
	}

	private void checkRegistryDocument(String domainName, int domain, String user ,AonConfiguration config
			, AccountingRegistryPanelCallback callback
			, AccountingRegistryType accountingRegistryType
			, Integer id
			, FullDocument fulldocument
			, FlowPanel documentWarningContainer, Boolean documentChange) {
		if ( id == null
		 && fulldocument.getType() != null 
		 && fulldocument.getCountry() != null 
		 && AonStringUtils.isNotBlank( fulldocument.getDocument())) {
			commonService.getAccountingRegistries(domainName, domain, user,
					new AccountingRegistryParams()
						.setType(accountingRegistryType)
						.setId(id)
						.setDocument(fulldocument.getDocument())
						.setDocumentType(fulldocument.getType())
						.setDocumentCountry(fulldocument.getCountry())
					,new AsyncCallback<LinkedList<AccountingRegistry>>() {
				
				@Override
				public void onSuccess(LinkedList<AccountingRegistry> result) {
					int count = 0;
					documentWarningContainer.clear();
					Boolean sameType = false;
					for (AccountingRegistry reg : result) {
						String icon = AON.AON_CSS.aonLetterCGreenIcon();
						sameType = sameType || accountingRegistryType == reg.getType();
						if (reg.getType() == AccountingRegistryType.SUPPLIER) {
							icon = AON.AON_CSS.aonLetterPBlueIcon();
						} else if (reg.getType() == AccountingRegistryType.CREDITOR) {
							icon = AON.AON_CSS.aonLetterAOrangeIcon();
						}
						if (!AonNumberUtils.equals(reg.getId(),id))  {
							++count;
							Label label = new Label(AonStringUtils.abbreviate( AccountingRegistry.getFullDescription(reg), 60) );
							label.setStyleName(AON.AON_CSS.aonMarginLeft());
							label.addStyleName(AON.AON_CSS.aonFixedFont());
							label.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
							label.addStyleName(icon);
							label.addStyleName(AON.AON_CSS.aonFontMedium());
							label.addStyleName(AON.AON_CSS.aonClickableBlock());
							label.addClickHandler( new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									if (reg.getType() == accountingRegistryType) {
										callback.onAccept(reg);
									} else {
										ConfirmDialog cd = new ConfirmDialog();
										cd.confirm("Seleccionar", "Ha seleccionado un "+
												reg.getType().getDescription()	
											+". Continuar?",new ConfirmDialogCallback() {
											
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
							documentWarningContainer.add(label);
						}
					} 
					
					if(!sameType && documentChange) {
						AccountingRegistry  ar = newAccountingRegistry(domain, null)
								.setDocumentCountry(fulldocument.getCountry())
								.setDocumentType(fulldocument.getType())
								.setDocument(fulldocument.getDocument())
								.setType(accountingRegistryType);
						commonService.getAccountingRegistry(domainName, domain, user, 
								ar,  new AsyncCallback<AccountingRegistry>() {
									
							@Override
							public void onSuccess(AccountingRegistry result) {
								
								show(domainName, domain, user, config, result, callback);		
							}
									
							@Override
							public void onFailure(Throwable caught) {
										
							}
						});
					}
					if (count > 0) {
						Label errorLabel = new Label( AON.MSG.existingRegistryWarning(result.size()));
						errorLabel.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
						errorLabel.addStyleName(AON.AON_CSS.aonIconWarn());
						errorLabel.addStyleName(AON.AON_CSS.aonBold());
						documentWarningContainer.insert(errorLabel, 0 );
					} 
				}
				
				@Override
				public void onFailure(Throwable caught) {
					show(domainName,domain, user,config, newAccountingRegistry(domain,null),callback);
				}
			});
		}
	}
}
