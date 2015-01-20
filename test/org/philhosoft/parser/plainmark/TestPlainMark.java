package org.philhosoft.parser.plainmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.format.HTMLBlockEndVisitor;
import org.philhosoft.formattedtext.format.HTMLBlockStartVisitor;
import org.philhosoft.formattedtext.format.HTMLVisitor;
import org.philhosoft.formattedtext.format.VisitorContext;


public class TestPlainMark
{
	public static final String TEST_FILE = "PlainMark - Simple Humane Markup";
	public static final String CSS_FILE = "PlainMark.css";
	public static final String OUTPUT_PATH = "output";
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@BeforeClass
	public static void classSetUp() throws Exception
	{
		File file = new File(OUTPUT_PATH);
		file.mkdirs();
	}

	@Test
	public void testConvertSpec() throws IOException, URISyntaxException
	{
		Path path = Paths.get(TEST_FILE + ".txt");
		String markedText = readFile(path);

		String css = readFile(Paths.get(CSS_FILE));

		HTMLVisitor visitor = new HTMLVisitor(2);
		BlockType.Visitor<VisitorContext> blockStartVisitor = new HTMLBlockStartVisitor()
		{
			@Override
			public void visitDocument(VisitorContext context)
			{
				context.append("<div class='mark'>");
			}
			@Override
			public void visitTitle1(VisitorContext context)
			{
				context.append("<h1>");
			}
			@Override
			public void visitTitle2(VisitorContext context)
			{
				context.append("<h2>");
			}
			@Override
			public void visitTitle3(VisitorContext context)
			{
				context.append("<h3>");
			}
		};
		BlockType.Visitor<VisitorContext> blockEndVisitor = new HTMLBlockEndVisitor()
		{
			@Override
			public void visitTitle1(VisitorContext context)
			{
				context.append("</h1>");
			}
			@Override
			public void visitTitle2(VisitorContext context)
			{
				context.append("</h2>");
			}
			@Override
			public void visitTitle3(VisitorContext context)
			{
				context.append("</h3>");
			}
		};
		visitor.setBlockVisitors(blockStartVisitor, blockEndVisitor);

		PlainMark sm = new PlainMark().setVisitor(visitor);
		String generatedHTML = sm.convert(markedText);

		generatedHTML = handleTables(generatedHTML);

		Path outputPath = Paths.get(OUTPUT_PATH, TEST_FILE + ".html");
		try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8))
		{
			writer.write("<!DOCTYPE html>\n");
			writer.write("<html>\n<head>\n");
			writer.write("\t<meta charset='utf-8'>\n");
			writer.write("\t<title>PlainMark - Simple Humane Markup</title>\n");
			writer.write("<style>\n");
			writer.write(css);
			writer.write("</style>\n");
			writer.write("<body>\n");

			writer.write(generatedHTML);

			writer.write("</body>\n</html>\n");
		}
	}

	private String readFile(Path path) throws IOException
	{
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

		StringBuilder sb = new StringBuilder();
		for (String line : lines)
		{
			sb.append(line).append('\n');
		}
		return sb.toString();
	}

	private String handleTables(String generatedHTML)
	{
		// Hack!
		generatedHTML = generatedHTML.replace("<p>| ", "<table><tr><td>");
		generatedHTML = generatedHTML.replace(" | ", "</td><td>");
		generatedHTML = generatedHTML.replace("| ", "<tr><td>");
		generatedHTML = generatedHTML.replace(" |<br>", "</td></tr>");
		generatedHTML = generatedHTML.replace(" |</p>", "</td></tr></table>");

		generatedHTML = generatedHTML.replace("<p>|||<br>", "<table><tr><td>");
		generatedHTML = generatedHTML.replace("<p>|||</p>", "</td></tr></table>");
		generatedHTML = generatedHTML.replace("<p>||<br>", "<tr><td>");
		generatedHTML = generatedHTML.replace("<p>||</p>", "</td></tr>");
		generatedHTML = generatedHTML.replace("|</p>", "</td><td>");
		return generatedHTML;
	}
}
