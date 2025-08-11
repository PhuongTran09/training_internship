import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { TaskService } from "../../service/task.service";

@Component({
  selector: 'login-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, FormsModule]
})
export class LoginTaskComponent implements OnInit {
  username = '';
  password = '';
  taskMessage = '';
  tasks: any[] = [];
  isLoading = false;

  constructor(private taskService: TaskService) {}

  ngOnInit() {
    this.loadTasks();
  }

  private handleMessage(success: boolean, successMsg: string, errorMsg: string) {
    this.taskMessage = success ? successMsg : errorMsg;
  }

  create() {
    this.taskService.startLoginProcess(this.username, this.password).subscribe({
      next: () => {
        this.handleMessage(true, "✅ Tạo task thành công", "");
        this.loadTasks();
      },
      error: () => this.handleMessage(false, "", "❌ Tạo thất bại")
    });
  }

  loadTasks() {
    this.taskService.getCurrentTasks().subscribe({
      next: (data) => {
        this.tasks = data;
        this.taskMessage = data?.length
          ? `📋 Có ${data.length} task cần xử lý.`
          : "Không có task nào.";
      },
      error: () => this.handleMessage(false, "", "Không thể tải task.")
    });
  }

  completeTask(task: any) {
    if (!confirm("Bạn có chắc chắn muốn hoàn thành task này không?")) return;

    this.taskService.completeTask(task.id, {
      username: this.username,
      password: this.password
    }).subscribe({
      next: (res) => {
        this.loadTasks();
        if (!res.loginSuccess) this.taskMessage = "Đăng nhập thất bại.";
      },
      error: () => this.handleMessage(false, "", "Lỗi khi hoàn thành task.")
    });
  }
}
