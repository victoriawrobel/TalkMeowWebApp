package uni.projects.talkmeow.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uni.projects.talkmeow.services.GlobalAttributeService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 02.10.2024
 */

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private GlobalAttributeService globalAttributeService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAllAttributes(globalAttributeService.getGlobalAttributes());
    }
}
