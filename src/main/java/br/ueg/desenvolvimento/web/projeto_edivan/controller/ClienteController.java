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

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente, Model model, RedirectAttributes redirectAttributes) {
        try {
            String documento = cliente.getCpfCnpj().replaceAll("\\D", "");

            if (documento.length() == 11 && !clienteService.validarCPF(documento)) {
                throw new IllegalArgumentException("CPF inválido!");
            }

            if (documento.length() == 14 && !clienteService.validarCNPJ(documento)) {
                throw new IllegalArgumentException("CNPJ inválido!");
            }

            // 🚀 Verifica se o CPF/CNPJ já existe no banco
            if (clienteRepository.existsByCpfCnpj(cliente.getCpfCnpj())) {
                throw new IllegalArgumentException("⚠ CPF/CNPJ já cadastrado! Use um diferente.");
            }

            clienteRepository.save(cliente);
            redirectAttributes.addFlashAttribute("sucesso", "✅ Cliente cadastrado com sucesso!");
            return "redirect:/clientes";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "formCliente"; // Retorna ao formulário com mensagem de erro
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
public String atualizarCliente(@PathVariable Long id, @ModelAttribute Cliente clienteAtualizado,
        RedirectAttributes redirectAttributes) {

    // Busca o cliente original no banco
    Cliente clienteExistente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

    // Proteção: mantém nome e CPF/CNPJ originais
    clienteAtualizado.setId(id);
    clienteAtualizado.setNome(clienteExistente.getNome());
    clienteAtualizado.setCpfCnpj(clienteExistente.getCpfCnpj());

    clienteRepository.save(clienteAtualizado);

    redirectAttributes.addFlashAttribute("sucesso", "✅ Cliente atualizado com sucesso!");
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
