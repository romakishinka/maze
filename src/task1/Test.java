package task1;

import java.io.File;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {

        Maze mazeClass = new Maze(new File("src\\task1\\resources\\source"));

        Helper helper = new Helper();

        List<Coordinate> path = helper.solve(mazeClass);

        mazeClass.printPath(path);
    }



}