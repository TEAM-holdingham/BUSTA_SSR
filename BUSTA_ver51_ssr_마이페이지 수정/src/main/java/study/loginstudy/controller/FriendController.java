package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.domain.entity.FriendRequest;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.FriendService;
import study.loginstudy.service.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String friendsPage() {
        return "friends";
    }

    @GetMapping("/user/principal")
    @ResponseBody
    public ResponseEntity<String> getPrincipalName(Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }

    @PostMapping("/request")
    @ResponseBody
    public ResponseEntity<?> sendFriendRequest(Principal principal, @RequestParam String receiverNickname) {
        String senderLoginId = principal.getName();
        System.out.println("Send request: Sender loginId: " + senderLoginId + ", Receiver nickname: " + receiverNickname);
        friendService.sendFriendRequest(senderLoginId, receiverNickname);
        return ResponseEntity.ok("Friend request sent to " + receiverNickname);
    }

    @PostMapping("/respond")
    @ResponseBody
    public ResponseEntity<?> respondToFriendRequest(Principal principal, @RequestParam String senderNickname, @RequestParam boolean accepted) {
        String receiverLoginId = principal.getName();
        System.out.println("Respond request: Sender nickname: " + senderNickname + ", Receiver loginId: " + receiverLoginId + ", Accepted: " + accepted);
        friendService.respondToFriendRequest(senderNickname, receiverLoginId, accepted);
        return ResponseEntity.ok("Response recorded");
    }

    @GetMapping("/pending")
    @ResponseBody
    public ResponseEntity<List<FriendRequest>> getPendingRequests(Principal principal) {
        String receiverLoginId = principal.getName();
        List<FriendRequest> requests = friendService.getPendingRequests(receiverLoginId);
        return ResponseEntity.ok(requests);
    }


    @GetMapping("/friendsList")
    @ResponseBody
    public ResponseEntity<List<FriendRequest>> getFriends(Principal principal) {
        String loginId = principal.getName();
        List<FriendRequest> friends = friendService.getFriends(loginId);
        return ResponseEntity.ok(friends);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFriend(
            @RequestParam String currentUserLoginId,
            @RequestParam String friendLoginId) {
        try {
            friendService.removeFriend(currentUserLoginId, friendLoginId);
            return ResponseEntity.ok("Friend deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting friend: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<User>> searchFriends(Principal principal, @RequestParam String nickname) {
        String currentUserNickname = userService.getNicknameByLoginId(principal.getName());
        List<User> users = userService.findUsersByNicknameStartingWithExcludingCurrentUser(nickname, currentUserNickname);
        return ResponseEntity.ok(users);
    }

    // API 엔드포인트
    @GetMapping("/api/friends")
    public ResponseEntity<Map<String, Object>> apiFriendsPage() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("page", "friends");

        return ResponseEntity.ok(response);
    }

}
