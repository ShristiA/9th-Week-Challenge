package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
public class HomeController {
@Autowired
ProfileRepository profileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    CloudinaryConfig cloudc;

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
    model.addAttribute("profile", new Profile());
    return "messageform";
}
@PostMapping("/process")
    public String processForm(@Valid Profile profile, BindingResult result, @RequestParam("file") MultipartFile file) {
    if (result.hasErrors()) {
        return "messageform";
    }
    profile.setUser(getUser()); //like saving a value of userid in profile table.
    profileRepository.save(profile);

    if (file.isEmpty()) {
        return "redirect:/";
    }
    try {
        Map uploadResult = cloudc.upload(file.getBytes(),
                ObjectUtils.asMap("resourceType", "auto"));
        profile.setHeadshot(uploadResult.get("url").toString());
        profileRepository.save(profile);
    } catch (
            IOException e) {
        e.printStackTrace();
        return "redirect:/add";
    }
    return "redirect:/";
}

    public String gravatarImage(@PathVariable("id") long id, Model model) {
        String email = profileRepository.findById(id).get().getEmail();
        String hash = UserService.md5Hex(email);
        String gravatar = "https://www.gravatar.com/avatar/" + hash;
        model.addAttribute("email", gravatar);
        return "redirect:/";

    }

@RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model) {
    model.addAttribute("profile", profileRepository.findById(id).get());
    return "show";
}
@RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
    model.addAttribute("headshot",profileRepository.findById(id).get().getHeadshot());
    model.addAttribute("profile", profileRepository.findById(id).get());

    return "messageform";

}
    protected User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByUserName(currentUsername);
        return user;
    }
}
