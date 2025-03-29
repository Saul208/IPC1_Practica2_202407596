package USACProcessingData.Model;

import java.io.*;
import java.util.*;

public class CSVLoader {

    public static List<DataEntry> loadFromFile(File file) throws IOException {
        List<DataEntry> dataList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine(); // Leer encabezado

        if (line == null) {
            reader.close();
            throw new IOException("El archivo está vacío.");
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
}
