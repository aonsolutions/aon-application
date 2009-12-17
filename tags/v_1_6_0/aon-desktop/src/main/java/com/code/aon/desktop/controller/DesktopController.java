package com.code.aon.desktop.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.Company;
import com.code.aon.config.User;
import com.code.aon.desktop.DesktopAlarm;
import com.code.aon.desktop.DesktopNoticeSummary;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.groupware.controller.AlarmController;
import com.code.aon.ui.groupware.controller.NoteController;
import com.code.aon.ui.groupware.controller.NoticeController;
import com.code.aon.ui.util.AonUtil;

public class DesktopController extends BasicController {
	
	private static final String NOTE_CONTROLLER_NAME = "note";
	private static final String ALARM_CONTROLLER_NAME = "alarm";
    private static final String NOTICE_CONTROLLER_NAME = "notice";

	private static final SelectItem NULL_SELECT_ITEM = new SelectItem(null, " ");	
    
    private ListDataModel recentNoteModel;
    private ListDataModel todayAlarmModel;
    private ListDataModel recentAlarmModel;
    private ListDataModel ancientAlarmModel;

	public SelectItem getNullValue() {
		return NULL_SELECT_ITEM;
	}
    
    @SuppressWarnings("unchecked")
    public List<DesktopNoticeSummary> getNoticeSummaryModel() {
        List<DesktopNoticeSummary> noticeSummaryList = new LinkedList<DesktopNoticeSummary>();
        NoticeType[] noticeTypes = NoticeType.values();
        for (int i=0; i<noticeTypes.length; i++) {
            DesktopNoticeSummary summary = new DesktopNoticeSummary(noticeTypes[i]);
            noticeSummaryList.add(summary);
        }

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar to = new GregorianCalendar();
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        String select = "select notice.type, count(*) " +
                        "from Notice as notice, Alarm as alarm " +
                        "where notice.id = alarm.sourceId " +
                        "and alarm.source = " + AlarmSource.NOTICE.ordinal() + " " +
                        "and alarm.status = " + AlarmStatus.PENDING.ordinal() + " " +
                        "and alarm.user = " + UserUtils.getInstance().getLoggedUser().getId() + " " +
                        "and alarm.alarmDate < '" + formatter.format(to.getTime()) + "' " +
                        "group by notice.type " +
                        "order by notice.type";
        Iterator iterator = this.createQuery(select).iterator();
        while (iterator.hasNext()) {
            Object[] obj = (Object[])iterator.next();
            NoticeType noticeType = (NoticeType)obj[0];
            Long count = (Long)obj[1];

            DesktopNoticeSummary summary = noticeSummaryList.get(noticeType.ordinal());
            summary.setCount(count);
        }

        return noticeSummaryList;
    }

    @SuppressWarnings("unchecked")
    private List createQuery(String select) {
        Session session = HibernateUtil.getSession();
        Query query = session.createQuery(select);
        return query.list();
    }

    public ListDataModel getRecentNoteModel() throws ManagerBeanException{
        try {
            IManagerBean noteBean = BeanManager.getManagerBean(Note.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(noteBean.getFieldName(IGroupWareAlias.NOTE_OWNER_ID), UserUtils.getInstance().getLoggedUser().getId());
            criteria.addOrder(noteBean.getFieldName(IGroupWareAlias.NOTE_DATE), false);
            this.recentNoteModel = new ListDataModel(noteBean.getList(criteria));

            return this.recentNoteModel;
        } catch (ManagerBeanException e) {
            throw new ManagerBeanException("Error obtaining recentNoteModel", e);
        }
    }

    public ListDataModel getTodayAlarmModel() throws ManagerBeanException{
        Calendar from = new GregorianCalendar();
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        Calendar to = new GregorianCalendar();
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        this.todayAlarmModel = new ListDataModel(this.createQuery(getUserAlarmSentence(from.getTime(), to.getTime())));
        return this.todayAlarmModel;
    }

    public ListDataModel getRecentAlarmModel() throws ManagerBeanException{
        Calendar from = new GregorianCalendar();
        from.add(Calendar.DATE, -5);
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        Calendar to = new GregorianCalendar();
        to.add(Calendar.DATE, -1);
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        this.recentAlarmModel = new ListDataModel(this.createQuery(getUserAlarmSentence(from.getTime(), to.getTime())));
        return this.recentAlarmModel;
    }

    public ListDataModel getAncientAlarmModel() throws ManagerBeanException{
        Calendar to = new GregorianCalendar();
        to.add(Calendar.DATE, -6);
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        this.ancientAlarmModel = new ListDataModel(this.createQuery(getUserAlarmSentence(null, to.getTime())));
        return this.ancientAlarmModel;
    }

    private String getUserAlarmSentence(Date from, Date to) throws ManagerBeanException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String select = "select new com.code.aon.desktop.DesktopAlarm(alarm.id, alarm.description, alarm.alarmDate, notice.type, alarm.priority) " +
                        "from Notice as notice, Alarm as alarm " +
                        "where notice.id = alarm.sourceId " +
                        "and alarm.source = " + AlarmSource.NOTICE.ordinal() + " " +
                        "and alarm.status = " + AlarmStatus.PENDING.ordinal() + " " +
                        "and alarm.user = " + UserUtils.getInstance().getLoggedUser().getId() + " ";
        if (from != null) {
            select += "and alarm.alarmDate >= '" + formatter.format(from) + "' ";
        }
        if (to != null) {
            select += "and alarm.alarmDate <= '" + formatter.format(to) + "' ";
        }
        select += "ORDER BY alarm.alarmDate DESC";

        return select;
    }

