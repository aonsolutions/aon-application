import { MSG } from '../../environments/environments.js';
import Apps from '../../services/app.js';

export const DocumentalSidenav = {
  DOCUMENTS: {
    id: 'Documents',
    name: MSG.DOCUMENTS.toUpperCase(),
    app: Apps.DOCUMENTAL
  },
  TYPES: {
    id: 'Types',
    name: MSG.TYPES.toUpperCase(),
    app: Apps.DOCUMENTAL
  },
  CATEGORIES: {
    id: 'Categories',
    name: MSG.CATEGORIES.toUpperCase(),
    app: Apps.DOCUMENTAL
  },
  DEFAULT_CATEGORIES: {
    id: 'DefaultCategories',
    // name: 'Categorias por defecto',
    name: MSG.DEFAULT_CATEGORIES.toUpperCase(),
    app: Apps.DOCUMENTAL
  },
  USER_CATEGORIES: {
    id: 'UserCategories',
    name: MSG.USER_CATEGORIES.toUpperCase(),
    app: Apps.DOCUMENTAL
  },
  TAGS: {
    id: 'Tags',
    name: MSG.TAGS.toUpperCase(),
    app: Apps.DOCUMENTAL
  }
}

export const DOCUMENTAL_FILTER = [
    {
      type: "checkbox",
      id: "categoryOldFilter",
      name: "categoryOldFilter",
      title: "Buscar en mis "+MSG.CATEGORY+"s"
    },
	{
      type: "select",
      id: "categoryOld",
      name: "categoryOld",
      title: "Mis "+MSG.CATEGORY+"s",
	  hidden: "true"
    },
	{
      type: "select",
      id: "category",
      name: "category",
      title: MSG.CATEGORY+"s despacho"
    },
	{
      type: "select",
      id: "category2",
      name: "category2",
      title: "Subcategoría",
	  hidden: "true"
	},
	{
      type: "select",
      id: "category3",
      name: "category3",
      title: "Administración",
	  hidden: "true"
	},
	{
      type: "select",
      id: "category4",
      name: "category4",
      title: "Modelo",
	  hidden: "true"
	},	  
	{
      type: "newDate",
      name: "start_date",
      id: "start_date",
      title: "Fecha del documento - desde",
    },
    {
      type: "newDate",
      name: "end_date",
      id: "end_date",
      title: "Fecha del documento - hasta",
    }
];
export const DOCUMENTAL_FILTER_ENTERPRISE = [
    {
      type: "checkbox",
      id: "employee",
      name: "employee",
      title: "Visible solo para "+MSG.EMPLOYEE
    },
    ...DOCUMENTAL_FILTER
];
export const DOCUMENTAL_FILTER_ASESOR = [
    {
      type: "checkbox",
      id: "asesor",
      name: "asesor",
      title: "Visible solo para "+MSG.ASESOR
    },
    ...DOCUMENTAL_FILTER_ENTERPRISE
];

export const ASESOR_TYPE_OPTION = [
  {value: 'enterprise', name: 'Empresa'},
  {value: 'employee', name: 'Empleado'},
  {value: 'asesor', name: 'Asesor'}
];

export const ENTERPRISE_TYPE_OPTION = [
  {value: 'enterprise', name: 'Empresa'},
  {value: 'employee', name: 'Empleado'}
];

export const EMPLOYEE_TYPE_OPTION = [
  {value: 'employee', name: 'Empleado'}
]

export const DOCUMENTAL_VIEWS = {
  AON_DOCUMENT_AYUDAT:"aonDocumentAyudat",
  AON_DOCUMENT_MOBILE_AYUDAT:"aonDocumentMobileAyudat",
  AON_DOCUMENTAL_LIST_AYUDAT:"aonDocumentalListAyudat",
  AON_DOCUMENTAL_AYUDAT:"aonDocumentalAyudat",
}