package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class RawdocLogPanel extends ScrollPanel  {
	
	RawdocLogPanel( String log) {
		if (AonStringUtils.isBlank(log)) {
			Label label = new Label(AON.MSG.noData());
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			setWidget(label);
		} else {
			AonDisplayGrid grid = new AonDisplayGrid();
			grid.addStyleName( AON.CSS.aonBlockCenter() );
			grid.addHeaderRow()
				.addCell( new Label(AON.MSG.date()), AON.CSS.aonWidth80(), AON.CSS.aonTextCenter())
				.addCell( new Label(AON.MSG.action()), AON.CSS.aonWidth100(), AON.CSS.aonTextCenter())
				.addCell( new Label(AON.MSG.user()), AON.CSS.aonWidth100(), AON.CSS.aonTextCenter())
				.addCell( new Label(AON.MSG.comments()), AON.CSS.aonWidthAuto(), AON.CSS.aonTextCenter())
			;
			JSONArray data = new JSONArray(JsonUtils.safeEval(log));
			for (int i = 0; i < data.size(); i++) {
				JSONValue l = data.get(i);
				JSONObject json = l.isObject();
				if (json != null) {
					grid.addRow()
					.addCell( new Label(getValue(json,IJsonNames.DATE)))
					.addCell( new Label(getValue(json,IJsonNames.STATUS)))
					.addCell( new Label(getValue(json,IJsonNames.USER)))
					.addCell( new Label(getCommentValue(json)))
				;
				}
			}
			setWidget(grid);
		}
	}
	
	private String getCommentValue(JSONObject json) {
		String comment = getValue(json,IJsonNames.COMMENT);
		if (AonStringUtils.isBlank(comment)) {
			comment = getValue(json, IJsonNames.REASON);
		}
		return comment;
	}
	
	private String getValue(JSONObject json, String attr) {
		JSONValue v = json.get(attr);
		return v == null? "" : v.isString().stringValue();
	}
	
}
