import { MATERIAL_ICONS } from "../../environments/environments";

const AON_MESSENGER_LIST = {
    name: 'Abiertas',
    icon: MATERIAL_ICONS.ASSIGNMENT,
    id: MATERIAL_ICONS.ASSIGNMENT
};

const AON_MESSENGER_LIST_CLOSE = {
    name: 'Cerradas',
    icon: MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE,
    id: MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE
};

const AON_MESSENGER_LIST_ARCHIVE = {
    name: 'Archivadas',
    icon: MATERIAL_ICONS.ARCHIVE,
    id: MATERIAL_ICONS.ARCHIVE
};

export const MessengerOptions = {
    AON_MESSENGER_LIST,
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
    ADD_ICON_BUTTON : "aonMessengerAddcommentButtonIconButton",
    MESSENGER_CHAT: "messengerChat",
    MAIN_DIV: "mainDiv",
    FORM_DINAMIC:"formDinamic",
    PROCESS_DIV: "processDiv",
    FIRST_DIV: "firstDiv",
    SECOND_DIV: "secondDiv",
    NO_MESSAGES : "noMessages",
    PROCESS_TYPE: "processType",
    WORKGROUP: "workgroupTask",
    TASKHOLDER: "taskHolderTask",
    COMMENT_TASK: "commentTask",
    DESCRIPTION_TASK: "descriptionTask",
    TITLE_TASK: "titleTask",
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

export const TASK_SOURCE = {
	MANUAL:"manual",
	ASSIGNED:"assigned",
	PROCESS:"process",
	CAU:"cau",
	GITHUB:"github"
} 


export const ICON_TYPES = {
    MATERIAL : "material",
    MATERIAL_OUTLINED : "material_outlined",
    MATERIAL_ICONS_OUTLINED : "material-icons-outlined"
}

export const MessengerSidenav = {
    ADD_COMMENT: {
        name: "Addcomment",
        icon: "add_comment",
        id: "Addcomment",
    }
};
  