/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author marcel.campos
 */
public class Empresa {
    
    private String nome;
    private String cnpj;
    private String inscEstadual;
    private String inscMunicipal;
    private Categoria categoria01;
    private Categoria categoria02;
    private Categoria categoria03;
    private String qualificacao;
    private String observacao;
    private String estado;
    private String cidade;
    private String bairro;
    private String logradouro;
    private int numero;
    private String cep;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscEstadual() {
        return inscEstadual;
    }

    public void setInscEstadual(String inscEstadual) {
        this.inscEstadual = inscEstadual;
    }

    public String getInscMunicipal() {
        return inscMunicipal;
    }

    public void setInscMunicipal(String inscMunicipal) {
        this.inscMunicipal = inscMunicipal;
    }

    public Categoria getCategoria01() {
        return categoria01;
    }

    public void setCategoria01(Categoria categoria01) {
        this.categoria01 = categoria01;
    }

    public Categoria getCategoria02() {
        return categoria02;
    }

    public void setCategoria02(Categoria categoria02) {
        this.categoria02 = categoria02;
    }

    public Categoria getCategoria03() {
        return categoria03;
    }

    public void setCategoria03(Categoria categoria03) {
        this.categoria03 = categoria03;
    }

    public String getQualificacao() {
        return qualificacao;
    }

    public void setQualificacao(String qualificacao) {
        this.qualificacao = qualificacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
    
    
}
