export class Message{
    type : number;
    name : string;
    subject : string;
    date : string;
    description : string;
    status: number;
    lastMessageOrigin: boolean = true;

    constructor(){
      this.type = 0;
      this.name = '';
      this.subject = '';
      this.date = '';
      this.description = '';
      this.status = 0;
      this.lastMessageOrigin = false;
    }
}
