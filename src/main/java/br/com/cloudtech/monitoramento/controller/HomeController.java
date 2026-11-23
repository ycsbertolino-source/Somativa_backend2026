package br.com.cloudtech.monitoramento.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints gerais: página inicial, status (RF05) e versão.
 */
@RestController
public class HomeController {

    @Value("${app.versao}")
    private String versao;

    @GetMapping("/")
    public Map<String, Object> paginaInicial() {
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", "Bem-vindo à API de Monitoramento de Máquinas Industriais - CloudTech Solutions");
        resposta.put("endpoints", List.of(
                "GET /", "GET /status", "GET /versao",
                "GET /maquinas", "GET /maquinas/{id}", "POST /maquinas/cadastro",
                "PUT /maquinas/{id}", "DELETE /maquinas/{id}",
                "GET /producao", "GET /producao/{id}", "GET /producao/historico/{maquinaId}",
                "POST /producao", "PUT /producao/{id}", "DELETE /producao/{id}"));
        return resposta;
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put("status", "online");
        resposta.put("mensagem", "Aplicação de monitoramento em execução");
        return resposta;
    }

    @GetMapping("/versao")
    public Map<String, String> versao() {
        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put("aplicacao", "monitoramento-maquinas");
        resposta.put("versao", versao);
        return resposta;
    }
}