    @SuppressWarnings("unused")
    public void onSelectNote(ActionEvent event) throws ManagerBeanException{
        NoteController noteController = (NoteController)AonUtil.getController(NOTE_CONTROLLER_NAME);
        Note note = (Note)recentNoteModel.getRowData();
        Criteria criteria = new Criteria();
        try {
            criteria.addEqualExpression(noteController.getFieldName(IGroupWareAlias.NOTE_ID), note.getId());
            noteController.setCriteria(criteria);
            noteController.onSearch(null);
            noteController.getModel().setRowIndex(0);
            noteController.onSelect(null);
        } catch (ManagerBeanException e) {
            throw new ManagerBeanException("Error obtaining note with id=" + note.getId(), e);
        }
    }

    @SuppressWarnings("unused")
    public void onSelectTodayAlarm(ActionEvent event) throws ManagerBeanException{
        onSelectAlarm(todayAlarmModel);
    }

    @SuppressWarnings("unused")
    public void onSelectRecentAlarm(ActionEvent event) throws ManagerBeanException{
        onSelectAlarm(recentAlarmModel);
    }

    @SuppressWarnings("unused")
    public void onSelectAncientAlarm(ActionEvent event) throws ManagerBeanException{
        onSelectAlarm(ancientAlarmModel);
    }

    private void onSelectAlarm(ListDataModel model) throws ManagerBeanException{
        AlarmController alarmController = (AlarmController)AonUtil.getController(ALARM_CONTROLLER_NAME);
        DesktopAlarm alarm = (DesktopAlarm)model.getRowData();
        Criteria criteria = new Criteria();
        try {
            criteria.addEqualExpression(alarmController.getFieldName(IGroupWareAlias.ALARM_ID), alarm.getId());
            alarmController.setCriteria(criteria);
            alarmController.onSearch(null);
            alarmController.getModel().setRowIndex(0);
            alarmController.onSelect(null);
        } catch (ManagerBeanException e) {
            throw new ManagerBeanException("Error obtaining alarm with id=" + alarm.getId(), e);
        }
    }

    public void onSearchNotice() throws ManagerBeanException {
        Calendar from = new GregorianCalendar();
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        Calendar to = new GregorianCalendar();
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        NoticeController noticeController = (NoticeController)AonUtil.getController(NOTICE_CONTROLLER_NAME);
        Criteria criteria = new Criteria();
        try {
            criteria.addEqualExpression(noticeController.getFieldName(IGroupWareAlias.NOTICE_RECIPIENT_ID), UserUtils.getInstance().getLoggedUser().getId());
            criteria.addBetweenExpression(noticeController.getFieldName(IGroupWareAlias.NOTICE_DATE), from.getTime(), to.getTime());
            noticeController.setCriteria(criteria);
            noticeController.onSearch(null);
        } catch (ManagerBeanException e) {
            throw new ManagerBeanException("Error obtaining notice for logged user", e);
        }
    }

    @SuppressWarnings("unchecked")
    public String getCompanyName() throws ManagerBeanException {
        try {
	    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
	        List companyList = companyBean.getList(null);
	        if (companyList.size() > 0) {
	            Company company = (Company)companyList.get(0);
	            return company.getName();
	        }
	        return null;
        }
        catch (Exception e) {
        	return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String getCompanyAlias() throws ManagerBeanException {
        try {
	    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
	        List companyList = companyBean.getList(null);
	        if (companyList.size() > 0) {
	            Company company = (Company)companyList.get(0);
	            return company.getAlias();
	        }
	        return null;
        }
        catch (Exception e) {
        	return null;
        }
    }

	public boolean isLogoAttached() throws ManagerBeanException {
		return !(obtainCompanyLogo() == null);
	}

	/**
	 * Obtains company logo.
	 * 
	 * @return the registry attachment
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	private RegistryAttachment obtainCompanyLogo() throws ManagerBeanException {
		try {
			IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.LOGO);
			Iterator iter = registryAttachBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (RegistryAttachment)iter.next();
			}
			return null;
	    }
	    catch (Exception e) {
	    	return null;
	    }
	}

    public String getLoggedUserName() {
        User user = UserUtils.getInstance().getLoggedUser();
        return user.getName();
    }

    public String getLoggedUser() {
        return UserUtils.getInstance().getPrincipal().getShortName();
    }
    
    public String getCurrentDate() {
        DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");

        return formatter.format(new Date()).toUpperCase();
    }
    
    public boolean isValidated() {
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (session.getAttribute("AON_KEY_VALIDATOR_OK") != null) return true;
		return false;
    }
    
    public boolean isRoleManager() {
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("Manager");
    }
}