package com.esferalia.aon.gwt.marketing.client.marketing.campaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class MarketingCompaignPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MarketingCompaignPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	private Map<Integer, MarketingCampaign> rowSCampaigns = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();

	private Integer deleteIterator = 0;
	
	private MarketingCompaignParams params;
	
	private boolean isTablet = false;
	private ArrayList<COLS> colTabletHidden = new ArrayList<>();
	
	private static enum COLS {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, DES(AON.MSG.description()					,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUD("Presupuesto"							,"7rem" 			,"")
		, BDA("P. Acumulado"						,"7rem" 			,"")
		, EXP("Gastos"								,"7rem" 			,"")
		, EXA("G. Acumulados"						,"7rem" 			,"")
		, STD("F. Inicio"							,"5rem" 			,"")
		, END("F. Fin"								,"5rem" 			,"")
		, TYP(AON.MSG.scope()						,"6rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, ACT("Estado"								,"4rem"  			,"")
		, BUT(AonStringUtils.EMPTY					,"3rem" 			,"")
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

	public MarketingCompaignPanel(MarketingCompaignParams params) {
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.isTablet = Window.getClientWidth() <= 980;
		initHiddenColumns();

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding", "0 1rem");
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
						searchData();
					}
				}
			}
		});
		
		onSearch();
		
	}
	
	private void initHiddenColumns() {
		colTabletHidden.clear();
		colTabletHidden.add(COLS.BDA);
		colTabletHidden.add(COLS.EXA);
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
	
	private void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight((Window.getClientHeight() - 200) + "px");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			if(col == COLS.CHK) {
				AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
				checkAllButton.addClickHandler(e -> {
					List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
					if (selectedItemList.size() == rowSCampaigns.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
						checkAllButton.addStyleName(AON.CSS.aonIconCheck());
						checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconCheck());
							check.removeStyleName(AON.CSS.aonIconChecked());
						});
						onDeleteEnable(false);
					} else {
						checkAllButton.addStyleName(AON.CSS.aonIconChecked());
						checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconChecked());
							check.removeStyleName(AON.CSS.aonIconCheck());
						});
						onDeleteEnable(true);
					}
				});
				
				tab.addHeader(checkAllButton, col.getColWidth());
			} else if(!isTablet || !colTabletHidden.contains(col))
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(marketingCampaigns -> {
			boolean something = false;
			
			for(MarketingCampaign marketingCampaign : marketingCampaigns) {
				something = true;
				paintRow(marketingCampaign);
			}
			
			if (marketingCampaigns.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + marketingCampaigns.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}
	
	private void paintRow(MarketingCampaign marketingCampaign) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton button;
		button = new AonTableButton("Borrar agente comercial", AON.CSS.aonIconDelete());
		if(!isTablet) button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Campa\u00f1a",
						new HTML("Se va a proceder a eliminar la campa\u00f1a <b>" + marketingCampaign.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(marketingCampaign);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onMarketingCampaignOpen(marketingCampaign), ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
		checkButton.addClickHandler(e -> {
			e.stopPropagation();
			if (AonStringUtils.containsIgnoreCase(checkButton.getStyleName(), AON.CSS.aonIconChecked())) {
				checkButton.addStyleName(AON.CSS.aonIconCheck());
				checkButton.removeStyleName(AON.CSS.aonIconChecked());
			} else {
				checkButton.addStyleName(AON.CSS.aonIconChecked());
				checkButton.removeStyleName(AON.CSS.aonIconCheck());
			}
			List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
			onDeleteEnable(!selectedItemList.isEmpty());
		});
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		Label name = new Label(marketingCampaign.getDescription());
		name.setTitle(marketingCampaign.getDescription());
		tab.addInlineStyle(name, COLS.DES.getCellStyleClass());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		tab.addRow(row, new Label(AON.FMT.format(marketingCampaign.getBudget()) + " \u20ac"), COLS.BUD.getColWidth());
		if(!isTablet) tab.addRow(row, new Label(AON.FMT.format(marketingCampaign.getBudget() + marketingCampaign.getActions().stream().mapToDouble(action -> action.getBudget()).sum()) + " \u20ac"), COLS.BDA.getColWidth());
		
		tab.addRow(row, new Label(AON.FMT.format(marketingCampaign.getExpense()) + " \u20ac"), COLS.EXP.getColWidth());
		if(!isTablet) tab.addRow(row, new Label(AON.FMT.format(marketingCampaign.getExpense() + marketingCampaign.getActions().stream().mapToDouble(action -> action.getExpense()).sum()) + " \u20ac"), COLS.EXA.getColWidth());
		
		tab.addRow(row, new Label(marketingCampaign.getStartDate() == null ? "" : formatDate.format(marketingCampaign.getStartDate())), COLS.STD.getColWidth());
		tab.addRow(row, new Label(marketingCampaign.getEndDate() == null ? "" : formatDate.format(marketingCampaign.getEndDate())), COLS.END.getColWidth());
		
		Label type = new Label(marketingCampaign.getScope() == null ? null : marketingCampaign.getScope().getDescription());
		type.setTitle(marketingCampaign.getScope() == null ? null : marketingCampaign.getScope().getDescription());
		tab.addInlineStyle(type, COLS.TYP.getCellStyleClass());
		tab.addRow(row, type, COLS.TYP.getColWidth());
		
		tab.addRow(row, new Label(marketingCampaign.isActive() ? "Activo" : "Inactivo"), COLS.ACT.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		rowSCampaigns.put(marketingCampaign.getId(), marketingCampaign);
		selectedItems.put(marketingCampaign.getId(), checkButton);
	}
	
	public void deleteCampaigns() {
		List<Integer> selectedCampaingsList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		
		AonDialog dialog = new AonDialog("Eliminaci\u00f3n Agente Comercial",
				new HTML(selectedCampaingsList.size() == selectedItems.entrySet().size() ? "Se va a proceder a eliminar <b>TODAS</b> las campa\u00f1as de marketing.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"
						: "Se va a proceder a eliminar <b>" + selectedCampaingsList.size() + " campa\u00f1as de marketing</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				onDeleteEnable(true);
			}

			@Override
			public void onAccept() {
				onShowLoadingMessage("Eliminando agentes comerciales seleccionados...");
				deleteIterator = 0;
				delete(selectedCampaingsList);
			}
		});
	}
	
	private void delete(List<Integer> selectedCampaingsList) {
		if(deleteIterator == selectedCampaingsList.size()) {
			resetSearchOffset();
			onSearch();
			onShowSuccessMessage("Agentes comerciales eliminados correctamente");
		} else {
			COMMON_SERVICE.deleteMarketingCampaign(params.getDomainName(), params.getDomain(), params.getUser(), selectedCampaingsList.get(deleteIterator), new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					deleteIterator++;
					delete(selectedCampaingsList);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error borrado: " + caught.getMessage());
				}
			});
		}
	}
	
	private void getList(Consumer<List<MarketingCampaign>> success) {
		COMMON_SERVICE.getMarketingCampaigns(params, new AsyncCallback<List<MarketingCampaign>>() {
			
			@Override
			public void onSuccess(List<MarketingCampaign> marketingCampaigns) {
				success.accept(marketingCampaigns);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(MarketingCampaign marketingCampaign) {
		COMMON_SERVICE.deleteMarketingCampaign(params.getDomainName(), params.getDomain(), params.getUser(), marketingCampaign.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onMarketingCampaignOpen(MarketingCampaign marketingCampaign);
	protected abstract void onDeleteEnable(boolean enabled);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onShowSuccessMessage(String successMessage);
	
}

