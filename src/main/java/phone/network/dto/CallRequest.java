package phone.network.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CallRequest {
    @NotBlank(message = "Номер целевого абонента обязателен")
    @Pattern(regexp = "\\d{4,}", message = "Номер должен содержать только цифры")
    private String targetNumber;

    public CallRequest() {}

    public CallRequest(String targetNumber) {
        this.targetNumber = targetNumber;
    }

    public String getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(String targetNumber) {
        this.targetNumber = targetNumber;
    }
}
