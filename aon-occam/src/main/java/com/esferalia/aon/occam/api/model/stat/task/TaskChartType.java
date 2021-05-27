package com.esferalia.aon.occam.api.model.stat.task;

import java.io.Serializable;

public enum TaskChartType implements Serializable {

	TASK_BY_STATUS("Tareas por estado", new ITypeVisitor() {
		@Override
		public void visit(ITaskChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByStatus();
		}
	})
	,TASK_BY_TYPE("Tareas por tipo", new ITypeVisitor() {
		@Override
		public void visit(ITaskChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByType();
		}
	})
	,TASK_BY_TAG("Tareas por etiqueta", new ITypeVisitor() {
		@Override
		public void visit(ITaskChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByTag();
		}
	})
	,TASK_BY_SCHEDULE("Tareas por franja horaria", new ITypeVisitor() {
		@Override
		public void visit(ITaskChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskBySchedule();
		}
	})
	,TASK_BY_DAY_OF_WEEK("Tareas por dia de la semana", new ITypeVisitor() {
		@Override
		public void visit(ITaskChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByDayOfWeek();
		}
	})
	,TASK_BY_MONTH("Tareas por mes", new ITypeVisitor() {
		@Override
		public void visit(ITaskChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByMonth();
		}
	})
	,TASK_BY_DAY("Tareas por dia", new ITypeVisitor() {
		@Override
		public void visit(ITaskChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByDay();
		}
	})
	,TASK_BY_CUSTOMER("Tareas por cliente", new ITypeVisitor(){
		@Override
		public void visit(ITaskChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByCustomer();
		}
	})
	;
	
	public static interface ITypeVisitor {
		void visit(ITaskChartTypeVisitor chartVisitor);
	}
	
	private String description;
	private ITypeVisitor typeVisitor;
	
	private TaskChartType(String description,ITypeVisitor typeVisitor ) {
		this.description = description;
		this.typeVisitor = typeVisitor;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void visit(ITaskChartTypeVisitor chartVisitor) {
		typeVisitor.visit(chartVisitor);
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}

}
