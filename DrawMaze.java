/* Audrey Clark
Draw maze class
*/
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

public class DrawMaze {

   public static final Color WALL_COLOR = Color.RED;
   public static final Color START_COLOR = Color.BLUE;
   public static final Color PATH_COLOR = Color.BLACK;
   public static final Color END_COLOR = Color.ORANGE;
   public static final Color BACKGROUND = new Color(0.9f, 0.9f, 0.9f);
   
   
   // Width of border in pixels
   public static int borderwidth;
   
   public static int cellsize;
   
   // Time to sleep after a move (in milliseconds)
   // Change to default to 50. 1000 gives time to observe the program.
   public static int sleeptime;
   
   public static int startX;
   public static int startY;
   public static int endX;
   public static int endY;
   
   public static boolean finish;
   // The drawing panel
   static DrawingPanel panel = null;
   
   // Utility routines.
   // celltop computes the y coordinate for the top of a cell in row r
   // (rows are numbered starting at zero.)  cellleft computes the x
   // coordinate for the left of a cell in column c (columns are also
   // numbered from zero. (cellleft(c), celltop(r)) gives the (x, y)
   // coordinates of the top left of a cell.
   static int celltop(int r, int cellsize, int borderwidth) {
      //System.out.println("Cellsize in celltop is " + cellsize);
      //System.out.println("Borderwidth in celltop is " + borderwidth);
      return borderwidth + cellsize * r;
   }
   
   static int cellleft(int c, int cellsize, int borderwidth) {
      //System.out.println("Cellsize in cellleft is " + cellsize);
      //System.out.println("Borderwidth in cellleft is " + borderwidth);
      return borderwidth + cellsize * c;
   }
   
   // Constants for drawStart
   static final double SZ_WLINE = 0.05;
   static final double SZ_RHEAD = 0.17;
   static final double SZ_YHEAD = 0.25;
   static final double SZ_XCENTR = 0.51;
   static final double SZ_YARM = 0.50;
   static final double SZ_XARM = 0.1;
   static final double SZ_YCROTCH = 0.67;
   static final double SZ_YFEET = 0.90;
   static final double SZ_XFEET = 0.25;
   
   public static void drawStart(int r, int c, int cellsize, int borderwidth) {
      Graphics g = panel.getGraphics();
      g.setColor(START_COLOR);
      int top = celltop(r, cellsize, borderwidth);
      int left = cellleft(c, cellsize, borderwidth);
      int right = left + cellsize;
      
      int rhead = (int)Math.round(SZ_RHEAD * cellsize);
      int wline = (int)Math.round(SZ_WLINE * cellsize);
      wline = Math.max(1, wline + (1 - wline % 2));
      int yhead = top + (int)Math.round((SZ_YHEAD - SZ_RHEAD) * cellsize);
      int xcentr = left + (int)Math.round(SZ_XCENTR * cellsize);
      int xhead = left + (int)Math.round((SZ_XCENTR - SZ_RHEAD) * cellsize);
      int yneck = top + -1 + (int)Math.round((SZ_YHEAD + SZ_RHEAD) * cellsize);
      int ycrotch = top + (int)Math.round(SZ_YCROTCH * cellsize);
      int yarm = top + (int)Math.round(SZ_YARM * cellsize);
      int xarm = (int)Math.round(SZ_XARM * cellsize);
      int xfeet = (int)Math.round(SZ_XFEET * cellsize);
      int yfeet = top + (int)Math.round(SZ_YFEET * cellsize);
      g.fillOval(xhead, yhead, 2 * rhead, 2 * rhead);
      int crotchleft = xcentr - wline / 2;
      g.fillRect(crotchleft, yneck, wline, ycrotch - yneck);
      g.fillRect(left + xarm, yarm - wline / 2, cellsize - 2 * xarm, wline);
      int leftlegleft = left + xfeet - wline / 2;
      int rightlegleft = left + cellsize - xfeet - wline / 2; 
      Polygon leg = new Polygon(new int[] {crotchleft, crotchleft + wline,
                                           leftlegleft + wline, leftlegleft},
                                new int[] {ycrotch, ycrotch, yfeet, yfeet}, 4);
      g.fillPolygon(leg);
      leg = new Polygon(new int[] {crotchleft, crotchleft + wline,
                                   rightlegleft + wline, rightlegleft},
                        new int[] {ycrotch, ycrotch, yfeet, yfeet}, 4);
      g.fillPolygon(leg);
   }
   
   // Constants for drawEnd
   static final double SZ_RIN = 0.2;
   static final double SZ_ROUT = 0.4;
   static final int N_PTS = 8;
   
