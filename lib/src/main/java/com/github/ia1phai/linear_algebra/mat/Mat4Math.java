package com.github.ia1phai.linear_algebra.mat;

import com.github.ia1phai.linear_algebra.Validator;
import com.github.ia1phai.linear_algebra.vec.Vec4;
import com.github.ia1phai.linear_algebra.vec.Vector4;

import static com.github.ia1phai.linear_algebra.mat.Matrix4Row.*;

/**
 * UncheckedMatrixOperations
 */
public class Mat4Math {

    public static final Matrix4Row[] ROWS = Matrix4Row.values();
    public static final Matrix4Col[] COLS = Matrix4Col.values();

    public static Matrix4 transpose(final Matrix4 m) {
        float tmp;
        for (int i = 0; i < m.width(); i++) {
            for (int j = i + 1; j < m.width(); j++) {
                tmp = m.get(ROWS[i], COLS[j]);
                m.set(ROWS[i], COLS[j], m.get(ROWS[j], COLS[i]));
                m.set(ROWS[j], COLS[i], tmp);
            }
        }

        return m;
    }

    public static Matrix4 transposed(final Matrix4 m) {
        return transpose(new Mat4(m));
    }

    public static Matrix4 swapRows(final Matrix4 m, final Matrix4Row r1, final Matrix4Row r2) {
        float tmp;
        for (final Matrix4Col c : COLS) {
            tmp = m.get(r1, c);
            m.set(r1, c, m.get(r2, c));
            m.set(r2, c, tmp);
        }

        return m;
    }

    public static Matrix4 swapRows(final Matrix4 m, final int r1, final int r2) {
        swapRows(m, ROWS[r1], ROWS[r2]);

        return m;
    }

    public static Matrix4 swappedRows(final Matrix4 m, final Matrix4Row r1, final Matrix4Row r2) {
        return swapRows(new Mat4(m), r1, r2);
    }

    public static Matrix4 swappedRows(final Matrix4 m, final int r1, final int r2) {
        return swapRows(new Mat4(m), r1, r2);
    }

    public static Matrix4 swapCols(final Matrix4 m, final Matrix4Col c1, final Matrix4Col c2) {
        float tmp;
        for (final Matrix4Row r : ROWS) {
            tmp = m.get(r, c1);
            m.set(r, c1, m.get(r, c2));
            m.set(r, c2, tmp);
        }

        return m;
    }

    public static Matrix4 swapCols(final Matrix4 m, final int c1, final int c2) {
        return swapCols(m, COLS[c1], COLS[c2]);
    }

    public static Matrix4 swappedCols(final Matrix4 m, final Matrix4Col c1, final Matrix4Col c2) {
        return swapCols(new Mat4(m), c1, c2);
    }

    public static Matrix4 swappedCols(final Matrix4 m, final int c1, final int c2) {
        return swapCols(new Mat4(m), c1, c2);
    }

