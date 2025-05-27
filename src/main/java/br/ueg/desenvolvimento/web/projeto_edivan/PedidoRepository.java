package br.ueg.desenvolvimento.web.projeto_edivan;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}