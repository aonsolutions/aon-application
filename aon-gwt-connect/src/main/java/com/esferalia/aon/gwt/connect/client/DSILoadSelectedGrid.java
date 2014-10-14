package com.esferalia.aon.gwt.connect.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class DSILoadSelectedGrid extends CustomDataGrid<DSILoadSelected> implements
	HasSelectionHandlers<DSILoadSelected>{
	
	interface Listener {
		void onSelectionChangeHandler (SelectionChangeEvent event);
	}
	
	public static class DefaultDSILoadSelected implements DSILoadSelected {

		private String razonSocial;
		
		public DefaultDSILoadSelected(String razonSocial) {
			setRazonSocial(razonSocial);
		}
		
		private void setRazonSocial(String razonSocial) {
			this.razonSocial = razonSocial;
		}
		
		@Override
		public String getName() {
			return razonSocial;
		}
		
		@Override
		public String getCompanyIcon() {			
			return "";
		}

		@Override
		public String getEmployeeIcon() {			
			return "";
		}		
	}
	
	public static class EnterpriseDSILoadSelected extends DefaultDSILoadSelected {
		
		public EnterpriseDSILoadSelected(String razonSocial) {
			super(razonSocial);
		}
		
		@Override
		public String getCompanyIcon() {			
			return AON.AON_CSS.aonIconEnterprise(); 
		}
	}
	
	private static abstract class IconStyleColumn<T extends DSILoadSelected> extends TextColumn<T> {
		
		@Override
		public String getValue(T object) {			
			return " ";
		}
		
		@Override
		public String getCellStyleNames(Context context, T object) {			
			return getIconStyle(context, object);
		}		
		
		public abstract String getIconStyle(Context context, T object);
	}	
		
	//******************************************************
					// TODO: EMPLOYEE
	//******************************************************
	
	private MultiSelectionModel<DSILoadSelected> selectionModel;
	private List<Listener> listeners;

	public DSILoadSelectedGrid() {
		super();
		
		listeners = new ArrayList<DSILoadSelectedGrid.Listener>();
		
		this.selectionModel = new MultiSelectionModel<DSILoadSelected>();

		setHeight("100%");		
		setAutoHeaderRefreshDisabled(false);		
		initializeSelectionModel();
		Header<Boolean> checkHeader = newCheckHeader();		
		Column<DSILoadSelected, Boolean> checkColumn = newCheckColumn();		
		addColumn(checkColumn, checkHeader);		
		setColumnWidth(checkColumn, 40, Unit.PX);		
		initializeColumns();		
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<DSILoadSelected> handler) {		
		return addHandler(handler, SelectionEvent.getType());
	}	
	
	private DSILoadSelected getLoadSelected() {
		return ((SingleSelectionModel<DSILoadSelected>) getSelectionModel())
				.getSelectedObject();		
	}

	private void initializeSelectionModel() {
		
		setSelectionModel(selectionModel, DefaultSelectionEventManager
				.<DSILoadSelected> createCheckboxManager(0));
		selectionModel.addSelectionChangeHandler(new Handler() {			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				for(Listener listener: listeners)
					listener.onSelectionChangeHandler(event);
				SelectionEvent.fire(DSILoadSelectedGrid.this, getLoadSelected());			
			}			
		});
	}

	private void initializeColumns() {
		int col = 0;
		
		addColumn(new TextColumn<DSILoadSelected>() {
			
			@Override
			public String getCellStyleNames(Context context,
					DSILoadSelected load) {				
				return AON.AON_BOLD + " " + AON.AON_BLACK;
			}
			
			@Override
			public String getValue(DSILoadSelected object) {
				return object.getName();
			}			
		});	
		setColumnWidth(col++, 100, Unit.PCT);		
	}
	
	private Header<Boolean> newCheckHeader() {
		
		Header<Boolean> header = new Header<Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue() {				
				return getVisibleItemCount() == ((MultiSelectionModel<?>) 
						getSelectionModel()).getSelectedSet().size();
			}
		};
		header.setUpdater(new ValueUpdater<Boolean>() {			
			@Override
			public void update(Boolean value) {
				for (int i = 0; i < getVisibleItemCount(); i++)
					getSelectionModel().setSelected(getVisibleItem(i), value);				
			}
		});
		return header;
	}
	
	private Column<DSILoadSelected, Boolean> newCheckColumn() {
	
		return new Column<DSILoadSelected, Boolean>(new CheckboxCell()) {

			@Override
			public Boolean getValue(DSILoadSelected object) {				
				return getSelectionModel().isSelected(object);
			}
		};
	}
	
	public Set<DSILoadSelected> getSelectedObject() {
		return selectionModel.getSelectedSet();		
	}
	
	public void clearSelected(DSILoadSelected object) {
		selectionModel.setSelected(object, false);
	}
}
