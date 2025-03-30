package USACProcessingData.Model;

import java.util.Collections;
import java.util.List;

public class Sorter {

    private int steps = 0;

    public int getSteps() {
        return steps;
    }

    public void resetSteps() {
        steps = 0;
    }
    
    public void incrementStep() {
        steps++;
    }

    public void bubbleSort(List<DataEntry> data, boolean ascending) {
        resetSteps();
        int n = data.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                steps++;
                boolean condition = ascending
                        ? data.get(j).getCount() > data.get(j + 1).getCount()
                        : data.get(j).getCount() < data.get(j + 1).getCount();
                if (condition) {
                    Collections.swap(data, j, j + 1);
                }
            }
        }
    }
}
