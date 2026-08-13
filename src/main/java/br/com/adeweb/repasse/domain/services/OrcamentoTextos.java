package br.com.adeweb.repasse.domain.services;

import br.com.adeweb.repasse.domain.enums.TipoOrcamento;

final class OrcamentoTextos {

    static final String OBS_PAGAMENTO_PADRAO = "À VISTA (DINHEIRO OU PIX)";

    static final String CONDICOES_PAGAMENTO =
            "À VISTA (DINHEIRO OU PIX) OU DIVIDIMOS EM ATÉ 6X SEM JUROS NO CARTÃO DE CRÉDITO.";

    static final String COMPLICACOES_PADRAO =
            "EM SITUAÇÕES DE COMPLICAÇÕES OU INTERCORRÊNCIAS QUE EXIJAM INTERVENÇÕES ADICIONAIS E PERMANÊNCIAS ALÉM DO PREVISTO, O ORÇAMENTO ORIGINAL PERDE A VALIDADE E A COBRANÇA SERÁ FEITA DE FORMA ABERTA.";

    private OrcamentoTextos() {
    }

    static String incluso(TipoOrcamento tipo) {
        int diarias = tipo == TipoOrcamento.CESAREA ? 2 : 1;
        return incluso(tipo, diarias);
    }

    static String incluso(TipoOrcamento tipo, int diarias) {
        int qtd = (diarias == 1 || diarias == 2 || diarias == 4) ? diarias : (tipo == TipoOrcamento.CESAREA ? 2 : 1);
        if (tipo == TipoOrcamento.CESAREA) {
            return "INTERNAÇÃO ATÉ " + qtd + " DIÁRIAS, TAXA DE SALA DE CENTRO CIRÚRGICO, SALA DE "
                    + "RECUPERAÇÃO PÓS ANESTÉSICA, ACOMODAÇÃO APARTAMENTO,GASES MEDICINAIS EM "
                    + "CENTRO CIRÚRGICO, 01 ACOMPANHANTE, MATERIAIS DE CONSUMO E MEDICAMENTOS COMUNS "
                    + "PERTINENTES AO PROCEDIMENTO, TODAS AS VACINAS PERTINENTES AO NASCIMENTO, TESTE "
                    + "OTOACÚSTICO, TESTE DO CORAÇÃOZINHO, TESTE DA LINGUINHA, TESTE DO OUVIDINHO, TESTE "
                    + "REFLEXO VERMELHO, EXAME LABORATORIAIS DO RN (TIPAGEM SANGUÍNEA E SÍFILES) "
                    + "ALIMENTAÇÃO BALANCEADA PARA PACIENTE.";
        }
        return "INTERNAÇÃO ATÉ " + qtd + " DIÁRIAS, TAXA DE SALA DE CENTRO CIRÚRGICO, SALA DE "
                + "RECUPERAÇÃO PÓS ANESTÉSICA, ACOMODAÇÃO APARTAMENTO,GASES MEDICINAIS EM "
                + "CENTRO CIRÚRGICO, 01 ACOMPANHANTE, MATERIAIS DE CONSUMO E MEDICAMENTOS COMUNS "
                + "PERTINENTES AO PROCEDIMENTO, ALIMENTAÇÃO BALANCEADA PARA PACIENTE.";
    }

    static String naoIncluso(TipoOrcamento tipo) {
        if (tipo == TipoOrcamento.CESAREA) {
            return "UTI, UTI NEONATAL, TRANSFUSÃO DE SANGUE, DIÁLISE, TOMOGRAFIA, RADIOGRAFIA, "
                    + "DIÁRIAS EXTRAS, LAQUEADURA, PROCEDIMENTOS REFERENTES A INTERCORRÊNCIAS, "
                    + "RESSONÂNCIA, ULTRASSONOGRAFIA, ENDOSCOPIA, COLONOSCOPIA, IACOR, RISCO CIRÚRGICO, "
                    + "RETORNO PÓS ALTA, CONSULTAS, EXAMES, CURATIVOS E REMOÇÃO. NÃO ESTA INCLUSO NOS "
                    + "SERVIÇOS CONTRATADOS O MEDICAMENTO MATHERGAN (VACINA) E/OU SIMILARES.";
        }
        return "UTI, TRANSFUSÃO DE SANGUE, DIÁLISE, TOMOGRAFIA, RADIOGRAFIA, "
                + " DIÁRIAS EXTRAS, UTI, PROCEDIMENTOS REFERENTES A INTERCORRÊNCIAS "
                + "RESSONÂNCIA, ULTRASSONOGRAFIA, ENDOSCOPIA, COLONOSCOPIA, IACOR, RISCO CIRÚRGICO, "
                + "RETORNO PÓS ALTA,CONSULTAS, EXAMES, CURATIVOS E REMOÇÃO.";
    }

    static String observacoes() {
        return "OS PAGAMENTOS DE SERVIÇOS PROFISSIONAIS E TAXAS HOSPITALARES SERÃO EFETUADAS "
                + "INDIVIDUALMENTE.\n"
                + "HORÁRIOS DE VISITA: DAS 7H AS 20H";
    }
}
