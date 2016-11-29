package com.esferalia.aon.gwt.common.client.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.vaadin.polymer.paper.widget.PaperProgress;

public class ProgressPanel extends ResizeComposite implements ProvidesResize {

	public static interface Task {
		String getDescription();
		void addTaskListener(TaskListener listener);
	}

	public static interface TaskListener {
		void finished();
		void messageChanged(String message);
	}

	public static class IndeterminateTask implements Task , TaskListener{
		
		private String description;
		private List<TaskListener> listeners = new ArrayList<TaskListener>(); 
		
		@Override
		public void finished() {
			for( TaskListener listener: listeners)
				listener.finished();
		}
		
		@Override
		public void messageChanged(String message) {
			for( TaskListener listener: listeners)
				listener.messageChanged(message);
		}
		
		// 
		 
		@Override
		public String getDescription() {
			return description;
		}
		
		@Override
		public void addTaskListener(TaskListener listener) {
			listeners.add(listener);
		}
		
		public IndeterminateTask setDescription(String description) {
			this.description = description;
			return this;
		}
		
	}
	
	
	
	interface Binder extends UiBinder<DockLayoutPanel, ProgressPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);


	@UiField 
	FlexTable flexTable;
	
	private List<Task> tasks ;

	public ProgressPanel() {
		tasks = new ArrayList<Task>();
		initWidget(binder.createAndBindUi(this));
	}


	
	public void showIndeterminateTask(final IndeterminateTask indeterminateTask){
		
		tasks.add(indeterminateTask);
		
		int row = flexTable.getRowCount();
		
		flexTable.insertRow(row);
		flexTable.setText(row, 0, indeterminateTask.getDescription());
		
		flexTable.insertRow(++row);
		PaperProgress paperProgress = new PaperProgress();
		paperProgress.setIndeterminate(true);
		paperProgress.addStyleName(AON.AON_WIDTH_ALL);
		
		flexTable.setWidget(row, 0, paperProgress);
		
		
		flexTable.insertRow(++row);
		indeterminateTask.addTaskListener( new TaskListener() {
			
			@Override
			public void finished() {
				int index = tasks.indexOf(indeterminateTask);
				Window.alert("finished " + index );
				flexTable.removeRow(( index * 3 )+2);
				flexTable.removeRow(( index * 3 )+1);
				flexTable.removeRow(( index * 3 )+0);
			}
			
			@Override
			public void messageChanged(String message) {
				int index = tasks.indexOf(indeterminateTask);
				flexTable.setText(( index * 3 )+2, 0, message);
			}
		});
	}

	// ------------------------------------------------------------- UIHandlers
	
	

}
