package com.code.aon.ui.form;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.model.FilterField;
import org.richfaces.model.Modifiable;
import org.richfaces.model.Ordering;
import org.richfaces.model.SortField2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;

/**
 * DataModel that loads <code>Page</code>s to load objects.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 31-may-2005
 * @since 1.0
 *
 */
public class ExtendedPageDataModel extends ExtendedDataModel implements Serializable, Modifiable {

	private static final long serialVersionUID = 5496498615778179844L;

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
			
	private SortOderMap sortOrder;
	
	private boolean sortable;
	
	private boolean updated;
    
	/**
	 * Instantiates a new page data model2.
	 * 
	 * @param dataProvider the IDataModelDataProvider
	 */
	public ExtendedPageDataModel(IDataModelDataProvider dataProvider) {
    	this.dataProvider = dataProvider;
    	this.page = Page.EMPTY_PAGE;
    	this.sortOrder = new SortOderMap();
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
        if (page.getList() == null) {
            return false;
        }
        return index >= 0 && index < rowCount;
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
     * @return offset
     */
    protected int ensureIndex(int i) {
        int start = page != null ? page.getStart() : -1;
        int offset = i - start;
        if(i != -1 && offset < 0)
            offset = backward(i);
        else
	        if(page == null || offset >= dataProvider.getPageLimit())
	            offset = forward(i);
        return offset;
    }

    /**
     * Backward search
     * 
     * @param i
     * @return new index
     */
    protected int backward(int i) {
        int start = Math.max((i), 0);
        page = getPage(start, dataProvider.getPageLimit());
        return i - start;
    }

    /**
     * Forward search
     * 
     * @param i
     * @return new index
     */
    protected int forward(int i) {
    	int start = i + 1 != rowCount ? i : rowCount - dataProvider.getPageLimit();
        page = getPage(start, dataProvider.getPageLimit());
        return i - start;
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
		IManagerBean bean = dataProvider.getManagerBean();
		Criteria criteria = dataProvider.getCriteria();
		this.rowCount = bean.getCount(criteria);
		this.page = getPage(start, limit);
		this._rowIndex = (this.rowCount > 0) ? start : -1;		
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
	public SortOderMap getSortOrder() {
		return sortOrder;
	}	
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject( getWrappedData() );
		oos.writeInt( getRowIndex() );
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		this.setWrappedData( ois.readObject() );
		this.setRowIndex( ois.readInt() );
	}

	@Override
	public Object getRowKey() {
		return _rowIndex;
	}

	@Override
	public void setRowKey(Object key) {
		_rowIndex = (key == null) ? -1 : (Integer) key;
	}

	private boolean areEqualRanges(SequenceRange range1, SequenceRange range2) {
		if (range1 == null || range2 == null) {
			return range1 == null && range2 == null;
		} else if ( range1.getFirstRow() == range2.getFirstRow() ) {
			int rows1 = ( range1.getRows() > 0 ) ? range1.getRows() : this.rowCount;
			int rows2 = ( range2.getRows() > 0 ) ? range2.getRows() : this.rowCount;
			return  rows1 == rows2;
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
				visitor.process(context, new Integer(currentIndex++), argument);
			}
		}
	}
	
	private String[] getAliases( String key ) {
		String[] aliases = StringUtils.split( (String) key, ',' );
		if ( aliases != null ) {
			for( int i = 0; i < aliases.length; i++ ) {
				aliases[i] = StringUtils.trim(aliases[i]);
			}
		}
		return aliases;
	}
	
	@Override
	public void modify(List<FilterField> filterFields, List<SortField2> sortFields) {
		if ( isSortable() && this.updated ) {
			setWrappedData( Collections.emptyList() );
			this.cachedRange = null;
			this.updated = false;
		}
	}

	/**
	 * The Class FakeMap.
	 */
	public class SortOderMap extends AbstractMap<String,Ordering> {
		
		private Criteria getCriteria() {
			try {
				return dataProvider.getCriteria();
			} catch (ManagerBeanException e) {
				throw new FacesException(e.getMessage(), e); 
			}
		}
		
		@Override
		public Ordering get(Object key) {
			Criteria  criteria = getCriteria();
			if ( criteria.hasOrders() ) {
				String[] aliases = getAliases( (String) key );
				if (! ArrayUtils.isEmpty(aliases) ) {
					Order order = criteria.getOrderByList().get(aliases[0]);
					if ( order != null ) {
						return order.isAscending() ? Ordering.ASCENDING : Ordering.DESCENDING;
					}					
				}
			}
			return Ordering.UNSORTED;
		}
		
		private void updateOrder( Criteria criteria, String alias, Ordering value ) {
			int index = -1;
			if ( criteria.hasOrders() ) {
				index = criteria.getOrderByList().indexOf(alias);
			}
			if ( index != -1 ) {
				Order order = criteria.getOrderByList().getOrders().get(index);
				if (! order.isAscending()) {
					criteria.getOrderByList().remove(index);
				} else {
					Order newOrder = new Order(order.getExpression(), false );
					criteria.getOrderByList().getOrders().set(index, newOrder);
				}
			} else {
				if ( value != Ordering.UNSORTED ) {
					criteria.addOrder(alias, Ordering.ASCENDING == value);
				}							
			}
		}
		
		@Override
		public Ordering put(String key, Ordering value) {
			Criteria  criteria = getCriteria();
			String[] aliases = getAliases( key );
			for( String alias : aliases ) {
				updateOrder(criteria, alias, value);
			}
			updated = true;			
			return value;
		}

		@Override
		public Set<java.util.Map.Entry<String, Ordering>> entrySet() {
			return null;
		}
		
	}	
	
}