package topicevolutionvis;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import topicevolutionvis.data.BibTeX2RIS;
import topicevolutionvis.data.ISICorpusDatabaseImporter;

import com.ironiacorp.io.IoUtil;

public class BibTeX2RISTest
{
	private BibTeX2RIS bib;
	
	@Before
	public void setUp() throws Exception {
		bib = new BibTeX2RIS();
	}

	@Test
	public void testSetInputFilename() {
		bib.setInputFilename("/home/magsilva/Projects/ScienceView/ScienceView/test-resources/topicevolutionvis/InproceedingsArticle.bib");
		File output = bib.getOutputFile();
		assertEquals("/home/magsilva/Projects/ScienceView/ScienceView/test-resources/topicevolutionvis/InproceedingsArticle.isi", output.getAbsolutePath());
	}

	@Test
	public void testSetInputFilenameWithoutExtension() {
		bib.setInputFilename("/home/magsilva/Projects/ScienceView/ScienceView/test-resources/topicevolutionvis/InproceedingsArticle");
		File output = bib.getOutputFile();
		assertEquals("/home/magsilva/Projects/ScienceView/ScienceView/test-resources/topicevolutionvis/InproceedingsArticle.isi", output.getAbsolutePath());
	}
	
	@Test
	public void testGetOutputFilename() {
		assertNull(bib.getOutputFile());
	}

	@Test
	public void testSetOutputFile() {
		bib.setInputFilename("/home/magsilva/Projects/ScienceView/ScienceView/test-resources/topicevolutionvis/InproceedingsArticle.bib");
		bib.setOutputFile("teste.isi");
		File output = bib.getOutputFile();
		assertEquals("teste.isi", output.getName());
	}

	@Test
	public void testReadData() {
		bib.setInputFilename("/home/magsilva/Projects/ScienceView/ScienceView/test-resources/topicevolutionvis/InproceedingsArticle.bib");
		bib.readData();
	}

	@Test(expected=RuntimeException.class)
	public void testReadData_InvalidData() {
		bib.setInputFilename("/home/magsilva/Projects/ScienceView/ScienceView/test-resources/topicevolutionvis/InproceedingsArticle_invalid.bib");
		bib.readData();
	}

	
	@Test
	public void testConvert() throws IOException {
		String actualIsiData, expectedIsiData;
		File outfile;
		File file = IoUtil.createTempFile(this.getClass().getName(), ISICorpusDatabaseImporter.FILE_EXTENSION);
		bib.setInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("topicevolutionvis/InproceedingsArticle.bib"));
		bib.setOutputFile(file);
		bib.readData();
		bib.convert();
		outfile = bib.getOutputFile();
		actualIsiData = IoUtil.dumpAsString(outfile);
		expectedIsiData = IoUtil.dumpAsString(Thread.currentThread().getContextClassLoader().getResourceAsStream("topicevolutionvis/InproceedingsArticle.isi"));
		assertEquals(expectedIsiData, actualIsiData);
	}
}
