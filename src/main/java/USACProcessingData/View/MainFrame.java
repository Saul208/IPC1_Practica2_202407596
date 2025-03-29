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
    private JButton browseButton, loadButton;
    private JPanel chartPanelContainer;
    private MainController controller;

    public MainFrame() {
        super("USAC Processing Data");
        this.controller = new MainController();
        initUI();
    }

    private void initUI() {
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior para selección de archivo
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        filePathField = new JTextField();
        browseButton = new JButton("Buscar");
        loadButton = new JButton("Aceptar");

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(filePathField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(browseButton);
        buttonPanel.add(loadButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Panel de la gráfica
        chartPanelContainer = new JPanel(new BorderLayout());
        add(chartPanelContainer, BorderLayout.CENTER);

        // Acciones
        browseButton.addActionListener(e -> onBrowse());
        loadButton.addActionListener(e -> onLoad());
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
        File file = new File(filePathField.getText());
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Archivo no encontrado.");
            return;
        }

        if (controller.loadData(file)) {
            List<DataEntry> data = controller.getDataList();
            showChart(data);
        } else {
            JOptionPane.showMessageDialog(this, "Error al cargar archivo.");
        }
    }

    private void showChart(List<DataEntry> dataList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DataEntry entry : dataList) {
            dataset.addValue(entry.getCount(), "Categorías", entry.getCategory());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Gráfica de datos", // Título
                "Categoría",        // Eje X
                "Cantidad",         // Eje Y
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 450));

        chartPanelContainer.removeAll();
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        chartPanelContainer.validate();
    }
}
