package br.ueg.desenvolvimento.web.projeto_edivan.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
public class LogAuditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String acao; // Exemplo: "Exclusão de Cliente"
    private String entidade; // Exemplo: "Cliente"
    private Long entidadeId; // ID do cliente ou pedido associado
    private String usuario; // Nome do usuário que realizou a ação
    private String detalhes; // Informações adicionais sobre a operação
    private LocalDateTime dataHora; // Data e hora da ação

    public LogAuditoria() {
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }

    public Long getEntidadeId() {
        return entidadeId;
    }

    public void setEntidadeId(Long entidadeId) {
        this.entidadeId = entidadeId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }



    public String getDetalhes() {
        return detalhes;
    }



    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }
    
}
