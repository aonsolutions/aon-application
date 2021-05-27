
export const TYPE_USER =[
    {
        name:"Empleado",
        value:"employee"
    },
    {
        name:"Personalizado",
        value:"personalized"
    }
];

export const NOTIFICATION_TABS = [
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

export const badgeUpdate = ({notification, messenger}) => [
    {
        id: "notification",
        badge: notification,
    },
    {
        id: "messenger",
        badge: messenger,
    },
]