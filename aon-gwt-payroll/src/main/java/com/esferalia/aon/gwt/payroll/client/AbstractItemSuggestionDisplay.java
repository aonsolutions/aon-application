package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public abstract class AbstractItemSuggestionDisplay<T extends Item<?>> extends
		DefaultSuggestionDisplay {

	@Override
	protected void showSuggestions(SuggestBox suggestBox,
			Collection<? extends Suggestion> suggestions,
			boolean isDisplayStringHTML, boolean isAutoSelectEnabled,
			SuggestionCallback callback) {

		Collection<Suggestion> mySuggestions = new ArrayList<Suggestion>(
				suggestions.size());

		for (Suggestion suggestion : suggestions) {
			String replacementString = suggestion.getReplacementString();
			T item = getItem(replacementString);

			SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

			String clazz = "concept";
//			if (StringUtils.isEmpty(item.getName()))
//				clazz = item.getScope() == Scope.CONTRACT ? "employee"
//						: "enterprise";
//			else
//				clazz = "concept";

			htmlBuilder.appendHtmlConstant("<span class=\"" + clazz + "\" >");
			htmlBuilder.appendHtmlConstant(suggestion.getDisplayString());
			htmlBuilder.appendHtmlConstant("</span>");

			mySuggestions.add(new MultiWordSuggestOracle.MultiWordSuggestion(
					replacementString, htmlBuilder.toSafeHtml().asString()));
		}

		super.showSuggestions(suggestBox, mySuggestions, isDisplayStringHTML,
				isAutoSelectEnabled, callback);
	}

	abstract T getItem(String replacementString);

}
