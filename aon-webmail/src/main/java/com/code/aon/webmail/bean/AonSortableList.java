package com.code.aon.webmail.bean;

import java.io.Serializable;

import com.code.aon.common.AonVersion;

public abstract class AonSortableList implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    protected String sort;
    protected boolean ascending;

    protected String oldSort;
    protected boolean oldAscending;

    public AonSortableList(String defaultSortColumn) {
        sort = defaultSortColumn;
        ascending = isDefaultAscending(defaultSortColumn);
        oldSort = sort;
        oldAscending = !ascending;
    }

    /**
     * Sort the list.
     */
    protected abstract void sort(String column, boolean ascending);

    /**
     * Is the default sort direction for the given column "ascending" ?
     */
    protected abstract boolean isDefaultAscending(String sortColumn);

    /**
     * Gets the sort column.
     *
     * @return column to sort
     */
    public String getSort() {
        return sort;
    }

    /**
     * Sets the sort column
     *
     * @param sort column to sort
     */
    public void setSort(String sort) {
        oldSort = this.sort;
        this.sort = sort;
    }

    /**
     * Is the sort ascending.
     *
     * @return true if the ascending sort otherwise false.
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * Set sort type.
     *
     * @param ascending true for ascending sort, false for desending sort.
     */
    public void setAscending(boolean ascending) {
        oldAscending = this.ascending;
        this.ascending = ascending;
    }
    
}