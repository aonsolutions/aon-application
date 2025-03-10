import { CONFIG_FB, EVENT, VAPIDKEY_FB } from "../environments/environments";

export class FirebaseService {
  TOKEN;

  constructor() {
    this.init();
  }

  init() {
    if(firebase && !firebase.apps.length)
      firebase.initializeApp(CONFIG_FB);
  }

  getMessagingObject = () => firebase.messaging();

  getTokenFB = async () => {
    const messaging = this.getMessagingObject();
    const permission = await this.requestPermission();
    let token = null;
    if (permission) {
      token = await messaging
        .getToken({ vapidKey: VAPIDKEY_FB })
        .catch((err) => {
          console.warn("An error occurred while retrieving token. ", err);
          return null;
        });
    }
    return token;
  };

  requestPermission = async () => {
    const permission = await Notification.requestPermission();
    return permission === "granted";
  };

  deleteToken = async () => {
    const messaging = this.getMessagingObject();
    await messaging
      .deleteToken()
      .then(() => {
        console.log("Token deleted.");
      })
      .catch((err) => {
        console.log("Unable to delete token. ", err);
      });
  };

  async pushNotification(payload) {
    const permission = await this.requestPermission();
    if (permission) {
      const options = {
        ...payload.notification,
        title: payload.notification.title,
        body: payload.notification.body,
      };
      window.dispatchEvent( new CustomEvent(EVENT.RECEIVED_NOTIFICATION, {detail:options}));
      if (payload.data && payload.data.click_action_web) {
        options["click_action"] = payload.data.click_action_web;
      }
      const notify = new Notification(options.title, options);
      notify.onclick = (ev) => {
        ev.preventDefault();
      };
    } else {
      console.log("notification without permission");
    }
  }

  getFirebase = () => firebase;
}
