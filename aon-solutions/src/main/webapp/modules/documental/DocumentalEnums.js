import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export const DocumentalAction = {
  UPLOAD: {
    id: 'Upload',
    name: MSG.AON_MSG_UPLOAD_FILE,
    icon: 'file_upload'
  },
  BACK: {
    id: 'Back',
    name: MSG.AON_MSG_BACK,
    icon: 'arrow_back'
  },
  NEXT: {
    id: 'NextDocument',
    name: MSG.AON_MSG_NEXT,
    icon: 'keyboard_arrow_right'
  },
  PREVIOUS: {
    id: 'PreviousDocument',
    name: MSG.AON_MSG_PREVIOUS,
    icon: 'keyboard_arrow_left'
  },
  SEND: {
    id: 'PreviousDocument',
    name: MSG.AON_MSG_SEND_FILE,
    icon: 'mail'
  },
  DELETE: {
    id: 'DeleteDocument',
    name: MSG.AON_MSG_DELETE_FILE,
    icon: 'delete'
  },
  DOWNLOAD: {
    id: 'DownloadDocument',
    name: MSG.AON_MSG_DOWNLOAD_FILE,
    icon: 'file_download'
  },
  EDIT: {
    id: 'EditDocument',
    name: MSG.AON_MSG_EDIT_DOCUMENT,
    icon: 'edit'
  }
}

export const DocumentalSidenav = {
  DOCUMENTS: {
    id: 'Documents',
    name: MSG.AON_MSG_DOCUMENTS.toUpperCase()
  },
  TYPES: {
    id: 'Types',
    name: MSG.AON_MSG_TYPES.toUpperCase()
  },
  CATEGORIES: {
    id: 'Categories',
    name: MSG.AON_MSG_CATEGORIES.toUpperCase()
  },
  TAGS: {
    id: 'Tags',
    name: MSG.AON_MSG_TAGS.toUpperCase()
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
