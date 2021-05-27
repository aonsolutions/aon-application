importScripts("https://www.gstatic.com/firebasejs/8.2.6/firebase-app.js");
importScripts("https://www.gstatic.com/firebasejs/8.2.6/firebase-messaging.js");

firebase.initializeApp({
  apiKey: "AIzaSyDeUk7hHabedCzkA7qBoHRu43VC7N9CJOs",
  authDomain: "aon-solutions-d69a4.firebaseapp.com",
  databaseURL: "https://aon-solutions-d69a4.firebaseio.com",
  projectId: "aon-solutions-d69a4",
  storageBucket: "aon-solutions-d69a4.appspot.com",
  messagingSenderId: "292041697338",
  appId: "1:292041697338:web:81dbe6e044074cbc58ed03",
  measurementId: "G-7MQDKLET0Y",
});

const messaging = firebase.messaging();

messaging.setBackgroundMessageHandler(function (payload) {
  const notificationTitle = payload.notification.title;
  const notificationOptions = { ...payload.notification };

  return self.registration.showNotification(
    notificationTitle,
    notificationOptions
  );
});

// self.addEventListener('notificationclick', function(event) {
//   // event.notification.close();
//   const clickedNotification = event.notification;
//   const link =  clickedNotification.data.click_action;
//   self.clients.openWindow(link);
//   // Do something as the result of the notification click
// });
