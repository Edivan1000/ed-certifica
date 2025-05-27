package br.ueg.desenvolvimento.web.projeto_edivan;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {
    
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
