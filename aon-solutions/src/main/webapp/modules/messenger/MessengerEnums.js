import { COLORS, CONSTANT, CSS, MATERIAL_ICONS, MSG } from "../../environments/environments.js";
import { PRESENCE_FILTER } from "../timecontrol/signinEnums.js";

const AON_MESSENGER_LIST_OPEN = {
    name: 'Abiertas',
    id:'Abiertas',
    icon: MATERIAL_ICONS.FIBER_MANUAL_RECORD,
    icon_color: "#2e7d32",
    icon_class: CONSTANT.MATERIAL_ICONS_OUTLINED
};

const AON_MESSENGER_LIST_IN_PROGRESS = {
    name: 'Derivadas',
    id:'Derivadas',
    icon: MATERIAL_ICONS.FIBER_MANUAL_RECORD,
    icon_color: CSS.variable(COLORS.MATERIAL_BLUE),
    icon_class: CONSTANT.MATERIAL_ICONS_OUTLINED
};

const AON_MESSENGER_LIST_CLOSE = {
    name: 'Cerradas',
    id:'Cerradas',
    icon: MATERIAL_ICONS.FIBER_MANUAL_RECORD,
    icon_color: CSS.variable(COLORS.AON_BLACK),
    icon_class: CONSTANT.MATERIAL_ICONS_OUTLINED
};

const AON_MESSENGER_LIST_ARCHIVE = {
    name: 'Archivadas',
    id: 'Archivadas',
    icon: MATERIAL_ICONS.FIBER_MANUAL_RECORD,
    icon_color: COLORS.ORANGE,
    icon_class: CONSTANT.MATERIAL_ICONS_OUTLINED
};

export const MessengerOptions = {
    AON_MESSENGER_LIST_OPEN,
    AON_MESSENGER_LIST_IN_PROGRESS,
    AON_MESSENGER_LIST_CLOSE,
    AON_MESSENGER_LIST_ARCHIVE
};

export const MESSENGER_VIEWS = {
    AON_MESSENGER: "aonMessenger",
    AON_MESSENGER_LIST: "aonMessengerList",
    AON_MESSENGER_CHAT: "aonMessengerChat",
    AON_MESSENGER_GRAPHIC: "aonMessengerGraphic"
}

export const MESSENGER_COMPONENTS = {
    WRITTER : "writter",
    MESSAGE : "message",
    WRAPPER : "wrapper",
    CHAT : "chat",
    ADVICE : 'advice',
}

export const MESSENGER_DIRECTION = {
    RIGHT:"right",
    LEFT:"left"
}

export const MESSENGER_IDS = {
    ICON_SEND_WORKFLOW:'iconSendWorkflow',
    ICON_EDIT_WORKFLOW:'iconEditWorkflow',
    MAIN_WRAPPER : "mainWrapper",
    MESSENGER_CHAT: "messengerChat",
    FORM_DINAMIC:"formDinamic",
    PROCESS_DIV: "processDiv",
    DINAMIC_DIV: "dinamicDiv",
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
    PROJECT_TASK: "projectTask",
    ADVISORY_TASK: "advisoryTask",
    DESCRIPTION_TASK: "descriptionTask",
    GTASK_ID_TASK: "GTaskIdTask",
    TITLE_TASK: "titleTask",
    SOURCE_TASK: "sourceTask",
    EXTERNAL_TASK: "externalTask",
    BTN_SEND_MESSAGE: "btnSendMessage",
    DIV_TASK_TAGS:"divTaskTags",
    TYPE_REQUEST_CAU: "typeRequestCau",
    SELECT_APP: "selectApp",
    AON_TAB: "aonTabMessenger",
    TOOLBAR_BRANCH: "toolbarBranch",
    TOOLBAR_LABELS: "toolbarLabels",
    DAILY_TRACKING: "dailyTracking",
    COMMENT_DAILY_TRACKING: "commentDailyTracking",
    JOB_TYPE: "jobType",
    MAIN_VIEW:"mainView"
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
    CONNECTED: "connected",
} 

