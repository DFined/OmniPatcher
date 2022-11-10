package external;

/**
 * This file was not made by me and is reused and modified under the Gnu General Public License. Original license:
 *
 * CRC-32 forcer (Java)
 * <p>
 * Copyright (c) 2021 Project Nayuki
 * https://www.nayuki.io/page/forcing-a-files-crc-to-any-value
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program (see COPYING.txt).
 * If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

public class CRCHack {
    /*---- Main function ----*/

    // Public library function.
    public static void modifyFileCrc32(File file, File fileToCopy, long offset, boolean printStatus) throws IOException {
        RandomAccessFile tf = new RandomAccessFile(fileToCopy, "rws");
        var newCrc = getCrc32(tf);
        Objects.requireNonNull(file);
        if (offset < 0)
            throw new IllegalArgumentException("Negative file offset");

        try (RandomAccessFile raf = new RandomAccessFile(file, "rws")) {
            long length = raf.length();
            if (length < 4 || offset > length - 4)
                throw new IllegalArgumentException("Byte offset plus 4 exceeds file length");

            // Read entire file and calculate original CRC-32 value
            int crc = getCrc32(raf);
            if (printStatus)
                System.out.printf("Original CRC-32: %08X%n", Integer.reverse(crc));

            // Compute the change to make
            int delta = crc ^ newCrc;
            delta = (int) multiplyMod(reciprocalMod(powMod(2, (length - offset) * 8)), delta & 0xFFFFFFFFL);

            // Patch 4 bytes in the file
            raf.seek(offset);
            byte[] bytes4 = new byte[4];
            raf.readFully(bytes4);
            for (int i = 0; i < bytes4.length; i++)
                bytes4[i] ^= Integer.reverse(delta) >>> (i * 8);
            raf.seek(offset);
            raf.write(bytes4);
            if (printStatus)
                System.out.println("Computed and wrote patch");

            // Recheck entire file
            if (getCrc32(raf) != newCrc)
                throw new AssertionError("Failed to update CRC-32 to desired value");
            else if (printStatus)
                System.out.println("New CRC-32 successfully verified");
        }
    }


    /*---- Utilities ----*/

    private static final long POLYNOMIAL = 0x104C11DB7L;  // Generator polynomial. Do not modify, because there are many dependencies


    private static int getCrc32(RandomAccessFile raf) throws IOException {
        raf.seek(0);
        int crc = 0xFFFFFFFF;
        byte[] buffer = new byte[32 * 1024];
        while (true) {
            int n = raf.read(buffer);
            if (n == -1)
                return ~crc;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 8; j++) {
                    crc ^= (buffer[i] >>> j) << 31;
                    if (crc < 0)
                        crc = (crc << 1) ^ (int) POLYNOMIAL;
                    else
                        crc <<= 1;
                }
            }
        }
    }


    /*---- Polynomial arithmetic ----*/

    // Returns polynomial x multiplied by polynomial y modulo the generator polynomial.
    private static long multiplyMod(long x, long y) {
        // Russian peasant multiplication algorithm
        long z = 0;
        while (y != 0) {
            z ^= x * (y & 1);
            y >>>= 1;
            x <<= 1;
            if (((x >>> 32) & 1) != 0)
                x ^= POLYNOMIAL;
        }
        return z;
    }


    // Returns polynomial x to the power of natural number y modulo the generator polynomial.
    private static long powMod(long x, long y) {
        // Exponentiation by squaring
        long z = 1;
        while (y != 0) {
            if ((y & 1) != 0)
                z = multiplyMod(z, x);
            x = multiplyMod(x, x);
            y >>>= 1;
        }
        return z;
    }


    // Computes polynomial x divided by polynomial y, returning the quotient and remainder.
    private static long[] divideAndRemainder(long x, long y) {
        if (y == 0)
            throw new IllegalArgumentException("Division by zero");
        if (x == 0)
            return new long[]{0, 0};

        int ydeg = getDegree(y);
        long z = 0;
        for (int i = getDegree(x) - ydeg; i >= 0; i--) {
            if (((x >>> (i + ydeg)) & 1) != 0) {
                x ^= y << i;
                z |= 1 << i;
            }
        }
        return new long[]{z, x};
    }


    // Returns the reciprocal of polynomial x with respect to the generator polynomial.
    private static long reciprocalMod(long x) {
        // Based on a simplification of the extended Euclidean algorithm
        long y = x;
        x = POLYNOMIAL;
        long a = 0;
        long b = 1;
        while (y != 0) {
            long[] divRem = divideAndRemainder(x, y);
            long c = a ^ multiplyMod(divRem[0], b);
            x = y;
            y = divRem[1];
            a = b;
            b = c;
        }
        if (x == 1)
            return a;
        else
            throw new IllegalArgumentException("Reciprocal does not exist");
    }


    private static int getDegree(long x) {
        return 63 - Long.numberOfLeadingZeros(x);
    }

}
