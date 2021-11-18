package com.code.aon.aio.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TimeControlController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	

	private final static Logger LOGGER = LoggerFactory.getLogger(TimeControlController.class);
	
	private Optional<TimeControl> weekTimeControl;
	private Optional<TimeControl> todayTimeControl;
	private Optional<DomainUserRoles> domainUserRoles;
	
	public boolean isIn() {
		return getOptionalTodayTimeControl().map( t -> t.getStatus() == TimeControlStatus.IN ).orElse(false)  ;
	}

	public boolean isOut() {
		return getOptionalTodayTimeControl().map( t -> t.getStatus() == TimeControlStatus.OUT || t.getStatus() == null ).orElse(false)  ;
	}
	
	public boolean isPause() {
		return getOptionalTodayTimeControl().map( t -> t.getStatus() == TimeControlStatus.PAUSE ).orElse(false)  ;
	}

	public boolean isActive() {
		return getOptionalDomainUserRoles().map( d -> d.isTimecontrol()).orElse(false);
	}
	
	public long getTodayHours() {
		long timeInMillis = getTodayTimeInMillis() + getInTimeInMillis();
		return timeInMillis / 3600000;
	}
	
	public long getTodayMinutes() {
		long timeInMillis = getTodayTimeInMillis() + getInTimeInMillis();
		return (timeInMillis % 3600000)  / 60000 ;
	}
	
	public long getTodaySeconds() {
		long timeInMillis = getTodayTimeInMillis() + getInTimeInMillis();
		return (timeInMillis % 60000 )  / 1000 ;
	}
	
	public String getTodayTime() {
		return String.format("%02d:%02d:%02d", getTodayHours(), getTodayMinutes(), getTodaySeconds());
	}
	
	public String getTodayLastTimeIn() {
		return getTodayLastTime();
	}
	
	public String getTodayLastTimeOut() {
		return getTodayLastTime();
	}

	public String getTodayLastTimePause() {
		return getTodayLastTime();
	}

	public long getWeekTimeInMillis() {
		return getOptionalWeekTimeControl().map(t -> t.getTime() ).orElse(0L);
	}
	
	public long getTodayTimeInMillis() {
		return getOptionalTodayTimeControl().map(t -> t.getTime() ).orElse(0L);
	}

	public long getInTimeInMillis() {
		if ( isIn()) 
			return getOptionalTodayTimeControl()
					.map( t -> t.getInDate() )
					.map( d -> d.getTime() )
					.map (t -> Math.max(t, getToday()))
					.map( t -> getNow() - t )
					.map( t -> Math.max(0,t))
					.orElse(0L)
					;
		else 
			return 0L;
	}

	public long getWeekHours() {
		long time = getWeekTimeInMillis() + getInTimeInMillis();
		return time / 3600000;
	}

	public long getWeekMinutes() {
		long time = getWeekTimeInMillis() + getInTimeInMillis();
		return (time % 3600000)  / 60000 ;
	}
	

	public String getWeekTime() {
		return String.format("%02d:%02d", getWeekHours(), getWeekMinutes());
	}
	
	public void in(ActionEvent e) {
		save(TimeControlStatus.IN);
	}
	
	public void out(ActionEvent e) {
		save(TimeControlStatus.OUT);
	}

	public void back(ActionEvent e) {
		save(TimeControlStatus.IN);
	}
	
	public void pause(ActionEvent e) {
		save(TimeControlStatus.PAUSE);
	}

	public String getTodayLastTime() {
		return getLastTime( d -> sameDay( new Date(), d));
	}

	public String getLastTime(Predicate<Date> predicate) {
		return getOptionalTodayTimeControl().map( t -> t.getLastDate() ).filter( predicate ).map( d -> String.format("%1$td/%1$tm/%1$tY %1$tH:%1$tM", d)).orElse(null);
	}
	

	private void save(TimeControlStatus timeControlStatus) {
		Date date = new Date();
		Location location = new Location();
		Coordinates coordinates = new Coordinates();

		
		TaskHolder taskHolder = getTaskHolder();

		User user = getUser();
		String login = user.getLogin();
		Domain domain = taskHolder.getDomain();
		
		TimeControlDetail timeControlDetail = 
				new TimeControlDetail()
				.setDate(date)
				//.setComments("")
				.setDomain(domain)
				.setLocation(location)
				.setTaskHolder(taskHolder)
				.setCoordinates(coordinates)
				.setStatus(timeControlStatus)
				;
		

		AON_SOLUTIONS.saveTimeControlDetail(taskHolder.getDomain(), login, timeControlDetail);
	}

	private Optional<TimeControl> getOptionalWeekTimeControl(){
		if ( weekTimeControl == null ) {
			try {
				weekTimeControl = Optional.of(getWeekTimeControl());
			} catch ( Exception e ) {
				weekTimeControl = Optional.empty();
			}
		}
		return weekTimeControl;
	}

	private Optional<TimeControl> getOptionalTodayTimeControl(){
		if ( todayTimeControl == null ) {
			try {
				todayTimeControl = Optional.of(getTodayTimeControl());
			} catch ( Exception e ) {
				todayTimeControl = Optional.empty();
			}
		}
		return todayTimeControl;
	}

	private Optional<DomainUserRoles> getOptionalDomainUserRoles(){
		if ( domainUserRoles == null ) {
			try {
				domainUserRoles = Optional.of(getDomainUserRoles());
			} catch ( Exception e ) {
				domainUserRoles = Optional.empty();
			}
		}
		return domainUserRoles;
	}
	
	private static Domain getDomain() {
		String login =  getUser().getLogin();
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		return AON.getDomain(domainName, domainId, login);
	}
	
	private static User getUser() {
		return UserUtils.getInstance().getLoggedUser();
	}
	
	private static TimeControl getWeekTimeControl() {
		TaskHolder taskHolder = getTaskHolder();
		
		if ( taskHolder.getId() == null )
			return new TimeControl();
		
		return getWeekTimeControl(taskHolder);
	}

	private static TimeControl getTodayTimeControl() {
		TaskHolder taskHolder = getTaskHolder();
		
		if ( taskHolder.getId() == null )
			return new TimeControl();
		
		return getTodayTimeControl(taskHolder);
	}

	private static TaskHolder getTaskHolder() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		User user =  UserUtils.getInstance().getLoggedUser();
		return AON.getTaskHolder(
			domainName,  
			domainId, 
			user.getLogin(), 
			f -> f.getUserIdProperty().eq(user.getId()));
	}
	
	private static DomainUserRoles getDomainUserRoles() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		User user =  UserUtils.getInstance().getLoggedUser();
		return SECURITY.getDomainUserRoles(domainName, domainId, user.getLogin(), user.getId());
	}
	
	private static TimeControl getWeekTimeControl(TaskHolder taskHolder) {
		Date weekStartAndEnd [] = AonDateUtils.getWeekDateRange(new Date());
		Date weekStartDate = AonDateUtils.getDateWithoutTime(weekStartAndEnd[0]);
		
		Date weekEndDate = AonDateUtils.addWeeks(weekStartDate, 1);
		weekEndDate = AonDateUtils.addSeconds(weekEndDate, -1);
		
		return getTimeControl(taskHolder, weekStartDate, weekEndDate);
	}

	private static TimeControl getTodayTimeControl(TaskHolder taskHolder) {
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		return getTimeControl(taskHolder, startDate, endDate);
	}
	
	private static TimeControl getTimeControl(TaskHolder taskHolder, Date startDate, Date endDate) {
		User user =  UserUtils.getInstance().getLoggedUser();
		
		return AON_SOLUTIONS.getTaskHolderTimeControl(taskHolder.getDomain(), user.getLogin(), taskHolder.getId(), startDate, endDate);
	}

	private static boolean sameDay ( Date d1, Date d2) {
		if ( Objects.equals(d1,  d2) ) 
			return true;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		return sameDay(c1, c2);
	}
	
	private static boolean sameDay ( Calendar c1, Calendar c2) {
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
			&& c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
			&& c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
	}
	
	private static long getNow() {
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		return today.getTimeInMillis();
	}

	private static long getToday() {
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.HOUR_OF_DAY, 0);
		return today.getTimeInMillis();
	}
	

}