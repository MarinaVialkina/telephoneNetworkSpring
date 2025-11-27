package phone.network.model;

import javax.persistence.*;


@Entity
@Table(name = "phones")
public class Phone {
    @Id
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private PhoneStatuses status;

    @Embedded
    private Call currentCall;

    public Phone() {}

    public Phone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.status = PhoneStatuses.FREE;
    }

    public Phone(String phoneNumber, PhoneStatuses status, Call currentCall) {
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.currentCall = currentCall;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public PhoneStatuses getStatus() { return status; }
    public void setStatus(PhoneStatuses status) {
        this.status = status;
    }

    public Call getCurrentCall() { return currentCall; }
    public void setCurrentCall(Call currentCall) {
        this.currentCall = currentCall;
    }
}