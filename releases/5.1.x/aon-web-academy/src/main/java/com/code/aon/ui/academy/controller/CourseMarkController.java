package com.code.aon.ui.academy.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.Mark;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.model.AlumnMarks;

public class CourseMarkController{

	private Course course;
	
	private int evaluation = 1;
	
	List<ITransferObject> academicSkills;
	
	private List<AlumnMarkHeader> alumnMarkHeaders;
	
	private DataModel result;
	
	private AlumnMarks to;

    /**
	 * @return the to
	 */
	public AlumnMarks getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(AlumnMarks to) {
		this.to = to;
	}

    public void onSelect(ActionEvent event) {
    	setTo((AlumnMarks) this.result.getRowData());
    }

    public void onAccept(ActionEvent event) throws ManagerBeanException {
    	Object[] marks = getTo().getValues();
    	Mark mark;
    	IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
    	for (int i = 0; i < marks.length; i++){
    		mark = (Mark)marks[i];
    		if (mark.getId()==null){
    			markBean.insert(mark);
    		}else{
    			markBean.update(mark);
    		}
    	}
    	resetTo();
    }

    public void onCancel(ActionEvent event) {
    	resetTo();
    }

    private void resetTo(){
        this.to = null;
    }
    
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

	/**
	 * @return the evaluation
	 */
	public int getEvaluation() {
		return evaluation;
	}

	/**
	 * @param evaluation the evaluation to set
	 */
	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	/**
	 * @return the result
	 */
	public DataModel getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(DataModel result) {
		this.result = result;
	}

	/**
	 * @return the alumnMarkHeaders
	 */
	public List<AlumnMarkHeader> getAlumnMarkHeaders() {
		return alumnMarkHeaders;
	}

	/**
	 * @param alumnMarkHeaders the alumnMarkHeaders to set
	 */
	public void setAlumnMarkHeaders(List<AlumnMarkHeader> alumnMarkHeaders) {
		this.alumnMarkHeaders = alumnMarkHeaders;
	}

	public void initCourse() throws ManagerBeanException{
    	IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
    	Criteria courseAcademicSkillCriteria = new Criteria();
    	courseAcademicSkillCriteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), course.getId());
    	academicSkills = courseAcademicSkillBean.getList(courseAcademicSkillCriteria);
    	alumnMarkHeaders = new LinkedList<AlumnMarkHeader>();
    	for (int i = 0; i < academicSkills.size(); i++){
        	AlumnMarkHeader alumnMarkHeader = new AlumnMarkHeader();
        	alumnMarkHeader.setPosition(i);
        	alumnMarkHeader.setCode(((CourseAcademicSkill)academicSkills.get(i)).getAcademicSkill().getCode());
        	alumnMarkHeader.setDescription(((CourseAcademicSkill)academicSkills.get(i)).getAcademicSkill().getDescription());
        	alumnMarkHeaders.add(i,alumnMarkHeader);
    	}
	}

	public void onSearch(ActionEvent event) throws ManagerBeanException {
		initCourse();
    	IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
    	IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
    	Criteria courseAlumnCriteria = new Criteria();
    	courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
    	courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
    	courseAlumnCriteria.addOrder(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_SURNAME));
    	courseAlumnCriteria.addOrder(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
    	List<ITransferObject> courseAlumnList = courseAlumnBean.getList(courseAlumnCriteria);
    	Iterator<ITransferObject> courseAlumnIter = courseAlumnList.iterator();
    	Criteria markCriteria;
    	List<ITransferObject> markList;
    	Iterator<ITransferObject> markIter;
    	Map<Integer, Mark> markMap;
    	List<AlumnMarks> alumnMarksList = new LinkedList<AlumnMarks>();
    	while (courseAlumnIter.hasNext()){
    		CourseAlumn courseAlumn = (CourseAlumn)courseAlumnIter.next();
        	markCriteria = new Criteria();
        	markCriteria.addEqualExpression(markBean.getFieldName(IAcademyAlias.MARK_ALUMN_ID), courseAlumn.getId());
        	markCriteria.addEqualExpression(markBean.getFieldName(IAcademyAlias.MARK_EVALUATION), new Integer(evaluation));
        	markCriteria.addOrder(markBean.getFieldName(IAcademyAlias.MARK_SUBJECT_ID));
        	markList = markBean.getList(markCriteria);
        	markIter = markList.iterator();
        	markMap = initMarks(courseAlumn);
        	while (markIter.hasNext()){
        		Mark mark = (Mark)markIter.next();
        		markMap.put(mark.getSubject().getId(), mark);
        	}
        	AlumnMarks alumnMarks = new AlumnMarks();
        	alumnMarks.setCustomer(courseAlumn.getCustomer());
        	alumnMarks.setValues(obtainOrderedValues(markMap));
        	alumnMarksList.add(alumnMarks);
    	}
		setResult(new ListDataModel(alumnMarksList));
    }

    private Object[] obtainOrderedValues(Map<Integer, Mark> markMap) {
    	Object[] array = new Object[academicSkills.size()];
    	for(int i = 0; i<academicSkills.size();i++){
    		CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)academicSkills.get(i);
    		array[i] = markMap.get(courseAcademicSkill.getId());
    	}
		return array;
	}

	private Map<Integer, Mark> initMarks(CourseAlumn alumn){
    	Map<Integer, Mark> map = new HashMap<Integer, Mark>();
    	Iterator<ITransferObject> iter = academicSkills.iterator();
    	while (iter.hasNext()){
    		Mark mark = new Mark();
    		mark.setAlumn(alumn);
    		mark.setEvaluation(evaluation);
    		CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)iter.next();
    		mark.setSubject(courseAcademicSkill);
    		map.put(courseAcademicSkill.getId(), mark);
    	}
    	return map;
    }

	public Double getAverageMark() {
		boolean printAverage = false;
		Double averageMark = 0.0;
		Double weightSum = 0.0;
		if (result.isRowAvailable()) {
			AlumnMarks marks = (AlumnMarks)result.getRowData();
			for(int i=0; i<marks.getValues().length; i++) {
				Mark mark = (Mark)marks.getValues()[i];
				if (mark.getMark() != null) {
					printAverage = true;
					averageMark += mark.getMark() * mark.getSubject().getWeight();
					weightSum += mark.getSubject().getWeight();
				}
			}
		}
		return (!printAverage || weightSum == 0)?null:round(averageMark / weightSum, 2);
	}

	private double round(double value, int precision) {
		return Math.round(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	public class AlumnMarkHeader{
    	
    	private int position; 
    	
    	private String code;
    	
    	private String description;

		/**
		 * @return the position
		 */
		public int getPosition() {
			return position;
		}

		/**
		 * @param position the position to set
		 */
		public void setPosition(int position) {
			this.position = position;
		}
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}
    }
}