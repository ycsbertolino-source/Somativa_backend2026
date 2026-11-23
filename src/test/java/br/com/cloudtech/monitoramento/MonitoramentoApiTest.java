package br.com.cloudtech.monitoramento;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes automatizados dos endpoints com JUnit 5 + MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MonitoramentoApiTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String MAQUINA_JSON = """
            {
              "nome": "Prensa Hidraulica 02",
              "tipo": "Prensa",
              "setor": "Estamparia",
              "status": "em operacao"
            }
            """;

    /** Cadastra uma máquina e devolve o id gerado (usado por vários testes). */
    private int cadastrarMaquina() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/maquinas/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAQUINA_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.id");
    }

    @Test
    @DisplayName("a) Página inicial responde com HTTP 200")
    void paginaInicialRespondeCorretamente() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("b) /status retorna aplicação online")
    void statusRetornaOnline() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("online"));
    }

    @Test
    @DisplayName("c) É possível cadastrar uma máquina (HTTP 201)")
    void cadastrarMaquina_retorna201() throws Exception {
        mockMvc.perform(post("/maquinas/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAQUINA_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Prensa Hidraulica 02"))
                .andExpect(jsonPath("$.tipo").value("Prensa"))
                .andExpect(jsonPath("$.setor").value("Estamparia"));
    }

    @Test
    @DisplayName("d) É possível listar as máquinas cadastradas")
    void listarMaquinas_retornaLista() throws Exception {
        mockMvc.perform(get("/maquinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("e) É possível registrar dados de produção (HTTP 201)")
    void registrarProducao_retorna201() throws Exception {
        int id = cadastrarMaquina();
        String producao = """
                { "maquinaId": %d, "consumoKw": 12.5, "horarioAcionamento": "08:30", "pecasProduzidas": 150 }
                """.formatted(id);

        mockMvc.perform(post("/producao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(producao))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.consumoKw").value(12.5))
                .andExpect(jsonPath("$.horarioAcionamento").value("08:30"))
                .andExpect(jsonPath("$.pecasProduzidas").value(150));
    }

    @Test
    @DisplayName("Consulta dos dados de produção retorna lista")
    void listarProducao_retornaLista() throws Exception {
        mockMvc.perform(get("/producao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Histórico de produção soma peças e consumo")
    void historicoProducao() throws Exception {
        int id = cadastrarMaquina();
        mockMvc.perform(post("/producao").contentType(MediaType.APPLICATION_JSON)
                .content("{\"maquinaId\": " + id + ", \"consumoKw\": 10.0, \"horarioAcionamento\": \"07:00\", \"pecasProduzidas\": 100}"));
        mockMvc.perform(post("/producao").contentType(MediaType.APPLICATION_JSON)
                .content("{\"maquinaId\": " + id + ", \"consumoKw\": 5.0, \"horarioAcionamento\": \"13:00\", \"pecasProduzidas\": 50}"));

        mockMvc.perform(get("/producao/historico/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRegistros").value(2))
                .andExpect(jsonPath("$.totalPecasProduzidas").value(150))
                .andExpect(jsonPath("$.consumoTotalKw").value(15.0));
    }

    @Test
    @DisplayName("f) PUT atualiza a máquina e retorna HTTP 200")
    void atualizarMaquina_retorna200() throws Exception {
        int id = cadastrarMaquina();
        mockMvc.perform(put("/maquinas/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Prensa 02\", \"tipo\": \"Prensa\", \"setor\": \"Estamparia\", \"status\": \"parada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("parada"));
    }

    @Test
    @DisplayName("f) DELETE remove a máquina e retorna HTTP 204")
    void excluirMaquina_retorna204() throws Exception {
        int id = cadastrarMaquina();
        mockMvc.perform(delete("/maquinas/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/maquinas/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("f) Máquina inexistente retorna HTTP 404")
    void maquinaInexistente_retorna404() throws Exception {
        mockMvc.perform(get("/maquinas/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("f) Produção para máquina inexistente retorna HTTP 404")
    void producaoMaquinaInexistente_retorna404() throws Exception {
        mockMvc.perform(post("/producao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"maquinaId\": 9999, \"consumoKw\": 1.0, \"horarioAcionamento\": \"10:00\", \"pecasProduzidas\": 1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("f) Cadastro sem nome retorna HTTP 400")
    void cadastroInvalido_retorna400() throws Exception {
        mockMvc.perform(post("/maquinas/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\": \"CNC\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("/versao retorna a versão da aplicação")
    void versao_retorna200() throws Exception {
        mockMvc.perform(get("/versao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versao").value("1.0.0"));
    }
}
