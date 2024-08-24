package study.loginstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.loginstudy.domain.entity.ToDoList;
import study.loginstudy.domain.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {
    List<ToDoList> findAllByUser(User user);
    Optional<ToDoList> findByIdAndUser(Long id, User user);
}
