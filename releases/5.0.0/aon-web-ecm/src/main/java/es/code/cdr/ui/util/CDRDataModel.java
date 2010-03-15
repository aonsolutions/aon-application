package es.code.cdr.ui.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.model.DataModel;

@SuppressWarnings("unchecked")
public class CDRDataModel extends DataModel implements Serializable {

	private static final long serialVersionUID = -3682220579098908496L;

	/** Loaded objects. */
	private List objects;
	/** Indicates the current row index of the <code>DataModel</code>. */
    private int _rowIndex = -1;

    /**
     * <p>Construct a new {@link CDRDataModel} with no specified
     * wrapped data.</p>
     */
    public CDRDataModel() {
        this(null);
    }

    /**
     * <p>Construct a new {@link CDRDataModel} wrapping the specified
     * list.</p>
     *
     * @param list List to be wrapped (if any)
     */
    public CDRDataModel(List list) {
        super();
        setWrappedData(list);
    }

	@Override
	public int getRowCount() {
        if (objects == null) {
            return -1;
        }
        return objects.size();
	}

	@Override
	public Object getRowData() {
        if (objects == null) {
            return null;
        }
        if (!isRowAvailable()) {
            throw new IllegalArgumentException("row is unavailable"); 
        }
		return objects.get( _rowIndex );
	}

	@Override
	public int getRowIndex() {
        return _rowIndex;
	}

	@Override
	public Object getWrappedData() {
		return objects;
	}

	@Override
	public boolean isRowAvailable() {
        if (objects == null) {
            return false;
        }
        return _rowIndex >= 0 && _rowIndex < objects.size();
	}

	@Override
	public void setRowIndex(int arg0) {
		_rowIndex = arg0;
	}

	@Override
	public void setWrappedData(Object arg0) {
        objects = (List) arg0;
        int rowIndex = (objects != null && objects.size() > 0) ? 0 : -1;
        setRowIndex(rowIndex);
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
