package USACProcessingData.Controller;

import USACProcessingData.Model.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {

    private List<DataEntry> dataList;

    public boolean loadData(File file) {
        try {
            this.dataList = CSVLoader.loadFromFile(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DataEntry> getDataList() {
        return dataList;
    }
}
