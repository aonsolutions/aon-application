package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public abstract class SellerWorkloadEntryPanel extends DeckLayoutPanel {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private final String EMPTY_STRING = "";
	
	private DockLayoutPanel sellerEntryPanel;

	private AonCustomToolbar toolbar;
	private Integer position = -1;
	private AonToolbarButton previusSeller;
	private Label sellerIteration;
	private AonToolbarButton nextSeller;
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private SellerModuleOptions options;
	private SellerWorkload sellerWorkload;
	
	public SellerWorkloadEntryPanel(SellerModuleOptions options) {
		
		this.options = options;
		initializeCommonService();
		
		sellerEntryPanel = new DockLayoutPanel(Unit.PX);
		add(sellerEntryPanel);
		showWidget(sellerEntryPanel);
		
		createToolbar();
	}
	
	private void createToolbar() {
		toolbar = new AonCustomToolbar( "Carga Trabajo (AC)" );
		toolbar.showBackButton();
		toolbar.getBackButton().addClickHandler(e -> onBackClick());
		
		previusSeller = new AonToolbarButton("Anterior Agente Comercial", AON.CSS.aonIconLeft());
		previusSeller.setEnabled(position > 0);
		previusSeller.addClickHandler(e -> {
			position = position - 1;
			getNextSeller(position, nextSeller -> onSellerWorkloadSelectionChange(nextSeller, position));
		});
		toolbar.addToolbarButton(previusSeller);
		
		getSellerWorkloadListCount(count -> {
			sellerIteration = new Label((null == sellerWorkload ? "ND" : (position + 1)) + " / " + count);
			toolbar.addToolbarButton(sellerIteration);
			
			nextSeller = new AonToolbarButton("Siguiente Agente Comercial", AON.CSS.aonIconRight());
			nextSeller.setEnabled(position < (count - 1));
			nextSeller.addClickHandler(e -> {
				position = position + 1;
				getNextSeller(position, nextSeller -> onSellerWorkloadSelectionChange(nextSeller, position));
			});
			toolbar.addToolbarButton(nextSeller);
		});
		
		sellerEntryPanel.addNorth(toolbar, 50);
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
			
			sellerEntryPanel.clear();
			
			createToolbar();
			toolbar.setToolbarTitle("Carga Trabajo / ", sellerWorkload.getName());
			
			container = new HTMLPanel(EMPTY_STRING);
			container.addStyleName(AON.CSS.aonFlexColumn());
			container.add(messagePanel);
			
			sellerEntryPanel.add(container);
			
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
