/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.esferalia.aon.gwt.template.client;

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
		@Source("com/esferalia/aon/gwt/common/client/css/data-grid.css")
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

	public ScrollPanel getScrollPanel() {
		HeaderPanel header = (HeaderPanel) getWidget();
		return (ScrollPanel) header.getContentWidget();
	}
	
	
	
}