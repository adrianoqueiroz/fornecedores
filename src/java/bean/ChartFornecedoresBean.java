/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.FornecedorJpaController;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Adriano
 */
@ManagedBean
public class ChartFornecedoresBean implements Serializable {

    private PieChartModel pie;

    @EJB
    private FornecedorJpaController fornecedorJpaController;

    @PostConstruct
    public void init() {
        createPieModels();
    }

    public PieChartModel getPie() {
        return pie;
    }

    private void createPieModels() {
        createPieModel2();
    }

       private void createPieModel2() {

        pie = new PieChartModel();

        pie.set("1 Estrela", fornecedorJpaController.findFornecedorByQualificacao(1).size());
        pie.set("2 Estrelas", fornecedorJpaController.findFornecedorByQualificacao(2).size());
        pie.set("3 Estrelas", fornecedorJpaController.findFornecedorByQualificacao(3).size());
        pie.set("4 Estrelas", fornecedorJpaController.findFornecedorByQualificacao(4).size());
        pie.set("5 Estrelas", fornecedorJpaController.findFornecedorByQualificacao(5).size());

        pie.setTitle("Qualificação Geral dos Fornecedores");
        pie.setLegendPosition("w");
        pie.setFill(true);
        pie.setShowDataLabels(true);
        pie.setDiameter(150);
    }

}
