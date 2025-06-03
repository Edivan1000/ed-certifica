package br.ueg.desenvolvimento.web.projeto_edivan.logs;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.ueg.desenvolvimento.web.projeto_edivan.models.LogAuditoria;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {
    // ✅ Busca todas as ações (Criação, Edição, Exclusão) relacionadas a clientes
    @Query("SELECT l FROM LogAuditoria l WHERE l.entidade = 'Cliente' ORDER BY l.dataHora DESC")
    List<LogAuditoria> buscarLogsClientes();
}


