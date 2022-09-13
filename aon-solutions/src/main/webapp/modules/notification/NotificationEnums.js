import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js";

const NOTIFICATION_IDS = {
    AON_NOTIFICATION_DESK:"aonNotificationDesk"
}

const TYPE_USER =[
    {
        name:"Empleado",
        value:"employee"
    },
    {
        name:"Personalizado",
        value:"personalized"
    }
];

const NOTIFICATION_TABS = [
    {
        name: "Notificaciones",
        id: "notification",
        icon: "notifications",
    },
    {
        name: "Solicitudes",
        id: "messenger",
        icon: "assignment",
    }
];

const badgeUpdate = ({notification, messenger}) => [
    {
        id: "notification",
        badge: notification,
    },
    {
        id: "messenger",
        badge: messenger,
    },
];

const NOTIFICATION_ALL = {
    name: "Todas",
    id:'notification_all',
    icon: MATERIAL_ICONS.ALL_INBOX
};

const NOTIFICATION_NOT_READ = {
    name: 'No leídas',
    id:'notification_not_read',
    icon:MATERIAL_ICONS.MOVE_TO_INBOX
};

const NotificationOptions = {
    NOTIFICATION_ALL,
    NOTIFICATION_NOT_READ
};


export const NotificationEnums = {
    TYPE_USER,
    NOTIFICATION_TABS,
    NOTIFICATION_IDS,
    badgeUpdate,
    NotificationOptions
}