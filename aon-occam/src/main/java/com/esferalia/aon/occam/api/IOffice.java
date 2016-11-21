package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;

public interface IOffice {

	public User getUser(AONContext ctx, Integer id);
	
	public List<User> getUsers(AONContext ctx);
					
	public boolean deleteTag(AONContext ctx, String labelName);

	public Tag addNewTag(AONContext ctx, Tag tag)
			throws IllegalArgumentException;

	public Tag editTag(AONContext ctx, String labelName, Tag tag);

	public Tag getTag(AONContext ctx, String name)
			throws IllegalArgumentException;

	public List<Tag> getTags(AONContext ctx) throws IllegalArgumentException;

	public List<Registry> getRegistries(AONContext ctx)
			throws IllegalArgumentException;
	
	public LinkedList<RegistryMedia> getRMediaList(AONContext ctx, RegistryMediaFilter filter);
	
	public NotificationInfo getNotificationInfo(AONContext ctx);
	public void insertNotificationInfo(AONContext ctx, NotificationInfo notificationInfo);
	public void insertNotificationInfo(AONContext ctx, String data, AppParam appParam);

	
}
