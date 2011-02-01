package com.code.aon.faces.component.richfaces.dataScroller2;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

public class ScrollerDataModel extends DataModel {

	private DataModel model;

	private int rowCount;

	private int currentPage;

	private ValueExpression page;
	
	private int pageSize;

	private int maxPages;

	public ScrollerDataModel(DataModel model) {
		this.maxPages = 5;
		this.pageSize = 0;
		setModel(model);
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

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
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
				.get("page");
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

}
