package com.esferalia.aon.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Room;

public class RoomBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	try {
    		Room to = (Room)evt.getTo();
    		if(!isRoomNameAvailable(to)){
    			throw new ManagerBeanVetoListenerException("Nombre duplicado en el hotel");
    		} 
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex);
    	}
    }

    private	boolean isRoomNameAvailable(Room room) throws ManagerBeanException {
    	IManagerBean bean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), room.getHotel().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_ASSET_NAME), room.getAsset().getName());
		Projection projection = Projection.countDistinct(bean.getFieldName(IEntityAlias.ROOM_ID));
		Object value = bean.getUniqueResult(projection, criteria);
		return (value == null || ((Integer)value) == 0 );
	}

}