    public static Matrix4 mult(final Matrix4 m, final float multiplier) {
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                m.set(r, c, m.get(r, c) * multiplier);
            }
        }

        return m;
    }

    public static Matrix4 multiplied(final Matrix4 m, final float multiplier) {
        return mult(new Mat4(m), multiplier);
    }

    public static Matrix4 divide(final Matrix4 m, final float divisor) {
        Validator.validateDivisor(divisor);
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                m.set(r, c, m.get(r, c) / divisor);
            }
        }

        return m;
    }

    public static Matrix4 divided(final Matrix4 m, final float divisor) {
        return divide(new Mat4(m), divisor);
    }

    public static Matrix4 add(final Matrix4 target, final Matrix4 addendum) {
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                target.set(r, c, target.get(r, c) + addendum.get(r, c));
            }
        }

        return target;
    }

    public static Matrix4 added(final Matrix4 target, final Matrix4 addendum) {
        return add(new Mat4(target), addendum);
    }

    public static Matrix4 sub(final Matrix4 target, final Matrix4 subtrahend) {
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                target.set(r, c, target.get(r, c) - subtrahend.get(r, c));
            }
        }

        return target;
    }

    public static Matrix4 subtracted(final Matrix4 target, final Matrix4 subtrahend) {
        return sub(new Mat4(target), subtrahend);
    }

    public static Matrix4 prod(final Matrix4 m1, final Matrix4 m2) {
        final Matrix4 result = new Mat4();
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                float value = 0;
                for (int i = 0; i < m1.width(); i++) {
                    value += m1.get(r, COLS[i]) * m2.get(ROWS[i], c);
                }
                result.set(r, c, value);
            }
        }

        return result;
    }

    public static Vector4 prod(final Matrix4 m, final Vector4 v) {
        final Vector4 result = new Vec4();
        for (int i = 0; i < m.height(); i++) {
            float value = 0;
            for (int elem = 0; elem < v.size(); elem++) {
                value += m.get(ROWS[i], COLS[elem]) * v.get(elem);
            }

            result.set(i, value);
        }

        return result;
    }

    public static Matrix4 triangulate(final Matrix4 m) {
        int countOfSwaps = 0;

        for (int i = 0; i < m.width(); i++) {
            if (Validator.equals(m.get(ROWS[i], COLS[i]), 0)) {
                boolean isNonZeroFound = false;

                for (int r = i + 1; r < m.height(); r++) {
                    if (!Validator.equals(m.get(ROWS[r], COLS[i]), 0)) {
                        swapRows(m, ROWS[i], ROWS[r]);
                        countOfSwaps++;
                        isNonZeroFound = true;
                        break;
                    }
                }

                if (!isNonZeroFound)
                    continue;
            }

            for (int r = i + 1; r < m.height(); r++) {
                final float coefficient = -(m.get(ROWS[r], COLS[i]) / m.get(ROWS[i], COLS[i]));

                for (int c = i; c < m.width(); c++) {
                    m.set(
                            ROWS[r],
                            COLS[c],
                            m.get(ROWS[r], COLS[c])
                                    + coefficient * m.get(ROWS[i], COLS[c]));
                }
            }
        }

        if (countOfSwaps % 2 == 1) {
            for (int i = 0; i < m.width(); i++) {
                m.set(R0, COLS[i], m.get(R0, COLS[i]) * -1);
            }
        }

        return m;
    }

    public static Matrix4 triangulated(final Matrix4 m) {
        return triangulate(new Mat4(m));
    }

    public static float det(final Matrix4 m) {
        float determinant = 0;
        for (final Matrix4Col c : Matrix4Col.values()) {
            determinant += m.get(R0, c) * cofactor(m, R0, c);
        }
        return determinant;
    }

    public static Matrix4 invertible(final Matrix4 m) {
        final Matrix4 result = cofactorMatrix(m);
        final float determinant = det(m);

        if (determinant == 0) {
            throw new RuntimeException("Invertible matrix does not exitst: determinant is 0");
        }
        transpose(result);
        mult(result, 1 / determinant);
        return result;
    }

    public static Matrix3 minorMatrix(final Matrix4 m, final Matrix4Row r, final Matrix4Col c) {
        final Matrix3 result = new Mat3();
        int destRow = 0;
        int destCol = 0;
        for (int i = 0; i < m.width(); i++) {
            if (i == r.ordinal()) {
                continue;
            }
            for (int j = 0; j < m.width(); j++) {
                if (j == c.ordinal()) {
                    continue;
                }

                result.set(
                        Matrix3Row.values()[destRow],
                        Matrix3Col.values()[destCol],
                        m.get(ROWS[i], COLS[j]));
                destCol++;
            }
            destCol = 0;
            destRow++;
        }

        return result;
    }

    public static Matrix3 minorMatrix(final Matrix4 m, final int r, final int c) {
        return minorMatrix(m, ROWS[r], COLS[c]);
    }

    public static float cofactor(final Matrix4 m, final Matrix4Row r, final Matrix4Col c) {
        final int coefficient = (r.ordinal() + c.ordinal()) % 2 == 0 ? 1 : -1;
        return coefficient * Mat3Math.det(minorMatrix(m, r, c));
    }

    public static float cofactor(final Matrix4 m, final int r, final int c) {
        return cofactor(m, ROWS[r], COLS[c]);
    }

    public static Matrix4 cofactorMatrix(final Matrix4 m) {
        final Matrix4 result = new Mat4();
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                result.set(r, c, cofactor(m, r, c));
            }
        }

        return result;
    }

    public static boolean equals(final Matrix4 m1, final Matrix4 m2) {
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                if (!Validator.equals(m1.get(r, c), m2.get(r, c))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean square(final Matrix4 m) {
        return true;
    }

    public static boolean isZeroed(final Matrix4 m) {
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                if (m.get(r, c) != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean diagonal(final Matrix4 m) {
        for (final Matrix4Row r : ROWS) {
            for (final Matrix4Col c : COLS) {
                if (r.ordinal() == c.ordinal()) {
                    continue;
                }
                if (!Validator.equals(m.get(r, c), 0)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static Matrix4 zeroMat() {
        return new Mat4();
    }

    public static Matrix4 unitMat() {
        final Matrix4 result = new Mat4();
        for (int i = 0; i < result.width(); i++) {
            result.set(ROWS[i], COLS[i], 1);
        }

        return result;
    }
}
