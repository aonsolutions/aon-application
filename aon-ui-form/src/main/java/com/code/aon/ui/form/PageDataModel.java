package com.code.aon.ui.form;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

/**
 * DataModel that loads <code>Page</code>s to load objects.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 31-may-2005
 * @since 1.0
 *
 */
public class PageDataModel extends DataModel implements Serializable {

	private static final long serialVersionUID = 8811317880674890755L;

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PageDataModel.class.getName());

	/** Default limit of rows to be load from de data source. */
    public static final int LIMIT = 20;
	/** Indicates the current row index of the <code>DataModel</code>. */
    private int _rowIndex = -1;
	/** Current page. */
    private Page page;
	/** Number of rows of the data source. */
    private int size;
	/** Limit of rows to be load from the data source. */
    private int limit = LIMIT;

    private IDataModelDataProvider dataProvider;

    
    
   	/**
	    * The Constructor.
	    * 
	    * @param list the list
	    * @param dataProvider the data provider
	    * @param limit limit of rows to be load from the data source
	    * @param size number of rows of the data source
	    */
	private PageDataModel(IDataModelDataProvider dataProvider, List<ITransferObject> list, int size, int limit ) {
    	this.dataProvider = dataProvider;
    	if (limit == -1 ) {
    		limit = Integer.MAX_VALUE;
    	}
    	this.limit = limit;
    	this.page = new Page(list, 0);
    	setWrappedData(list);
    	resize( size );
	}

	/**
	 * Empty Constructor.
	 * 
	 * @param dataProvider the data provider
	 * @param limit limit of rows to be load from the data source
	 */
    @SuppressWarnings("unchecked")
	public PageDataModel( IDataModelDataProvider dataProvider, int limit ) {
    	this( dataProvider, Collections.EMPTY_LIST, 0, limit );
    }
    
    /**
     * @param dataProvider the data provider
     * @param size number of rows of the data source
     * @param limit limit of rows to be load from the data source
     * @throws ManagerBeanException
     */
    public PageDataModel(IDataModelDataProvider dataProvider,int size,int limit) throws ManagerBeanException {
    	this( dataProvider, dataProvider.search(0, limit), size, limit );
    }

	/* (non-Javadoc)
	 * @see javax.faces.model.DataModel#getRowCount()
	 */
    public int getRowCount() {
        if (page.getList() == null) {
            return -1;
        }
        return size;
    }

	/* (non-Javadoc)
	 * @see javax.faces.model.DataModel#getRowData()
	 */
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
    
	/* (non-Javadoc)
	 * @see javax.faces.model.DataModel#getRowIndex()
	 */
    public int getRowIndex() {
        return _rowIndex;
    }

	/* (non-Javadoc)
	 * @see javax.faces.model.DataModel#setRowIndex(int)
	 */
    public Object getWrappedData() {
        return page.getList();
    }

	/* (non-Javadoc)
	 * @see javax.faces.model.DataModel#isRowAvailable()
	 */
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
    public boolean isRowAvailable( int index ) {
        if (page.getList() == null) {
            return false;
        }
        return index >= 0 && index < size;
    }
    
	/* (non-Javadoc)
	 * @see javax.faces.model.DataModel#setRowIndex(int)
	 */
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

	/* (non-Javadoc)
	 * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
	 */
    @SuppressWarnings("unchecked")
	public void setWrappedData(Object data) {
        List<ITransferObject> list = (List<ITransferObject>) data;
		page.setList(list);
        int rowIndex = (page.getList() != null && page.getSize() > 0) ? 0 : -1;
        setRowIndex(rowIndex);
    }

    /**
     * Resize the number of rows of the data source.
     *  
     * @param size
     */
    public void resize(int size) {
    	this.size = size;
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
	        if(page == null || offset >= limit)
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
        page = getPage(start, limit);
        return i - start;
    }

    /**
     * Forward search
     * 
     * @param i
     * @return new index
     */
    protected int forward(int i) {
    	int start = i + 1 != size ? i : size - limit;
        page = getPage(start, limit);
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
	public Page getPage(int start, int count) {
		try {
			List<ITransferObject> l = dataProvider.search(start, count);
			return new Page(l, start);
		} catch (ManagerBeanException e) {
			LOGGER.fine("Error happened while Page loading[" + e.getMessage() + "] Empty Page will be return.");
			return Page.EMPTY_PAGE;
		}
	}

	   
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject( getWrappedData() );
		oos.writeInt( getRowIndex() );
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		this.setWrappedData( ois.readObject() );
		this.setRowIndex( ois.readInt() );
	}
}