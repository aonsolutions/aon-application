package com.code.aon.ui.academy.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.Mark;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.model.AlumnMarkHeader;
import com.code.aon.ui.academy.model.AlumnMarks;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.esferalia.aon.entity.IEntityAlias;

public class CourseMarkController extends DataScrollerState {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Course course;
	
	private int evaluation;
	
	private List<ITransferObject> academicSkills;
	
	private List<AlumnMarkHeader> alumnMarkHeaders;
	
	private boolean isNew;
	
	private AlumnMarks to;

    public CourseMarkController() {
    	this.evaluation = 1;
    }

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
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
    	setTo((AlumnMarks) getDirectModel().getRowData());
    }

    public void onAccept(ActionEvent event) throws ManagerBeanException {
    	IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
    	for ( Mark mark : getTo().getValues() ) {
    		if ( mark.getMark() != null ) {
    			markBean.insertOrUpdate(mark);
    		} else if (mark.getId()!=null){
        		markBean.remove(mark);    			
    		}
    	}
    	resetTo();
    }

    public void onCancel(ActionEvent event) {
    	resetTo();
    }

	public void onRemove(ActionEvent event) throws ManagerBeanException {
    	IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
    	for ( Mark mark : getTo().getValues() ) {
    		if (mark.getId()!=null){
    			markBean.remove(mark);
    			mark.setMark(null);
    		}
    	}
		resetTo();
	}
    
    private void resetTo() {
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

	public void onEvaluationChange(ValueChangeEvent event) throws ManagerBeanException {
		setEvaluation( (Integer) event.getNewValue() );
		refresh();
	}	
		
	public void initCourse() throws ManagerBeanException{
    	IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
    	Criteria courseAcademicSkillCriteria = new Criteria();
    	courseAcademicSkillCriteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IEntityAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), course.getId());
    	academicSkills = courseAcademicSkillBean.getList(courseAcademicSkillCriteria);
    	alumnMarkHeaders = new LinkedList<AlumnMarkHeader>();
    	for (int i = 0; i < academicSkills.size(); i++) {
    		AcademicSkill academicSkill = ((CourseAcademicSkill)academicSkills.get(i)).getAcademicSkill();
        	AlumnMarkHeader alumnMarkHeader = new AlumnMarkHeader();
        	alumnMarkHeader.setPosition(i);
        	alumnMarkHeader.setCode(academicSkill.getCode());
        	alumnMarkHeader.setDescription(academicSkill.getDescription());
        	alumnMarkHeaders.add(i,alumnMarkHeader);
    	}
	}

	public void refresh() throws ManagerBeanException {
		initCourse();
    	IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
    	IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
    	Criteria courseAlumnCriteria = new Criteria();
    	courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId());
    	courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
    	courseAlumnCriteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
    	List<AlumnMarks> alumnMarksList = new LinkedList<AlumnMarks>();
    	for( ITransferObject to : courseAlumnBean.getList(courseAlumnCriteria) ) {
    		CourseAlumn courseAlumn = (CourseAlumn) to;
    		Criteria markCriteria = new Criteria();
        	markCriteria.addEqualExpression(markBean.getFieldName(IEntityAlias.MARK_ALUMN_ID), courseAlumn.getId());
        	markCriteria.addEqualExpression(markBean.getFieldName(IEntityAlias.MARK_EVALUATION), new Integer(evaluation));
        	markCriteria.addOrder(markBean.getFieldName(IEntityAlias.MARK_SUBJECT_ID));
        	List<ITransferObject> markList = markBean.getList(markCriteria);
        	Iterator<ITransferObject> markIter = markList.iterator();
        	Map<Integer, Mark> markMap = initMarks(courseAlumn);
        	while (markIter.hasNext()){
        		Mark mark = (Mark)markIter.next();
        		markMap.put(mark.getSubject().getId(), mark);
        	}
        	AlumnMarks alumnMarks = new AlumnMarks();
        	alumnMarks.setCustomer(courseAlumn.getCustomer());
        	alumnMarks.setValues(obtainOrderedValues(markMap));
        	alumnMarksList.add(alumnMarks);
    	}
		setModel(new SerializableListDataModel(alumnMarksList));
    }

    private Mark[] obtainOrderedValues(Map<Integer, Mark> markMap) {
    	Mark[] array = new Mark[academicSkills.size()];
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
		if (getDirectModel().isRowAvailable()) {
			AlumnMarks marks = (AlumnMarks) getDirectModel().getRowData();
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

}