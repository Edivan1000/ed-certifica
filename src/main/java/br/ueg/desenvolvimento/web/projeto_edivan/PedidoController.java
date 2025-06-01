package br.ueg.desenvolvimento.web.projeto_edivan;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Listar todos os pedidos
    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "pedidos";
    }

    // Exibir formulário para criar um novo pedido
    @GetMapping("/novo")
    public String novoPedido(Model model) {
        Pedido novoPedido = new Pedido();

        Long proximoCodigo = pedidoRepository.findMaxCodigo() + 1; // Evita erro ao excluir todos
        novoPedido.setCodigoPedido(String.valueOf(proximoCodigo)); // Define código automático

        model.addAttribute("pedido", novoPedido);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("modoExclusao", false);

        return "formPedido";
    }

    // Salvar um novo pedido ou atualizar um existente
    @PostMapping("/salvar")
    public String salvarPedido(@ModelAttribute Pedido pedido) {
        if (pedido.getId() != null) { // Se o pedido já existe, validamos se está no banco
            Pedido pedidoExistente = pedidoRepository.findById(pedido.getId()).orElse(null);
            if (pedidoExistente == null) {
                throw new IllegalArgumentException("Pedido não encontrado no banco!");
            }
        } else { // Se for um novo pedido, geramos código contínuo baseado no maior existente
            Long proximoCodigo = pedidoRepository.findMaxCodigo() + 1; // Sempre pega o maior e soma 1
            pedido.setCodigoPedido(String.valueOf(proximoCodigo)); // Define código automaticamente
        }

        pedidoRepository.save(pedido);
        return "redirect:/pedidos";
    }

    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteRepository.findAll()); // Carrega clientes para o select
        model.addAttribute("modoExclusao", false); // Garante que a variável está definida
        return "formPedido";
    }

    @PostMapping("/editar/{id}")
    public String atualizarPedido(@PathVariable Long id, @ModelAttribute Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido não encontrado: " + id);
        }
        pedido.setId(id); // Garante que o ID seja mantido corretamente
        pedidoRepository.save(pedido);
        return "redirect:/pedidos";
    }

    // Confirmar exclusão do pedido
    @GetMapping("/excluir/{id}")
    public String confirmarExclusao(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        model.addAttribute("pedido", pedido);
        model.addAttribute("modoExclusao", true); // Define que estamos no modo exclusão
        return "formPedido"; // Redireciona para o mesmo formPedido com modo exclusão ativo
    }

    @PostMapping("/excluir/{id}")
    public String excluirPedido(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        if (pedido.getStatusPedido().equals("PENDENTE") || pedido.getStatusPedido().equals("APROVADO")) {
            model.addAttribute("erro", "Não é possível excluir um pedido com status PENDENTE ou APROVADO.");
            model.addAttribute("pedido", pedido);
            model.addAttribute("modoExclusao", true); // Garante que o formulário de exclusão seja exibido
            return "formPedido"; // Retorna ao formulário com a mensagem de erro
        }

        pedidoRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Pedido excluído com sucesso!"); // Mensagem de sucesso
        return "redirect:/pedidos"; // Retorna à tela de pedidos com mensagem de confirmação
    }

    @GetMapping("/busca")
    public String buscarPedidos(@RequestParam(name = "termo", required = false) String termo, Model model) {
        List<Pedido> pedidos;

        if (termo != null && !termo.isEmpty()) {
            pedidos = pedidoRepository.findByCodigoPedidoContainingIgnoreCase(termo);
        } else {
            pedidos = pedidoRepository.findAll();
        }

        model.addAttribute("pedidos", pedidos);
        return "pedidos"; // Retorna para pedidos.html
    }

}