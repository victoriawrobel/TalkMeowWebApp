package uni.projects.talkmeow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uni.projects.talkmeow.services.GlobalAttributeService;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private GlobalAttributeService globalAttributeService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAllAttributes(globalAttributeService.getGlobalAttributes());
    }
}
