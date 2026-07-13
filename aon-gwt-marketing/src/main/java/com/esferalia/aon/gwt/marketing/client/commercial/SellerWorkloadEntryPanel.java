package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public abstract class SellerWorkloadEntryPanel extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private Integer position = 0;
	private AonToolbarButton previusSeller;
	private Label sellerIteration;
	private AonToolbarButton nextSeller;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimpleLayoutPanel centerPanel;
	
	private AonSearchPanelButton cleanButton;
	
	private AonCustomMultiSelectBox customerStatus = new AonCustomMultiSelectBox("Estado Cliente");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SellerModuleOptions options;
	
	private SellerWorkloadFeePanel sellerWorkloadFeePanel;
	
	private SellerWorkload sellerWorkload;
	
	// ------------------------------------------------- Constructor
	
	public SellerWorkloadEntryPanel(SellerModuleOptions options) {
		super("Carga Trabajo (AC)");
		
		this.options = options;
		initializeCommonService();
		
		addButtonsToolbar();
		getSearchTextBox().addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch( getSellerWorkloadListParams() );
			} else if(AonStringUtils.isBlank(value)) {
				sellerWorkloadFeePanel.resetSearchOffset();
				onSearch( getSellerWorkloadListParams() );
			}
		});
		setSearchPlaceholder("Filtrar por cliente, producto...");
		
		// Customer Status
		Set<String> customerStatusoptions = new LinkedHashSet<String>();
		customerStatusoptions.add("Activo");
		customerStatusoptions.add("Inactivo");
		customerStatusoptions.add("Bloqueado");
		customerStatus.setOptions(customerStatusoptions);
		
		customerStatus.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
            	if(customerStatus.getSelectedOptions().isEmpty()) {
            		Set<String> selectedOptions = new LinkedHashSet<String>();
            		selectedOptions.add("Activo");
            		customerStatus.setSelectedOptions(selectedOptions);
            	}
            	
            	onSearch( getSellerWorkloadListParams() );
            }
        });
		
		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		customerStatus.setSelectedOptions(selectedOptions);
		
		addFilterWidget(customerStatus);
		
		sort.addItem("Cliente", "customer");
		sort.addItem("Producto", "product");
		sort.getListBox().addChangeHandler(event -> onSearch( getSellerWorkloadListParams() ));
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch( getSellerWorkloadListParams() ));
		
		addSortWidget(sort);
		addSortWidget(asc);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(centerPanel);
		
		add(container);
	}
	
	@Override
	protected void onClearFilter() {
		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		customerStatus.setSelectedOptions(selectedOptions);
	}
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton("Listado", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		addToolbarButton(backButton);
		
		AonToolbarButton downloadExcel = new AonToolbarButton("Exportar Excel", AON.CSS.aonIconDownload());
		downloadExcel.addClickHandler(e -> {
			exportFees();
		});
		addToolbarButton(downloadExcel);
		
		previusSeller = new AonToolbarButton("Anterior Agente Comercial", AON.CSS.aonIconLeft());
		previusSeller.setEnabled(position > 0);
		previusSeller.addClickHandler(e -> {
			position = position - 1;
			getNextSeller(position, nextSeller -> onSellerWorkloadSelectionChange(nextSeller, position));
		});
		addToolbarButton(previusSeller);
		
		getSellerWorkloadListCount(count -> {
			sellerIteration = new Label((position + 1) + " / " + count);
			addToolbarButton(sellerIteration);
			
			nextSeller = new AonToolbarButton("Siguiente Agente Comercial", AON.CSS.aonIconRight());
			nextSeller.setEnabled(position < (count - 1));
			nextSeller.addClickHandler(e -> {
				position = position + 1;
				getNextSeller(position, nextSeller -> onSellerWorkloadSelectionChange(nextSeller, position));
			});
			addToolbarButton(nextSeller);
		});
		
	}
	
	private void exportFees() {
		JSONObject json = new JSONObject();
		
		SellerWorkloadParams sellerWorkloadListParams = getWidgetParams( getSellerWorkloadListParams() );
		
		json.put("period", new JSONString(sellerWorkloadListParams.getPeriod().toString()));
		
		if(null != sellerWorkloadListParams.getTaskHolder())
			json.put("taskHolder", new JSONString(sellerWorkloadListParams.getTaskHolder().toString()));
		else
			json.put("seller", new JSONString(sellerWorkloadListParams.getSeller().toString()));
		
		json.put("description", new JSONString(sellerWorkloadListParams.getDescription()));
		json.put("isSellerWorkload", new JSONString("true"));
		
		json.put("customerActive", new JSONString( Boolean.toString( customerStatus.getSelectedOptions().contains("Activo") )));
		json.put("customerInactive", new JSONString( Boolean.toString( customerStatus.getSelectedOptions().contains("Inactivo") )));
		json.put("customerBlocked", new JSONString( Boolean.toString( customerStatus.getSelectedOptions().contains("Bloqueado") )));
		
		String fileDownloadURL = GWT.getModuleBaseURL()+ "ms/gwt_download_fee/"
            	+ "?filter=" + btoa(json.toString())
            	+ "&domain_name=" + options.getDomainName()
            	+ "&domain_id=" + options.getDomain()
				+ "&username="+ options.getUser();
		
		Window.open( fileDownloadURL, "_blank",null);
		
	}
	
	private native String btoa(String str) /*-{
	    return btoa(str);
	}-*/;
	
	public void onSearch( SellerWorkloadParams params ) {
		
		SellerWorkloadParams parseParams = getWidgetParams(params);
		
		sellerWorkloadFeePanel = new SellerWorkloadFeePanel(parseParams) {

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
		
		};
			
		centerPanel.setWidget(sellerWorkloadFeePanel);
	}
	
	public SellerWorkloadParams getWidgetParams( SellerWorkloadParams params ) {
		SellerWorkloadParams sellerWorkloadParams = new SellerWorkloadParams();
		sellerWorkloadParams.setPeriod(params.getPeriod());
		sellerWorkloadParams.setPeriodStart(params.getPeriodStart());
		sellerWorkloadParams.setPeriodEnd(params.getPeriodEnd());
		sellerWorkloadParams.setCustomers(params.getCustomers());
		
		sellerWorkloadParams.setCustomerActive(customerStatus.getSelectedOptions().contains("Activo"));
		sellerWorkloadParams.setCustomerInactive(customerStatus.getSelectedOptions().contains("Inactivo"));
		sellerWorkloadParams.setCustomerBlocked(customerStatus.getSelectedOptions().contains("Bloqueado"));
		
		if(sellerWorkload.getProjectHolder() != null)
			sellerWorkloadParams.setTaskHolder(sellerWorkload.getProjectHolder().getTaskHolder().getId());
		else
			sellerWorkloadParams.setSeller(sellerWorkload.getId());
		
		sellerWorkloadParams.setDomainName(params.getDomainName())
			.setDomain(params.getDomain())
			.setUser(params.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		return sellerWorkloadParams;
	}
	
	public SellerWorkloadParams getSellerListParams() {
		return getWidgetParams( getSellerWorkloadListParams() );
	}

	private void getNextSeller(Integer nextPos, Consumer<SellerWorkload> sellerLoad) {
		SellerWorkloadParams params = getSellerWorkloadListParams();
		params.setOffset(nextPos);
		params.setLimit(1);
		
		commonService.getSellersWorkload(params, new AsyncCallback<List<SellerWorkload>>() {
			
			@Override
			public void onSuccess(List<SellerWorkload> sellersWorkload) {
				sellerLoad.accept(sellersWorkload.get(0));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

	private void hideNavegationOptions() {
		previusSeller.setVisible(false);
		sellerIteration.setVisible(false);
		nextSeller.setVisible(false);
	}

	private void showNavegationOptions() {
		previusSeller.setVisible(true);
		sellerIteration.setVisible(true);
		nextSeller.setVisible(true);
	}

	public void setSellerWorkload(SellerWorkloadParams params, SellerWorkload sellerWorkload, Integer sellectPos) {
		updateCustomerStatusByParams(params);
		
		setSellerWorkload(params, sellerWorkload, finish -> {
			if(position >= 0) showNavegationOptions();
			else hideNavegationOptions();
		});
	}
	
	private void updateCustomerStatusByParams(SellerWorkloadParams params) {
		Set<String> selectedOptions = new LinkedHashSet<String>();
		if(params.getCustomerActive()) selectedOptions.add("Activo");
		if(params.getCustomerInactive()) selectedOptions.add("Inactivo");
		if(params.getCustomerBlocked()) selectedOptions.add("Bloqueado");
		customerStatus.setSelectedOptions(selectedOptions);
	}

	public void setSellerWorkload(SellerWorkloadParams params, SellerWorkload sellerWorkload, Consumer<Void> finish) {
		this.sellerWorkload = sellerWorkload;
		
		setToolbarTitle("Carga Trabajo / " + (params.getByProject() ? sellerWorkload.getProjectHolder().getTaskHolder().getName() : sellerWorkload.getName()));
		
		updatePosition(f -> {
			onSearch(params);
			finish.accept(null);
		});
	}
	
	private void updatePosition(Consumer<Integer> finish) {
		getSellerWorkloadListCount(count -> {
			position = getSellerWorkloadPosition(sellerWorkload.getId());
			sellerIteration.setText((position + 1) + " / " + count);
			
			previusSeller.setEnabled(position > 0);
			nextSeller.setEnabled(position < (count - 1));
			
			finish.accept(count);
		});
	}
	
	protected abstract void onBackClick();
	
	protected abstract void getSellerWorkloadListCount(Consumer<Integer> finish);
	protected abstract SellerWorkloadParams getSellerWorkloadListParams();
	
	protected abstract Integer getSellerWorkloadPosition(Integer sellerWorkloadId);
	protected abstract void onSellerWorkloadSelectionChange(SellerWorkload sellerWorkload, Integer position);

}
