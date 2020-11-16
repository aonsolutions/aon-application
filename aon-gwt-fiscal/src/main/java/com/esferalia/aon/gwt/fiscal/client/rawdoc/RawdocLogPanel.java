package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class RawdocLogPanel extends ScrollPanel  {
	
	private static final String ACTION_DATE = "date";
	private static final String ACTION_USER = "user";
	private static final String ACTION_STATUS = "status";
	private static final String ACTION_REASON = "reason";

	public RawdocLogPanel( String log) {
		if (AonStringUtils.isBlank(log)) {
			Label label = new Label(AON.MSG.noData());
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			setWidget(label);
		} else {
			FlexTable tab = new FlexTable();
			tab.setStyleName(AON.CSS.aonGrid());
			int row = 0;
			int col = 0;
			
			tab.setWidget(row,col, new Label(AON.MSG.date()));
			tab.getCellFormatter().setStyleName(row, col,AON.CSS.aonTextCenter());
			tab.getCellFormatter().addStyleName(row, col,AON.CSS.aonGridHeader());
			tab.getColumnFormatter().setWidth(col, "80px");
			++col;
			tab.setWidget(row,col, new Label(AON.MSG.action()));
			tab.getCellFormatter().setStyleName(row, col,AON.CSS.aonTextCenter());
			tab.getCellFormatter().addStyleName(row, col,AON.CSS.aonGridHeader());
			tab.getColumnFormatter().setWidth(col, "100px");
			++col;
			tab.setWidget(row,col, new Label(AON.MSG.user()));
			tab.getCellFormatter().setStyleName(row, col,AON.CSS.aonTextCenter());
			tab.getCellFormatter().addStyleName(row, col,AON.CSS.aonGridHeader());
			tab.getColumnFormatter().setWidth(col, "100px");
			++col;
			tab.setWidget(row,col, new Label(AON.MSG.comments()));
			tab.getCellFormatter().addStyleName(row, col,AON.CSS.aonGridHeader());
			tab.getColumnFormatter().setWidth(col, "auto");
			++col;
			++row;
			
			JSONArray data = new JSONArray(JsonUtils.safeEval(log));
			for (int i = 0; i < data.size(); i++) {
				JSONValue l = data.get(i);
				JSONObject line = l.isObject();
				
				col = 0;
				JSONValue d = line.get(ACTION_DATE);
				tab.setWidget(row,col, new Label( d == null? "" : d.isString().stringValue() ));
				++col;
				
				JSONValue s = line.get(ACTION_STATUS);
				tab.setWidget(row,col, new Label( s == null? "" : s.isString().stringValue() ));
				++col;
				
				JSONValue u = line.get(ACTION_USER);
				tab.setWidget(row,col, new Label( u == null? "" : u.isString().stringValue() ));
				++col;
				
				JSONValue r = line.get(ACTION_REASON);
				tab.setWidget(row,col, new Label( r == null? "" : r.isString().stringValue() ));
				++col;
				++row;
			}
			setWidget(tab);
		}
	}
	
}
