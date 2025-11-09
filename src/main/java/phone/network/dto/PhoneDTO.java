package phone.network.dto;

import phone.network.model.Phone;
import phone.network.model.PhoneStatuses;

public class PhoneDTO {
    private String phoneNumber;
    private PhoneStatuses status;
    private String inCallWith;

    public PhoneDTO() {
    }

    public PhoneDTO(String phoneNumber, PhoneStatuses status, String inCallWith) {
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.inCallWith = inCallWith;
    }

    public static PhoneDTO fromEntity(Phone phone){
        if (phone == null){
            return null;
        }
        String inCallWith = null;
        if(phone.getCurrentCall() != null){
            inCallWith = phone.getCurrentCall().getOtherNumber(phone.getPhoneNumber());
        }
        return new PhoneDTO(phone.getPhoneNumber(), phone.getStatus(), inCallWith);
    }

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
}
