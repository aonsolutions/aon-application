package com.code.aon.aio.controller;

import static com.code.aon.webmail.bean.IMailConstants.INBOX_FOLDER_NAME;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.aio.DesktopNoticeSummary;
import com.code.aon.aio.TaskInfo;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.Task;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.TaskHolderWorkgroup;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonServer;
import com.esferalia.aon.entity.IEntityAlias;


public class DesktopController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DesktopController.class);

	private static final int UPDATE_CONNECTION_TIMEOUT = 5000;
	
    private ListDataModel recentNoteModel;
    
    private List<DesktopNoticeSummary> noticeSummaryList;

    private List<AonFolder> mailSummaryModel;

    private AonServer server;
    
    private List<TaskInfo> taskSummaryModel;
    
    private TaskHolder taskHolder;
    
    private boolean checkUpdateURL = true;
    
    private Boolean bigLogo;

    public DesktopController() {
		try {
			updateRecentNoteModel();
			updateNoticeSummaryModel();
			initWebmail();
			initTask();
	    } catch (ManagerBeanException e) {
	    	LOGGER.error( e.getMessage(), e );
	        throw new AbortProcessingException("Error initing desktop models", e);
		}
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
    	criteria.addEqualExpression(noteBean.getFieldName(IEntityAlias.NOTE_OWNER_ID), UserUtils.getInstance().getLoggedUser().getId());
    	criteria.addOrder(noteBean.getFieldName(IEntityAlias.NOTE_DATE), false);
    	this.recentNoteModel = new ListDataModel(noteBean.getList(criteria));
    }
    
    public ListDataModel getRecentNoteModel() {
    	return this.recentNoteModel;
    }    
    
	public void onRefresh( ActionEvent event ) {
		LOGGER.info( "Desktop Refresh" );
		try {
			updateRecentNoteModel();
			updateNoticeSummaryModel();
			updateMailSummaryModel();
			updateTaskSummaryModel();
	    } catch (ManagerBeanException e) {
	    	LOGGER.error( e.getMessage(), e );
	        throw new AbortProcessingException("Error updating desktop models", e);
		}
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
    
	private String getUpdateURL() {
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

	public boolean isBigLogo() {
		if ( bigLogo == null ) {
			bigLogo = Boolean.FALSE;
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			RegistryAttachment attach = companyController.getAttach();
			if ( (attach != null) && (!ArrayUtils.isEmpty(attach.getData())) ) {
				InputStream in = new ByteArrayInputStream(attach.getData());
				try {
					BufferedImage image = ImageIO.read(in);
					bigLogo = (image.getWidth() > 200);
				} catch (Throwable th) {
					LOGGER.error( "Error reading logo", th);
				}
			}
		}
		return bigLogo;
	}	

	private void initWebmail() {
		try {
			AuthPrincipal user = Utils.getAuthPrincipal();
			IMailAccount mailAccount = WebmailUtil.getDefaultAccount(user.getDomain(),user.getShortName());
			server = new AonServer(mailAccount);
			server.connect();
			updateMailSummaryModel();
		} catch (Throwable th) {
			LOGGER.error("Error on Webmail init", th);
			if ( server != null ) {
				server.disconnect();
				server = null;
			}
		}
	}

    private void updateMailSummaryModel() {
    	if ( isMailActive() ) {
    		AonFolder folder = server.getAonFolder( INBOX_FOLDER_NAME );
    		if ( folder != null ) {
    			mailSummaryModel = new LinkedList<AonFolder>();
    			mailSummaryModel.add(folder);
    		}    		
    	}
    }
    
    public List<AonFolder> getMailSummaryModel() {
		if ( mailSummaryModel != null ) {
			try {
				server.ensureConnection();
			} catch (MessagingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return mailSummaryModel;
    }

    public boolean isMailActive() {
		return (mailSummaryModel != null);
    }

	private void initTask() {
		try {
			taskHolder = new GroupwareUtils().getCurrentTaskHolder();
			updateTaskSummaryModel();
		} catch (Throwable th) {
			LOGGER.error("Error on task init", th);
		}
	}

	public boolean isTaskHolderAvailable() {
		return (taskHolder != null);
	}	
	
	private int getExpiredCount() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IEntityAlias.TASK_TASK_HOLDER_ID);
		criteria.addEqualExpression(user, this.taskHolder.getId());
		String statusAlias = bean.getFieldName(IEntityAlias.TASK_STATUS);
		Expression exp1  = ExpressionUtilities.getEqualExpression(statusAlias, TaskStatus.IN_PROGRESS);
		Expression exp2  = ExpressionUtilities.getEqualExpression(statusAlias, TaskStatus.PENDING);
		criteria.addExpression( ExpressionUtilities.getOrExpression(exp1, exp2) );
		String dueDate = bean.getFieldName(IEntityAlias.TASK_DUE_DATE);
		criteria.addLessThanExpression(dueDate, new Date());
		return bean.getCount(criteria);
	}	
	
	private int getTaskCount( TaskStatus status ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IEntityAlias.TASK_TASK_HOLDER_ID);
		criteria.addEqualExpression(user, this.taskHolder.getId());
		String statusAlias = bean.getFieldName(IEntityAlias.TASK_STATUS);
		criteria.addEqualExpression(statusAlias, status);
		return bean.getCount(criteria);
	}	
	
	private List<Integer> getUserWorkgroups() throws ManagerBeanException {
		List<Integer> result = new LinkedList<Integer>();
		IManagerBean bean = BeanManager.getManagerBean(TaskHolderWorkgroup.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_TASK_HOLDER_ID);
		criteria.addEqualExpression(user, this.taskHolder.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			TaskHolderWorkgroup uwg = (TaskHolderWorkgroup) to;
			result.add( uwg.getTaskHolder().getId() );
		}
		return result;
	}
	
	private int getUserWorkgroupCount() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IEntityAlias.TASK_TASK_HOLDER_ID);
		criteria.addNullExpression(user);
		String workGroup = bean.getFieldName(IEntityAlias.TASK_WORK_GROUP_ID);
		Expression expression = null;
		for( Integer id : getUserWorkgroups() ) {
			if ( expression == null ) {
				expression = ExpressionUtilities.getEqualExpression(workGroup, id);				
			} else {
				Expression exp  = ExpressionUtilities.getEqualExpression(workGroup, id);
				expression = ExpressionUtilities.getOrExpression(expression, exp);
			}
		}
		if ( expression != null ) {
			criteria.addExpression(expression);
		}
		return bean.getCount(criteria);
	}	

    private void updateTaskSummaryModel() throws ManagerBeanException {
    	if ( isTaskHolderAvailable() ) {
        	this.taskSummaryModel = new ArrayList<TaskInfo>();
    		int inprogress = getTaskCount( TaskStatus.IN_PROGRESS );
    		int pending = getTaskCount( TaskStatus.PENDING );
    		int group = getUserWorkgroupCount();
    		String desc = TaskStatus.IN_PROGRESS.getName( AonUtil.getCurrentLocale() );
    		taskSummaryModel.add( new TaskInfo( desc, inprogress + "/" + (inprogress + pending + group) ) );

    		String dues = AonUtil.getMessage( "aon_dues" );
    		taskSummaryModel.add( new TaskInfo( dues, "" + getExpiredCount() ) );    		
    	}
    }
    
    public List<TaskInfo> getTaskSummaryModel() {
    	return this.taskSummaryModel;
    }


}