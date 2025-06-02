package br.ueg.desenvolvimento.web.projeto_edivan.controller;

import br.ueg.desenvolvimento.web.projeto_edivan.models.Cliente;
import br.ueg.desenvolvimento.web.projeto_edivan.models.Pedido;
import br.ueg.desenvolvimento.web.projeto_edivan.repositories.ClienteRepository;
import br.ueg.desenvolvimento.web.projeto_edivan.repositories.PedidoRepository;
import br.ueg.desenvolvimento.web.projeto_edivan.service.ClienteService;
import br.ueg.desenvolvimento.web.projeto_edivan.service.LogAuditoriaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private LogAuditoriaService logAuditoriaService;

    // Listar todos os clientes
    @GetMapping
    public String listarClientes(Model model) {
        List<Cliente> clientes = clienteRepository.findAll();

        // Adicionar pedidos ao modelo para cada cliente
        Map<Long, List<Pedido>> pedidosPorCliente = new HashMap<>();
        for (Cliente cliente : clientes) {
            List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);
            pedidosPorCliente.put(cliente.getId(), pedidos != null ? pedidos : new ArrayList<>()); // ✅ Evita valores nulos

        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("pedidosPorCliente", pedidosPorCliente);

        return "clientes";
    }

    // Exibir formulário para criar um novo cliente
    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente()); // Garante que um objeto cliente vazio é criado
        model.addAttribute("modoExclusao", false); // Evita erro na avaliação
        return "formCliente";
    }

    @PostMapping("/clientes")
    public String salvarCliente(@ModelAttribute Cliente cliente) {
        clienteRepository.save(cliente);
        logAuditoriaService.registrarLog("Criação", "Cliente", cliente.getId(), "Administrador",
                "Cliente criado: " + cliente.getNome());
        return "redirect:/clientes";
    }

    // Salvar um novo cliente com validação
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
            model.addAttribute("erro", e.getMessage()); // Passa a mensagem para o formulário
            return "formCliente"; // Retorna ao formulário mostrando o erro
        }
    }

    // Editar um cliente existente
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        model.addAttribute("cliente", cliente);
        model.addAttribute("modoExclusao", false); // Define corretamente a variável
        return "formCliente";
    }

    // Atualizar um cliente
    @PostMapping("/editar/{id}")
    public String atualizarCliente(@PathVariable Long id, @ModelAttribute Cliente cliente) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado: " + id);
        }
        cliente.setId(id); // Garante que o ID seja mantido
        logAuditoriaService.registrarLog("Edição", "Cliente", id, "Administrador",
                "Cliente atualizado: " + cliente.getNome());
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }

    // Confirmar exclusão do cliente
    @GetMapping("/excluir/{id}")
    public String confirmarExclusao(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        model.addAttribute("cliente", cliente);
        model.addAttribute("modoExclusao", true); // Define que estamos no modo exclusão
        return "formCliente"; // Usa a mesma página sem permitir edição
    }

    // Excluir cliente
    @PostMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        clienteRepository.deleteById(id); // Remove do banco
        redirectAttributes.addFlashAttribute("sucesso", "Cliente excluído com sucesso!"); // Envia mensagem amigável
        logAuditoriaService.registrarLog("Exclusão", "Cliente", id, "Administrador", "Cliente excluído com ID: " + id);
        return "redirect:/clientes"; // Retorna à tela de clientes com confirmação
    }

    @GetMapping("/busca")
    public String buscarClientes(@RequestParam(name = "termo", required = false) String termo, Model model) {
        List<Cliente> clientes;

        if (termo != null && !termo.isEmpty()) {
            clientes = clienteRepository.findByNomeContainingIgnoreCase(termo);
        } else {
            clientes = clienteRepository.findAll();
        }
        logAuditoriaService.registrarLog("Busca", "Cliente", null, "Administrador",
                "Busca realizada com termo: " + termo);
        model.addAttribute("clientes", clientes);
        return "clientes"; // Retorna para clientes.html
    }

}