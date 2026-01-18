package phone.network.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calls")
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "caller_number", nullable = false)
    private String callerPhoneNumber;

    @Column(name = "receiver_number", nullable = false)
    private String receiverPhoneNumber;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // Constructors
    public Call() {}

    public Call(String callerPhoneNumber, String receiverPhoneNumber) {
        this.callerPhoneNumber = callerPhoneNumber;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.startTime = LocalDateTime.now();
    }

    // Метод для определения собеседника
    public String getOtherNumber(String phoneNumber) {
        if (callerPhoneNumber != null && callerPhoneNumber.equals(phoneNumber)) {
            return receiverPhoneNumber;
        }
        if (receiverPhoneNumber != null && receiverPhoneNumber.equals(phoneNumber)) {
            return callerPhoneNumber;
        }
        return null;
    }

    // Метод для проверки активности звонка
    public boolean isActive() {
        return endTime == null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCallerPhoneNumber() { return callerPhoneNumber; }
    public void setCallerPhoneNumber(String callerPhoneNumber) { this.callerPhoneNumber = callerPhoneNumber; }

    public String getReceiverPhoneNumber() { return receiverPhoneNumber; }
    public void setReceiverPhoneNumber(String receiverPhoneNumber) { this.receiverPhoneNumber = receiverPhoneNumber; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}