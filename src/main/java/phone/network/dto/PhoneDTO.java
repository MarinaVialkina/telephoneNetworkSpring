package phone.network.dto;

import phone.network.model.Phone;
import phone.network.model.PhoneStatuses;

public class PhoneDTO {
    private String phoneNumber;
    private PhoneStatuses status;
    private String inCallWith;
    private Long activeCallId; // Новое поле для ID активного звонка

    public PhoneDTO() {
    }

    public PhoneDTO(String phoneNumber, PhoneStatuses status, String inCallWith, Long activeCallId) {
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.inCallWith = inCallWith;
        this.activeCallId = activeCallId;
    }

    public static PhoneDTO fromEntity(Phone phone) {
        if (phone == null) {
            return null;
        }

        String inCallWith = null;
        Long activeCallId = null;

        if (phone.getActiveCall() != null) {
            activeCallId = phone.getActiveCall().getId();
            inCallWith = phone.getActiveCall().getOtherNumber(phone.getPhoneNumber());
        }

        return new PhoneDTO(phone.getPhoneNumber(), phone.getStatus(), inCallWith, activeCallId);
    }

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneStatuses getStatus() {
        return status;
    }

    public void setStatus(PhoneStatuses status) {
        this.status = status;
    }

    public String getInCallWith() {
        return inCallWith;
    }

    public void setInCallWith(String inCallWith) {
        this.inCallWith = inCallWith;
    }

    public Long getActiveCallId() {
        return activeCallId;
    }

    public void setActiveCallId(Long activeCallId) {
        this.activeCallId = activeCallId;
    }
}
