package phone.network.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phone.network.events.PhonesUpdatedEvent;
import phone.network.model.Call;
import phone.network.model.CallResult;
import phone.network.model.Phone;
import phone.network.model.PhoneStatuses;
import phone.network.repository.CallRepository;
import phone.network.repository.PhoneRepository;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

@Service
public class CallServiceImpl implements CallService {
    private final PhoneRepository phoneRepository;
    private final CallRepository callRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CallServiceImpl(PhoneRepository phoneRepository,
                           CallRepository callRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.phoneRepository = phoneRepository;
        this.callRepository = callRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public CallResult initiateCall(String callerNumber, String receiverNumber) {
        Phone caller = phoneRepository.findById(callerNumber).orElse(null);
        Phone receiver = phoneRepository.findById(receiverNumber).orElse(null);

        if (caller == null) {
            return new CallResult(false, "Абонент-инициатор не найден");
        }
        if (receiver == null) {
            return new CallResult(false, "Абонент не найден");
        }
        if (caller.getActiveCall() != null) {
            return new CallResult(false, "Абонент-инициатор уже в разговоре");
        }
        if (receiver.getActiveCall() != null) {
            return new CallResult(false, "Абонент занят");
        }

        // Создаем новый Call
        Call call = new Call(callerNumber, receiverNumber);
        Call savedCall = callRepository.save(call);

        // Обновляем статусы телефонов
        caller.setStatus(PhoneStatuses.BUSY);
        caller.setActiveCall(savedCall);
        phoneRepository.save(caller);

        receiver.setStatus(PhoneStatuses.RINGING);
        receiver.setActiveCall(savedCall);
        phoneRepository.save(receiver);

        eventPublisher.publishEvent(new PhonesUpdatedEvent(this));

        return new CallResult(true, "Вызов отправлен");
    }

    @Override
    @Transactional
    public CallResult answerCall(String receiverNumber) {
        Phone receiver = phoneRepository.findById(receiverNumber).orElse(null);

        if (receiver == null || receiver.getActiveCall() == null) {
            return new CallResult(false, "Нет активного вызова");
        }

        if (receiver.getStatus() != PhoneStatuses.RINGING) {
            return new CallResult(false, "Телефон не звонит");
        }

        // Находим звонящего
        Call call = receiver.getActiveCall();
        Phone caller = phoneRepository.findById(call.getCallerPhoneNumber()).orElse(null);

        if (caller == null) {
            return new CallResult(false, "Абонент не найден");
        }

        // Обновляем статусы
        receiver.setStatus(PhoneStatuses.BUSY);
        phoneRepository.save(receiver);

        caller.setStatus(PhoneStatuses.BUSY);
        phoneRepository.save(caller);

        eventPublisher.publishEvent(new PhonesUpdatedEvent(this));

        return new CallResult(true, "Вызов принят");
    }

    @Override
    @Transactional
    public CallResult terminateCall(String phoneNumber) {
        Phone phone = phoneRepository.findById(phoneNumber).orElse(null);

        if (phone == null || phone.getActiveCall() == null) {
            return new CallResult(false, "Нет активного вызова");
        }

        Call call = phone.getActiveCall();

        // Находим второго участника
        String otherNumber = call.getOtherNumber(phoneNumber);
        Phone otherPhone = phoneRepository.findById(otherNumber).orElse(null);

        // Обновляем звонок
        call.setEndTime(LocalDateTime.now());
        callRepository.save(call);

        // Освобождаем телефоны
        phone.setStatus(PhoneStatuses.FREE);
        phone.setActiveCall(null);
        phoneRepository.save(phone);

        if (otherPhone != null) {
            otherPhone.setStatus(PhoneStatuses.FREE);
            otherPhone.setActiveCall(null);
            phoneRepository.save(otherPhone);
        }

        eventPublisher.publishEvent(new PhonesUpdatedEvent(this));

        return new CallResult(true, "Вызов завершен");
    }
}