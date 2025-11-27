package phone.network.model;


import javax.persistence.Embeddable;

@Embeddable
public class Call {
    private String callerPhoneNumber;
    private String receiverPhoneNumber;

    public Call() {}

    public Call(String callerPhoneNumber, String receiverPhoneNumber) {
        this.callerPhoneNumber = callerPhoneNumber;
        this.receiverPhoneNumber = receiverPhoneNumber;
    }

    public String getOtherNumber(String phoneNumber) {
        if (callerPhoneNumber != null && callerPhoneNumber.equals(phoneNumber)) {
            return receiverPhoneNumber;
        }
        if (receiverPhoneNumber != null && receiverPhoneNumber.equals(phoneNumber)) {
            return callerPhoneNumber;
        }
        return null;
    }
}