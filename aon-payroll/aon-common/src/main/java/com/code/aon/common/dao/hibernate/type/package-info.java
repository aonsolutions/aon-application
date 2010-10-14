@TypeDefs({
	@TypeDef(
		name="stringClob",
        typeClass = com.code.aon.common.dao.hibernate.type.StringClobEnhancedType.class
    )
})
package com.code.aon.common.dao.hibernate.type;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
