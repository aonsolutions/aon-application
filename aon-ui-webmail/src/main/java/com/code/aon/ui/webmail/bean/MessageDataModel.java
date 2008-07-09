package com.code.aon.ui.webmail.bean;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.ArrayUtils;

public class MessageDataModel extends ExtendedDataModel {

	private AonMessage[] list;

	private int index;
	
	private int first;
	
	private int pageSize;

	public MessageDataModel(AonMessage[] list) {
		this.list = list;
		this.index = -1;
		this.pageSize = 20;
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

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public boolean isFirstNeeded() {
    	return this.first >= getPageSize();
	}
	
    public void first(ActionEvent event) {
   		this.first = 0;
    }
	
    private int getFirstInLastPage() {
    	int pages = getRowCount() / getPageSize();
    	return pages * getPageSize();
    }
    
    public boolean isPreviousNeeded() {
    	return (this.first - getPageSize()) >= 0;	
    }

    public void previous(ActionEvent event) {
    	if ( isPreviousNeeded() ) {
    		this.first -= getPageSize();
    	}
    }

	public boolean isNextNeeded() {
    	return this.first < getFirstInLastPage();
	}
	
    public void next(ActionEvent event) {
    	if ( isNextNeeded() ) {
    		this.first += getPageSize();
    	}
    }
        
    public boolean isLastNeeded() {
    	return (this.first < getFirstInLastPage());	
    }

    public void last(ActionEvent event) {
    	this.first = getFirstInLastPage();
    }
    
}
