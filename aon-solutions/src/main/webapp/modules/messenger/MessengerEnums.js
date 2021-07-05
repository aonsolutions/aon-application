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
    MESSENGER_CHAT: "messengerChat",
    NEW_REQUEST_PANEL : "newRequestPanel", 
    NEW_REQUEST_PANEL_TOOLBAR : "newRequestPanelToolbar", 
    NEW_REQUEST_PANEL_RECEIVER : "newRequestPanelReceiver", 
    NO_MESSAGES : "noMessages",
    ADD_ICON_BUTTON : "aonMessengerAddcommentButtonIconButton",
    BUTTON_SUBMIT_COMMENT:"btnSubmitComment",
    //NEW
    MAIN_DIV: "mainDiv",
    WORKGROUP: "workgroupTask",
    TASKHOLDER: "taskHolderTask",
    COMMENT_TASK: "commentTask",
    TITLE_TASK: "titleTask",
    DESCRIPTION_TASK: "descriptionTask",
    TASK_ID: "taskId"
}

export const MESSENGER_MODES = {
    DEMO : "demo",
    PRODUCTION : "production",
    TEST : "test",
}

export const TASK_WORKFLOW_TYPE = {
    OPEN: "opened",
	CLOSE: "closed",
	REOPEN: "reopened",
	DUPLICATE: "duplicate",
	LIBERATE: "liberate",
	DELETE: "deleted",
	RESTORE: "restore",
	COMMENT: "comment"
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

/**
 * 
 * Current state of the view
 * ---------------------------------
 * DEMO - Demo mode.
 * TEST - Beta in aonsolutions.org
 * PRODUCTION - Disable possible logs and testing content.
 * 
 */
 export const MESSENGER_MODE = MESSENGER_MODES.DEMO;

