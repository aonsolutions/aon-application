package com.esferalia.aon.gwt.common.client.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.vaadin.polymer.paper.widget.PaperProgress;

public class ProgressPanel extends ResizeComposite implements ProvidesResize {

	public static interface TaskListener {
		void finished();
		void messageChanged(String message);
		void progressChanged(double progress);
	}

	public static class Task implements TaskListener{
		
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
		
		@Override
		public void progressChanged(double progress) {
			for( TaskListener listener: listeners)
				listener.progressChanged(progress);
		}
		
		// 
		 
		public String getDescription() {
			return description;
		}
		
		public void addTaskListener(TaskListener listener) {
			listeners.add(listener);
		}
		
		public Task setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public boolean canStop() {
			return false;
		}
		
	}
	
	public static class TimeTask extends Task {
		private long startTimeMillis ;
		private long endTimeMillis ;
		
		public void startTime() {
			startTimeMillis = System.currentTimeMillis();
		}
	
		public void endTime() {
			endTimeMillis = System.currentTimeMillis();
		}
		
		public long getTimeMillis() {
			return endTimeMillis - startTimeMillis;
		}

		public long getTimeSeconds() {
			return (long) (getTimeMillis() / 1000.00);
		}
	}
	
	
	
	interface Binder extends UiBinder<DockLayoutPanel, ProgressPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);


	@UiField 
	FlexTable flexTable;
	
	private Set<Task> tasks ;

	public ProgressPanel() {
		tasks = new HashSet<Task>();
		initWidget(binder.createAndBindUi(this));
	}


	
	public void showTask(final Task task){
		
		tasks.add(task);
		
		int row = flexTable.getRowCount();
		
		int descriptionRow = row;
		flexTable.insertRow(descriptionRow);
		flexTable.setText(descriptionRow, 0, task.getDescription());
		
		int progressRow = row + 1;
		flexTable.insertRow(progressRow);
		PaperProgress paperProgress = new PaperProgress();
		paperProgress.setMax(100.00);
		paperProgress.setIndeterminate(true);
		paperProgress.addStyleName(AON.AON_WIDTH_ALL);
		
		Button cleanButton = new Button();
		cleanButton.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		cleanButton.addStyleName(AON.AON_ICON_DELETE);
		cleanButton.addClickHandler(e -> cleanTask(row));
		cleanButton.setVisible(task.canStop());
		
		flexTable.setWidget(progressRow, 0, paperProgress);
		flexTable.setWidget(progressRow, 1, cleanButton);
		flexTable.getColumnFormatter().addStyleName(0, AON.AON_WIDTH_ALL);
		
				
		
		int messageRow = row + 2;
		flexTable.insertRow(messageRow);
		task.addTaskListener( new TaskListener() {
			
			@Override
			public void finished() {
				paperProgress.setIndeterminate(false);
				paperProgress.setValue(paperProgress.getMax());
			}
			
			@Override
			public void messageChanged(String message) {
				flexTable.setText(messageRow, 0, message);
			}
			
			@Override
			public void progressChanged(double progress) {
				paperProgress.setIndeterminate(false);
				paperProgress.setValue(progress);
			}
		});
	}

	// ------------------------------------------------------------- UIHandlers
	
	private void cleanTask( int row) {
		flexTable.removeRow(row + 2);
		flexTable.removeRow(row + 1);
		flexTable.removeRow(row + 0);
	}
	
	

}
