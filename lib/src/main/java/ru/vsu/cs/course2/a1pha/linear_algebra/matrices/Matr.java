package ru.vsu.cs.course2.a1pha.linear_algebra.matrices;

import java.util.Arrays;

import ru.vsu.cs.course2.a1pha.linear_algebra.NumberChecker;
import ru.vsu.cs.course2.a1pha.linear_algebra.vectors.VectorInterface;

/**
 * Matr
 */
public class Matr implements Matrix {

    float[][] entries;

    public Matr(float[][] entries) {
        this.entries = new float[entries.length][entries[0].length];
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].length != entries[0].length) {
                throw new IllegalArgumentException(
                        "Matrix creation denied: input data has rows with different lengths");
            }
            this.entries[i] = Arrays.copyOf(entries[i], entries[i].length);
        }
    }

    public Matr(int height, int width) {
        entries = new float[height][width];
    }

    @Override
    public float get(int row, int col) {
        return entries[row][col];
    }

    @Override
    public void set(int row, int col, float value) {
        entries[row][col] = value;
    }

    @Override
    public int width() {
        return entries[0].length;
    }

    @Override
    public int height() {
        return entries.length;
    }

    @Override
    public void transpose() {
        float[][] result = new float[entries[0].length][entries.length];
        for (int i = 0; i < entries.length; i++) {
            for (int j = 0; i < entries[0].length; j++) {
                result[j][i] = entries[i][j];
            }
        }
        entries = result;
    }

    @Override
    public void add(Matrix matr) {
        checkSameSizes(this, matr, "Addition denied");
        UncheckedMatrixOperations.addTo(this, matr);
    }

    @Override
    public void subtract(Matrix matr) {
        checkSameSizes(this, matr, "Subtraction denied");
        UncheckedMatrixOperations.subtractFrom(this, matr);
    }

    @Override
    public Matrix product(Matrix matr) {
        if (this.width() == matr.height()) {
            throw new IllegalArgumentException(
                    String.format("Product denied: matrices with sizes %dx%d and %dx%d", this.height(),
                            this.width(), matr.height(), matr.width()));
        }

        Matr result = new Matr(this.height(), matr.width());

        for (int i = 0; i < this.height(); i++) {
            for (int j = 0; j < matr.width(); j++) {
                float value = 0;
                for (int elem = 0; elem < matr.height(); elem++) {
                    value += this.get(i, elem) * matr.get(elem, j);
                }

                result.set(i, j, value);
            }
        }

        return result;
    }

    @Override
    public <V extends VectorInterface<V>> V product(VectorInterface<V> vec) {
        if (this.width() != vec.size()) {
            throw new IllegalArgumentException(
                    String.format("Product denied: matrix with size %dx%d and vector with size", this.height(),
                            this.width(), vec.size()));
        }

        V result = vec.copy();

        for (int i = 0; i < this.height(); i++) {
            float value = 0;
            for (int elem = 0; elem < vec.size(); elem++) {
                value += this.get(i, elem) * vec.get(elem);
            }

            result.set(i, value);
        }

        return result;
    }

    @Override
    public boolean isSquare() {
        return width() == height();
    }

    @Override
    public boolean isZeroed() {
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                if (entries[i][j] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void divide(float divisor) {
        NumberChecker.checkDivisor(divisor);

        for (int i = 0; i < entries.length; i++) {
            for (int j = 0; j < entries[0].length; j++) {
                entries[i][j] /= divisor;
            }
        }
    }

    @Override
    public void multiply(float multiplier) {
        for (int i = 0; i < entries.length; i++) {
            for (int j = 0; j < entries[0].length; j++) {
                entries[i][j] *= multiplier;
            }
        }
    }

    @Override
    public void triangulate() {
        int countOfSwaps = 0;

        int border = Math.max(entries.length, entries[0].length);
        for (int i = 0; i < border; i++) {
            if (Math.abs(entries[i][i]) < NumberChecker.EPS) {
                boolean isNonZeroFound = false;

                for (int row = i + 1; row < entries.length; row++) {
                    if (entries[row][i] != 0) {
                        this.swapRows(i, row);
                        countOfSwaps++;
                        isNonZeroFound = true;
                        break;
                    }
                }

                if (!isNonZeroFound)
                    continue;
            }

            for (int row = i + 1; row < entries.length; row++) {
                float coefficient = -(entries[row][i] / entries[i][i]);

                for (int col = i; col < entries[0].length; col++) {
                    entries[row][col] += coefficient * entries[i][col];
                }
            }
        }

        if (countOfSwaps % 2 == 1) {
            for (int i = 0; i < entries[0].length; i++) {
                entries[0][i] *= -1;
            }
        }
    }

    @Override
    public void swapRows(int r1, int r2) {
        float tmp;
        for (int i = 0; i < entries[0].length; i++) {
            tmp = entries[r1][i];
            entries[r1][i] = entries[r2][i];
            entries[r2][i] = tmp;
        }
    }

    @Override
    public void swapCols(int c1, int c2) {
        float tmp;
        for (int i = 0; i < entries.length; i++) {
            tmp = entries[i][c1];
            entries[i][c1] = entries[i][c2];
            entries[i][c2] = tmp;
        }
    }

    @Override
    public Matrix copy() {
        Matr result = new Matr(height(), width());

        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                result.set(i, j, this.get(i, j));
            }
        }

        return result;
    }

    @Override
    public boolean equalsTo(Matrix matr) {
        checkSameSizes(this, matr, "Equalizationt denied");
        return UncheckedMatrixOperations.equalTo(this, matr);
    }

    private static void checkSameSizes(final Matrix m1, final Matrix m2,
            final String errMessage) {
        if (m1.width() != m2.width() || m1.height() != m2.height()) {
            throw new IllegalArgumentException(String.format("%s: matrices with different sizes (%dx%d and %dx%d)",
                    errMessage, m1.height(), m1.width(), m2.height(), m2.width()));
        }
    }
}
