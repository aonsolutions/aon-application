package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Enterprise implements Serializable, HasId<Integer>, HasName<String> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2430564873016945740L;
	
	private int			id;
	
	private String 			name;
	private List<Activity>	activities;
	private List<Workplace> workplaces;
	
	public Enterprise() {
		workplaces = new LinkedList<Workplace>();
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public List<Workplace> getWorkplaces() {
		return workplaces;
	}
	
	public void addWorkplace(Workplace workplace ) {
		workplaces.add(workplace);
	}
	
	public List<Activity> getActivities() {
		return activities;
	}
	
	
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}
	
	public void addActivity(Activity activity ) {
		activities.add(activity);
	}

	public static boolean isGPS(Enterprise enterprise) {
		return isGPS(enterprise.getName());
	}
	
	public static boolean isGPS(String name ) {
		return name != null && name.matches(".*GO\\s*LAM\\s*SEC.*");
	}
	
	
}
