package task1;

import java.io.*;
import java.util.List;
import java.util.Random;

public class Maze {
    private static final int EMPTY_ROOM = 0;
    private static final int WALL = 1;
    private static final int START = 2;
    private static final int EXIT = 3;
    private static final int BALL = 4;
    private static final int BARRIER = 5;  // перегородка

    private static final int N = 2; // на каждый N ход перегородка открывается ('/'  - перегородка открыта, двигаемся дальше)
    private static final int M = 3; // на каждый M ход перегородка закрывается ('|' - перегородка закрыта, пропускаем ход)

    private int[][] maze;  // представление лабиринта 8х8
    private boolean[][] visited; // посещена ли данная комната ранее
    private Coordinate start;  // начало лабиринта
    private Coordinate end;  //конец лабиринта

    public Maze(File fileName) throws IOException {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(fileName)) ;
        while (reader.ready()) {
            sb.append(reader.readLine());
            sb.append("\n");
        }
        initializeMaze(sb.toString());
        reader.close();


    }

    // проходимся по массиву и заносим в maze значения переданные из строки
    private void initializeMaze(String text) {

        String[] lines = text.split("\n");
        maze = new int[lines.length][lines[0].length()];
        visited = new boolean[lines.length][lines[0].length()];

        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                if (lines[row].charAt(col) == '#')
                    maze[row][col] = WALL;
                else if (lines[row].charAt(col) == 'S') {
                    maze[row][col] = START;
                    start = new Coordinate(row, col);
                } else if (lines[row].charAt(col) == 'E') {
                    maze[row][col] = EXIT;
                    end = new Coordinate(row, col);
                } else
                    maze[row][col] = EMPTY_ROOM;
            }
        }
    }

    public int getHeight() {
        return maze.length;
    }

    public int getWidth() {
        return maze[0].length;
    }

    public Coordinate getEntry() {
        return start;
    }

    public Coordinate getExit() {
        return end;
    }

    public boolean isExit(int x, int y) {
        return x == end.getX() && y == end.getY();
    }

    public boolean isStart(int x, int y) {
        return x == start.getX() && y == start.getY();
    }

    public boolean isVisited(int row, int col) {
        return visited[row][col];
    }

    public boolean isWall(int row, int col) {
        return maze[row][col] == WALL;
    }

    public void setVisited(int row, int col, boolean value) {
        visited[row][col] = value;
    }

    //обозначение допустимых границ
    public boolean isValidLocation(int row, int col) {
        if (row < 0 || row >= getHeight() || col < 0 || col >= getWidth()) {
            return false;
        }
        return true;
    }

    public void printPath(List<Coordinate> path) throws InterruptedException, IOException {
        FileWriter stringWriter = new FileWriter("src\\task1\\resources\\result");

        //обозначаем путь мячом
        for (Coordinate coordinate : path) {
            if (isStart(coordinate.getX(), coordinate.getY()) || isExit(coordinate.getX(), coordinate.getY())) {
                continue;
            }
            maze[coordinate.getX()][coordinate.getY()] = BALL;
        }

        printLegend(stringWriter);

        String result = getString(maze);
        System.out.println(result);


        stringWriter.write(result);
        stringWriter.flush();
        stringWriter.close();

    }



    public String getString(int[][] maze) throws InterruptedException {
        Random random = new Random();
        // время пути шара
        int time = 0;

        // счетчики перегородок
        int countM = 0;
        int countN = 0;

        //генерируем рандомное количество перегородок в некоторых, рандомных комнатах
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                if(maze[row][col] == BALL && random.nextBoolean() && random.nextBoolean() )
                maze[row][col]= BARRIER;
            }
        }


        StringBuilder result  = new StringBuilder(getWidth() * (getHeight() + 1));
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                if (maze[row][col] == EMPTY_ROOM) {
                    result.append(' ');
                } else if (maze[row][col] == WALL) {
                    result.append('#');
                } else if (maze[row][col] == START) {
                    result.append('S');
                    countM++;
                    countN++;
                } else if (maze[row][col] == EXIT) {
                    result.append('E');
                    time++;
                }

                else{
                    countM++;
                    countN++;
//                  если данные координаты являются мячом, то мы рисуем его ('.') и прибавляем один ход
                    if(maze[row][col] == BALL) {
                        result.append('.');
                        time++;

                    }
//                    если же данные координаты являются комнотой с перегородкой, то мы проверям, открыта ли данная перегородка
//                    на данный N-й ход,
//                    если да, то мы рисуем открытую перегородку('/') и засчитываем один ход.
//                    если перегородка закрыта в данный М-й ход, то мы рисуем закрытую перегородку('|') и дабавляем два хода
//                    (тем самым иммитируем пропуск хода, пока закрывающаяся перегородка не откроется в начале следующего хода)
//                    если перегородка не является ни открывающей, ни закрывающей, то мы считаем, что перегордок
//                    в данной комнате нет и рисуем мяч ('.')
                    else if (maze[row][col] == BARRIER) {

                        if(countN == N ) {
                            result.append('/');
                            time++;
                        }
                       else if(countM == M ) {
                            result.append('|');
                            time+=2;
                       }
                       else {
                            result.append('.');
                            time++;
                        }
                    }
//                  обнуляем счетчики
                    if(countM == M)
                        countM=0;
                    if(countN == N)
                        countN=0;
                }
            }
            result.append('\n');
        }
        result.append('\n');
        result.append("По заданному пути М = " + M +" шар докатился до выхода за " + time + " ход(-ов)");
        result.append('\n');

        return result.toString();
    }


    private void printLegend(FileWriter stringWriter) throws IOException {  //+'\n'+'\n'
        String legend = "Легенда лабиринта: \n" +
                "'#' - cтена \n" +
                "'S' - вход \n" +
                "'E' - выход \n" +
                "'.' - путь мяча \n" +
                "'/' - открытая перегородка, ход не пропускается \n" +
                "'|' - перегородка закрыта, пропускам ход \n";
        System.out.println(legend);
        stringWriter.write(legend);
    }
}
