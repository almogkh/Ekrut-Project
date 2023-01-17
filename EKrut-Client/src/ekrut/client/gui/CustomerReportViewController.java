package ekrut.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import ekrut.client.EKrutClient;
import ekrut.client.EKrutClientUI;
import ekrut.client.managers.ClientReportManager;
import ekrut.entity.Report;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.chart.LineChart;

/**
 * 
 * The class responsible for displaying customer reports.
 * It displays a bar chart and line chart showing customer activity and monthly
 * activity respectively.
 * The report data is passed to the class through the setCustomerReport method,
 * which also updates the charts and head labels.
 * 
 * @author Tal Gaon

 */
public class CustomerReportViewController {

	@FXML
	private BarChart<?, ?> CustomersActivityBarChart;

	@FXML
	private Label areaLbl;

	@FXML
	private CategoryAxis barChartCategoryAxis;

	@FXML
	private NumberAxis barChartNumberAxis;

	@FXML
	private Label dateLbl;

	@FXML
	private CategoryAxis lineChartCategoryAxis;

	@FXML
	private NumberAxis lineChartNumberAxis;

	@FXML
	private Label locationLbl;

	@FXML
	private LineChart<?, ?> monthlyActivityLineChart;

	private EKrutClient client = EKrutClientUI.getEkrutClient();
	ClientReportManager clientReportManager = client.getClientReportManager();
	Report report;

	// Set the customer report
	public void setCustomerReport(Report report) {
		this.report = report;
		setHeadLabels();
		// Set charts
		setCustomersActivityBarChart();
		setMonthlyActivityLineChart();
	}

	/*
	 * This class sets up a line chart to display monthly activity. It first creates
	 * an ArrayList of strings representing the days of the month (1-31) and
	 * converts it to an array. The x-axis of the line chart is set to the array of
	 * days.
	 */
	private void setMonthlyActivityLineChart() {
		ArrayList<String> days = new ArrayList<>();

		for (int i = 1; i <= 31; i++) {
			days.add(String.valueOf(i));
		}

		String[] daysArr = days.toArray(new String[days.size()]);
		lineChartCategoryAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(daysArr)));

		XYChart.Series series = new XYChart.Series();
		series.setName("Item");
		monthlyActivityLineChart.setLegendVisible(false);

		Map<Integer, Integer> customersOrdersByDate = report.getCustomersOrdersByDate();

		for (Map.Entry<Integer, Integer> entry : customersOrdersByDate.entrySet()) {
			series.getData().add(new XYChart.Data(String.valueOf(entry.getKey()), entry.getValue()));
		}

		monthlyActivityLineChart.getData().add(series);

		monthlyActivityLineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
		series.getNode().setStyle("-fx-stroke: #FFD6DC");
	}

	/*
	 * This class sets up a bar chart to display customer activity. It first
	 * retrieves a map of customer activity data from a report object. The x-axis of
	 * the bar chart is set to an array of categories ("1", "2", "3", "4",
	 * "5","6+").
	 */
	private void setCustomersActivityBarChart() {
		Map<String, Integer> CustomersActivityData = report.getCustomerReportData();

		String[] categories = { "1", "2", "3", "4", "5", "6+" };

		barChartCategoryAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories)));
		CustomersActivityBarChart.setLegendVisible(false);

		XYChart.Series series = new XYChart.Series();

		for (Map.Entry<String, Integer> entry : CustomersActivityData.entrySet()) {
			// Prepare XYChart.Series objects by setting data
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}
		CustomersActivityBarChart.getData().addAll(series);
		CustomersActivityBarChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
	}

	private void setHeadLabels() {
		locationLbl.setText(report.getEkrutLocation());
		areaLbl.setText(report.getArea());
		String date = (String.valueOf(report.getDate().getMonthValue()) + '/'
				+ String.valueOf(report.getDate().getYear()));
		dateLbl.setText(date);
	}
}
