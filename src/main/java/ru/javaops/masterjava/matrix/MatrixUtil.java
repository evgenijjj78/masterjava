package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        class MyTask implements Runnable {
            final int from;
            final int to;
            MyTask(int from, int to) {
                this.from = from;
                this.to = to;
            }
            @Override
            public void run() {
                for (int i = from; i < to; i++) {
                    int[] column = new int[matrixSize];
                    for (int l = 0; l < matrixSize; l++) {
                        column[l] = matrixB[l][i];
                    }
                    for (int j = 0; j < matrixSize; j++) {
                        int[] row = matrixA[j];
                        int sum = 0;
                        for (int k = 0; k < matrixSize; k++) {
                            sum += row[k] * column[k];
                        }
                        matrixC[j][i] = sum;
                    }
                }
            }
        }
        int tasksNumber = 7;
        ArrayList<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < tasksNumber; i++) {
            futures.add(executor.submit(new MyTask(i * matrixSize / tasksNumber, (i + 1) * matrixSize / tasksNumber)));
        }
        boolean check = false;
        while (!check) {
            for (Future<?> f : futures) {
                check = f.isDone();
                if (!check) break;
            }
        }
        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
