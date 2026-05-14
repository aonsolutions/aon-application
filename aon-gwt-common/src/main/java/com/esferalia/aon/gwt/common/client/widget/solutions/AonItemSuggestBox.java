package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.product.Item;
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

public class AonItemSuggestBox extends AonSuggestBox implements HasSelectionHandlers<Item>{

	private Item item;
	
	public <T> AonItemSuggestBox(AonModuleOptions<?> opts) {
		this(opts, AON.MSG.item());
	}
	
	public AonItemSuggestBox(AonModuleOptions<?> opts, String title) {
		super(title, new AonItemSuggestOracle(opts) );
		this.setAutoSelectEnabled(false);
		this.setPlaceHolder(AON.MSG.itemPlaceHolder());
		getSuggestBox().addSelectionHandler(e -> {
			ItemSuggestion selected = (ItemSuggestion) e.getSelectedItem();
			setItem( selected.getItem() );	
		});
		getSuggestBox().addSelectionHandler(event -> {
			ItemSuggestion selected = (ItemSuggestion) event.getSelectedItem();
			setItem( selected.getItem() );
		});
		getSuggestBox().addValueChangeHandler( e -> {
			if ( AonStringUtils.isBlank( getSuggestBox().getValue() )) {
				setItem( null );	
			}
		});
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Item> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<Item> getItem() {
		return Optional.ofNullable(item);
	}
	private void setItem(Item item) {
		setItem(item, true);
	}
	private void setItem(Item item, boolean fireEvent) {
		this.item = item;
		if (fireEvent) SelectionEvent.fire(AonItemSuggestBox.this, this.item );
	}
	
	private static class ItemSuggestion extends MultiWordSuggestion {
		
		private Item item;
		
		private ItemSuggestion(Item item, String replacementString, String displayString) {
			super( replacementString, displayString );
			this.item = item;
		}
		
		public Item getItem() {
			return item;
		}
	}
	
	private static class AonItemSuggestOracle extends MultiWordSuggestOracle {
		private Timer searchTimer;
		private AonModuleOptions<?> opts;
		private AonItemSuggestOracle(AonModuleOptions<?> opts) {
			this.opts = opts;
		}
		@Override
		public void requestSuggestions(final Request request,final Callback callback) {
			String query = request.getQuery();
	        int length = AonStringUtils.length(query);
	        if (length < MIN_CHARACTERS || length > MAX_CHARACTERS) return;
	        if (searchTimer != null && searchTimer.isRunning()) {
	            searchTimer.cancel();
	        }
	        searchTimer = new Timer() {
	        	@Override
	            public void run() {
		        	SERVICE.getItems(opts.getOccam(),opts.getDomain(),request.getQuery(),new AsyncCallback<LinkedList<Item>>() {
						@Override
						public void onSuccess(LinkedList<Item> result) {
							LinkedList<Suggestion> suggestions = AonCollectionUtils.stream(result)
								.map( i -> new ItemSuggestion( i, i.getFullName(), decorate(i, request.getQuery())))
								.collect( Collectors.toCollection(LinkedList::new)); 
							callback.onSuggestionsReady(request, new Response(suggestions));
						}
		
						@Override
						public void onFailure(Throwable caught) {
							callback.onSuggestionsReady(request, new Response());
						}
					});
	        	}
	        };
	        searchTimer.schedule(300);				
		}

		private static String decorate(Item item, String query) {
			String text = item.getFullName();
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
