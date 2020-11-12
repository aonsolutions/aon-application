// import { request, post, get } from "./request.js";
// import { API_URL } from "../environments/environments.js";


export const getSigninStatus = () => {
  let date = new Date();
  let started = localStorage.getItem('aon-signin-started');
  let time = localStorage.getItem('aon-signin-time') || 0;
  if(started) {
    let startedDate = new Date(Number(started));
    if(date.getDate() !== startedDate.getDate()){
      startedDate = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0, 0);
      started = startedDate.getTime();
      localStorage.setItem('aon-signin-started', started);
    }
    time = Number(time) + (date.getTime() - Number(started));
  }
  let signin = {
    status: localStorage.getItem('aon-signin-status') || 'out',
    time: time,
    started: started
  };
  return new Promise((resolve, reject) => {
    resolve(signin);
  });
}

export const updateSigninStatus = (data) => {
  let date = new Date();
  localStorage.setItem('aon-signin-status', data.status);
  if(data.status !== 'in'){
    let started = localStorage.getItem('aon-signin-started');
    let time = localStorage.getItem('aon-signin-time') || 0;
    time = Number(time) + (date.getTime() - Number(started));
    localStorage.setItem('aon-signin-time', time);
    localStorage.removeItem('aon-signin-started');
  } else {
    localStorage.setItem('aon-signin-started', date.getTime());
  }
}
