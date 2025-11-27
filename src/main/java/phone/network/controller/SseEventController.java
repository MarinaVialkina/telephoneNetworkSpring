package phone.network.controller;

import org.springframework.http.MediaType;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import phone.network.dto.PhoneDTO;
import phone.network.events.PhonesUpdatedEvent;
import phone.network.service.PhoneManagementService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/events")
public class SseEventController {

    private final PhoneManagementService phoneManagementService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEventController(PhoneManagementService phoneManagementService) {
        this.phoneManagementService = phoneManagementService;
    }

    // Подключение клиента к SSE потоку
    @GetMapping(path = "/phones", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPhones() {
        SseEmitter emitter = new SseEmitter(60_000L); // 60 секунд timeout

        this.emitters.add(emitter);

        // Отправляем начальное состояние при подключении
        sendPhoneUpdate(emitter);

        // Обработчики отключения клиента
        emitter.onCompletion(() -> {
            this.emitters.remove(emitter);
            System.out.println("SSE connection completed");
        });

        emitter.onTimeout(() -> {
            this.emitters.remove(emitter);
            System.out.println("SSE connection timed out");
        });

        return emitter;
    }

    // Обработчик события обновления телефонов
    @EventListener
    public void handlePhonesUpdated(PhonesUpdatedEvent event) {
        System.out.println("Received PhonesUpdatedEvent, notifying " + emitters.size() + " clients");
        notifyAllClients();
    }

    // Рассылка обновлений всем подключенным клиентам
    private void notifyAllClients() {
        List<PhoneDTO> phones = phoneManagementService.getAllPhones();

        for (SseEmitter emitter : new CopyOnWriteArrayList<>(emitters)) {
            sendPhoneUpdate(emitter, phones);
        }
    }

    // Отправка обновления конкретному клиенту
    private void sendPhoneUpdate(SseEmitter emitter) {
        List<PhoneDTO> phones = phoneManagementService.getAllPhones();
        sendPhoneUpdate(emitter, phones);
    }

    private void sendPhoneUpdate(SseEmitter emitter, List<PhoneDTO> phones) {
        try {
            emitter.send(SseEmitter.event()
                    .name("phones-update")
                    .data(phones));
        } catch (IOException e) {
            // Клиент отключился - удаляем из списка
            emitter.completeWithError(e);
            emitters.remove(emitter);
        }
    }
}