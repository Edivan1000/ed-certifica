package br.ueg.desenvolvimento.web.projeto_edivan.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ueg.desenvolvimento.web.projeto_edivan.models.Cliente;
import br.ueg.desenvolvimento.web.projeto_edivan.models.Pedido;
import br.ueg.desenvolvimento.web.projeto_edivan.repositories.ClienteRepository;
import br.ueg.desenvolvimento.web.projeto_edivan.repositories.PedidoRepository;
import br.ueg.desenvolvimento.web.projeto_edivan.service.LogAuditoriaService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private LogAuditoriaService logAuditoriaService;

    // Listar todos os pedidos
    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "pedidos";
    }

    @GetMapping("/novo")
    public String novoPedido(Model model, RedirectAttributes redirectAttributes) {
        List<Cliente> clientes = clienteRepository.findAll(); // Busca todos os clientes cadastrados

        if (clientes.isEmpty()) { // Se não houver clientes, bloqueia a criação do pedido
            redirectAttributes.addFlashAttribute("erro",
                    "Não há clientes cadastrados. Adicione um cliente antes de criar um pedido.");
            return "redirect:/clientes"; // Redireciona para a tela de clientes
        }

        Pedido novoPedido = new Pedido();
        Long proximoCodigo = Optional.ofNullable(pedidoRepository.findMaxCodigo()).orElse(0L) + 1;
        novoPedido.setCodigoPedido(String.valueOf(proximoCodigo));

        model.addAttribute("pedido", novoPedido);
        model.addAttribute("clientes", clientes);
        model.addAttribute("modoExclusao", false);
        return "formPedido";
    }

    // Salvar um novo pedido ou atualizar um existente
    @PostMapping("/salvar")
    public String salvarPedido(@ModelAttribute Pedido pedido) {
        boolean isEdicao = pedido.getId() != null && pedidoRepository.existsById(pedido.getId()); // ✅ Correção da
                                                                                                  // verificação

        Pedido pedidoAnterior = isEdicao ? pedidoRepository.findById(pedido.getId()).orElse(null) : null;

        if (isEdicao && pedidoAnterior == null) {
            throw new IllegalArgumentException("Pedido não encontrado no banco!");
        }

        // Mantém a data original se o usuário não alterou
        if (isEdicao && pedido.getDataPedido() == null) {
            pedido.setDataPedido(pedidoAnterior.getDataPedido());
        }

        // Se for um novo pedido, gera código contínuo baseado no maior existente
        if (!isEdicao) {
            Long proximoCodigo = pedidoRepository.findMaxCodigo() + 1; // Sempre pega o maior e soma 1
            pedido.setCodigoPedido(String.valueOf(proximoCodigo)); // Define código automaticamente
        }

        pedidoRepository.save(pedido); // ✅ Salva pedido com todas as validações

        // ✅ Adiciona lógica para registro correto no log
        if (isEdicao && pedidoAnterior != null) {
            String detalhesEdicao = compararPedidos(pedidoAnterior, pedido);
            logAuditoriaService.registrarLog("Edição", "Pedido", pedido.getId(), "Administrador", detalhesEdicao);
        } else {
            logAuditoriaService.registrarLog("Criação", "Pedido", pedido.getId(), "Administrador",
                    "Pedido criado: Código " + pedido.getCodigoPedido());
        }

        return "redirect:/pedidos";
    }

    private String compararPedidos(Pedido antigo, Pedido novo) {
    List<String> detalhes = new ArrayList<>();

    if (!Objects.equals(antigo.getDataPedido(), novo.getDataPedido())) {
        detalhes.add("🔹 Data do Pedido alterada: de '" + antigo.getDataPedido() + "' para '" + novo.getDataPedido() + "'.");
    }
    if (!Objects.equals(antigo.getCodigoPedido(), novo.getCodigoPedido())) {
        detalhes.add("🔹 Código do Pedido alterado: de '" + antigo.getCodigoPedido() + "' para '" + novo.getCodigoPedido() + "'.");
    }
    if (!Objects.equals(antigo.getTipoCertificado(), novo.getTipoCertificado())) {
        detalhes.add("🔹 Tipo de Certificado alterado: de '" + antigo.getTipoCertificado() + "' para '" + novo.getTipoCertificado() + "'.");
    }
    if (!Objects.equals(antigo.getStatusPedido(), novo.getStatusPedido())) {
        detalhes.add("🔹 Status do Pedido alterado: de '" + antigo.getStatusPedido() + "' para '" + novo.getStatusPedido() + "'.");
    }

    return detalhes.isEmpty() ? "⚠ Pedido editado e salvo sem alterações detectadas." : String.join("\n", detalhes);
}



    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        if ("APROVADO".equals(pedido.getStatusPedido()) || "CANCELADO".equals(pedido.getStatusPedido())) {
            redirectAttributes.addFlashAttribute("erro",
                    "Este pedido já foi " + pedido.getStatusPedido().toLowerCase() +
                            " e não pode ser editado.");

            return "redirect:/pedidos"; // Retorna para pedidos com erro visível
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("modoExclusao", false);
        return "formPedido"; // Retorna ao formulário de edição
    }

    @PostMapping("/editar/{id}")
    public String atualizarPedido(@PathVariable Long id, @ModelAttribute Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido não encontrado: " + id);
        }
        pedido.setId(id); // Garante que o ID seja mantido corretamente
        pedidoRepository.save(pedido);
        logAuditoriaService.registrarLog("Edição", "Pedido", id, "Administrador",
                "Pedido atualizado: Código " + pedido.getCodigoPedido());
        return "redirect:/pedidos";
    }

    // Confirmar exclusão do pedido
    @GetMapping("/excluir/{id}")
    public String confirmarExclusao(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        if ("PENDENTE".equals(pedido.getStatusPedido()) || "APROVADO".equals(pedido.getStatusPedido())) {
            redirectAttributes.addFlashAttribute("erro",
                    "Não é possível excluir um pedido com status PENDENTE ou APROVADO.");
            return "redirect:/pedidos"; // Retorna para a tela de pedidos com mensagem de erro visível
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("modoExclusao", true);
        return "formPedido"; // Abre a página de confirmação de exclusão
    }

    @PostMapping("/excluir/{id}")
    public String excluirPedido(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        if ("PENDENTE".equals(pedido.getStatusPedido()) || "APROVADO".equals(pedido.getStatusPedido())) {
            model.addAttribute("erro", "Não é possível excluir um pedido com status PENDENTE ou APROVADO.");
            model.addAttribute("pedido", pedido);
            model.addAttribute("modoExclusao", true); // Garante que o formulário de exclusão seja exibido
            return "formPedido"; // Exibe o formulário com mensagem de erro antes da exclusão
        }

        pedidoRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Pedido excluído com sucesso!"); // Mensagem de sucesso
        logAuditoriaService.registrarLog("Exclusão", "Pedido", id, "Administrador", "Pedido excluído: ID " + id);
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
        logAuditoriaService.registrarLog("Busca", "Pedido", null, "Administrador",
                "Busca realizada: Código contém '" + termo + "'");
        model.addAttribute("pedidos", pedidos);
        return "pedidos"; // Retorna para pedidos.html
    }

}