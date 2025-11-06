package phone.network.service;

import org.springframework.stereotype.Service;
import phone.network.model.Phone;
import phone.network.repository.PhoneRepository;

import java.util.List;

@Service
public class PhoneManagementServiceImpl implements PhoneManagementService{
    private final PhoneRepository phoneRepository;

    public PhoneManagementServiceImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

        @Override
    public Phone getPhone(String phoneNumber) {
            return phoneRepository.findById(phoneNumber).orElse(null);
    }

    @Override
    public List<Phone> getAllPhones() {
        return phoneRepository.findAll();
    }

    @Override
    public Phone addPhone(String phoneNumber) {
        if(phoneRepository.existsById(phoneNumber)){
            return null;
        }
        Phone newPhone = new Phone(phoneNumber);
        return phoneRepository.save(newPhone);

    }

    @Override
    public boolean deletePhone(String phoneNumber) {
        if (phoneRepository.existsById(phoneNumber)){
            phoneRepository.deleteById(phoneNumber);
            return true;
        }
        return false;
    }
}
