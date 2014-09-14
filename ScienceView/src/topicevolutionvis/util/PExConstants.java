/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2007 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Projection Explorer (PEx).
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
package topicevolutionvis.util;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public interface PExConstants {

    public static final int JOURNAL_ARTICLE = 0;
    public static final int CONFERENCE_PAPER = 1;
    public static final int REPORT = 2;
    public static final int THESIS = 3;
    public static final int GENERIC = 4;
    public static final int CONFERENCE_PROCEEDINGS = 5;
    public static final int UNPUBLISHED = 6;
    public static final int BOOK_CHAPTER = 7;
    public static final int BOOK = 8;
    public static final int NEWSLETTER = 9;
    public static final int ENCYCLOPEDIA_ENTRY = 10;
    public static final int DICTIONARY_ENTRY  = 11;
    public static final int LECTURE = 12;
    public static final String LABEL = "label";
    public static final String TOPICS = "topics";
    public static final String CDATA = "cdata";
    public static final String YEAR = "Publication Year";
    public static final String FNAME = "file name";
    public static final String NGRAM = "ngram";
    public static final String TITLE = "title";
    public static final String KMEANS = "kmeans-";
    public static final String KMEDOIDS = "bkmedoids-";
    public static final String BKMEANS = "bkmeans-";
    public static final String DBSCAN = "dbscan-";
    public static final String COVARIANCE = "covariance-";
    public static final String PIVOTS = "pivots";
    public static final String DELAUNAY = "Delaunay";
    public static final String BIBLIOGRAPHIC_COUPLING = "Bibliographic Coupling";
    public static final String SIMILARITY = "Similarity";
    public static final String CO_AUTHORSHIP = "Co-Authorship";
    public static final String CORE_CITATIONS = "Core Citations";
    public static final String LOCAL_CITATION_COUNT = "Local Citation Count";
    public static final String GLOBAL_CITATION_COUNT = "Global Citation Count";
    public static final String CLASS = "Class";
    public static final String DOTS = "...";
    public static final String NJ = "N-J";
    public static final String MST = "MST (prim)";
    public static final String ALINK = "hc-alink";
    public static final String CLINK = "hc-clink";
    public static final String SLINK = "hc-slink";
    public static final String JOIN = "joined";
    public static final String NULLV = "null vectors";
  //  public static final String PATCOM = "[^A-Za-záàãâéèêíìîóòõôúùû�?ÀÃÂÉÈÊ�?ÌÎÓÒÕÔÚÙÛçÇ]";
//    public static final String PATCOM = "[0-9\\-]*[\\p{L}]+[0-9\\-]*[\\p{L}]+[0-9\\-]*";
}
