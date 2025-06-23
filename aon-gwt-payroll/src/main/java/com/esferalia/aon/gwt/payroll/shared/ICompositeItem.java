package com.esferalia.aon.gwt.payroll.shared;

import java.util.Collection;

public interface  ICompositeItem<T extends Item<?>> {
	Collection<T> getChilds();
}