   static void drawEnd(int r, int c, int cellsize, int borderwidth) {
      Graphics g = panel.getGraphics();
      g.setColor(END_COLOR);
      int x0 = cellleft(c, cellsize, borderwidth) + cellsize / 2;
      int y0 = celltop(r, cellsize, borderwidth) + cellsize / 2;
      Polygon star = new Polygon();
      double inradius = SZ_RIN * cellsize;
      double outradius = SZ_ROUT * cellsize;
      
      for (int i = 0; i < N_PTS; i++) {
         double angle = (2 * i * Math.PI) / N_PTS ;
         int x = x0 + (int)Math.round(outradius * Math.cos(angle));
         int y = y0 + (int)Math.round(outradius * Math.sin(angle));
         star.addPoint(x, y);
         angle += Math.PI / N_PTS;
         x = x0 + (int)Math.round(inradius * Math.cos(angle));
         y = y0 + (int)Math.round(inradius * Math.sin(angle));
         star.addPoint(x, y);
      }
      g.fillPolygon(star);
   }
   /*Function is called from Maze solver to draw the maze in the given array with the given information
   */
   public static void draw(char[][] maze, int cellsize, int borderwidth, int sleeptime, int arrayWidth, int arrayHeight)
   {
      System.out.println("Sleeptime is " +sleeptime);
      int h = cellsize*borderwidth + 10;
      int w = cellsize*borderwidth + 30;
      //System.out.println("Height in draw() is " + h);
      //System.out.println("Width in draw() is " + w);
      
      if (panel == null) {
         panel = new DrawingPanel(w, h);
      }
      panel.clear();
      Graphics g = panel.getGraphics();
      g.setColor(BACKGROUND);
      g.drawRect(0, 0, w, h);
      g.fillRect(0, 0, w, h);

      g.setColor(WALL_COLOR);
      
      //calls function to find the start and finish of the maze
      findSE(maze, arrayWidth, arrayHeight, cellsize, borderwidth);
      
      //calls function to draw walls of the maze
      drawWalls(maze, arrayWidth, arrayHeight, cellsize, borderwidth);
      

   }
   //function is called from mazeSolver's recursive function to show the moves being made
   public static void move(int r0, int c0, int r1, int c1, int cellsize, int borderwidth, int sleeptime) {
      Graphics g = panel.getGraphics();
      g.setColor(PATH_COLOR);
      int delta = cellsize / 2;
      g.drawLine(cellleft(c0, cellsize, borderwidth) + delta, celltop(r0, cellsize, borderwidth) + delta,
                 cellleft(c1, cellsize, borderwidth) + delta, celltop(r1, cellsize, borderwidth) + delta);
      panel.sleep(sleeptime);
   }

   //function to find start and end points of given maze then calls drawStart, drawEnd functions
   public static void findSE(char[][] maze, int arrayWidth, int arrayHeight, int cellsize, int borderwidth){
   //create integer variables to hold point values of x,y for start and end
   for(int r = 0; r< arrayHeight; r++){
      for(int c = 0; c< arrayWidth; c++){
         if(maze[r][c] == 'S'){
         startX = r-1;
         startY = c-1;
         }else if(maze[r][c] == 'E'){
         endX = r-1;
         endY = c-1;
         }
      }
   }
   //System.out.println("Starting point is " + startX + " , " + startY);
   //System.out.println("Ending point is " + endX + " , " + endY);
   drawStart(startX, startY, cellsize, borderwidth); //calls to draw the starting point
   drawEnd(endX, endY, cellsize, borderwidth); //calls to draw the ending point
   }

/*This function takes in the maze and the program's information then outputs the placement of the walls.
First, it goes through the array with for loops to note points where there is a char that denotes a wall (|,-,+),
then it calls the drawline graphic to output these walls in the right color
*/
   public static void drawWalls(char[][] maze, int arrayWidth, int arrayHeight, int cellsize, int borderwidth){
      Graphics g = panel.getGraphics();
      g.setColor(WALL_COLOR);
      for(int r = 0; r< arrayHeight; r++){
         for(int c = 0; c< arrayWidth; c++){
            if(maze[r][c] == '|'){
               //System.out.println("Point is " + r + "," + c); 
               startX = c;
               startY = r - 1;
               endX = c;
               endY = r + 1;
               //System.out.println("Start line at " + startX + " , " + startY);
               //System.out.println("End line at " + endX + " , " + endY);
               g.drawLine(cellleft(startX, cellsize, borderwidth), celltop(startY, cellsize, borderwidth), cellleft(endX, cellsize, borderwidth), celltop(endY, cellsize, borderwidth));           
            }
            else if(maze[r][c] == '-'){
               startX = (c - 1);
               startY = r;
               endX = (c + 1);
               endY = r;
               g.drawLine(cellleft(startX, cellsize, borderwidth), celltop(startY, cellsize, borderwidth), cellleft(endX, cellsize, borderwidth), celltop(endY, cellsize, borderwidth));
               
            }  
         }

      }
   }
   }
