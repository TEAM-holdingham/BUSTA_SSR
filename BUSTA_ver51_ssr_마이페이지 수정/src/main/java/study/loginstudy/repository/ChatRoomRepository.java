package study.loginstudy.repository;

public interface ChatRoomRepository {

    String findRoomIdByKey(String roomKey);

    void saveRoomId(String roomKey, String roomId);
}
