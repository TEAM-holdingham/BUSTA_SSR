package study.loginstudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import study.loginstudy.domain.entity.ToDoList;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.ToDoListService;
import study.loginstudy.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ToDoListController {

    private final ToDoListService toDoListService;
    private final UserService userService;

    public ToDoListController(ToDoListService toDoListService, UserService userService) {
        this.toDoListService = toDoListService;
        this.userService = userService;
    }

    // 기존 코드: HTML 페이지 반환 및 리다이렉트

    @GetMapping("/todolist")
    public String viewHomePage(Model model) {
        User user = userService.getCurrentUser();  // 현재 사용자 가져오기
        List<ToDoList> listToDoLists = toDoListService.getAllToDoListsByUser(user);
        model.addAttribute("listToDoLists", listToDoLists);
        ToDoList toDoList = new ToDoList();
        model.addAttribute("toDoList", toDoList);
        return "todolist";
    }

    @PostMapping("/save")
    public String saveToDoList(@ModelAttribute("toDoList") ToDoList toDoList) {
        User user = userService.getCurrentUser();  // 현재 사용자 가져오기
        toDoList.setUser(user);  // 현재 사용자와 할 일 연결
        toDoListService.addOrUpdateToDoList(toDoList);
        return "redirect:/todolist";
    }

    @GetMapping("/delete/{id}")
    public String deleteToDoList(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        toDoListService.deleteToDoListByIdAndUser(id, user);
        return "redirect:/todolist";
    }

    @GetMapping("/complete/{id}")
    public String completeToDoList(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        toDoListService.markAsCompletedByUser(id, user);
        return "redirect:/todolist";
    }

//    // 새로 추가된 API: JSON 응답을 위한 메서드들
//
//    @GetMapping("/api/todolist")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> apiViewHomePage() {
//        User user = userService.getCurrentUser();  // 현재 사용자 가져오기
//        List<ToDoList> listToDoLists = toDoListService.getAllToDoListsByUser(user);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("listToDoLists", listToDoLists);
//        return ResponseEntity.ok(response);  // JSON 형식으로 할 일 목록 반환
//    }
//
//    @PostMapping("/api/save")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> apiSaveToDoList(@RequestBody ToDoList toDoList) {
//        User user = userService.getCurrentUser();  // 현재 사용자 가져오기
//        toDoList.setUser(user);  // 현재 사용자와 할 일 연결
//        toDoListService.addOrUpdateToDoList(toDoList);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "ToDoList saved successfully");
//        return ResponseEntity.ok(response);  // JSON 형식으로 결과 반환
//    }
//
//    @DeleteMapping("/api/delete/{id}")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> apiDeleteToDoList(@PathVariable("id") Long id) {
//        User user = userService.getCurrentUser();
//        toDoListService.deleteToDoListByIdAndUser(id, user);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "ToDoList deleted successfully");
//        return ResponseEntity.ok(response);  // JSON 형식으로 결과 반환
//    }
//
//    @PostMapping("/api/complete/{id}")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> apiCompleteToDoList(@PathVariable("id") Long id) {
//        User user = userService.getCurrentUser();
//        toDoListService.markAsCompletedByUser(id, user);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "ToDoList marked as completed");
//        return ResponseEntity.ok(response);  // JSON 형식으로 결과 반환
//    }
}
