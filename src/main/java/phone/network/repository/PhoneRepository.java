package phone.network.repository;

import phone.network.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, String> {
    // Spring Data JPA автоматически реализует основные методы
    // findByPhoneNumber, save, delete, findAll и т.д.
}