import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../../core/services/user.service';

@Component({
  selector: 'app-input-profile-personal-data',
  templateUrl: './input-profile-personal-data.component.html',
  styleUrls: ['./input-profile-personal-data.component.scss'],
})
export class InputProfilePersonalDataComponent implements OnInit {
  users: any[] = [];

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getUser('94385657M').then(user => {
      this.users.push(user);
      this.logUserNames();
    });
  }

  logUserNames(): void {
    for (const user of this.users) {
      console.log(user.Name);
    }
  }
}


// import { Component, OnInit } from '@angular/core';
// import { UserService } from '../../../../core/services/user.service';

// @Component({
//   selector: 'app-input-profile-personal-data',
//   templateUrl: './input-profile-personal-data.component.html',
//   styleUrls: ['./input-profile-personal-data.component.scss'],
// })
// export class InputProfilePersonalDataComponent implements OnInit {
//   users: any[] = [];

//   constructor(private userService: UserService) {}

//   ngOnInit(): void {
//     // let user = this.userService.getUser('94385657M');
//     // console.log(user);

//     this.userService.getUserList().then(userCollection => {
//       this.users = userCollection.toArray();
//       this.logUserNames();
//     });
//   }

//   logUserNames(): void {
//     for (const user of this.users) {
//       console.log(user.Name);
//     }
//   }
// }
