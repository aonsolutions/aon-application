package com.code.aon.desktop.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.Company;
import com.code.aon.desktop.DesktopAlarm;
import com.code.aon.desktop.DesktopNoticeSummary;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.groupware.controller.AlarmController;
import com.code.aon.ui.groupware.controller.NoteController;
import com.code.aon.ui.groupware.controller.NoticeController;
import com.code.aon.ui.util.AonUtil;

public class DesktopController extends BasicController implements IDesktopConstants {
	
	private static final Logger LOGGER = Logger.getLogger(DesktopController.class.getName());
	
	private static final SelectItem NULL_SELECT_ITEM = new SelectItem(null, " ");	
	private static final SelectItem ALL_SELECT_ITEM = new SelectItem(null, "Todos");	
    
    private ListDataModel recentNoteModel;
    private ListDataModel nextAlarmModel;
    private ListDataModel todayAlarmModel;
    private ListDataModel recentAlarmModel;
    private ListDataModel ancientAlarmModel;
    
    private boolean checkUpdateURL = true;

	public SelectItem getNullValue() {
		return NULL_SELECT_ITEM;
	}

	public SelectItem getAllValue() {
		return ALL_SELECT_ITEM;
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
    	String name = HibernateUtil.getSessionFactoryName();
        Session session = HibernateUtil.getSession(name);
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

    public ListDataModel getNextAlarmModel() throws ManagerBeanException{
        Calendar from = new GregorianCalendar();
        from.add(Calendar.DATE, 1);
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        Calendar to = new GregorianCalendar();
        to.add(Calendar.DATE, 6);
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);

        this.nextAlarmModel = new ListDataModel(this.createQuery(getUserAlarmSentence(from.getTime(), to.getTime())));
        return this.nextAlarmModel;
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

//        String select = "select new com.code.aon.desktop.DesktopAlarm(alarm.id, alarm.description, alarm.alarmDate, notice.type, alarm.priority) " +
//                        "from Notice as notice, Alarm as alarm " +
//                        "where notice.id = alarm.sourceId " +
//        					"and alarm.source = " + AlarmSource.NOTICE.ordinal() + " " +
        String select = "select new com.code.aon.desktop.DesktopAlarm(alarm.id, alarm.description, alarm.alarmDate, alarm.source, alarm.sourceId, alarm.priority) " +
      					"from Alarm as alarm " +
                        "where alarm.status = " + AlarmStatus.PENDING.ordinal() + " " +
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
        NoteController noteController = (NoteController)FormUtil.getController(NOTE_CONTROLLER_NAME);
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

    public String getTodayDate() {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar date = new GregorianCalendar();
        return formatter.format(date);
    }
    
    @SuppressWarnings("unused")
    public void onSelectNextAlarm(ActionEvent event) throws ManagerBeanException{
        onSelectAlarm(nextAlarmModel);
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
        AlarmController alarmController = (AlarmController)FormUtil.getController(ALARM_CONTROLLER_NAME);
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

        NoticeController noticeController = (NoticeController)FormUtil.getController(NOTICE_CONTROLLER_NAME);
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
    
    public boolean isValidated() {
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (session.getAttribute("AON_KEY_VALIDATOR_OK") != null) return true;
		return false;
    }

	public String getUpdateURL() {
    	String server = null;
		try {
			server = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			try {
				server = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				LOGGER.log( Level.SEVERE, "Error getting server address. " + e1.getMessage(), e1);
			}
		}
    	return "http://" + server + ":7654";		
	}
	    
    public boolean isUpdatesAvailable() {
    	boolean available = false;
    	if ( checkUpdateURL ) {
	    	try {
				URL url = new URL( getUpdateURL() + "/hasupdate.rpy" );
				InputStream in = url.openStream();
				char result = (char) in.read();
				in.close();
				available = (result == '1');
			} catch (Throwable e) {
				checkUpdateURL = false;
				LOGGER.log( Level.INFO, "Error getting updates available. " + e.getMessage(), e);
			}
    	}
    	return available;
    }
    
	public String getUpdateApplicationURL() {
		return getUpdateURL() + "/update.rpy";
	}

	public boolean isBigLogo() {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		RegistryAttachment attach = companyController.getAttach();
		if ( (attach != null) && (!ArrayUtils.isEmpty(attach.getData())) ) {
			InputStream in = new ByteArrayInputStream(attach.getData());
			try {
				BufferedImage image = ImageIO.read(in);
				return (image.getWidth() > 200);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error reading logo. " + e.getMessage(), e);
			}
		}
		return false;
	}
	
}