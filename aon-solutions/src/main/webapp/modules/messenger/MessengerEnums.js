import { COLORS, CSS, MATERIAL_ICONS, MSG } from "../../environments/environments";

export const ICON_TYPES = {
    MATERIAL : "material",
    MATERIAL_ICONS:"material-icons",
    MATERIAL_OUTLINED : "material_outlined",
    MATERIAL_ICONS_OUTLINED : "material-icons-outlined"
}

const AON_MESSENGER_LIST_OPEN = {
    name: 'Abiertas',
    id:'Abiertas',
    icon: MATERIAL_ICONS.FIBER_MANUAL_RECORD,
    icon_color: "#2e7d32",
    icon_class: ICON_TYPES.MATERIAL_ICONS_OUTLINED
};

const AON_MESSENGER_LIST_CLOSE = {
    name: 'Cerradas',
    id:'Cerradas',
    icon: MATERIAL_ICONS.FIBER_MANUAL_RECORD,
    icon_color: CSS.variable(COLORS.AON_BLACK),
    icon_class: ICON_TYPES.MATERIAL_ICONS_OUTLINED
};

const AON_MESSENGER_LIST_ARCHIVE = {
    name: 'Archivadas',
    id: 'Archivadas',
    icon: MATERIAL_ICONS.FIBER_MANUAL_RECORD,
    icon_color: COLORS.ORANGE,
    icon_class: ICON_TYPES.MATERIAL_ICONS_OUTLINED
};

export const MessengerOptions = {
    AON_MESSENGER_LIST_OPEN,
    AON_MESSENGER_LIST_CLOSE,
    AON_MESSENGER_LIST_ARCHIVE
};

export const MESSENGER_VIEWS = {
    AON_MESSENGER: "aonMessenger",
    AON_MESSENGER_LIST: "aonMessengerList",
    AON_MESSENGER_CHAT: "aonMessengerChat",
}

export const MESSENGER_COMPONENTS = {
    WRITTER : "writter",
    MESSAGE : "message",
    WRAPPER : "wrapper",
    CHAT : "chat",
    ADVICE : 'advice',
}

export const MESSENGER_IDS = {
    FORM_DIV : "formDiv",
    MAIN_WRAPPER : "mainWrapper",
    MESSENGER_CHAT: "messengerChat",
    MAIN_DIV: "mainDiv",
    FORM_DINAMIC:"formDinamic",
    PROCESS_DIV: "processDiv",
    FIRST_DIV: "firstDiv",
    SECOND_DIV: "secondDiv",
    DIV_MAIN_MOBILE: "divMainMobile",
    NO_MESSAGES : "noMessages",
    PROCESS_TYPE: "processType",
    WORKGROUP: "workgroupTask",
    TASKHOLDER: "taskHolderTask",
    TASKTAG: "taskTag",
    COMMENT_TASK: "commentTask",
    CUSTOMER_TASK: "customerTask",
    DESCRIPTION_TASK: "descriptionTask",
    GTASK_ID_TASK: "GTaskIdTask",
    TITLE_TASK: "titleTask",
    SOURCE_TASK: "sourceTask",
    INTERNAL_TASK: "internalTask",
    BUTTON_SEND: "buttonSend"
}

export const WORKFLOW_TYPES = {
    OPEN: "opened",
	CLOSE: "closed",
	REOPEN: "reopened",
	DUPLICATE: "duplicate",
	LIBERATE: "liberate",
	DELETE: "deleted",
	RESTORE: "restore",
	COMMENT: "comment",
    ASSIGN: "assigned",
    AON_FILE: "aonFile",
} 

export const WORKFLOW_TYPE = (type)=>{
    if(WORKFLOW_TYPES.OPEN.indexOf(type)!=-1)      return "Abierta"; //TODO
    if(WORKFLOW_TYPES.CLOSE.indexOf(type)!=-1)     return "Cerrada";//TODO
    if(WORKFLOW_TYPES.REOPEN.indexOf(type)!=-1)    return "Reabierta";//TODO
    if(WORKFLOW_TYPES.DUPLICATE.indexOf(type)!=-1) return "Duplicada";//TODO
    if(WORKFLOW_TYPES.LIBERATE.indexOf(type)!=-1)  return "Liberada";//TODO
    if(WORKFLOW_TYPES.DELETE.indexOf(type)!=-1)    return "Archivada";//TODO
    if(WORKFLOW_TYPES.RESTORE.indexOf(type)!=-1)   return "Restaurada";//TODO
    if(WORKFLOW_TYPES.ASSIGN.indexOf(type)!=-1)    return "Reasignada";//TODO
    return "Comentada"; //TODO
} 

export const TASK_STATUS = {
	DELETED:"deleted",
	PENDING:"pending",
	IN_PROGRESS: "in_progress",
	FINISHED:"finished",
	FAQ:"faq"
} 

export const TASK_STATUS_VALUE  = [
    {
        name:AON_MESSENGER_LIST_OPEN.name,
        value:TASK_STATUS.PENDING,
    },
    {
        name:AON_MESSENGER_LIST_CLOSE.name,
        value:TASK_STATUS.FINISHED,
    },
    {
        name:AON_MESSENGER_LIST_ARCHIVE.name,
        value:TASK_STATUS.DELETED,
    }
];

export const TASK_SOURCE = {
	MANUAL:"manual",
	ASSIGNED:"assigned",
	PROCESS:"process",
	CAU:"cau",
	GITHUB:"github",
    QUERY: "query",
	REQUEST: "request"
} 

export const MessengerSidenav = {
    ADD_COMMENT: {
        name: "Addcomment",
        icon: "add_comment",
        id: "Addcomment",
    }
};

export const TASK_FILTER = [
    {
      type: "select",
      id: "registry",
      name: "registry",
      title: MSG.CUSTOMER,
      autocomplete: true,
      default:true
    },
    {
        type: "select",
        id: "task_holder",
        name: "task_holder",
        title: MSG.EMPLOYEE,
        autocomplete: true,
        default:true
    },
    {
        type: "select",
        id: "status",
        name: "status",
        title: MSG.STATUS,
        default:true
    },
    // {
    //   type: "date",
    //   name: "startDate",
    //   id: "startDate",
    //   title: MSG.DATE,
    // }
];