package USACProcessingData.Model;

import java.io.*;
import java.util.*;

public class CSVLoader {
    private String xLabel = "Categoría";
    private String yLabel = "Cantidad";

    public List<DataEntry> loadFromFile(File file) throws IOException {
        List<DataEntry> dataList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        // Leer encabezados
        line = reader.readLine();
        if (line == null) throw new IOException("Archivo vacío");

        String[] headers = line.split(",");
        if (headers.length == 2) {
            xLabel = headers[0].trim();
            yLabel = headers[1].trim();
        }

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String category = parts[0].trim();
                int count = Integer.parseInt(parts[1].trim());
                dataList.add(new DataEntry(category, count));
            }
        }

        reader.close();
        return dataList;
    }

    public String getXLabel() {
        return xLabel;
    }

    public String getYLabel() {
        return yLabel;
    }
}
