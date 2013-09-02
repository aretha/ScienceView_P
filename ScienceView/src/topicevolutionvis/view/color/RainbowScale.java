/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Projection Explorer (PEx), based on the code presented 
 * in:
 *
 * http://www.cs.uml.edu/~haim/ColorCenter/Programs/ColorScales/Rainbow.java
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
public class RainbowScale extends ColorScale {

    /**
     * Creates a new instance of RainbowCS
     */
    public RainbowScale() {
        colors = new Color[256];
        colors[  0] = new Color(0, 0, 0);
        colors[  1] = new Color(45, 0, 36);
        colors[  2] = new Color(56, 0, 46);
        colors[  3] = new Color(60, 0, 49);
        colors[  4] = new Color(67, 0, 54);
        colors[  5] = new Color(70, 0, 59);
        colors[  6] = new Color(71, 0, 61);
        colors[  7] = new Color(75, 0, 68);
        colors[  8] = new Color(74, 0, 73);
        colors[  9] = new Color(74, 0, 77);
        colors[ 10] = new Color(73, 0, 81);
        colors[ 11] = new Color(71, 0, 87);
        colors[ 12] = new Color(69, 1, 90);
        colors[ 13] = new Color(68, 2, 94);
        colors[ 14] = new Color(66, 3, 97);
        colors[ 15] = new Color(63, 6, 102);
        colors[ 16] = new Color(61, 7, 106);
        colors[ 17] = new Color(58, 10, 109);
        colors[ 18] = new Color(56, 12, 113);
        colors[ 19] = new Color(53, 15, 116);
        colors[ 20] = new Color(48, 18, 119);
        colors[ 21] = new Color(47, 20, 121);
        colors[ 22] = new Color(44, 23, 124);
        colors[ 23] = new Color(41, 27, 128);
        colors[ 24] = new Color(40, 28, 129);
        colors[ 25] = new Color(37, 32, 132);
        colors[ 26] = new Color(34, 36, 134);
        colors[ 27] = new Color(29, 43, 137);
        colors[ 28] = new Color(25, 52, 138);
        colors[ 29] = new Color(24, 57, 139);
        colors[ 30] = new Color(24, 62, 141);
        colors[ 31] = new Color(24, 64, 142);
        colors[ 32] = new Color(23, 65, 142);
        colors[ 33] = new Color(23, 69, 143);
        colors[ 34] = new Color(23, 71, 142);
        colors[ 35] = new Color(23, 71, 142);
        colors[ 36] = new Color(23, 73, 142);
        colors[ 37] = new Color(23, 75, 142);
        colors[ 38] = new Color(23, 75, 142);
        colors[ 39] = new Color(23, 78, 142);
        colors[ 40] = new Color(23, 80, 142);
        colors[ 41] = new Color(23, 80, 142);
        colors[ 42] = new Color(23, 82, 141);
        colors[ 43] = new Color(23, 85, 141);
        colors[ 44] = new Color(23, 85, 141);
        colors[ 45] = new Color(23, 87, 140);
        colors[ 46] = new Color(23, 87, 140);
        colors[ 47] = new Color(24, 90, 140);
        colors[ 48] = new Color(24, 90, 140);
        colors[ 49] = new Color(24, 93, 139);
        colors[ 50] = new Color(24, 93, 139);
        colors[ 51] = new Color(24, 93, 139);
        colors[ 52] = new Color(24, 93, 139);
        colors[ 53] = new Color(24, 97, 139);
        colors[ 54] = new Color(24, 97, 139);
        colors[ 55] = new Color(25, 101, 138);
        colors[ 56] = new Color(25, 101, 138);
        colors[ 57] = new Color(25, 104, 137);
        colors[ 58] = new Color(25, 104, 137);
        colors[ 59] = new Color(25, 104, 137);
        colors[ 60] = new Color(26, 108, 137);
        colors[ 61] = new Color(26, 108, 137);
        colors[ 62] = new Color(27, 111, 136);
        colors[ 63] = new Color(27, 111, 136);
        colors[ 64] = new Color(27, 111, 136);
        colors[ 65] = new Color(27, 115, 135);
        colors[ 66] = new Color(27, 115, 135);
        colors[ 67] = new Color(28, 118, 134);
        colors[ 68] = new Color(28, 118, 134);
        colors[ 69] = new Color(29, 122, 133);
        colors[ 70] = new Color(29, 122, 133);
        colors[ 71] = new Color(29, 122, 133);
        colors[ 72] = new Color(29, 122, 133);
        colors[ 73] = new Color(29, 125, 132);
        colors[ 74] = new Color(29, 125, 132);
        colors[ 75] = new Color(30, 128, 131);
        colors[ 76] = new Color(30, 128, 131);
        colors[ 77] = new Color(31, 131, 130);
        colors[ 78] = new Color(31, 131, 130);
        colors[ 79] = new Color(31, 131, 130);
        colors[ 80] = new Color(32, 134, 128);
        colors[ 81] = new Color(32, 134, 128);
        colors[ 82] = new Color(33, 137, 127);
        colors[ 83] = new Color(33, 137, 127);
        colors[ 84] = new Color(33, 137, 127);
        colors[ 85] = new Color(34, 140, 125);
        colors[ 86] = new Color(34, 140, 125);
        colors[ 87] = new Color(35, 142, 123);
        colors[ 88] = new Color(35, 142, 123);
        colors[ 89] = new Color(36, 145, 121);
        colors[ 90] = new Color(36, 145, 121);
        colors[ 91] = new Color(36, 145, 121);
        colors[ 92] = new Color(37, 147, 118);
        colors[ 93] = new Color(37, 147, 118);
        colors[ 94] = new Color(38, 150, 116);
        colors[ 95] = new Color(38, 150, 116);
        colors[ 96] = new Color(40, 152, 113);
        colors[ 97] = new Color(40, 152, 113);
        colors[ 98] = new Color(41, 154, 111);
        colors[ 99] = new Color(41, 154, 111);
        colors[100] = new Color(42, 156, 108);
        colors[101] = new Color(42, 156, 108);
        colors[102] = new Color(43, 158, 106);
        colors[103] = new Color(43, 158, 106);
        colors[104] = new Color(43, 158, 106);
        colors[105] = new Color(45, 160, 104);
        colors[106] = new Color(45, 160, 104);
        colors[107] = new Color(46, 162, 101);
        colors[108] = new Color(46, 162, 101);
        colors[109] = new Color(48, 164, 99);
        colors[110] = new Color(48, 164, 99);
        colors[111] = new Color(50, 166, 97);
        colors[112] = new Color(50, 166, 97);
        colors[113] = new Color(51, 168, 95);
        colors[114] = new Color(53, 170, 93);
        colors[115] = new Color(53, 170, 93);
        colors[116] = new Color(53, 170, 93);
        colors[117] = new Color(55, 172, 91);
        colors[118] = new Color(55, 172, 91);
        colors[119] = new Color(57, 174, 88);
        colors[120] = new Color(57, 174, 88);
        colors[121] = new Color(59, 175, 86);
        colors[122] = new Color(62, 177, 84);
        colors[123] = new Color(64, 178, 82);
        colors[124] = new Color(64, 178, 82);
        colors[125] = new Color(67, 180, 80);
        colors[126] = new Color(67, 180, 80);
        colors[127] = new Color(69, 181, 79);
        colors[128] = new Color(72, 183, 77);
        colors[129] = new Color(72, 183, 77);
        colors[130] = new Color(72, 183, 77);
        colors[131] = new Color(75, 184, 76);
        colors[132] = new Color(77, 186, 74);
        colors[133] = new Color(80, 187, 73);
        colors[134] = new Color(83, 189, 72);
        colors[135] = new Color(87, 190, 72);
        colors[136] = new Color(91, 191, 71);
        colors[137] = new Color(95, 192, 70);
        colors[138] = new Color(99, 193, 70);
        colors[139] = new Color(103, 194, 70);
        colors[140] = new Color(107, 195, 70);
        colors[141] = new Color(111, 196, 70);
        colors[142] = new Color(111, 196, 70);
        colors[143] = new Color(115, 196, 70);
        colors[144] = new Color(119, 197, 70);
        colors[145] = new Color(123, 197, 70);
        colors[146] = new Color(130, 198, 71);
        colors[147] = new Color(133, 199, 71);
        colors[148] = new Color(137, 199, 72);
        colors[149] = new Color(140, 199, 72);
        colors[150] = new Color(143, 199, 73);
        colors[151] = new Color(143, 199, 73);
        colors[152] = new Color(147, 199, 73);
        colors[153] = new Color(150, 199, 74);
        colors[154] = new Color(153, 199, 74);
        colors[155] = new Color(156, 199, 75);
        colors[156] = new Color(160, 200, 76);
        colors[157] = new Color(167, 200, 78);
        colors[158] = new Color(170, 200, 79);
        colors[159] = new Color(173, 200, 79);
        colors[160] = new Color(173, 200, 79);
        colors[161] = new Color(177, 200, 80);
        colors[162] = new Color(180, 200, 81);
        colors[163] = new Color(183, 199, 82);
        colors[164] = new Color(186, 199, 82);
        colors[165] = new Color(190, 199, 83);
        colors[166] = new Color(196, 199, 85);
        colors[167] = new Color(199, 198, 85);
        colors[168] = new Color(199, 198, 85);
        colors[169] = new Color(203, 198, 86);
        colors[170] = new Color(206, 197, 87);
        colors[171] = new Color(212, 197, 89);
        colors[172] = new Color(215, 196, 90);
        colors[173] = new Color(218, 195, 91);
        colors[174] = new Color(224, 194, 94);
        colors[175] = new Color(224, 194, 94);
        colors[176] = new Color(230, 193, 96);
        colors[177] = new Color(233, 192, 98);
        colors[178] = new Color(236, 190, 100);
        colors[179] = new Color(238, 189, 104);
        colors[180] = new Color(240, 188, 106);
        colors[181] = new Color(240, 188, 106);
        colors[182] = new Color(242, 187, 110);
        colors[183] = new Color(244, 185, 114);
        colors[184] = new Color(245, 184, 116);
        colors[185] = new Color(247, 183, 120);
        colors[186] = new Color(248, 182, 123);
        colors[187] = new Color(248, 182, 123);
        colors[188] = new Color(250, 181, 125);
        colors[189] = new Color(251, 180, 128);
        colors[190] = new Color(252, 180, 130);
        colors[191] = new Color(253, 180, 133);
        colors[192] = new Color(253, 180, 133);
        colors[193] = new Color(254, 180, 134);
        colors[194] = new Color(254, 179, 138);
        colors[195] = new Color(255, 179, 142);
        colors[196] = new Color(255, 179, 145);
        colors[197] = new Color(255, 179, 145);
        colors[198] = new Color(255, 179, 152);
        colors[199] = new Color(255, 180, 161);
        colors[200] = new Color(255, 180, 164);
        colors[201] = new Color(255, 180, 167);
        colors[202] = new Color(255, 180, 167);
        colors[203] = new Color(255, 181, 169);
        colors[204] = new Color(255, 181, 170);
        colors[205] = new Color(255, 182, 173);
        colors[206] = new Color(255, 183, 176);
        colors[207] = new Color(255, 183, 176);
        colors[208] = new Color(255, 184, 179);
        colors[209] = new Color(255, 185, 179);
        colors[210] = new Color(255, 185, 182);
        colors[211] = new Color(255, 186, 182);
        colors[212] = new Color(255, 186, 182);
        colors[213] = new Color(255, 187, 185);
        colors[214] = new Color(255, 188, 185);
        colors[215] = new Color(255, 189, 188);
        colors[216] = new Color(255, 189, 188);
        colors[217] = new Color(255, 190, 188);
        colors[218] = new Color(255, 191, 191);
        colors[219] = new Color(255, 192, 191);
        colors[220] = new Color(255, 194, 194);
        colors[221] = new Color(255, 194, 194);
        colors[222] = new Color(255, 197, 197);
        colors[223] = new Color(255, 198, 198);
        colors[224] = new Color(255, 200, 200);
        colors[225] = new Color(255, 201, 201);
        colors[226] = new Color(255, 201, 201);
        colors[227] = new Color(255, 202, 202);
        colors[228] = new Color(255, 203, 203);
        colors[229] = new Color(255, 205, 205);
        colors[230] = new Color(255, 206, 206);
        colors[231] = new Color(255, 206, 206);
        colors[232] = new Color(255, 208, 208);
        colors[233] = new Color(255, 209, 209);
        colors[234] = new Color(255, 211, 211);
        colors[235] = new Color(255, 215, 215);
        colors[236] = new Color(255, 216, 216);
        colors[237] = new Color(255, 216, 216);
        colors[238] = new Color(255, 218, 218);
        colors[239] = new Color(255, 219, 219);
        colors[240] = new Color(255, 221, 221);
        colors[241] = new Color(255, 223, 223);
        colors[242] = new Color(255, 226, 226);
        colors[243] = new Color(255, 228, 228);
        colors[244] = new Color(255, 230, 230);
        colors[245] = new Color(255, 230, 230);
        colors[246] = new Color(255, 232, 232);
        colors[247] = new Color(255, 235, 235);
        colors[248] = new Color(255, 237, 237);
        colors[249] = new Color(255, 240, 240);
        colors[250] = new Color(255, 243, 243);
        colors[251] = new Color(255, 246, 246);
        colors[252] = new Color(255, 249, 249);
        colors[253] = new Color(255, 251, 251);
        colors[254] = new Color(255, 253, 253);
        colors[255] = new Color(255, 255, 255);
    }
}
