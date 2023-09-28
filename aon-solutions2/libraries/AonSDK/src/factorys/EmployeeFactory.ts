import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IEmployee } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { Employee, ApiEmployee, StorableEmployee } from "../models/Employee";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/constants";

export class EmployeeFactory implements ISingleObjectCrudFactory<IEmployee>, IMultipleObjectCrudFactory<IEmployee> {
    createSingleObjectCrud(): ISingleObjectCrud<IEmployee> {
        return new GenericSingleObjectCrud<Employee>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<Employee>(new ApiEmployee(), Employee) :
            new GenericSingleObjectCrudRepository<Employee>(new StorableEmployee(), Employee)
            ),
            Employee);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IEmployee> {
        return new GenericMultipleObjectCrud<Employee>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<Employee>(new ApiEmployee(), Employee) :
            new GenericMultipleObjectCrudRepository<Employee>(new StorableEmployee(), Employee)
            ),
            Employee);
    }
}