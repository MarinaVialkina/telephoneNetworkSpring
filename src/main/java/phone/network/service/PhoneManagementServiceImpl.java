package phone.network.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phone.network.dto.PhoneDTO;
import phone.network.events.PhonesUpdatedEvent;
import phone.network.model.Phone;
import phone.network.repository.PhoneRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhoneManagementServiceImpl implements PhoneManagementService {
    private final PhoneRepository phoneRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PhoneManagementServiceImpl(PhoneRepository phoneRepository,
                                      ApplicationEventPublisher eventPublisher) {
        this.phoneRepository = phoneRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PhoneDTO getPhone(String phoneNumber) {
        return phoneRepository.findById(phoneNumber)
                .map(PhoneDTO::fromEntity)
                .orElse(null);
    }

    @Override
    public List<PhoneDTO> getAllPhones() {
        return phoneRepository.findAll().stream()
                .map(PhoneDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Phone addPhone(String phoneNumber) {
        if (phoneRepository.existsById(phoneNumber)) {
            return null;
        }
        Phone newPhone = new Phone(phoneNumber);
        Phone savedPhone = phoneRepository.save(newPhone);

        eventPublisher.publishEvent(new PhonesUpdatedEvent(this));

        return savedPhone;
    }

    @Override
    @Transactional
    public boolean deletePhone(String phoneNumber) {
        Phone phone = phoneRepository.findById(phoneNumber).orElse(null);
        if (phone == null) {
            return false;
        }

        // Проверяем, не находится ли телефон в разговоре
        if (phone.getActiveCall() != null) {
            // Завершаем звонок перед удалением телефона
            // Можно либо запретить удаление, либо автоматически завершить
            // Для простоты запрещаем удаление
            return false;
        }

        phoneRepository.deleteById(phoneNumber);
        eventPublisher.publishEvent(new PhonesUpdatedEvent(this));

        return true;
    }
}