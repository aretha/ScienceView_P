/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Projection Explorer (PEx), based on the code presented 
 * in:
 * 
 * http://www.cs.uml.edu/~haim/ColorCenter/Programs/ColorScales/HeatedObject.java
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
public class HeatedObjectScale extends ColorScale {

    /** Creates a new instance of HeatedObjectCS */
    public HeatedObjectScale() {
        colors = new Color[256];
        colors[  0] = new Color(0, 0, 0);
        colors[  1] = new Color(35, 0, 0);
        colors[  2] = new Color(52, 0, 0);
        colors[  3] = new Color(60, 0, 0);
        colors[  4] = new Color(63, 1, 0);
        colors[  5] = new Color(64, 2, 0);
        colors[  6] = new Color(68, 5, 0);
        colors[  7] = new Color(69, 6, 0);
        colors[  8] = new Color(72, 8, 0);
        colors[  9] = new Color(74, 10, 0);
        colors[ 10] = new Color(77, 12, 0);
        colors[ 11] = new Color(78, 14, 0);
        colors[ 12] = new Color(81, 16, 0);
        colors[ 13] = new Color(83, 17, 0);
        colors[ 14] = new Color(85, 19, 0);
        colors[ 15] = new Color(86, 20, 0);
        colors[ 16] = new Color(89, 22, 0);
        colors[ 17] = new Color(91, 24, 0);
        colors[ 18] = new Color(92, 25, 0);
        colors[ 19] = new Color(94, 26, 0);
        colors[ 20] = new Color(95, 28, 0);
        colors[ 21] = new Color(98, 30, 0);
        colors[ 22] = new Color(100, 31, 0);
        colors[ 23] = new Color(102, 33, 0);
        colors[ 24] = new Color(103, 34, 0);
        colors[ 25] = new Color(105, 35, 0);
        colors[ 26] = new Color(106, 36, 0);
        colors[ 27] = new Color(108, 38, 0);
        colors[ 28] = new Color(109, 39, 0);
        colors[ 29] = new Color(111, 40, 0);
        colors[ 30] = new Color(112, 42, 0);
        colors[ 31] = new Color(114, 43, 0);
        colors[ 32] = new Color(115, 44, 0);
        colors[ 33] = new Color(117, 45, 0);
        colors[ 34] = new Color(119, 47, 0);
        colors[ 35] = new Color(119, 47, 0);
        colors[ 36] = new Color(120, 48, 0);
        colors[ 37] = new Color(122, 49, 0);
        colors[ 38] = new Color(123, 51, 0);
        colors[ 39] = new Color(125, 52, 0);
        colors[ 40] = new Color(125, 52, 0);
        colors[ 41] = new Color(126, 53, 0);
        colors[ 42] = new Color(128, 54, 0);
        colors[ 43] = new Color(129, 56, 0);
        colors[ 44] = new Color(129, 56, 0);
        colors[ 45] = new Color(131, 57, 0);
        colors[ 46] = new Color(132, 58, 0);
        colors[ 47] = new Color(134, 59, 0);
        colors[ 48] = new Color(134, 59, 0);
        colors[ 49] = new Color(136, 61, 0);
        colors[ 50] = new Color(137, 62, 0);
        colors[ 51] = new Color(137, 62, 0);
        colors[ 52] = new Color(139, 63, 0);
        colors[ 53] = new Color(139, 63, 0);
        colors[ 54] = new Color(140, 65, 0);
        colors[ 55] = new Color(142, 66, 0);
        colors[ 56] = new Color(142, 66, 0);
        colors[ 57] = new Color(143, 67, 0);
        colors[ 58] = new Color(143, 67, 0);
        colors[ 59] = new Color(145, 68, 0);
        colors[ 60] = new Color(145, 68, 0);
        colors[ 61] = new Color(146, 70, 0);
        colors[ 62] = new Color(146, 70, 0);
        colors[ 63] = new Color(148, 71, 0);
        colors[ 64] = new Color(148, 71, 0);
        colors[ 65] = new Color(149, 72, 0);
        colors[ 66] = new Color(149, 72, 0);
        colors[ 67] = new Color(151, 73, 0);
        colors[ 68] = new Color(151, 73, 0);
        colors[ 69] = new Color(153, 75, 0);
        colors[ 70] = new Color(153, 75, 0);
        colors[ 71] = new Color(154, 76, 0);
        colors[ 72] = new Color(154, 76, 0);
        colors[ 73] = new Color(154, 76, 0);
        colors[ 74] = new Color(156, 77, 0);
        colors[ 75] = new Color(156, 77, 0);
        colors[ 76] = new Color(157, 79, 0);
        colors[ 77] = new Color(157, 79, 0);
        colors[ 78] = new Color(159, 80, 0);
        colors[ 79] = new Color(159, 80, 0);
        colors[ 80] = new Color(159, 80, 0);
        colors[ 81] = new Color(160, 81, 0);
        colors[ 82] = new Color(160, 81, 0);
        colors[ 83] = new Color(162, 82, 0);
        colors[ 84] = new Color(162, 82, 0);
        colors[ 85] = new Color(163, 84, 0);
        colors[ 86] = new Color(163, 84, 0);
        colors[ 87] = new Color(165, 85, 0);
        colors[ 88] = new Color(165, 85, 0);
        colors[ 89] = new Color(166, 86, 0);
        colors[ 90] = new Color(166, 86, 0);
        colors[ 91] = new Color(166, 86, 0);
        colors[ 92] = new Color(168, 87, 0);
        colors[ 93] = new Color(168, 87, 0);
        colors[ 94] = new Color(170, 89, 0);
        colors[ 95] = new Color(170, 89, 0);
        colors[ 96] = new Color(171, 90, 0);
        colors[ 97] = new Color(171, 90, 0);
        colors[ 98] = new Color(173, 91, 0);
        colors[ 99] = new Color(173, 91, 0);
        colors[100] = new Color(174, 93, 0);
        colors[101] = new Color(174, 93, 0);
        colors[102] = new Color(176, 94, 0);
        colors[103] = new Color(176, 94, 0);
        colors[104] = new Color(177, 95, 0);
        colors[105] = new Color(177, 95, 0);
        colors[106] = new Color(179, 96, 0);
        colors[107] = new Color(179, 96, 0);
        colors[108] = new Color(180, 98, 0);
        colors[109] = new Color(182, 99, 0);
        colors[110] = new Color(182, 99, 0);
        colors[111] = new Color(183, 100, 0);
        colors[112] = new Color(183, 100, 0);
        colors[113] = new Color(185, 102, 0);
        colors[114] = new Color(185, 102, 0);
        colors[115] = new Color(187, 103, 0);
        colors[116] = new Color(187, 103, 0);
        colors[117] = new Color(188, 104, 0);
        colors[118] = new Color(188, 104, 0);
        colors[119] = new Color(190, 105, 0);
        colors[120] = new Color(191, 107, 0);
        colors[121] = new Color(191, 107, 0);
        colors[122] = new Color(193, 108, 0);
        colors[123] = new Color(193, 108, 0);
        colors[124] = new Color(194, 109, 0);
        colors[125] = new Color(196, 110, 0);
        colors[126] = new Color(196, 110, 0);
        colors[127] = new Color(197, 112, 0);
        colors[128] = new Color(197, 112, 0);
        colors[129] = new Color(199, 113, 0);
        colors[130] = new Color(200, 114, 0);
        colors[131] = new Color(200, 114, 0);
        colors[132] = new Color(202, 116, 0);
        colors[133] = new Color(202, 116, 0);
        colors[134] = new Color(204, 117, 0);
        colors[135] = new Color(205, 118, 0);
        colors[136] = new Color(205, 118, 0);
        colors[137] = new Color(207, 119, 0);
        colors[138] = new Color(208, 121, 0);
        colors[139] = new Color(208, 121, 0);
        colors[140] = new Color(210, 122, 0);
        colors[141] = new Color(211, 123, 0);
        colors[142] = new Color(211, 123, 0);
        colors[143] = new Color(213, 124, 0);
        colors[144] = new Color(214, 126, 0);
        colors[145] = new Color(214, 126, 0);
        colors[146] = new Color(216, 127, 0);
        colors[147] = new Color(217, 128, 0);
        colors[148] = new Color(217, 128, 0);
        colors[149] = new Color(219, 130, 0);
        colors[150] = new Color(221, 131, 0);
        colors[151] = new Color(221, 131, 0);
        colors[152] = new Color(222, 132, 0);
        colors[153] = new Color(224, 133, 0);
        colors[154] = new Color(224, 133, 0);
        colors[155] = new Color(225, 135, 0);
        colors[156] = new Color(227, 136, 0);
        colors[157] = new Color(227, 136, 0);
        colors[158] = new Color(228, 137, 0);
        colors[159] = new Color(230, 138, 0);
        colors[160] = new Color(230, 138, 0);
        colors[161] = new Color(231, 140, 0);
        colors[162] = new Color(233, 141, 0);
        colors[163] = new Color(233, 141, 0);
        colors[164] = new Color(234, 142, 0);
        colors[165] = new Color(236, 144, 0);
        colors[166] = new Color(236, 144, 0);
        colors[167] = new Color(238, 145, 0);
        colors[168] = new Color(239, 146, 0);
        colors[169] = new Color(241, 147, 0);
        colors[170] = new Color(241, 147, 0);
        colors[171] = new Color(242, 149, 0);
        colors[172] = new Color(244, 150, 0);
        colors[173] = new Color(244, 150, 0);
        colors[174] = new Color(245, 151, 0);
        colors[175] = new Color(247, 153, 0);
        colors[176] = new Color(247, 153, 0);
        colors[177] = new Color(248, 154, 0);
        colors[178] = new Color(250, 155, 0);
        colors[179] = new Color(251, 156, 0);
        colors[180] = new Color(251, 156, 0);
        colors[181] = new Color(253, 158, 0);
        colors[182] = new Color(255, 159, 0);
        colors[183] = new Color(255, 159, 0);
        colors[184] = new Color(255, 160, 0);
        colors[185] = new Color(255, 161, 0);
        colors[186] = new Color(255, 163, 0);
        colors[187] = new Color(255, 163, 0);
        colors[188] = new Color(255, 164, 0);
        colors[189] = new Color(255, 165, 0);
        colors[190] = new Color(255, 167, 0);
        colors[191] = new Color(255, 167, 0);
        colors[192] = new Color(255, 168, 0);
        colors[193] = new Color(255, 169, 0);
        colors[194] = new Color(255, 169, 0);
        colors[195] = new Color(255, 170, 0);
        colors[196] = new Color(255, 172, 0);
        colors[197] = new Color(255, 173, 0);
        colors[198] = new Color(255, 173, 0);
        colors[199] = new Color(255, 174, 0);
        colors[200] = new Color(255, 175, 0);
        colors[201] = new Color(255, 177, 0);
        colors[202] = new Color(255, 178, 0);
        colors[203] = new Color(255, 179, 0);
        colors[204] = new Color(255, 181, 0);
        colors[205] = new Color(255, 181, 0);
        colors[206] = new Color(255, 182, 0);
        colors[207] = new Color(255, 183, 0);
        colors[208] = new Color(255, 184, 0);
        colors[209] = new Color(255, 187, 7);
        colors[210] = new Color(255, 188, 10);
        colors[211] = new Color(255, 189, 14);
        colors[212] = new Color(255, 191, 18);
        colors[213] = new Color(255, 192, 21);
        colors[214] = new Color(255, 193, 25);
        colors[215] = new Color(255, 195, 29);
        colors[216] = new Color(255, 197, 36);
        colors[217] = new Color(255, 198, 40);
        colors[218] = new Color(255, 200, 43);
        colors[219] = new Color(255, 202, 51);
        colors[220] = new Color(255, 204, 54);
        colors[221] = new Color(255, 206, 61);
        colors[222] = new Color(255, 207, 65);
        colors[223] = new Color(255, 210, 72);
        colors[224] = new Color(255, 211, 76);
        colors[225] = new Color(255, 214, 83);
        colors[226] = new Color(255, 216, 91);
        colors[227] = new Color(255, 219, 98);
        colors[228] = new Color(255, 221, 105);
        colors[229] = new Color(255, 223, 109);
        colors[230] = new Color(255, 225, 116);
        colors[231] = new Color(255, 228, 123);
        colors[232] = new Color(255, 232, 134);
        colors[233] = new Color(255, 234, 142);
        colors[234] = new Color(255, 237, 149);
        colors[235] = new Color(255, 239, 156);
        colors[236] = new Color(255, 240, 160);
        colors[237] = new Color(255, 243, 167);
        colors[238] = new Color(255, 246, 174);
        colors[239] = new Color(255, 248, 182);
        colors[240] = new Color(255, 249, 185);
        colors[241] = new Color(255, 252, 193);
        colors[242] = new Color(255, 253, 196);
        colors[243] = new Color(255, 255, 204);
        colors[244] = new Color(255, 255, 207);
        colors[245] = new Color(255, 255, 211);
        colors[246] = new Color(255, 255, 218);
        colors[247] = new Color(255, 255, 222);
        colors[248] = new Color(255, 255, 225);
        colors[249] = new Color(255, 255, 229);
        colors[250] = new Color(255, 255, 233);
        colors[251] = new Color(255, 255, 236);
        colors[252] = new Color(255, 255, 240);
        colors[253] = new Color(255, 255, 244);
        colors[254] = new Color(255, 255, 247);
        colors[255] = new Color(255, 255, 255);
    }

}
