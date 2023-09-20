import { Injectable } from '@angular/core';
import { CollectionFactory, Factory } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root'
})
export class CommonService {

  public objectFactory = new Factory();
  public collectionFactory = new CollectionFactory();

  constructor() { }
}
