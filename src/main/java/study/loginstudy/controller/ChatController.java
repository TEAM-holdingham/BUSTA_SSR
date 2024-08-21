package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import study.loginstudy.domain.dto.ChatMessage;
import study.loginstudy.domain.entity.ChatMessageEntity;
import study.loginstudy.repository.ChatMessageRepository;
import study.loginstudy.repository.ChatRoomRepository;
import study.loginstudy.repository.UserRepository;
import study.loginstudy.domain.entity.User;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // 기존 코드: HTML 반환 및 단순 문자열 응답

    @MessageMapping("/chat.sendMessage")
    @Transactional
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in sendMessage");
            return;
        }
        String sender = principal.getName();
        chatMessage.setSender(sender);
        chatMessage.setType("CHAT");

        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setSender(sender);
        chatMessageEntity.setReceiver(chatMessage.getReceiver());
        chatMessageEntity.setContent(chatMessage.getContent());
        chatMessageEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
        chatMessageEntity.setRoomId(chatMessage.getRoomId());

        try {
            chatMessageRepository.save(chatMessageEntity);
            System.out.println("Message saved to DB: " + chatMessageEntity);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving message to DB: " + e.getMessage());
        }

        messagingTemplate.convertAndSend("/topic/messages/" + chatMessage.getRoomId(), chatMessage);

        System.out.println("Sent message: " + chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @Transactional
    public void addUser(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in addUser");
            return;
        }
        String sender = principal.getName();
        chatMessage.setSender(sender);
        chatMessage.setContent(sender + " joined!");
        chatMessage.setType("JOIN");
        chatMessage.setRoomId(chatMessage.getRoomId());

        messagingTemplate.convertAndSend("/topic/messages/" + chatMessage.getRoomId(), chatMessage);
        System.out.println("User added: " + chatMessage);
    }

    @MessageMapping("/chat.createRoom")
    public void createRoom(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in createRoom");
            return;
        }
        String roomId = UUID.randomUUID().toString();
        chatMessage.setRoomId(roomId);

        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/roomId", chatMessage);
        System.out.println("Created room: " + roomId);
    }

    @GetMapping("/chat")
    public String chatPage(@RequestParam String friend, Model model, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in chatPage");
            return "redirect:/login";
        }
        model.addAttribute("friend", friend);
        model.addAttribute("username", principal.getName());
        return "Chat";
    }

    @GetMapping("/chat/history")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@RequestParam String friend, @RequestParam String roomId, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is null in getChatHistory");
            return null;
        }
        String username = principal.getName();
        List<ChatMessageEntity> chatMessages = chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
        return chatMessages.stream().map(msg -> new ChatMessage(msg.getSender(), msg.getReceiver(), msg.getContent(), msg.getTimestamp().toString(), msg.getRoomId(), msg.getType())).collect(Collectors.toList());
    }

    @GetMapping("/chat/roomId")
    @ResponseBody
    public Map<String, String> getRoomId(@RequestParam String friend, Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Principal is null in getRoomId");
        }
        String senderLoginId = principal.getName();  // 현재 사용자의 로그인 ID를 가져옴
        String senderNickname = getNicknameByLoginId(senderLoginId);  // 현재 사용자의 닉네임을 가져옴
        String receiverNickname = friend;  // 친구의 닉네임은 직접 제공됨
        String receiverLoginId = getLoginIdByNickname(receiverNickname);  // 친구의 로그인 ID를 가져오는 로직 필요

        String roomId = generateRoomId(senderLoginId, senderNickname, receiverLoginId, receiverNickname);

        Map<String, String> response = new HashMap<>();
        response.put("roomId", roomId);
        return response;
    }

    private String generateRoomId(String id1, String nickname1, String id2, String nickname2) {
        List<String> keys = Arrays.asList(id1 + nickname1, id2 + nickname2);
        Collections.sort(keys);
        return String.join("-", keys);
    }

    private String getNicknameByLoginId(String loginId) {
        Optional<User> user = userRepository.findByLoginId(loginId);
        return user.map(User::getNickname).orElseThrow(() -> new RuntimeException("User not found for loginId: " + loginId));
    }

    private String getLoginIdByNickname(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.map(User::getLoginId).orElseThrow(() -> new RuntimeException("User not found for nickname: " + nickname));
    }

    // 새로 추가된 API: JSON 응답을 위한 메서드들

    @GetMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiChatPage(@RequestParam String friend, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal == null) {
            response.put("status", "error");
            response.put("message", "User not authenticated");
            return ResponseEntity.status(401).body(response);
        }
        response.put("friend", friend);
        response.put("username", principal.getName());
        response.put("status", "success");
        return ResponseEntity.ok(response);  // JSON 형식으로 사용자 정보 반환
    }

    @GetMapping("/api/chat/history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiGetChatHistory(@RequestParam String friend, @RequestParam String roomId, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal == null) {
            response.put("status", "error");
            response.put("message", "User not authenticated");
            return ResponseEntity.status(401).body(response);
        }
        String username = principal.getName();
        List<ChatMessageEntity> chatMessages = chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
        List<ChatMessage> chatMessageList = chatMessages.stream()
                .map(msg -> new ChatMessage(msg.getSender(), msg.getReceiver(), msg.getContent(), msg.getTimestamp().toString(), msg.getRoomId(), msg.getType()))
                .collect(Collectors.toList());

        response.put("status", "success");
        response.put("messages", chatMessageList);
        return ResponseEntity.ok(response);  // JSON 형식으로 대화 기록 반환
    }

    @GetMapping("/api/chat/roomId")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiGetRoomId(@RequestParam String friend, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "User not authenticated"));
        }
        String senderLoginId = principal.getName();
        String senderNickname = getNicknameByLoginId(senderLoginId);
        String receiverNickname = friend;
        String receiverLoginId = getLoginIdByNickname(receiverNickname);

        String roomId = generateRoomId(senderLoginId, senderNickname, receiverLoginId, receiverNickname);

        Map<String, String> response = new HashMap<>();
        response.put("roomId", roomId);
        return ResponseEntity.ok(response);  // JSON 형식으로 roomId 반환
    }
}
