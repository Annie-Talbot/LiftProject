import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SpinnerModel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

/**
 * Program Interface - A graphics interface for selecting the attributes for the next simulation,
 * running any simulation and for displaying any graph.
 * 
 * @author Annie Talbot
 * @version 1.3.1
 */
public class Program implements ActionListener
{
    /**
    * The amount of floors the next simulation should have
    */
    JSpinner floorsSpn;
    /**
     * The amount of people the next simulation should have
     */
    JSpinner peopleSpn;
    /**
     * Part of changing the discrete distribution responsible for the spawning the people in the simulation -
     * A number spinner used to select the floor that's probability distribution will be changed once the 
     * button is pressed.
     */
    JSpinner pDistFlrSpn;
    /**
     * Part of changing the discrete distribution responsible for the spawning the people in the simulation -
     * A number spinner used to select the new probability for the floor.
     */
    JSpinner pDistSpn;
    /**
     * The number spinner used to select the number of floors for the graph that could be created.
     */
    JSpinner graphFloorsSpn;
    /**
     * The number spinner used to select the number of people for the graph that could be created.
     */
    JSpinner graphPeopleSpn;
    /**
     * A text box that can be used to display to the user error messages or results.
     */
    JTextArea informTxt;
    /**
     * A check box for whether the mechanical system should be displayed
     */
    JRadioButton visualiseMechBtn;
    /**
     * A check box for whether the advanced system should be displayed
     */
    JRadioButton visualiseAdvBtn;
    /**
     * A check box for whether the optimal system should be run and displayed
     */
    JRadioButton visualiseOptBtn;
    /**
     * An array used to store the current probability of a person spawning on each floor
     */
    private ArrayList<Integer> distribution;
    /**
     * Constructor that builds the JFrame window and all the interactive features of the program
     */
    public Program()
    {
    	distribution = new ArrayList<Integer>();
    	
        JFrame frame = new JFrame("The Lift Problem");
        frame.setPreferredSize(new Dimension(1000, 500));
        frame.setMinimumSize(new Dimension(1000, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        JPanel pane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx= 1.0;
        c.weighty = 1.0;
        
        //TITLES
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 10;
        pane.add(new JLabel("The Lift System"), c);
        c.gridwidth = 5;
        c.gridy = 1;
        pane.add(new JLabel("Simulation"), c);
        c.gridwidth = 3;
        c.gridx = 6;
        pane.add(new JLabel("Display Results"), c);
        
        //SIMULATION OPTIONS
        //No floors spinner box (simulation)
        c.gridx = 0;
        c.gridy = 2;
        pane.add(new JLabel("No. Floors:"), c);
        SpinnerModel noFloorsSpn = new SpinnerNumberModel(6, 2, 50, 1);     
        floorsSpn = new JSpinner(noFloorsSpn);
        c.gridx = 1;
        pane.add(floorsSpn, c);
        
        //No people spinner box (simulation)
        c.gridx = 0;
        c.gridy = 3;
        pane.add(new JLabel("No. People:"), c);
        SpinnerModel noPeopleSpn = new SpinnerNumberModel(6, 2, 50, 1);     
        peopleSpn = new JSpinner(noPeopleSpn);
        c.gridx = 1;
        pane.add(peopleSpn, c);
        
        // Change probability options
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 4;
        pane.add(new JLabel("Change floor "), c);
        pDistFlrSpn = new JSpinner(new SpinnerNumberModel(1, 0,(int) floorsSpn.getValue() - 1, 1));
        
        c.gridx = 1;
        pane.add(pDistFlrSpn, c);
        c.gridx = 2;
        pane.add(new JLabel(" to  have P of "), c);
        pDistSpn = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        c.gridx = 3;
        pane.add(pDistSpn, c);
        JButton changePDistBtn = new JButton("Change");
        changePDistBtn.setActionCommand("changePDist");
        changePDistBtn.setPreferredSize(new Dimension(100, 30));
        changePDistBtn.addActionListener(this);
        c.gridwidth = 2;
        c.gridy = 5;
        c.gridx = 0;
        pane.add(changePDistBtn, c);
        JButton viewPDistBtn = new JButton("View");
        viewPDistBtn.setActionCommand("viewPDist");
        viewPDistBtn.setPreferredSize(new Dimension(100, 30));
        viewPDistBtn.addActionListener(this);
        c.gridwidth = 2;
        c.gridy = 5;
        c.gridx = 2;
        pane.add(viewPDistBtn, c);
        
        //Visualise simulation checker
        visualiseMechBtn = new JRadioButton("Visualise Mechanical Route", true);
        c.gridx = 1;
        c.gridy = 6;
        pane.add(visualiseMechBtn, c);
        visualiseAdvBtn = new JRadioButton("Visualise Advanced Route", true);
        c.gridy = 7;
        pane.add(visualiseAdvBtn, c);
        visualiseOptBtn = new JRadioButton("Run Optimum Route (WARNING: Do not run with no. people > 6)", false);
        c.gridy = 8;
        pane.add(visualiseOptBtn, c);
        
        // Run simulation button
        JButton simBtn = new JButton("Run Simulation");
        simBtn.setActionCommand("runSim");
        simBtn.setPreferredSize(new Dimension(150, 40));
        simBtn.addActionListener(this);
        c.gridwidth = 2;
        c.gridy = 9;
        c.gridx = 0;
        pane.add(simBtn, c);
        
        ////SEPERATOR
        JSeparator s = new JSeparator();
        s.setOrientation(SwingConstants.VERTICAL);
        pane.add(s, c);
        
        //GRAPH OPTIONS
        //No floors spinner box (graph)
        c.gridwidth = 1;
        c.gridy = 2;
        c.gridx = 6;
        pane.add(new JLabel("No. Floors:"), c);
        graphFloorsSpn = new JSpinner(noFloorsSpn);
        c.gridx = 7;
        pane.add(graphFloorsSpn, c);
        
        //No people spinner box (graph)
        c.gridwidth = 1;
        c.gridy = 3;
        c.gridx = 6;
        pane.add(new JLabel("No. People:"), c);
        graphPeopleSpn = new JSpinner(noPeopleSpn);
        c.gridx = 7;
        pane.add(graphPeopleSpn, c);
        
        // Compare as number of people changes
        JButton changingPeopleBtn = new JButton("Compare as no. People Increases");
        changingPeopleBtn.setActionCommand("comparePeople");
        changingPeopleBtn.setPreferredSize(new Dimension(150, 40));
        changingPeopleBtn.addActionListener(this);
        c.gridwidth = 2;
        c.gridx = 6;
        c.gridy = 5;
        pane.add(changingPeopleBtn, c);
        
        // Compare as number of floors change
        JButton changingFloorsBtn = new JButton("Compare as no. Floors Increases");
        changingFloorsBtn.setActionCommand("compareFloors");
        changingFloorsBtn.setPreferredSize(new Dimension(150, 40));
        changingFloorsBtn.addActionListener(this);
        c.gridwidth = 2;
        c.gridx = 6;
        c.gridy = 4;
        pane.add(changingFloorsBtn, c);
        
        // Display box and whisker graph button
        JButton graphBtn = new JButton("Compare Algorithms");
        graphBtn.setActionCommand("compareSystems");
        graphBtn.setPreferredSize(new Dimension(150, 40));
        graphBtn.addActionListener(this);
        c.gridwidth = 2;
        c.gridy = 6;
        pane.add(graphBtn, c);
        
        // report errors or information
        informTxt = new JTextArea("", 70, 3);
        c.gridy = 10;
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridheight = 3;
        pane.add(informTxt, c);
        
        frame.setContentPane(pane);
        frame.setVisible(true);
    }

    /**
     * This function is called automatically whenever an action is performed for this window
     */
    public void actionPerformed(ActionEvent e) 
    {
        String key = e.getActionCommand();
        switch (key) {
        	case ("changePDist"):
        	{
        		resize((int) floorsSpn.getValue());
        		changeValue((int) pDistFlrSpn.getValue(),(int) pDistSpn.getValue());
        		break;
        	}
        	case("viewPDist"):
        	{
        		resize((int) floorsSpn.getValue());
        		informTxt.setText(distribution.toString());
        		break;
        	}
            case ("runSim"):
            {
            	resize((int) floorsSpn.getValue());
                Simulation sim = new Simulation((int) floorsSpn.getValue(), (int) peopleSpn.getValue(), new DiscreteDistribution(distribution));
                Route mechRoute = sim.runMechanicalSystem();
                informTxt.setText("Mechanical: " + mechRoute.print());
                Route advRoute = sim.runAdvancedSystem();
                informTxt.setText(informTxt.getText() + "\n Advanced: " +  advRoute.print());
                if (visualiseAdvBtn.isSelected())
                {
                	VisualSimulation a = new VisualSimulation("Advanced", advRoute, sim.initPeople, sim.initFloors, sim.initLift, sim.initFloors.length);
                }
                if (visualiseMechBtn.isSelected())
                {
                	VisualSimulation m = new VisualSimulation("Mechanical", mechRoute, sim.initPeople, sim.initFloors, sim.initLift, sim.initFloors.length);
                }
                if (visualiseOptBtn.isSelected())
                {
                	Route optRoute = sim.runOptimumSystem();
                	informTxt.setText(informTxt.getText() + "\n Optimum: " +  optRoute.print());
                	VisualSimulation o = new VisualSimulation("Optimum", optRoute, sim.initPeople, sim.initFloors, sim.initLift, sim.initFloors.length);
                }
                break;
            }
            case ("compareFloors"):
            {
            	new PeopleGraph((int) graphPeopleSpn.getValue());
            	break;
            }
            case ("comparePeople"):
            {
            	new FloorGraph((int) graphFloorsSpn.getValue());
            	break;
            }
            case ("compareSystems"):
            {
            	new BoxAndWhiskerPlot((int) graphFloorsSpn.getValue(), (int) graphPeopleSpn.getValue());
            	break;
            }
        }
    }
    
    /**
     * Extends or reduces the size of the probability distribution to the size of the number of floors
     * on the building for the simulation.
     * 
     * @param noFloors		The size the distribution should be extended or reduced to - represents the 
     * 						amount of the floors in the building.
     */
    private void resize(int noFloors)
    {
    	if (distribution.size() < noFloors)
    	{
    		for (int i = distribution.size(); i < noFloors; i++)
        	{
        		distribution.add(1);
        	}
    	}
    	else
    	{
    		for (int i = distribution.size() - 1; i >= noFloors; i--)
        	{
        		distribution.remove(i);
        	}
    	}
    	
    }
    
    /**
     * Changes the probability for a person to be spawned on a certain floor by changing the 
     * probability distribution.
     * @param floorNum			The floor that's value probability should be changed
     * @param newProb			The new probability that the floor should be changed to
     */
    private void changeValue(int floorNum, int newProb) 
    {
    	distribution.set(floorNum, newProb);
    }
    
    public static void main(String args[]) 
    {
    	new Program();
    }
}
