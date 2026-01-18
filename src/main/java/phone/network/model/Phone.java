package phone.network.model;

import javax.persistence.*;

@Entity
@Table(name = "phones")
public class Phone {
    @Id
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private PhoneStatuses status;

    // Связь с активным звонком
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_call_id")
    private Call activeCall;

    // Constructors
    public Phone() {}

    public Phone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.status = PhoneStatuses.FREE;
    }

    // Вспомогательный метод для получения номера собеседника
    public String getOtherNumberInCall() {
        if (activeCall == null) {
            return null;
        }
        return activeCall.getOtherNumber(this.phoneNumber);
    }

    // Getters and Setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public PhoneStatuses getStatus() { return status; }
    public void setStatus(PhoneStatuses status) { this.status = status; }

    public Call getActiveCall() { return activeCall; }
    public void setActiveCall(Call activeCall) { this.activeCall = activeCall; }
}