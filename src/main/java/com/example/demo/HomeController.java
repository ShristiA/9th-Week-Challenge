package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class HomeController {
@Autowired
ProfileRepository profileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        if(result.hasErrors())
        {
            return "registration";
        }
        else
        {
            userService.saveUser(user);
            model.addAttribute("message","User Account Created");
        }
        return "login";
    }
    @RequestMapping("/login")
    public String index(){

        return"login";
    }

@RequestMapping("/")
    public String listMessage(Model model){
    model.addAttribute("messages", profileRepository.findAll());
    if(getUser() != null){
        model.addAttribute("user_id", getUser().getId()); //getting user name in the userid.
    }
    return "list";
}

@GetMapping("/add")
public String courseForm(Model model){
    model.addAttribute("message", new Profile());
    return "messageform";
}
@PostMapping("/process")
    public String processForm(@Valid Profile profile, BindingResult result) {
    if (result.hasErrors()) {
        return "messageform";
    }
    profile.setUser(getUser()); //like saving a value of userid in profile table.
    profileRepository.save(profile);
    return "redirect:/";
}

@RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model)
{
    model.addAttribute("message", profileRepository.findById(id).get());
    return "show";
}
@RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
    model.addAttribute("message", profileRepository.findById(id).get());
    return "messageform";

}
    protected User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByUserName(currentUsername);
        return user;
    }

}
