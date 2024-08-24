package study.loginstudy.domain.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Timer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean completed = false;

    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private LocalDateTime pauseTime;
    private boolean paused;
    private String pauseDuration;
    private LocalDateTime endTime;

    @Column(length = 1000)
    private String activityDescription;  // 활동 기록 필드 추가

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // User 엔티티와의 관계 설정 추가


    public String getPauseDuration() {
        return pauseDuration;
    }

    public void setPauseDuration(String pauseDuration) {
        this.pauseDuration = pauseDuration;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(LocalDateTime pauseTime) {
        this.pauseTime = pauseTime;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public long getStopToEndDuration() {
        return stopToEndDuration;
    }

    public void setStopToEndDuration(long stopToEndDuration) {
        this.stopToEndDuration = stopToEndDuration;
    }

    private long stopToEndDuration;
    private String elapsedTime; // HH:MM:SS 형식의 문자열로 저장될 elapsedTime

    // Getters and setters

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    // 다른 필드들에 대한 getter, setter 메서드들을 적절히 추가/수정해야 함

    //활동 기록 추가
    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }
}

