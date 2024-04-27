package com.esferalia.aon.gwt.marketing.client.marketing.action;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class MarketingActionTargetPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MarketingActionTargetPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt row = new MutableInt(0);
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private SimplePanel container;
	private FlexTable tab;
	private int lastScrollPos = 0;
	
	private MarketingActionTargetParams params;
	
	private static enum COLS {
		DES(AON.MSG.description()					,"auto"  ,null)
		, COM(AON.MSG.comments()					,"250px" ,null)
		, STA(AON.MSG.status()						,"150px" ,null)
		, BUT(AonStringUtils.EMPTY					,"50px"  ,null)
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

	public MarketingActionTargetPanel(MarketingActionTargetParams params) {
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingBottom());
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;

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
		tab = new FlexTable();
		tab.addStyleName(AON.CSS.aonGrid());
		
		paintHeader();
		container.setWidget(tab);
		row.setValue(1);
		searchData();
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
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(marketingActionTargets -> {
			boolean something = false;
			
			for(MarketingActionTarget marketingActionTarget : marketingActionTargets) {
				something = true;
				paintRow(marketingActionTarget);
			}
			
			if (marketingActionTargets.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + marketingActionTargets.size() - 1);
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
		final int r = row.getValue();
		paintRow(r, marketingActionTarget); 
		row.increment();
	}
	
	private void paintRow(final int r, MarketingActionTarget marketingActionTarget) {
		int col = 0;
		boolean myMarketingActionTarget =  marketingActionTarget == null || AonNumberUtils.equals(marketingActionTarget.getDomain().getId() , params.getDomain()); 
		if (myMarketingActionTarget) {
			paintActiveRow(r,col,marketingActionTarget);
		} else {
			paintInactiveRow(r,col,marketingActionTarget);
		}
	}

	private void paintInactiveRow(final int r, int col, MarketingActionTarget marketingActionTarget) {		
		tab.setWidget(r, col, new Label(marketingActionTarget.getName()));
		col++;
		
		tab.setWidget(r, col, new Label(marketingActionTarget.getComments()));
		col++;
		
		tab.setWidget(r, col, new Label(getActionStatus(marketingActionTarget.getActionTargetStatus())));
		col++;
	}

	private void paintActiveRow(final int r, int col, MarketingActionTarget marketingActionTarget) {
		tab.setWidget(r, col, new Label(marketingActionTarget.getName()));
		col++;
		
		tab.setWidget(r, col, new Label(marketingActionTarget.getComments()));
		col++;
		
		tab.setWidget(r, col, new Label(getActionStatus(marketingActionTarget.getActionTargetStatus())));
		col++;
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Acci\u00f3n",
						new HTML("Se va a proceder a eliminar al cliente potencial <b>" + marketingActionTarget.getName() + "</b> de la acci\u00f3n <b>" + marketingActionTarget.getMarketingAction().getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(marketingActionTarget);
					}
				});
			}
		});
		
		
		buttonContainer.add(button);
		
		tab.setWidget(r, col, buttonContainer);
		col++;
	}

	private String getActionStatus(Byte actionTargetStatus) {
		switch (actionTargetStatus) {
			case 0:
				return "Pendiente";
			case 1:
				return "Ausente";
			case 2:
				return "Incorrecto";
			case 3:
				return "Reintentar";
			case 4:
				return "Anular";
			case 5:
				return "Finalizado";
			case 6:
				return "Enviado";
			default:
				return "Desconocido";
		}
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
	
	private void delete(MarketingActionTarget marketingActionTarget) {
		COMMON_SERVICE.deleteMarketingActionTarget(params.getDomainName(), params.getDomain(), params.getUser(), marketingActionTarget.getActionTargetId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				resetSearchOffset();
				reloadMarketingAction();
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
	
	protected abstract void reloadMarketingAction();
	protected abstract void onShowErrorMessage(String errorMessage);
	
}

