package USACProcessingData.Model;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFReportGenerator {

    // Contador estático para que cada reporte tenga un nombre único
    private static int reportCounter = 1;

    /**
     * Genera un reporte PDF con la información del proceso de ordenamiento.
     *
     * @param outputPdfPath  Ruta base donde se creará el PDF.
     * @param studentName    Nombre del estudiante.
     * @param studentCarnet  Carné del estudiante.
     * @param algorithm      Algoritmo de ordenamiento utilizado.
     * @param speed          Velocidad seleccionada.
     * @param orderType      "Ascendente" o "Descendente".
     * @param totalTime      Tiempo total de ejecución (formato "00:00:000").
     * @param steps          Cantidad de pasos realizados.
     * @param originalData   Lista de datos leídos (sin ordenar).
     * @param sortedData     Lista de datos ordenados.
     * @param chartBefore    Gráfica inicial (sin ordenar).
     * @param chartAfter     Gráfica final (ordenada).
     * @throws DocumentException En caso de error en la creación del documento.
     * @throws IOException       En caso de error al escribir el archivo.
     */
    public static void generateReport(
            String outputPdfPath,
            String studentName,
            String studentCarnet,
            String algorithm,
            String speed,
            String orderType,
            String totalTime,
            int steps,
            List<DataEntry> originalData,
            List<DataEntry> sortedData,
            JFreeChart chartBefore,
            JFreeChart chartAfter
    ) throws DocumentException, IOException {

        // Modificar el nombre del archivo para no sobrescribir reportes previos
        String newOutputPdfPath;
        if (outputPdfPath.toLowerCase().endsWith(".pdf")) {
            newOutputPdfPath = outputPdfPath.substring(0, outputPdfPath.length() - 4)
                    + "_" + reportCounter + ".pdf";
        } else {
            newOutputPdfPath = outputPdfPath + "_" + reportCounter + ".pdf";
        }
        reportCounter++;

        Document document = new Document(PageSize.LETTER, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(newOutputPdfPath));
        document.open();

        // 1. Encabezado: Nombre y Carné
        Paragraph header = new Paragraph(
                "Reporte de Ordenamiento\n" +
                "Estudiante: " + studentName + "\n" +
                "Carné: " + studentCarnet + "\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)
        );
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        // 2. Información del proceso de ordenamiento
        Paragraph info = new Paragraph(
                "Algoritmo: " + algorithm + "\n" +
                "Velocidad: " + speed + "\n" +
                "Orden: " + orderType + "\n" +
                "Tiempo: " + totalTime + "\n" +
                "Pasos: " + steps + "\n\n",
                FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)
        );
        info.setAlignment(Element.ALIGN_LEFT);
        document.add(info);

        // 3. Dato mínimo y dato máximo (según la lista ordenada)
        if (!sortedData.isEmpty()) {
            DataEntry minEntry = sortedData.get(0);
            DataEntry maxEntry = sortedData.get(sortedData.size() - 1);
            Paragraph minMax = new Paragraph(
                    "Dato mínimo: " + minEntry.getCategory() + " (" + minEntry.getCount() + ")\n" +
                    "Dato máximo: " + maxEntry.getCategory() + " (" + maxEntry.getCount() + ")\n\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)
            );
            document.add(minMax);
        }

        // 4. Tabla de datos originales (sin ordenar)
        Paragraph originalTitle = new Paragraph("Datos Desordenados\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK));
        document.add(originalTitle);

        PdfPTable originalTable = new PdfPTable(2);
        originalTable.setWidthPercentage(80);
        PdfPCell catHeader = new PdfPCell(new Phrase("Categoría"));
        catHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
        originalTable.addCell(catHeader);
        PdfPCell valHeader = new PdfPCell(new Phrase("Valor"));
        valHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
        originalTable.addCell(valHeader);
        for (DataEntry entry : originalData) {
            originalTable.addCell(entry.getCategory());
            originalTable.addCell(String.valueOf(entry.getCount()));
        }
        document.add(originalTable);
        document.add(Chunk.NEWLINE);

        // 5. Gráfica inicial (sin ordenar)
        Paragraph beforeChartTitle = new Paragraph("Gráfica Inicial (No ordenada)\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK));
        document.add(beforeChartTitle);
        File chartBeforeFile = new File("chart_before.png");
        ChartUtils.saveChartAsPNG(chartBeforeFile, chartBefore, 600, 400);
        Image imgBefore = Image.getInstance(chartBeforeFile.getAbsolutePath());
        imgBefore.setAlignment(Image.ALIGN_CENTER);
        imgBefore.scaleToFit(500, 300);
        document.add(imgBefore);
        document.add(Chunk.NEWLINE);

        // 6. Tabla de datos ordenados
        Paragraph sortedTitle = new Paragraph("Datos Ordenados\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK));
        document.add(sortedTitle);

        PdfPTable sortedTable = new PdfPTable(2);
        sortedTable.setWidthPercentage(80);
        PdfPCell catHeader2 = new PdfPCell(new Phrase("Categoría"));
        catHeader2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        sortedTable.addCell(catHeader2);
        PdfPCell valHeader2 = new PdfPCell(new Phrase("Valor"));
        valHeader2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        sortedTable.addCell(valHeader2);
        for (DataEntry entry : sortedData) {
            sortedTable.addCell(entry.getCategory());
            sortedTable.addCell(String.valueOf(entry.getCount()));
        }
        document.add(sortedTable);
        document.add(Chunk.NEWLINE);

        // 7. Gráfica final (ordenada)
        Paragraph afterChartTitle = new Paragraph("Gráfica Final (Ordenada)\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK));
        document.add(afterChartTitle);
        File chartAfterFile = new File("chart_after.png");
        ChartUtils.saveChartAsPNG(chartAfterFile, chartAfter, 600, 400);
        Image imgAfter = Image.getInstance(chartAfterFile.getAbsolutePath());
        imgAfter.setAlignment(Image.ALIGN_CENTER);
        imgAfter.scaleToFit(500, 300);
        document.add(imgAfter);

        // Cerrar documento y writer
        document.close();
        writer.close();

        // Borrar archivos temporales
        chartBeforeFile.delete();
        chartAfterFile.delete();
    }
}
