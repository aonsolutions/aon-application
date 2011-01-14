@org.hibernate.annotations.TypeDefs({
	@org.hibernate.annotations.TypeDef(
		name="stringEnum",
		typeClass = com.code.aon.common.dao.hibernate.type.StringEnumUserType.class
	),
    @org.hibernate.annotations.TypeDef(
        name="siNoType",
        typeClass = com.esferalia.aon.payroll.type.SiNoType.class
    )
})
package com.esferalia.aon.payroll.type;