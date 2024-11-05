package ru.practicum.user.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdInOrderById(List<Long> ids, PageRequest page);

    List<User> findAllByOrderByIdAsc(PageRequest page);

    Optional<User> findUserByEmail(String email);

    default User getUserById(Long userId) {
        return findById(userId)
                .orElseThrow(() -> new NotFoundException(userId, User.class.toString()));
    }
}
