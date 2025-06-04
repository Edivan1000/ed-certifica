package br.ueg.desenvolvimento.web.projeto_edivan.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ueg.desenvolvimento.web.projeto_edivan.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    boolean existsByCpfCnpj(String cpfCnpj);
}