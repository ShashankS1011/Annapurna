package com.example.annapurna;

import org.junit.Test;
import static org.junit.Assert.*;

public class LinkParserUtilTest {

    @Test
    public void testExtractUrl() {
        String input = "Check out this recipe at https://www.youtube.com/watch?v=dQw4w9WgXcQ for delicious food!";
        String url = LinkParserUtil.extractUrl(input);
        assertEquals("https://www.youtube.com/watch?v=dQw4w9WgXcQ", url);
    }

    @Test
    public void testDetectSource() {
        assertEquals(LinkParserUtil.SOURCE_YOUTUBE, LinkParserUtil.detectSource("https://www.youtube.com/watch?v=12345"));
        assertEquals(LinkParserUtil.SOURCE_YOUTUBE, LinkParserUtil.detectSource("https://youtu.be/12345"));
        assertEquals(LinkParserUtil.SOURCE_INSTAGRAM, LinkParserUtil.detectSource("https://www.instagram.com/reel/C12345/"));
        assertEquals(LinkParserUtil.SOURCE_LOCAL, LinkParserUtil.detectSource("content://media/external/video/media/123"));
        assertEquals(LinkParserUtil.SOURCE_LOCAL, LinkParserUtil.detectSource(null));
    }

    @Test
    public void testExtractYouTubeId() {
        String id1 = LinkParserUtil.extractYouTubeId("https://www.youtube.com/watch?v=abcXYZ12345");
        assertEquals("abcXYZ12345", id1);

        String id2 = LinkParserUtil.extractYouTubeId("https://youtu.be/xyzABC98765");
        assertEquals("xyzABC98765", id2);
    }

    @Test
    public void testGetYouTubeThumbnailUrl() {
        String thumb = LinkParserUtil.getYouTubeThumbnailUrl("testVideoId");
        assertEquals("https://img.youtube.com/vi/testVideoId/hqdefault.jpg", thumb);
        assertNull(LinkParserUtil.getYouTubeThumbnailUrl(null));
    }

    @Test
    public void testEmbedHtml() {
        String ytHtml = LinkParserUtil.getYouTubeEmbedHtml("vid123");
        assertTrue(ytHtml.contains("vid123"));

        String igHtml = LinkParserUtil.getInstagramEmbedHtml("https://instagram.com/p/abc");
        assertTrue(igHtml.contains("instagram.com"));
    }
}
