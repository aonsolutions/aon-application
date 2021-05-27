package com.code.aon.ui.common.serialize;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import com.code.aon.AonVersion;

public class SerializableListDataModel extends DataModel implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    // The current row index (zero relative)
    private int index = -1;


    // The list we are wrapping
    private List list;	
	
	public SerializableListDataModel() {
		this(null);
	}

	@SuppressWarnings("rawtypes")
	public SerializableListDataModel(List list) {
		setWrappedData(list);
	}

	@Override
    public int getRowCount() {
        if (list == null) {
        	return -1;
        }
        return list.size();
    }	
    
    @Override
    public Object getRowData() {
        if (list == null) {
        	return null;
        }
        return list.get(index);
    }
    
    @Override
    public int getRowIndex() {
        return index;
    }    
    
    @Override
    public Object getWrappedData() {
        return this.list;
    }    
    
    @Override
 	public boolean isRowAvailable() {
        if (list == null) {
        	return false;
        }
        return (index >= 0) && (index < list.size());
 	}    
    
    @Override
    public void setRowIndex(int rowIndex) {
        if (rowIndex < -1) {
        	throw new IllegalArgumentException("Illegal row index for " + getClass() + ": " + rowIndex);
        }
        int old = index;
        index = rowIndex;
        if (list == null) {
        	return;
        }
        DataModelListener [] listeners = getDataModelListeners();
        if ((old != index) && (listeners != null)) {
        	Object rowData = isRowAvailable() ? getRowData() : null;
        	DataModelEvent event = new DataModelEvent(this, index, rowData);
        	int n = listeners.length;
        	for (int i = 0; i < n; i++) {
        		if (null != listeners[i]) {
        			listeners[i].rowSelected(event);
        		}
        	}
        }
    }    
    
	@Override
    @SuppressWarnings("rawtypes")
    public void setWrappedData(Object data) {
        if (data == null) {
            list = null;
            setRowIndex(-1);
        } else {
            list = (List) data;
            index = -1;
            setRowIndex(0);
        }
    }    

}