export const WORKFLOW_TYPE = (type)=>{
    if(WORKFLOW_TYPES.OPEN.includes(type))      return "Abierta"; //TODO
    if(WORKFLOW_TYPES.CLOSE.includes(type))     return "Cerrada";//TODO
    if(WORKFLOW_TYPES.REOPEN.includes(type))    return "Reabierta";//TODO
    if(WORKFLOW_TYPES.DUPLICATE.includes(type)) return "Duplicada";//TODO
    if(WORKFLOW_TYPES.LIBERATE.includes(type))  return "Liberada";//TODO
    if(WORKFLOW_TYPES.DELETE.includes(type))    return "Archivada";//TODO
    if(WORKFLOW_TYPES.RESTORE.includes(type))   return "Restaurada";//TODO
    if(WORKFLOW_TYPES.ASSIGN.includes(type))    return "Reasignada";//TODO
    if(WORKFLOW_TYPES.CONNECTED.includes(type)) return "Conectada";//TODO
    return "Comentada"; //TODO
} 

export const TASK_STATUS = {
	DELETED:"deleted",
	PENDING:"pending",
	IN_PROGRESS: "in_progress",
	FINISHED:"finished",
	FAQ:"faq"
} 

export const TAG_TYPE = {
    TASK_TYPE:"TASK_TYPE",
    TASK_LABEL: "TASK_LABEL",
    NOTE_TYPE:"NOTE_TYPE",
    NOTE_LABEL: "NOTE_LABEL"
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
	REQUEST: "request",
    GROUPED: "grouped",
    TASK:"task"
} 

export const TASK_EVALUATION = {
	VERY_BAD:"very_bad",
	BAD:"bad",
	REGULAR:"regular",
	GOOD:"good",
	VERY_GOOD:"very_good"
} 

export const MessengerSidenav = {
    ADD_COMMENT: {
        name: "Addcomment",
        icon: "add_comment",
        id: "Addcomment",
    },
    GRAPHIC: {
        name: "GRAPHIC",
        icon: "bar_chart",
        id: "bar_chartMessenger",
    },
};

export const TASK_FILTER = [
    {
        type: "select",
        id: "registry",
        name: "registry",
        title: MSG.CUSTOMER,
        autocomplete: true,
        default:true,
        emptyclear:true
    },
    {
        type: "select",
        id: "senderFilter",
        name: "searchsender",
        title: "Creador",
        autocomplete: true,
        default:true,
        emptyclear:true
    },
    {
        type: "select",
        id: "task_holder",
        name: "searchtask_holder",
        title: "Asignado",
        autocomplete: true,
        default:true,
        emptyclear:true
    },
    ...PRESENCE_FILTER
];

export const APP_PARAMS_REQUEST = {
    APP_REQUESTS_INT_WORKGROUP: "APP_REQUESTS_INT_WORKGROUP",
    APP_REQUESTS_INT_TASK_HOLDER: "APP_REQUESTS_INT_TASK_HOLDER",
    APP_REQUESTS_INT_OPENED: "APP_REQUESTS_INT_OPENED",
    APP_REQUESTS_INT_CLOSED: "APP_REQUESTS_INT_CLOSED",
    APP_REQUESTS_INT_COMMENT: "APP_REQUESTS_INT_COMMENT",
    APP_REQUESTS_INT_ASSIGN: "APP_REQUESTS_INT_ASSIGN",
    APP_REQUESTS_EXT_WORKGROUP: "APP_REQUESTS_EXT_WORKGROUP",
    APP_REQUESTS_EXT_TASK_HOLDER: "APP_REQUESTS_EXT_TASK_HOLDER",
    APP_REQUESTS_EXT_OPENED: "APP_REQUESTS_EXT_OPENED",
    APP_REQUESTS_EXT_CLOSED: "APP_REQUESTS_EXT_CLOSED",
    APP_REQUESTS_EXT_COMMENT: "APP_REQUESTS_EXT_COMMENT",
    APP_REQUESTS_EXT_ASSIGN: "APP_REQUESTS_EXT_ASSIGN",
    APP_REQUESTS_EMAIL_RATING: "APP_REQUESTS_EMAIL_RATING",

    APP_REQUESTS_INT_EMAIL_OPENED:"APP_REQUESTS_INT_EMAIL_OPENED",
	APP_REQUESTS_INT_EMAIL_CLOSED:"APP_REQUESTS_INT_EMAIL_CLOSED",
    APP_REQUESTS_INT_EMAIL_ASSIGN:"APP_REQUESTS_INT_EMAIL_ASSIGN",
    APP_REQUESTS_EXT_EMAIL_OPENED:"APP_REQUESTS_EXT_EMAIL_OPENED",
	APP_REQUESTS_EXT_EMAIL_CLOSED:"APP_REQUESTS_EXT_EMAIL_CLOSED",
    APP_REQUESTS_EMAIL_RATING_CLOSED: "APP_REQUESTS_EMAIL_RATING_CLOSED"
}