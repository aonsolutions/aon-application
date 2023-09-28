import { IStorable, IUser } from "../interfaces/modelsInterfaces";
import { IAuthenticationRepository } from "../interfaces/repositoryInterfaces";
import { FilterBuilder } from "../utils/FilterBuilder";
import { ApiHttpRequest } from "../utils/Http";
import { ErrorResponse } from "../utils/Response";
import { BASE_URL, GET_METHOD } from "../utils/constants";

export class AuthenticationRepository implements IAuthenticationRepository {

    private storableAuth: IStorable<IUser>;

    constructor(storableAuth: IStorable<IUser>) {
        this.storableAuth = storableAuth;
    }

    async userInfo(): Promise<void> {
    }

    async login(email: string, password: string): Promise<void> {
        let filter = new FilterBuilder();
        filter.addField('email', email);
        let collection = this.storableAuth.getCollection().filter(filter.getFilter());
        if(collection.size() == 1 && collection.toArray()[0].Password == password){
            localStorage.setItem('token', 'testToken')
            localStorage.setItem('user', collection.toArray()[0].Document)
        }
        else throw new ErrorResponse('0101');
    }

    async logout(): Promise<void> {
        if(localStorage.getItem('token')) {
            // localStorage.removeItem('token');
            // localStorage.removeItem('enterprise');
            // localStorage.removeItem('domainId');
            // localStorage.removeItem('domainName');
            // localStorage.removeItem('user');
            localStorage.clear();
        }
        else throw new ErrorResponse('0111');
    }

    async tokenLogin(token: string): Promise<void> {
        if(!localStorage.getItem('token')) localStorage.setItem('token', 'testToken');
        else throw new ErrorResponse('0101');
    }
}

export class APIAuthenticationRepository implements IAuthenticationRepository {

    httpRequest = new ApiHttpRequest();

    constructor() {
    }

    async login(email: string, password: string): Promise<void> {
        let data = {
            username: email,
            password: password
        }
        let url = BASE_URL + '/ms/api/login'
        let method = 'POST';
        let customHeaders = {}
        let result = await this.httpRequest.httpRequest(url,method,customHeaders,data)
        if(result.type == "error") throw new ErrorResponse('0101');
        else localStorage.setItem('token', result.session_id);
    }

    async logout(): Promise<void> {
        if(localStorage.getItem('token')) {
            localStorage.removeItem('token');
            localStorage.removeItem('enterprise');
            localStorage.removeItem('domainId');
            localStorage.removeItem('domainName');
        }
        else throw new ErrorResponse('0111');
    }

    async tokenLogin(token: string): Promise<void> {
    }

    async userInfo(): Promise<void> {
        let userInfo = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/user/info', GET_METHOD, {}, {})
        if(userInfo.type == "error") throw new ErrorResponse('0101');
        localStorage.setItem('login', userInfo.login);
    }
}
