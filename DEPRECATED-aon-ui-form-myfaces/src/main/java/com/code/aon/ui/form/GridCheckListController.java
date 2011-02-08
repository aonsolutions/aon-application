package com.code.aon.ui.form;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.sortheader.HtmlCommandSortHeader;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

/**
 * Controller for grid maintenances.
 */
public class GridCheckListController extends GridController {

	/** The name of the column, used to order the model */
	private String sort;

	/** Ascending. */
	private boolean ascending = true;
	
	/**
	 * The empty Constructor.
	 */
	public GridCheckListController( ) {
		super.setNew( false );
	}

	/**
	 * Sets the sort when creates the controller
	 * 
	 * @param sortColumnName the sort column name
	 */
	public GridCheckListController( String sortColumnName) {
		this();
		this.sort = sortColumnName;
	}
	

	/**
	 * Sorts the model using <code>sort</code> column.
	 * 
	 * @param event the event
	 */
	public void sort(ActionEvent event) {
		try {
			String sortColumn = null;
			UIComponent c = event.getComponent();
			if (c instanceof HtmlCommandSortHeader) {
				HtmlCommandSortHeader h = (HtmlCommandSortHeader) c;
				sortColumn = h.getColumnName();
			} else {
				String msg = "Unable to acquire columns sort identifier";
				addMessage(msg);
				throw new AbortProcessingException(msg);
			}

			if (sortColumn == null) {
				String msg = "Argument sortColumn must not be null.";
				addMessage(msg);
				throw new AbortProcessingException(msg);
			}

			if (sort.equals(sortColumn)) {
//				ascending = !ascending;
			} else {
				sort = sortColumn;
//				ascending = true;
			}
			Criteria criteria = new Criteria();
			criteria.addOrder(getFieldName(sortColumn), !isAscending());
			super.setCriteria(criteria);
			super.onSearch(null);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	/**
	 * Gets sort.
	 * 
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * Sets sort.
	 * 
	 * @param sort the sort
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * Checks if is ascending.
	 * 
	 * @return true, if is ascending
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * Sets the ascending.
	 * 
	 * @param ascending the ascending
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onAccept(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onAccept(ActionEvent event) {
		super.accept(event);
		super.onReset(event);
	}
	
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onRemove(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onRemove(ActionEvent event) {
		super.remove(event);
		super.onReset(event);
	}

}
