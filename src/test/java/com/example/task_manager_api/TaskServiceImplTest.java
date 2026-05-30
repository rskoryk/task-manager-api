package com.example.task_manager_api;

import com.example.task_manager_api.dto.TaskRequestDto;
import com.example.task_manager_api.dto.TaskResponseDto;
import com.example.task_manager_api.dto.TaskUpdateDto;
import com.example.task_manager_api.entity.Task;
import com.example.task_manager_api.entity.TaskStatus;
import com.example.task_manager_api.entity.User;
import com.example.task_manager_api.exception.ResourceAlreadyExistsException;
import com.example.task_manager_api.exception.ResourceNotFoundException;
import com.example.task_manager_api.mapper.TaskMapper;
import com.example.task_manager_api.repository.TaskRepository;
import com.example.task_manager_api.repository.UserRepository;
import com.example.task_manager_api.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class TaskServiceImplTest {

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        taskService = new TaskServiceImpl(taskRepository, userRepository);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCreateTask_successfullyCreatesTask() {
        TaskRequestDto taskDto = new TaskRequestDto();
        taskDto.setTitle("Hello");
        taskDto.setDescription("description");
        taskDto.setStatus(TaskStatus.TO_DO);

        taskService.createTask(taskDto);

        verify(taskRepository).save(Mockito.argThat(task ->
                task.getTitle().equals(taskDto.getTitle()) &&
                        task.getDescription().equals(taskDto.getDescription()) &&
                        task.getStatus() == taskDto.getStatus()
        ));
    }

    @Test
    public void testCreateTask_throwsException_whenTaskWithTitleAlreadyExists() {
        TaskRequestDto taskDto = new TaskRequestDto();
        taskDto.setTitle("Hello");
        taskDto.setDescription("description");
        taskDto.setStatus(TaskStatus.TO_DO);

        Task task = new Task();
        task.setTitle("Hello");
        task.setDescription("description");
        task.setStatus(TaskStatus.TO_DO);

        when(taskRepository.findByOwnerIdAndTitle(1L, taskDto.getTitle())).thenReturn(Optional.of(task));

        assertThrows(ResourceAlreadyExistsException.class, () ->
                taskService.createTask(taskDto)
        );

        verify(taskRepository, never()).save(any());
    }

    @Test
    public void testGetTaskById_successfullyReturnsTaskDto(){
        Long taskId = 1L;
        TaskResponseDto taskDto1 = new TaskResponseDto();
        taskDto1.setTitle("title");
        taskDto1.setDescription("description");
        taskDto1.setStatus(TaskStatus.TO_DO);

        Task task = TaskMapper.mapTaskResponseDtoToTask(taskDto1);
        User owner = new User();
        owner.setId(1L);
        task.setOwner(owner);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskResponseDto taskDto2 = taskService.getTaskById(taskId);

        assertEquals(taskDto1, taskDto2);
    }

    @Test
    public void testGetTaskById_throwsException_whenTaskNotFound(){
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(taskId));
    }

    @Test
    public void testGetTaskByTitle_successfullyReturnsTaskDto() {
        String title = "Hello";
        TaskResponseDto taskDto1 = new TaskResponseDto();
        taskDto1.setTitle(title);
        taskDto1.setDescription("description");
        taskDto1.setStatus(TaskStatus.TO_DO);

        when(taskRepository.findByOwnerIdAndTitle(1L, title)).thenReturn(Optional.of(TaskMapper.mapTaskResponseDtoToTask(taskDto1)));

        TaskResponseDto taskDto2 = taskService.getTaskByTitle(title);

        assertEquals(taskDto1, taskDto2);
    }

    @Test
    public void testGetTaskByTitle_throwsException_whenTaskNotFound() {
        String title = "Missing Task";

        when(taskRepository.findByOwnerIdAndTitle(1L, title)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskByTitle(title));
    }

    @Test
    public void testUpdateTask_successfullyUpdatesAndReturnsTaskDto() {
        String newTitle = "New Title";
        String newDesc = "New Description";
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        User owner = new User();
        owner.setId(1L);

        Task existingTask = new Task();
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.TO_DO);
        existingTask.setOwner(owner);

        TaskUpdateDto updated = new TaskUpdateDto();
        updated.setTitle(newTitle);
        updated.setDescription(newDesc);
        updated.setStatus(newStatus);

        Task updatedTask = new Task();
        updatedTask.setTitle(newTitle);
        updatedTask.setDescription(newDesc);
        updatedTask.setStatus(newStatus);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponseDto result = taskService.updateTask(1L, updated);

        assertEquals(newTitle, result.getTitle());
        assertEquals(newDesc, result.getDescription());
        assertEquals(newStatus, result.getStatus());
    }

    @Test
    public void testUpdateTask_throwsException_whenTaskNotFound() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskUpdateDto updated = new TaskUpdateDto();
        updated.setTitle("New Title");
        updated.setDescription("New Description");
        updated.setStatus(TaskStatus.IN_PROGRESS);

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskId, updated));
    }

    @Test
    public void testDeleteTask_throwsException_whenUserIsNotOwner() {
        Long taskId = 1L;

        User anotherUser = new User();
        anotherUser.setId(2L);

        Task task = new Task();
        task.setOwner(anotherUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class, () -> taskService.deleteTask(taskId));

        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    public void testDeleteTask_successfullyRemovesTask() {
        Long taskId = 1L;

        Task task = new Task();
        User owner = new User();
        owner.setId(1L);
        task.setOwner(owner);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    public void testGetTasks_returnsMappedTaskDtoList() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task-1");
        task1.setDescription("desc");
        task1.setStatus(TaskStatus.TO_DO);

        List<Task> tasks = List.of(task1);
        Page<Task> page = new PageImpl<>(tasks);

        when(taskRepository.findAllByOwnerId(1L, Pageable.unpaged())).thenReturn(page);

        Page<TaskResponseDto> result = taskService.getTasks(Pageable.unpaged());

        assertEquals(1L, result.getTotalElements());
        assertEquals("Task-1", result.getContent().getFirst().getTitle());
    }

    @Test
    public void testGetTasksByStatus_returnsListOfTasksDtoByStatus() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task-1");
        task1.setDescription("desc");
        task1.setStatus(TaskStatus.IN_PROGRESS);

        List<Task> tasks = List.of(task1);
        Page<Task> page = new PageImpl<>(tasks);

        when(taskRepository.findAllByOwnerIdAndStatus(1L, TaskStatus.IN_PROGRESS, Pageable.unpaged())).thenReturn(page);

        Page<TaskResponseDto> result = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS, Pageable.unpaged());

        assertEquals(1L, result.getTotalElements());
        assertEquals("Task-1", result.getContent().getFirst().getTitle());
    }

    @Test
    public void testCreateTask_throwsException_whenRaceConditionHappens() {
        TaskRequestDto taskDto = new TaskRequestDto();
        taskDto.setTitle("Duplicate");
        taskDto.setDescription("desc");
        taskDto.setStatus(TaskStatus.TO_DO);

        when(taskRepository.findByOwnerIdAndTitle(1L, "Duplicate")).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenThrow(new DataIntegrityViolationException("Race Condition"));

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> taskService.createTask(taskDto)
        );

        assertEquals("Task with title 'Duplicate' already exists", exception.getMessage());

        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
