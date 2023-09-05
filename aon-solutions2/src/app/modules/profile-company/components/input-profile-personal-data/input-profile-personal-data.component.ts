import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../../core/services/user.service';

@Component({
  selector: 'app-input-profile-personal-data',
  templateUrl: './input-profile-personal-data.component.html',
  styleUrls: ['./input-profile-personal-data.component.scss'],
})
export class InputProfilePersonalDataComponent implements OnInit {
  users: any[] = [];
  showPasswordFields: boolean = false;
  documentEnterprise!: string;

  constructor(private userService: UserService) {
    console.log('empresa - ', localStorage.getItem('enterprise'));

    this.userService.getUser('94385657M').then((user) => {
      this.users.push(user);
    });

    // this.userService.getUser(localStorage.getItem('enterprise')!).then((user) => {
    //   this.users.push(user);
    // });



  }

  ngOnInit(): void {}
  togglePasswordFields() {
    this.showPasswordFields = !this.showPasswordFields;
  }
}

// constructor(private userService: UserService) {
//   if (localStorage.getItem('enterprise')) {
//     this.documentEnterprise = localStorage.getItem('enterprise')!;
//   }
// }

// ngOnInit(): void {
//   console.log(this.documentEnterprise);
//   this.userService.getUser(this.documentEnterprise).then((user) => {
//     this.users.push(user);
//   });
// }
// togglePasswordFields() {
//   this.showPasswordFields = !this.showPasswordFields;
// }
// }
