package loaders;

import entities.GameObject;
import entities.Piece;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import soma.Solution;
import utilities.ResourceFile;

public class SolutionManager {
    @Getter
    private final static List<Solution> solutions;

    static {
        solutions = loadSolutions();
    }

    public static void main(String[] args) {
        /*
        exporting loaded solutions

        final File export = new File("C:\\Users\\jgb14002\\Desktop\\export.txt");
        try {
            export.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(export))) {
            for (int i = 0; i < solutions.length; i++) {
                final int[][][][] sln = solutions[i];
                for (int s = 0; s < 3; s++) {
                    for (int r = 0; r < 3; r++) {
                        for (int p = 0; p < 7; p++) {
                            int[] vals = sln[p][s][r];
                            writer.write(String.format("%d%d%d%s", vals[0], vals[1], vals[2], (p < 6) ? "  " : ""));
                        }
                        writer.newLine();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }


    private static List<Solution> loadSolutions() {
        int[][][][][] solutions = new int[240][7][3][3][3];

        try {
            int linesRead = 0;
            int matches = 0;
            int solutionIndex = 0;
            int pieceIndex = 0;
            int sliceIndex = 0;
            int row = 0;

            BufferedReader reader = new BufferedReader(new InputStreamReader(SolutionManager.class.getResourceAsStream("/soma/solutions.soma")));
            String line;
            while ((line = reader.readLine()) != null) {
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(line);
                while (m.find()) {
                    solutionIndex = linesRead / 9 % 240;
                    pieceIndex = matches % 7;
                    sliceIndex = linesRead / 3 % 3;
                    row = linesRead % 3;

                    for (int col = 0; col < 3; col++) {
                        int value = Integer.valueOf(m.group().substring(col, col + 1));
                        solutions[solutionIndex][pieceIndex][sliceIndex][row][col] = value;
                    }
                    matches++;
                }
                linesRead++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Solution> solutionList = new ArrayList<>();

        for (int i = 0; i < solutions.length; i++) {
            solutionList.add(new Solution(solutions[i]));
        }

        return solutionList;
    }

    private static GameObject.Axis[] ROTATION_PATH = {
            GameObject.Axis.X_AXIS,
            GameObject.Axis.X_AXIS,
            GameObject.Axis.Z_AXIS,
            GameObject.Axis.X_AXIS,
            GameObject.Axis.X_AXIS,
            GameObject.Axis.Z_AXIS
    };

    public static int getPossibleSolutionCount(Collection<Piece> pieces) {
        Set<Solution> foundSolutions = new HashSet<>();

        for (GameObject.Axis pathAxis : ROTATION_PATH) {
            for (int y = 0; y < 4; y++) {
                for (Solution s : solutions) {
                    for (int m = 0; m < 2; m++) {
                        if (s.isDerrivedFrom(pieces)) {
                            foundSolutions.add(s);
                        }
                        pieces.forEach(Piece::mirror);
                        s.invert();
                    }
                }
                pieces.forEach(piece -> piece.rotate(GameObject.Axis.Y_AXIS));
            }
            pieces.forEach(piece -> piece.rotate(pathAxis));
        }
        return foundSolutions.size();
    }

}
