package scene.windows;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class ChartWindow {
    private static Stage chartStage = new Stage();
    private static Map<Integer, Integer> roundBitMapping;

    public void start() throws IOException {
        NumberAxis xAxis = new NumberAxis(1, 16, 4);
        xAxis.setLabel("Rounds");

        List<Integer> rounds = new ArrayList<>(roundBitMapping.keySet());
        List<Integer> bits = new ArrayList<>(roundBitMapping.values());

        double yLowerBound = 0;
        double yUpperBound = bits.stream()
                .mapToDouble(v -> v)
                .max().orElseThrow(NoSuchElementException::new) + 5;

        NumberAxis yAxis = new NumberAxis(yLowerBound, yUpperBound, 5);
        yAxis.setLabel("Number of bits");

        LineChart linechart = new LineChart(xAxis, yAxis);

        XYChart.Series series = new XYChart.Series();
        series.setName("Number of bits in a round");

        for (int i = 0; i < 16; i++){
            series.getData().add(new XYChart.Data(rounds.get(i), bits.get(i)));
        }

        linechart.getData().add(series);

        BorderPane root = new BorderPane();
        root.setCenter(linechart);

        Scene scene = new Scene(root, 600, 400);

        chartStage.setTitle("Line Chart");
        chartStage.setScene(scene);
        chartStage.show();
    }

    public static Stage getChartStage() {
        return chartStage;
    }

    public static void setRoundBitMapping(Map<Integer, Integer> roundBitMapping) {
        ChartWindow.roundBitMapping = roundBitMapping;
    }
}
