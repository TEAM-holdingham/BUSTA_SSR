package study.loginstudy.domain.dto;

public class NotificationRequest {
    private String title;   // 추가된 필드
    private String message;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
