package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listmessages(Model model){
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String messageForm(Model model){
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm(@Valid @ModelAttribute Message message, BindingResult result,
                              @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()){
            return "messageform";
        }

        if (!file.isEmpty()){
            try {
                System.out.println("Try");

                Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype","auto"));
                message.setPicture(uploadResult.get("url").toString());
                messageRepository.save(message);
            } catch (IOException e) {
                System.out.println("Catch");

                e.printStackTrace();
                return "redirect:/add";
            }
        }
        else {
            messageRepository.save(message);
            return "redirect:/";
        }
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showCourse(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updatemessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/delete/{id}")
    public String delmessage(@PathVariable("id") long id) {
        messageRepository.deleteById(id);
        return "redirect:/";
    }
}
