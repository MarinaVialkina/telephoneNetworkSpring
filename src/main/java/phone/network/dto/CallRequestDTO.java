package phone.network.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CallRequestDTO {
    @NotBlank(message = "Номер звонящего обязателен")
    @Pattern(regexp = "\\d{4,}", message = "Номер должен содержать только цифры")
    private String callerNumber;

    @NotBlank(message = "Номер целевого абонента обязателен")
    @Pattern(regexp = "\\d{4,}", message = "Номер должен содержать только цифры")
    private String receiverNumber;

    public CallRequestDTO() {}

    public CallRequestDTO(String callerNumber, String receiverNumber) {
        this.callerNumber = callerNumber;
        this.receiverNumber = receiverNumber;
    }

    // геттеры и сеттеры для обоих полей
    public String getCallerNumber() { return callerNumber; }
    public void setCallerNumber(String callerNumber) { this.callerNumber = callerNumber; }

    public String getReceiverNumber() { return receiverNumber; }
    public void setReceiverNumber(String receiverNumber) { this.receiverNumber = receiverNumber; }
}