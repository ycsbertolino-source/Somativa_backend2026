package br.com.cloudtech.monitoramento.controller;

import br.com.cloudtech.monitoramento.models.Maquina;
import br.com.cloudtech.monitoramento.models.Producao;
import br.com.cloudtech.monitoramento.repository.MonitoramentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registro e consulta dos dados de produção e consumo (RF02 e RF04).
 */
@RestController
@RequestMapping("/producao")
public class ProducaoController {

    private final MonitoramentoRepository repository;

    public ProducaoController(MonitoramentoRepository repository) {
        this.repository = repository;
    }

    // RF04 - Consultar dados de produção
    @GetMapping
    public List<Producao> listar() {
        return repository.listarProducoes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return repository.buscarProducao(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> erro(HttpStatus.NOT_FOUND, "Registro de produção com id " + id + " não encontrado"));
    }

    // Histórico básico de produção e consumo de uma máquina
    @GetMapping("/historico/{maquinaId}")
    public ResponseEntity<?> historico(@PathVariable Long maquinaId) {
        Optional<Maquina> maquina = repository.buscarMaquina(maquinaId);
        if (maquina.isEmpty()) {
            return erro(HttpStatus.NOT_FOUND, "Máquina com id " + maquinaId + " não encontrada");
        }
        List<Producao> registros = repository.listarProducoesPorMaquina(maquinaId);

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("maquinaId", maquinaId);
        resposta.put("nomeMaquina", maquina.get().getNome());
        resposta.put("totalRegistros", registros.size());
        resposta.put("totalPecasProduzidas", registros.stream().mapToInt(Producao::getPecasProduzidas).sum());
        resposta.put("consumoTotalKw", registros.stream().mapToDouble(Producao::getConsumoKw).sum());
        resposta.put("registros", registros);
        return ResponseEntity.ok(resposta);
    }

    // RF02 - Registrar dados de produção
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Producao producao) {
        if (producao.getMaquinaId() == null || repository.buscarMaquina(producao.getMaquinaId()).isEmpty()) {
            return erro(HttpStatus.NOT_FOUND, "Máquina informada não existe");
        }
        Producao salva = repository.salvarProducao(producao);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Producao dados) {
        return repository.buscarProducao(id)
                .<ResponseEntity<?>>map(producao -> {
                    producao.setConsumoKw(dados.getConsumoKw());
                    producao.setHorarioAcionamento(dados.getHorarioAcionamento());
                    producao.setPecasProduzidas(dados.getPecasProduzidas());
                    return ResponseEntity.ok(producao);
                })
                .orElseGet(() -> erro(HttpStatus.NOT_FOUND, "Registro de produção com id " + id + " não encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (repository.removerProducao(id)) {
            return ResponseEntity.noContent().build();
        }
        return erro(HttpStatus.NOT_FOUND, "Registro de produção com id " + id + " não encontrado");
    }

    private ResponseEntity<?> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
