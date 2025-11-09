package phone.network.service;

import phone.network.dto.PhoneDTO;
import phone.network.model.Phone;

import java.util.List;

public interface PhoneManagementService {
    PhoneDTO getPhone(String phoneNumber);
    List<PhoneDTO> getAllPhones();
    Phone addPhone(String phoneNumber);
    boolean deletePhone(String phoneNumber);
}
