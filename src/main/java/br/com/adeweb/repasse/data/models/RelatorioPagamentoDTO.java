package br.com.adeweb.repasse.data.models;

import java.time.LocalDateTime;

public class RelatorioPagamentoDTO {
    private String bancoPagamento;
    private LocalDateTime dataDeposito;
    private int status;
    private String medico;
    private String email;
    private LocalDateTime dataProcedimento;
    private double valorProcedimento;
    private Long repasseId;
    private String procedimento;
    private String paciente;
    private String convenio;

    public RelatorioPagamentoDTO(String bancoPagamento, LocalDateTime dataDeposito, int status, String medico, String email, LocalDateTime dataProcedimento, double valorProcedimento, Long repasseId, String procedimento, String paciente, String convenio) {
        this.bancoPagamento = bancoPagamento;
        this.dataDeposito = dataDeposito;
        this.status = status;
        this.medico = medico;
        this.email = email;
        this.dataProcedimento = dataProcedimento;
        this.valorProcedimento = valorProcedimento;
        this.repasseId = repasseId;
        this.procedimento = procedimento;
        this.paciente = paciente;
        this.convenio = convenio;
    }

    public String getBancoPagamento() {
        return bancoPagamento;
    }

    public void setBancoPagamento(String bancoPagamento) {
        this.bancoPagamento = bancoPagamento;
    }

    public LocalDateTime getDataDeposito() {
        return dataDeposito;
    }

    public void setDataDeposito(LocalDateTime dataDeposito) {
        this.dataDeposito = dataDeposito;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDataProcedimento() {
        return dataProcedimento;
    }

    public void setDataProcedimento(LocalDateTime dataProcedimento) {
        this.dataProcedimento = dataProcedimento;
    }

    public double getValorProcedimento() {
        return valorProcedimento;
    }

    public void setValorProcedimento(double valorProcedimento) {
        this.valorProcedimento = valorProcedimento;
    }

    public Long getRepasseId() {
        return repasseId;
    }

    public void setRepasseId(Long repasseId) {
        this.repasseId = repasseId;
    }

    public String getProcedimento() {
        return procedimento;
    }

    public void setProcedimento(String procedimento) {
        this.procedimento = procedimento;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }
}
