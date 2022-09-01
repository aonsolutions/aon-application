let news = [];
let index = 0;

const getNews = () => news;

const setNews = (data) => news = data;

const addNews = (data) => {
  if(news.length) {
    data.forEach((item) => news.push(item));
  } else {
    news = data;
  }
}

const getNew = (data) => news[data];

const getPreviousNew = () =>{
  if(index == 0){
    index = news.length - 1;
  } else {
    index--;
  } 
  
  return news[index];
}

const getNextNew =() =>{
  if(index == news.length - 1) {
    index = 0;
  } else {
    index++;
  }

  return news[index];
}

const setIndexNews = (data) => index = data;

export const NewsCache = {
    addNews, setIndexNews, setNews
}

