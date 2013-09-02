/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Projection Explorer (PEx), based on the code presented 
 * in:
 * 
 * http://www.cs.uml.edu/~haim/ColorCenter/Programs/ColorScales/LinGray.java
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
public class LinearGrayScale extends ColorScale {

    /** Creates a new instance of LinGrayCS */
    public LinearGrayScale() {
        colors = new Color[256];
        colors[  0] = new Color(0, 0, 0);
        colors[  1] = new Color(0, 0, 0);
        colors[  2] = new Color(0, 0, 0);
        colors[  3] = new Color(0, 0, 0);
        colors[  4] = new Color(0, 0, 0);
        colors[  5] = new Color(0, 0, 0);
        colors[  6] = new Color(0, 0, 0);
        colors[  7] = new Color(1, 1, 1);
        colors[  8] = new Color(1, 1, 1);
        colors[  9] = new Color(1, 1, 1);
        colors[ 10] = new Color(1, 1, 1);
        colors[ 11] = new Color(1, 1, 1);
        colors[ 12] = new Color(1, 1, 1);
        colors[ 13] = new Color(1, 1, 1);
        colors[ 14] = new Color(1, 1, 1);
        colors[ 15] = new Color(1, 1, 1);
        colors[ 16] = new Color(2, 2, 2);
        colors[ 17] = new Color(2, 2, 2);
        colors[ 18] = new Color(2, 2, 2);
        colors[ 19] = new Color(2, 2, 2);
        colors[ 20] = new Color(2, 2, 2);
        colors[ 21] = new Color(2, 2, 2);
        colors[ 22] = new Color(2, 2, 2);
        colors[ 23] = new Color(3, 3, 3);
        colors[ 24] = new Color(3, 3, 3);
        colors[ 25] = new Color(3, 3, 3);
        colors[ 26] = new Color(3, 3, 3);
        colors[ 27] = new Color(3, 3, 3);
        colors[ 28] = new Color(3, 3, 3);
        colors[ 29] = new Color(3, 3, 3);
        colors[ 30] = new Color(4, 4, 4);
        colors[ 31] = new Color(4, 4, 4);
        colors[ 32] = new Color(4, 4, 4);
        colors[ 33] = new Color(4, 4, 4);
        colors[ 34] = new Color(4, 4, 4);
        colors[ 35] = new Color(5, 5, 5);
        colors[ 36] = new Color(5, 5, 5);
        colors[ 37] = new Color(5, 5, 5);
        colors[ 38] = new Color(5, 5, 5);
        colors[ 39] = new Color(5, 5, 5);
        colors[ 40] = new Color(6, 6, 6);
        colors[ 41] = new Color(6, 6, 6);
        colors[ 42] = new Color(6, 6, 6);
        colors[ 43] = new Color(6, 6, 6);
        colors[ 44] = new Color(6, 6, 6);
        colors[ 45] = new Color(7, 7, 7);
        colors[ 46] = new Color(7, 7, 7);
        colors[ 47] = new Color(7, 7, 7);
        colors[ 48] = new Color(7, 7, 7);
        colors[ 49] = new Color(7, 7, 7);
        colors[ 50] = new Color(8, 8, 8);
        colors[ 51] = new Color(8, 8, 8);
        colors[ 52] = new Color(9, 9, 9);
        colors[ 53] = new Color(9, 9, 9);
        colors[ 54] = new Color(9, 9, 9);
        colors[ 55] = new Color(9, 9, 9);
        colors[ 56] = new Color(10, 10, 10);
        colors[ 57] = new Color(10, 10, 10);
        colors[ 58] = new Color(10, 10, 10);
        colors[ 59] = new Color(10, 10, 10);
        colors[ 60] = new Color(10, 10, 10);
        colors[ 61] = new Color(11, 11, 11);
        colors[ 62] = new Color(11, 11, 11);
        colors[ 63] = new Color(12, 12, 12);
        colors[ 64] = new Color(12, 12, 12);
        colors[ 65] = new Color(12, 12, 12);
        colors[ 66] = new Color(13, 13, 13);
        colors[ 67] = new Color(13, 13, 13);
        colors[ 68] = new Color(14, 14, 14);
        colors[ 69] = new Color(14, 14, 14);
        colors[ 70] = new Color(15, 15, 15);
        colors[ 71] = new Color(15, 15, 15);
        colors[ 72] = new Color(15, 15, 15);
        colors[ 73] = new Color(16, 16, 16);
        colors[ 74] = new Color(16, 16, 16);
        colors[ 75] = new Color(17, 17, 17);
        colors[ 76] = new Color(17, 17, 17);
        colors[ 77] = new Color(18, 18, 18);
        colors[ 78] = new Color(18, 18, 18);
        colors[ 79] = new Color(19, 19, 19);
        colors[ 80] = new Color(19, 19, 19);
        colors[ 81] = new Color(19, 19, 19);
        colors[ 82] = new Color(19, 19, 19);
        colors[ 83] = new Color(19, 19, 19);
        colors[ 84] = new Color(20, 20, 20);
        colors[ 85] = new Color(20, 20, 20);
        colors[ 86] = new Color(22, 22, 22);
        colors[ 87] = new Color(22, 22, 22);
        colors[ 88] = new Color(22, 22, 22);
        colors[ 89] = new Color(23, 23, 23);
        colors[ 90] = new Color(23, 23, 23);
        colors[ 91] = new Color(24, 24, 24);
        colors[ 92] = new Color(24, 24, 24);
        colors[ 93] = new Color(26, 26, 26);
        colors[ 94] = new Color(26, 26, 26);
        colors[ 95] = new Color(26, 26, 26);
        colors[ 96] = new Color(27, 27, 27);
        colors[ 97] = new Color(27, 27, 27);
        colors[ 98] = new Color(29, 29, 29);
        colors[ 99] = new Color(29, 29, 29);
        colors[100] = new Color(30, 30, 30);
        colors[101] = new Color(30, 30, 30);
        colors[102] = new Color(32, 32, 32);
        colors[103] = new Color(32, 32, 32);
        colors[104] = new Color(32, 32, 32);
        colors[105] = new Color(32, 32, 32);
        colors[106] = new Color(32, 32, 32);
        colors[107] = new Color(34, 34, 34);
        colors[108] = new Color(34, 34, 34);
        colors[109] = new Color(35, 35, 35);
        colors[110] = new Color(35, 35, 35);
        colors[111] = new Color(35, 35, 35);
        colors[112] = new Color(37, 37, 37);
        colors[113] = new Color(37, 37, 37);
        colors[114] = new Color(39, 39, 39);
        colors[115] = new Color(39, 39, 39);
        colors[116] = new Color(41, 41, 41);
        colors[117] = new Color(41, 41, 41);
        colors[118] = new Color(41, 41, 41);
        colors[119] = new Color(43, 43, 43);
        colors[120] = new Color(43, 43, 43);
        colors[121] = new Color(45, 45, 45);
        colors[122] = new Color(45, 45, 45);
        colors[123] = new Color(46, 46, 46);
        colors[124] = new Color(46, 46, 46);
        colors[125] = new Color(46, 46, 46);
        colors[126] = new Color(47, 47, 47);
        colors[127] = new Color(47, 47, 47);
        colors[128] = new Color(49, 49, 49);
        colors[129] = new Color(49, 49, 49);
        colors[130] = new Color(51, 51, 51);
        colors[131] = new Color(51, 51, 51);
        colors[132] = new Color(52, 52, 52);
        colors[133] = new Color(52, 52, 52);
        colors[134] = new Color(52, 52, 52);
        colors[135] = new Color(54, 54, 54);
        colors[136] = new Color(54, 54, 54);
        colors[137] = new Color(56, 56, 56);
        colors[138] = new Color(56, 56, 56);
        colors[139] = new Color(59, 59, 59);
        colors[140] = new Color(59, 59, 59);
        colors[141] = new Color(59, 59, 59);
        colors[142] = new Color(61, 61, 61);
        colors[143] = new Color(61, 61, 61);
        colors[144] = new Color(64, 64, 64);
        colors[145] = new Color(64, 64, 64);
        colors[146] = new Color(67, 67, 67);
        colors[147] = new Color(67, 67, 67);
        colors[148] = new Color(67, 67, 67);
        colors[149] = new Color(69, 69, 69);
        colors[150] = new Color(69, 69, 69);
        colors[151] = new Color(72, 72, 72);
        colors[152] = new Color(72, 72, 72);
        colors[153] = new Color(75, 75, 75);
        colors[154] = new Color(75, 75, 75);
        colors[155] = new Color(76, 76, 76);
        colors[156] = new Color(76, 76, 76);
        colors[157] = new Color(76, 76, 76);
        colors[158] = new Color(78, 78, 78);
        colors[159] = new Color(78, 78, 78);
        colors[160] = new Color(81, 81, 81);
        colors[161] = new Color(81, 81, 81);
        colors[162] = new Color(84, 84, 84);
        colors[163] = new Color(84, 84, 84);
        colors[164] = new Color(84, 84, 84);
        colors[165] = new Color(87, 87, 87);
        colors[166] = new Color(87, 87, 87);
        colors[167] = new Color(91, 91, 91);
        colors[168] = new Color(91, 91, 91);
        colors[169] = new Color(94, 94, 94);
        colors[170] = new Color(94, 94, 94);
        colors[171] = new Color(94, 94, 94);
        colors[172] = new Color(97, 97, 97);
        colors[173] = new Color(97, 97, 97);
        colors[174] = new Color(101, 101, 101);
        colors[175] = new Color(101, 101, 101);
        colors[176] = new Color(104, 104, 104);
        colors[177] = new Color(104, 104, 104);
        colors[178] = new Color(107, 107, 107);
        colors[179] = new Color(107, 107, 107);
        colors[180] = new Color(107, 107, 107);
        colors[181] = new Color(108, 108, 108);
        colors[182] = new Color(108, 108, 108);
        colors[183] = new Color(112, 112, 112);
        colors[184] = new Color(112, 112, 112);
        colors[185] = new Color(116, 116, 116);
        colors[186] = new Color(116, 116, 116);
        colors[187] = new Color(116, 116, 116);
        colors[188] = new Color(120, 120, 120);
        colors[189] = new Color(120, 120, 120);
        colors[190] = new Color(124, 124, 124);
        colors[191] = new Color(124, 124, 124);
        colors[192] = new Color(128, 128, 128);
        colors[193] = new Color(128, 128, 128);
        colors[194] = new Color(128, 128, 128);
        colors[195] = new Color(132, 132, 132);
        colors[196] = new Color(132, 132, 132);
        colors[197] = new Color(136, 136, 136);
        colors[198] = new Color(136, 136, 136);
        colors[199] = new Color(141, 141, 141);
        colors[200] = new Color(141, 141, 141);
        colors[201] = new Color(145, 145, 145);
        colors[202] = new Color(145, 145, 145);
        colors[203] = new Color(145, 145, 145);
        colors[204] = new Color(147, 147, 147);
        colors[205] = new Color(147, 147, 147);
        colors[206] = new Color(150, 150, 150);
        colors[207] = new Color(150, 150, 150);
        colors[208] = new Color(154, 154, 154);
        colors[209] = new Color(154, 154, 154);
        colors[210] = new Color(154, 154, 154);
        colors[211] = new Color(159, 159, 159);
        colors[212] = new Color(159, 159, 159);
        colors[213] = new Color(164, 164, 164);
        colors[214] = new Color(164, 164, 164);
        colors[215] = new Color(169, 169, 169);
        colors[216] = new Color(169, 169, 169);
        colors[217] = new Color(169, 169, 169);
        colors[218] = new Color(174, 174, 174);
        colors[219] = new Color(174, 174, 174);
        colors[220] = new Color(179, 179, 179);
        colors[221] = new Color(179, 179, 179);
        colors[222] = new Color(185, 185, 185);
        colors[223] = new Color(185, 185, 185);
        colors[224] = new Color(190, 190, 190);
        colors[225] = new Color(190, 190, 190);
        colors[226] = new Color(190, 190, 190);
        colors[227] = new Color(195, 195, 195);
        colors[228] = new Color(195, 195, 195);
        colors[229] = new Color(195, 195, 195);
        colors[230] = new Color(195, 195, 195);
        colors[231] = new Color(201, 201, 201);
        colors[232] = new Color(201, 201, 201);
        colors[233] = new Color(201, 201, 201);
        colors[234] = new Color(207, 207, 207);
        colors[235] = new Color(207, 207, 207);
        colors[236] = new Color(212, 212, 212);
        colors[237] = new Color(212, 212, 212);
        colors[238] = new Color(218, 218, 218);
        colors[239] = new Color(218, 218, 218);
        colors[240] = new Color(218, 218, 218);
        colors[241] = new Color(224, 224, 224);
        colors[242] = new Color(224, 224, 224);
        colors[243] = new Color(230, 230, 230);
        colors[244] = new Color(230, 230, 230);
        colors[245] = new Color(237, 237, 237);
        colors[246] = new Color(237, 237, 237);
        colors[247] = new Color(243, 243, 243);
        colors[248] = new Color(243, 243, 243);
        colors[249] = new Color(243, 243, 243);
        colors[250] = new Color(249, 249, 249);
        colors[251] = new Color(249, 249, 249);
        colors[252] = new Color(252, 252, 252);
        colors[253] = new Color(252, 252, 252);
        colors[254] = new Color(252, 252, 252);
        colors[255] = new Color(255, 255, 255);
    }
    
}
