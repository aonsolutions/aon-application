import { MSG } from "../../environments/environments";

export const AON_MESSENGER_LIST = {
    name: 'Abiertas',
    icon: 'assignment',
    id: 'messenger'
};

export const AON_MESSENGER_LIST_CLOSE = {
    name: 'Cerradas',
    icon: 'assignment',
    id: 'messenger_close'
};

export const MessengerOptions = {
    AON_MESSENGER_LIST,
    AON_MESSENGER_LIST_CLOSE
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
    NO_MESSAGES : "noMessages",
    WORKGROUP: "workgroupTask",
    TASKHOLDER: "taskHolderTask",
    COMMENT_TASK: "commentTask",
    TITLE_TASK: "titleTask",
    INPUT_FILES: "inputFiles"
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
    FILE: "file",
} 

export const WORKFLOW_TYPE = (type)=>{
    if(WORKFLOW_TYPES.OPEN.indexOf(type)!=-1)      return "Abierta"; //TODO
    if(WORKFLOW_TYPES.CLOSE.indexOf(type)!=-1)     return "Cerrada";//TODO
    if(WORKFLOW_TYPES.REOPEN.indexOf(type)!=-1)    return "Reabierta";//TODO
    if(WORKFLOW_TYPES.DUPLICATE.indexOf(type)!=-1) return "Duplicada";//TODO
    if(WORKFLOW_TYPES.LIBERATE.indexOf(type)!=-1)  return "Liberada";//TODO
    if(WORKFLOW_TYPES.DELETE.indexOf(type)!=-1)    return "Eliminada";//TODO
    if(WORKFLOW_TYPES.RESTORE.indexOf(type)!=-1)   return "Restaurada";//TODO
    if(WORKFLOW_TYPES.ASSIGN.indexOf(type)!=-1)    return "Reasignada";//TODO
    if(WORKFLOW_TYPES.FILE.indexOf(type)!=-1)    return "Archivo";//TODO
    return "Comentada"; //TODO
} 

export const TASK_STATUS = {
	DELETED:"deleted",
	PENDING:"pending",
	IN_PROGRESS: "in_progress",
	FINISHED:"finished",
	FAQ:"faq"
} 

export const ICON_TYPES = {
    MATERIAL : "material",
    MATERIAL_OUTLINED : "material_outlined",
    MATERIAL_ICONS_OUTLINED : "material-icons-outlined"
}

export const REQUEST_FILTER = {
    ABIERTAS : "abiertas",
    CERRADAS : "cerradas"
}
