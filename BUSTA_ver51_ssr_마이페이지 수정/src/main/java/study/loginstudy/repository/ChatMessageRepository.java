package study.loginstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findBySenderAndReceiver(String sender, String receiver);
    List<ChatMessageEntity> findByReceiverAndSender(String receiver, String sender);
    List<ChatMessageEntity> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(String sender, String receiver, String receiver2, String sender2);

    List<ChatMessageEntity> findByRoomIdOrderByTimestampAsc(String roomId);
}
