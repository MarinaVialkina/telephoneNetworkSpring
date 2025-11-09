package phone.network.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CallRequest {
    @NotBlank(message = "Номер звонящего обязателен")
    @Pattern(regexp = "\\d{4,}", message = "Номер должен содержать только цифры")
    private String callerNumber;

    @NotBlank(message = "Номер целевого абонента обязателен")
    @Pattern(regexp = "\\d{4,}", message = "Номер должен содержать только цифры")
    private String targetNumber;

    public CallRequest() {}

    public CallRequest(String callerNumber, String targetNumber) {
        this.callerNumber = callerNumber;
        this.targetNumber = targetNumber;
    }

    // геттеры и сеттеры для обоих полей
    public String getCallerNumber() { return callerNumber; }
    public void setCallerNumber(String callerNumber) { this.callerNumber = callerNumber; }

    public String getTargetNumber() { return targetNumber; }
    public void setTargetNumber(String targetNumber) { this.targetNumber = targetNumber; }
}