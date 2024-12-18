package com.esferalia.aon.gwt.marketing.client.marketing.action;

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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetMassiveParams;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class MarketingActionTargetMassivePanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MarketingActionTargetMassivePanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	private Map<Integer, Registry> rowRegistries = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();

	private MarketingActionTargetMassiveParams params;
	
	private boolean isTablet = false;
	private ArrayList<COLS> colTabletHidden = new ArrayList<>();
	
	private static enum COLS {
		CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, DES("Cliente Potencial"					,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SCP(AON.MSG.scope()						,"6rem" 			,"")
		, ENT("Entidad"								,"5rem" 			,"")
		, PRO("Propaganda"							,"5rem" 			,"")
		, EXP("Expediente"							,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, ACT("Actividad"							,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA(AON.MSG.status()						,"4rem" 			,"")
		, CUS("Cliente"								,"3rem" 			,"")
		, ACC("Acci\u00f3n"							,"8rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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

	public MarketingActionTargetMassivePanel(MarketingActionTargetMassiveParams params) {
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingBottom());
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.isTablet = Window.getClientWidth() <= 980;
		
		initHiddenColumns();

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
						searchData();
					}
				}
			}
		});
		
		onSearch();
		
	}
	
	private void initHiddenColumns() {
		colTabletHidden.clear();
		colTabletHidden.add(COLS.ENT);
//		colTabletHidden.add(COLS.SUR);
		colTabletHidden.add(COLS.CUS);
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
		
		paintHeader();
		container.setWidget(tab);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			if(!isTablet || !colTabletHidden.contains(col)) {
				if(col == COLS.CHK) {
					AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
					checkAllButton.addClickHandler(e -> {
						List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
						if (selectedItemList.size() == rowRegistries.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
							checkAllButton.addStyleName(AON.CSS.aonIconCheck());
							checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconCheck());
								check.removeStyleName(AON.CSS.aonIconChecked());
							});
							onAcceptEnable(false);
						} else {
							checkAllButton.addStyleName(AON.CSS.aonIconChecked());
							checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconChecked());
								check.removeStyleName(AON.CSS.aonIconCheck());
							});
							onAcceptEnable(true);
						}
					});
					
					tab.addHeader(checkAllButton, col.getColWidth());
				} else
					tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
			}
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(targets -> {
			boolean something = false;
			
			for(MarketingActionTarget target : targets) {
				something = true;
				paintRow(target);
			}
			
			if (targets.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + targets.size() - 1);
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

	private void paintRow(MarketingActionTarget marketingActionTarget) {
		HTMLPanel row = tab.createRow();
		
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
			onAcceptEnable(!selectedItemList.isEmpty());
		});
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		Label name = new Label(marketingActionTarget.getName());
		name.setTitle(marketingActionTarget.getName());
		tab.addInlineStyle(name, COLS.DES.getCellStyleClass());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		tab.addRow(row, new Label(null == marketingActionTarget.getScope() ? "" : marketingActionTarget.getScope().getDescription()), COLS.SCP.getColWidth());
		
		if(!isTablet)
			tab.addRow(row, new Label(marketingActionTarget.isLegalPerson() ? "Pers. Fisica" : "Pers. Juridica"), COLS.ENT.getColWidth());
		
		tab.addRow(row, new Label(null == marketingActionTarget.getAdvertising() ? "" : marketingActionTarget.getAdvertising().getDescription()), COLS.PRO.getColWidth());
		
		Label project = new Label(null == marketingActionTarget.getProject() ? "" : marketingActionTarget.getProject().getName());
		project.setTitle(null == marketingActionTarget.getProject() ? "" : marketingActionTarget.getProject().getName());
		tab.addInlineStyle(project, COLS.EXP.getCellStyleClass());
		tab.addRow(row, project, COLS.EXP.getColWidth());
		
		Label projectActivity = new Label(null == marketingActionTarget.getProjectActivity() ? "" : marketingActionTarget.getProjectActivity().getActivityType().getDescription());
		projectActivity.setTitle(null == marketingActionTarget.getProjectActivity() ? "" : marketingActionTarget.getProjectActivity().getActivityType().getDescription());
		tab.addInlineStyle(projectActivity, COLS.ACT.getCellStyleClass());
		tab.addRow(row, projectActivity, COLS.ACT.getColWidth());
		
		tab.addRow(row, new Label(null == marketingActionTarget.getStatus() ? "" : marketingActionTarget.getStatus().getDescription()), COLS.STA.getColWidth());
		
		if(!isTablet)
			tab.addRow(row, new Label(marketingActionTarget.isCustomer() ? "Si" : "No"), COLS.CUS.getColWidth());
		
		Label action = new Label(null == marketingActionTarget.getMarketingAction() ? "" : marketingActionTarget.getMarketingAction().getDescription());
		action.setTitle(null == marketingActionTarget.getMarketingAction() ? "" : marketingActionTarget.getMarketingAction().getDescription());
		tab.addInlineStyle(action, COLS.ACC.getCellStyleClass());
		tab.addRow(row, action, COLS.ACC.getColWidth());
		
		rowRegistries.put(marketingActionTarget.getId(), marketingActionTarget);
		selectedItems.put(marketingActionTarget.getId(), checkButton);
		
	}
	
	private void getList(Consumer<List<MarketingActionTarget>> success) {
		COMMON_SERVICE.getMarketingActionTargets(params, new AsyncCallback<List<MarketingActionTarget>>() {
			
			@Override
			public void onSuccess(List<MarketingActionTarget> marketingActionTargets) {
				success.accept(marketingActionTargets);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
	}
	
	public boolean isAllSelected() {
		List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
		return selectedItemList.size() == rowRegistries.size();
	}
	
	public List<Integer> getSelectedTargets() {
		List<Integer> selectedTargetsList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		return selectedTargetsList;
	}
	
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onAcceptEnable(boolean enable);

}

