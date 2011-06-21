package com.code.aon.desktop.controller;

import static com.code.aon.ui.groupware.controller.IGroupWareConstants.ALARM_CONTROLLER_NAME;
import static com.code.aon.ui.groupware.controller.IGroupWareConstants.NOTE_CONTROLLER_NAME;
import static com.code.aon.ui.groupware.controller.IGroupWareConstants.NOTICE_CONTROLLER_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.imageio.ImageIO;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.Company;
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
import com.code.aon.ui.desktop.applications.WebmailManager;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.groupware.controller.AlarmController;
import com.code.aon.ui.groupware.controller.NoteController;
import com.code.aon.ui.groupware.controller.NoticeController;
import com.code.aon.ui.util.AonUtil;

public class DesktopController implements IDesktopConstants {
	
	private static final int UPDATE_CONNECTION_TIMEOUT = 5000;

	private final static Logger LOGGER = LoggerFactory.getLogger(DesktopController.class);
    
    private ListDataModel recentNoteModel;
    
    private List<DesktopNoticeSummary> noticeSummaryList;
    
    private boolean checkUpdateURL = true;
    
    public DesktopController() {
    	onRefresh(null);
    }
    
	public void updateNoticeSummaryModel() {
        this.noticeSummaryList = new LinkedList<DesktopNoticeSummary>();
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
        Iterator<?> iterator = this.createQuery(select).iterator();
        while (iterator.hasNext()) {
            Object[] obj = (Object[])iterator.next();
            NoticeType noticeType = (NoticeType)obj[0];
            Long count = (Long)obj[1];

            DesktopNoticeSummary summary = noticeSummaryList.get(noticeType.ordinal());
            summary.setCount(count);
        }

    }

	public List<DesktopNoticeSummary> getNoticeSummaryModel() {
        return noticeSummaryList;
    }

    private List<?> createQuery(String select) {
    	String name = HibernateUtil.getSessionFactoryName();
        Session session = HibernateUtil.getSession(name);
        Query query = session.createQuery(select);
        return query.list();
    }

    private void updateRecentNoteModel() throws ManagerBeanException {
    	IManagerBean noteBean = BeanManager.getManagerBean(Note.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(noteBean.getFieldName(IGroupWareAlias.NOTE_OWNER_ID), UserUtils.getInstance().getLoggedUser().getId());
    	criteria.addOrder(noteBean.getFieldName(IGroupWareAlias.NOTE_DATE), false);
    	this.recentNoteModel = new ListDataModel(noteBean.getList(criteria));
    }
    
    public ListDataModel getRecentNoteModel() {
    	return this.recentNoteModel;
    }    

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

    public String getCompanyAlias() throws ManagerBeanException {
        try {
	    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
	        List<ITransferObject> companyList = companyBean.getList(null);
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
	private RegistryAttachment obtainCompanyLogo() throws ManagerBeanException {
		try {
			IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.LOGO);
			Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (RegistryAttachment)iter.next();
			}
			return null;
	    }
	    catch (Exception e) {
	    	return null;
	    }
	}

	public String getUpdateURL() {
    	String server = null;
		try {
			server = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			try {
				server = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				LOGGER.error( "Error getting server address", e1);
			}
		}
    	return "http://" + server + ":7654";		
	}
	    
    public boolean isUpdatesAvailable() {
    	boolean available = false;
    	if ( checkUpdateURL ) {
    		try {
				URL url = new URL( getUpdateURL() + "/hasupdate.rpy" );
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(UPDATE_CONNECTION_TIMEOUT);
				InputStream in = connection.getInputStream();
				char result = (char) in.read();
				in.close();
				available = (result == '1');
    		} catch (ConnectException e) {
    			checkUpdateURL = false;
    			LOGGER.debug( "Timeout getting updates available", e);
			} catch (Throwable e) {
				checkUpdateURL = false;
				LOGGER.error( "Error getting updates available", e);
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
			} catch (Throwable th) {
				LOGGER.error( "Error reading logo", th);
			}
		}
		return true;
	}

	public boolean isHideHeaderContent() {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		boolean hide = companyController.isHideHeaderContent();
		if (! hide) {
			AonUserController userController = (AonUserController) AonUtil.getRegisteredBean(CURRENT_USER_CONTROLLER_NAME);
			if ( userController.isPasswordExpired() ) {
				companyController.setHideHeaderContent(true);
				return true;
			}			
		}
		return hide;
	}
	
	public void onRefresh( ActionEvent event ) {
		LOGGER.info( "Desktop Refresh" );
		AlarmController alarmController = (AlarmController) AonUtil.getRegisteredBean(ALARM_CONTROLLER_NAME);
		try {
			alarmController.updateModels();
			updateRecentNoteModel();
			updateNoticeSummaryModel();
	    } catch (ManagerBeanException e) {
	    	LOGGER.error( e.getMessage(), e );
	        throw new AbortProcessingException("Error updating desktop models", e);
		}
	}

    /**
     * Logout from the current session.
     * 
     * @param event the event
     */
    public void logout( ActionEvent event ) {
    	WebmailManager email = (WebmailManager) AonUtil.getRegisteredBean(EMAIL_CONTROLLER_NAME);
    	email.disconect();
    }	
    
}