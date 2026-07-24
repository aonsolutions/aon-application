package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.HasId;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class IdSelectionHandler<T extends HasId> extends FlowPanel implements HasValueChangeHandlers<Integer>{
	
	private final LinkedHashMap<Integer,T> selectedMap = new LinkedHashMap<>();
	
	private final InlineLabel selectedLabel = new InlineLabel();
	
	public IdSelectionHandler() {
		this.add(selectedLabel);
		refresh();
	}

	public void clean() {
		selectedMap.clear();
		refresh();
		fireEvent( new ValueChangeEvent<Integer>( selectedCount() ) {} );
	}
	
	public void refresh() {
		selectedLabel.setText( AON.MSG.selectedItem( selectedCount() ) );
	}
	
	public int selectedCount() {
		return AonCollectionUtils.size( selectedMap );
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void select(T t) {
		if (t != null) {
			selectedMap.put(t.getId(), t);
			refresh();
			fireEvent( new ValueChangeEvent<Integer>( selectedCount() ) {} );
		}
	}

	public void unselect(T t) {
		if (t != null) {
			selectedMap.remove(t.getId());
			refresh();
			fireEvent( new ValueChangeEvent<Integer>( selectedCount() ) {} );
		}
	}

	public Stream<T> stream() {
		return AonCollectionUtils.valuesStream( selectedMap );
	}
	
	public Integer[] array() {
		return AonCollectionUtils.keysStream( selectedMap ).toArray(Integer[]::new);
	}

	public boolean isEmpty() {
		return AonCollectionUtils.isEmpty( selectedMap);
	}
	public boolean isNotEmpty() {
		return !isEmpty();
	}

}
