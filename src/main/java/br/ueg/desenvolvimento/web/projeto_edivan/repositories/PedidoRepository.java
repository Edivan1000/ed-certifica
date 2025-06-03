package br.ueg.desenvolvimento.web.projeto_edivan.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.ueg.desenvolvimento.web.projeto_edivan.models.Cliente;
import br.ueg.desenvolvimento.web.projeto_edivan.models.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCodigoPedidoContainingIgnoreCase(String codigoPedido);

    List<Pedido> findByClienteId(Long clienteId);

    @Query("SELECT COALESCE(MAX(p.codigoPedido), 0) FROM Pedido p")
    Long findMaxCodigo();

    List<Pedido> findByCliente(Cliente cliente);

}
