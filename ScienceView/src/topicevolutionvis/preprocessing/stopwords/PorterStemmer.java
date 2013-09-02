/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Hierarchical Projection Explorer (H-PEx).
 *
 * H-PEx is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * H-PEx is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * This code was developed by members of Computer Graphics and Image
 * Processing Group (http://www.lcad.icmc.usp.br) at Instituto de Ciencias
 * Matematicas e de Computacao - ICMC - (http://www.icmc.usp.br) of 
 * Universidade de Sao Paulo, Sao Carlos/SP, Brazil. The initial developer 
 * of the original code is Fernando Vieira Paulovich <fpaulovich@gmail.com>.
 *
 * Contributor(s): Rosane Minghim <rminghim@icmc.usp.br>
 *
 * You should have received a copy of the GNU General Public License along 
 * with H-PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */

/**
 *
 * Porter stemmer in Java. The original paper is in
 *
 * Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14,
 * no. 3, pp 130-137,
 *
 * See also http://www.tartarus.org/~martin/PorterStemmer
 *
 * History:
 *
 * Release 1
 *
 * Bug 1 (reported by Gonzalo Parra 16/10/99) fixed as marked below.
 * The words 'aed', 'eed', 'oed' leave k at 'a' for step 3, and b[k-1]
 * is then out outside the bounds of b.
 *
 * Release 2
 *
 * Similarly,
 *
 * Bug 2 (reported by Steve Dyrdahl 22/2/00) fixed as marked below.
 * 'ion' by itself leaves j = -1 in the test for 'ion' in step 5, and
 * b[j] is then outside the bounds of b.
 *
 * Release 3
 *
 * Considerably revised 4/9/00 in the light of many helpful suggestions
 * from Brian Goetz of Quiotix Corporation (brian@quiotix.com).
 *
 * Release 4
 *
 */
package topicevolutionvis.preprocessing.stopwords;

import java.util.regex.Pattern;

/**
 * Stemmer, implementing the Porter Stemming Algorithm
 *
 * The Stemmer class transforms a word into its root form.  The input
 * word can be provided a character at time (by calling add()), or at once
 * by calling one of the various stem(something) methods.
 */
public class PorterStemmer {

    public static String stem(String str) {
        reset();
        String[] parts = Pattern.compile("[_\\W+]").split(str);
        for (int zz = 0; zz < parts.length; zz++) {
            char[] word = new char[parts[zz].length()];
            parts[zz].getChars(0, parts[zz].length(), word, 0);
            add(word, word.length);
            stem();
            parts[zz] = new String(b, 0, i_end);
            reset();
        }
        str = join(" ", parts);
        return str;
    } //stripAffixes    

    private static void reset() {
        b = new char[INC];
        i = 0;
        i_end = 0;
    }

    /**
     * Add a character to the word being stemmed.  When you are finished
     * adding characters, you can call stem(void) to stem the word.
     */
    public static void add(char ch) {
        if (i == b.length) {
            char[] new_b = new char[i + INC];
            System.arraycopy(b, 0, new_b, 0, i);
            b = new_b;
        }
        b[i++] = ch;
    }

    /** Adds wLen characters to the word being stemmed contained in a portion
     * of a char[] array. This is like repeated calls of add(char ch), but
     * faster.
     */
    public static void add(char[] w, int wLen) {
        if (i + wLen >= b.length) {
            char[] new_b = new char[i + wLen + INC];
            System.arraycopy(b, 0, new_b, 0, i);
            b = new_b;
        }
        for (int c = 0; c < wLen; c++) {
            b[i++] = w[c];
        }
    }

    /**
     * After a word has been stemmed, it can be retrieved by toString(),
     * or a reference to the internal buffer can be retrieved by getResultBuffer
     * and getResultLength (which is generally more efficient.)
     */
    @Override
    public String toString() {
        return new String(b, 0, i_end);
    }

    /**
     * Returns the length of the word resulting from the stemming process.
     */
    public int getResultLength() {
        return i_end;
    }

    /**
     * Returns a reference to a character buffer containing the results of
     * the stemming process.  You also need to consult getResultLength()
     * to determine the length of the result.
     */
    public char[] getResultBuffer() {
        return b;
    }

    /* cons(i) is true <=> b[i] is a consonant. */
    private static boolean cons(int i) {
        switch (b[i]) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return false;
            case 'y':
                return (i == 0) ? true : !cons(i - 1);
            default:
                return true;
        }
    }

    /* m() measures the number of consonant sequences between 0 and j. if c is
    a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
    presence,
    <c><v>       gives 0
    <c>vc<v>     gives 1
    <c>vcvc<v>   gives 2
    <c>vcvcvc<v> gives 3
    ....
     */
    private static int m() {
        int n = 0;
        int x = 0;
        while (true) {
            if (x > j) {
                return n;
            }
            if (!cons(x)) {
                break;
            }
            x++;
        }
        x++;
        while (true) {
            while (true) {
                if (x > j) {
                    return n;
                }
                if (cons(x)) {
                    break;
                }
                x++;
            }
            x++;
            n++;
            while (true) {
                if (x > j) {
                    return n;
                }
                if (!cons(x)) {
                    break;
                }
                x++;
            }
            x++;
        }
    }

    /* vowelinstem() is true <=> 0,...j contains a vowel */
    private static boolean vowelinstem() {
        int x;
        for (x = 0; x <= j; x++) {
            if (!cons(x)) {
                return true;
            }
        }
        return false;
    }

    /* floatc(j) is true <=> j,(j-1) contain a float consonant. */
    private static boolean floatc(int j) {
        if (j < 1) {
            return false;
        }
        if (b[j] != b[j - 1]) {
            return false;
        }
        return cons(j);
    }

    /* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
    and also if the second c is not w,x or y. this is used when trying to
    restore an e at the end of a short word. e.g.
    cav(e), lov(e), hop(e), crim(e), but
    snow, box, tray.
     */
    private static boolean cvc(int i) {
        if (i < 2 || !cons(i) || cons(i - 1) || !cons(i - 2)) {
            return false;
        }
        {
            int ch = b[i];
            if (ch == 'w' || ch == 'x' || ch == 'y') {
                return false;
            }
        }
        return true;
    }

    private static boolean ends(String s) {
        int l = s.length();
        int o = k - l + 1;
        if (o < 0) {
            return false;
        }
        for (int x = 0; x < l; x++) {
            if (b[o + x] != s.charAt(x)) {
                return false;
            }
        }
        j = k - l;
        return true;
    }

    /* setto(s) sets (j+1),...k to the characters in the string s, readjusting
    k. */
    private static void setto(String s) {
        int l = s.length();
        int o = j + 1;
        for (int x = 0; x < l; x++) {
            b[o + x] = s.charAt(x);
        }
        k = j + l;
    }

    /* r(s) is used further down. */
    private static void r(String s) {
        if (m() > 0) {
            setto(s);
        }
    }

    /* step1() gets rid of plurals and -ed or -ing. e.g.
    caresses  ->  caress
    ponies    ->  poni
    ties      ->  ti
    caress    ->  caress
    cats      ->  cat
    feed      ->  feed
    agreed    ->  agree
    disabled  ->  disable
    matting   ->  mat
    mating    ->  mate
    meeting   ->  meet
    milling   ->  mill
    messing   ->  mess
    meetings  ->  meet
     */
    private static void step1() {
        if (b[k] == 's') {
            if (ends("sses")) {
                k -= 2;
            } else if (ends("ies")) {
                setto("i");
            } else if (b[k - 1] != 's') {
                k--;
            }
        }
        if (ends("eed")) {
            if (m() > 0) {
                k--;
            }
        } else if ((ends("ed") || ends("ing")) && vowelinstem()) {
            k = j;
            if (ends("at")) {
                setto("ate");
            } else if (ends("bl")) {
                setto("ble");
            } else if (ends("iz")) {
                setto("ize");
            } else if (floatc(k)) {
                k--;
                {
                    int ch = b[k];
                    if (ch == 'l' || ch == 's' || ch == 'z') {
                        k++;
                    }
                }
            } else if (m() == 1 && cvc(k)) {
                setto("e");
            }
        }
    }

    /* step2() turns terminal y to i when there is another vowel in the stem. */
    private static void step2() {
        if (ends("y") && vowelinstem()) {
            b[k] = 'i';
        }
    }

    /* step3() maps float suffices to single ones. so -ization ( = -ize plus
    -ation) maps to -ize etc. note that the string before the suffix must give
    m() > 0. */
    private static void step3() {
        if (k == 0) {
            return;
        } /* For Bug 1 */
        switch (b[k - 1]) {
            case 'a':
                if (ends("ational")) {
                    r("ate");
                    break;
                }
                if (ends("tional")) {
                    r("tion");
                    break;
                }
                break;
            case 'c':
                if (ends("enci")) {
                    r("ence");
                    break;
                }
                if (ends("anci")) {
                    r("ance");
                    break;
                }
                break;
            case 'e':
                if (ends("izer")) {
                    r("ize");
                    break;
                }
                break;
            case 'l':
                if (ends("bli")) {
                    r("ble");
                    break;
                }
                if (ends("alli")) {
                    r("al");
                    break;
                }
                if (ends("entli")) {
                    r("ent");
                    break;
                }
                if (ends("eli")) {
                    r("e");
                    break;
                }
                if (ends("ousli")) {
                    r("ous");
                    break;
                }
                break;
            case 'o':
                if (ends("ization")) {
                    r("ize");
                    break;
                }
                if (ends("ation")) {
                    r("ate");
                    break;
                }
                if (ends("ator")) {
                    r("ate");
                    break;
                }
                break;
            case 's':
                if (ends("alism")) {
                    r("al");
                    break;
                }
                if (ends("iveness")) {
                    r("ive");
                    break;
                }
                if (ends("fulness")) {
                    r("ful");
                    break;
                }
                if (ends("ousness")) {
                    r("ous");
                    break;
                }
                break;
            case 't':
                if (ends("aliti")) {
                    r("al");
                    break;
                }
                if (ends("iviti")) {
                    r("ive");
                    break;
                }
                if (ends("biliti")) {
                    r("ble");
                    break;
                }
                break;
            case 'g':
                if (ends("logi")) {
                    r("log");
                    break;
                }
        }
    }

    /* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */
    private static void step4() {
        switch (b[k]) {
            case 'e':
                if (ends("icate")) {
                    r("ic");
                    break;
                }
                if (ends("ative")) {
                    r("");
                    break;
                }
                if (ends("alize")) {
                    r("al");
                    break;
                }
                break;
            case 'i':
                if (ends("iciti")) {
                    r("ic");
                    break;
                }
                break;
            case 'l':
                if (ends("ical")) {
                    r("ic");
                    break;
                }
                if (ends("ful")) {
                    r("");
                    break;
                }
                break;
            case 's':
                if (ends("ness")) {
                    r("");
                    break;
                }
                break;
        }
    }

    /* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */
    private static void step5() {
        if (k == 0) {
            return;
        } /* for Bug 1 */
        switch (b[k - 1]) {
            case 'a':
                if (ends("al")) {
                    break;
                }
                return;
            case 'c':
                if (ends("ance")) {
                    break;
                }
                if (ends("ence")) {
                    break;
                }
                return;
            case 'e':
                if (ends("er")) {
                    break;
                }
                return;
            case 'i':
                if (ends("ic")) {
                    break;
                }
                return;
            case 'l':
                if (ends("able")) {
                    break;
                }
                if (ends("ible")) {
                    break;
                }
                return;
            case 'n':
                if (ends("ant")) {
                    break;
                }
                if (ends("ement")) {
                    break;
                }
                if (ends("ment")) {
                    break;
                }
                /* element etc. not stripped before the m */
                if (ends("ent")) {
                    break;
                }
                return;
            case 'o':
                if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) {
                    break;
                }
                /* j >= 0 fixes Bug 2 */
                if (ends("ou")) {
                    break;
                }
                return;
            /* takes care of -ous */
            case 's':
                if (ends("ism")) {
                    break;
                }
                return;
            case 't':
                if (ends("ate")) {
                    break;
                }
                if (ends("iti")) {
                    break;
                }
                return;
            case 'u':
                if (ends("ous")) {
                    break;
                }
                return;
            case 'v':
                if (ends("ive")) {
                    break;
                }
                return;
            case 'z':
                if (ends("ize")) {
                    break;
                }
                return;
            default:
                return;
        }
        if (m() > 1) {
            k = j;
        }
    }

    /* step6() removes a final -e if m() > 1. */
    private static void step6() {
        j = k;
        if (b[k] == 'e') {
            int a = m();
            if (a > 1 || a == 1 && !cvc(k - 1)) {
                k--;
            }
        }
        if (b[k] == 'l' && floatc(k) && m() > 1) {
            k--;
        }
    }

    /** Stem the word placed into the Stemmer buffer through calls to add().
     * Returns true if the stemming process resulted in a word different
     * from the input.  You can retrieve the result with
     * getResultLength()/getResultBuffer() or toString().
     */
    public static void stem() {
        k = i - 1;
        if (k > 1) {
            step1();
            step2();
            step3();
            step4();
            step5();
            step6();
        }
        i_end = k + 1;
        i = 0;
    }

    /**joins the text in a string array with a given seperator*/
    private static String join(String seperator, String[] parts) {
        StringBuilder sb = new StringBuilder("");
        for (int x = 0; x < parts.length; x++) {
            String part = parts[x].trim();
            if (part.length() == 0) {
                continue;
            }
            sb.append(part);
            //if not the last part of text add the seperator in the text
            if (x != (parts.length - 1)) {
                sb.append(seperator);
            }
        }
        return sb.toString();
    }
    
    private static char[] b;
    private static int i, /* offset into b */  i_end, /* offset to end of stemmed word */  j,  k;
    private static final int INC = 50;
    /* unit of size whereby b is increased */
} //class

