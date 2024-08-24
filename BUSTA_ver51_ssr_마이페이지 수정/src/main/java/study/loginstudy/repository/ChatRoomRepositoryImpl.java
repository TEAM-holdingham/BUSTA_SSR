package study.loginstudy.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    private final Map<String, String> roomMap = new HashMap<>();

    @Override
    public String findRoomIdByKey(String roomKey) {
        return roomMap.get(roomKey);
    }

    @Override
    public void saveRoomId(String roomKey, String roomId) {
        roomMap.put(roomKey, roomId);
    }
}
