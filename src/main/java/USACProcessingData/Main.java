package USACProcessingData;

import javax.swing.SwingUtilities;
import USACProcessingData.View.MainFrame; // Importar la clase MainFrame

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
