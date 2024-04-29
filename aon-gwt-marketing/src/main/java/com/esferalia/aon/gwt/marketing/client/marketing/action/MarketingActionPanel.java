package com.esferalia.aon.gwt.marketing.client.marketing.action;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class MarketingActionPanel extends ScrollPanel {

	private static final int CHANGE_DISPLAY_MILLIS = 1000;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MarketingActionPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private final int limit = 100;
	private final MutableInt row = new MutableInt(0);
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private SimplePanel container;
	private FlexTable tab;
	private int lastScrollPos = 0;
	
	private MarketingActionParams params;
	
	private static enum COLS {
		  NUM(AonStringUtils.EMPTY					,"20px"  ,AON.CSS.aonTextCenter())
		, SEL(AonStringUtils.EMPTY					,"20px"  ,AON.CSS.aonTextCenter())
		, DES(AON.MSG.description()					,"auto"  ,null)
		, TYP(AON.MSG.type()						,"100px" ,null)
		, CAN("Canal"								,"150px" ,null)
		, BUD("Presupuesto"							,"150px" ,null)
		, STD("F. Inicio"							,"100px" ,null)
		, END("F. Fin"								,"100px" ,null)
		, SUR("Cuestionario"						,"150px" ,null)
		, TGT("Clientes Potenciales"				,"120px" ,null)
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

	public MarketingActionPanel(MarketingActionParams params) {
		
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
		final int r = row.getValue();
		paintRow(r, marketingAction); 
		row.increment();
	}
	
	private void paintRow(final int r, MarketingAction marketingAction) {
		int col = 0;
		boolean myMarketingAction =  marketingAction == null || AonNumberUtils.equals(marketingAction.getDomain() , params.getDomain()); 
		if (myMarketingAction) {
			paintActiveRow(r,col,marketingAction);
		} else {
			paintInactiveRow(r,col,marketingAction);
		}
	}

	private void paintInactiveRow(final int r, int col, MarketingAction marketingAction) {		
		Label msg = new Label("");
		msg.setStyleName(AON.CSS.aonTabIcon());
		msg.addStyleName(AON.CSS.aonIconLevelTop());
		tab.setWidget(r, col, msg);
		col++;
		
		Label sel = new Label("");
		tab.setWidget(r, col, sel);
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getDescription()));
		col++;
		
		tab.setWidget(r, col, new Label(getActivonType(marketingAction.getMediaType().getValue())));
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getMediaType().getDescription()));
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getBudget().toString()));
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getStartDate() == null ? "" : formatDate.format(marketingAction.getStartDate())));
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getEndDate() == null ? "" : formatDate.format(marketingAction.getEndDate())));
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getSurvey() == null ? "" : marketingAction.getSurvey().getDescription()));
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getTargets() == null ? "0" : marketingAction.getTargets().size() + ""));
		col++;

	}

	private void paintActiveRow(final int r, int col, MarketingAction marketingAction) {
		AonTableButton msg = new AonTableButton("");
		AonTableButton sel = new AonTableButton("", AON.CSS.aonIconRight());
		TextBox descriptionBox = new TextBox();
		Label typeAction = new Label();
		ListBox typeListBox = new ListBox();
		AonDoubleBox budget = new AonDoubleBox(15, 2);
		AonDateBox startDate = new AonDateBox();
		AonDateBox endDate = new AonDateBox();
		
		ValueChangeHandler<String> valueChangeHandlerString = new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				marketingAction.setDescription(descriptionBox.getValue());
				marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue())));
				marketingAction.setStartDate(startDate.getValue());
				marketingAction.setEndDate(endDate.getValue());
				marketingAction.setBudget(budget.getValue());
				
				save(marketingAction, msg);
			}
		};
		
		ValueChangeHandler<Date> valueChangeHandlerDate = new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				marketingAction.setDescription(descriptionBox.getValue());
				marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue())));
				marketingAction.setStartDate(startDate.getValue());
				marketingAction.setEndDate(endDate.getValue());
				marketingAction.setBudget(budget.getValue());
				
				save(marketingAction, msg);
			}
		};
		
		ValueChangeHandler<Double> valueChangeHandlerDouble = new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				marketingAction.setDescription(descriptionBox.getValue());
				marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue())));
				marketingAction.setStartDate(startDate.getValue());
				marketingAction.setEndDate(endDate.getValue());
				marketingAction.setBudget(budget.getValue());
				
				save(marketingAction, msg);
			}
		};
		
		sel.addClickHandler(e -> {
			onMarketingActionOpen(marketingAction);
		});
		
		descriptionBox.addValueChangeHandler(valueChangeHandlerString);
		typeListBox.addChangeHandler(e -> {
			marketingAction.setDescription(descriptionBox.getValue());
			marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue())));
			marketingAction.setStartDate(startDate.getValue());
			marketingAction.setEndDate(endDate.getValue());
			marketingAction.setBudget(budget.getValue());
			
			switch (MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue()))) {
				case PHONE:
					marketingAction.setNewsletter(null);
					break;
				case EMAIL:
					marketingAction.setNewsletter(null);
					marketingAction.setSurvey(null);
					break;
				case MAIL:
					marketingAction.setNews(null);
					marketingAction.setNewsletter(null);
					marketingAction.setSurvey(null);
					break;
				case BULLETIN:
					marketingAction.setNews(null);
					marketingAction.setSurvey(null);
					break;
				default:
					break;
			}
			
			typeAction.setText(getActivonType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue())).getValue()));
			
			save(marketingAction, msg);
		});
		budget.addValueChangeHandler(valueChangeHandlerDouble);
		startDate.addValueChangeHandler(valueChangeHandlerDate);
		endDate.addValueChangeHandler(valueChangeHandlerDate);
		
		tab.setWidget(r, col, msg);
		col++;
		
		tab.setWidget(r, col, sel);
		col++;
		
		descriptionBox.setStyleName(AON.CSS.aonBorderNone());
		descriptionBox.addStyleName(AON.CSS.aonWidthAll());
		descriptionBox.setMaxLength(128);
		descriptionBox.setValue(marketingAction.getDescription());
		tab.setWidget(r, col, descriptionBox);
		col++;
		
		typeAction.setText(getActivonType(marketingAction.getMediaType().getValue()));
		tab.setWidget(r, col, typeAction);
		col++;
		
		typeListBox.setStyleName(AON.CSS.aonBorderNone());
		typeListBox.addStyleName(AON.CSS.aonWidthAll());
		typeListBox.addItem( "-", "");
		for(int i=0; i<MarketingActionMediaType.values().length; i++) {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.values()[i];
			typeListBox.addItem(marketingActionMediaType.getDescription(), marketingActionMediaType.getValue().toString());
		}
		setSelectedValueLB(typeListBox, marketingAction.getMediaType().getValue() + "");
		tab.setWidget(r, col, typeListBox);
		col++;
		
		budget.setStyleName(AON.CSS.aonBorderNone());
		budget.addStyleName(AON.CSS.aonWidthAll());
		budget.setValue(marketingAction.getBudget());
		tab.setWidget(r, col, budget);
		col++;
		
		startDate.setStyleName(AON.CSS.aonBorderNone());
		startDate.addStyleName(AON.CSS.aonWidthAll());
		startDate.setValue(marketingAction.getStartDate());
		tab.setWidget(r, col, startDate);
		col++;
		
		endDate.setStyleName(AON.CSS.aonBorderNone());
		endDate.addStyleName(AON.CSS.aonWidthAll());
		endDate.setValue(marketingAction.getEndDate());
		tab.setWidget(r, col, endDate);
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getSurvey() == null ? "" : marketingAction.getSurvey().getDescription()));
		col++;
		
		tab.setWidget(r, col, new Label(marketingAction.getTargets() == null ? "0" : marketingAction.getTargets().size() + ""));
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
		
		tab.setWidget(r, col, buttonContainer);
		col++;
	}

	private String getActivonType(Integer type) {
		return type > 5 ? "Incoming" : "Outcoming";
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
		String text = str;
		int indexToFind = 0;
		for (int i = 0; i < lBox.getItemCount(); i++) {
			if (lBox.getValue(i).equals(text)) {
				indexToFind = i;
				break;
			}
		}
		lBox.setSelectedIndex(indexToFind);
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
	
	private void save(MarketingAction marketingAction, AonTableButton msg) {
		COMMON_SERVICE.saveMarketingAction(params.getDomainName(), params.getDomain(), params.getUser(), marketingAction, new AsyncCallback<MarketingAction>() {
			
			@Override
			public void onSuccess(MarketingAction result) {
				msg.addStyleName(AON.CSS.aonIconValid());
				new Timer() {
					@Override
					public void run() {
						msg.removeStyleName(AON.CSS.aonIconValid());
					}
				}.schedule(CHANGE_DISPLAY_MILLIS);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error guardado: " + caught.getMessage());
			}
		});
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
	}
	
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onMarketingActionOpen(MarketingAction marketingAction);
	
}

