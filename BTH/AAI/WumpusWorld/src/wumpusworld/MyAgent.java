package wumpusworld;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private World w;
    int rnd;
    int[] wumpusLoc = {0,0};
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;   
    }
   
            
    /**
     * Asks your solver agent to execute an action.
     */

    public void doAction()
    {
        
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
        
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY))
        {
            w.doAction(World.A_GRAB);
            return;
        }
        
        //Basic action:
        //We are in a pit. Climb up.
        if (w.isInPit())
        {
            w.doAction(World.A_CLIMB);
            return;
        }
        //Test the environment
        /*if (w.hasBreeze(cX, cY))
        {
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(cX, cY))
        {
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(cX, cY))
        {
            System.out.println("I am in a Pit");
        }
        if (w.getDirection() == World.DIR_RIGHT)
        {
            System.out.println("I am facing Right");
        }
        if (w.getDirection() == World.DIR_LEFT)
        {
            System.out.println("I am facing Left");
        }
        if (w.getDirection() == World.DIR_UP)
        {
            System.out.println("I am facing Up");
        }
        if (w.getDirection() == World.DIR_DOWN)
        {
            System.out.println("I am facing Down");
        }
        */
        
        //decide next move
        if(w.hasStench(cX, cY)&&w.hasArrow())//Wumpus can be shot if located
        {
            if((w.hasStench(cX, cY+2))||(w.hasStench(cX-1, cY+1)&&w.isVisited(cX-1, cY))||(w.hasStench(cX+1, cY+1)&&w.isVisited(cX+1, cY)))//Condition for checking wumpus location
            {
                turnTo(World.DIR_UP);
                w.doAction(World.A_SHOOT);
            }
            else if((w.hasStench(cX+2, cY))||(w.hasStench(cX+1, cY-1)&&w.isVisited(cX, cY-1))||(w.hasStench(cX+1, cY+1)&&w.isVisited(cX, cY+1)))//Condition for checking wumpus location
            {
                turnTo(World.DIR_RIGHT);
                w.doAction(World.A_SHOOT);
            }
            else if((w.hasStench(cX, cY-2))||(w.hasStench(cX-1, cY-1)&&w.isVisited(cX-1, cY))||(w.hasStench(cX+1, cY-1)&&w.isVisited(cX+1, cY)))//Condition for checking wumpus location
            {
                turnTo(World.DIR_DOWN);
                w.doAction(World.A_SHOOT);
            }
            else if((w.hasStench(cX-2, cY))||(w.hasStench(cX-1, cY+1)&&w.isVisited(cX, cY+1))||(w.hasStench(cX-1, cY-1)&&w.isVisited(cX, cY-1)))//Condition for checking wumpus location
            {
                turnTo(World.DIR_LEFT);
                w.doAction(World.A_SHOOT);
            }
            else if(cX==1&&cY==1) //First tile. Shoot North. If wumpus still alive, store its location as (2,1) to avoid it
            {
                turnTo(World.DIR_UP);
                w.doAction(World.A_SHOOT);
                if(w.hasStench(cX, cY))
                {
                    wumpusLoc[0] = 2;
                    wumpusLoc[1] = 1;
                }
            }
            else if(cX==1&&cY==4) //Condition for corner
            {
                if(w.isVisited(1, 3))
                {
                       turnTo(World.DIR_RIGHT);
                       w.doAction(World.A_SHOOT);
                }
                if(w.isVisited(2, 4))
                {
                       turnTo(World.DIR_DOWN);
                       w.doAction(World.A_SHOOT);
                }
                
            }
            else if(cX==4&&cY==1) //Condition for corner
            {
                if(w.isVisited(3, 1))
                {
                       turnTo(World.DIR_UP);
                       w.doAction(World.A_SHOOT);
                }
                if(w.isVisited(4, 2))
                {
                       turnTo(World.DIR_LEFT);
                       w.doAction(World.A_SHOOT);
                }
                
            }
            else if(cX==4&&cY==4) //Condition for corner
            {
                if(w.isVisited(3, 4))
                {
                       turnTo(World.DIR_DOWN);
                       w.doAction(World.A_SHOOT);
                }
                if(w.isVisited(4, 3))
                {
                       turnTo(World.DIR_LEFT);
                       w.doAction(World.A_SHOOT);
                }
                
            }
            else if((cX==1)&&(w.isVisited(cX+1, cY-1))) //Condition for edge
            {
                
                turnTo(World.DIR_UP);
                w.doAction(World.A_SHOOT);
            }
            else if((cX==4)&&(w.isVisited(cX-1, cY-1))) //Condition for edge
            {
                turnTo(World.DIR_UP);
                w.doAction(World.A_SHOOT);
            }
            else if((cY==1)&&(w.isVisited(cX-1, cY+1))) //Condition for edge
            {
                turnTo(World.DIR_RIGHT);
                w.doAction(World.A_SHOOT);
            }
            else if((cY==4)&&(w.isVisited(cX-1, cY-1))) //Condition for edge
            {
                turnTo(World.DIR_RIGHT);
                w.doAction(World.A_SHOOT);
            }
            else    //Can't locate wumpus, go back to safe location
            {
                turnTo((w.getDirection()+2)%4);
                w.doAction(World.A_MOVE);
            }
        }
        else // No stench. Explore 
        {
            if(w.isValidPosition(cX, cY-1)&&!w.isVisited(cX, cY-1)&&isSafe(cX, cY-1)) 
            {
                turnTo(World.DIR_DOWN);
                w.doAction(World.A_MOVE);
            }
            else if(w.isValidPosition(cX+1, cY)&&!w.isVisited(cX+1, cY)&&isSafe(cX+1, cY))
            {
                turnTo(World.DIR_RIGHT);
                w.doAction(World.A_MOVE);
            }
            else if(w.isValidPosition(cX-1, cY)&&!w.isVisited(cX-1, cY)&&isSafe(cX-1,cY))
            {
                turnTo(World.DIR_LEFT);
                w.doAction(World.A_MOVE);
            }
            else
            {
                if(w.isValidPosition(cX, cY+1))
                {
                    turnTo(World.DIR_UP);
                    w.doAction(World.A_MOVE);
                }
                else if(w.isValidPosition(cX+1, cY))
                {
                    turnTo(World.DIR_RIGHT);
                    w.doAction(World.A_MOVE);
                }
                else if(w.isValidPosition(cX-1, cY))
                {
                    turnTo(World.DIR_LEFT);
                    w.doAction(World.A_MOVE);
                }
            }
        }
                
    }    
    
    
    public void turnTo(int targetDir) //Function, turns the agent to indicated direction
    {
         
        if(w.getDirection()==targetDir);
        else if((w.getDirection()-targetDir == 1)||(w.getDirection()-targetDir == -3))
        {
            w.doAction(World.A_TURN_LEFT);
        }
        else if((w.getDirection()-targetDir == 2)||(w.getDirection()-targetDir == -2))
        {
            w.doAction(World.A_TURN_LEFT);
            w.doAction(World.A_TURN_LEFT);
        }
        else if((w.getDirection()-targetDir == 3)||(w.getDirection()-targetDir == -1))
        {
            w.doAction(World.A_TURN_RIGHT);
        }
    }
    public boolean isSafe(int x, int y) //Function, returns true if the tile is free of wumpus
    {
        if(x==wumpusLoc[0]&&y==wumpusLoc[1])
            return false;
        return true;
    }    
}

