package br.ueg.desenvolvimento.web.projeto_edivan.controller;

import br.ueg.desenvolvimento.web.projeto_edivan.models.LogAuditoria;
import br.ueg.desenvolvimento.web.projeto_edivan.logs.LogAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RelatorioController {

    @Autowired
    private LogAuditoriaRepository logAuditoriaRepository;

    @GetMapping("/relatorios")
    public String mostrarRelatorios(Model model) {
        List<LogAuditoria> logs = logAuditoriaRepository.findAll();
        model.addAttribute("logs", logs);
        return "relatorios";
    }
}