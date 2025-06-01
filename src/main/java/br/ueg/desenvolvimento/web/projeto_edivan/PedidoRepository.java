package br.ueg.desenvolvimento.web.projeto_edivan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCodigoPedidoContainingIgnoreCase(String codigoPedido);

    @Query("SELECT COALESCE(MAX(p.codigoPedido), 0) FROM Pedido p")
    Long findMaxCodigo();

}
