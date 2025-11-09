package phone.network.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CreatePhoneRequest {
    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "\\d{4,}", message = "Номер должен содержать только цифры (минимум 4)")
    private String phoneNumber;

    public CreatePhoneRequest() {
    }

    public CreatePhoneRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
