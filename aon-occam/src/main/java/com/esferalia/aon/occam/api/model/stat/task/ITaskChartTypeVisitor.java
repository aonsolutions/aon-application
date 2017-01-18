package com.esferalia.aon.occam.api.model.stat.task;

public interface ITaskChartTypeVisitor {

	void visitTaskByStatus();
	
	void visitTaskByType();

	void visitTaskByTag();
	
	void visitTaskBySchedule();
	
	void visitTaskByDayOfWeek();
	
	void visitTaskByMonth();
	
	void visitTaskByDay();
}
