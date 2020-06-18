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
 * of people in a simulation where the selected number of people are spawned at the beginning
 * and how it varies as the number of floors in the building increases.
 * Both mechanical and advanced wait times are shown as different lines.
 * 
 * @author Annie Talbot
 * @version 1.3.1
 */
public class PeopleGraph
{
	/**
	 * The number of people spawned in the building at the beginning of each simulation this 
	 * data set represents.
	 */
	private int noPeople;

	/**
	 * Constructor for the line graph that reads the files that contain the corresponding data,
	 * sorts this data so it can be graphed, and creates a window that displays this graph.
	 * 
	 * @param noPeople		The number of people spawned in the building that this graph is about
	 */
	public PeopleGraph(int noPeople)
	{
		this.noPeople = noPeople;
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
        	JFreeChart graph = ChartFactory.createXYLineChart("Average Waiting Times for " + noPeople + " People", 
            		"No. Floors", "Avg. Wait Time (No. Lift Movements)", dataset, PlotOrientation.VERTICAL, 
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
		
		Map<Integer, Integer> waitTimes = readFiles(noPeople, "mechanical");
		if (waitTimes.isEmpty() || waitTimes.size() < 5) {
			return null;
		}
		//waitTimes = shrinkData(waitTimes, 100);
		waitTimes.forEach((k, v) -> mech.add(k, v));
		
		waitTimes.clear();
		waitTimes = readFiles(noPeople, "advanced");
		if (waitTimes.isEmpty() || waitTimes.size() < 5) {
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
	 * no. people and lift algorithm. Each value is a recorded wait time and is appended to the average 
	 * calculation for that number of floors.
	 * 
	 * @param noPeople		The number of people for this simulation
	 * @param system		The string used to describe the lift control algorithm used by the simulation. 
	 * 						Possible values are: "mechanical", "advanced", "optimum".
	 * @return		A map that links the number of people spawned in the simulation to the average wait time
	 * 				of that simulation.
	 */
	private Map<Integer, Integer> readFiles(int noPeople, String system)
	{
		Map<Integer, Integer> noEntries = new HashMap<>();
		Map<Integer, Integer> sortedAverages = new HashMap<>();
		BufferedReader reader;
		File f;
		int fileNumber;
		int noFloors = 1;
		while (true) {
			noFloors ++;
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
							if (sortedAverages.containsKey(noFloors))
							{
								noEntries.put(noFloors, noEntries.get(noFloors) + 1);
								sortedAverages.replace(noFloors, ((sortedAverages.get(noFloors)*(noEntries.get(noFloors) - 1)) + time) / noEntries.get(noFloors));
							}
							else
							{
								sortedAverages.put(noFloors, time);
								noEntries.put(noFloors, 1);
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
				if (noFloors >= 50) 
				{ 
					return sortedAverages;
				}	
			}
		}	
	}
}
