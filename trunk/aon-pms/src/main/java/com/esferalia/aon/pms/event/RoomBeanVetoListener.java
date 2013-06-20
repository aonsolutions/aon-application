package com.esferalia.aon.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Room;

public class RoomBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	try {
    		Room to = (Room)evt.getTo();
    		to.getAsset().setName(to.getAsset().getName().trim());
    		if (!isRoomNameAvailable(to)) {
    			throw new ManagerBeanVetoListenerException("Ese Número de Habitación ya existe.");
    		} 
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex);
    	}
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	try {
    		Room to = (Room)evt.getTo();
    		to.getAsset().setName(to.getAsset().getName().trim());
    		if (!isRoomNameAvailable(to)) {
    			throw new ManagerBeanVetoListenerException("Ese Número de Habitación ya existe.");
    		} 
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex);
    	}
    }

    private	boolean isRoomNameAvailable(Room room) throws ManagerBeanException {
    	IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		if (room.getAsset().getId() != null) {
			criteria.addNotEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_ID), room.getAsset().getId());
		}
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), room.getHotel().getId());
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME), room.getAsset().getName());
		return (roomBean.getCount(criteria) == 0);
	}

}
