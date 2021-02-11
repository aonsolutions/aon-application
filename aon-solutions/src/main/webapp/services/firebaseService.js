export class FirebaseService {
  VAPIDKEY;
  config;
  TOKEN;

  constructor() {
    this.VAPIDKEY =
      "BCH91WxACVIpylkYRMj3xSpIfrzjz7Ixnctcj25BPMBZKSbGeKjJFIdaRsJGQ3F-SXVFGD0cr4outWLCFwemIkE";
    this.config = {
      apiKey: "AIzaSyDeUk7hHabedCzkA7qBoHRu43VC7N9CJOs",
      authDomain: "aon-solutions-d69a4.firebaseapp.com",
      databaseURL: "https://aon-solutions-d69a4.firebaseio.com",
      projectId: "aon-solutions-d69a4",
      storageBucket: "aon-solutions-d69a4.appspot.com",
      messagingSenderId: "292041697338",
      appId: "1:292041697338:web:81dbe6e044074cbc58ed03",
      measurementId: "G-7MQDKLET0Y",
    };

    this.init();
  }

  init() {
    firebase.initializeApp(this.config);
  }

  getMessagingObject = () => {
    return firebase.messaging();
  };

  getTokenFB = async () => {
    const messaging = this.getMessagingObject();
    const permission = await this.requestPermission();
    let token = null;
    if (permission) {
      token = await messaging
        .getToken({ vapidKey: this.VAPIDKEY })
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
      };
      if (payload.data && payload.data.click_action_web) {
        options["click_action"] = payload.data.click_action_web;
      }
      const notify = new Notification(options.title, options);
      notify.onclick = (ev) => {
        ev.preventDefault(); // Previene al buscador de mover el foco a la pestaña del Notification
        console.log("onClick test", payload.data);
      };
    } else {
      console.log("notification without permission");
    }
  }

  getFirebase() {
    return firebase;
  }
}
