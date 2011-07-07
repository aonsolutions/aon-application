package com.code.aon.sales.bridge;

import java.util.Date;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.project.Project;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.TasItem;

public class ProjectTasManager {

	public ProjectTas projectTas(Offer offer, String series, int number, Date issueDate, TasItem tasItem) throws ManagerBeanException {
		ProjectTas projectTas = createProjectTas(offer, series, number, issueDate, tasItem);
		updateOfferProject(offer, projectTas.getProject());
		return projectTas;
	}

	private ProjectTas createProjectTas(Offer offer, String series, int number, Date issueDate, TasItem tasItem) throws ManagerBeanException {
		Project project = new Project();
		project.setDate(issueDate);

		ProjectTas projectTas = new ProjectTas();
		projectTas.setProject(project);
		projectTas.setSeries(series);
		projectTas.setNumber((number > 0) ? number : obtainMaxNumber(series));
		projectTas.setTarget(offer.getTarget());
		projectTas.setTasItem(tasItem);
		projectTas.setComments(offer.getComments());

		IManagerBean projectTasBean = BeanManager.getManagerBean(ProjectTas.class);
		return (ProjectTas)projectTasBean.insert(projectTas);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, "ProjectTas");
	}

	private void updateOfferProject(Offer offer, Project project) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		offer.setProject(project);
		offer.setStatus(OfferStatus.APPROVED);
		offerBean.restoreNullSubPOJOs(offer);
		offerBean.update(offer);
	}

}
