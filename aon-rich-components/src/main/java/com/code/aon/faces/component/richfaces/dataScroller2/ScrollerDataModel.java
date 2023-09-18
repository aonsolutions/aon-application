package com.code.aon.faces.component.richfaces.dataScroller2;

import jakarta.el.ValueExpression;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.faces.component.richfaces.IRichFacesTags;

public class ScrollerDataModel extends DataModel {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScrollerDataModel.class);

	private DataModel model;

	private int rowCount;

	private int currentPage;

	private ValueExpression page;
	
	private UIData table;

	private int maxPages;
	
	private boolean hidePageSizeSelector;

	public ScrollerDataModel(DataModel model) {
		this.maxPages = 5;
		setModel(model);
	}
	
	public void setTable(UIData table) {
		this.table = table;
	}

	public DataModel getModel() {
		return model;
	}

	private boolean isDataModelChanged(DataModel model) {
		return (this.model != model)
				|| (this.model.getRowCount() != this.rowCount);
	}

	public void setModel(DataModel model) {
		if (isDataModelChanged(model)) {
			setCurrentPage(0);
		}
		this.model = model;
		this.page = null;
		this.rowCount = model.getRowCount();
	}

	@Override
	public int getRowCount() {
		return getModel().getRowCount();
	}

	@Override
	public Object getRowData() {
		return getModel().getRowData();
	}

	@Override
	public int getRowIndex() {
		return getModel().getRowIndex();
	}

	@Override
	public Object getWrappedData() {
		return getModel().getWrappedData();
	}

	@Override
	public boolean isRowAvailable() {
		return getModel().isRowAvailable();
	}

	@Override
	public void setRowIndex(int rowIndex) {
		getModel().setRowIndex(rowIndex);
	}

	@Override
	public void setWrappedData(Object data) {
		getModel().setWrappedData(data);
	}

	public Integer getPageSize() {
		return table.getRows();
	}
	
	public void setPageSize( Integer pageSize ) {
		if ( pageSize != getPageSize() ) {
			ValueExpression ve = table.getValueExpression(IRichFacesTags.ROWS);
			if ( ve != null ) {
				try {
					ve.setValue(FacesContext.getCurrentInstance().getELContext(), pageSize);
					setCurrentPage(0);				
				} catch ( Throwable th ) {
					LOGGER.error( "Error setting page size. " + th.getMessage(), th );
				}
			}			
		}
	}	

	public int getMaxPages() {
		return maxPages;
	}

	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}

	public void setCurrentPage(int currentPage) {
		if ( this.page != null ) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			this.page.setValue(ctx.getELContext(), currentPage+1);
		} else {
			this.currentPage = currentPage;
		}
	}

	public int getCurrentPage() {
		if ( this.page != null ) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			return (Integer) this.page.getValue(ctx.getELContext()) - 1;
		}
		return currentPage;		
	}
	
	public ValueExpression getPage() {
		return page;
	}

	public void setPage(ValueExpression page) {
		this.page = page;
		if ( getCurrentPage() < 0 ) {
			setCurrentPage(0);
		}
	}

	public int getFirst() {
		int first = getCurrentPage() * getPageSize();
		return Math.min(first, getRowCount());
	}

	public int getLast() {
		int last = (getPageSize() == 0) ? (getCurrentPage() + 1)
				: (getCurrentPage() + 1) * getPageSize();
		return Math.min(last, getRowCount());
	}

	public void first(ActionEvent event) {
		setCurrentPage(0);
	}

	public void previous(ActionEvent event) {
		if (isPreviousNeeded()) {
			setCurrentPage( getCurrentPage()-1 );
		}
	}

	public void next(ActionEvent event) {
		if (isNextNeeded()) {
			setCurrentPage( getCurrentPage()+1 );
		}
	}

	public void last(ActionEvent event) {
		setCurrentPage( getLastPage() );
	}

	public void moveToPage(ActionEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		Integer page = (Integer) context.getExternalContext().getRequestMap()
				.get(IRichFacesTags.PAGE);
		if (page != null) {
			setCurrentPage( page - 1 );
		}
	}

	public boolean isPreviousNeeded() {
		return getCurrentPage() > 0;
	}

	public boolean isNextNeeded() {
		return getCurrentPage() < getLastPage();
	}

	private int getLastPage() {
		if (getRowCount() > 0) {
			if (getPageSize() == 0) {
				return 1;
			}
			return (getRowCount() - 1) / getPageSize();
		}
		return 0;
	}

	private int getNumberOfPages() {
		if (getRowCount() > 0) {
			return ((getRowCount() - 1) / getPageSize()) + 1;
		}
		return 0;
	}

	public Integer[] getPages() {
		Integer[] pages = new Integer[getNumberOfVisiblePages()];
		int i = getFirstVisiblePage() + 1;
		for (int n = 0; n < pages.length; n++, i++) {
			pages[n] = i;
		}
		return pages;
	}

	public boolean isShowControls() {
		if (getPageSize() > 0) {
			return getRowCount() > getPageSize();
		}
		return false;
	}

	private int getFirstVisiblePage() {
		int lastMiddlePage = getNumberOfPages() - (getMaxPages() >> 1) - 1;
		if (getCurrentPage() >= lastMiddlePage) {
			return Math.max(0, getNumberOfPages() - getMaxPages());
		}
		return Math.max(0, getCurrentPage() - (getMaxPages() >> 1));
	}

	private int getNumberOfVisiblePages() {
		return Math.min(getNumberOfPages(), getMaxPages());
	}

	public boolean isHidePageSizeSelector() {
		return hidePageSizeSelector;
	}

	public void setHidePageSizeSelector(boolean hidePageSizeSelector) {
		this.hidePageSizeSelector = hidePageSizeSelector;
	}
	
}