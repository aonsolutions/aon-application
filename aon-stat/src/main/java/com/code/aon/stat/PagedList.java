package com.code.aon.stat;

import java.util.List;

import com.code.aon.common.util.CommonUtil;

public class PagedList<E> {

	private List<E> list;
	
	private int offset;
	private int pageSize = 20;
	private boolean nextAvailable;
	
	
	public List<E> getList() {
		return list;
	}
	public void setList(List<E> list) {
		this.list = list;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public boolean isNextAvailable() {
		return nextAvailable;
	}
	public void setNextAvailable(boolean nextAvailable) {
		this.nextAvailable = nextAvailable;
	}
	public boolean isPreviousAvailable() {
		return (getOffset() > getPageSize());
	}
	public void preparePreviousPage() {
		int currentPage = (int) CommonUtil.ceil(getOffset() / getPageSize());
		int previousPage = Math.max(currentPage - 1,0);
		setOffset(previousPage * getPageSize());
	}
	
	
}
