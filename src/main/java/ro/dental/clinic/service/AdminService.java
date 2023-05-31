package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.UserRepository;
import ro.dental.clinic.model.UserAccountList;
import ro.dental.clinic.model.UserAccountListItem;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public UserAccountList getAllUsers() {
        var userAccountList = new UserAccountList();
        userAccountList.setItems(userRepository.findAll().stream().map(user -> {
            var newUser = new UserAccountListItem();
            newUser.setId(user.getUserId());
            newUser.setRole(user.getRole());
            newUser.setEmail(user.getEmail());
            newUser.setUsername(user.getUsername());
            newUser.setLastName(user.getLastName());
            newUser.setFirstName(user.getFirstName());
            return newUser;
        }).collect(Collectors.toList()));
        return userAccountList;
    }
}
