package br.com.bank.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.bank.user_service.domain.DTO.UserRequest;
import br.com.bank.user_service.domain.DTO.UserResponse;
import br.com.bank.user_service.domain.model.User;
import br.com.bank.user_service.service.UserService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Flux<UserResponse> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public Mono<UserResponse> getUserById(@PathVariable("id") Long id) {
        return userService.findUserById(id);
    }

    @GetMapping("/email/{email}")
    public Mono<User> getUserByEmail(@PathVariable("email") String email) {
        return userService.findUserByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateUser(@PathVariable("id") Long id, @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }
}
