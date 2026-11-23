package br.com.cloudtech.monitoramento.models;

/**
 * Registro de dados operacionais de uma máquina (RF02):
 * consumo em kW, horário de acionamento e peças produzidas.
 */
public class Producao {

    private Long id;
    private Long maquinaId;
    private Double consumoKw;
    private String horarioAcionamento;
    private Integer pecasProduzidas;

    public Producao() {
    }

    public Producao(Long id, Long maquinaId, Double consumoKw, String horarioAcionamento, Integer pecasProduzidas) {
        this.id = id;
        this.maquinaId = maquinaId;
        this.consumoKw = consumoKw;
        this.horarioAcionamento = horarioAcionamento;
        this.pecasProduzidas = pecasProduzidas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMaquinaId() { return maquinaId; }
    public void setMaquinaId(Long maquinaId) { this.maquinaId = maquinaId; }

    public Double getConsumoKw() { return consumoKw; }
    public void setConsumoKw(Double consumoKw) { this.consumoKw = consumoKw; }

    public String getHorarioAcionamento() { return horarioAcionamento; }
    public void setHorarioAcionamento(String horarioAcionamento) { this.horarioAcionamento = horarioAcionamento; }

    public Integer getPecasProduzidas() { return pecasProduzidas; }
    public void setPecasProduzidas(Integer pecasProduzidas) { this.pecasProduzidas = pecasProduzidas; }
}
