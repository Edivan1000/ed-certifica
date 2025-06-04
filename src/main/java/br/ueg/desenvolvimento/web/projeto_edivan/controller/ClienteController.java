package br.ueg.desenvolvimento.web.projeto_edivan.controller;

import br.ueg.desenvolvimento.web.projeto_edivan.models.Cliente;
import br.ueg.desenvolvimento.web.projeto_edivan.models.Pedido;
import br.ueg.desenvolvimento.web.projeto_edivan.repositories.ClienteRepository;
import br.ueg.desenvolvimento.web.projeto_edivan.repositories.PedidoRepository;
import br.ueg.desenvolvimento.web.projeto_edivan.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private final ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Listar todos os clientes
    @GetMapping
    public String listarClientes(Model model) {
        List<Cliente> clientes = clienteRepository.findAll();

        Map<Long, List<Pedido>> pedidosPorCliente = new HashMap<>();
        for (Cliente cliente : clientes) {
            List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);
            pedidosPorCliente.put(cliente.getId(), pedidos != null ? pedidos : new ArrayList<>());
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("pedidosPorCliente", pedidosPorCliente);

        return "clientes";
    }

    // Exibir formulário para criar um novo cliente
    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modoExclusao", false);
        return "formCliente";
    }

    @PostMapping
    public String salvarCliente(@ModelAttribute Cliente cliente, Model model) {
        try {
            String documento = cliente.getCpfCnpj().replaceAll("\\D", "");

            if (documento.length() == 11 && !clienteService.validarCPF(documento)) {
                throw new IllegalArgumentException("CPF inválido!");
            }

            if (documento.length() == 14 && !clienteService.validarCNPJ(documento)) {
                throw new IllegalArgumentException("CNPJ inválido!");
            }

            clienteRepository.save(cliente);
            return "redirect:/clientes";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "formCliente";
        }
    }

    // Editar um cliente existente
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        model.addAttribute("cliente", cliente);
        model.addAttribute("modoExclusao", false);
        return "formCliente";
    }

    // Atualizar um cliente
    @PostMapping("/editar/{id}")
    public String atualizarCliente(@PathVariable Long id, @ModelAttribute Cliente cliente) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado: " + id);
        }

        cliente.setId(id);
        clienteRepository.save(cliente);

        return "redirect:/clientes";
    }

    // Confirmar exclusão do cliente
    @GetMapping("/excluir/{id}")
    public String confirmarExclusao(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        model.addAttribute("cliente", cliente);
        model.addAttribute("modoExclusao", true);
        return "formCliente";
    }

    // Excluir cliente
    @PostMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        clienteRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Cliente excluído com sucesso!");
        return "redirect:/clientes";
    }

    @GetMapping("/busca")
    public String buscarClientes(@RequestParam(name = "termo", required = false) String termo, Model model) {
        List<Cliente> clientes;

        if (termo != null && !termo.isEmpty()) {
            clientes = clienteRepository.findByNomeContainingIgnoreCase(termo);
        } else {
            clientes = clienteRepository.findAll();
        }

        model.addAttribute("clientes", clientes);
        return "clientes";
    }
}
