import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
/**
 * On construction this object opens a new window (JFrame) that displays the wait times of people
 * in a simulation with the selected number of floors and people. Both mechanical and advanced
 * wait times are shown in a box and whisker plot so they can be compared.
 * 
 * @author Annie Talbot
 * @version 1.3.1
 */
public class BoxAndWhiskerPlot
{
	/**
	 * The number of floors in the building this data set represents.
	 */
	private int noFloors;
	/**
	 * The number of people spawned at the beginning of each simulation this data set represents
	 */
	private int noPeople;

	/**
	 * Constructor for the box and whisker plot that reads the files that contain the corresponding data,
	 * sorts this data so it can be graphed and creates a window that displays this graph.
	 * 
	 * @param noFloors		The number of floors in the building this graph is about
	 * @param noPeople		The number of people spawned at the beginning of the simulations this graph is about
	 */
	public BoxAndWhiskerPlot(int noFloors, int noPeople)
	{
		this.noFloors = noFloors;
		this.noPeople = noPeople;
		// Create window
		JFrame frame = new JFrame("Wait Time Statistics");
        frame.setPreferredSize(new Dimension(400, 700));
        frame.setMinimumSize(new Dimension(400, 700));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        JPanel pane = new JPanel();
        // Fetch data to be used in the graph
        BoxAndWhiskerCategoryDataset dataset = createDataset();
        // If not enough data is found
        if (dataset == null) {
        	pane.setLayout(new GridBagLayout());
        	Label err = new Label("Insufficient data to create the graph, please run more simulations.");
        	err.setFont(new Font("Helvetica", Font.PLAIN, 30));
        	pane.add(err);
        }
        else {
        	// Plot the graph
        	final CategoryAxis xAxis = new CategoryAxis("Algorithm Used");
            final NumberAxis yAxis = new NumberAxis("Wait Time (No. Lift Movements)");
            yAxis.setAutoRangeIncludesZero(false);
            final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
            renderer.setFillBox(false);
            final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        	JFreeChart graph = new JFreeChart("Average Waiting Times for " + noFloors + " Floors and " + noPeople + " People", 
        			new Font("SansSerif", Font.BOLD, 14), plot, true);
            ChartPanel chartPanel = new ChartPanel(graph);
            chartPanel.setPreferredSize(new java.awt.Dimension(300, 600) );
            // Display graph in window
            pane.add(chartPanel);
        }
        frame.setContentPane(pane);
        frame.setVisible(true);
	}	
	
	/**
	 * A private function that reads from file, creates and returns the data set that can be used 
	 * to plot the graph. 
	 * @return		A data set that can be used to plot a box and whisker graph
	 */
	private BoxAndWhiskerCategoryDataset createDataset() {
		DefaultBoxAndWhiskerCategoryDataset boxData = new DefaultBoxAndWhiskerCategoryDataset();
		LinkedList<Integer> waitTimes = new LinkedList<Integer>();
		waitTimes = readFiles("mechanical");
		if (waitTimes == null) {
			return null;
		}
		boxData.add(waitTimes, "Mechanical", "Simulation");

		waitTimes.clear();
		waitTimes = readFiles("advanced");
		if (waitTimes.size() == 0) {
			return null;
		}
		boxData.add(waitTimes, "Advanced", "Simulation");
		
		return boxData;
    }
	
	/**
	 * A private function that reads through all the text files saved after simulations with the corresponding
	 * no. floors, no. people and lift algorithm. Each value is appended to a list, and this list is returned. 
	 * 
	 * @param system		The string used to describe the lift control algorithm used by the simulation. 
	 * 						Possible values are: "mechanical", "advanced", "optimum".
	 * @return		A list with every wait time collected from any simulation run with this no. floors,
	 * 				no. people, and lift algorithm.
	 */
	private LinkedList<Integer> readFiles(String system)
	{
		LinkedList<Integer> sortedWaitTimes = new LinkedList<Integer>();
		BufferedReader reader;
		int fileNumber = 0;
		try 
		{
			while (true)
			{
				fileNumber ++;
				reader = new BufferedReader(new FileReader("SimulationData/" + noFloors + "/" + noPeople + "/" + system + "/simulation" + fileNumber + ".txt"));
				String line = reader.readLine();
				while (line != null || line == "") 
				{
					sortedWaitTimes.add(Integer.parseInt(line));
					
					// read next line
					line = reader.readLine();
				}
				reader.close();
			}
		}
		catch (IOException e) 
		{
			if (fileNumber <= 1) 
			{ 
				return null;
			}
			return sortedWaitTimes;
		}
	}

}
