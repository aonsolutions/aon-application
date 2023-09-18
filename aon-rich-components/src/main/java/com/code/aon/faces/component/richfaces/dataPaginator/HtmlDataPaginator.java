package com.code.aon.faces.component.richfaces.dataPaginator;

import jakarta.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.richfaces.component.html.HtmlDatascroller;

public class HtmlDataPaginator extends HtmlDatascroller {
	
    public static final String COMPONENT_TYPE = "com.code.aon.faces.DataPaginator";

    public static final String RENDERER_TYPE = "com.code.aon.faces.DataPaginatorRenderer";
	
	private static final String ROWS_COUNT_ATTRIBUTE = "rowsCountVar";

	private static final String FIRST_ROW_INDEX_ATTRIBUTE = "firstRowIndexVar";

	private static final String LAST_ROW_INDEX_ATTRIBUTE = "lastRowIndexVar";
	
	private static final String DISPLAYED_ROWS_COUNT_ATTRIBUTE = "displayedRowsCountVar";

    private String _rowsCountVar;
    
    private String _displayedRowsCountVar;
    
    private String _firstRowIndexVar;
    
    private String _lastRowIndexVar;
    
	public HtmlDataPaginator() {
        setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

	/**
     * <p>Set the value of the <code>rowsCountVar</code> property.</p>
     */
    public void setRowsCountVar(String rowsCountVar) {
        _rowsCountVar = rowsCountVar;
    }

    /**
     * <p>Return the value of the <code>rowsCountVar</code> property.</p>
     */
    public String getRowsCountVar() {
        if (_rowsCountVar != null) {
            return _rowsCountVar;
        }
        ValueExpression vb = getValueExpression( ROWS_COUNT_ATTRIBUTE );
        return vb != null ? (String) vb.getValue(getFacesContext().getELContext()) : null;
    }

    /**
     * <p>Set the value of the <code>displayedRowsCountVar</code> property.</p>
     */
    public void setDisplayedRowsCountVar(String displayedRowsCountVar) {
        _displayedRowsCountVar = displayedRowsCountVar;
    }

    /**
     * <p>Return the value of the <code>displayedRowsCountVar</code>
     * property.</p>
     */
    public String getDisplayedRowsCountVar() {
        if (_displayedRowsCountVar != null) {
            return _displayedRowsCountVar;
        }
        ValueExpression vb = getValueExpression( DISPLAYED_ROWS_COUNT_ATTRIBUTE );
        return vb != null ? (String) vb.getValue(getFacesContext().getELContext()) : null;
    }

    /**
     * <p>Set the value of the <code>firstRowIndexVar</code> property.</p>
     */
    public void setFirstRowIndexVar(String firstRowIndexVar) {
        _firstRowIndexVar = firstRowIndexVar;
    }

    /**
     * <p>Return the value of the <code>firstRowIndexVar</code> property.</p>
     */
    public String getFirstRowIndexVar() {
        if (_firstRowIndexVar != null) {
            return _firstRowIndexVar;
        }
        ValueExpression vb = getValueExpression( FIRST_ROW_INDEX_ATTRIBUTE );
        return vb != null ? (String) vb.getValue(getFacesContext().getELContext()) : null;
    }

    /**
     * <p>Set the value of the <code>lastRowIndexVar</code> property.</p>
     */
    public void setLastRowIndexVar(String lastRowIndexVar) {
        _lastRowIndexVar = lastRowIndexVar;
    }

    /**
     * <p>Return the value of the <code>lastRowIndexVar</code> property.</p>
     */
    public String getLastRowIndexVar() {
        if (_lastRowIndexVar != null) {
            return _lastRowIndexVar;
        }
        ValueExpression vb = getValueExpression( LAST_ROW_INDEX_ATTRIBUTE );
        return vb != null ? (String) vb.getValue(getFacesContext().getELContext()) : null;
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[5];
        values[0] = super.saveState(context);
        values[1] = _rowsCountVar;
        values[2] = _displayedRowsCountVar;
        values[3] = _firstRowIndexVar;
        values[4] = _lastRowIndexVar;
        return values;
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _rowsCountVar = (String) values[1];
        _displayedRowsCountVar = (String) values[2];
        _firstRowIndexVar = (String) values[3];
        _lastRowIndexVar = (String) values[4];
    }

}
