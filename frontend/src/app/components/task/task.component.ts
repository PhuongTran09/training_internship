import {Component, OnInit} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {TaskService} from "../../service/task.service";

@Component({
  selector: 'login-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    FormsModule,
  ]
})
export class LoginTaskComponent implements OnInit {
  username: string = '';
  password: string = '';
  taskMessage: string = '';
  tasks: any[] = [];
  selectedTask: any = null;
  isLoading: boolean = false;

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  create(): void {
    this.taskMessage = '';
    this.selectedTask = null;

    this.taskService.startLoginProcess(this.username, this.password).subscribe({
      next: (res: any) => {
        this.taskMessage = "✅ Tạo task thành công";
        this.loadTasks();
      },
      error: (err) => {
        console.error(err);
        this.taskMessage = "❌ Tạo thất bại";
      }
    });
  }

loadTasks(): void {
  this.taskService.getCurrentTasks().subscribe({
    next: (data) => {
      this.tasks = data;
      if (data.length === 0) {
        this.taskMessage = "Không còn task nào.";
      } else {
        this.taskMessage = `📋 Có ${data.length} task cần xử lý.`;
      }
    },
    error: (err) => {
      console.error("Lỗi khi load task:", err);
      this.taskMessage = "Không thể tải task.";
    }
  });
}

  completeTask(task: any): void {

    const confirmed = confirm("Bạn có chắc chắn muốn hoàn thành task này không?");
    if (!confirmed) return;
    this.taskService.completeTask(task.id, {
      username: this.username,
      password: this.password
    }).subscribe({
      next: (res: any) => {
        if (res.loginSuccess) {
          this.loadTasks();
        } else {
          this.loadTasks();
        }
      },
      error: (err) => {
        console.error("Lỗi khi complete task:", err);
        this.taskMessage = "Lỗi khi hoàn thành task.";
      }
    });

  }



}
