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

	@SuppressWarnings("unused")
    public void onSelect(ActionEvent event) {
    	setTo((AlumnMarks) this.result.getRowData());
    }

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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
    	courseAcademicSkillCriteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMICSKILL_COURSE_ID), course.getId());
    	academicSkills = courseAcademicSkillBean.getList(courseAcademicSkillCriteria);
    	alumnMarkHeaders = new LinkedList<AlumnMarkHeader>();
    	for (int i = 0; i < academicSkills.size(); i++){
        	AlumnMarkHeader alumnMarkHeader = new AlumnMarkHeader();
        	alumnMarkHeader.setPosition(i);
        	alumnMarkHeader.setDescription(((CourseAcademicSkill)academicSkills.get(i)).getAcademicSkill().getDescription());
        	alumnMarkHeaders.add(i,alumnMarkHeader);
    	}
	}
	
	@SuppressWarnings("unused")
	public void onSearch(ActionEvent event) throws ManagerBeanException {
		initCourse();
    	IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
    	IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
    	Criteria courseAlumnCriteria = new Criteria();
    	courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
    	courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
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
        	markList = markBean.getList(markCriteria);
        	markIter = markList.iterator();
        	markMap = initMarks(courseAlumn);
        	while (markIter.hasNext()){
        		Mark mark = (Mark)markIter.next();
        		markMap.put(mark.getSubject().getId(), mark);
        	}
        	AlumnMarks alumnMarks = new AlumnMarks();
        	alumnMarks.setCustomer(courseAlumn.getCustomer());
        	alumnMarks.setValues(markMap.values().toArray());
        	alumnMarksList.add(alumnMarks);
    	}
		setResult(new ListDataModel(alumnMarksList));
    }

    private Map<Integer, Mark> initMarks(CourseAlumn alumn){
    	Iterator<ITransferObject> iter = academicSkills.iterator();
    	Map<Integer, Mark> map = new HashMap<Integer, Mark>();
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
    
    public class AlumnMarkHeader{
    	
    	private int position; 
    	
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
