package com.esferalia.aon.gwt.common.client.widget.cell;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class TabCheckboxCell extends CheckboxCell {

  private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\"  checked/>");
  private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" />");

  public TabCheckboxCell() {
    super();
  }

  @Override
  public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
    // Get the view data.
    Object key = context.getKey();
    Boolean viewData = getViewData(key);
    if (viewData != null && viewData.equals(value)) {
      clearViewData(key);
      viewData = null;
    }

    if (value != null && ((viewData != null) ? viewData : value)) {
      sb.append(INPUT_CHECKED);
    } else {
      sb.append(INPUT_UNCHECKED);
    }
  }
  
}
