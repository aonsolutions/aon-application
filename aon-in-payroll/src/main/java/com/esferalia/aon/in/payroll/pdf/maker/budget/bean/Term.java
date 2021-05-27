package com.esferalia.aon.in.payroll.pdf.maker.budget.bean;

public class Term {

	private String title;
	private String description;
	
	public Term(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Term :\t\n{ \n\ttitle: \t\t" + title + ", \n\tdescription: \t\t" + description + "\n}";
	}
	
	
}
