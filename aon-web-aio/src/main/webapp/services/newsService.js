import {  post, get, remove } from "./request.js";
import { API_URL, MSG } from "../environments/environments.js";


export const getNews    = (data) => get(`${API_URL}/news`, data);
export const getNewOne  = (data) => get(`${API_URL}/news/one`, data);
export const saveNews   = (data) => post(`${API_URL}/news`, data);
export const deleteNews = (data) => remove(`${API_URL}/news`, data);

export const getNewsType = (data)=>  new Promise((resolve) =>{
    let json = [
        { value:"NEWS", name: MSG.NEWS},
        { value:"MESSAGE", name: MSG.MESSAGE},
        { value:"COMMUNICATION", name: MSG.COMMUNICATION}
    ];

    if(data) {
        json = json.find((r) => r.value == data);
    }

    resolve(json);
});


//-------------------- RSS ----------

export const getRss = (data) => {
    return new Promise((resolve, reject) =>{
        resolve({
            channel:[
                {
                    title: 'W3Schools.com',
                    description: 'New RSS tutorial on W3Schools',
                    link: 'https://www.w3schools.com',
                    category: 'Laboral',
                    pubDate: 'Wed, 30 Jun 2019 09:00:00 GMT',
                    language: 'es',
                    image:{
                        url: 'https://www.w3schools.com/images/logo.gif',
                        title: 'W3Schools.com',
                        link: 'https://www.w3schools.com',
                    },
                    item: [
                        {
                            title: 'Laboral',
                            description: 'New RSS tutorial on W3Schools',
                            link: 'https://www.w3schools.com',
                            category: 'Contrato',
                            comments: 'Comentario 1',
                            author: 'rvasquez',
                            pubDate: 'Wed, 30 Jun 2019 09:00:00 GMT'
                        },
                        {
                            title: 'Admin',
                            description: 'New RSS tutorial on W3Schools',
                            link: 'https://www.w3schools.com',
                            category: 'ITs',
                            comments: 'Comentario 2',
                            author: 'rvasquez',
                            pubDate: 'Wed, 30 Jun 2019 09:00:00 GMT'
                        },
                    ]
                }
            ]
        });
    });
}
