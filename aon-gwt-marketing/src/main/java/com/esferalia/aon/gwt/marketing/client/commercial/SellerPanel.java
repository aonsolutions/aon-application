package com.esferalia.aon.gwt.marketing.client.commercial;

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
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class SellerPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(SellerPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private SellerParams params;
	private Map<Integer, Seller> rowSellers = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();
	
	private Integer deleteIterator = 0;
	
	private SimplePanel parentPanel;
	
	private static enum COLS {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, DES(AON.MSG.name()						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUD(AON.MSG.alias()						,"12rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DOC(AON.MSG.document()					,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP(AON.MSG.scope()						,"10rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, ACT("Estado"								,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"3rem"				,"")
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel,String colWidth,String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return styles;
		}
	}

	public SellerPanel(SellerParams params, SimplePanel centerPanel) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.parentPanel = centerPanel;
		
		this.params = params;
		this.rowSellers.clear();

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
		tab = new AonCustomTable();
		setWidget(tab);
		getElement().getStyle().setProperty("margin", "0 1rem");
		
		paintHeader();
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			if(col == COLS.CHK) {
				AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
				checkAllButton.addClickHandler(e -> {
					List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
					if (selectedItemList.size() == rowSellers.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
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
			} else tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(sellers -> {
			boolean something = false;
			
			for(Seller seller : sellers) {
				something = true;
				paintRow(seller);
			}
			
			if (sellers.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + sellers.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				parentPanel.clear();
				parentPanel.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}
	
	private void paintRow(Seller seller) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteButton = new AonTableButton("Borrar agente comercial", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Agente Comercial",
					new HTML("Se va a proceder a eliminar el agente <b>" + seller.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(seller.getId());
				}
			});
		});
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onSellerOpen(seller), ClickEvent.getType());
		
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
		
		
		Label name = new Label(seller.getName());
		name.setTitle(seller.getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		Label alias = new Label(seller.getAlias());
		alias.setTitle(seller.getAlias());
		tab.addInlineStyle(alias, COLS.BUD.getStyles());
		tab.addRow(row, alias, COLS.BUD.getColWidth());
		
		Label document = new Label(seller.getDocument());
		document.setTitle(seller.getDocument());
		tab.addInlineStyle(document, COLS.DOC.getStyles());
		tab.addRow(row, document, COLS.DOC.getColWidth());
		
		String scopeValue = seller.getScope() == null ? null : seller.getScope().getDescription();
		Label scope = new Label(scopeValue);
		scope.setTitle(scopeValue);
		tab.addInlineStyle(scope, COLS.TYP.getStyles());
		tab.addRow(row, scope, COLS.TYP.getColWidth());
		
		String statusValue = seller.isActive() ? "Activo" : "Inactivo";
		Label status = new Label(statusValue);
		status.setTitle(statusValue);
		tab.addInlineStyle(status, COLS.ACT.getStyles());
		tab.addRow(row, status, COLS.ACT.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		rowSellers.put(seller.getId(), seller);
		selectedItems.put(seller.getId(), checkButton);
	}
	
	private void getList(Consumer<List<Seller>> success) {
		COMMON_SERVICE.getSellers(params, new AsyncCallback<List<Seller>>() {
			
			@Override
			public void onSuccess(List<Seller> sellers) {
				success.accept(sellers);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(Integer sellerId) {
		COMMON_SERVICE.deleteSeller(params.getDomainName(), params.getDomain(), params.getUser(), sellerId, new AsyncCallback<Void>() {
			
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
		rowSellers.clear();
		selectedItems.clear();
	}
	
	public AonCustomTable getTable() {
		return tab;
	}

	public Integer getSellerListPosition(Integer sellerId) {
		List<Seller> sellers = rowSellers.values().stream().collect(Collectors.toList());
		for(int i=0; i<sellers.size(); i++)
			if(sellers.get(i).getId().equals(sellerId))
				return i;
		return 0;
	}
	
	public void getSellerListCount(Consumer<Integer> finish) {
		COMMON_SERVICE.getSellersCount(params, new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer count) {
						finish.accept(count);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						finish.accept(null);
					}
				});
	}
	
	public void deleteSellers() {
		List<Integer> selectedSellerList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		
		AonDialog dialog = new AonDialog("Eliminaci\u00f3n Agente Comercial",
				new HTML(selectedSellerList.size() == selectedItems.entrySet().size() ? "Se va a proceder a eliminar <b>TODOS</b> los agentes comerciales.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"
						: "Se va a proceder a eliminar <b>" + selectedSellerList.size() + " agentes comerciales</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				onDeleteEnable(true);
			}

			@Override
			public void onAccept() {
				onShowLoadingMessage("Eliminando agentes comerciales seleccionados...");
				deleteIterator = 0;
				delete(selectedSellerList);
			}
		});
	}
	
	private void delete(List<Integer> selectedSellerList) {
		if(deleteIterator == selectedSellerList.size()) {
			resetSearchOffset();
			onSearch();
			onShowSuccessMessage("Agentes comerciales eliminados correctamente");
		} else {
			COMMON_SERVICE.deleteSeller(params.getDomainName(), params.getDomain(), params.getUser(), selectedSellerList.get(deleteIterator), new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					deleteIterator++;
					delete(selectedSellerList);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error borrado: " + caught.getMessage());
				}
			});
		}
	}

	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onSellerOpen(Seller seller);
	protected abstract void onDeleteEnable(boolean enabled);
	
}

