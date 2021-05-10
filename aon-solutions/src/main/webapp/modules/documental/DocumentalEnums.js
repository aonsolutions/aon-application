import { MSG } from '../../environments/environments.js';

export const DocumentalSidenav = {
  DOCUMENTS: {
    id: 'Documents',
    name: MSG.DOCUMENTS.toUpperCase()
  },
  TYPES: {
    id: 'Types',
    name: MSG.TYPES.toUpperCase()
  },
  CATEGORIES: {
    id: 'Categories',
    name: MSG.CATEGORIES.toUpperCase()
  },
  TAGS: {
    id: 'Tags',
    name: MSG.TAGS.toUpperCase()
  }
}

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