package pl.electronicgradebook.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.electronicgradebook.dto.*;
import pl.electronicgradebook.exceptions.AppException;
import pl.electronicgradebook.mappers.UserMapper;
import pl.electronicgradebook.model.Role;
import pl.electronicgradebook.model.User;
import pl.electronicgradebook.repo.UserRepository;
import pl.electronicgradebook.security.UserAuthenticationProvider;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final UserAuthenticationProvider userAuthenticationProvider;

    public JwtResultDto login(LoginTO credentialsDto) {
        User user = userRepository.findByLogin(credentialsDto.getLogin())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.BAD_REQUEST));

        if (!credentialsDto.getPassword().equals(user.getPassword())) {
            return JwtResultDto.builder().success(false).build();
        }

//        if (!passwordEncoder.matches(user.getPassword(), credentialsDto.getPassword())) {
//            return JwtResultDto.builder().success(false).build();
//        }

        String token = userAuthenticationProvider.createToken(user);
        return JwtResultDto.builder().accessToken(token).success(true).build();
    }

    @Override
    public UserDto findUserByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.BAD_REQUEST));
        return userMapper.toUserDto(user);
    }

    @Override
    public ProfileDataDto getProfileData(UserDto userDto) {
        // TODO: Factory for every role!
        return ProfileDataDto.builder()
                .password("password")
                .firstName("Ala")
                .secondName("Yara")
                .dateOfBirth("1991")
                .email("testmail@gmail.com")
                .build();
    }


    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUserByLogin = userRepository.findByLogin(userDto.login());
        Optional<User> optionalUserByEmail = userRepository.findByEmail(userDto.email());

        if (optionalUserByLogin.isPresent()) {
            throw new AppException("Account with this login already exists", HttpStatus.BAD_REQUEST);
        } else if (optionalUserByEmail.isPresent()) {
            throw new AppException("Account with this email already exists", HttpStatus.BAD_REQUEST);
        } else {
            User user = userMapper.signUpToUser(userDto);
            user.setPassword(passwordEncoder.encode(userDto.password()));
            user.setRole(Role.STUDENT);

            User savedUser = userRepository.save(user);

            return userMapper.toUserDtoWithRoles(savedUser);
        }
    }
}
