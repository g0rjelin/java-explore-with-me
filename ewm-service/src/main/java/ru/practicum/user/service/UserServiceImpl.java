package ru.practicum.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.exception.UniqueConstraintException;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;

    static final String DUPLICATE_EMAIL_ERROR = "Электронная почта %s уже используется";


    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return UserMapper.toUserDto((Objects.isNull(ids) || ids.isEmpty()) ?
                userRepository.findAllByOrderByIdAsc(page) : userRepository.findAllByIdInOrderById(ids, page));
    }

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        checkUniqueEmail(newUserRequest.getEmail());
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(newUserRequest)));
    }

    @Override
    public void delete(Long id) {
        User delUser = userRepository.getUserById(id);
        userRepository.delete(delUser);
    }

    private void checkUniqueEmail(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new UniqueConstraintException(String.format(DUPLICATE_EMAIL_ERROR, email));
        }
    }
}
