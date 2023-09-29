import { IMark } from "../interfaces/modelsInterfaces";
import { IMarkSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { ApiMark } from "../models/Mark";
import { Collection } from "../utils/Collection";
import { ApiHttpRequest } from "../utils/Http";
import { GET_METHOD, BASE_URL, GET_MULTIPLE } from "../utils/Environment";

export class APIMarkSpecificMethodsRepository implements IMarkSpecificMethodsRepository {
    private http: ApiHttpRequest = new ApiHttpRequest();
    private model: ApiMark = new ApiMark();

    async getMarksOfOneUser(userId: string, filter?: IFilter): Promise<ICollection<IMark>> {
        let url = '/ms/api/timecontrol/list-holder';
        let params = {
            taskHolderId: userId,
            group: 'DAY',
            startDate: '2023-09-04',
            endDate: '2023-09-08'
        }
        url = ApiHttpRequest.makeURL(url, params);
        let method = GET_METHOD;
        let collection: ICollection<IMark> = new Collection<IMark>();
        let response = await ApiHttpRequest.httpRequest(BASE_URL + url, method, {}, {})
        response.forEach((element: any) => {
            collection.add(this.model.parseDataToReceive(element, GET_MULTIPLE, filter))
        })
        if(this.model.localFilter() && collection.size() > 0){
            if(filter?.intervalFields || filter?.fields) collection = collection.filter(filter);
            if(filter?.orderBy) collection.sort(filter);
            if(filter?.pageItems && filter.pageNum) collection = collection.paginate(filter.pageNum,filter.pageItems);
        }
        return collection;
    }
}