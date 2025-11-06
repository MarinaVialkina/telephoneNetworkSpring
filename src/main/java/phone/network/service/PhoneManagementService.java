package phone.network.service;

import phone.network.model.CallResult;
import phone.network.model.Phone;

import java.util.List;

public interface PhoneManagementService {
    Phone getPhone(String phoneNumber);
    List<Phone> getAllPhones();
    Phone addPhone(String phoneNumber);
    boolean deletePhone(String phoneNumber);
}
