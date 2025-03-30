package USACProcessingData.View;

import USACProcessingData.Controller.MainController;
import USACProcessingData.Model.DataEntry;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainFrame extends JFrame {

    private JTextField filePathField;
    private JTextField titleField;
    private JButton browseButton, loadButton, sortButton;
    private JPanel chartPanelContainer;
    private MainController controller;

    public MainFrame() {
        super("USAC Processing Data");
        this.controller = new MainController();
        initUI();
    }

    private void initUI() {
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de archivo
        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        filePathField = new JTextField();
        browseButton = new JButton("Buscar");
        filePanel.add(new JLabel("Ruta del archivo:"), BorderLayout.NORTH);
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);

        // Panel de título
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titleField = new JTextField();
        titlePanel.add(new JLabel("Título para la gráfica:"), BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);

        // Botón Aceptar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loadButton = new JButton("Aceptar");
        buttonPanel.add(loadButton);

        // Añadir paneles al panel de entrada
        inputPanel.add(filePanel);
        inputPanel.add(titlePanel);
        inputPanel.add(buttonPanel);

        add(inputPanel, BorderLayout.NORTH);

        // Contenedor de la gráfica
        chartPanelContainer = new JPanel(new BorderLayout());
        add(chartPanelContainer, BorderLayout.CENTER);

        // Botón para ordenar (inicialmente deshabilitado)
        sortButton = new JButton("Ordenar");
        sortButton.setEnabled(false);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(sortButton);
        add(southPanel, BorderLayout.SOUTH);

        // Eventos
        browseButton.addActionListener(e -> onBrowse());
        loadButton.addActionListener(e -> onLoad());
        sortButton.addActionListener(e -> onSort());
    }

    private void onBrowse() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void onLoad() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar un título para la gráfica.");
            return;
        }

        File file = new File(filePathField.getText());
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Archivo no encontrado.");
            return;
        }

        if (controller.loadData(file)) {
            List<DataEntry> data = controller.getDataList();
            showChart(data, title);
            sortButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error al cargar archivo.");
        }
    }

    private void showChart(List<DataEntry> dataList, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DataEntry entry : dataList) {
            dataset.addValue(entry.getCount(), controller.getYLabel(), entry.getCategory());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,                        // Título de la gráfica
                controller.getXLabel(),       // Etiqueta eje X (desde CSV)
                controller.getYLabel(),       // Etiqueta eje Y (desde CSV)
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 450));

        chartPanelContainer.removeAll();
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }

    private void onSort() {
        List<DataEntry> data = controller.getDataList();
        SortDialog sortDialog = new SortDialog(this, data, controller.getXLabel(), controller.getYLabel());
        sortDialog.setVisible(true);
    }
}
