package com.esferalia.aon.gwt.payroll.client;


public abstract class AbstractSpinnable<E  extends IDocument> implements ISpinnable<E> {
	
	private int current ;
	
	@Override
	public final int currentIndex() {
		return current;
	}
	
	@Override
	public final void first() {
		current = size() > 0 ? 0 : -1; ;
	}
	
	@Override
	public final void last() {
		current = size() -1;
	}
	
	@Override
	public final void next() {
		if ( hasNext () ){
			current++;
		}
	}

	@Override
	public final void previous() {
		if ( hasPrevious () ){
			current--;
		}
	}

	@Override
	public final boolean hasNext() {
		return current + 1 < size();
	}

	@Override
	public final boolean hasPrevious() {
		return current > 0;
	}
	

}
