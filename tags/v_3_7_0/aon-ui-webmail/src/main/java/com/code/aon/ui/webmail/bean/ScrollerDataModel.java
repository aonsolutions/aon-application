package com.code.aon.ui.webmail.bean;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.ArrayUtils;

public class ScrollerDataModel extends ExtendedDataModel {

	private Object[] list;

	private int index;
	
	private int currentPage;
	
	private int pageSize;
	
	private int maxPages;

	public ScrollerDataModel(Object[] list) {
		this.list = list;
		this.index = -1;
		this.pageSize = 20;
		this.maxPages = 5;
	}

	@Override
	public Object getRowKey() {
		if (index < 0) {
			return null;
		}
		return index;
	}

	@Override
	public void setRowKey(Object key) {
		if (null == key) {
			this.index = -1;
		} else {
			this.index = (Integer) key;
		}
	}

	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range,
			Object argument) throws IOException {
		final SequenceRange seqRange = (SequenceRange) range;
		int rows = seqRange.getRows();
		int rowCount = getRowCount();
		int currentRow = seqRange.getFirstRow();
		if (rows > 0) {
			rows += currentRow;
			if (rowCount >= 0) {
				rows = Math.min(rows, rowCount);
			}
		} else if (rowCount >= 0) {
			rows = rowCount;
		} else {
			rows = -1;
		}
		while (rows < 0 || currentRow < rows) {
			setRowIndex(currentRow);
			if (isRowAvailable()) {
				visitor.process(context, new Integer(currentRow), argument);
			} else {
				break;
			}
			currentRow++;
		}
	}

	@Override
	public int getRowCount() {
		return ArrayUtils.getLength(list);
	}

	@Override
	public Object getRowData() {
		return ((list != null) && (index != -1)) ? list[index] : null;
	}

	@Override
	public int getRowIndex() {
		return this.index;
	}

	@Override
	public Object getWrappedData() {
		return list;
	}

	@Override
	public boolean isRowAvailable() {
		return (list != null) && (index != -1) && (list.length > index);
	}

	@Override
	public void setRowIndex(int rowIndex) {
		this.index = rowIndex;
	}

	@Override
	public void setWrappedData(Object arg0) {
		this.list = (AonMessage[]) arg0;
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
		return this.currentPage * getPageSize();
	}

    public void first(ActionEvent event) {
   		this.currentPage = 0;
    }
	
    public boolean isPreviousNeeded() {
    	return this.currentPage > 0;	
    }

    public void previous(ActionEvent event) {
    	if ( isPreviousNeeded() ) {
    		this.currentPage--;
    	}
    }

	public boolean isNextNeeded() {
    	return this.currentPage < getLastPage();
	}
	
    public void next(ActionEvent event) {
    	if ( isNextNeeded() ) {
    		this.currentPage++;
    	}
    }
        
    public void last(ActionEvent event) {
    	this.currentPage = getLastPage();
    }
    
    public int getLastPage() {
    	if ( getRowCount() > 0 ) {
    		return getRowCount() / getPageSize();
    	}
    	return 0;
    }
    
    public int getNumberOfPages() {
    	if ( getRowCount() > 0 ) {
    		return (getRowCount() / getPageSize()) + 1;
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
    
    public void moveToPage(ActionEvent event) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	Integer page = (Integer) context.getExternalContext().getRequestMap().get("page");
    	if ( page != null ) {
    		this.currentPage = page - 1;
    	}
    }
    
}
