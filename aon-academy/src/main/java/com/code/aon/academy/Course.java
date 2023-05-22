package com.code.aon.academy;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.CourseDB;

/**
 * The Class Course.
 */
@Entity
@Table(name = "course")
public class Course extends CourseDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<CourseAlumn> alumns = new HashSet<CourseAlumn>();

    public Course() {
    	setStatus( CourseStatus.ACTIVE );
    }

	@OneToMany(mappedBy = "course", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<CourseAlumn> getAlumns() {
		return this.alumns;
	}
	public void setAlumns(Set<CourseAlumn> alumns) {
		this.alumns = alumns;
	}
	
	@Transient
	public Course getCourse(){
		return this;
	}
	
	@Transient
	public Integer getAlumnCount() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CourseAlumn.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
		return bean.getCount(criteria);
	}
	
    @Transient
    public String getFullDescription() {
    	return getCode() + ( StringUtils.isEmpty(getDescription()) ? "" : " " + getDescription() );
    }
	

}