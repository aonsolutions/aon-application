@org.hibernate.annotations.TypeDefs({
	@org.hibernate.annotations.TypeDef(
		name="stringEnum",
		typeClass = com.code.aon.common.dao.hibernate.type.StringEnumUserType.class
	),
    @org.hibernate.annotations.TypeDef(
        name="siNoType",
        typeClass = com.code.aon.payroll.SiNoType.class
    )
})
package com.code.aon.payroll;