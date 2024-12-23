import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Maps {

    public static int[][] map1;

    public static class ReadMap {
        static String[][] map1String;
        static int y = 0;
        static int x;

        public static void loadMap() {
            try {
                File myObj = new File("map1");
                Scanner map1Scanner = new Scanner(myObj);

                int lineCount = 0;
                while (map1Scanner.hasNextLine()) {
                    map1Scanner.nextLine();
                    lineCount++;
                }
                map1Scanner.close();

                map1String = new String[lineCount][];
                map1Scanner = new Scanner(myObj);

                for (int i = 0; i < lineCount; i++) {
                    String line = map1Scanner.nextLine();
                    map1String[i] = line.split(" ");
                }

                y = lineCount;
                x = map1String[0].length;

                map1 = new int[y][x];

                for (int i = 0; i < y; i++) {
                    for (int j = 0; j < x; j++) {
                        map1[i][j] = Integer.parseInt(map1String[i][j]);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }
}
