package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;

public class AonSeriesSuggestBox extends AonSuggestBox implements HasSelectionHandlers<Series>{

	private Series series;
	
	public <T> AonSeriesSuggestBox(AonModuleOptions<?> opts) {
		this(opts, AON.MSG.series(),false);
	}

	public <T> AonSeriesSuggestBox(AonModuleOptions<?> opts, boolean rectifier) {
		this(opts, AON.MSG.series(),rectifier);
	}
	
	public AonSeriesSuggestBox(AonModuleOptions<?> opts, String title, boolean rectifier) {
		super(title, new AonSeriesSuggestOracle(opts,rectifier) );
		this.setAutoSelectEnabled(false);
		this.setPlaceHolder(AON.MSG.registryPlaceHolder());
		getSuggestBox().addSelectionHandler(e -> {
			SeriesSuggestion selected = (SeriesSuggestion) e.getSelectedItem();
			setSeries( selected.getSeries() );	
		});
		getSuggestBox().addSelectionHandler(event -> {
			SeriesSuggestion selected = (SeriesSuggestion) event.getSelectedItem();
			setSeries( selected.getSeries() );
		});
		getSuggestBox().addValueChangeHandler( e -> {
			if ( AonStringUtils.isBlank( getSuggestBox().getValue() )) {
				setSeries( null );	
			}
		});
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Series> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<Series> getSeries() {
		return Optional.ofNullable(series);
	}
	private void setSeries(Series series) {
		setCustomer(series, true);
	}
	private void setCustomer(Series series, boolean fireEvent) {
		this.series = series;
		if (fireEvent) SelectionEvent.fire(AonSeriesSuggestBox.this, this.series );
	}
	
	private static class SeriesSuggestion extends MultiWordSuggestion {
		
		private Series series;
		
		private SeriesSuggestion(Series series, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.series = series;
		}
		
		public Series getSeries() {
			return series;
		}
	}
	
	private static class AonSeriesSuggestOracle extends MultiWordSuggestOracle {
		
		private Timer searchTimer;
		private final AonModuleOptions<?> opts;
		private final boolean rectifier;
		
		private AonSeriesSuggestOracle(AonModuleOptions<?> opts, boolean rectifier) {
			this.opts = opts;
			this.rectifier = rectifier ;
		}
		@Override
		public void requestSuggestions(final Request request,final Callback callback) {
	        if (searchTimer != null && searchTimer.isRunning()) {
	            searchTimer.cancel();
	        }
	        searchTimer = new Timer() {
	        	@Override
	            public void run() {
					SERVICE.getSeries(opts.getOccam(),opts.getDomain(),request.getQuery(),new AsyncCallback<LinkedList<Series>>() {
						public void onSuccess(LinkedList<Series> result) {
							LinkedList<Suggestion> suggestions = AonCollectionUtils.stream(result)
								.filter( s -> s.isInvoice() || s.isRectification() )
								.filter( s -> rectifier == s.isRectification() )
								.map( s -> new SeriesSuggestion( s, s.getCode(),decorate(s, request.getQuery())))
								.collect( Collectors.toCollection(LinkedList::new)); 
							callback.onSuggestionsReady(request, new Response(suggestions));
						}
		
						public void onFailure(Throwable caught) {
							callback.onSuggestionsReady(request, new Response());
						}
					});
	        	}
	        };
	        searchTimer.schedule(300);				
		}

		private static String decorate(Series series, String query) {
			String text = series.getCode();
			int index = AonStringUtils.indexOfIgnoreCase(text, query);

			SafeHtmlBuilder builder = new SafeHtmlBuilder()
				.appendHtmlConstant("<span style=\"font-size: 0.7rem;\" class=\"" 
					+ AON.CSS.aonTabIcon()
					+ AonStringUtils.SPACE 
					+ AON.CSS.aonIconBullet() 
				+ "\">");
			if (index != -1) {
				int end = index + AonStringUtils.length(query);
				builder
					.appendEscaped(AonStringUtils.substring(text, 0, index))
					.appendHtmlConstant(BEGIN_STRONG)
					.appendEscaped(AonStringUtils.substring(text, index, end)).appendHtmlConstant(END_STRONG)
					.appendEscaped(AonStringUtils.substring(text, end));
			} else {
				builder.appendEscaped(text);
			}

			return builder
				.appendHtmlConstant("</span>")
				.toSafeHtml()
				.asString();
		}

	}

}
