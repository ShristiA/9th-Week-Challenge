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
CourseRepository courseRepository;

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
    public String listCourse(Model model){
    model.addAttribute("courses", courseRepository.findAll());
    return "list";
}

@GetMapping("/add")
public String courseForm(Model model){
    model.addAttribute("course", new Course());
    return "courseform";
}
@PostMapping("/process")
    public String processForm(@Valid Course course, BindingResult result) {
    if (result.hasErrors()) {
        return "courseform";
    }
    courseRepository.save(course);
    return "redirect:/";
}

@RequestMapping("/detail/{id}")
    public String showCourse(@PathVariable("id") long id, Model model)
{
    model.addAttribute("course", courseRepository.findById(id).get());
    return "show";
}
@RequestMapping("/update/{id}")
    public String updateCourse(@PathVariable("id") long id, Model model){
    model.addAttribute("course", courseRepository.findById(id).get());
    return "courseform";
}
@RequestMapping("/delete/{id}")
    public String delCourse(@PathVariable("id") long id){
    courseRepository.deleteById(id);
    return "redirect:/";
}
    protected User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByUserName(currentUsername);
        return user;
    }

}
