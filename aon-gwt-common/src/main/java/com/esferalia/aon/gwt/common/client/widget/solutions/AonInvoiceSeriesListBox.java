package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ListBox;

public class AonInvoiceSeriesListBox extends ListBox implements HasSelectionHandlers<String> {

	private LinkedList<String> seriesList;
	private String series;
	
	public <T> AonInvoiceSeriesListBox(AonModuleOptions<?> opts) {
		this(opts, false);
	}

	public <T> AonInvoiceSeriesListBox(AonModuleOptions<?> opts, boolean rectifier) {
		addItem( "------", "" );
		if ( opts != null && opts.getConfiguration() != null ) {
			seriesList = (rectifier)
				? opts.getConfiguration().getInvoiceRectificationSalesSeries()
				: opts.getConfiguration().getInvoiceSalesSeries();
			AonCollectionUtils.stream( seriesList )
				.forEach( s -> addItem( s, s ));
		}
		addChangeHandler(e -> {
			setSeries( getSelectedValue() );
		});
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<String> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<String> getSeries() {
		return Optional.ofNullable(series);
	}
	
	public void setSeries(String series) {
		setSeries(series, true);
	}

	public void setSeries(String series, boolean fireEvents) {
		this.series = AonStringUtils.trimToNull(series);
		for (int i = 0; i < seriesList.size(); i++) {
			if (AonStringUtils.equals(seriesList.get(i), this.series)) {
				setSelectedIndex(i + 1);
				break;
			}
		}
		if (fireEvents) {
			SelectionEvent.fire(this, this.series);
		}		
	}
	
}
