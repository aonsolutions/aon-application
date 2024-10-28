package com.code.aon.ui.form;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.richfaces.model.FilterField;
import org.richfaces.model.Modifiable;
import org.richfaces.model.SortField2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

/**
 * DataModel that loads <code>Page</code>s to load objects.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 31-may-2005
 * @since 1.0
 *
 */
public class ExtendedPageDataModel extends ExtendedDataModel implements Serializable, Modifiable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedPageDataModel.class);

    /** Indicates the current row index of the <code>DataModel</code>. */
    private Integer _rowIndex = -1;

    /** Current page. */
    private Page page;

    /** Number of rows of the data source. */
    private int rowCount;

    private IDataModelDataProvider dataProvider;

    private SequenceRange cachedRange;
			
	private SortOrderMap sortOrder;
	
	private boolean sortable;
    
	private Set<String> visibleFields;
	
	
	/**
	 * Instantiates a new page data model2.
	 * 
	 * @param dataProvider the IDataModelDataProvider
	 * @param criteriaProvider the criteria provider
	 */
	public ExtendedPageDataModel(IDataModelDataProvider dataProvider, ICriteriaProvider criteriaProvider) {
    	this.page = Page.EMPTY_PAGE;
    	this.dataProvider = dataProvider;
    	this.visibleFields = new HashSet<>();
    	this.sortOrder = new SortOrderMap(criteriaProvider);
	}

	/**
	 * Instantiates a new extended page data model.
	 * 
	 * @param controller the controller
	 */
	public ExtendedPageDataModel(IController controller) {
		this( controller, controller );
	}	
	
	@Override
    public int getRowCount() {
        if (page.getList() == null) {
            return -1;
        }
        return rowCount;
    }

	@Override
	public Object getRowData() {
        if (page.getList() == null) {
            return null;
        }
        if (!isRowAvailable()) {
            throw new IllegalArgumentException("row is unavailable"); 
        }
		return page.getList().get(_rowIndex - page.getStart());
    }

    /**
     * Sets the row data.
     * 
     * @param index 
     * @param data the data
     */
    public void setRowData( int index, ITransferObject data ) {
        if (!isRowAvailable(index)) {
            throw new IllegalArgumentException("row " + index + " is unavailable"); 
        }
		page.getList().set(index - page.getStart(), data);
    }
	
	@Override
    public int getRowIndex() {
        return _rowIndex;
    }

	@Override
	public Object getWrappedData() {
        return page.getList();
    }

	@Override
    public boolean isRowAvailable() {
		return isRowAvailable( _rowIndex );
    }
    
    /**
     * Return a flag indicating whether there is <code>rowData</code>
     * available at <code>index</code>. If no wrappedData is available,
     * return false.
     * 
     * @param index the index
     * 
     * @return true, if is row available
     */
    private boolean isRowAvailable( int index ) {
    	return page.isRowAvailable(index);
    }	
    
    @Override
    public void setRowIndex(int rowIndex) {
        if (rowIndex < -1) {
            throw new IllegalArgumentException("illegal rowIndex " + rowIndex); 
        }
        int oldRowIndex = _rowIndex;
        _rowIndex = rowIndex;
        if (page.getList() != null && oldRowIndex != _rowIndex) {
        	ensureIndex(_rowIndex);
        	Object data = isRowAvailable() ? getRowData() : null;
            DataModelEvent event = new DataModelEvent(this, _rowIndex, data);
            DataModelListener[] listeners = getDataModelListeners();
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].rowSelected(event);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
	public void setWrappedData(Object data) {
        List<ITransferObject> list = (List<ITransferObject>) data;
		page.setList(list);
		int rowIndex = page.isEmpty() ? -1 : 0;
        setRowIndex(rowIndex);
    }

    /**
     * Calculates the offset within the list and if the index is out of specified bounds realizes a new
     * search.
     * 
     * @param i
     */
    private void ensureIndex(int i) {
    	if ( i != -1 &&
    		(page==null || i<page.getStart() || i>=page.getStartOfNextPage()) ) {
			int start = 0;
			if ( dataProvider.getPageLimit() > 0 ) {
    	    	int pageNumber = i / dataProvider.getPageLimit();
    	    	start = pageNumber * dataProvider.getPageLimit();    				
			}
	        this.page = getPage(start, dataProvider.getPageLimit());
    	}
    }

    /**
     * Searches a <code>Page</code> within the stablished bounds delimited by 
     * <code>start</code> and <code>count</code>.
     *  
     * @param start
     * @param count
     * @return page
     */
	private Page getPage(int start, int count) {
		try {
			List<ITransferObject> l = dataProvider.search(start, count);
			this.cachedRange = new SequenceRange(start, count);
			return new Page(l, start);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error happened while Page loading, empty Page will be return.",e);
			return Page.EMPTY_PAGE;
		}
	}

	/**
	 * Update.
	 * 
	 * @param start the start
	 * @param limit the limit
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void update( int start, int limit ) throws ManagerBeanException {
		this.rowCount = dataProvider.getRowCount();
		this.page = getPage(start, limit);
		this._rowIndex = this.rowCount>0 ? start : -1;		
    }
	
	/**
	 * Refresh.
	 */
	public void refresh() {
		this.page = getPage( this.page.getStart(), dataProvider.getPageLimit());
		if ( this.page.isEmpty() ) {
			this._rowIndex = -1;
		}
	}
	
	/**
	 * Checks if is sortable.
	 * 
	 * @return true, if is sortable
	 */
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * Sets the sortable.
	 * 
	 * @param sortable the new sortable
	 */
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
	
	/**
	 * Gets the sort order.
	 * 
	 * @return the sort order
	 */
	public SortOrderMap getSortOrder() {
		return sortOrder;
	}	

	@Override
	public Object getRowKey() {
		return _rowIndex;
	}

	@Override
	public void setRowKey(Object key) {
		_rowIndex = (key == null) ? -1 : (Integer) key;
	}

	private int getRows(SequenceRange range) {
		if ( range.getRows() > 0 ) {
			return Math.min( range.getRows(), this.rowCount );
		}
		return this.rowCount;
	}
	
	private boolean areEqualRanges(SequenceRange range1, SequenceRange range2) {
		if (range1 == null || range2 == null) {
			return range1 == null && range2 == null;
		} else if ( range1.getFirstRow() == range2.getFirstRow() ) {
			return  getRows(range1) == getRows(range2);
		}
		return false;
	}
	
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range,
			Object argument) throws IOException {
		SequenceRange sequenceRange = (SequenceRange) range;
		
		if (! areEqualRanges(this.cachedRange, sequenceRange)) {
			int first = -1;
			int rows = -1;
			if (sequenceRange != null) {
				first = sequenceRange.getFirstRow();
				if ( sequenceRange.getRows() > 0  ) {
					rows = sequenceRange.getRows();	
				}
			}
			try {
				update(first, rows);
			} catch (ManagerBeanException e) {
				throw new FacesException(e.getMessage(), e); 
			}
		}
		if (! this.page.isEmpty() ) {
			for( int i = 0, currentIndex = this.page.getStart(); i < this.page.getSize(); i++ ) {
				visitor.process(context, Integer.valueOf(currentIndex++), argument);
			}
		}
	}
	
	@Override
	public void modify(List<FilterField> filterFields, List<SortField2> sortFields) {
		if ( isSortable() && this.sortOrder.isUpdated() ) {
			setWrappedData( Collections.emptyList() );
			this.cachedRange = null;
			this.sortOrder.setUpdated(false);
		}
	}
	
	public void setVisibleFields(String ...fields) {
		visibleFields = new HashSet<>();
		Arrays.stream(fields).forEach(visibleFields::add);
	}
	
	public Collection<String> getVisibleFields() {
		return Collections.unmodifiableCollection(visibleFields);
	}
	
}