package USACProcessingData.View;

import USACProcessingData.Model.DataEntry;
import USACProcessingData.Model.PDFReportGenerator;
import USACProcessingData.Model.Sorter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SortDialog extends JDialog {

    private List<DataEntry> dataList;      // Datos originales (sin ordenar)
    private List<DataEntry> sortingData;   // Copia para ordenar
    private String xLabel;
    private String yLabel;

    private JComboBox<String> algorithmComboBox;
    private JRadioButton ascendingRadio;
    private JRadioButton descendingRadio;
    private JComboBox<String> speedComboBox;
    private JButton startButton;
    private JLabel algorithmLabel;
    private JLabel speedLabel;
    private JLabel orderTypeLabel;
    private JLabel timeLabel;   // Se actualizará en cada paso
    private JLabel stepsLabel;  // Cantidad de pasos
    private ChartPanel chartPanel;

    private Sorter sorter;

    public SortDialog(Frame owner, List<DataEntry> dataList, String xLabel, String yLabel) {
        super(owner, "Opciones de Ordenamiento", true);
        this.dataList = dataList;
        // Crear copia de la lista para no modificar la original
        this.sortingData = new ArrayList<>();
        for (DataEntry de : dataList) {
            this.sortingData.add(new DataEntry(de.getCategory(), de.getCount()));
        }
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.sorter = new Sorter();
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Panel con las opciones
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel topOptions = new JPanel(new FlowLayout());

        // Selección de algoritmo
        algorithmComboBox = new JComboBox<>(new String[]{
                "Bubble Sort", "Insert Sort", "Select Sort", "Merge Sort", "Quicksort", "Shellsort"
        });
        topOptions.add(new JLabel("Algoritmo:"));
        topOptions.add(algorithmComboBox);

        // Orden asc/desc
        ascendingRadio = new JRadioButton("Ascendente", true);
        descendingRadio = new JRadioButton("Descendente");
        ButtonGroup orderGroup = new ButtonGroup();
        orderGroup.add(ascendingRadio);
        orderGroup.add(descendingRadio);
        topOptions.add(new JLabel("Tipo:"));
        topOptions.add(ascendingRadio);
        topOptions.add(descendingRadio);

        // Selección de velocidad
        speedComboBox = new JComboBox<>(new String[]{"Alta", "Media", "Baja"});
        topOptions.add(new JLabel("Velocidad:"));
        topOptions.add(speedComboBox);

        // Botón iniciar ordenamiento
        startButton = new JButton("Iniciar Ordenamiento");
        topOptions.add(startButton);

        optionsPanel.add(topOptions);

        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new FlowLayout());
        algorithmLabel = new JLabel("Algoritmo: ");
        speedLabel = new JLabel("Velocidad: ");
        orderTypeLabel = new JLabel("Orden: ");
        timeLabel = new JLabel("Tiempo: 00:00:000");
        stepsLabel = new JLabel("Pasos: 0");
        statsPanel.add(algorithmLabel);
        statsPanel.add(speedLabel);
        statsPanel.add(orderTypeLabel);
        statsPanel.add(timeLabel);
        statsPanel.add(stepsLabel);
        optionsPanel.add(statsPanel);

        add(optionsPanel, BorderLayout.NORTH);

        // Panel para la gráfica
        chartPanel = new ChartPanel(createChart());
        chartPanel.setPreferredSize(new Dimension(800, 450));
        add(chartPanel, BorderLayout.CENTER);

        // Evento del botón iniciar
        startButton.addActionListener(e -> startSorting());
    }

    private JFreeChart createChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DataEntry entry : sortingData) {
            dataset.addValue(entry.getCount(), yLabel, entry.getCategory());
        }
        return ChartFactory.createBarChart(
                "Proceso de Ordenamiento",
                xLabel,
                yLabel,
                dataset
        );
    }

    // Método auxiliar para crear un gráfico a partir de una lista de datos
    private JFreeChart createChartFromData(List<DataEntry> data, String xLabel, String yLabel) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DataEntry entry : data) {
            dataset.addValue(entry.getCount(), yLabel, entry.getCategory());
        }
        return ChartFactory.createBarChart("Gráfica", xLabel, yLabel, dataset);
    }

    private void updateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DataEntry entry : sortingData) {
            dataset.addValue(entry.getCount(), yLabel, entry.getCategory());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Proceso de Ordenamiento",
                xLabel,
                yLabel,
                dataset
        );
        SwingUtilities.invokeLater(() -> chartPanel.setChart(chart));
    }

    private int getDelay() {
        String speed = (String) speedComboBox.getSelectedItem();
        if (speed.equals("Alta")) {
            return 200;
        } else if (speed.equals("Media")) {
            return 500;
        } else { // "Baja"
            return 1000;
        }
    }

    private void startSorting() {
        startButton.setEnabled(false);
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        algorithmLabel.setText("Algoritmo: " + selectedAlgorithm);
        String selectedSpeed = (String) speedComboBox.getSelectedItem();
        speedLabel.setText("Velocidad: " + selectedSpeed);
        String orderType = ascendingRadio.isSelected() ? "Ascendente" : "Descendente";
        orderTypeLabel.setText("Orden: " + orderType);

        boolean ascending = ascendingRadio.isSelected();
        Thread sortingThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            sorter.resetSteps();
            try {
                switch (selectedAlgorithm) {
                    case "Bubble Sort":
                        sortBubble(ascending, startTime);
                        break;
                    case "Insert Sort":
                        sortInsertion(ascending, startTime);
                        break;
                    case "Select Sort":
                        sortSelection(ascending, startTime);
                        break;
                    case "Merge Sort":
                        sortMerge(ascending, startTime);
                        break;
                    case "Quicksort":
                        sortQuick(ascending, startTime);
                        break;
                    case "Shellsort":
                        sortShell(ascending, startTime);
                        break;
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            String finalTime = formatTime(endTime - startTime);
            SwingUtilities.invokeLater(() -> {
                timeLabel.setText("Tiempo: " + finalTime);
                startButton.setEnabled(true);
            });

            // Al finalizar el ordenamiento, generar el reporte PDF
            SwingUtilities.invokeLater(() -> {
                try {
                    JFreeChart chartBefore = createChartFromData(dataList, xLabel, yLabel);
                    JFreeChart chartAfter = createChartFromData(sortingData, xLabel, yLabel);
                    PDFReportGenerator.generateReport(
                            "ReporteOrdenamiento.pdf", // Ruta de salida
                            "Tu Nombre",               // Reemplaza con tu nombre
                            "202407596",               // Reemplaza con tu carné
                            selectedAlgorithm,
                            selectedSpeed,
                            orderType,
                            finalTime,
                            sorter.getSteps(),
                            dataList,      // Datos originales (sin ordenar)
                            sortingData,   // Datos ordenados
                            chartBefore,
                            chartAfter
                    );
                    JOptionPane.showMessageDialog(this, "¡Reporte generado con éxito en: ReporteOrdenamiento.pdf!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al generar reporte: " + ex.getMessage());
                }
            });
        });
        sortingThread.start();
    }

    private String formatTime(long millis) {
        long minutes = (millis / 60000);
        long seconds = (millis % 60000) / 1000;
        long ms = millis % 1000;
        return String.format("%02d:%02d:%03d", minutes, seconds, ms);
    }

    // ===============================
    // Métodos de Ordenamiento Paso a Paso
    // ===============================

    private void updateStatsAndChart(long startTime) throws InterruptedException {
        updateChart();
        long currentTime = System.currentTimeMillis();
        String currentTimeStr = formatTime(currentTime - startTime);
        SwingUtilities.invokeLater(() -> {
            stepsLabel.setText("Pasos: " + sorter.getSteps());
            timeLabel.setText("Tiempo: " + currentTimeStr);
        });
        Thread.sleep(getDelay());
    }

    // 1. Bubble Sort
    private void sortBubble(boolean ascending, long startTime) throws InterruptedException {
        int n = sortingData.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                sorter.incrementStep();
                boolean condition = ascending
                        ? sortingData.get(j).getCount() > sortingData.get(j + 1).getCount()
                        : sortingData.get(j).getCount() < sortingData.get(j + 1).getCount();
                if (condition) {
                    DataEntry temp = sortingData.get(j);
                    sortingData.set(j, sortingData.get(j + 1));
                    sortingData.set(j + 1, temp);
                }
                updateStatsAndChart(startTime);
            }
        }
    }

    // 2. Insertion Sort
    private void sortInsertion(boolean ascending, long startTime) throws InterruptedException {
        int n = sortingData.size();
        for (int i = 1; i < n; i++) {
            DataEntry key = sortingData.get(i);
            int j = i - 1;
            while (j >= 0 && (ascending ? sortingData.get(j).getCount() > key.getCount()
                    : sortingData.get(j).getCount() < key.getCount())) {
                sorter.incrementStep();
                sortingData.set(j + 1, sortingData.get(j));
                j--;
                updateStatsAndChart(startTime);
            }
            sortingData.set(j + 1, key);
            updateStatsAndChart(startTime);
        }
    }

    // 3. Selection Sort
    private void sortSelection(boolean ascending, long startTime) throws InterruptedException {
        int n = sortingData.size();
        for (int i = 0; i < n - 1; i++) {
            int selectedIndex = i;
            for (int j = i + 1; j < n; j++) {
                sorter.incrementStep();
                if (ascending ? sortingData.get(j).getCount() < sortingData.get(selectedIndex).getCount()
                              : sortingData.get(j).getCount() > sortingData.get(selectedIndex).getCount()) {
                    selectedIndex = j;
                }
                updateStatsAndChart(startTime);
            }
            if (selectedIndex != i) {
                DataEntry temp = sortingData.get(i);
                sortingData.set(i, sortingData.get(selectedIndex));
                sortingData.set(selectedIndex, temp);
                updateStatsAndChart(startTime);
            }
        }
    }

    // 4. Merge Sort (iterativo, bottom-up)
    private void sortMerge(boolean ascending, long startTime) throws InterruptedException {
        int n = sortingData.size();
        for (int width = 1; width < n; width *= 2) {
            for (int i = 0; i < n; i += 2 * width) {
                int left = i;
                int mid = Math.min(i + width, n);
                int right = Math.min(i + 2 * width, n);
                merge(ascending, left, mid, right, startTime);
            }
        }
    }

    private void merge(boolean ascending, int left, int mid, int right, long startTime) throws InterruptedException {
        List<DataEntry> temp = new ArrayList<>();
        int i = left, j = mid;
        while (i < mid && j < right) {
            sorter.incrementStep();
            if (ascending
                    ? sortingData.get(i).getCount() <= sortingData.get(j).getCount()
                    : sortingData.get(i).getCount() >= sortingData.get(j).getCount()) {
                temp.add(sortingData.get(i));
                i++;
            } else {
                temp.add(sortingData.get(j));
                j++;
            }
            updateStatsAndChart(startTime);
        }
        while (i < mid) {
            sorter.incrementStep();
            temp.add(sortingData.get(i));
            i++;
            updateStatsAndChart(startTime);
        }
        while (j < right) {
            sorter.incrementStep();
            temp.add(sortingData.get(j));
            j++;
            updateStatsAndChart(startTime);
        }
        for (int k = 0; k < temp.size(); k++) {
            sortingData.set(left + k, temp.get(k));
            updateStatsAndChart(startTime);
        }
    }

    // 5. Quicksort
    private void sortQuick(boolean ascending, long startTime) throws InterruptedException {
        quickSortRecursive(ascending, 0, sortingData.size() - 1, startTime);
    }

    private void quickSortRecursive(boolean ascending, int low, int high, long startTime) throws InterruptedException {
        if (low < high) {
            int pi = partition(ascending, low, high, startTime);
            quickSortRecursive(ascending, low, pi - 1, startTime);
            quickSortRecursive(ascending, pi + 1, high, startTime);
        }
    }

    private int partition(boolean ascending, int low, int high, long startTime) throws InterruptedException {
        DataEntry pivot = sortingData.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            sorter.incrementStep();
            if (ascending
                    ? sortingData.get(j).getCount() <= pivot.getCount()
                    : sortingData.get(j).getCount() >= pivot.getCount()) {
                i++;
                DataEntry temp = sortingData.get(i);
                sortingData.set(i, sortingData.get(j));
                sortingData.set(j, temp);
                updateStatsAndChart(startTime);
            }
        }
        DataEntry temp = sortingData.get(i + 1);
        sortingData.set(i + 1, sortingData.get(high));
        sortingData.set(high, temp);
        updateStatsAndChart(startTime);
        return i + 1;
    }

    // 6. Shellsort
    private void sortShell(boolean ascending, long startTime) throws InterruptedException {
        int n = sortingData.size();
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                DataEntry temp = sortingData.get(i);
                int j = i;
                while (j >= gap && (ascending
                        ? sortingData.get(j - gap).getCount() > temp.getCount()
                        : sortingData.get(j - gap).getCount() < temp.getCount())) {
                    sorter.incrementStep();
                    sortingData.set(j, sortingData.get(j - gap));
                    j -= gap;
                    updateStatsAndChart(startTime);
                }
                sortingData.set(j, temp);
                updateStatsAndChart(startTime);
            }
        }
    }
}
