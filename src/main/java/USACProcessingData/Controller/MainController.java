package USACProcessingData.Controller;

import USACProcessingData.Model.CSVLoader;
import USACProcessingData.Model.DataEntry;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {

    private List<DataEntry> dataList;
    private String xLabel = "Categoría";
    private String yLabel = "Cantidad";

    public boolean loadData(File file) {
        try {
            CSVLoader loader = new CSVLoader();
            this.dataList = loader.loadFromFile(file);
            this.xLabel = loader.getXLabel();
            this.yLabel = loader.getYLabel();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DataEntry> getDataList() {
        return dataList;
    }

    public String getXLabel() {
        return xLabel;
    }

    public String getYLabel() {
        return yLabel;
    }
}
