package br.com.cloudtech.monitoramento.repository;

import br.com.cloudtech.monitoramento.models.Maquina;
import br.com.cloudtech.monitoramento.models.Producao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Armazenamento em memória das máquinas e dos registros de produção.
 * (Os dados são reiniciados quando a aplicação reinicia.)
 */
@Component
public class MonitoramentoRepository {

    private final List<Maquina> maquinas = new CopyOnWriteArrayList<>();
    private final List<Producao> producoes = new CopyOnWriteArrayList<>();
    private final AtomicLong sequenciaMaquina = new AtomicLong();
    private final AtomicLong sequenciaProducao = new AtomicLong();

    public MonitoramentoRepository() {
        // Dados iniciais de exemplo
        salvarMaquina(new Maquina(null, "Máquina CNC 01", "CNC", "Usinagem", "em operação"));
        salvarProducao(new Producao(null, 1L, 12.5, "08:30", 150));
    }

    // ---------- Máquinas ----------

    public List<Maquina> listarMaquinas() {
        return maquinas;
    }

    public Optional<Maquina> buscarMaquina(Long id) {
        return maquinas.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public Maquina salvarMaquina(Maquina maquina) {
        maquina.setId(sequenciaMaquina.incrementAndGet());
        maquinas.add(maquina);
        return maquina;
    }

    public boolean removerMaquina(Long id) {
        producoes.removeIf(p -> p.getMaquinaId().equals(id));
        return maquinas.removeIf(m -> m.getId().equals(id));
    }

    // ---------- Produção ----------

    public List<Producao> listarProducoes() {
        return producoes;
    }

    public List<Producao> listarProducoesPorMaquina(Long maquinaId) {
        return producoes.stream().filter(p -> p.getMaquinaId().equals(maquinaId)).toList();
    }

    public Optional<Producao> buscarProducao(Long id) {
        return producoes.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Producao salvarProducao(Producao producao) {
        producao.setId(sequenciaProducao.incrementAndGet());
        producoes.add(producao);
        return producao;
    }

    public boolean removerProducao(Long id) {
        return producoes.removeIf(p -> p.getId().equals(id));
    }
}
