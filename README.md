# LiftProject
As part of a first year university module called Data Structures and Algorithms, we were given a project to create a simulation of a lift in a building. We were then to attempt to optimise this lift.

##The Algorithms
### The Mechanical Algorithm
The most basic algorithm. The lift travels from the bottom to the top of the building until all people are delivered to their destination.

### The Advanced Algorithm
This algorithm divides the building into 3 sections; top, middle and bottom. When the lift is in either top/bottom and capacity is not reached, everyone who can be delivered in the section is delivered before the lift moves on. In the middle section, the lift will move in the direction it is already travelling unless there is no one to pick up or drop off in that direction. If the capacity is likely to be full, the lift will only travel somewhere it can drop someone off.

### The Optimum Algorithm
This is not applicable in the real world but can be used to find the optimum route that the lift could have taken for the purposes of the simulation. 
The algorithm uses recursion to test every possible (sensible) path that the lift could take, and then picks the best option. 

## Snippets
### Submission Video
https://www.youtube.com/watch?v=KfQjE0Gh2-M
### The main GUI 
![image](https://user-images.githubusercontent.com/42321644/189490972-6a0d7d61-b701-45eb-bcb6-bda2895d0767.png)
There are many controls here to adapt the simulation:
  * The number of floors in the building
  * The number of people spawned at the beginning of the simulation
  * Altering the probability of having a person spawn on a certain floor
  * Which algorithms you want to visualise

The simulation visualization opens a window to display the route e.g.
![image](https://user-images.githubusercontent.com/42321644/189491005-75540892-1372-4881-9dd3-885cebe6b96b.png)

### Results Analysis
To visualise how well the algorithms perform, I stored the results data and enabled the creation of graphs through the GUI. e.g.
![image](https://user-images.githubusercontent.com/42321644/189490865-610c2227-b301-4f2f-adf9-c620e5f2e140.png)
