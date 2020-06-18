import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
/**
 * On construction this object opens a new window (JFrame) that displays the average wait time
 * of people in a simulation with the selected number of floors and how it varies as more people
 * are added. Both mechanical and advanced wait times are shown as different lines.
 * 
 * @author Annie Talbot
 * @version 1.3.1
 */
public class FloorGraph
{
	/**
	 * The number of floors in the building this data set represents.
	 */
	private int noFloors;

	/**
	 * Constructor for the line graph that reads the files that contain the corresponding data,
	 * sorts this data so it can be graphed, and creates a window that displays this graph.
	 * 
	 * @param noFloors		The number of floors in the building this graph is about
	 */
	public FloorGraph(int noFloors)
	{
		this.noFloors = noFloors;
		// Create window
		JFrame frame = new JFrame("Wait Time Statistics");
        frame.setPreferredSize(new Dimension(1000, 600));
        frame.setMinimumSize(new Dimension(1000, 600));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        JPanel pane = new JPanel();
        // Fetch data to be used in the graph
        XYDataset dataset = createDataset();
        if (dataset == null) // If there is not enough data to make the graph
        {
        	pane.setLayout(new GridBagLayout());
        	Label err = new Label("Insufficient data to create the graph, please run more simulations.");
        	err.setFont(new Font("Helvetica", Font.PLAIN, 30));
        	pane.add(err);
        }
        else 
        {
        	// Plot the graph
        	JFreeChart graph = ChartFactory.createXYLineChart("Average Waiting Times for " + noFloors + " Floors", 
            		"No. People", "Avg. Wait Time (No. Lift Movements)", dataset, PlotOrientation.VERTICAL, 
            		true, true, false);
            
            ChartPanel chartPanel = new ChartPanel(graph);        
            chartPanel.setPreferredSize(new java.awt.Dimension(1000, 500) );  
            // Display the graph
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
	private XYDataset createDataset() {
		final XYSeries adv = new XYSeries("Advanced");        
		final XYSeries mech = new XYSeries("Mechanical");
		
		Map<Integer, Integer> waitTimes = readFiles(noFloors, "mechanical");
		if (waitTimes.isEmpty()) {
			return null;
		}
		//waitTimes = shrinkData(waitTimes, 100);
		waitTimes.forEach((k, v) -> mech.add(k, v));
		
		waitTimes.clear();
		waitTimes = readFiles(noFloors, "advanced");
		if (waitTimes.isEmpty()) {
			return null;
		}
		//waitTimes = shrinkData(waitTimes, 100);
		waitTimes.forEach((k, v) -> adv.add(k, v));
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(mech);
		dataset.addSeries(adv);
		return dataset;
    }
	
	/**
	 * A private function that reads through all the text files saved after simulations with the corresponding
	 * no. floors and lift algorithm. Each value is a recorded wait time and is appended to the average 
	 * calculation for that number of people.
	 * 
	 * @param noFloors		The number of floors for this simulation
	 * @param system		The string used to describe the lift control algorithm used by the simulation. 
	 * 						Possible values are: "mechanical", "advanced", "optimum".
	 * @return		A map that links the number of people spawned in the simulation to the average wait time
	 * 				of that simulation.
	 */
	private Map<Integer, Integer> readFiles(int noFloors, String system)
	{
		Map<Integer, Integer> noEntries = new HashMap<>();
		Map<Integer, Integer> sortedAverages = new HashMap<>();
		BufferedReader reader;
		File f;
		int fileNumber;
		int noPeople = 1;
		while (true) {
			noPeople ++;
			fileNumber = 0;
			f = new File("SimulationData/" + noFloors + "/" + noPeople);
			if (f.exists())
			{
				while (true)
				{
					try 
					{
						fileNumber ++;
						f = new File("SimulationData/" + noFloors + "/" + noPeople + "/" + system + "/simulation" + fileNumber + ".txt");
						reader = new BufferedReader(new FileReader(f));
						String line = reader.readLine();
						while (line != null || line == "")
						{
							int time = Integer.parseInt(line);
							if (sortedAverages.containsKey(noPeople))
							{
								noEntries.put(noPeople, noEntries.get(noPeople) + 1);
								sortedAverages.replace(noPeople, ((sortedAverages.get(noPeople)*(noEntries.get(noPeople) - 1)) + time) / noEntries.get(noPeople));
							}
							else
							{
								sortedAverages.put(noPeople, time);
								noEntries.put(noPeople, 1);
							}
							// read next line
							line = reader.readLine();
						}
						reader.close();
						fileNumber++;
					}
					catch (IOException e) 
					{				
						break;
					}
				}
			}
			else
			{
				if (noPeople >= 50) 
				{ 
					return sortedAverages;
				}	
			}
		}
	}
}
