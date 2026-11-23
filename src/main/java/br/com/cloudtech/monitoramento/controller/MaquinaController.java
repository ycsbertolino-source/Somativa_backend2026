package br.com.cloudtech.monitoramento.controller;

import br.com.cloudtech.monitoramento.models.Maquina;
import br.com.cloudtech.monitoramento.repository.MonitoramentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CRUD de máquinas (RF01 e RF03).
 */
@RestController
@RequestMapping("/maquinas")
public class MaquinaController {

    private final MonitoramentoRepository repository;

    public MaquinaController(MonitoramentoRepository repository) {
        this.repository = repository;
    }

    // RF03 - Consultar máquinas cadastradas
    @GetMapping
    public List<Maquina> listar() {
        return repository.listarMaquinas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return repository.buscarMaquina(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> naoEncontrada(id));
    }

    // RF01 - Cadastrar máquina
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody Maquina maquina) {
        if (maquina.getNome() == null || maquina.getTipo() == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Os campos nome e tipo são obrigatórios"));
        }
        Maquina salva = repository.salvarMaquina(maquina);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Maquina dados) {
        return repository.buscarMaquina(id)
                .<ResponseEntity<?>>map(maquina -> {
                    maquina.setNome(dados.getNome());
                    maquina.setTipo(dados.getTipo());
                    maquina.setSetor(dados.getSetor());
                    maquina.setStatus(dados.getStatus());
                    return ResponseEntity.ok(maquina);
                })
                .orElseGet(() -> naoEncontrada(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (repository.removerMaquina(id)) {
            return ResponseEntity.noContent().build();
        }
        return naoEncontrada(id);
    }

    private ResponseEntity<?> naoEncontrada(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", "Máquina com id " + id + " não encontrada"));
    }
}
