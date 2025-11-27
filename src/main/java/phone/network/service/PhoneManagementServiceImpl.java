package phone.network.service;

import org.springframework.stereotype.Service;
import phone.network.dto.PhoneDTO;
import phone.network.events.PhonesUpdatedEvent;
import phone.network.model.Phone;
import phone.network.repository.PhoneRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;

@Service
public class PhoneManagementServiceImpl implements PhoneManagementService{
    private final PhoneRepository phoneRepository;

    private final ApplicationEventPublisher eventPublisher; // ← НОВОЕ ПОЛЕ

    public PhoneManagementServiceImpl(PhoneRepository phoneRepository,
                                      ApplicationEventPublisher eventPublisher) { // ← НОВЫЙ ПАРАМЕТР
        this.phoneRepository = phoneRepository;
        this.eventPublisher = eventPublisher;
    }

        @Override
    public PhoneDTO getPhone(String phoneNumber) {
            return phoneRepository.findById(phoneNumber).map(PhoneDTO::fromEntity).orElse(null);
    }

    @Override
    public List<PhoneDTO> getAllPhones() {
        return phoneRepository.findAll().stream()
                .map(PhoneDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Phone addPhone(String phoneNumber) {
        if(phoneRepository.existsById(phoneNumber)){
            return null;
        }
        Phone newPhone = new Phone(phoneNumber);
        Phone savedPhone = phoneRepository.save(newPhone);

        eventPublisher.publishEvent(new PhonesUpdatedEvent(this)); // ← ОДНА СТРОКА

        return savedPhone;
    }

    @Override
    public boolean deletePhone(String phoneNumber) {
        if (phoneRepository.existsById(phoneNumber)){
            phoneRepository.deleteById(phoneNumber);

            eventPublisher.publishEvent(new PhonesUpdatedEvent(this)); // ← ОДНА СТРОКА

            return true;
        }
        return false;
    }
}
