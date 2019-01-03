package com.wiredbrain.friends.controller;

import com.wiredbrain.friends.model.Friend;
import com.wiredbrain.friends.service.FriendService;
import com.wiredbrain.friends.util.FieldErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FriendController {

    @Autowired
    FriendService friendService;

    /**
     *
     * @param friend
     * @return
     * @throws ValidationException
     */
    @PostMapping("/friend")
    Friend create(@Valid @RequestBody Friend friend) throws ValidationException {
        if (friend.getId() == 0 && friend.getFirstName() != null && friend.getLastName() != null)
            return friendService.save(friend);
        else throw new ValidationException("friend cannot be created");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    List<FieldErrorMessage> exceptionHandler(MethodArgumentNotValidException e){
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
       List<FieldErrorMessage> fieldErrorMessages =  fieldErrors.stream().map(fieldError ->
                new FieldErrorMessage(fieldError.getField(),fieldError.getDefaultMessage())).collect(Collectors.toList());
       return fieldErrorMessages;
    }

    /**
     *
     * @return
     */
    @GetMapping("/friend")
    Iterable<Friend> read() {
        return friendService.findAll();
    }

    /**
     *
     * @param friend
     * @return
     */
    @PutMapping("/friend")
    ResponseEntity<Friend> update(@RequestBody Friend friend) {
        if (friendService.findById(friend.getId()).isPresent())
            return new ResponseEntity(friendService.save(friend), HttpStatus.OK);
        else
            return new ResponseEntity(friend, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/friend/{id}")
    void delete(@PathVariable Integer id) {
        friendService.deleteById(id);
    }

    /**
     *
     * @param Id
     * @return
     */
    @GetMapping("/friend/{id}")
    Optional<Friend> findById(@PathVariable Integer Id) {
        return friendService.findById(Id);
    }

    /**
     *
     * @param firstName
     * @param lastName
     * @return
     */
    @GetMapping("/friend/search")
    Iterable<Friend> findByQuery(
            @RequestParam(value = "first", required = false) String firstName,
            @RequestParam(value = "last", required = false) String lastName
    ) {

        if (firstName != null && lastName != null) {

            return friendService.findByFirstNameAndLastName(firstName, lastName);
        } else if (firstName != null) {
            return friendService.findByFirstName(firstName);

        } else if (lastName != null) {
            return friendService.findByLastName(lastName);

        } else
            return friendService.findAll();


    }

}
