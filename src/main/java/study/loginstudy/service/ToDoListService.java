package study.loginstudy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import study.loginstudy.domain.entity.ToDoList;
import study.loginstudy.domain.entity.User;
import study.loginstudy.repository.ToDoListRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ToDoListService {

    private final ToDoListRepository toDoListRepository;

    @Autowired
    public ToDoListService(ToDoListRepository toDoListRepository) {
        this.toDoListRepository = toDoListRepository;
    }

    public List<ToDoList> getAllToDoListsByUser(User user) {
        return toDoListRepository.findAllByUser(user);
    }

    public void addOrUpdateToDoList(ToDoList toDoList) {
        toDoListRepository.save(toDoList);
    }

    public void deleteToDoListByIdAndUser(Long id, User user) {
        Optional<ToDoList> toDoList = toDoListRepository.findByIdAndUser(id, user);
        toDoList.ifPresent(toDoListRepository::delete);
    }

    public void markAsCompletedByUser(Long id, User user) {
        Optional<ToDoList> toDoList = toDoListRepository.findByIdAndUser(id, user);
        toDoList.ifPresent(list -> {
            list.setCompleted("true");
            toDoListRepository.save(list);
        });
    }
}
