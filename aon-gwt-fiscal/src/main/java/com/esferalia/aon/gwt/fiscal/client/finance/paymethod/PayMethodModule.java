package com.esferalia.aon.gwt.fiscal.client.finance.paymethod;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonPayMethodGroupPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonPayMethodGroupPanel.AonPayMethodGroupPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonPayMethodPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonPayMethodPanel.AonPayMethodPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceModuleOptions;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.PayMethodParams;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class PayMethodModule extends MainEntryPoint {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	private AonCustomDockLayout dockLayoutPanel;
	
	private AonToolbarButton groupButton;
	
	private SimpleLayoutPanel centerPanel;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private FinanceModuleOptions options;
	private PayMethodPanel payMethodPanel;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		FinanceModuleOptions options = new FinanceModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	private void initializeCommonService() {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}
	
	public void onModuleLoad( final FinanceModuleOptions opt ) {
		AON.ensureInjected();
		
		initializeCommonService();
		this.options = opt;
		
		// AonCustomDockLayout
		dockLayoutPanel = new AonCustomDockLayout("Formas de pago", true) {
			
			@Override
			protected void onClearFilter() {
				getSearchTextBox().setValue(null, false);
				type.getListBox().setSelectedIndex(0);
				
				payMethodPanel.resetSearchOffset();
				
				onSearch();
			}
		};
		opt.getParentWidget().add(dockLayoutPanel);
		
		// Toolbar & SearchPanel
		initializeToolbarSearch();
		
		// LoadModule
		if ( opt.getConfiguration() == null)
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					opt.setConfiguration(result);
					loadModule();					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
				}
			});
		else loadModule();
		
	}
	
	private void initializeToolbarSearch() {
		addButtonsToolbar();
		dockLayoutPanel.setSearchPlaceholder("Filtre por metodo de pago...");
		dockLayoutPanel.addKeyUpHandler(e -> {
			String value = dockLayoutPanel.getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		
		type.addItem( "Todos", "");	
		for (PayMethodType pmt : PayMethodType.values())
			type.addItem( pmt.getDescription(), pmt.value() + "");	
		type.getListBox().setSelectedIndex(0);
		type.getListBox().addChangeHandler(event -> onSearch());
		dockLayoutPanel.addFilterWidget(type);
		
		sort.addItem("Descripci\u00f3n", "name");
		sort.addItem("Tipo", "type");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		dockLayoutPanel.addSortWidget(sort);
		dockLayoutPanel.addSortWidget(asc);
	}

	private void addButtonsToolbar() {
		AonToolbarButton addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.addClickHandler(e -> showPaymethodDialog(new PayMethod()));
		dockLayoutPanel.addToolbarButton(addButton);
		
		groupButton = new AonToolbarButton( "Agrupar", AON.CSS.aonIconMoveGroup() );
		groupButton.addClickHandler(e -> showPaymethodGroupDialog());
		groupButton.setEnabled(false);
		dockLayoutPanel.addToolbarButton(groupButton);
	}

	private void showPaymethodDialog(PayMethod paymethod) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nueva Forma de Pago" );
		final AonPayMethodPanel marketingCampaignPanel = new AonPayMethodPanel( options.getDomainName(), options.getDomain(), options.getUser(), paymethod, new AonPayMethodPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(PayMethod paymethodDB) {
				dialog.hide();
				if(paymethod.getId() == null) AonMessagePanel.showSuccess(messagePanel, "Forma de pago creada correctamente");
				else AonMessagePanel.showSuccess(messagePanel, "Forma de pago actualizada correctamente");
				onSearch();
			}
		
		});
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoadedCB(new AonCustomDialogCallback() {
			
			@Override
			public void onEnd() {
				marketingCampaignPanel.setNameFocus();
			}
		});
	}
	
	private void showPaymethodGroupDialog() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Agrupar Forma de Pago" );
		new AonPayMethodGroupPanel( options.getDomainName(), options.getDomain(), options.getUser(), payMethodPanel.getSelectedPaymethods(), new AonPayMethodGroupPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				AonMessagePanel.showSuccess(messagePanel, "Se han agrupado " + payMethodPanel.getSelectedPaymethods().size() + " formas de pago correctamente");
				onSearch();
			}

			@Override
			public void endView(AonPayMethodGroupPanel panel) {
				dialog.add(panel);
				dialog.showLoaded();
			}
		
		});
		
	}
	
	public void onSearch() {
		PayMethodParams params = getWidgetParams();
		payMethodPanel = new PayMethodPanel(params) {

			@Override
			protected void onPaymethodOpen(PayMethod payMethod) {
				showPaymethodDialog(payMethod);
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onShowSuccessMessage(String successMessage) {
				AonMessagePanel.showSuccess(messagePanel, successMessage);
			}

			@Override
			protected void onShowLoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}

			@Override
			protected void onGroupEnable(boolean enabled) {
				groupButton.setEnabled(enabled);
			}
		
		};
			
		centerPanel.setWidget(payMethodPanel);
	}
	
	public PayMethodParams getWidgetParams() {
		return new PayMethodParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(dockLayoutPanel.getSearchTextBox().getValue())
			.setType(AonStringUtils.isBlank(type.getValue()) ? null : Byte.parseByte(type.getValue()))
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
	}

	private void loadModule() {
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(centerPanel);
		
		dockLayoutPanel.add(container);
		onSearch();
	}
	
}		
