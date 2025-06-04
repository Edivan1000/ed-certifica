package br.ueg.desenvolvimento.web.projeto_edivan.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ueg.desenvolvimento.web.projeto_edivan.models.Cliente;
import br.ueg.desenvolvimento.web.projeto_edivan.repositories.ClienteRepository;

@Service
public class ClienteService {
     @Autowired
    private ClienteRepository clienteRepository;

    public void salvarCliente(Cliente cliente) {
        Optional<Cliente> clienteExistente = clienteRepository.findByCpfCnpj(cliente.getCpfCnpj());

        if (clienteExistente.isPresent() && !clienteExistente.get().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("CPF/CNPJ já cadastrado!");
        }

        clienteRepository.save(cliente);
    }

    public boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", ""); // Remove caracteres não numéricos
        if (cpf.length() != 11)
            return false;

        // Impedir CPFs com números repetidos (ex: 000.000.000-00, 111.111.111-11)
        if (cpf.matches("(\\d)\\1{10}"))
            return false;

        int soma = 0, resto;
        int[] pesoCPF = { 10, 9, 8, 7, 6, 5, 4, 3, 2 };

        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * pesoCPF[i];
        }

        resto = soma % 11;
        int dv1 = (resto < 2) ? 0 : 11 - resto;

        soma = 0;
        int[] pesoCPF2 = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * pesoCPF2[i];
        }

        resto = soma % 11;
        int dv2 = (resto < 2) ? 0 : 11 - resto;

        return cpf.charAt(9) - '0' == dv1 && cpf.charAt(10) - '0' == dv2;
    }

    public boolean validarCNPJ(String cnpj) {
        cnpj = cnpj.replaceAll("\\D", ""); // Remove caracteres não numéricos
        if (cnpj.length() != 14)
            return false;

        // Impedir CNPJs com números repetidos (ex: 00.000.000/0000-00,
        // 11.111.111/1111-11)
        if (cnpj.matches("(\\d)\\1{13}"))
            return false;

        int[] pesoCNPJ1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
        int[] pesoCNPJ2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
        int soma = 0, resto;

        for (int i = 0; i < 12; i++) {
            soma += (cnpj.charAt(i) - '0') * pesoCNPJ1[i];
        }

        resto = soma % 11;
        int dv1 = (resto < 2) ? 0 : 11 - resto;

        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += (cnpj.charAt(i) - '0') * pesoCNPJ2[i];
        }

        resto = soma % 11;
        int dv2 = (resto < 2) ? 0 : 11 - resto;

        return cnpj.charAt(12) - '0' == dv1 && cnpj.charAt(13) - '0' == dv2;
    }

}
