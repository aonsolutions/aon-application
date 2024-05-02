package com.esferalia.aon.gwt.marketing.client.marketing.campaign;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.occam.api.model.security.Scope;
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
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class MarketingCompaignPanel extends ScrollPanel {

	private static final int CHANGE_DISPLAY_MILLIS = 1000;
	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(MarketingCompaignPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt row = new MutableInt(0);
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private SimplePanel container;
	private FlexTable tab;
	private int lastScrollPos = 0;
	
	private MarketingCompaignParams params;
	private MarketingModuleOptions options;
	
	private static enum COLS {
		  NUM(AonStringUtils.EMPTY					,"20px"  ,AON.CSS.aonTextCenter())
		, SEL(AonStringUtils.EMPTY					,"20px"  ,AON.CSS.aonTextCenter())
		, DES(AON.MSG.description()					,"auto"  ,null)
		, BUD("Presupuesto"							,"150px" ,null)
		, BDA("Presu. Acumulado"					,"150px" ,null)
		, TYP(AON.MSG.scope()						,"150px" ,null)
		, ACT("Activa"								,"50px"  ,null)
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

	public MarketingCompaignPanel(MarketingCompaignParams params, MarketingModuleOptions options) {
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonPaddingBottom());
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.options = options;

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
		final int r = row.getValue();
		paintRow(r, marketingCampaign); 
		row.increment();
	}
	
	private void paintRow(final int r, MarketingCampaign marketingCampaign) {
		int col = 0;
		boolean myMarketingCampaign =  marketingCampaign == null || AonNumberUtils.equals(marketingCampaign.getDomain() , params.getDomain()); 
		if (myMarketingCampaign) {
			paintActiveRow(r,col,marketingCampaign);
		} else {
			paintInactiveRow(r,col,marketingCampaign);
		}
	}

	private void paintInactiveRow(final int r, int col,  MarketingCampaign marketingCampaign) {
		Label msg = new Label("");
		msg.setStyleName(AON.CSS.aonTabIcon());
		msg.addStyleName(AON.CSS.aonIconLevelTop());
		tab.setWidget(r, col, msg);
		col++;
		
		Label sel = new Label("");
		tab.setWidget(r, col, sel);
		col++;
		
		tab.setWidget(r, col, new Label(marketingCampaign.getDescription()));
		col++;
		
		tab.setWidget(r, col, new Label(marketingCampaign.getBudget().toString()));
		col++;
		
		tab.setWidget(r, col, new Label(marketingCampaign.getActions().stream().mapToDouble(action -> action.getBudget()).sum() + ""));
		col++;
		
		tab.setWidget(r, col, new Label(marketingCampaign.getScope() == null ? "" : marketingCampaign.getScope().getDescription()));
		col++;
		
		Button activeBtn = new Button();
		getEnableDisableButton(activeBtn, marketingCampaign.isActive());
		activeBtn.setEnabled(false);
		tab.setWidget(r, col, activeBtn);
		col++;

	}

	private void paintActiveRow(final int r, int col,  MarketingCampaign marketingCampaign) {
		AonTableButton msg = new AonTableButton("");
		AonTableButton sel = new AonTableButton("", AON.CSS.aonIconRight());
		TextBox descriptionBox = new TextBox();
		AonDoubleBox budget = new AonDoubleBox(15, 2);
		ListBox scopeListBox = new ListBox();
		Button activeBtn = new Button();
		
		ValueChangeHandler<String> valueChangeHandlerString = new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				marketingCampaign.setDescription(descriptionBox.getValue());
				marketingCampaign.setScope(new Scope().setId(Integer.parseInt(scopeListBox.getSelectedValue())));
				boolean currentActive = isActiveToggleButton(activeBtn);
				marketingCampaign.setActive(currentActive);
				marketingCampaign.setBudget(budget.getValue());
				
				save(marketingCampaign, msg);
			}
		};
		
		ValueChangeHandler<Double> valueChangeHandlerDouble = new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				marketingCampaign.setDescription(descriptionBox.getValue());
				marketingCampaign.setScope(new Scope().setId(Integer.parseInt(scopeListBox.getSelectedValue())));
				boolean currentActive = isActiveToggleButton(activeBtn);
				marketingCampaign.setActive(currentActive);
				marketingCampaign.setBudget(budget.getValue());
				
				save(marketingCampaign, msg);
			}
		};
		
		sel.addClickHandler(e -> {
			onMarketingCampaignOpen(marketingCampaign);
		});
		
		descriptionBox.addValueChangeHandler(valueChangeHandlerString);
		budget.addValueChangeHandler(valueChangeHandlerDouble);
		scopeListBox.addChangeHandler(e -> {
			marketingCampaign.setDescription(descriptionBox.getValue());
			marketingCampaign.setScope(new Scope().setId(Integer.parseInt(scopeListBox.getSelectedValue())));
			boolean currentActive = isActiveToggleButton(activeBtn);
			marketingCampaign.setActive(currentActive);
			marketingCampaign.setBudget(budget.getValue());
			
			save(marketingCampaign, msg);
		});
		
		activeBtn.addClickHandler(e -> {
			getEnableDisableButton(activeBtn, !isActiveToggleButton(activeBtn));
			
			marketingCampaign.setDescription(descriptionBox.getValue());
			marketingCampaign.setScope(new Scope().setId(Integer.parseInt(scopeListBox.getSelectedValue())));
			boolean currentActive = isActiveToggleButton(activeBtn);
			marketingCampaign.setActive(currentActive);
			marketingCampaign.setBudget(budget.getValue());
			
			save(marketingCampaign, msg);
		});
		
		tab.setWidget(r, col, msg);
		col++;
		
		tab.setWidget(r, col, sel);
		col++;
		
		descriptionBox.setStyleName(AON.CSS.aonBorderNone());
		descriptionBox.addStyleName(AON.CSS.aonWidthAll());
		descriptionBox.setMaxLength(128);
		descriptionBox.setValue(marketingCampaign.getDescription());
		tab.setWidget(r, col, descriptionBox);
		col++;
		
		budget.setStyleName(AON.CSS.aonBorderNone());
		budget.addStyleName(AON.CSS.aonWidthAll());
		budget.setValue(marketingCampaign.getBudget());
		tab.setWidget(r, col, budget);
		col++;
		
		tab.setWidget(r, col, new Label(marketingCampaign.getActions().stream().mapToDouble(action -> action.getBudget()).sum() + ""));
		col++;
		
		scopeListBox.setStyleName(AON.CSS.aonBorderNone());
		scopeListBox.addStyleName(AON.CSS.aonWidthAll());
		options.getConfiguration().getAvailableScopes().forEach(sc -> scopeListBox.addItem(sc.getDescription(), sc.getId() + ""));
		setSelectedValueLB(scopeListBox, marketingCampaign.getScope() == null ? null : marketingCampaign.getScope().getId().toString());
		tab.setWidget(r, col, scopeListBox);
		col++;

		getEnableDisableButton(activeBtn, marketingCampaign.isActive());
		tab.setWidget(r, col, activeBtn);
		col++;
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
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
		
		tab.setWidget(r, col, buttonContainer);
		col++;
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
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
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
	
	private void save(MarketingCampaign marketingCampaign, AonTableButton msg) {
		COMMON_SERVICE.saveMarketingCampaign(params.getDomainName(), params.getDomain(), params.getUser(), marketingCampaign, new AsyncCallback<MarketingCampaign>() {
			
			@Override
			public void onSuccess(MarketingCampaign result) {
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
	protected abstract void onMarketingCampaignOpen(MarketingCampaign marketingCampaign);
	
}

