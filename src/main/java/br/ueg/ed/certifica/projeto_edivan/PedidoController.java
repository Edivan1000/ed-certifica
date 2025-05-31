package br.ueg.ed.certifica.projeto_edivan;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "pedidos";
    }

    @GetMapping("/novo")
    public String novoPedido(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("clientes", clienteRepository.findAll());
        return "formPedido";
    }

    @PostMapping
    public String salvarPedido(@ModelAttribute Pedido pedido) {
        pedido.setDataPedido(LocalDate.now());
        pedidoRepository.save(pedido);
        return "redirect:/pedidos";
    }
}

