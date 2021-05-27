package com.esferalia.aon.gwt.payroll.client;


public abstract class AbstractSpinnable<E  extends IDocument> implements ISpinnable<E> {
	
	private int currentIndex ;
	
	@Override
	public final void first() {
		currentIndex = size() > 0 ? 0 : -1; ;
	}
	
	@Override
	public final void last() {
		currentIndex = size() -1;
	}
	
	@Override
	public final void next() {
		if ( hasNext () ){
			currentIndex++;
		}
	}

	@Override
	public final void previous() {
		if ( hasPrevious () ){
			currentIndex--;
		}
	}

	@Override
	public final boolean hasNext() {
		return currentIndex + 1 < size();
	}

	@Override
	public final boolean hasPrevious() {
		return currentIndex > 0;
	}
	
	@Override
	public final int getCurrentIndex() {
		return currentIndex;
	}
	
	public void setCurrentIndex(int currentIndex) {
		if ( currentIndex < 0 ) 
			throw new IndexOutOfBoundsException();
		if ( currentIndex >= size() ) 
			throw new IndexOutOfBoundsException();
		
		this.currentIndex = currentIndex;
	}
	
}
