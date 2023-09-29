import { DELETE_METHOD, GET_METHOD, POST_METHOD, PUT_METHOD } from "./Environment";

export class ApiHttpRequest {

    static async get(url:string, customHeaders: any = {}, data: any): Promise<any> {
        return await this.httpRequest(url, GET_METHOD, customHeaders, data);
    }

    static async post(url:string, customHeaders: any = {}, data: any): Promise<any> {
        return await this.httpRequest(url, POST_METHOD, customHeaders, data);
    }

    static async put(url:string, customHeaders: any = {}, data: any): Promise<any> {
        return await this.httpRequest(url, PUT_METHOD, customHeaders, data);
    }

    static async delete(url:string, customHeaders: any = {}, data: any): Promise<any> {
        return await this.httpRequest(url, DELETE_METHOD, customHeaders, data);
    }

    static async httpRequest(url: string, method: string, customHeaders: any = {}, data: any): Promise<any> {
        let headersAuth = {
            session_id: localStorage.getItem('token'),
            domain_name: localStorage.getItem('domainName'),
            domain_id: localStorage.getItem('domainId'),
            domain_login: localStorage.getItem('login'),
        }
        let headers = new Object();
        Object.assign(headers,customHeaders);
        Object.assign(headers,headersAuth);
        let options = new Object();
        Object.defineProperty(options,'method',{value: method});
        Object.defineProperty(options,'headers',{value: headers});
        if(method == 'POST') Object.defineProperty(options,'body',{value: JSON.stringify(data)});
        let result = await fetch(url,options);
        let dataJson = await result.json();
        return dataJson;
    }

    static makeURL(url: string, params: any): string {
        const esc = encodeURIComponent;
        return url + '?' + Object.keys(params).map(k => `${esc(k)}=${esc(params[k as keyof typeof params])}`).join('&')
    }
}