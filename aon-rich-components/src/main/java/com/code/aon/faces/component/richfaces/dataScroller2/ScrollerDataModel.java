package com.code.aon.faces.component.richfaces.dataScroller2;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

public class ScrollerDataModel extends DataModel {

	private DataModel model;
	
	private int rowCount;
	
	private int currentPage;
	
	private int pageSize;
	
	private int maxPages;

	public ScrollerDataModel( DataModel model ) {
		this.maxPages = 5;		
		this.pageSize = 0;
		setModel( model );
	}

	public DataModel getModel() {
		return model;
	}

	private boolean isDataModelChanged( DataModel model ) {
		return (this.model != model) || (this.model.getRowCount() != this.rowCount);
	}
	
	public void setModel(DataModel model) {
		if ( isDataModelChanged(model) ) {
			this.currentPage = 0;
		}
		this.model = model;
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
	
	public int getCurrentPage() {
		return currentPage;
	}

	public int getFirst() {
		int first = this.currentPage * getPageSize();
		return Math.min(first, getRowCount());
	}

	public int getLast() {
		int last = (getPageSize()==0)?(this.currentPage+1):(this.currentPage+1) * getPageSize();
		return Math.min(last, getRowCount());
	}
	
    public void first(ActionEvent event) {
   		this.currentPage = 0;
    }

    public void previous(ActionEvent event) {
    	if ( isPreviousNeeded() ) {
    		this.currentPage--;
    	}
    }
   
    public void next(ActionEvent event) {
    	if ( isNextNeeded() ) {
    		this.currentPage++;
    	}
    }
        
    public void last(ActionEvent event) {
    	this.currentPage = getLastPage();
    }

    
    public void moveToPage(ActionEvent event) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	Integer page = (Integer) context.getExternalContext().getRequestMap().get("page");
    	if ( page != null ) {
    		this.currentPage = page - 1;
    	}
    }
    
    public boolean isPreviousNeeded() {
    	return this.currentPage > 0;	
    }

	public boolean isNextNeeded() {
    	return this.currentPage < getLastPage();
	}
	
    private int getLastPage() {
    	if ( getRowCount() > 0 ) {
    		if (getPageSize() == 0) {
    			return 1;
    		}
    		return (getRowCount()-1) / getPageSize();
    	}
    	return 0;
    }
    
    private int getNumberOfPages() {
    	if ( getRowCount() > 0 ) {
    		return ( (getRowCount()-1) / getPageSize()) + 1;
    	}
    	return 0;
    }
    
    public Integer[] getPages() {
    	Integer[] pages = new Integer[getNumberOfVisiblePages()];
    	int i = getFirstVisiblePage() + 1;
    	for( int n = 0; n < pages.length; n++, i++ ) {
    		pages[n] = i;
    	}
    	return pages;
    }

    public boolean isShowControls() {
    	if ( getPageSize() > 0 ) {
    		return getRowCount() > getPageSize();
    	}
    	return false;
    }
    
    private int getFirstVisiblePage() {
    	int lastMiddlePage = getNumberOfPages() - (getMaxPages() >> 1) - 1;
    	if ( this.currentPage >= lastMiddlePage ) {
    		return Math.max( 0, getNumberOfPages() - getMaxPages() );
    	}
    	return Math.max( 0, this.currentPage - (getMaxPages() >> 1) );
    }
    
    private int getNumberOfVisiblePages() {
    	return Math.min( getNumberOfPages(), getMaxPages());
    }
    
}
