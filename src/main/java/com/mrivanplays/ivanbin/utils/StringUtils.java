/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.ivanbin.utils;

import java.util.concurrent.ThreadLocalRandom;

public class StringUtils
{

    private static final String data = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateRandomString(int length)
    {
        if (length < 5)
        {
            throw new IllegalArgumentException("Length should be at least 5 characters.");
        }

        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            int randomCharAt = ThreadLocalRandom.current().nextInt(0, data.length());
            char randomChar = data.charAt(randomCharAt);

            builder.append(randomChar);
        }

        return builder.toString();
    }

    public static boolean equals(String one, String two)
    {
        return applyReplacements(one).equalsIgnoreCase(applyReplacements(two));
    }

    private static String applyReplacements(String s)
    {
        return s.replace(" ", "").replace("\n", "");
    }
}
