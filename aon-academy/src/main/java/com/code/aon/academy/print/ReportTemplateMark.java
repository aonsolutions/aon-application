package com.code.aon.academy.print;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.academy.Course;
import com.code.aon.academy.AcademicSkill;
import com.code.aon.common.ITransferObject;
import com.code.aon.company.resources.Employee;
import com.code.aon.customer.Customer;

public class ReportTemplateMark implements ITransferObject {

	private Course course;
	
	private Employee instructor;
	
	private List<TemplateMarkDetail> details = new ArrayList<TemplateMarkDetail>();

	/**
	/**
	 * @return the course
	 */
	public Course getCourse() {
		return course;
	}

	/**
	 * @param course the course to set
	 */
	public void setCourse(Course course) {
		this.course = course;
	}

	public Employee getInstructor() {
		return instructor;
	}

	public void setInstructor(Employee instructor) {
		this.instructor = instructor;
	}

	/**
	 * @return the details
	 */
	public List<TemplateMarkDetail> getDetails() {
		return details;
	}

	public TemplateMarkDetail addDetail(AcademicSkill academicSkill,Customer customer){
		TemplateMarkDetail detail = new TemplateMarkDetail();
		detail.setAcademicSkill(academicSkill);
		detail.setAlumn(customer);
		details.add(detail);
		return detail;
	}
	
	public class TemplateMarkDetail implements ITransferObject {
		
		private AcademicSkill academicSkill;
		private Customer alumn;
		/**
		 * @return the academicSkill
		 */
		public AcademicSkill getAcademicSkill() {
			return academicSkill;
		}
		/**
		 * @param academicSkill the academicSkill to set
		 */
		public void setAcademicSkill(AcademicSkill academicSkill) {
			this.academicSkill = academicSkill;
		}
		/**
		 * @return the alumn
		 */
		public Customer getAlumn() {
			return alumn;
		}
		/**
		 * @param alumn the alumn to set
		 */
		public void setAlumn(Customer alumn) {
			this.alumn = alumn;
		}
	}
}