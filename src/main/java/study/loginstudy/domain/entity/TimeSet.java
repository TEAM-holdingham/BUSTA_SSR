package study.loginstudy.domain.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TimeSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setTargetHours(int targetHours) {
        this.targetHours = targetHours;
    }

    @Column(nullable = false)
    private int targetHours;

    public void setTargetMinutes(int targetMinutes) {
        this.targetMinutes = targetMinutes;
    }

    @Column(nullable = false)
    private int targetMinutes;

    private LocalDateTime createdTime;

    @OneToOne
    @JoinColumn(name = "timer_id")
    private Timer timer;

    public TimeSet() {}

    public TimeSet(User user, int targetHours, int targetMinutes, LocalDateTime createdTime, Timer timer) {
        this.user = user;
        this.targetHours = targetHours;
        this.targetMinutes = targetMinutes;
        this.createdTime = createdTime;
        this.timer = timer;
    }

    // Getters for targetHours and targetMinutes
    public int getTargetHours() {
        return targetHours;
    }

    public int getTargetMinutes() {
        return targetMinutes;
    }

    // Getters and setters for other fields (if needed)

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
