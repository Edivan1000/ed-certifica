package br.ueg.desenvolvimento.web.projeto_edivan.logs;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ueg.desenvolvimento.web.projeto_edivan.models.LogAuditoria;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {
}

