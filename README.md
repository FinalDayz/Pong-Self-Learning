# Pong-Self-Learning
An A.I. that learns to play pong.

There are 2 neural networks that play against eachother. The fitness is based on the goals they make.
The side that is doing the worst is learning.
Each time there are 36 neural networks playing 2 games (1 game is 12000 ticks, about 120 seconds). 
All the networks are slightly ajusted based on the network of the previous generation (parent network).
In the beginning there are 36 random networks, and the best one is selected.
When all 36 networks played against the oponent, the best one is selected and so on.

