import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class MazeSolver {

   // The name of the file describing the maze
   static String mazefile;
   static int cellsize;
   static int borderwidth;
   static int sleeptime;
   static boolean argu;
   static boolean finish;
   static char[][] maze;
   static int x;
   static int y;

   public static void main(String[] args) throws FileNotFoundException {
      String mazefile = args[0];
      File inputfile = new File(mazefile);
      try{ //tests the length of the arguments and assigns new values to variables, if values are in argument
         if(args.length >=2){
            cellsize = Integer.parseInt(args[1]);
            if(args.length >=3){
               borderwidth = Integer.parseInt(args[2]);
               if(args.length ==4){
                  sleeptime = Integer.parseInt(args[3]);
                  }
               }
            }
            else{
               cellsize = 30;
               borderwidth = 40;
               sleeptime = 50;
            }
         }
         catch(NumberFormatException ex){ //catches any problems that could happen in arguments
            System.out.println("Cellsize, Borderwidth, and Sleeptime must be integers");
            argu = false;
         }

      if (handleArguments(args)) {
         
         System.out.println("Cellsize is " + cellsize);
         System.out.println("Borderwidth is " + borderwidth);
         System.out.println("Sleeptime is " + sleeptime);
         
         
         maze = readMazeFile(mazefile); //reads file into a char array called maze
         
      }
      else{
         System.out.println("Incorrect argument input");
      }
   }
   
   // Handle the input arguments
   /*This function takes in the arguments provided and tests how many there are,
   if they are in proper form (integers or strings), and if they are in the range
   required for the variable. Then the function assigns the variable the argument
   */
   static boolean handleArguments(String[] args) {
        mazefile = args[0];
        File inputfile = new File(mazefile);
        if(args.length == 1){
            if (inputfile.canRead()){
                argu = true;
            }
        }else if(args.length == 2){
            if (inputfile.canRead() && cellsize >= 10) {
               argu = true;
            }                 
        }else if(args.length == 3){
            if ((inputfile.canRead()) && (cellsize >= 10) && (borderwidth >= 5)){
               argu = true;
            }
        }else if(args.length == 4){
            if ((inputfile.canRead()) && (cellsize >=10) && (borderwidth >=5)){
               if ((sleeptime > 0) && (sleeptime <=1000)){
                  argu = true;
               }
               else{
                  argu = false;
               }
            }
        }else{
            argu = false;
        }
        return argu;
   }
   
   // Read the file describing the maze.
   /*This file takes in the mazefile string and reads in the mazefile into a 2D char array
   using for loops. Once the array is initialized, the array is sent to the DrawMaze.class to
   be drawn for the user. Finally, the function calls on the solve function to test the maze for 
   a solution, which will draw the solution happening
   */
   static char[][] readMazeFile(String mazefile) throws FileNotFoundException {
      File inputfile = new File(mazefile);
      Scanner input = new Scanner(inputfile);
      int arrayHeight = 2*(input.nextInt()) + 1;
      int arrayWidth = 2*(input.nextInt()) + 1;
      //System.out.println("Array Height is " + arrayHeight);
      //System.out.println("Array Width is " + arrayWidth);
      // Height = number of rows, Width = number of columns
      char[][] maze = new char[arrayHeight][arrayWidth];
      String line = input.nextLine();
      line = input.nextLine(); //second line to skip line that includes H and W of maze
      //System.out.println("This line is " + line);
      for(int j = 0; j<arrayHeight; j++){
         for(int i = 0; i < arrayWidth; i++){
            maze[j][i] = line.charAt(i);
            System.out.print(maze[j][i]);
         }
         System.out.println();
         if(j < arrayHeight - 1){
            line = input.nextLine();
         }
      }
      DrawMaze.draw(maze, cellsize, borderwidth, sleeptime, arrayWidth, arrayHeight);
      for(int r = 0; r< arrayHeight; r++){
            for(int c = 0; c< arrayWidth; c++){
               if(maze[r][c] == 'S'){
                  x = r;
                  y = c;
               }
            }
         }
            
      if (solveMaze(maze, x, y, arrayHeight, arrayWidth, cellsize, sleeptime))
         System.out.println("Solved!");
      else
         System.out.println("Maze has no solution.");

      return maze;
   }

   // Solve the maze.    
   /*This function takes in the maze array, the point in the array, the array info, and the function info
   to return a true or false value of if the function has solved the array.
   Using recursion, the function first tests if the point is at the end. Then it calls upon functions to test
   if moving in the four directions is possible. If the functions return true, this function continues until the 
   point can no longer move (which it will go backwards until it can move again) or return false, that the
   maze is unsolvable.
   */  
   static boolean solveMaze(char[][] maze, int x, int y, int arrayHeight, int arrayWidth, int cellsize, int sleeptime) {
      finish = false;
      if(maze[x][y] == 'E'){
         return true;
      }
      else{
        maze[x][y] = 'w';
        if(checkup(maze, x-1, y, arrayHeight)){
           DrawMaze.move(x, y, x-1, y, cellsize, borderwidth, sleeptime);
           finish = solveMaze(maze, x-1, y, arrayHeight, arrayWidth, cellsize, sleeptime);
           if(finish){
               return finish;
           }
        }
        if(checkdown(maze, x+1, y, arrayHeight)){
           DrawMaze.move(x, y, x+1, y, cellsize, borderwidth, sleeptime);
           finish = solveMaze(maze, x+1, y, arrayHeight, arrayWidth, cellsize, sleeptime);
           if(finish){
               return finish;
           }
        }
        if(checkright(maze, x, y+1, arrayWidth)){
           DrawMaze.move(x, y, x, y+1, cellsize, borderwidth, sleeptime);
           finish = solveMaze(maze, x, y+1, arrayHeight, arrayWidth, cellsize, sleeptime);
           if(finish){
               return finish;
           }
        }
        if(checkleft(maze, x, y-1, arrayWidth)){
           DrawMaze.move(x, y, x, y-1, cellsize, borderwidth, sleeptime);
           finish = solveMaze(maze, x, y-1, arrayHeight, arrayWidth, cellsize, sleeptime);
           if(finish){
               return finish;
           }
        }
      } 
      return finish;
   }
   // checks if function can go up in values without running into walls or already visited values
   public static boolean checkup(char[][]maze, int x, int y, int arrayHeight){
      if(x>=0){
         if(maze[x][y] != '|' && maze[x][y] != '+' && maze[x][y] != '-' && maze[x][y] != 'w'){
            return true;
         }
         else{
            return false;
         }
      }
      else{
         return false;
      }
   }
   // checks if function can go down in values without running into walls or already visited values

   public static boolean checkdown(char[][]maze, int x, int y, int arrayHeight){
      if(x<=arrayHeight){
         if(maze[x][y] != '|' && maze[x][y] != '+' && maze[x][y] != '-' && maze[x][y] != 'w'){
            return true;
         }
         else{
            return false;
         }
      }
      else{
         return false;
      }
   }
   // checks if function can go right in values without running into walls or already visited values

   public static boolean checkright(char[][]maze, int x, int y, int arrayWidth){
      if(y<= arrayWidth){
         if(maze[x][y] != '|' && maze[x][y] != '+' && maze[x][y] != '-' && maze[x][y] != 'w'){
            return true; 
         }
         else{
            return false;
         }
      }
      else{
         return false;
      }
   }
   // checks if function can go left in values without running into walls or already visited values

   public static boolean checkleft(char[][]maze, int x, int y, int arrayWidth){
      if(y>= 0){
         if(maze[x][y] != '|' && maze [x][y] != '+' && maze [x][y] != '-' && maze[x][y] != 'w'){
            return true;
         }
         else{
            return false;
         }
      }
      else{
         return false;
      }
   }

}