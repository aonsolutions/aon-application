package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
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
	
	private Integer position = -1;
	private AonToolbarButton previusSeller;
	private Label sellerIteration;
	private AonToolbarButton nextSeller;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimpleLayoutPanel centerPanel;
	
	private AonSearchPanelButton cleanButton;
	
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
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch( options );
			} else if(AonStringUtils.isBlank(value)) {
				onSearch( options );
			}
		});
		setSearchPlaceholder("Filtrar por cliente, producto...");
		
		addFilterMenu();
		
		cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			getSearchTextBox().setValue(null, false);
			
			sellerWorkloadFeePanel.resetSearchOffset();
			
			onSearch( options );
		});
		
		addFilterToolbarButton(cleanButton);
		
		addSortMenu();
		
		sort.addItem("Nombre", "name");
		sort.addItem("Alias", "alias");
		sort.addItem("Documento", "document");
		sort.getListBox().addChangeHandler(event -> onSearch( options ));
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch( options ));
		
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
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton("Listado", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		addToolbarButton(backButton);
		
		AonToolbarButton downloadExcel = new AonToolbarButton("Exportar Excel", AON.CSS.aonIconDownload());
		downloadExcel.addClickHandler(e -> {
			Window.alert("Exportar cargas de trabajo a Excel");
			
//			String fileDownloadURL = 
//					"/ms/api/seller-excel/" + 
//					"?domainId=" + options.getDomain() + 
//					"&domainName=" + options.getDomainName() + 
//					"&login=" + options.getUser() +
//					"&description=" + getSearchTextBox().getValue() +
//					"&scope=" + scope.getValue() +
//					"&active=" + active.getValue() +
//					"&orderBy=" + sort.getValue() +
//					"&asc=" + asc.getValue()
//					;
//			
//			Window.open(fileDownloadURL, "_blank", null);
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
			sellerIteration = new Label((null == sellerWorkload ? "ND" : (position + 1)) + " / " + count);
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
	
	public void onSearch( SellerModuleOptions options ) {
		SellerWorkloadParams params = getWidgetParams( options );
		sellerWorkloadFeePanel = new SellerWorkloadFeePanel(params) {

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
	
	public SellerWorkloadParams getWidgetParams( SellerModuleOptions options) {
		SellerWorkloadParams sellerWorkloadParams = new SellerWorkloadParams();
		
		sellerWorkloadParams.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		return sellerWorkloadParams;
	}
	
	public SellerWorkloadParams getSellerListParams() {
		return getWidgetParams(options);
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

	public void setSellerWorkload(SellerWorkload sellerWorkload, Integer sellectPos) {
		this.position = sellectPos;
		setSellerWorkload(sellerWorkload, finish -> {
			if(position >= 0) showNavegationOptions();
			else hideNavegationOptions();
		});
	}
	
	public void setSellerWorkload(SellerWorkload sellerWorkload, Consumer<Void> finish) {
		getSellerWorkload(sellerWorkload.getId(), dbSeller -> {
			this.sellerWorkload = sellerWorkload;
			
			setToolbarTitle("Carga Trabajo / " + sellerWorkload.getName());
			
			onSearch(options);
			
			finish.accept(null);
		});
	}
	
	private void getSellerWorkload(Integer sellerId, Consumer<Seller> success) {
		commonService.getSeller(options.getDomainName(), options.getDomain(), options.getUser(), sellerId, new AsyncCallback<Seller>() {
			
			@Override
			public void onSuccess(Seller seller) {
				success.accept(seller);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	protected abstract void onBackClick();
	
	protected abstract void getSellerWorkloadListCount(Consumer<Integer> finish);
	protected abstract SellerWorkloadParams getSellerWorkloadListParams();
	
	protected abstract void onSellerWorkloadSelectionChange(SellerWorkload sellerWorkload, Integer position);

}
