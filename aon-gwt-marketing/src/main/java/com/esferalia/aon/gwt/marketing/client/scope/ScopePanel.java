package com.esferalia.aon.gwt.marketing.client.scope;

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
import com.esferalia.aon.occam.api.model.scope.ScopeParams;
import com.esferalia.aon.occam.api.model.security.Scope;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ScopePanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(ScopePanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private ScopeParams params;
	private boolean isOffice;
	private Map<Integer, Scope> rowScopes = new HashMap<>();
	
	private static enum COLS {
		
		  DOM(AonStringUtils.EMPTY					,"4rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DES(AON.MSG.description()					,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		//, BUT(AonStringUtils.EMPTY					,"3rem"				,"")
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

	public ScopePanel(ScopeParams params, boolean isOffice) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.isOffice = isOffice;
		this.rowScopes.clear();

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
		tab.getElement().getStyle().setProperty("padding", "0");
		setWidget(tab);
		
		paintHeader();
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values())
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(scopes -> {
			boolean something = false;
			
			for(Scope scope : scopes) {
				something = true;
				paintRow(scope);
			}
			
			if (scopes.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + scopes.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				paintEmptyRow();
				disableMoreData();
			}
			enableSearch();
			
		});
		
	}
	
	private void paintEmptyRow() {
		HTMLPanel row = tab.createRow();
		
		Label name = new Label("No existen \u00e1mbitos");
		name.setTitle("No existen \u00e1mbitos");
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
	}

	private void paintRow(Scope scope) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteButton = new AonTableButton("Borrar \u00e1mbito", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n \u00c1mbito",
					new HTML("Se va a proceder a eliminar el \u00e1mbito <b>" + scope.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(scope.getId(), deleteButton);
				}
			});
		});
		deleteButton.setVisible(scope.getDomain().equals(params.getDomain()) || isOffice);
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onScopeOpen(scope), ClickEvent.getType());
		
		Widget userDomain;
		if(!scope.getDomain().equals(params.getDomain())){
			userDomain = new AonTableButton("Entorno Padre", AON.CSS.aonIconEnterprise());
			userDomain.addStyleName(AON.CSS.aonCustomRowButtom());
		} else {
			userDomain = new AonTableButton("Entorno Local", AON.CSS.aonIconShieldLocked());
			userDomain.addStyleName(AON.CSS.aonCustomRowButtom());
		}
		
		tab.addRow(row, userDomain, COLS.DOM.getColWidth());
		
		Label name = new Label(scope.getDescription());
		name.setTitle(scope.getDescription());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		//tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		rowScopes.put(scope.getId(), scope);
	}
	
	private void getList(Consumer<List<Scope>> success) {
		COMMON_SERVICE.getScopeList(params, new AsyncCallback<List<Scope>>() {
			
			@Override
			public void onSuccess(List<Scope> taskHolders) {
				success.accept(taskHolders);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo \u00e1mbitos : " + caught.getMessage());
			}
		});
	}
	
	private void delete(Integer scopeId, AonTableButton deleteButton) {
		COMMON_SERVICE.deleteScope(params.getDomainName(), params.getDomain(), params.getUser(), scopeId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
				deleteButton.setEnabled(true);
			}
		});
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
		rowScopes.clear();
	}
	
	public AonCustomTable getTable() {
		return tab;
	}

	public Integer getScopeListPosition(Integer scopeId) {
		List<Scope> scopes = rowScopes.values().stream().collect(Collectors.toList());
		for(int i=0; i<scopes.size(); i++)
			if(scopes.get(i).getId().equals(scopeId))
				return i;
		return 0;
	}
	
	public void getScopeListCount(Consumer<Integer> finish) {
		COMMON_SERVICE.getScopesCount(params, new AsyncCallback<Integer>() {
					
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

	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onScopeOpen(Scope scope);
	protected abstract void onScopeCreation(Scope scope);
	
}

