import { MSG } from '../../environments/environments.js';
import Apps from '../../services/app.js';

export const DocumentalSidenav = {
  DOCUMENTS: {
    id: 'Documents',
    name: MSG.DOCUMENTS.toUpperCase(),
    color: Apps.DOCUMENTAL.color,
    backgroundColor: Apps.DOCUMENTAL.backgroundColor
  },
  TYPES: {
    id: 'Types',
    name: MSG.TYPES.toUpperCase(),
    color: Apps.DOCUMENTAL.color,
    backgroundColor: Apps.DOCUMENTAL.backgroundColor
  },
  CATEGORIES: {
    id: 'Categories',
    name: MSG.CATEGORIES.toUpperCase(),
    color: Apps.DOCUMENTAL.color,
    backgroundColor: Apps.DOCUMENTAL.backgroundColor
  },
  TAGS: {
    id: 'Tags',
    name: MSG.TAGS.toUpperCase(),
    color: Apps.DOCUMENTAL.color,
    backgroundColor: Apps.DOCUMENTAL.backgroundColor
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