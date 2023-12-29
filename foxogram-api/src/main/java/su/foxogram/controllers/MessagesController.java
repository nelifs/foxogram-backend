package su.foxogram.controllers;

import org.springframework.web.bind.annotation.*;
import su.foxogram.constructors.Message;
import su.foxogram.enums.APIEnum;

import java.util.List;

@RestController
@RequestMapping(APIEnum.MESSAGES)
public class MessagesController {

	@GetMapping
	public List<Message> getMessages(@RequestParam(required = false) int timestamp, @RequestParam(required = false) int before, @RequestParam(required = false) int limit) {
		return null;
	}

	@GetMapping("/{id}")
	public Message getMessage(@PathVariable long id) {
		return null;
	}

	@PostMapping("/{id}")
	public Message postMessage(@PathVariable long id) {
		return null;
	}

	@DeleteMapping("/{id}")
	public Message deleteMessage(@PathVariable long id) {
		return null;
	}

	@PatchMapping("/{id}")
	public Message patchMessage(@PathVariable long id) {
		return null;
	}
}
