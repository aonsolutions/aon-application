package com.esferalia.aon.gwt.marketing.client.marketing.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
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

public abstract class MarketingActionPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MarketingActionPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private MarketingActionParams params;
	
	private boolean isTablet = false;
	private ArrayList<COLS> colTabletHidden = new ArrayList<>();
	
	private static enum COLS {
		  DES(AON.MSG.description()					,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CAN("Canal"								,"7rem" 			,"")
		, TYP(AON.MSG.type()						,"4rem" 			,"")
		, BUD("Presupuesto"							,"6rem" 			,"")
		, EXP("Gastos"								,"6rem" 			,"")
		, STD("F. Inicio"							,"5rem" 			,"")
		, END("F. Fin"								,"5rem" 			,"")
		, COM("A. Comercial"						,"6rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SUR("Cuestionario"						,"6rem" 			,"")
		, TGT("C. Potenciales"						,"6rem" 			,"")
		, BUT(AonStringUtils.EMPTY					,"2rem" 			,"")
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

	public MarketingActionPanel(MarketingActionParams params) {
		
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
		colTabletHidden.add(COLS.COM);
		colTabletHidden.add(COLS.SUR);
		colTabletHidden.add(COLS.TGT);
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
			if(!isTablet || !colTabletHidden.contains(col))
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(marketingActions -> {
			boolean something = false;
			
			for(MarketingAction marketingAction : marketingActions) {
				something = true;
				paintRow(marketingAction);
			}
			
			if (marketingActions.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + marketingActions.size() - 1);
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

	private void paintRow(MarketingAction marketingAction) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton button = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		if(!isTablet) button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Acci\u00f3n",
						new HTML("Se va a proceder a eliminar la acci\u00f3n <b>" + marketingAction.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(marketingAction);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onMarketingActionOpen(marketingAction), ClickEvent.getType());
		
		Label name = new Label(marketingAction.getDescription());
		name.setTitle(marketingAction.getDescription());
		tab.addInlineStyle(name, COLS.DES.getCellStyleClass());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		tab.addRow(row, new Label(null == marketingAction.getMediaType() ? "" : marketingAction.getMediaType().getDescription()), COLS.CAN.getColWidth());
		tab.addRow(row, new Label(null == marketingAction.getMediaType() ? "" : getActivonType(marketingAction.getMediaType().getValue())), COLS.TYP.getColWidth());
		
		tab.addRow(row, new Label(AON.FMT.format(marketingAction.getBudget()) + " \u20ac"), COLS.BUD.getColWidth());
		tab.addRow(row, new Label(AON.FMT.format(marketingAction.getExpense()) + " \u20ac"), COLS.EXP.getColWidth());
		
		tab.addRow(row, new Label(marketingAction.getStartDate() == null ? "" : formatDate.format(marketingAction.getStartDate())), COLS.STD.getColWidth());
		tab.addRow(row, new Label(marketingAction.getEndDate() == null ? "" : formatDate.format(marketingAction.getEndDate())), COLS.END.getColWidth());
		
		if(!isTablet) {
			Label taskHolder = new Label(marketingAction.getTaskHolder() == null ? "" : marketingAction.getTaskHolder().getName());
			taskHolder.setTitle(marketingAction.getTaskHolder() == null ? "" : marketingAction.getTaskHolder().getName());
			tab.addInlineStyle(taskHolder, COLS.COM.getCellStyleClass());
			tab.addRow(row, taskHolder, COLS.COM.getColWidth());
			
			tab.addRow(row, new Label(marketingAction.getSurvey() == null ? "" : marketingAction.getSurvey().getDescription()), COLS.SUR.getColWidth());
			tab.addRow(row, new Label(marketingAction.getTargets() == null ? "0" : marketingAction.getTargets().size() + ""), COLS.TGT.getColWidth());
		}
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}

	private String getActivonType(Integer type) {
		return type > 5 ? "Incoming" : "Outcoming";
	}
	
	private void getList(Consumer<List<MarketingAction>> success) {
		COMMON_SERVICE.getMarketingActions(params, new AsyncCallback<List<MarketingAction>>() {
			
			@Override
			public void onSuccess(List<MarketingAction> marketingActions) {
				success.accept(marketingActions);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(MarketingAction marketingAction) {
		COMMON_SERVICE.deleteMarketingAction(params.getDomainName(), params.getDomain(), params.getUser(), marketingAction.getId(), new AsyncCallback<Void>() {
			
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
	protected abstract void onMarketingActionOpen(MarketingAction marketingAction);
	
}

