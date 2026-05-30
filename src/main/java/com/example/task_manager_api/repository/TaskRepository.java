package com.example.task_manager_api.repository;

import org.springframework.data.domain.Page;
import com.example.task_manager_api.entity.Task;
import com.example.task_manager_api.entity.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByOwnerIdAndTitle(Long userId, String title);
    Page<Task> findAllByOwnerId(Long userId, Pageable pageable);
    Page<Task> findAllByOwnerIdAndStatus(Long userId, TaskStatus status, Pageable pageable);
}
