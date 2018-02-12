package org.davidespadini.gerrit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.gerrit.extensions.common.CommentInfo;

public class GerritMinerTests {
	
	@Test
	public void FormatStringTest(){
		GerritMiner gm = new GerritMiner();
		
		assertEquals("Asd asd", gm.formatString("Asd' asd"));
		assertEquals("Asd asd", gm.formatString("Asd''' asd"));
		assertEquals("Asd asd", gm.formatString("Asd'\'\' asd"));
		assertEquals("Asd asd", gm.formatString("Asd'\'\\%' asd"));
	}
	
	@Test
	public void getNumberOfCommentsTest(){
		GerritMiner gm = new GerritMiner();
		Set<String> files = new HashSet<String>();
		Map<String, List<CommentInfo>> comments = new HashMap<String, List<CommentInfo>>();
		files.add("file1");
		files.add("file2");
		files.add("file3");
		
		CommentInfo ci1 = new CommentInfo();
		CommentInfo ci2 = new CommentInfo();
		CommentInfo ci3 = new CommentInfo();
		CommentInfo ci4 = new CommentInfo();
		CommentInfo ci5 = new CommentInfo();
		CommentInfo ci6 = new CommentInfo();
		CommentInfo ci7 = new CommentInfo();
		
		comments.put("file1", Arrays.asList(ci1, ci2));
		comments.put("file2", Arrays.asList(ci3, ci4, ci5, ci6, ci7));
		
		HashMap<String, Integer> res = gm.getNumberOfComments(files, comments);
		assertEquals(Integer.parseInt(res.get("file1").toString()), 2);
		assertEquals(Integer.parseInt(res.get("file2").toString()), 5);
		assertEquals(Integer.parseInt(res.get("file3").toString()), 0);
	}
    
	
//	@Test
//	public void RecordTest(){
//		Review r1 = new Review
//		Review r1 = new Review("asd", "asd", "asd", "asd", "asd", 0);
//		Review r2 = new Review("asd", "asd", "asd", "asd", "asd", 0);
//		Review r3 = new Review("asd", "asd", "asd", "asd", "asd", 1);
//		Review r4 = new Review("asd", "asd", "asd", "asd", "foo", 0);
//		Review r5 = new Review("asd", "asd", "asd", "foo", "asd", 0);
//		Review r6 = new Review("asd", "asd", "foo", "asd", "asd", 0);
//		Review r7 = new Review("asd", "foo", "asd", "asd", "asd", 0);
//		Review r8 = new Review("foo", "asd", "asd", "asd", "asd", 0);
//		
//		assertTrue(r1.equals(r2));
//		assertFalse(r1.equals(r3));
//		assertFalse(r1.equals(r4));
//		assertFalse(r1.equals(r5));
//		assertFalse(r1.equals(r6));
//		assertFalse(r1.equals(r7));
//		assertFalse(r1.equals(r8));
//		
//	}
	
	@Test
	public void getCommentsBodyTest(){
		GerritMiner gm = new GerritMiner();
		Set<String> files = new HashSet<String>();
		Map<String, List<CommentInfo>> comments = new HashMap<String, List<CommentInfo>>();
		files.add("file1");
		files.add("file2");
		
		CommentInfo ci1 = new CommentInfo();
		CommentInfo ci2 = new CommentInfo();
		
		ci1.message = "ciaoooo";
		ci2.message = "come stai?";
		
		comments.put("file1", Arrays.asList(ci1, ci2));

		String res = "ciaoooo" +
					 "\n###NEWCOMMENT###\n" + "come stai?";
		
		assertEquals(gm.getCommentsBody(files, comments).get("file1"), res);
		assertEquals(gm.getCommentsBody(files, comments).get("file2"), null);
	}
}
