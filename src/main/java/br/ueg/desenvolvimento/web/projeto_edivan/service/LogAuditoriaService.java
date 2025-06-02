package br.ueg.desenvolvimento.web.projeto_edivan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;

import br.ueg.desenvolvimento.web.projeto_edivan.logs.LogAuditoriaRepository;
import br.ueg.desenvolvimento.web.projeto_edivan.models.LogAuditoria;

@Service
public class LogAuditoriaService {

    private static final Logger logger = LoggerFactory.getLogger(LogAuditoriaService.class);
    private final LogAuditoriaRepository logAuditoriaRepository;

    @Autowired
    public LogAuditoriaService(LogAuditoriaRepository logAuditoriaRepository) {
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    public List<LogAuditoria> buscarLogsClientes() {
        return logAuditoriaRepository.buscarLogsClientes(); // ✅ Obtendo logs corretamente
    }

    public void registrarLog(String acao, String entidade, Long entidadeId, String usuario, String detalhes) {
        try {
            if (detalhes == null || detalhes.trim().isEmpty()) {
                detalhes = "⚠ Nenhuma alteração detalhada fornecida.";
            }

            LogAuditoria log = new LogAuditoria();
            log.setAcao(acao);
            log.setEntidade(entidade);
            log.setEntidadeId(entidadeId);
            log.setUsuario(usuario);
            log.setDetalhes(detalhes);
            log.setDataHora(LocalDateTime.now());

            logAuditoriaRepository.save(log);
            logger.info("✅ Log registrado com sucesso: [{}] {} - ID: {} - Usuário: {} - Detalhes: {}",
                    acao, entidade, entidadeId, usuario, detalhes);

        } catch (Exception e) {
            logger.error("❌ Erro ao registrar log de auditoria: ", e);
        }
    }
}

