package com.code.aon.report.config;

/**
 * Configuration for report fetch mode.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * 
 */
public class ReportFetchMode {

	/**
	 * The report is paginated?
	 */
	private boolean paginated;

	/**
	 * Number of beans that must be recovered in each IFinderBean.getList()
	 * method invocation.
	 */
	private int pageCount;

	/**
	 * The maximum size (in
	 * <code>net.sf.jasperreports.engine.JRVirtualizable</code> objects) of
	 * the paged in cache.
	 */
	private int virtualizerPageMax;

	/**
	 * Returns the number of beans that must be recovered in each
	 * IFinderBean.getList() method invocation.
	 * 
	 * @return The number of beans.
	 * @see com.code.aon.common.IFinderBean#getList(com.code.aon.ql.Criteria ,
	 *      int, int)
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * Sets the number of beans that must be recovered in each
	 * IFinderBean.getList() method invocation.
	 * 
	 * @param pageCount
	 *            The number of beans.
	 * @see com.code.aon.common.IFinderBean#getList(com.code.aon.ql.Criteria ,
	 *      int, int)
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * Returns if the fetch mode is paginated.
	 * 
	 * @return if the fetch mode is paginated.
	 */
	public boolean isPaginated() {
		return paginated;
	}

	/**
	 * Sets if the fetch mode is paginated.
	 * 
	 * @param paginated
	 *            True if is paginated, false otherwise.
	 */
	public void setPaginated(boolean paginated) {
		this.paginated = paginated;
	}

	/**
	 * Gets the maximum size (in
	 * <code>net.sf.jasperreports.engine.JRVirtualizable</code> objects) of
	 * the paged in cache.
	 * 
	 * @return The maximum size.
	 */
	public int getVirtualizerPageMax() {
		return virtualizerPageMax;
	}

	/**
	 * Sets the maximum size (in
	 * <code>net.sf.jasperreports.engine.JRVirtualizable</code> objects) of
	 * the paged in cache.
	 * 
	 * @param virtualizerPageMax
	 *            The maximum size.
	 */
	public void setVirtualizerPageMax(int virtualizerPageMax) {
		this.virtualizerPageMax = virtualizerPageMax;
	}

}
