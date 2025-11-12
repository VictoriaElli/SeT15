package controllers;

import domain.model.*;
import domain.service.OperationMessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final OperationMessageService messageService;

    public MessageController(OperationMessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public void addMessage(@RequestBody OperationMessage message) {
        messageService.addMessage(message);
    }

    @GetMapping("/{id}")
    public OperationMessage getMessage(@PathVariable int id) {
        return messageService.getMessage(id);
    }

    @GetMapping
    public List<OperationMessage> getAllMessages() {
        return messageService.getAllMessages();
    }

    @PutMapping("/{id}")
    public void updateMessage(@PathVariable int id, @RequestBody OperationMessage message) {
        message.setId(id);
        messageService.updateMessage(message);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable int id) {
        messageService.removeMessage(id);
    }
}
