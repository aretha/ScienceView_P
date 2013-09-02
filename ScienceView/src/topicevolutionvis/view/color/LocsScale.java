/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Projection Explorer (PEx), based on the code presented 
 * in::
 * 
 * http://www.cs.uml.edu/~haim/ColorCenter/Programs/ColorScales/LOCS.java
 *
 * How to cite this work:
 *  
@inproceedings{paulovich2007pex,
author = {Fernando V. Paulovich and Maria Cristina F. Oliveira and Rosane 
Minghim},
title = {The Projection Explorer: A Flexible Tool for Projection-based 
Multidimensional Visualization},
booktitle = {SIBGRAPI '07: Proceedings of the XX Brazilian Symposium on 
Computer Graphics and Image Processing (SIBGRAPI 2007)},
year = {2007},
isbn = {0-7695-2996-8},
pages = {27--34},
doi = {http://dx.doi.org/10.1109/SIBGRAPI.2007.39},
publisher = {IEEE Computer Society},
address = {Washington, DC, USA},
}
 *  
 * PEx is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * PEx is distributed in the hope that it will be useful, but WITHOUT 
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
 * with PEx. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */

package topicevolutionvis.view.color;

import java.awt.Color;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class LocsScale extends ColorScale {

    /** Creates a new instance of LocsCS */
    public LocsScale() {
        colors = new Color[256];
        colors[  0] = new Color(0, 0, 0);
        colors[  1] = new Color(0, 0, 0);
        colors[  2] = new Color(0, 0, 0);
        colors[  3] = new Color(1, 0, 0);
        colors[  4] = new Color(2, 0, 0);
        colors[  5] = new Color(2, 0, 0);
        colors[  6] = new Color(3, 0, 0);
        colors[  7] = new Color(3, 0, 0);
        colors[  8] = new Color(4, 0, 0);
        colors[  9] = new Color(5, 0, 0);
        colors[ 10] = new Color(5, 0, 0);
        colors[ 11] = new Color(6, 0, 0);
        colors[ 12] = new Color(7, 0, 0);
        colors[ 13] = new Color(7, 0, 0);
        colors[ 14] = new Color(8, 0, 0);
        colors[ 15] = new Color(9, 0, 0);
        colors[ 16] = new Color(9, 0, 0);
        colors[ 17] = new Color(10, 0, 0);
        colors[ 18] = new Color(11, 0, 0);
        colors[ 19] = new Color(12, 0, 0);
        colors[ 20] = new Color(13, 0, 0);
        colors[ 21] = new Color(14, 0, 0);
        colors[ 22] = new Color(15, 0, 0);
        colors[ 23] = new Color(16, 0, 0);
        colors[ 24] = new Color(17, 0, 0);
        colors[ 25] = new Color(18, 0, 0);
        colors[ 26] = new Color(19, 0, 0);
        colors[ 27] = new Color(20, 0, 0);
        colors[ 28] = new Color(21, 0, 0);
        colors[ 29] = new Color(22, 0, 0);
        colors[ 30] = new Color(23, 0, 0);
        colors[ 31] = new Color(25, 0, 0);
        colors[ 32] = new Color(26, 0, 0);
        colors[ 33] = new Color(27, 0, 0);
        colors[ 34] = new Color(28, 0, 0);
        colors[ 35] = new Color(30, 0, 0);
        colors[ 36] = new Color(31, 0, 0);
        colors[ 37] = new Color(33, 0, 0);
        colors[ 38] = new Color(34, 0, 0);
        colors[ 39] = new Color(35, 0, 0);
        colors[ 40] = new Color(37, 0, 0);
        colors[ 41] = new Color(39, 0, 0);
        colors[ 42] = new Color(40, 0, 0);
        colors[ 43] = new Color(43, 0, 0);
        colors[ 44] = new Color(45, 0, 0);
        colors[ 45] = new Color(46, 0, 0);
        colors[ 46] = new Color(49, 0, 0);
        colors[ 47] = new Color(51, 0, 0);
        colors[ 48] = new Color(53, 0, 0);
        colors[ 49] = new Color(54, 0, 0);
        colors[ 50] = new Color(56, 0, 0);
        colors[ 51] = new Color(58, 0, 0);
        colors[ 52] = new Color(60, 0, 0);
        colors[ 53] = new Color(62, 0, 0);
        colors[ 54] = new Color(64, 0, 0);
        colors[ 55] = new Color(67, 0, 0);
        colors[ 56] = new Color(69, 0, 0);
        colors[ 57] = new Color(71, 0, 0);
        colors[ 58] = new Color(74, 0, 0);
        colors[ 59] = new Color(76, 0, 0);
        colors[ 60] = new Color(80, 0, 0);
        colors[ 61] = new Color(81, 0, 0);
        colors[ 62] = new Color(84, 0, 0);
        colors[ 63] = new Color(86, 0, 0);
        colors[ 64] = new Color(89, 0, 0);
        colors[ 65] = new Color(92, 0, 0);
        colors[ 66] = new Color(94, 0, 0);
        colors[ 67] = new Color(97, 0, 0);
        colors[ 68] = new Color(100, 0, 0);
        colors[ 69] = new Color(103, 0, 0);
        colors[ 70] = new Color(106, 0, 0);
        colors[ 71] = new Color(109, 0, 0);
        colors[ 72] = new Color(112, 0, 0);
        colors[ 73] = new Color(115, 0, 0);
        colors[ 74] = new Color(117, 0, 0);
        colors[ 75] = new Color(122, 0, 0);
        colors[ 76] = new Color(126, 0, 0);
        colors[ 77] = new Color(128, 0, 0);
        colors[ 78] = new Color(131, 0, 0);
        colors[ 79] = new Color(135, 0, 0);
        colors[ 80] = new Color(135, 0, 0);
        colors[ 81] = new Color(135, 1, 0);
        colors[ 82] = new Color(135, 2, 0);
        colors[ 83] = new Color(135, 3, 0);
        colors[ 84] = new Color(135, 4, 0);
        colors[ 85] = new Color(135, 6, 0);
        colors[ 86] = new Color(135, 6, 0);
        colors[ 87] = new Color(135, 8, 0);
        colors[ 88] = new Color(135, 9, 0);
        colors[ 89] = new Color(135, 10, 0);
        colors[ 90] = new Color(135, 11, 0);
        colors[ 91] = new Color(135, 13, 0);
        colors[ 92] = new Color(135, 13, 0);
        colors[ 93] = new Color(135, 15, 0);
        colors[ 94] = new Color(135, 17, 0);
        colors[ 95] = new Color(135, 17, 0);
        colors[ 96] = new Color(135, 19, 0);
        colors[ 97] = new Color(135, 21, 0);
        colors[ 98] = new Color(135, 22, 0);
        colors[ 99] = new Color(135, 23, 0);
        colors[100] = new Color(135, 25, 0);
        colors[101] = new Color(135, 26, 0);
        colors[102] = new Color(135, 27, 0);
        colors[103] = new Color(135, 29, 0);
        colors[104] = new Color(135, 31, 0);
        colors[105] = new Color(135, 32, 0);
        colors[106] = new Color(135, 33, 0);
        colors[107] = new Color(135, 35, 0);
        colors[108] = new Color(135, 36, 0);
        colors[109] = new Color(135, 38, 0);
        colors[110] = new Color(135, 40, 0);
        colors[111] = new Color(135, 42, 0);
        colors[112] = new Color(135, 44, 0);
        colors[113] = new Color(135, 46, 0);
        colors[114] = new Color(135, 47, 0);
        colors[115] = new Color(135, 49, 0);
        colors[116] = new Color(135, 51, 0);
        colors[117] = new Color(135, 52, 0);
        colors[118] = new Color(135, 54, 0);
        colors[119] = new Color(135, 56, 0);
        colors[120] = new Color(135, 57, 0);
        colors[121] = new Color(135, 59, 0);
        colors[122] = new Color(135, 62, 0);
        colors[123] = new Color(135, 63, 0);
        colors[124] = new Color(135, 65, 0);
        colors[125] = new Color(135, 67, 0);
        colors[126] = new Color(135, 69, 0);
        colors[127] = new Color(135, 72, 0);
        colors[128] = new Color(135, 73, 0);
        colors[129] = new Color(135, 76, 0);
        colors[130] = new Color(135, 78, 0);
        colors[131] = new Color(135, 80, 0);
        colors[132] = new Color(135, 82, 0);
        colors[133] = new Color(135, 84, 0);
        colors[134] = new Color(135, 87, 0);
        colors[135] = new Color(135, 88, 0);
        colors[136] = new Color(135, 90, 0);
        colors[137] = new Color(135, 93, 0);
        colors[138] = new Color(135, 95, 0);
        colors[139] = new Color(135, 98, 0);
        colors[140] = new Color(135, 101, 0);
        colors[141] = new Color(135, 103, 0);
        colors[142] = new Color(135, 106, 0);
        colors[143] = new Color(135, 107, 0);
        colors[144] = new Color(135, 110, 0);
        colors[145] = new Color(135, 113, 0);
        colors[146] = new Color(135, 115, 0);
        colors[147] = new Color(135, 118, 0);
        colors[148] = new Color(135, 121, 0);
        colors[149] = new Color(135, 124, 0);
        colors[150] = new Color(135, 127, 0);
        colors[151] = new Color(135, 129, 0);
        colors[152] = new Color(135, 133, 0);
        colors[153] = new Color(135, 135, 0);
        colors[154] = new Color(135, 138, 0);
        colors[155] = new Color(135, 141, 0);
        colors[156] = new Color(135, 144, 0);
        colors[157] = new Color(135, 148, 0);
        colors[158] = new Color(135, 150, 0);
        colors[159] = new Color(135, 155, 0);
        colors[160] = new Color(135, 157, 0);
        colors[161] = new Color(135, 160, 0);
        colors[162] = new Color(135, 163, 0);
        colors[163] = new Color(135, 166, 0);
        colors[164] = new Color(135, 170, 0);
        colors[165] = new Color(135, 174, 0);
        colors[166] = new Color(135, 177, 0);
        colors[167] = new Color(135, 180, 0);
        colors[168] = new Color(135, 184, 0);
        colors[169] = new Color(135, 188, 0);
        colors[170] = new Color(135, 192, 0);
        colors[171] = new Color(135, 195, 0);
        colors[172] = new Color(135, 200, 0);
        colors[173] = new Color(135, 203, 0);
        colors[174] = new Color(135, 205, 0);
        colors[175] = new Color(135, 210, 0);
        colors[176] = new Color(135, 214, 0);
        colors[177] = new Color(135, 218, 0);
        colors[178] = new Color(135, 222, 0);
        colors[179] = new Color(135, 226, 0);
        colors[180] = new Color(135, 231, 0);
        colors[181] = new Color(135, 236, 0);
        colors[182] = new Color(135, 239, 0);
        colors[183] = new Color(135, 244, 0);
        colors[184] = new Color(135, 249, 0);
        colors[185] = new Color(135, 254, 0);
        colors[186] = new Color(135, 255, 1);
        colors[187] = new Color(135, 255, 5);
        colors[188] = new Color(135, 255, 10);
        colors[189] = new Color(135, 255, 15);
        colors[190] = new Color(135, 255, 20);
        colors[191] = new Color(135, 255, 23);
        colors[192] = new Color(135, 255, 28);
        colors[193] = new Color(135, 255, 33);
        colors[194] = new Color(135, 255, 38);
        colors[195] = new Color(135, 255, 43);
        colors[196] = new Color(135, 255, 45);
        colors[197] = new Color(135, 255, 49);
        colors[198] = new Color(135, 255, 54);
        colors[199] = new Color(135, 255, 59);
        colors[200] = new Color(135, 255, 65);
        colors[201] = new Color(135, 255, 70);
        colors[202] = new Color(135, 255, 74);
        colors[203] = new Color(135, 255, 80);
        colors[204] = new Color(135, 255, 84);
        colors[205] = new Color(135, 255, 90);
        colors[206] = new Color(135, 255, 95);
        colors[207] = new Color(135, 255, 98);
        colors[208] = new Color(135, 255, 104);
        colors[209] = new Color(135, 255, 110);
        colors[210] = new Color(135, 255, 116);
        colors[211] = new Color(135, 255, 120);
        colors[212] = new Color(135, 255, 125);
        colors[213] = new Color(135, 255, 131);
        colors[214] = new Color(135, 255, 137);
        colors[215] = new Color(135, 255, 144);
        colors[216] = new Color(135, 255, 149);
        colors[217] = new Color(135, 255, 154);
        colors[218] = new Color(135, 255, 158);
        colors[219] = new Color(135, 255, 165);
        colors[220] = new Color(135, 255, 172);
        colors[221] = new Color(135, 255, 179);
        colors[222] = new Color(135, 255, 186);
        colors[223] = new Color(135, 255, 191);
        colors[224] = new Color(135, 255, 198);
        colors[225] = new Color(135, 255, 203);
        colors[226] = new Color(135, 255, 211);
        colors[227] = new Color(135, 255, 216);
        colors[228] = new Color(135, 255, 224);
        colors[229] = new Color(135, 255, 232);
        colors[230] = new Color(135, 255, 240);
        colors[231] = new Color(135, 255, 248);
        colors[232] = new Color(135, 255, 254);
        colors[233] = new Color(135, 255, 255);
        colors[234] = new Color(140, 255, 255);
        colors[235] = new Color(146, 255, 255);
        colors[236] = new Color(153, 255, 255);
        colors[237] = new Color(156, 255, 255);
        colors[238] = new Color(161, 255, 255);
        colors[239] = new Color(168, 255, 255);
        colors[240] = new Color(172, 255, 255);
        colors[241] = new Color(177, 255, 255);
        colors[242] = new Color(182, 255, 255);
        colors[243] = new Color(189, 255, 255);
        colors[244] = new Color(192, 255, 255);
        colors[245] = new Color(199, 255, 255);
        colors[246] = new Color(204, 255, 255);
        colors[247] = new Color(210, 255, 255);
        colors[248] = new Color(215, 255, 255);
        colors[249] = new Color(220, 255, 255);
        colors[250] = new Color(225, 255, 255);
        colors[251] = new Color(232, 255, 255);
        colors[252] = new Color(236, 255, 255);
        colors[253] = new Color(240, 255, 255);
        colors[254] = new Color(248, 255, 255);
        colors[255] = new Color(255, 255, 255);
    }

}
