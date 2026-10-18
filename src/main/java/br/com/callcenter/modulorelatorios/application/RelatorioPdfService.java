package br.com.callcenter.modulorelatorios.application;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Gera PDFs reais dos relatórios gerenciais via JasperReports construído
 * dinamicamente em memória (mesma técnica do fallback do contracheque),
 * sem depender de arquivos .jrxml/.jasper no classpath.
 */
@Service
public class RelatorioPdfService {

    private static final int PAGE_WIDTH = 595;   // A4 em pontos
    private static final int PAGE_HEIGHT = 842;
    private static final int COLUMN_WIDTH = 555;

    /**
     * @param titulo   título do relatório (banda de título)
     * @param subtitulo linha auxiliar (período, data de emissão etc.)
     * @param linhas   linhas do corpo — cada linha é um par [rótulo, valor]
     */
    public byte[] gerarRelatorio(String titulo, String subtitulo, List<String[]> linhas) throws Exception {
        JasperDesign design = new JasperDesign();
        design.setName("RelatorioGerencial");
        design.setPageWidth(PAGE_WIDTH);
        design.setPageHeight(PAGE_HEIGHT);
        design.setColumnWidth(COLUMN_WIDTH);
        design.setLeftMargin(20);
        design.setRightMargin(20);
        design.setTopMargin(20);
        design.setBottomMargin(20);

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(46);
        titleBand.addElement(staticText(0, 4, COLUMN_WIDTH, 20, titulo, true));
        if (subtitulo != null && !subtitulo.isBlank()) {
            titleBand.addElement(staticText(0, 26, COLUMN_WIDTH, 14, subtitulo, false));
        }
        design.setTitle(titleBand);

        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(16);
        int y = 0;
        for (String[] linha : linhas) {
            boolean secao = linha.length > 2 && "SECAO".equals(linha[2]);
            if (secao) {
                detailBand.addElement(staticText(0, y, COLUMN_WIDTH, 14, linha[0], true));
                y += 16;
            } else {
                detailBand.addElement(staticText(10, y, 330, 14, linha[0], false));
                detailBand.addElement(staticText(350, y, 205, 14, linha[1] == null ? "" : linha[1], false));
                y += 14;
            }
            if (y > 700) {
                break; // proteção contra overflow de uma única página de detalhe
            }
        }
        detailBand.setSplitType(SplitTypeEnum.STRETCH);

        // JasperReports não expõe setDetailSection público no JasperDesign;
        // injeção por reflection (padrão já usado no ContrachequeService).
        JRDesignSection detailSection = new JRDesignSection(null);
        detailSection.addBand(detailBand);
        java.lang.reflect.Field detailField =
                net.sf.jasperreports.engine.base.JRBaseReport.class.getDeclaredField("detailSection");
        detailField.setAccessible(true);
        detailField.set(design, detailSection);

        var jasperReport = JasperCompileManager.compileReport(design);
        var jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JREmptyDataSource(1));
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private JRDesignStaticText staticText(int x, int y, int width, int height, String text, boolean bold) {
        JRDesignStaticText st = new JRDesignStaticText();
        st.setX(x);
        st.setY(y);
        st.setWidth(width);
        st.setHeight(height);
        st.setText(text);
        st.setFontSize(bold ? 12f : 9f);
        if (bold) {
            st.setBold(Boolean.TRUE);
        }
        return st;
    }
}
