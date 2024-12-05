package com.esferalia.aon.gwt.fiscal.client.finance.paymethod;

import java.util.HashMap;
import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.PayMethodParams;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
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

public abstract class PayMethodPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(PayMethodPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private PayMethodParams params;
	private Map<Integer, PayMethod> rowPaymethod = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();
	
	private static enum COLS {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, DES(AON.MSG.name()						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP(AON.MSG.type()						,"15rem"			,"")
		, BUT(AonStringUtils.EMPTY					,"2rem"				,"")
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

	public PayMethodPanel(PayMethodParams params) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.rowPaymethod.clear();

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding", "0 1rem 0 1px");
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
					if (selectedItemList.size() == rowPaymethod.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
						checkAllButton.addStyleName(AON.CSS.aonIconCheck());
						checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconCheck());
							check.removeStyleName(AON.CSS.aonIconChecked());
						});
						onGroupEnable(false);
					} else {
						checkAllButton.addStyleName(AON.CSS.aonIconChecked());
						checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconChecked());
							check.removeStyleName(AON.CSS.aonIconCheck());
						});
						onGroupEnable(true);
					}
				});
				
				tab.addHeader(checkAllButton, col.getColWidth());
			} else tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(paymethods -> {
			boolean something = false;
			
			for(PayMethod payMethod : paymethods) {
				something = true;
				paintRow(payMethod);
			}
			
			if (paymethods.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + paymethods.size() - 1);
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
	
	private void paintRow(PayMethod payMethod) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton button;
		button = new AonTableButton("Borrar forma de pago", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Forma de Pago",
						new HTML("Se va a proceder a eliminar la forma de pago <b>" + payMethod.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(payMethod.getId());
						button.setEnabled(true);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onPaymethodOpen(payMethod), ClickEvent.getType());
		
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
			onGroupEnable(!selectedItemList.isEmpty());
		});
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		
		Label name = new Label(payMethod.getName());
		name.setTitle(payMethod.getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		tab.addRow(row, new Label(payMethod.getType().getDescription()), COLS.TYP.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		rowPaymethod.put(payMethod.getId(), payMethod);
		selectedItems.put(payMethod.getId(), checkButton);
	}
	
	private void getList(Consumer<LinkedList<PayMethod>> success) {
		COMMON_SERVICE.getPayMethods(params, new AsyncCallback<LinkedList<PayMethod>>() {
			
			@Override
			public void onSuccess(LinkedList<PayMethod> payMethods) {
				success.accept(payMethods);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(Integer payMethodId) {
		COMMON_SERVICE.deletePayMethod(params.getDomainName(), params.getDomain(), params.getUser(), payMethodId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onShowSuccessMessage("Forma de pago eliminada correctamente");
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
		rowPaymethod.clear();
		selectedItems.clear();
	}
	
	public AonCustomTable getTable() {
		return tab;
	}
	
	public List<PayMethod> getSelectedPaymethods() {
		List<Integer> selectedItemList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		List<PayMethod> selectedPaymethodList = rowPaymethod.entrySet().stream().filter(entry -> selectedItemList.contains(entry.getKey())).map(entry -> entry.getValue()).collect(Collectors.toList());
		
		return selectedPaymethodList;
	}
	
	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onPaymethodOpen(PayMethod payMethod);
	protected abstract void onGroupEnable(boolean enabled);
	
}

