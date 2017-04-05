package com.esferalia.aon.gwt.common.client.widget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ProvidesKey;

public class CustomDataGrid<T> extends DataGrid<T> {

	private static Resources DEFAULT_RESOURCES = GWT
			.create(CustomDataGrid.MyResources.class);

	static interface MyResources extends Resources {

		@Override
		@Source("com/esferalia/aon/gwt/common/client/widget/DataGrid.css")
		public Style dataGridStyle();

	}
	
	public CustomDataGrid() {
		super(50, DEFAULT_RESOURCES);
	}
	
	public CustomDataGrid(ProvidesKey<T> keyProvider) {
		super(50, DEFAULT_RESOURCES, keyProvider);
	}

	public CustomDataGrid(int pageSize, ProvidesKey<T> keyProvider) {
		super(pageSize, DEFAULT_RESOURCES, keyProvider);
	}
	public CustomDataGrid(int pageSize, Resources resources, ProvidesKey<T> keyProvider) {
		super(pageSize, resources, keyProvider);
	}


	public ScrollPanel getScrollPanel() {
		HeaderPanel header = (HeaderPanel) getWidget();
		return (ScrollPanel) header.getContentWidget();
	}
	
	
	
}