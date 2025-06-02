package br.ueg.desenvolvimento.web.projeto_edivan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ueg.desenvolvimento.web.projeto_edivan.logs.LogAuditoriaRepository;
import br.ueg.desenvolvimento.web.projeto_edivan.models.LogAuditoria;

@Service
public class LogAuditoriaService {
    @Autowired
    private LogAuditoriaRepository logAuditoriaRepository;

    public void registrarLog(String acao, String entidade, Long entidadeId, String usuario, String detalhes) {
        LogAuditoria log = new LogAuditoria();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setUsuario(usuario);
        log.setDetalhes(detalhes);
        logAuditoriaRepository.save(log);
    }
}